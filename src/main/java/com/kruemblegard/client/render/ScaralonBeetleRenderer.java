package com.kruemblegard.client.render;

import com.kruemblegard.client.render.model.ScaralonBeetleModel;
import com.kruemblegard.entity.ScaralonBeetleEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ScaralonBeetleRenderer extends GeoEntityRenderer<ScaralonBeetleEntity> {
    public ScaralonBeetleRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ScaralonBeetleModel());
        this.shadowRadius = 0.7F;
    }
}
