package com.kruemblegard.pressurelogic;

public final class PressureConstants {
    private PressureConstants() {}

    public static final int MIN_PRESSURE = 0;
    public static final int MAX_PRESSURE = 100;

    public static int clampPressure(int value) {
        if (value < MIN_PRESSURE) return MIN_PRESSURE;
        if (value > MAX_PRESSURE) return MAX_PRESSURE;
        return value;
    }
}
