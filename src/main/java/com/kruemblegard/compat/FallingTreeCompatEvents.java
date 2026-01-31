package com.kruemblegard.compat;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.registry.ModTags;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;

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
public final class FallingTreeCompatEvents {
    private static final String FALLING_TREE_MODID = "fallingtree";

    private static final int MAX_TRUNKS_TO_SCAN = 4096;
    private static final int LEAF_XZ_PADDING = 5;
    private static final int LEAF_Y_PADDING_UP = 10;
    private static final int LEAF_Y_PADDING_DOWN = 1;

    private static final long PENDING_WINDOW_TICKS = 1;
    private static final long DROP_REDIRECT_WINDOW_TICKS = 2;

    private static final Map<UUID, PendingFell> PENDING = new ConcurrentHashMap<>();
    private static final Map<UUID, DropRedirect> REDIRECT = new ConcurrentHashMap<>();

    private FallingTreeCompatEvents() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBlockBreakPre(BlockEvent.BreakEvent event) {
        if (!ModList.get().isLoaded(FALLING_TREE_MODID)) {
            return;
        }

        LevelAccessor level = event.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (!(event.getPlayer() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        PendingFell pending = PendingFell.capture(serverLevel, event.getPos(), event.getState());
        if (pending == null) {
            return;
        }

        PENDING.put(serverPlayer.getUUID(), pending);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockBreakPost(BlockEvent.BreakEvent event) {
        if (!ModList.get().isLoaded(FALLING_TREE_MODID)) {
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
        PendingFell pending = PENDING.get(playerId);
        if (pending == null) {
            return;
        }

        long now = serverLevel.getGameTime();

        REDIRECT.entrySet().removeIf(e -> e.getValue().dimension.equals(serverLevel.dimension()) && now > e.getValue().expiresGameTime);

        if (!pending.matches(serverLevel, now)) {
            PENDING.remove(playerId);
            return;
        }

        int airTrunks = pending.countAirTrunks(serverLevel);
        if (airTrunks < 2) {
            // FallingTree didn't fell anything; do nothing.
            PENDING.remove(playerId);
            return;
        }

        AABB treeAabb = pending.scanAabb();

        DropRedirect redirect = new DropRedirect(serverLevel.dimension(), treeAabb, now + DROP_REDIRECT_WINDOW_TICKS);
        REDIRECT.put(playerId, redirect);

        relocateNearbyNewDrops(serverLevel, serverPlayer, treeAabb);
        breakLeafLikeIntoPlayer(serverLevel, serverPlayer, treeAabb, pending.type);

        PENDING.remove(playerId);
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!ModList.get().isLoaded(FALLING_TREE_MODID)) {
            return;
        }

        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        Entity entity = event.getEntity();
        if (!(entity instanceof ItemEntity itemEntity)) {
            return;
        }

        if (itemEntity.getAge() > 5) {
            return;
        }

        long now = serverLevel.getGameTime();

        REDIRECT.entrySet().removeIf(e -> e.getValue().dimension.equals(serverLevel.dimension()) && now > e.getValue().expiresGameTime);

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

    private enum PendingType {
        NORMAL_TREE,
        GIANT_MUSHROOM
    }

    private static void breakLeafLikeIntoPlayer(ServerLevel level, ServerPlayer player, AABB aabb, PendingType pendingType) {
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
            if (state.isAir() || state.is(BlockTags.LOGS) || isGiantMushroomStem(level, pos, state)) {
                continue;
            }

            boolean shouldBreak = false;

            if (state.is(BlockTags.LEAVES) || state.is(BlockTags.WART_BLOCKS)) {
                shouldBreak = true;
            }

            if (!shouldBreak && pendingType == PendingType.NORMAL_TREE && state.is(ModTags.Blocks.TREE_HARVESTER_LEAF_LIKE)) {
                shouldBreak = true;
            }

            if (!shouldBreak && pendingType == PendingType.GIANT_MUSHROOM && state.is(ModTags.Blocks.TREE_HARVESTER_MUSHROOM_CAP_SLABS)) {
                shouldBreak = true;
            }

            if (!shouldBreak) {
                continue;
            }

            BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
            List<ItemStack> drops = Block.getDrops(state, level, pos, blockEntity, player, player.getMainHandItem());
            mergeDrops(mergedDrops, drops);

            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }

        spawnDropsAtPlayer(level, player, mergedDrops);
    }

    private static boolean isGiantMushroomStem(ServerLevel level, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof HugeMushroomBlock)) {
            return false;
        }
        return state.getMapColor(level, pos) == MapColor.WOOL;
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
                    break;
                }
            }

            if (!stack.isEmpty()) {
                merged.add(stack.copy());
            }
        }
    }

    private record DropRedirect(ResourceKey<Level> dimension, AABB aabb, long expiresGameTime) {
        boolean isActive(ServerLevel level, long now) {
            return level.dimension().equals(dimension) && now <= expiresGameTime;
        }
    }

    private record PendingFell(ResourceKey<Level> dimension, long gameTime, PendingType type, List<BlockPos> trunks, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        static PendingFell capture(ServerLevel level, BlockPos start, BlockState startState) {
            long now = level.getGameTime();

            PendingType type;
            if (startState.is(BlockTags.LOGS)) {
                type = PendingType.NORMAL_TREE;
            } else if (isGiantMushroomStem(level, start, startState)) {
                type = PendingType.GIANT_MUSHROOM;
            } else {
                return null;
            }

            LongOpenHashSet visited = new LongOpenHashSet();
            ArrayDeque<BlockPos> queue = new ArrayDeque<>();
            queue.add(start);

            List<BlockPos> trunks = new ArrayList<>();

            int minX = start.getX();
            int minY = start.getY();
            int minZ = start.getZ();
            int maxX = start.getX();
            int maxY = start.getY();
            int maxZ = start.getZ();

            while (!queue.isEmpty() && trunks.size() < MAX_TRUNKS_TO_SCAN) {
                BlockPos pos = queue.removeFirst();
                long key = pos.asLong();
                if (!visited.add(key)) {
                    continue;
                }

                if (!level.isLoaded(pos)) {
                    continue;
                }

                BlockState state = level.getBlockState(pos);
                boolean isTrunk = switch (type) {
                    case NORMAL_TREE -> state.is(BlockTags.LOGS);
                    case GIANT_MUSHROOM -> isGiantMushroomStem(level, pos, state);
                };

                if (!isTrunk) {
                    continue;
                }

                trunks.add(pos.immutable());

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

            if (trunks.size() <= 1) {
                return null;
            }

            return new PendingFell(level.dimension(), now, type, trunks, minX, minY, minZ, maxX, maxY, maxZ);
        }

        boolean matches(ServerLevel level, long now) {
            return level.dimension().equals(dimension) && (now - gameTime) <= PENDING_WINDOW_TICKS;
        }

        int countAirTrunks(ServerLevel level) {
            int air = 0;
            for (BlockPos pos : trunks) {
                if (level.getBlockState(pos).isAir()) {
                    air++;
                }
            }
            return air;
        }

        AABB scanAabb() {
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
