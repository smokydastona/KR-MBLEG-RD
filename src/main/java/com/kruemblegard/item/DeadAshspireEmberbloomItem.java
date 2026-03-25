package com.kruemblegard.item;

import com.kruemblegard.block.AshspireEmberbloomBlock;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class DeadAshspireEmberbloomItem extends BlockItem {

    public DeadAshspireEmberbloomItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected BlockState getPlacementState(BlockPlaceContext context) {
        BlockState placement = super.getPlacementState(context);
        if (placement == null) {
            return null;
        }

        if (placement.hasProperty(AshspireEmberbloomBlock.AGE)) {
            placement = placement.setValue(AshspireEmberbloomBlock.AGE, 5);
        }

        return placement.canSurvive(context.getLevel(), context.getClickedPos()) ? placement : null;
    }
}