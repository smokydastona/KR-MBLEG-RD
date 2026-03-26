package com.kruemblegard.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * A Cephalari-flavored village golem.
 *
 * Behavior intentionally matches vanilla {@link IronGolem}; only rendering and flavor details differ.
 */
public class CephalariGolemEntity extends IronGolem implements GeoEntity {

    private static final int TEXTURE_VARIANT_MIN = 1;
    private static final int TEXTURE_VARIANT_MAX = 6;
    private static final String NBT_TEXTURE_VARIANT = "CephalariGolemTextureVariant";
    private static final EntityDataAccessor<Boolean> DATA_ANGRY_ANIMATION =
        SynchedEntityData.defineId(CephalariGolemEntity.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation PASSIVE_IDLE_LOOP = RawAnimation.begin().thenLoop("animation.cephalari_golem.idle_passive");
    private static final RawAnimation ANGRY_IDLE_LOOP = RawAnimation.begin().thenLoop("animation.cephalari_golem.idle_angry");
    private static final RawAnimation PASSIVE_MOVE_LOOP = RawAnimation.begin().thenLoop("animation.cephalari_golem.move_passive");
    private static final RawAnimation ANGRY_MOVE_LOOP = RawAnimation.begin().thenLoop("animation.cephalari_golem.move_angry");
    private static final RawAnimation OFFER_FLOWER_LOOP = RawAnimation.begin().thenLoop("animation.cephalari_golem.offer_flower");

    private static final RawAnimation ATTACK_ONCE = RawAnimation.begin().thenPlay("animation.cephalari_golem.attack");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int textureVariant = 0;

    public CephalariGolemEntity(EntityType<? extends IronGolem> type, Level level) {
        super(type, level);
        this.setMaxUpStep(1.5F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        // Match vanilla Iron Golem baseline (AI/behavior parity).
        return IronGolem.createAttributes();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ANGRY_ANIMATION, false);
    }

    public int getTextureVariant() {
        int variant = this.textureVariant;
        if (variant < TEXTURE_VARIANT_MIN || variant > TEXTURE_VARIANT_MAX) {
            return TEXTURE_VARIANT_MIN;
        }
        return variant;
    }

    public void setTextureVariant(int variant) {
        this.textureVariant = Mth.clamp(variant, TEXTURE_VARIANT_MIN, TEXTURE_VARIANT_MAX);
    }

    private void ensureTextureVariantAssigned() {
        if (this.textureVariant != 0) {
            return;
        }

        int variant = TEXTURE_VARIANT_MIN + this.random.nextInt(TEXTURE_VARIANT_MAX - TEXTURE_VARIANT_MIN + 1);
        setTextureVariant(variant);
    }

    public ResourceLocation getTextureResource() {
        return ResourceLocation.tryBuild(
            "kruemblegard",
            "textures/entity/cephalari/cephalari_golem/cephalari_golem_" + getTextureVariant() + ".png"
        );
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(NBT_TEXTURE_VARIANT, this.textureVariant);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(NBT_TEXTURE_VARIANT)) {
            int variant = tag.getInt(NBT_TEXTURE_VARIANT);
            if (variant == 0) {
                // Legacy/invalid: assign on next server tick.
                this.textureVariant = 0;
            } else {
                setTextureVariant(variant);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            return;
        }

        ensureTextureVariantAssigned();
        this.entityData.set(DATA_ANGRY_ANIMATION, this.getTarget() != null || this.swinging);
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        triggerAnim("actionController", "attack");
        return super.doHurtTarget(target);
    }

    private boolean hasNearbyChildVillager(double radius) {
        return !this.level().getEntitiesOfClass(
            Villager.class,
            this.getBoundingBox().inflate(radius),
            villager -> villager != null && villager.isBaby()
        ).isEmpty();
    }

    /**
     * Vanilla Iron Golems offer a poppy. Cephalari golems visually offer a Runebloom instead.
     *
     * We intentionally only show this offering when a child villager is nearby (vanilla or Cephalari).
     */
    public boolean isOfferingRunebloom() {
        return this.getOfferFlowerTick() > 0 && hasNearbyChildVillager(6.0D);
    }

    public boolean isAngryAnimationState() {
        return this.entityData.get(DATA_ANGRY_ANIMATION);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "moveController", 2, state -> {
            boolean angry = isAngryAnimationState();

            if (angry) {
                if (state.isMoving()) {
                    state.setAndContinue(ANGRY_MOVE_LOOP);
                    return PlayState.CONTINUE;
                }

                state.setAndContinue(ANGRY_IDLE_LOOP);
                return PlayState.CONTINUE;
            }

            if (isOfferingRunebloom()) {
                state.setAndContinue(OFFER_FLOWER_LOOP);
                return PlayState.CONTINUE;
            }

            if (state.isMoving()) {
                state.setAndContinue(PASSIVE_MOVE_LOOP);
                return PlayState.CONTINUE;
            }

            state.setAndContinue(PASSIVE_IDLE_LOOP);
            return PlayState.CONTINUE;
        }));

        controllers.add(new AnimationController<>(this, "actionController", 0, state -> PlayState.CONTINUE)
            .triggerableAnim("attack", ATTACK_ONCE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
