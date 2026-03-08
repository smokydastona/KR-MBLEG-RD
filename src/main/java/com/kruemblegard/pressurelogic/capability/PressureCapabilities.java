package com.kruemblegard.pressurelogic.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public final class PressureCapabilities {
    private PressureCapabilities() {}

    public static final Capability<IPressureHandler> PRESSURE_HANDLER =
        CapabilityManager.get(new CapabilityToken<>() {});
}
