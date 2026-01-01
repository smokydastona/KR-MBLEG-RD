package com.kruemblegard.advancement;

import net.minecraft.resources.ResourceLocation;

/**
 * Compatibility wrapper for the name used in the earlier design docs.
 * Prefer using {@link SimpleAdvancementTrigger} directly.
 */
public final class HauntedWaystoneClickedTrigger extends SimpleAdvancementTrigger {
    public HauntedWaystoneClickedTrigger(ResourceLocation id) {
        super(id);
    }
}
