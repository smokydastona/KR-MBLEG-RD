package com.kruemblegard.client.render;

import com.kruemblegard.client.render.model.EchoHarnessModel;
import com.kruemblegard.entity.adultform.EchoHarnessEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class EchoHarnessRenderer extends CephalariAdultFormRenderer<EchoHarnessEntity> {

    public EchoHarnessRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new EchoHarnessModel());
    }
}
