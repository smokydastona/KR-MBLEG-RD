package com.kruemblegard.client.render.layer;

import com.kruemblegard.entity.adultform.CephalariAdultFormEntity;
import com.kruemblegard.entity.adultform.DriftSkimmerEntity;
import com.kruemblegard.entity.adultform.SpiralStriderEntity;

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
 * Renders the adult-form entity's body texture on the embedded {@code cephalari} subtree inside adult-form geos.
 */
public final class CephalariAdultFormBodyOverlayLayer<T extends CephalariAdultFormEntity> extends GeoRenderLayer<T> {

    private static final String CEPHALARI_ROOT_BONE = "cephalari";

    private static final String PROFESSION_BONE = "profession";
    private static final String PROFESSION_HAT_BONE = "profession_hat";
    private static final String PROFESSION_LEVEL_BONE = "profession_level";

    public CephalariAdultFormBodyOverlayLayer(GeoRenderer<T> renderer) {
        super(renderer);
    }

    @Override
    public void render(
        PoseStack poseStack,
        T animatable,
        BakedGeoModel bakedModel,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay
    ) {
        // No-op: render per-bone in renderForBone.
    }

    @Override
    public void renderForBone(
        PoseStack poseStack,
        T animatable,
        GeoBone bone,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay
    ) {
        if (bone == null || animatable == null) {
            return;
        }

        if (!isInCephalariSubtree(bone)) {
            return;
        }

        // Skip the dedicated profession overlay bones.
        String name = bone.getName();
        if (PROFESSION_BONE.equals(name) || PROFESSION_HAT_BONE.equals(name) || PROFESSION_LEVEL_BONE.equals(name)) {
            return;
        }

        ResourceLocation overlayTexture = animatable.getBodyTextureResource();
        RenderType overlayType = useDepthSafeOverlay(animatable)
            ? RenderType.entityTranslucent(overlayTexture)
            : RenderType.entityCutoutNoCull(overlayTexture);
        VertexConsumer overlayBuffer = bufferSource.getBuffer(overlayType);

        // GeoRenderLayer base methods are no-ops in GeckoLib 4.8.
        getRenderer().renderCubesOfBone(poseStack, bone, overlayBuffer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
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

    private static boolean useDepthSafeOverlay(CephalariAdultFormEntity animatable) {
        return animatable instanceof SpiralStriderEntity || animatable instanceof DriftSkimmerEntity;
    }
}
