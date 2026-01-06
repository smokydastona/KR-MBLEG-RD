package com.kruemblegard.block;

import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.registry.ModTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class AshmossBlock extends WayfallSurfaceBlock {

    public AshmossBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.getBiome(pos).is(ModTags.Biomes.ASH_HEAVY)) {
            level.setBlock(pos, ModBlocks.VEILGROWTH.get().defaultBlockState(), 2);
            return;
        }

        if (random.nextInt(12) != 0) {
            return;
        }

        Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        BlockPos target = pos.relative(dir);
        BlockState targetState = level.getBlockState(target);
        if (targetState.is(ModBlocks.VEILGROWTH.get())) {
            level.setBlock(target, ModBlocks.ASHMOSS.get().defaultBlockState(), 2);
        }
    }
}
