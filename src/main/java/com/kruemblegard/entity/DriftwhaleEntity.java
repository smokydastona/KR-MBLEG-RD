package com.kruemblegard.entity;

import java.util.EnumSet;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.phys.Vec3;

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
 * - Spawns on solid ground like other Wayfall mobs (see spawn placement), then immediately drifts airborne.
 */
public class DriftwhaleEntity extends PathfinderMob implements GeoEntity {

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

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.16D)
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
            if (delta.y < -0.06D) {
                this.setDeltaMovement(delta.x, -0.06D, delta.z);
            }
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
