package com.kruemblegard.init;

import net.minecraftforge.eventbus.api.IEventBus;

public final class ModWorldgen {
    private ModWorldgen() {}

    /**
     * Worldgen is data-driven via JSON under data/kruemblegard/worldgen.
     *
     * <p>Code references are centralized in {@code com.kruemblegard.worldgen.ModWorldgenKeys},
     * and critical datapack resources are validated at runtime by {@code com.kruemblegard.worldgen.WorldgenValidator}.</p>
     */
    public static void register(IEventBus bus) {
        // Intentionally empty.
    }
}
