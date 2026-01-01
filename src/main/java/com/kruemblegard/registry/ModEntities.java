package com.kruemblegard.registry;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.KruemblegardBossEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Kruemblegard.MOD_ID);

    public static final RegistryObject<EntityType<KruemblegardBossEntity>> KRUEMBLEGARD =
        ENTITIES.register(
            "kruemblegard",
            () -> EntityType.Builder.<KruemblegardBossEntity>of(
                    KruemblegardBossEntity::new, MobCategory.MONSTER)
                .sized(1.4f, 4.5f)
                .build(new ResourceLocation(Kruemblegard.MOD_ID, "kruemblegard").toString())
        );
}
