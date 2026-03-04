package com.kruemblegard.item;

import com.kruemblegard.init.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;

public class WaylilyItem extends BlockItem {

    public WaylilyItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();

        // Kelp-like behavior: allow clicking anywhere in a water column, but always plant at the
        // bottom of the water column (the lowest water block, directly above the floor).

        BlockPos candidateWaterPos;
        if (level.getFluidState(clickedPos).getType() == Fluids.WATER) {
            candidateWaterPos = clickedPos;
        } else if (level.getFluidState(clickedPos.above()).getType() == Fluids.WATER) {
            candidateWaterPos = clickedPos.above();
        } else {
            return InteractionResult.FAIL;
        }

        BlockPos bottomWaterPos = candidateWaterPos;
        while (level.getFluidState(bottomWaterPos.below()).getType() == Fluids.WATER) {
            bottomWaterPos = bottomWaterPos.below();
        }

        // Must be a real water block and have a solid floor below.
        if (level.getFluidState(bottomWaterPos).getType() != Fluids.WATER) {
            return InteractionResult.FAIL;
        }

        BlockPos floorPos = bottomWaterPos.below();
        if (!level.getBlockState(floorPos).isFaceSturdy(level, floorPos, Direction.UP)) {
            return InteractionResult.FAIL;
        }

        // Don't overwrite non-water plants/blocks.
        if (!level.getBlockState(bottomWaterPos).canBeReplaced()) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide) {
            level.setBlock(bottomWaterPos, ModBlocks.WAYLILY_BUD.get().defaultBlockState(), Block.UPDATE_ALL);
            level.scheduleTick(bottomWaterPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
