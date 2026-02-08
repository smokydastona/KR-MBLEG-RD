package com.kruemblegard.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.playerdata.KruemblegardPlayerData;
import com.kruemblegard.util.AdvancementUtil;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WayfallSleepEvents {
    private WayfallSleepEvents() {}

    private static final ResourceLocation ADV_TIME_IS_AN_ILLUSION = new ResourceLocation(
            Kruemblegard.MOD_ID,
            "time_is_an_illusion"
    );

    // Enough to show a short "nap" animation without trapping players in bed.
    private static final long WAYFALL_MIN_SLEEP_TICKS = 60;

    private static final Map<UUID, Long> sleepStartTick = new HashMap<>();
    private static final Set<UUID> wasSleeping = new HashSet<>();

    @SubscribeEvent
    public static void onSleepingTimeCheck(SleepingTimeCheckEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!player.level().dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return;
        }

        // Allow sleeping at any time in Wayfall.
        event.setResult(Event.Result.ALLOW);
    }

    @SubscribeEvent
    public static void onSleepFinishedTime(SleepFinishedTimeEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        if (!level.dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return;
        }

        // Prevent time skipping forward when players sleep in Wayfall.
        event.setTimeAddition(0L);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (!(event.player instanceof ServerPlayer player)) {
            return;
        }

        if (!player.level().dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            sleepStartTick.remove(player.getUUID());
            wasSleeping.remove(player.getUUID());
            return;
        }

        UUID id = player.getUUID();

        if (player.isSleeping()) {
            wasSleeping.add(id);
            sleepStartTick.putIfAbsent(id, player.level().getGameTime());

            long start = sleepStartTick.get(id);
            long sleptTicks = player.level().getGameTime() - start;

            if (sleptTicks >= WAYFALL_MIN_SLEEP_TICKS) {
                // Wake the player even though time didn't advance.
                player.stopSleepInBed(false, true);
            }

            return;
        }

        boolean wokeUp = wasSleeping.remove(id);
        sleepStartTick.remove(id);

        if (wokeUp) {
            awardFirstWayfallSleep(player);
        }
    }

    private static void awardFirstWayfallSleep(ServerPlayer player) {
        var data = KruemblegardPlayerData.read(player.getPersistentData());
        if (data.sleptInWayfall()) {
            return;
        }

        AdvancementUtil.award(player, ADV_TIME_IS_AN_ILLUSION);
        data.withSleptInWayfall(true).write(player.getPersistentData());
    }
}
