package com.kruemblegard.entity;

import com.kruemblegard.registry.ModEntities;
import com.kruemblegard.entity.mount.CephalariMountEntity;
import com.kruemblegard.entity.mount.CephalariMounts;
import com.kruemblegard.registry.ModParticles;
import com.kruemblegard.registry.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ServerLevelAccessor;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Zombified Cephalari.
 *
 * Extends vanilla {@link ZombieVillager} so curing/conversion mechanics behave like villagers,
 * but swaps the cured result to {@link CephalariEntity}.
 */
public class CephalariZombieEntity extends ZombieVillager implements GeoEntity {

    private static final RawAnimation IDLE_LOOP = RawAnimation.begin().thenLoop("animation.cephalari_zombie.idle");
    private static final RawAnimation WALK_LOOP = RawAnimation.begin().thenLoop("animation.cephalari_zombie.walk");
    private static final RawAnimation RIDING_LOOP = RawAnimation.begin().thenLoop("animation.cephalari_zombie.riding_pose");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private boolean forwardingLinkedDamage = false;

    public CephalariZombieEntity(EntityType<? extends ZombieVillager> type, Level level) {
        super(type, level);
    }

    @Override
    public BlockPos blockPosition() {
        if (this.getVehicle() instanceof Mob mount) {
            return mount.blockPosition();
        }

        return super.blockPosition();
    }

