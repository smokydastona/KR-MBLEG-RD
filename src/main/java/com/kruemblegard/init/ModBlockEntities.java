package com.kruemblegard.init;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.blockentity.ArenaAnchorBlockEntity;
import com.kruemblegard.blockentity.HauntedWaystoneBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    private ModBlockEntities() {}

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
                        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Kruemblegard.MODID);

    public static final RegistryObject<BlockEntityType<HauntedWaystoneBlockEntity>> HAUNTED_WAYSTONE =
            BLOCK_ENTITY_TYPES.register(
                    "haunted_waystone",
                    () -> BlockEntityType.Builder.of(
                            HauntedWaystoneBlockEntity::new,
                            ModBlocks.HAUNTED_WAYSTONE.get(),
                            ModBlocks.FALSE_WAYSTONE.get()
                    ).build(null)
            );

    public static final RegistryObject<BlockEntityType<ArenaAnchorBlockEntity>> ARENA_ANCHOR =
            BLOCK_ENTITY_TYPES.register(
                    "arena_anchor",
                    () -> BlockEntityType.Builder.of(
                            ArenaAnchorBlockEntity::new,
                            ModBlocks.ARENA_ANCHOR.get()
                    ).build(null)
            );

    public static void register(IEventBus bus) {
        BLOCK_ENTITY_TYPES.register(bus);
    }
}
