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
        // Placeholder: reuse an existing mod texture so we don't ship a missing texture.
        // Replace with "textures/entity/great_hunger.png" when you add the real texture.
        return new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/traprock.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GreatHungerEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/great_hunger.animation.json");
    }
}
