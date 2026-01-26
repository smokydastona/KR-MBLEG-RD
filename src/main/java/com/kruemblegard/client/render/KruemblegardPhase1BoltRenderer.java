package com.kruemblegard.client.render;

import com.kruemblegard.client.render.model.KruemblegardPhase1BoltModel;
import com.kruemblegard.entity.projectile.KruemblegardPhase1BoltEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class KruemblegardPhase1BoltRenderer extends GeoEntityRenderer<KruemblegardPhase1BoltEntity> {

    public KruemblegardPhase1BoltRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new KruemblegardPhase1BoltModel());
        this.shadowRadius = 0.0f;
    }
}
