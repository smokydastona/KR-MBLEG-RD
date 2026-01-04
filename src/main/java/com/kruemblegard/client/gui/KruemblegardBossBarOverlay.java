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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    // Enhanced-style extras
    private static final int ICON_SIZE = 16;
    private static final int ICON_PADDING = 2;

    private static final int ATLAS_WIDTH = 256;
    private static final int ATLAS_HEIGHT = 32;

    // Atlas layout (all sizes in pixels)
    // - background:  u=0,   v=0,  w=182, h=5
    // - fill:        u=0,   v=5,  w=182, h=5
    // - overlay:     u=0,   v=10, w=182, h=5
    // - flash fill:  u=0,   v=15, w=182, h=5
    // - icon:        u=182, v=0,  w=16,  h=16

    private static final Map<UUID, Float> LAST_PROGRESS = new HashMap<>();
    private static final Map<UUID, Integer> FLASH_UNTIL_TICK = new HashMap<>();
    private static final int HIT_FLASH_TICKS = 10;

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

        trackHitFlash(bossEvent.getId(), bossEvent.getProgress());

        renderBossBar(guiGraphics, x, y, bossEvent.getId(), bossEvent.getProgress(), bossEvent.getName());
    }

    private static void renderBossBar(GuiGraphics guiGraphics, int x, int y, UUID id, float progress, Component name) {
        Minecraft minecraft = Minecraft.getInstance();

        int tick = minecraft.gui.getGuiTicks();
        boolean flashing = tick < FLASH_UNTIL_TICK.getOrDefault(id, 0);

        // Icon to the left of the bar.
        int iconX = x - ICON_SIZE - ICON_PADDING;
        int iconY = y - 6;
        guiGraphics.blit(BOSS_BAR_TEXTURE, iconX, iconY, BAR_WIDTH, 0, ICON_SIZE, ICON_SIZE, ATLAS_WIDTH, ATLAS_HEIGHT);

        // Background.
        guiGraphics.blit(BOSS_BAR_TEXTURE, x, y, 0, 0, BAR_WIDTH, BAR_HEIGHT, ATLAS_WIDTH, ATLAS_HEIGHT);

        // Fill (optionally flashing).
        int filled = (int) (progress * BAR_WIDTH);
        if (filled > 0) {
            int v = flashing ? 15 : 5;
            guiGraphics.blit(BOSS_BAR_TEXTURE, x, y, 0, v, filled, BAR_HEIGHT, ATLAS_WIDTH, ATLAS_HEIGHT);
        }

        // Overlay/frame.
        guiGraphics.blit(BOSS_BAR_TEXTURE, x, y, 0, 10, BAR_WIDTH, BAR_HEIGHT, ATLAS_WIDTH, ATLAS_HEIGHT);

        int textWidth = minecraft.font.width(name);
        int textX = x + (BAR_WIDTH / 2) - (textWidth / 2);
        int textY = y - 9;
        guiGraphics.drawString(minecraft.font, name, textX, textY, 0xFFFFFF, true);
    }

    private static void trackHitFlash(UUID id, float progress) {
        Minecraft minecraft = Minecraft.getInstance();
        int tick = minecraft.gui.getGuiTicks();

        Float last = LAST_PROGRESS.get(id);
        if (last != null && progress + 1.0E-6F < last) {
            FLASH_UNTIL_TICK.put(id, tick + HIT_FLASH_TICKS);
        }
        LAST_PROGRESS.put(id, progress);
    }
}
