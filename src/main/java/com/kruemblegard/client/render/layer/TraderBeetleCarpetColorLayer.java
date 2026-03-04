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

import java.util.Optional;

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
        if (animatable.isBaby()) {
            return;
        }

        DyeColor color = animatable.getCarpetColor();
        if (color == null) {
            return;
        }

        ResourceLocation overlayTexture = new ResourceLocation(
            Kruemblegard.MOD_ID,
            "textures/entity/scaralon_beetle/trader_decor/" + color.getName() + ".png"
        );

        RenderType overlayType = RenderType.entityCutoutNoCull(overlayTexture);
        VertexConsumer overlayBuffer = bufferSource.getBuffer(overlayType);

        GeoBone bone = getBone(bakedModel, CARPET_COLOR_BONE);
        if (bone == null) {
            bone = getBone(bakedModel, CARPET_BONE_FALLBACK);
        }

        if (bone == null) {
            return;
        }

        getRenderer().renderRecursively(
            poseStack,
            animatable,
            bone,
            overlayType,
            bufferSource,
            overlayBuffer,
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
