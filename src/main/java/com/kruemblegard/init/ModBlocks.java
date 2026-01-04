package com.kruemblegard.init;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.block.AncientWaystoneBlock;
import com.kruemblegard.block.BerryBushBlock;
import com.kruemblegard.block.EchocapBlock;
import com.kruemblegard.block.GravevineBlock;
import com.kruemblegard.block.RunebloomBlock;
import com.kruemblegard.block.SoulberryShrubBlock;
import com.kruemblegard.block.WispstalkBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

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

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
