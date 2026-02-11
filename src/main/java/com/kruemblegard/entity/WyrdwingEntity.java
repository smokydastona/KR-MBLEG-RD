package com.kruemblegard.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WyrdwingEntity extends PathfinderMob implements GeoEntity {

    private static final EntityDataAccessor<Boolean> GLIDING =
            SynchedEntityData.defineId(WyrdwingEntity.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation IDLE_LOOP =
            RawAnimation.begin().thenLoop("animation.wyrdwing.idle");

    private static final RawAnimation WALK_LOOP =
            RawAnimation.begin().thenLoop("animation.wyrdwing.walk");

    private static final RawAnimation GLIDE_LOOP =
            RawAnimation.begin().thenLoop("animation.wyrdwing.glide");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public WyrdwingEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setMaxUpStep(1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 18.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(GLIDING, false);
    }

    public boolean isGliding() {
        return this.entityData.get(GLIDING);
    }

    private void setGliding(boolean gliding) {
        this.entityData.set(GLIDING, gliding);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15D, true));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.55F));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.onGround()) {
            setGliding(false);
            return;
        }

        // Glide when falling: slow the descent and keep some forward drift.
        Vec3 delta = this.getDeltaMovement();
        boolean shouldGlide = delta.y < -0.08D;
        setGliding(shouldGlide);

        if (!shouldGlide) {
            return;
        }

        double clampedY = Mth.clamp(delta.y, -0.12D, 0.4D);
        double forwardBoost = 0.02D;
        Vec3 look = this.getLookAngle();
        Vec3 boosted = new Vec3(
                delta.x + look.x * forwardBoost,
                clampedY,
                delta.z + look.z * forwardBoost
        );
        this.setDeltaMovement(boosted);
        this.fallDistance = 0.0F;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void playStepSound(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState blockIn) {
        this.playSound(net.minecraft.sounds.SoundEvents.PARROT_STEP, 0.15F, 1.0F);
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();
        setGliding(false);
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity entity) {
        boolean hit = super.doHurtTarget(entity);
        if (hit && entity instanceof LivingEntity living) {
            living.knockback(0.35F, Mth.sin(this.getYRot() * ((float) Math.PI / 180F)), -Mth.cos(this.getYRot() * ((float) Math.PI / 180F)));
        }
        return hit;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 4, state -> {
            if (isGliding()) {
                state.setAnimation(GLIDE_LOOP);
                return PlayState.CONTINUE;
            }

            if (state.isMoving()) {
                state.setAnimation(WALK_LOOP);
                return PlayState.CONTINUE;
            }

            state.setAnimation(IDLE_LOOP);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
