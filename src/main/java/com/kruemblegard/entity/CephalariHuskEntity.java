package com.kruemblegard.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.level.Level;

/**
 * Husk-flavored Zombified Cephalari.
 *
 * Separate entity type so spawns/loot/conversion mirror vanilla Husk behavior.
 */
public final class CephalariHuskEntity extends CephalariZombieEntity {

    private int submergedConversionTicks = 0;

    public CephalariHuskEntity(EntityType<? extends ZombieVillager> type, Level level) {
        super(type, level);
        setBodyTextureType(BODY_TEXTURE_HUSK);
    }

    @Override
    public int getFixedBodyTextureType() {
        return BODY_TEXTURE_HUSK;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (level().isClientSide) {
            return;
        }

        // Vanilla-like: Husk -> Zombie after time submerged.
        if (isInWaterOrBubble()) {
            submergedConversionTicks++;
            if (submergedConversionTicks >= 300) {
                submergedConversionTicks = 0;
                convertToUndeadVariant(UndeadVariant.ZOMBIE);
            }
        } else {
            submergedConversionTicks = 0;
        }
    }

    @Override
    protected boolean isSunSensitive() {
        // Vanilla husks do not burn in daylight.
        return false;
    }
}
