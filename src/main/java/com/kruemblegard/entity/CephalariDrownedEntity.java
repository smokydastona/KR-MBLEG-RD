package com.kruemblegard.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.level.Level;

/**
 * Drowned-flavored Zombified Cephalari.
 *
 * Separate entity type so spawns/loot/conversion mirror vanilla Drowned behavior.
 */
public final class CephalariDrownedEntity extends CephalariZombieEntity {

    public CephalariDrownedEntity(EntityType<? extends ZombieVillager> type, Level level) {
        super(type, level);
        setBodyTextureType(BODY_TEXTURE_DROWNED);
    }

    @Override
    public int getFixedBodyTextureType() {
        return BODY_TEXTURE_DROWNED;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    protected boolean isSunSensitive() {
        // Vanilla drowned do not burn in daylight.
        return false;
    }
}
