package com.kruemblegard.world.grower;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class FixedConfiguredFeatureTreeGrower extends AbstractTreeGrower {
    private final ResourceKey<ConfiguredFeature<?, ?>> featureKey;

    public FixedConfiguredFeatureTreeGrower(ResourceKey<ConfiguredFeature<?, ?>> featureKey) {
        this.featureKey = featureKey;
    }

    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean hasFlowers) {
        return featureKey;
    }
}
