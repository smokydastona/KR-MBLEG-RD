package com.kruemblegard.blockentity;

import com.kruemblegard.block.VentPistonBlock;
import com.kruemblegard.init.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class VentPistonBlockEntity extends CephalariMachineBlockEntity {
    private float extension;

    public VentPistonBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VENT_PISTON.get(), pos, state);
        this.extension = state.getValue(VentPistonBlock.EXTENSION);
    }

    public float getExtension() {
        return extension;
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, VentPistonBlockEntity be) {
        float target = state.getValue(VentPistonBlock.EXTENSION);
        be.extension = Mth.lerp(0.35F, be.extension, target);
        be.stepAnim(be.extension > 0.0F ? 0.025F : 0.0F);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, VentPistonBlockEntity be) {
        long t = level.getGameTime();
        if (((t + pos.asLong()) % 20) != 0) {
            return;
        }
        if (state.getValue(VentPistonBlock.EXTENSION) > 0) {
            be.recordActiveSample(20);
        }
    }
}
