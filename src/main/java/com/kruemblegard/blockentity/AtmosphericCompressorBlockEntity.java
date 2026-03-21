package com.kruemblegard.blockentity;

import com.kruemblegard.block.AtmosphericCompressorBlock;
import com.kruemblegard.init.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AtmosphericCompressorBlockEntity extends CephalariMachineBlockEntity {
    public AtmosphericCompressorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ATMOSPHERIC_COMPRESSOR.get(), pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, AtmosphericCompressorBlockEntity be) {
        int stability = state.getValue(AtmosphericCompressorBlock.STABILITY_LEVEL);
        float speed = stability <= 0 ? 0.0F : (0.012F + (stability * 0.010F));
        be.stepAnim(speed);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AtmosphericCompressorBlockEntity be) {
        long t = level.getGameTime();
        if (((t + pos.asLong()) % 40) != 0) {
            return;
        }
        if (state.getValue(AtmosphericCompressorBlock.STABILITY_LEVEL) > 0) {
            be.recordActiveSample(40);
        }
    }
}
