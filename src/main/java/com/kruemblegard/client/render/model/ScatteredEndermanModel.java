package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.ScatteredEndermanEntity;

import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.model.GeoModel;

public class ScatteredEndermanModel extends GeoModel<ScatteredEndermanEntity> {

    @Override
    public ResourceLocation getModelResource(ScatteredEndermanEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "geo/scattered_enderman.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ScatteredEndermanEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/scattered_enderman.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ScatteredEndermanEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/scattered_enderman.animation.json");
    }
}
