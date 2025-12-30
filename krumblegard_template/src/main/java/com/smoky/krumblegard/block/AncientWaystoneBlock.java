package com.smoky.krumblegard.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class AncientWaystoneBlock extends Block {
    public AncientWaystoneBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        // This is called client-side for visual ambience.
        if (random.nextInt(3) != 0) return;

        level.addParticle(
                ParticleTypes.END_ROD,
                pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.25,
                pos.getY() + 1.05 + random.nextDouble() * 0.35,
                pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.25,
                0.0,
                0.01,
                0.0
        );
    }
}
