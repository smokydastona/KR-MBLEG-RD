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
        RawAnimation.begin().thenLoop("animation.scattered_enderman.move");

    private static final RawAnimation ANGRY_LOOP =
        RawAnimation.begin().thenLoop("animation.scattered_enderman.angry");

    private static final RawAnimation HOLD_BLOCK_LOOP =
        RawAnimation.begin().thenLoop("animation.scattered_enderman.hold_block");

    private static final RawAnimation ANGRY_HOLD_BLOCK_LOOP =
        RawAnimation.begin().thenLoop("animation.scattered_enderman.angry_hold_block");

    private static final RawAnimation ATTACK_ONESHOT =
        RawAnimation.begin().thenPlay("animation.scattered_enderman.attack");

    private static final RawAnimation TELEPORT_ONESHOT =
        RawAnimation.begin().thenPlay("animation.scattered_enderman.teleport");

    private static final int ATTACK_ANIM_TICKS = 10;
    private static final int TELEPORT_ANIM_TICKS = 10;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int attackAnimTicks;
    private int teleportAnimTicks;
    private boolean wasSwinging;

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
    public boolean randomTeleport(double x, double y, double z, boolean showParticles) {
        boolean success = super.randomTeleport(x, y, z, showParticles);
        if (success) {
            this.teleportAnimTicks = TELEPORT_ANIM_TICKS;
        }
        return success;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "mainController", 0, state -> {
            boolean angry = this.getTarget() != null;
            boolean carrying = this.getCarriedBlock() != null;

            if (this.teleportAnimTicks > 0) {
                state.setAnimation(TELEPORT_ONESHOT);
            } else if (this.attackAnimTicks > 0) {
                state.setAnimation(ATTACK_ONESHOT);
            } else if (angry && carrying) {
                state.setAnimation(ANGRY_HOLD_BLOCK_LOOP);
            } else if (carrying) {
                state.setAnimation(HOLD_BLOCK_LOOP);
            } else if (angry) {
                state.setAnimation(ANGRY_LOOP);
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
