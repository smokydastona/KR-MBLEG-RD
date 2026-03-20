package com.kruemblegard.client.render;

import com.kruemblegard.client.render.model.SpiralStriderModel;
import com.kruemblegard.entity.mount.SpiralStriderEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class SpiralStriderRenderer extends CephalariMountRenderer<SpiralStriderEntity> {

    public SpiralStriderRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SpiralStriderModel());
    }
}
