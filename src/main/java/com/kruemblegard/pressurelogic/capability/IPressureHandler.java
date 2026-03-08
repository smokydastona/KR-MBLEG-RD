package com.kruemblegard.pressurelogic.capability;

/**
 * Minimal Forge capability surface for Krümblegård pressure.
 *
 * This is intentionally small so other mods can interop without depending on
 * internal BlockEntity classes.
 */
public interface IPressureHandler {
    int getPressure();

    /**
     * Sets the current pressure.
     *
     * Implementations should clamp to the supported range.
     */
    void setPressure(int value);

    int getMaxPressure();
}
