package com.kruemblegard.client.render.layer;

import com.kruemblegard.entity.CephalariGolemEntity;
import com.kruemblegard.registry.ModItems;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/**
 * Renders a Runebloom in the Cephalari golem's hand while it is offering a flower.
 *
 * Vanilla iron golems render a poppy in-hand; this layer swaps the visual to Runebloom.
 */
public final class CephalariGolemRunebloomLayer extends GeoRenderLayer<CephalariGolemEntity> {
    private static final String HAND_BONE = "hand_right";

    public CephalariGolemRunebloomLayer(GeoRenderer<CephalariGolemEntity> renderer) {
        super(renderer);
    }

    @Override
    public void render(
        PoseStack poseStack,
        CephalariGolemEntity animatable,
        BakedGeoModel bakedModel,
        RenderType renderType,
        MultiBufferSource bufferSource,
        com.mojang.blaze3d.vertex.VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay
    ) {
        // No-op: render per-bone in renderForBone.
    }

    @Override
    public void renderForBone(
        PoseStack poseStack,
        CephalariGolemEntity animatable,
        GeoBone bone,
        RenderType renderType,
        MultiBufferSource bufferSource,
        com.mojang.blaze3d.vertex.VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay
    ) {
        if (bone == null || animatable == null) {
            return;
        }

        if (!animatable.isOfferingRunebloom()) {
            return;
        }

        if (!HAND_BONE.equals(bone.getName())) {
            return;
        }

        ItemStack runebloom = new ItemStack(ModItems.RUNEBLOOM_ITEM.get());

        poseStack.pushPose();
        // Bone space: tweak the item to sit in the palm.
        poseStack.translate(0.0D, 0.2D, -0.15D);
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        poseStack.scale(0.55F, 0.55F, 0.55F);

        Minecraft.getInstance().getItemRenderer().renderStatic(
            runebloom,
            ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
            packedLight,
            OverlayTexture.NO_OVERLAY,
            poseStack,
            bufferSource,
            animatable.level(),
            animatable.getId()
        );

        poseStack.popPose();
    }
}
