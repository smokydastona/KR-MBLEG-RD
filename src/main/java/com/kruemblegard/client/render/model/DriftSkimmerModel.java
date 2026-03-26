package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.adultform.DriftSkimmerEntity;

import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class DriftSkimmerModel extends GeoModel<DriftSkimmerEntity> {
    private static final int GOLEM_TEXTURE_VARIANT_MIN = 1;
    private static final int GOLEM_TEXTURE_VARIANT_MAX = 6;

    @Override
    public ResourceLocation getModelResource(DriftSkimmerEntity animatable) {
        return new ResourceLocation(Kruemblegard.MODID, "geo/driftskimmer.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DriftSkimmerEntity animatable) {
        int variant = animatable.getTextureVariant();
        if (variant < GOLEM_TEXTURE_VARIANT_MIN || variant > GOLEM_TEXTURE_VARIANT_MAX) {
            variant = GOLEM_TEXTURE_VARIANT_MIN;
        }

        return new ResourceLocation(
            Kruemblegard.MOD_ID,
            "textures/entity/cephalari/cephalari_golem/cephalari_golem_" + variant + ".png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(DriftSkimmerEntity animatable) {
        return new ResourceLocation(Kruemblegard.MODID, "animations/driftskimmer.animation.json");
    }

    @Override
    public void setCustomAnimations(DriftSkimmerEntity animatable, long instanceId, AnimationState<DriftSkimmerEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        AdultSleepPoseMirroring.apply(this, animatable);
    }
}
