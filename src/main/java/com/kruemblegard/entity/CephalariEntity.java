package com.kruemblegard.entity;

import java.util.UUID;

import com.kruemblegard.registry.ModEntities;
import com.kruemblegard.entity.mount.CephalariMountEntity;
import com.kruemblegard.entity.mount.CephalariMounts;
import com.kruemblegard.registry.ModParticles;
import com.kruemblegard.registry.ModSounds;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.core.particles.ParticleOptions;

import org.jetbrains.annotations.Nullable;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Cephalari
 *
 * Implementation strategy: extend vanilla {@link Villager} so this entity inherits the full villager
 * brain/POI/profession/trading behavior, and the rest of the game treats it as a villager-class mob.
 */
public class CephalariEntity extends Villager implements GeoEntity {

    private static final RawAnimation IDLE_LOOP = RawAnimation.begin().thenLoop("animation.cephalari.idle");
    private static final RawAnimation WALK_LOOP = RawAnimation.begin().thenLoop("animation.cephalari.walk");
    private static final RawAnimation RIDING_LOOP = RawAnimation.begin().thenLoop("animation.cephalari.riding_pose");

    private static final RawAnimation ZOMBIFY_ONCE = RawAnimation.begin().thenPlay("animation.cephalari.zombify_cinematic");

    private static final int NON_WAYFALL_SUFFOCATION_INTERVAL_TICKS = 40;
    private static final float NON_WAYFALL_SUFFOCATION_DAMAGE = 1.0F;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private boolean zombifyInProgress = false;
    private int zombifyTicks = 0;
    private @Nullable String zombifyStoredMountId;
    private @Nullable EntityType<? extends Mob> zombifyZombieMountType;
    private @Nullable UUID zombifySpawnedMountUuid;
    private boolean zombifyMountSpawned = false;

    private boolean forwardingLinkedDamage = false;

    public CephalariEntity(EntityType<? extends Villager> type, Level level) {
        super(type, level);
    }

    @Override
    public BlockPos blockPosition() {
        if (this.getVehicle() instanceof CephalariMountEntity mount) {
            return mount.blockPosition();
        }

        return super.blockPosition();
    }

