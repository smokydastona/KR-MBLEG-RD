package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.GraveCairnEntity;

import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.model.GeoModel;

public class GraveCairnModel extends GeoModel<GraveCairnEntity> {

    @Override
    public ResourceLocation getModelResource(GraveCairnEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "geo/grave_cairn.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GraveCairnEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/grave_cairn.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GraveCairnEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/grave_cairn.animation.json");
    }
}
