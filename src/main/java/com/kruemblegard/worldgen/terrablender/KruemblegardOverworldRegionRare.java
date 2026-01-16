package com.kruemblegard.worldgen.terrablender;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.config.worldgen.WorldgenTuningConfig;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import com.mojang.datafixers.util.Pair;

import java.util.function.Consumer;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;

import terrablender.api.Region;
import terrablender.api.RegionType;

public final class KruemblegardOverworldRegionRare extends Region {
    public static final ResourceLocation LOCATION = new ResourceLocation(Kruemblegard.MOD_ID, "overworld_rare");

    public KruemblegardOverworldRegionRare(int weight) {
        super(LOCATION, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        var config = WorldgenTuningConfig.get();
        var overworld = config.terraBlender.overworld;

        boolean enableBasinOfScars = isEnabled(overworld.enabledBiomes, ModWorldgenKeys.Biomes.BASIN_OF_SCARS);

        if (!enableBasinOfScars) {
            return;
        }

        if (config.strictValidation) {
            validatePresentOrThrow(registry, ModWorldgenKeys.Biomes.BASIN_OF_SCARS, true);
        }

        this.addModifiedVanillaOverworldBiomes(mapper, builder -> {
            builder.replaceBiome(Biomes.DESERT, ModWorldgenKeys.Biomes.BASIN_OF_SCARS);
        });
    }

    private static boolean isEnabled(java.util.Map<String, Boolean> enabledBiomes, ResourceKey<Biome> biome) {
        if (enabledBiomes == null) {
            return false;
        }
        return Boolean.TRUE.equals(enabledBiomes.get(biome.location().toString()));
    }

    private static void validatePresentOrThrow(Registry<Biome> registry, ResourceKey<Biome> biomeKey, boolean enabled) {
        if (!enabled) {
            return;
        }
        if (!registry.containsKey(biomeKey.location())) {
            throw new IllegalArgumentException("Configured overworld biome is missing from registry: " + biomeKey.location());
        }
    }
}
