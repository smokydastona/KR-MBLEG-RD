package com.kruemblegard.world;

import com.kruemblegard.util.PaleweftRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public final class PaleweftBloom {
    private PaleweftBloom() {}

    /**
     * Bonemeal-like burst that places a small mix of Paleweft grass variants near the origin.
     * Intended to be called from Runegrowth bonemeal (Wayfall-only flavor), without any periodic ticking.
     */
    public static void bloom(ServerLevel level, BlockPos origin, RandomSource random, int attempts) {
        Block rubbleTilth = PaleweftRegistry.getBlock(PaleweftRegistry.RUBBLE_TILTH_ID);
        Block paleweftGrass = PaleweftRegistry.getBlock(PaleweftRegistry.PALEWEFT_GRASS_ID);
        Block paleweftTall = PaleweftRegistry.getBlock(PaleweftRegistry.PALEWEFT_TALL_GRASS_ID);

        if (paleweftGrass == null) {
            return;
        }

        for (int i = 0; i < attempts; i++) {
            int x = origin.getX() + random.nextInt(9) - 4;
            int z = origin.getZ() + random.nextInt(9) - 4;

            BlockPos top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z));
            BlockPos groundPos = top.below();
            BlockState ground = level.getBlockState(groundPos);

            boolean tillable = ground.is(PaleweftRegistry.RUBBLE_TILLABLE_TAG);
            if (!tillable && (rubbleTilth == null || !ground.is(rubbleTilth))) {
                continue;
            }

            BlockPos plantPos = groundPos.above();
            if (!level.getBlockState(plantPos).isAir()) {
                continue;
            }

            BlockState shortState = paleweftGrass.defaultBlockState();
            if (!shortState.canSurvive(level, plantPos)) {
                continue;
            }

            // ~25% chance: tall, if possible.
            if (paleweftTall != null && random.nextInt(4) == 0 && level.getBlockState(plantPos.above()).isAir()) {
                BlockState tallState = paleweftTall.defaultBlockState();
                if (tallState.canSurvive(level, plantPos)) {
                    DoublePlantBlock.placeAt(level, tallState, plantPos, 2);
                    continue;
                }
            }

            level.setBlock(plantPos, shortState, Block.UPDATE_CLIENTS);
        }
    }
}
