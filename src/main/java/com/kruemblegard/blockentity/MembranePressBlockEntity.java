package com.kruemblegard.blockentity;

import com.kruemblegard.block.MembranePressBlock;
import com.kruemblegard.init.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MembranePressBlockEntity extends CephalariMachineBlockEntity {
    private float press;

    public MembranePressBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MEMBRANE_PRESS.get(), pos, state);
        this.press = state.getValue(MembranePressBlock.PRESS_PHASE) / 3.0F;
    }

    public float getPress() {
        return press;
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, MembranePressBlockEntity be) {
        float target = state.getValue(MembranePressBlock.PRESS_PHASE) / 3.0F;
        be.press = Mth.lerp(0.30F, be.press, target);
        be.stepAnim(state.getValue(MembranePressBlock.POWERED) ? (0.01F + be.press * 0.03F) : 0.0F);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MembranePressBlockEntity be) {
        long t = level.getGameTime();
        if (((t + pos.asLong()) % 80) != 0) {
            return;
        }
        if (state.getValue(MembranePressBlock.POWERED)) {
            be.recordActiveSample(80);
        }
    }
}
