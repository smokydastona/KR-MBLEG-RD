package com.kruemblegard.blockentity;

import com.kruemblegard.block.SpiralShaftBlock;
import com.kruemblegard.init.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SpiralShaftBlockEntity extends CephalariMachineBlockEntity {
    public SpiralShaftBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPIRAL_SHAFT.get(), pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, SpiralShaftBlockEntity be) {
        int speedLevel = state.getValue(SpiralShaftBlock.ROTATION_SPEED);
        float speed = speedLevel <= 0 ? 0.0F : (0.02F + speedLevel * 0.02F);
        be.stepAnim(speed);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SpiralShaftBlockEntity be) {
        long t = level.getGameTime();
        if (((t + pos.asLong()) % 20) != 0) {
            return;
        }
        if (state.getValue(SpiralShaftBlock.ROTATION_SPEED) > 0) {
            be.recordActiveSample(20);
        }
    }
}
