package com.kruemblegard.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Wayfall farmland analogue, created by hoeing Wayfall soils.
 */
public class RubbleTilthBlock extends FarmBlock {

    public RubbleTilthBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Preserve vanilla farmland behavior.
        super.randomTick(state, level, pos, random);
    }
}
