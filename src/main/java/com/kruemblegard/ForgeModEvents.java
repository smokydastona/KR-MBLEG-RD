package com.kruemblegard;

import com.kruemblegard.item.CrumblingCodexItem;
import com.kruemblegard.registry.ModItems;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ForgeModEvents {
    private static final String PLAYER_DATA_KEY = Kruemblegard.MOD_ID;
    private static final String TAG_GIVEN_CODEX = "given_crumbling_codex";

    private ForgeModEvents() {}

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        CompoundTag persistent = player.getPersistentData();
        CompoundTag modData = persistent.getCompound(PLAYER_DATA_KEY);
        if (modData.getBoolean(TAG_GIVEN_CODEX)) {
            return;
        }

        ItemStack codex = CrumblingCodexItem.createServerFilledStack(ModItems.CRUMBLING_CODEX.get(), player.level());

        boolean added = player.getInventory().add(codex);
        if (!added) {
            player.drop(codex, false);
        }

        modData.putBoolean(TAG_GIVEN_CODEX, true);
        persistent.put(PLAYER_DATA_KEY, modData);
    }
}
