package com.kruemblegard.client.render;

import com.kruemblegard.client.render.layer.CephalariZombieDrownedOuterLayer;
import com.kruemblegard.client.render.layer.CephalariZombieInnerLayer;
import com.kruemblegard.client.render.layer.CephalariZombieProfessionLayer;
import com.kruemblegard.client.render.model.CephalariZombieModel;
import com.kruemblegard.entity.CephalariZombieEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtils;

public class CephalariZombieRenderer<T extends CephalariZombieEntity> extends GeoEntityRenderer<T> {
    public CephalariZombieRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CephalariZombieModel<>());
        this.shadowRadius = 0.5F;

        addRenderLayer(new CephalariZombieInnerLayer<>(this));
        addRenderLayer(new CephalariZombieDrownedOuterLayer<>(this));
        addRenderLayer(new CephalariZombieProfessionLayer<>(this));
    }

    @Override
    public void renderRecursively(
        PoseStack poseStack,
        T animatable,
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
        if (!isReRender && animatable != null && bone != null) {
            // Render in deterministic passes (inner -> outer -> profession) per bone.
            // Avoid relying on the base GeoEntityRenderer cube pass, which can:
            // - write depth through fully-transparent pixels depending on RenderType
            // - paint overlay bones with the wrong texture
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
        T animatable,
        ResourceLocation texture,
        MultiBufferSource bufferSource,
        float partialTick
    ) {
        // Cutout prevents fully-transparent pixels from writing depth (Scaralon carpet fix behavior).
        return RenderType.entityCutoutNoCull(texture);
    }
}
