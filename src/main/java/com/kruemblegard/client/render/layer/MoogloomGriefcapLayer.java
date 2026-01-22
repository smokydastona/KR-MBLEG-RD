package com.kruemblegard.client.render.layer;

import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.entity.MoogloomEntity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.CowModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Renders Griefcap blocks on Moogloom back/head instead of vanilla mooshroom mushrooms.
 */
public final class MoogloomGriefcapLayer extends RenderLayer<MoogloomEntity, CowModel<MoogloomEntity>> {
    private final BlockRenderDispatcher blockRenderer;

    public MoogloomGriefcapLayer(RenderLayerParent<MoogloomEntity, CowModel<MoogloomEntity>> parent, BlockRenderDispatcher blockRenderer) {
        super(parent);
        this.blockRenderer = blockRenderer;
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            MoogloomEntity entity,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        if (entity.isBaby()) {
            return;
        }

        BlockState griefcap = ModBlocks.GRIEFCAP.get().defaultBlockState();

        // Render griefcaps at fixed offsets in entity space.
        // This avoids relying on protected model-part fields while still giving the intended look.

        // Back (two caps).
        poseStack.pushPose();
        poseStack.translate(0.25F, 0.9F, 0.25F);
        poseStack.scale(-0.5F, -0.5F, 0.5F);
        this.blockRenderer.renderSingleBlock(griefcap, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(-0.25F, 0.9F, 0.25F);
        poseStack.scale(-0.5F, -0.5F, 0.5F);
        this.blockRenderer.renderSingleBlock(griefcap, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        // Head (one cap).
        poseStack.pushPose();
        poseStack.translate(0.0F, 1.35F, -0.45F);
        poseStack.scale(-0.55F, -0.55F, 0.55F);
        this.blockRenderer.renderSingleBlock(griefcap, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
