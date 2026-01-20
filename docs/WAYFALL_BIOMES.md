# Wayfall Biomes — Detailed Reference

This document is the **authoritative, detailed list** of Wayfall biomes.

## Update rules (keep this accurate)
- If you add/remove/rename any Wayfall biome JSON under `data/kruemblegard/worldgen/biome/`, update this doc in the same change.
- If you change a biome’s **spawns**, **particles**, **music**, or **features list**, update the matching entry here.

## Global Wayfall biome invariants (current)
- **Precipitation is rare**: most biomes set `has_precipitation: false` and `downfall: 0.0`.
  - Exception: the coldest biomes (currently Hollow Transit Plains and Glyphscar Reach) are cold enough for snow cover and include `minecraft:freeze_top_layer` so snow layers generate during worldgen.
- **No vanilla spawns**: all spawn lists are intentionally limited to Krümblegård mobs.
- **No carvers**: `carvers.air` is empty.
- Sky is always black (`sky_color: 0`).
- Terrain is floating-island style with a void fade band that begins around Y≈96; islands should be common enough that all Wayfall biomes can be found for progression.
- Biome regions are intentionally kept relatively small (more frequent biome transitions) so players can find specific biomes without traveling extreme distances.
- Flora placement is enforced via Forge biome modifiers (global staples + per-biome sets), and density is primarily controlled by each placed feature’s `rarity_filter`.
  - Some Wayfall trees include rare mega variants in natural generation (Wayroot, Glimmerpine, Driftwillow, Monument Oak).
- Surface palette is defined in Wayfall noise settings (`kruemblegard:wayfall`) with a small set of explicit biome-specific stacks:
  - **Default Wayfall surface**: Runegrowth → Fault Dust → (Stoneveil Rubble / Runed Stoneveil Rubble mix).
  - **Strata Collapse**: Ashfall Loam → Ashfall Stone → (Stoneveil Rubble / Runed Stoneveil Rubble mix).
  - **Shatterplate Flats**: Voidfelt → Fault Dust → (Stoneveil Rubble / Runed Stoneveil Rubble mix).
  - **Fracture Shoals**: Fractured Wayrock → Crushstone → (Stoneveil Rubble / Runed Stoneveil Rubble mix).
  - **Glyphscar Reach**: Fractured Wayrock → Fault Dust → (Scarstone / Cracked Scarstone mix) → (Stoneveil Rubble / Runed Stoneveil Rubble mix).
  - **Basin of Scars**: Runegrowth → Fault Dust → (Scarstone / Cracked Scarstone mix) → (Stoneveil Rubble / Runed Stoneveil Rubble mix).
  - Note: Wayfall’s deep “base stone” no longer defaults to Fractured Wayrock; Fractured Wayrock is now reserved for explicit biome surface identity (currently Fracture Shoals and Glyphscar Reach).
- Ores: Wayfall Iron Ore, Wayfall Copper Ore, and Wayfall Diamond Ore generate in all Wayfall biomes (diamond generates lower on average).
- Portal entry always lands the player on a placed **Wayfall origin-island structure** at **(0, 175, 0)**.
  - Structure templates live under `data/kruemblegard/structures/wayfall_origin_island/` (as `.snbt` + generated raw `.nbt`).
  - Jigsaw template pools for the per-type 5-variant sets live under `data/kruemblegard/worldgen/template_pool/wayfall_origin_island/`.
  - The pool is selected by the biome at the spawn location (with a temperature-based fallback for cold).
  - Wayfall's dimension spawn is kept aligned to the origin-island surface landing.


## Biome index
All biomes live in `src/main/resources/data/kruemblegard/worldgen/biome/`.

### `kruemblegard:basin_of_scars`
- File: `basin_of_scars.json`
- Temperature: 2.00
- Description: A smoke-hazed basin dotted with dense standing stones; feels heavy and volcanic.
- Terrain surface: Default Wayfall surface (Runegrowth → Fault Dust), with deeper Scarstone/Cracked Scarstone sublayers.
- Intended flora (design target):
  - Trees: Shardbark Pine, Fallbark
  - Plants: Soulberry Shrub (food), Faultgrass, Dustpetal, Gravevine, Ruin Thistle
- Spawns (monster): Traprock (weight 8, solo), Pebblit (weight 1, solo)
- Features: (none listed)
- Features:
  - Volcanic disks: `kruemblegard:basin_of_scars_basalt_disk`, `kruemblegard:basin_of_scars_magma_disk`
