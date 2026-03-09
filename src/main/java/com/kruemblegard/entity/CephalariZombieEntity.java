package com.kruemblegard.entity;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.registry.ModEntities;
import com.kruemblegard.entity.mount.CephalariMounts;
import com.kruemblegard.registry.ModParticles;
import com.kruemblegard.registry.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
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

    private static final ResourceLocation TEXTURE_ZOMBIE = new ResourceLocation(
        Kruemblegard.MOD_ID,
        "textures/entity/cephalari/cephalari_zombie/cephalari_zombie.png"
    );
    private static final ResourceLocation TEXTURE_HUSK = new ResourceLocation(
        Kruemblegard.MOD_ID,
        "textures/entity/cephalari/cephalari_zombie/cephalari_husk.png"
    );
    private static final ResourceLocation TEXTURE_DROWNED = new ResourceLocation(
        Kruemblegard.MOD_ID,
        "textures/entity/cephalari/cephalari_zombie/cephalari_drown.png"
    );
    private static final ResourceLocation TEXTURE_DROWNED_OUTER = new ResourceLocation(
        Kruemblegard.MOD_ID,
        "textures/entity/cephalari/cephalari_zombie/cephalari_drowned_outer_layer.png"
    );

    private static final RawAnimation IDLE_LOOP = RawAnimation.begin().thenLoop("animation.cephalari_zombie.idle");
    private static final RawAnimation WALK_LOOP = RawAnimation.begin().thenLoop("animation.cephalari_zombie.walk");
    private static final RawAnimation RIDING_LOOP = RawAnimation.begin().thenLoop("animation.cephalari_zombie.riding_pose");

    private static final RawAnimation ATTACK_ONCE = RawAnimation.begin().thenPlay("animation.cephalari_zombie.attack");
    private static final RawAnimation HURT_ONCE = RawAnimation.begin().thenPlay("animation.cephalari_zombie.hurt");

    private static final String NBT_ADULT_ZOMBIE_VARIANT = "KruemblegardCephalariZombieAdultVariant";
    private static final String NBT_ADULT_MOUNT_TEXTURE_VARIANT = "KruemblegardCephalariZombieAdultMountTextureVariant";

    private static final int NO_ZOMBIE_VARIANT = -1;
    private static final int ZOMBIE_VARIANTS = 5;
    private static final int MOUNT_TEXTURE_VARIANTS = 6;

    private static final EntityDataAccessor<Integer> DATA_ADULT_ZOMBIE_VARIANT =
        SynchedEntityData.defineId(CephalariZombieEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ADULT_MOUNT_TEXTURE_VARIANT =
        SynchedEntityData.defineId(CephalariZombieEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private boolean forwardingLinkedDamage = false;

    private int hurtAnimCooldownTicks = 0;

    public CephalariZombieEntity(EntityType<? extends ZombieVillager> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        // 1..ZOMBIE_VARIANTS, or -1 for none (babies)
        this.entityData.define(DATA_ADULT_ZOMBIE_VARIANT, NO_ZOMBIE_VARIANT);
        // 1..MOUNT_TEXTURE_VARIANTS (base layer)
        this.entityData.define(DATA_ADULT_MOUNT_TEXTURE_VARIANT, 1);
    }

    public boolean hasAdultMountAppearance() {
        return !this.isBaby() && this.entityData.get(DATA_ADULT_ZOMBIE_VARIANT) != NO_ZOMBIE_VARIANT;
    }

    public int getAdultZombieVariant() {
        return this.entityData.get(DATA_ADULT_ZOMBIE_VARIANT);
    }

    public int getAdultMountTextureVariant() {
        return this.entityData.get(DATA_ADULT_MOUNT_TEXTURE_VARIANT);
    }

    public boolean isDrownedVariant() {
        return !this.isBaby() && this.entityData.get(DATA_ADULT_ZOMBIE_VARIANT) == 3;
    }

    public ResourceLocation getBodyTextureResource() {
        int variant = this.entityData.get(DATA_ADULT_ZOMBIE_VARIANT);
        return switch (variant) {
            case 2 -> TEXTURE_HUSK;
            case 3 -> TEXTURE_DROWNED;
            default -> TEXTURE_ZOMBIE;
        };
    }

    public ResourceLocation getDrownedOuterTextureResource() {
        return TEXTURE_DROWNED_OUTER;
    }

    public void setAdultZombieVariant(int variant) {
        int clamped = Math.max(1, Math.min(ZOMBIE_VARIANTS, variant));
        this.entityData.set(DATA_ADULT_ZOMBIE_VARIANT, clamped);
    }

    public void setAdultMountTextureVariant(int variant) {
        int clamped = Math.max(1, Math.min(MOUNT_TEXTURE_VARIANTS, variant));
        this.entityData.set(DATA_ADULT_MOUNT_TEXTURE_VARIANT, clamped);
    }

    private void ensureAdultVariantsAssigned() {
        if (this.isBaby()) {
            this.entityData.set(DATA_ADULT_ZOMBIE_VARIANT, NO_ZOMBIE_VARIANT);
            return;
        }

        if (this.entityData.get(DATA_ADULT_ZOMBIE_VARIANT) == NO_ZOMBIE_VARIANT) {
            setAdultZombieVariant(1 + this.getRandom().nextInt(ZOMBIE_VARIANTS));
        }

        int tex = this.entityData.get(DATA_ADULT_MOUNT_TEXTURE_VARIANT);
        if (tex < 1 || tex > MOUNT_TEXTURE_VARIANTS) {
            setAdultMountTextureVariant(1);
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
        SpawnGroupData spawnData,
        net.minecraft.nbt.CompoundTag tag
    ) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnData, tag);

        if (!this.level().isClientSide) {
            if (this.isBaby()) {
                ensureBabyHasMount(level);
            } else {
                ensureAdultVariantsAssigned();
                if (this.entityData.get(DATA_ADULT_MOUNT_TEXTURE_VARIANT) == 1) {
                    setAdultMountTextureVariant(1 + this.getRandom().nextInt(MOUNT_TEXTURE_VARIANTS));
                }
            }
        }

        return data;
    }

    private void ensureBabyHasMount(ServerLevelAccessor accessor) {
        if (!(accessor instanceof ServerLevel level)) {
            return;
        }

        if (!this.isBaby() || this.isPassenger()) {
            return;
        }

        // Pick from a small, vanilla-ish jockey pool.
        int pick = this.getRandom().nextInt(5);
        EntityType<? extends Mob> mountType = switch (pick) {
            case 0 -> EntityType.CHICKEN;
            case 1 -> EntityType.WOLF;
            case 2 -> EntityType.PIG;
            case 3 -> EntityType.SHEEP;
            default -> EntityType.RABBIT;
        };

        Mob mount = mountType.create(level);
        if (mount == null) {
            return;
        }

        mount.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
        mount.finalizeSpawn(level, level.getCurrentDifficultyAt(mount.blockPosition()), MobSpawnType.JOCKEY, null, null);
        mount.setPersistenceRequired();

        level.addFreshEntity(mount);
        this.startRiding(mount, true);
        this.setPersistenceRequired();
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(NBT_ADULT_ZOMBIE_VARIANT, this.entityData.get(DATA_ADULT_ZOMBIE_VARIANT));
        tag.putInt(NBT_ADULT_MOUNT_TEXTURE_VARIANT, this.entityData.get(DATA_ADULT_MOUNT_TEXTURE_VARIANT));
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains(NBT_ADULT_ZOMBIE_VARIANT)) {
            int variant = tag.getInt(NBT_ADULT_ZOMBIE_VARIANT);
            if (variant == NO_ZOMBIE_VARIANT) {
                this.entityData.set(DATA_ADULT_ZOMBIE_VARIANT, NO_ZOMBIE_VARIANT);
            } else {
                setAdultZombieVariant(variant);
            }
        }

        if (tag.contains(NBT_ADULT_MOUNT_TEXTURE_VARIANT)) {
            setAdultMountTextureVariant(tag.getInt(NBT_ADULT_MOUNT_TEXTURE_VARIANT));
        }
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

        if (hurtAnimCooldownTicks > 0) {
            hurtAnimCooldownTicks--;
        }

        // Legacy cleanup: old-world zombie cephalari may still be riding a zombie mount.
        Entity vehicle = this.getVehicle();
        if (vehicle instanceof Mob mob
            && (mob instanceof Zombie
                || mob instanceof Husk
                || mob instanceof Drowned
                || mob instanceof ZombifiedPiglin
                || mob instanceof Zoglin)) {
            int variant;
            if (mob instanceof Zombie) {
                variant = 1;
            } else if (mob instanceof Husk) {
                variant = 2;
            } else if (mob instanceof Drowned) {
                variant = 3;
            } else if (mob instanceof ZombifiedPiglin) {
                variant = 4;
            } else {
                variant = 5;
            }

            setAdultZombieVariant(variant);
            this.stopRiding();
            mob.discard();
        }

        if (!this.isBaby()) {
            ensureAdultVariantsAssigned();
        }
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

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean didHurt = super.doHurtTarget(target);
        if (didHurt && !level().isClientSide) {
            triggerAnim("attackController", "attack");
        }
        return didHurt;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);
        if (!level().isClientSide && result && hurtAnimCooldownTicks <= 0) {
            hurtAnimCooldownTicks = 10;
            triggerAnim("hurtController", "hurt");
        }
        return result;
    }

    @Override
    public void heal(float amount) {
        super.heal(amount);
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

        controllers.add(new AnimationController<>(this, "attackController", 0, state -> PlayState.STOP)
            .triggerableAnim("attack", ATTACK_ONCE));

        controllers.add(new AnimationController<>(this, "hurtController", 0, state -> PlayState.STOP)
            .triggerableAnim("hurt", HURT_ONCE));
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

            T converted = super.convertTo(target, keepEquipment);
            if (converted instanceof CephalariEntity cephalari) {
                cephalari.setVillagerData(this.getVillagerData());
                cephalari.setNoAi(this.isNoAi());
                cephalari.setCustomName(this.getCustomName());
                cephalari.setCustomNameVisible(this.isCustomNameVisible());

                if (storedMountId != null) {
                    cephalari.setAdultMountId(storedMountId);
                }

                if (!cephalari.isBaby()) {
                    cephalari.setAdultMountTextureVariant(this.getAdultMountTextureVariant());
                }
            }
            return converted;
        }

        return super.convertTo(entityType, keepEquipment);
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
    }
}
