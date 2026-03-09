package com.kruemblegard.client.render;

import com.kruemblegard.client.render.layer.ScaralonCarpetColorLayer;
import com.kruemblegard.client.render.model.ScaralonBeetleModel;
import com.kruemblegard.entity.ScaralonBeetleEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ScaralonBeetleRenderer extends GeoEntityRenderer<ScaralonBeetleEntity> {
    public ScaralonBeetleRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ScaralonBeetleModel());
        this.shadowRadius = 0.7F;

        addRenderLayer(new ScaralonCarpetColorLayer(this));
    }

    @Override
    public RenderType getRenderType(
        ScaralonBeetleEntity animatable,
        ResourceLocation texture,
        MultiBufferSource bufferSource,
        float partialTick
    ) {
        // Use cutout so fully-transparent pixels do NOT write depth.
        // This prevents transparent overlay bones (e.g., carpet meshes) from hiding the base beetle.
        return RenderType.entityCutoutNoCull(texture);
    }
}
