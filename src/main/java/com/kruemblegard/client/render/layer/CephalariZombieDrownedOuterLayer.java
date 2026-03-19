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
 * Outer overlay layer for zombified Cephalari.
 *
 * The base texture is the shared inner layer; this renders one of the three outer variants
 * (zombie / husk / drowned) on top.
 */
public final class CephalariZombieDrownedOuterLayer<T extends CephalariZombieEntity> extends GeoRenderLayer<T> {
    public CephalariZombieDrownedOuterLayer(GeoRenderer<T> renderer) {
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
        // No-op: render per-bone in renderForBone so overlays only affect the embedded Cephalari subtree.
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

        ResourceLocation outerTexture = animatable.getOuterTextureResource();
        RenderType outerType = RenderType.entityCutoutNoCull(outerTexture);
        VertexConsumer outerBuffer = bufferSource.getBuffer(outerType);

        // GeoRenderLayer base methods are no-ops in GeckoLib 4.8.
        getRenderer().renderCubesOfBone(poseStack, bone, outerBuffer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
