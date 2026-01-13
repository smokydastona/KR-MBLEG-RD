package com.kruemblegard.world;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public final class RunegrowthBonemeal {
    private RunegrowthBonemeal() {
    }

    /**
     * Like vanilla grass bonemeal, but scoped to Wayfall's injected vegetation patches:
     * - Spawns Paleweft (short/tall mix)
     * - Also has a chance to place other vegetation patches from the local biome
     *   (excluding food plants/saplings/trees)
     */
    public static void bonemeal(ServerLevel level, BlockPos origin, RandomSource random) {
        // Paleweft: stronger than the previous 18-attempt burst.
        PaleweftBloom.bloom(level, origin, random, 32);

        // Biome flora: try a couple of patch placements so biome-specific plants become renewable.
        int placements = 2;
        for (int i = 0; i < placements; i++) {
            // Not guaranteed every time; keep a little vanilla-like randomness.
            if (random.nextFloat() < 0.75f) {
                tryPlaceBiomeVegetationPatch(level, origin, random);
            }
        }
    }

    private static boolean tryPlaceBiomeVegetationPatch(ServerLevel level, BlockPos origin, RandomSource random) {
        // A few attempts to find a nearby valid surface.
        for (int attempt = 0; attempt < 6; attempt++) {
            int dx = Mth.nextInt(random, -6, 6);
            int dz = Mth.nextInt(random, -6, 6);
            BlockPos sample = origin.offset(dx, 0, dz);
            BlockPos surface = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, sample);

            Holder<Biome> biome = level.getBiome(surface);
            List<Holder<PlacedFeature>> candidates = getBiomeVegetationPatchCandidates(biome);
            if (candidates.isEmpty()) {
                return false;
            }

            Holder<PlacedFeature> chosen = candidates.get(random.nextInt(candidates.size()));
            return chosen.value().place(level, level.getChunkSource().getGenerator(), random, surface);
        }

        return false;
    }

    private static List<Holder<PlacedFeature>> getBiomeVegetationPatchCandidates(Holder<Biome> biomeHolder) {
        Biome biome = biomeHolder.value();
        int stepIndex = GenerationStep.Decoration.VEGETAL_DECORATION.ordinal();

        List<HolderSet<PlacedFeature>> steps = biome.getGenerationSettings().features();
        if (stepIndex < 0 || stepIndex >= steps.size()) {
            return List.of();
        }

        HolderSet<PlacedFeature> vegetal = steps.get(stepIndex);
        List<Holder<PlacedFeature>> candidates = new ArrayList<>();

        for (Holder<PlacedFeature> holder : vegetal) {
            ResourceKey<PlacedFeature> key = holder.unwrapKey().orElse(null);
            if (key == null) {
                continue;
            }

            String namespace = key.location().getNamespace();
            String path = key.location().getPath();

            // Only consider this mod's injected vegetation.
            if (!"kruemblegard".equals(namespace)) {
                continue;
            }

            // We only want plant patches, not trees/structures/etc.
            if (!path.endsWith("_patch")) {
                continue;
            }

            // Exclusions:
            // - food plants (the other three)
            // - saplings/trees
            // - paleweft (handled by PaleweftBloom)
            if (path.contains("soulberry") || path.contains("ghoulberry") || path.contains("wispstalk")) {
                continue;
            }
            if (path.contains("sapling") || path.contains("tree")) {
                continue;
            }
            if (path.contains("paleweft")) {
                continue;
            }

            candidates.add(holder);
        }

        return candidates;
    }
}
