package com.kruemblegard.blockentity;

import com.kruemblegard.block.CrystalInfuserBlock;
import com.kruemblegard.init.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CrystalInfuserBlockEntity extends CephalariMachineBlockEntity {
    private float glow;

    public CrystalInfuserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRYSTAL_INFUSER.get(), pos, state);
        this.glow = state.getValue(CrystalInfuserBlock.INFUSE_PHASE) / 3.0F;
    }

    public float getGlow() {
        return glow;
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, CrystalInfuserBlockEntity be) {
        float target = state.getValue(CrystalInfuserBlock.INFUSE_PHASE) / 3.0F;
        be.glow = Mth.lerp(0.25F, be.glow, target);
        be.stepAnim(state.getValue(CrystalInfuserBlock.POWERED) ? (0.015F + be.glow * 0.02F) : 0.0F);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CrystalInfuserBlockEntity be) {
        long t = level.getGameTime();
        if (((t + pos.asLong()) % 80) != 0) {
            return;
        }
        if (state.getValue(CrystalInfuserBlock.POWERED)) {
            be.recordActiveSample(80);
        }
    }
}
