package com.kruemblegard.client.render.layer;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.ScaralonBeetleEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/**
 * When the Scaralon has a carpet, the model's base texture switches to the carpet color underlay.
 * This layer re-renders the normal beetle texture on top, so the beetle details sit above the carpet.
 */
public final class ScaralonBeetleBaseTextureLayer extends GeoRenderLayer<ScaralonBeetleEntity> {

    public ScaralonBeetleBaseTextureLayer(GeoRenderer<ScaralonBeetleEntity> renderer) {
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

        if (!animatable.hasCarpet() || animatable.getCarpetColor() == null) {
            return;
        }

        int variant = animatable.getTextureVariant();
        ResourceLocation beetleTexture = new ResourceLocation(
            Kruemblegard.MOD_ID,
            "textures/entity/scaralon_beetle/scaralon_beetle_" + variant + ".png"
        );

        RenderType beetleType = RenderType.entityCutoutNoCull(beetleTexture);
        VertexConsumer beetleBuffer = bufferSource.getBuffer(beetleType);

        // GeoRenderLayer base methods are no-ops in GeckoLib 4.8.
        // Re-render the full baked model with the beetle texture.
        getRenderer().reRender(
            bakedModel,
            poseStack,
            bufferSource,
            animatable,
            beetleType,
            beetleBuffer,
            partialTick,
            packedLight,
            packedOverlay,
            1.0F,
            1.0F,
            1.0F,
            1.0F
        );
    }
}
