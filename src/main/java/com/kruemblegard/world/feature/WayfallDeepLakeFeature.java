package com.kruemblegard.world.feature;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

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

        // Heightmap placement can hand us an air block above the surface; snap down to the first solid.
        BlockPos surface = origin;
        while (surface.getY() > level.getMinBuildHeight() + 2 && level.getBlockState(surface).isAir()) {
            surface = surface.below();
        }

        // If we never found solid ground, don't place.
        if (level.getBlockState(surface).isAir()) {
            return false;
        }

        int waterSurfaceY = surface.getY();
        BlockPos center = new BlockPos(origin.getX(), waterSurfaceY, origin.getZ());

        double radiusSq = (double) radius * (double) radius;
        double depthSq = (double) depth * (double) depth;

        boolean placedAnyWater = false;

        // Carve a bowl and fill with water.
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double horizSq = (double) dx * (double) dx + (double) dz * (double) dz;
                if (horizSq > radiusSq) {
                    continue;
                }

                for (int dy = 0; dy <= depth; dy++) {
                    double ny = (double) dy * (double) dy / depthSq;
                    double nxz = horizSq / radiusSq;
                    if (nxz + ny > 1.0D) {
                        continue;
                    }

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
        int shellRadius = radius + 1;
        int shellDepth = depth + 1;
        for (int dx = -shellRadius; dx <= shellRadius; dx++) {
            for (int dz = -shellRadius; dz <= shellRadius; dz++) {
                for (int dy = 0; dy <= shellDepth; dy++) {
                    BlockPos p = center.offset(dx, -dy, dz);
                    if (level.isOutsideBuildHeight(p)) {
                        continue;
                    }

                    BlockState current = level.getBlockState(p);
                    if (current.isAir()) {
                        continue;
                    }

                    if (current.getFluidState().isSource()) {
                        continue;
                    }

                    if (isAdjacentToWater(level, p)) {
                        BlockState barrierState = cfg.barrier().getState(random, p);
                        level.setBlock(p, barrierState, 2);
                    }
                }
            }
        }

        return true;
    }

    private static boolean isAdjacentToWater(LevelAccessor level, BlockPos pos) {
        for (var dir : net.minecraft.core.Direction.values()) {
            BlockPos n = pos.relative(dir);
            if (level.getBlockState(n).getFluidState().isSource()) {
                return true;
            }
        }
        return false;
    }
}
