package com.kruemblegard.world.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record WayfallDeepLakeConfiguration(
        BlockStateProvider fluid,
        BlockStateProvider barrier,
        int minRadius,
        int maxRadius,
        int minDepth,
        int maxDepth
) implements FeatureConfiguration {
    public static final Codec<WayfallDeepLakeConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockStateProvider.CODEC.fieldOf("fluid").forGetter(WayfallDeepLakeConfiguration::fluid),
            BlockStateProvider.CODEC.fieldOf("barrier").forGetter(WayfallDeepLakeConfiguration::barrier),
            Codec.intRange(1, 64).fieldOf("min_radius").forGetter(WayfallDeepLakeConfiguration::minRadius),
            Codec.intRange(1, 64).fieldOf("max_radius").forGetter(WayfallDeepLakeConfiguration::maxRadius),
            Codec.intRange(1, 32).fieldOf("min_depth").forGetter(WayfallDeepLakeConfiguration::minDepth),
            Codec.intRange(1, 32).fieldOf("max_depth").forGetter(WayfallDeepLakeConfiguration::maxDepth)
    ).apply(instance, WayfallDeepLakeConfiguration::new));
}