- Ambience:
  - Particles: `minecraft:ash` (0.0025)
  - Music: `kruemblegard:music.wayfall`
  - Mood: `minecraft:ambient.basalt_deltas.mood`
  - Additions: `minecraft:ambient.basalt_deltas.additions`

### `kruemblegard:betweenlight_void`
- File: `betweenlight_void.json`
- Temperature: 0.75
- Description: A cold, dim void pocket where reverse-portal motes drift around small attuned stone disks.
- Intended flora (design target):
  - Trees: Hollowway Tree
  - Plants: Wispstalk (food), Voidfern, Void Lichen, Waythread
- Spawns (monster): Traprock (weight 4, solo), Pebblit (weight 4, 1–2)
- Features:
  - Small Attuned Stone disk: `kruemblegard:attuned_stone_disk_small`
- Ambience:
  - Particles: `minecraft:reverse_portal` (0.0005)
  - Music: `minecraft:music.end`
  - Additions: `minecraft:ambient.basalt_deltas.mood`

### `kruemblegard:crumbled_crossing`
- File: `crumbled_crossing.json`
- Temperature: 0.95
- Description: A windblown crossing of broken island fragments, dusted with white ash and marked by large attuned disks.
- Intended flora (design target):
  - Trees: Driftwillow (rare mega variant), Cairn Tree
  - Plants: Wispstalk (food), Waylily, Milestone Grass, Echocap, Memory Rot, Moteshrub
- Spawns (monster): Pebblit (weight 10, 1–3), Traprock (weight 2, solo)
- Features:
  - Large Attuned Stone disk: `kruemblegard:attuned_stone_disk_large`
- Ambience:
  - Particles: `minecraft:white_ash` (0.0012)
  - Music: `minecraft:music.nether.soul_sand_valley`
  - Additions: `minecraft:ambient.soul_sand_valley.additions`

### `kruemblegard:driftway_chasm`
- File: `driftway_chasm.json`
- Temperature: 0.30
- Description: A fractured chasm corridor with falling ash and sparse megaliths; small attuned disks appear like waymarkers.
- Intended flora (design target):
  - Trees: Driftwillow (rare mega variant)
  - Plants: Soulberry Shrub (food), Pathreed, Runedrift Reed, Cairnroot, Misstep Vine
- Spawns (monster): Pebblit (weight 8, 1–2), Traprock (weight 4, solo)
- Features:
  - Small Attuned Stone disk: `kruemblegard:attuned_stone_disk_small`
- Ambience:
  - Particles: `minecraft:ash` (0.001)
  - Music: `minecraft:music.end`
  - Additions: `minecraft:ambient.basalt_deltas.additions`

### `kruemblegard:faulted_expanse`
- File: `faulted_expanse.json`
- Temperature: 0.60
- Description: A wide, faulted stretch of floating plateaus where dense standing stones break up the ash haze.
- Intended flora (design target):
  - Trees: Faultwood, Fallbark
  - Plants: Wispstalk (food), Waygrasp Vine, Static Fungus, Wayscar Ivy
- Spawns (monster): Traprock (weight 9, solo), Pebblit (weight 2, solo)
- Features: (none listed)
- Ambience:
  - Particles: `minecraft:ash` (0.0008)
  - Music: `minecraft:music.nether.basalt_deltas`
  - Additions: `minecraft:ambient.basalt_deltas.additions`

### `kruemblegard:fracture_shoals`
- File: `fracture_shoals.json`
- Temperature: 1.75
- Description: Shallow “shoals” of broken stone islands with faint crimson spores drifting through the air.
- Terrain surface: Fractured Wayrock → Crushstone, with deeper Stoneveil/Runed Stoneveil rubble mix.
- Intended flora (design target):
  - Trees: Waytorch Tree
  - Plants: Soulberry Shrub (food), Wayseed Cluster, Black Echo Fungus, Runebloom, Fallseed Pods
- Spawns (monster): Pebblit (weight 7, 1–2), Traprock (weight 3, solo)
- Features:
  - Small Attuned Stone disk: `kruemblegard:attuned_stone_disk_small`
- Ambience:
  - Particles: `minecraft:crimson_spore` (0.0009)
  - Music: `minecraft:music.end`

