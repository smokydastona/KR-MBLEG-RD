package com.kruemblegard.entity.projectile;

import com.kruemblegard.config.ClientConfig;
import com.kruemblegard.registry.ModProjectileEntities;
import com.kruemblegard.util.DistanceCulling;
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

public class MeteorArmEntity extends Projectile {

    public MeteorArmEntity(EntityType<? extends MeteorArmEntity> type, Level level) {
        super(type, level);
    }

    public MeteorArmEntity(Level level, Entity owner) {
        super(ModProjectileEntities.METEOR_ARM.get(), level);
        this.setOwner(owner);
    }

    @Override
    public void tick() {
        super.tick();

        // Falling motion
        this.setDeltaMovement(this.getDeltaMovement().add(0, -0.05, 0));

        // Particles (client-only, optionally distance-culled)
        if (this.level().isClientSide) {
            int interval = ClientConfig.PROJECTILE_PARTICLE_SPAWN_INTERVAL_TICKS.get();
            if (interval <= 1 || this.tickCount % interval == 0) {
                boolean shouldSpawn = true;
                if (ClientConfig.ENABLE_DISTANCE_CULLED_COSMETICS.get()) {
                    double maxDistance = ClientConfig.COSMETIC_CULL_DISTANCE_BLOCKS.get();
                    var viewer = this.level().getNearestPlayer(this, maxDistance);
                    shouldSpawn = viewer != null && DistanceCulling.isWithinDistance(
                        viewer.position(),
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        maxDistance,
                        ClientConfig.COSMETIC_VERTICAL_STRETCH.get()
                    );
                }

                if (shouldSpawn) {
                    for (int i = 0; i < 6; i++) {
                        this.level().addParticle(
                            ParticleTypes.SMOKE,
                            this.getX(),
                            this.getY(),
                            this.getZ(),
                            0,
                            0,
                            0
                        );
                    }
                }
            }
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
