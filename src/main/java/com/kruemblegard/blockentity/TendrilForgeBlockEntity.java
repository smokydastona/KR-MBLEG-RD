package com.kruemblegard.blockentity;

import com.kruemblegard.init.ModBlockEntities;
import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.registry.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class TendrilForgeBlockEntity extends CephalariWorkstationBlockEntity {
    public TendrilForgeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TENDRIL_FORGE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TendrilForgeBlockEntity blockEntity) {
        blockEntity.tickServer();
    }

    @Override
    protected ItemStack createResult(ItemStack inputStack) {
        if (inputStack.is(ModItems.VOLATILE_RESIN.get())) {
            return new ItemStack(ModItems.TENDRIL_STRANDS.get(), 2);
        }
        if (inputStack.is(ModItems.RUNE_PETALS.get())) {
            return new ItemStack(ModItems.TENDRIL_STRANDS.get());
        }
        if (inputStack.is(ModItems.RUNIC_SCRAP.get())) {
            return new ItemStack(ModItems.RESONANCE_SHARD.get());
        }
        if (inputStack.is(ModItems.ATTUNED_RUNE_SHARD.get())) {
            return new ItemStack(ModItems.RESONANCE_SHARD.get(), 2);
        }
        if (inputStack.is(ModItems.RUNIC_DEBRIS_ITEM.get())) {
            return new ItemStack(ModItems.MOISTURE_STONE.get(), 2);
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected int getProcessTime(ItemStack inputStack) {
        return inputStack.is(ModItems.RUNIC_DEBRIS_ITEM.get()) ? 240 : 160;
    }

    @Override
    protected int getWorkSpeed(Level level, BlockPos pos) {
        int speed = 1;
        int bonusBlocks = 0;
        for (BlockPos nearby : BlockPos.betweenClosed(pos.offset(-2, -1, -2), pos.offset(2, 1, 2))) {
            BlockState state = level.getBlockState(nearby);
            if (state.is(ModBlocks.ATTUNED_STONE.get())
                    || state.is(ModBlocks.RUNIC_DEBRIS.get())
                    || state.is(ModBlocks.STANDING_STONE.get())
                    || state.is(ModBlocks.WAYPOINT_MOLD.get())) {
                bonusBlocks++;
            }
        }

        speed += Math.min(3, bonusBlocks / 2);

        if (!level.canSeeSky(pos.above())) {
            speed++;
        }

        return speed;
    }

    @Override
    protected SoundEvent getCompleteSound() {
        return SoundEvents.AMETHYST_BLOCK_CHIME;
    }
}