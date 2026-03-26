package com.kruemblegard.entity;

import javax.annotation.Nullable;

import com.kruemblegard.entity.goal.RunegrowthGrazingGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import com.kruemblegard.registry.ModEntities;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Locale;

public class MossbackTortoiseEntity extends Animal implements GeoEntity {

    private static final String NBT_SHEARED = "Sheared";
    private static final String NBT_MOSS_VARIANT = "MossVariant";

    private static final int MOSS_VARIANT_COUNT = 3;

    private static final EntityDataAccessor<Boolean> DATA_SHEARED =
        SynchedEntityData.defineId(MossbackTortoiseEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> DATA_MOSS_VARIANT =
        SynchedEntityData.defineId(MossbackTortoiseEntity.class, EntityDataSerializers.INT);

    private static final RawAnimation IDLE_LOOP =
        RawAnimation.begin().thenLoop("animation.mossback_tortoise.idle");

    private static final RawAnimation WALK_LOOP =
        RawAnimation.begin().thenLoop("animation.mossback_tortoise.walk");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public MossbackTortoiseEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
        this.setMaxUpStep(1.0F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SHEARED, false);
        this.entityData.define(DATA_MOSS_VARIANT, 0);
    }

    @Override
    public SpawnGroupData finalizeSpawn(
        ServerLevelAccessor level,
        DifficultyInstance difficulty,
        MobSpawnType reason,
        @Nullable SpawnGroupData spawnData,
        @Nullable CompoundTag tag
    ) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, spawnData, tag);
        this.setSheared(false);
        this.setMossVariant(this.random.nextInt(MOSS_VARIANT_COUNT));
        return data;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 22.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.14D)
            .add(Attributes.ARMOR, 6.0D)
            .add(Attributes.FOLLOW_RANGE, 12.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.10D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 0.8D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, Ingredient.of(Items.SEAGRASS), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 0.9D));
        this.goalSelector.addGoal(5, new RunegrowthGrazingGoal(this, mob -> ((MossbackTortoiseEntity) mob).isSheared(), true, 80));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.7D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.SEAGRASS);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        MossbackTortoiseEntity child = ModEntities.MOSSBACK_TORTOISE.get().create(level);
        if (child != null) {
            child.setSheared(false);
            child.setMossVariant(this.random.nextInt(MOSS_VARIANT_COUNT));
        }
        return child;
    }

    public boolean isSheared() {
        return this.entityData.get(DATA_SHEARED);
    }

    public void setSheared(boolean sheared) {
        this.entityData.set(DATA_SHEARED, sheared);
    }

    public int getMossVariant() {
        return this.entityData.get(DATA_MOSS_VARIANT);
    }

    public void setMossVariant(int variant) {
        this.entityData.set(DATA_MOSS_VARIANT, Mth.clamp(variant, 0, MOSS_VARIANT_COUNT - 1));
    }

    @Override
    public void ate() {
        super.ate();
        if (this.isSheared()) {
            this.setSheared(false);
        }
    }

    private boolean readyForShearing() {
        return !this.isBaby() && !this.isSheared();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.SHEARS) && readyForShearing()) {
            if (!this.level().isClientSide) {
                this.gameEvent(GameEvent.SHEAR, player);
                this.level().playSound(null, this, SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1.0F, 1.0F);

                this.setSheared(true);

                int count = 1 + this.random.nextInt(2);
                for (int i = 0; i < count; i++) {
                    this.spawnAtLocation(new ItemStack(Blocks.MOSS_CARPET.asItem()), 1.0F);
                }

                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean(NBT_SHEARED, this.isSheared());
        tag.putInt(NBT_MOSS_VARIANT, this.getMossVariant());
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // Match Scaralon Beetle behavior: immune to thorny / berry-bush collision damage.
        if (isBerryBushTypeDamage(source)) {
            return false;
        }
        return super.hurt(source, amount);
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
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setSheared(tag.getBoolean(NBT_SHEARED));
        if (tag.contains(NBT_MOSS_VARIANT, Tag.TAG_INT)) {
            this.setMossVariant(tag.getInt(NBT_MOSS_VARIANT));
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "baseController", 0, state -> {
            state.setAnimation(state.isMoving() ? WALK_LOOP : IDLE_LOOP);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
