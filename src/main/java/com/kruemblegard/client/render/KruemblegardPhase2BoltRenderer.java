package com.kruemblegard.client.render;

import com.kruemblegard.client.render.model.KruemblegardPhase2BoltModel;
import com.kruemblegard.entity.projectile.KruemblegardPhase2BoltEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class KruemblegardPhase2BoltRenderer extends GeoEntityRenderer<KruemblegardPhase2BoltEntity> {

    public KruemblegardPhase2BoltRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new KruemblegardPhase2BoltModel());
        this.shadowRadius = 0.0f;
    }
}
