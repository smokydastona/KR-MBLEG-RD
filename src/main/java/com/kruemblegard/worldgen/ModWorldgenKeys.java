package com.kruemblegard.worldgen;

import com.kruemblegard.Kruemblegard;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.biome.Biome;

/**
 * Centralized registry keys for data-driven worldgen.
 *
 * <p>Forge/Minecraft load everything under data/kruemblegard/worldgen/** automatically.
 * These keys exist so code refers to a single source of truth (no scattered strings),
 * and so we can validate critical resources at runtime.</p>
 */
public final class ModWorldgenKeys {
    private ModWorldgenKeys() {}

    private static ResourceLocation id(String path) {
        return new ResourceLocation(Kruemblegard.MOD_ID, path);
    }

    public static final class Dimensions {
        private Dimensions() {}

        public static final ResourceKey<LevelStem> WAYFALL = ResourceKey.create(Registries.LEVEL_STEM, id("wayfall"));
    }

    public static final class Levels {
        private Levels() {}

        public static final ResourceKey<Level> WAYFALL = ResourceKey.create(Registries.DIMENSION, id("wayfall"));
    }

    public static final class DimensionTypes {
        private DimensionTypes() {}

        public static final ResourceKey<DimensionType> WAYFALL = ResourceKey.create(Registries.DIMENSION_TYPE, id("wayfall"));
    }

    public static final class NoiseSettings {
        private NoiseSettings() {}

        public static final ResourceKey<NoiseGeneratorSettings> WAYFALL = ResourceKey.create(Registries.NOISE_SETTINGS, id("wayfall"));
    }

    public static final class Biomes {
        private Biomes() {}

        public static final ResourceKey<Biome> DRIFTWAY_CHASM = ResourceKey.create(Registries.BIOME, id("driftway_chasm"));
        public static final ResourceKey<Biome> RIVEN_CAUSEWAYS = ResourceKey.create(Registries.BIOME, id("riven_causeways"));
        public static final ResourceKey<Biome> CRUMBLED_CROSSING = ResourceKey.create(Registries.BIOME, id("crumbled_crossing"));
        public static final ResourceKey<Biome> HOLLOW_TRANSIT_PLAINS = ResourceKey.create(Registries.BIOME, id("hollow_transit_plains"));
        public static final ResourceKey<Biome> MIDWEFT_WILDS = ResourceKey.create(Registries.BIOME, id("midweft_wilds"));
        public static final ResourceKey<Biome> FAULTED_EXPANSE = ResourceKey.create(Registries.BIOME, id("faulted_expanse"));
        public static final ResourceKey<Biome> FRACTURE_SHOALS = ResourceKey.create(Registries.BIOME, id("fracture_shoals"));
        public static final ResourceKey<Biome> STRATA_COLLAPSE = ResourceKey.create(Registries.BIOME, id("strata_collapse"));
        public static final ResourceKey<Biome> UNDERWAY_FALLS = ResourceKey.create(Registries.BIOME, id("underway_falls"));
        public static final ResourceKey<Biome> BASIN_OF_SCARS = ResourceKey.create(Registries.BIOME, id("basin_of_scars"));
        public static final ResourceKey<Biome> SHATTERPLATE_FLATS = ResourceKey.create(Registries.BIOME, id("shatterplate_flats"));
        public static final ResourceKey<Biome> GLYPHSCAR_REACH = ResourceKey.create(Registries.BIOME, id("glyphscar_reach"));
    }

    public static final class Structures {
        private Structures() {}

        public static final ResourceKey<Structure> MEGALITHIC_CIRCLE = ResourceKey.create(Registries.STRUCTURE, id("megalithic_circle"));
        public static final ResourceKey<Structure> LOST_PILLAGER_SHIP = ResourceKey.create(Registries.STRUCTURE, id("lost_pillager_ship"));
    }

    public static final class StructureSets {
        private StructureSets() {}

        public static final ResourceKey<StructureSet> MEGALITHIC_CIRCLE = ResourceKey.create(Registries.STRUCTURE_SET, id("megalithic_circle"));
        public static final ResourceKey<StructureSet> LOST_PILLAGER_SHIP = ResourceKey.create(Registries.STRUCTURE_SET, id("lost_pillager_ship"));
    }

