package com.kruemblegard.world;

import com.kruemblegard.Kruemblegard;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public final class WayfallOriginMonumentSavedData extends SavedData {
    private static final String DATA_NAME = Kruemblegard.MOD_ID + "_wayfall_origin_monument";
    private static final String KEY_PLACED = "Placed";

    private boolean placed;

    public static WayfallOriginMonumentSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                WayfallOriginMonumentSavedData::load,
                WayfallOriginMonumentSavedData::new,
                DATA_NAME
        );
    }

    public static WayfallOriginMonumentSavedData load(CompoundTag tag) {
        WayfallOriginMonumentSavedData data = new WayfallOriginMonumentSavedData();
        data.placed = tag.getBoolean(KEY_PLACED);
        return data;
    }

    public boolean isPlaced() {
        return placed;
    }

    public void setPlaced(boolean placed) {
        this.placed = placed;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean(KEY_PLACED, placed);
        return tag;
    }
}
