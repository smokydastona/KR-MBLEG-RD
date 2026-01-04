package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.TraprockEntity;

import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.model.GeoModel;

public class TraprockModel extends GeoModel<TraprockEntity> {

    @Override
    public ResourceLocation getModelResource(TraprockEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "geo/traprock.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TraprockEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/traprock.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TraprockEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/traprock.animation.json");
    }
}
