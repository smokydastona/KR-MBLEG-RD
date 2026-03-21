package com.kruemblegard.blockentity;

import com.kruemblegard.block.PressureSensorBlock;
import com.kruemblegard.init.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PressureSensorBlockEntity extends CephalariMachineBlockEntity {
    private float needle;

    public PressureSensorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRESSURE_SENSOR.get(), pos, state);
        this.needle = state.getValue(PressureSensorBlock.SIGNAL) / 15.0F;
    }

    public float getNeedle() {
        return needle;
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, PressureSensorBlockEntity be) {
        float target = state.getValue(PressureSensorBlock.SIGNAL) / 15.0F;
        be.needle = Mth.lerp(0.25F, be.needle, target);
        be.stepAnim(0.01F);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PressureSensorBlockEntity be) {
        long t = level.getGameTime();
        if (((t + pos.asLong()) % 40) != 0) {
            return;
        }
        if (state.getValue(PressureSensorBlock.SIGNAL) > 0) {
            be.recordActiveSample(40);
        }
    }
}
