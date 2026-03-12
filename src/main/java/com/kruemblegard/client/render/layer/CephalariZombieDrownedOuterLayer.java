package com.kruemblegard.client.render.layer;

import com.kruemblegard.entity.CephalariZombieEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/**
 * Outer overlay layer for zombified Cephalari.
 *
 * The base texture is the shared inner layer; this renders one of the three outer variants
 * (zombie / husk / drowned) on top.
 */
public final class CephalariZombieDrownedOuterLayer extends GeoRenderLayer<CephalariZombieEntity> {

    public CephalariZombieDrownedOuterLayer(GeoRenderer<CephalariZombieEntity> renderer) {
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
        ResourceLocation outerTexture = animatable.getOuterTextureResource();
        RenderType outerType = RenderType.entityCutoutNoCull(outerTexture);
        VertexConsumer outerBuffer = bufferSource.getBuffer(outerType);
        super.render(poseStack, animatable, bakedModel, outerType, bufferSource, outerBuffer, partialTick, packedLight, packedOverlay);
    }
}
