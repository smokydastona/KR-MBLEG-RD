package com.kruemblegard.entity;

import com.kruemblegard.registry.ModEntities;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Cephalari
 *
 * Implementation strategy: extend vanilla {@link Villager} so this entity inherits the full villager
 * brain/POI/profession/trading behavior, and the rest of the game treats it as a villager-class mob.
 */
public class CephalariEntity extends Villager implements GeoEntity {

    private static final RawAnimation IDLE_LOOP = RawAnimation.begin().thenLoop("animation.cephalari.idle");
    private static final RawAnimation WALK_LOOP = RawAnimation.begin().thenLoop("animation.cephalari.walk");

    private static final int NON_WAYFALL_SUFFOCATION_INTERVAL_TICKS = 40;
    private static final float NON_WAYFALL_SUFFOCATION_DAMAGE = 1.0F;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public CephalariEntity(EntityType<? extends Villager> type, Level level) {
        super(type, level);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (level().isClientSide) {
            return;
        }

        if (level().dimension().equals(ModWorldgenKeys.Levels.WAYFALL)) {
            return;
        }

        if (this.tickCount % NON_WAYFALL_SUFFOCATION_INTERVAL_TICKS == 0) {
            this.hurt(this.damageSources().drown(), NON_WAYFALL_SUFFOCATION_DAMAGE);
        }
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
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
        return this.cache;
    }

    /**
     * Prevent cross-species breeding:
     * - Cephalari + Cephalari -> Cephalari
     * - Cephalari + Villager -> no child
     */
    @Override
    public @Nullable CephalariEntity getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        if (!(otherParent instanceof CephalariEntity)) {
            return null;
        }

        CephalariEntity baby = ModEntities.CEPHALARI.get().create(level);
        if (baby != null) {
            VillagerData data = this.getVillagerData();
            baby.setVillagerData(new VillagerData(data.getType(), VillagerProfession.NONE, 1));
        }
        return baby;
    }

    /**
     * Keep vanilla villager zombification behavior, but convert into the Cephalari zombie variant.
     */
    @Override
    public void die(DamageSource source) {
        if (!level().isClientSide) {
            Entity attacker = source.getEntity();
            if (attacker instanceof Zombie && level() instanceof ServerLevel serverLevel) {
                Difficulty difficulty = serverLevel.getDifficulty();

                boolean shouldConvert;
                if (difficulty == Difficulty.HARD) {
                    shouldConvert = true;
                } else if (difficulty == Difficulty.NORMAL) {
                    shouldConvert = serverLevel.getRandom().nextBoolean();
                } else {
                    shouldConvert = false;
                }

                if (shouldConvert) {
                    CephalariZombieEntity zombie = ModEntities.CEPHALARI_ZOMBIE.get().create(serverLevel);
                    if (zombie != null) {
                        zombie.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                        zombie.setVillagerData(this.getVillagerData());
                        zombie.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()), net.minecraft.world.entity.MobSpawnType.CONVERSION, null, null);
                        zombie.setNoAi(this.isNoAi());
                        zombie.setCustomName(this.getCustomName());
                        zombie.setCustomNameVisible(this.isCustomNameVisible());

                        serverLevel.addFreshEntity(zombie);
                    }

                    this.discard();
                    return;
                }
            }
        }

        super.die(source);
    }
}
