package com.kruemblegard.registry;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.init.ModBlocks;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.WrittenBookItem;

import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, Kruemblegard.MOD_ID);

    public static final RegistryObject<Item> HAUNTED_WAYSTONE_ITEM = ITEMS.register(
        "haunted_waystone",
        () -> new BlockItem(ModBlocks.HAUNTED_WAYSTONE.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> FALSE_WAYSTONE_ITEM = ITEMS.register(
        "false_waystone",
        () -> new BlockItem(ModBlocks.FALSE_WAYSTONE.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> ANCIENT_WAYSTONE_ITEM = ITEMS.register(
        "ancient_waystone",
        () -> new BlockItem(ModBlocks.ANCIENT_WAYSTONE.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> ATTUNED_STONE_ITEM = ITEMS.register(
        "attuned_stone",
        () -> new BlockItem(ModBlocks.ATTUNED_STONE.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> STANDING_STONE_ITEM = ITEMS.register(
        "standing_stone",
        () -> new BlockItem(ModBlocks.STANDING_STONE.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> CRUMBLING_CODEX = ITEMS.register(
        "crumbling_codex",
        () -> new WrittenBookItem(new Item.Properties().stacksTo(1))
    );

    public static final RegistryObject<Item> ATTUNED_RUNE_SHARD = ITEMS.register(
        "attuned_rune_shard",
        () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> RUNIC_CORE_FRAGMENT =
        ITEMS.register("runic_core_fragment", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> STONE_CORE_FRAGMENT = RUNIC_CORE_FRAGMENT;

    public static final RegistryObject<Item> RADIANT_ESSENCE = ITEMS.register(
        "radiant_essence",
        () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> RADIANT_SWORD = ITEMS.register(
        "radiant_sword",
        () -> new SwordItem(Tiers.IRON, 4, -2.2F, new Item.Properties())
    );

    public static final RegistryObject<Item> KRUEMBLEGARD_SPAWN_EGG =
        ITEMS.register(
            "kruemblegard_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.KRUEMBLEGARD, 0x3b2f4a, 0x7a4fff,
                new Item.Properties()));

    @SubscribeEvent
    public static void addToCreativeTabs(BuildCreativeModeTabContentsEvent event) {

    if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
        event.accept(KRUEMBLEGARD_SPAWN_EGG);
    }

    if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
        event.accept(RUNIC_CORE_FRAGMENT);
    }
    }
}
