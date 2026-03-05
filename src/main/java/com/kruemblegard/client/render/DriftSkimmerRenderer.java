package com.kruemblegard.client.render;

import com.kruemblegard.client.render.model.DriftSkimmerModel;
import com.kruemblegard.entity.mount.DriftSkimmerEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DriftSkimmerRenderer extends GeoEntityRenderer<DriftSkimmerEntity> {

    public DriftSkimmerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DriftSkimmerModel());
    }
}
