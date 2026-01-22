package com.kruemblegard.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

public class WaylilyItem extends BlockItem {

    public WaylilyItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();

        BlockPos waterPos = null;
        if (level.getFluidState(clickedPos).getType() == Fluids.WATER) {
            waterPos = clickedPos;
        } else {
            BlockPos adjacent = clickedPos.relative(clickedFace);
            if (level.getFluidState(adjacent).getType() == Fluids.WATER) {
                waterPos = adjacent;
            }
        }

        if (waterPos == null) {
            return super.useOn(context);
        }

        // Climb up the water column to find the surface.
        BlockPos surfaceWaterPos = waterPos;
        while (level.getFluidState(surfaceWaterPos.above()).getType() == Fluids.WATER) {
            surfaceWaterPos = surfaceWaterPos.above();
        }

        // Require air (or replaceable) above the surface, and at least one water block below for the tail.
        if (!level.getBlockState(surfaceWaterPos.above()).canBeReplaced()) {
            return InteractionResult.FAIL;
        }

        if (level.getFluidState(surfaceWaterPos.below()).getType() != Fluids.WATER) {
            return InteractionResult.FAIL;
        }

        BlockHitResult hitResult = new BlockHitResult(
                context.getClickLocation(),
                Direction.UP,
                surfaceWaterPos,
                false
        );

        BlockPlaceContext placeContext = new BlockPlaceContext(
                level,
                context.getPlayer(),
                context.getHand(),
                context.getItemInHand(),
                hitResult
        );

        BlockState placementState = this.getPlacementState(placeContext);
        if (placementState == null) {
            return InteractionResult.FAIL;
        }

        return this.place(placeContext);
    }
}
