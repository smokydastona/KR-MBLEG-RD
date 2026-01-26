package com.kruemblegard.client.render;

import com.kruemblegard.client.render.model.KruemblegardPhase3MeteorModel;
import com.kruemblegard.entity.projectile.KruemblegardPhase3MeteorEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class KruemblegardPhase3MeteorRenderer extends GeoEntityRenderer<KruemblegardPhase3MeteorEntity> {

    public KruemblegardPhase3MeteorRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new KruemblegardPhase3MeteorModel());
        this.shadowRadius = 0.0f;
    }
}
