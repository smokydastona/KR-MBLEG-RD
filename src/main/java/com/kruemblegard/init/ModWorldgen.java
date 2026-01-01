package com.kruemblegard.init;

import net.minecraftforge.eventbus.api.IEventBus;

public final class ModWorldgen {
    private ModWorldgen() {}

    /**
    * Worldgen features are data-driven via JSON under data/kruemblegard/worldgen.
     * (See the Forge biome modifier under data/forge/biome_modifier.)
     */
    public static void register(IEventBus bus) {
        // Intentionally empty.
    }
}
