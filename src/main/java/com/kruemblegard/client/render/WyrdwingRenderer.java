package com.kruemblegard.client.render;

import com.kruemblegard.client.render.model.WyrdwingModel;
import com.kruemblegard.entity.WyrdwingEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WyrdwingRenderer extends GeoEntityRenderer<WyrdwingEntity> {
    private static final float WYRDWING_RENDER_SCALE = 0.75F;

    public WyrdwingRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WyrdwingModel());
        this.shadowRadius = 0.5F * WYRDWING_RENDER_SCALE;
    }

    @Override
    public void render(WyrdwingEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(WYRDWING_RENDER_SCALE, WYRDWING_RENDER_SCALE, WYRDWING_RENDER_SCALE);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
