package com.kruemblegard.blockentity;

import com.kruemblegard.block.ConveyorMembraneBlock;
import com.kruemblegard.init.ModBlockEntities;
import com.kruemblegard.rotationlogic.RotationUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ConveyorMembraneBlockEntity extends CephalariMachineBlockEntity {
    public ConveyorMembraneBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CONVEYOR_MEMBRANE.get(), pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, ConveyorMembraneBlockEntity be) {
        int rotation = RotationUtil.getRotationLevel(level, pos);
        float speed = (rotation <= 0) ? 0.0F : (0.02F + rotation * 0.015F);
        be.stepAnim(speed);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ConveyorMembraneBlockEntity be) {
        long t = level.getGameTime();
        if (((t + pos.asLong()) % 40) != 0) {
            return;
        }
        if (state.getValue(ConveyorMembraneBlock.PULSE_PHASE) != 0) {
            be.recordActiveSample(40);
        }
    }
}
