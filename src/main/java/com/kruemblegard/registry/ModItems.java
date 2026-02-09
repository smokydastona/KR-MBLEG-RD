package com.kruemblegard.registry;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.vehicle.KruemblegardBoatType;
import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.init.ModTiers;
import com.kruemblegard.item.KruemblegardBoatItem;
import com.kruemblegard.item.RunePetalItem;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ChorusFruitItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
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

    // --- Ashspire cactus drops ---

    public static final RegistryObject<Item> ASHSPIRE_NEEDLES = ITEMS.register(
        "ashspire_needles",
        () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> VOLATILE_PULP = ITEMS.register(
        "volatile_pulp",
        () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> ASHSPIRE_SHARDS = ITEMS.register(
        "ashspire_shards",
        () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> ASHSPIRE_FRUIT = ITEMS.register(
        "ashspire_fruit",
        () -> new ChorusFruitItem(new Item.Properties().food(Foods.CHORUS_FRUIT))
    );

    public static final RegistryObject<Item> VOLATILE_RESIN = ITEMS.register(
        "volatile_resin",
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

    public static final RegistryObject<Item> ATTUNED_STONE_STAIRS_ITEM = registerBlockItem("attuned_stone_stairs", ModBlocks.ATTUNED_STONE_STAIRS);
    public static final RegistryObject<Item> ATTUNED_STONE_SLAB_ITEM = registerBlockItem("attuned_stone_slab", ModBlocks.ATTUNED_STONE_SLAB);
    public static final RegistryObject<Item> ATTUNED_STONE_WALL_ITEM = registerBlockItem("attuned_stone_wall", ModBlocks.ATTUNED_STONE_WALL);

    // --- Wayfall geology block items ---

    public static final RegistryObject<Item> FRACTURED_WAYROCK_ITEM = registerBlockItem("fractured_wayrock", ModBlocks.FRACTURED_WAYROCK);
    public static final RegistryObject<Item> FRACTURED_WAYROCK_STAIRS_ITEM = registerBlockItem("fractured_wayrock_stairs", ModBlocks.FRACTURED_WAYROCK_STAIRS);
    public static final RegistryObject<Item> FRACTURED_WAYROCK_SLAB_ITEM = registerBlockItem("fractured_wayrock_slab", ModBlocks.FRACTURED_WAYROCK_SLAB);
    public static final RegistryObject<Item> FRACTURED_WAYROCK_WALL_ITEM = registerBlockItem("fractured_wayrock_wall", ModBlocks.FRACTURED_WAYROCK_WALL);
    public static final RegistryObject<Item> CRUSHSTONE_ITEM = registerBlockItem("crushstone", ModBlocks.CRUSHSTONE);
    public static final RegistryObject<Item> CRUSHSTONE_STAIRS_ITEM = registerBlockItem("crushstone_stairs", ModBlocks.CRUSHSTONE_STAIRS);
    public static final RegistryObject<Item> CRUSHSTONE_SLAB_ITEM = registerBlockItem("crushstone_slab", ModBlocks.CRUSHSTONE_SLAB);
    public static final RegistryObject<Item> CRUSHSTONE_WALL_ITEM = registerBlockItem("crushstone_wall", ModBlocks.CRUSHSTONE_WALL);
    public static final RegistryObject<Item> ASHFALL_LOAM_ITEM = registerBlockItem("ashfall_loam", ModBlocks.ASHFALL_LOAM);
    public static final RegistryObject<Item> ASHFALL_STONE_ITEM = registerBlockItem("ashfall_stone", ModBlocks.ASHFALL_STONE);
    public static final RegistryObject<Item> ASHFALL_STONE_STAIRS_ITEM = registerBlockItem("ashfall_stone_stairs", ModBlocks.ASHFALL_STONE_STAIRS);
    public static final RegistryObject<Item> ASHFALL_STONE_SLAB_ITEM = registerBlockItem("ashfall_stone_slab", ModBlocks.ASHFALL_STONE_SLAB);
    public static final RegistryObject<Item> ASHFALL_STONE_WALL_ITEM = registerBlockItem("ashfall_stone_wall", ModBlocks.ASHFALL_STONE_WALL);
    public static final RegistryObject<Item> POLISHED_ASHFALL_STONE_ITEM = registerBlockItem("polished_ashfall_stone", ModBlocks.POLISHED_ASHFALL_STONE);
    public static final RegistryObject<Item> POLISHED_ASHFALL_STONE_STAIRS_ITEM = registerBlockItem("polished_ashfall_stone_stairs", ModBlocks.POLISHED_ASHFALL_STONE_STAIRS);
    public static final RegistryObject<Item> POLISHED_ASHFALL_STONE_SLAB_ITEM = registerBlockItem("polished_ashfall_stone_slab", ModBlocks.POLISHED_ASHFALL_STONE_SLAB);
    public static final RegistryObject<Item> POLISHED_ASHFALL_STONE_WALL_ITEM = registerBlockItem("polished_ashfall_stone_wall", ModBlocks.POLISHED_ASHFALL_STONE_WALL);
    public static final RegistryObject<Item> FAULT_DUST_ITEM = registerBlockItem("fault_dust", ModBlocks.FAULT_DUST);
    public static final RegistryObject<Item> RUBBLE_TILTH_ITEM = registerBlockItem("rubble_tilth", ModBlocks.RUBBLE_TILTH);
    public static final RegistryObject<Item> SCARSTONE_ITEM = registerBlockItem("scarstone", ModBlocks.SCARSTONE);
    public static final RegistryObject<Item> SCARSTONE_STAIRS_ITEM = registerBlockItem("scarstone_stairs", ModBlocks.SCARSTONE_STAIRS);
    public static final RegistryObject<Item> SCARSTONE_SLAB_ITEM = registerBlockItem("scarstone_slab", ModBlocks.SCARSTONE_SLAB);
    public static final RegistryObject<Item> SCARSTONE_WALL_ITEM = registerBlockItem("scarstone_wall", ModBlocks.SCARSTONE_WALL);
    public static final RegistryObject<Item> POLISHED_SCARSTONE_ITEM = registerBlockItem("polished_scarstone", ModBlocks.POLISHED_SCARSTONE);
    public static final RegistryObject<Item> POLISHED_SCARSTONE_STAIRS_ITEM = registerBlockItem("polished_scarstone_stairs", ModBlocks.POLISHED_SCARSTONE_STAIRS);
    public static final RegistryObject<Item> POLISHED_SCARSTONE_SLAB_ITEM = registerBlockItem("polished_scarstone_slab", ModBlocks.POLISHED_SCARSTONE_SLAB);
    public static final RegistryObject<Item> POLISHED_SCARSTONE_WALL_ITEM = registerBlockItem("polished_scarstone_wall", ModBlocks.POLISHED_SCARSTONE_WALL);
    public static final RegistryObject<Item> CHISELED_SCARSTONE_ITEM = registerBlockItem("chiseled_scarstone", ModBlocks.CHISELED_SCARSTONE);
    public static final RegistryObject<Item> CRACKED_SCARSTONE_ITEM = registerBlockItem("cracked_scarstone", ModBlocks.CRACKED_SCARSTONE);
    public static final RegistryObject<Item> CRACKED_SCARSTONE_STAIRS_ITEM = registerBlockItem("cracked_scarstone_stairs", ModBlocks.CRACKED_SCARSTONE_STAIRS);
    public static final RegistryObject<Item> CRACKED_SCARSTONE_SLAB_ITEM = registerBlockItem("cracked_scarstone_slab", ModBlocks.CRACKED_SCARSTONE_SLAB);
    public static final RegistryObject<Item> CRACKED_SCARSTONE_WALL_ITEM = registerBlockItem("cracked_scarstone_wall", ModBlocks.CRACKED_SCARSTONE_WALL);

    // --- Stoneveil rubble block items ---

    public static final RegistryObject<Item> STONEVEIL_RUBBLE_ITEM = registerBlockItem("stoneveil_rubble", ModBlocks.STONEVEIL_RUBBLE);
    public static final RegistryObject<Item> STONEVEIL_RUBBLE_STAIRS_ITEM = registerBlockItem("stoneveil_rubble_stairs", ModBlocks.STONEVEIL_RUBBLE_STAIRS);
    public static final RegistryObject<Item> STONEVEIL_RUBBLE_SLAB_ITEM = registerBlockItem("stoneveil_rubble_slab", ModBlocks.STONEVEIL_RUBBLE_SLAB);
    public static final RegistryObject<Item> STONEVEIL_RUBBLE_WALL_ITEM = registerBlockItem("stoneveil_rubble_wall", ModBlocks.STONEVEIL_RUBBLE_WALL);
    public static final RegistryObject<Item> POLISHED_STONEVEIL_RUBBLE_ITEM = registerBlockItem("polished_stoneveil_rubble", ModBlocks.POLISHED_STONEVEIL_RUBBLE);
    public static final RegistryObject<Item> POLISHED_STONEVEIL_RUBBLE_STAIRS_ITEM = registerBlockItem("polished_stoneveil_rubble_stairs", ModBlocks.POLISHED_STONEVEIL_RUBBLE_STAIRS);
    public static final RegistryObject<Item> POLISHED_STONEVEIL_RUBBLE_SLAB_ITEM = registerBlockItem("polished_stoneveil_rubble_slab", ModBlocks.POLISHED_STONEVEIL_RUBBLE_SLAB);
    public static final RegistryObject<Item> POLISHED_STONEVEIL_RUBBLE_WALL_ITEM = registerBlockItem("polished_stoneveil_rubble_wall", ModBlocks.POLISHED_STONEVEIL_RUBBLE_WALL);
    public static final RegistryObject<Item> RUNED_STONEVEIL_RUBBLE_ITEM = registerBlockItem("runed_stoneveil_rubble", ModBlocks.RUNED_STONEVEIL_RUBBLE);
    public static final RegistryObject<Item> RUNED_STONEVEIL_RUBBLE_STAIRS_ITEM = registerBlockItem("runed_stoneveil_rubble_stairs", ModBlocks.RUNED_STONEVEIL_RUBBLE_STAIRS);
    public static final RegistryObject<Item> RUNED_STONEVEIL_RUBBLE_SLAB_ITEM = registerBlockItem("runed_stoneveil_rubble_slab", ModBlocks.RUNED_STONEVEIL_RUBBLE_SLAB);
    public static final RegistryObject<Item> RUNED_STONEVEIL_RUBBLE_WALL_ITEM = registerBlockItem("runed_stoneveil_rubble_wall", ModBlocks.RUNED_STONEVEIL_RUBBLE_WALL);

    // --- Wayfall surface covers block items ---

    public static final RegistryObject<Item> ASHMOSS_ITEM = registerBlockItem("ashmoss", ModBlocks.ASHMOSS);
    public static final RegistryObject<Item> ASHMOSS_CARPET_ITEM = registerBlockItem("ashmoss_carpet", ModBlocks.ASHMOSS_CARPET);
    public static final RegistryObject<Item> RUNEGROWTH_ITEM = registerBlockItem("runegrowth", ModBlocks.RUNEGROWTH);
    public static final RegistryObject<Item> FROSTBOUND_RUNEGROWTH_ITEM = registerBlockItem("frostbound_runegrowth", ModBlocks.FROSTBOUND_RUNEGROWTH);
    public static final RegistryObject<Item> VERDANT_RUNEGROWTH_ITEM = registerBlockItem("verdant_runegrowth", ModBlocks.VERDANT_RUNEGROWTH);
    public static final RegistryObject<Item> EMBERWARMED_RUNEGROWTH_ITEM = registerBlockItem("emberwarmed_runegrowth", ModBlocks.EMBERWARMED_RUNEGROWTH);
    public static final RegistryObject<Item> VOIDFELT_ITEM = registerBlockItem("voidfelt", ModBlocks.VOIDFELT);

    public static final RegistryObject<Item> WAYFALL_IRON_ORE_ITEM = registerBlockItem("wayfall_iron_ore", ModBlocks.WAYFALL_IRON_ORE);
    public static final RegistryObject<Item> WAYFALL_COPPER_ORE_ITEM = registerBlockItem("wayfall_copper_ore", ModBlocks.WAYFALL_COPPER_ORE);
    public static final RegistryObject<Item> WAYFALL_DIAMOND_ORE_ITEM = registerBlockItem("wayfall_diamond_ore", ModBlocks.WAYFALL_DIAMOND_ORE);

    public static final RegistryObject<Item> RUNIC_DEBRIS_ITEM = ITEMS.register(
        "runic_debris",
        () -> new BlockItem(ModBlocks.RUNIC_DEBRIS.get(), new Item.Properties())
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

    public static final RegistryObject<Item> PYROKELP_ITEM = ITEMS.register(
        "pyrokelp",
        () -> new BlockItem(ModBlocks.PYROKELP.get(), new Item.Properties())
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

    public static final RegistryObject<Item> PALEWEFT_GRASS_ITEM = registerBlockItem("paleweft_grass", ModBlocks.PALEWEFT_GRASS);
    public static final RegistryObject<Item> PALEWEFT_TALL_GRASS_ITEM = registerBlockItem("paleweft_tall_grass", ModBlocks.PALEWEFT_TALL_GRASS);
    public static final RegistryObject<Item> GIANT_BLACK_ECHO_FUNGUS_CAP_ITEM = registerBlockItem("giant_black_echo_fungus_cap", ModBlocks.GIANT_BLACK_ECHO_FUNGUS_CAP);
    public static final RegistryObject<Item> GIANT_BLACK_ECHO_FUNGUS_CAP_SLAB_ITEM = registerBlockItem("giant_black_echo_fungus_cap_slab", ModBlocks.GIANT_BLACK_ECHO_FUNGUS_CAP_SLAB);
    public static final RegistryObject<Item> GIANT_BLACK_ECHO_FUNGUS_STEM_ITEM = registerBlockItem("giant_black_echo_fungus_stem", ModBlocks.GIANT_BLACK_ECHO_FUNGUS_STEM);
    public static final RegistryObject<Item> GIANT_ECHOCAP_CAP_ITEM = registerBlockItem("giant_echocap_cap", ModBlocks.GIANT_ECHOCAP_CAP);
    public static final RegistryObject<Item> GIANT_ECHOCAP_CAP_SLAB_ITEM = registerBlockItem("giant_echocap_cap_slab", ModBlocks.GIANT_ECHOCAP_CAP_SLAB);
    public static final RegistryObject<Item> GIANT_ECHOCAP_STEM_ITEM = registerBlockItem("giant_echocap_stem", ModBlocks.GIANT_ECHOCAP_STEM);
    public static final RegistryObject<Item> GIANT_ECHO_PUFF_CAP_ITEM = registerBlockItem("giant_echo_puff_cap", ModBlocks.GIANT_ECHO_PUFF_CAP);
    public static final RegistryObject<Item> GIANT_ECHO_PUFF_CAP_SLAB_ITEM = registerBlockItem("giant_echo_puff_cap_slab", ModBlocks.GIANT_ECHO_PUFF_CAP_SLAB);
    public static final RegistryObject<Item> GIANT_ECHO_PUFF_STEM_ITEM = registerBlockItem("giant_echo_puff_stem", ModBlocks.GIANT_ECHO_PUFF_STEM);
    public static final RegistryObject<Item> GIANT_GRIEFCAP_CAP_ITEM = registerBlockItem("giant_griefcap_cap", ModBlocks.GIANT_GRIEFCAP_CAP);
    public static final RegistryObject<Item> GIANT_GRIEFCAP_CAP_SLAB_ITEM = registerBlockItem("giant_griefcap_cap_slab", ModBlocks.GIANT_GRIEFCAP_CAP_SLAB);
    public static final RegistryObject<Item> GIANT_GRIEFCAP_STEM_ITEM = registerBlockItem("giant_griefcap_stem", ModBlocks.GIANT_GRIEFCAP_STEM);
    public static final RegistryObject<Item> GIANT_STATIC_FUNGUS_CAP_ITEM = registerBlockItem("giant_static_fungus_cap", ModBlocks.GIANT_STATIC_FUNGUS_CAP);
    public static final RegistryObject<Item> GIANT_STATIC_FUNGUS_CAP_SLAB_ITEM = registerBlockItem("giant_static_fungus_cap_slab", ModBlocks.GIANT_STATIC_FUNGUS_CAP_SLAB);
    public static final RegistryObject<Item> GIANT_STATIC_FUNGUS_STEM_ITEM = registerBlockItem("giant_static_fungus_stem", ModBlocks.GIANT_STATIC_FUNGUS_STEM);
    public static final RegistryObject<Item> GIANT_VOIDCAP_BRIAR_CAP_ITEM = registerBlockItem("giant_voidcap_briar_cap", ModBlocks.GIANT_VOIDCAP_BRIAR_CAP);
    public static final RegistryObject<Item> GIANT_VOIDCAP_BRIAR_CAP_SLAB_ITEM = registerBlockItem("giant_voidcap_briar_cap_slab", ModBlocks.GIANT_VOIDCAP_BRIAR_CAP_SLAB);
    public static final RegistryObject<Item> GIANT_VOIDCAP_BRIAR_STEM_ITEM = registerBlockItem("giant_voidcap_briar_stem", ModBlocks.GIANT_VOIDCAP_BRIAR_STEM);
    public static final RegistryObject<Item> GIANT_WAYBURN_FUNGUS_CAP_ITEM = registerBlockItem("giant_wayburn_fungus_cap", ModBlocks.GIANT_WAYBURN_FUNGUS_CAP);
    public static final RegistryObject<Item> GIANT_WAYBURN_FUNGUS_CAP_SLAB_ITEM = registerBlockItem("giant_wayburn_fungus_cap_slab", ModBlocks.GIANT_WAYBURN_FUNGUS_CAP_SLAB);
    public static final RegistryObject<Item> GIANT_WAYBURN_FUNGUS_STEM_ITEM = registerBlockItem("giant_wayburn_fungus_stem", ModBlocks.GIANT_WAYBURN_FUNGUS_STEM);
    public static final RegistryObject<Item> GIANT_WAYROT_FUNGUS_CAP_ITEM = registerBlockItem("giant_wayrot_fungus_cap", ModBlocks.GIANT_WAYROT_FUNGUS_CAP);
    public static final RegistryObject<Item> GIANT_WAYROT_FUNGUS_CAP_SLAB_ITEM = registerBlockItem("giant_wayrot_fungus_cap_slab", ModBlocks.GIANT_WAYROT_FUNGUS_CAP_SLAB);
    public static final RegistryObject<Item> GIANT_WAYROT_FUNGUS_STEM_ITEM = registerBlockItem("giant_wayrot_fungus_stem", ModBlocks.GIANT_WAYROT_FUNGUS_STEM);
    public static final RegistryObject<Item> GIANT_MEMORY_ROT_CAP_ITEM = registerBlockItem("giant_memory_rot_cap", ModBlocks.GIANT_MEMORY_ROT_CAP);
    public static final RegistryObject<Item> GIANT_MEMORY_ROT_CAP_SLAB_ITEM = registerBlockItem("giant_memory_rot_cap_slab", ModBlocks.GIANT_MEMORY_ROT_CAP_SLAB);
    public static final RegistryObject<Item> GIANT_MEMORY_ROT_STEM_ITEM = registerBlockItem("giant_memory_rot_stem", ModBlocks.GIANT_MEMORY_ROT_STEM);

    public static final RegistryObject<Item> RED_MUSHROOM_BLOCK_SLAB_ITEM = registerBlockItem("red_mushroom_block_slab", ModBlocks.RED_MUSHROOM_BLOCK_SLAB);
    public static final RegistryObject<Item> BROWN_MUSHROOM_BLOCK_SLAB_ITEM = registerBlockItem("brown_mushroom_block_slab", ModBlocks.BROWN_MUSHROOM_BLOCK_SLAB);
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

        // --- Signs + boats (wood set completeness) ---

        public static final RegistryObject<Item> WAYROOT_SIGN_ITEM = ITEMS.register(
            "wayroot_sign",
            () -> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.WAYROOT_SIGN.get(), ModBlocks.WAYROOT_WALL_SIGN.get())
        );
        public static final RegistryObject<Item> WAYROOT_HANGING_SIGN_ITEM = ITEMS.register(
            "wayroot_hanging_sign",
            () -> new HangingSignItem(ModBlocks.WAYROOT_HANGING_SIGN.get(), ModBlocks.WAYROOT_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16))
        );
        public static final RegistryObject<Item> WAYROOT_BOAT = ITEMS.register(
            "wayroot_boat",
            () -> new KruemblegardBoatItem(KruemblegardBoatType.WAYROOT, false, new Item.Properties())
        );
        public static final RegistryObject<Item> WAYROOT_CHEST_BOAT = ITEMS.register(
            "wayroot_chest_boat",
            () -> new KruemblegardBoatItem(KruemblegardBoatType.WAYROOT, true, new Item.Properties())
        );

        public static final RegistryObject<Item> FALLBARK_SIGN_ITEM = ITEMS.register(
            "fallbark_sign",
            () -> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.FALLBARK_SIGN.get(), ModBlocks.FALLBARK_WALL_SIGN.get())
        );
        public static final RegistryObject<Item> FALLBARK_HANGING_SIGN_ITEM = ITEMS.register(
            "fallbark_hanging_sign",
            () -> new HangingSignItem(ModBlocks.FALLBARK_HANGING_SIGN.get(), ModBlocks.FALLBARK_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16))
        );
        public static final RegistryObject<Item> FALLBARK_BOAT = ITEMS.register(
            "fallbark_boat",
            () -> new KruemblegardBoatItem(KruemblegardBoatType.FALLBARK, false, new Item.Properties())
        );
        public static final RegistryObject<Item> FALLBARK_CHEST_BOAT = ITEMS.register(
            "fallbark_chest_boat",
            () -> new KruemblegardBoatItem(KruemblegardBoatType.FALLBARK, true, new Item.Properties())
        );

        public static final RegistryObject<Item> ECHOWOOD_SIGN_ITEM = ITEMS.register(
            "echowood_sign",
            () -> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.ECHOWOOD_SIGN.get(), ModBlocks.ECHOWOOD_WALL_SIGN.get())
        );
        public static final RegistryObject<Item> ECHOWOOD_HANGING_SIGN_ITEM = ITEMS.register(
            "echowood_hanging_sign",
            () -> new HangingSignItem(ModBlocks.ECHOWOOD_HANGING_SIGN.get(), ModBlocks.ECHOWOOD_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16))
        );
        public static final RegistryObject<Item> ECHOWOOD_BOAT = ITEMS.register(
            "echowood_boat",
            () -> new KruemblegardBoatItem(KruemblegardBoatType.ECHOWOOD, false, new Item.Properties())
        );
        public static final RegistryObject<Item> ECHOWOOD_CHEST_BOAT = ITEMS.register(
            "echowood_chest_boat",
            () -> new KruemblegardBoatItem(KruemblegardBoatType.ECHOWOOD, true, new Item.Properties())
        );

        public static final RegistryObject<Item> CAIRN_TREE_SIGN_ITEM = ITEMS.register(
            "cairn_tree_sign",
            () -> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.CAIRN_TREE_SIGN.get(), ModBlocks.CAIRN_TREE_WALL_SIGN.get())
        );
        public static final RegistryObject<Item> CAIRN_TREE_HANGING_SIGN_ITEM = ITEMS.register(
            "cairn_tree_hanging_sign",
            () -> new HangingSignItem(ModBlocks.CAIRN_TREE_HANGING_SIGN.get(), ModBlocks.CAIRN_TREE_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16))
        );
        public static final RegistryObject<Item> CAIRN_TREE_BOAT = ITEMS.register(
            "cairn_tree_boat",
            () -> new KruemblegardBoatItem(KruemblegardBoatType.CAIRN_TREE, false, new Item.Properties())
        );
        public static final RegistryObject<Item> CAIRN_TREE_CHEST_BOAT = ITEMS.register(
            "cairn_tree_chest_boat",
            () -> new KruemblegardBoatItem(KruemblegardBoatType.CAIRN_TREE, true, new Item.Properties())
        );

        public static final RegistryObject<Item> WAYGLASS_SIGN_ITEM = ITEMS.register(
            "wayglass_sign",
            () -> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.WAYGLASS_SIGN.get(), ModBlocks.WAYGLASS_WALL_SIGN.get())
        );
        public static final RegistryObject<Item> WAYGLASS_HANGING_SIGN_ITEM = ITEMS.register(
            "wayglass_hanging_sign",
            () -> new HangingSignItem(ModBlocks.WAYGLASS_HANGING_SIGN.get(), ModBlocks.WAYGLASS_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16))
        );
        public static final RegistryObject<Item> WAYGLASS_BOAT = ITEMS.register(
            "wayglass_boat",
            () -> new KruemblegardBoatItem(KruemblegardBoatType.WAYGLASS, false, new Item.Properties())
        );
        public static final RegistryObject<Item> WAYGLASS_CHEST_BOAT = ITEMS.register(
            "wayglass_chest_boat",
            () -> new KruemblegardBoatItem(KruemblegardBoatType.WAYGLASS, true, new Item.Properties())
        );

        public static final RegistryObject<Item> SPLINTERSPORE_SIGN_ITEM = ITEMS.register(
            "splinterspore_sign",
            () -> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.SPLINTERSPORE_SIGN.get(), ModBlocks.SPLINTERSPORE_WALL_SIGN.get())
        );
        public static final RegistryObject<Item> SPLINTERSPORE_HANGING_SIGN_ITEM = ITEMS.register(
            "splinterspore_hanging_sign",
            () -> new HangingSignItem(ModBlocks.SPLINTERSPORE_HANGING_SIGN.get(), ModBlocks.SPLINTERSPORE_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16))
        );
        public static final RegistryObject<Item> SPLINTERSPORE_BOAT = ITEMS.register(
            "splinterspore_boat",
            () -> new KruemblegardBoatItem(KruemblegardBoatType.SPLINTERSPORE, false, new Item.Properties())
        );
        public static final RegistryObject<Item> SPLINTERSPORE_CHEST_BOAT = ITEMS.register(
            "splinterspore_chest_boat",
            () -> new KruemblegardBoatItem(KruemblegardBoatType.SPLINTERSPORE, true, new Item.Properties())
        );

        public static final RegistryObject<Item> HOLLOWWAY_TREE_SIGN_ITEM = ITEMS.register(
            "hollowway_tree_sign",
            () -> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.HOLLOWWAY_TREE_SIGN.get(), ModBlocks.HOLLOWWAY_TREE_WALL_SIGN.get())
        );
        public static final RegistryObject<Item> HOLLOWWAY_TREE_HANGING_SIGN_ITEM = ITEMS.register(
            "hollowway_tree_hanging_sign",
            () -> new HangingSignItem(ModBlocks.HOLLOWWAY_TREE_HANGING_SIGN.get(), ModBlocks.HOLLOWWAY_TREE_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16))
        );
        public static final RegistryObject<Item> HOLLOWWAY_TREE_BOAT = ITEMS.register(
            "hollowway_tree_boat",
            () -> new KruemblegardBoatItem(KruemblegardBoatType.HOLLOWWAY_TREE, false, new Item.Properties())
        );
        public static final RegistryObject<Item> HOLLOWWAY_TREE_CHEST_BOAT = ITEMS.register(
            "hollowway_tree_chest_boat",
            () -> new KruemblegardBoatItem(KruemblegardBoatType.HOLLOWWAY_TREE, true, new Item.Properties())
        );

        public static final RegistryObject<Item> DRIFTWILLOW_SIGN_ITEM = ITEMS.register(
            "driftwillow_sign",
            () -> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.DRIFTWILLOW_SIGN.get(), ModBlocks.DRIFTWILLOW_WALL_SIGN.get())
        );
        public static final RegistryObject<Item> DRIFTWILLOW_HANGING_SIGN_ITEM = ITEMS.register(
            "driftwillow_hanging_sign",
            () -> new HangingSignItem(ModBlocks.DRIFTWILLOW_HANGING_SIGN.get(), ModBlocks.DRIFTWILLOW_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16))
        );
        public static final RegistryObject<Item> DRIFTWILLOW_BOAT = ITEMS.register(
            "driftwillow_boat",
            () -> new KruemblegardBoatItem(KruemblegardBoatType.DRIFTWILLOW, false, new Item.Properties())
        );
        public static final RegistryObject<Item> DRIFTWILLOW_CHEST_BOAT = ITEMS.register(
            "driftwillow_chest_boat",
            () -> new KruemblegardBoatItem(KruemblegardBoatType.DRIFTWILLOW, true, new Item.Properties())
        );

        public static final RegistryObject<Item> MONUMENT_OAK_SIGN_ITEM = ITEMS.register(
            "monument_oak_sign",
            () -> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.MONUMENT_OAK_SIGN.get(), ModBlocks.MONUMENT_OAK_WALL_SIGN.get())
        );
        public static final RegistryObject<Item> MONUMENT_OAK_HANGING_SIGN_ITEM = ITEMS.register(
            "monument_oak_hanging_sign",
            () -> new HangingSignItem(ModBlocks.MONUMENT_OAK_HANGING_SIGN.get(), ModBlocks.MONUMENT_OAK_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16))
        );
        public static final RegistryObject<Item> MONUMENT_OAK_BOAT = ITEMS.register(
            "monument_oak_boat",
            () -> new KruemblegardBoatItem(KruemblegardBoatType.MONUMENT_OAK, false, new Item.Properties())
        );
        public static final RegistryObject<Item> MONUMENT_OAK_CHEST_BOAT = ITEMS.register(
            "monument_oak_chest_boat",
            () -> new KruemblegardBoatItem(KruemblegardBoatType.MONUMENT_OAK, true, new Item.Properties())
        );

        public static final RegistryObject<Item> WAYTORCH_TREE_SIGN_ITEM = ITEMS.register(
            "waytorch_tree_sign",
            () -> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.WAYTORCH_TREE_SIGN.get(), ModBlocks.WAYTORCH_TREE_WALL_SIGN.get())
        );
        public static final RegistryObject<Item> WAYTORCH_TREE_HANGING_SIGN_ITEM = ITEMS.register(
            "waytorch_tree_hanging_sign",
            () -> new HangingSignItem(ModBlocks.WAYTORCH_TREE_HANGING_SIGN.get(), ModBlocks.WAYTORCH_TREE_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16))
        );
        public static final RegistryObject<Item> WAYTORCH_TREE_BOAT = ITEMS.register(
            "waytorch_tree_boat",
            () -> new KruemblegardBoatItem(KruemblegardBoatType.WAYTORCH_TREE, false, new Item.Properties())
        );
        public static final RegistryObject<Item> WAYTORCH_TREE_CHEST_BOAT = ITEMS.register(
            "waytorch_tree_chest_boat",
            () -> new KruemblegardBoatItem(KruemblegardBoatType.WAYTORCH_TREE, true, new Item.Properties())
        );

        public static final RegistryObject<Item> FAULTWOOD_SIGN_ITEM = ITEMS.register(
            "faultwood_sign",
            () -> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.FAULTWOOD_SIGN.get(), ModBlocks.FAULTWOOD_WALL_SIGN.get())
        );
        public static final RegistryObject<Item> FAULTWOOD_HANGING_SIGN_ITEM = ITEMS.register(
            "faultwood_hanging_sign",
            () -> new HangingSignItem(ModBlocks.FAULTWOOD_HANGING_SIGN.get(), ModBlocks.FAULTWOOD_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16))
        );
        public static final RegistryObject<Item> FAULTWOOD_BOAT = ITEMS.register(
            "faultwood_boat",
            () -> new KruemblegardBoatItem(KruemblegardBoatType.FAULTWOOD, false, new Item.Properties())
        );
        public static final RegistryObject<Item> FAULTWOOD_CHEST_BOAT = ITEMS.register(
            "faultwood_chest_boat",
            () -> new KruemblegardBoatItem(KruemblegardBoatType.FAULTWOOD, true, new Item.Properties())
        );

        public static final RegistryObject<Item> ASHBLOOM_SIGN_ITEM = ITEMS.register(
            "ashbloom_sign",
            () -> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.ASHBLOOM_SIGN.get(), ModBlocks.ASHBLOOM_WALL_SIGN.get())
        );
        public static final RegistryObject<Item> ASHBLOOM_HANGING_SIGN_ITEM = ITEMS.register(
            "ashbloom_hanging_sign",
            () -> new HangingSignItem(ModBlocks.ASHBLOOM_HANGING_SIGN.get(), ModBlocks.ASHBLOOM_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16))
        );
        public static final RegistryObject<Item> ASHBLOOM_BOAT = ITEMS.register(
            "ashbloom_boat",
            () -> new KruemblegardBoatItem(KruemblegardBoatType.ASHBLOOM, false, new Item.Properties())
        );
        public static final RegistryObject<Item> ASHBLOOM_CHEST_BOAT = ITEMS.register(
            "ashbloom_chest_boat",
            () -> new KruemblegardBoatItem(KruemblegardBoatType.ASHBLOOM, true, new Item.Properties())
        );

        public static final RegistryObject<Item> GLIMMERPINE_SIGN_ITEM = ITEMS.register(
            "glimmerpine_sign",
            () -> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.GLIMMERPINE_SIGN.get(), ModBlocks.GLIMMERPINE_WALL_SIGN.get())
        );
        public static final RegistryObject<Item> GLIMMERPINE_HANGING_SIGN_ITEM = ITEMS.register(
            "glimmerpine_hanging_sign",
            () -> new HangingSignItem(ModBlocks.GLIMMERPINE_HANGING_SIGN.get(), ModBlocks.GLIMMERPINE_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16))
        );
        public static final RegistryObject<Item> GLIMMERPINE_BOAT = ITEMS.register(
            "glimmerpine_boat",
            () -> new KruemblegardBoatItem(KruemblegardBoatType.GLIMMERPINE, false, new Item.Properties())
        );
        public static final RegistryObject<Item> GLIMMERPINE_CHEST_BOAT = ITEMS.register(
            "glimmerpine_chest_boat",
            () -> new KruemblegardBoatItem(KruemblegardBoatType.GLIMMERPINE, true, new Item.Properties())
        );

        public static final RegistryObject<Item> DRIFTWOOD_SIGN = ITEMS.register(
            "driftwood_sign",
            () -> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.DRIFTWOOD_SIGN.get(), ModBlocks.DRIFTWOOD_WALL_SIGN.get())
        );
        public static final RegistryObject<Item> DRIFTWOOD_HANGING_SIGN = ITEMS.register(
            "driftwood_hanging_sign",
            () -> new HangingSignItem(ModBlocks.DRIFTWOOD_HANGING_SIGN.get(), ModBlocks.DRIFTWOOD_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16))
        );
        public static final RegistryObject<Item> DRIFTWOOD_BOAT = ITEMS.register(
            "driftwood_boat",
            () -> new KruemblegardBoatItem(KruemblegardBoatType.DRIFTWOOD, false, new Item.Properties())
        );
        public static final RegistryObject<Item> DRIFTWOOD_CHEST_BOAT = ITEMS.register(
            "driftwood_chest_boat",
            () -> new KruemblegardBoatItem(KruemblegardBoatType.DRIFTWOOD, true, new Item.Properties())
        );

    public static final RegistryObject<Item> REMNANT_SEEDS = ITEMS.register(
        "remnant_seeds",
        () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> PALEWEFT_SEEDS = ITEMS.register(
        "paleweft_seeds",
        () -> new ItemNameBlockItem(ModBlocks.PALEWEFT_CORN.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> WEFTKERN = ITEMS.register(
        "weftkern",
        () -> new Item(
            new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.3f).build())
        )
    );

    public static final RegistryObject<Item> ECHOKERN = ITEMS.register(
        "echokern",
        () -> new Item(
            new Item.Properties()
                .rarity(Rarity.UNCOMMON)
                .food(new FoodProperties.Builder().nutrition(1).saturationMod(0.3f).build())
        )
    );

    public static final RegistryObject<Item> WEFTMEAL = ITEMS.register(
        "weftmeal",
        () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationMod(0.6f).build()))
    );

    public static final RegistryObject<Item> RUNE_PETALS = ITEMS.register(
        "rune_petals",
        () -> new RunePetalItem(new Item.Properties())
    );

    public static final RegistryObject<Item> SOULBERRIES = ITEMS.register(
        "soulberries",
        () -> new ItemNameBlockItem(
            ModBlocks.SOULBERRY_SHRUB.get(),
            new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.3f).build())
        )
    );

    public static final RegistryObject<Item> GHOULBERRIES = ITEMS.register(
        "ghoulberries",
        () -> new ItemNameBlockItem(
            ModBlocks.GHOULBERRY_SHRUB.get(),
            new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.1f).build())
        )
    );

    public static final RegistryObject<Item> WISPSHOOT = ITEMS.register(
        "wispshoot",
        () -> new ItemNameBlockItem(
            ModBlocks.WISPSTALK.get(),
            new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.2f).build())
        )
    );



    public static final RegistryObject<Item> ATTUNED_RUNE_SHARD = ITEMS.register(
        "attuned_rune_shard",
        () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> RUNIC_SCRAP = ITEMS.register(
        "runic_scrap",
        () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> RUNIC_INGOT = ITEMS.register(
        "runic_ingot",
        () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> ATTUNED_INGOT = ITEMS.register(
        "attuned_ingot",
        () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> RUNIC_CORE =
        ITEMS.register("runic_core", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> FAULT_SHARD =
        ITEMS.register("fault_shard", () -> new Item(new Item.Properties()));

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

    public static final RegistryObject<Item> GREAT_HUNGER_SPAWN_EGG =
        ITEMS.register(
            "great_hunger_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.GREAT_HUNGER, 0x3a2a20, 0xc6a35d,
                new Item.Properties()));

    public static final RegistryObject<Item> SCATTERED_ENDERMAN_SPAWN_EGG =
        ITEMS.register(
            "scattered_enderman_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.SCATTERED_ENDERMAN, 0x161616, 0x9a30ff,
                new Item.Properties()));

    public static final RegistryObject<Item> MOOGLOOM_SPAWN_EGG =
        ITEMS.register(
            "moogloom_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.MOOGLOOM, 0x2b1f33, 0x7d5aa6,
                new Item.Properties()));

    public static final RegistryObject<Item> FAULT_CRAWLER_SPAWN_EGG =
        ITEMS.register(
            "fault_crawler_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.FAULT_CRAWLER, 0x4b4b4b, 0xb67a52,
                new Item.Properties()));

    public static final RegistryObject<Item> SCARALON_BEETLE_SPAWN_EGG =
        ITEMS.register(
            "scaralon_beetle_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.SCARALON_BEETLE, 0x4b1e10, 0xd1b02c,
                new Item.Properties()));

}
