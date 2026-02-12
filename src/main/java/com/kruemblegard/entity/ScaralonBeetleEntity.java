package com.kruemblegard.entity;

import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.registry.ModEntities;
import com.kruemblegard.registry.ModItems;
import com.kruemblegard.block.ScaralonEggBlock;
import net.minecraft.world.level.block.Block;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.UUID;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Scaralon Beetle
 * - Horse-style taming + saddling + riding
 * - Flight is controlled via Space (takeoff/rise) + X (descend) with server-synced inputs.
 * - Babies shed Elytra Scutes on maturation (renewable progression loop without killing mounts).
 */
public class ScaralonBeetleEntity extends AbstractHorse implements GeoEntity {

    private static final String NBT_HAS_SHED_SCUTES = "HasShedAdultScutes";
    private static final String NBT_SHEAR_COOLDOWN = "ShearCooldown";
    private static final String NBT_FLIGHT_STAMINA = "FlightStamina";
    private static final String NBT_TEXTURE_VARIANT = "TextureVariant";
    private static final String NBT_HAS_EGGS = "HasEggsToLay";
    private static final String NBT_EGG_COUNT = "EggLayCount";
    private static final String NBT_EGG_LAY_POS = "EggLayPos";
    private static final String NBT_EGG_LAY_TIMEOUT = "EggLayTimeout";
    private static final String NBT_EGG_LAY_VARIANT = "EggLayVariant";

    private static final int TEXTURE_VARIANT_MIN = 1;
    private static final int TEXTURE_VARIANT_MAX = 8;

    private static final float BABY_TEXTURE_MUTATION_CHANCE = 0.03F;

    // Flight stamina is measured in ticks. Max stamina is intentionally modest so flight feels meaningful.
    // When stamina is depleted, the beetle can no longer gain altitude and will flutter down slowly.
    private static final int MAX_FLIGHT_STAMINA_TICKS = 20 * 10; // 10 seconds at full stamina
    private static final int STAMINA_REGEN_PER_TICK_GROUNDED = 3;
    private static final int STAMINA_DRAIN_PER_TICK_FLYING = 1;

    private static final int DISMOUNT_FOLLOW_TICKS = 20 * 4;
    private static final int DISMOUNT_SLOW_FALL_TICKS = 20 * 5;

    private static final double EXHAUSTED_AUTO_LAND_DESCENT_VELOCITY = -1.15D;
    private static final double MAX_EXHAUSTED_DOWNWARD_VELOCITY = -1.25D;
    private static final double MAX_UPWARD_VELOCITY = 0.55D;
    private static final double MAX_DOWNWARD_VELOCITY = -0.55D;

    private static final int FLIGHT_DOUBLE_TAP_WINDOW_TICKS = 7;
    private static final int FLIGHT_TOGGLE_GROUND_GRACE_TICKS = 10;

    private static final double GLIDE_SINK_VELOCITY = -0.03D;

        private static final UUID GROUND_SPRINT_MODIFIER_ID = UUID.fromString("b65a5be6-9e7a-49ba-b2ea-3b518b09a2f0");
        private static final AttributeModifier GROUND_SPRINT_SPEED_MODIFIER = new AttributeModifier(
            GROUND_SPRINT_MODIFIER_ID,
            "Scaralon sprint speed",
            0.35D,
            AttributeModifier.Operation.MULTIPLY_TOTAL
        );

    private static final EntityDataAccessor<Boolean> FLYING =
            SynchedEntityData.defineId(ScaralonBeetleEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> FLIGHT_STAMINA =
            SynchedEntityData.defineId(ScaralonBeetleEntity.class, EntityDataSerializers.INT);

        private static final EntityDataAccessor<Boolean> JUMP_CHARGING =
            SynchedEntityData.defineId(ScaralonBeetleEntity.class, EntityDataSerializers.BOOLEAN);

        /** 0..90ish (vanilla horse jumpPower scale). */
        private static final EntityDataAccessor<Integer> JUMP_CHARGE_POWER =
            SynchedEntityData.defineId(ScaralonBeetleEntity.class, EntityDataSerializers.INT);

    /** 1..8, assigned once on spawn and persisted. */
    private static final EntityDataAccessor<Integer> TEXTURE_VARIANT =
            SynchedEntityData.defineId(ScaralonBeetleEntity.class, EntityDataSerializers.INT);

    private static final RawAnimation IDLE_LOOP = RawAnimation.begin().thenLoop("animation.scaralon_beetle.idle");
    private static final RawAnimation WALK_LOOP = RawAnimation.begin().thenLoop("animation.scaralon_beetle.walk");
    private static final RawAnimation RUN_LOOP = RawAnimation.begin().thenLoop("animation.scaralon_beetle.run");
    private static final RawAnimation JUMP_LOOP = RawAnimation.begin().thenLoop("animation.scaralon_beetle.jump");
    private static final RawAnimation REAR_LOOP = RawAnimation.begin().thenLoop("animation.scaralon_beetle.rear");
    private static final RawAnimation FLY_LOOP = RawAnimation.begin().thenLoop("animation.scaralon_beetle.fly");
    private static final RawAnimation HOVER_LOOP = RawAnimation.begin().thenLoop("animation.scaralon_beetle.hover");
    private static final RawAnimation GLIDE_LOOP = RawAnimation.begin().thenLoop("animation.scaralon_beetle.glide");
    private static final RawAnimation LOVE_LOOP = RawAnimation.begin().thenLoop("animation.scaralon_beetle.love");

    private static final RawAnimation TAKEOFF_ONCE = RawAnimation.begin().thenPlay("animation.scaralon_beetle.takeoff");
    private static final RawAnimation LAND_ONCE = RawAnimation.begin().thenPlay("animation.scaralon_beetle.land");
    private static final RawAnimation HURT_ONCE = RawAnimation.begin().thenPlay("animation.scaralon_beetle.hurt");
    private static final RawAnimation DEATH_ONCE = RawAnimation.begin().thenPlay("animation.scaralon_beetle.death");
    private static final RawAnimation EAT_ONCE = RawAnimation.begin().thenPlay("animation.scaralon_beetle.eat");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private boolean wasBabyLastTick = false;
    private boolean hasShedAdultScutes = false;
    private int shearCooldownTicks = 0;

    private boolean serverAscendHeld = false;
    private boolean serverDescendHeld = false;
    private boolean serverSprintHeld = false;
    private boolean pendingFlightHover = false;
    private boolean pendingFlightHoverLeftGround = false;
    private int pendingFlightHoverTicks = 0;

    private int takeoffAssistAscendTicks = 0;

    private int lastOnGroundTick = 0;
    private @Nullable BlockPos rescueLandingPos = null;
    private int rescueRepathTicks = 0;

    private int flightToggleGraceTicks = 0;
    private int lastGroundJumpTapTick = -9999;

    private @Nullable UUID lastDismountedPlayerId = null;
    private int dismountFollowTicks = 0;

    private boolean playedDeathAnim = false;


    private boolean hasEggsToLay = false;
    private int eggLayCount = 0;
    private @Nullable BlockPos eggLayPos = null;
    private int eggLayTimeoutTicks = 0;
    private int eggLayVariant = TEXTURE_VARIANT_MIN;

    private boolean shouldPlayAirborneFlyAnim() {
        if (isFlying() && !onGround()) {
            return true;
        }

        // Wayfall flavor: unmounted "air swimmers" use no-gravity without actually being in mount-flight mode.
        return !onGround()
                && !isInWaterOrBubble()
                && !isVehicle()
                && isNoGravity();
    }

    public ScaralonBeetleEntity(EntityType<? extends AbstractHorse> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 26.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.26D)
                .add(Attributes.JUMP_STRENGTH, 0.80D)
                .add(Attributes.FLYING_SPEED, 0.045D)
                // Big tough beetle: durable baseline.
                .add(Attributes.ARMOR, 12.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 6.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLYING, false);
        this.entityData.define(FLIGHT_STAMINA, MAX_FLIGHT_STAMINA_TICKS);
        this.entityData.define(JUMP_CHARGING, false);
        this.entityData.define(JUMP_CHARGE_POWER, 0);
        this.entityData.define(TEXTURE_VARIANT, TEXTURE_VARIANT_MIN);
    }

