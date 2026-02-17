package com.kruemblegard.entity;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import com.kruemblegard.registry.ModSounds;

import org.jetbrains.annotations.Nullable;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FaultCrawlerEntity extends Monster implements GeoEntity {

    private static final RawAnimation IDLE_LOOP = RawAnimation.begin().thenLoop("animation.fault_crawler.idle");
    private static final RawAnimation WALK_LOOP = RawAnimation.begin().thenLoop("animation.fault_crawler.walk");
    private static final RawAnimation BURIED_LOOP = RawAnimation.begin().thenLoop("animation.fault_crawler.buried");

    private static final RawAnimation SLAM_ONCE = RawAnimation.begin().thenPlay("animation.fault_crawler.slam");
    private static final RawAnimation EMERGE_ONCE = RawAnimation.begin().thenPlay("animation.fault_crawler.emerge");
    private static final RawAnimation PULSE_ONCE = RawAnimation.begin().thenPlay("animation.fault_crawler.pulse");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private boolean buried = false;
    private int emergeTicks = 0;

    private int faultPulseCooldown = 20 * 6;

    public FaultCrawlerEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 18.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.24D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 20.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.05D, true));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        // Neutral until provoked.
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, net.minecraft.world.DifficultyInstance difficulty,
                                       MobSpawnType spawnType, @Nullable SpawnGroupData spawnData,
                                       @Nullable net.minecraft.nbt.CompoundTag tag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnData, tag);

        // Often found partially buried.
        if (spawnType != MobSpawnType.STRUCTURE && this.random.nextFloat() < 0.60F) {
            setBuried(true);
        }

        return data;
    }

    private void setBuried(boolean value) {
        buried = value;
        if (buried) {
            setNoAi(true);
            getNavigation().stop();
            setDeltaMovement(0.0D, 0.0D, 0.0D);
        } else {
            setNoAi(false);
        }
    }

    private void emerge() {
        if (!buried) return;

        setBuried(false);
        emergeTicks = 20;
        triggerAnim("actionController", "emerge");
        playSound(ModSounds.FAULT_CRAWLER_EMERGE.get(), 0.95F, 0.9F + random.nextFloat() * 0.2F);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            if (buried) {
                // Emerge when disturbed.
                Player nearest = level().getNearestPlayer(this, 3.5D);
                if (nearest != null && !nearest.isSpectator() && nearest.isAlive()) {
                    emerge();
                }
                return;
            }

            if (emergeTicks > 0) {
                emergeTicks--;
                getNavigation().stop();
                setDeltaMovement(0.0D, 0.0D, 0.0D);
                return;
            }

            // Occasional small knockback pulse.
            if (faultPulseCooldown > 0) {
                faultPulseCooldown--;
            } else {
                boolean hasNearbyPlayer = level().getNearestPlayer(this, 3.25D) != null;
                if (hasNearbyPlayer && this.random.nextFloat() < 0.25F) {
                    doFaultPulse();
                    faultPulseCooldown = 20 * (6 + this.random.nextInt(6));
                } else {
                    faultPulseCooldown = 20 * 2;
                }
            }
        }
    }

    private void doFaultPulse() {
        triggerAnim("actionController", "pulse");

        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        List<Player> players = level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(3.25D, 1.5D, 3.25D),
                p -> p.isAlive() && !p.isSpectator());

        for (Player player : players) {
            double dx = player.getX() - getX();
            double dz = player.getZ() - getZ();
            player.knockback(0.65F, dx, dz);
        }

        serverLevel.sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()),
                getX(), getY() + 0.25D, getZ(),
                16,
                0.35D, 0.15D, 0.35D,
                0.08D
        );
        playSound(ModSounds.FAULT_CRAWLER_PULSE.get(), 0.9F, 0.9F + random.nextFloat() * 0.2F);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean didHurt = super.doHurtTarget(target);
        if (didHurt) {
            triggerAnim("actionController", "slam");
            playSound(ModSounds.FAULT_CRAWLER_SLAM.get(), 0.95F, 0.9F + random.nextFloat() * 0.2F);
        }
        return didHurt;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);

        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, Blocks.COBBLESTONE.defaultBlockState()),
                    getX(), getY() + 0.25D, getZ(),
                    28,
                    0.4D, 0.2D, 0.4D,
                    0.10D
            );
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.FAULT_CRAWLER_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.FAULT_CRAWLER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.FAULT_CRAWLER_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockState) {
        playSound(ModSounds.FAULT_CRAWLER_STEP.get(), 0.12F, 0.9F + random.nextFloat() * 0.2F);
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, net.minecraft.world.level.block.state.BlockState state, BlockPos pos) {
        // Rocky mass: ignore fall damage.
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "moveController", 0, state -> {
            if (buried) {
                state.setAnimation(BURIED_LOOP);
                return PlayState.CONTINUE;
            }

            if (emergeTicks > 0) {
                state.setAnimation(EMERGE_ONCE);
                return PlayState.CONTINUE;
            }

            state.setAnimation(state.isMoving() ? WALK_LOOP : IDLE_LOOP);
            return PlayState.CONTINUE;
        }));

        controllers.add(new AnimationController<>(this, "actionController", 0, state -> PlayState.STOP)
                .triggerableAnim("slam", SLAM_ONCE)
                .triggerableAnim("pulse", PULSE_ONCE)
                .triggerableAnim("emerge", EMERGE_ONCE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
