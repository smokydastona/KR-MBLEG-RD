package com.kruemblegard.init;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.block.AncientWaystoneBlock;
import com.kruemblegard.block.BerryBushBlock;
import com.kruemblegard.block.EchocapBlock;
import com.kruemblegard.block.BonemealableWayfallFungusBlock;
import com.kruemblegard.block.GravevineBlock;
import com.kruemblegard.block.FranchFenceBlock;
import com.kruemblegard.block.FranchFenceGateBlock;
import com.kruemblegard.block.FranchPlanksBlock;
import com.kruemblegard.block.FranchSlabBlock;
import com.kruemblegard.block.FranchStairBlock;
import com.kruemblegard.block.FranchStringBlock;
import com.kruemblegard.block.FranchTrapDoorBlock;
import com.kruemblegard.block.PyrokelpHeadBlock;
import com.kruemblegard.block.PyrokelpPlantBlock;
import com.kruemblegard.block.GlimmerpineLeavesBlock;
import com.kruemblegard.block.AshbloomLeavesBlock;
import com.kruemblegard.block.DriftwoodLeavesBlock;
import com.kruemblegard.block.AshmossBlock;
import com.kruemblegard.block.RunedStoneveilRubbleBlock;
import com.kruemblegard.block.RunegrowthBlock;
import com.kruemblegard.block.RunegrowthVariantBlock;
import com.kruemblegard.block.RubbleTilthBlock;
import com.kruemblegard.block.ScarstoneBlock;
import com.kruemblegard.block.VoidfeltBlock;
import com.kruemblegard.block.PaleweftGrassBlock;
import com.kruemblegard.block.PaleweftCornCropBlock;
import com.kruemblegard.block.WayfallReactivePlantBlock;
import com.kruemblegard.block.WayfallPortalBlock;
import com.kruemblegard.block.AshveilBlock;
import com.kruemblegard.block.RunebloomBlock;
import com.kruemblegard.block.SoulberryShrubBlock;
import com.kruemblegard.block.StrippableRotatedPillarBlock;
import com.kruemblegard.block.UndersideParticleHugeMushroomBlock;
import com.kruemblegard.block.WayfallFeatureSaplingBlock;
import com.kruemblegard.block.WayfallPlantBlock;
import com.kruemblegard.block.WaylilyBlock;
import com.kruemblegard.block.WispstalkBlock;
import com.kruemblegard.registry.ModParticles;
import com.kruemblegard.world.grower.TwoByTwoConfiguredFeatureTreeGrower;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
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

    public static final RegistryObject<Block> ATTUNED_STONE_STAIRS = BLOCKS.register(
            "attuned_stone_stairs",
            () -> new StairBlock(() -> ATTUNED_STONE.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(3.0F, 30.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> ATTUNED_STONE_SLAB = BLOCKS.register(
            "attuned_stone_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(3.0F, 30.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> ATTUNED_STONE_WALL = BLOCKS.register(
            "attuned_stone_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of()
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

    public static final RegistryObject<Block> WAYFALL_PORTAL = BLOCKS.register(
            "wayfall_portal",
            () -> new WayfallPortalBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .noCollission()
                    .strength(2.0F, 18.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> FRACTURED_WAYROCK_STAIRS = BLOCKS.register(
            "fractured_wayrock_stairs",
            () -> new StairBlock(() -> FRACTURED_WAYROCK.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(2.0F, 18.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> FRACTURED_WAYROCK_SLAB = BLOCKS.register(
            "fractured_wayrock_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(2.0F, 18.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> FRACTURED_WAYROCK_WALL = BLOCKS.register(
            "fractured_wayrock_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of()
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

    public static final RegistryObject<Block> CRUSHSTONE_STAIRS = BLOCKS.register(
            "crushstone_stairs",
            () -> new StairBlock(() -> CRUSHSTONE.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(1.2F, 6.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> CRUSHSTONE_SLAB = BLOCKS.register(
            "crushstone_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(1.2F, 6.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> CRUSHSTONE_WALL = BLOCKS.register(
            "crushstone_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(1.2F, 6.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> ASHFALL_LOAM = BLOCKS.register(
            "ashfall_loam",
            () -> new FallingBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIRT)
                    .strength(0.6F, 0.6F)
                    .sound(SoundType.SAND))
    );

    public static final RegistryObject<Block> ASHFALL_STONE = BLOCKS.register(
            "ashfall_stone",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .strength(1.2F, 6.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> ASHFALL_STONE_STAIRS = BLOCKS.register(
            "ashfall_stone_stairs",
            () -> new StairBlock(() -> ASHFALL_STONE.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .strength(1.2F, 6.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> ASHFALL_STONE_SLAB = BLOCKS.register(
            "ashfall_stone_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .strength(1.2F, 6.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> ASHFALL_STONE_WALL = BLOCKS.register(
            "ashfall_stone_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .strength(1.2F, 6.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> POLISHED_ASHFALL_STONE = BLOCKS.register(
            "polished_ashfall_stone",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .strength(1.5F, 6.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> POLISHED_ASHFALL_STONE_STAIRS = BLOCKS.register(
            "polished_ashfall_stone_stairs",
            () -> new StairBlock(
                    () -> POLISHED_ASHFALL_STONE.get().defaultBlockState(),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.SAND)
                            .strength(1.5F, 6.0F)
                            .sound(SoundType.STONE)
            )
    );

    public static final RegistryObject<Block> POLISHED_ASHFALL_STONE_SLAB = BLOCKS.register(
            "polished_ashfall_stone_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .strength(1.5F, 6.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> POLISHED_ASHFALL_STONE_WALL = BLOCKS.register(
            "polished_ashfall_stone_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .strength(1.5F, 6.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> FAULT_DUST = BLOCKS.register(
            "fault_dust",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .strength(0.5F, 0.5F)
                    .sound(SoundType.ROOTED_DIRT))
    );

    public static final RegistryObject<Block> CRACKED_SCARSTONE = BLOCKS.register(
            "cracked_scarstone",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DEEPSLATE)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 36.0F)
                    .sound(SoundType.DEEPSLATE))
    );

    public static final RegistryObject<Block> CRACKED_SCARSTONE_STAIRS = BLOCKS.register(
            "cracked_scarstone_stairs",
            () -> new StairBlock(() -> CRACKED_SCARSTONE.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DEEPSLATE)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 36.0F)
                    .sound(SoundType.DEEPSLATE))
    );

    public static final RegistryObject<Block> CRACKED_SCARSTONE_SLAB = BLOCKS.register(
            "cracked_scarstone_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DEEPSLATE)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 36.0F)
                    .sound(SoundType.DEEPSLATE))
    );

    public static final RegistryObject<Block> CRACKED_SCARSTONE_WALL = BLOCKS.register(
            "cracked_scarstone_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of()
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

    public static final RegistryObject<Block> SCARSTONE_STAIRS = BLOCKS.register(
            "scarstone_stairs",
            () -> new StairBlock(() -> SCARSTONE.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DEEPSLATE)
                    .requiresCorrectToolForDrops()
                    .strength(3.5F, 42.0F)
                    .sound(SoundType.DEEPSLATE))
    );

    public static final RegistryObject<Block> SCARSTONE_SLAB = BLOCKS.register(
            "scarstone_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DEEPSLATE)
                    .requiresCorrectToolForDrops()
                    .strength(3.5F, 42.0F)
                    .sound(SoundType.DEEPSLATE))
    );

    public static final RegistryObject<Block> SCARSTONE_WALL = BLOCKS.register(
            "scarstone_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of()
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

    public static final RegistryObject<Block> POLISHED_SCARSTONE_STAIRS = BLOCKS.register(
            "polished_scarstone_stairs",
            () -> new StairBlock(() -> POLISHED_SCARSTONE.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DEEPSLATE)
                    .requiresCorrectToolForDrops()
                    .strength(4.0F, 50.0F)
                    .sound(SoundType.DEEPSLATE))
    );

    public static final RegistryObject<Block> POLISHED_SCARSTONE_SLAB = BLOCKS.register(
            "polished_scarstone_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DEEPSLATE)
                    .requiresCorrectToolForDrops()
                    .strength(4.0F, 50.0F)
                    .sound(SoundType.DEEPSLATE))
    );

    public static final RegistryObject<Block> POLISHED_SCARSTONE_WALL = BLOCKS.register(
            "polished_scarstone_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of()
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

    public static final RegistryObject<Block> STONEVEIL_RUBBLE_STAIRS = BLOCKS.register(
            "stoneveil_rubble_stairs",
            () -> new StairBlock(() -> STONEVEIL_RUBBLE.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(2.2F, 18.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> STONEVEIL_RUBBLE_SLAB = BLOCKS.register(
            "stoneveil_rubble_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(2.2F, 18.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> STONEVEIL_RUBBLE_WALL = BLOCKS.register(
            "stoneveil_rubble_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of()
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

    public static final RegistryObject<Block> POLISHED_STONEVEIL_RUBBLE_STAIRS = BLOCKS.register(
            "polished_stoneveil_rubble_stairs",
            () -> new StairBlock(() -> POLISHED_STONEVEIL_RUBBLE.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(2.6F, 20.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> POLISHED_STONEVEIL_RUBBLE_SLAB = BLOCKS.register(
            "polished_stoneveil_rubble_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(2.6F, 20.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> POLISHED_STONEVEIL_RUBBLE_WALL = BLOCKS.register(
            "polished_stoneveil_rubble_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of()
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

    public static final RegistryObject<Block> RUNED_STONEVEIL_RUBBLE_STAIRS = BLOCKS.register(
            "runed_stoneveil_rubble_stairs",
            () -> new StairBlock(() -> RUNED_STONEVEIL_RUBBLE.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(2.8F, 22.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> RUNED_STONEVEIL_RUBBLE_SLAB = BLOCKS.register(
            "runed_stoneveil_rubble_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(2.8F, 22.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> RUNED_STONEVEIL_RUBBLE_WALL = BLOCKS.register(
            "runed_stoneveil_rubble_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(2.8F, 22.0F)
                    .sound(SoundType.STONE))
    );

    // --- Wayfall surface covers (spread / conversion) ---

    public static final RegistryObject<Block> ASHMOSS = BLOCKS.register(
            "ashmoss",
            () -> new AshmossBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(0.6F, 0.6F)
                    .sound(SoundType.MOSS))
    );

    public static final RegistryObject<Block> ASHMOSS_CARPET = BLOCKS.register(
            "ashmoss_carpet",
            () -> new CarpetBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(0.1F)
                    .sound(SoundType.MOSS_CARPET))
    );

    public static final RegistryObject<Block> RUNEGROWTH = BLOCKS.register(
            "runegrowth",
            () -> new RunegrowthBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLUE)
                    .strength(0.6F, 0.6F)
                    .sound(SoundType.GRASS))
    );

    public static final RegistryObject<Block> FROSTBOUND_RUNEGROWTH = BLOCKS.register(
            "frostbound_runegrowth",
            () -> new RunegrowthVariantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.ICE)
                    .strength(0.6F, 0.6F)
                    .sound(SoundType.GRASS))
    );

    public static final RegistryObject<Block> VERDANT_RUNEGROWTH = BLOCKS.register(
            "verdant_runegrowth",
            () -> new RunegrowthVariantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(0.6F, 0.6F)
                    .sound(SoundType.GRASS))
    );

    public static final RegistryObject<Block> EMBERWARMED_RUNEGROWTH = BLOCKS.register(
            "emberwarmed_runegrowth",
            () -> new RunegrowthVariantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.FIRE)
                    .strength(0.6F, 0.6F)
                    .sound(SoundType.GRASS))
    );

    public static final RegistryObject<Block> VOIDFELT = BLOCKS.register(
            "voidfelt",
            () -> new VoidfeltBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(0.6F, 0.6F)
                    .sound(SoundType.ROOTED_DIRT))
    );

    public static final RegistryObject<Block> RUBBLE_TILTH = BLOCKS.register(
            "rubble_tilth",
            () -> new RubbleTilthBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIRT)
                    .strength(0.6F, 0.6F)
                    .sound(SoundType.GRAVEL)
                    .randomTicks())
    );

    public static final RegistryObject<Block> RUNIC_DEBRIS = BLOCKS.register(
            "runic_debris",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 60.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> WAYFALL_IRON_ORE = BLOCKS.register(
            "wayfall_iron_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 30.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> WAYFALL_COPPER_ORE = BLOCKS.register(
            "wayfall_copper_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 30.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> WAYFALL_DIAMOND_ORE = BLOCKS.register(
            "wayfall_diamond_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(4.0F, 30.0F)
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
                    .noOcclusion()
                    .instabreak()
                    .sound(SoundType.VINE)
                    .randomTicks())
    );

    public static final RegistryObject<Block> PYROKELP = BLOCKS.register(
            "pyrokelp",
            () -> new PyrokelpHeadBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.VINE)
                    .randomTicks())
    );

    public static final RegistryObject<Block> PYROKELP_PLANT = BLOCKS.register(
            "pyrokelp_plant",
            () -> new PyrokelpPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.VINE))
    );

    public static final RegistryObject<Block> ECHOCAP = BLOCKS.register(
            "echocap",
            () -> new EchocapBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.FUNGUS),
                    java.util.List.of(
                            new ResourceLocation(Kruemblegard.MODID, "giant_echocap_1"),
                            new ResourceLocation(Kruemblegard.MODID, "giant_echocap_2")
                    ))
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
                    .sound(SoundType.GRASS))
    );

    public static final RegistryObject<Block> FAULTGRASS = BLOCKS.register(
            "faultgrass",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS))
    );

    public static final RegistryObject<Block> DRIFTBLOOM = BLOCKS.register(
            "driftbloom",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PINK)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS))
    );

    public static final RegistryObject<Block> CAIRN_MOSS = BLOCKS.register(
            "cairn_moss",
            () -> new WayfallPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.MOSS_CARPET))
    );

    public static final RegistryObject<Block> WAYLILY = BLOCKS.register(
            "waylily",
            () -> new WaylilyBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLUE)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.LILY_PAD))
    );

    public static final RegistryObject<Block> GRIEFCAP = BLOCKS.register(
            "griefcap",
            () -> new BonemealableWayfallFungusBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.FUNGUS),
                    java.util.List.of(
                            new ResourceLocation(Kruemblegard.MODID, "giant_griefcap_1"),
                            new ResourceLocation(Kruemblegard.MODID, "giant_griefcap_2")
                    ))
    );

    public static final RegistryObject<Block> STATIC_FUNGUS = BLOCKS.register(
            "static_fungus",
            () -> new BonemealableWayfallFungusBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.FUNGUS),
                    java.util.List.of(
                            new ResourceLocation(Kruemblegard.MODID, "giant_static_fungus_1"),
                            new ResourceLocation(Kruemblegard.MODID, "giant_static_fungus_2")
                    ))
    );

    public static final RegistryObject<Block> WAYROT_FUNGUS = BLOCKS.register(
            "wayrot_fungus",
            () -> new BonemealableWayfallFungusBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.FUNGUS),
                    java.util.List.of(
                            new ResourceLocation(Kruemblegard.MODID, "giant_wayrot_fungus_1"),
                            new ResourceLocation(Kruemblegard.MODID, "giant_wayrot_fungus_2")
                    ))
    );

    // --- Paleweft flora / crops ---

    public static final RegistryObject<Block> PALEWEFT_GRASS = BLOCKS.register(
            "paleweft_grass",
            () -> new PaleweftGrassBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS))
    );

    public static final RegistryObject<Block> PALEWEFT_TALL_GRASS = BLOCKS.register(
            "paleweft_tall_grass",
            () -> new DoublePlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS))
    );

    public static final RegistryObject<Block> PALEWEFT_CORN = BLOCKS.register(
            "paleweft_corn",
            () -> new PaleweftCornCropBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.CROP)
                    .randomTicks())
    );

    public static final RegistryObject<Block> ECHO_PUFF = BLOCKS.register(
            "echo_puff",
            () -> new BonemealableWayfallFungusBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .noCollission()
                    .instabreak()
                    .lightLevel(state -> 3)
                    .sound(SoundType.FUNGUS)
                    .randomTicks(),
                    java.util.List.of(
                            new ResourceLocation(Kruemblegard.MODID, "giant_echo_puff_1"),
                            new ResourceLocation(Kruemblegard.MODID, "giant_echo_puff_2")
                    ))
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
            () -> new BonemealableWayfallFungusBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.FUNGUS)
                    .randomTicks(),
                    java.util.List.of(
                            new ResourceLocation(Kruemblegard.MODID, "giant_black_echo_fungus_1"),
                            new ResourceLocation(Kruemblegard.MODID, "giant_black_echo_fungus_2")
                    ))
    );

    public static final RegistryObject<Block> WAYBURN_FUNGUS = BLOCKS.register(
            "wayburn_fungus",
            () -> new BonemealableWayfallFungusBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .noCollission()
                    .instabreak()
                    .lightLevel(state -> 3)
                    .sound(SoundType.FUNGUS)
                    .randomTicks(),
                    java.util.List.of(
                            new ResourceLocation(Kruemblegard.MODID, "giant_wayburn_fungus_1"),
                            new ResourceLocation(Kruemblegard.MODID, "giant_wayburn_fungus_2")
                    ))
    );

    public static final RegistryObject<Block> MEMORY_ROT = BLOCKS.register(
            "memory_rot",
            () -> new BonemealableWayfallFungusBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_RED)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.SLIME_BLOCK)
                    .randomTicks(),
                    java.util.List.of(
                            new ResourceLocation(Kruemblegard.MODID, "giant_memory_rot_1"),
                            new ResourceLocation(Kruemblegard.MODID, "giant_memory_rot_2")
                    ))
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
            () -> new BonemealableWayfallFungusBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.FUNGUS)
                    .randomTicks(),
                    java.util.List.of(
                            new ResourceLocation(Kruemblegard.MODID, "giant_voidcap_briar_1"),
                            new ResourceLocation(Kruemblegard.MODID, "giant_voidcap_briar_2")
                    ))
    );

    // --- Giant fungi (stems + caps) ---

    private static BlockBehaviour.Properties giantFungusProps(MapColor mapColor) {
        return BlockBehaviour.Properties.of()
                .mapColor(mapColor)
                .strength(0.2F)
                .sound(SoundType.WOOD);
    }

        private static BlockBehaviour.Properties giantFungusCapProps() {
                // Compatibility: Serilum's Tree Harvester recognizes huge mushroom caps by HugeMushroomBlock + MapColor.DIRT/COLOR_RED.
                return giantFungusProps(MapColor.DIRT);
        }

        private static BlockBehaviour.Properties giantFungusStemProps() {
                // Compatibility: Serilum's Tree Harvester recognizes huge mushroom stems by HugeMushroomBlock + MapColor.WOOL.
                return giantFungusProps(MapColor.WOOL);
        }

    public static final RegistryObject<Block> GIANT_BLACK_ECHO_FUNGUS_CAP = BLOCKS.register(
            "giant_black_echo_fungus_cap",
            () -> new HugeMushroomBlock(giantFungusCapProps())
    );
    public static final RegistryObject<Block> GIANT_BLACK_ECHO_FUNGUS_CAP_SLAB = BLOCKS.register(
            "giant_black_echo_fungus_cap_slab",
            () -> new SlabBlock(giantFungusCapProps())
    );
    public static final RegistryObject<Block> GIANT_BLACK_ECHO_FUNGUS_STEM = BLOCKS.register(
            "giant_black_echo_fungus_stem",
            () -> new HugeMushroomBlock(giantFungusStemProps())
    );

    public static final RegistryObject<Block> GIANT_ECHOCAP_CAP = BLOCKS.register(
            "giant_echocap_cap",
            () -> new HugeMushroomBlock(giantFungusCapProps())
    );
    public static final RegistryObject<Block> GIANT_ECHOCAP_CAP_SLAB = BLOCKS.register(
            "giant_echocap_cap_slab",
            () -> new SlabBlock(giantFungusCapProps())
    );
    public static final RegistryObject<Block> GIANT_ECHOCAP_STEM = BLOCKS.register(
            "giant_echocap_stem",
            () -> new HugeMushroomBlock(giantFungusStemProps())
    );

    public static final RegistryObject<Block> GIANT_ECHO_PUFF_CAP = BLOCKS.register(
            "giant_echo_puff_cap",
            () -> new HugeMushroomBlock(giantFungusCapProps()
                    .lightLevel(state -> {
                        // Light only when an "inside" face can exist (i.e. at least one face is NOT cap).
                        // Note: we intentionally ignore DOWN to avoid "always lit" due to underside rules.
                        boolean hasAnyInsideFace =
                                !state.getValue(HugeMushroomBlock.UP)
                                        || !state.getValue(HugeMushroomBlock.NORTH)
                                        || !state.getValue(HugeMushroomBlock.SOUTH)
                                        || !state.getValue(HugeMushroomBlock.EAST)
                                        || !state.getValue(HugeMushroomBlock.WEST);
                        return hasAnyInsideFace ? 4 : 0;
                    }))
    );
    public static final RegistryObject<Block> GIANT_ECHO_PUFF_CAP_SLAB = BLOCKS.register(
            "giant_echo_puff_cap_slab",
            () -> new SlabBlock(giantFungusCapProps())
    );
    public static final RegistryObject<Block> GIANT_ECHO_PUFF_STEM = BLOCKS.register(
            "giant_echo_puff_stem",
            () -> new HugeMushroomBlock(giantFungusStemProps())
    );

    public static final RegistryObject<Block> GIANT_GRIEFCAP_CAP = BLOCKS.register(
            "giant_griefcap_cap",
            () -> new UndersideParticleHugeMushroomBlock(
                    giantFungusCapProps(),
                    ParticleTypes.DRIPPING_WATER,
                    8
            )
    );
    public static final RegistryObject<Block> GIANT_GRIEFCAP_CAP_SLAB = BLOCKS.register(
            "giant_griefcap_cap_slab",
            () -> new SlabBlock(giantFungusCapProps())
    );
    public static final RegistryObject<Block> GIANT_GRIEFCAP_STEM = BLOCKS.register(
            "giant_griefcap_stem",
            () -> new HugeMushroomBlock(giantFungusStemProps())
    );

    public static final RegistryObject<Block> GIANT_STATIC_FUNGUS_CAP = BLOCKS.register(
            "giant_static_fungus_cap",
            () -> new HugeMushroomBlock(giantFungusCapProps())
    );
    public static final RegistryObject<Block> GIANT_STATIC_FUNGUS_CAP_SLAB = BLOCKS.register(
            "giant_static_fungus_cap_slab",
            () -> new SlabBlock(giantFungusCapProps())
    );
    public static final RegistryObject<Block> GIANT_STATIC_FUNGUS_STEM = BLOCKS.register(
            "giant_static_fungus_stem",
            () -> new HugeMushroomBlock(giantFungusStemProps())
    );

    public static final RegistryObject<Block> GIANT_VOIDCAP_BRIAR_CAP = BLOCKS.register(
            "giant_voidcap_briar_cap",
            () -> new HugeMushroomBlock(giantFungusCapProps())
    );
    public static final RegistryObject<Block> GIANT_VOIDCAP_BRIAR_CAP_SLAB = BLOCKS.register(
            "giant_voidcap_briar_cap_slab",
            () -> new SlabBlock(giantFungusCapProps())
    );
    public static final RegistryObject<Block> GIANT_VOIDCAP_BRIAR_STEM = BLOCKS.register(
            "giant_voidcap_briar_stem",
            () -> new HugeMushroomBlock(giantFungusStemProps())
    );

    public static final RegistryObject<Block> GIANT_WAYBURN_FUNGUS_CAP = BLOCKS.register(
            "giant_wayburn_fungus_cap",
            () -> new UndersideParticleHugeMushroomBlock(giantFungusCapProps()
                    .lightLevel(state -> {
                        // Light only when an "inside" face can exist (i.e. at least one face is NOT cap).
                        // Note: we intentionally ignore DOWN to avoid "always lit" due to underside rules.
                        boolean hasAnyInsideFace =
                                !state.getValue(HugeMushroomBlock.UP)
                                        || !state.getValue(HugeMushroomBlock.NORTH)
                                        || !state.getValue(HugeMushroomBlock.SOUTH)
                                        || !state.getValue(HugeMushroomBlock.EAST)
                                        || !state.getValue(HugeMushroomBlock.WEST);
                        return hasAnyInsideFace ? 4 : 0;
                    }),
                    ParticleTypes.DRIPPING_LAVA,
                    8
            )
    );
    public static final RegistryObject<Block> GIANT_WAYBURN_FUNGUS_CAP_SLAB = BLOCKS.register(
            "giant_wayburn_fungus_cap_slab",
            () -> new SlabBlock(giantFungusCapProps())
    );
    public static final RegistryObject<Block> GIANT_WAYBURN_FUNGUS_STEM = BLOCKS.register(
            "giant_wayburn_fungus_stem",
            () -> new HugeMushroomBlock(giantFungusStemProps())
    );

    public static final RegistryObject<Block> GIANT_WAYROT_FUNGUS_CAP = BLOCKS.register(
            "giant_wayrot_fungus_cap",
            () -> new UndersideParticleHugeMushroomBlock(
                    giantFungusCapProps(),
                    ParticleTypes.SPORE_BLOSSOM_AIR,
                    6
            )
    );
    public static final RegistryObject<Block> GIANT_WAYROT_FUNGUS_CAP_SLAB = BLOCKS.register(
            "giant_wayrot_fungus_cap_slab",
            () -> new SlabBlock(giantFungusCapProps())
    );
    public static final RegistryObject<Block> GIANT_WAYROT_FUNGUS_STEM = BLOCKS.register(
            "giant_wayrot_fungus_stem",
            () -> new HugeMushroomBlock(giantFungusStemProps())
    );

    public static final RegistryObject<Block> GIANT_MEMORY_ROT_CAP = BLOCKS.register(
            "giant_memory_rot_cap",
            () -> new HugeMushroomBlock(giantFungusCapProps())
    );
    public static final RegistryObject<Block> GIANT_MEMORY_ROT_CAP_SLAB = BLOCKS.register(
            "giant_memory_rot_cap_slab",
            () -> new SlabBlock(giantFungusCapProps())
    );
    public static final RegistryObject<Block> GIANT_MEMORY_ROT_STEM = BLOCKS.register(
            "giant_memory_rot_stem",
            () -> new HugeMushroomBlock(giantFungusStemProps())
    );

    // --- Vanilla mushroom blocks (slab variants; used by schematic-based huge mushrooms) ---

    public static final RegistryObject<Block> RED_MUSHROOM_BLOCK_SLAB = BLOCKS.register(
            "red_mushroom_block_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.RED_MUSHROOM_BLOCK))
    );

    public static final RegistryObject<Block> BROWN_MUSHROOM_BLOCK_SLAB = BLOCKS.register(
            "brown_mushroom_block_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.BROWN_MUSHROOM_BLOCK))
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
                    () -> ParticleTypes.REVERSE_PORTAL,
                    () -> ParticleTypes.REVERSE_PORTAL,
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
                    () -> ParticleTypes.ENCHANT,
                    ModParticles.ARCANE_SPARK,
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
                    () -> ParticleTypes.END_ROD,
                    () -> ParticleTypes.END_ROD,
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
                            .lightLevel(s -> 8)
                            .randomTicks(),
                    () -> ParticleTypes.WITCH,
                    ModParticles.ARCANE_SPARK,
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
                    () -> ParticleTypes.SMOKE,
                    ModParticles.ARCANE_SPARK,
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

        private static RegistryObject<Block> registerStrippableLog(String id, Supplier<? extends Block> stripped) {
                return BLOCKS.register(id, () -> new StrippableRotatedPillarBlock(stripped, BlockBehaviour.Properties.of()
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
                return BLOCKS.register(id, () -> new com.kruemblegard.block.KruemblegardLeavesBlock(BlockBehaviour.Properties.of()
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

        private static RegistryObject<Block> registerFranch(String id, RegistryObject<Block> planks) {
                return BLOCKS.register(id, () -> new FranchFenceBlock(woodFamilyProperties().randomTicks()));
        }

        private static RegistryObject<Block> registerFranchGate(String id, RegistryObject<Block> planks) {
                return BLOCKS.register(id, () -> new FranchFenceGateBlock(woodFamilyProperties().randomTicks(), WoodType.OAK));
        }

        private static RegistryObject<Block> registerFranchPlanks(String id, RegistryObject<Block> planks) {
                return BLOCKS.register(id,
                                () -> new FranchPlanksBlock(BlockBehaviour.Properties.copy(planks.get()).randomTicks()));
        }

        private static RegistryObject<Block> registerFranchStairs(String id, RegistryObject<Block> planks) {
                return BLOCKS.register(id,
                                () -> new FranchStairBlock(() -> planks.get().defaultBlockState(), woodFamilyProperties().randomTicks()));
        }

        private static RegistryObject<Block> registerFranchSlab(String id, RegistryObject<Block> planks) {
                return BLOCKS.register(id, () -> new FranchSlabBlock(woodFamilyProperties().randomTicks()));
        }

        private static RegistryObject<Block> registerFranchTrapdoor(String id, RegistryObject<Block> planks) {
                return BLOCKS.register(id,
                                () -> new FranchTrapDoorBlock(woodFamilyProperties().noOcclusion().randomTicks(), BlockSetType.OAK));
        }

        private static RegistryObject<Block> registerVanillaFranch(String id, Block copyFrom) {
                return BLOCKS.register(id,
                                () -> new FranchFenceBlock(BlockBehaviour.Properties.copy(copyFrom).randomTicks()));
        }

        private static RegistryObject<Block> registerVanillaFranchGate(String id, Block copyFrom, WoodType woodType) {
                return BLOCKS.register(id,
                                () -> new FranchFenceGateBlock(BlockBehaviour.Properties.copy(copyFrom).randomTicks(), woodType));
        }

        private static RegistryObject<Block> registerVanillaFranchPlanks(String id, Block copyFrom) {
                return BLOCKS.register(id,
                                () -> new FranchPlanksBlock(BlockBehaviour.Properties.copy(copyFrom).randomTicks()));
        }

        private static RegistryObject<Block> registerVanillaFranchStairs(String id, Block copyFromStairs, Block copyFromPlanks) {
                return BLOCKS.register(id,
                                () -> new FranchStairBlock(copyFromPlanks::defaultBlockState,
                                                BlockBehaviour.Properties.copy(copyFromStairs).randomTicks()));
        }

        private static RegistryObject<Block> registerVanillaFranchSlab(String id, Block copyFromSlab) {
                return BLOCKS.register(id,
                                () -> new FranchSlabBlock(BlockBehaviour.Properties.copy(copyFromSlab).randomTicks()));
        }

        private static RegistryObject<Block> registerVanillaFranchTrapdoor(String id, Block copyFromTrapdoor, BlockSetType blockSetType) {
                return BLOCKS.register(id,
                                () -> new FranchTrapDoorBlock(BlockBehaviour.Properties.copy(copyFromTrapdoor).randomTicks(), blockSetType));
        }

        private static RegistryObject<Block> registerStringFranch(String id) {
                return BLOCKS.register(id,
                                () -> new FranchStringBlock(BlockBehaviour.Properties.of()
                                                .mapColor(MapColor.COLOR_LIGHT_GRAY)
                                                .strength(0.0F)
                                                .noOcclusion()
                                                .randomTicks()
                                                .sound(SoundType.WOOL)));
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

        private static RegistryObject<Block> registerFeatureSapling2x2(String id,
                                                                       ResourceKey<ConfiguredFeature<?, ?>> smallFeatureKey,
                                                                       ResourceKey<ConfiguredFeature<?, ?>> megaFeatureKey) {
                return BLOCKS.register(id, () -> new WayfallFeatureSaplingBlock(
                                new TwoByTwoConfiguredFeatureTreeGrower(smallFeatureKey, megaFeatureKey),
                                BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.OAK_SAPLING)
                ));
        }

        private static ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureKey(String path) {
                return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(Kruemblegard.MODID, path));
        }

                // Vanilla-wood schematic-only "Franch" fences and gates.
                public static final RegistryObject<Block> OAK_FRANCH = registerVanillaFranch("oak_franch", net.minecraft.world.level.block.Blocks.OAK_FENCE);
                public static final RegistryObject<Block> OAK_FRANCH_GATE = registerVanillaFranchGate("oak_franch_gate", net.minecraft.world.level.block.Blocks.OAK_FENCE_GATE, WoodType.OAK);
                public static final RegistryObject<Block> OAK_FRANCH_PLANKS = registerVanillaFranchPlanks("oak_franch_planks", net.minecraft.world.level.block.Blocks.OAK_PLANKS);
                public static final RegistryObject<Block> OAK_FRANCH_STAIRS = registerVanillaFranchStairs("oak_franch_stairs", net.minecraft.world.level.block.Blocks.OAK_STAIRS, net.minecraft.world.level.block.Blocks.OAK_PLANKS);
                public static final RegistryObject<Block> OAK_FRANCH_SLAB = registerVanillaFranchSlab("oak_franch_slab", net.minecraft.world.level.block.Blocks.OAK_SLAB);
                public static final RegistryObject<Block> OAK_FRANCH_TRAPDOOR = registerVanillaFranchTrapdoor("oak_franch_trapdoor", net.minecraft.world.level.block.Blocks.OAK_TRAPDOOR, BlockSetType.OAK);
                public static final RegistryObject<Block> SPRUCE_FRANCH = registerVanillaFranch("spruce_franch", net.minecraft.world.level.block.Blocks.SPRUCE_FENCE);
                public static final RegistryObject<Block> SPRUCE_FRANCH_GATE = registerVanillaFranchGate("spruce_franch_gate", net.minecraft.world.level.block.Blocks.SPRUCE_FENCE_GATE, WoodType.SPRUCE);
                public static final RegistryObject<Block> SPRUCE_FRANCH_PLANKS = registerVanillaFranchPlanks("spruce_franch_planks", net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS);
                public static final RegistryObject<Block> SPRUCE_FRANCH_STAIRS = registerVanillaFranchStairs("spruce_franch_stairs", net.minecraft.world.level.block.Blocks.SPRUCE_STAIRS, net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS);
                public static final RegistryObject<Block> SPRUCE_FRANCH_SLAB = registerVanillaFranchSlab("spruce_franch_slab", net.minecraft.world.level.block.Blocks.SPRUCE_SLAB);
                public static final RegistryObject<Block> SPRUCE_FRANCH_TRAPDOOR = registerVanillaFranchTrapdoor("spruce_franch_trapdoor", net.minecraft.world.level.block.Blocks.SPRUCE_TRAPDOOR, BlockSetType.SPRUCE);
                public static final RegistryObject<Block> BIRCH_FRANCH = registerVanillaFranch("birch_franch", net.minecraft.world.level.block.Blocks.BIRCH_FENCE);
                public static final RegistryObject<Block> BIRCH_FRANCH_GATE = registerVanillaFranchGate("birch_franch_gate", net.minecraft.world.level.block.Blocks.BIRCH_FENCE_GATE, WoodType.BIRCH);
                public static final RegistryObject<Block> BIRCH_FRANCH_PLANKS = registerVanillaFranchPlanks("birch_franch_planks", net.minecraft.world.level.block.Blocks.BIRCH_PLANKS);
                public static final RegistryObject<Block> BIRCH_FRANCH_STAIRS = registerVanillaFranchStairs("birch_franch_stairs", net.minecraft.world.level.block.Blocks.BIRCH_STAIRS, net.minecraft.world.level.block.Blocks.BIRCH_PLANKS);
                public static final RegistryObject<Block> BIRCH_FRANCH_SLAB = registerVanillaFranchSlab("birch_franch_slab", net.minecraft.world.level.block.Blocks.BIRCH_SLAB);
                public static final RegistryObject<Block> BIRCH_FRANCH_TRAPDOOR = registerVanillaFranchTrapdoor("birch_franch_trapdoor", net.minecraft.world.level.block.Blocks.BIRCH_TRAPDOOR, BlockSetType.BIRCH);
                public static final RegistryObject<Block> JUNGLE_FRANCH = registerVanillaFranch("jungle_franch", net.minecraft.world.level.block.Blocks.JUNGLE_FENCE);
                public static final RegistryObject<Block> JUNGLE_FRANCH_GATE = registerVanillaFranchGate("jungle_franch_gate", net.minecraft.world.level.block.Blocks.JUNGLE_FENCE_GATE, WoodType.JUNGLE);
                public static final RegistryObject<Block> JUNGLE_FRANCH_PLANKS = registerVanillaFranchPlanks("jungle_franch_planks", net.minecraft.world.level.block.Blocks.JUNGLE_PLANKS);
                public static final RegistryObject<Block> JUNGLE_FRANCH_STAIRS = registerVanillaFranchStairs("jungle_franch_stairs", net.minecraft.world.level.block.Blocks.JUNGLE_STAIRS, net.minecraft.world.level.block.Blocks.JUNGLE_PLANKS);
                public static final RegistryObject<Block> JUNGLE_FRANCH_SLAB = registerVanillaFranchSlab("jungle_franch_slab", net.minecraft.world.level.block.Blocks.JUNGLE_SLAB);
                public static final RegistryObject<Block> JUNGLE_FRANCH_TRAPDOOR = registerVanillaFranchTrapdoor("jungle_franch_trapdoor", net.minecraft.world.level.block.Blocks.JUNGLE_TRAPDOOR, BlockSetType.JUNGLE);
                public static final RegistryObject<Block> ACACIA_FRANCH = registerVanillaFranch("acacia_franch", net.minecraft.world.level.block.Blocks.ACACIA_FENCE);
                public static final RegistryObject<Block> ACACIA_FRANCH_GATE = registerVanillaFranchGate("acacia_franch_gate", net.minecraft.world.level.block.Blocks.ACACIA_FENCE_GATE, WoodType.ACACIA);
                public static final RegistryObject<Block> ACACIA_FRANCH_PLANKS = registerVanillaFranchPlanks("acacia_franch_planks", net.minecraft.world.level.block.Blocks.ACACIA_PLANKS);
                public static final RegistryObject<Block> ACACIA_FRANCH_STAIRS = registerVanillaFranchStairs("acacia_franch_stairs", net.minecraft.world.level.block.Blocks.ACACIA_STAIRS, net.minecraft.world.level.block.Blocks.ACACIA_PLANKS);
                public static final RegistryObject<Block> ACACIA_FRANCH_SLAB = registerVanillaFranchSlab("acacia_franch_slab", net.minecraft.world.level.block.Blocks.ACACIA_SLAB);
                public static final RegistryObject<Block> ACACIA_FRANCH_TRAPDOOR = registerVanillaFranchTrapdoor("acacia_franch_trapdoor", net.minecraft.world.level.block.Blocks.ACACIA_TRAPDOOR, BlockSetType.ACACIA);
                public static final RegistryObject<Block> DARK_OAK_FRANCH = registerVanillaFranch("dark_oak_franch", net.minecraft.world.level.block.Blocks.DARK_OAK_FENCE);
                public static final RegistryObject<Block> DARK_OAK_FRANCH_GATE = registerVanillaFranchGate("dark_oak_franch_gate", net.minecraft.world.level.block.Blocks.DARK_OAK_FENCE_GATE, WoodType.DARK_OAK);
                public static final RegistryObject<Block> DARK_OAK_FRANCH_PLANKS = registerVanillaFranchPlanks("dark_oak_franch_planks", net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS);
                public static final RegistryObject<Block> DARK_OAK_FRANCH_STAIRS = registerVanillaFranchStairs("dark_oak_franch_stairs", net.minecraft.world.level.block.Blocks.DARK_OAK_STAIRS, net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS);
                public static final RegistryObject<Block> DARK_OAK_FRANCH_SLAB = registerVanillaFranchSlab("dark_oak_franch_slab", net.minecraft.world.level.block.Blocks.DARK_OAK_SLAB);
                public static final RegistryObject<Block> DARK_OAK_FRANCH_TRAPDOOR = registerVanillaFranchTrapdoor("dark_oak_franch_trapdoor", net.minecraft.world.level.block.Blocks.DARK_OAK_TRAPDOOR, BlockSetType.DARK_OAK);
                public static final RegistryObject<Block> MANGROVE_FRANCH = registerVanillaFranch("mangrove_franch", net.minecraft.world.level.block.Blocks.MANGROVE_FENCE);
                public static final RegistryObject<Block> MANGROVE_FRANCH_GATE = registerVanillaFranchGate("mangrove_franch_gate", net.minecraft.world.level.block.Blocks.MANGROVE_FENCE_GATE, WoodType.MANGROVE);
                public static final RegistryObject<Block> MANGROVE_FRANCH_PLANKS = registerVanillaFranchPlanks("mangrove_franch_planks", net.minecraft.world.level.block.Blocks.MANGROVE_PLANKS);
                public static final RegistryObject<Block> MANGROVE_FRANCH_STAIRS = registerVanillaFranchStairs("mangrove_franch_stairs", net.minecraft.world.level.block.Blocks.MANGROVE_STAIRS, net.minecraft.world.level.block.Blocks.MANGROVE_PLANKS);
                public static final RegistryObject<Block> MANGROVE_FRANCH_SLAB = registerVanillaFranchSlab("mangrove_franch_slab", net.minecraft.world.level.block.Blocks.MANGROVE_SLAB);
                public static final RegistryObject<Block> MANGROVE_FRANCH_TRAPDOOR = registerVanillaFranchTrapdoor("mangrove_franch_trapdoor", net.minecraft.world.level.block.Blocks.MANGROVE_TRAPDOOR, BlockSetType.MANGROVE);
                public static final RegistryObject<Block> CHERRY_FRANCH = registerVanillaFranch("cherry_franch", net.minecraft.world.level.block.Blocks.CHERRY_FENCE);
                public static final RegistryObject<Block> CHERRY_FRANCH_GATE = registerVanillaFranchGate("cherry_franch_gate", net.minecraft.world.level.block.Blocks.CHERRY_FENCE_GATE, WoodType.CHERRY);
                public static final RegistryObject<Block> CHERRY_FRANCH_PLANKS = registerVanillaFranchPlanks("cherry_franch_planks", net.minecraft.world.level.block.Blocks.CHERRY_PLANKS);
                public static final RegistryObject<Block> CHERRY_FRANCH_STAIRS = registerVanillaFranchStairs("cherry_franch_stairs", net.minecraft.world.level.block.Blocks.CHERRY_STAIRS, net.minecraft.world.level.block.Blocks.CHERRY_PLANKS);
                public static final RegistryObject<Block> CHERRY_FRANCH_SLAB = registerVanillaFranchSlab("cherry_franch_slab", net.minecraft.world.level.block.Blocks.CHERRY_SLAB);
                public static final RegistryObject<Block> CHERRY_FRANCH_TRAPDOOR = registerVanillaFranchTrapdoor("cherry_franch_trapdoor", net.minecraft.world.level.block.Blocks.CHERRY_TRAPDOOR, BlockSetType.CHERRY);

                // Schematic-only "Franch" string block (for using string/tripwire placeholders in tree schematics).
                public static final RegistryObject<Block> STRING_FRANCH = registerStringFranch("string_franch");

        public static final RegistryObject<Block> STRIPPED_WAYROOT_LOG = registerLog("stripped_wayroot_log");
        public static final RegistryObject<Block> STRIPPED_WAYROOT_WOOD = registerLog("stripped_wayroot_wood");
        public static final RegistryObject<Block> WAYROOT_LOG = registerStrippableLog("wayroot_log", () -> STRIPPED_WAYROOT_LOG.get());
                public static final RegistryObject<Block> WAYROOT_WOOD = registerStrippableLog("wayroot_wood", () -> STRIPPED_WAYROOT_WOOD.get());
    public static final RegistryObject<Block> WAYROOT_PLANKS = registerPlanks("wayroot_planks");
    public static final RegistryObject<Block> WAYROOT_LEAVES = registerLeaves("wayroot_leaves");
        public static final RegistryObject<Block> WAYROOT_SAPLING = registerFeatureSapling2x2(
                        "wayroot_sapling",
                        configuredFeatureKey("wayroot/sapling"),
                        configuredFeatureKey("wayroot/mega")
        );

        public static final RegistryObject<Block> WAYROOT_STAIRS = registerStairs("wayroot_stairs", WAYROOT_PLANKS);
        public static final RegistryObject<Block> WAYROOT_SLAB = registerSlab("wayroot_slab", WAYROOT_PLANKS);
        public static final RegistryObject<Block> WAYROOT_FENCE = registerFence("wayroot_fence", WAYROOT_PLANKS);
        public static final RegistryObject<Block> WAYROOT_FRANCH = registerFranch("wayroot_franch", WAYROOT_PLANKS);
        public static final RegistryObject<Block> WAYROOT_FRANCH_GATE = registerFranchGate("wayroot_franch_gate", WAYROOT_PLANKS);
        public static final RegistryObject<Block> WAYROOT_FRANCH_PLANKS = registerFranchPlanks("wayroot_franch_planks", WAYROOT_PLANKS);
        public static final RegistryObject<Block> WAYROOT_FRANCH_STAIRS = registerFranchStairs("wayroot_franch_stairs", WAYROOT_PLANKS);
        public static final RegistryObject<Block> WAYROOT_FRANCH_SLAB = registerFranchSlab("wayroot_franch_slab", WAYROOT_PLANKS);
        public static final RegistryObject<Block> WAYROOT_FRANCH_TRAPDOOR = registerFranchTrapdoor("wayroot_franch_trapdoor", WAYROOT_PLANKS);
        public static final RegistryObject<Block> WAYROOT_FENCE_GATE = registerFenceGate("wayroot_fence_gate", WAYROOT_PLANKS);
        public static final RegistryObject<Block> WAYROOT_DOOR = registerDoor("wayroot_door", WAYROOT_PLANKS);
        public static final RegistryObject<Block> WAYROOT_TRAPDOOR = registerTrapdoor("wayroot_trapdoor", WAYROOT_PLANKS);
        public static final RegistryObject<Block> WAYROOT_BUTTON = registerButton("wayroot_button", WAYROOT_PLANKS);
        public static final RegistryObject<Block> WAYROOT_PRESSURE_PLATE = registerPressurePlate("wayroot_pressure_plate", WAYROOT_PLANKS);

        public static final RegistryObject<Block> STRIPPED_FALLBARK_LOG = registerLog("stripped_fallbark_log");
        public static final RegistryObject<Block> STRIPPED_FALLBARK_WOOD = registerLog("stripped_fallbark_wood");
        public static final RegistryObject<Block> FALLBARK_LOG = registerStrippableLog("fallbark_log", () -> STRIPPED_FALLBARK_LOG.get());
                public static final RegistryObject<Block> FALLBARK_WOOD = registerStrippableLog("fallbark_wood", () -> STRIPPED_FALLBARK_WOOD.get());
    public static final RegistryObject<Block> FALLBARK_PLANKS = registerPlanks("fallbark_planks");
    public static final RegistryObject<Block> FALLBARK_LEAVES = registerLeaves("fallbark_leaves");
        public static final RegistryObject<Block> FALLBARK_SAPLING = registerFeatureSapling2x2(
                        "fallbark_sapling",
                        configuredFeatureKey("fallbark/sapling"),
                        configuredFeatureKey("fallbark/mega")
        );

        public static final RegistryObject<Block> FALLBARK_STAIRS = registerStairs("fallbark_stairs", FALLBARK_PLANKS);
        public static final RegistryObject<Block> FALLBARK_SLAB = registerSlab("fallbark_slab", FALLBARK_PLANKS);
        public static final RegistryObject<Block> FALLBARK_FENCE = registerFence("fallbark_fence", FALLBARK_PLANKS);
        public static final RegistryObject<Block> FALLBARK_FRANCH = registerFranch("fallbark_franch", FALLBARK_PLANKS);
        public static final RegistryObject<Block> FALLBARK_FRANCH_GATE = registerFranchGate("fallbark_franch_gate", FALLBARK_PLANKS);
        public static final RegistryObject<Block> FALLBARK_FRANCH_PLANKS = registerFranchPlanks("fallbark_franch_planks", FALLBARK_PLANKS);
        public static final RegistryObject<Block> FALLBARK_FRANCH_STAIRS = registerFranchStairs("fallbark_franch_stairs", FALLBARK_PLANKS);
        public static final RegistryObject<Block> FALLBARK_FRANCH_SLAB = registerFranchSlab("fallbark_franch_slab", FALLBARK_PLANKS);
        public static final RegistryObject<Block> FALLBARK_FRANCH_TRAPDOOR = registerFranchTrapdoor("fallbark_franch_trapdoor", FALLBARK_PLANKS);
        public static final RegistryObject<Block> FALLBARK_FENCE_GATE = registerFenceGate("fallbark_fence_gate", FALLBARK_PLANKS);
        public static final RegistryObject<Block> FALLBARK_DOOR = registerDoor("fallbark_door", FALLBARK_PLANKS);
        public static final RegistryObject<Block> FALLBARK_TRAPDOOR = registerTrapdoor("fallbark_trapdoor", FALLBARK_PLANKS);
        public static final RegistryObject<Block> FALLBARK_BUTTON = registerButton("fallbark_button", FALLBARK_PLANKS);
        public static final RegistryObject<Block> FALLBARK_PRESSURE_PLATE = registerPressurePlate("fallbark_pressure_plate", FALLBARK_PLANKS);

        public static final RegistryObject<Block> STRIPPED_ECHOWOOD_LOG = registerLog("stripped_echowood_log");
        public static final RegistryObject<Block> STRIPPED_ECHOWOOD_WOOD = registerLog("stripped_echowood_wood");
        public static final RegistryObject<Block> ECHOWOOD_LOG = registerStrippableLog("echowood_log", () -> STRIPPED_ECHOWOOD_LOG.get());
                public static final RegistryObject<Block> ECHOWOOD_WOOD = registerStrippableLog("echowood_wood", () -> STRIPPED_ECHOWOOD_WOOD.get());
    public static final RegistryObject<Block> ECHOWOOD_PLANKS = registerPlanks("echowood_planks");
    public static final RegistryObject<Block> ECHOWOOD_LEAVES = registerLeaves("echowood_leaves");
        public static final RegistryObject<Block> ECHOWOOD_SAPLING = registerFeatureSapling2x2(
                        "echowood_sapling",
                        configuredFeatureKey("echowood/sapling"),
                        configuredFeatureKey("echowood/mega")
        );

        public static final RegistryObject<Block> ECHOWOOD_STAIRS = registerStairs("echowood_stairs", ECHOWOOD_PLANKS);
        public static final RegistryObject<Block> ECHOWOOD_SLAB = registerSlab("echowood_slab", ECHOWOOD_PLANKS);
        public static final RegistryObject<Block> ECHOWOOD_FENCE = registerFence("echowood_fence", ECHOWOOD_PLANKS);
        public static final RegistryObject<Block> ECHOWOOD_FRANCH = registerFranch("echowood_franch", ECHOWOOD_PLANKS);
        public static final RegistryObject<Block> ECHOWOOD_FRANCH_GATE = registerFranchGate("echowood_franch_gate", ECHOWOOD_PLANKS);
        public static final RegistryObject<Block> ECHOWOOD_FRANCH_PLANKS = registerFranchPlanks("echowood_franch_planks", ECHOWOOD_PLANKS);
        public static final RegistryObject<Block> ECHOWOOD_FRANCH_STAIRS = registerFranchStairs("echowood_franch_stairs", ECHOWOOD_PLANKS);
        public static final RegistryObject<Block> ECHOWOOD_FRANCH_SLAB = registerFranchSlab("echowood_franch_slab", ECHOWOOD_PLANKS);
        public static final RegistryObject<Block> ECHOWOOD_FRANCH_TRAPDOOR = registerFranchTrapdoor("echowood_franch_trapdoor", ECHOWOOD_PLANKS);
        public static final RegistryObject<Block> ECHOWOOD_FENCE_GATE = registerFenceGate("echowood_fence_gate", ECHOWOOD_PLANKS);
        public static final RegistryObject<Block> ECHOWOOD_DOOR = registerDoor("echowood_door", ECHOWOOD_PLANKS);
        public static final RegistryObject<Block> ECHOWOOD_TRAPDOOR = registerTrapdoor("echowood_trapdoor", ECHOWOOD_PLANKS);
        public static final RegistryObject<Block> ECHOWOOD_BUTTON = registerButton("echowood_button", ECHOWOOD_PLANKS);
        public static final RegistryObject<Block> ECHOWOOD_PRESSURE_PLATE = registerPressurePlate("echowood_pressure_plate", ECHOWOOD_PLANKS);

        public static final RegistryObject<Block> STRIPPED_CAIRN_TREE_LOG = registerLog("stripped_cairn_tree_log");
        public static final RegistryObject<Block> STRIPPED_CAIRN_TREE_WOOD = registerLog("stripped_cairn_tree_wood");
        public static final RegistryObject<Block> CAIRN_TREE_LOG = registerStrippableLog("cairn_tree_log", () -> STRIPPED_CAIRN_TREE_LOG.get());
                public static final RegistryObject<Block> CAIRN_TREE_WOOD = registerStrippableLog("cairn_tree_wood", () -> STRIPPED_CAIRN_TREE_WOOD.get());
    public static final RegistryObject<Block> CAIRN_TREE_PLANKS = registerPlanks("cairn_tree_planks");
    public static final RegistryObject<Block> CAIRN_TREE_LEAVES = registerLeaves("cairn_tree_leaves");
        public static final RegistryObject<Block> CAIRN_TREE_SAPLING = registerFeatureSapling2x2(
                        "cairn_tree_sapling",
                        configuredFeatureKey("cairn_tree/sapling"),
                        configuredFeatureKey("cairn_tree/mega")
        );

        public static final RegistryObject<Block> CAIRN_TREE_STAIRS = registerStairs("cairn_tree_stairs", CAIRN_TREE_PLANKS);
        public static final RegistryObject<Block> CAIRN_TREE_SLAB = registerSlab("cairn_tree_slab", CAIRN_TREE_PLANKS);
        public static final RegistryObject<Block> CAIRN_TREE_FENCE = registerFence("cairn_tree_fence", CAIRN_TREE_PLANKS);
        public static final RegistryObject<Block> CAIRN_TREE_FRANCH = registerFranch("cairn_tree_franch", CAIRN_TREE_PLANKS);
        public static final RegistryObject<Block> CAIRN_TREE_FRANCH_GATE = registerFranchGate("cairn_tree_franch_gate", CAIRN_TREE_PLANKS);
        public static final RegistryObject<Block> CAIRN_TREE_FRANCH_PLANKS = registerFranchPlanks("cairn_tree_franch_planks", CAIRN_TREE_PLANKS);
        public static final RegistryObject<Block> CAIRN_TREE_FRANCH_STAIRS = registerFranchStairs("cairn_tree_franch_stairs", CAIRN_TREE_PLANKS);
        public static final RegistryObject<Block> CAIRN_TREE_FRANCH_SLAB = registerFranchSlab("cairn_tree_franch_slab", CAIRN_TREE_PLANKS);
        public static final RegistryObject<Block> CAIRN_TREE_FRANCH_TRAPDOOR = registerFranchTrapdoor("cairn_tree_franch_trapdoor", CAIRN_TREE_PLANKS);
        public static final RegistryObject<Block> CAIRN_TREE_FENCE_GATE = registerFenceGate("cairn_tree_fence_gate", CAIRN_TREE_PLANKS);
        public static final RegistryObject<Block> CAIRN_TREE_DOOR = registerDoor("cairn_tree_door", CAIRN_TREE_PLANKS);
        public static final RegistryObject<Block> CAIRN_TREE_TRAPDOOR = registerTrapdoor("cairn_tree_trapdoor", CAIRN_TREE_PLANKS);
        public static final RegistryObject<Block> CAIRN_TREE_BUTTON = registerButton("cairn_tree_button", CAIRN_TREE_PLANKS);
        public static final RegistryObject<Block> CAIRN_TREE_PRESSURE_PLATE = registerPressurePlate("cairn_tree_pressure_plate", CAIRN_TREE_PLANKS);

        public static final RegistryObject<Block> STRIPPED_WAYGLASS_LOG = registerLog("stripped_wayglass_log");
        public static final RegistryObject<Block> STRIPPED_WAYGLASS_WOOD = registerLog("stripped_wayglass_wood");
        public static final RegistryObject<Block> WAYGLASS_LOG = registerStrippableLog("wayglass_log", () -> STRIPPED_WAYGLASS_LOG.get());
                public static final RegistryObject<Block> WAYGLASS_WOOD = registerStrippableLog("wayglass_wood", () -> STRIPPED_WAYGLASS_WOOD.get());
    public static final RegistryObject<Block> WAYGLASS_PLANKS = registerPlanks("wayglass_planks");
    public static final RegistryObject<Block> WAYGLASS_LEAVES = registerLeaves("wayglass_leaves");
        public static final RegistryObject<Block> WAYGLASS_SAPLING = registerFeatureSapling2x2(
                        "wayglass_sapling",
                        configuredFeatureKey("wayglass/sapling"),
                        configuredFeatureKey("wayglass/mega")
        );

        public static final RegistryObject<Block> WAYGLASS_STAIRS = registerStairs("wayglass_stairs", WAYGLASS_PLANKS);
        public static final RegistryObject<Block> WAYGLASS_SLAB = registerSlab("wayglass_slab", WAYGLASS_PLANKS);
        public static final RegistryObject<Block> WAYGLASS_FENCE = registerFence("wayglass_fence", WAYGLASS_PLANKS);
        public static final RegistryObject<Block> WAYGLASS_FRANCH = registerFranch("wayglass_franch", WAYGLASS_PLANKS);
        public static final RegistryObject<Block> WAYGLASS_FRANCH_GATE = registerFranchGate("wayglass_franch_gate", WAYGLASS_PLANKS);
        public static final RegistryObject<Block> WAYGLASS_FRANCH_PLANKS = registerFranchPlanks("wayglass_franch_planks", WAYGLASS_PLANKS);
        public static final RegistryObject<Block> WAYGLASS_FRANCH_STAIRS = registerFranchStairs("wayglass_franch_stairs", WAYGLASS_PLANKS);
        public static final RegistryObject<Block> WAYGLASS_FRANCH_SLAB = registerFranchSlab("wayglass_franch_slab", WAYGLASS_PLANKS);
        public static final RegistryObject<Block> WAYGLASS_FRANCH_TRAPDOOR = registerFranchTrapdoor("wayglass_franch_trapdoor", WAYGLASS_PLANKS);
        public static final RegistryObject<Block> WAYGLASS_FENCE_GATE = registerFenceGate("wayglass_fence_gate", WAYGLASS_PLANKS);
        public static final RegistryObject<Block> WAYGLASS_DOOR = registerDoor("wayglass_door", WAYGLASS_PLANKS);
        public static final RegistryObject<Block> WAYGLASS_TRAPDOOR = registerTrapdoor("wayglass_trapdoor", WAYGLASS_PLANKS);
        public static final RegistryObject<Block> WAYGLASS_BUTTON = registerButton("wayglass_button", WAYGLASS_PLANKS);
        public static final RegistryObject<Block> WAYGLASS_PRESSURE_PLATE = registerPressurePlate("wayglass_pressure_plate", WAYGLASS_PLANKS);

        public static final RegistryObject<Block> STRIPPED_SPLINTERSPORE_LOG = registerLog("stripped_splinterspore_log");
        public static final RegistryObject<Block> STRIPPED_SPLINTERSPORE_WOOD = registerLog("stripped_splinterspore_wood");
        public static final RegistryObject<Block> SPLINTERSPORE_LOG = registerStrippableLog("splinterspore_log", () -> STRIPPED_SPLINTERSPORE_LOG.get());
                public static final RegistryObject<Block> SPLINTERSPORE_WOOD = registerStrippableLog("splinterspore_wood", () -> STRIPPED_SPLINTERSPORE_WOOD.get());
    public static final RegistryObject<Block> SPLINTERSPORE_PLANKS = registerPlanks("splinterspore_planks");
    public static final RegistryObject<Block> SPLINTERSPORE_LEAVES = registerLeaves("splinterspore_leaves");
        public static final RegistryObject<Block> SPLINTERSPORE_SAPLING = registerFeatureSapling2x2(
                        "splinterspore_sapling",
                        configuredFeatureKey("splinterspore/sapling"),
                        configuredFeatureKey("splinterspore/mega")
        );

        public static final RegistryObject<Block> SPLINTERSPORE_STAIRS = registerStairs("splinterspore_stairs", SPLINTERSPORE_PLANKS);
        public static final RegistryObject<Block> SPLINTERSPORE_SLAB = registerSlab("splinterspore_slab", SPLINTERSPORE_PLANKS);
        public static final RegistryObject<Block> SPLINTERSPORE_FENCE = registerFence("splinterspore_fence", SPLINTERSPORE_PLANKS);
        public static final RegistryObject<Block> SPLINTERSPORE_FRANCH = registerFranch("splinterspore_franch", SPLINTERSPORE_PLANKS);
        public static final RegistryObject<Block> SPLINTERSPORE_FRANCH_GATE = registerFranchGate("splinterspore_franch_gate", SPLINTERSPORE_PLANKS);
        public static final RegistryObject<Block> SPLINTERSPORE_FRANCH_PLANKS = registerFranchPlanks("splinterspore_franch_planks", SPLINTERSPORE_PLANKS);
        public static final RegistryObject<Block> SPLINTERSPORE_FRANCH_STAIRS = registerFranchStairs("splinterspore_franch_stairs", SPLINTERSPORE_PLANKS);
        public static final RegistryObject<Block> SPLINTERSPORE_FRANCH_SLAB = registerFranchSlab("splinterspore_franch_slab", SPLINTERSPORE_PLANKS);
        public static final RegistryObject<Block> SPLINTERSPORE_FRANCH_TRAPDOOR = registerFranchTrapdoor("splinterspore_franch_trapdoor", SPLINTERSPORE_PLANKS);
        public static final RegistryObject<Block> SPLINTERSPORE_FENCE_GATE = registerFenceGate("splinterspore_fence_gate", SPLINTERSPORE_PLANKS);
        public static final RegistryObject<Block> SPLINTERSPORE_DOOR = registerDoor("splinterspore_door", SPLINTERSPORE_PLANKS);
        public static final RegistryObject<Block> SPLINTERSPORE_TRAPDOOR = registerTrapdoor("splinterspore_trapdoor", SPLINTERSPORE_PLANKS);
        public static final RegistryObject<Block> SPLINTERSPORE_BUTTON = registerButton("splinterspore_button", SPLINTERSPORE_PLANKS);
        public static final RegistryObject<Block> SPLINTERSPORE_PRESSURE_PLATE = registerPressurePlate("splinterspore_pressure_plate", SPLINTERSPORE_PLANKS);

        public static final RegistryObject<Block> STRIPPED_HOLLOWWAY_TREE_LOG = registerLog("stripped_hollowway_tree_log");
        public static final RegistryObject<Block> STRIPPED_HOLLOWWAY_TREE_WOOD = registerLog("stripped_hollowway_tree_wood");
        public static final RegistryObject<Block> HOLLOWWAY_TREE_LOG = registerStrippableLog("hollowway_tree_log", () -> STRIPPED_HOLLOWWAY_TREE_LOG.get());
                public static final RegistryObject<Block> HOLLOWWAY_TREE_WOOD = registerStrippableLog("hollowway_tree_wood", () -> STRIPPED_HOLLOWWAY_TREE_WOOD.get());
    public static final RegistryObject<Block> HOLLOWWAY_TREE_PLANKS = registerPlanks("hollowway_tree_planks");
    public static final RegistryObject<Block> HOLLOWWAY_TREE_LEAVES = registerLeaves("hollowway_tree_leaves");
        public static final RegistryObject<Block> HOLLOWWAY_TREE_SAPLING = registerFeatureSapling2x2(
                        "hollowway_tree_sapling",
                        configuredFeatureKey("hollowway_tree/sapling"),
                        configuredFeatureKey("hollowway_tree/mega")
        );

        public static final RegistryObject<Block> HOLLOWWAY_TREE_STAIRS = registerStairs("hollowway_tree_stairs", HOLLOWWAY_TREE_PLANKS);
        public static final RegistryObject<Block> HOLLOWWAY_TREE_SLAB = registerSlab("hollowway_tree_slab", HOLLOWWAY_TREE_PLANKS);
        public static final RegistryObject<Block> HOLLOWWAY_TREE_FENCE = registerFence("hollowway_tree_fence", HOLLOWWAY_TREE_PLANKS);
        public static final RegistryObject<Block> HOLLOWWAY_TREE_FRANCH = registerFranch("hollowway_tree_franch", HOLLOWWAY_TREE_PLANKS);
        public static final RegistryObject<Block> HOLLOWWAY_TREE_FRANCH_GATE = registerFranchGate("hollowway_tree_franch_gate", HOLLOWWAY_TREE_PLANKS);
        public static final RegistryObject<Block> HOLLOWWAY_TREE_FRANCH_PLANKS = registerFranchPlanks("hollowway_tree_franch_planks", HOLLOWWAY_TREE_PLANKS);
        public static final RegistryObject<Block> HOLLOWWAY_TREE_FRANCH_STAIRS = registerFranchStairs("hollowway_tree_franch_stairs", HOLLOWWAY_TREE_PLANKS);
        public static final RegistryObject<Block> HOLLOWWAY_TREE_FRANCH_SLAB = registerFranchSlab("hollowway_tree_franch_slab", HOLLOWWAY_TREE_PLANKS);
        public static final RegistryObject<Block> HOLLOWWAY_TREE_FRANCH_TRAPDOOR = registerFranchTrapdoor("hollowway_tree_franch_trapdoor", HOLLOWWAY_TREE_PLANKS);
        public static final RegistryObject<Block> HOLLOWWAY_TREE_FENCE_GATE = registerFenceGate("hollowway_tree_fence_gate", HOLLOWWAY_TREE_PLANKS);
        public static final RegistryObject<Block> HOLLOWWAY_TREE_DOOR = registerDoor("hollowway_tree_door", HOLLOWWAY_TREE_PLANKS);
        public static final RegistryObject<Block> HOLLOWWAY_TREE_TRAPDOOR = registerTrapdoor("hollowway_tree_trapdoor", HOLLOWWAY_TREE_PLANKS);
        public static final RegistryObject<Block> HOLLOWWAY_TREE_BUTTON = registerButton("hollowway_tree_button", HOLLOWWAY_TREE_PLANKS);
        public static final RegistryObject<Block> HOLLOWWAY_TREE_PRESSURE_PLATE = registerPressurePlate("hollowway_tree_pressure_plate", HOLLOWWAY_TREE_PLANKS);

        public static final RegistryObject<Block> STRIPPED_DRIFTWILLOW_LOG = registerLog("stripped_driftwillow_log");
        public static final RegistryObject<Block> STRIPPED_DRIFTWILLOW_WOOD = registerLog("stripped_driftwillow_wood");
        public static final RegistryObject<Block> DRIFTWILLOW_LOG = registerStrippableLog("driftwillow_log", () -> STRIPPED_DRIFTWILLOW_LOG.get());
                public static final RegistryObject<Block> DRIFTWILLOW_WOOD = registerStrippableLog("driftwillow_wood", () -> STRIPPED_DRIFTWILLOW_WOOD.get());
    public static final RegistryObject<Block> DRIFTWILLOW_PLANKS = registerPlanks("driftwillow_planks");
    public static final RegistryObject<Block> DRIFTWILLOW_LEAVES = registerLeaves("driftwillow_leaves");
        public static final RegistryObject<Block> DRIFTWILLOW_SAPLING = registerFeatureSapling2x2(
                        "driftwillow_sapling",
                        configuredFeatureKey("driftwillow/sapling"),
                        configuredFeatureKey("driftwillow/mega")
        );

        public static final RegistryObject<Block> DRIFTWILLOW_STAIRS = registerStairs("driftwillow_stairs", DRIFTWILLOW_PLANKS);
        public static final RegistryObject<Block> DRIFTWILLOW_SLAB = registerSlab("driftwillow_slab", DRIFTWILLOW_PLANKS);
        public static final RegistryObject<Block> DRIFTWILLOW_FENCE = registerFence("driftwillow_fence", DRIFTWILLOW_PLANKS);
        public static final RegistryObject<Block> DRIFTWILLOW_FRANCH = registerFranch("driftwillow_franch", DRIFTWILLOW_PLANKS);
        public static final RegistryObject<Block> DRIFTWILLOW_FRANCH_GATE = registerFranchGate("driftwillow_franch_gate", DRIFTWILLOW_PLANKS);
        public static final RegistryObject<Block> DRIFTWILLOW_FRANCH_PLANKS = registerFranchPlanks("driftwillow_franch_planks", DRIFTWILLOW_PLANKS);
        public static final RegistryObject<Block> DRIFTWILLOW_FRANCH_STAIRS = registerFranchStairs("driftwillow_franch_stairs", DRIFTWILLOW_PLANKS);
        public static final RegistryObject<Block> DRIFTWILLOW_FRANCH_SLAB = registerFranchSlab("driftwillow_franch_slab", DRIFTWILLOW_PLANKS);
        public static final RegistryObject<Block> DRIFTWILLOW_FRANCH_TRAPDOOR = registerFranchTrapdoor("driftwillow_franch_trapdoor", DRIFTWILLOW_PLANKS);
        public static final RegistryObject<Block> DRIFTWILLOW_FENCE_GATE = registerFenceGate("driftwillow_fence_gate", DRIFTWILLOW_PLANKS);
        public static final RegistryObject<Block> DRIFTWILLOW_DOOR = registerDoor("driftwillow_door", DRIFTWILLOW_PLANKS);
        public static final RegistryObject<Block> DRIFTWILLOW_TRAPDOOR = registerTrapdoor("driftwillow_trapdoor", DRIFTWILLOW_PLANKS);
        public static final RegistryObject<Block> DRIFTWILLOW_BUTTON = registerButton("driftwillow_button", DRIFTWILLOW_PLANKS);
        public static final RegistryObject<Block> DRIFTWILLOW_PRESSURE_PLATE = registerPressurePlate("driftwillow_pressure_plate", DRIFTWILLOW_PLANKS);

        public static final RegistryObject<Block> STRIPPED_MONUMENT_OAK_LOG = registerLog("stripped_monument_oak_log");
        public static final RegistryObject<Block> STRIPPED_MONUMENT_OAK_WOOD = registerLog("stripped_monument_oak_wood");
        public static final RegistryObject<Block> MONUMENT_OAK_LOG = registerStrippableLog("monument_oak_log", () -> STRIPPED_MONUMENT_OAK_LOG.get());
                public static final RegistryObject<Block> MONUMENT_OAK_WOOD = registerStrippableLog("monument_oak_wood", () -> STRIPPED_MONUMENT_OAK_WOOD.get());
    public static final RegistryObject<Block> MONUMENT_OAK_PLANKS = registerPlanks("monument_oak_planks");
    public static final RegistryObject<Block> MONUMENT_OAK_LEAVES = registerLeaves("monument_oak_leaves");
        public static final RegistryObject<Block> MONUMENT_OAK_SAPLING = registerFeatureSapling2x2(
                        "monument_oak_sapling",
                        configuredFeatureKey("monument_oak/sapling"),
                        configuredFeatureKey("monument_oak/mega")
        );

        public static final RegistryObject<Block> MONUMENT_OAK_STAIRS = registerStairs("monument_oak_stairs", MONUMENT_OAK_PLANKS);
        public static final RegistryObject<Block> MONUMENT_OAK_SLAB = registerSlab("monument_oak_slab", MONUMENT_OAK_PLANKS);
        public static final RegistryObject<Block> MONUMENT_OAK_FENCE = registerFence("monument_oak_fence", MONUMENT_OAK_PLANKS);
        public static final RegistryObject<Block> MONUMENT_OAK_FRANCH = registerFranch("monument_oak_franch", MONUMENT_OAK_PLANKS);
        public static final RegistryObject<Block> MONUMENT_OAK_FRANCH_GATE = registerFranchGate("monument_oak_franch_gate", MONUMENT_OAK_PLANKS);
        public static final RegistryObject<Block> MONUMENT_OAK_FRANCH_PLANKS = registerFranchPlanks("monument_oak_franch_planks", MONUMENT_OAK_PLANKS);
        public static final RegistryObject<Block> MONUMENT_OAK_FRANCH_STAIRS = registerFranchStairs("monument_oak_franch_stairs", MONUMENT_OAK_PLANKS);
        public static final RegistryObject<Block> MONUMENT_OAK_FRANCH_SLAB = registerFranchSlab("monument_oak_franch_slab", MONUMENT_OAK_PLANKS);
        public static final RegistryObject<Block> MONUMENT_OAK_FRANCH_TRAPDOOR = registerFranchTrapdoor("monument_oak_franch_trapdoor", MONUMENT_OAK_PLANKS);
        public static final RegistryObject<Block> MONUMENT_OAK_FENCE_GATE = registerFenceGate("monument_oak_fence_gate", MONUMENT_OAK_PLANKS);
        public static final RegistryObject<Block> MONUMENT_OAK_DOOR = registerDoor("monument_oak_door", MONUMENT_OAK_PLANKS);
        public static final RegistryObject<Block> MONUMENT_OAK_TRAPDOOR = registerTrapdoor("monument_oak_trapdoor", MONUMENT_OAK_PLANKS);
        public static final RegistryObject<Block> MONUMENT_OAK_BUTTON = registerButton("monument_oak_button", MONUMENT_OAK_PLANKS);
        public static final RegistryObject<Block> MONUMENT_OAK_PRESSURE_PLATE = registerPressurePlate("monument_oak_pressure_plate", MONUMENT_OAK_PLANKS);

        public static final RegistryObject<Block> STRIPPED_WAYTORCH_TREE_LOG = registerLog("stripped_waytorch_tree_log");
        public static final RegistryObject<Block> STRIPPED_WAYTORCH_TREE_WOOD = registerLog("stripped_waytorch_tree_wood");
        public static final RegistryObject<Block> WAYTORCH_TREE_LOG = registerStrippableLog("waytorch_tree_log", () -> STRIPPED_WAYTORCH_TREE_LOG.get());
                public static final RegistryObject<Block> WAYTORCH_TREE_WOOD = registerStrippableLog("waytorch_tree_wood", () -> STRIPPED_WAYTORCH_TREE_WOOD.get());
    public static final RegistryObject<Block> WAYTORCH_TREE_PLANKS = registerPlanks("waytorch_tree_planks");
    public static final RegistryObject<Block> WAYTORCH_TREE_LEAVES = registerLeaves("waytorch_tree_leaves");
        public static final RegistryObject<Block> WAYTORCH_TREE_SAPLING = registerFeatureSapling2x2(
                                                "waytorch_tree_sapling",
                        configuredFeatureKey("waytorch_tree/sapling"),
                        configuredFeatureKey("waytorch_tree/mega")
        );

        public static final RegistryObject<Block> WAYTORCH_TREE_STAIRS = registerStairs("waytorch_tree_stairs", WAYTORCH_TREE_PLANKS);
        public static final RegistryObject<Block> WAYTORCH_TREE_SLAB = registerSlab("waytorch_tree_slab", WAYTORCH_TREE_PLANKS);
        public static final RegistryObject<Block> WAYTORCH_TREE_FENCE = registerFence("waytorch_tree_fence", WAYTORCH_TREE_PLANKS);
        public static final RegistryObject<Block> WAYTORCH_TREE_FRANCH = registerFranch("waytorch_tree_franch", WAYTORCH_TREE_PLANKS);
        public static final RegistryObject<Block> WAYTORCH_TREE_FRANCH_GATE = registerFranchGate("waytorch_tree_franch_gate", WAYTORCH_TREE_PLANKS);
        public static final RegistryObject<Block> WAYTORCH_TREE_FRANCH_PLANKS = registerFranchPlanks("waytorch_tree_franch_planks", WAYTORCH_TREE_PLANKS);
        public static final RegistryObject<Block> WAYTORCH_TREE_FRANCH_STAIRS = registerFranchStairs("waytorch_tree_franch_stairs", WAYTORCH_TREE_PLANKS);
        public static final RegistryObject<Block> WAYTORCH_TREE_FRANCH_SLAB = registerFranchSlab("waytorch_tree_franch_slab", WAYTORCH_TREE_PLANKS);
        public static final RegistryObject<Block> WAYTORCH_TREE_FRANCH_TRAPDOOR = registerFranchTrapdoor("waytorch_tree_franch_trapdoor", WAYTORCH_TREE_PLANKS);
        public static final RegistryObject<Block> WAYTORCH_TREE_FENCE_GATE = registerFenceGate("waytorch_tree_fence_gate", WAYTORCH_TREE_PLANKS);
        public static final RegistryObject<Block> WAYTORCH_TREE_DOOR = registerDoor("waytorch_tree_door", WAYTORCH_TREE_PLANKS);
        public static final RegistryObject<Block> WAYTORCH_TREE_TRAPDOOR = registerTrapdoor("waytorch_tree_trapdoor", WAYTORCH_TREE_PLANKS);
        public static final RegistryObject<Block> WAYTORCH_TREE_BUTTON = registerButton("waytorch_tree_button", WAYTORCH_TREE_PLANKS);
        public static final RegistryObject<Block> WAYTORCH_TREE_PRESSURE_PLATE = registerPressurePlate("waytorch_tree_pressure_plate", WAYTORCH_TREE_PLANKS);

        public static final RegistryObject<Block> STRIPPED_FAULTWOOD_LOG = registerLog("stripped_faultwood_log");
        public static final RegistryObject<Block> STRIPPED_FAULTWOOD_WOOD = registerLog("stripped_faultwood_wood");
        public static final RegistryObject<Block> FAULTWOOD_LOG = registerStrippableLog("faultwood_log", () -> STRIPPED_FAULTWOOD_LOG.get());
                public static final RegistryObject<Block> FAULTWOOD_WOOD = registerStrippableLog("faultwood_wood", () -> STRIPPED_FAULTWOOD_WOOD.get());
    public static final RegistryObject<Block> FAULTWOOD_PLANKS = registerPlanks("faultwood_planks");
    public static final RegistryObject<Block> FAULTWOOD_LEAVES = registerLeaves("faultwood_leaves");
        public static final RegistryObject<Block> FAULTWOOD_SAPLING = registerFeatureSapling2x2(
                        "faultwood_sapling",
                        configuredFeatureKey("faultwood/sapling"),
                        configuredFeatureKey("faultwood/mega")
        );

        public static final RegistryObject<Block> FAULTWOOD_STAIRS = registerStairs("faultwood_stairs", FAULTWOOD_PLANKS);
        public static final RegistryObject<Block> FAULTWOOD_SLAB = registerSlab("faultwood_slab", FAULTWOOD_PLANKS);

    // --- Staple trees (all-biome) ---

        public static final RegistryObject<Block> STRIPPED_ASHBLOOM_LOG = registerLog("stripped_ashbloom_log");
        public static final RegistryObject<Block> STRIPPED_ASHBLOOM_WOOD = registerLog("stripped_ashbloom_wood");
        public static final RegistryObject<Block> ASHBLOOM_LOG = registerStrippableLog("ashbloom_log", () -> STRIPPED_ASHBLOOM_LOG.get());
                public static final RegistryObject<Block> ASHBLOOM_WOOD = registerStrippableLog("ashbloom_wood", () -> STRIPPED_ASHBLOOM_WOOD.get());
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
    public static final RegistryObject<Block> ASHBLOOM_SAPLING = registerFeatureSapling2x2(
            "ashbloom_sapling",
            configuredFeatureKey("ashbloom/sapling"),
            configuredFeatureKey("ashbloom/mega")
    );

    public static final RegistryObject<Block> ASHBLOOM_STAIRS = registerStairs("ashbloom_stairs", ASHBLOOM_PLANKS);
    public static final RegistryObject<Block> ASHBLOOM_SLAB = registerSlab("ashbloom_slab", ASHBLOOM_PLANKS);
    public static final RegistryObject<Block> ASHBLOOM_FENCE = registerFence("ashbloom_fence", ASHBLOOM_PLANKS);
        public static final RegistryObject<Block> ASHBLOOM_FRANCH = registerFranch("ashbloom_franch", ASHBLOOM_PLANKS);
                public static final RegistryObject<Block> ASHBLOOM_FRANCH_GATE = registerFranchGate("ashbloom_franch_gate", ASHBLOOM_PLANKS);
                public static final RegistryObject<Block> ASHBLOOM_FRANCH_PLANKS = registerFranchPlanks("ashbloom_franch_planks", ASHBLOOM_PLANKS);
                public static final RegistryObject<Block> ASHBLOOM_FRANCH_STAIRS = registerFranchStairs("ashbloom_franch_stairs", ASHBLOOM_PLANKS);
                public static final RegistryObject<Block> ASHBLOOM_FRANCH_SLAB = registerFranchSlab("ashbloom_franch_slab", ASHBLOOM_PLANKS);
                public static final RegistryObject<Block> ASHBLOOM_FRANCH_TRAPDOOR = registerFranchTrapdoor("ashbloom_franch_trapdoor", ASHBLOOM_PLANKS);
    public static final RegistryObject<Block> ASHBLOOM_FENCE_GATE = registerFenceGate("ashbloom_fence_gate", ASHBLOOM_PLANKS);
    public static final RegistryObject<Block> ASHBLOOM_DOOR = registerDoor("ashbloom_door", ASHBLOOM_PLANKS);
    public static final RegistryObject<Block> ASHBLOOM_TRAPDOOR = registerTrapdoor("ashbloom_trapdoor", ASHBLOOM_PLANKS);
    public static final RegistryObject<Block> ASHBLOOM_BUTTON = registerButton("ashbloom_button", ASHBLOOM_PLANKS);
    public static final RegistryObject<Block> ASHBLOOM_PRESSURE_PLATE = registerPressurePlate("ashbloom_pressure_plate", ASHBLOOM_PLANKS);

        public static final RegistryObject<Block> STRIPPED_GLIMMERPINE_LOG = registerLog("stripped_glimmerpine_log");
        public static final RegistryObject<Block> STRIPPED_GLIMMERPINE_WOOD = registerLog("stripped_glimmerpine_wood");
        public static final RegistryObject<Block> GLIMMERPINE_LOG = registerStrippableLog("glimmerpine_log", () -> STRIPPED_GLIMMERPINE_LOG.get());
                public static final RegistryObject<Block> GLIMMERPINE_WOOD = registerStrippableLog("glimmerpine_wood", () -> STRIPPED_GLIMMERPINE_WOOD.get());
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
    public static final RegistryObject<Block> GLIMMERPINE_SAPLING = registerFeatureSapling2x2(
            "glimmerpine_sapling",
            configuredFeatureKey("glimmerpine/sapling"),
            configuredFeatureKey("glimmerpine/mega")
    );

    public static final RegistryObject<Block> GLIMMERPINE_STAIRS = registerStairs("glimmerpine_stairs", GLIMMERPINE_PLANKS);
    public static final RegistryObject<Block> GLIMMERPINE_SLAB = registerSlab("glimmerpine_slab", GLIMMERPINE_PLANKS);
    public static final RegistryObject<Block> GLIMMERPINE_FENCE = registerFence("glimmerpine_fence", GLIMMERPINE_PLANKS);
        public static final RegistryObject<Block> GLIMMERPINE_FRANCH = registerFranch("glimmerpine_franch", GLIMMERPINE_PLANKS);
                public static final RegistryObject<Block> GLIMMERPINE_FRANCH_GATE = registerFranchGate("glimmerpine_franch_gate", GLIMMERPINE_PLANKS);
                public static final RegistryObject<Block> GLIMMERPINE_FRANCH_PLANKS = registerFranchPlanks("glimmerpine_franch_planks", GLIMMERPINE_PLANKS);
                public static final RegistryObject<Block> GLIMMERPINE_FRANCH_STAIRS = registerFranchStairs("glimmerpine_franch_stairs", GLIMMERPINE_PLANKS);
                public static final RegistryObject<Block> GLIMMERPINE_FRANCH_SLAB = registerFranchSlab("glimmerpine_franch_slab", GLIMMERPINE_PLANKS);
                public static final RegistryObject<Block> GLIMMERPINE_FRANCH_TRAPDOOR = registerFranchTrapdoor("glimmerpine_franch_trapdoor", GLIMMERPINE_PLANKS);
    public static final RegistryObject<Block> GLIMMERPINE_FENCE_GATE = registerFenceGate("glimmerpine_fence_gate", GLIMMERPINE_PLANKS);
    public static final RegistryObject<Block> GLIMMERPINE_DOOR = registerDoor("glimmerpine_door", GLIMMERPINE_PLANKS);
    public static final RegistryObject<Block> GLIMMERPINE_TRAPDOOR = registerTrapdoor("glimmerpine_trapdoor", GLIMMERPINE_PLANKS);
    public static final RegistryObject<Block> GLIMMERPINE_BUTTON = registerButton("glimmerpine_button", GLIMMERPINE_PLANKS);
    public static final RegistryObject<Block> GLIMMERPINE_PRESSURE_PLATE = registerPressurePlate("glimmerpine_pressure_plate", GLIMMERPINE_PLANKS);

        public static final RegistryObject<Block> STRIPPED_DRIFTWOOD_LOG = registerLog("stripped_driftwood_log");
        public static final RegistryObject<Block> STRIPPED_DRIFTWOOD_WOOD = registerLog("stripped_driftwood_wood");
        public static final RegistryObject<Block> DRIFTWOOD_LOG = registerStrippableLog("driftwood_log", () -> STRIPPED_DRIFTWOOD_LOG.get());
                public static final RegistryObject<Block> DRIFTWOOD_WOOD = registerStrippableLog("driftwood_wood", () -> STRIPPED_DRIFTWOOD_WOOD.get());
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
    public static final RegistryObject<Block> DRIFTWOOD_SAPLING = registerFeatureSapling2x2(
            "driftwood_sapling",
            configuredFeatureKey("driftwood/sapling"),
            configuredFeatureKey("driftwood/mega")
    );

    public static final RegistryObject<Block> DRIFTWOOD_STAIRS = registerStairs("driftwood_stairs", DRIFTWOOD_PLANKS);
    public static final RegistryObject<Block> DRIFTWOOD_SLAB = registerSlab("driftwood_slab", DRIFTWOOD_PLANKS);
    public static final RegistryObject<Block> DRIFTWOOD_FENCE = registerFence("driftwood_fence", DRIFTWOOD_PLANKS);
        public static final RegistryObject<Block> DRIFTWOOD_FRANCH = registerFranch("driftwood_franch", DRIFTWOOD_PLANKS);
                public static final RegistryObject<Block> DRIFTWOOD_FRANCH_GATE = registerFranchGate("driftwood_franch_gate", DRIFTWOOD_PLANKS);
                public static final RegistryObject<Block> DRIFTWOOD_FRANCH_PLANKS = registerFranchPlanks("driftwood_franch_planks", DRIFTWOOD_PLANKS);
                public static final RegistryObject<Block> DRIFTWOOD_FRANCH_STAIRS = registerFranchStairs("driftwood_franch_stairs", DRIFTWOOD_PLANKS);
                public static final RegistryObject<Block> DRIFTWOOD_FRANCH_SLAB = registerFranchSlab("driftwood_franch_slab", DRIFTWOOD_PLANKS);
                public static final RegistryObject<Block> DRIFTWOOD_FRANCH_TRAPDOOR = registerFranchTrapdoor("driftwood_franch_trapdoor", DRIFTWOOD_PLANKS);
    public static final RegistryObject<Block> DRIFTWOOD_FENCE_GATE = registerFenceGate("driftwood_fence_gate", DRIFTWOOD_PLANKS);
    public static final RegistryObject<Block> DRIFTWOOD_DOOR = registerDoor("driftwood_door", DRIFTWOOD_PLANKS);
    public static final RegistryObject<Block> DRIFTWOOD_TRAPDOOR = registerTrapdoor("driftwood_trapdoor", DRIFTWOOD_PLANKS);
    public static final RegistryObject<Block> DRIFTWOOD_BUTTON = registerButton("driftwood_button", DRIFTWOOD_PLANKS);
    public static final RegistryObject<Block> DRIFTWOOD_PRESSURE_PLATE = registerPressurePlate("driftwood_pressure_plate", DRIFTWOOD_PLANKS);
        public static final RegistryObject<Block> FAULTWOOD_FENCE = registerFence("faultwood_fence", FAULTWOOD_PLANKS);
                public static final RegistryObject<Block> FAULTWOOD_FRANCH = registerFranch("faultwood_franch", FAULTWOOD_PLANKS);
                                public static final RegistryObject<Block> FAULTWOOD_FRANCH_GATE = registerFranchGate("faultwood_franch_gate", FAULTWOOD_PLANKS);
                                public static final RegistryObject<Block> FAULTWOOD_FRANCH_PLANKS = registerFranchPlanks("faultwood_franch_planks", FAULTWOOD_PLANKS);
                                public static final RegistryObject<Block> FAULTWOOD_FRANCH_STAIRS = registerFranchStairs("faultwood_franch_stairs", FAULTWOOD_PLANKS);
                                public static final RegistryObject<Block> FAULTWOOD_FRANCH_SLAB = registerFranchSlab("faultwood_franch_slab", FAULTWOOD_PLANKS);
                                public static final RegistryObject<Block> FAULTWOOD_FRANCH_TRAPDOOR = registerFranchTrapdoor("faultwood_franch_trapdoor", FAULTWOOD_PLANKS);
        public static final RegistryObject<Block> FAULTWOOD_FENCE_GATE = registerFenceGate("faultwood_fence_gate", FAULTWOOD_PLANKS);
        public static final RegistryObject<Block> FAULTWOOD_DOOR = registerDoor("faultwood_door", FAULTWOOD_PLANKS);
        public static final RegistryObject<Block> FAULTWOOD_TRAPDOOR = registerTrapdoor("faultwood_trapdoor", FAULTWOOD_PLANKS);
        public static final RegistryObject<Block> FAULTWOOD_BUTTON = registerButton("faultwood_button", FAULTWOOD_PLANKS);
        public static final RegistryObject<Block> FAULTWOOD_PRESSURE_PLATE = registerPressurePlate("faultwood_pressure_plate", FAULTWOOD_PLANKS);

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
