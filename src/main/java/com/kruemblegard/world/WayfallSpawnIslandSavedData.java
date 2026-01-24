package com.kruemblegard.world;

import com.kruemblegard.Kruemblegard;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public final class WayfallSpawnIslandSavedData extends SavedData {
    private static final String DATA_NAME = Kruemblegard.MOD_ID + "_wayfall_spawn_island";

    private static final String TAG_PLACED = "placed";
    private static final String TAG_X = "x";
    private static final String TAG_Y = "y";
    private static final String TAG_Z = "z";
    private static final String TAG_STRUCTURE = "structure";
    private static final String TAG_PRELOAD_DONE = "preload_done";

    private boolean placed;
    private boolean preloadDone;
    private BlockPos anchor;
    private ResourceLocation structureId;

    private WayfallSpawnIslandSavedData() {
        this.placed = false;
        this.preloadDone = false;
        this.anchor = BlockPos.ZERO;
        this.structureId = null;
    }

    public static WayfallSpawnIslandSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                WayfallSpawnIslandSavedData::load,
                WayfallSpawnIslandSavedData::new,
                DATA_NAME
        );
    }

    public static WayfallSpawnIslandSavedData load(CompoundTag tag) {
        WayfallSpawnIslandSavedData data = new WayfallSpawnIslandSavedData();
        data.placed = tag.getBoolean(TAG_PLACED);
        data.preloadDone = tag.getBoolean(TAG_PRELOAD_DONE);
        int x = tag.getInt(TAG_X);
        int y = tag.getInt(TAG_Y);
        int z = tag.getInt(TAG_Z);
        data.anchor = new BlockPos(x, y, z);
        String structure = tag.getString(TAG_STRUCTURE);
        data.structureId = structure == null || structure.isBlank() ? null : ResourceLocation.tryParse(structure);
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean(TAG_PLACED, this.placed);
        tag.putBoolean(TAG_PRELOAD_DONE, this.preloadDone);
        tag.putInt(TAG_X, this.anchor.getX());
        tag.putInt(TAG_Y, this.anchor.getY());
        tag.putInt(TAG_Z, this.anchor.getZ());
        tag.putString(TAG_STRUCTURE, this.structureId == null ? "" : this.structureId.toString());
        return tag;
    }

    public boolean isPlaced() {
        return placed;
    }

    public boolean isPreloadDone() {
        return preloadDone;
    }

    public void setPlaced(boolean placed) {
        this.placed = placed;
    }

    public void setPreloadDone(boolean preloadDone) {
        this.preloadDone = preloadDone;
    }

    public BlockPos getAnchor() {
        return anchor;
    }

    public void setAnchor(BlockPos anchor) {
        this.anchor = anchor;
    }

    public ResourceLocation getStructureId() {
        return structureId;
    }

    public void setStructureId(ResourceLocation structureId) {
        this.structureId = structureId;
    }
}
