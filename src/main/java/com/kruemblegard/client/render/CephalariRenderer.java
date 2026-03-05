package com.kruemblegard.client.render;

import com.kruemblegard.client.render.model.CephalariModel;
import com.kruemblegard.entity.CephalariEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CephalariRenderer extends GeoEntityRenderer<CephalariEntity> {
    public CephalariRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CephalariModel());
        this.shadowRadius = 0.5F;
    }
}
