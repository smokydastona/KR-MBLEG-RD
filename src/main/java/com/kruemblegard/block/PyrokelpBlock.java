package com.kruemblegard.block;

import com.kruemblegard.registry.ModTags;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;

public class PyrokelpBlock extends BushBlock implements BonemealableBlock {
    private static final int MAX_HEIGHT = 10;

    public PyrokelpBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(BlockTags.DIRT)
                || state.is(ModTags.Blocks.WAYFALL_GROUND)
                || state.is(this);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.getBiome(pos).is(ModTags.WorldgenBiomes.BASIN_OF_SCARS)) {
            return;
        }

        if (random.nextInt(6) != 0) {
            return;
        }

        tryGrow(level, pos, random, 1);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return level.getBiome(pos).is(ModTags.WorldgenBiomes.BASIN_OF_SCARS)
                && canGrowInto(level, pos.above())
                && getColumnHeight(level, pos) < MAX_HEIGHT;
    }

    @Override
    public boolean isBonemealSuccess(net.minecraft.world.level.Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        if (!level.getBiome(pos).is(ModTags.WorldgenBiomes.BASIN_OF_SCARS)) {
            return;
        }

        tryGrow(level, pos, random, 1 + random.nextInt(3));
    }

    private void tryGrow(ServerLevel level, BlockPos pos, RandomSource random, int attempts) {
        int height = getColumnHeight(level, pos);
        if (height >= MAX_HEIGHT) {
            return;
        }

        BlockPos growPos = pos.above();
        for (int i = 0; i < attempts && height < MAX_HEIGHT; i++) {
            if (!canGrowInto(level, growPos)) {
                return;
            }

            level.setBlock(growPos, defaultBlockState(), 2);
            height++;
            growPos = growPos.above();

            // Small chance to stop early so patches vary in height.
            if (random.nextInt(4) == 0) {
                return;
            }
        }
    }

    private static boolean canGrowInto(LevelReader level, BlockPos pos) {
        return level.getBlockState(pos).isAir();
    }

    private int getColumnHeight(LevelReader level, BlockPos pos) {
        int height = 1;
        BlockPos cursor = pos.below();
        while (height < MAX_HEIGHT && level.getBlockState(cursor).is(this)) {
            height++;
            cursor = cursor.below();
        }
        return height;
    }
}
