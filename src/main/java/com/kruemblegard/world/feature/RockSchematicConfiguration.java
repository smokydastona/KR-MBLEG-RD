package com.kruemblegard.world.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record RockSchematicConfiguration(
        ResourceLocation schematicRoot,
        int baseRarity,
        int biomeRaritySpread,
        int maxBurial,
        float chipChance,
        float mossyChance,
        float crackedChance
) implements FeatureConfiguration {
    public static final Codec<RockSchematicConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("schematic_root").forGetter(RockSchematicConfiguration::schematicRoot),
            Codec.intRange(1, 10_000).fieldOf("base_rarity").forGetter(RockSchematicConfiguration::baseRarity),
            Codec.intRange(0, 10_000).fieldOf("biome_rarity_spread").forGetter(RockSchematicConfiguration::biomeRaritySpread),
            Codec.intRange(0, 32).fieldOf("max_burial").forGetter(RockSchematicConfiguration::maxBurial),
            Codec.floatRange(0.0F, 1.0F).fieldOf("chip_chance").forGetter(RockSchematicConfiguration::chipChance),
            Codec.floatRange(0.0F, 1.0F).fieldOf("mossy_chance").forGetter(RockSchematicConfiguration::mossyChance),
            Codec.floatRange(0.0F, 1.0F).fieldOf("cracked_chance").forGetter(RockSchematicConfiguration::crackedChance)
    ).apply(instance, RockSchematicConfiguration::new));
}
