package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.mount.SpiralStriderEntity;

import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.model.GeoModel;

public class SpiralStriderModel extends GeoModel<SpiralStriderEntity> {

    @Override
    public ResourceLocation getModelResource(SpiralStriderEntity animatable) {
        return new ResourceLocation(Kruemblegard.MODID, "geo/spiral_strider.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SpiralStriderEntity animatable) {
        return new ResourceLocation(Kruemblegard.MODID, "textures/entity/spiral_strider.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SpiralStriderEntity animatable) {
        return new ResourceLocation(Kruemblegard.MODID, "animations/spiral_strider.animation.json");
    }
}
