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
    private static int resendCooldownTicks = 0;

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

        // Periodic resend helps prevent "stuck" vertical control if a packet is dropped
        // (especially for the custom descend key).
        if (resendCooldownTicks > 0) {
            resendCooldownTicks--;
        }

        boolean shouldResend = resendCooldownTicks <= 0;

        if (mountChanged || ascendHeld != lastAscendHeld || descendHeld != lastDescendHeld || shouldResend) {
            ModNetworking.CHANNEL.sendToServer(new ScaralonFlightInputC2SPacket(mountId, ascendHeld, descendHeld));
            lastMountId = mountId;
            lastAscendHeld = ascendHeld;
            lastDescendHeld = descendHeld;
            resendCooldownTicks = 4;
        }
    }

    private static void resetIfNeeded() {
        if (lastMountId != -1 || lastAscendHeld || lastDescendHeld) {
            lastMountId = -1;
            lastAscendHeld = false;
            lastDescendHeld = false;
            resendCooldownTicks = 0;
        }
    }
}
