package com.kruemblegard.client.gui;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.ScaralonBeetleEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ScaralonStaminaOverlay {

    private ScaralonStaminaOverlay() {}

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
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
        float pct = stamina / (float) max;

        GuiGraphics g = event.getGuiGraphics();
        int w = g.guiWidth();
        int h = g.guiHeight();

        int barWidth = 90;
        int barHeight = 8;

        int x = (w - barWidth) / 2;
        int y = h - 58;

        int filled = (int) (pct * barWidth);

        int fillColor;
        if (pct > 0.50F) {
            fillColor = 0xFF3BD66F;
        } else if (pct > 0.25F) {
            fillColor = 0xFFF2C94C;
        } else {
            fillColor = 0xFFE74C3C;
        }

        // Backplate + border
        g.fill(x - 1, y - 1, x + barWidth + 1, y + barHeight + 1, 0xAA000000);
        g.fill(x, y, x + barWidth, y + barHeight, 0xFF1B1B1B);

        // Fill
        if (filled > 0) {
            g.fill(x, y, x + filled, y + barHeight, fillColor);
        }

        // Subtle gloss line
        g.fill(x, y, x + barWidth, y + 1, 0x33FFFFFF);
    }
}
