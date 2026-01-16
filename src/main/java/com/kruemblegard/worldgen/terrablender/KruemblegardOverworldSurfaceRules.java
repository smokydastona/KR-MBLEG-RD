package com.kruemblegard.worldgen.terrablender;

import com.kruemblegard.worldgen.ModWorldgenKeys;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;

public final class KruemblegardOverworldSurfaceRules {
    private KruemblegardOverworldSurfaceRules() {}

    public static SurfaceRules.RuleSource makeRules() {
        SurfaceRules.RuleSource basaltSurface = simpleTopWithFiller(Blocks.BASALT, Blocks.BASALT);
        SurfaceRules.RuleSource gravelSurface = simpleTopWithFiller(Blocks.GRAVEL, Blocks.GRAVEL);
        SurfaceRules.RuleSource coarseDirtSurface = simpleTopWithFiller(Blocks.COARSE_DIRT, Blocks.DIRT);

        // These are intentionally conservative and disabled by default via config.
        // They exist primarily to keep surface-rule logic isolated and ready for future iteration.
        return SurfaceRules.sequence(
            SurfaceRules.ifTrue(
                SurfaceRules.isBiome(ModWorldgenKeys.Biomes.BASIN_OF_SCARS),
                basaltSurface
            ),
            SurfaceRules.ifTrue(
                SurfaceRules.isBiome(ModWorldgenKeys.Biomes.FRACTURE_SHOALS),
                gravelSurface
            ),
            SurfaceRules.ifTrue(
                SurfaceRules.isBiome(ModWorldgenKeys.Biomes.CRUMBLED_CROSSING, ModWorldgenKeys.Biomes.UNDERWAY_FALLS),
                coarseDirtSurface
            )
        );
    }

    private static SurfaceRules.RuleSource simpleTopWithFiller(Block top, Block filler) {
        SurfaceRules.RuleSource topRule = SurfaceRules.state(top.defaultBlockState());
        SurfaceRules.RuleSource fillerRule = SurfaceRules.state(filler.defaultBlockState());
        SurfaceRules.RuleSource surface = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), topRule), fillerRule);
        return SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, surface);
    }
}
