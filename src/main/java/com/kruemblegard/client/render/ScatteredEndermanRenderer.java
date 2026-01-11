package com.kruemblegard.client.render;

import com.kruemblegard.client.render.model.ScatteredEndermanModel;
import com.kruemblegard.entity.ScatteredEndermanEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ScatteredEndermanRenderer extends GeoEntityRenderer<ScatteredEndermanEntity> {
    public ScatteredEndermanRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ScatteredEndermanModel());
        this.shadowRadius = 0.6f;
    }
}
