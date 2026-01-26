package com.kruemblegard.client.render;

import com.kruemblegard.entity.projectile.KruemblegardPhase4BeamBoltEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class KruemblegardPhase4BeamBoltRenderer extends GeoEntityRenderer<KruemblegardPhase4BeamBoltEntity> {

    public KruemblegardPhase4BeamBoltRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new com.kruemblegard.client.render.model.KruemblegardPhase4BeamBoltModel());
        this.shadowRadius = 0.0f;
    }
}
