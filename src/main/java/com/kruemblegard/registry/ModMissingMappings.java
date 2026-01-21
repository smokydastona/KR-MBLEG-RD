package com.kruemblegard.registry;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.init.ModBlocks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ModMissingMappings {
    private ModMissingMappings() {}

    @SubscribeEvent
    public static void onMissingMappings(final MissingMappingsEvent event) {
        // Keep existing worlds/inventories stable after progression renames.
        remapBlock(event, "attuned_ore", ModBlocks.RUNIC_DEBRIS.get());
        remapItem(event, "attuned_ore", ModItems.RUNIC_DEBRIS_ITEM.get());
        remapItem(event, "raw_attuned_ore", ModItems.RUNIC_SCRAP.get());
    }

    private static void remapBlock(final MissingMappingsEvent event, final String oldPath, final Block remapTo) {
        final ResourceLocation oldId = new ResourceLocation(Kruemblegard.MOD_ID, oldPath);
        final List<MissingMappingsEvent.Mapping<Block>> mappings =
                event.getMappings(ForgeRegistries.Keys.BLOCKS, Kruemblegard.MOD_ID);

        for (MissingMappingsEvent.Mapping<Block> mapping : mappings) {
            if (oldId.equals(mapping.getKey())) {
                mapping.remap(remapTo);
            }
        }
    }

    private static void remapItem(final MissingMappingsEvent event, final String oldPath, final Item remapTo) {
        final ResourceLocation oldId = new ResourceLocation(Kruemblegard.MOD_ID, oldPath);
        final List<MissingMappingsEvent.Mapping<Item>> mappings =
                event.getMappings(ForgeRegistries.Keys.ITEMS, Kruemblegard.MOD_ID);

        for (MissingMappingsEvent.Mapping<Item> mapping : mappings) {
            if (oldId.equals(mapping.getKey())) {
                mapping.remap(remapTo);
            }
        }
    }
}
