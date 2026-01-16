package com.kruemblegard.worldgen;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.config.worldgen.WorldgenTuningConfig;
import com.kruemblegard.registry.ModTags;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public final class WorldgenValidator {
    private WorldgenValidator() {}

    public static void validate(MinecraftServer server) {
        validate(server, WorldgenTuningConfig.get().strictValidation);
    }

    public static void validate(MinecraftServer server, boolean strict) {
        if (server == null) {
            return;
        }

        var registries = server.registryAccess();

        // Biome tag sanity.
        validateBiomeTagNonEmpty(registries.registryOrThrow(Registries.BIOME), ModTags.WorldgenBiomes.WAYFALL, strict);
        validateBiomeTagNonEmpty(registries.registryOrThrow(Registries.BIOME), ModTags.WorldgenBiomes.HAS_MEGALITHIC_CIRCLE, strict);

        // Critical registry keys referenced by datapack.
        validatePresent(registries.registryOrThrow(Registries.NOISE_SETTINGS), ModWorldgenKeys.NoiseSettings.WAYFALL, strict);
        validatePresent(registries.registryOrThrow(Registries.LEVEL_STEM), ModWorldgenKeys.Dimensions.WAYFALL, strict);
        validatePresent(registries.registryOrThrow(Registries.DIMENSION_TYPE), ModWorldgenKeys.DimensionTypes.WAYFALL, strict);

        validatePresent(registries.registryOrThrow(Registries.STRUCTURE), ModWorldgenKeys.Structures.MEGALITHIC_CIRCLE, strict);
        validatePresent(registries.registryOrThrow(Registries.STRUCTURE_SET), ModWorldgenKeys.StructureSets.MEGALITHIC_CIRCLE, strict);
        validatePresent(registries.registryOrThrow(Registries.TEMPLATE_POOL), ModWorldgenKeys.TemplatePools.MEGALITHIC_CIRCLE_START, strict);
        validatePresent(registries.registryOrThrow(Registries.PROCESSOR_LIST), ModWorldgenKeys.ProcessorLists.MEGALITHIC_RUINS, strict);

        // Representative placed features referenced by biome modifiers.
        validatePresent(registries.registryOrThrow(Registries.PLACED_FEATURE), ModWorldgenKeys.PlacedFeatures.ASHBLOOM_TREE, strict);
        validatePresent(registries.registryOrThrow(Registries.PLACED_FEATURE), ModWorldgenKeys.PlacedFeatures.GLIMMERPINE_TREE, strict);
        validatePresent(registries.registryOrThrow(Registries.PLACED_FEATURE), ModWorldgenKeys.PlacedFeatures.DRIFTWOOD_TREE, strict);
        validatePresent(registries.registryOrThrow(Registries.PLACED_FEATURE), ModWorldgenKeys.PlacedFeatures.ATTUNED_ORE, strict);

        // Detect whether biome modifiers actually injected those placed features into Wayfall biomes.
        validateWayfallBiomesContainInjectedFeatures(
            registries.registryOrThrow(Registries.BIOME),
            registries.registryOrThrow(Registries.PLACED_FEATURE)
        );
    }

    private static <T> void validatePresent(Registry<T> registry, net.minecraft.resources.ResourceKey<T> key, boolean strict) {
        if (registry.containsKey(key.location())) {
            return;
        }

        String message = "Worldgen registry missing " + registry.key().location() + ": " + key.location();
        if (strict) {
            throw new IllegalStateException(message);
        }
        Kruemblegard.LOGGER.warn(message);
    }

    private static void validateBiomeTagNonEmpty(Registry<net.minecraft.world.level.biome.Biome> registry, TagKey<net.minecraft.world.level.biome.Biome> tag, boolean strict) {
        var holders = registry.getTag(tag);
        if (holders.isEmpty()) {
            String message = "Biome tag is missing or empty: " + tag.location();
            if (strict) {
                throw new IllegalStateException(message);
            }
            Kruemblegard.LOGGER.warn(message);
            return;
        }

        if (!holders.get().iterator().hasNext()) {
            String message = "Biome tag is present but empty: " + tag.location();
            if (strict) {
                throw new IllegalStateException(message);
            }
            Kruemblegard.LOGGER.warn(message);
        }
    }

    private static void validateWayfallBiomesContainInjectedFeatures(Registry<Biome> biomeRegistry, Registry<PlacedFeature> placedFeatureRegistry) {
        var wayfallTag = biomeRegistry.getTag(ModTags.WorldgenBiomes.WAYFALL);
        if (wayfallTag.isEmpty() || !wayfallTag.get().iterator().hasNext()) {
            return;
        }

        var expectedKeys = List.of(
                ModWorldgenKeys.PlacedFeatures.ASHBLOOM_TREE,
                ModWorldgenKeys.PlacedFeatures.GLIMMERPINE_TREE,
                ModWorldgenKeys.PlacedFeatures.DRIFTWOOD_TREE,
                ModWorldgenKeys.PlacedFeatures.ATTUNED_ORE
        );

        Set<ResourceLocation> expectedLocations = new HashSet<>();
        for (var key : expectedKeys) {
            if (placedFeatureRegistry.containsKey(key.location())) {
                expectedLocations.add(key.location());
            }
        }

        int wayfallBiomesChecked = 0;
        int wayfallBiomesWithAnyPlacedFeatures = 0;
        int wayfallBiomesWithExpectedPlacedFeatures = 0;
        Map<ResourceLocation, Integer> expectedSeenCounts = new HashMap<>();

        for (Holder<Biome> biomeHolder : wayfallTag.get()) {
            wayfallBiomesChecked++;

            Biome biome = biomeHolder.value();
            int totalPlacedFeatures = 0;
            boolean foundExpectedInThisBiome = false;

            for (var stepFeatures : biome.getGenerationSettings().features()) {
                for (Holder<PlacedFeature> placedFeatureHolder : stepFeatures) {
                    totalPlacedFeatures++;
                    var keyOpt = placedFeatureHolder.unwrapKey();
                    if (keyOpt.isPresent()) {
                        ResourceLocation id = keyOpt.get().location();
                        if (expectedLocations.contains(id)) {
                            foundExpectedInThisBiome = true;
                            expectedSeenCounts.merge(id, 1, Integer::sum);
                        }
                    }
                }
            }

            if (totalPlacedFeatures > 0) {
                wayfallBiomesWithAnyPlacedFeatures++;
            } else {
                Kruemblegard.LOGGER.warn(
                        "Wayfall biome has 0 placed features (biome modifiers likely not applied): {}",
                        biomeHolder.unwrapKey().map(ResourceKey::location).orElseGet(() -> new ResourceLocation("unknown", "unknown"))
                );
            }

            if (foundExpectedInThisBiome) {
                wayfallBiomesWithExpectedPlacedFeatures++;
            }
        }

        if (expectedLocations.isEmpty()) {
            return;
        }

        int wayfallBiomesWithZeroPlacedFeatures = wayfallBiomesChecked - wayfallBiomesWithAnyPlacedFeatures;
        if (wayfallBiomesWithZeroPlacedFeatures > 0) {
            Kruemblegard.LOGGER.warn(
                    "{} / {} Wayfall biomes have 0 placed features (biome modifiers likely not applying consistently).",
                    wayfallBiomesWithZeroPlacedFeatures,
                    wayfallBiomesChecked
            );
        }

        if (wayfallBiomesWithExpectedPlacedFeatures == 0) {
            Kruemblegard.LOGGER.warn(
                    "No Wayfall biomes include expected placed features {} (biome modifiers likely not applied at all).",
                    expectedLocations
            );
            return;
        }

        for (var expectedId : expectedLocations) {
            if (!expectedSeenCounts.containsKey(expectedId)) {
                Kruemblegard.LOGGER.warn(
                        "Expected placed feature was not found in any Wayfall biome generation settings: {}",
                        expectedId
                );
            }
        }
    }
}
