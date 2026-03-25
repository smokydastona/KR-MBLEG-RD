package com.kruemblegard.init;

import com.kruemblegard.Kruemblegard;

import net.minecraft.world.level.block.entity.BlockEntityType;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModBlockEntities {
    private ModBlockEntities() {}

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Kruemblegard.MOD_ID);

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
