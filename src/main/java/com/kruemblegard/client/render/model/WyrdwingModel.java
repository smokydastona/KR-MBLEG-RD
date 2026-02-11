package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.WyrdwingEntity;

import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.model.GeoModel;

public class WyrdwingModel extends GeoModel<WyrdwingEntity> {
    @Override
    public ResourceLocation getModelResource(WyrdwingEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "geo/wyrdwing.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WyrdwingEntity animatable) {
        // Placeholder texture reuse until Wyrdwing gets its own paint.
        return new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/scaralon_larva.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WyrdwingEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/wyrdwing.animation.json");
    }
}
