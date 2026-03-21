package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.UnkeeperEntity;

import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.model.GeoModel;

public class UnkeeperModel extends GeoModel<UnkeeperEntity> {

    @Override
    public ResourceLocation getModelResource(UnkeeperEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "geo/unkeeper.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(UnkeeperEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/unkeeper.png");
    }

    @Override
    public ResourceLocation getAnimationResource(UnkeeperEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/unkeeper.animation.json");
    }
}
