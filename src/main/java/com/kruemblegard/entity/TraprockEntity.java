package com.kruemblegard.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.Nullable;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class TraprockEntity extends Blaze implements GeoEntity {

    private static final EntityDataAccessor<Boolean> AWAKENED =
            SynchedEntityData.defineId(TraprockEntity.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation SLEEP_LOOP =
            RawAnimation.begin().thenLoop("animation.traprock.sleep");

    private static final RawAnimation AWAKE_LOOP =
            RawAnimation.begin().thenLoop("animation.traprock.awake");

    private static final RawAnimation AWAKEN_ONESHOT =
            RawAnimation.begin().thenPlay("animation.traprock.awaken");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int lingerTicks;
    private boolean playedAwakenAnim;

    public TraprockEntity(EntityType<? extends Blaze> type, Level level) {
        super(type, level);
        this.setNoAi(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Blaze.createAttributes()
                .add(Attributes.MAX_HEALTH, 26.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.FOLLOW_RANGE, 18.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(AWAKENED, false);
    }

    public boolean isAwakened() {
        return this.entityData.get(AWAKENED);
    }

    private void awaken(@Nullable LivingEntity target) {
        if (isAwakened()) return;

        this.entityData.set(AWAKENED, true);
        this.setNoAi(false);
        this.lingerTicks = 0;
        this.playedAwakenAnim = false;

        if (target != null) {
            this.setTarget(target);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            return;
        }

        if (!isAwakened()) {
            // Keep it totally still.
            setDeltaMovement(0, 0, 0);
            getNavigation().stop();

            // Proximity trigger: if a player lingers nearby, awaken.
            double radius = 3.5;
            AABB scan = getBoundingBox().inflate(radius);
            Player nearby = level().getNearestPlayer(this, radius);
            if (nearby != null && scan.contains(nearby.position())) {
                lingerTicks++;
                if (lingerTicks >= 60) {
                    awaken(nearby);
                }
            } else {
                lingerTicks = 0;
            }
        }
    }

    @Override
    public void aiStep() {
        // Traprock sleeps as a dormant "waystone" and should not emit Blaze smoke/sound particles.
        if (this.level().isClientSide && !this.isAwakened()) {
            return;
        }

        super.aiStep();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!level().isClientSide) {
            if (player instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) player;
                awaken(living);
            }
        }

        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!level().isClientSide) {
            if (!isAwakened() && source.getEntity() instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) source.getEntity();
                awaken(living);
            }
        }

        return super.hurt(source, amount);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 0, state -> {
            if (!isAwakened()) {
                state.setAnimation(SLEEP_LOOP);
                return PlayState.CONTINUE;
            }

            if (!playedAwakenAnim) {
                playedAwakenAnim = true;
                state.setAnimation(AWAKEN_ONESHOT);
                return PlayState.CONTINUE;
            }

            state.setAnimation(AWAKE_LOOP);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
