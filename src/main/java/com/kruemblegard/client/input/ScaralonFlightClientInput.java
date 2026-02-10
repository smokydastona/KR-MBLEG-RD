package com.kruemblegard.client.input;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.ScaralonBeetleEntity;
import com.kruemblegard.network.ModNetworking;
import com.kruemblegard.network.ScaralonFlightInputC2SPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ScaralonFlightClientInput {

    private static boolean lastAscendHeld = false;
    private static boolean lastDescendHeld = false;
    private static int lastMountId = -1;

    private ScaralonFlightClientInput() {}

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) {
            return;
        }

        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof ScaralonBeetleEntity scaralon)) {
            resetIfNeeded();
            return;
        }

        boolean ascendHeld = mc.options.keyJump.isDown();
        boolean descendHeld = ModKeyMappings.SCARALON_DESCEND.isDown();

        int mountId = scaralon.getId();
        boolean mountChanged = (mountId != lastMountId);

        if (mountChanged || ascendHeld != lastAscendHeld || descendHeld != lastDescendHeld) {
            ModNetworking.CHANNEL.sendToServer(new ScaralonFlightInputC2SPacket(mountId, ascendHeld, descendHeld));
            lastMountId = mountId;
            lastAscendHeld = ascendHeld;
            lastDescendHeld = descendHeld;
        }
    }

    private static void resetIfNeeded() {
        if (lastMountId != -1 || lastAscendHeld || lastDescendHeld) {
            lastMountId = -1;
            lastAscendHeld = false;
            lastDescendHeld = false;
        }
    }
}
