package com.kruemblegard.block;

import com.kruemblegard.debug.RunegrowthTickDebug;
import com.kruemblegard.world.RunegrowthBonemeal;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Fixed-variant Runegrowth (Frostbound/Verdant/Emberwarmed).
 *
 * Uses the same server behavior as {@link RunegrowthBlock}, but without the TEMP property.
 */
public class RunegrowthVariantBlock extends SpreadingSnowyDirtBlock implements BonemealableBlock {

    public RunegrowthVariantBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        RunegrowthTickDebug.record(level);

        if (!RunegrowthBlock.canRemainRunegrowth(level, pos)) {
            level.setBlock(pos, com.kruemblegard.init.ModBlocks.FAULT_DUST.get().defaultBlockState(), 2);
            return;
        }

        if (random.nextInt(4) != 0) {
            return;
        }

        if (level.getMaxLocalRawBrightness(pos.above()) >= 9) {
            for (int i = 0; i < 4; i++) {
                BlockPos targetPos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);

                if (!level.isLoaded(targetPos)) {
                    continue;
                }

                BlockState targetState = level.getBlockState(targetPos);
                if (RunegrowthBlock.isSpreadableWayfallDirt(targetState) && RunegrowthBlock.canRemainRunegrowth(level, targetPos)) {
                    BlockState spreadState = applySnowyOnly(this.defaultBlockState(), level, targetPos);
                    level.setBlock(targetPos, spreadState, 2);
                }
            }
        }
    }

    private static BlockState applySnowyOnly(BlockState state, LevelReader level, BlockPos pos) {
        return state.setValue(SNOWY, RunegrowthBlock.isNearSnow(level, pos));
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        RunegrowthBonemeal.bonemeal(level, pos, random);
    }
}
