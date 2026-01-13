package com.kruemblegard.world;

import com.kruemblegard.util.PaleweftRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class WayfallSurfaceBloom {
    private WayfallSurfaceBloom() {}

    public static void bloomFromRunegrowth(ServerLevel level, BlockPos origin, RandomSource random, int attempts) {
        Block rubbleTilth = PaleweftRegistry.getBlock(PaleweftRegistry.RUBBLE_TILTH_ID);
        Block paleweftGrass = PaleweftRegistry.getBlock(PaleweftRegistry.PALEWEFT_GRASS_ID);
        Block paleweftTall = PaleweftRegistry.getBlock(PaleweftRegistry.PALEWEFT_TALL_GRASS_ID);

        if (paleweftGrass == null) {
            return;
        }

        for (int i = 0; i < attempts; i++) {
            int dx = random.nextInt(7) - 3;
            int dz = random.nextInt(7) - 3;
            BlockPos base = origin.offset(dx, 0, dz);

            // Only bloom on "Wayfall soil" surfaces.
            BlockState ground = level.getBlockState(base);
            boolean tillable = ground.is(PaleweftRegistry.RUBBLE_TILLABLE_TAG);
            if (!tillable && (rubbleTilth == null || !ground.is(rubbleTilth))) {
                continue;
            }

            BlockPos plantPos = base.above();
            if (!level.getBlockState(plantPos).isAir()) {
                continue;
            }

            // Try to place Paleweft Grass, occasionally tall.
            BlockState grass = paleweftGrass.defaultBlockState();
            if (!grass.canSurvive(level, plantPos)) {
                continue;
            }

            if (paleweftTall != null && random.nextInt(5) == 0 && level.getBlockState(plantPos.above()).isAir()) {
                BlockState tall = paleweftTall.defaultBlockState();
                if (tall.canSurvive(level, plantPos)) {
                    net.minecraft.world.level.block.DoublePlantBlock.placeAt(level, tall, plantPos, 2);
                    continue;
                }
            }

            level.setBlock(plantPos, grass, Block.UPDATE_CLIENTS);
        }
    }
}
