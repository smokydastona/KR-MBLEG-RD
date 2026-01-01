package com.kruemblegard.registry;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.projectile.ArcaneStormProjectileEntity;
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

    public static final RegistryObject<EntityType<MeteorArmEntity>> METEOR_ARM =
            PROJECTILES.register("meteor_arm",
                    () -> EntityType.Builder.<MeteorArmEntity>of(MeteorArmEntity::new, MobCategory.MISC)
                            .sized(1.0f, 1.0f)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build("meteor_arm"));

    public static final RegistryObject<EntityType<ArcaneStormProjectileEntity>> ARCANE_STORM =
            PROJECTILES.register("arcane_storm",
                    () -> EntityType.Builder.<ArcaneStormProjectileEntity>of(ArcaneStormProjectileEntity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build("arcane_storm"));
}
