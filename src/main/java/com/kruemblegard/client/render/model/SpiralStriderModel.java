package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.adultform.SpiralStriderEntity;

import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.core.animation.AnimationState;

public class SpiralStriderModel extends MirroredMobGeoModel<SpiralStriderEntity> {
    private static final int GOLEM_TEXTURE_VARIANT_MIN = 1;
    private static final int GOLEM_TEXTURE_VARIANT_MAX = 6;

    @Override
    public ResourceLocation getModelResource(SpiralStriderEntity animatable) {
        return new ResourceLocation(Kruemblegard.MODID, "geo/spiral_strider.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SpiralStriderEntity animatable) {
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
    public ResourceLocation getAnimationResource(SpiralStriderEntity animatable) {
        return new ResourceLocation(Kruemblegard.MODID, "animations/spiral_strider.animation.json");
    }

    @Override
    public void setCustomAnimations(SpiralStriderEntity animatable, long instanceId, AnimationState<SpiralStriderEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
    }
}
