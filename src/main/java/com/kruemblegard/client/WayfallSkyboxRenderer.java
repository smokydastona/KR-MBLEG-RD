package com.kruemblegard.client;

import com.kruemblegard.Kruemblegard;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class WayfallSkyboxRenderer {
    private WayfallSkyboxRenderer() {}

    private static final ResourceLocation SKY_NORTH = new ResourceLocation(Kruemblegard.MOD_ID, "textures/environment/wayfall_skybox_north.png");
    private static final ResourceLocation SKY_SOUTH = new ResourceLocation(Kruemblegard.MOD_ID, "textures/environment/wayfall_skybox_south.png");
    private static final ResourceLocation SKY_EAST = new ResourceLocation(Kruemblegard.MOD_ID, "textures/environment/wayfall_skybox_east.png");
    private static final ResourceLocation SKY_WEST = new ResourceLocation(Kruemblegard.MOD_ID, "textures/environment/wayfall_skybox_west.png");
    private static final ResourceLocation SKY_UP = new ResourceLocation(Kruemblegard.MOD_ID, "textures/environment/wayfall_skybox_up.png");
    private static final ResourceLocation SKY_DOWN = new ResourceLocation(Kruemblegard.MOD_ID, "textures/environment/wayfall_skybox_down.png");

    public static void render(PoseStack poseStack, float partialTick, long gameTime) {
        // A subtle rotation + brightness pulse so the Wayfall feels "alive" even with a fixed time.
        float t = gameTime + partialTick;
        float yaw = (t * 0.05f) % 360.0f;
        float pulse = 0.85f + 0.15f * Mth.sin(t * 0.01f);

        poseStack.pushPose();
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(yaw));

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(pulse, pulse, pulse, 1.0f);

        // Render the cube around the camera.
        float s = 100.0f;
        drawFace(poseStack, SKY_NORTH, -s, -s, -s, s, s, -s); // north (z-)
        drawFace(poseStack, SKY_SOUTH, s, -s, s, -s, s, s);  // south (z+)
        drawFace(poseStack, SKY_EAST, s, -s, -s, s, s, s);   // east (x+)
        drawFace(poseStack, SKY_WEST, -s, -s, s, -s, s, -s); // west (x-)
        drawFaceUpDown(poseStack, SKY_UP, s, s, -s, -s, s, s);       // up (y+)
        drawFaceUpDown(poseStack, SKY_DOWN, -s, -s, -s, s, -s, s);   // down (y-)

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();

        poseStack.popPose();
    }

    // Draw a vertical face (quad) with UVs 0..1.
    private static void drawFace(PoseStack poseStack, ResourceLocation texture, float x1, float y1, float z1, float x2, float y2, float z2) {
        RenderSystem.setShaderTexture(0, texture);

        var pose = poseStack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(pose, x1, y1, z1).uv(0.0f, 0.0f).endVertex();
        buffer.vertex(pose, x1, y2, z1).uv(0.0f, 1.0f).endVertex();
        buffer.vertex(pose, x2, y2, z2).uv(1.0f, 1.0f).endVertex();
        buffer.vertex(pose, x2, y1, z2).uv(1.0f, 0.0f).endVertex();
        Tesselator.getInstance().end();
    }

    // Draw a horizontal face (top/bottom) with UVs 0..1.
    private static void drawFaceUpDown(PoseStack poseStack, ResourceLocation texture, float x1, float y, float z1, float x2, float y2, float z2) {
        RenderSystem.setShaderTexture(0, texture);

        var pose = poseStack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(pose, x1, y, z1).uv(0.0f, 0.0f).endVertex();
        buffer.vertex(pose, x2, y, z1).uv(1.0f, 0.0f).endVertex();
        buffer.vertex(pose, x2, y2, z2).uv(1.0f, 1.0f).endVertex();
        buffer.vertex(pose, x1, y2, z2).uv(0.0f, 1.0f).endVertex();
        Tesselator.getInstance().end();
    }
}
