package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.TraderBeetleEntity;
import com.kruemblegard.registry.ModEntities;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * Wayfall behavior:
 * - Wandering Traders can spawn in Wayfall.
 * - Any Wandering Trader that spawns in Wayfall gets a Trader Beetle mount (100%).
 */
@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WayfallWanderingTraderEvents {
    private WayfallWanderingTraderEvents() {}

    private static final String DATA_NAME = "kruemblegard_wayfall_trader_spawner";

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof WanderingTrader trader)) {
            return;
        }

        if (event.getLevel().isClientSide()) {
            return;
        }

        if (!event.getLevel().dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return;
        }

        ensureTraderBeetleMount(trader);
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (!(event.level instanceof ServerLevel level)) {
            return;
        }

        if (!level.dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return;
        }

        WayfallTraderSpawnerData data = level.getDataStorage().computeIfAbsent(
            WayfallTraderSpawnerData::load,
            WayfallTraderSpawnerData::new,
            DATA_NAME
        );

        if (!data.tick(level)) {
            return;
        }

        // Spawn occurred.
        data.setDirty();
    }

    private static void ensureTraderBeetleMount(WanderingTrader trader) {
        if (trader.level().isClientSide) {
            return;
        }

        if (!(trader.level() instanceof ServerLevel level)) {
            return;
        }

        if (trader.isPassenger()) {
            return;
        }

        TraderBeetleEntity beetle = ModEntities.TRADER_BEETLE.get().create(level);
        if (beetle == null) {
            return;
        }

        beetle.moveTo(trader.getX(), trader.getY(), trader.getZ(), trader.getYRot(), trader.getXRot());
        beetle.finalizeSpawn(level, level.getCurrentDifficultyAt(beetle.blockPosition()), MobSpawnType.EVENT, null, null);

        // Keep beetle lifetime aligned with the trader lifetime.
        beetle.setOwnerTrader(trader);
        beetle.setDespawnDelay(TraderBeetleEntity.DEFAULT_DESPAWN_DELAY_TICKS);

        // Trader mounts always spawn with a chest containing some wandering-trader themed loot.
        beetle.ensureTraderChestLoot(trader);

        level.addFreshEntity(beetle);
        trader.startRiding(beetle, true);
    }

    /**
     * Lightweight wandering trader spawner for Wayfall.
     * This is intentionally simple: it tries periodically and uses vanilla-ish timing.
     */
    static final class WayfallTraderSpawnerData extends SavedData {
        private static final String TAG_TICK_DELAY = "TickDelay";
        private static final String TAG_SPAWN_DELAY = "SpawnDelay";
        private static final String TAG_CHANCE = "Chance";

        // Vanilla spawner logic (approx): ticks every 1200, but only attempts a spawn about once per day.
        private int tickDelayTicks = 1200;
        private int spawnDelayTicks = 24000;
        private int chance = 25; // scales up when failing

        WayfallTraderSpawnerData() {}

        static WayfallTraderSpawnerData load(CompoundTag tag) {
            WayfallTraderSpawnerData data = new WayfallTraderSpawnerData();

            if (tag.contains(TAG_TICK_DELAY)) {
                data.tickDelayTicks = Math.max(1, tag.getInt(TAG_TICK_DELAY));
                data.spawnDelayTicks = Math.max(1, tag.getInt(TAG_SPAWN_DELAY));
            } else {
                // Back-compat: older saves stored a single "Delay" which behaved like the tick delay.
                data.tickDelayTicks = Math.max(1, tag.getInt("Delay"));
                data.spawnDelayTicks = 24000;
            }

            data.chance = Math.max(1, tag.getInt(TAG_CHANCE));
            return data;
        }

        @Override
        public CompoundTag save(CompoundTag tag) {
            tag.putInt(TAG_TICK_DELAY, tickDelayTicks);
            tag.putInt(TAG_SPAWN_DELAY, spawnDelayTicks);
            tag.putInt(TAG_CHANCE, chance);
            return tag;
        }

        /** @return true if a trader was spawned */
        boolean tick(ServerLevel level) {
            // Respect gamerule if present.
            if (!level.getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_DO_TRADER_SPAWNING)) {
                return false;
            }

            if (--tickDelayTicks > 0) {
                return false;
            }

            tickDelayTicks = 1200;

            // Vanilla-ish: only attempt a spawn about once per day.
            spawnDelayTicks = Math.max(0, spawnDelayTicks - 1200);
            if (spawnDelayTicks > 0) {
                return false;
            }

            spawnDelayTicks = 24000;

            // Keep at most one wandering trader alive in Wayfall at a time.
            for (ServerPlayer p : level.players()) {
                if (p.isSpectator()) {
                    continue;
                }

                AABB nearby = p.getBoundingBox().inflate(256.0D);
                if (!level.getEntitiesOfClass(WanderingTrader.class, nearby).isEmpty()) {
                    return false;
                }
            }

            if (level.getRandom().nextInt(100) >= chance) {
                chance = Math.min(75, chance + 25);
                return false;
            }

            boolean spawned = trySpawnTrader(level);
            if (spawned) {
                chance = 25;
            } else {
                chance = Math.min(75, chance + 25);
            }

            return spawned;
        }

        private static boolean trySpawnTrader(ServerLevel level) {
            List<ServerPlayer> players = level.players();
            if (players.isEmpty()) {
                return false;
            }

            // Pick any non-spectator player.
            ServerPlayer chosen = null;
            for (int i = 0; i < players.size(); i++) {
                ServerPlayer p = players.get(level.getRandom().nextInt(players.size()));
                if (!p.isSpectator()) {
                    chosen = p;
                    break;
                }
            }

            if (chosen == null) {
                return false;
            }

            // Try a few random spots around the player.
            for (int i = 0; i < 16; i++) {
                int dx = level.getRandom().nextInt(97) - 48;
                int dz = level.getRandom().nextInt(97) - 48;
                int x = Mth.clamp((int) chosen.getX() + dx, -30000000, 30000000);
                int z = Mth.clamp((int) chosen.getZ() + dz, -30000000, 30000000);

                int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
                BlockPos pos = new BlockPos(x, y, z);

                WanderingTrader trader = EntityType.WANDERING_TRADER.create(level);
                if (trader == null) {
                    return false;
                }

                trader.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, level.getRandom().nextFloat() * 360.0F, 0.0F);
                if (!level.noCollision(trader)) {
                    continue;
                }

                trader.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.NATURAL, null, null);

                // Align lifetime with vanilla trader.
                trader.setDespawnDelay(TraderBeetleEntity.DEFAULT_DESPAWN_DELAY_TICKS);

                level.addFreshEntity(trader);

                // Ensure beetle mount.
                ensureTraderBeetleMount(trader);

                return true;
            }

            return false;
        }
    }
}
