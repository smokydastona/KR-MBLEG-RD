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
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.Block;

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

    public static final RegistryObject<Item> ANCIENT_WAYSTONE_ITEM = ITEMS.register(
        "ancient_waystone",
        () -> new BlockItem(ModBlocks.ANCIENT_WAYSTONE.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> ATTUNED_STONE_ITEM = ITEMS.register(
        "attuned_stone",
        () -> new BlockItem(ModBlocks.ATTUNED_STONE.get(), new Item.Properties())
    );

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

    private static RegistryObject<Item> registerBlockItem(String id, RegistryObject<? extends Block> block) {
        return ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    // --- Wayfall flora block items ---

    public static final RegistryObject<Item> PATHREED_ITEM = registerBlockItem("pathreed", ModBlocks.PATHREED);
    public static final RegistryObject<Item> FAULTGRASS_ITEM = registerBlockItem("faultgrass", ModBlocks.FAULTGRASS);
    public static final RegistryObject<Item> DRIFTBLOOM_ITEM = registerBlockItem("driftbloom", ModBlocks.DRIFTBLOOM);
    public static final RegistryObject<Item> CAIRN_MOSS_ITEM = registerBlockItem("cairn_moss", ModBlocks.CAIRN_MOSS);
    public static final RegistryObject<Item> WAYLILY_ITEM = registerBlockItem("waylily", ModBlocks.WAYLILY);
    public static final RegistryObject<Item> GRIEFCAP_ITEM = registerBlockItem("griefcap", ModBlocks.GRIEFCAP);
    public static final RegistryObject<Item> STATIC_FUNGUS_ITEM = registerBlockItem("static_fungus", ModBlocks.STATIC_FUNGUS);
    public static final RegistryObject<Item> WAYROT_FUNGUS_ITEM = registerBlockItem("wayrot_fungus", ModBlocks.WAYROT_FUNGUS);
    public static final RegistryObject<Item> ECHO_PUFF_ITEM = registerBlockItem("echo_puff", ModBlocks.ECHO_PUFF);
    public static final RegistryObject<Item> RUIN_THISTLE_ITEM = registerBlockItem("ruin_thistle", ModBlocks.RUIN_THISTLE);
    public static final RegistryObject<Item> WAYSEED_CLUSTER_ITEM = registerBlockItem("wayseed_cluster", ModBlocks.WAYSEED_CLUSTER);
    public static final RegistryObject<Item> VOID_LICHEN_ITEM = registerBlockItem("void_lichen", ModBlocks.VOID_LICHEN);
    public static final RegistryObject<Item> TRANSIT_FERN_ITEM = registerBlockItem("transit_fern", ModBlocks.TRANSIT_FERN);
    public static final RegistryObject<Item> MISSTEP_VINE_ITEM = registerBlockItem("misstep_vine", ModBlocks.MISSTEP_VINE);
    public static final RegistryObject<Item> WAYBIND_CREEPER_ITEM = registerBlockItem("waybind_creeper", ModBlocks.WAYBIND_CREEPER);
    public static final RegistryObject<Item> MILESTONE_GRASS_ITEM = registerBlockItem("milestone_grass", ModBlocks.MILESTONE_GRASS);
    public static final RegistryObject<Item> RUNEDRIFT_REED_ITEM = registerBlockItem("runedrift_reed", ModBlocks.RUNEDRIFT_REED);
    public static final RegistryObject<Item> WAYSCAR_IVY_ITEM = registerBlockItem("wayscar_ivy", ModBlocks.WAYSCAR_IVY);
    public static final RegistryObject<Item> ASHPETAL_ITEM = registerBlockItem("ashpetal", ModBlocks.ASHPETAL);
    public static final RegistryObject<Item> TRANSIT_BLOOM_ITEM = registerBlockItem("transit_bloom", ModBlocks.TRANSIT_BLOOM);
    public static final RegistryObject<Item> CAIRNROOT_ITEM = registerBlockItem("cairnroot", ModBlocks.CAIRNROOT);
    public static final RegistryObject<Item> WAYPOINT_MOLD_ITEM = registerBlockItem("waypoint_mold", ModBlocks.WAYPOINT_MOLD);
    public static final RegistryObject<Item> BLACK_ECHO_FUNGUS_ITEM = registerBlockItem("black_echo_fungus", ModBlocks.BLACK_ECHO_FUNGUS);
    public static final RegistryObject<Item> WAYBURN_FUNGUS_ITEM = registerBlockItem("wayburn_fungus", ModBlocks.WAYBURN_FUNGUS);
    public static final RegistryObject<Item> MEMORY_ROT_ITEM = registerBlockItem("memory_rot", ModBlocks.MEMORY_ROT);
    public static final RegistryObject<Item> FALSEPATH_THORNS_ITEM = registerBlockItem("falsepath_thorns", ModBlocks.FALSEPATH_THORNS);
    public static final RegistryObject<Item> SLIPROOT_ITEM = registerBlockItem("sliproot", ModBlocks.SLIPROOT);
    public static final RegistryObject<Item> WAYGRASP_VINE_ITEM = registerBlockItem("waygrasp_vine", ModBlocks.WAYGRASP_VINE);
    public static final RegistryObject<Item> VOIDCAP_BRIAR_ITEM = registerBlockItem("voidcap_briar", ModBlocks.VOIDCAP_BRIAR);
    public static final RegistryObject<Item> DUSTPETAL_ITEM = registerBlockItem("dustpetal", ModBlocks.DUSTPETAL);
    public static final RegistryObject<Item> RUNE_SPROUTS_ITEM = registerBlockItem("rune_sprouts", ModBlocks.RUNE_SPROUTS);
    public static final RegistryObject<Item> WAYTHREAD_ITEM = registerBlockItem("waythread", ModBlocks.WAYTHREAD);
    public static final RegistryObject<Item> GRAVEMINT_ITEM = registerBlockItem("gravemint", ModBlocks.GRAVEMINT);
    public static final RegistryObject<Item> FALLSEED_PODS_ITEM = registerBlockItem("fallseed_pods", ModBlocks.FALLSEED_PODS);
    public static final RegistryObject<Item> REVERSE_PORTAL_SPORES_ITEM = registerBlockItem("reverse_portal_spores", ModBlocks.REVERSE_PORTAL_SPORES);

    // --- Wayfall staple flora block items ---

    public static final RegistryObject<Item> VOIDFERN_ITEM = registerBlockItem("voidfern", ModBlocks.VOIDFERN);
    public static final RegistryObject<Item> RUNEBLOSSOM_ITEM = registerBlockItem("runeblossom", ModBlocks.RUNEBLOSSOM);
    public static final RegistryObject<Item> MOTESHRUB_ITEM = registerBlockItem("moteshrub", ModBlocks.MOTESHRUB);
    public static final RegistryObject<Item> ASHVEIL_ITEM = registerBlockItem("ashveil", ModBlocks.ASHVEIL);
    public static final RegistryObject<Item> TWILIGHT_BULB_ITEM = registerBlockItem("twilight_bulb", ModBlocks.TWILIGHT_BULB);
    public static final RegistryObject<Item> WHISPERVINE_ITEM = registerBlockItem("whispervine", ModBlocks.WHISPERVINE);

    // --- Wayfall tree block items ---

    public static final RegistryObject<Item> WAYROOT_LOG_ITEM = registerBlockItem("wayroot_log", ModBlocks.WAYROOT_LOG);
    public static final RegistryObject<Item> WAYROOT_PLANKS_ITEM = registerBlockItem("wayroot_planks", ModBlocks.WAYROOT_PLANKS);
    public static final RegistryObject<Item> WAYROOT_LEAVES_ITEM = registerBlockItem("wayroot_leaves", ModBlocks.WAYROOT_LEAVES);
    public static final RegistryObject<Item> WAYROOT_SAPLING_ITEM = registerBlockItem("wayroot_sapling", ModBlocks.WAYROOT_SAPLING);

    public static final RegistryObject<Item> WAYROOT_STAIRS_ITEM = registerBlockItem("wayroot_stairs", ModBlocks.WAYROOT_STAIRS);
    public static final RegistryObject<Item> WAYROOT_SLAB_ITEM = registerBlockItem("wayroot_slab", ModBlocks.WAYROOT_SLAB);
    public static final RegistryObject<Item> WAYROOT_FENCE_ITEM = registerBlockItem("wayroot_fence", ModBlocks.WAYROOT_FENCE);
    public static final RegistryObject<Item> WAYROOT_FENCE_GATE_ITEM = registerBlockItem("wayroot_fence_gate", ModBlocks.WAYROOT_FENCE_GATE);
    public static final RegistryObject<Item> WAYROOT_DOOR_ITEM = registerBlockItem("wayroot_door", ModBlocks.WAYROOT_DOOR);
    public static final RegistryObject<Item> WAYROOT_TRAPDOOR_ITEM = registerBlockItem("wayroot_trapdoor", ModBlocks.WAYROOT_TRAPDOOR);
    public static final RegistryObject<Item> WAYROOT_BUTTON_ITEM = registerBlockItem("wayroot_button", ModBlocks.WAYROOT_BUTTON);
    public static final RegistryObject<Item> WAYROOT_PRESSURE_PLATE_ITEM = registerBlockItem("wayroot_pressure_plate", ModBlocks.WAYROOT_PRESSURE_PLATE);

    public static final RegistryObject<Item> FALLBARK_LOG_ITEM = registerBlockItem("fallbark_log", ModBlocks.FALLBARK_LOG);
    public static final RegistryObject<Item> FALLBARK_PLANKS_ITEM = registerBlockItem("fallbark_planks", ModBlocks.FALLBARK_PLANKS);
    public static final RegistryObject<Item> FALLBARK_LEAVES_ITEM = registerBlockItem("fallbark_leaves", ModBlocks.FALLBARK_LEAVES);
    public static final RegistryObject<Item> FALLBARK_SAPLING_ITEM = registerBlockItem("fallbark_sapling", ModBlocks.FALLBARK_SAPLING);

    public static final RegistryObject<Item> FALLBARK_STAIRS_ITEM = registerBlockItem("fallbark_stairs", ModBlocks.FALLBARK_STAIRS);
    public static final RegistryObject<Item> FALLBARK_SLAB_ITEM = registerBlockItem("fallbark_slab", ModBlocks.FALLBARK_SLAB);
    public static final RegistryObject<Item> FALLBARK_FENCE_ITEM = registerBlockItem("fallbark_fence", ModBlocks.FALLBARK_FENCE);
    public static final RegistryObject<Item> FALLBARK_FENCE_GATE_ITEM = registerBlockItem("fallbark_fence_gate", ModBlocks.FALLBARK_FENCE_GATE);
    public static final RegistryObject<Item> FALLBARK_DOOR_ITEM = registerBlockItem("fallbark_door", ModBlocks.FALLBARK_DOOR);
    public static final RegistryObject<Item> FALLBARK_TRAPDOOR_ITEM = registerBlockItem("fallbark_trapdoor", ModBlocks.FALLBARK_TRAPDOOR);
    public static final RegistryObject<Item> FALLBARK_BUTTON_ITEM = registerBlockItem("fallbark_button", ModBlocks.FALLBARK_BUTTON);
    public static final RegistryObject<Item> FALLBARK_PRESSURE_PLATE_ITEM = registerBlockItem("fallbark_pressure_plate", ModBlocks.FALLBARK_PRESSURE_PLATE);

    public static final RegistryObject<Item> ECHOWOOD_LOG_ITEM = registerBlockItem("echowood_log", ModBlocks.ECHOWOOD_LOG);
    public static final RegistryObject<Item> ECHOWOOD_PLANKS_ITEM = registerBlockItem("echowood_planks", ModBlocks.ECHOWOOD_PLANKS);
    public static final RegistryObject<Item> ECHOWOOD_LEAVES_ITEM = registerBlockItem("echowood_leaves", ModBlocks.ECHOWOOD_LEAVES);
    public static final RegistryObject<Item> ECHOWOOD_SAPLING_ITEM = registerBlockItem("echowood_sapling", ModBlocks.ECHOWOOD_SAPLING);

    public static final RegistryObject<Item> ECHOWOOD_STAIRS_ITEM = registerBlockItem("echowood_stairs", ModBlocks.ECHOWOOD_STAIRS);
    public static final RegistryObject<Item> ECHOWOOD_SLAB_ITEM = registerBlockItem("echowood_slab", ModBlocks.ECHOWOOD_SLAB);
    public static final RegistryObject<Item> ECHOWOOD_FENCE_ITEM = registerBlockItem("echowood_fence", ModBlocks.ECHOWOOD_FENCE);
    public static final RegistryObject<Item> ECHOWOOD_FENCE_GATE_ITEM = registerBlockItem("echowood_fence_gate", ModBlocks.ECHOWOOD_FENCE_GATE);
    public static final RegistryObject<Item> ECHOWOOD_DOOR_ITEM = registerBlockItem("echowood_door", ModBlocks.ECHOWOOD_DOOR);
    public static final RegistryObject<Item> ECHOWOOD_TRAPDOOR_ITEM = registerBlockItem("echowood_trapdoor", ModBlocks.ECHOWOOD_TRAPDOOR);
    public static final RegistryObject<Item> ECHOWOOD_BUTTON_ITEM = registerBlockItem("echowood_button", ModBlocks.ECHOWOOD_BUTTON);
    public static final RegistryObject<Item> ECHOWOOD_PRESSURE_PLATE_ITEM = registerBlockItem("echowood_pressure_plate", ModBlocks.ECHOWOOD_PRESSURE_PLATE);

    public static final RegistryObject<Item> CAIRN_TREE_LOG_ITEM = registerBlockItem("cairn_tree_log", ModBlocks.CAIRN_TREE_LOG);
    public static final RegistryObject<Item> CAIRN_TREE_PLANKS_ITEM = registerBlockItem("cairn_tree_planks", ModBlocks.CAIRN_TREE_PLANKS);
    public static final RegistryObject<Item> CAIRN_TREE_LEAVES_ITEM = registerBlockItem("cairn_tree_leaves", ModBlocks.CAIRN_TREE_LEAVES);
    public static final RegistryObject<Item> CAIRN_TREE_SAPLING_ITEM = registerBlockItem("cairn_tree_sapling", ModBlocks.CAIRN_TREE_SAPLING);

    public static final RegistryObject<Item> CAIRN_TREE_STAIRS_ITEM = registerBlockItem("cairn_tree_stairs", ModBlocks.CAIRN_TREE_STAIRS);
    public static final RegistryObject<Item> CAIRN_TREE_SLAB_ITEM = registerBlockItem("cairn_tree_slab", ModBlocks.CAIRN_TREE_SLAB);
    public static final RegistryObject<Item> CAIRN_TREE_FENCE_ITEM = registerBlockItem("cairn_tree_fence", ModBlocks.CAIRN_TREE_FENCE);
    public static final RegistryObject<Item> CAIRN_TREE_FENCE_GATE_ITEM = registerBlockItem("cairn_tree_fence_gate", ModBlocks.CAIRN_TREE_FENCE_GATE);
    public static final RegistryObject<Item> CAIRN_TREE_DOOR_ITEM = registerBlockItem("cairn_tree_door", ModBlocks.CAIRN_TREE_DOOR);
    public static final RegistryObject<Item> CAIRN_TREE_TRAPDOOR_ITEM = registerBlockItem("cairn_tree_trapdoor", ModBlocks.CAIRN_TREE_TRAPDOOR);
    public static final RegistryObject<Item> CAIRN_TREE_BUTTON_ITEM = registerBlockItem("cairn_tree_button", ModBlocks.CAIRN_TREE_BUTTON);
    public static final RegistryObject<Item> CAIRN_TREE_PRESSURE_PLATE_ITEM = registerBlockItem("cairn_tree_pressure_plate", ModBlocks.CAIRN_TREE_PRESSURE_PLATE);

    public static final RegistryObject<Item> WAYGLASS_LOG_ITEM = registerBlockItem("wayglass_log", ModBlocks.WAYGLASS_LOG);
    public static final RegistryObject<Item> WAYGLASS_PLANKS_ITEM = registerBlockItem("wayglass_planks", ModBlocks.WAYGLASS_PLANKS);
    public static final RegistryObject<Item> WAYGLASS_LEAVES_ITEM = registerBlockItem("wayglass_leaves", ModBlocks.WAYGLASS_LEAVES);
    public static final RegistryObject<Item> WAYGLASS_SAPLING_ITEM = registerBlockItem("wayglass_sapling", ModBlocks.WAYGLASS_SAPLING);

    public static final RegistryObject<Item> WAYGLASS_STAIRS_ITEM = registerBlockItem("wayglass_stairs", ModBlocks.WAYGLASS_STAIRS);
    public static final RegistryObject<Item> WAYGLASS_SLAB_ITEM = registerBlockItem("wayglass_slab", ModBlocks.WAYGLASS_SLAB);
    public static final RegistryObject<Item> WAYGLASS_FENCE_ITEM = registerBlockItem("wayglass_fence", ModBlocks.WAYGLASS_FENCE);
    public static final RegistryObject<Item> WAYGLASS_FENCE_GATE_ITEM = registerBlockItem("wayglass_fence_gate", ModBlocks.WAYGLASS_FENCE_GATE);
    public static final RegistryObject<Item> WAYGLASS_DOOR_ITEM = registerBlockItem("wayglass_door", ModBlocks.WAYGLASS_DOOR);
    public static final RegistryObject<Item> WAYGLASS_TRAPDOOR_ITEM = registerBlockItem("wayglass_trapdoor", ModBlocks.WAYGLASS_TRAPDOOR);
    public static final RegistryObject<Item> WAYGLASS_BUTTON_ITEM = registerBlockItem("wayglass_button", ModBlocks.WAYGLASS_BUTTON);
    public static final RegistryObject<Item> WAYGLASS_PRESSURE_PLATE_ITEM = registerBlockItem("wayglass_pressure_plate", ModBlocks.WAYGLASS_PRESSURE_PLATE);

    public static final RegistryObject<Item> SHARDBARK_PINE_LOG_ITEM = registerBlockItem("shardbark_pine_log", ModBlocks.SHARDBARK_PINE_LOG);
    public static final RegistryObject<Item> SHARDBARK_PINE_PLANKS_ITEM = registerBlockItem("shardbark_pine_planks", ModBlocks.SHARDBARK_PINE_PLANKS);
    public static final RegistryObject<Item> SHARDBARK_PINE_LEAVES_ITEM = registerBlockItem("shardbark_pine_leaves", ModBlocks.SHARDBARK_PINE_LEAVES);
    public static final RegistryObject<Item> SHARDBARK_PINE_SAPLING_ITEM = registerBlockItem("shardbark_pine_sapling", ModBlocks.SHARDBARK_PINE_SAPLING);

    public static final RegistryObject<Item> SHARDBARK_PINE_STAIRS_ITEM = registerBlockItem("shardbark_pine_stairs", ModBlocks.SHARDBARK_PINE_STAIRS);
    public static final RegistryObject<Item> SHARDBARK_PINE_SLAB_ITEM = registerBlockItem("shardbark_pine_slab", ModBlocks.SHARDBARK_PINE_SLAB);
    public static final RegistryObject<Item> SHARDBARK_PINE_FENCE_ITEM = registerBlockItem("shardbark_pine_fence", ModBlocks.SHARDBARK_PINE_FENCE);
    public static final RegistryObject<Item> SHARDBARK_PINE_FENCE_GATE_ITEM = registerBlockItem("shardbark_pine_fence_gate", ModBlocks.SHARDBARK_PINE_FENCE_GATE);
    public static final RegistryObject<Item> SHARDBARK_PINE_DOOR_ITEM = registerBlockItem("shardbark_pine_door", ModBlocks.SHARDBARK_PINE_DOOR);
    public static final RegistryObject<Item> SHARDBARK_PINE_TRAPDOOR_ITEM = registerBlockItem("shardbark_pine_trapdoor", ModBlocks.SHARDBARK_PINE_TRAPDOOR);
    public static final RegistryObject<Item> SHARDBARK_PINE_BUTTON_ITEM = registerBlockItem("shardbark_pine_button", ModBlocks.SHARDBARK_PINE_BUTTON);
    public static final RegistryObject<Item> SHARDBARK_PINE_PRESSURE_PLATE_ITEM = registerBlockItem("shardbark_pine_pressure_plate", ModBlocks.SHARDBARK_PINE_PRESSURE_PLATE);

    public static final RegistryObject<Item> HOLLOWWAY_TREE_LOG_ITEM = registerBlockItem("hollowway_tree_log", ModBlocks.HOLLOWWAY_TREE_LOG);
    public static final RegistryObject<Item> HOLLOWWAY_TREE_PLANKS_ITEM = registerBlockItem("hollowway_tree_planks", ModBlocks.HOLLOWWAY_TREE_PLANKS);
    public static final RegistryObject<Item> HOLLOWWAY_TREE_LEAVES_ITEM = registerBlockItem("hollowway_tree_leaves", ModBlocks.HOLLOWWAY_TREE_LEAVES);
    public static final RegistryObject<Item> HOLLOWWAY_TREE_SAPLING_ITEM = registerBlockItem("hollowway_tree_sapling", ModBlocks.HOLLOWWAY_TREE_SAPLING);

    public static final RegistryObject<Item> HOLLOWWAY_TREE_STAIRS_ITEM = registerBlockItem("hollowway_tree_stairs", ModBlocks.HOLLOWWAY_TREE_STAIRS);
    public static final RegistryObject<Item> HOLLOWWAY_TREE_SLAB_ITEM = registerBlockItem("hollowway_tree_slab", ModBlocks.HOLLOWWAY_TREE_SLAB);
    public static final RegistryObject<Item> HOLLOWWAY_TREE_FENCE_ITEM = registerBlockItem("hollowway_tree_fence", ModBlocks.HOLLOWWAY_TREE_FENCE);
    public static final RegistryObject<Item> HOLLOWWAY_TREE_FENCE_GATE_ITEM = registerBlockItem("hollowway_tree_fence_gate", ModBlocks.HOLLOWWAY_TREE_FENCE_GATE);
    public static final RegistryObject<Item> HOLLOWWAY_TREE_DOOR_ITEM = registerBlockItem("hollowway_tree_door", ModBlocks.HOLLOWWAY_TREE_DOOR);
    public static final RegistryObject<Item> HOLLOWWAY_TREE_TRAPDOOR_ITEM = registerBlockItem("hollowway_tree_trapdoor", ModBlocks.HOLLOWWAY_TREE_TRAPDOOR);
    public static final RegistryObject<Item> HOLLOWWAY_TREE_BUTTON_ITEM = registerBlockItem("hollowway_tree_button", ModBlocks.HOLLOWWAY_TREE_BUTTON);
    public static final RegistryObject<Item> HOLLOWWAY_TREE_PRESSURE_PLATE_ITEM = registerBlockItem("hollowway_tree_pressure_plate", ModBlocks.HOLLOWWAY_TREE_PRESSURE_PLATE);

    public static final RegistryObject<Item> DRIFTWILLOW_LOG_ITEM = registerBlockItem("driftwillow_log", ModBlocks.DRIFTWILLOW_LOG);
    public static final RegistryObject<Item> DRIFTWILLOW_PLANKS_ITEM = registerBlockItem("driftwillow_planks", ModBlocks.DRIFTWILLOW_PLANKS);
    public static final RegistryObject<Item> DRIFTWILLOW_LEAVES_ITEM = registerBlockItem("driftwillow_leaves", ModBlocks.DRIFTWILLOW_LEAVES);
    public static final RegistryObject<Item> DRIFTWILLOW_SAPLING_ITEM = registerBlockItem("driftwillow_sapling", ModBlocks.DRIFTWILLOW_SAPLING);

    public static final RegistryObject<Item> DRIFTWILLOW_STAIRS_ITEM = registerBlockItem("driftwillow_stairs", ModBlocks.DRIFTWILLOW_STAIRS);
    public static final RegistryObject<Item> DRIFTWILLOW_SLAB_ITEM = registerBlockItem("driftwillow_slab", ModBlocks.DRIFTWILLOW_SLAB);
    public static final RegistryObject<Item> DRIFTWILLOW_FENCE_ITEM = registerBlockItem("driftwillow_fence", ModBlocks.DRIFTWILLOW_FENCE);
    public static final RegistryObject<Item> DRIFTWILLOW_FENCE_GATE_ITEM = registerBlockItem("driftwillow_fence_gate", ModBlocks.DRIFTWILLOW_FENCE_GATE);
    public static final RegistryObject<Item> DRIFTWILLOW_DOOR_ITEM = registerBlockItem("driftwillow_door", ModBlocks.DRIFTWILLOW_DOOR);
    public static final RegistryObject<Item> DRIFTWILLOW_TRAPDOOR_ITEM = registerBlockItem("driftwillow_trapdoor", ModBlocks.DRIFTWILLOW_TRAPDOOR);
    public static final RegistryObject<Item> DRIFTWILLOW_BUTTON_ITEM = registerBlockItem("driftwillow_button", ModBlocks.DRIFTWILLOW_BUTTON);
    public static final RegistryObject<Item> DRIFTWILLOW_PRESSURE_PLATE_ITEM = registerBlockItem("driftwillow_pressure_plate", ModBlocks.DRIFTWILLOW_PRESSURE_PLATE);

    public static final RegistryObject<Item> MONUMENT_OAK_LOG_ITEM = registerBlockItem("monument_oak_log", ModBlocks.MONUMENT_OAK_LOG);
    public static final RegistryObject<Item> MONUMENT_OAK_PLANKS_ITEM = registerBlockItem("monument_oak_planks", ModBlocks.MONUMENT_OAK_PLANKS);
    public static final RegistryObject<Item> MONUMENT_OAK_LEAVES_ITEM = registerBlockItem("monument_oak_leaves", ModBlocks.MONUMENT_OAK_LEAVES);
    public static final RegistryObject<Item> MONUMENT_OAK_SAPLING_ITEM = registerBlockItem("monument_oak_sapling", ModBlocks.MONUMENT_OAK_SAPLING);

    public static final RegistryObject<Item> MONUMENT_OAK_STAIRS_ITEM = registerBlockItem("monument_oak_stairs", ModBlocks.MONUMENT_OAK_STAIRS);
    public static final RegistryObject<Item> MONUMENT_OAK_SLAB_ITEM = registerBlockItem("monument_oak_slab", ModBlocks.MONUMENT_OAK_SLAB);
    public static final RegistryObject<Item> MONUMENT_OAK_FENCE_ITEM = registerBlockItem("monument_oak_fence", ModBlocks.MONUMENT_OAK_FENCE);
    public static final RegistryObject<Item> MONUMENT_OAK_FENCE_GATE_ITEM = registerBlockItem("monument_oak_fence_gate", ModBlocks.MONUMENT_OAK_FENCE_GATE);
    public static final RegistryObject<Item> MONUMENT_OAK_DOOR_ITEM = registerBlockItem("monument_oak_door", ModBlocks.MONUMENT_OAK_DOOR);
    public static final RegistryObject<Item> MONUMENT_OAK_TRAPDOOR_ITEM = registerBlockItem("monument_oak_trapdoor", ModBlocks.MONUMENT_OAK_TRAPDOOR);
    public static final RegistryObject<Item> MONUMENT_OAK_BUTTON_ITEM = registerBlockItem("monument_oak_button", ModBlocks.MONUMENT_OAK_BUTTON);
    public static final RegistryObject<Item> MONUMENT_OAK_PRESSURE_PLATE_ITEM = registerBlockItem("monument_oak_pressure_plate", ModBlocks.MONUMENT_OAK_PRESSURE_PLATE);

    public static final RegistryObject<Item> WAYTORCH_TREE_LOG_ITEM = registerBlockItem("waytorch_tree_log", ModBlocks.WAYTORCH_TREE_LOG);
    public static final RegistryObject<Item> WAYTORCH_TREE_PLANKS_ITEM = registerBlockItem("waytorch_tree_planks", ModBlocks.WAYTORCH_TREE_PLANKS);
    public static final RegistryObject<Item> WAYTORCH_TREE_LEAVES_ITEM = registerBlockItem("waytorch_tree_leaves", ModBlocks.WAYTORCH_TREE_LEAVES);
    public static final RegistryObject<Item> WAYTORCH_TREE_SAPLING_ITEM = registerBlockItem("waytorch_tree_sapling", ModBlocks.WAYTORCH_TREE_SAPLING);

    public static final RegistryObject<Item> WAYTORCH_TREE_STAIRS_ITEM = registerBlockItem("waytorch_tree_stairs", ModBlocks.WAYTORCH_TREE_STAIRS);
    public static final RegistryObject<Item> WAYTORCH_TREE_SLAB_ITEM = registerBlockItem("waytorch_tree_slab", ModBlocks.WAYTORCH_TREE_SLAB);
    public static final RegistryObject<Item> WAYTORCH_TREE_FENCE_ITEM = registerBlockItem("waytorch_tree_fence", ModBlocks.WAYTORCH_TREE_FENCE);
    public static final RegistryObject<Item> WAYTORCH_TREE_FENCE_GATE_ITEM = registerBlockItem("waytorch_tree_fence_gate", ModBlocks.WAYTORCH_TREE_FENCE_GATE);
    public static final RegistryObject<Item> WAYTORCH_TREE_DOOR_ITEM = registerBlockItem("waytorch_tree_door", ModBlocks.WAYTORCH_TREE_DOOR);
    public static final RegistryObject<Item> WAYTORCH_TREE_TRAPDOOR_ITEM = registerBlockItem("waytorch_tree_trapdoor", ModBlocks.WAYTORCH_TREE_TRAPDOOR);
    public static final RegistryObject<Item> WAYTORCH_TREE_BUTTON_ITEM = registerBlockItem("waytorch_tree_button", ModBlocks.WAYTORCH_TREE_BUTTON);
    public static final RegistryObject<Item> WAYTORCH_TREE_PRESSURE_PLATE_ITEM = registerBlockItem("waytorch_tree_pressure_plate", ModBlocks.WAYTORCH_TREE_PRESSURE_PLATE);

    public static final RegistryObject<Item> FAULTWOOD_LOG_ITEM = registerBlockItem("faultwood_log", ModBlocks.FAULTWOOD_LOG);
    public static final RegistryObject<Item> FAULTWOOD_PLANKS_ITEM = registerBlockItem("faultwood_planks", ModBlocks.FAULTWOOD_PLANKS);
    public static final RegistryObject<Item> FAULTWOOD_LEAVES_ITEM = registerBlockItem("faultwood_leaves", ModBlocks.FAULTWOOD_LEAVES);
    public static final RegistryObject<Item> FAULTWOOD_SAPLING_ITEM = registerBlockItem("faultwood_sapling", ModBlocks.FAULTWOOD_SAPLING);

    public static final RegistryObject<Item> FAULTWOOD_STAIRS_ITEM = registerBlockItem("faultwood_stairs", ModBlocks.FAULTWOOD_STAIRS);
    public static final RegistryObject<Item> FAULTWOOD_SLAB_ITEM = registerBlockItem("faultwood_slab", ModBlocks.FAULTWOOD_SLAB);
    public static final RegistryObject<Item> FAULTWOOD_FENCE_ITEM = registerBlockItem("faultwood_fence", ModBlocks.FAULTWOOD_FENCE);
    public static final RegistryObject<Item> FAULTWOOD_FENCE_GATE_ITEM = registerBlockItem("faultwood_fence_gate", ModBlocks.FAULTWOOD_FENCE_GATE);
    public static final RegistryObject<Item> FAULTWOOD_DOOR_ITEM = registerBlockItem("faultwood_door", ModBlocks.FAULTWOOD_DOOR);
    public static final RegistryObject<Item> FAULTWOOD_TRAPDOOR_ITEM = registerBlockItem("faultwood_trapdoor", ModBlocks.FAULTWOOD_TRAPDOOR);
    public static final RegistryObject<Item> FAULTWOOD_BUTTON_ITEM = registerBlockItem("faultwood_button", ModBlocks.FAULTWOOD_BUTTON);
    public static final RegistryObject<Item> FAULTWOOD_PRESSURE_PLATE_ITEM = registerBlockItem("faultwood_pressure_plate", ModBlocks.FAULTWOOD_PRESSURE_PLATE);

    // --- Wayfall staple tree block items ---

    public static final RegistryObject<Item> ASHBLOOM_LOG_ITEM = registerBlockItem("ashbloom_log", ModBlocks.ASHBLOOM_LOG);
    public static final RegistryObject<Item> ASHBLOOM_PLANKS_ITEM = registerBlockItem("ashbloom_planks", ModBlocks.ASHBLOOM_PLANKS);
    public static final RegistryObject<Item> ASHBLOOM_LEAVES_ITEM = registerBlockItem("ashbloom_leaves", ModBlocks.ASHBLOOM_LEAVES);
    public static final RegistryObject<Item> ASHBLOOM_SAPLING_ITEM = registerBlockItem("ashbloom_sapling", ModBlocks.ASHBLOOM_SAPLING);
    public static final RegistryObject<Item> ASHBLOOM_STAIRS_ITEM = registerBlockItem("ashbloom_stairs", ModBlocks.ASHBLOOM_STAIRS);
    public static final RegistryObject<Item> ASHBLOOM_SLAB_ITEM = registerBlockItem("ashbloom_slab", ModBlocks.ASHBLOOM_SLAB);
    public static final RegistryObject<Item> ASHBLOOM_FENCE_ITEM = registerBlockItem("ashbloom_fence", ModBlocks.ASHBLOOM_FENCE);
    public static final RegistryObject<Item> ASHBLOOM_FENCE_GATE_ITEM = registerBlockItem("ashbloom_fence_gate", ModBlocks.ASHBLOOM_FENCE_GATE);
    public static final RegistryObject<Item> ASHBLOOM_DOOR_ITEM = registerBlockItem("ashbloom_door", ModBlocks.ASHBLOOM_DOOR);
    public static final RegistryObject<Item> ASHBLOOM_TRAPDOOR_ITEM = registerBlockItem("ashbloom_trapdoor", ModBlocks.ASHBLOOM_TRAPDOOR);
    public static final RegistryObject<Item> ASHBLOOM_BUTTON_ITEM = registerBlockItem("ashbloom_button", ModBlocks.ASHBLOOM_BUTTON);
    public static final RegistryObject<Item> ASHBLOOM_PRESSURE_PLATE_ITEM = registerBlockItem("ashbloom_pressure_plate", ModBlocks.ASHBLOOM_PRESSURE_PLATE);

    public static final RegistryObject<Item> GLIMMERPINE_LOG_ITEM = registerBlockItem("glimmerpine_log", ModBlocks.GLIMMERPINE_LOG);
    public static final RegistryObject<Item> GLIMMERPINE_PLANKS_ITEM = registerBlockItem("glimmerpine_planks", ModBlocks.GLIMMERPINE_PLANKS);
    public static final RegistryObject<Item> GLIMMERPINE_LEAVES_ITEM = registerBlockItem("glimmerpine_leaves", ModBlocks.GLIMMERPINE_LEAVES);
    public static final RegistryObject<Item> GLIMMERPINE_SAPLING_ITEM = registerBlockItem("glimmerpine_sapling", ModBlocks.GLIMMERPINE_SAPLING);
    public static final RegistryObject<Item> GLIMMERPINE_STAIRS_ITEM = registerBlockItem("glimmerpine_stairs", ModBlocks.GLIMMERPINE_STAIRS);
    public static final RegistryObject<Item> GLIMMERPINE_SLAB_ITEM = registerBlockItem("glimmerpine_slab", ModBlocks.GLIMMERPINE_SLAB);
    public static final RegistryObject<Item> GLIMMERPINE_FENCE_ITEM = registerBlockItem("glimmerpine_fence", ModBlocks.GLIMMERPINE_FENCE);
    public static final RegistryObject<Item> GLIMMERPINE_FENCE_GATE_ITEM = registerBlockItem("glimmerpine_fence_gate", ModBlocks.GLIMMERPINE_FENCE_GATE);
    public static final RegistryObject<Item> GLIMMERPINE_DOOR_ITEM = registerBlockItem("glimmerpine_door", ModBlocks.GLIMMERPINE_DOOR);
    public static final RegistryObject<Item> GLIMMERPINE_TRAPDOOR_ITEM = registerBlockItem("glimmerpine_trapdoor", ModBlocks.GLIMMERPINE_TRAPDOOR);
    public static final RegistryObject<Item> GLIMMERPINE_BUTTON_ITEM = registerBlockItem("glimmerpine_button", ModBlocks.GLIMMERPINE_BUTTON);
    public static final RegistryObject<Item> GLIMMERPINE_PRESSURE_PLATE_ITEM = registerBlockItem("glimmerpine_pressure_plate", ModBlocks.GLIMMERPINE_PRESSURE_PLATE);

    public static final RegistryObject<Item> DRIFTWOOD_LOG_ITEM = registerBlockItem("driftwood_log", ModBlocks.DRIFTWOOD_LOG);
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
