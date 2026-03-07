package com.kruemblegard.pressurelogic;

import java.util.Objects;

/**
 * Mutable, clamped pressure value used by Pressure-Logic machines.
 *
 * Spec: pressure is a continuous value from 0–100.
 */
public final class PressureValue {
    private int value;

    public PressureValue() {
        this(0);
    }

    public PressureValue(int value) {
        this.value = PressureConstants.clampPressure(value);
    }

    public int get() {
        return value;
    }

    public void set(int value) {
        this.value = PressureConstants.clampPressure(value);
    }

    public void add(int delta) {
        set(this.value + delta);
    }

    public boolean isEmpty() {
        return value <= PressureConstants.MIN_PRESSURE;
    }

    public boolean isFull() {
        return value >= PressureConstants.MAX_PRESSURE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PressureValue that)) return false;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "PressureValue{" + value + "}";
    }
}
