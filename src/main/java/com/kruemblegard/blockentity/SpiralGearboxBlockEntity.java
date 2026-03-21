package com.kruemblegard.blockentity;

import com.kruemblegard.block.SpiralGearboxBlock;
import com.kruemblegard.init.ModBlockEntities;
import com.kruemblegard.rotationlogic.RotationUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SpiralGearboxBlockEntity extends CephalariMachineBlockEntity {
    public SpiralGearboxBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPIRAL_GEARBOX.get(), pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, SpiralGearboxBlockEntity be) {
        int rotation = RotationUtil.getRotationLevel(level, pos);
        float ratioMul = switch (state.getValue(SpiralGearboxBlock.RATIO)) {
            case RATIO_1_2, RATIO_1_4 -> 0.8F;
            case RATIO_2_1, RATIO_4_1 -> 1.25F;
            default -> 1.0F;
        };
        float speed = (rotation <= 0) ? 0.0F : (0.02F + rotation * 0.02F) * ratioMul;
        be.stepAnim(speed);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SpiralGearboxBlockEntity be) {
        long t = level.getGameTime();
        if (((t + pos.asLong()) % 20) != 0) {
            return;
        }
        if (RotationUtil.getRotationLevel(level, pos) > 0) {
            be.recordActiveSample(20);
        }
    }
}
