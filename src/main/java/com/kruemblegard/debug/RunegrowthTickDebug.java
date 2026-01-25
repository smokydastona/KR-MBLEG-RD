package com.kruemblegard.debug;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

import com.kruemblegard.Kruemblegard;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public final class RunegrowthTickDebug {

    // Enable with JVM arg: -Dkruemblegard.debug.runegrowthTicks=true
    private static final boolean ENABLED = Boolean.getBoolean("kruemblegard.debug.runegrowthTicks");

    private static volatile boolean loggedEnabled = false;

    private static final ConcurrentHashMap<ResourceKey<Level>, LongAdder> COUNTS = new ConcurrentHashMap<>();
    private static int serverTicks = 0;

    private RunegrowthTickDebug() {}

    public static void record(ServerLevel level) {
        if (!ENABLED) {
            return;
        }

        if (!loggedEnabled) {
            loggedEnabled = true;
            Kruemblegard.LOGGER.info("[Debug] Runegrowth tick counter enabled (-Dkruemblegard.debug.runegrowthTicks=true)");
        }

        COUNTS.computeIfAbsent(level.dimension(), ignored -> new LongAdder()).increment();
    }

    @Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static final class Events {
        private Events() {}

        @SubscribeEvent
        public static void onServerTick(TickEvent.ServerTickEvent event) {
            if (!ENABLED) {
                return;
            }

            if (event.phase != TickEvent.Phase.END) {
                return;
            }

            serverTicks++;
            if (serverTicks < 20) {
                return;
            }

            serverTicks = 0;

            StringBuilder sb = new StringBuilder();
            for (var entry : COUNTS.entrySet()) {
                long count = entry.getValue().sumThenReset();
                if (count <= 0) {
                    continue;
                }

                if (sb.length() > 0) {
                    sb.append(", ");
                }

                sb.append(entry.getKey().location()).append('=');
                sb.append(count);
            }

            if (sb.length() > 0) {
                Kruemblegard.LOGGER.info("[Debug] Runegrowth randomTicks/s: {}", sb);
            }
        }
    }
}
