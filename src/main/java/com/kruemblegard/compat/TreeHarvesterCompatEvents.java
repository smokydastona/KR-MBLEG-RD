package com.kruemblegard.compat;

import com.kruemblegard.Kruemblegard;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import com.kruemblegard.registry.ModTags;

import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class TreeHarvesterCompatEvents {
    private static final String TREE_HARVESTER_MODID = "treeharvester";

    private static final int MAX_LOGS_TO_SCAN = 4096;
    private static final int LEAF_XZ_PADDING = 5;
    private static final int LEAF_Y_PADDING_UP = 6;
    private static final int LEAF_Y_PADDING_DOWN = 1;

    private static final long PENDING_WINDOW_TICKS = 1;
    private static final long DROP_REDIRECT_WINDOW_TICKS = 2;

    private static final Map<UUID, PendingTree> PENDING = new ConcurrentHashMap<>();
    private static final Map<UUID, DropRedirect> REDIRECT = new ConcurrentHashMap<>();

    private TreeHarvesterCompatEvents() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBlockBreakPre(BlockEvent.BreakEvent event) {
        if (!ModList.get().isLoaded(TREE_HARVESTER_MODID)) {
            return;
        }

        LevelAccessor level = event.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (!(event.getPlayer() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        BlockState state = event.getState();
        if (!state.is(BlockTags.LOGS)) {
            return;
        }

        PendingTree pendingTree = PendingTree.capture(serverLevel, event.getPos());
        if (pendingTree == null) {
            return;
        }

        PENDING.put(serverPlayer.getUUID(), pendingTree);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockBreakPost(BlockEvent.BreakEvent event) {
        if (!ModList.get().isLoaded(TREE_HARVESTER_MODID)) {
            return;
        }

        LevelAccessor level = event.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (!(event.getPlayer() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        UUID playerId = serverPlayer.getUUID();
        PendingTree pendingTree = PENDING.get(playerId);
        if (pendingTree == null) {
            return;
        }

        long now = serverLevel.getGameTime();

        // Best-effort cleanup so this map can’t grow forever.
        REDIRECT.entrySet().removeIf(e -> e.getValue().dimension.equals(serverLevel.dimension()) && now > e.getValue().expiresGameTime);

        if (!pendingTree.matches(serverLevel, now)) {
            PENDING.remove(playerId);
            return;
        }

        int airLogs = pendingTree.countAirLogs(serverLevel);
        if (airLogs < 2) {
            // If Tree Harvester didn’t trigger, do nothing.
            PENDING.remove(playerId);
            return;
        }

        // Tree Harvester likely ran and removed multiple logs.
        AABB treeAabb = pendingTree.leafScanAabb();

        DropRedirect redirect = new DropRedirect(serverLevel.dimension(), treeAabb, now + DROP_REDIRECT_WINDOW_TICKS);
        REDIRECT.put(playerId, redirect);

        // Relocate any just-spawned drops (Tree Harvester drops logs immediately).
        relocateNearbyNewDrops(serverLevel, serverPlayer, treeAabb);

        // Instantly remove leaves + franches (anything in minecraft:leaves but not logs)
        // and spawn their drops at the player’s feet.
        breakLeavesIntoPlayer(serverLevel, serverPlayer, treeAabb);

        PENDING.remove(playerId);
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!ModList.get().isLoaded(TREE_HARVESTER_MODID)) {
            return;
        }

        Level level = event.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        Entity entity = event.getEntity();
        if (!(entity instanceof ItemEntity itemEntity)) {
            return;
        }

        // Only redirect very new drops.
        if (itemEntity.getAge() > 5) {
            return;
        }

        long now = serverLevel.getGameTime();

        // Best-effort cleanup so this map can’t grow forever.
        REDIRECT.entrySet().removeIf(e -> e.getValue().dimension.equals(serverLevel.dimension()) && now > e.getValue().expiresGameTime);

        // Iterate active redirects; list is expected to be tiny.
        for (Map.Entry<UUID, DropRedirect> entry : REDIRECT.entrySet()) {
            DropRedirect redirect = entry.getValue();
            if (!redirect.isActive(serverLevel, now)) {
                continue;
            }

            ServerPlayer player = serverLevel.getServer().getPlayerList().getPlayer(entry.getKey());
            if (player == null || player.level() != serverLevel) {
                continue;
            }

            if (player.distanceToSqr(itemEntity) > (128.0 * 128.0)) {
                continue;
            }

            if (!redirect.aabb.contains(itemEntity.position())) {
                continue;
            }

            moveDropToPlayer(itemEntity, player);
            return;
        }
    }

    private static void relocateNearbyNewDrops(ServerLevel level, ServerPlayer player, AABB aabb) {
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, aabb);
        for (ItemEntity item : items) {
            if (item.getAge() > 10) {
                continue;
            }

            moveDropToPlayer(item, player);
        }
    }

    private static void moveDropToPlayer(ItemEntity itemEntity, ServerPlayer player) {
        itemEntity.setPos(player.getX(), player.getY() + 0.1, player.getZ());
    }

    private static void breakLeavesIntoPlayer(ServerLevel level, ServerPlayer player, AABB aabb) {
        int minX = (int) Math.floor(aabb.minX);
        int minY = (int) Math.floor(aabb.minY);
        int minZ = (int) Math.floor(aabb.minZ);
        int maxX = (int) Math.floor(aabb.maxX);
        int maxY = (int) Math.floor(aabb.maxY);
        int maxZ = (int) Math.floor(aabb.maxZ);

        List<ItemStack> mergedDrops = new ArrayList<>();

        for (BlockPos pos : BlockPos.betweenClosed(minX, minY, minZ, maxX, maxY, maxZ)) {
            if (!level.isLoaded(pos)) {
                continue;
            }

            BlockState state = level.getBlockState(pos);
            if (state.isAir() || state.is(BlockTags.LOGS)) {
                continue;
            }

            if (!state.is(BlockTags.LEAVES)) {
                continue;
            }

            BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
            List<ItemStack> drops = Block.getDrops(state, level, pos, blockEntity, player, player.getMainHandItem());
            mergeDrops(mergedDrops, drops);

            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }

        spawnDropsAtPlayer(level, player, mergedDrops);
    }

    private static void spawnDropsAtPlayer(ServerLevel level, ServerPlayer player, List<ItemStack> drops) {
        if (drops.isEmpty()) {
            return;
        }

        drops.sort(Comparator.comparingInt(ItemStack::getCount).reversed());

        for (ItemStack stack : drops) {
            if (stack.isEmpty()) {
                continue;
            }

            ItemEntity entity = new ItemEntity(level, player.getX(), player.getY() + 0.1, player.getZ(), stack);
            entity.setDefaultPickUpDelay();
            level.addFreshEntity(entity);
        }
    }

    private static void mergeDrops(List<ItemStack> merged, List<ItemStack> incoming) {
        for (ItemStack stack : incoming) {
            if (stack.isEmpty()) {
                continue;
            }

            boolean mergedIntoExisting = false;
            for (ItemStack existing : merged) {
                if (!ItemStack.isSameItemSameTags(existing, stack)) {
                    continue;
                }

                int space = existing.getMaxStackSize() - existing.getCount();
                if (space <= 0) {
                    continue;
                }

                int toMove = Math.min(space, stack.getCount());
                existing.grow(toMove);
                stack.shrink(toMove);
                if (stack.isEmpty()) {
                    mergedIntoExisting = true;
                    break;
                }
            }

            if (!stack.isEmpty()) {
                merged.add(stack.copy());
            } else if (!mergedIntoExisting) {
                // no-op
            }
        }
    }

    private record DropRedirect(ResourceKey<Level> dimension, AABB aabb, long expiresGameTime) {
        boolean isActive(ServerLevel level, long now) {
            return level.dimension().equals(dimension) && now <= expiresGameTime;
        }
    }

    private record PendingTree(ResourceKey<Level> dimension, long gameTime, List<BlockPos> logs, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        static PendingTree capture(ServerLevel level, BlockPos start) {
            long now = level.getGameTime();

            LongOpenHashSet visited = new LongOpenHashSet();
            ArrayDeque<BlockPos> queue = new ArrayDeque<>();
            queue.add(start);

            List<BlockPos> logs = new ArrayList<>();

            int minX = start.getX();
            int minY = start.getY();
            int minZ = start.getZ();
            int maxX = start.getX();
            int maxY = start.getY();
            int maxZ = start.getZ();

            while (!queue.isEmpty() && logs.size() < MAX_LOGS_TO_SCAN) {
                BlockPos pos = queue.removeFirst();
                long key = pos.asLong();
                if (!visited.add(key)) {
                    continue;
                }

                if (!level.isLoaded(pos)) {
                    continue;
                }

                BlockState state = level.getBlockState(pos);
                if (!state.is(BlockTags.LOGS)) {
                    continue;
                }

                logs.add(pos.immutable());

                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();

                if (x < minX) minX = x;
                if (y < minY) minY = y;
                if (z < minZ) minZ = z;
                if (x > maxX) maxX = x;
                if (y > maxY) maxY = y;
                if (z > maxZ) maxZ = z;

                for (Direction dir : Direction.values()) {
                    queue.addLast(pos.relative(dir));
                }
            }

            if (logs.size() <= 1) {
                // Avoid treating lone logs as trees.
                return null;
            }

            return new PendingTree(level.dimension(), now, logs, minX, minY, minZ, maxX, maxY, maxZ);
        }

        boolean matches(ServerLevel level, long now) {
            return level.dimension().equals(dimension) && (now - gameTime) <= PENDING_WINDOW_TICKS;
        }

        int countAirLogs(ServerLevel level) {
            int air = 0;
            for (BlockPos pos : logs) {
                if (level.getBlockState(pos).isAir()) {
                    air++;
                }
            }
            return air;
        }

        AABB leafScanAabb() {
            int sx0 = minX - LEAF_XZ_PADDING;
            int sy0 = minY - LEAF_Y_PADDING_DOWN;
            int sz0 = minZ - LEAF_XZ_PADDING;

            int sx1 = maxX + LEAF_XZ_PADDING;
            int sy1 = maxY + LEAF_Y_PADDING_UP;
            int sz1 = maxZ + LEAF_XZ_PADDING;

            return new AABB(sx0, sy0, sz0, sx1 + 1, sy1 + 1, sz1 + 1);
        }
    }
}
