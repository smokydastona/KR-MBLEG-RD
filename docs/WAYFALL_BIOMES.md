# Wayfall Biomes — Detailed Reference

This document is the **authoritative, detailed list** of Wayfall biomes.

## Update rules (keep this accurate)
- If you add/remove/rename any Wayfall biome JSON under `data/kruemblegard/worldgen/biome/`, update this doc in the same change.
- If you change a biome’s **spawns**, **particles**, **music**, or **features list**, update the matching entry here.

## Global Wayfall biome invariants (current)
- **No precipitation**: all biomes set `has_precipitation: false` and `downfall: 0.0`.
- **No vanilla spawns**: all spawn lists are intentionally limited to Krümblegård mobs.
- **No carvers**: `carvers.air` is empty.
- Sky is always black (`sky_color: 0`).
- Terrain is floating-island style above the Y=130 cutoff; islands should be common enough that all Wayfall biomes can be found for progression.
- Flora placement is enforced via Forge biome modifiers (global staples + per-biome sets), and density is primarily controlled by each placed feature’s `rarity_filter`.
  - Some Wayfall trees include rare mega variants in natural generation (Wayroot, Glimmerpine, Driftwillow, Monument Oak).
- Surface palette can be biome-driven via Wayfall noise settings (`kruemblegard:wayfall`) using biome tags:
  - `#kruemblegard:wayfall_ash_heavy` → surface defaults to **Ashfall Loam** with shallow **Crushstone** beneath.
  - `#kruemblegard:wayfall_void` → surface defaults to **Voidfelt** (rare; currently only **Shatterplate Flats**) with shallow **Crushstone** beneath.

## Biome index
All biomes live in `src/main/resources/data/kruemblegard/worldgen/biome/`.

### `kruemblegard:basin_of_scars`
- File: `basin_of_scars.json`
- Description: A smoke-hazed basin dotted with dense standing stones; feels heavy and volcanic.
- Intended flora (design target):
  - Trees: Shardbark Pine, Fallbark
  - Plants: Soulberry Shrub (food), Faultgrass, Dustpetal, Gravevine, Ruin Thistle
- Spawns (monster): Traprock (weight 8, solo), Pebblit (weight 1, solo)
- Features: (none listed)
- Ambience:
  - Particles: `minecraft:smoke` (0.0007)
  - Music: `minecraft:music.nether.basalt_deltas`
  - Additions: `minecraft:ambient.soul_sand_valley.mood`

### `kruemblegard:betweenlight_void`
- File: `betweenlight_void.json`
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
- Description: Shallow “shoals” of broken stone islands with faint crimson spores drifting through the air.
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
- Description: A rune-bright reach where enchant motes float and stone structures feel subtly “active.”
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
- Description: Quiet, open transit plains with sparse standing stones and long stretches between points of interest.
- Intended flora (design target):
  - Trees: Hollowway Tree
  - Plants: Soulberry Shrub (food), Transit Bloom, Transit Fern, Twilight Bulb, Cairn Moss
- Spawns (monster): Traprock (weight 6, solo), Pebblit (weight 2, solo)
- Features: (none listed)
- Ambience:
  - Music: `minecraft:music.end` (longer delay)

### `kruemblegard:riven_causeways`
- File: `riven_causeways.json`
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
