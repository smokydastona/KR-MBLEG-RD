package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.world.WayfallSpawnIslandSavedData;
import com.kruemblegard.world.WayfallSpawnPlatform;
import com.kruemblegard.world.WayfallWorkScheduler;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Wayfall voidfall rescue.
 *
 * <p>This only triggers in extreme cases (deep below the dimension floor), and is meant to
 * prevent soft-locks if the player ends up falling in an ungenerated/unsent void due to chunk issues.
 */
@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WayfallVoidfallTeleportEvents {
    private WayfallVoidfallTeleportEvents() {}

    private static final String TAG_NEXT_RESCUE_TICK = "kruemblegard_wayfall_void_rescue_next_tick";

    // Rescue only when we are clearly in a broken state.
    private static final int RESCUE_Y_THRESHOLD = -512;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (!(event.player instanceof ServerPlayer player)) {
            return;
        }

        if (!(player.level() instanceof ServerLevel level) || !level.dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return;
        }

        if (player.isSpectator()) {
            return;
        }

        if (player.getY() > RESCUE_Y_THRESHOLD) {
            return;
        }

        int nextTick = player.getPersistentData().getInt(TAG_NEXT_RESCUE_TICK);
        if (player.tickCount < nextTick) {
            return;
        }

        // Back off a little so we don't spam rescues if something is really wrong.
        player.getPersistentData().putInt(TAG_NEXT_RESCUE_TICK, player.tickCount + 40);

        // Ensure Wayfall initialization keeps progressing.
        WayfallWorkScheduler.enqueueWayfallInit(level);

        BlockPos landing;
        if (WayfallSpawnIslandSavedData.get(level).isPlaced()) {
            landing = WayfallSpawnPlatform.ensureSpawnLanding(level);
            player.setNoGravity(false);
        } else {
            landing = WayfallSpawnPlatform.getSpawnIslandAnchor().above(8);
            player.setNoGravity(true);
        }

        // Force the landing chunk to exist before teleporting to it.
        level.getChunkAt(landing);

        player.setDeltaMovement(Vec3.ZERO);
        player.fallDistance = 0;
        player.teleportTo(level, landing.getX() + 0.5D, landing.getY(), landing.getZ() + 0.5D, player.getYRot(), player.getXRot());
    }
}
