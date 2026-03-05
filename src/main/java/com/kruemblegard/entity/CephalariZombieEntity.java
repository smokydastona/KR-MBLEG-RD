package com.kruemblegard.entity;

import com.kruemblegard.registry.ModEntities;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.level.Level;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Zombified Cephalari.
 *
 * Extends vanilla {@link ZombieVillager} so curing/conversion mechanics behave like villagers,
 * but swaps the cured result to {@link CephalariEntity}.
 */
public class CephalariZombieEntity extends ZombieVillager implements GeoEntity {

    private static final RawAnimation IDLE_LOOP = RawAnimation.begin().thenLoop("animation.cephalari_zombie.idle");
    private static final RawAnimation WALK_LOOP = RawAnimation.begin().thenLoop("animation.cephalari_zombie.walk");
    private static final RawAnimation RIDING_LOOP = RawAnimation.begin().thenLoop("animation.cephalari_zombie.riding_pose");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public CephalariZombieEntity(EntityType<? extends ZombieVillager> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "baseController", 0, state -> {
            if (this.isPassenger()) {
                state.setAnimation(RIDING_LOOP);
                return PlayState.CONTINUE;
            }

            state.setAnimation(state.isMoving() ? WALK_LOOP : IDLE_LOOP);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public <T extends Mob> T convertTo(EntityType<T> entityType, boolean keepEquipment) {
        if (entityType == EntityType.VILLAGER) {
            @SuppressWarnings("unchecked")
            EntityType<T> target = (EntityType<T>) ModEntities.CEPHALARI.get();

            T converted = super.convertTo(target, keepEquipment);
            if (converted instanceof CephalariEntity cephalari) {
                cephalari.setVillagerData(this.getVillagerData());
                cephalari.setNoAi(this.isNoAi());
                cephalari.setCustomName(this.getCustomName());
                cephalari.setCustomNameVisible(this.isCustomNameVisible());
            }
            return converted;
        }

        return super.convertTo(entityType, keepEquipment);
    }
}
