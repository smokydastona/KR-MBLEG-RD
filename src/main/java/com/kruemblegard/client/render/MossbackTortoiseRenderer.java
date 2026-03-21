package com.kruemblegard.client.render;

import com.kruemblegard.client.render.model.MossbackTortoiseModel;
import com.kruemblegard.entity.MossbackTortoiseEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MossbackTortoiseRenderer extends GeoEntityRenderer<MossbackTortoiseEntity> {

    public MossbackTortoiseRenderer(EntityRendererProvider.Context context) {
        super(context, new MossbackTortoiseModel());
        this.shadowRadius = 0.7F;
    }

    @Override
    public RenderType getRenderType(
        MossbackTortoiseEntity animatable,
        ResourceLocation texture,
        MultiBufferSource bufferSource,
        float partialTick
    ) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
