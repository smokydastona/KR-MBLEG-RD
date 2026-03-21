package com.kruemblegard.client.render.layer;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.UnkeeperEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/**
 * Renders the Unkeeper's eye glow bones at full-bright.
 *
 * Note: We intentionally render per-bone in {@link #renderForBone} because GeckoLib
 * render layers are no-ops by default; per-bone rendering keeps the glow aligned
 * with animation transforms.
 */
public final class UnkeeperEyesLayer extends GeoRenderLayer<UnkeeperEntity> {
    private static final String EYE_GLOW_L_BONE = "eye_glow_L";
    private static final String EYE_GLOW_R_BONE = "eye_glow_R";

    private static final ResourceLocation EYES_TEXTURE = new ResourceLocation(
        Kruemblegard.MOD_ID,
        "textures/entity/unkeeper_eyes.png"
    );

    public UnkeeperEyesLayer(GeoRenderer<UnkeeperEntity> renderer) {
        super(renderer);
    }

    @Override
    public void render(
        PoseStack poseStack,
        UnkeeperEntity animatable,
        BakedGeoModel bakedModel,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay
    ) {
        // No-op: render per-bone in renderForBone.
    }

    @Override
    public void renderForBone(
        PoseStack poseStack,
        UnkeeperEntity animatable,
        GeoBone bone,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay
    ) {
        String name = bone.getName();
        if (!EYE_GLOW_L_BONE.equals(name) && !EYE_GLOW_R_BONE.equals(name)) {
            return;
        }

        RenderType eyesType = RenderType.eyes(EYES_TEXTURE);
        VertexConsumer eyesBuffer = bufferSource.getBuffer(eyesType);

        getRenderer().renderCubesOfBone(
            poseStack,
            bone,
            eyesBuffer,
            LightTexture.FULL_BRIGHT,
            packedOverlay,
            1.0F,
            1.0F,
            1.0F,
            1.0F
        );
    }
}
