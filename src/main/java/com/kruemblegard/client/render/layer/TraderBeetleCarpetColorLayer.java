package com.kruemblegard.client.render.layer;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.TraderBeetleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/**
 * Same as ScaralonCarpetColorLayer, but uses the trader beetle carpet texture set.
 */
public final class TraderBeetleCarpetColorLayer extends GeoRenderLayer<TraderBeetleEntity> {
    private static final String CARPET_COLOR_BONE = "carpet_color";
    private static final String CARPET_BONE_FALLBACK = "carpet";

    public TraderBeetleCarpetColorLayer(GeoRenderer<TraderBeetleEntity> renderer) {
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
        // No-op: render per-bone in renderForBone so overlay stays in sync with parent bone transforms.
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

        DyeColor color = animatable.getCarpetColor();
        if (color == null) {
            return;
        }

        String name = bone.getName();
        if (!CARPET_COLOR_BONE.equals(name) && !CARPET_BONE_FALLBACK.equals(name)) {
            return;
        }

        ResourceLocation overlayTexture = new ResourceLocation(
            Kruemblegard.MOD_ID,
            "textures/entity/scaralon_beetle/trader_decor/" + color.getName() + ".png"
        );

        // Critical: use cutout so fully transparent pixels do NOT write depth.
        // This prevents the overlay pass from hiding the base beetle even if the overlay geometry
        // includes fully-transparent areas.
        RenderType overlayType = RenderType.entityCutoutNoCull(overlayTexture);
        VertexConsumer overlayBuffer = bufferSource.getBuffer(overlayType);

        super.renderForBone(poseStack, animatable, bone, overlayType, bufferSource, overlayBuffer, partialTick, packedLight, packedOverlay);
    }
}
