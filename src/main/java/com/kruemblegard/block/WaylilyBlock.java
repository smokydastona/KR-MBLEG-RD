package com.kruemblegard.block;

import com.kruemblegard.init.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import org.jetbrains.annotations.Nullable;

public class WaylilyBlock extends Block {

    private static final int MAX_STALK_DEPTH = 64;

    public WaylilyBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();

        // Surface-flower behavior:
        // This block sits in the air block *above* the surface water.
        if (!level.getBlockState(pos).canBeReplaced()) {
            return null;
        }

        if (level.getFluidState(pos.below()).getType() != Fluids.WATER) {
            return null;
        }

        return this.defaultBlockState();
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);

        if (level.isClientSide) {
            return;
        }

        ensureStalkColumn(level, pos);
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            net.minecraft.core.Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        if (direction == net.minecraft.core.Direction.DOWN) {
            // Break if the supporting water is removed.
            if (level.getFluidState(pos.below()).getType() != Fluids.WATER) {
                return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
            }

            // Keep the stalk column in sync if the water column changes.
            if (!level.isClientSide()) {
                ensureStalkColumn(level, pos);
            }
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        // Flower survives only if there is water directly beneath it.
        return level.getFluidState(pos.below()).getType() == Fluids.WATER;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return super.getFluidState(state);
    }

    private static void ensureStalkColumn(LevelAccessor level, BlockPos flowerPos) {
        BlockPos cursor = flowerPos.below();
        int placed = 0;
        BlockPos lastWaterPos = null;

        // Fill the entire water column beneath the flower with stalk blocks, down to the floor.
        while (placed < MAX_STALK_DEPTH && level.getFluidState(cursor).getType() == Fluids.WATER) {
            BlockState existing = level.getBlockState(cursor);

            // Only replace true water/replaceable blocks; don't overwrite other plants/blocks.
            if (existing.is(Blocks.WATER)) {
                level.setBlock(cursor, ModBlocks.WAYLILY_STALK.get().defaultBlockState(), Block.UPDATE_ALL);
                lastWaterPos = cursor;
            } else if (existing.is(ModBlocks.WAYLILY_STALK.get()) || existing.is(ModBlocks.WAYLILY_BUD.get())) {
                // Already part of a waylily column; keep going.
                lastWaterPos = cursor;
            } else if (existing.is(ModBlocks.WAYLILY_STALK_BASE.get())) {
                lastWaterPos = cursor;
            } else {
                break;
            }

            placed++;
            cursor = cursor.below();
        }

        // Keep water ticking where we replaced it.
        if (placed > 0) {
            if (lastWaterPos != null) {
                level.setBlock(lastWaterPos, ModBlocks.WAYLILY_STALK_BASE.get().defaultBlockState(), Block.UPDATE_ALL);
            }
            BlockPos surfaceWater = flowerPos.below();
            level.scheduleTick(surfaceWater, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
    }

}

