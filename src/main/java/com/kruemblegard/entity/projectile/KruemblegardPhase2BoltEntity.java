package com.kruemblegard.entity.projectile;

import com.kruemblegard.registry.ModProjectileEntities;
import com.kruemblegard.util.KruemblegardDamageSources;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib.core.animation.RawAnimation;

public class KruemblegardPhase2BoltEntity extends KruemblegardBossProjectileBase {

    private static final RawAnimation IDLE_LOOP =
        RawAnimation.begin().thenLoop("animation.kruemblegard_phase2_bolt.idle");

    public KruemblegardPhase2BoltEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public KruemblegardPhase2BoltEntity(Level level, Entity owner) {
        super(ModProjectileEntities.PHASE2_BOLT.get(), level);
        this.setOwner(owner);
        this.setNoGravity(true);
    }

    @Override
    protected RawAnimation getIdleAnimation() {
        return IDLE_LOOP;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide && this.tickCount % 2 == 0) {
            this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }

        if (this.tickCount > 80) {
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        if (!this.level().isClientSide) {
            if (result instanceof EntityHitResult ehr) {
                Entity target = ehr.getEntity();
                DamageSource src = KruemblegardDamageSources.runeBolt(this, this.getOwner());
                target.hurt(src, 10f);
            }
            this.discard();
        }
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
