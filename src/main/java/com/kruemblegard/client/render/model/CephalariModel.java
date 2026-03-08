package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.CephalariEntity;

import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.model.GeoModel;

public class CephalariModel extends GeoModel<CephalariEntity> {
    @Override
    public ResourceLocation getModelResource(CephalariEntity animatable) {
        if (animatable.isBaby() || !animatable.hasAdultMountAppearance()) {
            return new ResourceLocation(Kruemblegard.MOD_ID, "geo/cephalari.geo.json");
        }

        String geoPath = switch (animatable.getAdultMountVariant()) {
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
        if (animatable.isBaby() || !animatable.hasAdultMountAppearance()) {
            return new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/cephalari/cephalari.png");
        }

        return new ResourceLocation(
            Kruemblegard.MOD_ID,
            "textures/entity/cephalari/mounts/cephalari_mount_" + animatable.getAdultMountTextureVariant() + ".png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(CephalariEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/cephalari.animation.json");
    }
}
