package com.kruemblegard.blockentity;

import com.kruemblegard.block.PressureRegulatorBlock;
import com.kruemblegard.init.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PressureRegulatorBlockEntity extends CephalariMachineBlockEntity {
    private float dial;

    public PressureRegulatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRESSURE_REGULATOR.get(), pos, state);
        this.dial = state.getValue(PressureRegulatorBlock.SIGNAL) / 15.0F;
    }

    public float getDial() {
        return dial;
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, PressureRegulatorBlockEntity be) {
        float target = state.getValue(PressureRegulatorBlock.SIGNAL) / 15.0F;
        be.dial = Mth.lerp(0.20F, be.dial, target);
        be.stepAnim(0.01F);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PressureRegulatorBlockEntity be) {
        long t = level.getGameTime();
        if (((t + pos.asLong()) % 40) != 0) {
            return;
        }
        if (state.getValue(PressureRegulatorBlock.SIGNAL) > 0) {
            be.recordActiveSample(40);
        }
    }
}
