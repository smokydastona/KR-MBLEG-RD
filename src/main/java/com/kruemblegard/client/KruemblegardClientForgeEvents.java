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
import net.minecraftforge.event.TickEvent;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class KruemblegardClientForgeEvents {
    private KruemblegardClientForgeEvents() {
    }

    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening event) {
        event.setNewScreen(replaceMerchantScreen(event.getScreen()));
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Screen replacement = replaceMerchantScreen(minecraft.screen);
        if (replacement != minecraft.screen) {
            minecraft.setScreen(replacement);
        }
    }

    private static Screen replaceMerchantScreen(Screen screen) {
        if (!(screen instanceof MerchantScreen merchantScreen) || screen instanceof CephalariMerchantScreen) {
            return screen;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return screen;
        }

        return new CephalariMerchantScreen(
            merchantScreen.getMenu(),
            minecraft.player.getInventory(),
            merchantScreen.getTitle()
        );
    }
}