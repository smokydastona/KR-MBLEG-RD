package com.kruemblegard.entity.projectile;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public class TraprockStoneProjectileEntity extends Projectile implements ItemSupplier {

    public TraprockStoneProjectileEntity(EntityType<? extends TraprockStoneProjectileEntity> type, Level level) {
        super(type, level);
    }

    public TraprockStoneProjectileEntity(EntityType<? extends TraprockStoneProjectileEntity> type, Level level, Entity owner) {
        super(type, level);
        this.setOwner(owner);
    }

    @Override
    public void tick() {
        super.tick();

        // Simple gravity.
        this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));

        if (this.tickCount > 60) {
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        if (!this.level().isClientSide) {
            if (result instanceof EntityHitResult ehr) {
                Entity target = ehr.getEntity();
                target.hurt(this.damageSources().thrown(this, this.getOwner()), 6f);
            }
            this.discard();
        }
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Items.COBBLESTONE);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
