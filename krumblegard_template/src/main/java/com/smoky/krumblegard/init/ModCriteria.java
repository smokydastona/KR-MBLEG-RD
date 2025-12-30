package com.smoky.krumblegard.init;

import com.smoky.krumblegard.KrumblegardMod;
import com.smoky.krumblegard.advancement.SimpleAdvancementTrigger;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;

public final class ModCriteria {
    private ModCriteria() {}

    public static final SimpleAdvancementTrigger HAUNTED_WAYSTONE_CLICKED =
            new SimpleAdvancementTrigger(new ResourceLocation(KrumblegardMod.MODID, "haunted_waystone_clicked"));

    public static final SimpleAdvancementTrigger KRUMBLEGARD_SURVIVED =
            new SimpleAdvancementTrigger(new ResourceLocation(KrumblegardMod.MODID, "krumblegard_survived"));

    public static final SimpleAdvancementTrigger KRUMBLEGARD_CLEANSED =
            new SimpleAdvancementTrigger(new ResourceLocation(KrumblegardMod.MODID, "krumblegard_cleansed"));

    public static void register() {
        CriteriaTriggers.register(HAUNTED_WAYSTONE_CLICKED);
        CriteriaTriggers.register(KRUMBLEGARD_SURVIVED);
        CriteriaTriggers.register(KRUMBLEGARD_CLEANSED);
    }
}