    public static final class TemplatePools {
        private TemplatePools() {}

        public static final ResourceKey<StructureTemplatePool> MEGALITHIC_CIRCLE_START = ResourceKey.create(
                Registries.TEMPLATE_POOL,
                id("megalithic_circle/start")
        );

        public static final ResourceKey<StructureTemplatePool> WAYFALL_ORIGIN_ISLAND_DEFAULT = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            id("wayfall_origin_island/default")
        );
        public static final ResourceKey<StructureTemplatePool> WAYFALL_ORIGIN_ISLAND_ASHFALL = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            id("wayfall_origin_island/ashfall")
        );
        public static final ResourceKey<StructureTemplatePool> WAYFALL_ORIGIN_ISLAND_VOIDFELT = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            id("wayfall_origin_island/voidfelt")
        );
        public static final ResourceKey<StructureTemplatePool> WAYFALL_ORIGIN_ISLAND_FRACTURED = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            id("wayfall_origin_island/fractured")
        );
        public static final ResourceKey<StructureTemplatePool> WAYFALL_ORIGIN_ISLAND_GLYPHSCAR = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            id("wayfall_origin_island/glyphscar")
        );
        public static final ResourceKey<StructureTemplatePool> WAYFALL_ORIGIN_ISLAND_BASIN_OF_SCARS = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            id("wayfall_origin_island/basin_of_scars")
        );
        public static final ResourceKey<StructureTemplatePool> WAYFALL_ORIGIN_ISLAND_COLD = ResourceKey.create(
            Registries.TEMPLATE_POOL,
            id("wayfall_origin_island/cold")
        );
    }

    public static final class ProcessorLists {
        private ProcessorLists() {}

        public static final ResourceKey<StructureProcessorList> MEGALITHIC_RUINS = ResourceKey.create(
                Registries.PROCESSOR_LIST,
                id("megalithic_ruins")
        );

        public static final ResourceKey<StructureProcessorList> WAYFALL_ORIGIN_ISLAND_RUNEGROWTH_RESONANT = ResourceKey.create(
            Registries.PROCESSOR_LIST,
            id("wayfall_origin_island_runegrowth_resonant")
        );
        public static final ResourceKey<StructureProcessorList> WAYFALL_ORIGIN_ISLAND_RUNEGROWTH_FROSTBOUND = ResourceKey.create(
            Registries.PROCESSOR_LIST,
            id("wayfall_origin_island_runegrowth_frostbound")
        );
        public static final ResourceKey<StructureProcessorList> WAYFALL_ORIGIN_ISLAND_RUNEGROWTH_VERDANT = ResourceKey.create(
            Registries.PROCESSOR_LIST,
            id("wayfall_origin_island_runegrowth_verdant")
        );
        public static final ResourceKey<StructureProcessorList> WAYFALL_ORIGIN_ISLAND_RUNEGROWTH_EMBERWARMED = ResourceKey.create(
            Registries.PROCESSOR_LIST,
            id("wayfall_origin_island_runegrowth_emberwarmed")
        );
    }

    public static final class PlacedFeatures {
        private PlacedFeatures() {}

        public static final ResourceKey<PlacedFeature> ASHBLOOM_TREE = ResourceKey.create(Registries.PLACED_FEATURE, id("ashbloom_tree"));
        public static final ResourceKey<PlacedFeature> ASHBLOOM_SAPLINGS = ResourceKey.create(Registries.PLACED_FEATURE, id("ashbloom_saplings"));
        public static final ResourceKey<PlacedFeature> GLIMMERPINE_TREE = ResourceKey.create(Registries.PLACED_FEATURE, id("glimmerpine_tree"));
        public static final ResourceKey<PlacedFeature> DRIFTWOOD_TREE = ResourceKey.create(Registries.PLACED_FEATURE, id("driftwood_tree"));
        public static final ResourceKey<PlacedFeature> RUNIC_DEBRIS = ResourceKey.create(Registries.PLACED_FEATURE, id("runic_debris"));
    }

    public static final class ConfiguredFeatures {
        private ConfiguredFeatures() {}

        // Intentionally empty for now; your configured features are currently referenced only from JSON.
        // Add ResourceKey constants here if you start referencing configured features from code.
    }
}
