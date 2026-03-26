package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.DriftwhaleEntity;

import net.minecraft.resources.ResourceLocation;


public class DriftwhaleModel extends MirroredMobGeoModel<DriftwhaleEntity> {

    @Override
    public ResourceLocation getModelResource(DriftwhaleEntity animatable) {
        if (animatable.isBaby()) {
            return new ResourceLocation(Kruemblegard.MOD_ID, "geo/driftwhale_baby.geo.json");
        }
        return new ResourceLocation(Kruemblegard.MOD_ID, "geo/driftwhale.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DriftwhaleEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/driftwhale.png");
    }

    @Override
    public ResourceLocation getAnimationResource(DriftwhaleEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/driftwhale.animation.json");
    }
}
