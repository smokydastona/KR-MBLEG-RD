package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.ScaralonBeetleEntity;

import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;

import software.bernie.geckolib.model.GeoModel;

public class ScaralonBeetleModel extends GeoModel<ScaralonBeetleEntity> {
    private static final String SADDLE_BONE = "saddle";

    @Override
    public ResourceLocation getModelResource(ScaralonBeetleEntity animatable) {
        if (animatable.isBaby()) {
            return new ResourceLocation(Kruemblegard.MOD_ID, "geo/scaralon_larva.geo.json");
        }
        return new ResourceLocation(Kruemblegard.MOD_ID, "geo/scaralon_beetle.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ScaralonBeetleEntity animatable) {
        if (animatable.isBaby()) {
            return new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/scaralon_larva.png");
        }
        int variant = animatable.getTextureVariant();
        return new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/scaralon_beetle_" + variant + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(ScaralonBeetleEntity animatable) {
        if (animatable.isBaby()) {
            return new ResourceLocation(Kruemblegard.MOD_ID, "animations/scaralon_larva.animation.json");
        }
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/scaralon_beetle.animation.json");
    }

    @Override
    public void setCustomAnimations(ScaralonBeetleEntity animatable, long instanceId, AnimationState<ScaralonBeetleEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        // Hide saddle geometry unless the beetle is actually saddled.
        if (animatable.isBaby()) {
            return;
        }

        CoreGeoBone saddle = this.getAnimationProcessor().getBone(SADDLE_BONE);
        if (saddle != null) {
            saddle.setHidden(!animatable.isSaddled());
        }
    }
}
