package com.kruemblegard.block;

import com.kruemblegard.init.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WayfallPlantBlock extends BushBlock {
    public WayfallPlantBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(BlockTags.DIRT)
                || state.is(net.minecraft.world.level.block.Blocks.END_STONE)
                || state.is(ModBlocks.ATTUNED_STONE.get());
    }
}
