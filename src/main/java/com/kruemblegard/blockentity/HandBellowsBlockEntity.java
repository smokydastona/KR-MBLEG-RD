package com.kruemblegard.blockentity;

import com.kruemblegard.block.HandBellowsBlock;
import com.kruemblegard.init.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class HandBellowsBlockEntity extends CephalariMachineBlockEntity {
    private static final String TAG_LAST_PUMP_GAME_TIME = "LastPumpGameTime";

    private long lastPumpGameTime;

    public HandBellowsBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HAND_BELLOWS.get(), pos, state);
    }

    public boolean canPump(Level level) {
        return level.getGameTime() - lastPumpGameTime >= 6;
    }

    public void markPumped(Level level) {
        lastPumpGameTime = level.getGameTime();
        recordActiveSample(1);
        setChanged();
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, HandBellowsBlockEntity be) {
        if (state.getValue(HandBellowsBlock.ACTIVE)) {
            be.stepAnim(0.35F);
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, HandBellowsBlockEntity be) {
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (lastPumpGameTime != 0L) {
            tag.putLong(TAG_LAST_PUMP_GAME_TIME, lastPumpGameTime);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        lastPumpGameTime = tag.getLong(TAG_LAST_PUMP_GAME_TIME);
    }
}