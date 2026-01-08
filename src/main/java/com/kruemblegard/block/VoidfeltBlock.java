package com.kruemblegard.block;

import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.registry.ModTags;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class VoidfeltBlock extends WayfallSurfaceBlock {

    public VoidfeltBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Voidfelt is generated only; outside void-biomes it slowly collapses back into Wayfall dirt.
        if (!level.getBiome(pos).is(ModTags.Biomes.VOID_BIOME) && random.nextInt(20) == 0) {
            level.setBlock(pos, ModBlocks.FAULT_DUST.get().defaultBlockState(), 2);
        }
    }
}
