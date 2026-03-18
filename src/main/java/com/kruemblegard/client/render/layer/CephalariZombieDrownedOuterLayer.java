package com.kruemblegard.client.render.layer;

import com.kruemblegard.entity.CephalariZombieEntity;

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
 * Outer overlay layer for zombified Cephalari.
 *
 * The base texture is the shared inner layer; this renders one of the three outer variants
 * (zombie / husk / drowned) on top.
 */
public final class CephalariZombieDrownedOuterLayer extends GeoRenderLayer<CephalariZombieEntity> {

    private static final String CEPHALARI_ROOT_BONE = "cephalari";

    public CephalariZombieDrownedOuterLayer(GeoRenderer<CephalariZombieEntity> renderer) {
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
        // No-op: render per-bone in renderForBone so overlays only affect the embedded Cephalari subtree.
    }

    @Override
    public void renderForBone(
        PoseStack poseStack,
        CephalariZombieEntity animatable,
        GeoBone bone,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay
    ) {
        if (bone == null) {
            return;
        }

        // For babies/non-adult appearance, base renders the inner layer and this renders the outer layer over it.
        // For adult mount appearance, base is the mount texture, so restrict overlays to the embedded Cephalari subtree.
        if (!animatable.isBaby() && animatable.hasAdultMountAppearance() && !isInCephalariSubtree(bone)) {
            return;
        }

        ResourceLocation outerTexture = animatable.getOuterTextureResource();
        RenderType outerType = RenderType.entityCutoutNoCull(outerTexture);
        VertexConsumer outerBuffer = bufferSource.getBuffer(outerType);
        super.renderForBone(poseStack, animatable, bone, outerType, bufferSource, outerBuffer, partialTick, packedLight, packedOverlay);
    }

    private static boolean isInCephalariSubtree(GeoBone bone) {
        if (bone == null) {
            return false;
        }

        // Avoid matching the root itself; it is usually a pivot-only bone.
        if (CEPHALARI_ROOT_BONE.equals(bone.getName())) {
            return false;
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
