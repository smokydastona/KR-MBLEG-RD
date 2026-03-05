package com.kruemblegard.client.render;

import com.kruemblegard.client.render.model.EchoHarnessModel;
import com.kruemblegard.entity.mount.EchoHarnessEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EchoHarnessRenderer extends GeoEntityRenderer<EchoHarnessEntity> {

    public EchoHarnessRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new EchoHarnessModel());
    }
}
