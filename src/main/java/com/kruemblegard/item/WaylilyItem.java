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

        // Lily-pad-like behavior: allow clicking anywhere in a water column, but always place on the
        // true surface water block (the highest water block with non-water above it).

        // Raycasts against water often report the solid block below the water surface.
        BlockPos candidateWaterPos;
        if (level.getFluidState(clickedPos).getType() == Fluids.WATER) {
            candidateWaterPos = clickedPos;
        } else if (level.getFluidState(clickedPos.above()).getType() == Fluids.WATER) {
            candidateWaterPos = clickedPos.above();
        } else {
            return InteractionResult.FAIL;
        }

        BlockPos surfaceWaterPos = candidateWaterPos;
        while (level.getFluidState(surfaceWaterPos.above()).getType() == Fluids.WATER) {
            surfaceWaterPos = surfaceWaterPos.above();
        }

        // Place the flower block in the air *above* the surface water block.
        // This keeps the water surface intact (no "hole" where water got replaced).
        BlockPos placePos = surfaceWaterPos.above();

        // Require air (or replaceable) above the surface, and at least one water block below for the tail.
        if (!level.getBlockState(placePos).canBeReplaced()) {
            return InteractionResult.FAIL;
        }

        if (level.getFluidState(surfaceWaterPos).getType() != Fluids.WATER) {
            return InteractionResult.FAIL;
        }

        BlockHitResult hitResult = new BlockHitResult(
                context.getClickLocation(),
                Direction.UP,
            placePos,
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
