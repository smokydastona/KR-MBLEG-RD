package com.kruemblegard.client.gui;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.ScaralonBeetleEntity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ScaralonStaminaOverlay {

    private static final ResourceLocation MELON_SLICE_TEXTURE = new ResourceLocation(
            "minecraft",
            "textures/item/melon_slice.png"
    );

    private static final int ICONS = 10;
    private static final int ICON_STEP = 8; // matches vanilla survival HUD spacing
    private static final float ICON_SCALE = 0.5F; // 16px item texture -> 8px HUD icon

    private ScaralonStaminaOverlay() {}

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.HOTBAR.type()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) {
            return;
        }

        Player player = mc.player;
        if (player == null) {
            return;
        }

        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof ScaralonBeetleEntity scaralon)) {
            return;
        }

        int max = scaralon.getMaxFlightStaminaTicks();
        if (max <= 0) {
            return;
        }

        int stamina = Mth.clamp(scaralon.getFlightStaminaTicks(), 0, max);
        int pips = Mth.clamp(Mth.ceil(stamina * 20.0F / (float) max), 0, 20);

        GuiGraphics g = event.getGuiGraphics();
        int w = g.guiWidth();
        int h = g.guiHeight();

        // Draw where the hunger bar normally lives (it doesn't render while mounted).
        int xRight = (w / 2) + 91 - 8;
        int y = h - 39;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        var pose = g.pose();
        pose.pushPose();
        pose.scale(ICON_SCALE, ICON_SCALE, 1.0F);

        for (int i = 0; i < ICONS; i++) {
            int x = xRight - (i * ICON_STEP);
            int drawX = (int) (x / ICON_SCALE);
            int drawY = (int) (y / ICON_SCALE);

            // Always draw an "empty" (dim) melon slice background.
            RenderSystem.setShaderColor(0.22F, 0.22F, 0.22F, 1.0F);
            g.blit(MELON_SLICE_TEXTURE, drawX, drawY, 0, 0, 16, 16, 16, 16);

            // Overlay half/full fill based on pips.
            int pipFill = pips - (i * 2);
            if (pipFill >= 2) {
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                g.blit(MELON_SLICE_TEXTURE, drawX, drawY, 0, 0, 16, 16, 16, 16);
            } else if (pipFill == 1) {
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                g.blit(MELON_SLICE_TEXTURE, drawX, drawY, 0, 0, 8, 16, 16, 16);
            }
        }

        pose.popPose();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
