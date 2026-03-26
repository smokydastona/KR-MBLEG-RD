package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.PebblitEntity;

import net.minecraft.resources.ResourceLocation;


public class PebblitModel extends MirroredMobGeoModel<PebblitEntity> {

    @Override
    public ResourceLocation getModelResource(PebblitEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "geo/pebblit.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PebblitEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/pebblit.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PebblitEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/pebblit.animation.json");
    }
}
