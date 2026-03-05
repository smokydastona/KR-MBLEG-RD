package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.CephalariEntity;

import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.model.GeoModel;

public class CephalariModel extends GeoModel<CephalariEntity> {
    @Override
    public ResourceLocation getModelResource(CephalariEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "geo/cephalari.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CephalariEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/cephalari/cephalari.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CephalariEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/cephalari.animation.json");
    }
}
