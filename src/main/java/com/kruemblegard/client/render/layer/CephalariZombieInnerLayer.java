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
 * Inner/base layer for zombified Cephalari.
 *
 * This explicitly renders the shared inner texture as a cutout/no-cull pass.
 * It is intentionally redundant with the base model texture selection, but ensures:
 * - The inner layer is always present (even if base texture logic changes).
 * - Outer overlays with transparency (especially drowned) never render "hollow".
 */
public final class CephalariZombieInnerLayer<T extends CephalariZombieEntity> extends GeoRenderLayer<T> {
    public CephalariZombieInnerLayer(GeoRenderer<T> renderer) {
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
        if (bone == null) {
            return;
        }

        ResourceLocation innerTexture = animatable.getBodyTextureResource();
        RenderType innerType = RenderType.entityCutoutNoCull(innerTexture);
        VertexConsumer innerBuffer = bufferSource.getBuffer(innerType);

        // GeoRenderLayer base methods are no-ops in GeckoLib 4.8.
        getRenderer().renderCubesOfBone(poseStack, bone, innerBuffer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
