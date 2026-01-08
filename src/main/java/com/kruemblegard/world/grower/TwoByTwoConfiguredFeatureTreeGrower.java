package com.kruemblegard.world.grower;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.server.level.ServerLevel;

public class TwoByTwoConfiguredFeatureTreeGrower extends AbstractTreeGrower {
    private final ResourceKey<ConfiguredFeature<?, ?>> smallFeatureKey;
    private final ResourceKey<ConfiguredFeature<?, ?>> megaFeatureKey;

    public TwoByTwoConfiguredFeatureTreeGrower(ResourceKey<ConfiguredFeature<?, ?>> smallFeatureKey,
                                               ResourceKey<ConfiguredFeature<?, ?>> megaFeatureKey) {
        this.smallFeatureKey = smallFeatureKey;
        this.megaFeatureKey = megaFeatureKey;
    }

    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean hasFlowers) {
        return smallFeatureKey;
    }

    @Override
    public boolean growTree(ServerLevel level, ChunkGenerator generator, BlockPos pos, BlockState state, RandomSource random) {
        Block saplingBlock = state.getBlock();

        BlockPos megaBasePos = findTwoByTwoSapling(level, pos, saplingBlock);
        if (megaBasePos != null) {
            return placeAt(level, generator, random, megaBasePos, saplingBlock, megaFeatureKey, true);
        }

        return placeAt(level, generator, random, pos, saplingBlock, smallFeatureKey, false);
    }

    private static BlockPos findTwoByTwoSapling(ServerLevel level, BlockPos pos, Block saplingBlock) {
        for (int xOffset = 0; xOffset >= -1; xOffset--) {
            for (int zOffset = 0; zOffset >= -1; zOffset--) {
                BlockPos base = pos.offset(xOffset, 0, zOffset);
                if (isTwoByTwoAt(level, base, saplingBlock)) {
                    return base;
                }
            }
        }
        return null;
    }

    private static boolean isTwoByTwoAt(ServerLevel level, BlockPos base, Block saplingBlock) {
        return level.getBlockState(base).is(saplingBlock)
                && level.getBlockState(base.east()).is(saplingBlock)
                && level.getBlockState(base.south()).is(saplingBlock)
                && level.getBlockState(base.south().east()).is(saplingBlock);
    }

    private static boolean placeAt(ServerLevel level,
                                   ChunkGenerator generator,
                                   RandomSource random,
                                   BlockPos pos,
                                   Block saplingBlock,
                                   ResourceKey<ConfiguredFeature<?, ?>> featureKey,
                                   boolean isMega) {
        Optional<Holder.Reference<ConfiguredFeature<?, ?>>> holder = level.registryAccess()
                .registryOrThrow(Registries.CONFIGURED_FEATURE)
                .getHolder(featureKey);

        if (holder.isEmpty()) {
            return false;
        }

        BlockState saplingState = saplingBlock.defaultBlockState();
        BlockPos[] cleared;
        if (isMega) {
            cleared = new BlockPos[]{
                    pos,
                    pos.east(),
                    pos.south(),
                    pos.south().east()
            };
        } else {
            cleared = new BlockPos[]{pos};
        }

        for (BlockPos clearPos : cleared) {
            level.setBlock(clearPos, Blocks.AIR.defaultBlockState(), 4);
        }

        boolean success = holder.get().value().place(level, generator, random, pos);

        if (!success) {
            for (BlockPos restorePos : cleared) {
                level.setBlock(restorePos, saplingState, 4);
            }
        }

        return success;
    }
}
