package com.kruemblegard.world;

import com.kruemblegard.init.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;

public final class WayfallSpawnPlatform {
    private WayfallSpawnPlatform() {}

    private static final int DEFAULT_PLATFORM_Y = 160;
    private static final int ISLAND_RADIUS = 10;
    private static final int ISLAND_DEPTH = 7;

    public static BlockPos ensureSpawnLanding(ServerLevel wayfall) {
        BlockPos spawn = wayfall.getSharedSpawnPos();

        int platformY = MthClamp.clamp(DEFAULT_PLATFORM_Y, wayfall.getMinBuildHeight() + 8, wayfall.getMaxBuildHeight() - 8);
        BlockPos center = new BlockPos(spawn.getX(), platformY, spawn.getZ());

        // Ensure affected chunks are fully generated/loaded before writing blocks.
        int minChunkX = (center.getX() - ISLAND_RADIUS) >> 4;
        int maxChunkX = (center.getX() + ISLAND_RADIUS) >> 4;
        int minChunkZ = (center.getZ() - ISLAND_RADIUS) >> 4;
        int maxChunkZ = (center.getZ() + ISLAND_RADIUS) >> 4;
        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                wayfall.getChunkSource().getChunk(cx, cz, ChunkStatus.FULL, true);
            }
        }

        BlockState block = ModBlocks.FRACTURED_WAYROCK.get().defaultBlockState();

        // Build a small tapered "asteroid" island rather than a thin flat platform.
        // This keeps portal entry safe (solid landing) and matches Wayfall's floating-island aesthetics.
        for (int dx = -ISLAND_RADIUS; dx <= ISLAND_RADIUS; dx++) {
            for (int dz = -ISLAND_RADIUS; dz <= ISLAND_RADIUS; dz++) {
                double dist = Math.sqrt((double) dx * dx + (double) dz * dz);
                if (dist > (double) ISLAND_RADIUS + 0.35D) {
                    continue;
                }

                double t = 1.0D - (dist / (double) ISLAND_RADIUS);
                int columnDepth = 1 + (int) Math.floor(t * (double) ISLAND_DEPTH);

                // Keep the top surface generous and the underside tapered.
                BlockPos top = center.offset(dx, 0, dz);
                wayfall.setBlockAndUpdate(top, block);
                for (int dy = 1; dy <= columnDepth; dy++) {
                    wayfall.setBlockAndUpdate(top.below(dy), block);
                }
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
