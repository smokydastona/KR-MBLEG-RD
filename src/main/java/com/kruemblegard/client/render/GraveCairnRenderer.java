package com.kruemblegard.client.render;

import com.kruemblegard.client.render.model.GraveCairnModel;
import com.kruemblegard.entity.GraveCairnEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GraveCairnRenderer extends GeoEntityRenderer<GraveCairnEntity> {

    public GraveCairnRenderer(EntityRendererProvider.Context context) {
        super(context, new GraveCairnModel());
        this.shadowRadius = 0.55F;
    }

    @Override
    public RenderType getRenderType(
        GraveCairnEntity animatable,
        ResourceLocation texture,
        MultiBufferSource bufferSource,
        float partialTick
    ) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
