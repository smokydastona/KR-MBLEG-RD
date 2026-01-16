package com.kruemblegard.worldgen.terrablender;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.config.worldgen.WorldgenTuningConfig;

import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;

public final class KruemblegardTerraBlender {
    private KruemblegardTerraBlender() {}

    public static void register() {
        var config = WorldgenTuningConfig.get();
        var overworld = config.terraBlender.overworld;

        boolean anyBiomeEnabled = overworld.isAnyBiomeEnabled();

        if (anyBiomeEnabled && overworld.primaryWeight > 0) {
            Regions.register(new KruemblegardOverworldRegionPrimary(overworld.primaryWeight));
            Kruemblegard.LOGGER.info("TerraBlender: registered overworld primary region (weight={})", overworld.primaryWeight);
        }

        if (anyBiomeEnabled && overworld.secondaryWeight > 0) {
            Regions.register(new KruemblegardOverworldRegionSecondary(overworld.secondaryWeight));
            Kruemblegard.LOGGER.info("TerraBlender: registered overworld secondary region (weight={})", overworld.secondaryWeight);
        }

        if (anyBiomeEnabled && overworld.rareWeight > 0) {
            Regions.register(new KruemblegardOverworldRegionRare(overworld.rareWeight));
            Kruemblegard.LOGGER.info("TerraBlender: registered overworld rare region (weight={})", overworld.rareWeight);
        }

        if (overworld.enableSurfaceRules) {
            SurfaceRuleManager.addSurfaceRules(
                SurfaceRuleManager.RuleCategory.OVERWORLD,
                Kruemblegard.MOD_ID,
                KruemblegardOverworldSurfaceRules.makeRules()
            );
            Kruemblegard.LOGGER.info("TerraBlender: registered overworld surface rules");
        }
    }
}
