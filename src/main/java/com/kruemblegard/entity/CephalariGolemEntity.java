package com.kruemblegard.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.level.Level;

/**
 * A Cephalari-flavored village golem.
 *
 * Currently reuses vanilla Iron Golem behavior; spawned via event logic when villages with Cephalari
 * would otherwise spawn a normal iron golem.
 */
public class CephalariGolemEntity extends IronGolem {
    public CephalariGolemEntity(EntityType<? extends IronGolem> type, Level level) {
        super(type, level);
    }
}
