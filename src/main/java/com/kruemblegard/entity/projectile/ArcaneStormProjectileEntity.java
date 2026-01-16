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

public class ArcaneStormProjectileEntity extends Projectile {

    public ArcaneStormProjectileEntity(EntityType<? extends ArcaneStormProjectileEntity> type, Level level) {
        super(type, level);
    }

    public ArcaneStormProjectileEntity(Level level, Entity owner) {
        super(ModProjectileEntities.ARCANE_STORM.get(), level);
        this.setOwner(owner);
    }

    @Override
    public void tick() {
        super.tick();

        // Falling motion
        this.setDeltaMovement(0, -0.3, 0);

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
                    this.level().addParticle(
                        ParticleTypes.ENCHANT,
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

        if (this.tickCount > 40) {
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        if (!this.level().isClientSide) {
            if (result instanceof EntityHitResult ehr) {
                Entity target = ehr.getEntity();
                DamageSource src = KruemblegardDamageSources.arcaneStorm(this, this.getOwner());
                target.hurt(src, 8f);
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
