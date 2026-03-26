package com.kruemblegard.client.render.layer;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.CephalariEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/**
 * Renders the adult-form outer shell on every non-embedded-cephalari bone for live Cephalari.
 */
public final class CephalariOuterShellLayer extends GeoRenderLayer<CephalariEntity> {
    private static final String CEPHALARI_ROOT_BONE = "cephalari";

    public CephalariOuterShellLayer(GeoRenderer<CephalariEntity> renderer) {
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
        // No-op: render per-bone in renderForBone.
    }

    @Override
    public void renderForBone(
        PoseStack poseStack,
        CephalariEntity animatable,
        GeoBone bone,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay
    ) {
        if (bone == null || animatable == null || animatable.isBaby() || !animatable.hasAdultFormAppearance() || isInCephalariSubtree(bone)) {
            return;
        }

        ResourceLocation outerTexture = new ResourceLocation(
            Kruemblegard.MOD_ID,
            "textures/entity/cephalari/cephalari_golem/cephalari_golem_" + animatable.getAdultFormTextureVariant() + ".png"
        );
        VertexConsumer outerBuffer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(outerTexture));

        getRenderer().renderCubesOfBone(poseStack, bone, outerBuffer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static boolean isInCephalariSubtree(GeoBone bone) {
        if (bone == null) {
            return false;
        }

        if (CEPHALARI_ROOT_BONE.equals(bone.getName())) {
            return true;
        }

        GeoBone current = bone;
        while (current != null) {
            GeoBone parent = current.getParent();
            if (parent != null && CEPHALARI_ROOT_BONE.equals(parent.getName())) {
                return true;
            }
            current = parent;
        }

        return false;
    }
}