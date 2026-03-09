package com.kruemblegard.entity;

import com.kruemblegard.Kruemblegard;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.schedule.Activity;
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

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Cephalari
 *
 * Implementation strategy: extend vanilla {@link Villager} so this entity inherits the full villager
 * brain/POI/profession/trading behavior, and the rest of the game treats it as a villager-class mob.
 */
public class CephalariEntity extends Villager implements GeoEntity {

    public enum Temperament {
        CALM,
        CURIOUS,
        SKITTISH
    }

    private static final RawAnimation IDLE_CALM_LOOP = RawAnimation.begin().thenLoop("animation.cephalari.idle_calm");
    private static final RawAnimation IDLE_CURIOUS_LOOP = RawAnimation.begin().thenLoop("animation.cephalari.idle_curious");
    private static final RawAnimation IDLE_SKITTISH_LOOP = RawAnimation.begin().thenLoop("animation.cephalari.idle_skittish");
    private static final RawAnimation WALK_LOOP = RawAnimation.begin().thenLoop("animation.cephalari.walk");
    private static final RawAnimation MOUNT_IDLE_LOOP = RawAnimation.begin().thenLoop("animation.cephalari.mount_idle");
    private static final RawAnimation MOUNT_WALK_LOOP = RawAnimation.begin().thenLoop("animation.cephalari.mount_walk");
    private static final RawAnimation RIDING_LOOP = RawAnimation.begin().thenLoop("animation.cephalari.riding_pose");

    private static final RawAnimation SLEEP_LOOP = RawAnimation.begin().thenLoop("animation.cephalari.sleep");
    private static final RawAnimation TRADE_LOOP = RawAnimation.begin().thenLoop("animation.cephalari.trade");
    private static final RawAnimation WORK_LOOP = RawAnimation.begin().thenLoop("animation.cephalari.work");
    private static final RawAnimation CELEBRATE_LOOP = RawAnimation.begin().thenLoop("animation.cephalari.celebrate");

    private static final RawAnimation ZOMBIFY_ONCE = RawAnimation.begin().thenPlay("animation.cephalari.zombify_cinematic");
    private static final RawAnimation HURT_ONCE = RawAnimation.begin().thenPlay("animation.cephalari.hurt");

    private static final RawAnimation CURIOUS_HEAD_TILT_ONCE = RawAnimation.begin().thenPlay("animation.cephalari.curious_head_tilt");
    private static final RawAnimation SKITTISH_TWITCH_ONCE = RawAnimation.begin().thenPlay("animation.cephalari.skittish_twitch");

    private static final int NON_WAYFALL_SUFFOCATION_INTERVAL_TICKS = 40;
    private static final float NON_WAYFALL_SUFFOCATION_DAMAGE = 1.0F;

    private static final String NBT_ADULT_MOUNT_VARIANT = "KruemblegardCephalariAdultMountVariant";
    private static final String NBT_ADULT_MOUNT_TEXTURE_VARIANT = "KruemblegardCephalariAdultMountTextureVariant";
    private static final String NBT_TEMPERAMENT = "KruemblegardCephalariTemperament";
    private static final String NBT_BODY_TEXTURE = "KruemblegardCephalariBodyTexture";

    private static final int NO_MOUNT_VARIANT = -1;
    private static final int MOUNT_TEXTURE_VARIANTS = 6;

    private static final int TEMPERAMENT_UNASSIGNED = -1;
    private static final UUID TEMPERAMENT_SPEED_UUID = UUID.fromString("d1da79a2-5c62-4c1c-bc4d-8a8bb0b03091");

