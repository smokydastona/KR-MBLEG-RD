package com.smoky.krumblegard.entity.boss;

import com.smoky.krumblegard.KrumblegardMod;
import com.smoky.krumblegard.init.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.minecraft.world.BossEvent;
import net.minecraft.server.level.ServerBossEvent;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class KrumblegardEntity extends Monster implements GeoEntity {

    public enum Phase {
        SENTINEL,
        AWAKENED,
        BROKEN
    }

    public enum AttackState {
        NONE,
        SMASH,
        GROUND_SLAM,
        RUNE_BARRAGE
    }

    private static final ResourceLocation LOOT_TABLE = new ResourceLocation(KrumblegardMod.MODID, "entities/krumblegard");

        private static final EntityDataAccessor<Boolean> DATA_ENGAGED =
            SynchedEntityData.defineId(KrumblegardEntity.class, EntityDataSerializers.BOOLEAN);

    private Phase phase = Phase.SENTINEL;

    private boolean emerging;
    private double emergeTargetY;

    private boolean engaged;
    private final ServerBossEvent bossEvent;

    private AttackState attackState = AttackState.NONE;
    private int attackTicks;
    private int attackCooldownTicks;

    private BlockPos arenaCenter;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public KrumblegardEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 500;

        this.bossEvent = new ServerBossEvent(
                Component.literal("Krümblegård"),
                BossEvent.BossBarColor.PURPLE,
                BossEvent.BossBarOverlay.NOTCHED_10
        );
        // We handle custom music client-side via MusicManager; don't enable vanilla boss-music (dragon) here.
        this.bossEvent.setPlayBossMusic(false);
        this.bossEvent.setCreateWorldFog(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 500.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.20D)
                .add(Attributes.ARMOR, 20.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 18.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 12.0F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        targetSelector.addGoal(2, new HurtByTargetGoal(this));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public void beginEmergence(double targetY) {
        emerging = true;
        emergeTargetY = targetY;
    }

    public void setArenaCenter(BlockPos pos) {
        arenaCenter = pos == null ? null : pos.immutable();
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            bossEvent.setProgress(getHealth() / getMaxHealth());

            // Engage once the boss actually has a target and is out of the emergence sequence.
            if (!engaged && !emerging && getTarget() != null && getTarget().isAlive()) {
                engaged = true;
                entityData.set(DATA_ENGAGED, true);
            }
        }

        if (level().isClientSide) {
            return;
        }

        updatePhase();

        if (emerging) {
            tickEmergence();
            return;
        }

        tickCombat();
    }

    public boolean isEngaged() {
        return entityData.get(DATA_ENGAGED);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_ENGAGED, false);
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossEvent.removePlayer(player);
    }

    private void updatePhase() {
        float healthPct = getHealth() / getMaxHealth();

        if (healthPct <= 0.35f) {
            phase = Phase.BROKEN;
        } else if (healthPct <= 0.70f) {
            phase = Phase.AWAKENED;
        } else {
            phase = Phase.SENTINEL;
        }
    }

    private void tickEmergence() {
        setDeltaMovement(0, 0.05, 0);
        move(MoverType.SELF, getDeltaMovement());

        if (getY() >= emergeTargetY) {
            emerging = false;
            setDeltaMovement(Vec3.ZERO);
            setNoAi(false);
            setInvulnerable(false);
        }
    }

    private void tickCombat() {
        LivingEntity target = getTarget();

        if (attackCooldownTicks > 0) {
            attackCooldownTicks--;
        }

        if (attackState != AttackState.NONE) {
            attackTicks++;
            switch (attackState) {
                case SMASH -> handleSmash(target);
                case GROUND_SLAM -> handleGroundSlam();
                case RUNE_BARRAGE -> handleRuneBarrage(target);
            }
            return;
        }

        if (attackCooldownTicks > 0) return;
        if (target == null || !target.isAlive()) return;

        double d = distanceTo(target);

        AttackState chosen;
        if (d > 7.0) {
            chosen = AttackState.RUNE_BARRAGE;
        } else if (d < 3.0) {
            chosen = AttackState.GROUND_SLAM;
        } else {
            chosen = AttackState.SMASH;
        }

        startAttack(chosen);
    }

    private void startAttack(AttackState next) {
        attackState = next;
        attackTicks = 0;

        switch (next) {
            case SMASH -> triggerAnim("combat", "attack_smash");
            case GROUND_SLAM -> triggerAnim("combat", "attack_ground_slam");
            case RUNE_BARRAGE -> triggerAnim("combat", "attack_rune_barrage");
        }

        // Sounds are played on the impact frames inside the handlers.
    }

    private void resetAttack() {
        attackState = AttackState.NONE;
        attackTicks = 0;

        int base = switch (phase) {
            case SENTINEL -> 55;
            case AWAKENED -> 45;
            case BROKEN -> 35;
        };

        attackCooldownTicks = base + random.nextInt(20);
    }

    private void handleSmash(LivingEntity target) {
        if (attackTicks == 1) {
            level().playSound(null, blockPosition(), ModSounds.KRUMBLEGARD_ATTACK_SMASH.get(), SoundSource.HOSTILE, 1.5F, 1.0F);
        }

        if (attackTicks == 15 && target != null && target.isAlive()) {
            if (distanceTo(target) < 4.5) {
                target.hurt(damageSources().mobAttack(this), 22.0F);
                target.knockback(1.2F, getX() - target.getX(), getZ() - target.getZ());
            }
        }

        if (attackTicks > 32) {
            resetAttack();
        }
    }

    private void handleGroundSlam() {
        if (attackTicks == 1) {
            level().playSound(null, blockPosition(), ModSounds.KRUMBLEGARD_ATTACK_SLAM.get(), SoundSource.HOSTILE, 1.7F, 1.0F);
        }

        if (attackTicks == 20) {
            AABB area = getBoundingBox().inflate(6.0);
            for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, area, e -> e != this)) {
                e.hurt(damageSources().mobAttack(this), 16.0F);
                e.setDeltaMovement(e.getDeltaMovement().add(0, 0.55, 0));
            }
        }

        if (attackTicks > 42) {
            resetAttack();
        }
    }

    private void handleRuneBarrage(LivingEntity target) {
        if (attackTicks == 1) {
            level().playSound(null, blockPosition(), ModSounds.KRUMBLEGARD_ATTACK_RUNE.get(), SoundSource.HOSTILE, 1.2F, 1.0F);
        }

        // Template hook: replace with real projectiles.
        if (attackTicks == 18 && target != null && target.isAlive()) {
            if (distanceTo(target) < 12.0) {
                target.hurt(damageSources().mobAttack(this), 10.0F);
            }
        }

        if (attackTicks > 35) {
            resetAttack();
        }
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        return LOOT_TABLE;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        bossEvent.removeAllPlayers();
        // The ArenaAnchorBlockEntity handles cleansing + Ancient Waystone placement.
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        bossEvent.removeAllPlayers();
        super.remove(reason);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Emerging", emerging);
        tag.putDouble("EmergeTargetY", emergeTargetY);
        tag.putString("Phase", phase.name());
        tag.putBoolean("Engaged", engaged);
        if (arenaCenter != null) {
            tag.putInt("ArenaCenterX", arenaCenter.getX());
            tag.putInt("ArenaCenterY", arenaCenter.getY());
            tag.putInt("ArenaCenterZ", arenaCenter.getZ());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        emerging = tag.getBoolean("Emerging");
        emergeTargetY = tag.getDouble("EmergeTargetY");
        engaged = tag.getBoolean("Engaged");
        entityData.set(DATA_ENGAGED, engaged);
        try {
            phase = Phase.valueOf(tag.getString("Phase"));
        } catch (Exception ignored) {
            phase = Phase.SENTINEL;
        }
        if (tag.contains("ArenaCenterX")) {
            arenaCenter = new BlockPos(tag.getInt("ArenaCenterX"), tag.getInt("ArenaCenterY"), tag.getInt("ArenaCenterZ"));
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 5, this::movementController));

        controllers.add(new AnimationController<KrumblegardEntity>(this, "combat", 0, this::combatController)
                .triggerableAnim("attack_smash", RawAnimation.begin().thenPlay("animation.krumblegard.attack_smash"))
                .triggerableAnim("attack_ground_slam", RawAnimation.begin().thenPlay("animation.krumblegard.attack_ground_slam"))
                .triggerableAnim("attack_rune_barrage", RawAnimation.begin().thenPlay("animation.krumblegard.attack_rune_barrage"))
        );

        controllers.add(new AnimationController<>(this, "runes", 0, state -> {
            state.setAnimation(RawAnimation.begin().thenLoop("animation.krumblegard.runes_idle"));
            return PlayState.CONTINUE;
        }));
        controllers.add(new AnimationController<>(this, "core", 0, state -> {
            state.setAnimation(RawAnimation.begin().thenLoop("animation.krumblegard.core_pulse"));
            return PlayState.CONTINUE;
        }));
    }

    private PlayState movementController(AnimationState<KrumblegardEntity> state) {
        if (emerging) {
            state.setAnimation(RawAnimation.begin().thenLoop("animation.krumblegard.emerge"));
            return PlayState.CONTINUE;
        }

        if (state.isMoving()) {
            state.setAnimation(RawAnimation.begin().thenLoop("animation.krumblegard.walk"));
        } else {
            state.setAnimation(RawAnimation.begin().thenLoop("animation.krumblegard.idle"));
        }
        return PlayState.CONTINUE;
    }

    private PlayState combatController(AnimationState<KrumblegardEntity> state) {
        return PlayState.STOP;
    }
}
