package com.kruemblegard.registry;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.GreatHungerEntity;
import com.kruemblegard.entity.FaultCrawlerEntity;
import com.kruemblegard.entity.KruemblegardBossEntity;
import com.kruemblegard.entity.MoogloomEntity;
import com.kruemblegard.entity.PebblitEntity;
import com.kruemblegard.entity.ScaralonBeetleEntity;
import com.kruemblegard.entity.ScatteredEndermanEntity;
import com.kruemblegard.entity.TraprockEntity;
import com.kruemblegard.entity.WyrdwingEntity;
import com.kruemblegard.entity.vehicle.KruemblegardBoatEntity;
import com.kruemblegard.entity.vehicle.KruemblegardChestBoatEntity;

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

    public static final RegistryObject<EntityType<ScatteredEndermanEntity>> SCATTERED_ENDERMAN =
        ENTITIES.register(
            "scattered_enderman",
            () -> EntityType.Builder.<ScatteredEndermanEntity>of(
                    ScatteredEndermanEntity::new, MobCategory.MONSTER)
                .sized(0.6f, 2.9f)
                .build(new ResourceLocation(Kruemblegard.MOD_ID, "scattered_enderman").toString())
        );

    public static final RegistryObject<EntityType<FaultCrawlerEntity>> FAULT_CRAWLER =
        ENTITIES.register(
            "fault_crawler",
            () -> EntityType.Builder.<FaultCrawlerEntity>of(
                    FaultCrawlerEntity::new, MobCategory.MONSTER)
                .sized(1.0f, 0.7f)
                .build(new ResourceLocation(Kruemblegard.MOD_ID, "fault_crawler").toString())
        );

    public static final RegistryObject<EntityType<ScaralonBeetleEntity>> SCARALON_BEETLE =
        ENTITIES.register(
            "scaralon_beetle",
            () -> EntityType.Builder.<ScaralonBeetleEntity>of(
                    ScaralonBeetleEntity::new, MobCategory.CREATURE)
                .sized(1.2f, 1.4f)
                .build(new ResourceLocation(Kruemblegard.MOD_ID, "scaralon_beetle").toString())
        );

    public static final RegistryObject<EntityType<WyrdwingEntity>> WYRDWING =
        ENTITIES.register(
            "wyrdwing",
            () -> EntityType.Builder.<WyrdwingEntity>of(
                    WyrdwingEntity::new, MobCategory.CREATURE)
                .sized(0.9f, 0.9f)
                .build(new ResourceLocation(Kruemblegard.MOD_ID, "wyrdwing").toString())
        );

    public static final RegistryObject<EntityType<MoogloomEntity>> MOOGLOOM =
        ENTITIES.register(
            "moogloom",
            () -> EntityType.Builder.<MoogloomEntity>of(
                    MoogloomEntity::new, MobCategory.CREATURE)
                .sized(0.9f, 1.4f)
                .build(new ResourceLocation(Kruemblegard.MOD_ID, "moogloom").toString())
        );

    public static final RegistryObject<EntityType<KruemblegardBoatEntity>> KRUEMBLEGARD_BOAT =
        ENTITIES.register(
            "kruemblegard_boat",
            () -> EntityType.Builder.<KruemblegardBoatEntity>of(KruemblegardBoatEntity::new, MobCategory.MISC)
                .sized(1.375F, 0.5625F)
                .clientTrackingRange(10)
                .updateInterval(3)
                .build(new ResourceLocation(Kruemblegard.MOD_ID, "kruemblegard_boat").toString())
        );

    public static final RegistryObject<EntityType<KruemblegardChestBoatEntity>> KRUEMBLEGARD_CHEST_BOAT =
        ENTITIES.register(
            "kruemblegard_chest_boat",
            () -> EntityType.Builder.<KruemblegardChestBoatEntity>of(KruemblegardChestBoatEntity::new, MobCategory.MISC)
                .sized(1.375F, 0.5625F)
                .clientTrackingRange(10)
                .updateInterval(3)
                .build(new ResourceLocation(Kruemblegard.MOD_ID, "kruemblegard_chest_boat").toString())
        );
}