    private static final EntityDataAccessor<Integer> DATA_ADULT_MOUNT_VARIANT =
        SynchedEntityData.defineId(CephalariEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ADULT_MOUNT_TEXTURE_VARIANT =
        SynchedEntityData.defineId(CephalariEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TEMPERAMENT =
        SynchedEntityData.defineId(CephalariEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> DATA_BODY_TEXTURE =
        SynchedEntityData.defineId(CephalariEntity.class, EntityDataSerializers.STRING);

    private static final ResourceLocation DEFAULT_BODY_TEXTURE = new ResourceLocation(
        Kruemblegard.MOD_ID,
        "textures/entity/cephalari/cephalari/cephalari_underway_falls.png"
    );

    private static final List<ResourceLocation> WAYFALL_BIOME_BODY_TEXTURES = List.of(
        new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/cephalari/cephalari/cephalari_basin_of_scars.png"),
        new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/cephalari/cephalari/cephalari_crumbled_crossing.png"),
        new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/cephalari/cephalari/cephalari_driftway_chasm.png"),
        new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/cephalari/cephalari/cephalari_faulted_expanse.png"),
        new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/cephalari/cephalari/cephalari_fracture_shoals.png"),
        new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/cephalari/cephalari/cephalari_glyphscar_reach.png"),
        new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/cephalari/cephalari/cephalari_hollow_transit_plains.png"),
        new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/cephalari/cephalari/cephalari_midweft_wilds.png"),
        new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/cephalari/cephalari/cephalari_riven_causeways.png"),
        new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/cephalari/cephalari/cephalari_shatterplate_flats.png"),
        new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/cephalari/cephalari/cephalari_strata_collapse.png"),
        new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/cephalari/cephalari/cephalari_underway_falls.png")
    );

    private static final Set<String> WAYFALL_BIOME_TEXTURE_PATHS = Set.of(
        "basin_of_scars",
        "crumbled_crossing",
        "driftway_chasm",
        "faulted_expanse",
        "fracture_shoals",
        "glyphscar_reach",
        "hollow_transit_plains",
        "midweft_wilds",
        "riven_causeways",
        "shatterplate_flats",
        "strata_collapse",
        "underway_falls"
    );

    private static final List<ResourceLocation> BONUS_BODY_TEXTURES = List.of(
        new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/cephalari/cephalari/cephalari_bonus_1.png"),
        new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/cephalari/cephalari/cephalari_bonus_2.png"),
        new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/cephalari/cephalari/cephalari_bonus_3.png"),
        new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/cephalari/cephalari/cephalari_bonus_4.png")
    );

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private boolean zombifyInProgress = false;
    private int zombifyTicks = 0;
    private @Nullable String zombifyStoredMountId;
    private int zombifyZombieVariant = 1;
    private boolean zombifyMountSpawned = false;

    private boolean forwardingLinkedDamage = false;

    private int hurtAnimCooldownTicks = 0;

    private int appliedTemperament = TEMPERAMENT_UNASSIGNED;

    private long nextPersonalityEventTick = -1L;
    private int personalityAnimTicksRemaining = 0;
    private @Nullable RawAnimation personalityAnim;

    public CephalariEntity(EntityType<? extends Villager> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ADULT_MOUNT_VARIANT, NO_MOUNT_VARIANT);
        // 1..MOUNT_TEXTURE_VARIANTS (1-based because textures are named _1.._6)
        this.entityData.define(DATA_ADULT_MOUNT_TEXTURE_VARIANT, 1);
        this.entityData.define(DATA_TEMPERAMENT, TEMPERAMENT_UNASSIGNED);
        // Empty means "use default" (and allows breeding to set explicit textures before finalizeSpawn).
        this.entityData.define(DATA_BODY_TEXTURE, "");
    }

    public ResourceLocation getBodyTextureResource() {
        String raw = this.entityData.get(DATA_BODY_TEXTURE);
        if (raw == null || raw.isEmpty()) {
            return DEFAULT_BODY_TEXTURE;
        }

        ResourceLocation parsed = ResourceLocation.tryParse(raw);
        return parsed != null ? parsed : DEFAULT_BODY_TEXTURE;
    }

    public void setBodyTextureResource(ResourceLocation texture) {
        if (texture == null) {
            this.entityData.set(DATA_BODY_TEXTURE, "");
            return;
        }
        this.entityData.set(DATA_BODY_TEXTURE, texture.toString());
    }

    private void ensureBodyTextureAssigned(ServerLevelAccessor levelAccessor) {
        if (this.entityData.get(DATA_BODY_TEXTURE) != null && !this.entityData.get(DATA_BODY_TEXTURE).isEmpty()) {
            return;
        }

        ResourceLocation chosen = pickSpawnBodyTexture(levelAccessor, this.blockPosition());
        setBodyTextureResource(chosen);
    }

    private ResourceLocation pickSpawnBodyTexture(ServerLevelAccessor accessor, BlockPos pos) {
        // 10% bonus, 5% "different biome", otherwise current biome.
        float roll = this.getRandom().nextFloat();
        if (roll < 0.10F && !BONUS_BODY_TEXTURES.isEmpty()) {
            return BONUS_BODY_TEXTURES.get(this.getRandom().nextInt(BONUS_BODY_TEXTURES.size()));
        }

        ResourceLocation currentBiome = getBiomeBodyTexture(accessor, pos);
        if (roll < 0.15F && !WAYFALL_BIOME_BODY_TEXTURES.isEmpty()) {
            // Pick a different Wayfall biome texture (fallback to current if identical).
            for (int i = 0; i < 6; i++) {
                ResourceLocation candidate = WAYFALL_BIOME_BODY_TEXTURES.get(this.getRandom().nextInt(WAYFALL_BIOME_BODY_TEXTURES.size()));
                if (!candidate.equals(currentBiome)) {
                    return candidate;
                }
            }
        }

        return currentBiome;
    }

    private static ResourceLocation getBiomeBodyTexture(ServerLevelAccessor accessor, BlockPos pos) {
        var biomeKey = accessor.getBiome(pos).unwrapKey().orElse(null);
        if (biomeKey == null) {
            return DEFAULT_BODY_TEXTURE;
        }

        ResourceLocation biomeId = biomeKey.location();
        if (!Kruemblegard.MOD_ID.equals(biomeId.getNamespace())) {
            return DEFAULT_BODY_TEXTURE;
        }

        String path = biomeId.getPath();
        if (!WAYFALL_BIOME_TEXTURE_PATHS.contains(path)) {
            return DEFAULT_BODY_TEXTURE;
        }

        return new ResourceLocation(
            Kruemblegard.MOD_ID,
            "textures/entity/cephalari/cephalari/cephalari_" + path + ".png"
        );
    }

    public Temperament getTemperament() {
        int raw = this.entityData.get(DATA_TEMPERAMENT);
        if (raw < 0 || raw >= Temperament.values().length) {
            return Temperament.CALM;
        }
        return Temperament.values()[raw];
    }

    private void ensureTemperamentAssigned() {
        if (this.entityData.get(DATA_TEMPERAMENT) == TEMPERAMENT_UNASSIGNED) {
            this.entityData.set(DATA_TEMPERAMENT, this.getRandom().nextInt(Temperament.values().length));
        }

        if (!this.level().isClientSide) {
            applyTemperamentMovementModifier();
        }
    }

    private void applyTemperamentMovementModifier() {
        int raw = this.entityData.get(DATA_TEMPERAMENT);
        if (raw == TEMPERAMENT_UNASSIGNED || raw == appliedTemperament) {
            return;
        }

        AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) {
            appliedTemperament = raw;
            return;
        }

        // Remove any previous modifier (safe even if absent).
        speed.removeModifier(TEMPERAMENT_SPEED_UUID);

        Temperament temperament = getTemperament();
        double mult = switch (temperament) {
            case CALM -> -0.03D;
            case CURIOUS -> 0.00D;
            case SKITTISH -> 0.03D;
        };

        if (mult != 0.0D) {
            speed.addPermanentModifier(new AttributeModifier(
                TEMPERAMENT_SPEED_UUID,
                "Cephalari temperament",
                mult,
                AttributeModifier.Operation.MULTIPLY_TOTAL
            ));
        }

        appliedTemperament = raw;
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
            ensureTemperamentAssigned();
            ensureBodyTextureAssigned(level);
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
        tag.putInt(NBT_TEMPERAMENT, this.entityData.get(DATA_TEMPERAMENT));
        String bodyTex = this.entityData.get(DATA_BODY_TEXTURE);
        if (bodyTex != null && !bodyTex.isEmpty()) {
            tag.putString(NBT_BODY_TEXTURE, bodyTex);
        }
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

        if (tag.contains(NBT_TEMPERAMENT)) {
            int raw = tag.getInt(NBT_TEMPERAMENT);
            if (raw < 0 || raw >= Temperament.values().length) {
                raw = Temperament.CALM.ordinal();
            }
            this.entityData.set(DATA_TEMPERAMENT, raw);
        }

        if (tag.contains(NBT_BODY_TEXTURE)) {
            String raw = tag.getString(NBT_BODY_TEXTURE);
            if (raw != null && !raw.isEmpty() && ResourceLocation.tryParse(raw) != null) {
                this.entityData.set(DATA_BODY_TEXTURE, raw);
            } else {
                this.entityData.set(DATA_BODY_TEXTURE, "");
            }
        }
    }

    @Override
    public void aiStep() {
        if (!level().isClientSide && hurtAnimCooldownTicks > 0) {
            hurtAnimCooldownTicks--;
        }

        if (!level().isClientSide && personalityAnimTicksRemaining > 0) {
            personalityAnimTicksRemaining--;
            if (personalityAnimTicksRemaining <= 0) {
                personalityAnim = null;
            }
        }

        if (!level().isClientSide && zombifyInProgress) {
            tickZombifySequence();
            super.aiStep();
            return;
        }

        super.aiStep();

        if (level().isClientSide) {
            return;
        }

        ensureTemperamentAssigned();

        tickPersonalityIdleEvents();

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

    private void tickPersonalityIdleEvents() {
        if (zombifyInProgress) {
            return;
        }

        if (this.isSleeping() || this.isTrading() || isWorkingNow() || isCelebratingNow() || this.isPassenger()) {
            return;
        }

        // Only run when idle on the ground (keeps the behavior subtle and avoids spam).
        if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6) {
            return;
        }

        if (this.hasAdultMountAppearance()) {
            // Mount geometry already has richer idle motion; skip extra personality one-shots for now.
            return;
        }

        long now = this.tickCount;
        if (nextPersonalityEventTick < 0L) {
            nextPersonalityEventTick = now + 80 + this.getRandom().nextInt(121);
            return;
        }

        if (now < nextPersonalityEventTick) {
            return;
        }

        // Schedule next check first (prevents tight loops if something below early-outs).
        nextPersonalityEventTick = now + 80 + this.getRandom().nextInt(121);

        if (personalityAnimTicksRemaining > 0) {
            return;
        }

        Temperament temperament = getTemperament();
        if (temperament == Temperament.CURIOUS) {
            // Occasional short head tilt.
            if (this.getRandom().nextFloat() < 0.55F) {
                personalityAnim = CURIOUS_HEAD_TILT_ONCE;
                personalityAnimTicksRemaining = 14;
            }
        } else if (temperament == Temperament.SKITTISH) {
            // Occasional micro-twitch.
            if (this.getRandom().nextFloat() < 0.75F) {
                personalityAnim = SKITTISH_TWITCH_ONCE;
                personalityAnimTicksRemaining = 8;
            }
        }
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Layer 1: high-priority action/pose loops.
        controllers.add(new AnimationController<>(this, "action_loop", 0, state -> {
            if (zombifyInProgress) {
                return PlayState.STOP;
            }

            if (this.isPassenger()) {
                state.getController().setAnimationSpeed(1.0D);
                return state.setAndContinue(RIDING_LOOP);
            }

            if (this.isSleeping()) {
                state.getController().setAnimationSpeed(1.0D);
                return state.setAndContinue(SLEEP_LOOP);
            }

            if (this.isTrading()) {
                state.getController().setAnimationSpeed(1.0D);
                return state.setAndContinue(TRADE_LOOP);
            }

            if (isCelebratingNow()) {
                state.getController().setAnimationSpeed(1.0D);
                return state.setAndContinue(CELEBRATE_LOOP);
            }

            if (isWorkingNow()) {
                state.getController().setAnimationSpeed(1.0D);
                return state.setAndContinue(WORK_LOOP);
            }

            return PlayState.STOP;
        }));

        // Layer 2: locomotion.
        controllers.add(new AnimationController<>(this, "walk_loop", 0, state -> {
            if (zombifyInProgress || this.isPassenger() || this.isSleeping() || this.isTrading() || isCelebratingNow() || isWorkingNow()) {
                return PlayState.STOP;
            }

            if (!state.isMoving()) {
                return PlayState.STOP;
            }

            double animSpeed = switch (getTemperament()) {
                case CALM -> 0.92D;
                case CURIOUS -> 1.00D;
                case SKITTISH -> 1.12D;
            };
            state.getController().setAnimationSpeed(animSpeed);

            if (this.hasAdultMountAppearance()) {
                return state.setAndContinue(MOUNT_WALK_LOOP);
            }
            return state.setAndContinue(WALK_LOOP);
        }));

        // Layer 3: idle + personality.
        controllers.add(new AnimationController<>(this, "idle_loop", 0, state -> {
            if (zombifyInProgress || this.isPassenger() || this.isSleeping() || this.isTrading() || isCelebratingNow() || isWorkingNow()) {
                return PlayState.STOP;
            }

            if (state.isMoving()) {
                return PlayState.STOP;
            }

            // Play occasional one-shot personality animations when idle.
            if (personalityAnimTicksRemaining > 0 && personalityAnim != null) {
                state.getController().setAnimationSpeed(1.0D);
                return state.setAndContinue(personalityAnim);
            }

            double animSpeed = switch (getTemperament()) {
                case CALM -> 0.90D;
                case CURIOUS -> 1.05D;
                case SKITTISH -> 1.10D;
            };
            state.getController().setAnimationSpeed(animSpeed);

            if (this.hasAdultMountAppearance()) {
                return state.setAndContinue(MOUNT_IDLE_LOOP);
            }

            RawAnimation idle = switch (getTemperament()) {
                case CALM -> IDLE_CALM_LOOP;
                case CURIOUS -> IDLE_CURIOUS_LOOP;
                case SKITTISH -> IDLE_SKITTISH_LOOP;
            };
            return state.setAndContinue(idle);
        }));

        controllers.add(new AnimationController<>(this, "actionController", 0, state -> PlayState.STOP)
            .triggerableAnim("zombify", ZOMBIFY_ONCE));

        controllers.add(new AnimationController<>(this, "hurtController", 0, state -> PlayState.STOP)
            .triggerableAnim("hurt", HURT_ONCE));
    }

    private boolean isCelebratingNow() {
        return this.getBrain().getActiveNonCoreActivity().orElse(null) == Activity.CELEBRATE;
    }

    private boolean isWorkingNow() {
        return this.getBrain().getActiveNonCoreActivity().orElse(null) == Activity.WORK;
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

        boolean result = super.hurt(source, amount);
        if (!level().isClientSide && result && !zombifyInProgress && hurtAnimCooldownTicks <= 0) {
            hurtAnimCooldownTicks = 10;
            triggerAnim("hurtController", "hurt");
        }
        return result;
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

            ResourceLocation babyTexture = pickOffspringBodyTexture(level, (CephalariEntity) otherParent, this.blockPosition());
            baby.setBodyTextureResource(babyTexture);
        }
        return baby;
    }

