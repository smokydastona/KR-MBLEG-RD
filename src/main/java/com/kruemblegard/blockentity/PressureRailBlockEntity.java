package com.kruemblegard.blockentity;

import com.kruemblegard.block.PressureRailBlock;
import com.kruemblegard.init.ModBlockEntities;
import com.kruemblegard.rotationlogic.RotationUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PressureRailBlockEntity extends CephalariMachineBlockEntity {
    public PressureRailBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRESSURE_RAIL.get(), pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, PressureRailBlockEntity be) {
        boolean powered = state.getValue(PressureRailBlock.POWERED);
        int rotation = RotationUtil.getRotationLevel(level, pos);
        int phase = state.getValue(PressureRailBlock.PULSE_PHASE);
        float speed = powered ? (0.01F + (rotation * 0.005F) + (phase * 0.002F)) : 0.0F;
        be.stepAnim(speed);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PressureRailBlockEntity be) {
        long t = level.getGameTime();
        if (((t + pos.asLong()) % 40) != 0) {
            return;
        }
        if (state.getValue(PressureRailBlock.POWERED)) {
            be.recordActiveSample(40);
        }
    }
}
