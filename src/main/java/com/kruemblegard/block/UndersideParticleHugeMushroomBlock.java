package com.kruemblegard.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;

public class UndersideParticleHugeMushroomBlock extends HugeMushroomBlock {
    private final ParticleOptions undersideParticle;
    private final int particleChance;

    public UndersideParticleHugeMushroomBlock(Properties properties, ParticleOptions undersideParticle, int particleChance) {
        super(properties);
        this.undersideParticle = undersideParticle;
        this.particleChance = particleChance;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(particleChance) != 0) return;

        // Only emit from the underside when that face is "inside".
        // This pairs with our cap/inside rendering logic and avoids dripping from exposed cap bottoms.
        if (state.getValue(DOWN)) return;

        BlockPos below = pos.below();
        if (!level.getBlockState(below).isAir()) return;

        level.addParticle(
                undersideParticle,
                pos.getX() + random.nextDouble(),
                pos.getY() - 0.05,
                pos.getZ() + random.nextDouble(),
                0.0,
                -0.02,
                0.0
        );
    }
}
