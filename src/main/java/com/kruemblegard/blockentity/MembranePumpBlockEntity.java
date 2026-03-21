package com.kruemblegard.blockentity;

import com.kruemblegard.block.MembranePumpBlock;
import com.kruemblegard.init.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MembranePumpBlockEntity extends CephalariMachineBlockEntity {
    public MembranePumpBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MEMBRANE_PUMP.get(), pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, MembranePumpBlockEntity be) {
        boolean powered = state.getValue(MembranePumpBlock.POWERED);
        int pulseRate = state.getValue(MembranePumpBlock.PULSE_RATE);
        float speed = powered ? (0.04F + (pulseRate * 0.03F)) : 0.0F;
        be.stepAnim(speed);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MembranePumpBlockEntity be) {
        long t = level.getGameTime();
        if (((t + pos.asLong()) % 20) != 0) {
            return;
        }
        if (state.getValue(MembranePumpBlock.POWERED)) {
            be.recordActiveSample(20);
        }
    }
}
