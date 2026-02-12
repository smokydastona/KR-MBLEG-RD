package com.kruemblegard.entity;

import com.kruemblegard.init.ModBlocks;
import com.kruemblegard.registry.ModEntities;
import com.kruemblegard.registry.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
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

    private static final int TEXTURE_VARIANT_MIN = 1;
    private static final int TEXTURE_VARIANT_MAX = 8;

    // Flight stamina is measured in ticks. Max stamina is intentionally modest so flight feels meaningful.
    // When stamina is depleted, the beetle can no longer gain altitude and will flutter down slowly.
    private static final int MAX_FLIGHT_STAMINA_TICKS = 20 * 20; // 20 seconds at full stamina
    private static final int STAMINA_REGEN_PER_TICK_GROUNDED = 3;
    private static final int STAMINA_DRAIN_PER_TICK_FLYING = 1;

    private static final int DISMOUNT_FOLLOW_TICKS = 20 * 4;
    private static final int DISMOUNT_SLOW_FALL_TICKS = 20 * 5;

    private static final double ASCEND_VELOCITY = 0.26D;
    private static final double DESCEND_VELOCITY = 0.28D;
    private static final double EXHAUSTED_DESCENT_VELOCITY = -0.07D;
    private static final double MAX_UPWARD_VELOCITY = 0.55D;
    private static final double MAX_DOWNWARD_VELOCITY = -0.55D;

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
    private boolean pendingFlightHover = false;
    private boolean pendingFlightHoverLeftGround = false;
    private int pendingFlightHoverTicks = 0;

    private @Nullable UUID lastDismountedPlayerId = null;
    private int dismountFollowTicks = 0;

    private boolean playedDeathAnim = false;

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

    private void setTextureVariant(int variant) {
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

    public void setFlightInputs(boolean ascendHeld, boolean descendHeld) {
        if (level().isClientSide) {
            return;
        }

        this.serverAscendHeld = ascendHeld;
        this.serverDescendHeld = descendHeld;

        // Responsiveness: if the rider is trying to control vertical movement while airborne,
        // ensure flight mode is engaged immediately (instead of waiting for safety heuristics).
        if (!isFlying()
                && !pendingFlightHover
                && isVehicle()
                && isSaddled()
                && isTamed()
                && getControllingPassenger() instanceof Player
                && !onGround()
                && (ascendHeld || descendHeld)) {
            setFlying(true);
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
            // Reset if rider state becomes invalid.
            if (!(isVehicle() && isSaddled() && isTamed() && getControllingPassenger() instanceof Player)) {
                serverAscendHeld = false;
                serverDescendHeld = false;
                pendingFlightHover = false;
                pendingFlightHoverLeftGround = false;
                pendingFlightHoverTicks = 0;
            }

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
            if (isFlying() && onGround() && !isInWaterOrBubble()) {
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
                boolean falling = v.y < -0.02D || fallDistance > 0.5F;
                boolean wet = isInWaterOrBubble();
                boolean wantsFlight = serverAscendHeld || serverDescendHeld;

                // Safety + responsiveness: if the rider is airborne and is falling/wet OR
                // is actively trying to ascend/descend, engage flight mode.
                if (falling || wet || wantsFlight) {
                    setFlying(true);
                }
            }

            // If we launched via charged jump, switch into flight-mode hover near the apex.
            // We keep gravity during the initial launch so it feels like a jump, then enter hover-flight.
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
                        // Wait until upward velocity mostly decays (apex-ish), then engage flight and hover.
                        if (getDeltaMovement().y <= 0.03D || pendingFlightHoverTicks >= 20) {
                            pendingFlightHover = false;
                            pendingFlightHoverLeftGround = false;
                            pendingFlightHoverTicks = 0;

                            setFlying(true);
                            Vec3 v = getDeltaMovement();
                            setDeltaMovement(v.x * 0.90D, 0.0D, v.z * 0.90D);
                        }
                    }
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

            // Soft harmonic hum near arcane blocks.
            if (random.nextInt(90) == 0 && isNearArcaneEnergy()) {
                playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 0.35F, 0.8F + random.nextFloat() * 0.25F);
            }
        }
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

                // Use FLYING_SPEED as a base, but scale it to feel like an actual mount.
                float baseFlySpeed = (float) getAttributeValue(Attributes.FLYING_SPEED);
                float flySpeed = baseFlySpeed * 4.0F;

                // Apply horizontal steering relative to the beetle's yaw (already synced to rider).
                moveRelative(flySpeed, new Vec3(strafe, 0.0D, forward));
                Vec3 v = getDeltaMovement();

                double targetY;
                if (exhausted) {
                    targetY = EXHAUSTED_DESCENT_VELOCITY;
                } else {
                    targetY = 0.0D;
                    if (serverAscendHeld) {
                        targetY += ASCEND_VELOCITY;
                    }
                    if (serverDescendHeld) {
                        targetY -= DESCEND_VELOCITY;
                    }
                }

                // Smoothly approach target vertical velocity.
                double y = v.y + (targetY - v.y) * 0.35D;
                y = Mth.clamp(y, MAX_DOWNWARD_VELOCITY, MAX_UPWARD_VELOCITY);

                // Apply drag so hovering feels stable and flight isn't slippery.
                Vec3 steered = new Vec3(v.x * 0.93D, y, v.z * 0.93D);
                setDeltaMovement(steered);
                move(MoverType.SELF, steered);
                hasImpulse = true;

                fallDistance = 0.0F;
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
        super.handleStartJump(jumpPower);

        if (level().isClientSide) {
            return;
        }

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
        super.handleStopJump();

        if (level().isClientSide) {
            return;
        }

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
                || stack.is(ModItems.ATTUNED_RUNE_SHARD.get())
                || stack.is(ModItems.RUNIC_CORE.get());
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        ScaralonBeetleEntity baby = ModEntities.SCARALON_BEETLE.get().create(level);
        if (baby != null) {
            int variant = getTextureVariant();
            if (otherParent instanceof ScaralonBeetleEntity other) {
                variant = random.nextBoolean() ? getTextureVariant() : other.getTextureVariant();
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
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "moveController", 0, state -> {
            if (isFlying() && !onGround()) {
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
