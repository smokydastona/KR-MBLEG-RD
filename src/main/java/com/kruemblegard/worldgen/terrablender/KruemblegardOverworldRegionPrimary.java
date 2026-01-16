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

public final class KruemblegardOverworldRegionPrimary extends Region {
    public static final ResourceLocation LOCATION = new ResourceLocation(Kruemblegard.MOD_ID, "overworld_primary");

    public KruemblegardOverworldRegionPrimary(int weight) {
        super(LOCATION, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        var config = WorldgenTuningConfig.get();
        var overworld = config.terraBlender.overworld;

        boolean enableCrumbledCrossing = isEnabled(overworld.enabledBiomes, ModWorldgenKeys.Biomes.CRUMBLED_CROSSING);
        boolean enableUnderwayFalls = isEnabled(overworld.enabledBiomes, ModWorldgenKeys.Biomes.UNDERWAY_FALLS);
        boolean enableFractureShoals = isEnabled(overworld.enabledBiomes, ModWorldgenKeys.Biomes.FRACTURE_SHOALS);

        if (!(enableCrumbledCrossing || enableUnderwayFalls || enableFractureShoals)) {
            return;
        }

        if (config.strictValidation) {
            validatePresentOrThrow(registry, ModWorldgenKeys.Biomes.CRUMBLED_CROSSING, enableCrumbledCrossing);
            validatePresentOrThrow(registry, ModWorldgenKeys.Biomes.UNDERWAY_FALLS, enableUnderwayFalls);
            validatePresentOrThrow(registry, ModWorldgenKeys.Biomes.FRACTURE_SHOALS, enableFractureShoals);
        }

        this.addModifiedVanillaOverworldBiomes(mapper, builder -> {
            if (enableCrumbledCrossing) {
                builder.replaceBiome(Biomes.PLAINS, ModWorldgenKeys.Biomes.CRUMBLED_CROSSING);
            }
            if (enableUnderwayFalls) {
                builder.replaceBiome(Biomes.SWAMP, ModWorldgenKeys.Biomes.UNDERWAY_FALLS);
            }
            if (enableFractureShoals) {
                builder.replaceBiome(Biomes.BEACH, ModWorldgenKeys.Biomes.FRACTURE_SHOALS);
            }
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
