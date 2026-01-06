package com.kruemblegard;

import com.kruemblegard.item.CrumblingCodexItem;
import com.kruemblegard.network.ModNetworking;
import com.kruemblegard.network.packet.CodexStateSyncS2CPacket;
import com.kruemblegard.playerdata.KruemblegardPlayerData;
import com.kruemblegard.registry.ModItems;
import com.kruemblegard.worldgen.WorldgenValidator;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ForgeModEvents {
    private ForgeModEvents() {}

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        WorldgenValidator.validate(event.getServer());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        var data = KruemblegardPlayerData.read(player.getPersistentData());
        if (!data.givenCrumblingCodex()) {
            ItemStack codex = CrumblingCodexItem.createServerFilledStack(ModItems.CRUMBLING_CODEX.get(), player.level());

            boolean added = player.getInventory().add(codex);
            if (!added) {
                player.drop(codex, false);
            }

            data = data.withGivenCrumblingCodex(true);
            data.write(player.getPersistentData());
        }

        // Keep a tiny server-truth flag in sync to avoid client-side guessing.
        ModNetworking.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new CodexStateSyncS2CPacket(data.givenCrumblingCodex()));
    }
}
