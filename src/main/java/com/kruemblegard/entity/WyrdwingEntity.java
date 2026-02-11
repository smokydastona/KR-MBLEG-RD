package com.kruemblegard.entity;

import java.util.EnumSet;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WyrdwingEntity extends PathfinderMob implements GeoEntity {

    private static final EntityDataAccessor<Integer> ATTACK_ANIM_TICKS =
        SynchedEntityData.defineId(WyrdwingEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> VOID_RECOVER_TICKS =
        SynchedEntityData.defineId(WyrdwingEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> GLIDE_START_TICKS =
        SynchedEntityData.defineId(WyrdwingEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> FLEE_TICKS =
        SynchedEntityData.defineId(WyrdwingEntity.class, EntityDataSerializers.INT);

    private static final RawAnimation IDLE_LOOP =
            RawAnimation.begin().thenLoop("animation.wyrdwing.idle");

    private static final RawAnimation WALK_LOOP =
            RawAnimation.begin().thenLoop("animation.wyrdwing.walk");

        private static final RawAnimation GLIDE_START_ONCE =
            RawAnimation.begin().thenPlay("animation.wyrdwing.glide_start");

        private static final RawAnimation GLIDE_LOOP =
            RawAnimation.begin().thenLoop("animation.wyrdwing.glide_loop");

        private static final RawAnimation FLAP_LOOP =
            RawAnimation.begin().thenLoop("animation.wyrdwing.flight_flap");

        private static final RawAnimation HOVER_LOOP =
            RawAnimation.begin().thenLoop("animation.wyrdwing.flight_hover");

        private static final RawAnimation VOID_RECOVER_ONCE =
            RawAnimation.begin().thenPlay("animation.wyrdwing.void_recover");

        private static final RawAnimation ATTACK_ONCE =
            RawAnimation.begin().thenPlay("animation.wyrdwing.attack");

        private static final double GLIDE_MAX_FALL_SPEED = -0.12D;
        private static final double GLIDE_MIN_FALL_SPEED = -0.035D;

        private static final double FLAP_ASCEND_BOOST = 0.065D;
        private static final double FLAP_FORWARD_BOOST = 0.035D;

        private static final double HOVER_DAMPING = 0.80D;

        private static final float LOW_HEALTH_FLEE_THRESHOLD = 0.30F;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

        private boolean wasOnGroundLastTick = true;
        private BlockPos perchTarget = null;
        private int perchRepathCooldown = 0;

    public WyrdwingEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setMaxUpStep(1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 18.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.ATTACK_DAMAGE, 3.5D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_ANIM_TICKS, 0);
        this.entityData.define(VOID_RECOVER_TICKS, 0);
        this.entityData.define(GLIDE_START_TICKS, 0);
        this.entityData.define(FLEE_TICKS, 0);
    }

    private int getAttackAnimTicks() {
        return this.entityData.get(ATTACK_ANIM_TICKS);
    }

    private void setAttackAnimTicks(int ticks) {
        this.entityData.set(ATTACK_ANIM_TICKS, ticks);
    }

    private int getVoidRecoverTicks() {
        return this.entityData.get(VOID_RECOVER_TICKS);
    }

    private void setVoidRecoverTicks(int ticks) {
        this.entityData.set(VOID_RECOVER_TICKS, ticks);
    }

    private int getGlideStartTicks() {
        return this.entityData.get(GLIDE_START_TICKS);
    }

    private void setGlideStartTicks(int ticks) {
        this.entityData.set(GLIDE_START_TICKS, ticks);
    }

    private int getFleeTicks() {
        return this.entityData.get(FLEE_TICKS);
    }

    private void setFleeTicks(int ticks) {
        this.entityData.set(FLEE_TICKS, ticks);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new FleeWhenLowHealthGoal(this));
        this.goalSelector.addGoal(2, new SwoopAttackGoal(this));
        this.goalSelector.addGoal(6, new PerchInTreesGoal(this));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            tickAggression();
            tickTimers();
            tickVoidRecovery();

            // Detect leaving the ground for a glide-start animation pop.
            if (wasOnGroundLastTick && !this.onGround() && getGlideStartTicks() == 0) {
                setGlideStartTicks(10);
            }

            tickAirLocomotion();

            wasOnGroundLastTick = this.onGround();
        }
    }

    private void tickAggression() {
        // Mild aggression: sometimes decide to harass nearby players.
        if (getFleeTicks() > 0 || getTarget() != null) {
            return;
        }

        if (this.tickCount % 40 != 0) {
            return;
        }

        if (random.nextInt(6) != 0) {
            return;
        }

        Player nearest = this.level().getNearestPlayer(this, 10.0D);
        if (nearest == null || nearest.isCreative() || nearest.isSpectator()) {
            return;
        }

        AABB box = this.getBoundingBox().inflate(10.0D, 6.0D, 10.0D);
        if (!box.contains(nearest.position())) {
            return;
        }

        this.setTarget(nearest);
    }

    private void tickTimers() {
        if (getAttackAnimTicks() > 0) {
            setAttackAnimTicks(getAttackAnimTicks() - 1);
        }

        if (getVoidRecoverTicks() > 0) {
            setVoidRecoverTicks(getVoidRecoverTicks() - 1);
        }

        if (getGlideStartTicks() > 0) {
            setGlideStartTicks(getGlideStartTicks() - 1);
        }

        if (getFleeTicks() > 0) {
            setFleeTicks(getFleeTicks() - 1);
        }
    }

    private void tickAirLocomotion() {
        // Default airborne behavior is gliding.
        if (this.onGround() || this.isInWaterOrBubble()) {
            return;
        }

        Vec3 delta = this.getDeltaMovement();
        double horizontalSpeed = Math.sqrt(delta.x * delta.x + delta.z * delta.z);

        boolean wantsToAscend = wantsToGainAltitude();
        boolean rising = delta.y > 0.06D;
        boolean hovering = !wantsToAscend && Math.abs(delta.y) < 0.03D && horizontalSpeed < 0.05D;

        if (wantsToAscend || rising) {
            applyFlapPhysics();
            return;
        }

        if (hovering) {
            applyHoverPhysics();
            return;
        }

        applyGlidePhysics();
    }

    private boolean wantsToGainAltitude() {
        if (getVoidRecoverTicks() > 0) {
            return true;
        }

        LivingEntity target = getTarget();
        if (target != null && target.isAlive()) {
            if (target.getY() > this.getY() + 2.0D) {
                return true;
            }
        }

        if (perchTarget != null) {
            return perchTarget.getY() > this.blockPosition().getY() + 1;
        }

        return false;
    }

    private void applyGlidePhysics() {
        Vec3 delta = this.getDeltaMovement();

        double y = Mth.clamp(delta.y, GLIDE_MAX_FALL_SPEED, 0.40D);
        if (y < 0.0D) {
            y = Math.max(y, GLIDE_MIN_FALL_SPEED);
        }

        Vec3 look = this.getLookAngle();
        Vec3 boosted = new Vec3(
                delta.x + look.x * 0.02D,
                y,
                delta.z + look.z * 0.02D
        );

        this.setDeltaMovement(boosted);
        this.fallDistance = 0.0F;
    }

    private void applyFlapPhysics() {
        Vec3 delta = this.getDeltaMovement();
        Vec3 look = this.getLookAngle();

        Vec3 boosted = new Vec3(
                delta.x + look.x * FLAP_FORWARD_BOOST,
                Math.max(delta.y, 0.0D) + FLAP_ASCEND_BOOST,
                delta.z + look.z * FLAP_FORWARD_BOOST
        );

        this.setDeltaMovement(boosted);
        this.fallDistance = 0.0F;
    }

    private void applyHoverPhysics() {
        Vec3 delta = this.getDeltaMovement();
        Vec3 damped = new Vec3(
                delta.x * HOVER_DAMPING,
                delta.y * 0.70D,
                delta.z * HOVER_DAMPING
        );
        this.setDeltaMovement(damped);
        this.fallDistance = 0.0F;
    }

    private void tickVoidRecovery() {
        int minY = this.level().getMinBuildHeight();
        if (this.getY() > minY - 8) {
            return;
        }

        if (getVoidRecoverTicks() == 0) {
            setVoidRecoverTicks(20);
        }

        BlockPos pos = this.blockPosition();
        int safeY = this.level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
        safeY = Math.max(safeY + 2, minY + 8);

        this.setPos(pos.getX() + 0.5D, safeY, pos.getZ() + 0.5D);
        this.setDeltaMovement(Vec3.ZERO);
        this.fallDistance = 0.0F;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void playStepSound(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState blockIn) {
        this.playSound(SoundEvents.PARROT_STEP, 0.15F, 1.0F);
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();
        setVoidRecoverTicks(0);
        setGlideStartTicks(0);
        setAttackAnimTicks(0);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean hit = super.doHurtTarget(entity);
        if (hit && entity instanceof LivingEntity living) {
            living.knockback(0.35F, Mth.sin(this.getYRot() * ((float) Math.PI / 180F)), -Mth.cos(this.getYRot() * ((float) Math.PI / 180F)));
            if (!this.level().isClientSide) {
                setAttackAnimTicks(8);
            }
        }
        return hit;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 4, state -> {
            if (getVoidRecoverTicks() > 0) {
                state.setAnimation(VOID_RECOVER_ONCE);
                return PlayState.CONTINUE;
            }

            if (getAttackAnimTicks() > 0) {
                state.setAnimation(ATTACK_ONCE);
                return PlayState.CONTINUE;
            }

            if (this.onGround()) {
                if (state.isMoving()) {
                    state.setAnimation(WALK_LOOP);
                    return PlayState.CONTINUE;
                }
                state.setAnimation(IDLE_LOOP);
                return PlayState.CONTINUE;
            }

            Vec3 delta = this.getDeltaMovement();
            double horizontalSpeed = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
            boolean wantsToAscend = wantsToGainAltitude();
            boolean rising = delta.y > 0.06D;
            boolean hovering = !wantsToAscend && Math.abs(delta.y) < 0.03D && horizontalSpeed < 0.05D;

            if (wantsToAscend || rising) {
                state.setAnimation(FLAP_LOOP);
                return PlayState.CONTINUE;
            }

            if (hovering) {
                state.setAnimation(HOVER_LOOP);
                return PlayState.CONTINUE;
            }

            if (getGlideStartTicks() > 0) {
                state.setAnimation(GLIDE_START_ONCE);
                return PlayState.CONTINUE;
            }

            state.setAnimation(GLIDE_LOOP);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private static class FleeWhenLowHealthGoal extends Goal {
        private final WyrdwingEntity mob;

        private FleeWhenLowHealthGoal(WyrdwingEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (mob.getFleeTicks() > 0) {
                return true;
            }

            LivingEntity target = mob.getTarget();
            if (target == null || !target.isAlive()) {
                return false;
            }

            float hpRatio = mob.getHealth() / mob.getMaxHealth();
            return hpRatio <= LOW_HEALTH_FLEE_THRESHOLD;
        }

        @Override
        public void start() {
            mob.setFleeTicks(80);
        }

        @Override
        public void tick() {
            LivingEntity threat = mob.getTarget();
            if (threat == null) {
                return;
            }

            mob.setTarget(null);

            Vec3 away = mob.position().subtract(threat.position());
            if (away.lengthSqr() < 0.001D) {
                away = new Vec3(1.0D, 0.0D, 0.0D);
            }

            away = away.normalize().scale(1.6D);
            Vec3 desired = mob.position().add(away);
            mob.getNavigation().moveTo(desired.x, desired.y, desired.z, 1.3D);
        }

        @Override
        public boolean canContinueToUse() {
            return mob.getFleeTicks() > 0;
        }
    }

    private static class SwoopAttackGoal extends Goal {
        private final WyrdwingEntity mob;
        private int swoopCooldown = 0;

        private SwoopAttackGoal(WyrdwingEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (mob.getFleeTicks() > 0) {
                return false;
            }

            LivingEntity target = mob.getTarget();
            if (target == null || !target.isAlive()) {
                return false;
            }

            double dist = mob.distanceToSqr(target);
            return dist < (18.0D * 18.0D);
        }

        @Override
        public void tick() {
            LivingEntity target = mob.getTarget();
            if (target == null) {
                return;
            }

            if (swoopCooldown > 0) {
                swoopCooldown--;
            }

            mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

            if (mob.onGround()) {
                mob.setDeltaMovement(mob.getDeltaMovement().add(0.0D, 0.45D, 0.0D));
                return;
            }

            Vec3 toTarget = target.position().add(0.0D, target.getBbHeight() * 0.6D, 0.0D).subtract(mob.position());
            if (toTarget.lengthSqr() < 0.01D) {
                return;
            }

            Vec3 dir = toTarget.normalize();
            Vec3 delta = mob.getDeltaMovement();

            Vec3 steering = new Vec3(dir.x * 0.12D, dir.y * 0.10D - 0.02D, dir.z * 0.12D);
            mob.setDeltaMovement(delta.add(steering));
            mob.fallDistance = 0.0F;

            if (swoopCooldown == 0 && mob.distanceToSqr(target) < (2.2D * 2.2D)) {
                mob.doHurtTarget(target);
                swoopCooldown = 20;
            }
        }
    }

    private static class PerchInTreesGoal extends Goal {
        private final WyrdwingEntity mob;
        private int searchCooldown = 0;

        private PerchInTreesGoal(WyrdwingEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (mob.getTarget() != null) {
                return false;
            }

            if (mob.getFleeTicks() > 0) {
                return false;
            }

            return true;
        }

        @Override
        public void tick() {
            if (searchCooldown > 0) {
                searchCooldown--;
            }

            if (mob.perchRepathCooldown > 0) {
                mob.perchRepathCooldown--;
            }

            if (mob.perchTarget == null || mob.perchRepathCooldown == 0) {
                if (searchCooldown == 0) {
                    mob.perchTarget = findTreePerch(mob);
                    searchCooldown = 80;
                }
                mob.perchRepathCooldown = 40;
            }

            if (mob.perchTarget == null) {
                return;
            }

            double dist = mob.distanceToSqr(Vec3.atCenterOf(mob.perchTarget));
            if (dist < (3.0D * 3.0D)) {
                return;
            }

            if (mob.onGround()) {
                mob.getNavigation().moveTo(
                        mob.perchTarget.getX() + 0.5D,
                        mob.perchTarget.getY(),
                        mob.perchTarget.getZ() + 0.5D,
                        1.0D
                );
            }
        }

        private static BlockPos findTreePerch(WyrdwingEntity mob) {
            BlockPos origin = mob.blockPosition();
            int radius = 10;

            BlockPos best = null;
            int bestScore = Integer.MIN_VALUE;

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos base = origin.offset(dx, 0, dz);
                    int topY = mob.level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, base.getX(), base.getZ());
                    for (int dy = 0; dy <= 8; dy++) {
                        BlockPos pos = new BlockPos(base.getX(), topY + dy, base.getZ());

                        BlockState state = mob.level().getBlockState(pos);
                        if (!state.is(BlockTags.LOGS) && !state.is(BlockTags.LEAVES)) {
                            continue;
                        }

                        BlockPos above = pos.above();
                        if (!mob.level().getBlockState(above).getCollisionShape(mob.level(), above).isEmpty()) {
                            continue;
                        }

                        int score = pos.getY() - origin.getY();
                        if (score > bestScore) {
                            bestScore = score;
                            best = above;
                        }
                    }
                }
            }

            return best;
        }
    }
}
