package com.kruemblegard.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.Level;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ScatteredEndermanEntity extends EnderMan implements GeoEntity {

    private static final RawAnimation IDLE_LOOP =
        RawAnimation.begin().thenLoop("animation.scattered_enderman.idle");

    private static final RawAnimation MOVE_LOOP =
        RawAnimation.begin().thenLoop("animation.scattered_enderman.walk");

    private static final RawAnimation LOOK_LOOP =
        RawAnimation.begin().thenLoop("animation.scattered_enderman.look");

    private static final RawAnimation STARE_LOOP =
        RawAnimation.begin().thenLoop("animation.scattered_enderman.stare");

    private static final RawAnimation ANGRY_LOOP =
        RawAnimation.begin().thenLoop("animation.scattered_enderman.scream");

    private static final RawAnimation SHAKE_LOOP =
        RawAnimation.begin().thenLoop("animation.scattered_enderman.shake");

    private static final RawAnimation HOLD_BLOCK_LOOP =
        RawAnimation.begin().thenLoop("animation.scattered_enderman.hold_block_idle");

    private static final RawAnimation ATTACK_ONESHOT =
        RawAnimation.begin().thenPlay("animation.scattered_enderman.attack");

    private static final RawAnimation TELEPORT_SEQUENCE =
        RawAnimation.begin()
            .thenPlay("animation.scattered_enderman.teleport")
            .thenPlay("animation.scattered_enderman.vanish")
            .thenPlay("animation.scattered_enderman.appear");

    private static final int ATTACK_ANIM_TICKS = 10;
    private static final int TELEPORT_ANIM_TICKS = 25;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int attackAnimTicks;
    private int teleportAnimTicks;
    private boolean wasSwinging;

    private static boolean isFacingTarget(ScatteredEndermanEntity self) {
        if (self.getTarget() == null) {
            return false;
        }
        var target = self.getTarget();
        if (!self.hasLineOfSight(target)) {
            return false;
        }
        var toTarget = target.position().subtract(self.position());
        if (toTarget.lengthSqr() < 1.0E-6D) {
            return true;
        }
        var look = self.getLookAngle();
        double dot = look.normalize().dot(toTarget.normalize());
        return dot > 0.965D;
    }

    public ScatteredEndermanEntity(EntityType<? extends EnderMan> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EnderMan.createAttributes();
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
        if (this.teleportAnimTicks > 0) {
            --this.teleportAnimTicks;
        }
    }

    @Override
    public void teleportTo(double x, double y, double z) {
        double oldX = this.getX();
        double oldY = this.getY();
        double oldZ = this.getZ();
        super.teleportTo(x, y, z);
        double dx = this.getX() - oldX;
        double dy = this.getY() - oldY;
        double dz = this.getZ() - oldZ;
        if ((dx * dx + dy * dy + dz * dz) > 1.0E-6D) {
            this.teleportAnimTicks = TELEPORT_ANIM_TICKS;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "baseController", 0, state -> {
            boolean dead = this.isDeadOrDying() || this.getHealth() <= 0.0F;
            boolean angry = this.getTarget() != null;
            boolean carrying = this.getCarriedBlock() != null;
            boolean staring = !angry && isFacingTarget(this);

            if (dead) {
                state.setAnimation(RawAnimation.begin().thenPlay("animation.scattered_enderman.death"));
            } else if (this.teleportAnimTicks > 0) {
                state.setAnimation(TELEPORT_SEQUENCE);
            } else if (this.attackAnimTicks > 0) {
                state.setAnimation(ATTACK_ONESHOT);
            } else if (carrying) {
                state.setAnimation(HOLD_BLOCK_LOOP);
            } else if (angry) {
                state.setAnimation(ANGRY_LOOP);
            } else if (staring) {
                state.setAnimation(STARE_LOOP);
            } else {
                state.setAnimation(state.isMoving() ? MOVE_LOOP : IDLE_LOOP);
            }
            return PlayState.CONTINUE;
        }));

        controllers.add(new AnimationController<>(this, "shakeController", 0, state -> {
            boolean dead = this.isDeadOrDying() || this.getHealth() <= 0.0F;
            boolean angry = this.getTarget() != null;
            if (!dead && angry && this.teleportAnimTicks <= 0 && this.attackAnimTicks <= 0) {
                state.setAnimation(SHAKE_LOOP);
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));

        controllers.add(new AnimationController<>(this, "lookController", 0, state -> {
            boolean dead = this.isDeadOrDying() || this.getHealth() <= 0.0F;
            boolean angry = this.getTarget() != null;
            boolean carrying = this.getCarriedBlock() != null;
            boolean staring = !angry && isFacingTarget(this);

            if (!dead && this.teleportAnimTicks <= 0 && this.attackAnimTicks <= 0
                && !angry && !carrying && !staring) {
                state.setAnimation(LOOK_LOOP);
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
