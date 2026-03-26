package com.kruemblegard.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;

/**
 * Keeps the vanilla merchant UI, but trims long Cephalari profession headers so they do not collide
 * with the top-of-screen title area.
 */
public class CephalariMerchantScreen extends MerchantScreen {
    private static final int HEADER_PADDING = 12;
    private static final String ELLIPSIS = "...";

    public CephalariMerchantScreen(MerchantMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Component header = buildHeader();
        String headerText = fitHeader(header, this.imageWidth - HEADER_PADDING);
        int headerX = Math.max(6, (this.imageWidth - this.font.width(headerText)) / 2);

        guiGraphics.drawString(this.font, headerText, headerX, this.titleLabelY, 4210752, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }

    private Component buildHeader() {
        int traderLevel = this.menu.getTraderLevel();
        if (traderLevel > 0 && traderLevel <= 5 && this.menu.showProgressBar()) {
            return Component.translatable("merchant.title", this.title, Component.translatable("merchant.level." + traderLevel));
        }

        return this.title;
    }

    private String fitHeader(Component header, int maxWidth) {
        String raw = header.getString();
        if (this.font.width(raw) <= maxWidth) {
            return raw;
        }

        int trimmedWidth = maxWidth - this.font.width(ELLIPSIS);
        if (trimmedWidth <= 0) {
            return ELLIPSIS;
        }

        return this.font.plainSubstrByWidth(raw, trimmedWidth) + ELLIPSIS;
    }
}