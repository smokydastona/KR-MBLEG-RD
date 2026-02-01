package com.kruemblegard.entity.vehicle;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class KruemblegardChestBoatEntity extends ChestBoat {
    private static final String NBT_TYPE = "KruemblegardBoatType";

    private static final EntityDataAccessor<String> DATA_KRUEMBLEGARD_TYPE =
            SynchedEntityData.defineId(KruemblegardChestBoatEntity.class, EntityDataSerializers.STRING);

    public KruemblegardChestBoatEntity(EntityType<? extends KruemblegardChestBoatEntity> type, Level level) {
        super(type, level);
    }

    public void setKruemblegardBoatType(KruemblegardBoatType type) {
        this.entityData.set(DATA_KRUEMBLEGARD_TYPE, type.id());
    }

    public KruemblegardBoatType getKruemblegardBoatType() {
        return KruemblegardBoatType.fromId(this.entityData.get(DATA_KRUEMBLEGARD_TYPE));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_KRUEMBLEGARD_TYPE, KruemblegardBoatType.WAYROOT.id());
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString(NBT_TYPE, this.entityData.get(DATA_KRUEMBLEGARD_TYPE));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(NBT_TYPE)) {
            this.entityData.set(DATA_KRUEMBLEGARD_TYPE, tag.getString(NBT_TYPE));
        }
    }

    @Override
    public Item getDropItem() {
        Item item = getKruemblegardBoatType().chestBoatItem();
        if (item != null && item != Items.AIR) {
            return item;
        }
        return super.getDropItem();
    }

    @Override
    public ItemStack getPickResult() {
        Item item = getKruemblegardBoatType().chestBoatItem();
        return (item != null && item != Items.AIR) ? new ItemStack(item) : super.getPickResult();
    }
}
