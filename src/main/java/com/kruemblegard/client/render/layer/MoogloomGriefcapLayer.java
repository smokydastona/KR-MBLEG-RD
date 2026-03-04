package com.kruemblegard.client.render.layer;

import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.entity.MoogloomEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

/**
 * Renders Griefcap blocks on Moogloom back/head instead of vanilla mooshroom mushrooms.
 */
public final class MoogloomGriefcapLayer extends BlockAndItemGeoLayer<MoogloomEntity> {
    private static final String GRIEFCAP_BACK_1_BONE = "griefcap_back_1";
    private static final String GRIEFCAP_BACK_2_BONE = "griefcap_back_2";
    private static final String GRIEFCAP_HEAD_BONE = "griefcap_head";

    private static final float GRIEFCAP_SCALE = 0.5F;
    private static final float GRIEFCAP_Y_ADJUST = (1.0F - GRIEFCAP_SCALE) * 0.5F;

    public MoogloomGriefcapLayer(GeoRenderer<MoogloomEntity> renderer) {
        super(
            renderer,
            (bone, entity) -> ItemStack.EMPTY,
            (bone, entity) -> {
                String name = bone.getName();
                if (!GRIEFCAP_BACK_1_BONE.equals(name)
                    && !GRIEFCAP_BACK_2_BONE.equals(name)
                    && !GRIEFCAP_HEAD_BONE.equals(name)) {
                    return null;
                }

                return ModBlocks.GRIEFCAP.get().defaultBlockState();
            }
        );
    }

    @Override
    public void renderForBone(
        PoseStack poseStack,
        MoogloomEntity entity,
        GeoBone bone,
        net.minecraft.client.renderer.RenderType renderType,
        net.minecraft.client.renderer.MultiBufferSource bufferSource,
        com.mojang.blaze3d.vertex.VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay
    ) {
        String name = bone.getName();
        if (!GRIEFCAP_BACK_1_BONE.equals(name)
            && !GRIEFCAP_BACK_2_BONE.equals(name)
            && !GRIEFCAP_HEAD_BONE.equals(name)) {
            return;
        }

        if (entity.isBaby()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        boolean glowingOutline = minecraft.shouldEntityAppearGlowing(entity) && entity.isInvisible();
        if (entity.isInvisible() && !glowingOutline) {
            return;
        }

        poseStack.pushPose();

        // Match the existing pre-Geo transforms closely so the caps sit in the same spots.
        if (GRIEFCAP_BACK_1_BONE.equals(name)) {
            poseStack.translate(0.2F, -0.35F + GRIEFCAP_Y_ADJUST, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(-48.0F));
        } else if (GRIEFCAP_BACK_2_BONE.equals(name)) {
            poseStack.translate(0.2F, -0.35F + GRIEFCAP_Y_ADJUST, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(42.0F));
            poseStack.translate(0.1F, 0.0F, -0.6F);
            poseStack.mulPose(Axis.YP.rotationDegrees(-48.0F));
        } else {
            poseStack.translate(0.0F, -0.7F + GRIEFCAP_Y_ADJUST, -0.2F);
            poseStack.mulPose(Axis.YP.rotationDegrees(-78.0F));
        }

        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        poseStack.scale(GRIEFCAP_SCALE, GRIEFCAP_SCALE, GRIEFCAP_SCALE);

        super.renderForBone(poseStack, entity, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);

        poseStack.popPose();
    }
}
