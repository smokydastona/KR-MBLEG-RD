# Copilot instructions (KR-MBLEG-RD / Krümblegård)
# Copilot instructions (KR-MBLEG-RD / Krümblegård)

## Repo map (important)
- Forge 1.20.1 + GeckoLib mod.
- **Real sources live here:** `krumblegard_template/src/main/{java,resources}`.
- Root Gradle build is a wrapper that compiles the template via `sourceSets.main` (see `build.gradle`).
- Mod id: `krumblegard` | Base package: `com.smoky.krumblegard`.

## Gameplay architecture (follow this flow)
- **Trigger → persistent controller**:
  - Right-click `HauntedWaystoneBlock` or `FalseWaystoneBlock` → `HauntedWaystoneBlockEntity.activate()`.
  - The trigger removes itself and places invisible `arena_anchor` **below**.
  - `ArenaAnchorBlockEntity` runs a server-side state machine: `BUILDING → FIGHT → CLEANSE`.
- Arena building lives in `world/arena/ArenaBuilder`.
- Boss spawns underground and emerges via `KrumblegardEntity.beginEmergence()`.
- Multiplayer scaling is applied at spawn in `ArenaAnchorBlockEntity`.

## Advancements (project-specific)
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

## GeckoLib + client conventions
- Geo bindings: `client/model/KrumblegardModel` (geo/texture/animation ResourceLocations).
- Renderer registration: `client/ClientModEvents` + `client/render/KrumblegardRenderer`.
- Boss animations/controllers live in `entity/boss/KrumblegardEntity`.

## Boss music (custom track)
- Sound event key: `music.krumblegard` (see `init/ModSounds` + `assets/krumblegard/sounds.json`).
- Music playback is client-side via `client/music/KrumblegardBossMusicHandler` using `MusicManager`.
- Engage state is synced via `KrumblegardEntity.isEngaged()` (SynchedEntityData).

## Assets
- Entity texture: `assets/krumblegard/textures/entity/krumblegard.png`.
- `assets/krumblegard/textures/block/standing_stone.png` is intentionally reused by multiple blocks.

## Build / run
- Local build: `./gradlew --no-daemon clean build`
- Dev client: `./gradlew runClient`
- CI: `.github/workflows/build.yml` uses the Gradle wrapper; keep CI on `./gradlew`.


Copilot Instructions — KR-MBLEG-RD / Krümblegård (With Safety Features)
