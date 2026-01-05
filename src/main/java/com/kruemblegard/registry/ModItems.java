package com.kruemblegard.registry;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.init.ModTiers;
import com.kruemblegard.item.CrumblingCodexItem;
import com.kruemblegard.item.RunePetalItem;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.Block;

import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, Kruemblegard.MOD_ID);

    private static RegistryObject<Item> registerBlockItem(String id, RegistryObject<Block> block) {
        return ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static final RegistryObject<Item> MENU_TAB = ITEMS.register(
        "menu_tab",
        () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> ANCIENT_WAYSTONE_ITEM = ITEMS.register(
        "ancient_waystone",
        () -> new BlockItem(ModBlocks.ANCIENT_WAYSTONE.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> ATTUNED_STONE_ITEM = ITEMS.register(
        "attuned_stone",
        () -> new BlockItem(ModBlocks.ATTUNED_STONE.get(), new Item.Properties())
    );

    // --- Wayfall geology block items ---

    public static final RegistryObject<Item> FRACTURED_WAYROCK_ITEM = registerBlockItem("fractured_wayrock", ModBlocks.FRACTURED_WAYROCK);
    public static final RegistryObject<Item> CRUSHSTONE_ITEM = registerBlockItem("crushstone", ModBlocks.CRUSHSTONE);
    public static final RegistryObject<Item> ASHFALL_LOAM_ITEM = registerBlockItem("ashfall_loam", ModBlocks.ASHFALL_LOAM);
    public static final RegistryObject<Item> FAULT_DUST_ITEM = registerBlockItem("fault_dust", ModBlocks.FAULT_DUST);
    public static final RegistryObject<Item> SCARSTONE_ITEM = registerBlockItem("scarstone", ModBlocks.SCARSTONE);
    public static final RegistryObject<Item> POLISHED_SCARSTONE_ITEM = registerBlockItem("polished_scarstone", ModBlocks.POLISHED_SCARSTONE);
    public static final RegistryObject<Item> CHISELED_SCARSTONE_ITEM = registerBlockItem("chiseled_scarstone", ModBlocks.CHISELED_SCARSTONE);
    public static final RegistryObject<Item> CRACKED_SCARSTONE_ITEM = registerBlockItem("cracked_scarstone", ModBlocks.CRACKED_SCARSTONE);

    // --- Stoneveil rubble block items ---

    public static final RegistryObject<Item> STONEVEIL_RUBBLE_ITEM = registerBlockItem("stoneveil_rubble", ModBlocks.STONEVEIL_RUBBLE);
    public static final RegistryObject<Item> POLISHED_STONEVEIL_RUBBLE_ITEM = registerBlockItem("polished_stoneveil_rubble", ModBlocks.POLISHED_STONEVEIL_RUBBLE);
    public static final RegistryObject<Item> RUNED_STONEVEIL_RUBBLE_ITEM = registerBlockItem("runed_stoneveil_rubble", ModBlocks.RUNED_STONEVEIL_RUBBLE);

    // --- Wayfall surface covers block items ---

    public static final RegistryObject<Item> VEILGROWTH_ITEM = registerBlockItem("veilgrowth", ModBlocks.VEILGROWTH);
    public static final RegistryObject<Item> ASHMOSS_ITEM = registerBlockItem("ashmoss", ModBlocks.ASHMOSS);
    public static final RegistryObject<Item> RUNEGROWTH_ITEM = registerBlockItem("runegrowth", ModBlocks.RUNEGROWTH);
    public static final RegistryObject<Item> VOIDFELT_ITEM = registerBlockItem("voidfelt", ModBlocks.VOIDFELT);

    public static final RegistryObject<Item> ATTUNED_ORE_ITEM = ITEMS.register(
        "attuned_ore",
        () -> new BlockItem(ModBlocks.ATTUNED_ORE.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> STANDING_STONE_ITEM = ITEMS.register(
        "standing_stone",
        () -> new BlockItem(ModBlocks.STANDING_STONE.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> WISPSTALK_ITEM = ITEMS.register(
        "wispstalk",
        () -> new BlockItem(ModBlocks.WISPSTALK.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> GRAVEVINE_ITEM = ITEMS.register(
        "gravevine",
        () -> new BlockItem(ModBlocks.GRAVEVINE.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> ECHOCAP_ITEM = ITEMS.register(
        "echocap",
        () -> new BlockItem(ModBlocks.ECHOCAP.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> RUNEBLOOM_ITEM = ITEMS.register(
        "runebloom",
        () -> new BlockItem(ModBlocks.RUNEBLOOM.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> SOULBERRY_SHRUB_ITEM = ITEMS.register(
        "soulberry_shrub",
        () -> new BlockItem(ModBlocks.SOULBERRY_SHRUB.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> GHOULBERRY_SHRUB_ITEM = ITEMS.register(
        "ghoulberry_shrub",
        () -> new BlockItem(ModBlocks.GHOULBERRY_SHRUB.get(), new Item.Properties())
    );
    public static final RegistryObject<Item> DRIFTWOOD_WOOD_ITEM = registerBlockItem("driftwood_wood", ModBlocks.DRIFTWOOD_WOOD);
    public static final RegistryObject<Item> STRIPPED_DRIFTWOOD_LOG_ITEM = registerBlockItem("stripped_driftwood_log", ModBlocks.STRIPPED_DRIFTWOOD_LOG);
    public static final RegistryObject<Item> STRIPPED_DRIFTWOOD_WOOD_ITEM = registerBlockItem("stripped_driftwood_wood", ModBlocks.STRIPPED_DRIFTWOOD_WOOD);
    public static final RegistryObject<Item> DRIFTWOOD_PLANKS_ITEM = registerBlockItem("driftwood_planks", ModBlocks.DRIFTWOOD_PLANKS);
    public static final RegistryObject<Item> DRIFTWOOD_LEAVES_ITEM = registerBlockItem("driftwood_leaves", ModBlocks.DRIFTWOOD_LEAVES);
    public static final RegistryObject<Item> DRIFTWOOD_SAPLING_ITEM = registerBlockItem("driftwood_sapling", ModBlocks.DRIFTWOOD_SAPLING);
    public static final RegistryObject<Item> DRIFTWOOD_STAIRS_ITEM = registerBlockItem("driftwood_stairs", ModBlocks.DRIFTWOOD_STAIRS);
    public static final RegistryObject<Item> DRIFTWOOD_SLAB_ITEM = registerBlockItem("driftwood_slab", ModBlocks.DRIFTWOOD_SLAB);
    public static final RegistryObject<Item> DRIFTWOOD_FENCE_ITEM = registerBlockItem("driftwood_fence", ModBlocks.DRIFTWOOD_FENCE);
    public static final RegistryObject<Item> DRIFTWOOD_FENCE_GATE_ITEM = registerBlockItem("driftwood_fence_gate", ModBlocks.DRIFTWOOD_FENCE_GATE);
    public static final RegistryObject<Item> DRIFTWOOD_DOOR_ITEM = registerBlockItem("driftwood_door", ModBlocks.DRIFTWOOD_DOOR);
    public static final RegistryObject<Item> DRIFTWOOD_TRAPDOOR_ITEM = registerBlockItem("driftwood_trapdoor", ModBlocks.DRIFTWOOD_TRAPDOOR);
    public static final RegistryObject<Item> DRIFTWOOD_BUTTON_ITEM = registerBlockItem("driftwood_button", ModBlocks.DRIFTWOOD_BUTTON);
    public static final RegistryObject<Item> DRIFTWOOD_PRESSURE_PLATE_ITEM = registerBlockItem("driftwood_pressure_plate", ModBlocks.DRIFTWOOD_PRESSURE_PLATE);

    public static final RegistryObject<Item> REMNANT_SEEDS = ITEMS.register(
        "remnant_seeds",
        () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> RUNE_PETALS = ITEMS.register(
        "rune_petals",
        () -> new RunePetalItem(new Item.Properties())
    );

    public static final RegistryObject<Item> SOULBERRIES = ITEMS.register(
        "soulberries",
        () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.3f).build()))
    );

    public static final RegistryObject<Item> GHOULBERRIES = ITEMS.register(
        "ghoulberries",
        () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.1f).build()))
    );

    public static final RegistryObject<Item> CRUMBLING_CODEX = ITEMS.register(
        "crumbling_codex",
        () -> new CrumblingCodexItem(new Item.Properties().stacksTo(1))
    );

    public static final RegistryObject<Item> ATTUNED_RUNE_SHARD = ITEMS.register(
        "attuned_rune_shard",
        () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> RUNIC_CORE =
        ITEMS.register("runic_core", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> RUNIC_SWORD = ITEMS.register(
        "runic_sword",
        () -> new SwordItem(ModTiers.RUNIC, 3, -2.4F, new Item.Properties())
    );

    public static final RegistryObject<Item> RUNIC_PICKAXE = ITEMS.register(
        "runic_pickaxe",
        () -> new PickaxeItem(ModTiers.RUNIC, 1, -2.8F, new Item.Properties())
    );

    public static final RegistryObject<Item> RUNIC_AXE = ITEMS.register(
        "runic_axe",
        () -> new AxeItem(ModTiers.RUNIC, 5.0F, -3.0F, new Item.Properties())
    );

    public static final RegistryObject<Item> RUNIC_SHOVEL = ITEMS.register(
        "runic_shovel",
        () -> new ShovelItem(ModTiers.RUNIC, 1.5F, -3.0F, new Item.Properties())
    );

    public static final RegistryObject<Item> RUNIC_HOE = ITEMS.register(
        "runic_hoe",
        () -> new HoeItem(ModTiers.RUNIC, -3, 0.0F, new Item.Properties())
    );

    public static final RegistryObject<Item> KRUEMBLEGARD_SPAWN_EGG =
        ITEMS.register(
            "kruemblegard_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.KRUEMBLEGARD, 0x3b2f4a, 0x7a4fff,
                new Item.Properties()));

    public static final RegistryObject<Item> TRAPROCK_SPAWN_EGG =
        ITEMS.register(
            "traprock_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.TRAPROCK, 0x3a2f2a, 0xd86b2e,
                new Item.Properties()));

    public static final RegistryObject<Item> PEBBLIT_SPAWN_EGG =
        ITEMS.register(
            "pebblit_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.PEBBLIT, 0x5a5147, 0xb4aa9d,
                new Item.Properties()));
}
