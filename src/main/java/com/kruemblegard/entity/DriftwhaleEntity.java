package com.kruemblegard.entity;

import java.util.EnumSet;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Driftwhale: a slow, peaceful sky-swimmer.
 *
 * Design notes:
 * - Uses a lightweight flying navigation + random drift goal.
 * - Spawns with ground support (for safety) but lifts into open air immediately.
 */
public class DriftwhaleEntity extends PathfinderMob implements GeoEntity {

    public static final float MIN_SPAWN_SCALE = 1.0F;
    public static final float MAX_SPAWN_SCALE = 2.5F;

    public static final float MIN_BABY_SCALE = 0.8F;
    public static final float MAX_BABY_SCALE = 1.0F;

    private static final EntityDataAccessor<Float> SPAWN_SCALE =
        SynchedEntityData.defineId(DriftwhaleEntity.class, EntityDataSerializers.FLOAT);

    private static final RawAnimation IDLE_LOOP =
        RawAnimation.begin().thenLoop("animation.driftwhale.idle");

    private static final RawAnimation MOVE_LOOP =
        RawAnimation.begin().thenLoop("animation.driftwhale.move");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public DriftwhaleEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 16, true);
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SPAWN_SCALE, MIN_SPAWN_SCALE);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (SPAWN_SCALE.equals(key)) {
            this.refreshDimensions();
        }
    }

    public float getSpawnScale() {
        float raw = this.entityData.get(SPAWN_SCALE);
        if (this.isBaby()) {
            return Mth.clamp(raw, MIN_BABY_SCALE, MAX_BABY_SCALE);
        }
        return Mth.clamp(raw, MIN_SPAWN_SCALE, MAX_SPAWN_SCALE);
    }

    private void setSpawnScale(float scale) {
        // Store raw value; clamping is applied contextually in getSpawnScale() based on baby/adult.
        this.entityData.set(SPAWN_SCALE, scale);
        this.refreshDimensions();
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return super.getDimensions(pose).scale(getSpawnScale());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.16D)
            .add(Attributes.FLYING_SPEED, 0.20D)
            .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation nav = new FlyingPathNavigation(this, level);
        nav.setCanOpenDoors(false);
        nav.setCanFloat(true);
        return nav;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new DriftwhaleDriftGoal(this));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide) {
            Vec3 delta = this.getDeltaMovement();

            // If a Driftwhale ever ends up grounded (spawned/teleported onto solid blocks),
            // nudge it back into the air so it doesn't get stuck sliding along terrain.
            if (this.onGround() && delta.y <= 0.02D) {
                this.setDeltaMovement(delta.x, 0.10D, delta.z);
                this.hasImpulse = true;
                delta = this.getDeltaMovement();
            }

            if (delta.y < -0.06D) {
                this.setDeltaMovement(delta.x, -0.06D, delta.z);
            }
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(
        ServerLevelAccessor level,
        DifficultyInstance difficulty,
        MobSpawnType spawnType,
        @Nullable SpawnGroupData spawnGroupData,
        @Nullable net.minecraft.nbt.CompoundTag dataTag
    ) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData, dataTag);

        // Natural Driftwhales vary in size (never smaller than the base size).
        // Synced to clients + saved to NBT so it persists across reloads.
        float minScale = this.isBaby() ? MIN_BABY_SCALE : MIN_SPAWN_SCALE;
        float maxScale = this.isBaby() ? MAX_BABY_SCALE : MAX_SPAWN_SCALE;
        float scale = Mth.lerp(this.getRandom().nextFloat(), minScale, maxScale);
        setSpawnScale(scale);

        // Natural spawns are chosen from a grounded position for safety; lift into open air
        // so the mob behaves like a sky-swimmer immediately.
        if (spawnType == MobSpawnType.NATURAL || spawnType == MobSpawnType.CHUNK_GENERATION) {
            liftIntoAir(level);
        }

        return data;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("SpawnScale", getSpawnScale());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("SpawnScale")) {
            setSpawnScale(tag.getFloat("SpawnScale"));
        }
    }

    private void liftIntoAir(ServerLevelAccessor level) {
        RandomSource random = this.getRandom();
        BlockPos base = this.blockPosition();

        int minLift = 6;
        int maxLift = 14;
        int attempts = 10;

        int minY = level.getLevel().getMinBuildHeight() + 2;
        int maxY = level.getLevel().getMaxBuildHeight() - 3;

        for (int i = 0; i < attempts; i++) {
            int lift = minLift + random.nextInt(maxLift - minLift + 1);
            BlockPos candidate = new BlockPos(base.getX(), Mth.clamp(base.getY() + lift, minY, maxY), base.getZ());

            if (!level.getBlockState(candidate).getCollisionShape(level, candidate).isEmpty()) {
                continue;
            }

            if (!level.getBlockState(candidate.above()).getCollisionShape(level, candidate.above()).isEmpty()) {
                continue;
            }

            // Leave a little buffer below so we don't immediately collide with the ground.
            this.moveTo(this.getX(), candidate.getY() + 0.15D, this.getZ(), this.getYRot(), this.getXRot());
            Vec3 delta = this.getDeltaMovement();
            if (delta.y < 0.06D) {
                this.setDeltaMovement(delta.x, 0.08D, delta.z);
            }
            this.hasImpulse = true;
            return;
        }
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, net.minecraft.world.damagesource.DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, net.minecraft.world.level.block.state.BlockState state, BlockPos pos) {
        // no-op
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "baseController", 0, state -> {
            state.setAnimation(state.isMoving() ? MOVE_LOOP : IDLE_LOOP);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private static final class DriftwhaleDriftGoal extends Goal {
        private final DriftwhaleEntity mob;
        private int cooldownTicks = 0;

        private DriftwhaleDriftGoal(DriftwhaleEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (mob.getTarget() != null) {
                return false;
            }

            if (cooldownTicks > 0) {
                cooldownTicks--;
                return false;
            }

            return !mob.getNavigation().isInProgress();
        }

        @Override
        public void start() {
            RandomSource random = mob.getRandom();

            Vec3 origin = mob.position();
            double dx = origin.x + (random.nextDouble() * 24.0D - 12.0D);
            double dz = origin.z + (random.nextDouble() * 24.0D - 12.0D);

            double dy = origin.y + (random.nextDouble() * 10.0D - 5.0D);
            dy = Mth.clamp(dy, mob.level().getMinBuildHeight() + 8.0D, mob.level().getMaxBuildHeight() - 8.0D);

            BlockPos candidate = BlockPos.containing(dx, dy, dz);
            if (!mob.level().getBlockState(candidate).getCollisionShape(mob.level(), candidate).isEmpty()) {
                cooldownTicks = 20;
                return;
            }

            mob.getNavigation().moveTo(dx, dy, dz, 0.8D);
            cooldownTicks = 30 + random.nextInt(40);
        }
    }
}
