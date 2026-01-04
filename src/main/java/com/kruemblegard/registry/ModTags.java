package com.kruemblegard.registry;

import com.kruemblegard.Kruemblegard;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class ModTags {
    private ModTags() {}

    public static final class Blocks {
        private Blocks() {}

        public static final TagKey<Block> WAYSTONE_ENERGY_SOURCES = TagKey.create(
                Registries.BLOCK,
                new ResourceLocation(Kruemblegard.MOD_ID, "waystone_energy_sources")
        );
    }
}
