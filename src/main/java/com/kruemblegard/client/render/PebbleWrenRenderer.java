package com.kruemblegard.client.render;

import com.kruemblegard.client.render.model.PebbleWrenModel;
import com.kruemblegard.entity.PebbleWrenEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PebbleWrenRenderer extends GeoEntityRenderer<PebbleWrenEntity> {

    public PebbleWrenRenderer(EntityRendererProvider.Context context) {
        super(context, new PebbleWrenModel());
        this.shadowRadius = 0.25F;
    }

    @Override
    public RenderType getRenderType(
        PebbleWrenEntity animatable,
        ResourceLocation texture,
        MultiBufferSource bufferSource,
        float partialTick
    ) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
