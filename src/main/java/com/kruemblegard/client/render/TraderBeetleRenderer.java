package com.kruemblegard.client.render;

import com.kruemblegard.client.render.layer.TraderBeetleCarpetColorLayer;
import com.kruemblegard.client.render.layer.TraderBeetleDefaultCarpetLayer;
import com.kruemblegard.client.render.model.TraderBeetleModel;
import com.kruemblegard.entity.TraderBeetleEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TraderBeetleRenderer extends GeoEntityRenderer<TraderBeetleEntity> {
    public TraderBeetleRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TraderBeetleModel());
        this.shadowRadius = 0.7F;

        addRenderLayer(new TraderBeetleDefaultCarpetLayer(this));
        addRenderLayer(new TraderBeetleCarpetColorLayer(this));
    }
}
