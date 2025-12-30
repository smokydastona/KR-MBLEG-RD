# Copilot instructions (KR-MBLEG-RD / Krümblegård)

## Big picture
- This repo is a Forge 1.20.1 + GeckoLib mod template.
- **All mod sources live under** `krumblegard_template/src/main/{java,resources}`.
- The root Gradle project compiles those sources by wiring `sourceSets.main` to the template paths (see `build.gradle`).
- Mod id is **`krumblegard`** and base package is **`com.smoky.krumblegard`** (see `KrumblegardMod.java`, `mods.toml`, and `assets/krumblegard`).

## Core gameplay architecture (follow these patterns)
- **Trigger → persistent controller** pattern:
  - Right-click `HauntedWaystoneBlock` / `FalseWaystoneBlock` triggers `HauntedWaystoneBlockEntity.activate()`.
  - The waystone removes itself and places an invisible `arena_anchor` block **below** as a persistent controller.
  - `ArenaAnchorBlockEntity` runs a server-side state machine: BUILDING → FIGHT → CLEANSE.
- **Arena building** is centralized in `world/arena/ArenaBuilder` (floor layers + standing stone ring).
- **Boss lifecycle**:
  - Boss is spawned underground and emerges (no teleport) via `KrumblegardEntity.beginEmergence()`.
  - Multiplayer scaling is applied at spawn time in `ArenaAnchorBlockEntity`.

## Advancements (project-specific)
- Do **not** award vanilla advancements directly.
- Use the custom triggers in `init/ModCriteria` and trigger them from gameplay flow:
  - `HAUNTED_WAYSTONE_CLICKED` from `HauntedWaystoneBlockEntity`
  - `KRUMBLEGARD_SURVIVED` / `KRUMBLEGARD_CLEANSED` from `ArenaAnchorBlockEntity`

## Worldgen (config-driven)
- False waystone generation is **code-registered** in `init/ModWorldgen`.
- Enable/rarity is controlled by `config/ModConfig` (COMMON): `enableWaystones`, `waystoneRarity`.
- Biome selection is data-driven:
  - biome modifier: `src/main/resources/data/forge/biome_modifier/add_false_waystone.json`
  - biome tag: `src/main/resources/data/krumblegard/tags/worldgen/biome/has_false_waystone.json`
- Avoid reintroducing `data/krumblegard/worldgen/*` JSON for this feature (it will conflict with code registration).

## GeckoLib conventions
- Entity model/anim/texture binding is in `client/model/KrumblegardModel`.
- Renderer registration is via `client/ClientModEvents`.
- Boss animations are controller-driven in `entity/boss/KrumblegardEntity` (movement + triggerable combat anims).

## Boss music (custom track)
- Sound event key: `music.krumblegard` registered in `init/ModSounds`.
- Music is played client-side through `MusicManager` in `client/music/KrumblegardBossMusicHandler`.
- Engagement is synced via `KrumblegardEntity.isEngaged()` (SynchedEntityData) so music starts only after the fight begins.

## Assets expectations
- Entity texture path: `assets/krumblegard/textures/entity/krumblegard.png`.
- Block texture reuse: `assets/krumblegard/textures/block/standing_stone.png` is intentionally used by multiple blocks.

## Dev workflows
- Build: `./gradlew --no-daemon clean build`
- Run client (dev): `./gradlew runClient`
- CI uses the Gradle wrapper (see `.github/workflows/build.yml`); don’t switch CI to system `gradle`.
