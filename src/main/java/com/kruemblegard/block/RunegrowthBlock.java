package com.kruemblegard.block;

import com.kruemblegard.init.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class RunegrowthBlock extends WayfallSurfaceBlock {

    public RunegrowthBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!VeilgrowthBlock.isNearWaystoneEnergy(level, pos, 6)) {
            level.setBlock(pos, ModBlocks.VEILGROWTH.get().defaultBlockState(), 2);
        }
    }
}
