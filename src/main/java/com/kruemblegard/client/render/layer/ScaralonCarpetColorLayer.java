package com.kruemblegard.client.render.layer;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.ScaralonBeetleEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import software.bernie.geckolib.cache.object.BakedGeoModel;

import java.util.Optional;

/**
 * Renders only the carpet color geometry with a separate texture, so the frame can stay on the base entity texture.
 *
 * Expected texture convention:
 * - textures/entity/scaralon_beetle_carpet_<color>.png should have transparency everywhere except the color area.
 */
public final class ScaralonCarpetColorLayer extends GeoRenderLayer<ScaralonBeetleEntity> {
    private static final String CARPET_COLOR_BONE = "carpet_color";
    private static final String CARPET_BONE_FALLBACK = "carpet";

    public ScaralonCarpetColorLayer(GeoRenderer<ScaralonBeetleEntity> renderer) {
        super(renderer);
    }

    @Override
    public void render(
        PoseStack poseStack,
        ScaralonBeetleEntity animatable,
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
            "textures/entity/scaralon_beetle/decor/" + color.getName() + ".png"
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

        // Render only the carpet color geometry with the overlay texture.
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
