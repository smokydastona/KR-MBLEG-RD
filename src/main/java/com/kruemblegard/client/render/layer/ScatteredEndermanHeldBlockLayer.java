package com.kruemblegard.client.render.layer;

import com.kruemblegard.entity.ScatteredEndermanEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;

import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

/**
 * Renders the Scattered Enderman's carried block anchored to a dedicated Geo bone.
 */
public final class ScatteredEndermanHeldBlockLayer extends BlockAndItemGeoLayer<ScatteredEndermanEntity> {
    private static final String HELD_BLOCK_BONE = "held_block";

    public ScatteredEndermanHeldBlockLayer(GeoRenderer<ScatteredEndermanEntity> renderer) {
        super(
            renderer,
            (bone, entity) -> ItemStack.EMPTY,
            (bone, entity) -> {
                if (!HELD_BLOCK_BONE.equals(bone.getName())) {
                    return null;
                }

                var carried = entity.getCarriedBlock();
                if (carried == null || carried.getRenderShape() == RenderShape.INVISIBLE) {
                    return null;
                }

                return carried;
            }
        );
    }

    @Override
    public void renderForBone(
        PoseStack poseStack,
        ScatteredEndermanEntity animatable,
        GeoBone bone,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay
    ) {
        if (!HELD_BLOCK_BONE.equals(bone.getName())) {
            return;
        }

        poseStack.pushPose();
        // Double the held-block size to better match the Scattered Enderman model proportions.
        poseStack.scale(1.0F, 1.0F, 1.0F);
        super.renderForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
