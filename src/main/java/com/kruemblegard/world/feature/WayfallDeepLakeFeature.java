package com.kruemblegard.world.feature;

import com.kruemblegard.block.WaylilyBlock;
import com.kruemblegard.init.ModBlocks;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.material.Fluids;

public class WayfallDeepLakeFeature extends Feature<WayfallDeepLakeConfiguration> {
    public WayfallDeepLakeFeature(Codec<WayfallDeepLakeConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WayfallDeepLakeConfiguration> ctx) {
        LevelAccessor level = ctx.level();
        BlockPos origin = ctx.origin();
        RandomSource random = ctx.random();
        WayfallDeepLakeConfiguration cfg = ctx.config();

        int radius = Mth.nextInt(random, cfg.minRadius(), cfg.maxRadius());
        int depth = Mth.nextInt(random, cfg.minDepth(), cfg.maxDepth());

        // Keep edits within the local generation region to avoid far-chunk writes.
        // We also snap the lake center to the chunk center so large lakes don't drift into 2+ chunks away.
        radius = Math.min(radius, 24);
        depth = Math.min(depth, 24);

        // Heightmap placement can hand us an air block above the surface; snap down to the first solid.
        BlockPos surface = origin;
        while (surface.getY() > level.getMinBuildHeight() + 2 && level.getBlockState(surface).isAir()) {
            surface = surface.below();
        }

        // If we never found solid ground, don't place.
        if (level.getBlockState(surface).isAir()) {
            return false;
        }

        int waterSurfaceY;
        if (level.getFluidState(surface).getType() == Fluids.WATER) {
            // If we somehow landed inside an existing water column, treat this as the surface water block.
            waterSurfaceY = surface.getY();
        } else {
            // Normal case: heightmap gave us terrain, so the water surface should be in the air block above.
            waterSurfaceY = surface.getY() + 1;
        }

        BlockPos center = new BlockPos((origin.getX() & ~15) + 8, waterSurfaceY, (origin.getZ() & ~15) + 8);

        long noiseSeed = random.nextLong();
        // Multi-lobed outline (feels less like a circle).
        int lobe1x = Mth.nextInt(random, -radius / 3, radius / 3);
        int lobe1z = Mth.nextInt(random, -radius / 3, radius / 3);
        int lobe2x = Mth.nextInt(random, -radius / 3, radius / 3);
        int lobe2z = Mth.nextInt(random, -radius / 3, radius / 3);

        int diameter = radius * 2 + 1;
        int[] columnDepths = new int[diameter * diameter];

        boolean placedAnyWater = false;

        // Carve an irregular bowl: per-column radius + depth jitter for a more natural outline.
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double dist = minDistanceToLobes(dx, dz, lobe1x, lobe1z, lobe2x, lobe2z);
                double colRadius = columnRadius(dx, dz, radius, noiseSeed);
                if (dist > colRadius) {
                    continue;
                }

                int colDepth = columnDepth(dx, dz, depth, noiseSeed);
                double t = dist / Math.max(1.0D, colRadius);
                int localDepth = Mth.clamp((int) Math.round(colDepth * (1.0D - t * t)), 1, colDepth);

                columnDepths[(dx + radius) + (dz + radius) * diameter] = localDepth;

                for (int dy = 0; dy <= localDepth; dy++) {
                    BlockPos p = center.offset(dx, -dy, dz);
                    if (level.isOutsideBuildHeight(p)) {
                        continue;
                    }

                    BlockState fluidState = cfg.fluid().getState(random, p);
                    level.setBlock(p, fluidState, 2);
                    placedAnyWater = true;
                }
            }
        }

        if (!placedAnyWater) {
            return false;
        }

        // Place a barrier shell around the water to prevent leaks into adjacent caves.
        // Important: we *do* fill adjacent air below the surface, otherwise the lake will spill.
        BlockState lakeFluidSample = cfg.fluid().getState(random, center);
        int shellRadius = radius + 1;
        int shellDepth = depth + 2;
        for (int dx = -shellRadius; dx <= shellRadius; dx++) {
            for (int dz = -shellRadius; dz <= shellRadius; dz++) {
                for (int dy = 0; dy <= shellDepth; dy++) {
                    BlockPos p = center.offset(dx, -dy, dz);
                    if (level.isOutsideBuildHeight(p)) {
                        continue;
                    }

                    if (p.getY() > waterSurfaceY) {
                        continue;
                    }

                    if (isInsideLake(columnDepths, diameter, radius, dx, dz, dy)) {
                        continue;
                    }

                    if (!isAdjacentToLake(columnDepths, diameter, radius, dx, dz, dy)) {
                        continue;
                    }

                    BlockState current = level.getBlockState(p);
                    if (current.getFluidState().isSource() && current.getFluidState().getType() == lakeFluidSample.getFluidState().getType()) {
                        continue;
                    }

                    if (!(current.isAir() || current.canBeReplaced() || !current.getFluidState().isEmpty())) {
                        continue;
                    }

                    BlockState barrierState = cfg.barrier().getState(random, p);
                    level.setBlock(p, barrierState, 2);
                }
            }
        }

        // Cave blending: if the lake edge meets a cave pocket, add a small "cap" above the water line
        // (but only when there is an overhead ceiling). This makes the shoreline feel embedded instead of
        // cutting cleanly through caves.
        int capUp = 2;
        var lakeFluid = lakeFluidSample.getFluidState().getType();
        for (int dx = -shellRadius; dx <= shellRadius; dx++) {
            for (int dz = -shellRadius; dz <= shellRadius; dz++) {
                for (int up = 1; up <= capUp; up++) {
                    BlockPos p = center.offset(dx, up, dz);
                    if (level.isOutsideBuildHeight(p)) {
                        continue;
                    }

                    BlockState cur = level.getBlockState(p);
                    if (!cur.isAir() && !cur.canBeReplaced()) {
                        continue;
                    }

                    if (!isUnderCeiling(level, p, 10)) {
                        continue;
                    }

                    BlockPos below = p.below();
                    boolean touchesLake = level.getFluidState(below).getType() == lakeFluid && level.getFluidState(below).isSource();
                    if (!touchesLake) {
                        continue;
                    }

                    BlockState barrierState = cfg.barrier().getState(random, p);
                    level.setBlock(p, barrierState, 2);
                }
            }
        }

        // Surface decoration: place waylilies on the lake surface.
        // This is done after the barrier pass so the barrier shell doesn't overwrite the lily blocks.
        // Keep density modest even for very large lakes.
        int lilyAttempts = Mth.clamp(radius * 2, 12, 96);
        for (int i = 0; i < lilyAttempts; i++) {
            int dx = random.nextInt(radius * 2 + 1) - radius;
            int dz = random.nextInt(radius * 2 + 1) - radius;

            if (columnDepths[(dx + radius) + (dz + radius) * diameter] <= 0) {
                continue;
            }

            BlockPos lilyPos = center.offset(dx, 0, dz);
            tryPlaceWaylily(level, lilyPos, random);
        }

        return true;
    }

    private static void tryPlaceWaylily(LevelAccessor level, BlockPos surfacePos, RandomSource random) {
        // If our candidate position is submerged, scan up to the true surface.
        while (level.getFluidState(surfacePos).getType() == Fluids.WATER
                && level.getFluidState(surfacePos.above()).getType() == Fluids.WATER) {
            surfacePos = surfacePos.above();
        }

        // Must be surface water.
        if (level.getFluidState(surfacePos).getType() != Fluids.WATER) {
            return;
        }

        if (level.getFluidState(surfacePos.above()).getType() == Fluids.WATER) {
            return;
        }

        if (!level.getBlockState(surfacePos.above()).canBeReplaced()) {
            return;
        }

        BlockPos lowerPos = surfacePos.below();
        if (level.getFluidState(lowerPos).getType() != Fluids.WATER) {
            return;
        }

        BlockPos lower2Pos = surfacePos.below(2);
        boolean canHaveLower2 = level.getFluidState(lower2Pos).getType() == Fluids.WATER;
        boolean wantsLower2 = canHaveLower2 && random.nextBoolean();

        BlockState upperState = ModBlocks.WAYLILY.get()
                .defaultBlockState()
                .setValue(WaylilyBlock.PART, WaylilyBlock.Part.UPPER)
                .setValue(WaylilyBlock.WATERLOGGED, Boolean.FALSE);

        BlockState lowerState = ModBlocks.WAYLILY.get()
                .defaultBlockState()
                .setValue(WaylilyBlock.PART, WaylilyBlock.Part.LOWER)
                .setValue(WaylilyBlock.WATERLOGGED, Boolean.TRUE);

        BlockState lower2State = ModBlocks.WAYLILY.get()
                .defaultBlockState()
                .setValue(WaylilyBlock.PART, WaylilyBlock.Part.LOWER2)
                .setValue(WaylilyBlock.WATERLOGGED, Boolean.TRUE);

        // Replace only water blocks; don't stomp other worldgen decoration.
        if (!level.getBlockState(surfacePos).canBeReplaced()) {
            return;
        }

        level.setBlock(surfacePos, upperState, 2);
        level.setBlock(lowerPos, lowerState, 2);
        if (wantsLower2) {
            level.setBlock(lower2Pos, lower2State, 2);
        }

        // Ensure water ticks for the tails.
        level.scheduleTick(lowerPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        if (wantsLower2) {
            level.scheduleTick(lower2Pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
    }

    private static boolean isInsideLake(int[] depths, int diameter, int radius, int dx, int dz, int dy) {
        if (dx < -radius || dx > radius || dz < -radius || dz > radius) {
            return false;
        }
        int localDepth = depths[(dx + radius) + (dz + radius) * diameter];
        return localDepth > 0 && dy <= localDepth;
    }

    private static boolean isAdjacentToLake(int[] depths, int diameter, int radius, int dx, int dz, int dy) {
        return isInsideLake(depths, diameter, radius, dx + 1, dz, dy)
                || isInsideLake(depths, diameter, radius, dx - 1, dz, dy)
                || isInsideLake(depths, diameter, radius, dx, dz + 1, dy)
                || isInsideLake(depths, diameter, radius, dx, dz - 1, dy)
                || isInsideLake(depths, diameter, radius, dx, dz, dy + 1)
                || isInsideLake(depths, diameter, radius, dx, dz, dy - 1);
    }

    private static double minDistanceToLobes(int dx, int dz, int lobe1x, int lobe1z, int lobe2x, int lobe2z) {
        double d0 = Mth.sqrt((float) (dx * dx + dz * dz));
        int dx1 = dx - lobe1x;
        int dz1 = dz - lobe1z;
        double d1 = Mth.sqrt((float) (dx1 * dx1 + dz1 * dz1));
        int dx2 = dx - lobe2x;
        int dz2 = dz - lobe2z;
        double d2 = Mth.sqrt((float) (dx2 * dx2 + dz2 * dz2));
        return Math.min(d0, Math.min(d1, d2));
    }

    private static double columnRadius(int dx, int dz, int radius, long seed) {
        // Smooth-ish value noise so the edge is irregular but not "pixel jagged".
        double n1 = valueNoise2D(dx, dz, 7, seed);
        double n2 = valueNoise2D(dx, dz, 13, seed ^ 0x9E3779B97F4A7C15L);
        double noise = n1 * 0.70D + n2 * 0.30D;

        double angle = Math.atan2(dz, dx);
        double wave = Math.sin(angle * 3.0D + (seed & 0xFFFF) * 0.0007D) * 0.6D
                + Math.sin(angle * 7.0D + (seed >>> 16 & 0xFFFF) * 0.0011D) * 0.4D;

        double jitter = Mth.clamp((float) (noise * 0.85D + wave * 0.15D), -1.0F, 1.0F);
        double factor = 1.0D + 0.30D * jitter;
        factor = Mth.clamp((float) factor, 0.70F, 1.12F);
        return radius * factor;
    }

    private static int columnDepth(int dx, int dz, int depth, long seed) {
        double n = valueNoise2D(dx, dz, 9, seed ^ 0xD1B54A32D192ED03L);
        double factor = 1.0D + 0.15D * n;
        int out = (int) Math.round(depth * factor);
        return Mth.clamp(out, Math.max(2, depth - 4), depth + 4);
    }

    private static boolean isUnderCeiling(LevelAccessor level, BlockPos pos, int maxUp) {
        for (int i = 1; i <= maxUp; i++) {
            BlockPos p = pos.above(i);
            if (level.isOutsideBuildHeight(p)) {
                break;
            }
            if (!level.getBlockState(p).isAir()) {
                return true;
            }
        }
        return false;
    }

    private static double valueNoise2D(int x, int z, int scale, long seed) {
        int x0 = Mth.floor((float) x / (float) scale);
        int z0 = Mth.floor((float) z / (float) scale);
        int x1 = x0 + 1;
        int z1 = z0 + 1;

        double fx = ((double) x / (double) scale) - x0;
        double fz = ((double) z / (double) scale) - z0;
        double u = fade(fx);
        double v = fade(fz);

        double v00 = hashNoise(x0, z0, seed);
        double v10 = hashNoise(x1, z0, seed);
        double v01 = hashNoise(x0, z1, seed);
        double v11 = hashNoise(x1, z1, seed);

        double ix0 = Mth.lerp(u, v00, v10);
        double ix1 = Mth.lerp(u, v01, v11);
        return Mth.lerp(v, ix0, ix1);
    }

    private static double fade(double t) {
        // 6t^5 - 15t^4 + 10t^3
        return t * t * t * (t * (t * 6.0D - 15.0D) + 10.0D);
    }

    private static double hashNoise(int x, int z, long seed) {
        long h = seed;
        h ^= (long) x * 341873128712L;
        h ^= (long) z * 132897987541L;
        h = (h ^ (h >>> 33)) * 0xff51afd7ed558ccdL;
        h = (h ^ (h >>> 33)) * 0xc4ceb9fe1a85ec53L;
        h = h ^ (h >>> 33);
        // Map to [-1, 1]
        return ((h >>> 11) * 0x1.0p-53) * 2.0D - 1.0D;
    }
}
