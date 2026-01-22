package com.kruemblegard.client.render.layer;

import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.entity.MoogloomEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
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

        Minecraft minecraft = Minecraft.getInstance();
        boolean glowingOutline = minecraft.shouldEntityAppearGlowing(entity) && entity.isInvisible();
        if (entity.isInvisible() && !glowingOutline) {
            return;
        }

        BlockState griefcap = ModBlocks.GRIEFCAP.get().defaultBlockState();
        int overlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);
        BakedModel bakedModel = this.blockRenderer.getBlockModel(griefcap);

        // Match vanilla mooshroom positioning so the cap stays attached (especially to the head).
        poseStack.pushPose();
        poseStack.translate(0.2F, -0.35F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-48.0F));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        this.renderGriefcapBlock(poseStack, buffer, packedLight, glowingOutline, griefcap, overlay, bakedModel);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.2F, -0.35F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(42.0F));
        poseStack.translate(0.1F, 0.0F, -0.6F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-48.0F));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        this.renderGriefcapBlock(poseStack, buffer, packedLight, glowingOutline, griefcap, overlay, bakedModel);
        poseStack.popPose();

        poseStack.pushPose();
        this.getParentModel().getHead().translateAndRotate(poseStack);
        poseStack.translate(0.0F, -0.7F, -0.2F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-78.0F));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        this.renderGriefcapBlock(poseStack, buffer, packedLight, glowingOutline, griefcap, overlay, bakedModel);
        poseStack.popPose();
    }

    private void renderGriefcapBlock(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            boolean outline,
            BlockState state,
            int overlay,
            BakedModel model
    ) {
        if (outline) {
            this.blockRenderer.getModelRenderer().renderModel(
                    poseStack.last(),
                    buffer.getBuffer(RenderType.outline(TextureAtlas.LOCATION_BLOCKS)),
                    state,
                    model,
                    0.0F,
                    0.0F,
                    0.0F,
                    packedLight,
                    overlay
            );
        } else {
            this.blockRenderer.renderSingleBlock(state, poseStack, buffer, packedLight, overlay);
        }
    }
}
