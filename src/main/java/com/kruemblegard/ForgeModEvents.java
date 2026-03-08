package com.kruemblegard;

import com.kruemblegard.book.KruemblegardGuidebook;
import com.kruemblegard.config.worldgen.WorldgenTuningConfig;
import com.kruemblegard.playerdata.KruemblegardPlayerData;
import com.kruemblegard.worldgen.WorldgenValidator;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.server.ServerStartedEvent;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ForgeModEvents {
    private ForgeModEvents() {}

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        // Worldgen validation can be expensive (it touches registries and reads some structure NBTs).
        // For normal gameplay, only run it when the user explicitly enables strict validation.
        if (!WorldgenTuningConfig.get().strictValidation) {
            return;
        }

        WorldgenValidator.validate(event.getServer(), true);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        var data = KruemblegardPlayerData.read(player.getPersistentData());
        if (!data.givenGuidebook()) {
            ItemStack book = KruemblegardGuidebook.createServerFilledBook(player.server);

            boolean added = player.getInventory().add(book);
            if (!added) {
                player.drop(book, false);
            }

            data = data.withGivenGuidebook(true);
            data.write(player.getPersistentData());
        }
    }
}
