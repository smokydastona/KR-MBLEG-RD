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
 * Renders the Cephalari body texture over the adult-form base texture for adult Cephalari.
 */
public final class CephalariBodyOverlayLayer extends GeoRenderLayer<CephalariEntity> {

    private static final String CEPHALARI_ROOT_BONE = "cephalari";

    private static final String PROFESSION_BONE = "profession";
    private static final String PROFESSION_HAT_BONE = "profession_hat";
    private static final String PROFESSION_LEVEL_BONE = "profession_level";

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

        if (animatable.isBaby() || !animatable.hasAdultFormAppearance()) {
            return;
        }

        if (!isInCephalariSubtree(bone)) {
            return;
        }

        // Do not paint over the dedicated profession overlay bones.
        // Those are rendered by CephalariProfessionLayer.
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

    private static boolean useDepthSafeOverlay(CephalariEntity animatable) {
        int adultFormVariant = animatable.getAdultFormVariant();
        return adultFormVariant == 0 || adultFormVariant == 1;
    }
}