    private ResourceLocation pickOffspringBodyTexture(ServerLevel level, CephalariEntity otherParent, BlockPos pos) {
        // 10% chance: completely random texture (biome or bonus).
        if (this.getRandom().nextFloat() < 0.10F) {
            return pickAnyBodyTexture(level, pos);
        }

        float roll = this.getRandom().nextFloat();
        if (roll < 0.25F) {
            return this.getBodyTextureResource();
        }
        if (roll < 0.50F) {
            return otherParent.getBodyTextureResource();
        }

        // 50%: current biome texture (no bonus/off-biome overrides).
        return getBiomeBodyTexture(level, pos);
    }

    private ResourceLocation pickAnyBodyTexture(ServerLevelAccessor accessor, BlockPos pos) {
        // Mix biome + bonus pools; fall back to biome-derived.
        int biomeCount = WAYFALL_BIOME_BODY_TEXTURES.size();
        int bonusCount = BONUS_BODY_TEXTURES.size();
        int total = biomeCount + bonusCount;
        if (total <= 0) {
            return getBiomeBodyTexture(accessor, pos);
        }

        int pick = this.getRandom().nextInt(total);
        if (pick < biomeCount) {
            return WAYFALL_BIOME_BODY_TEXTURES.get(pick);
        }
        return BONUS_BODY_TEXTURES.get(pick - biomeCount);
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
