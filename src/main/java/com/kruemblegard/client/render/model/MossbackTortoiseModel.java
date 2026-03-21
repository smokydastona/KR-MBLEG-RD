package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.MossbackTortoiseEntity;

import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.model.GeoModel;

public class MossbackTortoiseModel extends GeoModel<MossbackTortoiseEntity> {

    @Override
    public ResourceLocation getModelResource(MossbackTortoiseEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "geo/mossback_tortoise.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MossbackTortoiseEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/mossback_tortoise.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MossbackTortoiseEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/mossback_tortoise.animation.json");
    }
}
