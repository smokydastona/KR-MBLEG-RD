package com.kruemblegard.block;

import com.kruemblegard.init.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.ChorusPlantBlock;
import net.minecraft.world.level.block.state.BlockState;

public class AshspireEmberbloomBlock extends ChorusFlowerBlock implements BonemealableBlock {

    private final ChorusPlantBlock ashspirePlant;

    public AshspireEmberbloomBlock(ChorusPlantBlock plant, Properties properties) {
        super(plant, properties);
        this.ashspirePlant = plant;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        if (!(below.is(ModBlocks.ASHSPIRE_CACTUS.get()) || below.is(ModBlocks.ASHSPIRE_COLOSSUS.get()))) {
            return false;
        }

        // Vanilla chorus flower also needs a ceiling-free space.
        if (!level.getBlockState(pos.above()).getFluidState().isEmpty()) {
            return false;
        }

        // Extra safety: don't allow sideways face attachment.
        return below.isFaceSturdy(level, pos.below(), Direction.UP) || below.is(ModBlocks.ASHSPIRE_CACTUS.get()) || below.is(ModBlocks.ASHSPIRE_COLOSSUS.get());
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.isClientSide) {
            return;
        }

        int age = state.getValue(AGE);
        if (age >= 5) {
            return;
        }

        // If the space above is blocked or fluid-filled, do nothing.
        if (!level.getBlockState(pos.above()).getFluidState().isEmpty()) {
            return;
        }

        // Prefer sideways branching heavily; occasionally grow upward.
        boolean grew = false;
        if (random.nextInt(6) == 0) {
            grew = tryGrowUp(level, pos, age);
        }
        if (!grew) {
            grew = tryBranchSideways(level, pos, random, age);
        }

        // If nothing worked, fall back to vanilla behavior rarely.
        // (keeps some of the chorus randomness without taking over completely).
        if (!grew && random.nextInt(12) == 0) {
            super.randomTick(state, level, pos, random);
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return state.getValue(AGE) < 5 && canSurvive(state, level, pos);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        // Multiple attempts so bonemeal reliably produces extra side branches.
        for (int i = 0; i < 3; i++) {
            BlockState current = level.getBlockState(pos);
            if (!(current.getBlock() instanceof AshspireEmberbloomBlock)) {
                break;
            }
            randomTick(current, level, pos, random);
        }
    }

    private boolean tryGrowUp(Level level, BlockPos pos, int age) {
        BlockPos up = pos.above();
        if (!level.getBlockState(up).isAir()) {
            return false;
        }

        // Convert this flower to plant, and move the flower tip upward.
        level.setBlock(pos, this.ashspirePlant.defaultBlockState(), 2);
        level.setBlock(up, defaultBlockState().setValue(AGE, Math.min(5, age + 1)), 2);
        return true;
    }

    private boolean tryBranchSideways(Level level, BlockPos pos, RandomSource random, int age) {
        // Convert this flower to plant first so any new branch segments
        // immediately have a horizontal attachment point.
        level.setBlock(pos, this.ashspirePlant.defaultBlockState(), 2);

        int attempts = 2 + random.nextInt(4); // 2..5 (more sideways than vanilla)
        boolean placedAny = false;
        Direction[] dirs = new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };

        for (int i = 0; i < attempts; i++) {
            Direction dir = dirs[random.nextInt(dirs.length)];
            BlockPos base = pos.relative(dir);
            BlockPos tip = base.above();

            if (!level.getFluidState(base).isEmpty() || !level.getFluidState(tip).isEmpty()) {
                continue;
            }
            if (!level.getBlockState(base).isAir() || !level.getBlockState(tip).isAir()) {
                continue;
            }

            level.setBlock(base, this.ashspirePlant.defaultBlockState(), 2);
            level.setBlock(tip, defaultBlockState().setValue(AGE, Math.min(5, age + 1)), 2);
            placedAny = true;
        }

        if (!placedAny) {
            // Revert back to a flower tip if we failed to place any branches.
            level.setBlock(pos, defaultBlockState().setValue(AGE, age), 2);
        }

        return placedAny;
    }
}
