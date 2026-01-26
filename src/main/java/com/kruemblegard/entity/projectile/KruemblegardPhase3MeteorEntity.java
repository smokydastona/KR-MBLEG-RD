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

public class KruemblegardPhase3MeteorEntity extends KruemblegardBossProjectileBase {

    private static final RawAnimation IDLE_LOOP =
        RawAnimation.begin().thenLoop("animation.kruemblegard_phase3_meteor.idle");

    public KruemblegardPhase3MeteorEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    public KruemblegardPhase3MeteorEntity(Level level, Entity owner) {
        super(ModProjectileEntities.PHASE3_METEOR.get(), level);
        this.setOwner(owner);
    }

    @Override
    protected RawAnimation getIdleAnimation() {
        return IDLE_LOOP;
    }

    @Override
    public void tick() {
        super.tick();

        // Falling motion
        this.setDeltaMovement(this.getDeltaMovement().add(0, -0.05, 0));

        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }

        if (this.tickCount > 100) {
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        if (!this.level().isClientSide) {
            if (result instanceof EntityHitResult ehr) {
                Entity target = ehr.getEntity();
                DamageSource src = KruemblegardDamageSources.meteorArm(this, this.getOwner());
                target.hurt(src, 20f);
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
