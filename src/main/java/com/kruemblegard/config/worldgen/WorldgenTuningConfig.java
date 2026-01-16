package com.kruemblegard.config.worldgen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import com.kruemblegard.Kruemblegard;
import com.kruemblegard.worldgen.ModWorldgenKeys;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.fml.loading.FMLPaths;

public final class WorldgenTuningConfig {
    private WorldgenTuningConfig() {}

    public static final int SCHEMA_VERSION = 1;
    public static final String FILE_NAME = "kruemblegard-worldgen.json5";

    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create();

    private static volatile Data cached = null;

    public static Data get() {
        Data existing = cached;
        if (existing != null) {
            return existing;
        }
        return loadAndSync();
    }

    public static synchronized Data loadAndSync() {
        Data existing = cached;
        if (existing != null) {
            return existing;
        }

        Path configPath = getConfigPath();
        Data loaded = null;
        boolean shouldWrite = false;

        if (Files.exists(configPath)) {
            try (BufferedReader reader = Files.newBufferedReader(configPath, StandardCharsets.UTF_8)) {
                JsonReader jsonReader = new JsonReader(reader);
                jsonReader.setLenient(true);
                loaded = GSON.fromJson(jsonReader, Data.class);
            } catch (IOException | JsonParseException ex) {
                Kruemblegard.LOGGER.warn("Failed to read worldgen config {} (using defaults): {}", configPath, ex.toString());
                loaded = null;
            }
        }

        if (loaded == null) {
            loaded = Data.defaults();
            shouldWrite = true;
        }

        if (loaded.mergeDefaultsInPlace()) {
            shouldWrite = true;
        }

        if (shouldWrite) {
            try {
                writeConfig(configPath, loaded);
            } catch (IOException ex) {
                Kruemblegard.LOGGER.warn("Failed to write worldgen config {}: {}", configPath, ex.toString());
            }
        }

        cached = loaded;
        return loaded;
    }

    private static Path getConfigPath() {
        return FMLPaths.CONFIGDIR.get().resolve(FILE_NAME);
    }

    private static void writeConfig(Path configPath, Data data) throws IOException {
        Files.createDirectories(configPath.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(configPath, StandardCharsets.UTF_8)) {
            writer.write("// Krümblegård worldgen configuration (JSON5-ish).\n");
            writer.write("//\n");
            writer.write("// Notes:\n");
            writer.write("// - This file is auto-created and auto-extended when new keys are added.\n");
            writer.write("// - TerraBlender overworld integration is safe by default (weights default to 0).\n");
            writer.write("// - Set strictValidation=true to hard-fail on missing/invalid worldgen references at server start.\n\n");
            writer.write(GSON.toJson(data));
            writer.write("\n");
        }

        Kruemblegard.LOGGER.info("Worldgen config written: {}", configPath);
    }

    public static final class Data {
        public int schemaVersion = SCHEMA_VERSION;
        public boolean strictValidation = false;
        public TerraBlender terraBlender = TerraBlender.defaults();

        public static Data defaults() {
            Data out = new Data();
            out.schemaVersion = SCHEMA_VERSION;
            out.strictValidation = false;
            out.terraBlender = TerraBlender.defaults();
            return out;
        }

        public boolean mergeDefaultsInPlace() {
            boolean changed = false;

            if (this.schemaVersion <= 0) {
                this.schemaVersion = SCHEMA_VERSION;
                changed = true;
            }

            if (this.terraBlender == null) {
                this.terraBlender = TerraBlender.defaults();
                changed = true;
            } else {
                changed |= this.terraBlender.mergeDefaultsInPlace();
            }

            return changed;
        }
    }

    public static final class TerraBlender {
        public Overworld overworld = Overworld.defaults();

        public static TerraBlender defaults() {
            TerraBlender out = new TerraBlender();
            out.overworld = Overworld.defaults();
            return out;
        }

        public boolean mergeDefaultsInPlace() {
            boolean changed = false;

            if (this.overworld == null) {
                this.overworld = Overworld.defaults();
                changed = true;
            } else {
                changed |= this.overworld.mergeDefaultsInPlace();
            }

            return changed;
        }
    }

    public static final class Overworld {
        public int primaryWeight = 0;
        public int secondaryWeight = 0;
        public int rareWeight = 0;

        public boolean enableSurfaceRules = false;

        /**
         * Per-biome enable flags for TerraBlender overworld mapping.
         * Keys are ResourceLocation strings (e.g. "kruemblegard:crumbled_crossing").
         */
        public Map<String, Boolean> enabledBiomes = defaultOverworldBiomeToggles();

        public static Overworld defaults() {
            Overworld out = new Overworld();
            out.primaryWeight = 0;
            out.secondaryWeight = 0;
            out.rareWeight = 0;
            out.enableSurfaceRules = false;
            out.enabledBiomes = defaultOverworldBiomeToggles();
            return out;
        }

        public boolean mergeDefaultsInPlace() {
            boolean changed = false;

            if (this.enabledBiomes == null) {
                this.enabledBiomes = defaultOverworldBiomeToggles();
                changed = true;
            } else {
                Map<String, Boolean> defaults = defaultOverworldBiomeToggles();
                for (var entry : defaults.entrySet()) {
                    if (!this.enabledBiomes.containsKey(entry.getKey())) {
                        this.enabledBiomes.put(entry.getKey(), entry.getValue());
                        changed = true;
                    }
                }
            }

            if (this.primaryWeight < 0) {
                this.primaryWeight = 0;
                changed = true;
            }
            if (this.secondaryWeight < 0) {
                this.secondaryWeight = 0;
                changed = true;
            }
            if (this.rareWeight < 0) {
                this.rareWeight = 0;
                changed = true;
            }

            return changed;
        }

        public boolean isAnyBiomeEnabled() {
            if (enabledBiomes == null || enabledBiomes.isEmpty()) {
                return false;
            }

            for (boolean enabled : enabledBiomes.values()) {
                if (enabled) {
                    return true;
                }
            }

            return false;
        }
    }

    private static Map<String, Boolean> defaultOverworldBiomeToggles() {
        Map<String, Boolean> out = new LinkedHashMap<>();
        for (ResourceKey<Biome> biomeKey : knownWayfallBiomes()) {
            out.put(biomeKey.location().toString(), false);
        }
        return out;
    }

    private static List<ResourceKey<Biome>> knownWayfallBiomes() {
        return List.of(
            ModWorldgenKeys.Biomes.BASIN_OF_SCARS,
            ModWorldgenKeys.Biomes.BETWEENLIGHT_VOID,
            ModWorldgenKeys.Biomes.CRUMBLED_CROSSING,
            ModWorldgenKeys.Biomes.DRIFTWAY_CHASM,
            ModWorldgenKeys.Biomes.FAULTED_EXPANSE,
            ModWorldgenKeys.Biomes.FRACTURE_SHOALS,
            ModWorldgenKeys.Biomes.GLYPHSCAR_REACH,
            ModWorldgenKeys.Biomes.HOLLOW_TRANSIT_PLAINS,
            ModWorldgenKeys.Biomes.RIVEN_CAUSEWAYS,
            ModWorldgenKeys.Biomes.SHATTERPLATE_FLATS,
            ModWorldgenKeys.Biomes.STRATA_COLLAPSE,
            ModWorldgenKeys.Biomes.UNDERWAY_FALLS
        );
    }
}
