package com.kruemblegard.client.render.layer;

import com.kruemblegard.client.render.PebblitShoulderRenderContext;
import com.kruemblegard.entity.PebblitEntity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.HumanoidArm;

public final class PebblitShoulderLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public PebblitShoulderLayer(net.minecraft.client.renderer.entity.RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            AbstractClientPlayer player,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch) {

        PebblitEntity pebblit = player.getPassengers().stream()
                .filter(e -> e instanceof PebblitEntity)
                .map(e -> (PebblitEntity) e)
                .findFirst()
                .orElse(null);

        if (pebblit == null) {
            return;
        }

        poseStack.pushPose();

        HumanoidArm arm = player.getMainArm();
        if (arm == HumanoidArm.LEFT) {
            this.getParentModel().leftArm.translateAndRotate(poseStack);
        } else {
            this.getParentModel().rightArm.translateAndRotate(poseStack);
        }

        // Shoulder-ish offset (player-model space). Keep it tight so it reads like an armor piece.
        double side = (arm == HumanoidArm.LEFT) ? -0.15D : 0.15D;
        poseStack.translate(side, -0.10D, -0.05D);
        poseStack.scale(0.85F, 0.85F, 0.85F);

        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();

        PebblitShoulderRenderContext.withInShoulderLayer(() -> {
            float yaw = player.getYRot();
            dispatcher.render(pebblit, 0.0D, 0.0D, 0.0D, yaw, partialTick, poseStack, buffer, packedLight);
        });

        poseStack.popPose();
    }
}
