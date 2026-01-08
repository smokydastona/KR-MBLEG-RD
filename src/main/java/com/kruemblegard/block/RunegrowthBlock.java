package com.kruemblegard.block;

import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.registry.ModTags;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;

public class RunegrowthBlock extends SpreadingSnowyDirtBlock {

    public RunegrowthBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Acts like a grass block, but is powered by Waystone energy.
        // If unpowered (or unable to survive), it falls back to Wayfall dirt.
        if (!isNearWaystoneEnergy(level, pos, 6)) {
            level.setBlock(pos, ModBlocks.FAULT_DUST.get().defaultBlockState(), 2);
            return;
        }

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
        }
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

    private static boolean isNearWaystoneEnergy(ServerLevel level, BlockPos pos, int radius) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    cursor.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    if (level.getBlockState(cursor).is(ModTags.Blocks.WAYSTONE_ENERGY_SOURCES)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
