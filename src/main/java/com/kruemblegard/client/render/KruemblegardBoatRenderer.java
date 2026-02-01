package com.kruemblegard.client.render;

import com.kruemblegard.entity.vehicle.KruemblegardBoatEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class KruemblegardBoatRenderer extends EntityRenderer<KruemblegardBoatEntity> {
    private final BoatModel model;

    public KruemblegardBoatRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new BoatModel(context.bakeLayer(ModelLayers.createBoatModelName(net.minecraft.world.entity.vehicle.Boat.Type.OAK)));
        this.shadowRadius = 0.8F;
    }

    @Override
    public void render(KruemblegardBoatEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.375D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));

        float hurtTime = entity.getHurtTime() - partialTicks;
        float damage = entity.getDamage() - partialTicks;
        if (damage < 0.0F) {
            damage = 0.0F;
        }

        if (hurtTime > 0.0F) {
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(hurtTime) * hurtTime * damage / 10.0F * entity.getHurtDir()));
        }

        float bubbleAngle = entity.getBubbleAngle(partialTicks);
        if (!Mth.equal(bubbleAngle, 0.0F)) {
            poseStack.mulPose(Axis.YP.rotationDegrees(bubbleAngle));
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        this.model.setupAnim(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);

        ResourceLocation texture = getTextureLocation(entity);
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        this.model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(KruemblegardBoatEntity entity) {
        return entity.getKruemblegardBoatType().boatTexture();
    }
}
