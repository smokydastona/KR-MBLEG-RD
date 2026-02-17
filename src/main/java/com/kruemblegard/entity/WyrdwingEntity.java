package com.kruemblegard.entity;

import java.util.EnumSet;

import com.kruemblegard.registry.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import com.kruemblegard.registry.ModEntities;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WyrdwingEntity extends TamableAnimal implements GeoEntity {

    private static final EntityDataAccessor<Integer> ATTACK_ANIM_TICKS =
        SynchedEntityData.defineId(WyrdwingEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> VOID_RECOVER_TICKS =
        SynchedEntityData.defineId(WyrdwingEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> GLIDE_START_TICKS =
        SynchedEntityData.defineId(WyrdwingEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> FLEE_TICKS =
        SynchedEntityData.defineId(WyrdwingEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> EAT_ANIM_TICKS =
        SynchedEntityData.defineId(WyrdwingEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> POUNCE_ANIM_TICKS =
        SynchedEntityData.defineId(WyrdwingEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> SCRATCH_ANIM_TICKS =
        SynchedEntityData.defineId(WyrdwingEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> CALL_ANIM_TICKS =
        SynchedEntityData.defineId(WyrdwingEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> SHAKE_ANIM_TICKS =
        SynchedEntityData.defineId(WyrdwingEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> HEAD_BOB_ANIM_TICKS =
        SynchedEntityData.defineId(WyrdwingEntity.class, EntityDataSerializers.INT);

    private static final RawAnimation IDLE_LOOP =
            RawAnimation.begin().thenLoop("animation.wyrdwing.idle");

    private static final RawAnimation WALK_LOOP =
            RawAnimation.begin().thenLoop("animation.wyrdwing.walk");

        private static final RawAnimation SIT_LOOP =
            RawAnimation.begin().thenLoop("animation.wyrdwing.sit");

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

        private static final RawAnimation EAT_ONCE =
            RawAnimation.begin().thenPlay("animation.wyrdwing.eat");

        // Optional (only used if you add matching entries in wyrdwing.animation.json)
        private static final RawAnimation POUNCE_ONCE =
            RawAnimation.begin().thenPlay("animation.wyrdwing.pounce");

        // Optional alt-melee (only used if you add matching entries in wyrdwing.animation.json)
        private static final RawAnimation SCRATCH_ONCE =
            RawAnimation.begin().thenPlay("animation.wyrdwing.scratch");

        private static final RawAnimation CALL_ONCE =
            RawAnimation.begin().thenPlay("animation.wyrdwing.call_1");

        private static final RawAnimation SHAKE_ONCE =
            RawAnimation.begin().thenPlay("animation.wyrdwing.shake");

        private static final RawAnimation HEAD_BOB_ONCE =
            RawAnimation.begin().thenPlay("animation.wyrdwing.head_bob");

        private static final double GLIDE_MAX_FALL_SPEED = -0.12D;
        private static final double GLIDE_MIN_FALL_SPEED = -0.035D;

        private static final double FLAP_ASCEND_BOOST = 0.065D;
        private static final double FLAP_FORWARD_BOOST = 0.035D;

        private static final double HOVER_DAMPING = 0.80D;

        private static final float LOW_HEALTH_FLEE_THRESHOLD = 0.30F;

        // Scaralon-style "forced land": Wyrdwings prefer to remain airborne, but should not
        // orbit forever. Use a simple stamina-like budget that drains while airborne and
        // regenerates quickly while grounded.
        private static final int MAX_FLIGHT_STAMINA_TICKS = 20 * 25; // 25 seconds
        private static final int STAMINA_REGEN_PER_TICK_GROUNDED = 3;
        private static final int STAMINA_DRAIN_PER_TICK_AIRBORNE = 1;
        private static final double EXHAUSTED_AUTO_LAND_DESCENT_VELOCITY = -0.35D;
        private static final double MAX_EXHAUSTED_DOWNWARD_VELOCITY = -0.65D;
        private static final double EXHAUSTED_HORIZONTAL_DAMPING = 0.55D;
        private static final int IDLE_TAKEOFF_COOLDOWN_TICKS = 20 * 6;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

        private boolean wasOnGroundLastTick = true;
        private BlockPos perchTarget = null;
        private int perchRepathCooldown = 0;

        private int flightStaminaTicks = MAX_FLIGHT_STAMINA_TICKS;
        private int idleTakeoffCooldownTicks = 0;
        private int groundTakeoffCooldownTicks = 0;

        private int headBobCooldownTicks = 0;

    public WyrdwingEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        this.setMaxUpStep(1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 18.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.ATTACK_DAMAGE, 3.5D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        WyrdwingEntity child = ModEntities.WYRDWING.get().create(level);
        if (child == null) {
            return null;
        }

        if (this.isTame() && this.getOwnerUUID() != null) {
            child.setOwnerUUID(this.getOwnerUUID());
            child.setTame(true);
        }

        return child;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_ANIM_TICKS, 0);
        this.entityData.define(VOID_RECOVER_TICKS, 0);
        this.entityData.define(GLIDE_START_TICKS, 0);
        this.entityData.define(FLEE_TICKS, 0);
        this.entityData.define(EAT_ANIM_TICKS, 0);
        this.entityData.define(POUNCE_ANIM_TICKS, 0);
        this.entityData.define(SCRATCH_ANIM_TICKS, 0);
        this.entityData.define(CALL_ANIM_TICKS, 0);
        this.entityData.define(SHAKE_ANIM_TICKS, 0);
        this.entityData.define(HEAD_BOB_ANIM_TICKS, 0);
    }

    private int getHeadBobAnimTicks() {
        return this.entityData.get(HEAD_BOB_ANIM_TICKS);
    }

    private void setHeadBobAnimTicks(int ticks) {
        this.entityData.set(HEAD_BOB_ANIM_TICKS, ticks);
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

    private int getEatAnimTicks() {
        return this.entityData.get(EAT_ANIM_TICKS);
    }

    private void setEatAnimTicks(int ticks) {
        this.entityData.set(EAT_ANIM_TICKS, ticks);
    }

    private int getPounceAnimTicks() {
        return this.entityData.get(POUNCE_ANIM_TICKS);
    }

    private void setPounceAnimTicks(int ticks) {
        this.entityData.set(POUNCE_ANIM_TICKS, ticks);
    }

    private int getScratchAnimTicks() {
        return this.entityData.get(SCRATCH_ANIM_TICKS);
    }

    private void setScratchAnimTicks(int ticks) {
        this.entityData.set(SCRATCH_ANIM_TICKS, ticks);
    }

    private int getCallAnimTicks() {
        return this.entityData.get(CALL_ANIM_TICKS);
    }

    private void setCallAnimTicks(int ticks) {
        this.entityData.set(CALL_ANIM_TICKS, ticks);
    }

    private int getShakeAnimTicks() {
        return this.entityData.get(SHAKE_ANIM_TICKS);
    }

    private void setShakeAnimTicks(int ticks) {
        this.entityData.set(SHAKE_ANIM_TICKS, ticks);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new FleeWhenLowHealthGoal(this));
        this.goalSelector.addGoal(3, new AirSwoopAttackGoal(this));
        this.goalSelector.addGoal(4, new GroundAttackGoal(this));
        this.goalSelector.addGoal(5, new EatBugMeatItemGoal(this));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.15D, 10.0F, 2.0F, false));

        // Perching is split into:
        // - a low-impact search/timer goal that chooses a perch target
        // - dedicated approach goals for ground vs air movement
        this.goalSelector.addGoal(7, new PerchInTreesGoal(this));

        // Grounded idle preference: occasionally take off to orbit the chosen perch tree.
        // (But don't immediately re-takeoff when exhausted; stamina must recover.)
        this.goalSelector.addGoal(8, new TakeoffToOrbitGoal(this));
        this.goalSelector.addGoal(9, new GroundPerchApproachGoal(this));
        this.goalSelector.addGoal(10, new AirPerchApproachGoal(this));

        // Idle airborne orbiting around the selected tree perch.
        this.goalSelector.addGoal(11, new AirWanderGoal(this));

        this.goalSelector.addGoal(12, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(13, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(14, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Mob.class, 10, true, false,
            target -> target.getMobType() == MobType.ARTHROPOD));
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            tickAggression();
            tickTimers();
            tickFlightBudget();
            tickVoidRecovery();
            tickAmbientAnimations();
            tickWalkHeadBob();

            // Detect leaving the ground for a glide-start animation pop.
            if (wasOnGroundLastTick && !this.onGround() && getGlideStartTicks() == 0) {
                setGlideStartTicks(10);
            }

            tickAirLocomotion();
            tickFaceMovementDirection();

            wasOnGroundLastTick = this.onGround();
        }
    }

    private void tickWalkHeadBob() {
        if (this.isOrderedToSit()) {
            return;
        }

        if (!this.onGround()) {
            return;
        }

        if (this.isInWaterOrBubble()) {
            return;
        }

        if (getHeadBobAnimTicks() > 0) {
            return;
        }

        if (getVoidRecoverTicks() > 0 || getAttackAnimTicks() > 0 || getScratchAnimTicks() > 0 || getEatAnimTicks() > 0 || getPounceAnimTicks() > 0) {
            return;
        }

        if (getCallAnimTicks() > 0 || getShakeAnimTicks() > 0) {
            return;
        }

        if (this.getDeltaMovement().horizontalDistanceSqr() <= 1.0E-4D) {
            headBobCooldownTicks = 0;
            return;
        }

        if (headBobCooldownTicks > 0) {
            headBobCooldownTicks--;
            return;
        }

        // A birdy head-bob beat every few steps while ground-walking.
        if (this.random.nextInt(3) == 0) {
            setHeadBobAnimTicks(12);
        }

        headBobCooldownTicks = 10 + this.random.nextInt(16);
    }

    private void tickFlightBudget() {
        if (idleTakeoffCooldownTicks > 0) {
            idleTakeoffCooldownTicks--;
        }

        if (groundTakeoffCooldownTicks > 0) {
            groundTakeoffCooldownTicks--;
        }

        // Regenerate budget while grounded (or wet), drain while airborne.
        if (this.onGround() || this.isInWaterOrBubble()) {
            if (flightStaminaTicks < MAX_FLIGHT_STAMINA_TICKS) {
                flightStaminaTicks = Math.min(MAX_FLIGHT_STAMINA_TICKS, flightStaminaTicks + STAMINA_REGEN_PER_TICK_GROUNDED);
            }
            return;
        }

        if (flightStaminaTicks > 0) {
            flightStaminaTicks = Math.max(0, flightStaminaTicks - STAMINA_DRAIN_PER_TICK_AIRBORNE);
        }
    }

    private boolean isFlightExhausted() {
        return flightStaminaTicks <= 0;
    }

    private boolean hasTakeoffStamina() {
        return flightStaminaTicks >= (int) (MAX_FLIGHT_STAMINA_TICKS * 0.75F);
    }

    private boolean canGroundTakeoffNow() {
        return groundTakeoffCooldownTicks <= 0 && this.getDeltaMovement().y <= 0.05D;
    }

    private void triggerGroundTakeoff(int cooldownTicks) {
        groundTakeoffCooldownTicks = Math.max(groundTakeoffCooldownTicks, cooldownTicks);
        this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.42D, 0.0D));
        this.fallDistance = 0.0F;
    }

    private void tickFaceMovementDirection() {
        if (this.isOrderedToSit()) {
            return;
        }

        Vec3 delta = this.getDeltaMovement();
        double horizSqr = delta.x * delta.x + delta.z * delta.z;
        if (horizSqr < 1.0E-4D) {
            return;
        }

        float yaw = (float) (Mth.atan2(delta.z, delta.x) * (180.0D / Math.PI)) - 90.0F;
        this.setYRot(yaw);
        this.setYHeadRot(yaw);
        this.setYBodyRot(yaw);
    }

    private void tickAmbientAnimations() {
        if (this.isOrderedToSit()) {
            return;
        }

        if (!this.onGround()) {
            return;
        }

        if (this.isInWaterOrBubble()) {
            return;
        }

        if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-4) {
            return;
        }

        if (this.getTarget() != null || getFleeTicks() > 0) {
            return;
        }

        if (getVoidRecoverTicks() > 0 || getAttackAnimTicks() > 0 || getEatAnimTicks() > 0 || getPounceAnimTicks() > 0 || getScratchAnimTicks() > 0) {
            return;
        }

        if (getCallAnimTicks() > 0 || getShakeAnimTicks() > 0) {
            return;
        }

        if (this.tickCount % 120 != 0) {
            return;
        }

        // Very occasional flavor beats while idle on the ground.
        // Call is more common than shake.
        if (this.random.nextInt(7) == 0) {
            setCallAnimTicks(15);
        } else if (this.random.nextInt(14) == 0) {
            setShakeAnimTicks(40);
        }
    }

    private void tickAggression() {
        // Mild aggression: sometimes decide to harass nearby players.
        if (this.isTame()) {
            return;
        }
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

        if (getScratchAnimTicks() > 0) {
            setScratchAnimTicks(getScratchAnimTicks() - 1);
        }

        if (getPounceAnimTicks() > 0) {
            setPounceAnimTicks(getPounceAnimTicks() - 1);
        }

        if (getEatAnimTicks() > 0) {
            setEatAnimTicks(getEatAnimTicks() - 1);
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

        if (getCallAnimTicks() > 0) {
            setCallAnimTicks(getCallAnimTicks() - 1);
        }

        if (getShakeAnimTicks() > 0) {
            setShakeAnimTicks(getShakeAnimTicks() - 1);
        }

        if (getHeadBobAnimTicks() > 0) {
            setHeadBobAnimTicks(getHeadBobAnimTicks() - 1);
        }
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ModItems.BUG_MEAT.get()) || stack.is(ModItems.COOKED_BUG_MEAT.get());
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.level().isClientSide) {
            boolean canTameOrFeed = isFood(stack) || (this.isTame() && this.isOwnedBy(player) && stack.isEmpty());
            return canTameOrFeed ? InteractionResult.SUCCESS : super.mobInteract(player, hand);
        }

        if (isFood(stack)) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            if (!this.isTame()) {
                if (this.random.nextInt(3) == 0) {
                    this.tame(player);
                    this.setOrderedToSit(false);
                    this.setTarget(null);
                    this.level().broadcastEntityEvent(this, (byte) 7); // hearts
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6); // smoke
                }
            } else {
                this.heal(3.0F);
            }

            setEatAnimTicks(12);
            this.playSound(SoundEvents.GENERIC_EAT, 0.7F, 0.9F + this.random.nextFloat() * 0.2F);
            return InteractionResult.CONSUME;
        }

        // Owner control: right-click with empty hand to sit/stand.
        if (this.isTame() && this.isOwnedBy(player) && stack.isEmpty()) {
            this.setOrderedToSit(!this.isOrderedToSit());
            this.getNavigation().stop();
            this.setTarget(null);
            return InteractionResult.CONSUME;
        }

        return super.mobInteract(player, hand);
    }

    private void tickAirLocomotion() {
        // Default airborne behavior is gliding.
        if (this.onGround() || this.isInWaterOrBubble()) {
            return;
        }

        // Out of flight budget: force a gentle-but-decisive landing.
        // Exception: allow void recovery to regain altitude.
        if (isFlightExhausted() && getVoidRecoverTicks() == 0) {
            applyExhaustedAutoLandPhysics();
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

        // Exhausted: cannot intentionally gain altitude.
        if (isFlightExhausted()) {
            return false;
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

    private void applyExhaustedAutoLandPhysics() {
        Vec3 delta = this.getDeltaMovement();

        double y = Math.min(delta.y, EXHAUSTED_AUTO_LAND_DESCENT_VELOCITY);
        y = Mth.clamp(y, MAX_EXHAUSTED_DOWNWARD_VELOCITY, 0.0D);

        Vec3 forced = new Vec3(delta.x * EXHAUSTED_HORIZONTAL_DAMPING, y, delta.z * EXHAUSTED_HORIZONTAL_DAMPING);
        this.setDeltaMovement(forced);
        this.fallDistance = 0.0F;
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
        setEatAnimTicks(0);
        setPounceAnimTicks(0);
        setScratchAnimTicks(0);
        setCallAnimTicks(0);
        setShakeAnimTicks(0);

        flightStaminaTicks = MAX_FLIGHT_STAMINA_TICKS;
        idleTakeoffCooldownTicks = 0;
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean hit = super.doHurtTarget(entity);
        if (hit && entity instanceof LivingEntity living) {
            living.knockback(0.35F, Mth.sin(this.getYRot() * ((float) Math.PI / 180F)), -Mth.cos(this.getYRot() * ((float) Math.PI / 180F)));
            if (!this.level().isClientSide) {
                // Prefer the original Wyrdwing bite animation, but occasionally play an alternate
                // (useful if you add a raptor-style claw/scratch animation to wyrdwing.animation.json).
                if (this.random.nextInt(4) == 0) {
                    setScratchAnimTicks(10);
                } else {
                    setAttackAnimTicks(8);
                }
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

            if (getPounceAnimTicks() > 0) {
                state.setAnimation(POUNCE_ONCE);
                return PlayState.CONTINUE;
            }

            if (getAttackAnimTicks() > 0) {
                state.setAnimation(ATTACK_ONCE);
                return PlayState.CONTINUE;
            }

            if (getScratchAnimTicks() > 0) {
                state.setAnimation(SCRATCH_ONCE);
                return PlayState.CONTINUE;
            }

            if (getEatAnimTicks() > 0) {
                state.setAnimation(EAT_ONCE);
                return PlayState.CONTINUE;
            }

            if (getCallAnimTicks() > 0) {
                state.setAnimation(CALL_ONCE);
                return PlayState.CONTINUE;
            }

            if (getShakeAnimTicks() > 0) {
                state.setAnimation(SHAKE_ONCE);
                return PlayState.CONTINUE;
            }

            if (this.isOrderedToSit()) {
                state.setAnimation(SIT_LOOP);
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

        controllers.add(new AnimationController<>(this, "head_bob", 0, state -> {
            if (!this.onGround() || this.isOrderedToSit() || !state.isMoving()) {
                return PlayState.STOP;
            }

            if (getHeadBobAnimTicks() <= 0) {
                return PlayState.STOP;
            }

            state.setAnimation(HEAD_BOB_ONCE);
            return PlayState.CONTINUE;
        }));
    }

    private static class EatBugMeatItemGoal extends Goal {
        private final WyrdwingEntity mob;
        private ItemEntity targetItem;
        private int nextSearchTick = 0;

        private EatBugMeatItemGoal(WyrdwingEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (mob.isOrderedToSit() || mob.getTarget() != null || mob.getFleeTicks() > 0) {
                return false;
            }

            if (mob.tickCount < nextSearchTick) {
                return false;
            }

            nextSearchTick = mob.tickCount + 20;
            targetItem = findNearestBugMeat();
            return targetItem != null;
        }

        @Override
        public boolean canContinueToUse() {
            return targetItem != null
                    && targetItem.isAlive()
                    && !mob.isOrderedToSit()
                    && mob.getTarget() == null
                    && mob.getFleeTicks() == 0;
        }

        @Override
        public void start() {
            if (targetItem != null) {
                mob.getNavigation().moveTo(targetItem, 1.2D);
            }
        }

        @Override
        public void stop() {
            targetItem = null;
            mob.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (targetItem == null) {
                return;
            }

            mob.getLookControl().setLookAt(targetItem, 30.0F, 30.0F);

            if (mob.distanceToSqr(targetItem) > 2.25D) {
                if (mob.tickCount % 10 == 0) {
                    mob.getNavigation().moveTo(targetItem, 1.2D);
                }
                return;
            }

            ItemStack stack = targetItem.getItem();
            if (stack.is(ModItems.BUG_MEAT.get()) && !stack.isEmpty()) {
                stack.shrink(1);
                mob.heal(2.0F);
                mob.setEatAnimTicks(12);
                mob.playSound(SoundEvents.GENERIC_EAT, 0.7F, 0.9F + mob.random.nextFloat() * 0.2F);
                if (stack.isEmpty()) {
                    targetItem.discard();
                }
            }

            stop();
        }

        private ItemEntity findNearestBugMeat() {
            AABB box = mob.getBoundingBox().inflate(10.0D, 6.0D, 10.0D);
            ItemEntity best = null;
            double bestDist = Double.MAX_VALUE;
            for (ItemEntity item : mob.level().getEntitiesOfClass(ItemEntity.class, box, e -> e.isAlive() && e.getItem().is(ModItems.BUG_MEAT.get()))) {
                double d = mob.distanceToSqr(item);
                if (d < bestDist) {
                    bestDist = d;
                    best = item;
                }
            }
            return best;
        }
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

    private static class GroundAttackGoal extends Goal {
        private final WyrdwingEntity mob;
        private int attackCooldown = 0;
        private int pounceCooldown = 0;

        private GroundAttackGoal(WyrdwingEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (mob.getFleeTicks() > 0 || mob.isOrderedToSit()) {
                return false;
            }

            if (!mob.onGround()) {
                return false;
            }

            LivingEntity target = mob.getTarget();
            if (target == null || !target.isAlive()) {
                return false;
            }

            return mob.distanceToSqr(target) < (18.0D * 18.0D);
        }

        @Override
        public boolean canContinueToUse() {
            if (!mob.onGround()) {
                return false;
            }

            LivingEntity target = mob.getTarget();
            return target != null && target.isAlive() && mob.getFleeTicks() == 0 && !mob.isOrderedToSit();
        }

        @Override
        public void tick() {
            LivingEntity target = mob.getTarget();
            if (target == null) {
                return;
            }

            if (attackCooldown > 0) {
                attackCooldown--;
            }

            if (pounceCooldown > 0) {
                pounceCooldown--;
            }

            mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

            double distSqr = mob.distanceToSqr(target);
            boolean targetHigh = target.getY() > mob.getY() + 1.5D;

            // Hop into the air to transition into air combat if we need altitude or the target is far.
            if (targetHigh || distSqr > (9.0D * 9.0D)) {
                if (!mob.isFlightExhausted() && mob.canGroundTakeoffNow()) {
                    mob.triggerGroundTakeoff(14);
                    return;
                }
            }

            mob.getNavigation().moveTo(target, 1.25D);

            // Optional raptor-style pounce.
            if (pounceCooldown == 0 && distSqr > (3.0D * 3.0D) && distSqr < (6.0D * 6.0D)) {
                Vec3 to = target.position().subtract(mob.position());
                Vec3 horiz = new Vec3(to.x, 0.0D, to.z);
                if (horiz.lengthSqr() > 0.001D) {
                    Vec3 dir = horiz.normalize();
                    mob.setDeltaMovement(mob.getDeltaMovement().add(dir.x * 0.55D, 0.40D, dir.z * 0.55D));
                    mob.fallDistance = 0.0F;
                    if (!mob.level().isClientSide) {
                        mob.setPounceAnimTicks(12);
                    }
                    pounceCooldown = 55;
                    return;
                }
            }

            if (attackCooldown == 0 && distSqr < (2.2D * 2.2D)) {
                mob.doHurtTarget(target);
                attackCooldown = 20;
            }
        }
    }

    private static class AirSwoopAttackGoal extends Goal {
        private final WyrdwingEntity mob;
        private int swoopCooldown = 0;

        private AirSwoopAttackGoal(WyrdwingEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (mob.getFleeTicks() > 0 || mob.isOrderedToSit()) {
                return false;
            }

            if (mob.onGround() || mob.isInWaterOrBubble()) {
                return false;
            }

            if (mob.isFlightExhausted()) {
                return false;
            }

            LivingEntity target = mob.getTarget();
            if (target == null || !target.isAlive()) {
                return false;
            }

            return mob.distanceToSqr(target) < (18.0D * 18.0D);
        }

        @Override
        public boolean canContinueToUse() {
            if (mob.onGround() || mob.isInWaterOrBubble()) {
                return false;
            }

            if (mob.isFlightExhausted()) {
                return false;
            }

            LivingEntity target = mob.getTarget();
            return target != null && target.isAlive() && mob.getFleeTicks() == 0 && !mob.isOrderedToSit();
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

            double distSqr = mob.distanceToSqr(target);
            Vec3 toTarget = target.position().add(0.0D, target.getBbHeight() * 0.6D, 0.0D).subtract(mob.position());
            if (toTarget.lengthSqr() < 0.01D) {
                return;
            }

            Vec3 dir = toTarget.normalize();
            Vec3 delta = mob.getDeltaMovement();

            Vec3 steering = new Vec3(dir.x * 0.12D, dir.y * 0.10D - 0.02D, dir.z * 0.12D);
            mob.setDeltaMovement(delta.add(steering));
            mob.fallDistance = 0.0F;

            if (swoopCooldown == 0 && distSqr < (2.2D * 2.2D)) {
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
        }

        @Override
        public boolean canUse() {
            if (mob.getTarget() != null) {
                return false;
            }

            if (mob.getFleeTicks() > 0) {
                return false;
            }

            if (mob.isOrderedToSit()) {
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

    private static class GroundPerchApproachGoal extends Goal {
        private final WyrdwingEntity mob;

        private GroundPerchApproachGoal(WyrdwingEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (mob.getTarget() != null || mob.getFleeTicks() > 0 || mob.isOrderedToSit()) {
                return false;
            }

            if (!mob.onGround()) {
                return false;
            }

            if (mob.perchTarget == null) {
                return false;
            }

            return mob.distanceToSqr(Vec3.atCenterOf(mob.perchTarget)) >= (3.0D * 3.0D);
        }

        @Override
        public boolean canContinueToUse() {
            return canUse();
        }

        @Override
        public void tick() {
            if (mob.perchTarget == null) {
                return;
            }

            double dist = mob.distanceToSqr(Vec3.atCenterOf(mob.perchTarget));
            if (dist < (3.0D * 3.0D)) {
                mob.getNavigation().stop();
                return;
            }

            // If the perch is significantly above us, take off to let air-perch steering take over.
            if (mob.perchTarget.getY() > mob.getY() + 1.0D && !mob.isFlightExhausted()) {
                if (mob.canGroundTakeoffNow()) {
                    mob.triggerGroundTakeoff(18);
                    return;
                }
            }

            mob.getNavigation().moveTo(
                    mob.perchTarget.getX() + 0.5D,
                    mob.perchTarget.getY(),
                    mob.perchTarget.getZ() + 0.5D,
                    1.0D
            );
        }
    }

    private static class TakeoffToOrbitGoal extends Goal {
        private final WyrdwingEntity mob;

        private TakeoffToOrbitGoal(WyrdwingEntity mob) {
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            if (mob.getTarget() != null || mob.getFleeTicks() > 0 || mob.isOrderedToSit()) {
                return false;
            }

            if (!mob.onGround() || mob.isInWaterOrBubble()) {
                return false;
            }

            if (mob.perchTarget == null) {
                return false;
            }

            if (mob.idleTakeoffCooldownTicks > 0) {
                return false;
            }

            if (!mob.hasTakeoffStamina()) {
                return false;
            }

            if (!mob.canGroundTakeoffNow()) {
                return false;
            }

            double distSqr = mob.distanceToSqr(Vec3.atCenterOf(mob.perchTarget));
            if (distSqr < (5.0D * 5.0D) || distSqr > (18.0D * 18.0D)) {
                return false;
            }

            // Keep it fairly rare so it doesn't look twitchy; still frequent enough to feel like
            // it prefers being airborne when rested.
            return mob.random.nextInt(30) == 0;
        }

        @Override
        public void start() {
            mob.getNavigation().stop();
            mob.triggerGroundTakeoff(18);
            mob.idleTakeoffCooldownTicks = IDLE_TAKEOFF_COOLDOWN_TICKS;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }
    }

    private static class AirPerchApproachGoal extends Goal {
        private final WyrdwingEntity mob;

        private AirPerchApproachGoal(WyrdwingEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (mob.getTarget() != null || mob.getFleeTicks() > 0 || mob.isOrderedToSit()) {
                return false;
            }

            if (mob.onGround() || mob.isInWaterOrBubble()) {
                return false;
            }

            if (mob.isFlightExhausted()) {
                return false;
            }

            if (mob.perchTarget == null) {
                return false;
            }

            // Only use approach when we're meaningfully far. When close, orbit instead.
            return mob.distanceToSqr(Vec3.atCenterOf(mob.perchTarget)) >= (12.0D * 12.0D);
        }

        @Override
        public boolean canContinueToUse() {
            return canUse();
        }

        @Override
        public void tick() {
            if (mob.perchTarget == null) {
                return;
            }

            Vec3 desired = Vec3.atCenterOf(mob.perchTarget).add(0.0D, 0.35D, 0.0D);
            steerToward(desired, 0.10D, 0.08D);
        }

        private void steerToward(Vec3 desired, double horiz, double vert) {
            mob.getLookControl().setLookAt(desired.x, desired.y, desired.z, 25.0F, 25.0F);

            Vec3 to = desired.subtract(mob.position());
            if (to.lengthSqr() < 0.01D) {
                return;
            }

            Vec3 dir = to.normalize();
            Vec3 delta = mob.getDeltaMovement();
            Vec3 steering = new Vec3(dir.x * horiz, dir.y * vert - 0.01D, dir.z * horiz);
            mob.setDeltaMovement(delta.add(steering));
            mob.fallDistance = 0.0F;
        }
    }

    private static class AirWanderGoal extends Goal {
        private final WyrdwingEntity mob;
        private double orbitAngleRad = 0.0D;
        private double orbitRadius = 7.5D;

        private AirWanderGoal(WyrdwingEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (mob.getTarget() != null || mob.getFleeTicks() > 0 || mob.isOrderedToSit()) {
                return false;
            }

            if (mob.onGround() || mob.isInWaterOrBubble()) {
                return false;
            }

            if (mob.isFlightExhausted()) {
                return false;
            }

            // Orbit the chosen perch tree when idle.
            if (mob.perchTarget == null) {
                return false;
            }

            // If we're too far, let AirPerchApproachGoal bring us closer.
            return mob.distanceToSqr(Vec3.atCenterOf(mob.perchTarget)) < (16.0D * 16.0D);
        }

        @Override
        public void start() {
            orbitAngleRad = mob.random.nextDouble() * (Math.PI * 2.0D);
            orbitRadius = 6.5D + mob.random.nextDouble() * 4.5D;
        }

        @Override
        public boolean canContinueToUse() {
            return canUse();
        }

        @Override
        public void tick() {
            if (mob.perchTarget == null) {
                return;
            }

            // Smooth orbit around the perch tree. We keep altitude slightly above the perch.
            orbitAngleRad += 0.10D;

            double cx = mob.perchTarget.getX() + 0.5D;
            double cz = mob.perchTarget.getZ() + 0.5D;
            double baseY = mob.perchTarget.getY() + 3.5D;
            double bob = Math.sin(orbitAngleRad * 0.5D) * 0.6D;

            double x = cx + Math.cos(orbitAngleRad) * orbitRadius;
            double z = cz + Math.sin(orbitAngleRad) * orbitRadius;
            double y = Math.max(baseY + bob, mob.level().getMinBuildHeight() + 4.0D);

            Vec3 desired = new Vec3(x, y, z);
            steerToward(desired, 0.085D, 0.060D);
        }

        private void steerToward(Vec3 desired, double horiz, double vert) {
            mob.getLookControl().setLookAt(desired.x, desired.y, desired.z, 20.0F, 20.0F);

            Vec3 to = desired.subtract(mob.position());
            if (to.lengthSqr() < 0.01D) {
                return;
            }

            Vec3 dir = to.normalize();
            Vec3 delta = mob.getDeltaMovement();
            Vec3 steering = new Vec3(dir.x * horiz, dir.y * vert - 0.01D, dir.z * horiz);
            mob.setDeltaMovement(delta.add(steering));
            mob.fallDistance = 0.0F;
        }
    }
}
