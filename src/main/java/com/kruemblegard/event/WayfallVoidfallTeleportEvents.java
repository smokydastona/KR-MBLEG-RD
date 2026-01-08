package com.kruemblegard.event;

import java.util.ArrayList;
import java.util.List;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WayfallVoidfallTeleportEvents {
    private WayfallVoidfallTeleportEvents() {}

    private static final String ROOT_KEY = Kruemblegard.MOD_ID;

    private static final String TAG_VOIDFALL = "voidfall";
    private static final String TAG_TARGET = "target";
    private static final String TAG_TARGET_DIM = "dim";
    private static final String TAG_TARGET_X = "x";
    private static final String TAG_TARGET_Y = "y";
    private static final String TAG_TARGET_Z = "z";

    private static final String TAG_COOLDOWN_UNTIL_TICK = "cooldown_until_tick";
    private static final String TAG_LAST_ASSIGN_TICK = "last_assign_tick";

    // Wayfall’s intended void band starts below Y=130.
    // Trigger a bit below that so we don’t accidentally fire during normal island traversal.
    private static final double VOIDFALL_TRIGGER_Y = 120.0;

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

        MinecraftServer server = player.server;

        // Ensure we always have a “next” escape destination prepared ahead of time.
        ensureVoidfallTarget(player, server);

        // Optional early trigger (before OUT_OF_WORLD damage fires).
        // Keeps the experience snappy and avoids long free-fall.
        if (player.getY() < VOIDFALL_TRIGGER_Y && player.getDeltaMovement().y < -0.15 && !player.onGround()) {
            tryTeleportVoidfall(player, server);
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!player.level().dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return;
        }

        // In 1.20.1 the void damage source is exposed via DamageSources.
        if (event.getSource() != player.damageSources().fellOutOfWorld()) {
            return;
        }

        // Don’t let the void kill you in Wayfall; whisk you elsewhere.
        boolean teleported = tryTeleportVoidfall(player, player.server);
        if (teleported) {
            event.setCanceled(true);
        }
    }

    private static void ensureVoidfallTarget(ServerPlayer player, MinecraftServer server) {
        CompoundTag voidfall = getOrCreateVoidfallTag(player);

        long now = server.getTickCount();
        long lastAssign = voidfall.getLong(TAG_LAST_ASSIGN_TICK);

        if (voidfall.contains(TAG_TARGET, Tag.TAG_COMPOUND)) {
            // Already have a target.
            return;
        }

        // Rate-limit expensive scanning/chunk loads.
        if (now - lastAssign < 200) {
            return;
        }

        voidfall.putLong(TAG_LAST_ASSIGN_TICK, now);

        RandomSource rng = RandomSource.create(player.getUUID().getLeastSignificantBits() ^ now);
        VoidfallTarget target = computeRandomSafeTarget(server, rng);
        if (target == null) {
            return;
        }

        writeTarget(voidfall, target);
        flushVoidfallTag(player, voidfall);
    }

    private static boolean tryTeleportVoidfall(ServerPlayer player, MinecraftServer server) {
        CompoundTag voidfall = getOrCreateVoidfallTag(player);

        long now = server.getTickCount();
        if (now < voidfall.getLong(TAG_COOLDOWN_UNTIL_TICK)) {
            return false;
        }

        VoidfallTarget target = readTarget(voidfall);
        if (target == null) {
            RandomSource rng = RandomSource.create(player.getUUID().getMostSignificantBits() ^ now);
            target = computeRandomSafeTarget(server, rng);
            if (target == null) {
                return false;
            }
        }

        ServerLevel dest = server.getLevel(target.dimension);
        if (dest == null) {
            // Dimension not available; pick a new one.
            RandomSource rng = RandomSource.create(player.getUUID().getLeastSignificantBits() ^ (now + 1));
            target = computeRandomSafeTarget(server, rng);
            if (target == null) {
                return false;
            }
            dest = server.getLevel(target.dimension);
            if (dest == null) {
                return false;
            }
        }

        // Teleport.
        player.fallDistance = 0;
        player.setDeltaMovement(Vec3.ZERO);

        player.teleportTo(
                dest,
                target.pos.getX() + 0.5,
                target.pos.getY(),
                target.pos.getZ() + 0.5,
                player.getYRot(),
                player.getXRot()
        );

        // Immediately schedule the next destination.
        voidfall.remove(TAG_TARGET);
        voidfall.putLong(TAG_COOLDOWN_UNTIL_TICK, now + 40);
        voidfall.putLong(TAG_LAST_ASSIGN_TICK, now);

        RandomSource rngNext = RandomSource.create(player.getUUID().getLeastSignificantBits() ^ (now + 2));
        VoidfallTarget next = computeRandomSafeTarget(server, rngNext);
        if (next != null) {
            writeTarget(voidfall, next);
        }

        flushVoidfallTag(player, voidfall);
        return true;
    }

    private static VoidfallTarget computeRandomSafeTarget(MinecraftServer server, RandomSource rng) {
        List<ResourceKey<Level>> candidates = new ArrayList<>();
        for (ResourceKey<Level> key : server.levelKeys()) {
            if (key.equals(ModWorldgenKeys.Levels.WAYFALL)) {
                continue;
            }
            if (server.getLevel(key) == null) {
                continue;
            }
            candidates.add(key);
        }

        if (candidates.isEmpty()) {
            return null;
        }

        int dimAttempts = Math.min(40, candidates.size() * 5);
        for (int i = 0; i < dimAttempts; i++) {
            ResourceKey<Level> dimKey = candidates.get(rng.nextInt(candidates.size()));
            ServerLevel level = server.getLevel(dimKey);
            if (level == null) {
                continue;
            }

            BlockPos spawn = level.getSharedSpawnPos();
            int radius = 8000;

            for (int j = 0; j < 20; j++) {
                int x = spawn.getX() + rng.nextInt(radius * 2 + 1) - radius;
                int z = spawn.getZ() + rng.nextInt(radius * 2 + 1) - radius;

                WorldBorder border = level.getWorldBorder();
                if (!border.isWithinBounds(new BlockPos(x, spawn.getY(), z))) {
                    continue;
                }

                BlockPos probe = new BlockPos(x, 0, z);
                BlockPos top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, probe);

                // Heightmap can resolve to the bottom if the column is empty.
                if (top.getY() <= level.getMinBuildHeight() + 1) {
                    continue;
                }

                BlockState topState = level.getBlockState(top);
                if (topState.isAir() || !isSafeLandingBlock(level, top, topState)) {
                    continue;
                }

                BlockPos feet = top.above();
                if (!level.getBlockState(feet).getCollisionShape(level, feet).isEmpty()) {
                    continue;
                }
                if (!level.getBlockState(feet.above()).getCollisionShape(level, feet.above()).isEmpty()) {
                    continue;
                }

                // Clamp to safe build height band.
                int y = Mth.clamp(feet.getY(), level.getMinBuildHeight() + 2, level.getMaxBuildHeight() - 3);
                BlockPos safeFeet = new BlockPos(feet.getX(), y, feet.getZ());

                return new VoidfallTarget(dimKey, safeFeet);
            }
        }

        return null;
    }

    private static boolean isSafeLandingBlock(ServerLevel level, BlockPos pos, BlockState state) {
        // Must be solid enough to stand on.
        if (!state.isFaceSturdy(level, pos, net.minecraft.core.Direction.UP)) {
            return false;
        }

        // Avoid obvious hazards.
        if (state.is(Blocks.MAGMA_BLOCK)
                || state.is(Blocks.CACTUS)
                || state.is(Blocks.CAMPFIRE)
                || state.is(Blocks.SOUL_CAMPFIRE)
                || state.is(Blocks.FIRE)
                || state.is(Blocks.SOUL_FIRE)
                || state.is(Blocks.LAVA)
                || state.is(Blocks.POWDER_SNOW)) {
            return false;
        }

        if (!state.getFluidState().isEmpty()) {
            return false;
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

    private static VoidfallTarget readTarget(CompoundTag voidfall) {
        if (!voidfall.contains(TAG_TARGET, Tag.TAG_COMPOUND)) {
            return null;
        }

        CompoundTag targetTag = voidfall.getCompound(TAG_TARGET);
        String dimStr = targetTag.getString(TAG_TARGET_DIM);
        ResourceLocation dimId = ResourceLocation.tryParse(dimStr);
        if (dimId == null) {
            return null;
        }

        ResourceKey<Level> dimKey = ResourceKey.create(Registries.DIMENSION, dimId);

        int x = targetTag.getInt(TAG_TARGET_X);
        int y = targetTag.getInt(TAG_TARGET_Y);
        int z = targetTag.getInt(TAG_TARGET_Z);

        return new VoidfallTarget(dimKey, new BlockPos(x, y, z));
    }

    private static void writeTarget(CompoundTag voidfall, VoidfallTarget target) {
        CompoundTag targetTag = new CompoundTag();
        targetTag.putString(TAG_TARGET_DIM, target.dimension.location().toString());
        targetTag.putInt(TAG_TARGET_X, target.pos.getX());
        targetTag.putInt(TAG_TARGET_Y, target.pos.getY());
        targetTag.putInt(TAG_TARGET_Z, target.pos.getZ());
        voidfall.put(TAG_TARGET, targetTag);
    }

    private record VoidfallTarget(ResourceKey<Level> dimension, BlockPos pos) {}
}
