package com.kruemblegard.world.grower;

import java.util.Optional;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.registry.ModTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.server.level.ServerLevel;

import net.minecraftforge.registries.ForgeRegistries;

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
        } else if (isMega) {
            spreadScarestoneLikePodzol(level, random, pos);
        }

        return success;
    }

    private static void spreadScarestoneLikePodzol(ServerLevel level, RandomSource random, BlockPos megaBasePos) {
        Block scarestone = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(Kruemblegard.MODID, "scarstone"));
        if (scarestone == null || scarestone == Blocks.AIR) {
            return;
        }

        // Roughly mimic mega spruce: patch around each trunk corner.
        placeScarestonePatch(level, random, megaBasePos, scarestone);
        placeScarestonePatch(level, random, megaBasePos.east(), scarestone);
        placeScarestonePatch(level, random, megaBasePos.south(), scarestone);
        placeScarestonePatch(level, random, megaBasePos.south().east(), scarestone);
    }

    private static void placeScarestonePatch(ServerLevel level, RandomSource random, BlockPos trunkPos, Block scarestone) {
        BlockPos groundCenter = trunkPos.below();

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                int ax = Math.abs(dx);
                int az = Math.abs(dz);

                float chance;
                if (ax == 2 && az == 2) {
                    chance = 0.2F;
                } else if (ax == 2 || az == 2) {
                    chance = 0.5F;
                } else {
                    chance = 1.0F;
                }

                if (random.nextFloat() > chance) {
                    continue;
                }

                BlockPos groundPos = groundCenter.offset(dx, 0, dz);
                BlockState groundState = level.getBlockState(groundPos);

                if (!(groundState.is(net.minecraft.tags.BlockTags.DIRT)
                        || groundState.is(ModTags.Blocks.WAYFALL_GROUND)
                        || groundState.is(Blocks.ROOTED_DIRT))) {
                    continue;
                }

                BlockPos abovePos = groundPos.above();
                BlockState aboveState = level.getBlockState(abovePos);
                if (!aboveState.getFluidState().isEmpty()) {
                    continue;
                }
                if (!aboveState.isAir() && !aboveState.canBeReplaced()) {
                    continue;
                }

                level.setBlock(groundPos, scarestone.defaultBlockState(), 2);
            }
        }
    }
}
