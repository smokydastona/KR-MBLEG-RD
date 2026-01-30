package com.kruemblegard.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class GlimmerpineLeavesBlock extends KruemblegardLeavesBlock {
    public GlimmerpineLeavesBlock(Properties properties, TagKey<Block> anchorLogs) {
        super(properties, anchorLogs);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(10) != 0) return;

        level.addParticle(
                ParticleTypes.END_ROD,
                pos.getX() + 0.2 + random.nextDouble() * 0.6,
                pos.getY() + 0.4 + random.nextDouble() * 0.6,
                pos.getZ() + 0.2 + random.nextDouble() * 0.6,
                0.0,
                0.01,
                0.0
        );
    }
}
