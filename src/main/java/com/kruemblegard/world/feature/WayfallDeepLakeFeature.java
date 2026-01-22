package com.kruemblegard.world.feature;

import com.kruemblegard.block.WaylilyBlock;
import com.kruemblegard.init.ModBlocks;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class WayfallDeepLakeFeature extends Feature<WayfallDeepLakeConfiguration> {
    private static final int MAX_SAFE_RADIUS = 16; // stays within ±1 chunk even near chunk edges
    private static final int MAX_SAFE_DEPTH = 18;

    public WayfallDeepLakeFeature(Codec<WayfallDeepLakeConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WayfallDeepLakeConfiguration> ctx) {
        WorldGenLevel level = ctx.level();
        BlockPos origin = ctx.origin();
        RandomSource random = ctx.random();
        WayfallDeepLakeConfiguration cfg = ctx.config();

        ChunkPos originChunk = new ChunkPos(origin);

        int radius = sampleBiasedRadius(random, cfg.minRadius(), cfg.maxRadius());
        int depth = Mth.nextInt(random, cfg.minDepth(), cfg.maxDepth());

        // Hard safety clamp: placed features are expected to write within a small worldgen region.
        // Bigger radii will spam "far chunk" warnings and can cause extreme lag during worldgen.
        radius = Math.min(radius, MAX_SAFE_RADIUS);
        depth = Math.min(depth, MAX_SAFE_DEPTH);

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

        // Use the provided placement origin for more natural distribution.
        // Chunk-centering creates visible chunk-grid artifacts (hard shoreline lines at chunk borders).
        BlockPos center = new BlockPos(origin.getX(), waterSurfaceY, origin.getZ());

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

                    if (!canWrite(level, originChunk, p)) {
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

        BlockState lakeFluidSample = cfg.fluid().getState(random, center);
        Fluid lakeFluid = lakeFluidSample.getFluidState().getType();

        // Seal *internal* air pockets below the surface (e.g. cave tunnels that intersect the bowl).
        // Without this, water can escape through caves even if the outer shell is sealed.
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dy = 0; dy <= depth + 2; dy++) {
                    BlockPos p = center.offset(dx, -dy, dz);
                    if (level.isOutsideBuildHeight(p)) {
                        continue;
                    }

                    if (p.getY() > waterSurfaceY) {
                        continue;
                    }

                    BlockState cur = level.getBlockState(p);
                    if (!cur.isAir() && !cur.canBeReplaced()) {
                        continue;
                    }

                    if (cur.getFluidState().getType() == lakeFluid) {
                        continue;
                    }

                    if (!touchesLakeFluid(level, p, lakeFluid)) {
                        continue;
                    }

                    if (!canWrite(level, originChunk, p)) {
                        continue;
                    }

                    BlockState barrierState = cfg.barrier().getState(random, p);
                    level.setBlock(p, barrierState, 2);
                }
            }
        }

        // Place a barrier shell around the water to prevent leaks into adjacent caves.
        // Important: we *do* fill adjacent air below the surface, otherwise the lake will spill.
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

                    if (!canWrite(level, originChunk, p)) {
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
        for (int dx = -shellRadius; dx <= shellRadius; dx++) {
            for (int dz = -shellRadius; dz <= shellRadius; dz++) {
                for (int up = 1; up <= capUp; up++) {
                    BlockPos p = center.offset(dx, up, dz);
                    if (level.isOutsideBuildHeight(p)) {
                        continue;
                    }

                    if (!canWrite(level, originChunk, p)) {
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

                    if (!canWrite(level, originChunk, p)) {
                        continue;
                    }

                    BlockState barrierState = cfg.barrier().getState(random, p);
                    level.setBlock(p, barrierState, 2);
                }
            }
        }

        // Shoreline blending: build a 2-layer Stoneveil Rubble berm around the surface edge
        // to prevent surface spill sheets and help the lake read as "contained" in floating-island terrain.
        placeStoneveilRubbleRim(level, originChunk, center, waterSurfaceY, lakeFluid, radius, random);

        // Underwater flora: dress some lake floor blocks and place seagrass.
        // This guarantees lakes don't feel "sterile" even if biome modifiers are sparse.
        if (lakeFluid == Fluids.WATER) {
            int attempts = Mth.clamp(radius * radius / 2, 48, 320);
            for (int i = 0; i < attempts; i++) {
                int dx = random.nextInt(radius * 2 + 1) - radius;
                int dz = random.nextInt(radius * 2 + 1) - radius;

                int localDepth = columnDepths[(dx + radius) + (dz + radius) * diameter];
                if (localDepth <= 2) {
                    continue;
                }

                BlockPos bottomWaterPos = center.offset(dx, -localDepth, dz);
                tryDressAndPlaceSeagrass(level, originChunk, bottomWaterPos, random);
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
            tryPlaceWaylily(level, originChunk, lilyPos, random);
        }

        return true;
    }

    private static void placeStoneveilRubbleRim(
            WorldGenLevel level,
            ChunkPos originChunk,
            BlockPos center,
            int waterSurfaceY,
            Fluid lakeFluid,
            int radius,
            RandomSource random
    ) {
        BlockState rubble = ModBlocks.STONEVEIL_RUBBLE.get().defaultBlockState();
        int thickness = 2;

        // Only look around the surface band; we just need to stop surface flow + blend the rim.
        for (int dx = -radius - 1; dx <= radius + 1; dx++) {
            for (int dz = -radius - 1; dz <= radius + 1; dz++) {
                BlockPos surface = new BlockPos(center.getX() + dx, waterSurfaceY, center.getZ() + dz);
                if (level.isOutsideBuildHeight(surface) || !canWrite(level, originChunk, surface)) {
                    continue;
                }

                // Only operate on true surface-fluid columns.
                if (level.getFluidState(surface).getType() != lakeFluid) {
                    continue;
                }

                if (level.getFluidState(surface.above()).getType() == lakeFluid) {
                    continue;
                }

                for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.Plane.HORIZONTAL) {
                    BlockPos neighbor = surface.relative(dir);
                    if (level.isOutsideBuildHeight(neighbor) || !canWrite(level, originChunk, neighbor)) {
                        continue;
                    }

                    // Only build berms where the lake edge is exposed to air/replaceable blocks.
                    if (level.getFluidState(neighbor).getType() == lakeFluid) {
                        continue;
                    }

                    BlockState neighborState = level.getBlockState(neighbor);
                    boolean exposed = neighborState.isAir() || neighborState.canBeReplaced() || !neighborState.getFluidState().isEmpty();
                    if (!exposed) {
                        continue;
                    }

                    for (int step = 1; step <= thickness; step++) {
                        // Feather the outermost ring so the berm blends into terrain.
                        if (step == thickness && random.nextFloat() < 0.35F) {
                            continue;
                        }

                        BlockPos berm = surface.relative(dir, step);
                        if (level.isOutsideBuildHeight(berm) || !canWrite(level, originChunk, berm)) {
                            continue;
                        }

                        // Always place at water level to stop surface flow.
                        tryPlaceRubble(level, originChunk, berm, rubble);

                        // Randomly place a second layer above for a contained "lip".
                        if (random.nextFloat() < 0.55F) {
                            tryPlaceRubble(level, originChunk, berm.above(), rubble);
                        }

                        // Blend downward if this berm position is hanging over air.
                        int maxDown = 5;
                        BlockPos down = berm.below();
                        for (int d = 0; d < maxDown; d++) {
                            if (level.isOutsideBuildHeight(down) || !canWrite(level, originChunk, down)) {
                                break;
                            }

                            BlockState downState = level.getBlockState(down);
                            if (!downState.isAir() && !downState.canBeReplaced()) {
                                break;
                            }

                            tryPlaceRubble(level, originChunk, down, rubble);
                            down = down.below();
                        }
                    }
                }
            }
        }
    }

    private static void tryPlaceRubble(WorldGenLevel level, ChunkPos originChunk, BlockPos pos, BlockState rubble) {
        if (!canWrite(level, originChunk, pos)) {
            return;
        }

        BlockState state = level.getBlockState(pos);
        if (!state.isAir() && !state.canBeReplaced() && state.getFluidState().isEmpty()) {
            return;
        }

        // Never overwrite the lake fluid itself.
        if (!state.getFluidState().isEmpty()) {
            // If this is the lake fluid, do nothing.
            // If it's some other fluid, treat it as replaceable.
        }

        level.setBlock(pos, rubble, 2);
    }

    private static int sampleBiasedRadius(RandomSource random, int min, int max) {
        if (max <= min) {
            return min;
        }

        // Bias toward smaller values so average radius is closer to ~1/3 of the range above min.
        // With min=10 and max=50 this yields an average around ~23–25.
        float t = random.nextFloat();
        t = t * t;
        return min + Math.round((max - min) * t);
    }

    private static void tryPlaceWaylily(LevelAccessor level, ChunkPos originChunk, BlockPos surfacePos, RandomSource random) {
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

        BlockPos upperPos = surfacePos.above();
        if (!level.getBlockState(upperPos).canBeReplaced()) {
            return;
        }

        // Lower tail sits in the surface water block.
        BlockPos lowerPos = surfacePos;

        // Optional second tail block one deeper.
        BlockPos lower2Pos = surfacePos.below();
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

        // Replace only air/replaceable for the upper, and only water for the tails.
        if (!level.getBlockState(upperPos).canBeReplaced()) {
            return;
        }

        if (level.getFluidState(lowerPos).getType() != Fluids.WATER) {
            return;
        }

        if (!canWrite(level, originChunk, upperPos)
            || !canWrite(level, originChunk, lowerPos)
            || (wantsLower2 && !canWrite(level, originChunk, lower2Pos))) {
            return;
        }

        level.setBlock(upperPos, upperState, 2);
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

    private static boolean touchesLakeFluid(LevelAccessor level, BlockPos pos, Fluid lakeFluid) {
        return level.getFluidState(pos.above()).getType() == lakeFluid
                || level.getFluidState(pos.below()).getType() == lakeFluid
                || level.getFluidState(pos.north()).getType() == lakeFluid
                || level.getFluidState(pos.south()).getType() == lakeFluid
                || level.getFluidState(pos.west()).getType() == lakeFluid
                || level.getFluidState(pos.east()).getType() == lakeFluid;
    }

    private static void tryDressAndPlaceSeagrass(LevelAccessor level, ChunkPos originChunk, BlockPos bottomWaterPos, RandomSource random) {
        if (level.getFluidState(bottomWaterPos).getType() != Fluids.WATER) {
            return;
        }

        BlockPos floorPos = bottomWaterPos.below();
        if (level.isOutsideBuildHeight(floorPos)) {
            return;
        }

        BlockState floor = level.getBlockState(floorPos);
        if (floor.isAir() || !floor.getFluidState().isEmpty()) {
            return;
        }

        // Ensure the floor is something seagrass can survive on.
        if (!(floor.is(Blocks.SAND) || floor.is(Blocks.GRAVEL) || floor.is(Blocks.CLAY))) {
            BlockState newFloor;
            float r = random.nextFloat();
            if (r < 0.55F) {
                newFloor = Blocks.SAND.defaultBlockState();
            } else if (r < 0.90F) {
                newFloor = Blocks.GRAVEL.defaultBlockState();
            } else {
                newFloor = Blocks.CLAY.defaultBlockState();
            }

            if (!canWrite(level, originChunk, floorPos)) {
                return;
            }
            level.setBlock(floorPos, newFloor, 2);
            floor = newFloor;
        }

        // Place seagrass in the bottom water block.
        BlockState seagrass = Blocks.SEAGRASS.defaultBlockState();
        if (seagrass.hasProperty(BlockStateProperties.WATERLOGGED)) {
            seagrass = seagrass.setValue(BlockStateProperties.WATERLOGGED, Boolean.TRUE);
        }

        if (!seagrass.canSurvive(level, bottomWaterPos)) {
            return;
        }

        if (!level.getBlockState(bottomWaterPos).canBeReplaced()) {
            return;
        }

        if (!canWrite(level, originChunk, bottomWaterPos)) {
            return;
        }

        level.setBlock(bottomWaterPos, seagrass, 2);
    }

    private static boolean canWrite(LevelAccessor level, ChunkPos originChunk, BlockPos pos) {
        // First: cheap, non-logging guard to keep edits within ±1 chunk of the feature origin.
        // This prevents both far-chunk writes *and* far-chunk "can write" probes that can spam logs.
        ChunkPos posChunk = new ChunkPos(pos);
        if (Math.abs(posChunk.x - originChunk.x) > 1 || Math.abs(posChunk.z - originChunk.z) > 1) {
            return false;
        }

        // Second: defer to the worldgen region's own safety check.
        if (level instanceof WorldGenLevel worldGenLevel) {
            return worldGenLevel.ensureCanWrite(pos);
        }

        // If something ever calls this outside worldgen, avoid writing into unloaded chunks.
        if (level instanceof net.minecraft.world.level.Level l) {
            return l.hasChunkAt(pos);
        }

        return true;
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
