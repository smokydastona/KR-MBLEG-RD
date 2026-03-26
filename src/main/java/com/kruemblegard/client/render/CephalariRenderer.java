package com.kruemblegard.client.render;

import com.kruemblegard.client.render.layer.CephalariBodyOverlayLayer;
import com.kruemblegard.client.render.layer.CephalariOuterShellLayer;
import com.kruemblegard.client.render.layer.CephalariProfessionLayer;
import com.kruemblegard.client.render.model.CephalariModel;
import com.kruemblegard.entity.CephalariEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtils;

public class CephalariRenderer extends GeoEntityRenderer<CephalariEntity> {
    public CephalariRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CephalariModel());
        this.shadowRadius = 0.5F;

        addRenderLayer(new CephalariBodyOverlayLayer(this));
        addRenderLayer(new CephalariProfessionLayer(this));
        addRenderLayer(new CephalariOuterShellLayer(this));
    }

    @Override
    public void renderRecursively(
        PoseStack poseStack,
        CephalariEntity animatable,
        GeoBone bone,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        boolean isReRender,
        float partialTick,
        int packedLight,
        int packedOverlay,
        float red,
        float green,
        float blue,
        float alpha
    ) {
        if (!isReRender
            && animatable != null
            && !animatable.isBaby()
            && animatable.hasAdultFormAppearance()
            && bone != null) {

            poseStack.pushPose();
            RenderUtils.translateMatrixToBone(poseStack, bone);
            RenderUtils.translateToPivotPoint(poseStack, bone);
            RenderUtils.rotateMatrixAroundBone(poseStack, bone);
            RenderUtils.scaleMatrixForBone(poseStack, bone);

            RenderUtils.translateAwayFromPivotPoint(poseStack, bone);

            applyRenderLayersForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);

            renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

            poseStack.popPose();
            return;
        }

        super.renderRecursively(
            poseStack,
            animatable,
            bone,
            renderType,
            bufferSource,
            buffer,
            isReRender,
            partialTick,
            packedLight,
            packedOverlay,
            red,
            green,
            blue,
            alpha
        );
    }

    @Override
    public RenderType getRenderType(
        CephalariEntity animatable,
        ResourceLocation texture,
        MultiBufferSource bufferSource,
        float partialTick
    ) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
