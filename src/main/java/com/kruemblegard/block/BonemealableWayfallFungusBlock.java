package com.kruemblegard.block;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class BonemealableWayfallFungusBlock extends WayfallPlantBlock implements BonemealableBlock {
    private final List<ResourceLocation> growFeatures;

    public BonemealableWayfallFungusBlock(Properties properties, List<ResourceLocation> growFeatures) {
        super(properties);
        this.growFeatures = List.copyOf(growFeatures);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return !growFeatures.isEmpty();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return !growFeatures.isEmpty();
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        if (growFeatures.isEmpty()) return;

        ResourceLocation featureId = growFeatures.get(random.nextInt(growFeatures.size()));
        ResourceKey<ConfiguredFeature<?, ?>> featureKey = ResourceKey.create(Registries.CONFIGURED_FEATURE, featureId);

        BlockState oldState = level.getBlockState(pos);
        level.removeBlock(pos, false);

        boolean placed = level.registryAccess()
                .registryOrThrow(Registries.CONFIGURED_FEATURE)
                .getHolder(featureKey)
                .map(holder -> holder.value().place(level, level.getChunkSource().getGenerator(), random, pos))
                .orElse(false);

        if (!placed) {
            level.setBlock(pos, oldState, 3);
        }
    }
}
