package com.kruemblegard.blockentity;

import com.kruemblegard.block.PressureTurbineBlock;
import com.kruemblegard.init.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PressureTurbineBlockEntity extends CephalariMachineBlockEntity {
    public PressureTurbineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRESSURE_TURBINE.get(), pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, PressureTurbineBlockEntity be) {
        int speedLevel = state.getValue(PressureTurbineBlock.ROTATION_SPEED);
        float speed = speedLevel <= 0 ? 0.0F : (0.03F + speedLevel * 0.025F);
        be.stepAnim(speed);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PressureTurbineBlockEntity be) {
        long t = level.getGameTime();
        if (((t + pos.asLong()) % 20) != 0) {
            return;
        }
        if (state.getValue(PressureTurbineBlock.ROTATION_SPEED) > 0) {
            be.recordActiveSample(20);
        }
    }
}
