package com.kruemblegard.entity;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.GlowSquid;
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
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.phys.AABB;
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

    private static final String TAG_SPAWN_SCALE = "SpawnScale";
    private static final String TAG_POD_ANCHOR = "PodAnchor";

    private static final double POD_SCAN_RADIUS = 28.0D;
    private static final double POD_MAX_WANDER_RADIUS = 16.0D;
    private static final double POD_RETURN_DISTANCE = 24.0D;

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

    private static final RawAnimation BREATHE_LOOP =
        RawAnimation.begin().thenLoop("animation.driftwhale.breathe");

    private static final RawAnimation THERMAL_LIFT_ONCE =
        RawAnimation.begin().thenPlay("animation.driftwhale.thermal_lift");

    private static final RawAnimation STARTLE_ONCE =
        RawAnimation.begin().thenPlay("animation.driftwhale.startle");

    private static final RawAnimation DEATH_ONCE =
        RawAnimation.begin().thenPlay("animation.driftwhale.death");

    private static final int STARTLE_ANIM_COOLDOWN_TICKS = 10;
    private static final int THERMAL_LIFT_MIN_COOLDOWN_TICKS = 20 * 16;
    private static final int THERMAL_LIFT_MAX_COOLDOWN_TICKS = 20 * 34;
    private static final int THERMAL_LIFT_RECENT_HURT_SUPPRESSION_TICKS = 20 * 5;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int startleAnimCooldownTicks = 0;
    private int thermalLiftCooldownTicks = THERMAL_LIFT_MIN_COOLDOWN_TICKS;
    private int recentHurtSuppressionTicks = 0;
    private boolean playedDeathAnim = false;

    @Nullable
    private BlockPos podAnchor;

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
            .add(Attributes.MOVEMENT_SPEED, 0.18D)
            .add(Attributes.FLYING_SPEED, 0.26D)
            .add(Attributes.FOLLOW_RANGE, 24.0D)
            .add(Attributes.ATTACK_DAMAGE, 6.0D);
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
        this.goalSelector.addGoal(1, new DriftwhalePodCohesionGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15D, true));
        this.goalSelector.addGoal(3, new DriftwhaleDriftGoal(this));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, GlowSquid.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide) {
            if (this.startleAnimCooldownTicks > 0) {
                this.startleAnimCooldownTicks--;
            }

            if (this.recentHurtSuppressionTicks > 0) {
                this.recentHurtSuppressionTicks--;
            }

            if (this.thermalLiftCooldownTicks > 0) {
                this.thermalLiftCooldownTicks--;
            }

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

            if (shouldTriggerThermalLift(delta)) {
                triggerAnim("actionController", "thermal_lift");
                resetThermalLiftCooldown();
            }
        }
    }

    private boolean shouldTriggerThermalLift(Vec3 delta) {
        if (!this.isAlive() || this.isBaby()) {
            return false;
        }

        if (this.getTarget() != null || this.recentHurtSuppressionTicks > 0 || this.startleAnimCooldownTicks > 0) {
            return false;
        }

        if (this.thermalLiftCooldownTicks > 0 || this.onGround()) {
            return false;
        }

        double horizontalSpeedSq = delta.horizontalDistanceSqr();
        return horizontalSpeedSq <= 0.085D && Math.abs(delta.y) <= 0.08D;
    }

    private void resetThermalLiftCooldown() {
        this.thermalLiftCooldownTicks = THERMAL_LIFT_MIN_COOLDOWN_TICKS
            + this.getRandom().nextInt(THERMAL_LIFT_MAX_COOLDOWN_TICKS - THERMAL_LIFT_MIN_COOLDOWN_TICKS + 1);
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

        if (this.podAnchor == null) {
            this.podAnchor = this.blockPosition();
        }

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
        tag.putFloat(TAG_SPAWN_SCALE, getSpawnScale());
        if (this.podAnchor != null) {
            tag.putLong(TAG_POD_ANCHOR, this.podAnchor.asLong());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(TAG_SPAWN_SCALE)) {
            setSpawnScale(tag.getFloat(TAG_SPAWN_SCALE));
        }

        if (tag.contains(TAG_POD_ANCHOR)) {
            this.podAnchor = BlockPos.of(tag.getLong(TAG_POD_ANCHOR));
        } else if (this.podAnchor == null) {
            this.podAnchor = this.blockPosition();
        }
    }

    private Vec3 getPodFocusPoint() {
        BlockPos anchor = this.podAnchor != null ? this.podAnchor : this.blockPosition();
        Vec3 focus = Vec3.atCenterOf(anchor);

        AABB scan = this.getBoundingBox().inflate(POD_SCAN_RADIUS);
        List<DriftwhaleEntity> podmates = this.level().getEntitiesOfClass(
            DriftwhaleEntity.class,
            scan,
            other -> other != this && other.isAlive()
        );

        if (podmates.isEmpty()) {
            return focus;
        }

        // Bias toward staying near the group instead of wandering off.
        double sumX = this.getX();
        double sumY = this.getY();
        double sumZ = this.getZ();
        int count = 1;

        int limit = Math.min(6, podmates.size());
        for (int i = 0; i < limit; i++) {
            DriftwhaleEntity mate = podmates.get(i);
            sumX += mate.getX();
            sumY += mate.getY();
            sumZ += mate.getZ();
            count++;
        }

        return new Vec3(sumX / count, sumY / count, sumZ / count);
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
    public boolean hurt(DamageSource source, float amount) {
        boolean didHurt = super.hurt(source, amount);
        if (didHurt && !this.level().isClientSide && this.isAlive()) {
            this.recentHurtSuppressionTicks = THERMAL_LIFT_RECENT_HURT_SUPPRESSION_TICKS;
            this.thermalLiftCooldownTicks = Math.max(this.thermalLiftCooldownTicks, THERMAL_LIFT_MIN_COOLDOWN_TICKS / 2);

            if (this.startleAnimCooldownTicks <= 0) {
                this.startleAnimCooldownTicks = STARTLE_ANIM_COOLDOWN_TICKS;
                triggerAnim("actionController", "startle");
            }
        }
        return didHurt;
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide && !this.playedDeathAnim) {
            this.playedDeathAnim = true;
            triggerAnim("actionController", "death");
        }
        super.die(source);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "baseController", 4, state -> {
            state.setAnimation(state.isMoving() ? MOVE_LOOP : IDLE_LOOP);
            return PlayState.CONTINUE;
        }));

        controllers.add(new AnimationController<>(this, "breatheController", 0, state -> {
            state.setAnimation(BREATHE_LOOP);
            return PlayState.CONTINUE;
        }));

        controllers.add(new AnimationController<>(this, "actionController", 0, state -> PlayState.STOP)
            .triggerableAnim("thermal_lift", THERMAL_LIFT_ONCE)
            .triggerableAnim("startle", STARTLE_ONCE)
            .triggerableAnim("death", DEATH_ONCE));
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

            Vec3 origin = mob.getPodFocusPoint();
            double dx = origin.x + (random.nextDouble() * (POD_MAX_WANDER_RADIUS * 2.0D) - POD_MAX_WANDER_RADIUS);
            double dz = origin.z + (random.nextDouble() * (POD_MAX_WANDER_RADIUS * 2.0D) - POD_MAX_WANDER_RADIUS);

            double dy = origin.y + (random.nextDouble() * 10.0D - 5.0D);
            dy = Mth.clamp(dy, mob.level().getMinBuildHeight() + 8.0D, mob.level().getMaxBuildHeight() - 8.0D);

            BlockPos candidate = BlockPos.containing(dx, dy, dz);
            if (!mob.level().getBlockState(candidate).getCollisionShape(mob.level(), candidate).isEmpty()) {
                cooldownTicks = 10;
                return;
            }

            if (!mob.level().noCollision(mob, mob.getBoundingBox().move(dx - mob.getX(), dy - mob.getY(), dz - mob.getZ()))) {
                cooldownTicks = 10;
                return;
            }

            mob.getNavigation().moveTo(dx, dy, dz, 1.00D);
            cooldownTicks = 12 + random.nextInt(24);
        }
    }

    private static final class DriftwhalePodCohesionGoal extends Goal {
        private final DriftwhaleEntity mob;
        private int retargetTicks;

        private DriftwhalePodCohesionGoal(DriftwhaleEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (mob.getTarget() != null) {
                return false;
            }

            Vec3 focus = mob.getPodFocusPoint();
            return mob.position().distanceToSqr(focus) > (POD_RETURN_DISTANCE * POD_RETURN_DISTANCE);
        }

        @Override
        public boolean canContinueToUse() {
            if (mob.getTarget() != null) {
                return false;
            }

            Vec3 focus = mob.getPodFocusPoint();
            return mob.position().distanceToSqr(focus) > (POD_MAX_WANDER_RADIUS * POD_MAX_WANDER_RADIUS)
                && mob.getNavigation().isInProgress();
        }

        @Override
        public void start() {
            this.retargetTicks = 0;
            Vec3 focus = mob.getPodFocusPoint();
            mob.getNavigation().moveTo(focus.x, focus.y, focus.z, 1.15D);
        }

        @Override
        public void tick() {
            if (retargetTicks-- > 0) {
                return;
            }

            retargetTicks = 20;
            Vec3 focus = mob.getPodFocusPoint();
            mob.getNavigation().moveTo(focus.x, focus.y, focus.z, 1.15D);
        }
    }
}
