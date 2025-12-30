package com.smoky.krumblegard.init;

import com.smoky.krumblegard.KrumblegardMod;
import com.smoky.krumblegard.entity.boss.KrumblegardEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    private ModEntities() {}

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, KrumblegardMod.MODID);

    public static final RegistryObject<EntityType<KrumblegardEntity>> KRUMBLEGARD = ENTITY_TYPES.register(
            "krumblegard",
            () -> EntityType.Builder.of(KrumblegardEntity::new, MobCategory.MONSTER)
                    .sized(1.4F, 3.4F)
                    .build(new ResourceLocation(KrumblegardMod.MODID, "krumblegard").toString())
    );

    public static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
    }
}
