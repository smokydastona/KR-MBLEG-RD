package com.kruemblegard.entity;

import com.kruemblegard.init.ModBlocks;

import net.minecraft.core.BlockPos;
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
 * Base AI is still vanilla {@link IronGolem}, but with a simple internal stability meter:
 * - Higher stability keeps it responsive.
 * - At zero stability it shuts down into a dormant statue (NoAI) until recharged.
 */
public class CephalariGolemEntity extends IronGolem implements GeoEntity {

    private static final int PRESSURE_MAX = 100;
    private static final String NBT_PRESSURE = "CephalariPressure";

    private static final int TEXTURE_VARIANT_MIN = 1;
    private static final int TEXTURE_VARIANT_MAX = 6;
    private static final String NBT_TEXTURE_VARIANT = "CephalariGolemTextureVariant";

    private static final EntityDataAccessor<Integer> PRESSURE =
        SynchedEntityData.defineId(CephalariGolemEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> TEXTURE_VARIANT =
        SynchedEntityData.defineId(CephalariGolemEntity.class, EntityDataSerializers.INT);

    private static final RawAnimation IDLE_LOOP = RawAnimation.begin().thenLoop("animation.cephalari_golem.idle");
    private static final RawAnimation WALK_LOOP = RawAnimation.begin().thenLoop("animation.cephalari_golem.walk");
    private static final RawAnimation OFFER_FLOWER_LOOP = RawAnimation.begin().thenLoop("animation.cephalari_golem.offer_flower");

    private static final RawAnimation ATTACK_ONCE = RawAnimation.begin().thenPlay("animation.cephalari_golem.attack");
    private static final RawAnimation SHUTDOWN_ONCE = RawAnimation.begin().thenPlay("animation.cephalari_golem.shutdown");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private boolean wasShutdown = false;

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
        this.entityData.define(PRESSURE, PRESSURE_MAX);
        this.entityData.define(TEXTURE_VARIANT, 0);
    }

    public int getPressure() {
        return this.entityData.get(PRESSURE);
    }

    public void setPressure(int value) {
        this.entityData.set(PRESSURE, Mth.clamp(value, 0, PRESSURE_MAX));
    }

    public boolean isShutdown() {
        return getPressure() <= 0;
    }

    public int getTextureVariant() {
        int variant = this.entityData.get(TEXTURE_VARIANT);
        if (variant < TEXTURE_VARIANT_MIN || variant > TEXTURE_VARIANT_MAX) {
            return TEXTURE_VARIANT_MIN;
        }
        return variant;
    }

    public void setTextureVariant(int variant) {
        this.entityData.set(TEXTURE_VARIANT, Mth.clamp(variant, TEXTURE_VARIANT_MIN, TEXTURE_VARIANT_MAX));
    }

    private void ensureTextureVariantAssigned() {
        if (this.entityData.get(TEXTURE_VARIANT) != 0) {
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

    private void addPressure(int delta) {
        if (delta == 0) return;
        setPressure(getPressure() + delta);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(NBT_PRESSURE, getPressure());
        tag.putInt(NBT_TEXTURE_VARIANT, this.entityData.get(TEXTURE_VARIANT));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(NBT_PRESSURE)) {
            setPressure(tag.getInt(NBT_PRESSURE));
        }

        if (tag.contains(NBT_TEXTURE_VARIANT)) {
            int variant = tag.getInt(NBT_TEXTURE_VARIANT);
            if (variant == 0) {
                // Legacy/invalid: assign on next server tick.
                this.entityData.set(TEXTURE_VARIANT, 0);
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

        // Passive leak: ~1 unit/sec.
        if (tickCount % 20 == 0 && getPressure() > 0) {
            addPressure(-1);
        }

        // Recharge when near Cephalari attunement infrastructure.
        // (Kept intentionally simple: proximity-based, 1..3 units/sec)
        if (tickCount % 20 == 0 && getPressure() < PRESSURE_MAX) {
            int nearbySources = countNearbyAttunementSources(2);
            if (nearbySources > 0) {
                addPressure(Mth.clamp(nearbySources, 1, 3));
            }
        }

        boolean shutdownNow = isShutdown();
        if (shutdownNow && !wasShutdown) {
            wasShutdown = true;
            triggerAnim("actionController", "shutdown");
            setNoAi(true);
            getNavigation().stop();
            setDeltaMovement(0.0D, 0.0D, 0.0D);
        } else if (!shutdownNow && wasShutdown) {
            wasShutdown = false;
            setNoAi(false);
        }
    }

    private int countNearbyAttunementSources(int radius) {
        BlockPos origin = blockPosition();

        int count = 0;
        for (BlockPos pos : BlockPos.betweenClosed(
                origin.offset(-radius, -radius, -radius),
                origin.offset(radius, radius, radius))) {
            if (!level().isLoaded(pos)) continue;

            var state = level().getBlockState(pos);
            if (state.is(ModBlocks.ATTUNED_STONE.get())
                || state.is(ModBlocks.ATTUNED_STONE_STAIRS.get())
                || state.is(ModBlocks.ATTUNED_STONE_SLAB.get())
                || state.is(ModBlocks.ATTUNED_STONE_WALL.get())
                || state.is(ModBlocks.RUNIC_DEBRIS.get())) {
                count++;
                if (count >= 3) return 3;
            }
        }

        return count;
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        if (isShutdown()) {
            return false;
        }

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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "moveController", 2, state -> {
            if (isShutdown()) {
                return PlayState.STOP;
            }

            if (isOfferingRunebloom()) {
                state.setAndContinue(OFFER_FLOWER_LOOP);
                return PlayState.CONTINUE;
            }

            if (state.isMoving()) {
                state.setAndContinue(WALK_LOOP);
                return PlayState.CONTINUE;
            }

            state.setAndContinue(IDLE_LOOP);
            return PlayState.CONTINUE;
        }));

        controllers.add(new AnimationController<>(this, "actionController", 0, state -> PlayState.CONTINUE)
            .triggerableAnim("attack", ATTACK_ONCE)
            .triggerableAnim("shutdown", SHUTDOWN_ONCE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
