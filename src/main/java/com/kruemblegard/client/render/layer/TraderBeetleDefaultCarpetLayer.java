package com.kruemblegard.client.render.layer;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.TraderBeetleEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import software.bernie.geckolib.cache.object.BakedGeoModel;

/**
 * Renders the default Trader Beetle "blanket" texture on the carpet bones.
 *
 * Behavior:
 * - Always renders the carpet frame from trader_beetle.png.
 * - If no carpet is attached, also renders the carpet color bone from trader_beetle.png.
 * - If a carpet is attached, the color bone is expected to be provided by the color overlay layer.
 */
public final class TraderBeetleDefaultCarpetLayer extends GeoRenderLayer<TraderBeetleEntity> {
    private static final String CARPET_FRAME_BONE = "carpet_frame";
    private static final String CARPET_COLOR_BONE = "carpet_color";
    private static final String CARPET_BONE_FALLBACK = "carpet";

    private static final ResourceLocation DEFAULT_CARPET_TEXTURE = new ResourceLocation(
        Kruemblegard.MOD_ID,
        "textures/entity/scaralon_beetle/trader_decor/trader_beetle.png"
    );

    public TraderBeetleDefaultCarpetLayer(GeoRenderer<TraderBeetleEntity> renderer) {
        super(renderer);
    }

    @Override
    public void render(
        PoseStack poseStack,
        TraderBeetleEntity animatable,
        BakedGeoModel bakedModel,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay
    ) {
        // No-op: render per-bone in renderForBone so the blanket stays aligned with animation transforms.
    }

    @Override
    public void renderForBone(
        PoseStack poseStack,
        TraderBeetleEntity animatable,
        GeoBone bone,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay
    ) {
        if (animatable.isBaby()) {
            return;
        }

        String name = bone.getName();
        boolean renderFrame = CARPET_FRAME_BONE.equals(name);
        boolean renderDefaultColor = !animatable.hasCarpet() && CARPET_COLOR_BONE.equals(name);
        boolean renderFallbackWholeCarpet = !animatable.hasCarpet() && CARPET_BONE_FALLBACK.equals(name);

        if (!renderFrame && !renderDefaultColor && !renderFallbackWholeCarpet) {
            return;
        }

        RenderType blanketType = RenderType.entityCutoutNoCull(DEFAULT_CARPET_TEXTURE);
        VertexConsumer blanketBuffer = bufferSource.getBuffer(blanketType);

        super.renderForBone(poseStack, animatable, bone, blanketType, bufferSource, blanketBuffer, partialTick, packedLight, packedOverlay);
    }
}
