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

        // Shardbark Pine -> Splinterspore (full registry rename).
        remapBlock(event, "shardbark_pine_log", ModBlocks.SPLINTERSPORE_LOG.get());
        remapBlock(event, "shardbark_pine_wood", ModBlocks.SPLINTERSPORE_WOOD.get());
        remapBlock(event, "stripped_shardbark_pine_log", ModBlocks.STRIPPED_SPLINTERSPORE_LOG.get());
        remapBlock(event, "stripped_shardbark_pine_wood", ModBlocks.STRIPPED_SPLINTERSPORE_WOOD.get());
        remapBlock(event, "shardbark_pine_planks", ModBlocks.SPLINTERSPORE_PLANKS.get());
        remapBlock(event, "shardbark_pine_leaves", ModBlocks.SPLINTERSPORE_LEAVES.get());
        remapBlock(event, "shardbark_pine_sapling", ModBlocks.SPLINTERSPORE_SAPLING.get());
        remapBlock(event, "shardbark_pine_stairs", ModBlocks.SPLINTERSPORE_STAIRS.get());
        remapBlock(event, "shardbark_pine_slab", ModBlocks.SPLINTERSPORE_SLAB.get());
        remapBlock(event, "shardbark_pine_fence", ModBlocks.SPLINTERSPORE_FENCE.get());
        remapBlock(event, "shardbark_pine_fence_gate", ModBlocks.SPLINTERSPORE_FENCE_GATE.get());
        remapBlock(event, "shardbark_pine_door", ModBlocks.SPLINTERSPORE_DOOR.get());
        remapBlock(event, "shardbark_pine_trapdoor", ModBlocks.SPLINTERSPORE_TRAPDOOR.get());
        remapBlock(event, "shardbark_pine_button", ModBlocks.SPLINTERSPORE_BUTTON.get());
        remapBlock(event, "shardbark_pine_pressure_plate", ModBlocks.SPLINTERSPORE_PRESSURE_PLATE.get());

        remapAutoBlockItem(event, "shardbark_pine_log", "splinterspore_log");
        remapAutoBlockItem(event, "shardbark_pine_wood", "splinterspore_wood");
        remapAutoBlockItem(event, "stripped_shardbark_pine_log", "stripped_splinterspore_log");
        remapAutoBlockItem(event, "stripped_shardbark_pine_wood", "stripped_splinterspore_wood");
        remapAutoBlockItem(event, "shardbark_pine_planks", "splinterspore_planks");
        remapAutoBlockItem(event, "shardbark_pine_leaves", "splinterspore_leaves");
        remapAutoBlockItem(event, "shardbark_pine_sapling", "splinterspore_sapling");
        remapAutoBlockItem(event, "shardbark_pine_stairs", "splinterspore_stairs");
        remapAutoBlockItem(event, "shardbark_pine_slab", "splinterspore_slab");
        remapAutoBlockItem(event, "shardbark_pine_fence", "splinterspore_fence");
        remapAutoBlockItem(event, "shardbark_pine_fence_gate", "splinterspore_fence_gate");
        remapAutoBlockItem(event, "shardbark_pine_door", "splinterspore_door");
        remapAutoBlockItem(event, "shardbark_pine_trapdoor", "splinterspore_trapdoor");
        remapAutoBlockItem(event, "shardbark_pine_button", "splinterspore_button");
        remapAutoBlockItem(event, "shardbark_pine_pressure_plate", "splinterspore_pressure_plate");
    }

    private static void remapAutoBlockItem(
            final MissingMappingsEvent event,
            final String oldPath,
            final String newPath
    ) {
        final ResourceLocation newId = new ResourceLocation(Kruemblegard.MOD_ID, newPath);
        final Item remapTo = ForgeRegistries.ITEMS.getValue(newId);
        if (remapTo != null) {
            remapItem(event, oldPath, remapTo);
        }
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
