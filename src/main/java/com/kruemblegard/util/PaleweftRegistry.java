package com.kruemblegard.util;

import com.kruemblegard.Kruemblegard;

import javax.annotation.Nullable;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import net.minecraftforge.registries.ForgeRegistries;

public final class PaleweftRegistry {
    private PaleweftRegistry() {}

    public static final ResourceLocation RUBBLE_TILTH_ID = new ResourceLocation(Kruemblegard.MOD_ID, "rubble_tilth");
    public static final ResourceLocation PALEWEFT_GRASS_ID = new ResourceLocation(Kruemblegard.MOD_ID, "paleweft_grass");
    public static final ResourceLocation PALEWEFT_TALL_GRASS_ID = new ResourceLocation(Kruemblegard.MOD_ID, "paleweft_tall_grass");

    public static final ResourceLocation PALEWEFT_SEEDS_ID = new ResourceLocation(Kruemblegard.MOD_ID, "paleweft_seeds");

    public static final TagKey<Block> RUBBLE_TILLABLE_TAG = TagKey.create(
            Registries.BLOCK,
            new ResourceLocation(Kruemblegard.MOD_ID, "rubble_tillable")
    );

    @Nullable
    public static Block getBlock(ResourceLocation id) {
        return ForgeRegistries.BLOCKS.getValue(id);
    }

    @Nullable
    public static Item getItem(ResourceLocation id) {
        return ForgeRegistries.ITEMS.getValue(id);
    }
}
