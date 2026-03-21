package com.kruemblegard.blockentity;

import com.kruemblegard.block.BuoyancyLiftPlatformBlock;
import com.kruemblegard.init.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BuoyancyLiftPlatformBlockEntity extends CephalariMachineBlockEntity {
    public BuoyancyLiftPlatformBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BUOYANCY_LIFT_PLATFORM.get(), pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, BuoyancyLiftPlatformBlockEntity be) {
        BuoyancyLiftPlatformBlock.LiftState lift = state.getValue(BuoyancyLiftPlatformBlock.LIFT_STATE);
        float speed = switch (lift) {
            case RISING -> 0.03F;
            case FALLING -> -0.02F;
            default -> 0.0F;
        };
        be.stepAnim(speed);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BuoyancyLiftPlatformBlockEntity be) {
        long t = level.getGameTime();
        if (((t + pos.asLong()) % 20) != 0) {
            return;
        }
        if (state.getValue(BuoyancyLiftPlatformBlock.LIFT_STATE) != BuoyancyLiftPlatformBlock.LiftState.IDLE) {
            be.recordActiveSample(20);
        }
    }
}
