package com.kruemblegard.pressurelogic;

import net.minecraft.nbt.CompoundTag;

public final class PressureNbt {
    private PressureNbt() {}

    public static final String TAG_PRESSURE = "Pressure";

    public static void save(CompoundTag tag, PressureValue pressure) {
        tag.putInt(TAG_PRESSURE, pressure.get());
    }

    public static PressureValue load(CompoundTag tag) {
        return new PressureValue(tag.getInt(TAG_PRESSURE));
    }

    public static void loadInto(CompoundTag tag, PressureValue pressure) {
        pressure.set(tag.getInt(TAG_PRESSURE));
    }
}
