package com.kruemblegard.block;

import com.kruemblegard.init.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SporeBlossomBlock;
import net.minecraft.world.level.block.state.BlockState;

public class NightFlowerBlock extends SporeBlossomBlock {

    public NightFlowerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState above = level.getBlockState(pos.above());
        if (above.isFaceSturdy(level, pos.above(), Direction.DOWN)) {
            return true;
        }

        return above.is(ModBlocks.ASHSPIRE_CACTUS.get())
                || above.is(ModBlocks.ASHSPIRE_COLOSSUS.get())
                || above.is(ModBlocks.ASHSPIRE_EMBERBLOOM.get());
    }
}
