package com.kruemblegard.entity;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.registry.ModEntities;
import com.kruemblegard.entity.adultform.CephalariAdultForms;
import com.kruemblegard.registry.ModParticles;
import com.kruemblegard.registry.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.player.Player;
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

    // Textures: one shared inner/base texture, plus an outer overlay variant.
    private static final ResourceLocation TEXTURE_INNER = new ResourceLocation(
        Kruemblegard.MOD_ID,
        "textures/entity/cephalari/cephalari_zombie/cephalari_inner_layer.png"
    );
    private static final ResourceLocation TEXTURE_OUTER_ZOMBIE = new ResourceLocation(
        Kruemblegard.MOD_ID,
        "textures/entity/cephalari/cephalari_zombie/cephalari_zombie_outer_layer.png"
    );
    private static final ResourceLocation TEXTURE_OUTER_HUSK = new ResourceLocation(
        Kruemblegard.MOD_ID,
        "textures/entity/cephalari/cephalari_zombie/cephalari_husk_outer_layer.png"
    );
    private static final ResourceLocation TEXTURE_OUTER_DROWNED = new ResourceLocation(
        Kruemblegard.MOD_ID,
        "textures/entity/cephalari/cephalari_zombie/cephalari_drowned_outer_layer.png"
    );

    private static final RawAnimation IDLE_LOOP = RawAnimation.begin().thenLoop("animation.cephalari_zombie.idle");
    private static final RawAnimation WALK_LOOP = RawAnimation.begin().thenLoop("animation.cephalari_zombie.walk");
    private static final RawAnimation RIDING_LOOP = RawAnimation.begin().thenLoop("animation.cephalari_zombie.riding_pose");

    private static final RawAnimation ATTACK_ONCE = RawAnimation.begin().thenPlay("animation.cephalari_zombie.attack");
    private static final RawAnimation HURT_ONCE = RawAnimation.begin().thenPlay("animation.cephalari_zombie.hurt");

    private static final String NBT_ADULT_ZOMBIE_VARIANT = "KruemblegardCephalariZombieAdultVariant";
    private static final String NBT_BODY_TEXTURE_TYPE = "KruemblegardCephalariZombieBodyTextureType";
    private static final String NBT_ADULT_FORM_TEXTURE_VARIANT = "KruemblegardCephalariZombieAdultFormTextureVariant";
    private static final String NBT_ADULT_FORM_TEXTURE_VARIANT_LEGACY = "KruemblegardCephalariZombieAdultMountTextureVariant";

    private static final int NO_ZOMBIE_VARIANT = -1;
    private static final int ZOMBIE_VARIANTS = 5;

    // Body texture selection is independent from the geo variant.
    // 1=zombie, 2=husk, 3=drowned
    protected static final int BODY_TEXTURE_ZOMBIE = 1;
    protected static final int BODY_TEXTURE_HUSK = 2;
    protected static final int BODY_TEXTURE_DROWNED = 3;
    protected static final int BODY_TEXTURE_TYPES = 3;
    protected static final int ADULT_FORM_TEXTURE_VARIANTS = 6;

    public enum UndeadVariant {
        ZOMBIE,
        HUSK,
        DROWNED
    }

    private static final EntityDataAccessor<Integer> DATA_ADULT_ZOMBIE_VARIANT =
        SynchedEntityData.defineId(CephalariZombieEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_BODY_TEXTURE_TYPE =
        SynchedEntityData.defineId(CephalariZombieEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ADULT_FORM_TEXTURE_VARIANT =
        SynchedEntityData.defineId(CephalariZombieEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private boolean forwardingLinkedDamage = false;

    private int hurtAnimCooldownTicks = 0;

    private int submergedConversionTicks = 0;

    public CephalariZombieEntity(EntityType<? extends ZombieVillager> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        // 1..ZOMBIE_VARIANTS, or -1 for none (babies)
        this.entityData.define(DATA_ADULT_ZOMBIE_VARIANT, NO_ZOMBIE_VARIANT);
        // 1..BODY_TEXTURE_TYPES
        this.entityData.define(DATA_BODY_TEXTURE_TYPE, BODY_TEXTURE_ZOMBIE);
        // 1..ADULT_FORM_TEXTURE_VARIANTS (base layer)
        this.entityData.define(DATA_ADULT_FORM_TEXTURE_VARIANT, 1);
    }

    public boolean hasAdultFormAppearance() {
        return !this.isBaby() && this.entityData.get(DATA_ADULT_ZOMBIE_VARIANT) != NO_ZOMBIE_VARIANT;
    }

    public int getAdultZombieVariant() {
        return this.entityData.get(DATA_ADULT_ZOMBIE_VARIANT);
    }

    public int getAdultFormTextureVariant() {
        return this.entityData.get(DATA_ADULT_FORM_TEXTURE_VARIANT);
    }

    public int getBodyTextureType() {
        return this.entityData.get(DATA_BODY_TEXTURE_TYPE);
    }

    public boolean isDrownedVariant() {
        return !this.isBaby() && this.entityData.get(DATA_BODY_TEXTURE_TYPE) == BODY_TEXTURE_DROWNED;
    }

    public ResourceLocation getBodyTextureResource() {
        // Base texture is always the shared inner layer.
        return TEXTURE_INNER;
    }

    public ResourceLocation getOuterTextureResource() {
        int type = this.entityData.get(DATA_BODY_TEXTURE_TYPE);
        return switch (type) {
            case BODY_TEXTURE_HUSK -> TEXTURE_OUTER_HUSK;
            case BODY_TEXTURE_DROWNED -> TEXTURE_OUTER_DROWNED;
            default -> TEXTURE_OUTER_ZOMBIE;
        };
    }

    public void setAdultZombieVariant(int variant) {
        int clamped = Math.max(1, Math.min(ZOMBIE_VARIANTS, variant));
        this.entityData.set(DATA_ADULT_ZOMBIE_VARIANT, clamped);
    }

    public void setBodyTextureType(int type) {
        int clamped = Math.max(1, Math.min(BODY_TEXTURE_TYPES, type));
        this.entityData.set(DATA_BODY_TEXTURE_TYPE, clamped);
    }

    public void setAdultFormTextureVariant(int variant) {
        int clamped = Math.max(1, Math.min(ADULT_FORM_TEXTURE_VARIANTS, variant));
        this.entityData.set(DATA_ADULT_FORM_TEXTURE_VARIANT, clamped);
    }

    /**
     * Returns the body texture type that this entity type should always use.
     *
     * Subclasses override this to lock to husk/drowned variants.
     */
    public int getFixedBodyTextureType() {
        return BODY_TEXTURE_ZOMBIE;
    }

    protected final void convertToUndeadVariant(UndeadVariant target) {
        EntityType<? extends CephalariZombieEntity> targetType = switch (target) {
            case HUSK -> ModEntities.CEPHALARI_HUSK.get();
            case DROWNED -> ModEntities.CEPHALARI_DROWNED.get();
            case ZOMBIE -> ModEntities.CEPHALARI_ZOMBIE.get();
        };

        convertToUndead(targetType, true);
    }

    private <T extends Mob> T convertToUndead(EntityType<T> targetType, boolean keepEquipment) {
        String storedAdultFormId = CephalariAdultForms.getAdultFormId(this);
        VillagerData storedVillagerData = this.getVillagerData();
        Component storedName = this.getCustomName();
        boolean storedNameVisible = this.isCustomNameVisible();
        boolean storedNoAi = this.isNoAi();
        boolean storedPersistence = this.isPersistenceRequired();

        int storedAdultVariant = this.getAdultZombieVariant();
        int storedAdultFormTextureVariant = this.getAdultFormTextureVariant();

        T converted = super.convertTo(targetType, keepEquipment);
        if (converted instanceof CephalariZombieEntity undead) {
            undead.setVillagerData(storedVillagerData);
            undead.setNoAi(storedNoAi);

            if (storedName != null) {
                undead.setCustomName(storedName);
            }
            undead.setCustomNameVisible(storedNameVisible);

            if (storedPersistence) {
                undead.setPersistenceRequired();
            }

            if (storedAdultFormId != null) {
                CephalariAdultForms.setAdultFormId(undead, storedAdultFormId);
            }

            if (!undead.isBaby()) {
                if (storedAdultVariant != NO_ZOMBIE_VARIANT) {
                    undead.setAdultZombieVariant(storedAdultVariant);
                }
                undead.setAdultFormTextureVariant(storedAdultFormTextureVariant);
            }

            undead.setBodyTextureType(undead.getFixedBodyTextureType());
        }

        return converted;
    }

    private void ensureAdultVariantsAssigned() {
        if (this.isBaby()) {
            this.entityData.set(DATA_ADULT_ZOMBIE_VARIANT, NO_ZOMBIE_VARIANT);
            return;
        }

        if (this.entityData.get(DATA_ADULT_ZOMBIE_VARIANT) == NO_ZOMBIE_VARIANT) {
            setAdultZombieVariant(1 + this.getRandom().nextInt(ZOMBIE_VARIANTS));
        }

        int bodyType = this.entityData.get(DATA_BODY_TEXTURE_TYPE);
        if (bodyType < 1 || bodyType > BODY_TEXTURE_TYPES) {
            setBodyTextureType(BODY_TEXTURE_ZOMBIE);
        }

        int tex = this.entityData.get(DATA_ADULT_FORM_TEXTURE_VARIANT);
        if (tex < 1 || tex > ADULT_FORM_TEXTURE_VARIANTS) {
            setAdultFormTextureVariant(1);
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
                if (this.entityData.get(DATA_ADULT_FORM_TEXTURE_VARIANT) == 1) {
                    setAdultFormTextureVariant(1 + this.getRandom().nextInt(ADULT_FORM_TEXTURE_VARIANTS));
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

        makeMountPermanentlyHostile(mount);

        level.addFreshEntity(mount);
        this.startRiding(mount, true);
        this.setPersistenceRequired();
    }

    private static void makeMountPermanentlyHostile(Mob mount) {
        // Force the mount to behave like a hostile mob, even if it's normally passive/neutral.
        // We do this by injecting a simple melee + player-targeting goal set.
        if (!(mount instanceof PathfinderMob pathfinder)) {
            return;
        }

        pathfinder.setAggressive(true);

        pathfinder.goalSelector.addGoal(1, new MeleeAttackGoal(pathfinder, 1.2D, true));
        pathfinder.targetSelector.addGoal(1, new HurtByTargetGoal(pathfinder));
        pathfinder.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(pathfinder, Player.class, true));
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(NBT_ADULT_ZOMBIE_VARIANT, this.entityData.get(DATA_ADULT_ZOMBIE_VARIANT));
        tag.putInt(NBT_BODY_TEXTURE_TYPE, this.entityData.get(DATA_BODY_TEXTURE_TYPE));
        tag.putInt(NBT_ADULT_FORM_TEXTURE_VARIANT, this.entityData.get(DATA_ADULT_FORM_TEXTURE_VARIANT));
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        boolean readLegacyVariant = false;
        int legacyVariant = 1;
        if (tag.contains(NBT_ADULT_ZOMBIE_VARIANT)) {
            int variant = tag.getInt(NBT_ADULT_ZOMBIE_VARIANT);
            if (variant == NO_ZOMBIE_VARIANT) {
                this.entityData.set(DATA_ADULT_ZOMBIE_VARIANT, NO_ZOMBIE_VARIANT);
            } else {
                setAdultZombieVariant(variant);
            }
            readLegacyVariant = true;
            legacyVariant = variant;
        }

        // Newer saves store body texture selection explicitly; older saves inferred it from variant.
        if (tag.contains(NBT_BODY_TEXTURE_TYPE)) {
            setBodyTextureType(tag.getInt(NBT_BODY_TEXTURE_TYPE));
        } else if (readLegacyVariant) {
            if (legacyVariant == 2) {
                setBodyTextureType(BODY_TEXTURE_HUSK);
            } else if (legacyVariant == 3) {
                setBodyTextureType(BODY_TEXTURE_DROWNED);
            } else {
                setBodyTextureType(BODY_TEXTURE_ZOMBIE);
            }
        }

        String textureVariantKey = tag.contains(NBT_ADULT_FORM_TEXTURE_VARIANT) ? NBT_ADULT_FORM_TEXTURE_VARIANT : NBT_ADULT_FORM_TEXTURE_VARIANT_LEGACY;
        if (tag.contains(textureVariantKey)) {
            setAdultFormTextureVariant(tag.getInt(textureVariantKey));
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

        // Some creation paths can bypass finalizeSpawn. Ensure baby jockey behavior still happens,
        // but only during the initial spawn window (avoid endlessly respawning mounts).
        if (this.isBaby() && !this.isPassenger() && this.tickCount < 20 && level() instanceof ServerLevelAccessor accessor) {
            ensureBabyHasMount(accessor);
        }

        // Some creation paths (notably vanilla conversion mechanics) can bypass finalizeSpawn.
        // Ensure adult visuals (geo variant + texture selection) are always initialized.
        boolean variantsWereUnassigned = !this.isBaby() && this.entityData.get(DATA_ADULT_ZOMBIE_VARIANT) == NO_ZOMBIE_VARIANT;
        ensureAdultVariantsAssigned();
        if (variantsWereUnassigned && !this.isBaby() && this.entityData.get(DATA_ADULT_FORM_TEXTURE_VARIANT) == 1) {
            setAdultFormTextureVariant(1 + this.getRandom().nextInt(ADULT_FORM_TEXTURE_VARIANTS));
        }

        // Migrate legacy cephalari_zombie entities that used bodyTextureType to represent husk/drowned.
        // With separate entity types, those should become cephalari_husk / cephalari_drowned.
        if (this.getType() == ModEntities.CEPHALARI_ZOMBIE.get() && !this.isBaby()) {
            int type = this.getBodyTextureType();
            if (type == BODY_TEXTURE_HUSK) {
                convertToUndeadVariant(UndeadVariant.HUSK);
                return;
            }
            if (type == BODY_TEXTURE_DROWNED) {
                convertToUndeadVariant(UndeadVariant.DROWNED);
                return;
            }
        }

        // Enforce fixed texture type per entity class.
        int fixedType = getFixedBodyTextureType();
        if (this.getBodyTextureType() != fixedType) {
            setBodyTextureType(fixedType);
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
            // Old worlds could have used the mount type as the "texture variant".
            // Now: keep the existing geo variant, and only translate the mount type into a body texture type.
            if (mob instanceof Husk) {
                setBodyTextureType(BODY_TEXTURE_HUSK);
            } else if (mob instanceof Drowned) {
                setBodyTextureType(BODY_TEXTURE_DROWNED);
            } else {
                setBodyTextureType(BODY_TEXTURE_ZOMBIE);
            }
            this.stopRiding();
            mob.discard();
        }

        if (!this.isBaby()) {
            ensureAdultVariantsAssigned();
        }

        // Vanilla-like: Zombie -> Drowned after time submerged.
        if (this.getType() == ModEntities.CEPHALARI_ZOMBIE.get() && !this.isBaby()) {
            if (isInWaterOrBubble()) {
                submergedConversionTicks++;
                if (submergedConversionTicks >= 600) {
                    submergedConversionTicks = 0;
                    convertToUndeadVariant(UndeadVariant.DROWNED);
                }
            } else {
                submergedConversionTicks = 0;
            }
        } else {
            submergedConversionTicks = 0;
        }
    }

    public boolean isForwardingLinkedDamage() {
        return forwardingLinkedDamage;
    }

    public void hurtLinkedFromAdultForm(DamageSource source, float amount) {
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

    public void healLinkedFromAdultForm(float amount) {
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

            String storedAdultFormId = CephalariAdultForms.getAdultFormId(this);

            T converted = super.convertTo(target, keepEquipment);
            if (converted instanceof CephalariEntity cephalari) {
                cephalari.setVillagerData(this.getVillagerData());
                cephalari.setNoAi(this.isNoAi());
                cephalari.setCustomName(this.getCustomName());
                cephalari.setCustomNameVisible(this.isCustomNameVisible());

                if (storedAdultFormId != null) {
                    cephalari.setAdultFormId(storedAdultFormId);
                }

                if (!cephalari.isBaby()) {
                    cephalari.setAdultFormTextureVariant(this.getAdultFormTextureVariant());
                }
            }
            return converted;
        }

        // Redirect vanilla undead conversions to the modded variants.
        // This prevents e.g. water conversion creating a vanilla Drowned.
        if (entityType == EntityType.DROWNED) {
            @SuppressWarnings("unchecked")
            EntityType<T> target = (EntityType<T>) ModEntities.CEPHALARI_DROWNED.get();
            return convertToUndead(target, keepEquipment);
        }

        if (entityType == EntityType.ZOMBIE) {
            @SuppressWarnings("unchecked")
            EntityType<T> target = (EntityType<T>) ModEntities.CEPHALARI_ZOMBIE.get();
            return convertToUndead(target, keepEquipment);
        }

        if (entityType == EntityType.HUSK) {
            @SuppressWarnings("unchecked")
            EntityType<T> target = (EntityType<T>) ModEntities.CEPHALARI_HUSK.get();
            return convertToUndead(target, keepEquipment);
        }

        return super.convertTo(entityType, keepEquipment);
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
    }
}
