package com.kruemblegard.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A sapling that never grows via random ticks; growth requires bonemeal.
 */
public class BonemealOnlyWayfallSaplingBlock extends WayfallFeatureSaplingBlock {
    public BonemealOnlyWayfallSaplingBlock(AbstractTreeGrower treeGrower, Properties properties) {
        super(treeGrower, properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Intentionally no-op: bonemeal-only growth.
    }
}
