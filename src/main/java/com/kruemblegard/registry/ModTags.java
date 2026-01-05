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
}
