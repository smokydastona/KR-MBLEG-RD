package com.kruemblegard.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class CephalariMachineBlockEntity extends BlockEntity {
    private static final String TAG_ACTIVE_TICKS = "ActiveTicks";
    private static final int ACTIVE_TICKS_DIRTY_THRESHOLD = 20 * 60;

    private int unsavedActiveTicks;

    protected int activeTicks;
    protected float anim;

    protected CephalariMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public int getActiveTicks() {
        return activeTicks;
    }

    public float getAnim() {
        return anim;
    }

    protected void stepAnim(float speed) {
        if (speed == 0.0F) {
            return;
        }

        anim += speed;
        if (anim >= Mth.TWO_PI) {
            anim -= Mth.TWO_PI;
        } else if (anim < 0.0F) {
            anim += Mth.TWO_PI;
        }
    }

    protected void recordActiveSample(int deltaTicks) {
        if (deltaTicks <= 0) {
            return;
        }

        activeTicks += deltaTicks;
        unsavedActiveTicks += deltaTicks;
        if (unsavedActiveTicks >= ACTIVE_TICKS_DIRTY_THRESHOLD) {
            unsavedActiveTicks = 0;
            setChanged();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (activeTicks > 0) {
            tag.putInt(TAG_ACTIVE_TICKS, activeTicks);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        activeTicks = tag.getInt(TAG_ACTIVE_TICKS);
        unsavedActiveTicks = 0;
    }
}
