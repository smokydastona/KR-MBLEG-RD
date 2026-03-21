package com.kruemblegard.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class GraveCairnEntity extends Monster implements GeoEntity {

    private static final RawAnimation IDLE_LOOP =
        RawAnimation.begin().thenLoop("animation.grave_cairn.idle");

    private static final RawAnimation WALK_LOOP =
        RawAnimation.begin().thenLoop("animation.grave_cairn.walk");

    private static final RawAnimation ATTACK_ONCE =
        RawAnimation.begin().thenPlay("animation.grave_cairn.attack");

    private static final RawAnimation DIE_ONCE =
        RawAnimation.begin().thenPlay("animation.grave_cairn.die");

    private static final int ATTACK_ANIM_TICKS = 10;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int attackAnimTicks;
    private boolean wasSwinging;

    public GraveCairnEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.setMaxUpStep(1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 30.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.22D)
            .add(Attributes.ATTACK_DAMAGE, 7.0D)
            .add(Attributes.ARMOR, 5.0D)
            .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    public void tick() {
        super.tick();

        boolean swingingNow = this.swinging;
        if (swingingNow && !this.wasSwinging) {
            this.attackAnimTicks = ATTACK_ANIM_TICKS;
        }
        this.wasSwinging = swingingNow;

        if (this.attackAnimTicks > 0) {
            --this.attackAnimTicks;
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.85D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected net.minecraft.sounds.SoundEvent getHurtSound(DamageSource source) {
        return net.minecraft.sounds.SoundEvents.STONE_HIT;
    }

    @Override
    protected net.minecraft.sounds.SoundEvent getDeathSound() {
        return net.minecraft.sounds.SoundEvents.STONE_BREAK;
    }

    @Override
    protected void playStepSound(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        this.playSound(net.minecraft.sounds.SoundEvents.STONE_STEP, 0.25F, 0.9F + (this.random.nextFloat() * 0.2F));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "baseController", 0, state -> {
            boolean dead = this.isDeadOrDying() || this.getHealth() <= 0.0F;
            if (dead) {
                state.setAnimation(DIE_ONCE);
            } else if (this.attackAnimTicks > 0) {
                state.setAnimation(ATTACK_ONCE);
            } else {
                state.setAnimation(state.isMoving() ? WALK_LOOP : IDLE_LOOP);
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