    @Override
    public SpawnGroupData finalizeSpawn(
        ServerLevelAccessor level,
        net.minecraft.world.DifficultyInstance difficulty,
        MobSpawnType spawnType,
        SpawnGroupData spawnData,
        net.minecraft.nbt.CompoundTag tag
    ) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnData, tag);

        // Only assign a zombie-type mount for "real" spawns. Conversions handle their own mount cinematic.
        if (spawnType != MobSpawnType.CONVERSION
            && !this.level().isClientSide
            && level instanceof ServerLevel serverLevel
            && !this.isPassenger()) {
            ensureZombieMount(serverLevel);
        }

        return data;
    }

    private void ensureZombieMount(ServerLevel level) {
        if (this.isPassenger()) {
            return;
        }

        // Pick a zombie-type mount.
        int roll = this.getRandom().nextInt(5);
        EntityType<? extends Mob> mountType = switch (roll) {
            case 0 -> EntityType.ZOMBIE;
            case 1 -> EntityType.HUSK;
            case 2 -> EntityType.DROWNED;
            case 3 -> EntityType.ZOMBIFIED_PIGLIN;
            default -> EntityType.ZOGLIN;
        };

        Mob mount = mountType.create(level);
        if (mount == null) {
            return;
        }

        mount.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        mount.finalizeSpawn(level, level.getCurrentDifficultyAt(mount.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);

        // Let the mount navigate like a normal mob.
        if (mount instanceof Zombie z) {
            z.setCanPickUpLoot(false);
        }
        if (mount instanceof Husk h) {
            h.setCanPickUpLoot(false);
        }
        if (mount instanceof Drowned d) {
            d.setCanPickUpLoot(false);
        }
        if (mount instanceof ZombifiedPiglin p) {
            p.setCanPickUpLoot(false);
        }
        if (mount instanceof Zoglin z) {
            z.setCanPickUpLoot(false);
        }

        level.addFreshEntity(mount);
        this.startRiding(mount, true);
    }

    public boolean isForwardingLinkedDamage() {
        return forwardingLinkedDamage;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (level().isClientSide) {
            return;
        }

        if (this.getVehicle() instanceof Mob mount) {
            if (!mount.isAlive() && this.isAlive()) {
                this.kill();
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!level().isClientSide && !forwardingLinkedDamage && this.getVehicle() instanceof Mob mount && mount.isAlive()) {
            boolean result = super.hurt(source, amount);
            if (result) {
                forwardingLinkedDamage = true;
                try {
                    mount.hurt(source, amount);
                } finally {
                    forwardingLinkedDamage = false;
                }
            }
            return result;
        }

        return super.hurt(source, amount);
    }

    public boolean hurtLinkedFromMount(DamageSource source, float amount) {
        if (level().isClientSide) {
            return super.hurt(source, amount);
        }

        if (forwardingLinkedDamage) {
            return super.hurt(source, amount);
        }

        forwardingLinkedDamage = true;
        try {
            return super.hurt(source, amount);
        } finally {
            forwardingLinkedDamage = false;
        }
    }

    @Override
    public void heal(float amount) {
        super.heal(amount);

        if (level().isClientSide) {
            return;
        }

        if (!forwardingLinkedDamage && this.getVehicle() instanceof Mob mount && mount.isAlive()) {
            forwardingLinkedDamage = true;
            try {
                mount.heal(amount);
            } finally {
                forwardingLinkedDamage = false;
            }
        }
    }

    public void healLinkedFromMount(float amount) {
        if (level().isClientSide) {
            super.heal(amount);
            return;
        }

        if (forwardingLinkedDamage) {
            super.heal(amount);
            return;
        }

        forwardingLinkedDamage = true;
        try {
            super.heal(amount);
        } finally {
            forwardingLinkedDamage = false;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "baseController", 0, state -> {
            if (this.isPassenger()) {
                state.setAnimation(RIDING_LOOP);
                return PlayState.CONTINUE;
            }

            state.setAnimation(state.isMoving() ? WALK_LOOP : IDLE_LOOP);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public <T extends Mob> T convertTo(EntityType<T> entityType, boolean keepEquipment) {
        if (entityType == EntityType.VILLAGER) {
            @SuppressWarnings("unchecked")
            EntityType<T> target = (EntityType<T>) ModEntities.CEPHALARI.get();

            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ModParticles.CEPHALARI_CURE.get(), getX(), getY() + 0.85D, getZ(), 18, 0.35D, 0.35D, 0.35D, 0.06D);
                serverLevel.sendParticles(ModParticles.CEPHALARI_SHELL_SPIRAL.get(), getX(), getY() + 0.85D, getZ(), 10, 0.25D, 0.35D, 0.25D, 0.03D);
                serverLevel.sendParticles(ModParticles.CEPHALARI_SHELL_DUST.get(), getX(), getY() + 0.85D, getZ(), 12, 0.30D, 0.25D, 0.30D, 0.04D);
            }
            playSound(ModSounds.CEPHALARI_CURE.get(), 0.95F, 0.95F + random.nextFloat() * 0.1F);

            String storedMountId = CephalariMounts.getMountId(this);
            if (this.getVehicle() instanceof CephalariMountEntity cephalariMount) {
                this.stopRiding();
                cephalariMount.discard();
            } else if (this.getVehicle() instanceof Mob zombieMount) {
                // Curing always removes the zombie mount and restores the Cephalari mount type.
                this.stopRiding();
                zombieMount.discard();
            }

            T converted = super.convertTo(target, keepEquipment);
            if (converted instanceof CephalariEntity cephalari) {
                cephalari.setVillagerData(this.getVillagerData());
                cephalari.setNoAi(this.isNoAi());
                cephalari.setCustomName(this.getCustomName());
                cephalari.setCustomNameVisible(this.isCustomNameVisible());

                if (storedMountId != null) {
                    CephalariMounts.setMountId(cephalari, storedMountId);
                }

                if (cephalari.level() instanceof ServerLevel serverLevel && !cephalari.isPassenger() && !cephalari.isBaby()) {
                    String mountId = CephalariMounts.getOrAssignMountId(cephalari);
                    CephalariMounts.spawnMountAndRide(cephalari, serverLevel, mountId);
                }
            }
            return converted;
        }

        return super.convertTo(entityType, keepEquipment);
    }

    @Override
    public void die(DamageSource source) {
        if (!level().isClientSide) {
            if (this.getVehicle() instanceof CephalariMountEntity mount) {
                this.stopRiding();
                mount.discard();
            } else if (this.getVehicle() instanceof Mob) {
                // Zombie mounts persist independently after the rider dies.
                this.stopRiding();
            }
        }

        super.die(source);
    }
}
