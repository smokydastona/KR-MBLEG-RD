package com.kruemblegard.client.render;

import com.kruemblegard.client.render.model.WyrdwingModel;
import com.kruemblegard.entity.WyrdwingEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WyrdwingRenderer extends GeoEntityRenderer<WyrdwingEntity> {
    public WyrdwingRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WyrdwingModel());
        this.shadowRadius = 0.5F;
    }
}