    @Override
    public SpawnGroupData finalizeSpawn(
        ServerLevelAccessor level,
        net.minecraft.world.DifficultyInstance difficulty,
        MobSpawnType spawnType,
        @Nullable SpawnGroupData spawnData,
        @Nullable net.minecraft.nbt.CompoundTag tag
    ) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, spawnType, spawnData, tag);

        if (level instanceof ServerLevel serverLevel) {
            if (!this.isBaby() && !this.isPassenger()) {
                String mountId = CephalariMounts.getOrAssignMountId(this);
                CephalariMounts.spawnMountAndRide(this, serverLevel, mountId);
            }
        }

        return result;
    }

    @Override
    public void aiStep() {
        if (!level().isClientSide && zombifyInProgress) {
            tickZombifySequence();
            super.aiStep();
            return;
        }

        super.aiStep();

        if (level().isClientSide) {
            return;
        }

        if (this.getVehicle() instanceof CephalariMountEntity mount) {
            // Keep the pair alive/dead together.
            if (!mount.isAlive() && this.isAlive()) {
                this.kill();
                return;
            }
        }

        if (level().dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return;
        }

        if (this.tickCount % NON_WAYFALL_SUFFOCATION_INTERVAL_TICKS == 0) {
            this.hurt(this.damageSources().drown(), NON_WAYFALL_SUFFOCATION_DAMAGE);
        }
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "baseController", 0, state -> {
            if (zombifyInProgress) {
                return PlayState.STOP;
            }

            if (this.isPassenger()) {
                state.setAnimation(RIDING_LOOP);
                return PlayState.CONTINUE;
            }

            state.setAnimation(state.isMoving() ? WALK_LOOP : IDLE_LOOP);
            return PlayState.CONTINUE;
        }));

        controllers.add(new AnimationController<>(this, "actionController", 0, state -> PlayState.STOP)
            .triggerableAnim("zombify", ZOMBIFY_ONCE));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!level().isClientSide && zombifyInProgress) {
            return false;
        }

        Entity attacker = source.getEntity();
        if (!level().isClientSide && attacker instanceof Zombie && level() instanceof ServerLevel serverLevel) {
            // Intercept lethal zombie damage to play the cinematic instead of instant swap.
            if (amount >= this.getHealth() && !this.isBaby()) {
                Difficulty difficulty = serverLevel.getDifficulty();

                boolean shouldConvert;
                if (difficulty == Difficulty.HARD) {
                    shouldConvert = true;
                } else if (difficulty == Difficulty.NORMAL) {
                    shouldConvert = serverLevel.getRandom().nextBoolean();
                } else {
                    shouldConvert = false;
                }

                if (shouldConvert) {
                    startZombifySequence(serverLevel);
                    // Cancel the lethal hit; the sequence will handle the swap.
                    return false;
                }
            }
        }

        if (!level().isClientSide && !forwardingLinkedDamage && this.getVehicle() instanceof CephalariMountEntity mount) {
            boolean result = super.hurt(source, amount);
            if (result && mount.isAlive()) {
                mount.hurtLinkedFromCephalari(source, amount);
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

        if (!forwardingLinkedDamage && this.getVehicle() instanceof CephalariMountEntity mount && mount.isAlive()) {
            mount.healLinkedFromCephalari(amount);
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

    private void startZombifySequence(ServerLevel serverLevel) {
        if (zombifyInProgress) {
            return;
        }

        zombifyInProgress = true;
        zombifyTicks = 0;
        zombifyMountSpawned = false;

        String mountId = CephalariMounts.getMountId(this);
        if (mountId == null && this.getVehicle() != null) {
            mountId = CephalariMounts.getMountIdFromVehicle(this.getVehicle());
        }
        zombifyStoredMountId = mountId;

        if (this.getVehicle() instanceof CephalariMountEntity mount) {
            this.stopRiding();
            mount.discard();
        }

        // During conversion, manifest a *zombie-type* mount (never a Zoglin).
        int roll = serverLevel.getRandom().nextInt(4);
        zombifyZombieMountType = switch (roll) {
            case 0 -> EntityType.ZOMBIE;
            case 1 -> EntityType.HUSK;
            case 2 -> EntityType.DROWNED;
            default -> EntityType.ZOMBIFIED_PIGLIN;
        };
        zombifySpawnedMountUuid = null;

        setNoAi(true);
        getNavigation().stop();
        setDeltaMovement(0.0D, 0.0D, 0.0D);

        triggerAnim("actionController", "zombify");
        playSound(ModSounds.CEPHALARI_ZOMBIFY.get(), 0.95F, 0.95F + random.nextFloat() * 0.1F);

        spawnBurst(serverLevel, ModParticles.CEPHALARI_ZOMBIFY.get(), 18, 0.35D, 0.35D, 0.35D, 0.06D);
        spawnBurst(serverLevel, ModParticles.CEPHALARI_SHELL_DUST.get(), 14, 0.30D, 0.25D, 0.30D, 0.04D);
    }

    private void tickZombifySequence() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        zombifyTicks++;
        getNavigation().stop();
        setDeltaMovement(0.0D, 0.0D, 0.0D);

        // +0.8s: manifest zombie mount under the Cephalari.
        if (!zombifyMountSpawned && zombifyTicks >= 16) {
            zombifyMountSpawned = true;

            if (zombifyZombieMountType != null) {
                Mob mount = zombifyZombieMountType.create(serverLevel);
                if (mount != null) {
                    mount.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                    mount.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(mount.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);

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

                    serverLevel.addFreshEntity(mount);
                    this.startRiding(mount, true);
                    zombifySpawnedMountUuid = mount.getUUID();
                }
            }

            spawnBurst(serverLevel, ModParticles.CEPHALARI_SHELL_FRAGMENT.get(), 18, 0.40D, 0.25D, 0.40D, 0.08D);
            spawnBurst(serverLevel, ModParticles.CEPHALARI_SHELL_SPIRAL.get(), 12, 0.25D, 0.35D, 0.25D, 0.03D);
        }

        // +2.2s: swap to zombified entity and keep the mount.
        if (zombifyTicks >= 44) {
            CephalariZombieEntity zombie = ModEntities.CEPHALARI_ZOMBIE.get().create(serverLevel);
            if (zombie != null) {
                zombie.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                zombie.setVillagerData(this.getVillagerData());
                zombie.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.CONVERSION, null, null);
                zombie.setNoAi(true);
                zombie.setCustomName(this.getCustomName());
                zombie.setCustomNameVisible(this.isCustomNameVisible());

                if (zombifyStoredMountId != null) {
                    CephalariMounts.setMountId(zombie, zombifyStoredMountId);
                }

                serverLevel.addFreshEntity(zombie);

                if (zombifySpawnedMountUuid != null) {
                    Entity mount = serverLevel.getEntity(zombifySpawnedMountUuid);
                    if (mount instanceof Mob mobMount) {
                        zombie.startRiding(mobMount, true);
                    }
                }

                zombie.setNoAi(false);
            }

            spawnBurst(serverLevel, ModParticles.CEPHALARI_ZOMBIFY.get(), 14, 0.25D, 0.30D, 0.25D, 0.05D);
            this.discard();
        }
    }

    private void spawnBurst(ServerLevel serverLevel, ParticleOptions particle, int count, double dx, double dy, double dz, double speed) {
        serverLevel.sendParticles(
            particle,
            getX(),
            getY() + 0.85D,
            getZ(),
            count,
            dx, dy, dz,
            speed
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    /**
     * Prevent cross-species breeding:
     * - Cephalari + Cephalari -> Cephalari
     * - Cephalari + Villager -> no child
     */
    @Override
    public @Nullable CephalariEntity getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        if (!(otherParent instanceof CephalariEntity)) {
            return null;
        }

        CephalariEntity baby = ModEntities.CEPHALARI.get().create(level);
        if (baby != null) {
            VillagerData data = this.getVillagerData();
            baby.setVillagerData(new VillagerData(data.getType(), VillagerProfession.NONE, 1));
        }
        return baby;
    }

    /**
     * Keep vanilla villager zombification behavior, but convert into the Cephalari zombie variant.
     */
    @Override
    public void die(DamageSource source) {
        // Zombification is handled in hurt() so we can play the cinematic.

        if (this.getVehicle() instanceof CephalariMountEntity mount) {
            this.stopRiding();
            mount.discard();
        }

        super.die(source);
    }
}
