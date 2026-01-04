package com.kruemblegard.block;

import com.kruemblegard.init.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.state.BlockState;

public class AshveilBlock extends CarpetBlock {
    public AshveilBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        return below.is(BlockTags.DIRT)
                || below.is(net.minecraft.world.level.block.Blocks.END_STONE)
                || below.is(ModBlocks.ATTUNED_STONE.get())
                || below.is(net.minecraft.world.level.block.Blocks.STONE)
                || below.is(net.minecraft.world.level.block.Blocks.COBBLESTONE);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(6) != 0) return;

        level.addParticle(
                ParticleTypes.ASH,
                pos.getX() + 0.2 + random.nextDouble() * 0.6,
                pos.getY() + 0.05,
                pos.getZ() + 0.2 + random.nextDouble() * 0.6,
                0.0,
                0.02,
                0.0
        );
    }
}
