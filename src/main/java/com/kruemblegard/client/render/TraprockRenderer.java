package com.kruemblegard.client.render;

import com.kruemblegard.client.render.model.TraprockModel;
import com.kruemblegard.entity.TraprockEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TraprockRenderer extends GeoEntityRenderer<TraprockEntity> {

    public TraprockRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new TraprockModel());
        this.shadowRadius = 0.6f;
    }
}
