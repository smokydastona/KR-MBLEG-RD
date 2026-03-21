package com.kruemblegard.entity;

import com.kruemblegard.registry.ModSounds;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
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

public class UnkeeperEntity extends Monster implements GeoEntity {

    private static final RawAnimation IDLE_LOOP =
            RawAnimation.begin().thenLoop("animation.unkeeper.idle");

    private static final RawAnimation MOVE_LOOP =
            RawAnimation.begin().thenLoop("animation.unkeeper.move");

    private static final RawAnimation BITE_ATTACK_ONCE =
            RawAnimation.begin().thenPlay("animation.unkeeper.bite_attack");

        private static final RawAnimation DIE_ONCE =
            RawAnimation.begin().thenPlay("animation.unkeeper.die");

        private static final int BITE_ATTACK_ANIM_TICKS = 11;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int biteAttackAnimTicks;
    private boolean wasSwinging;

    public UnkeeperEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 26.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    public void tick() {
        super.tick();

        boolean swingingNow = this.swinging;
        if (swingingNow && !this.wasSwinging) {
            this.biteAttackAnimTicks = BITE_ATTACK_ANIM_TICKS;
        }
        this.wasSwinging = swingingNow;

        if (this.biteAttackAnimTicks > 0) {
            --this.biteAttackAnimTicks;
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.05D, true));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean didHurt = super.doHurtTarget(target);
        if (didHurt) {
            this.playSound(ModSounds.UNKEEPER_BITE.get(), 0.9F, 0.95F + (this.random.nextFloat() * 0.1F));
        }
        return didHurt;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return super.hurt(source, amount);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.UNKEEPER_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.UNKEEPER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.UNKEEPER_DEATH.get();
    }

    @Override
    protected void playStepSound(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        this.playSound(ModSounds.UNKEEPER_STEP.get(), 0.2F, 0.9F + (this.random.nextFloat() * 0.2F));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "baseController", 0, state -> {
            boolean dead = this.isDeadOrDying() || this.getHealth() <= 0.0F;
            if (dead) {
                state.setAnimation(DIE_ONCE);
            } else if (this.biteAttackAnimTicks > 0) {
                state.setAnimation(BITE_ATTACK_ONCE);
            } else {
                state.setAnimation(state.isMoving() ? MOVE_LOOP : IDLE_LOOP);
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
