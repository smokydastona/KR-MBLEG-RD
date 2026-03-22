package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.PebbleWrenEntity;

import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.model.GeoModel;

public class PebbleWrenModel extends GeoModel<PebbleWrenEntity> {

    @Override
    public ResourceLocation getModelResource(PebbleWrenEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "geo/pebble_wren.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PebbleWrenEntity animatable) {
        int variant = animatable.getTextureVariant();
        return new ResourceLocation(
            Kruemblegard.MOD_ID,
            "textures/entity/pebble_wren/pebble_wren_" + variant + ".png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(PebbleWrenEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/pebble_wren.animation.json");
    }
}
