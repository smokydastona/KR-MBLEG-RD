package com.kruemblegard.blockentity;

import com.kruemblegard.block.PressureValveBlock;
import com.kruemblegard.init.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PressureValveBlockEntity extends CephalariMachineBlockEntity {
    private float openness;

    public PressureValveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRESSURE_VALVE.get(), pos, state);
        this.openness = state.getValue(PressureValveBlock.POWERED) ? 1.0F : 0.0F;
    }

    public float getOpenness() {
        return openness;
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, PressureValveBlockEntity be) {
        float target = state.getValue(PressureValveBlock.POWERED) ? 1.0F : 0.0F;
        be.openness = Mth.lerp(0.25F, be.openness, target);
        be.stepAnim(be.openness > 0.0F ? 0.01F : 0.0F);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PressureValveBlockEntity be) {
        long t = level.getGameTime();
        if (((t + pos.asLong()) % 40) != 0) {
            return;
        }
        if (state.getValue(PressureValveBlock.POWERED)) {
            be.recordActiveSample(40);
        }
    }
}
