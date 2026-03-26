package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.CephalariEntity;

import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.model.GeoModel;

public class CephalariModel extends GeoModel<CephalariEntity> {
    private static final int GOLEM_TEXTURE_VARIANT_MIN = 1;
    private static final int GOLEM_TEXTURE_VARIANT_MAX = 6;
    private static final ResourceLocation CEPHALARI_ANIMATIONS = new ResourceLocation(Kruemblegard.MOD_ID, "animations/cephalari.animation.json");

    @Override
    public ResourceLocation getModelResource(CephalariEntity animatable) {
        if (animatable.isBaby() || !animatable.hasAdultFormAppearance()) {
            return new ResourceLocation(Kruemblegard.MOD_ID, "geo/cephalari.geo.json");
        }

        String geoPath = switch (animatable.getAdultFormVariant()) {
            case 0 -> "geo/spiral_strider.geo.json";
            case 1 -> "geo/driftskimmer.geo.json";
            case 2 -> "geo/treadwinder.geo.json";
            case 3 -> "geo/echo_harness.geo.json";
            default -> "geo/cephalari.geo.json";
        };

        return new ResourceLocation(Kruemblegard.MOD_ID, geoPath);
    }

    @Override
    public ResourceLocation getTextureResource(CephalariEntity animatable) {
        if (animatable.isBaby() || !animatable.hasAdultFormAppearance()) {
            return animatable.getBodyTextureResource();
        }

        int variant = animatable.getAdultFormTextureVariant();
        if (variant < GOLEM_TEXTURE_VARIANT_MIN || variant > GOLEM_TEXTURE_VARIANT_MAX) {
            variant = GOLEM_TEXTURE_VARIANT_MIN;
        }

        return new ResourceLocation(
            Kruemblegard.MOD_ID,
            "textures/entity/cephalari/cephalari_golem/cephalari_golem_" + variant + ".png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(CephalariEntity animatable) {
        return CEPHALARI_ANIMATIONS;
    }
}
