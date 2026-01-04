package com.kruemblegard.client.gui;

import com.kruemblegard.Kruemblegard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class KruemblegardBossBarOverlay {

    private static final ResourceLocation BOSS_BAR_TEXTURE = new ResourceLocation(
            Kruemblegard.MOD_ID,
            "textures/gui/kruemblegard_bossbar.png"
    );

    private static final String BOSS_ENTITY_TRANSLATION_KEY = "entity.kruemblegard.kruemblegard";

    // Matches vanilla bossbar width.
    private static final int BAR_WIDTH = 182;
    // Matches vanilla bossbar height.
    private static final int BAR_HEIGHT = 5;

    private static boolean isKruemblegardBar(Component name) {
        if (!(name.getContents() instanceof TranslatableContents translatableContents)) {
            return false;
        }
        return BOSS_ENTITY_TRANSLATION_KEY.equals(translatableContents.getKey());
    }

    @SubscribeEvent
    public static void onBossBarRender(CustomizeGuiOverlayEvent.BossEventProgress event) {
        var bossEvent = event.getBossEvent();
        if (!isKruemblegardBar(bossEvent.getName())) {
            return;
        }

        // Prevent vanilla rendering for this bar; we'll draw our own.
        event.setCanceled(true);

        GuiGraphics guiGraphics = event.getGuiGraphics();
        int x = event.getX();
        int y = event.getY();

        renderBossBar(guiGraphics, x, y, bossEvent.getProgress(), bossEvent.getName());
    }

    private static void renderBossBar(GuiGraphics guiGraphics, int x, int y, float progress, Component name) {
        Minecraft minecraft = Minecraft.getInstance();

        // Our texture is 256x32.
        // Row 0 (v=0): background (182x5)
        // Row 1 (v=5): fill (182x5)
        guiGraphics.blit(BOSS_BAR_TEXTURE, x, y, 0, 0, BAR_WIDTH, BAR_HEIGHT, 256, 32);

        int filled = (int) (progress * BAR_WIDTH);
        if (filled > 0) {
            guiGraphics.blit(BOSS_BAR_TEXTURE, x, y, 0, BAR_HEIGHT, filled, BAR_HEIGHT, 256, 32);
        }

        int textWidth = minecraft.font.width(name);
        int textX = x + (BAR_WIDTH / 2) - (textWidth / 2);
        int textY = y - 9;
        guiGraphics.drawString(minecraft.font, name, textX, textY, 0xFFFFFF, true);
    }
}
