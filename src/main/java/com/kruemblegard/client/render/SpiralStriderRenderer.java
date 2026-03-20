package com.kruemblegard.client.render;

import com.kruemblegard.client.render.model.SpiralStriderModel;
import com.kruemblegard.entity.adultform.SpiralStriderEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class SpiralStriderRenderer extends CephalariAdultFormRenderer<SpiralStriderEntity> {

    public SpiralStriderRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SpiralStriderModel());
    }
}
