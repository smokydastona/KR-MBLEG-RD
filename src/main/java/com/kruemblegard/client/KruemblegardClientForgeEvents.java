package com.kruemblegard.client;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.client.gui.CephalariMerchantScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class KruemblegardClientForgeEvents {
    private KruemblegardClientForgeEvents() {
    }

    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof MerchantScreen merchantScreen) || screen instanceof CephalariMerchantScreen) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        event.setNewScreen(new CephalariMerchantScreen(
                merchantScreen.getMenu(),
                minecraft.player.getInventory(),
                merchantScreen.getTitle()
        ));
    }
}