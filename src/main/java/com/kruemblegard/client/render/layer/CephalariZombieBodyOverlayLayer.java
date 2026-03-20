package com.kruemblegard.client.render.layer;

import com.kruemblegard.entity.CephalariZombieEntity;

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
 * Renders the zombified Cephalari texture over the adult-form base texture for adult Zombified Cephalari.
 */
public final class CephalariZombieBodyOverlayLayer extends GeoRenderLayer<CephalariZombieEntity> {

    private static final String CEPHALARI_ROOT_BONE = "cephalari";

    public CephalariZombieBodyOverlayLayer(GeoRenderer<CephalariZombieEntity> renderer) {
        super(renderer);
    }

    @Override
    public void render(
        PoseStack poseStack,
        CephalariZombieEntity animatable,
        BakedGeoModel bakedModel,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay
    ) {
        // No-op: render per-bone in renderForBone so overlays only affect the embedded Cephalari subtree.
    }

    @Override
    public void renderForBone(
        PoseStack poseStack,
        CephalariZombieEntity animatable,
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

        if (animatable.isBaby() || !animatable.hasAdultFormAppearance()) {
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
