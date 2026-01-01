package com.smoky.krumblegard.client.model;

import com.smoky.krumblegard.KrumblegardMod;
import com.smoky.krumblegard.entity.boss.KrumblegardEntity;

import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.model.GeoModel;

public class KrumblegardModel extends GeoModel<KrumblegardEntity> {
    @Override
    public ResourceLocation getModelResource(KrumblegardEntity animatable) {
        return new ResourceLocation(KrumblegardMod.MODID, "geo/krumblegard.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KrumblegardEntity animatable) {
        return new ResourceLocation(KrumblegardMod.MODID, "textures/entity/krumblegard.png");
    }

    @Override
    public ResourceLocation getAnimationResource(KrumblegardEntity animatable) {
        return new ResourceLocation(KrumblegardMod.MODID, "animations/krumblegard.animation.json");
    }
}
