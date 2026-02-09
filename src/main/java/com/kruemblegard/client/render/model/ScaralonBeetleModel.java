package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.ScaralonBeetleEntity;

import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.model.GeoModel;

public class ScaralonBeetleModel extends GeoModel<ScaralonBeetleEntity> {
    @Override
    public ResourceLocation getModelResource(ScaralonBeetleEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "geo/scaralon_beetle.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ScaralonBeetleEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/scaralon_beetle.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ScaralonBeetleEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/scaralon_beetle.animation.json");
    }
}
