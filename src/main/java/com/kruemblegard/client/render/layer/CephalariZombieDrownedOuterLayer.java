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
 * Extra outer layer for the "drowned" zombified Cephalari variant.
 *
 * This mimics vanilla Drowned's outer layer behavior by rendering an additional texture pass
 * over the base zombified texture.
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
        if (animatable.isBaby() || !animatable.hasAdultMountAppearance()) {
            return;
        }

        if (!animatable.isDrownedVariant()) {
            return;
        }

        ResourceLocation outerTexture = animatable.getDrownedOuterTextureResource();
        RenderType outerType = RenderType.entityCutoutNoCull(outerTexture);
        VertexConsumer outerBuffer = bufferSource.getBuffer(outerType);
        super.render(poseStack, animatable, bakedModel, outerType, bufferSource, outerBuffer, partialTick, packedLight, packedOverlay);
    }
}
