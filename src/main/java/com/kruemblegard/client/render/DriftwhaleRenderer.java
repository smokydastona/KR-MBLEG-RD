package com.kruemblegard.client.render;

import com.kruemblegard.client.render.model.DriftwhaleModel;
import com.kruemblegard.entity.DriftwhaleEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import com.mojang.blaze3d.vertex.PoseStack;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DriftwhaleRenderer extends GeoEntityRenderer<DriftwhaleEntity> {

    private static final float BASE_SHADOW_RADIUS = 0.8F;

    public DriftwhaleRenderer(EntityRendererProvider.Context context) {
        super(context, new DriftwhaleModel());
        this.shadowRadius = BASE_SHADOW_RADIUS;
    }

    @Override
    public void render(
        DriftwhaleEntity entity,
        float entityYaw,
        float partialTick,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int packedLight
    ) {
        float scale = entity.getSpawnScale();

        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);

        float prevShadow = this.shadowRadius;
        this.shadowRadius = BASE_SHADOW_RADIUS * scale;
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        this.shadowRadius = prevShadow;

        poseStack.popPose();
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
