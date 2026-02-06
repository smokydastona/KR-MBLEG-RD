package com.kruemblegard.block;

import com.kruemblegard.init.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.ChorusPlantBlock;
import net.minecraft.world.level.block.state.BlockState;

public class AshspireEmberbloomBlock extends ChorusFlowerBlock {

    public AshspireEmberbloomBlock(ChorusPlantBlock plant, Properties properties) {
        super(plant, properties);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        if (!(below.is(ModBlocks.ASHSPIRE_CACTUS.get()) || below.is(ModBlocks.ASHSPIRE_COLOSSUS.get()))) {
            return false;
        }

        // Vanilla chorus flower also needs a ceiling-free space.
        if (!level.getBlockState(pos.above()).getFluidState().isEmpty()) {
            return false;
        }

        // Extra safety: don't allow sideways face attachment.
        return below.isFaceSturdy(level, pos.below(), Direction.UP) || below.is(ModBlocks.ASHSPIRE_CACTUS.get()) || below.is(ModBlocks.ASHSPIRE_COLOSSUS.get());
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);

        // Make Ashspire branch out more than vanilla chorus by giving it
        // an occasional extra growth attempt.
        if (random.nextInt(2) == 0) {
            super.randomTick(state, level, pos, random);
        }
    }
}
