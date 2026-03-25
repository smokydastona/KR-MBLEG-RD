package com.kruemblegard.blockentity;

import com.kruemblegard.block.CephalariWorkstationBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.Containers;

public abstract class CephalariWorkstationBlockEntity extends BlockEntity {
    private static final String INPUT_TAG = "Input";
    private static final String OUTPUT_TAG = "Output";
    private static final String PROGRESS_TAG = "Progress";
    private static final String MAX_PROGRESS_TAG = "MaxProgress";

    protected ItemStack input = ItemStack.EMPTY;
    protected ItemStack output = ItemStack.EMPTY;
    protected int progress;
    protected int maxProgress = 200;

    protected CephalariWorkstationBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public InteractionResult handleUse(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (!output.isEmpty()) {
                giveStackToPlayer(player, output.copy());
                output = ItemStack.EMPTY;
                setChangedAndSync();
                return InteractionResult.CONSUME;
            }
            if (!input.isEmpty()) {
                giveStackToPlayer(player, input.copy());
                input = ItemStack.EMPTY;
                progress = 0;
                setChangedAndSync();
                return InteractionResult.CONSUME;
            }
            return InteractionResult.PASS;
        }

        if (held.isEmpty()) {
            if (!output.isEmpty()) {
                giveStackToPlayer(player, output.copy());
                output = ItemStack.EMPTY;
                setChangedAndSync();
                return InteractionResult.CONSUME;
            }
            return InteractionResult.PASS;
        }

        if (!canAcceptInput(held)) {
            return InteractionResult.PASS;
        }

        ItemStack inserted = held.copyWithCount(1);
        if (input.isEmpty()) {
            input = inserted;
            maxProgress = getProcessTime(input);
        } else {
            input.grow(1);
        }

        held.shrink(1);
        setChangedAndSync();
        return InteractionResult.CONSUME;
    }

    public void dropContents() {
        if (level == null || level.isClientSide) {
            return;
        }
        if (!input.isEmpty()) {
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), input);
            input = ItemStack.EMPTY;
        }
        if (!output.isEmpty()) {
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), output);
            output = ItemStack.EMPTY;
        }
    }

    public int getComparatorValue() {
        if (!output.isEmpty()) {
            return 15;
        }
        if (!input.isEmpty()) {
            return 4 + Math.min(10, (progress * 10) / Math.max(1, maxProgress));
        }
        return 0;
    }

    protected void tickServer() {
        if (level == null || level.isClientSide) {
            return;
        }

        boolean shouldBeActive = false;
        if (!input.isEmpty()) {
            ItemStack recipeInput = input.copyWithCount(1);
            ItemStack result = createResult(recipeInput);
            if (!result.isEmpty() && canStoreOutput(result)) {
                shouldBeActive = true;
                progress += getWorkSpeed(level, worldPosition);
                if (progress >= maxProgress) {
                    finishCycle(result);
                }
            } else {
                progress = 0;
            }
        } else {
            progress = 0;
        }

        updateActiveState(shouldBeActive);
    }

    protected void finishCycle(ItemStack result) {
        if (output.isEmpty()) {
            output = result.copy();
        } else {
            output.grow(result.getCount());
        }

        input.shrink(1);
        if (input.isEmpty()) {
            input = ItemStack.EMPTY;
            progress = 0;
        } else {
            progress = 0;
            maxProgress = getProcessTime(input);
        }

        if (level instanceof ServerLevel serverLevel) {
            SoundEvent sound = getCompleteSound();
            if (sound != null) {
                serverLevel.playSound(null, worldPosition, sound, SoundSource.BLOCKS, 0.75F, 1.0F + serverLevel.random.nextFloat() * 0.2F);
            }
        }

        setChangedAndSync();
    }

    protected boolean canAcceptInput(ItemStack stack) {
        ItemStack result = createResult(stack.copyWithCount(1));
        if (result.isEmpty()) {
            return false;
        }
        if (!input.isEmpty() && !ItemStack.isSameItemSameTags(input, stack)) {
            return false;
        }
        if (!input.isEmpty() && input.getCount() >= Math.min(input.getMaxStackSize(), 16)) {
            return false;
        }
        return canStoreOutput(result);
    }

    protected boolean canStoreOutput(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (output.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameTags(output, stack)) {
            return false;
        }
        return output.getCount() + stack.getCount() <= output.getMaxStackSize();
    }

    protected void giveStackToPlayer(Player player, ItemStack stack) {
        if (!player.addItem(stack)) {
            player.drop(stack, false);
        }
    }

    protected void setChangedAndSync() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    protected void updateActiveState(boolean active) {
        if (level == null) {
            return;
        }
        BlockState state = getBlockState();
        if (state.getBlock() instanceof CephalariWorkstationBlock && state.getValue(CephalariWorkstationBlock.ACTIVE) != active) {
            level.setBlock(worldPosition, state.setValue(CephalariWorkstationBlock.ACTIVE, active), 3);
        }
    }

    protected abstract ItemStack createResult(ItemStack inputStack);

    protected abstract int getProcessTime(ItemStack inputStack);

    protected abstract int getWorkSpeed(Level level, BlockPos pos);

    protected abstract SoundEvent getCompleteSound();

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!input.isEmpty()) {
            tag.put(INPUT_TAG, input.save(new CompoundTag()));
        }
        if (!output.isEmpty()) {
            tag.put(OUTPUT_TAG, output.save(new CompoundTag()));
        }
        tag.putInt(PROGRESS_TAG, progress);
        tag.putInt(MAX_PROGRESS_TAG, maxProgress);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        input = tag.contains(INPUT_TAG) ? ItemStack.of(tag.getCompound(INPUT_TAG)) : ItemStack.EMPTY;
        output = tag.contains(OUTPUT_TAG) ? ItemStack.of(tag.getCompound(OUTPUT_TAG)) : ItemStack.EMPTY;
        progress = tag.getInt(PROGRESS_TAG);
        maxProgress = Math.max(1, tag.getInt(MAX_PROGRESS_TAG));
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }
}