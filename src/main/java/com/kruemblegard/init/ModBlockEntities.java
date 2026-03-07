package com.kruemblegard.init;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.blockentity.PressureConduitBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    private ModBlockEntities() {}

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Kruemblegard.MOD_ID);

    public static final RegistryObject<BlockEntityType<PressureConduitBlockEntity>> PRESSURE_CONDUIT =
            BLOCK_ENTITIES.register(
                    "pressure_conduit",
                    () -> BlockEntityType.Builder.of(PressureConduitBlockEntity::new, ModBlocks.PRESSURE_CONDUIT.get()).build(null)
            );

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
