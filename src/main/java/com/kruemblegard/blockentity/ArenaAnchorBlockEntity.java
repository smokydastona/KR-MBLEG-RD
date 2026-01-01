package com.kruemblegard.blockentity;

import com.kruemblegard.entity.KruemblegardBossEntity;
import com.kruemblegard.init.ModBlockEntities;
import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.init.ModCriteria;
import com.kruemblegard.registry.ModEntities;
import com.kruemblegard.registry.ModSounds;
import com.kruemblegard.world.arena.ArenaBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.UUID;

public class ArenaAnchorBlockEntity extends BlockEntity {

    private enum State {
        IDLE,
        BUILDING,
        FIGHT,
        CLEANSE
    }

    private static final int DEFAULT_RADIUS = 10;
    private static final int DEFAULT_DEPTH = 4;

    private State state = State.IDLE;
    private BlockPos centerPos;
    private UUID activator;
    private UUID bossUuid;

    private int buildLayerY; // relative Y from -depth .. 0
    private int buildDelayTicks;

    private int cleanseTicks;

    public ArenaAnchorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ARENA_ANCHOR.get(), pos, state);
    }

    public void start(BlockPos centerPos, UUID activator) {
        if (level == null || level.isClientSide) return;
        this.centerPos = centerPos.immutable();
        this.activator = activator;
        this.state = State.BUILDING;
        this.buildLayerY = -DEFAULT_DEPTH;
        this.buildDelayTicks = 0;
        this.bossUuid = null;
        setChanged();

        spawnBoss((ServerLevel) level);
    }

    private void spawnBoss(ServerLevel serverLevel) {
        KruemblegardBossEntity boss = ModEntities.KRUEMBLEGARD.get().create(serverLevel);
        if (boss == null) return;

        int playersInArena = Math.max(1, countPlayersForScaling(serverLevel));

        boss.setPos(centerPos.getX() + 0.5, centerPos.getY() - 3, centerPos.getZ() + 0.5);
        boss.setInvulnerable(true);
        boss.setNoAi(true);
        boss.beginEmergence(centerPos.getY() + 0.05);
        boss.setArenaCenter(centerPos);

        applyMultiplayerScaling(boss, playersInArena);

        serverLevel.addFreshEntity(boss);
        bossUuid = boss.getUUID();
        setChanged();
    }

    private int countPlayersForScaling(ServerLevel level) {
        if (centerPos == null) return 1;

        AABB scan = ArenaBuilder.getArenaBounds(centerPos, DEFAULT_RADIUS).inflate(8);
        double cx = centerPos.getX() + 0.5;
        double cy = centerPos.getY();
        double cz = centerPos.getZ() + 0.5;
        double radius = DEFAULT_RADIUS + 1.0;
        double radiusSq = radius * radius;

        int count = 0;
        for (ServerPlayer sp : level.getEntitiesOfClass(ServerPlayer.class, scan)) {
            if (sp.isSpectator() || !sp.isAlive()) continue;
            if (sp.distanceToSqr(cx, cy, cz) <= radiusSq) {
                count++;
            }
        }

        return Math.max(1, count);
    }

    private static void applyMultiplayerScaling(KruemblegardBossEntity boss, int playersInArena) {
        // Simple, predictable scaling.
        // Health: +50% per extra player
        // Damage: +15% per extra player
        int extra = Math.max(0, playersInArena - 1);

        AttributeInstance maxHealth = boss.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            double base = maxHealth.getBaseValue();
            double scaled = base * (1.0 + 0.50 * extra);
            maxHealth.setBaseValue(scaled);
            boss.setHealth((float) scaled);
        }

        AttributeInstance attackDamage = boss.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null) {
            double base = attackDamage.getBaseValue();
            double scaled = base * (1.0 + 0.15 * extra);
            attackDamage.setBaseValue(scaled);
        }
    }

    public static void serverTick(ServerLevel level, BlockPos pos, BlockState state, ArenaAnchorBlockEntity be) {
        be.tickServer(level);
    }

    private void tickServer(ServerLevel level) {
        if (centerPos == null) return;

        switch (state) {
            case BUILDING -> tickBuilding(level);
            case FIGHT -> tickFight(level);
            case CLEANSE -> tickCleanse(level);
            case IDLE -> {
            }
        }
    }

    private void tickBuilding(ServerLevel level) {
        if (buildDelayTicks > 0) {
            buildDelayTicks--;
            return;
        }

        ArenaBuilder.placeFloorLayer(level, centerPos, DEFAULT_RADIUS, buildLayerY);
        level.sendParticles(
                ParticleTypes.CLOUD,
                centerPos.getX() + 0.5,
                centerPos.getY() + buildLayerY + 0.2,
                centerPos.getZ() + 0.5,
                12,
                1.2, 0.2, 1.2,
                0.01
        );

        buildLayerY++;
        buildDelayTicks = 3;

        if (buildLayerY > 0) {
            ArenaBuilder.placeStandingStones(level, centerPos, DEFAULT_RADIUS);
            state = State.FIGHT;
            setChanged();
        }
    }

    private void tickFight(ServerLevel level) {
        KruemblegardBossEntity boss = getBoss(level);
        if (boss == null || !boss.isAlive()) {
            startCleanse(level);
            return;
        }

        enforceArena(level, boss);
    }

    private void enforceArena(ServerLevel level, KruemblegardBossEntity boss) {
        double cx = centerPos.getX() + 0.5;
        double cy = centerPos.getY();
        double cz = centerPos.getZ() + 0.5;

        double radius = DEFAULT_RADIUS + 1.0;
        double radiusSq = radius * radius;

        AABB scan = ArenaBuilder.getArenaBounds(centerPos, DEFAULT_RADIUS).inflate(16);
        for (Player player : level.getEntitiesOfClass(Player.class, scan)) {
            double distSq = player.distanceToSqr(cx, cy, cz);
            if (distSq <= radiusSq) continue;

            if (player instanceof ServerPlayer sp) {
                sp.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 4, true, true, true));
                sp.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 3, true, true, true));
            }

            boss.heal(1.0F);
        }
    }

    private void startCleanse(ServerLevel level) {
        if (state == State.CLEANSE) return;
        state = State.CLEANSE;
        cleanseTicks = 40; // ~2 seconds
        setChanged();

        level.playSound(null, centerPos, ModSounds.KRUEMBLEGARD_RADIANT.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

        forEachArenaPlayer(level, sp -> ModCriteria.KRUEMBLEGARD_SURVIVED.trigger(sp));
    }

    private void tickCleanse(ServerLevel level) {
        if (cleanseTicks-- > 0) {
            if (cleanseTicks % 5 == 0) {
                level.sendParticles(
                        ParticleTypes.END_ROD,
                        centerPos.getX() + 0.5,
                        centerPos.getY() + 1.0,
                        centerPos.getZ() + 0.5,
                        8,
                        1.2, 0.8, 1.2,
                        0.01
                );
            }
            return;
        }

        level.setBlock(centerPos, ModBlocks.ANCIENT_WAYSTONE.get().defaultBlockState(), 3);
        level.removeBlock(worldPosition, false);

        forEachArenaPlayer(level, sp -> ModCriteria.KRUEMBLEGARD_CLEANSED.trigger(sp));
    }

    private void forEachArenaPlayer(ServerLevel level, java.util.function.Consumer<ServerPlayer> action) {
        if (centerPos == null) return;
        AABB scan = ArenaBuilder.getArenaBounds(centerPos, DEFAULT_RADIUS).inflate(16);
        double cx = centerPos.getX() + 0.5;
        double cy = centerPos.getY();
        double cz = centerPos.getZ() + 0.5;
        double radius = DEFAULT_RADIUS + 1.0;
        double radiusSq = radius * radius;

        for (ServerPlayer sp : level.getEntitiesOfClass(ServerPlayer.class, scan)) {
            if (sp.isSpectator() || !sp.isAlive()) continue;
            if (sp.distanceToSqr(cx, cy, cz) <= radiusSq) {
                action.accept(sp);
            }
        }
    }

    private KruemblegardBossEntity getBoss(ServerLevel level) {
        if (bossUuid == null) return null;
        Entity entity = level.getEntity(bossUuid);
        return entity instanceof KruemblegardBossEntity boss ? boss : null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("State", state.name());
        if (centerPos != null) {
            tag.putInt("CenterX", centerPos.getX());
            tag.putInt("CenterY", centerPos.getY());
            tag.putInt("CenterZ", centerPos.getZ());
        }
        if (activator != null) tag.putUUID("Activator", activator);
        if (bossUuid != null) tag.putUUID("Boss", bossUuid);
        tag.putInt("BuildLayerY", buildLayerY);
        tag.putInt("BuildDelay", buildDelayTicks);
        tag.putInt("CleanseTicks", cleanseTicks);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        try {
            state = State.valueOf(tag.getString("State"));
        } catch (Exception ignored) {
            state = State.IDLE;
        }

        if (tag.contains("CenterX")) {
            centerPos = new BlockPos(tag.getInt("CenterX"), tag.getInt("CenterY"), tag.getInt("CenterZ"));
        }
        activator = tag.hasUUID("Activator") ? tag.getUUID("Activator") : null;
        bossUuid = tag.hasUUID("Boss") ? tag.getUUID("Boss") : null;
        buildLayerY = tag.getInt("BuildLayerY");
        buildDelayTicks = tag.getInt("BuildDelay");
        cleanseTicks = tag.getInt("CleanseTicks");
    }
}
