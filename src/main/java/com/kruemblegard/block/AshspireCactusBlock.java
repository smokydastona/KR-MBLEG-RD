package com.kruemblegard.block;

import com.kruemblegard.registry.ModTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.ChorusPlantBlock;
import net.minecraft.world.level.block.state.BlockState;

public class AshspireCactusBlock extends ChorusPlantBlock {

    private static final float CONTACT_DAMAGE = 0.5f;

    public AshspireCactusBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState belowState = level.getBlockState(pos.below());
        if (belowState.is(this) || belowState.is(ModTags.Blocks.ASHSPIRE_CACTUS_GROWABLE_ON)) {
            return true;
        }

        // Allow chorus-like sideways branches to persist when hanging over air,
        // as long as they're attached to another Ashspire cactus horizontally.
        if (belowState.isAir()) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                if (level.getBlockState(pos.relative(dir)).is(this)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        entity.hurt(level.damageSources().cactus(), CONTACT_DAMAGE);

        if (level.isClientSide) {
            if (level.random.nextInt(6) == 0) {
                double px = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.6;
                double py = pos.getY() + 0.5 + level.random.nextDouble() * 0.8;
                double pz = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.6;

                level.addParticle(ParticleTypes.ASH, px, py, pz, 0.0, 0.02, 0.0);
            }
        }
    }
}
