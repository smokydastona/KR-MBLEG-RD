package com.kruemblegard.util;

import com.kruemblegard.Kruemblegard;

import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class AdvancementUtil {
    private AdvancementUtil() {}

    public static final ResourceLocation ADV_OBSERVED = new ResourceLocation(Kruemblegard.MODID, "kruemblegard_observed");
    public static final ResourceLocation ADV_SURVIVED = new ResourceLocation(Kruemblegard.MODID, "kruemblegard_survived");
    public static final ResourceLocation ADV_CLEANSED = new ResourceLocation(Kruemblegard.MODID, "kruemblegard_cleansed");

    public static void award(ServerPlayer player, ResourceLocation advancementId) {
        Advancement adv = player.server.getAdvancements().getAdvancement(advancementId);
        if (adv == null) return;

        var progress = player.getAdvancements().getOrStartProgress(adv);
        for (String criterion : progress.getRemainingCriteria()) {
            player.getAdvancements().award(adv, criterion);
        }
    }
}
