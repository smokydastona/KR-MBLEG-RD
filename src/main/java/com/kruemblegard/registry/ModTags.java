package com.kruemblegard.registry;

import com.kruemblegard.Kruemblegard;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public final class ModTags {
    private ModTags() {}

    public static final class Blocks {
        private Blocks() {}

        public static final TagKey<Block> WAYSTONE_ENERGY_SOURCES = TagKey.create(
                Registries.BLOCK,
                new ResourceLocation(Kruemblegard.MOD_ID, "waystone_energy_sources")
        );

        public static final TagKey<Block> WAYFALL_GROUND = TagKey.create(
            Registries.BLOCK,
            new ResourceLocation(Kruemblegard.MOD_ID, "wayfall_ground")
        );

        public static final TagKey<Block> ASHMOSS_SPREAD_TARGETS = TagKey.create(
            Registries.BLOCK,
            new ResourceLocation(Kruemblegard.MOD_ID, "ashmoss_spread_targets")
        );

        public static final TagKey<Block> RUBBLE_TILLABLE = TagKey.create(
            Registries.BLOCK,
            new ResourceLocation(Kruemblegard.MOD_ID, "rubble_tillable")
        );

        public static final TagKey<Block> TREEHARVESTER_CLEAR = TagKey.create(
            Registries.BLOCK,
            new ResourceLocation(Kruemblegard.MOD_ID, "treeharvester_clear")
        );
    }

    public static final class Biomes {
        private Biomes() {}

        public static final TagKey<Biome> ASH_HEAVY = TagKey.create(
                Registries.BIOME,
                new ResourceLocation(Kruemblegard.MOD_ID, "wayfall_ash_heavy")
        );

        public static final TagKey<Biome> VOID_BIOME = TagKey.create(
                Registries.BIOME,
                new ResourceLocation(Kruemblegard.MOD_ID, "wayfall_void")
        );
    }

    /**
     * Biome tags used specifically by worldgen wiring (biome modifiers, structure biomes, etc.).
     *
     * <p>Note: biome tags live under data/&lt;namespace&gt;/tags/worldgen/biome, but are referenced by ID only.</p>
     */
    public static final class WorldgenBiomes {
        private WorldgenBiomes() {}

        public static final TagKey<Biome> WAYFALL = TagKey.create(
                Registries.BIOME,
                new ResourceLocation(Kruemblegard.MOD_ID, "wayfall")
        );

        public static final TagKey<Biome> HAS_MEGALITHIC_CIRCLE = TagKey.create(
                Registries.BIOME,
                new ResourceLocation(Kruemblegard.MOD_ID, "has_structure/megalithic_circle")
        );

        public static final TagKey<Biome> BASIN_OF_SCARS = TagKey.create(
            Registries.BIOME,
            new ResourceLocation(Kruemblegard.MOD_ID, "basin_of_scars")
        );
    }
}
