package com.kruemblegard.block;

import com.kruemblegard.init.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class AshspireEmberbloomBlock extends Block {

    public AshspireEmberbloomBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        if (!(below.is(ModBlocks.ASHSPIRE_CACTUS.get()) || below.is(ModBlocks.ASHSPIRE_COLOSSUS.get()))) {
            return false;
        }

        return level.getBlockState(pos.above()).getFluidState().isEmpty();
    }
}
