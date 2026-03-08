package com.kruemblegard.client.render.layer;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.CephalariEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/**
 * Renders the Cephalari body texture over the mount base texture for adult Cephalari.
 */
public final class CephalariBodyOverlayLayer extends GeoRenderLayer<CephalariEntity> {

    private static final ResourceLocation CEPHALARI_OVERLAY = new ResourceLocation(
        Kruemblegard.MOD_ID,
        "textures/entity/cephalari/cephalari.png"
    );

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
        if (animatable.isBaby() || !animatable.hasAdultMountAppearance()) {
            return;
        }

        RenderType overlayType = RenderType.entityCutoutNoCull(CEPHALARI_OVERLAY);
        VertexConsumer overlayBuffer = bufferSource.getBuffer(overlayType);
        super.render(poseStack, animatable, bakedModel, overlayType, bufferSource, overlayBuffer, partialTick, packedLight, packedOverlay);
    }
}
