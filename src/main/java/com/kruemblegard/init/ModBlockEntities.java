package com.kruemblegard.init;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.blockentity.BrineGardenBasinBlockEntity;
import com.kruemblegard.blockentity.TendrilForgeBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    private ModBlockEntities() {}

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Kruemblegard.MOD_ID);

        public static final RegistryObject<BlockEntityType<BrineGardenBasinBlockEntity>> BRINE_GARDEN_BASIN =
            BLOCK_ENTITIES.register(
                "brine_garden_basin",
                () -> BlockEntityType.Builder.of(
                    BrineGardenBasinBlockEntity::new,
                    ModBlocks.BRINE_GARDEN_BASIN.get()
                ).build(null)
            );

        public static final RegistryObject<BlockEntityType<TendrilForgeBlockEntity>> TENDRIL_FORGE =
            BLOCK_ENTITIES.register(
                "tendril_forge",
                () -> BlockEntityType.Builder.of(
                    TendrilForgeBlockEntity::new,
                    ModBlocks.TENDRIL_FORGE.get()
                ).build(null)
            );

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
