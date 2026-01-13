package com.kruemblegard.block;

import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.world.WayfallSurfaceBloom;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public class RunegrowthBlock extends SpreadingSnowyDirtBlock implements BonemealableBlock {

    public RunegrowthBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Acts like a grass block. If unable to survive, it falls back to Wayfall dirt.
        if (!canRemainRunegrowth(level, pos)) {
            level.setBlock(pos, ModBlocks.FAULT_DUST.get().defaultBlockState(), 2);
            return;
        }

        if (level.getMaxLocalRawBrightness(pos.above()) >= 9) {
            BlockState spreadState = this.defaultBlockState();
            for (int i = 0; i < 4; i++) {
                BlockPos targetPos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                BlockState targetState = level.getBlockState(targetPos);
                if (isSpreadableWayfallDirt(targetState) && canRemainRunegrowth(level, targetPos)) {
                    level.setBlock(targetPos, spreadState, 2);
                }
            }

            // Ambient "surface bonemeal" pulse: Runegrowth occasionally stitches Paleweft flora onto nearby soils.
            if (random.nextInt(8) == 0) {
                WayfallSurfaceBloom.bloomFromRunegrowth(level, pos, random, 6);
            }
        }
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
        WayfallSurfaceBloom.bloomFromRunegrowth(level, pos, random, 18);
    }

    private static boolean isSpreadableWayfallDirt(BlockState state) {
        // Explicit whitelist to avoid spreading onto podzol-like or moss blocks.
        return state.is(ModBlocks.FAULT_DUST.get());
    }

    private static boolean canRemainRunegrowth(ServerLevel level, BlockPos pos) {
        BlockPos abovePos = pos.above();
        BlockState above = level.getBlockState(abovePos);

        // If there's a source fluid above (e.g., water), it should die.
        if (!above.getFluidState().isEmpty() && above.getFluidState().isSource()) {
            return false;
        }

        int light = level.getMaxLocalRawBrightness(abovePos);
        int opacity = above.getLightBlock(level, abovePos);
        return !(light < 4 && opacity > 2);
    }
}
