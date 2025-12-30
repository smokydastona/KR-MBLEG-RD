# Copilot Instructions — KR-MBLEG-RD / Krümblegård (With Safety Features)

## Big picture
- Repo is a Forge 1.20.1 + GeckoLib mod template for Krümblegård.
- **Real sources live here:** `krumblegard_template/src/main/{java,resources}`.
- Root Gradle project compiles the template via `sourceSets.main` (see `build.gradle`).
- Mod id: `krumblegard` | Base package: `com.smoky.krumblegard`.

## Core gameplay architecture (follow this flow)
- **Trigger → persistent controller pattern**:
  - Right-click `HauntedWaystoneBlock` or `FalseWaystoneBlock` → `HauntedWaystoneBlockEntity.activate()`.
  - Trigger removes itself and places invisible `arena_anchor` **below** as the persistent controller.
  - `ArenaAnchorBlockEntity` runs a server-side state machine: `BUILDING → FIGHT → CLEANSE`.
- Arena building lives in `world/arena/ArenaBuilder`.
- Boss spawns underground and emerges via `KrumblegardEntity.beginEmergence()`.
- Multiplayer scaling is applied at spawn in `ArenaAnchorBlockEntity`.

## Advancements & triggers (project-specific)
- Don’t grant vanilla advancements directly.
- Use `init/ModCriteria` triggers from gameplay:
  - `HAUNTED_WAYSTONE_CLICKED` (waystone BE)
  - `KRUMBLEGARD_SURVIVED` / `KRUMBLEGARD_CLEANSED` (arena anchor BE)

## Worldgen (config-driven + data-driven biomes)
- Features are **code-registered** in `init/ModWorldgen`.
- Config is in `config/ModConfig` (COMMON): `enableWaystones`, `waystoneRarity`.
- Biome selection is data-driven:
  - `krumblegard_template/src/main/resources/data/forge/biome_modifier/add_false_waystone.json`
  - `krumblegard_template/src/main/resources/data/krumblegard/tags/worldgen/biome/has_false_waystone.json`
- Avoid adding `data/krumblegard/worldgen/*` JSON for waystones (conflicts with code registration).

## GeckoLib conventions
- Model/animation/texture binding: `client/model/KrumblegardModel` (geo/texture/animation `ResourceLocation`s).
- Renderer registration: `client/ClientModEvents` + `client/render/KrumblegardRenderer`.
- Boss animations/controllers live in `entity/boss/KrumblegardEntity`.

## Boss music
- Key: `music.krumblegard` → registered in `init/ModSounds` and mapped in `assets/krumblegard/sounds.json`.
- Played client-side via `MusicManager` in `client/music/KrumblegardBossMusicHandler`.
- Synced with fight using `KrumblegardEntity.isEngaged()` (SynchedEntityData).

## Assets
- Entity texture: `assets/krumblegard/textures/entity/krumblegard.png`.
- Block texture reuse: `assets/krumblegard/textures/block/standing_stone.png` is intentionally reused by multiple blocks.

## Dev workflows & safety features
- Every change should follow this workflow:
  - Scan likely impact radius first (call sites, registries, data files, client/server split).
  - Fix errors systematically; don’t leave the workspace in a half-compiling state.
  - Re-validate after fixes (build/run) so no new errors slip in.
  - Explain changes (what was wrong, what changed, why).
- Build validation:
  - Never build locally (as Copilot/agent) — rely on GitHub Actions for validation.
  - GitHub Actions is the authoritative “clean environment” build.
- Keep workspace clean:
  - Don’t commit generated outputs like `build/` or `.gradle/`.
- Log handling:
  - When asked to “read the log”, use the newest `latest.log` from the current run; don’t rely on stale copies.
- Releases:
  - Don’t auto-tag or create releases; user manages tags/releases manually.

## Copilot behavior rules (repo-specific)
- Always respect: mod id `krumblegard`, base package `com.smoky.krumblegard`, and asset paths under `assets/krumblegard`.
- Always follow the Trigger → Controller → Boss flow when changing gameplay.
- Client-only features (renderer, music, GeckoLib model wiring) go under `client/`.
- Persistent logic (arena controller, boss state, arena build) goes under `blockentity/`, `entity/`, `world/arena/`.
- Don’t suggest code outside: `krumblegard_template/src/main/java/com/smoky/krumblegard`.

## Build / run
- CI build command: `./gradlew --no-daemon clean build`
- CI: `.github/workflows/build.yml` uses the Gradle wrapper; keep CI on `./gradlew`.

