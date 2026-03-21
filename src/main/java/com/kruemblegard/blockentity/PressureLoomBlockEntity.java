package com.kruemblegard.blockentity;

import com.kruemblegard.block.PressureLoomBlock;
import com.kruemblegard.init.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PressureLoomBlockEntity extends CephalariMachineBlockEntity {
    public PressureLoomBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRESSURE_LOOM.get(), pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, PressureLoomBlockEntity be) {
        boolean powered = state.getValue(PressureLoomBlock.POWERED);
        int phase = state.getValue(PressureLoomBlock.WEAVE_PHASE);
        float speed = powered ? (0.01F + phase * 0.01F) : 0.0F;
        be.stepAnim(speed);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PressureLoomBlockEntity be) {
        long t = level.getGameTime();
        if (((t + pos.asLong()) % 40) != 0) {
            return;
        }
        if (state.getValue(PressureLoomBlock.POWERED) && state.getValue(PressureLoomBlock.WEAVE_PHASE) != 0) {
            be.recordActiveSample(40);
        }
    }
}
