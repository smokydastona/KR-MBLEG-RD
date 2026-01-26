package com.kruemblegard.registry;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.projectile.ArcaneStormProjectileEntity;
import com.kruemblegard.entity.projectile.KruemblegardPhase1BoltEntity;
import com.kruemblegard.entity.projectile.KruemblegardPhase2BoltEntity;
import com.kruemblegard.entity.projectile.KruemblegardPhase3MeteorEntity;
import com.kruemblegard.entity.projectile.KruemblegardPhase4BeamBoltEntity;
import com.kruemblegard.entity.projectile.MeteorArmEntity;
import com.kruemblegard.entity.projectile.RuneBoltEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModProjectileEntities {

    public static final DeferredRegister<EntityType<?>> PROJECTILES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Kruemblegard.MOD_ID);

    public static final RegistryObject<EntityType<RuneBoltEntity>> RUNE_BOLT =
            PROJECTILES.register("rune_bolt",
                    () -> EntityType.Builder.<RuneBoltEntity>of(RuneBoltEntity::new, MobCategory.MISC)
                            .sized(0.4f, 0.4f)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build("rune_bolt"));

    public static final RegistryObject<EntityType<KruemblegardPhase1BoltEntity>> PHASE1_BOLT =
            PROJECTILES.register("kruemblegard_phase1_bolt",
                    () -> EntityType.Builder.<KruemblegardPhase1BoltEntity>of(KruemblegardPhase1BoltEntity::new, MobCategory.MISC)
                            .sized(0.45f, 0.45f)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build("kruemblegard_phase1_bolt"));

    public static final RegistryObject<EntityType<KruemblegardPhase2BoltEntity>> PHASE2_BOLT =
            PROJECTILES.register("kruemblegard_phase2_bolt",
                    () -> EntityType.Builder.<KruemblegardPhase2BoltEntity>of(KruemblegardPhase2BoltEntity::new, MobCategory.MISC)
                            .sized(0.45f, 0.45f)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build("kruemblegard_phase2_bolt"));

    public static final RegistryObject<EntityType<MeteorArmEntity>> METEOR_ARM =
            PROJECTILES.register("meteor_arm",
                    () -> EntityType.Builder.<MeteorArmEntity>of(MeteorArmEntity::new, MobCategory.MISC)
                            .sized(1.0f, 1.0f)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build("meteor_arm"));

    public static final RegistryObject<EntityType<KruemblegardPhase3MeteorEntity>> PHASE3_METEOR =
            PROJECTILES.register("kruemblegard_phase3_meteor",
                    () -> EntityType.Builder.<KruemblegardPhase3MeteorEntity>of(KruemblegardPhase3MeteorEntity::new, MobCategory.MISC)
                            .sized(1.0f, 1.0f)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build("kruemblegard_phase3_meteor"));

    public static final RegistryObject<EntityType<ArcaneStormProjectileEntity>> ARCANE_STORM =
            PROJECTILES.register("arcane_storm",
                    () -> EntityType.Builder.<ArcaneStormProjectileEntity>of(ArcaneStormProjectileEntity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build("arcane_storm"));

    public static final RegistryObject<EntityType<KruemblegardPhase4BeamBoltEntity>> PHASE4_BEAM_BOLT =
            PROJECTILES.register("kruemblegard_phase4_beam_bolt",
                    () -> EntityType.Builder.<KruemblegardPhase4BeamBoltEntity>of(KruemblegardPhase4BeamBoltEntity::new, MobCategory.MISC)
                            .sized(0.6f, 0.6f)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build("kruemblegard_phase4_beam_bolt"));
}
