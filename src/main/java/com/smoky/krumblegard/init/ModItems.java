package com.smoky.krumblegard.init;

import com.smoky.krumblegard.KrumblegardMod;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.WrittenBookItem;

import net.minecraftforge.common.ForgeSpawnEggItem;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    private ModItems() {}

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, KrumblegardMod.MODID);

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

    // Lore/UI: vanilla written book screen, content is set via loot-table NBT.
    public static final RegistryObject<Item> CRUMBLING_CODEX = ITEMS.register(
            "crumbling_codex",
            () -> new WrittenBookItem(new Item.Properties().stacksTo(1))
    );

    public static final RegistryObject<Item> ATTUNED_RUNE_SHARD = ITEMS.register(
            "attuned_rune_shard",
            () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> STONE_CORE_FRAGMENT = ITEMS.register(
            "stone_core_fragment",
            () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> RADIANT_ESSENCE = ITEMS.register(
            "radiant_essence",
            () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> RADIANT_SWORD = ITEMS.register(
            "radiant_sword",
            () -> new SwordItem(Tiers.IRON, 4, -2.2F, new Item.Properties())
    );

    public static final RegistryObject<Item> KRUMBLEGARD_SPAWN_EGG = ITEMS.register(
            "krumblegard_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.KRUMBLEGARD,
                    0x2A2A2A,
                    0xC9B36D,
                    new Item.Properties()
            )
    );

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
