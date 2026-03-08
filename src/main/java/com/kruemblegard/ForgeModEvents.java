package com.kruemblegard;

import com.kruemblegard.book.KruemblegardGuidebook;
import com.kruemblegard.config.worldgen.WorldgenTuningConfig;
import com.kruemblegard.playerdata.KruemblegardPlayerData;
import com.kruemblegard.worldgen.WorldgenValidator;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ForgeModEvents {
    private ForgeModEvents() {}

    private static final long STARTUP_HANG_WATCHDOG_THRESHOLD_NANOS = TimeUnit.SECONDS.toNanos(45);
    private static final long STARTUP_HANG_WATCHDOG_DUMP_COOLDOWN_NANOS = TimeUnit.SECONDS.toNanos(45);

    private static final AtomicLong startupHangWatchdogHeartbeatNanos = new AtomicLong(System.nanoTime());
    private static final AtomicLong startupHangWatchdogLastDumpNanos = new AtomicLong(0L);
    private static volatile boolean startupHangWatchdogArmed = false;
    private static volatile Thread startupHangWatchdogThread;
    private static volatile String startupHangWatchdogServerName = "<unknown>";

    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        startupHangWatchdogServerName = String.valueOf(event.getServer());
        armStartupHangWatchdog();
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (!startupHangWatchdogArmed) {
            return;
        }

        startupHangWatchdogHeartbeatNanos.set(System.nanoTime());
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        disarmStartupHangWatchdog();
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        // If we made it here, startup completed; stop the watchdog.
        disarmStartupHangWatchdog();

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

    private static void armStartupHangWatchdog() {
        startupHangWatchdogArmed = true;
        startupHangWatchdogHeartbeatNanos.set(System.nanoTime());

        Kruemblegard.LOGGER.info(
            "Startup hang watchdog: armed (threshold={}s)",
            TimeUnit.NANOSECONDS.toSeconds(STARTUP_HANG_WATCHDOG_THRESHOLD_NANOS)
        );

        Thread thread = startupHangWatchdogThread;
        if (thread != null && thread.isAlive()) {
            return;
        }

        startupHangWatchdogThread = new Thread(ForgeModEvents::runStartupHangWatchdog, "kruemblegard-startup-hang-watchdog");
        startupHangWatchdogThread.setDaemon(true);
        startupHangWatchdogThread.start();
    }

    private static void disarmStartupHangWatchdog() {
        if (!startupHangWatchdogArmed) {
            return;
        }

        startupHangWatchdogArmed = false;
        Kruemblegard.LOGGER.info("Startup hang watchdog: disarmed");

        Thread thread = startupHangWatchdogThread;
        if (thread != null) {
            thread.interrupt();
        }
    }

    private static void runStartupHangWatchdog() {
        while (startupHangWatchdogArmed) {
            try {
                Thread.sleep(5_000);
            } catch (InterruptedException ignored) {
                // fall through
            }

            if (!startupHangWatchdogArmed) {
                return;
            }

            long now = System.nanoTime();
            long heartbeat = startupHangWatchdogHeartbeatNanos.get();
            long stallNanos = now - heartbeat;
            if (stallNanos < STARTUP_HANG_WATCHDOG_THRESHOLD_NANOS) {
                continue;
            }

            long lastDump = startupHangWatchdogLastDumpNanos.get();
            if (now - lastDump < STARTUP_HANG_WATCHDOG_DUMP_COOLDOWN_NANOS) {
                continue;
            }

            startupHangWatchdogLastDumpNanos.set(now);

            long stallSeconds = TimeUnit.NANOSECONDS.toSeconds(stallNanos);
            Kruemblegard.LOGGER.error(
                    "Startup hang watchdog: no server tick for {}s (server={}); dumping threads",
                    stallSeconds,
                    startupHangWatchdogServerName
            );

            logPotentialDeadlocks();
            logInterestingThreads();
        }
    }

    private static void logPotentialDeadlocks() {
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        long[] deadlocked = threadBean.findDeadlockedThreads();
        if (deadlocked == null || deadlocked.length == 0) {
            return;
        }

        Kruemblegard.LOGGER.error("Startup hang watchdog: JVM reports {} deadlocked threads", deadlocked.length);
        ThreadInfo[] infos = threadBean.getThreadInfo(deadlocked, true, true);
        if (infos == null) {
            return;
        }
        for (ThreadInfo info : infos) {
            if (info == null) {
                continue;
            }
            Kruemblegard.LOGGER.error(info.toString());
        }
    }

    private static void logInterestingThreads() {
        Map<Thread, StackTraceElement[]> all = Thread.getAllStackTraces();
        int logged = 0;

        for (var entry : all.entrySet()) {
            Thread thread = entry.getKey();
            if (thread == null) {
                continue;
            }

            String name = thread.getName();
            if (!isInterestingThreadName(name)) {
                continue;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("\n\nThread: ").append(name)
                    .append(" (state=").append(thread.getState()).append(", daemon=").append(thread.isDaemon()).append(")");

            for (StackTraceElement el : entry.getValue()) {
                sb.append("\n  at ").append(el);
            }

            Kruemblegard.LOGGER.error(sb.toString());
            logged++;
        }

        if (logged == 0) {
            Kruemblegard.LOGGER.error("Startup hang watchdog: no interesting threads matched for dump; totalThreads={}", all.size());
        }
    }

    private static boolean isInterestingThreadName(String name) {
        if (name == null) {
            return false;
        }

        return name.equals("Server thread")
                || name.equals("Render thread")
                || name.equals("main")
                || name.startsWith("Worker-")
                || name.startsWith("Worker-Main-")
                || name.startsWith("ForkJoinPool")
                || name.startsWith("IO-")
                || name.contains("Netty")
                || name.contains("FileSystem")
                || name.contains("Chunk")
                || name.contains("WorldGen")
                || name.contains("Telemetry")
                || name.contains("kruemblegard");
    }
}
