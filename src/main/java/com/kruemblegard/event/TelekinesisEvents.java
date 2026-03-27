package com.kruemblegard.event;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.registry.ModEnchantments;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kruemblegard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class TelekinesisEvents {
    private static final String NBT_PROJECTILE_TELEKINESIS = "kruemblegard:telekinesis_projectile";
    private static final int DROP_REDIRECT_WINDOW_TICKS = 5;
    private static final double DROP_REDIRECT_RADIUS = 2.5D;
    private static final Map<UUID, PendingTelekinesisRedirect> PENDING_REDIRECTS = new HashMap<>();

    private TelekinesisEvents() {}

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }

        if (player.isSpectator() || !hasTelekinesis(player.getMainHandItem())) {
            return;
        }

        long now = level.getGameTime();
        cleanupExpired(now);

        AABB aabb = new AABB(event.getPos()).inflate(DROP_REDIRECT_RADIUS, DROP_REDIRECT_RADIUS, DROP_REDIRECT_RADIUS);
        PENDING_REDIRECTS.put(player.getUUID(), new PendingTelekinesisRedirect(level.dimension(), aabb, now + DROP_REDIRECT_WINDOW_TICKS));

        relocateNearbyNewDrops(level, player, aabb);
    }

    @SubscribeEvent
    public static void onItemJoin(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        if (event.getEntity() instanceof Projectile projectile) {
            Entity owner = projectile.getOwner();
            if (owner instanceof Player player && hasTelekinesis(player.getMainHandItem())) {
                projectile.getPersistentData().putBoolean(NBT_PROJECTILE_TELEKINESIS, true);
            }
            return;
        }

        if (!(event.getEntity() instanceof ItemEntity itemEntity)) {
            return;
        }

        long now = level.getGameTime();
        cleanupExpired(now);

        for (Map.Entry<UUID, PendingTelekinesisRedirect> entry : PENDING_REDIRECTS.entrySet()) {
            PendingTelekinesisRedirect redirect = entry.getValue();
            if (!redirect.isActive(level.dimension(), now) || !redirect.aabb.contains(itemEntity.position())) {
                continue;
            }

            ServerPlayer player = level.getServer().getPlayerList().getPlayer(entry.getKey());
            if (player == null || player.level() != level || player.isSpectator()) {
                continue;
            }

            if (absorbItemEntity(itemEntity, player)) {
                event.setCanceled(true);
            }
            return;
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel)) {
            return;
        }

        Player player = getTelekinesisPlayer(event.getSource().getEntity(), event.getSource().getDirectEntity());
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        Iterator<ItemEntity> iterator = event.getDrops().iterator();
        while (iterator.hasNext()) {
            ItemEntity drop = iterator.next();
            if (absorbItemEntity(drop, serverPlayer)) {
                iterator.remove();
            }
        }
    }

    @SubscribeEvent
    public static void onLivingExperienceDrop(LivingExperienceDropEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel level)) {
            return;
        }

        if (event.getDroppedExperience() <= 0) {
            return;
        }

        Entity directEntity = event.getEntity().getLastDamageSource() == null
                ? null
                : event.getEntity().getLastDamageSource().getDirectEntity();
        ServerPlayer player = getTelekinesisProjectilePlayer(directEntity);
        if (player == null) {
            return;
        }

        ExperienceOrb.award(level, player.position(), event.getDroppedExperience());
        event.setDroppedExperience(0);
    }

    private static void relocateNearbyNewDrops(ServerLevel level, ServerPlayer player, AABB aabb) {
        for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, aabb)) {
            if (itemEntity.getAge() > 10) {
                continue;
            }
            absorbItemEntity(itemEntity, player);
        }
    }

    private static boolean absorbItemEntity(ItemEntity itemEntity, ServerPlayer player) {
        int originalCount = itemEntity.getItem().getCount();
        ItemStack remaining = itemEntity.getItem().copy();
        player.getInventory().add(remaining);
        int absorbedCount = originalCount - remaining.getCount();

        if (absorbedCount > 0) {
            playPickupCue(player, absorbedCount);
        }

        if (remaining.isEmpty()) {
            itemEntity.discard();
            return true;
        }

        itemEntity.setItem(remaining);
        itemEntity.setPos(player.getX(), player.getY() + 0.1D, player.getZ());
        itemEntity.setNoPickUpDelay();
        return false;
    }

    private static void playPickupCue(ServerPlayer player, int absorbedCount) {
        float volume = 0.2F;
        float pitch = ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F;
        if (absorbedCount > 1) {
            pitch = Math.max(0.6F, pitch - Math.min(0.5F, absorbedCount * 0.03F));
        }

        player.level().playSound(null, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, volume, pitch);
    }

    private static Player getTelekinesisPlayer(Entity attacker, Entity directEntity) {
        if (!(attacker instanceof Player player)) {
            return getTelekinesisProjectilePlayer(directEntity);
        }
        return hasTelekinesis(player.getMainHandItem()) ? player : null;
    }

    private static ServerPlayer getTelekinesisProjectilePlayer(Entity directEntity) {
        if (!(directEntity instanceof Projectile projectile)) {
            return null;
        }

        if (!projectile.getPersistentData().getBoolean(NBT_PROJECTILE_TELEKINESIS)) {
            return null;
        }

        if (!(projectile.getOwner() instanceof ServerPlayer projectileOwner) || projectileOwner.isSpectator()) {
            return null;
        }

        return projectileOwner;
    }

    private static boolean hasTelekinesis(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.TELEKINESIS.get(), stack) > 0;
    }

    private static void cleanupExpired(long now) {
        Iterator<Map.Entry<UUID, PendingTelekinesisRedirect>> iterator = PENDING_REDIRECTS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, PendingTelekinesisRedirect> entry = iterator.next();
            if (now > entry.getValue().expiresGameTime()) {
                iterator.remove();
            }
        }
    }

    private record PendingTelekinesisRedirect(ResourceKey<Level> dimension, AABB aabb, long expiresGameTime) {
        private boolean isActive(ResourceKey<Level> currentDimension, long now) {
            return this.dimension.equals(currentDimension) && now <= this.expiresGameTime;
        }
    }
}