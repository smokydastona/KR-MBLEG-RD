package com.kruemblegard.world.arena;

import com.kruemblegard.init.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public final class ArenaBuilder {
    private ArenaBuilder() {}

    public static void placeFloorLayer(ServerLevel level, BlockPos center, int radius, int relY) {
        BlockState floor = ModBlocks.ATTUNED_STONE.get().defaultBlockState();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz <= radius * radius) {
                    BlockPos pos = center.offset(dx, relY, dz);
                    level.setBlock(pos, floor, 3);
                }
            }
        }
    }

    public static void placeStandingStones(ServerLevel level, BlockPos center, int radius) {
        RandomSource random = level.random;

        int stones = 12;
        for (int i = 0; i < stones; i++) {
            double angle = (Math.PI * 2.0 / stones) * i;
            int x = Mth.floor(Math.cos(angle) * (radius + 1));
            int z = Mth.floor(Math.sin(angle) * (radius + 1));

            BlockPos base = center.offset(x, 0, z);
            int height = 4 + random.nextInt(3);

            for (int y = 0; y < height; y++) {
                level.setBlock(base.above(y), ModBlocks.STANDING_STONE.get().defaultBlockState(), 3);
            }
        }
    }

    public static AABB getArenaBounds(BlockPos center, int radius) {
        return new AABB(
                center.offset(-radius, -6, -radius),
                center.offset(radius, 12, radius)
        );
    }
}
