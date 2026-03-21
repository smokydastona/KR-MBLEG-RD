package com.kruemblegard.blockentity;

import com.kruemblegard.block.PneumaticCatapultBlock;
import com.kruemblegard.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PneumaticCatapultBlockEntity extends CephalariMachineBlockEntity {
    private float charge;

    public PneumaticCatapultBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PNEUMATIC_CATAPULT.get(), pos, state);
        this.charge = state.getValue(PneumaticCatapultBlock.CHARGE_LEVEL) / 3.0F;
    }

    public float getCharge() {
        return charge;
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, PneumaticCatapultBlockEntity be) {
        float target = state.getValue(PneumaticCatapultBlock.CHARGE_LEVEL) / 3.0F;
        be.charge = Mth.lerp(0.30F, be.charge, target);
        be.stepAnim(be.charge > 0.0F ? (0.01F + be.charge * 0.03F) : 0.0F);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PneumaticCatapultBlockEntity be) {
        long t = level.getGameTime();
        if (((t + pos.asLong()) % 40) != 0) {
            return;
        }
        if (state.getValue(PneumaticCatapultBlock.CHARGE_LEVEL) > 0) {
            be.recordActiveSample(40);
        }
    }
}
