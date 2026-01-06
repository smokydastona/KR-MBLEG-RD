package com.kruemblegard.worldgen;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.registry.ModTags;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;

public final class WorldgenValidator {
    private WorldgenValidator() {}

    public static void validate(MinecraftServer server) {
        if (server == null) {
            return;
        }

        var registries = server.registryAccess();

        // Biome tag sanity.
        validateBiomeTagNonEmpty(registries.registryOrThrow(Registries.BIOME), ModTags.WorldgenBiomes.WAYFALL);
        validateBiomeTagNonEmpty(registries.registryOrThrow(Registries.BIOME), ModTags.WorldgenBiomes.HAS_MEGALITHIC_CIRCLE);

        // Critical registry keys referenced by datapack.
        validatePresent(registries.registryOrThrow(Registries.NOISE_SETTINGS), ModWorldgenKeys.NoiseSettings.WAYFALL);
        validatePresent(registries.registryOrThrow(Registries.LEVEL_STEM), ModWorldgenKeys.Dimensions.WAYFALL);
        validatePresent(registries.registryOrThrow(Registries.DIMENSION_TYPE), ModWorldgenKeys.DimensionTypes.WAYFALL);

        validatePresent(registries.registryOrThrow(Registries.STRUCTURE), ModWorldgenKeys.Structures.MEGALITHIC_CIRCLE);
        validatePresent(registries.registryOrThrow(Registries.STRUCTURE_SET), ModWorldgenKeys.StructureSets.MEGALITHIC_CIRCLE);
        validatePresent(registries.registryOrThrow(Registries.TEMPLATE_POOL), ModWorldgenKeys.TemplatePools.MEGALITHIC_CIRCLE_START);
        validatePresent(registries.registryOrThrow(Registries.PROCESSOR_LIST), ModWorldgenKeys.ProcessorLists.MEGALITHIC_RUINS);

        // Representative placed features referenced by biome modifiers.
        validatePresent(registries.registryOrThrow(Registries.PLACED_FEATURE), ModWorldgenKeys.PlacedFeatures.ASHBLOOM_TREE);
        validatePresent(registries.registryOrThrow(Registries.PLACED_FEATURE), ModWorldgenKeys.PlacedFeatures.GLIMMERPINE_TREE);
        validatePresent(registries.registryOrThrow(Registries.PLACED_FEATURE), ModWorldgenKeys.PlacedFeatures.DRIFTWOOD_TREE);
        validatePresent(registries.registryOrThrow(Registries.PLACED_FEATURE), ModWorldgenKeys.PlacedFeatures.ATTUNED_ORE);
    }

    private static <T> void validatePresent(Registry<T> registry, net.minecraft.resources.ResourceKey<T> key) {
        if (!registry.containsKey(key.location())) {
            Kruemblegard.LOGGER.warn("Worldgen registry missing {}: {}", registry.key().location(), key.location());
        }
    }

    private static void validateBiomeTagNonEmpty(Registry<net.minecraft.world.level.biome.Biome> registry, TagKey<net.minecraft.world.level.biome.Biome> tag) {
        var holders = registry.getTag(tag);
        if (holders.isEmpty()) {
            Kruemblegard.LOGGER.warn("Biome tag is missing or empty: {}", tag.location());
            return;
        }

        if (!holders.get().iterator().hasNext()) {
            Kruemblegard.LOGGER.warn("Biome tag is present but empty: {}", tag.location());
        }
    }
}
