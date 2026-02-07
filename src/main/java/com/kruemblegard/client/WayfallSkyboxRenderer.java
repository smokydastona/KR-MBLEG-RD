package com.kruemblegard.client;

import com.kruemblegard.Kruemblegard;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.Optional;

public final class WayfallSkyboxRenderer {
    private WayfallSkyboxRenderer() {}

    /**
     * Emergency toggle for stability/debugging.
     * Set JVM arg: -Dkruemblegard.disableWayfallSkybox=true
     */
    private static final boolean DISABLED_BY_PROPERTY = Boolean.getBoolean("kruemblegard.disableWayfallSkybox");

    // Single-texture panorama (equirectangular) sky.
    // Expected mapping: u = 0..1 across 360°, v = 0..1 from top (north pole) to bottom (south pole).
    private static final ResourceLocation SKY_PANORAMA = new ResourceLocation(Kruemblegard.MOD_ID, "textures/environment/wayfall_panorama.png");

    public static boolean isDisabled() {
        return DISABLED_BY_PROPERTY;
    }

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

        // Panorama is required.
        if (hasResource(SKY_PANORAMA)) {
            drawPanoramaDome(poseStack, SKY_PANORAMA);
        }

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();

        poseStack.popPose();
    }

    private static boolean hasResource(ResourceLocation location) {
        // Avoid hard-failing if the user hasn't added the panorama yet.
        Optional<?> res = Minecraft.getInstance().getResourceManager().getResource(location);
        return res.isPresent();
    }

    private static void drawPanoramaDome(PoseStack poseStack, ResourceLocation texture) {
        RenderSystem.setShaderTexture(0, texture);

        // Inside-out sphere around the camera.
        final float radius = 100.0f;
        // Keep this moderately low-poly to reduce CPU cost in modpacks.
        final int slices = 48;
        final int stacks = 24;

        var pose = poseStack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        // Latitude from -90..+90 degrees (bottom to top)
        for (int i = 0; i < stacks; i++) {
            float v0 = (float) i / (float) stacks;
            float v1 = (float) (i + 1) / (float) stacks;
            float lat0 = (v0 - 0.5f) * (float) Math.PI;
            float lat1 = (v1 - 0.5f) * (float) Math.PI;

            float y0 = Mth.sin(lat0) * radius;
            float y1 = Mth.sin(lat1) * radius;
            float r0 = Mth.cos(lat0) * radius;
            float r1 = Mth.cos(lat1) * radius;

            for (int j = 0; j < slices; j++) {
                float u0 = (float) j / (float) slices;
                float u1 = (float) (j + 1) / (float) slices;
                float lon0 = (u0 - 0.5f) * (float) (Math.PI * 2.0);
                float lon1 = (u1 - 0.5f) * (float) (Math.PI * 2.0);

                float x00 = Mth.cos(lon0) * r0;
                float z00 = Mth.sin(lon0) * r0;
                float x10 = Mth.cos(lon1) * r0;
                float z10 = Mth.sin(lon1) * r0;

                float x01 = Mth.cos(lon0) * r1;
                float z01 = Mth.sin(lon0) * r1;
                float x11 = Mth.cos(lon1) * r1;
                float z11 = Mth.sin(lon1) * r1;

                // Invert winding by ordering vertices for inside view; culling is disabled anyway.
                buffer.vertex(pose, x10, y0, z10).uv(u1, 1.0f - v0).endVertex();
                buffer.vertex(pose, x00, y0, z00).uv(u0, 1.0f - v0).endVertex();
                buffer.vertex(pose, x01, y1, z01).uv(u0, 1.0f - v1).endVertex();
                buffer.vertex(pose, x11, y1, z11).uv(u1, 1.0f - v1).endVertex();
            }
        }

        Tesselator.getInstance().end();
    }

}
