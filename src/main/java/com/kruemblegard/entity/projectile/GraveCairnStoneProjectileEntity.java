package com.kruemblegard.entity.projectile;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public class GraveCairnStoneProjectileEntity extends Projectile implements ItemSupplier {

    public GraveCairnStoneProjectileEntity(EntityType<? extends GraveCairnStoneProjectileEntity> type, Level level) {
        super(type, level);
    }

    public GraveCairnStoneProjectileEntity(EntityType<? extends GraveCairnStoneProjectileEntity> type, Level level, Entity owner) {
        super(type, level);
        this.setOwner(owner);
    }

    @Override
    public void tick() {
        super.tick();

        // Simple gravity.
        this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));

        if (this.tickCount > 80) {
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        if (this.level().isClientSide) {
            return;
        }

        if (result instanceof EntityHitResult ehr) {
            Entity target = ehr.getEntity();
            target.hurt(this.damageSources().thrown(this, this.getOwner()), 5.0F);
        }

        if (this.level() instanceof ServerLevel serverLevel) {
            BlockParticleOption burst = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.COBBLESTONE.defaultBlockState());
            serverLevel.sendParticles(burst, getX(), getY(), getZ(), 12, 0.25D, 0.25D, 0.25D, 0.12D);
            serverLevel.sendParticles(ParticleTypes.POOF, getX(), getY(), getZ(), 6, 0.18D, 0.18D, 0.18D, 0.02D);
        }

        this.playSound(SoundEvents.STONE_HIT, 0.7F, 0.9F + (this.random.nextFloat() * 0.2F));
        this.discard();
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
