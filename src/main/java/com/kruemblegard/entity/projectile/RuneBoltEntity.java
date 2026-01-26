package com.kruemblegard.entity.projectile;

import com.kruemblegard.config.ClientConfig;
import com.kruemblegard.registry.ModProjectileEntities;
import com.kruemblegard.registry.ModItems;
import com.kruemblegard.util.KruemblegardDamageSources;
import com.kruemblegard.util.CosmeticEffectsPolicy;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public class RuneBoltEntity extends Projectile implements ItemSupplier {

    public RuneBoltEntity(EntityType<? extends RuneBoltEntity> type, Level level) {
        super(type, level);
    }

    public RuneBoltEntity(Level level, Entity owner) {
        super(ModProjectileEntities.RUNE_BOLT.get(), level);
        this.setOwner(owner);
    }

    @Override
    public void tick() {
        super.tick();

        // Motion
        this.setDeltaMovement(this.getDeltaMovement());

        // Particles (client-only, optionally distance-culled)
        if (this.level().isClientSide) {
            int interval = ClientConfig.PROJECTILE_PARTICLE_SPAWN_INTERVAL_TICKS.get();
            if (interval <= 1 || this.tickCount % interval == 0) {
                int particleCount = 3;
                if (CosmeticEffectsPolicy.shouldSpawnCosmeticParticles(this.level(), this, particleCount)) {
                    for (int i = 0; i < particleCount; i++) {
                        this.level().addParticle(
                            ParticleTypes.END_ROD,
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

        // Lifetime
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
    public ItemStack getItem() {
        return new ItemStack(ModItems.RUNE_PETALS.get());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
