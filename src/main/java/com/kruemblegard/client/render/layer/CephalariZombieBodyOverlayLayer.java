package com.kruemblegard.client.render.layer;

import com.kruemblegard.Kruemblegard;
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
 * Renders the zombified Cephalari texture over the mount base texture for adult Zombified Cephalari.
 */
public final class CephalariZombieBodyOverlayLayer extends GeoRenderLayer<CephalariZombieEntity> {

    private static final ResourceLocation CEPHALARI_ZOMBIE_OVERLAY = new ResourceLocation(
        Kruemblegard.MOD_ID,
        "textures/entity/cephalari/cephalari_zombie.png"
    );

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
        if (animatable.isBaby() || !animatable.hasAdultMountAppearance()) {
            return;
        }

        RenderType overlayType = RenderType.entityCutoutNoCull(CEPHALARI_ZOMBIE_OVERLAY);
        VertexConsumer overlayBuffer = bufferSource.getBuffer(overlayType);
        super.render(poseStack, animatable, bakedModel, overlayType, bufferSource, overlayBuffer, partialTick, packedLight, packedOverlay);
    }
}
