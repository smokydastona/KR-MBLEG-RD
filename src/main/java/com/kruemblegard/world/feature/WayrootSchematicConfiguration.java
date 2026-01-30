package com.kruemblegard.world.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record WayrootSchematicConfiguration(ResourceLocation schematic) implements FeatureConfiguration {
    public static final Codec<WayrootSchematicConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("schematic").forGetter(WayrootSchematicConfiguration::schematic)
    ).apply(instance, WayrootSchematicConfiguration::new));
}
