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
import net.minecraft.world.entity.ai.attributes.Attributes;
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
 * Base AI is still vanilla {@link IronGolem}, but with a simple internal pressure meter:
 * - Higher pressure keeps it responsive.
 * - At zero pressure it shuts down into a dormant statue (NoAI) until recharged.
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

    private static final RawAnimation ATTACK_ONCE = RawAnimation.begin().thenPlay("animation.cephalari_golem.attack");
    private static final RawAnimation SHUTDOWN_ONCE = RawAnimation.begin().thenPlay("animation.cephalari_golem.shutdown");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private boolean wasShutdown = false;

    public CephalariGolemEntity(EntityType<? extends IronGolem> type, Level level) {
        super(type, level);
        this.setMaxUpStep(1.5F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        // Baseline tuned toward the blueprint (tough, slightly slower than an iron golem).
        return IronGolem.createAttributes()
            .add(Attributes.MAX_HEALTH, 120.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.20D)
            .add(Attributes.ATTACK_DAMAGE, 14.0D)
            .add(Attributes.ARMOR, 18.0D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.9D);
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

        // Recharge when near Cephalari pressure infrastructure.
        // (Kept intentionally simple: proximity-based, 1..3 units/sec)
        if (tickCount % 20 == 0 && getPressure() < PRESSURE_MAX) {
            int nearbySources = countNearbyPressureSources(2);
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

    private int countNearbyPressureSources(int radius) {
        BlockPos origin = blockPosition();

        int count = 0;
        for (BlockPos pos : BlockPos.betweenClosed(
                origin.offset(-radius, -radius, -radius),
                origin.offset(radius, radius, radius))) {
            if (!level().isLoaded(pos)) continue;

            var state = level().getBlockState(pos);
            if (state.is(ModBlocks.PRESSURE_CONDUIT.get())
                || state.is(ModBlocks.MEMBRANE_PUMP.get())
                || state.is(ModBlocks.PRESSURE_RAIL.get())
                || state.is(ModBlocks.AIR_LIFT_TUBE.get())) {
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

        // Pressure burst (rare): small AOE knockback when well-pressurized.
        if (getPressure() >= 20 && random.nextFloat() < 0.10F) {
            addPressure(-8);
            float radius = 3.0F;
            for (var entity : level().getEntities(this, getBoundingBox().inflate(radius), e -> e != this)) {
                if (!(entity instanceof net.minecraft.world.entity.LivingEntity living)) continue;
                if (living.isAlliedTo(this)) continue;
                living.knockback(0.7D, this.getX() - living.getX(), this.getZ() - living.getZ());
            }
        }

        // Scale damage slightly with pressure (14..20-ish via temporary attribute boost).
        // (Keep it conservative; the base attribute is already 14.)
        if (getPressure() >= 80) {
            // High pressure: a little extra punch.
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(18.0D);
        } else if (getPressure() >= 40) {
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(16.0D);
        } else {
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(14.0D);
        }

        return super.doHurtTarget(target);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "moveController", 2, state -> {
            if (isShutdown()) {
                return PlayState.STOP;
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
