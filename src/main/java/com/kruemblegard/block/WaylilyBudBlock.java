package com.kruemblegard.block;

import com.kruemblegard.init.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WaylilyBudBlock extends GrowingPlantHeadBlock {
    private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);

    public WaylilyBudBlock(Properties properties) {
        // Similar to kelp: grows upward in water.
        super(properties, Direction.UP, SHAPE, true, 0.14D);
    }

    @Override
    protected GrowingPlantBodyBlock getBodyBlock() {
        return (GrowingPlantBodyBlock) ModBlocks.WAYLILY_STALK.get();
    }

    @Override
    protected boolean canGrowInto(BlockState state) {
        return state.is(Blocks.WATER);
    }

    @Override
    protected int getBlocksToGrowWhenBonemealed(RandomSource random) {
        return 1 + random.nextInt(3);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // If we've reached the surface (air above water), bloom into the surface flower.
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);

        if (level.getFluidState(pos).getType() == Fluids.WATER
                && aboveState.canBeReplaced()
                && level.getFluidState(abovePos).isEmpty()) {
            BlockState flowerState = ModBlocks.WAYLILY.get().defaultBlockState();
            if (flowerState.canSurvive(level, abovePos)) {
                level.setBlock(abovePos, flowerState, Block.UPDATE_ALL);
                level.setBlock(pos, ModBlocks.WAYLILY_STALK.get().defaultBlockState(), Block.UPDATE_ALL);
                level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
                return;
            }
        }

        super.randomTick(state, level, pos, random);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (level.getFluidState(pos).getType() != Fluids.WATER) {
            return false;
        }

        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);

        // Allow continuing from an existing stalk/base.
        if (belowState.is(ModBlocks.WAYLILY_STALK.get()) || belowState.is(ModBlocks.WAYLILY_STALK_BASE.get())) {
            return true;
        }

        return belowState.isFaceSturdy(level, belowPos, Direction.UP) && level.getFluidState(belowPos).isEmpty();
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return Fluids.WATER.getSource(false);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
