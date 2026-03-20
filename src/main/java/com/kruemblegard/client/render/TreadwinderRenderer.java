package com.kruemblegard.client.render;

import com.kruemblegard.client.render.model.TreadwinderModel;
import com.kruemblegard.entity.mount.TreadwinderEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class TreadwinderRenderer extends CephalariMountRenderer<TreadwinderEntity> {

    public TreadwinderRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TreadwinderModel());
    }
}
