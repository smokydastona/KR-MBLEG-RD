package com.kruemblegard.blockentity;

import com.kruemblegard.block.PressureSequencerBlock;
import com.kruemblegard.init.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PressureSequencerBlockEntity extends CephalariMachineBlockEntity {
    public PressureSequencerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRESSURE_SEQUENCER.get(), pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, PressureSequencerBlockEntity be) {
        boolean powered = state.getValue(PressureSequencerBlock.POWERED);
        int step = state.getValue(PressureSequencerBlock.STEP);
        float speed = powered ? (0.02F + step * 0.005F) : 0.0F;
        be.stepAnim(speed);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PressureSequencerBlockEntity be) {
        long t = level.getGameTime();
        if (((t + pos.asLong()) % 40) != 0) {
            return;
        }
        if (state.getValue(PressureSequencerBlock.POWERED)) {
            be.recordActiveSample(40);
        }
    }
}
