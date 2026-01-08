package com.kruemblegard.registry;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.GreatHungerEntity;
import com.kruemblegard.entity.KruemblegardBossEntity;
import com.kruemblegard.entity.PebblitEntity;
import com.kruemblegard.entity.TraprockEntity;

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

    public static final RegistryObject<EntityType<TraprockEntity>> TRAPROCK =
        ENTITIES.register(
            "traprock",
            () -> EntityType.Builder.<TraprockEntity>of(
                    TraprockEntity::new, MobCategory.MONSTER)
                .sized(0.6f, 1.8f)
                .build(new ResourceLocation(Kruemblegard.MOD_ID, "traprock").toString())
        );

    public static final RegistryObject<EntityType<PebblitEntity>> PEBBLIT =
        ENTITIES.register(
            "pebblit",
            () -> EntityType.Builder.<PebblitEntity>of(
                    PebblitEntity::new, MobCategory.MONSTER)
                .sized(0.4f, 0.3f)
                .build(new ResourceLocation(Kruemblegard.MOD_ID, "pebblit").toString())
        );

    public static final RegistryObject<EntityType<GreatHungerEntity>> GREAT_HUNGER =
        ENTITIES.register(
            "great_hunger",
            () -> EntityType.Builder.<GreatHungerEntity>of(
                    GreatHungerEntity::new, MobCategory.MONSTER)
                .sized(1.0f, 0.6f)
                .build(new ResourceLocation(Kruemblegard.MOD_ID, "great_hunger").toString())
        );
}
