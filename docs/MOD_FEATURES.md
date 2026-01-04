# Krümblegård — Mod Features Reference

This document is a **complete, living reference** of current Krümblegård features.

Keep it up to date whenever you add/remove/rename content.

## Core gameplay loop
- **Haunted Waystone**: right-clicking triggers the encounter.
- Haunted Waystone removes itself and places an invisible persistent controller: **Arena Anchor** (`arena_anchor`) below.
- **Arena Anchor state machine (server-side)**: `BUILDING → FIGHT → CLEANSE`.
- Arena builds upward in layers, then places standing stones.
- **Krümblegård boss spawns underground and emerges** (no teleport).
- When the boss is defeated, the arena cleanses and the **Ancient Waystone** forms at the center.

## Anti-cheese / arena rules
- Arena enforcement checks player distance from center.
- Leaving the arena applies **Slowness + Mining Fatigue**, and the boss heals.

## Boss: Krümblegård
- **4 phases** with phase-specific visuals (bone hiding) and locomotion.
- **12 unique attacks** across phases (fast/heavy/ranged kits per phase).
- Phase transitions trigger one-shot transition animations.
- Boss awards **Ender Dragon XP** on death.

## Multiplayer scaling
- On spawn, the boss scales based on number of players present in the arena:
  - Health: +50% per extra player
  - Damage: +15% per extra player

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
- Haunted Waystone block + block entity.
- Ancient Waystone block (result of cleansing).
- Arena Anchor block + block entity (persistent encounter controller).
- Standing Stone + Attuned Stone blocks.

## Advancements / criteria (project-specific)
- Vanilla advancements are not granted directly.
- Custom triggers (registered in `init/ModCriteria`):
  - `kruemblegard:haunted_waystone_clicked`
  - `kruemblegard:kruemblegard_survived`
  - `kruemblegard:kruemblegard_cleansed`

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
