package com.kruemblegard.init;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.block.AncientWaystoneBlock;
import com.kruemblegard.block.BerryBushBlock;
import com.kruemblegard.block.EchocapBlock;
import com.kruemblegard.block.GravevineBlock;
import com.kruemblegard.block.GlimmerpineLeavesBlock;
import com.kruemblegard.block.AshbloomLeavesBlock;
import com.kruemblegard.block.DriftwoodLeavesBlock;
import com.kruemblegard.block.AshmossBlock;
import com.kruemblegard.block.RunedStoneveilRubbleBlock;
import com.kruemblegard.block.RunegrowthBlock;
import com.kruemblegard.block.ScarstoneBlock;
import com.kruemblegard.block.VeilgrowthBlock;
import com.kruemblegard.block.VoidfeltBlock;
import com.kruemblegard.block.WayfallReactivePlantBlock;
import com.kruemblegard.block.AshveilBlock;
import com.kruemblegard.block.RunebloomBlock;
import com.kruemblegard.block.SoulberryShrubBlock;
import com.kruemblegard.block.WayfallFeatureSaplingBlock;
import com.kruemblegard.block.WayfallPlantBlock;
import com.kruemblegard.block.WispstalkBlock;
import com.kruemblegard.world.grower.FixedConfiguredFeatureTreeGrower;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.material.MapColor;

