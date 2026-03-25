package com.kruemblegard.client.render;

import com.kruemblegard.client.render.layer.CephalariAdultFormBodyOverlayLayer;
import com.kruemblegard.client.render.layer.CephalariAdultFormProfessionOverlayLayer;
import com.kruemblegard.entity.adultform.CephalariAdultFormEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import org.joml.Matrix4f;

import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtils;

/**
 * Shared renderer for Cephalari adult-form entities.
 *
 * The adult-form geo embeds a {@code cephalari} subtree (plus profession overlay bones). We must avoid
 * painting those bones with the base texture, and instead re-texture that subtree using the adult-form
 * entity's own body texture and profession/badge layers.
 */
public class CephalariAdultFormRenderer<T extends CephalariAdultFormEntity> extends GeoEntityRenderer<T> {
    private static final String CEPHALARI_ROOT_BONE = "cephalari";

    public CephalariAdultFormRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        super(renderManager, model);
        this.shadowRadius = 0.7F;

        addRenderLayer(new CephalariAdultFormProfessionOverlayLayer<>(this));
        addRenderLayer(new CephalariAdultFormBodyOverlayLayer<>(this));
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
            poseStack.pushPose();
            RenderUtils.translateMatrixToBone(poseStack, bone);
            RenderUtils.translateToPivotPoint(poseStack, bone);
            RenderUtils.rotateMatrixAroundBone(poseStack, bone);
            RenderUtils.scaleMatrixForBone(poseStack, bone);

            if (bone.isTrackingMatrices()) {
                Matrix4f poseState = new Matrix4f(poseStack.last().pose());
                Matrix4f localMatrix = RenderUtils.invertAndMultiplyMatrices(poseState, this.entityRenderTranslations);

                bone.setModelSpaceMatrix(RenderUtils.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
                bone.setLocalSpaceMatrix(RenderUtils.translateMatrix(localMatrix, getRenderOffset(this.animatable, 1).toVector3f()));
                bone.setWorldSpaceMatrix(RenderUtils.translateMatrix(new Matrix4f(localMatrix), this.animatable.position().toVector3f()));
            }

            RenderUtils.translateAwayFromPivotPoint(poseStack, bone);

            if (!isInCephalariSubtree(bone)) {
                renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            }

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

    private static boolean isInCephalariSubtree(GeoBone bone) {
        if (bone == null) {
            return false;
        }

        if (CEPHALARI_ROOT_BONE.equals(bone.getName())) {
            return true;
        }

        GeoBone current = bone;
        while (current != null) {
            GeoBone parent = current.getParent();
            if (parent != null && CEPHALARI_ROOT_BONE.equals(parent.getName())) {
                return true;
            }
            current = parent;
        }

        return false;
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
