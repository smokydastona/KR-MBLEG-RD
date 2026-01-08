package com.kruemblegard.client.render;

import com.kruemblegard.client.render.model.PebblitModel;
import com.kruemblegard.entity.PebblitEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PebblitRenderer extends GeoEntityRenderer<PebblitEntity> {

    public PebblitRenderer(EntityRendererProvider.Context context) {
        super(context, new PebblitModel());
        this.shadowRadius = 0.35f;
    }
}
