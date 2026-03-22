package com.kruemblegard.blockentity;

import com.kruemblegard.init.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ThermoCondenserBlockEntity extends CephalariMachineBlockEntity {
    private static final String TAG_BURN_TIME = "BurnTime";
    private static final String TAG_BURN_TIME_TOTAL = "BurnTimeTotal";

    private int burnTime;
    private int burnTimeTotal;

    public ThermoCondenserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.THERMO_CONDENSER.get(), pos, state);
    }

    public int getBurnTime() {
        return burnTime;
    }

    public boolean isLit() {
        return burnTime > 0;
    }

    public void startBurn(int fuelBurnTime) {
        burnTime = Math.max(0, fuelBurnTime);
        burnTimeTotal = Math.max(1, fuelBurnTime);
        setChanged();
    }

    public void consumeBurnTime(int amount) {
        if (burnTime <= 0 || amount <= 0) {
            return;
        }

        burnTime = Math.max(0, burnTime - amount);
        if (burnTime == 0) {
            burnTimeTotal = 0;
        }
        setChanged();
    }

    public int getHeatLevel() {
        if (burnTime <= 0 || burnTimeTotal <= 0) {
            return 0;
        }

        float ratio = burnTime / (float) burnTimeTotal;
        return Mth.clamp(1 + Mth.floor(ratio * 4.999F), 1, 5);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, ThermoCondenserBlockEntity be) {
        int heat = be.getHeatLevel();
        if (heat > 0) {
            be.stepAnim(0.03F + (heat * 0.015F));
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ThermoCondenserBlockEntity be) {
        long t = level.getGameTime();
        if (be.getBurnTime() > 0 && ((t + pos.asLong()) % 20) == 0) {
            be.recordActiveSample(20);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (burnTime > 0) {
            tag.putInt(TAG_BURN_TIME, burnTime);
            tag.putInt(TAG_BURN_TIME_TOTAL, burnTimeTotal);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        burnTime = tag.getInt(TAG_BURN_TIME);
        burnTimeTotal = tag.getInt(TAG_BURN_TIME_TOTAL);
    }
}