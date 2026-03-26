package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.MoogloomEntity;

import net.minecraft.resources.ResourceLocation;


public class MoogloomModel extends MirroredMobGeoModel<MoogloomEntity> {

    @Override
    public ResourceLocation getModelResource(MoogloomEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "geo/moogloom.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MoogloomEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/moogloom.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MoogloomEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/moogloom.animation.json");
    }
}
