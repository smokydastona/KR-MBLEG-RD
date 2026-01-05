# Krümblegård — Mod Features Reference

This document is a **complete, living reference** of current Krümblegård features.

Keep it up to date whenever you add/remove/rename content.

## Core gameplay loop
- **Traprock** can appear as a dormant stone-creature.
- It awakens if a player interacts with it or lingers too close.

## Mobs
- **Traprock** (`traprock`)
  - Implemented as a Blaze-derived GeckoLib mob.
  - Uses editable resources: `geo/traprock.geo.json`, `animations/traprock.animation.json`, `textures/entity/traprock.png`.

- **Pebblit** (`pebblit`)
  - Hostile Silverfish-like creature.
  - Can be tamed by right-clicking with cobblestone; follows its owner.
  - Texture: `assets/kruemblegard/textures/entity/pebblit.png` (rendered via a Silverfish-derived renderer).

## Boss: Krümblegård
- **4 phases** with phase-specific visuals (bone hiding) and locomotion.
- **12 unique attacks** across phases (fast/heavy/ranged kits per phase).
- Phase transitions trigger one-shot transition animations.
- Boss awards **Ender Dragon XP** on death.

Note: Krümblegård is no longer spawned by waystones. It can still be spawned via commands/spawn egg for testing.

## Items & progression
- **Crumbling Codex** (`crumbling_codex`)
  - Given **once per player** on first join.
  - Opens as a **pre-filled guidebook**.
  - Text source (editable): `src/main/resources/data/kruemblegard/books/crumbling_codex.json`.
- **Runic Core** (`runic_core`)
  - Dropped by Krümblegård.
  - Used to craft Runic tools.
- **Runic tool set**
  - Runic Sword (`runic_sword`)
  - Runic Pickaxe (`runic_pickaxe`)
  - Runic Axe (`runic_axe`)
  - Runic Shovel (`runic_shovel`)
  - Runic Hoe (`runic_hoe`)
  - Tier: Runic tools are above Netherite.
  - Crafting: Runic tools require Attuned Rune Shards + a Runic Core catalyst.

## Blocks
- Ancient Waystone block.
- Standing Stone + Attuned Stone blocks.
- Attuned Ore block (Wayfall-only worldgen).
- Wayfall flora (plants + shrubs + fungi), including Wispstalk, Gravevine, Echocap, Runebloom, Soulberry Shrub (can corrupt into Ghoulberry Shrub) and additional Wayfall plants.
  - Detailed reference (keep updated): docs/FLORA_REFERENCE.md
- Wayfall staple flora (new): Voidfern, Runeblossom, Moteshrub, Ashveil, Twilight Bulb, Whispervine.
- Wayfall trees (block sets): logs/planks/leaves/saplings exist as blocks/items.
  - Saplings can grow via random ticks or bonemeal.
    - Wayfall saplings grow into their matching worldgen configured features (variant selectors), matching natural generation.
  - Staple wood sets: Ashbloom, Glimmerpine, Driftwood.
  - A simple custom tree Feature exists for data-driven placement (`registry/ModFeatures` + `world/feature/WayfallSimpleTreeFeature`).

### Reactive staple plants
- Some Wayfall staples use a “reactive” plant base that can activate near blocks tagged as `kruemblegard:waystone_energy_sources`.

## Dimensions
  - Void dimension with End-like floating islands.
  - Uses only Krümblegård Wayfall biomes (no vanilla biomes).
  - Detailed biome list (keep updated): docs/WAYFALL_BIOMES.md
  - Attuned Ore generates here.
  - Spawns in Wayfall are limited to Krümblegård mobs (no vanilla mob spawns).
- Wayfall flora/saplings use the `kruemblegard:wayfall_ground` block tag for valid substrate (so the terrain palette can evolve without hard-coded `END_STONE`).

## Advancements / criteria (project-specific)
- Vanilla advancements are not granted directly.
- `init/ModCriteria` is the home for custom triggers, but none are currently registered.

## Loot
- Krümblegård entity loot table drops Runic Core.
- A global loot modifier exists (see `data/kruemblegard/loot_modifiers/*`).

## UI
- Custom Krümblegård boss bar rendering:
  - Cancels vanilla bossbar rendering for this boss.
  - Draws from a single atlas texture: `assets/kruemblegard/textures/gui/kruemblegard_bossbar.png`.
  - Enhanced-style: icon + overlay + hit-flash.

## Sounds
- Sound events are registered in `registry/ModSounds` and mapped in `assets/kruemblegard/sounds.json`.
- Includes encounter sounds like `kruemblegard.rise` and `kruemblegard.radiant`.

## Config
- Common config: `config/kruemblegard-common.toml`
- Includes `enableWaystones` and `waystoneRarity` (see `config/ModConfig`).

## Worldgen
- Current worldgen is minimal; `init/ModWorldgen` is intentionally empty.
- Wayfall flora placement is data-driven via biome modifiers/tags/worldgen JSON.
- Wayfall flora is injected via Forge biome modifiers:
  - Global “staple” trees/plants are injected into all Wayfall biomes via `data/forge/biome_modifier/add_wayfall_plants.json`.
  - Biome-specific trees/plants are injected via `data/forge/biome_modifier/add_wayfall_*_flora.json`.

## Removed / not present (by design)
- `false_waystone` (block + worldgen + biome modifier/tag) was removed.
- `radiant_essence` was removed.
- `runic_core_fragment` was removed.
