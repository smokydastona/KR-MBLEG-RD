package com.kruemblegard.client.render.model;

import net.minecraft.world.entity.LivingEntity;

import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public abstract class MirroredMobGeoModel<T extends LivingEntity & GeoAnimatable> extends GeoModel<T> {
    @Override
    public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        AdultSleepPoseMirroring.apply(this, animatable);
    }
}