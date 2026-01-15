package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.world.WayfallSpawnPlatform;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WayfallVoidfallTeleportEvents {
    private WayfallVoidfallTeleportEvents() {}

    private static final String ROOT_KEY = Kruemblegard.MOD_ID;

    private static final String TAG_VOIDFALL = "voidfall";
    private static final String TAG_COOLDOWN_UNTIL_TICK = "cooldown_until_tick";

    // Wayfall void begins below the island band (cutoff currently at Y≈96).
    // Trigger well below that to avoid false positives during normal traversal.
    private static final double VOIDFALL_EARLY_TRIGGER_Y = 80.0;
    private static final int VOIDFALL_CLEARANCE_SCAN = 32;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (!(event.player instanceof ServerPlayer player)) {
            return;
        }

        if (player.isDeadOrDying()) {
            return;
        }

        if (!player.level().dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return;
        }

        // Optional early rescue: only when we're clearly falling into empty space below the islands.
        // This avoids the "randomly got yanked" feeling when jumping around on low terrain.
        if (isLikelyVoidfall(player)) {
            rescueToWayfallSpawn(player, player.server);
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!player.level().dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return;
        }

        // Cancel the void before it applies damage.
        if (!"fell_out_of_world".equals(event.getSource().getMsgId())) {
            return;
        }

        if (rescueToWayfallSpawn(player, player.server)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!player.level().dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return;
        }

        if (!"fell_out_of_world".equals(event.getSource().getMsgId())) {
            return;
        }

        if (rescueToWayfallSpawn(player, player.server)) {
            event.setCanceled(true);
        }
    }

    private static boolean rescueToWayfallSpawn(ServerPlayer player, MinecraftServer server) {
        CompoundTag voidfall = getOrCreateVoidfallTag(player);

        long now = server.getTickCount();
        if (now < voidfall.getLong(TAG_COOLDOWN_UNTIL_TICK)) {
            return false;
        }

        ServerLevel wayfall = server.getLevel(ModWorldgenKeys.Levels.WAYFALL);
        if (wayfall == null) {
            return false;
        }

        BlockPos landing = WayfallSpawnPlatform.ensureSpawnLanding(wayfall);

        // Teleport.
        player.fallDistance = 0;
        player.setDeltaMovement(Vec3.ZERO);

        player.teleportTo(
                wayfall,
                landing.getX() + 0.5,
                landing.getY(),
                landing.getZ() + 0.5,
                player.getYRot(),
                player.getXRot()
        );

        voidfall.putLong(TAG_COOLDOWN_UNTIL_TICK, now + 40);
        flushVoidfallTag(player, voidfall);
        return true;
    }

    private static boolean isLikelyVoidfall(ServerPlayer player) {
        if (player.onGround()) {
            return false;
        }

        if (player.getDeltaMovement().y > -0.15) {
            return false;
        }

        if (player.getY() > VOIDFALL_EARLY_TRIGGER_Y) {
            return false;
        }

        if (!(player.level() instanceof ServerLevel level)) {
            return false;
        }

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos(player.getBlockX(), player.getBlockY(), player.getBlockZ());

        // If there's any solid collision below within a short distance, don't rescue yet.
        for (int i = 0; i < VOIDFALL_CLEARANCE_SCAN && cursor.getY() > level.getMinBuildHeight(); i++) {
            cursor.move(0, -1, 0);
            BlockState below = level.getBlockState(cursor);
            if (!below.getFluidState().isEmpty()) {
                return false;
            }
            if (!below.getCollisionShape(level, cursor).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    private static CompoundTag getOrCreateVoidfallTag(ServerPlayer player) {
        CompoundTag persistent = player.getPersistentData();
        CompoundTag root;
        if (persistent.contains(ROOT_KEY, Tag.TAG_COMPOUND)) {
            root = persistent.getCompound(ROOT_KEY);
        } else {
            root = new CompoundTag();
            persistent.put(ROOT_KEY, root);
        }

        CompoundTag voidfall;
        if (root.contains(TAG_VOIDFALL, Tag.TAG_COMPOUND)) {
            voidfall = root.getCompound(TAG_VOIDFALL);
        } else {
            voidfall = new CompoundTag();
            root.put(TAG_VOIDFALL, voidfall);
        }

        return voidfall;
    }

    private static void flushVoidfallTag(ServerPlayer player, CompoundTag voidfall) {
        CompoundTag persistent = player.getPersistentData();
        CompoundTag root = persistent.getCompound(ROOT_KEY);
        root.put(TAG_VOIDFALL, voidfall);
        persistent.put(ROOT_KEY, root);
    }


}
