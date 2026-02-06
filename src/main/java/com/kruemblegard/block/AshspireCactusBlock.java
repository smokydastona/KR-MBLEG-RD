package com.kruemblegard.block;

import com.kruemblegard.registry.ModTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.state.BlockState;

public class AshspireCactusBlock extends CactusBlock {

    private static final float CONTACT_DAMAGE = 0.5f;
    private static final float CONTACT_DAMAGE_EMBERBLOOM = 1.0f;

    private final boolean emberbloom;

    public AshspireCactusBlock(Properties properties, boolean emberbloom) {
        super(properties);
        this.emberbloom = emberbloom;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos neighborPos = pos.relative(dir);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (neighborState.isSolid() || !level.getFluidState(neighborPos).isEmpty()) {
                return false;
            }
        }

        BlockState belowState = level.getBlockState(pos.below());
        if (!(belowState.is(this) || belowState.is(ModTags.Blocks.ASHPIRE_CACTUS_GROWABLE_ON))) {
            return false;
        }

        return level.getBlockState(pos.above()).getFluidState().isEmpty();
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos abovePos = pos.above();
        if (!level.isEmptyBlock(abovePos)) {
            return;
        }

        int height = 1;
        while (level.getBlockState(pos.below(height)).is(this)) {
            height++;
        }

        int maxHeight = 3;
        boolean allowSpireHeight = random.nextInt(20) == 0; // ~5%
        if (height >= maxHeight && !(height == maxHeight && allowSpireHeight)) {
            return;
        }

        int age = state.getValue(AGE);
        if (age >= 15) {
            level.setBlockAndUpdate(abovePos, defaultBlockState());
            level.setBlock(pos, state.setValue(AGE, 0), Block.UPDATE_CLIENTS);
        } else {
            level.setBlock(pos, state.setValue(AGE, age + 1), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        float damage = emberbloom ? CONTACT_DAMAGE_EMBERBLOOM : CONTACT_DAMAGE;
        entity.hurt(level.damageSources().cactus(), damage);

        if (level.isClientSide) {
            if (level.random.nextInt(6) == 0) {
                double px = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.6;
                double py = pos.getY() + 0.5 + level.random.nextDouble() * 0.8;
                double pz = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.6;

                level.addParticle(ParticleTypes.ASH, px, py, pz, 0.0, 0.02, 0.0);
                if (emberbloom && level.random.nextInt(3) == 0) {
                    level.addParticle(ParticleTypes.SMALL_FLAME, px, py, pz, 0.0, 0.01, 0.0);
                }
            }
        }
    }
}
