package com.kruemblegard.client.render;

import com.kruemblegard.client.render.model.GreatHungerModel;
import com.kruemblegard.entity.GreatHungerEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GreatHungerRenderer extends GeoEntityRenderer<GreatHungerEntity> {

    public GreatHungerRenderer(EntityRendererProvider.Context context) {
        super(context, new GreatHungerModel());
        this.shadowRadius = 0.6f;
    }
}
