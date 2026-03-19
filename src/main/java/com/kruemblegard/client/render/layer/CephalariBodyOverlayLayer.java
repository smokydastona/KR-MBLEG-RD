package com.kruemblegard.client.render.layer;

import com.kruemblegard.entity.CephalariEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/**
 * Renders the Cephalari body texture over the mount base texture for adult Cephalari.
 */
public final class CephalariBodyOverlayLayer extends GeoRenderLayer<CephalariEntity> {

    private static final String CEPHALARI_ROOT_BONE = "cephalari";

    public CephalariBodyOverlayLayer(GeoRenderer<CephalariEntity> renderer) {
        super(renderer);
    }

    @Override
    public void render(
        PoseStack poseStack,
        CephalariEntity animatable,
        BakedGeoModel bakedModel,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay
    ) {
        // No-op: render per-bone in renderForBone so the body overlay only affects the embedded Cephalari subtree.
    }

    @Override
    public void renderForBone(
        PoseStack poseStack,
        CephalariEntity animatable,
        GeoBone bone,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay
    ) {
        if (bone == null) {
            return;
        }

        if (animatable.isBaby() || !animatable.hasAdultMountAppearance()) {
            return;
        }

        if (!isInCephalariSubtree(bone)) {
            return;
        }

        ResourceLocation overlayTexture = animatable.getBodyTextureResource();
        RenderType overlayType = RenderType.entityCutoutNoCull(overlayTexture);
        VertexConsumer overlayBuffer = bufferSource.getBuffer(overlayType);

        // GeoRenderLayer base methods are no-ops in GeckoLib 4.8.
        getRenderer().renderCubesOfBone(poseStack, bone, overlayBuffer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static boolean isInCephalariSubtree(GeoBone bone) {
        if (bone == null) {
            return false;
        }

        // Avoid matching the root itself; it is usually a pivot-only bone.
        if (CEPHALARI_ROOT_BONE.equals(bone.getName())) {
            return false;
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
}
