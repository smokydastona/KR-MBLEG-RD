package com.kruemblegard.blockentity;

import com.kruemblegard.init.ModBlockEntities;
import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.registry.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class BrineGardenBasinBlockEntity extends CephalariWorkstationBlockEntity {
    public BrineGardenBasinBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BRINE_GARDEN_BASIN.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BrineGardenBasinBlockEntity blockEntity) {
        blockEntity.tickServer();
    }

    @Override
    protected ItemStack createResult(ItemStack inputStack) {
        if (inputStack.is(ModItems.VOLATILE_RESIN.get())) {
            return new ItemStack(ModItems.BIO_RESIN.get(), 2);
        }
        if (inputStack.is(ModItems.RUNE_PETALS.get())) {
            return new ItemStack(ModItems.MOISTURE_STONE.get());
        }
        if (inputStack.is(ModItems.REMNANT_SEEDS.get())) {
            return new ItemStack(ModItems.REMNANT_SEEDS.get(), 2);
        }
        if (inputStack.is(ModItems.PALEWEFT_SEEDS.get())) {
            return new ItemStack(ModItems.PALEWEFT_SEEDS.get(), 2);
        }
        if (inputStack.is(ModItems.SOULBERRIES.get())) {
            return new ItemStack(ModItems.SOULBERRIES.get(), 3);
        }
        if (inputStack.is(ModItems.GHOULBERRIES.get())) {
            return new ItemStack(ModItems.GHOULBERRIES.get(), 3);
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected int getProcessTime(ItemStack inputStack) {
        return inputStack.is(ModItems.VOLATILE_RESIN.get()) ? 180 : 220;
    }

    @Override
    protected int getWorkSpeed(Level level, BlockPos pos) {
        int speed = 1;

        Biome biome = level.getBiome(pos).value();
        if (biome.getModifiedClimateSettings().downfall() >= 0.75F || biome.warmEnoughToRain(pos)) {
            speed++;
        }

        if (hasSupportBlock(level, pos, Blocks.WATER.defaultBlockState()) || hasSupportBlock(level, pos, Blocks.KELP.defaultBlockState())) {
            speed++;
        }

        if (hasSupportBlock(level, pos, ModBlocks.ATTUNED_STONE.get().defaultBlockState())
                || hasSupportBlock(level, pos, ModBlocks.RUBBLE_TILTH.get().defaultBlockState())) {
            speed++;
        }

        return speed;
    }

    private boolean hasSupportBlock(Level level, BlockPos pos, BlockState targetState) {
        for (BlockPos nearby : BlockPos.betweenClosed(pos.offset(-2, -1, -2), pos.offset(2, 1, 2))) {
            if (level.getBlockState(nearby).is(targetState.getBlock())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected SoundEvent getCompleteSound() {
        return SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT;
    }
}