package com.kruemblegard.init;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.block.AncientWaystoneBlock;
import com.kruemblegard.block.ArenaAnchorBlock;
import com.kruemblegard.block.FalseWaystoneBlock;
import com.kruemblegard.block.HauntedWaystoneBlock;

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

    public static final RegistryObject<Block> HAUNTED_WAYSTONE = BLOCKS.register(
            "haunted_waystone",
            () -> new HauntedWaystoneBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(3.5F, 30.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> FALSE_WAYSTONE = BLOCKS.register(
            "false_waystone",
            () -> new FalseWaystoneBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(3.5F, 30.0F)
                    .sound(SoundType.STONE)
                    .noLootTable())
    );

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

    public static final RegistryObject<Block> STANDING_STONE = BLOCKS.register(
            "standing_stone",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(4.0F, 60.0F)
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> ARENA_ANCHOR = BLOCKS.register(
            "arena_anchor",
            () -> new ArenaAnchorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.NONE)
                    .noOcclusion()
                    .strength(-1.0F, 3600000.0F)
                    .noLootTable())
    );

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