### `kruemblegard:glyphscar_reach`
- File: `glyphscar_reach.json`
- Temperature: 0.20
- Description: A rune-bright reach where enchant motes float and stone structures feel subtly “active.”
- Climate: Cold; generates snow cover (snow layers placed during worldgen).
- Terrain surface: Fractured Wayrock → Fault Dust, with deeper Scarstone/Cracked Scarstone then Stoneveil/Runed Stoneveil rubble mix.
- Intended flora (design target):
  - Trees: Monument Oak (rare mega variant), Waytorch Tree
  - Plants: Wispstalk (food), Runeblossom, Rune Sprouts, Wayburn Fungus, Gravemint
- Spawns (monster): Traprock (weight 5, solo), Pebblit (weight 5, 1–2)
- Features:
  - Large Attuned Stone disk: `kruemblegard:attuned_stone_disk_large`
- Ambience:
  - Particles: `minecraft:enchant` (0.001)
  - Music: `minecraft:music.end` (shorter delay)
  - Additions: `minecraft:block.enchantment_table.use` (rare)

### `kruemblegard:hollow_transit_plains`
- File: `hollow_transit_plains.json`
- Temperature: 0.10
- Description: Quiet, open transit plains with sparse standing stones and long stretches between points of interest.
- Climate: Cold; generates snow cover (snow layers placed during worldgen).
- Intended flora (design target):
  - Trees: Hollowway Tree
  - Plants: Soulberry Shrub (food), Transit Bloom, Transit Fern, Twilight Bulb, Cairn Moss
- Spawns (monster): Traprock (weight 6, solo), Pebblit (weight 2, solo)
- Features: (none listed)
- Ambience:
  - Music: `minecraft:music.end` (longer delay)

### `kruemblegard:riven_causeways`
- File: `riven_causeways.json`
- Temperature: 0.50
- Description: Broken causeways and riven slabs stitched together by dense standing stones; smoke hangs low.
- Intended flora (design target):
  - Trees: Faultwood, Driftwillow (rare mega variant)
  - Plants: Wispstalk (food), Falsepath Thorns, Sliproot, Ashpetal, Wayrot Fungus
- Spawns (monster): Traprock (weight 10, solo), Pebblit (weight 3, solo)
- Features: (none listed)
- Ambience:
  - Particles: `minecraft:smoke` (0.0006)
  - Music: `minecraft:music.end`

### `kruemblegard:shatterplate_flats`
- File: `shatterplate_flats.json`
- Temperature: 0.90
- Description: Flat, shattered “plates” of stone with warped spores drifting above the cracks; primarily Pebblit territory.
- Intended flora (design target):
  - Trees: Shardbark Pine
  - Plants: Soulberry Shrub (food), Voidcap Briar, Echo Puff, Waybind Creeper, Waypoint Mold
- Spawns (monster): Pebblit (weight 6, 1–2)
- Features: (none listed)
- Ambience:
  - Particles: `minecraft:warped_spore` (0.0005)
  - Music: `minecraft:music.end`

### `kruemblegard:strata_collapse`
- File: `strata_collapse.json`
- Temperature: 1.50
- Description: Collapsed strata terraces where white ash gathers around sparse megaliths and large attuned disks.
- Intended flora (design target):
  - Trees: Cairn Tree, Faultwood
  - Plants: Wispstalk (food), Griefcap, Ashveil
- Spawns (monster): Traprock (weight 7, solo), Pebblit (weight 6, 1–2)
- Features:
  - Large Attuned Stone disk: `kruemblegard:attuned_stone_disk_large`
- Ambience:
  - Particles: `minecraft:white_ash` (0.0007)
  - Music: `minecraft:music.end`

### `kruemblegard:underway_falls`
- File: `underway_falls.json`
- Temperature: 1.00
- Description: A drifting corridor of island fragments where reverse-portal motes flicker like falling embers.
- Intended flora (design target):
  - Trees: Driftwillow (rare mega variant), Waytorch Tree
  - Plants: Soulberry Shrub (food), Driftbloom, Reverse Portal Spores (custom particle plant), Whispervine
- Spawns (monster): Pebblit (weight 5, 1–2), Traprock (weight 5, solo)
- Features:
  - Small Attuned Stone disk: `kruemblegard:attuned_stone_disk_small`
- Ambience:
  - Particles: `minecraft:reverse_portal` (0.0009)
  - Music: `minecraft:music.end`
