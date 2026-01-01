package com.kruemblegard.init;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.advancement.SimpleAdvancementTrigger;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;

public final class ModCriteria {
    private ModCriteria() {}

    public static final SimpleAdvancementTrigger HAUNTED_WAYSTONE_CLICKED =
            new SimpleAdvancementTrigger(new ResourceLocation(Kruemblegard.MODID, "haunted_waystone_clicked"));

    public static final SimpleAdvancementTrigger KRUEMBLEGARD_SURVIVED =
            new SimpleAdvancementTrigger(new ResourceLocation(Kruemblegard.MODID, "kruemblegard_survived"));

    public static final SimpleAdvancementTrigger KRUEMBLEGARD_CLEANSED =
            new SimpleAdvancementTrigger(new ResourceLocation(Kruemblegard.MODID, "kruemblegard_cleansed"));

    public static void register() {
        CriteriaTriggers.register(HAUNTED_WAYSTONE_CLICKED);
                CriteriaTriggers.register(KRUEMBLEGARD_SURVIVED);
                CriteriaTriggers.register(KRUEMBLEGARD_CLEANSED);
    }
}
