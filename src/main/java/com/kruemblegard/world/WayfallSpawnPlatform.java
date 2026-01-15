package com.kruemblegard.world;

import com.kruemblegard.init.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class WayfallSpawnPlatform {
    private WayfallSpawnPlatform() {}

    private static final int DEFAULT_PLATFORM_Y = 160;
    private static final int PLATFORM_HALF_SIZE = 4;

    public static BlockPos ensureSpawnLanding(ServerLevel wayfall) {
        BlockPos spawn = wayfall.getSharedSpawnPos();

        int platformY = MthClamp.clamp(DEFAULT_PLATFORM_Y, wayfall.getMinBuildHeight() + 8, wayfall.getMaxBuildHeight() - 8);
        BlockPos center = new BlockPos(spawn.getX(), platformY, spawn.getZ());

        // Ensure chunk is loaded before writing blocks.
        wayfall.getChunk(center);

        BlockState block = ModBlocks.FRACTURED_WAYROCK.get().defaultBlockState();

        for (int dx = -PLATFORM_HALF_SIZE; dx <= PLATFORM_HALF_SIZE; dx++) {
            for (int dz = -PLATFORM_HALF_SIZE; dz <= PLATFORM_HALF_SIZE; dz++) {
                BlockPos p = center.offset(dx, 0, dz);
                wayfall.setBlockAndUpdate(p, block);
                wayfall.setBlockAndUpdate(p.below(), block);
            }
        }

        BlockPos landing = center.above();
        wayfall.setBlockAndUpdate(landing, Blocks.AIR.defaultBlockState());
        wayfall.setBlockAndUpdate(landing.above(), Blocks.AIR.defaultBlockState());
        wayfall.setBlockAndUpdate(landing.above(2), Blocks.AIR.defaultBlockState());

        return landing;
    }

    /** Minimal clamp utility to avoid pulling extra math dependencies into world-only helper. */
    private static final class MthClamp {
        private static int clamp(int value, int min, int max) {
            if (value < min) {
                return min;
            }
            return Math.min(value, max);
        }
    }
}
