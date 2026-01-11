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

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ScatteredEndermanEntity(EntityType<? extends EnderMan> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EnderMan.createAttributes();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "moveController", 0, state -> {
            state.setAnimation(state.isMoving() ? MOVE_LOOP : IDLE_LOOP);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
