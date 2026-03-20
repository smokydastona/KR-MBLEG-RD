package com.kruemblegard.client.render;

import com.kruemblegard.client.render.model.DriftSkimmerModel;
import com.kruemblegard.entity.mount.DriftSkimmerEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class DriftSkimmerRenderer extends CephalariMountRenderer<DriftSkimmerEntity> {

    public DriftSkimmerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DriftSkimmerModel());
    }
}
