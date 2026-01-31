package com.kruemblegard.world.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record EchowoodSchematicConfiguration(ResourceLocation schematic) implements FeatureConfiguration {
    public static final Codec<EchowoodSchematicConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("schematic").forGetter(EchowoodSchematicConfiguration::schematic)
    ).apply(instance, EchowoodSchematicConfiguration::new));
}
