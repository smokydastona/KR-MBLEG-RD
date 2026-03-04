package com.kruemblegard.client.render.layer;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.TraderBeetleEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.Optional;

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
        if (animatable.isBaby()) {
            return;
        }

        RenderType blanketType = RenderType.entityCutoutNoCull(DEFAULT_CARPET_TEXTURE);
        VertexConsumer blanketBuffer = bufferSource.getBuffer(blanketType);

        GeoBone frame = getBone(bakedModel, CARPET_FRAME_BONE);
        if (frame != null) {
            renderBone(poseStack, animatable, frame, blanketType, bufferSource, blanketBuffer, partialTick, packedLight, packedOverlay);
        }

        // If no carpet color is selected, show the default color section from trader_beetle.png.
        if (!animatable.hasCarpet()) {
            GeoBone color = getBone(bakedModel, CARPET_COLOR_BONE);
            if (color != null) {
                renderBone(poseStack, animatable, color, blanketType, bufferSource, blanketBuffer, partialTick, packedLight, packedOverlay);
                return;
            }

            // Legacy/fallback: if the model doesn't have split bones, render the whole carpet.
            GeoBone carpet = getBone(bakedModel, CARPET_BONE_FALLBACK);
            if (carpet != null) {
                renderBone(poseStack, animatable, carpet, blanketType, bufferSource, blanketBuffer, partialTick, packedLight, packedOverlay);
            }
        }
    }

    private void renderBone(
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
        getRenderer().renderRecursively(
            poseStack,
            animatable,
            bone,
            renderType,
            bufferSource,
            buffer,
            true,
            partialTick,
            packedLight,
            packedOverlay,
            1.0F,
            1.0F,
            1.0F,
            1.0F
        );
    }

    private static GeoBone getBone(BakedGeoModel bakedModel, String name) {
        Optional<GeoBone> bone = bakedModel.getBone(name);
        return bone.orElse(null);
    }
}
