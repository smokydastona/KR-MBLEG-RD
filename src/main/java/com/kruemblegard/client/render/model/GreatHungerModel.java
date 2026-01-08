package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.GreatHungerEntity;

import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.model.GeoModel;

public class GreatHungerModel extends GeoModel<GreatHungerEntity> {

    @Override
    public ResourceLocation getModelResource(GreatHungerEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "geo/great_hunger.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GreatHungerEntity animatable) {
        // Placeholder texture (can be replaced later with real art).
        return new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/great_hunger.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GreatHungerEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/great_hunger.animation.json");
    }
}
