package com.kruemblegard.block;

import com.kruemblegard.registry.ModTags;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WayfallSaplingBlock extends BushBlock implements BonemealableBlock {
    private final Supplier<? extends Block> log;
    private final Supplier<? extends Block> leaves;

    public WayfallSaplingBlock(Properties properties, Supplier<? extends Block> log, Supplier<? extends Block> leaves) {
        super(properties);
        this.log = log;
        this.leaves = leaves;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(BlockTags.DIRT)
                || state.is(ModTags.Blocks.WAYFALL_GROUND);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return random.nextFloat() < 0.45f;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        tryGrow(level, random, pos);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(10) == 0) {
            tryGrow(level, random, pos);
        }
    }

    private void tryGrow(ServerLevel level, RandomSource random, BlockPos pos) {
        int height = 4 + random.nextInt(3);
        if (!hasSpace(level, pos, height)) {
            return;
        }

        BlockState logState = log.get().defaultBlockState();
        BlockState leafState = leaves.get().defaultBlockState();

        // Trunk
        for (int dy = 0; dy < height; dy++) {
            level.setBlock(pos.above(dy), logState, 2);
        }

        // Canopy
        BlockPos top = pos.above(height);
        for (int dy = -2; dy <= 0; dy++) {
            int radius = (dy == 0) ? 1 : 2;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) == radius && Math.abs(dz) == radius && random.nextInt(3) == 0) {
                        continue;
                    }

                    BlockPos p = top.offset(dx, dy, dz);
                    BlockState existing = level.getBlockState(p);
                    if (existing.isAir() || existing.getBlock() instanceof LeavesBlock || existing.getBlock() instanceof BushBlock) {
                        level.setBlock(p, leafState, 2);
                    }
                }
            }
        }

        // One extra leaf tuft
        BlockPos tuft = top.above();
        if (level.getBlockState(tuft).isAir()) {
            level.setBlock(tuft, leafState, 2);
        }
    }

    private boolean hasSpace(ServerLevel level, BlockPos pos, int height) {
        for (int dy = 0; dy <= height + 1; dy++) {
            BlockPos p = pos.above(dy);
            BlockState existing = level.getBlockState(p);
            if (!(existing.isAir() || existing.getBlock() instanceof LeavesBlock || existing.getBlock() instanceof BushBlock || existing.is(this))) {
                return false;
            }
        }
        return true;
    }
}
