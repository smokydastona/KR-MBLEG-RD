package com.kruemblegard.client.render.model;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.FaultCrawlerEntity;

import net.minecraft.resources.ResourceLocation;


public class FaultCrawlerModel extends MirroredMobGeoModel<FaultCrawlerEntity> {
    @Override
    public ResourceLocation getModelResource(FaultCrawlerEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "geo/fault_crawler.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FaultCrawlerEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "textures/entity/fault_crawler.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FaultCrawlerEntity animatable) {
        return new ResourceLocation(Kruemblegard.MOD_ID, "animations/fault_crawler.animation.json");
    }
}
