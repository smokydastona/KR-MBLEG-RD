package com.kruemblegard.client.render;

import com.kruemblegard.client.render.model.EchoHarnessModel;
import com.kruemblegard.entity.mount.EchoHarnessEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class EchoHarnessRenderer extends CephalariMountRenderer<EchoHarnessEntity> {

    public EchoHarnessRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new EchoHarnessModel());
    }
}
