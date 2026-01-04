package com.kruemblegard.world.feature;

import java.util.function.Supplier;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class WayfallSimpleTreeFeature extends Feature<NoneFeatureConfiguration> {
    private final Supplier<? extends Block> log;
    private final Supplier<? extends Block> leaves;

    public WayfallSimpleTreeFeature(Codec<NoneFeatureConfiguration> codec, Supplier<? extends Block> log, Supplier<? extends Block> leaves) {
        super(codec);
        this.log = log;
        this.leaves = leaves;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        LevelAccessor level = ctx.level();
        BlockPos pos = ctx.origin();
        RandomSource random = ctx.random();

        // Try to place on the surface.
        BlockPos ground = pos;
        while (ground.getY() > level.getMinBuildHeight() + 2 && level.getBlockState(ground).isAir()) {
            ground = ground.below();
        }
        BlockPos start = ground.above();

        if (!level.getBlockState(start).isAir()) {
            return false;
        }

        int height = 5 + random.nextInt(3);
        if (!hasSpace(level, start, height)) {
            return false;
        }

        BlockState logState = log.get().defaultBlockState();
        BlockState leafState = leaves.get().defaultBlockState();

        // Trunk
        for (int dy = 0; dy < height; dy++) {
            level.setBlock(start.above(dy), logState, 2);
        }

        // Canopy
        BlockPos top = start.above(height);
        for (int dy = -2; dy <= 0; dy++) {
            int radius = (dy == 0) ? 1 : 2;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) == radius && Math.abs(dz) == radius && random.nextInt(3) == 0) {
                        continue;
                    }

                    BlockPos p = top.offset(dx, dy, dz);
                    BlockState existing = level.getBlockState(p);
                    if (existing.isAir() || existing.getBlock() instanceof LeavesBlock || existing.getBlock() instanceof BushBlock) {
                        level.setBlock(p, leafState, 2);
                    }
                }
            }
        }

        BlockPos tuft = top.above();
        if (level.getBlockState(tuft).isAir()) {
            level.setBlock(tuft, leafState, 2);
        }

        return true;
    }

    private static boolean hasSpace(LevelAccessor level, BlockPos pos, int height) {
        for (int dy = 0; dy <= height + 1; dy++) {
            BlockPos p = pos.above(dy);
            BlockState existing = level.getBlockState(p);
            if (!(existing.isAir() || existing.getBlock() instanceof LeavesBlock || existing.getBlock() instanceof BushBlock)) {
                return false;
            }
        }
        return true;
    }
}
