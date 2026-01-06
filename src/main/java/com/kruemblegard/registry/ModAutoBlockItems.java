package com.kruemblegard.registry;

import com.kruemblegard.Kruemblegard;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.LiquidBlock;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

/**
 * Ensures every mod block has a corresponding BlockItem unless explicitly special-cased.
 *
 * Without this, blocks exist in the registry but do not appear in creative tabs because
 * they lack an item form.
 */
@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModAutoBlockItems {
    private ModAutoBlockItems() {}

    @SubscribeEvent
    public static void onRegister(final RegisterEvent event) {
        if (!event.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS)) {
            return;
        }

        event.register(ForgeRegistries.Keys.ITEMS, helper -> {
            for (Block block : ForgeRegistries.BLOCKS.getValues()) {
                ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
                if (id == null || !Kruemblegard.MOD_ID.equals(id.getNamespace())) {
                    continue;
                }

                // Already has an item registered (manual or special item type).
                if (ForgeRegistries.ITEMS.containsKey(id)) {
                    continue;
                }

                // Common blocks that intentionally don't have a direct item.
                if (block instanceof FlowerPotBlock || block instanceof LiquidBlock) {
                    continue;
                }

                helper.register(id, new BlockItem(block, new Item.Properties()));
            }
        });
    }
}
