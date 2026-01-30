package com.kruemblegard.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class DriftwoodLeavesBlock extends KruemblegardLeavesBlock {
    public DriftwoodLeavesBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(12) != 0) return;

        level.addParticle(
                ParticleTypes.WHITE_ASH,
                pos.getX() + 0.2 + random.nextDouble() * 0.6,
                pos.getY() + 0.3 + random.nextDouble() * 0.6,
                pos.getZ() + 0.2 + random.nextDouble() * 0.6,
                (random.nextDouble() - 0.5) * 0.01,
                0.005,
                (random.nextDouble() - 0.5) * 0.01
        );
    }
}
