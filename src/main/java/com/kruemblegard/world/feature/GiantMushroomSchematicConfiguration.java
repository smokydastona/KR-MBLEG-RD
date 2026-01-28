package com.kruemblegard.world.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.List;

public record GiantMushroomSchematicConfiguration(
        List<ResourceLocation> schematics,
        Block capBlock,
        Block stemBlock,
        Block slabBlock
) implements FeatureConfiguration {
    public static final Codec<GiantMushroomSchematicConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.listOf().fieldOf("schematics").forGetter(GiantMushroomSchematicConfiguration::schematics),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("cap_block").forGetter(GiantMushroomSchematicConfiguration::capBlock),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("stem_block").forGetter(GiantMushroomSchematicConfiguration::stemBlock),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("slab_block").forGetter(GiantMushroomSchematicConfiguration::slabBlock)
    ).apply(instance, GiantMushroomSchematicConfiguration::new));
}
