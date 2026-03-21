package com.kruemblegard.blockentity;

import com.kruemblegard.block.PressureClutchBlock;
import com.kruemblegard.init.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PressureClutchBlockEntity extends CephalariMachineBlockEntity {
    private float engagement;

    public PressureClutchBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRESSURE_CLUTCH.get(), pos, state);
        this.engagement = state.getValue(PressureClutchBlock.POWERED) ? 1.0F : 0.0F;
    }

    public float getEngagement() {
        return engagement;
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, PressureClutchBlockEntity be) {
        float target = state.getValue(PressureClutchBlock.POWERED) ? 1.0F : 0.0F;
        be.engagement = Mth.lerp(0.25F, be.engagement, target);
        be.stepAnim(be.engagement > 0.0F ? 0.015F : 0.0F);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PressureClutchBlockEntity be) {
        long t = level.getGameTime();
        if (((t + pos.asLong()) % 40) != 0) {
            return;
        }
        if (state.getValue(PressureClutchBlock.POWERED)) {
            be.recordActiveSample(40);
        }
    }
}
