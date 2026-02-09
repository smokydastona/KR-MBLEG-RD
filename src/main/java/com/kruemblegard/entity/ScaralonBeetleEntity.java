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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
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
 * Scaralon Beetle
 * - Horse-style taming + saddling + riding
 * - Flight is controlled via Space (takeoff/rise) + X (descend) with server-synced inputs.
 * - Babies shed Elytra Scutes on maturation (renewable progression loop without killing mounts).
 */
public class ScaralonBeetleEntity extends AbstractHorse implements GeoEntity {

    private static final String NBT_HAS_SHED_SCUTES = "HasShedAdultScutes";
    private static final String NBT_SHEAR_COOLDOWN = "ShearCooldown";

    private static final EntityDataAccessor<Boolean> FLYING =
            SynchedEntityData.defineId(ScaralonBeetleEntity.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation IDLE_LOOP = RawAnimation.begin().thenLoop("animation.scaralon_beetle.idle");
    private static final RawAnimation WALK_LOOP = RawAnimation.begin().thenLoop("animation.scaralon_beetle.walk");
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
    private int takeoffChargeTicks = 0;

    private boolean playedDeathAnim = false;

    public ScaralonBeetleEntity(EntityType<? extends AbstractHorse> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 26.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.26D)
                .add(Attributes.JUMP_STRENGTH, 0.80D)
                .add(Attributes.FLYING_SPEED, 0.045D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLYING, false);
    }

    private boolean isFlying() {
        return this.entityData.get(FLYING);
    }

    private void setFlying(boolean flying) {
        boolean wasFlying = isFlying();
        this.entityData.set(FLYING, flying);
        this.setNoGravity(flying);
        if (flying) {
            this.fallDistance = 0.0F;
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

        if (!ascendHeld) {
            this.takeoffChargeTicks = 0;
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
                takeoffChargeTicks = 0;
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

            // Stop flying when grounded and calm.
            if (isFlying() && onGround() && !serverAscendHeld) {
                setFlying(false);
            }

            // Soft harmonic hum near arcane blocks.
            if (random.nextInt(90) == 0 && isNearArcaneEnergy()) {
                playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 0.35F, 0.8F + random.nextFloat() * 0.25F);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean didHurt = super.hurt(source, amount);
        if (didHurt && !level().isClientSide && isAlive()) {
            triggerAnim("actionController", "hurt");
        }
        return didHurt;
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
            setYRot(player.getYRot());
            yRotO = getYRot();
            setXRot(player.getXRot() * 0.5F);
            setRot(getYRot(), getXRot());
            yBodyRot = getYRot();
            yHeadRot = getYRot();

            float strafe = player.xxa * 0.55F;
            float forward = player.zza;

            // Takeoff: hold Space while grounded to lift off.
            if (onGround() && !isFlying()) {
                if (serverAscendHeld) {
                    takeoffChargeTicks++;
                    if (takeoffChargeTicks >= 10) {
                        setFlying(true);
                    }
                } else {
                    takeoffChargeTicks = 0;
                }
            }

            // Engage flight while ridden once airborne.
            if (!onGround() && !isFlying()) {
                setFlying(true);
            }

            if (isFlying()) {
                // Synced inputs:
                // - Space: takeoff/rise
                // - X: descend
                double vertical = -0.02D; // gentle glide when no input
                if (serverAscendHeld) {
                    vertical += 0.10D;
                }
                if (serverDescendHeld) {
                    vertical -= 0.12D;
                }

                double glideCap = serverDescendHeld ? -0.40D : -0.14D;

                setSpeed((float) getAttributeValue(Attributes.FLYING_SPEED));
                super.travel(new Vec3(strafe, 0.0D, forward));

                Vec3 after = getDeltaMovement();
                double y = Mth.clamp(after.y * 0.60D + vertical, glideCap, 0.65D);
                setDeltaMovement(after.x * 0.98D, y, after.z * 0.98D);
                fallDistance = 0.0F;
                return;
            }

            setSpeed((float) getAttributeValue(Attributes.MOVEMENT_SPEED));
            super.travel(new Vec3(strafe, travelVector.y, forward));
            return;
        }

        super.travel(travelVector);
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
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        hasShedAdultScutes = tag.getBoolean(NBT_HAS_SHED_SCUTES);
        shearCooldownTicks = tag.getInt(NBT_SHEAR_COOLDOWN);

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

            if (isInLove() && !state.isMoving()) {
                state.setAnimation(LOVE_LOOP);
                return PlayState.CONTINUE;
            }

            state.setAnimation(state.isMoving() ? WALK_LOOP : IDLE_LOOP);
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
