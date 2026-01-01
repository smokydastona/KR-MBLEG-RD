package com.kruemblegard.util;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * Minimal helper for custom damage sources used by projectiles.
 *
 * This is intentionally lightweight; you can expand it later with proper
 * DamageType JSON/data-driven registration if desired.
 */
public class KruemblegardDamageSources {

    public static DamageSource runeBolt(Entity projectile, Entity owner) {
        return projectile.damageSources().indirectMagic(projectile, owner);
    }

    public static DamageSource meteorArm(Entity projectile, Entity owner) {
        if (owner instanceof LivingEntity living) {
            return projectile.damageSources().mobAttack(living);
        }
        return projectile.damageSources().generic();
    }

    public static DamageSource arcaneStorm(Entity projectile, Entity owner) {
        return projectile.damageSources().indirectMagic(projectile, owner);
    }
}