    public int getTextureVariant() {
        return Mth.clamp(this.entityData.get(TEXTURE_VARIANT), TEXTURE_VARIANT_MIN, TEXTURE_VARIANT_MAX);
    }

    public void setTextureVariant(int variant) {
        this.entityData.set(TEXTURE_VARIANT, Mth.clamp(variant, TEXTURE_VARIANT_MIN, TEXTURE_VARIANT_MAX));
    }

    private void randomizeTextureVariant() {
        int range = TEXTURE_VARIANT_MAX - TEXTURE_VARIANT_MIN + 1;
        setTextureVariant(TEXTURE_VARIANT_MIN + this.random.nextInt(range));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor level,
            DifficultyInstance difficulty,
            MobSpawnType spawnType,
            @Nullable SpawnGroupData spawnGroupData,
            @Nullable CompoundTag dataTag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData, dataTag);
        randomizeTextureVariant();
        return data;
    }

    public boolean isJumpCharging() {
        return this.entityData.get(JUMP_CHARGING);
    }

    public int getJumpChargePower() {
        return this.entityData.get(JUMP_CHARGE_POWER);
    }

    private void setJumpCharging(boolean charging, int jumpPower) {
        this.entityData.set(JUMP_CHARGING, charging);
        this.entityData.set(JUMP_CHARGE_POWER, Mth.clamp(jumpPower, 0, 90));
    }

    private int getFlightStamina() {
        return this.entityData.get(FLIGHT_STAMINA);
    }

    /** Client HUD helper (synced via entity data). */
    public int getFlightStaminaTicks() {
        return getFlightStamina();
    }

    /** Client HUD helper. */
    public int getMaxFlightStaminaTicks() {
        return MAX_FLIGHT_STAMINA_TICKS;
    }

    private void setFlightStamina(int value) {
        this.entityData.set(FLIGHT_STAMINA, Mth.clamp(value, 0, MAX_FLIGHT_STAMINA_TICKS));
    }

    private boolean isFlightExhausted() {
        return getFlightStamina() <= 0;
    }

    public boolean isFlying() {
        return this.entityData.get(FLYING);
    }

    private void setFlying(boolean flying) {
        boolean wasFlying = isFlying();
        this.entityData.set(FLYING, flying);
        this.setNoGravity(flying);
        if (flying) {
            this.fallDistance = 0.0F;
            if (!level().isClientSide) {
                setJumpCharging(false, 0);
            }
        }

        if (!level().isClientSide && flying != wasFlying) {
            triggerAnim("actionController", flying ? "takeoff" : "land");
        }
    }

    public void setFlightInputs(boolean ascendHeld, boolean descendHeld, boolean sprintHeld) {
        if (level().isClientSide) {
            return;
        }

        this.serverAscendHeld = ascendHeld;
        this.serverDescendHeld = descendHeld;
        this.serverSprintHeld = sprintHeld;
    }

    private void updateGroundSprintSpeedModifier() {
        if (level().isClientSide) {
            return;
        }

        var speed = getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) {
            return;
        }

        boolean shouldSprint = false;
        if (!isFlying()
                && isVehicle()
                && isSaddled()
                && isTamed()
                && getControllingPassenger() instanceof Player rider) {
            shouldSprint = serverSprintHeld || rider.isSprinting();
        }

        if (shouldSprint) {
            if (speed.getModifier(GROUND_SPRINT_MODIFIER_ID) == null) {
                speed.addTransientModifier(GROUND_SPRINT_SPEED_MODIFIER);
            }
        } else {
            if (speed.getModifier(GROUND_SPRINT_MODIFIER_ID) != null) {
                speed.removeModifier(GROUND_SPRINT_MODIFIER_ID);
            }
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // Skittish when wild.
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Player.class, 10.0F, 1.25D, 1.45D, p -> !isTamed()));

        // Attracted to rune-ish items (helps with taming + moving them around).
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.15D,
            Ingredient.of(
                ModItems.SOULBERRIES.get(),
                Items.MELON_SLICE,
                ModItems.ATTUNED_RUNE_SHARD.get(),
                ModItems.RUNIC_CORE.get()
            ),
            false));

        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            if (onGround()) {
                lastOnGroundTick = tickCount;
            }

            if (flightToggleGraceTicks > 0) {
                flightToggleGraceTicks--;
            }

            // Reset if rider state becomes invalid.
            if (!isVehicle() || !(getControllingPassenger() instanceof Player)) {
                serverAscendHeld = false;
                serverDescendHeld = false;
                serverSprintHeld = false;
                pendingFlightHover = false;
                pendingFlightHoverLeftGround = false;
                pendingFlightHoverTicks = 0;
                flightToggleGraceTicks = 0;
            }

            updateGroundSprintSpeedModifier();

            if (shearCooldownTicks > 0) {
                shearCooldownTicks--;
            }

            // Babies shed Elytra Scutes when they mature.
            boolean isBabyNow = isBaby();
            if (wasBabyLastTick && !isBabyNow && !hasShedAdultScutes) {
                shedElytraScutes();
                hasShedAdultScutes = true;
            }
            wasBabyLastTick = isBabyNow;

            // Flight should auto-exit back to ground mode the moment we touch solid ground.
            // Exception: don't force-land while wet (shallow water can report onGround=true).
            if (isFlying() && onGround() && !isInWaterOrBubble() && flightToggleGraceTicks <= 0) {
                setFlying(false);
                pendingFlightHover = false;
                pendingFlightHoverLeftGround = false;
                pendingFlightHoverTicks = 0;
            }

            // Safety: if the mount leaves the ground unexpectedly (cliff, knockback) or enters water,
            // engage flight mode so it doesn't hard-fall. (If stamina is empty, flight will still
            // flutter-down slowly instead of freefalling.)
            if (!isFlying()
                    && !pendingFlightHover
                    && isVehicle()
                    && isSaddled()
                    && isTamed()
                    && getControllingPassenger() instanceof Player
                    && !onGround()) {

                Vec3 v = getDeltaMovement();
                boolean bigFall = fallDistance >= 10.0F;
                boolean fallingFast = v.y < -0.14D;
                boolean falling = fallingFast || bigFall;
                boolean wet = isInWaterOrBubble();

                // Safety only: do NOT auto-engage flight just because the jump key is held,
                // otherwise normal horse jumping gets hijacked into flight.
                if (falling || wet) {
                    setFlying(true);
                    flightToggleGraceTicks = Math.max(flightToggleGraceTicks, FLIGHT_TOGGLE_GROUND_GRACE_TICKS);
                    takeoffAssistAscendTicks = 6;
                    serverAscendHeld = true;

                    // Kill extreme downward velocity so it feels like a recovery.
                    setDeltaMovement(v.x, Math.max(v.y, -0.10D), v.z);
                    hasImpulse = true;
                }
            }

            // If we launched via charged jump, switch into flight-mode hover near the apex.
            // We keep gravity during the initial launch so it feels like a jump, then quickly enter powered flight.
            if (pendingFlightHover) {
                pendingFlightHoverTicks++;

                // NOTE: when the jump-release packet arrives, we may still be marked onGround
                // for 1-2 ticks. Don't cancel pending hover until we've actually left the ground.
                if (!pendingFlightHoverLeftGround) {
                    if (!onGround()) {
                        pendingFlightHoverLeftGround = true;
                    }
                } else {
                    if (onGround()) {
                        pendingFlightHover = false;
                        pendingFlightHoverLeftGround = false;
                        pendingFlightHoverTicks = 0;
                    } else {
                        // After we are firmly airborne, engage powered flight immediately.
                        if (pendingFlightHoverTicks >= 2) {
                            pendingFlightHover = false;
                            pendingFlightHoverLeftGround = false;
                            pendingFlightHoverTicks = 0;

                            setFlying(true);
                            flightToggleGraceTicks = Math.max(flightToggleGraceTicks, FLIGHT_TOGGLE_GROUND_GRACE_TICKS);
                            takeoffAssistAscendTicks = 6;
                            fallDistance = 0.0F;
                        }
                    }
                }
            }

            if (takeoffAssistAscendTicks > 0) {
                takeoffAssistAscendTicks--;
                if (isFlying()
                        && !serverDescendHeld
                        && isVehicle()
                        && isSaddled()
                        && isTamed()
                        && getControllingPassenger() instanceof Player) {
                    serverAscendHeld = true;
                }
            }

            // Flight stamina:
            // - drains while actively flying (airborne)
            // - regenerates while grounded
            if (onGround()) {
                if (getFlightStamina() < MAX_FLIGHT_STAMINA_TICKS) {
                    setFlightStamina(getFlightStamina() + STAMINA_REGEN_PER_TICK_GROUNDED);
                }
            } else if (isFlying() && isVehicle() && getControllingPassenger() instanceof Player) {
                if (getFlightStamina() > 0) {
                    setFlightStamina(getFlightStamina() - STAMINA_DRAIN_PER_TICK_FLYING);
                }
            }

            // Post-dismount safety: hover near the player briefly (useful if they jump/fall off mid-flight).
            if (dismountFollowTicks > 0 && lastDismountedPlayerId != null && !isVehicle()) {
                dismountFollowTicks--;

                if (level() instanceof ServerLevel serverLevel) {
                    Player p = serverLevel.getPlayerByUUID(lastDismountedPlayerId);
                    if (p == null || !p.isAlive()) {
                        dismountFollowTicks = 0;
                        lastDismountedPlayerId = null;
                    } else {
                        setFlying(true);

                        Vec3 target = p.position().add(0.0D, 1.25D, 0.0D);
                        Vec3 to = target.subtract(position());
                        double distSq = to.lengthSqr();

                        Vec3 desired;
                        if (distSq < 2.0D * 2.0D) {
                            desired = new Vec3(0.0D, 0.0D, 0.0D);
                        } else {
                            desired = to.normalize().scale(0.22D);
                        }

                        Vec3 v = getDeltaMovement();
                        Vec3 steered = v.add(desired.subtract(v).scale(0.28D)).scale(0.96D);
                        steered = new Vec3(steered.x, Mth.clamp(steered.y, -0.12D, 0.18D), steered.z);

                        setDeltaMovement(steered);
                        move(MoverType.SELF, steered);
                        hasImpulse = true;
                        fallDistance = 0.0F;
                    }
                }
            }

            // Unmounted safety: prevent drowning and "void loss" by engaging self-rescue.
            // In Wayfall this is especially important due to large open-air drops.
            if (!isVehicle()) {
                tickUnmountedSelfRescue();
            }

            // Egg laying (turtle-style): after breeding, one parent will seek a nearby reachable spot
            // and place Scaralon eggs which hatch into larva.
            if (hasEggsToLay && eggLayCount > 0) {
                if (eggLayTimeoutTicks > 0) {
                    eggLayTimeoutTicks--;
                }

                if (eggLayPos == null) {
                    eggLayPos = findNearbyEggLayPos((ServerLevel) level(), blockPosition());
                }

                BlockPos targetPos = eggLayPos;
                if (targetPos == null) {
                    targetPos = blockPosition().above();
                }

                // Try to walk to the lay position; if we can't, we'll eventually give up and lay nearby.
                if (this.getNavigation().isDone() && eggLayTimeoutTicks > 0) {
                    this.getNavigation().moveTo(targetPos.getX() + 0.5D, targetPos.getY(), targetPos.getZ() + 0.5D, 1.10D);
                }

                double distSq = this.position().distanceToSqr(Vec3.atCenterOf(targetPos));
                boolean timedOut = eggLayTimeoutTicks <= 0;
                boolean closeEnough = distSq <= 2.25D;

                if ((closeEnough && this.onGround()) || timedOut) {
                    BlockPos layAt = closeEnough ? targetPos : blockPosition().above();

                    // Ensure we place on air above a solid-ish block.
                    if (!level().getBlockState(layAt).isAir()) {
                        layAt = blockPosition().above();
                    }

                    BlockPos below = layAt.below();
                    if (level().getBlockState(below).isAir()) {
                        // Find something nearby solid to lay on.
                        BlockPos alt = findNearbyEggLayPos((ServerLevel) level(), blockPosition());
                        if (alt != null) {
                            layAt = alt;
                            below = layAt.below();
                        }
                    }

                    if (level().getBlockState(layAt).isAir() && !level().getBlockState(below).isAir()) {
                        int eggs = Mth.clamp(eggLayCount, 1, 4);
                        level().setBlock(
                                layAt,
                                ModBlocks.SCARALON_EGG.get().defaultBlockState()
                                        .setValue(ScaralonEggBlock.EGGS, eggs)
                                .setValue(ScaralonEggBlock.HATCH, 0)
                                .setValue(ScaralonEggBlock.VARIANT, Mth.clamp(eggLayVariant, TEXTURE_VARIANT_MIN, TEXTURE_VARIANT_MAX)),
                                Block.UPDATE_CLIENTS);
                        playSound(SoundEvents.TURTLE_LAY_EGG, 0.9F, 0.9F + random.nextFloat() * 0.2F);
                    }

                    hasEggsToLay = false;
                    eggLayCount = 0;
                    eggLayPos = null;
                    eggLayTimeoutTicks = 0;
                    eggLayVariant = TEXTURE_VARIANT_MIN;
                }
            }

            // Soft harmonic hum near arcane blocks.
            if (random.nextInt(90) == 0 && isNearArcaneEnergy()) {
                playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 0.35F, 0.8F + random.nextFloat() * 0.25F);
            }
        }
    }

    private void tickUnmountedSelfRescue() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (onGround()) {
            rescueLandingPos = null;
            rescueRepathTicks = 0;
            // If we were in a rescue state, allow normal gravity/ground behavior again.
            if (isFlying() && !isInWaterOrBubble()) {
                setFlying(false);
            }
            return;
        }

        boolean wet = isInWaterOrBubble();
        boolean overVoid = isOverMostlyAir(serverLevel, 18);
        boolean fallingDeep = fallDistance >= 10.0F;
        boolean needsRescue = wet || overVoid || fallingDeep;

        if (!needsRescue) {
            rescueLandingPos = null;
            rescueRepathTicks = 0;
            return;
        }

        setFlying(true);
        fallDistance = 0.0F;
        setAirSupply(getMaxAirSupply());

        if (rescueRepathTicks > 0) {
            rescueRepathTicks--;
        }

        if (rescueLandingPos == null || rescueRepathTicks <= 0 || !serverLevel.isLoaded(rescueLandingPos)) {
            rescueLandingPos = findRescueLandingPos(serverLevel);
            rescueRepathTicks = 20;
        }

        // If we failed to find a surface, at least climb to reduce risk.
        Vec3 target;
        if (rescueLandingPos != null) {
            target = Vec3.atCenterOf(rescueLandingPos).add(0.0D, 0.35D, 0.0D);
        } else {
            target = position().add(0.0D, 6.0D, 0.0D);
        }

        Vec3 to = target.subtract(position());
        double distSq = to.lengthSqr();

        // When close to the landing target and there's solid ground under us, drop out of flight.
        if (rescueLandingPos != null && distSq < 2.5D * 2.5D) {
            BlockPos below = rescueLandingPos.below();
            if (!serverLevel.getBlockState(below).isAir() && serverLevel.getBlockState(rescueLandingPos).isAir()) {
                setFlying(false);
                setNoGravity(false);
                rescueLandingPos = null;
                rescueRepathTicks = 0;
                setDeltaMovement(getDeltaMovement().multiply(0.25D, 0.0D, 0.25D));
                hasImpulse = true;
                return;
            }
        }

        Vec3 desired;
        if (distSq < 1.75D * 1.75D) {
            desired = new Vec3(0.0D, wet ? 0.12D : 0.0D, 0.0D);
        } else {
            desired = to.normalize().scale(0.24D);
            desired = new Vec3(desired.x, desired.y + 0.10D, desired.z);
        }

        Vec3 v = getDeltaMovement();
        Vec3 steered = v.add(desired.subtract(v).scale(0.25D)).scale(0.96D);
        steered = new Vec3(steered.x, Mth.clamp(steered.y, -0.12D, 0.28D), steered.z);

        setDeltaMovement(steered);
        move(MoverType.SELF, steered);
        hasImpulse = true;
    }

    private boolean isOverMostlyAir(ServerLevel level, int depth) {
        if (getY() <= level.getMinBuildHeight() + 2) {
            return true;
        }

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos(blockPosition().getX(), blockPosition().getY(), blockPosition().getZ());
        for (int i = 1; i <= depth; i++) {
            cursor.setY(blockPosition().getY() - i);
            if (!level.isLoaded(cursor)) {
                return false;
            }
            if (!level.getBlockState(cursor).isAir()) {
                return false;
            }
        }
        return true;
    }

    private @Nullable BlockPos findRescueLandingPos(ServerLevel level) {
        BlockPos origin = blockPosition();

        BlockPos best = null;
        double bestScore = -1.0E18;

        // Search for nearby surface columns using a heightmap (fast + reliable for islands).
        for (int r = 4; r <= 28; r += 4) {
            for (int i = 0; i < 10; i++) {
                int dx = Mth.nextInt(random, -r, r);
                int dz = Mth.nextInt(random, -r, r);

                BlockPos col = new BlockPos(origin.getX() + dx, origin.getY(), origin.getZ() + dz);
                if (!level.isLoaded(col)) {
                    continue;
                }

                BlockPos surface = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, col);
                if (surface.getY() <= level.getMinBuildHeight() + 1) {
                    continue;
                }

                BlockPos landAt = surface.above();
                if (!level.isLoaded(landAt)) {
                    continue;
                }
                if (!level.getBlockState(landAt).isAir()) {
                    continue;
                }

                Vec3 target = Vec3.atCenterOf(landAt);
                double distSq = target.distanceToSqr(position());

                // Prefer higher ground and closer targets.
                double score = surface.getY() * 3.0D - distSq * 0.015D;
                if (score > bestScore) {
                    bestScore = score;
                    best = landAt;
                }
            }
        }

        return best;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // Immune to thorny / berry-bush style block collision damage (vanilla + most modded plants).
        // Mods commonly reuse vanilla damage types (sweet_berry_bush / cactus) for brambles.
        // Some use custom ids; we also guard common msgId patterns.
        if (isBerryBushTypeDamage(source)) {
            return false;
        }

        boolean didHurt = super.hurt(source, amount);
        if (didHurt && !level().isClientSide && isAlive()) {
            triggerAnim("actionController", "hurt");
        }
        return didHurt;
    }

    private boolean isBerryBushTypeDamage(DamageSource source) {
        if (source == null) {
            return false;
        }

        if (source.is(DamageTypes.SWEET_BERRY_BUSH) || source.is(DamageTypes.CACTUS)) {
            return true;
        }

        String id = source.getMsgId();
        if (id == null || id.isBlank()) {
            return false;
        }

        String msg = id.toLowerCase(Locale.ROOT);
        return msg.equals("sweetberrybush")
                || msg.equals("sweet_berry_bush")
                || msg.contains("berry_bush")
                || msg.contains("sweetberry")
                || msg.contains("sweet_berry")
                || msg.equals("cactus")
                || msg.contains("thorn")
                || msg.contains("bramble");
    }

    @Override
    public void die(DamageSource source) {
        if (!level().isClientSide && !playedDeathAnim) {
            playedDeathAnim = true;
            triggerAnim("actionController", "death");
        }
        super.die(source);
    }

    private boolean isNearArcaneEnergy() {
        BlockPos center = blockPosition();
        int r = 4;

        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    if (level().getBlockState(pos).is(ModBlocks.ANCIENT_WAYSTONE.get())
                            || level().getBlockState(pos).is(ModBlocks.ATTUNED_STONE.get())
                            || level().getBlockState(pos).is(ModBlocks.RUNEBLOOM.get())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void shedElytraScutes() {
        if (!(level() instanceof ServerLevel)) {
            return;
        }

        int count = 1 + random.nextInt(2);
        if (random.nextFloat() < 0.15F) {
            count++;
        }

        for (int i = 0; i < count; i++) {
            spawnAtLocation(new ItemStack(ModItems.ELYTRA_SCUTE.get()), 1.0F);
        }

        playSound(SoundEvents.ARMOR_EQUIP_ELYTRA, 0.7F, 1.0F + random.nextFloat() * 0.15F);
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (!isAlive()) {
            super.travel(travelVector);
            return;
        }

        if (isVehicle() && isSaddled() && isTamed() && getControllingPassenger() instanceof Player player) {
            if (isFlying()) {
                setYRot(player.getYRot());
                yRotO = getYRot();
                setXRot(player.getXRot() * 0.5F);
                setRot(getYRot(), getXRot());
                yBodyRot = getYRot();
                yHeadRot = getYRot();

                float strafe = player.xxa;
                float forward = player.zza;

                // True 3D mount flight (server-authoritative):
                // - WASD steers horizontally
                // - Space ascends, X descends
                // - No input hovers (doesn't slowly sink) while stamina remains
                // - When stamina is empty: cannot ascend; slowly flutters down until grounded

                boolean exhausted = isFlightExhausted();

                // Out of stamina: force an auto-land.
                // The rider loses precise control and the beetle rapidly descends straight down.
                if (exhausted) {
                    serverAscendHeld = false;
                    serverDescendHeld = false;
                    takeoffAssistAscendTicks = 0;

                    Vec3 v = getDeltaMovement();
                    Vec3 forced = new Vec3(v.x * 0.25D, EXHAUSTED_AUTO_LAND_DESCENT_VELOCITY, v.z * 0.25D);
                    setDeltaMovement(forced);
                    move(MoverType.SELF, forced);

                    Vec3 after = getDeltaMovement();
                    double y = Mth.clamp(after.y, MAX_EXHAUSTED_DOWNWARD_VELOCITY, 0.0D);
                    setDeltaMovement(0.0D, y, 0.0D);
                    hasImpulse = true;
                    fallDistance = 0.0F;
                    return;
                }

                // Powered mount flight (inspired by Ice & Fire): apply rider travel vector each tick,
                // then damp motion with drag so it feels responsive and "powered" instead of swimmy.
                float baseFlySpeed = (float) getAttributeValue(Attributes.FLYING_SPEED);
                float speed = baseFlySpeed * 7.5F;
                if (serverSprintHeld || player.isSprinting()) {
                    speed *= 1.20F;
                }

                // Make strafing/backpedaling a bit slower for stability.
                double forwardScaled = forward * (forward > 0.0F ? 1.0D : 0.55D);
                double strafeScaled = strafe * 0.60D;

                boolean ascend = serverAscendHeld && !serverDescendHeld;
                boolean descend = serverDescendHeld && !serverAscendHeld;

                double vertical;
                if (ascend) {
                    vertical = 0.85D;
                } else if (descend) {
                    vertical = -0.85D;
                } else {
                    // Mouse pitch control: while moving forward, looking up climbs and looking down dives.
                    float pitchRad = player.getXRot() * ((float) Math.PI / 180.0F);
                    double pitchVertical = -Math.sin(pitchRad) * Math.abs(forwardScaled) * 0.85D;
                    vertical = pitchVertical;

                    // Without intent, very gently sink (prevents unnatural hovering while idle).
                    if (Math.abs(forwardScaled) < 0.01D && Math.abs(strafeScaled) < 0.01D) {
                        vertical = GLIDE_SINK_VELOCITY;
                    }
                }

                this.setSpeed(speed);
                this.moveRelative(speed, new Vec3(strafeScaled, vertical, forwardScaled));
                this.move(MoverType.SELF, this.getDeltaMovement());

                Vec3 motion = this.getDeltaMovement();
                if (this.horizontalCollision) {
                    motion = new Vec3(motion.x, Math.max(motion.y, 0.20D), motion.z);
                }

                double clampedY = Mth.clamp(motion.y, MAX_DOWNWARD_VELOCITY, MAX_UPWARD_VELOCITY);
                double drag = 0.72D;
                this.setDeltaMovement(motion.x * drag, clampedY * 0.88D, motion.z * drag);
                this.hasImpulse = true;
                this.fallDistance = 0.0F;
                return;
            }

            // Ground mode: keep vanilla AbstractHorse movement/jump behavior.
            super.travel(travelVector);
            return;
        }

        super.travel(travelVector);
    }

    /**
     * Vanilla horse jump is already a charged jump (hold, release).
     * We preserve that on the ground, but for a sufficiently charged jump we convert it
     * into a "takeoff" that launches upward and then transitions into flight-mode hover.
     */
    @Override
    public void onPlayerJump(int jumpPower) {
        if (level().isClientSide) {
            super.onPlayerJump(jumpPower);
            return;
        }

        // Ensure any charge/rear state clears on release.
        setJumpCharging(false, 0);

        // Only apply special takeoff logic when ridden/saddled/tamed and not already flying.
        if (!(isVehicle() && isSaddled() && isTamed() && getControllingPassenger() instanceof Player)) {
            super.onPlayerJump(jumpPower);
            return;
        }

        if (isFlying()) {
            // In flight mode, Space is handled as ascend input; don't translate it into horse jumping.
            return;
        }

        // Require some charge + some stamina to take off.
        // Horse jumpPower typically ranges 0..90.
        final int takeoffThreshold = 55;
        if (jumpPower < takeoffThreshold || isFlightExhausted()) {
            super.onPlayerJump(jumpPower);
            return;
        }

        double t = Mth.clamp((jumpPower - takeoffThreshold) / (90.0D - takeoffThreshold), 0.0D, 1.0D);
        // Tuned to reach roughly 7-12 blocks of height before transitioning to hover.
        double launchY = Mth.lerp(t, 0.95D, 1.25D);

        // Apply upward impulse, keep gravity during the ascent, then engage hover-flight at the top.
        setNoGravity(false);
        this.entityData.set(FLYING, false);

        Vec3 v = getDeltaMovement();
        setDeltaMovement(v.x, launchY, v.z);
        hasImpulse = true;

        pendingFlightHover = true;
        pendingFlightHoverLeftGround = false;
        pendingFlightHoverTicks = 0;
        fallDistance = 0.0F;
    }

    @Override
    public void handleStartJump(int jumpPower) {
        if (level().isClientSide) {
            super.handleStartJump(jumpPower);
            return;
        }

        // Double-tap Space toggles flight mode.
        // Works as a true "double jump": if you tap again shortly after jumping, it instantly enters flight.
        if (!isFlying()
                && !pendingFlightHover
                && isVehicle()
                && isSaddled()
                && isTamed()
                && getControllingPassenger() instanceof Player) {

            int now = this.tickCount;
            boolean recentlyGrounded = onGround() || (now - lastOnGroundTick <= 7);
            if (now - lastGroundJumpTapTick <= FLIGHT_DOUBLE_TAP_WINDOW_TICKS) {
                setJumpCharging(false, 0);
                setFlying(true);
                flightToggleGraceTicks = FLIGHT_TOGGLE_GROUND_GRACE_TICKS;
                serverAscendHeld = true;
                takeoffAssistAscendTicks = 4;

                Vec3 v = getDeltaMovement();
                setDeltaMovement(v.x, Math.max(v.y, 0.42D), v.z);
                hasImpulse = true;
                fallDistance = 0.0F;

                lastGroundJumpTapTick = -9999;
                return;
            }

            // Only arm the double-tap window if we are on/near the ground (prevents accidental flight arming mid-air).
            if (recentlyGrounded) {
                lastGroundJumpTapTick = now;
            }
        }

        // While flying, repurpose the vanilla mount-jump "start" packet as ascend-held.
        // This is more reliable than client key polling because it is already synchronized
        // for mounted entities.
        if (isFlying() && isVehicle() && isSaddled() && isTamed() && getControllingPassenger() instanceof Player) {
            serverAscendHeld = true;
            return;
        }

        super.handleStartJump(jumpPower);

        // Horse-like rear while charging a ground jump. Never rear in flight.
        if (isFlying() || pendingFlightHover || !onGround()) {
            return;
        }

        if (!(isVehicle() && isSaddled() && isTamed() && getControllingPassenger() instanceof Player)) {
            return;
        }

        setJumpCharging(true, jumpPower);
    }

    @Override
    public void handleStopJump() {
        if (level().isClientSide) {
            super.handleStopJump();
            return;
        }

        // While flying, repurpose the vanilla mount-jump "stop" packet as ascend-release.
        if (isFlying() && isVehicle() && isSaddled() && isTamed() && getControllingPassenger() instanceof Player) {
            serverAscendHeld = false;
            return;
        }

        super.handleStopJump();

        setJumpCharging(false, 0);
    }

    @Override
    public void removePassenger(net.minecraft.world.entity.Entity passenger) {
        boolean wasInAir = !onGround() || isFlying() || pendingFlightHover;

        super.removePassenger(passenger);

        if (level().isClientSide) {
            return;
        }

        if (passenger instanceof ServerPlayer sp) {
            // If the player jumps/falls off, immediately restore stamina.
            setFlightStamina(MAX_FLIGHT_STAMINA_TICKS);

            if (wasInAir) {
                // Prevent fall damage and keep the beetle nearby.
                sp.fallDistance = 0.0F;
                sp.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, DISMOUNT_SLOW_FALL_TICKS, 0, true, false, true));

                lastDismountedPlayerId = sp.getUUID();
                dismountFollowTicks = DISMOUNT_FOLLOW_TICKS;
                setFlying(true);
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Melon slices are the Scaralon's breeding item. If the player is holding a melon slice,
        // always treat the interaction as feeding/breeding (never mounting).
        if (stack.is(Items.MELON_SLICE)) {
            if (level().isClientSide) {
                return InteractionResult.SUCCESS;
            }

            boolean didSomething = false;

            if (!isBaby() && canFallInLove()) {
                setInLove(player);
                didSomething = true;
            } else if (getHealth() < getMaxHealth()) {
                heal(2.0F);
                didSomething = true;
            } else if (isBaby()) {
                ageUp(20, true);
                didSomething = true;
            }

            if (didSomething) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                triggerAnim("actionController", "eat");
            }

            return InteractionResult.CONSUME;
        }

        // Larva are bucketable.
        if (!level().isClientSide && isBaby() && stack.is(Items.BUCKET)) {
            ItemStack filled = new ItemStack(ModItems.SCARALON_LARVA_BUCKET.get());
            filled.getOrCreateTag().putInt(NBT_TEXTURE_VARIANT, getTextureVariant());

            playSound(SoundEvents.BUCKET_FILL_AXOLOTL, 1.0F, 1.0F);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            if (stack.isEmpty()) {
                player.setItemInHand(hand, filled);
            } else if (!player.getInventory().add(filled)) {
                player.drop(filled, false);
            }

            discard();
            return InteractionResult.CONSUME;
        }

        // Harvest: shear adults for plates without killing.
        if (!level().isClientSide
                && !isBaby()
                && shearCooldownTicks <= 0
                && stack.is(Items.SHEARS)) {

            int count = 1 + random.nextInt(2);
            if (random.nextFloat() < 0.10F) {
                count++;
            }

            for (int i = 0; i < count; i++) {
                spawnAtLocation(new ItemStack(ModItems.RUNE_ETCHED_CHITIN_PLATE.get()), 1.0F);
            }

            playSound(SoundEvents.SHEEP_SHEAR, 0.9F, 0.8F + random.nextFloat() * 0.25F);
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            shearCooldownTicks = 20 * (45 + random.nextInt(45));

            return InteractionResult.CONSUME;
        }

        boolean isFoodInteraction = isFood(stack);
        InteractionResult result = super.mobInteract(player, hand);

        if (!level().isClientSide && isFoodInteraction && result.consumesAction()) {
            triggerAnim("actionController", "eat");
        }

        return result;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        // Breeding: Soulberries. Also accept a couple rune items as "taming aids".
        return stack.is(ModItems.SOULBERRIES.get())
                || stack.is(Items.MELON_SLICE)
                || stack.is(ModItems.ATTUNED_RUNE_SHARD.get())
                || stack.is(ModItems.RUNIC_CORE.get());
    }

    @Override
    public boolean canMate(Animal other) {
        if (other == this) {
            return false;
        }

        if (!(other instanceof ScaralonBeetleEntity scaralon)) {
            return false;
        }

        return !this.isBaby() && !scaralon.isBaby() && this.isInLove() && scaralon.isInLove();
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel level, net.minecraft.world.entity.animal.Animal otherParent) {
        if (!(otherParent instanceof ScaralonBeetleEntity other)) {
            super.spawnChildFromBreeding(level, otherParent);
            return;
        }

        // Decide which parent lays eggs.
        ScaralonBeetleEntity layer = this.random.nextBoolean() ? this : other;

        // Decide which parent's adult texture variant the babies will inherit (50/50), with a small mutation chance.
        int inheritedVariant = this.random.nextBoolean() ? this.getTextureVariant() : other.getTextureVariant();
        if (this.random.nextFloat() < BABY_TEXTURE_MUTATION_CHANCE) {
            inheritedVariant = TEXTURE_VARIANT_MIN + this.random.nextInt(TEXTURE_VARIANT_MAX - TEXTURE_VARIANT_MIN + 1);
        }

        int eggs = Mth.nextInt(this.random, 1, 3);
        layer.hasEggsToLay = true;
        layer.eggLayCount = eggs;
        layer.eggLayPos = layer.findNearbyEggLayPos(level, layer.blockPosition());
        layer.eggLayTimeoutTicks = 20 * 18;
        layer.eggLayVariant = inheritedVariant;

        // Breeding cooldown + clear love state.
        this.setAge(6000);
        other.setAge(6000);
        this.resetLove();
        other.resetLove();

        // Vanilla-style XP reward.
        ExperienceOrb.award(level, this.position(), Mth.nextInt(this.random, 1, 7));
    }

    private @Nullable BlockPos findNearbyEggLayPos(ServerLevel level, BlockPos origin) {
        for (int i = 0; i < 24; i++) {
            int dx = Mth.nextInt(random, -7, 7);
            int dz = Mth.nextInt(random, -7, 7);
            int dy = Mth.nextInt(random, -2, 2);

            BlockPos base = origin.offset(dx, dy, dz);

            // We want an air block with a non-air block beneath.
            BlockPos eggPos = base;
            BlockPos below = eggPos.below();

            if (!level.isLoaded(eggPos)) {
                continue;
            }

            if (!level.getBlockState(eggPos).isAir()) {
                continue;
            }

            if (level.getBlockState(below).isAir()) {
                continue;
            }

            // Must be navigable/reachable-ish.
            var path = this.getNavigation().createPath(eggPos, 0);
            if (path == null || !path.canReach()) {
                continue;
            }

            return eggPos;
        }

        return null;
    }

    // --- Bucketable (larva only) ---


    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        ScaralonBeetleEntity baby = ModEntities.SCARALON_BEETLE.get().create(level);
        if (baby != null) {
            int variant = getTextureVariant();
            if (otherParent instanceof ScaralonBeetleEntity other) {
                variant = random.nextBoolean() ? getTextureVariant() : other.getTextureVariant();
            }

            if (random.nextFloat() < BABY_TEXTURE_MUTATION_CHANCE) {
                variant = TEXTURE_VARIANT_MIN + random.nextInt(TEXTURE_VARIANT_MAX - TEXTURE_VARIANT_MIN + 1);
            }
            baby.setTextureVariant(variant);

            // Basic stat variation: small RNG drift around the parents.
            double maxHealth = Mth.clamp((getMaxHealth() + ((ScaralonBeetleEntity) otherParent).getMaxHealth()) / 2.0D
                    + (random.nextDouble() * 4.0D - 2.0D), 18.0D, 34.0D);

            double moveSpeed = Mth.clamp((getAttributeBaseValue(Attributes.MOVEMENT_SPEED)
                    + ((ScaralonBeetleEntity) otherParent).getAttributeBaseValue(Attributes.MOVEMENT_SPEED)) / 2.0D
                    + (random.nextDouble() * 0.06D - 0.03D), 0.18D, 0.36D);

            double flightStrength = Mth.clamp((getAttributeBaseValue(Attributes.FLYING_SPEED)
                    + ((ScaralonBeetleEntity) otherParent).getAttributeBaseValue(Attributes.FLYING_SPEED)) / 2.0D
                    + (random.nextDouble() * 0.020D - 0.010D), 0.030D, 0.070D);

            baby.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHealth);
            baby.setHealth((float) maxHealth);
            baby.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(moveSpeed);
            baby.getAttribute(Attributes.FLYING_SPEED).setBaseValue(flightStrength);
        }

        return baby;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean(NBT_HAS_SHED_SCUTES, hasShedAdultScutes);
        tag.putInt(NBT_SHEAR_COOLDOWN, shearCooldownTicks);
        tag.putInt(NBT_FLIGHT_STAMINA, getFlightStamina());
        tag.putInt(NBT_TEXTURE_VARIANT, getTextureVariant());

        tag.putBoolean(NBT_HAS_EGGS, hasEggsToLay);
        tag.putInt(NBT_EGG_COUNT, eggLayCount);
        if (eggLayPos != null) {
            tag.putLong(NBT_EGG_LAY_POS, eggLayPos.asLong());
        }
        tag.putInt(NBT_EGG_LAY_TIMEOUT, eggLayTimeoutTicks);
        tag.putInt(NBT_EGG_LAY_VARIANT, eggLayVariant);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        hasShedAdultScutes = tag.getBoolean(NBT_HAS_SHED_SCUTES);
        shearCooldownTicks = tag.getInt(NBT_SHEAR_COOLDOWN);
        if (tag.contains(NBT_FLIGHT_STAMINA)) {
            setFlightStamina(tag.getInt(NBT_FLIGHT_STAMINA));
        }

        if (tag.contains(NBT_TEXTURE_VARIANT)) {
            setTextureVariant(tag.getInt(NBT_TEXTURE_VARIANT));
        }

        hasEggsToLay = tag.getBoolean(NBT_HAS_EGGS);
        eggLayCount = tag.getInt(NBT_EGG_COUNT);
        if (tag.contains(NBT_EGG_LAY_POS)) {
            eggLayPos = BlockPos.of(tag.getLong(NBT_EGG_LAY_POS));
        } else {
            eggLayPos = null;
        }
        eggLayTimeoutTicks = tag.getInt(NBT_EGG_LAY_TIMEOUT);
        if (tag.contains(NBT_EGG_LAY_VARIANT)) {
            eggLayVariant = Mth.clamp(tag.getInt(NBT_EGG_LAY_VARIANT), TEXTURE_VARIANT_MIN, TEXTURE_VARIANT_MAX);
        } else {
            eggLayVariant = TEXTURE_VARIANT_MIN;
        }

        // Ensure gravity state is consistent after load.
        if (isFlying()) {
            setNoGravity(true);
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, net.minecraft.world.level.block.state.BlockState block) {
        playSound(SoundEvents.TURTLE_SHAMBLE, 0.25F, 0.8F + random.nextFloat() * 0.15F);
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, net.minecraft.world.level.block.state.BlockState state, BlockPos pos) {
        // Flying mount: do not apply fall damage.
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "moveController", 0, state -> {
            if (shouldPlayAirborneFlyAnim()) {
                Vec3 dm = getDeltaMovement();
                double horiz = dm.horizontalDistanceSqr();
                double y = dm.y;

                if (y < -0.18D) {
                    state.setAnimation(GLIDE_LOOP);
                    return PlayState.CONTINUE;
                }

                if (horiz < 0.0025D && Math.abs(y) < 0.04D) {
                    state.setAnimation(HOVER_LOOP);
                    return PlayState.CONTINUE;
                }

                state.setAnimation(FLY_LOOP);
                return PlayState.CONTINUE;
            }

            // Ground jump (horse-style): while airborne but not in flight mode.
            if (!onGround() && !isFlying()) {
                state.setAnimation(JUMP_LOOP);
                return PlayState.CONTINUE;
            }

            // Horse-style rear while charging a jump.
            if (onGround()
                    && !isFlying()
                    && isJumpCharging()
                    && isVehicle()
                    && isSaddled()
                    && isTamed()
                    && getControllingPassenger() instanceof Player) {
                state.setAnimation(REAR_LOOP);
                return PlayState.CONTINUE;
            }

            if (isInLove() && !state.isMoving()) {
                state.setAnimation(LOVE_LOOP);
                return PlayState.CONTINUE;
            }

            if (state.isMoving()) {
                double speedSq = getDeltaMovement().horizontalDistanceSqr();
                // A light heuristic to pick a "gallop" equivalent when moving quickly.
                state.setAnimation(speedSq > 0.055D ? RUN_LOOP : WALK_LOOP);
            } else {
                state.setAnimation(IDLE_LOOP);
            }
            return PlayState.CONTINUE;
        }));

        controllers.add(new AnimationController<>(this, "actionController", 0, state -> PlayState.STOP)
                .triggerableAnim("takeoff", TAKEOFF_ONCE)
                .triggerableAnim("land", LAND_ONCE)
                .triggerableAnim("hurt", HURT_ONCE)
            .triggerableAnim("death", DEATH_ONCE)
            .triggerableAnim("eat", EAT_ONCE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
