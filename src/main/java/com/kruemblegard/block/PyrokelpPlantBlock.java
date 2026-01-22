package com.kruemblegard.block;

import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.registry.ModTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PyrokelpPlantBlock extends GrowingPlantBodyBlock {
    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public PyrokelpPlantBlock(Properties properties) {
        super(properties, Direction.UP, SHAPE, false);
    }

    @Override
    protected GrowingPlantHeadBlock getHeadBlock() {
        return (GrowingPlantHeadBlock) ModBlocks.PYROKELP.get();
    }

    private BlockPos findHeadPos(LevelReader level, BlockPos startPos) {
        BlockPos cursor = startPos;
        while (level.getBlockState(cursor).is(this)) {
            cursor = cursor.above();
        }
        return cursor;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        BlockPos headPos = findHeadPos(level, pos);
        BlockState headState = level.getBlockState(headPos);
        if (!(headState.getBlock() instanceof BonemealableBlock bonemealable)) {
            return false;
        }

        return bonemealable.isValidBonemealTarget(level, headPos, headState, isClient);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        BlockPos headPos = findHeadPos(level, pos);
        BlockState headState = level.getBlockState(headPos);
        if (!(headState.getBlock() instanceof BonemealableBlock bonemealable)) {
            return;
        }

        bonemealable.performBonemeal(level, random, headPos, headState);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        return below.is(BlockTags.DIRT) || below.is(ModTags.Blocks.WAYFALL_GROUND) || below.is(this) || below.is(getHeadBlock()) || super.canSurvive(state, level, pos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
