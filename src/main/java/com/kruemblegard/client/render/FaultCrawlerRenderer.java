package com.kruemblegard.client.render;

import com.kruemblegard.client.render.model.FaultCrawlerModel;
import com.kruemblegard.entity.FaultCrawlerEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FaultCrawlerRenderer extends GeoEntityRenderer<FaultCrawlerEntity> {
    public FaultCrawlerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FaultCrawlerModel());
        this.shadowRadius = 0.7F;
    }
}