import net.minecraft.core.particles.ParticleTypes;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    private ModBlocks() {}

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Kruemblegard.MODID);

    public static final RegistryObject<Block> ANCIENT_WAYSTONE = BLOCKS.register(
            "ancient_waystone",
            () -> new AncientWaystoneBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(3.5F, 30.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> ATTUNED_STONE = BLOCKS.register(
            "attuned_stone",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(3.0F, 30.0F)
                    .sound(SoundType.STONE))
    );

    // --- Wayfall geology (palette foundation) ---

    public static final RegistryObject<Block> FRACTURED_WAYROCK = BLOCKS.register(
            "fractured_wayrock",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(2.0F, 18.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> CRUSHSTONE = BLOCKS.register(
            "crushstone",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(1.2F, 6.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> ASHFALL_LOAM = BLOCKS.register(
            "ashfall_loam",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIRT)
                    .strength(0.6F, 0.6F)
                    .sound(SoundType.ROOTED_DIRT))
    );

    public static final RegistryObject<Block> FAULT_DUST = BLOCKS.register(
            "fault_dust",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .strength(0.5F, 0.5F)
                    .sound(SoundType.SAND))
    );

    public static final RegistryObject<Block> CRACKED_SCARSTONE = BLOCKS.register(
            "cracked_scarstone",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DEEPSLATE)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 36.0F)
                    .sound(SoundType.DEEPSLATE))
    );

    public static final RegistryObject<Block> SCARSTONE = BLOCKS.register(
            "scarstone",
            () -> new ScarstoneBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DEEPSLATE)
                    .requiresCorrectToolForDrops()
                    .strength(3.5F, 42.0F)
                    .sound(SoundType.DEEPSLATE))
    );

    public static final RegistryObject<Block> POLISHED_SCARSTONE = BLOCKS.register(
            "polished_scarstone",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DEEPSLATE)
                    .requiresCorrectToolForDrops()
                    .strength(4.0F, 50.0F)
                    .sound(SoundType.DEEPSLATE))
    );

    public static final RegistryObject<Block> CHISELED_SCARSTONE = BLOCKS.register(
            "chiseled_scarstone",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DEEPSLATE)
                    .requiresCorrectToolForDrops()
                    .strength(4.5F, 60.0F)
                    .sound(SoundType.DEEPSLATE))
    );

    // --- Stoneveil rubble (structure palette) ---

    public static final RegistryObject<Block> STONEVEIL_RUBBLE = BLOCKS.register(
            "stoneveil_rubble",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(2.2F, 18.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> POLISHED_STONEVEIL_RUBBLE = BLOCKS.register(
            "polished_stoneveil_rubble",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(2.6F, 20.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> RUNED_STONEVEIL_RUBBLE = BLOCKS.register(
            "runed_stoneveil_rubble",
            () -> new RunedStoneveilRubbleBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(2.8F, 22.0F)
                    .sound(SoundType.STONE))
    );

    // --- Wayfall surface covers (spread / conversion) ---

    public static final RegistryObject<Block> VEILGROWTH = BLOCKS.register(
            "veilgrowth",
            () -> new VeilgrowthBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(0.6F, 0.6F)
                    .sound(SoundType.GRASS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> ASHMOSS = BLOCKS.register(
            "ashmoss",
            () -> new AshmossBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(0.6F, 0.6F)
                    .sound(SoundType.MOSS_CARPET)
                    .randomTicks())
    );

    public static final RegistryObject<Block> RUNEGROWTH = BLOCKS.register(
            "runegrowth",
            () -> new RunegrowthBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLUE)
                    .strength(0.6F, 0.6F)
                    .sound(SoundType.GRASS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> VOIDFELT = BLOCKS.register(
            "voidfelt",
            () -> new VoidfeltBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(0.6F, 0.6F)
                    .sound(SoundType.WOOL)
                    .randomTicks())
    );

    public static final RegistryObject<Block> ATTUNED_ORE = BLOCKS.register(
            "attuned_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 60.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> STANDING_STONE = BLOCKS.register(
            "standing_stone",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(4.0F, 60.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> WISPSTALK = BLOCKS.register(
            "wispstalk",
            () -> new WispstalkBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> GRAVEVINE = BLOCKS.register(
            "gravevine",
            () -> new GravevineBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> ECHOCAP = BLOCKS.register(
            "echocap",
            () -> new EchocapBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.FUNGUS))
    );

    public static final RegistryObject<Block> RUNEBLOOM = BLOCKS.register(
            "runebloom",
            () -> new RunebloomBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLUE)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> SOULBERRY_SHRUB = BLOCKS.register(
            "soulberry_shrub",
            () -> new SoulberryShrubBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.SWEET_BERRY_BUSH)
                    .randomTicks())
    );

    public static final RegistryObject<Block> GHOULBERRY_SHRUB = BLOCKS.register(
            "ghoulberry_shrub",
            () -> new BerryBushBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.SWEET_BERRY_BUSH)
                    .randomTicks(),
                    Kruemblegard.MOD_ID,
                    "ghoulberries",
                    1.0f)
    );

    // --- Wayfall flora (plants) ---

    public static final RegistryObject<Block> PATHREED = BLOCKS.register(
            "pathreed",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> FAULTGRASS = BLOCKS.register(
            "faultgrass",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> DRIFTBLOOM = BLOCKS.register(
            "driftbloom",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PINK)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> CAIRN_MOSS = BLOCKS.register(
            "cairn_moss",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.MOSS_CARPET)
                    .randomTicks())
    );

    public static final RegistryObject<Block> WAYLILY = BLOCKS.register(
            "waylily",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLUE)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.LILY_PAD)
                    .randomTicks())
    );

    public static final RegistryObject<Block> GRIEFCAP = BLOCKS.register(
            "griefcap",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.FUNGUS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> STATIC_FUNGUS = BLOCKS.register(
            "static_fungus",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.FUNGUS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> WAYROT_FUNGUS = BLOCKS.register(
            "wayrot_fungus",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.FUNGUS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> ECHO_PUFF = BLOCKS.register(
            "echo_puff",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.FUNGUS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> RUIN_THISTLE = BLOCKS.register(
            "ruin_thistle",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> WAYSEED_CLUSTER = BLOCKS.register(
            "wayseed_cluster",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> VOID_LICHEN = BLOCKS.register(
            "void_lichen",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.MOSS_CARPET)
                    .randomTicks())
    );

    public static final RegistryObject<Block> TRANSIT_FERN = BLOCKS.register(
            "transit_fern",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> MISSTEP_VINE = BLOCKS.register(
            "misstep_vine",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.VINE)
                    .randomTicks())
    );

    public static final RegistryObject<Block> WAYBIND_CREEPER = BLOCKS.register(
            "waybind_creeper",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> MILESTONE_GRASS = BLOCKS.register(
            "milestone_grass",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> RUNEDRIFT_REED = BLOCKS.register(
            "runedrift_reed",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> WAYSCAR_IVY = BLOCKS.register(
            "wayscar_ivy",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.VINE)
                    .randomTicks())
    );

    public static final RegistryObject<Block> ASHPETAL = BLOCKS.register(
            "ashpetal",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> TRANSIT_BLOOM = BLOCKS.register(
            "transit_bloom",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PINK)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> CAIRNROOT = BLOCKS.register(
            "cairnroot",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.ROOTS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> WAYPOINT_MOLD = BLOCKS.register(
            "waypoint_mold",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.FUNGUS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> BLACK_ECHO_FUNGUS = BLOCKS.register(
            "black_echo_fungus",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.FUNGUS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> WAYBURN_FUNGUS = BLOCKS.register(
            "wayburn_fungus",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.FUNGUS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> MEMORY_ROT = BLOCKS.register(
            "memory_rot",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_RED)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.SLIME_BLOCK)
                    .randomTicks())
    );

    public static final RegistryObject<Block> FALSEPATH_THORNS = BLOCKS.register(
            "falsepath_thorns",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> SLIPROOT = BLOCKS.register(
            "sliproot",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.ROOTS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> WAYGRASP_VINE = BLOCKS.register(
            "waygrasp_vine",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.VINE)
                    .randomTicks())
    );

    public static final RegistryObject<Block> VOIDCAP_BRIAR = BLOCKS.register(
            "voidcap_briar",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.FUNGUS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> DUSTPETAL = BLOCKS.register(
            "dustpetal",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> RUNE_SPROUTS = BLOCKS.register(
            "rune_sprouts",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> WAYTHREAD = BLOCKS.register(
            "waythread",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> GRAVEMINT = BLOCKS.register(
            "gravemint",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> FALLSEED_PODS = BLOCKS.register(
            "fallseed_pods",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .randomTicks())
    );

    public static final RegistryObject<Block> REVERSE_PORTAL_SPORES = BLOCKS.register(
            "reverse_portal_spores",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .randomTicks())
    );

    // --- Wayfall staples (all-biome plants/shrubs) ---

    public static final RegistryObject<Block> VOIDFERN = BLOCKS.register(
            "voidfern",
            () -> new WayfallReactivePlantBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_PURPLE)
                            .noCollission()
                            .instabreak()
                            .sound(SoundType.GRASS)
                            .randomTicks(),
                    ParticleTypes.REVERSE_PORTAL,
                    ParticleTypes.REVERSE_PORTAL,
                    0,
                    0)
    );

    public static final RegistryObject<Block> RUNEBLOSSOM = BLOCKS.register(
            "runeblossom",
            () -> new WayfallReactivePlantBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BLUE)
                            .noCollission()
                            .instabreak()
                            .sound(SoundType.GRASS)
                            .randomTicks(),
                    ParticleTypes.ENCHANT,
                    ParticleTypes.END_ROD,
                    0,
                    6)
    );

    public static final RegistryObject<Block> MOTESHRUB = BLOCKS.register(
            "moteshrub",
            () -> new WayfallReactivePlantBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_LIGHT_GRAY)
                            .noCollission()
                            .instabreak()
                            .sound(SoundType.GRASS)
                            .randomTicks(),
                    ParticleTypes.END_ROD,
                    ParticleTypes.END_ROD,
                    0,
                    0)
    );

    public static final RegistryObject<Block> ASHVEIL = BLOCKS.register(
            "ashveil",
            () -> new AshveilBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.MOSS_CARPET)
                    .randomTicks())
    );

    public static final RegistryObject<Block> TWILIGHT_BULB = BLOCKS.register(
            "twilight_bulb",
            () -> new WayfallReactivePlantBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BLACK)
                            .noCollission()
                            .instabreak()
                            .sound(SoundType.GRASS)
                            .lightLevel(s -> 4)
                            .randomTicks(),
                    ParticleTypes.WITCH,
                    ParticleTypes.END_ROD,
                    6,
                    6)
    );

    public static final RegistryObject<Block> WHISPERVINE = BLOCKS.register(
            "whispervine",
            () -> new WayfallReactivePlantBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BLACK)
                            .noCollission()
                            .instabreak()
                            .sound(SoundType.GRASS)
                            .randomTicks(),
                    ParticleTypes.SMOKE,
                    ParticleTypes.SMOKE,
                    0,
                    0)
    );

        // --- Wayfall trees (block sets; saplings grow into matching configured features) ---

    private static RegistryObject<Block> registerLog(String id) {
        return BLOCKS.register(id, () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)
                .strength(2.0F)
                .sound(SoundType.WOOD)));
    }

    private static RegistryObject<Block> registerPlanks(String id) {
        return BLOCKS.register(id, () -> new Block(BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)
                .strength(2.0F, 3.0F)
                .sound(SoundType.WOOD)));
    }

    private static RegistryObject<Block> registerLeaves(String id) {
        return BLOCKS.register(id, () -> new LeavesBlock(BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .strength(0.2F)
                .randomTicks()
                .noOcclusion()
                .sound(SoundType.GRASS)));
    }

        private static BlockBehaviour.Properties woodFamilyProperties() {
                return BlockBehaviour.Properties.of()
                                .mapColor(MapColor.WOOD)
                                .strength(2.0F, 3.0F)
                                .sound(SoundType.WOOD);
        }

    private static RegistryObject<Block> registerStairs(String id, RegistryObject<Block> planks) {
        return BLOCKS.register(id,
                                () -> new StairBlock(net.minecraft.world.level.block.Blocks.OAK_PLANKS::defaultBlockState, woodFamilyProperties()));
    }

    private static RegistryObject<Block> registerSlab(String id, RegistryObject<Block> planks) {
                return BLOCKS.register(id, () -> new SlabBlock(woodFamilyProperties()));
    }

    private static RegistryObject<Block> registerFence(String id, RegistryObject<Block> planks) {
                return BLOCKS.register(id, () -> new FenceBlock(woodFamilyProperties()));
    }

    private static RegistryObject<Block> registerFenceGate(String id, RegistryObject<Block> planks) {
                return BLOCKS.register(id, () -> new FenceGateBlock(woodFamilyProperties(), WoodType.OAK));
    }

    private static RegistryObject<Block> registerDoor(String id, RegistryObject<Block> planks) {
        return BLOCKS.register(id,
                                () -> new DoorBlock(woodFamilyProperties().noOcclusion(), BlockSetType.OAK));
    }

    private static RegistryObject<Block> registerTrapdoor(String id, RegistryObject<Block> planks) {
        return BLOCKS.register(id,
                                () -> new TrapDoorBlock(woodFamilyProperties().noOcclusion(), BlockSetType.OAK));
    }

    private static RegistryObject<Block> registerButton(String id, RegistryObject<Block> planks) {
        return BLOCKS.register(id,
                () -> new ButtonBlock(BlockBehaviour.Properties.of()
                        .mapColor(MapColor.WOOD)
                        .noCollission()
                        .strength(0.5F)
                        .sound(SoundType.WOOD), BlockSetType.OAK, 30, true));
    }

    private static RegistryObject<Block> registerPressurePlate(String id, RegistryObject<Block> planks) {
        return BLOCKS.register(id,
                () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.of()
                        .mapColor(MapColor.WOOD)
                        .strength(0.5F)
                        .sound(SoundType.WOOD), BlockSetType.OAK));
    }

        private static RegistryObject<Block> registerFeatureSapling(String id, ResourceKey<ConfiguredFeature<?, ?>> featureKey) {
                return BLOCKS.register(id, () -> new WayfallFeatureSaplingBlock(
                                new FixedConfiguredFeatureTreeGrower(featureKey),
                                BlockBehaviour.Properties.of()
                                                .mapColor(MapColor.PLANT)
                                                .noCollission()
                                                .instabreak()
                                                .sound(SoundType.GRASS)
                                                .randomTicks()
                ));
        }

        private static ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureKey(String path) {
                return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(Kruemblegard.MODID, path));
        }

    public static final RegistryObject<Block> WAYROOT_LOG = registerLog("wayroot_log");
    public static final RegistryObject<Block> WAYROOT_PLANKS = registerPlanks("wayroot_planks");
    public static final RegistryObject<Block> WAYROOT_LEAVES = registerLeaves("wayroot_leaves");
        public static final RegistryObject<Block> WAYROOT_SAPLING = registerFeatureSapling(
                        "wayroot_sapling",
                        configuredFeatureKey("wayroot/sapling")
        );

        public static final RegistryObject<Block> WAYROOT_STAIRS = registerStairs("wayroot_stairs", WAYROOT_PLANKS);
        public static final RegistryObject<Block> WAYROOT_SLAB = registerSlab("wayroot_slab", WAYROOT_PLANKS);
        public static final RegistryObject<Block> WAYROOT_FENCE = registerFence("wayroot_fence", WAYROOT_PLANKS);
        public static final RegistryObject<Block> WAYROOT_FENCE_GATE = registerFenceGate("wayroot_fence_gate", WAYROOT_PLANKS);
        public static final RegistryObject<Block> WAYROOT_DOOR = registerDoor("wayroot_door", WAYROOT_PLANKS);
        public static final RegistryObject<Block> WAYROOT_TRAPDOOR = registerTrapdoor("wayroot_trapdoor", WAYROOT_PLANKS);
        public static final RegistryObject<Block> WAYROOT_BUTTON = registerButton("wayroot_button", WAYROOT_PLANKS);
        public static final RegistryObject<Block> WAYROOT_PRESSURE_PLATE = registerPressurePlate("wayroot_pressure_plate", WAYROOT_PLANKS);

    public static final RegistryObject<Block> FALLBARK_LOG = registerLog("fallbark_log");
    public static final RegistryObject<Block> FALLBARK_PLANKS = registerPlanks("fallbark_planks");
    public static final RegistryObject<Block> FALLBARK_LEAVES = registerLeaves("fallbark_leaves");
        public static final RegistryObject<Block> FALLBARK_SAPLING = registerFeatureSapling(
                        "fallbark_sapling",
                        configuredFeatureKey("fallbark/sapling")
        );

        public static final RegistryObject<Block> FALLBARK_STAIRS = registerStairs("fallbark_stairs", FALLBARK_PLANKS);
        public static final RegistryObject<Block> FALLBARK_SLAB = registerSlab("fallbark_slab", FALLBARK_PLANKS);
        public static final RegistryObject<Block> FALLBARK_FENCE = registerFence("fallbark_fence", FALLBARK_PLANKS);
        public static final RegistryObject<Block> FALLBARK_FENCE_GATE = registerFenceGate("fallbark_fence_gate", FALLBARK_PLANKS);
        public static final RegistryObject<Block> FALLBARK_DOOR = registerDoor("fallbark_door", FALLBARK_PLANKS);
        public static final RegistryObject<Block> FALLBARK_TRAPDOOR = registerTrapdoor("fallbark_trapdoor", FALLBARK_PLANKS);
        public static final RegistryObject<Block> FALLBARK_BUTTON = registerButton("fallbark_button", FALLBARK_PLANKS);
        public static final RegistryObject<Block> FALLBARK_PRESSURE_PLATE = registerPressurePlate("fallbark_pressure_plate", FALLBARK_PLANKS);

    public static final RegistryObject<Block> ECHOWOOD_LOG = registerLog("echowood_log");
    public static final RegistryObject<Block> ECHOWOOD_PLANKS = registerPlanks("echowood_planks");
    public static final RegistryObject<Block> ECHOWOOD_LEAVES = registerLeaves("echowood_leaves");
        public static final RegistryObject<Block> ECHOWOOD_SAPLING = registerFeatureSapling(
                        "echowood_sapling",
                        configuredFeatureKey("echowood/sapling")
        );

        public static final RegistryObject<Block> ECHOWOOD_STAIRS = registerStairs("echowood_stairs", ECHOWOOD_PLANKS);
        public static final RegistryObject<Block> ECHOWOOD_SLAB = registerSlab("echowood_slab", ECHOWOOD_PLANKS);
        public static final RegistryObject<Block> ECHOWOOD_FENCE = registerFence("echowood_fence", ECHOWOOD_PLANKS);
        public static final RegistryObject<Block> ECHOWOOD_FENCE_GATE = registerFenceGate("echowood_fence_gate", ECHOWOOD_PLANKS);
        public static final RegistryObject<Block> ECHOWOOD_DOOR = registerDoor("echowood_door", ECHOWOOD_PLANKS);
        public static final RegistryObject<Block> ECHOWOOD_TRAPDOOR = registerTrapdoor("echowood_trapdoor", ECHOWOOD_PLANKS);
        public static final RegistryObject<Block> ECHOWOOD_BUTTON = registerButton("echowood_button", ECHOWOOD_PLANKS);
        public static final RegistryObject<Block> ECHOWOOD_PRESSURE_PLATE = registerPressurePlate("echowood_pressure_plate", ECHOWOOD_PLANKS);

    public static final RegistryObject<Block> CAIRN_TREE_LOG = registerLog("cairn_tree_log");
    public static final RegistryObject<Block> CAIRN_TREE_PLANKS = registerPlanks("cairn_tree_planks");
    public static final RegistryObject<Block> CAIRN_TREE_LEAVES = registerLeaves("cairn_tree_leaves");
        public static final RegistryObject<Block> CAIRN_TREE_SAPLING = registerFeatureSapling(
                        "cairn_tree_sapling",
                        configuredFeatureKey("cairn_tree/sapling")
        );

        public static final RegistryObject<Block> CAIRN_TREE_STAIRS = registerStairs("cairn_tree_stairs", CAIRN_TREE_PLANKS);
        public static final RegistryObject<Block> CAIRN_TREE_SLAB = registerSlab("cairn_tree_slab", CAIRN_TREE_PLANKS);
        public static final RegistryObject<Block> CAIRN_TREE_FENCE = registerFence("cairn_tree_fence", CAIRN_TREE_PLANKS);
        public static final RegistryObject<Block> CAIRN_TREE_FENCE_GATE = registerFenceGate("cairn_tree_fence_gate", CAIRN_TREE_PLANKS);
        public static final RegistryObject<Block> CAIRN_TREE_DOOR = registerDoor("cairn_tree_door", CAIRN_TREE_PLANKS);
        public static final RegistryObject<Block> CAIRN_TREE_TRAPDOOR = registerTrapdoor("cairn_tree_trapdoor", CAIRN_TREE_PLANKS);
        public static final RegistryObject<Block> CAIRN_TREE_BUTTON = registerButton("cairn_tree_button", CAIRN_TREE_PLANKS);
        public static final RegistryObject<Block> CAIRN_TREE_PRESSURE_PLATE = registerPressurePlate("cairn_tree_pressure_plate", CAIRN_TREE_PLANKS);

    public static final RegistryObject<Block> WAYGLASS_LOG = registerLog("wayglass_log");
    public static final RegistryObject<Block> WAYGLASS_PLANKS = registerPlanks("wayglass_planks");
    public static final RegistryObject<Block> WAYGLASS_LEAVES = registerLeaves("wayglass_leaves");
        public static final RegistryObject<Block> WAYGLASS_SAPLING = registerFeatureSapling(
                        "wayglass_sapling",
                        configuredFeatureKey("wayglass/sapling")
        );

        public static final RegistryObject<Block> WAYGLASS_STAIRS = registerStairs("wayglass_stairs", WAYGLASS_PLANKS);
        public static final RegistryObject<Block> WAYGLASS_SLAB = registerSlab("wayglass_slab", WAYGLASS_PLANKS);
        public static final RegistryObject<Block> WAYGLASS_FENCE = registerFence("wayglass_fence", WAYGLASS_PLANKS);
        public static final RegistryObject<Block> WAYGLASS_FENCE_GATE = registerFenceGate("wayglass_fence_gate", WAYGLASS_PLANKS);
        public static final RegistryObject<Block> WAYGLASS_DOOR = registerDoor("wayglass_door", WAYGLASS_PLANKS);
        public static final RegistryObject<Block> WAYGLASS_TRAPDOOR = registerTrapdoor("wayglass_trapdoor", WAYGLASS_PLANKS);
        public static final RegistryObject<Block> WAYGLASS_BUTTON = registerButton("wayglass_button", WAYGLASS_PLANKS);
        public static final RegistryObject<Block> WAYGLASS_PRESSURE_PLATE = registerPressurePlate("wayglass_pressure_plate", WAYGLASS_PLANKS);

    public static final RegistryObject<Block> SHARDBARK_PINE_LOG = registerLog("shardbark_pine_log");
    public static final RegistryObject<Block> SHARDBARK_PINE_PLANKS = registerPlanks("shardbark_pine_planks");
    public static final RegistryObject<Block> SHARDBARK_PINE_LEAVES = registerLeaves("shardbark_pine_leaves");
        public static final RegistryObject<Block> SHARDBARK_PINE_SAPLING = registerFeatureSapling(
                        "shardbark_pine_sapling",
                        configuredFeatureKey("shardbark_pine/sapling")
        );

        public static final RegistryObject<Block> SHARDBARK_PINE_STAIRS = registerStairs("shardbark_pine_stairs", SHARDBARK_PINE_PLANKS);
        public static final RegistryObject<Block> SHARDBARK_PINE_SLAB = registerSlab("shardbark_pine_slab", SHARDBARK_PINE_PLANKS);
        public static final RegistryObject<Block> SHARDBARK_PINE_FENCE = registerFence("shardbark_pine_fence", SHARDBARK_PINE_PLANKS);
        public static final RegistryObject<Block> SHARDBARK_PINE_FENCE_GATE = registerFenceGate("shardbark_pine_fence_gate", SHARDBARK_PINE_PLANKS);
        public static final RegistryObject<Block> SHARDBARK_PINE_DOOR = registerDoor("shardbark_pine_door", SHARDBARK_PINE_PLANKS);
        public static final RegistryObject<Block> SHARDBARK_PINE_TRAPDOOR = registerTrapdoor("shardbark_pine_trapdoor", SHARDBARK_PINE_PLANKS);
        public static final RegistryObject<Block> SHARDBARK_PINE_BUTTON = registerButton("shardbark_pine_button", SHARDBARK_PINE_PLANKS);
        public static final RegistryObject<Block> SHARDBARK_PINE_PRESSURE_PLATE = registerPressurePlate("shardbark_pine_pressure_plate", SHARDBARK_PINE_PLANKS);

    public static final RegistryObject<Block> HOLLOWWAY_TREE_LOG = registerLog("hollowway_tree_log");
    public static final RegistryObject<Block> HOLLOWWAY_TREE_PLANKS = registerPlanks("hollowway_tree_planks");
    public static final RegistryObject<Block> HOLLOWWAY_TREE_LEAVES = registerLeaves("hollowway_tree_leaves");
        public static final RegistryObject<Block> HOLLOWWAY_TREE_SAPLING = registerFeatureSapling(
                        "hollowway_tree_sapling",
                        configuredFeatureKey("hollowway_tree/sapling")
        );

        public static final RegistryObject<Block> HOLLOWWAY_TREE_STAIRS = registerStairs("hollowway_tree_stairs", HOLLOWWAY_TREE_PLANKS);
        public static final RegistryObject<Block> HOLLOWWAY_TREE_SLAB = registerSlab("hollowway_tree_slab", HOLLOWWAY_TREE_PLANKS);
        public static final RegistryObject<Block> HOLLOWWAY_TREE_FENCE = registerFence("hollowway_tree_fence", HOLLOWWAY_TREE_PLANKS);
        public static final RegistryObject<Block> HOLLOWWAY_TREE_FENCE_GATE = registerFenceGate("hollowway_tree_fence_gate", HOLLOWWAY_TREE_PLANKS);
        public static final RegistryObject<Block> HOLLOWWAY_TREE_DOOR = registerDoor("hollowway_tree_door", HOLLOWWAY_TREE_PLANKS);
        public static final RegistryObject<Block> HOLLOWWAY_TREE_TRAPDOOR = registerTrapdoor("hollowway_tree_trapdoor", HOLLOWWAY_TREE_PLANKS);
        public static final RegistryObject<Block> HOLLOWWAY_TREE_BUTTON = registerButton("hollowway_tree_button", HOLLOWWAY_TREE_PLANKS);
        public static final RegistryObject<Block> HOLLOWWAY_TREE_PRESSURE_PLATE = registerPressurePlate("hollowway_tree_pressure_plate", HOLLOWWAY_TREE_PLANKS);

    public static final RegistryObject<Block> DRIFTWILLOW_LOG = registerLog("driftwillow_log");
    public static final RegistryObject<Block> DRIFTWILLOW_PLANKS = registerPlanks("driftwillow_planks");
    public static final RegistryObject<Block> DRIFTWILLOW_LEAVES = registerLeaves("driftwillow_leaves");
        public static final RegistryObject<Block> DRIFTWILLOW_SAPLING = registerFeatureSapling(
                        "driftwillow_sapling",
                        configuredFeatureKey("driftwillow/sapling")
        );

        public static final RegistryObject<Block> DRIFTWILLOW_STAIRS = registerStairs("driftwillow_stairs", DRIFTWILLOW_PLANKS);
        public static final RegistryObject<Block> DRIFTWILLOW_SLAB = registerSlab("driftwillow_slab", DRIFTWILLOW_PLANKS);
        public static final RegistryObject<Block> DRIFTWILLOW_FENCE = registerFence("driftwillow_fence", DRIFTWILLOW_PLANKS);
        public static final RegistryObject<Block> DRIFTWILLOW_FENCE_GATE = registerFenceGate("driftwillow_fence_gate", DRIFTWILLOW_PLANKS);
        public static final RegistryObject<Block> DRIFTWILLOW_DOOR = registerDoor("driftwillow_door", DRIFTWILLOW_PLANKS);
        public static final RegistryObject<Block> DRIFTWILLOW_TRAPDOOR = registerTrapdoor("driftwillow_trapdoor", DRIFTWILLOW_PLANKS);
        public static final RegistryObject<Block> DRIFTWILLOW_BUTTON = registerButton("driftwillow_button", DRIFTWILLOW_PLANKS);
        public static final RegistryObject<Block> DRIFTWILLOW_PRESSURE_PLATE = registerPressurePlate("driftwillow_pressure_plate", DRIFTWILLOW_PLANKS);

    public static final RegistryObject<Block> MONUMENT_OAK_LOG = registerLog("monument_oak_log");
    public static final RegistryObject<Block> MONUMENT_OAK_PLANKS = registerPlanks("monument_oak_planks");
    public static final RegistryObject<Block> MONUMENT_OAK_LEAVES = registerLeaves("monument_oak_leaves");
        public static final RegistryObject<Block> MONUMENT_OAK_SAPLING = registerFeatureSapling(
                        "monument_oak_sapling",
                        configuredFeatureKey("monument_oak/sapling")
        );

        public static final RegistryObject<Block> MONUMENT_OAK_STAIRS = registerStairs("monument_oak_stairs", MONUMENT_OAK_PLANKS);
        public static final RegistryObject<Block> MONUMENT_OAK_SLAB = registerSlab("monument_oak_slab", MONUMENT_OAK_PLANKS);
        public static final RegistryObject<Block> MONUMENT_OAK_FENCE = registerFence("monument_oak_fence", MONUMENT_OAK_PLANKS);
        public static final RegistryObject<Block> MONUMENT_OAK_FENCE_GATE = registerFenceGate("monument_oak_fence_gate", MONUMENT_OAK_PLANKS);
        public static final RegistryObject<Block> MONUMENT_OAK_DOOR = registerDoor("monument_oak_door", MONUMENT_OAK_PLANKS);
        public static final RegistryObject<Block> MONUMENT_OAK_TRAPDOOR = registerTrapdoor("monument_oak_trapdoor", MONUMENT_OAK_PLANKS);
        public static final RegistryObject<Block> MONUMENT_OAK_BUTTON = registerButton("monument_oak_button", MONUMENT_OAK_PLANKS);
        public static final RegistryObject<Block> MONUMENT_OAK_PRESSURE_PLATE = registerPressurePlate("monument_oak_pressure_plate", MONUMENT_OAK_PLANKS);

    public static final RegistryObject<Block> WAYTORCH_TREE_LOG = registerLog("waytorch_tree_log");
    public static final RegistryObject<Block> WAYTORCH_TREE_PLANKS = registerPlanks("waytorch_tree_planks");
    public static final RegistryObject<Block> WAYTORCH_TREE_LEAVES = registerLeaves("waytorch_tree_leaves");
        public static final RegistryObject<Block> WAYTORCH_TREE_SAPLING = registerFeatureSapling(
                        "waytorch_tree_sapling",
                        configuredFeatureKey("waytorch_tree/sapling")
        );

        public static final RegistryObject<Block> WAYTORCH_TREE_STAIRS = registerStairs("waytorch_tree_stairs", WAYTORCH_TREE_PLANKS);
        public static final RegistryObject<Block> WAYTORCH_TREE_SLAB = registerSlab("waytorch_tree_slab", WAYTORCH_TREE_PLANKS);
        public static final RegistryObject<Block> WAYTORCH_TREE_FENCE = registerFence("waytorch_tree_fence", WAYTORCH_TREE_PLANKS);
        public static final RegistryObject<Block> WAYTORCH_TREE_FENCE_GATE = registerFenceGate("waytorch_tree_fence_gate", WAYTORCH_TREE_PLANKS);
        public static final RegistryObject<Block> WAYTORCH_TREE_DOOR = registerDoor("waytorch_tree_door", WAYTORCH_TREE_PLANKS);
        public static final RegistryObject<Block> WAYTORCH_TREE_TRAPDOOR = registerTrapdoor("waytorch_tree_trapdoor", WAYTORCH_TREE_PLANKS);
        public static final RegistryObject<Block> WAYTORCH_TREE_BUTTON = registerButton("waytorch_tree_button", WAYTORCH_TREE_PLANKS);
        public static final RegistryObject<Block> WAYTORCH_TREE_PRESSURE_PLATE = registerPressurePlate("waytorch_tree_pressure_plate", WAYTORCH_TREE_PLANKS);

    public static final RegistryObject<Block> FAULTWOOD_LOG = registerLog("faultwood_log");
    public static final RegistryObject<Block> FAULTWOOD_PLANKS = registerPlanks("faultwood_planks");
    public static final RegistryObject<Block> FAULTWOOD_LEAVES = registerLeaves("faultwood_leaves");
        public static final RegistryObject<Block> FAULTWOOD_SAPLING = registerFeatureSapling(
                        "faultwood_sapling",
                        configuredFeatureKey("faultwood/sapling")
        );

        public static final RegistryObject<Block> FAULTWOOD_STAIRS = registerStairs("faultwood_stairs", FAULTWOOD_PLANKS);
        public static final RegistryObject<Block> FAULTWOOD_SLAB = registerSlab("faultwood_slab", FAULTWOOD_PLANKS);

    // --- Staple trees (all-biome) ---

    public static final RegistryObject<Block> ASHBLOOM_LOG = registerLog("ashbloom_log");
    public static final RegistryObject<Block> ASHBLOOM_PLANKS = registerPlanks("ashbloom_planks");
    public static final RegistryObject<Block> ASHBLOOM_LEAVES = BLOCKS.register(
            "ashbloom_leaves",
            () -> new AshbloomLeavesBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .strength(0.2F)
                    .randomTicks()
                    .noOcclusion()
                    .sound(SoundType.GRASS)
                    .lightLevel(s -> 3))
    );
    public static final RegistryObject<Block> ASHBLOOM_SAPLING = registerFeatureSapling(
            "ashbloom_sapling",
            configuredFeatureKey("ashbloom/sapling")
    );

    public static final RegistryObject<Block> ASHBLOOM_STAIRS = registerStairs("ashbloom_stairs", ASHBLOOM_PLANKS);
    public static final RegistryObject<Block> ASHBLOOM_SLAB = registerSlab("ashbloom_slab", ASHBLOOM_PLANKS);
    public static final RegistryObject<Block> ASHBLOOM_FENCE = registerFence("ashbloom_fence", ASHBLOOM_PLANKS);
    public static final RegistryObject<Block> ASHBLOOM_FENCE_GATE = registerFenceGate("ashbloom_fence_gate", ASHBLOOM_PLANKS);
    public static final RegistryObject<Block> ASHBLOOM_DOOR = registerDoor("ashbloom_door", ASHBLOOM_PLANKS);
    public static final RegistryObject<Block> ASHBLOOM_TRAPDOOR = registerTrapdoor("ashbloom_trapdoor", ASHBLOOM_PLANKS);
    public static final RegistryObject<Block> ASHBLOOM_BUTTON = registerButton("ashbloom_button", ASHBLOOM_PLANKS);
    public static final RegistryObject<Block> ASHBLOOM_PRESSURE_PLATE = registerPressurePlate("ashbloom_pressure_plate", ASHBLOOM_PLANKS);

    public static final RegistryObject<Block> GLIMMERPINE_LOG = registerLog("glimmerpine_log");
    public static final RegistryObject<Block> GLIMMERPINE_PLANKS = BLOCKS.register(
            "glimmerpine_planks",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)
                    .lightLevel(s -> 3))
    );
    public static final RegistryObject<Block> GLIMMERPINE_LEAVES = BLOCKS.register(
            "glimmerpine_leaves",
            () -> new GlimmerpineLeavesBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .strength(0.2F)
                    .randomTicks()
                    .noOcclusion()
                    .sound(SoundType.GRASS)
                    .lightLevel(s -> 2))
    );
    public static final RegistryObject<Block> GLIMMERPINE_SAPLING = registerFeatureSapling(
            "glimmerpine_sapling",
            configuredFeatureKey("glimmerpine/sapling")
    );

    public static final RegistryObject<Block> GLIMMERPINE_STAIRS = registerStairs("glimmerpine_stairs", GLIMMERPINE_PLANKS);
    public static final RegistryObject<Block> GLIMMERPINE_SLAB = registerSlab("glimmerpine_slab", GLIMMERPINE_PLANKS);
    public static final RegistryObject<Block> GLIMMERPINE_FENCE = registerFence("glimmerpine_fence", GLIMMERPINE_PLANKS);
    public static final RegistryObject<Block> GLIMMERPINE_FENCE_GATE = registerFenceGate("glimmerpine_fence_gate", GLIMMERPINE_PLANKS);
    public static final RegistryObject<Block> GLIMMERPINE_DOOR = registerDoor("glimmerpine_door", GLIMMERPINE_PLANKS);
    public static final RegistryObject<Block> GLIMMERPINE_TRAPDOOR = registerTrapdoor("glimmerpine_trapdoor", GLIMMERPINE_PLANKS);
    public static final RegistryObject<Block> GLIMMERPINE_BUTTON = registerButton("glimmerpine_button", GLIMMERPINE_PLANKS);
    public static final RegistryObject<Block> GLIMMERPINE_PRESSURE_PLATE = registerPressurePlate("glimmerpine_pressure_plate", GLIMMERPINE_PLANKS);

    public static final RegistryObject<Block> DRIFTWOOD_LOG = registerLog("driftwood_log");
    public static final RegistryObject<Block> DRIFTWOOD_PLANKS = registerPlanks("driftwood_planks");
    public static final RegistryObject<Block> DRIFTWOOD_LEAVES = BLOCKS.register(
            "driftwood_leaves",
            () -> new DriftwoodLeavesBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .strength(0.2F)
                    .randomTicks()
                    .noOcclusion()
                    .sound(SoundType.GRASS))
    );
    public static final RegistryObject<Block> DRIFTWOOD_SAPLING = registerFeatureSapling(
            "driftwood_sapling",
            configuredFeatureKey("driftwood/sapling")
    );

    public static final RegistryObject<Block> DRIFTWOOD_STAIRS = registerStairs("driftwood_stairs", DRIFTWOOD_PLANKS);
    public static final RegistryObject<Block> DRIFTWOOD_SLAB = registerSlab("driftwood_slab", DRIFTWOOD_PLANKS);
    public static final RegistryObject<Block> DRIFTWOOD_FENCE = registerFence("driftwood_fence", DRIFTWOOD_PLANKS);
    public static final RegistryObject<Block> DRIFTWOOD_FENCE_GATE = registerFenceGate("driftwood_fence_gate", DRIFTWOOD_PLANKS);
    public static final RegistryObject<Block> DRIFTWOOD_DOOR = registerDoor("driftwood_door", DRIFTWOOD_PLANKS);
    public static final RegistryObject<Block> DRIFTWOOD_TRAPDOOR = registerTrapdoor("driftwood_trapdoor", DRIFTWOOD_PLANKS);
    public static final RegistryObject<Block> DRIFTWOOD_BUTTON = registerButton("driftwood_button", DRIFTWOOD_PLANKS);
    public static final RegistryObject<Block> DRIFTWOOD_PRESSURE_PLATE = registerPressurePlate("driftwood_pressure_plate", DRIFTWOOD_PLANKS);
        public static final RegistryObject<Block> FAULTWOOD_FENCE = registerFence("faultwood_fence", FAULTWOOD_PLANKS);
        public static final RegistryObject<Block> FAULTWOOD_FENCE_GATE = registerFenceGate("faultwood_fence_gate", FAULTWOOD_PLANKS);
        public static final RegistryObject<Block> FAULTWOOD_DOOR = registerDoor("faultwood_door", FAULTWOOD_PLANKS);
        public static final RegistryObject<Block> FAULTWOOD_TRAPDOOR = registerTrapdoor("faultwood_trapdoor", FAULTWOOD_PLANKS);
        public static final RegistryObject<Block> FAULTWOOD_BUTTON = registerButton("faultwood_button", FAULTWOOD_PLANKS);
        public static final RegistryObject<Block> FAULTWOOD_PRESSURE_PLATE = registerPressurePlate("faultwood_pressure_plate", FAULTWOOD_PLANKS);

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
