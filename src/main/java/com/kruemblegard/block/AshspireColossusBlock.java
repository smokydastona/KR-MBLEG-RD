package com.kruemblegard.block;

import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.registry.ModTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class AshspireColossusBlock extends Block {

    private static final float CONTACT_DAMAGE = 2.0f;

    public AshspireColossusBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState belowState = level.getBlockState(pos.below());
        if (belowState.is(ModTags.Blocks.ASHSPIRE_CACTUS_GROWABLE_ON)
                || belowState.is(ModBlocks.ASHSPIRE_CACTUS.get())
                || belowState.is(ModBlocks.ASHSPIRE_COLOSSUS.get())) {
            return level.getBlockState(pos.above()).getFluidState().isEmpty();
        }

        // Allow sideways persistence when attached to another Ashspire segment.
        if (belowState.isAir()) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockState neighbor = level.getBlockState(pos.relative(dir));
                if (neighbor.is(ModBlocks.ASHSPIRE_CACTUS.get()) || neighbor.is(ModBlocks.ASHSPIRE_COLOSSUS.get())) {
                    return level.getBlockState(pos.above()).getFluidState().isEmpty();
                }
            }
        }

        return false;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        entity.hurt(level.damageSources().cactus(), CONTACT_DAMAGE);

        if (level.isClientSide && level.random.nextInt(10) == 0) {
            double px = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.8;
            double py = pos.getY() + 0.5 + level.random.nextDouble() * 0.9;
            double pz = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.8;
            level.addParticle(ParticleTypes.SMOKE, px, py, pz, 0.0, 0.01, 0.0);
        }
    }
}
