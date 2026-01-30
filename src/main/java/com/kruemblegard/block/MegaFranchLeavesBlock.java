package com.kruemblegard.block;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Schematic-only leaves variant used for mega trees.
 *
 * Behavior matches normal Kruemblegard leaves (decay/connectivity), but pick-block returns the
 * normal leaves block so it isn't obvious to players.
 *
 * Drops are controlled by loot tables (which reference the normal leaves/sapling/stick), with
 * reduced chances relative to the normal leaves.
 */
public final class MegaFranchLeavesBlock extends KruemblegardLeavesBlock {
    private final Supplier<? extends Block> cloneTo;

    public MegaFranchLeavesBlock(Supplier<? extends Block> cloneTo, Properties properties, TagKey<Block> anchorLogs) {
        super(properties, anchorLogs);
        this.cloneTo = cloneTo;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return new ItemStack(cloneTo.get());
    }
}
