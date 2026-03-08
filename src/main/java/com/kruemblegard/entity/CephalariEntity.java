package com.kruemblegard.entity;

import com.kruemblegard.registry.ModEntities;
import com.kruemblegard.entity.mount.CephalariMountEntity;
import com.kruemblegard.entity.mount.CephalariMounts;
import com.kruemblegard.registry.ModParticles;
import com.kruemblegard.registry.ModSounds;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Zombie;
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

    private static final String NBT_ADULT_MOUNT_VARIANT = "KruemblegardCephalariAdultMountVariant";
    private static final String NBT_ADULT_MOUNT_TEXTURE_VARIANT = "KruemblegardCephalariAdultMountTextureVariant";

    private static final int NO_MOUNT_VARIANT = -1;
    private static final int MOUNT_TEXTURE_VARIANTS = 6;

    private static final EntityDataAccessor<Integer> DATA_ADULT_MOUNT_VARIANT =
        SynchedEntityData.defineId(CephalariEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ADULT_MOUNT_TEXTURE_VARIANT =
        SynchedEntityData.defineId(CephalariEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private boolean zombifyInProgress = false;
    private int zombifyTicks = 0;
    private @Nullable String zombifyStoredMountId;
    private int zombifyZombieVariant = 1;
    private boolean zombifyMountSpawned = false;

    private boolean forwardingLinkedDamage = false;

    public CephalariEntity(EntityType<? extends Villager> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ADULT_MOUNT_VARIANT, NO_MOUNT_VARIANT);
        // 1..MOUNT_TEXTURE_VARIANTS (1-based because textures are named _1.._6)
        this.entityData.define(DATA_ADULT_MOUNT_TEXTURE_VARIANT, 1);
    }

    public boolean hasAdultMountAppearance() {
        return !this.isBaby() && this.entityData.get(DATA_ADULT_MOUNT_VARIANT) != NO_MOUNT_VARIANT;
    }

    public int getAdultMountVariant() {
        return this.entityData.get(DATA_ADULT_MOUNT_VARIANT);
    }

    public int getAdultMountTextureVariant() {
        return this.entityData.get(DATA_ADULT_MOUNT_TEXTURE_VARIANT);
    }

    public void setAdultMountVariant(int variant) {
        if (this.isBaby()) {
            this.entityData.set(DATA_ADULT_MOUNT_VARIANT, NO_MOUNT_VARIANT);
            return;
        }

        int clamped = Math.max(0, Math.min(3, variant));
        this.entityData.set(DATA_ADULT_MOUNT_VARIANT, clamped);
        String mountId = CephalariMounts.getMountIdByVariantIndex(clamped);
        if (mountId != null) {
            CephalariMounts.setMountId(this, mountId);
        }
    }

    public void setAdultMountTextureVariant(int variant) {
        int clamped = Math.max(1, Math.min(MOUNT_TEXTURE_VARIANTS, variant));
        this.entityData.set(DATA_ADULT_MOUNT_TEXTURE_VARIANT, clamped);
    }

    public void setAdultMountId(String mountId) {
        if (this.isBaby()) {
            return;
        }

        CephalariMounts.setMountId(this, mountId);
        int variant = CephalariMounts.getMountVariantIndex(mountId);
        if (variant != NO_MOUNT_VARIANT) {
            this.entityData.set(DATA_ADULT_MOUNT_VARIANT, variant);
        }
    }

    private void ensureAdultMountAppearance() {
        if (this.isBaby()) {
            if (this.entityData.get(DATA_ADULT_MOUNT_VARIANT) != NO_MOUNT_VARIANT) {
                this.entityData.set(DATA_ADULT_MOUNT_VARIANT, NO_MOUNT_VARIANT);
            }
            return;
        }

        if (this.entityData.get(DATA_ADULT_MOUNT_VARIANT) == NO_MOUNT_VARIANT) {
            String mountId = CephalariMounts.getOrAssignMountId(this);
            int variant = CephalariMounts.getMountVariantIndex(mountId);
            if (variant == NO_MOUNT_VARIANT) {
                variant = 0;
                CephalariMounts.setMountId(this, "spiral_strider");
            }
            this.entityData.set(DATA_ADULT_MOUNT_VARIANT, variant);
        }

        int textureVariant = this.entityData.get(DATA_ADULT_MOUNT_TEXTURE_VARIANT);
        if (textureVariant < 1 || textureVariant > MOUNT_TEXTURE_VARIANTS) {
            this.entityData.set(DATA_ADULT_MOUNT_TEXTURE_VARIANT, 1);
        }
    }

    @Override
    public BlockPos blockPosition() {
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

        if (level instanceof ServerLevel) {
            if (!this.isBaby()) {
                ensureAdultMountAppearance();
                if (this.entityData.get(DATA_ADULT_MOUNT_TEXTURE_VARIANT) == 1) {
                    this.entityData.set(DATA_ADULT_MOUNT_TEXTURE_VARIANT, 1 + this.getRandom().nextInt(MOUNT_TEXTURE_VARIANTS));
                }
            }
        }

        return result;
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(NBT_ADULT_MOUNT_VARIANT, this.entityData.get(DATA_ADULT_MOUNT_VARIANT));
        tag.putInt(NBT_ADULT_MOUNT_TEXTURE_VARIANT, this.entityData.get(DATA_ADULT_MOUNT_TEXTURE_VARIANT));
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains(NBT_ADULT_MOUNT_VARIANT)) {
            int variant = tag.getInt(NBT_ADULT_MOUNT_VARIANT);
            this.entityData.set(DATA_ADULT_MOUNT_VARIANT, variant);
            String mountId = CephalariMounts.getMountIdByVariantIndex(variant);
            if (mountId != null) {
                CephalariMounts.setMountId(this, mountId);
            }
        }

        if (tag.contains(NBT_ADULT_MOUNT_TEXTURE_VARIANT)) {
            int textureVariant = tag.getInt(NBT_ADULT_MOUNT_TEXTURE_VARIANT);
            int clamped = Math.max(1, Math.min(MOUNT_TEXTURE_VARIANTS, textureVariant));
            this.entityData.set(DATA_ADULT_MOUNT_TEXTURE_VARIANT, clamped);
        }
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

        // Legacy cleanup: if an old-world Cephalari is still riding a mount entity, merge it back into a single-mob state.
        if (this.getVehicle() instanceof CephalariMountEntity mount) {
            String mountId = CephalariMounts.getMountIdFromVehicle(mount);
            if (mountId != null) {
                CephalariMounts.setMountId(this, mountId);
                this.entityData.set(DATA_ADULT_MOUNT_VARIANT, CephalariMounts.getMountVariantIndex(mountId));
            }
            this.stopRiding();
            mount.discard();
        }

        if (!this.isBaby()) {
            ensureAdultMountAppearance();
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

            if (this.hasAdultMountAppearance()) {
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

        return super.hurt(source, amount);
    }

    @Override
    public void heal(float amount) {
        super.heal(amount);
    }

    public boolean isForwardingLinkedDamage() {
        return forwardingLinkedDamage;
    }

    public void hurtLinkedFromMount(DamageSource source, float amount) {
        if (level().isClientSide) {
            super.hurt(source, amount);
            return;
        }

        if (forwardingLinkedDamage) {
            super.hurt(source, amount);
            return;
        }

        forwardingLinkedDamage = true;
        try {
            super.hurt(source, amount);
        } finally {
            forwardingLinkedDamage = false;
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

        // During conversion, pick a zombie mount-variant for the adult model (1..5).
        zombifyZombieVariant = 1 + serverLevel.getRandom().nextInt(5);

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

        // +0.8s: manifest burst effects (legacy mount spawn removed; this is now a single-mob visual system).
        if (!zombifyMountSpawned && zombifyTicks >= 16) {
            zombifyMountSpawned = true;

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

                zombie.setAdultZombieVariant(zombifyZombieVariant);
                zombie.setAdultMountTextureVariant(this.getAdultMountTextureVariant());

                serverLevel.addFreshEntity(zombie);
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
        super.die(source);
    }
}
