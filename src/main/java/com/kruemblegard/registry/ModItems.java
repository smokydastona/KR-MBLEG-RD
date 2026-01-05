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

    @SubscribeEvent
    public static void addToCreativeTabs(BuildCreativeModeTabContentsEvent event) {

    if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
        event.accept(KRUEMBLEGARD_SPAWN_EGG);
        event.accept(TRAPROCK_SPAWN_EGG);
        event.accept(PEBBLIT_SPAWN_EGG);
    }

    if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
        event.accept(RUNIC_CORE);
    }

    if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
        event.accept(ANCIENT_WAYSTONE_ITEM);
        event.accept(ATTUNED_STONE_ITEM);
        event.accept(ATTUNED_ORE_ITEM);
        event.accept(STANDING_STONE_ITEM);

        event.accept(FRACTURED_WAYROCK_ITEM);
        event.accept(CRUSHSTONE_ITEM);
        event.accept(SCARSTONE_ITEM);
        event.accept(CRACKED_SCARSTONE_ITEM);
        event.accept(POLISHED_SCARSTONE_ITEM);
        event.accept(CHISELED_SCARSTONE_ITEM);
        event.accept(STONEVEIL_RUBBLE_ITEM);
        event.accept(POLISHED_STONEVEIL_RUBBLE_ITEM);
        event.accept(RUNED_STONEVEIL_RUBBLE_ITEM);

        event.accept(WAYROOT_LOG_ITEM);
        event.accept(WAYROOT_PLANKS_ITEM);
        event.accept(FALLBARK_LOG_ITEM);
        event.accept(FALLBARK_PLANKS_ITEM);
        event.accept(ECHOWOOD_LOG_ITEM);
        event.accept(ECHOWOOD_PLANKS_ITEM);
        event.accept(CAIRN_TREE_LOG_ITEM);
        event.accept(CAIRN_TREE_PLANKS_ITEM);
        event.accept(WAYGLASS_LOG_ITEM);
        event.accept(WAYGLASS_PLANKS_ITEM);
        event.accept(SHARDBARK_PINE_LOG_ITEM);
        event.accept(SHARDBARK_PINE_PLANKS_ITEM);
        event.accept(HOLLOWWAY_TREE_LOG_ITEM);
        event.accept(HOLLOWWAY_TREE_PLANKS_ITEM);
        event.accept(DRIFTWILLOW_LOG_ITEM);
        event.accept(DRIFTWILLOW_PLANKS_ITEM);
        event.accept(MONUMENT_OAK_LOG_ITEM);
        event.accept(MONUMENT_OAK_PLANKS_ITEM);
        event.accept(WAYTORCH_TREE_LOG_ITEM);
        event.accept(WAYTORCH_TREE_PLANKS_ITEM);
        event.accept(FAULTWOOD_LOG_ITEM);
        event.accept(FAULTWOOD_PLANKS_ITEM);

        event.accept(ASHBLOOM_LOG_ITEM);
        event.accept(ASHBLOOM_PLANKS_ITEM);
        event.accept(GLIMMERPINE_LOG_ITEM);
        event.accept(GLIMMERPINE_PLANKS_ITEM);
        event.accept(DRIFTWOOD_LOG_ITEM);
        event.accept(DRIFTWOOD_PLANKS_ITEM);

        event.accept(WAYROOT_STAIRS_ITEM);
        event.accept(WAYROOT_SLAB_ITEM);
        event.accept(WAYROOT_FENCE_ITEM);
        event.accept(WAYROOT_FENCE_GATE_ITEM);
        event.accept(WAYROOT_DOOR_ITEM);
        event.accept(WAYROOT_TRAPDOOR_ITEM);
        event.accept(WAYROOT_BUTTON_ITEM);
        event.accept(WAYROOT_PRESSURE_PLATE_ITEM);

        event.accept(FALLBARK_STAIRS_ITEM);
        event.accept(FALLBARK_SLAB_ITEM);
        event.accept(FALLBARK_FENCE_ITEM);
        event.accept(FALLBARK_FENCE_GATE_ITEM);
        event.accept(FALLBARK_DOOR_ITEM);
        event.accept(FALLBARK_TRAPDOOR_ITEM);
        event.accept(FALLBARK_BUTTON_ITEM);
        event.accept(FALLBARK_PRESSURE_PLATE_ITEM);

        event.accept(ECHOWOOD_STAIRS_ITEM);
        event.accept(ECHOWOOD_SLAB_ITEM);
        event.accept(ECHOWOOD_FENCE_ITEM);
        event.accept(ECHOWOOD_FENCE_GATE_ITEM);
        event.accept(ECHOWOOD_DOOR_ITEM);
        event.accept(ECHOWOOD_TRAPDOOR_ITEM);
        event.accept(ECHOWOOD_BUTTON_ITEM);
        event.accept(ECHOWOOD_PRESSURE_PLATE_ITEM);

        event.accept(CAIRN_TREE_STAIRS_ITEM);
        event.accept(CAIRN_TREE_SLAB_ITEM);
        event.accept(CAIRN_TREE_FENCE_ITEM);
        event.accept(CAIRN_TREE_FENCE_GATE_ITEM);
        event.accept(CAIRN_TREE_DOOR_ITEM);
        event.accept(CAIRN_TREE_TRAPDOOR_ITEM);
        event.accept(CAIRN_TREE_BUTTON_ITEM);
        event.accept(CAIRN_TREE_PRESSURE_PLATE_ITEM);

        event.accept(WAYGLASS_STAIRS_ITEM);
        event.accept(WAYGLASS_SLAB_ITEM);
        event.accept(WAYGLASS_FENCE_ITEM);
        event.accept(WAYGLASS_FENCE_GATE_ITEM);
        event.accept(WAYGLASS_DOOR_ITEM);
        event.accept(WAYGLASS_TRAPDOOR_ITEM);
        event.accept(WAYGLASS_BUTTON_ITEM);
        event.accept(WAYGLASS_PRESSURE_PLATE_ITEM);

        event.accept(SHARDBARK_PINE_STAIRS_ITEM);
        event.accept(SHARDBARK_PINE_SLAB_ITEM);
        event.accept(SHARDBARK_PINE_FENCE_ITEM);
        event.accept(SHARDBARK_PINE_FENCE_GATE_ITEM);
        event.accept(SHARDBARK_PINE_DOOR_ITEM);
        event.accept(SHARDBARK_PINE_TRAPDOOR_ITEM);
        event.accept(SHARDBARK_PINE_BUTTON_ITEM);
        event.accept(SHARDBARK_PINE_PRESSURE_PLATE_ITEM);

        event.accept(HOLLOWWAY_TREE_STAIRS_ITEM);
        event.accept(HOLLOWWAY_TREE_SLAB_ITEM);
        event.accept(HOLLOWWAY_TREE_FENCE_ITEM);
        event.accept(HOLLOWWAY_TREE_FENCE_GATE_ITEM);
        event.accept(HOLLOWWAY_TREE_DOOR_ITEM);
        event.accept(HOLLOWWAY_TREE_TRAPDOOR_ITEM);
        event.accept(HOLLOWWAY_TREE_BUTTON_ITEM);
        event.accept(HOLLOWWAY_TREE_PRESSURE_PLATE_ITEM);

        event.accept(DRIFTWILLOW_STAIRS_ITEM);
        event.accept(DRIFTWILLOW_SLAB_ITEM);
        event.accept(DRIFTWILLOW_FENCE_ITEM);
        event.accept(DRIFTWILLOW_FENCE_GATE_ITEM);
        event.accept(DRIFTWILLOW_DOOR_ITEM);
        event.accept(DRIFTWILLOW_TRAPDOOR_ITEM);
        event.accept(DRIFTWILLOW_BUTTON_ITEM);
        event.accept(DRIFTWILLOW_PRESSURE_PLATE_ITEM);

        event.accept(MONUMENT_OAK_STAIRS_ITEM);
        event.accept(MONUMENT_OAK_SLAB_ITEM);
        event.accept(MONUMENT_OAK_FENCE_ITEM);
        event.accept(MONUMENT_OAK_FENCE_GATE_ITEM);
        event.accept(MONUMENT_OAK_DOOR_ITEM);
        event.accept(MONUMENT_OAK_TRAPDOOR_ITEM);
        event.accept(MONUMENT_OAK_BUTTON_ITEM);
        event.accept(MONUMENT_OAK_PRESSURE_PLATE_ITEM);

        event.accept(WAYTORCH_TREE_STAIRS_ITEM);
        event.accept(WAYTORCH_TREE_SLAB_ITEM);
        event.accept(WAYTORCH_TREE_FENCE_ITEM);
        event.accept(WAYTORCH_TREE_FENCE_GATE_ITEM);
        event.accept(WAYTORCH_TREE_DOOR_ITEM);
        event.accept(WAYTORCH_TREE_TRAPDOOR_ITEM);
        event.accept(WAYTORCH_TREE_BUTTON_ITEM);
        event.accept(WAYTORCH_TREE_PRESSURE_PLATE_ITEM);

        event.accept(FAULTWOOD_STAIRS_ITEM);
        event.accept(FAULTWOOD_SLAB_ITEM);
        event.accept(FAULTWOOD_FENCE_ITEM);
        event.accept(FAULTWOOD_FENCE_GATE_ITEM);
        event.accept(FAULTWOOD_DOOR_ITEM);
        event.accept(FAULTWOOD_TRAPDOOR_ITEM);
        event.accept(FAULTWOOD_BUTTON_ITEM);
        event.accept(FAULTWOOD_PRESSURE_PLATE_ITEM);

        event.accept(ASHBLOOM_STAIRS_ITEM);
        event.accept(ASHBLOOM_SLAB_ITEM);
        event.accept(ASHBLOOM_FENCE_ITEM);
        event.accept(ASHBLOOM_FENCE_GATE_ITEM);
        event.accept(ASHBLOOM_DOOR_ITEM);
        event.accept(ASHBLOOM_TRAPDOOR_ITEM);
        event.accept(ASHBLOOM_BUTTON_ITEM);
        event.accept(ASHBLOOM_PRESSURE_PLATE_ITEM);

        event.accept(GLIMMERPINE_STAIRS_ITEM);
        event.accept(GLIMMERPINE_SLAB_ITEM);
        event.accept(GLIMMERPINE_FENCE_ITEM);
        event.accept(GLIMMERPINE_FENCE_GATE_ITEM);
        event.accept(GLIMMERPINE_DOOR_ITEM);
        event.accept(GLIMMERPINE_TRAPDOOR_ITEM);
        event.accept(GLIMMERPINE_BUTTON_ITEM);
        event.accept(GLIMMERPINE_PRESSURE_PLATE_ITEM);

        event.accept(DRIFTWOOD_STAIRS_ITEM);
        event.accept(DRIFTWOOD_SLAB_ITEM);
        event.accept(DRIFTWOOD_FENCE_ITEM);
        event.accept(DRIFTWOOD_FENCE_GATE_ITEM);
        event.accept(DRIFTWOOD_DOOR_ITEM);
        event.accept(DRIFTWOOD_TRAPDOOR_ITEM);
        event.accept(DRIFTWOOD_BUTTON_ITEM);
        event.accept(DRIFTWOOD_PRESSURE_PLATE_ITEM);
    }

    if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
        event.accept(ASHFALL_LOAM_ITEM);
        event.accept(FAULT_DUST_ITEM);
        event.accept(VEILGROWTH_ITEM);
        event.accept(ASHMOSS_ITEM);
        event.accept(RUNEGROWTH_ITEM);
        event.accept(VOIDFELT_ITEM);

        event.accept(WISPSTALK_ITEM);
        event.accept(GRAVEVINE_ITEM);
        event.accept(ECHOCAP_ITEM);
        event.accept(RUNEBLOOM_ITEM);
        event.accept(SOULBERRY_SHRUB_ITEM);
        event.accept(GHOULBERRY_SHRUB_ITEM);

        event.accept(PATHREED_ITEM);
        event.accept(FAULTGRASS_ITEM);
        event.accept(DRIFTBLOOM_ITEM);
        event.accept(CAIRN_MOSS_ITEM);
        event.accept(WAYLILY_ITEM);
        event.accept(GRIEFCAP_ITEM);
        event.accept(STATIC_FUNGUS_ITEM);
        event.accept(WAYROT_FUNGUS_ITEM);
        event.accept(ECHO_PUFF_ITEM);
        event.accept(RUIN_THISTLE_ITEM);
        event.accept(WAYSEED_CLUSTER_ITEM);
        event.accept(VOID_LICHEN_ITEM);
        event.accept(TRANSIT_FERN_ITEM);
        event.accept(MISSTEP_VINE_ITEM);
        event.accept(WAYBIND_CREEPER_ITEM);
        event.accept(MILESTONE_GRASS_ITEM);
        event.accept(RUNEDRIFT_REED_ITEM);
        event.accept(WAYSCAR_IVY_ITEM);
        event.accept(ASHPETAL_ITEM);
        event.accept(TRANSIT_BLOOM_ITEM);
        event.accept(CAIRNROOT_ITEM);
        event.accept(WAYPOINT_MOLD_ITEM);
        event.accept(BLACK_ECHO_FUNGUS_ITEM);
        event.accept(WAYBURN_FUNGUS_ITEM);
        event.accept(MEMORY_ROT_ITEM);
        event.accept(FALSEPATH_THORNS_ITEM);
        event.accept(SLIPROOT_ITEM);
        event.accept(WAYGRASP_VINE_ITEM);
        event.accept(VOIDCAP_BRIAR_ITEM);
        event.accept(DUSTPETAL_ITEM);
        event.accept(RUNE_SPROUTS_ITEM);
        event.accept(WAYTHREAD_ITEM);
        event.accept(GRAVEMINT_ITEM);
        event.accept(FALLSEED_PODS_ITEM);
        event.accept(REVERSE_PORTAL_SPORES_ITEM);

        event.accept(VOIDFERN_ITEM);
        event.accept(RUNEBLOSSOM_ITEM);
        event.accept(MOTESHRUB_ITEM);
        event.accept(ASHVEIL_ITEM);
        event.accept(TWILIGHT_BULB_ITEM);
        event.accept(WHISPERVINE_ITEM);

        event.accept(WAYROOT_LEAVES_ITEM);
        event.accept(WAYROOT_SAPLING_ITEM);
        event.accept(FALLBARK_LEAVES_ITEM);
        event.accept(FALLBARK_SAPLING_ITEM);
        event.accept(ECHOWOOD_LEAVES_ITEM);
        event.accept(ECHOWOOD_SAPLING_ITEM);
        event.accept(CAIRN_TREE_LEAVES_ITEM);
        event.accept(CAIRN_TREE_SAPLING_ITEM);
        event.accept(WAYGLASS_LEAVES_ITEM);
        event.accept(WAYGLASS_SAPLING_ITEM);
        event.accept(SHARDBARK_PINE_LEAVES_ITEM);
        event.accept(SHARDBARK_PINE_SAPLING_ITEM);
        event.accept(HOLLOWWAY_TREE_LEAVES_ITEM);
        event.accept(HOLLOWWAY_TREE_SAPLING_ITEM);
        event.accept(DRIFTWILLOW_LEAVES_ITEM);
        event.accept(DRIFTWILLOW_SAPLING_ITEM);
        event.accept(MONUMENT_OAK_LEAVES_ITEM);
        event.accept(MONUMENT_OAK_SAPLING_ITEM);
        event.accept(WAYTORCH_TREE_LEAVES_ITEM);
        event.accept(WAYTORCH_TREE_SAPLING_ITEM);
        event.accept(FAULTWOOD_LEAVES_ITEM);
        event.accept(FAULTWOOD_SAPLING_ITEM);

        event.accept(ASHBLOOM_LEAVES_ITEM);
        event.accept(ASHBLOOM_SAPLING_ITEM);
        event.accept(GLIMMERPINE_LEAVES_ITEM);
        event.accept(GLIMMERPINE_SAPLING_ITEM);
        event.accept(DRIFTWOOD_LEAVES_ITEM);
        event.accept(DRIFTWOOD_SAPLING_ITEM);
    }
    }
}
