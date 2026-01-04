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

## Blocks
- Ancient Waystone block.
- Standing Stone + Attuned Stone blocks.

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
- If/when worldgen is added back, keep biome modifiers/tags/worldgen JSON consistent.

## Removed / not present (by design)
- `false_waystone` (block + worldgen + biome modifier/tag) was removed.
- `radiant_essence` was removed.
- `runic_core_fragment` was removed.
