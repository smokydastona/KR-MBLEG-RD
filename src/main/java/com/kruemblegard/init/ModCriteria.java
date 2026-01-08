package com.kruemblegard.init;

import com.kruemblegard.advancement.SimpleAdvancementTrigger;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;

public final class ModCriteria {
    private ModCriteria() {}

    public static final SimpleAdvancementTrigger PEBBLIT_SHOULDER =
            new SimpleAdvancementTrigger(new ResourceLocation("kruemblegard", "pebblit_shoulder"));

    public static void register() {
        CriteriaTriggers.register(PEBBLIT_SHOULDER);
    }
}
