package com.kruemblegard.client.render;

import com.kruemblegard.client.render.model.DriftwhaleModel;
import com.kruemblegard.entity.DriftwhaleEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DriftwhaleRenderer extends GeoEntityRenderer<DriftwhaleEntity> {

    public DriftwhaleRenderer(EntityRendererProvider.Context context) {
        super(context, new DriftwhaleModel());
        this.shadowRadius = 0.8F;
    }

    @Override
    public RenderType getRenderType(
        DriftwhaleEntity animatable,
        ResourceLocation texture,
        MultiBufferSource bufferSource,
        float partialTick
    ) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
