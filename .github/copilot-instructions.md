# Copilot Instructions — Krümblegård Mod

## Big picture
- Repo is a Forge 1.20.1 + GeckoLib mod template for Krümblegård.
- **Real sources live here:** `src/main/{java,resources}`.
- Root Gradle project compiles the template via `sourceSets.main` (see `build.gradle`).
- Mod id: `kruemblegard` | Base package: `com.kruemblegard`.

## Core gameplay architecture (follow this flow)
- **Trigger → persistent controller pattern**:
  - Right-click `HauntedWaystoneBlock` or `FalseWaystoneBlock` → `HauntedWaystoneBlockEntity.activate()`.
  - Trigger removes itself and places invisible `arena_anchor` **below** as the persistent controller.
  - `ArenaAnchorBlockEntity` runs a server-side state machine: `BUILDING → FIGHT → CLEANSE`.
- Arena building lives in `world/arena/ArenaBuilder`.
- Boss spawns underground and emerges via `KruemblegardBossEntity.beginEmergence()`.
- Multiplayer scaling is applied at spawn in `ArenaAnchorBlockEntity`.

## Advancements & triggers (project-specific)
- Don’t grant vanilla advancements directly.
- Use `init/ModCriteria` triggers from gameplay:
  - `HAUNTED_WAYSTONE_CLICKED` (waystone BE)
  - `KRUEMBLEGARD_SURVIVED` / `KRUEMBLEGARD_CLEANSED` (arena anchor BE)

## Worldgen (config-driven + data-driven biomes)
- Features are **code-registered** in `init/ModWorldgen`.
- Config is in `config/ModConfig` (COMMON): `enableWaystones`, `waystoneRarity`.
- Biome selection is data-driven:
  - `src/main/resources/data/forge/biome_modifier/add_false_waystone.json`
  - `src/main/resources/data/kruemblegard/tags/worldgen/biome/has_false_waystone.json`
- Avoid adding `data/kruemblegard/worldgen/*` JSON for waystones (conflicts with code registration).

## GeckoLib conventions
- Model/animation/texture binding: `client/render/model/KruemblegardBossModel` (geo/texture/animation `ResourceLocation`s).
- Renderer registration: `client/KruemblegardClient` + `client/render/KruemblegardBossRenderer`.
- Boss animations/controllers live in `entity/KruemblegardBossEntity`.

## Boss music
- Key: `music.kruemblegard` → registered in `registry/ModSounds` and mapped in `assets/kruemblegard/sounds.json`.
- Synced with fight using `KruemblegardBossEntity.isEngaged()` (SynchedEntityData).

## Assets
- Entity texture: `assets/kruemblegard/textures/entity/kruemblegard.png`.
- Block texture reuse: `assets/kruemblegard/textures/block/standing_stone.png` is intentionally reused by multiple blocks.

## Dev workflows & safety features
- Every change should follow this workflow:
  - Scan likely impact radius first (call sites, registries, data files, client/server split).
  - Fix errors systematically; don’t leave the workspace in a half-compiling state.
  - Re-validate after fixes (build/run) so no new errors slip in.
  - Explain changes (what was wrong, what changed, why).

### “Scan likely impact radius” definition (do this before committing)
- This does **not** mean “read every file in the repo”. It means: identify what the change touches and proactively scan the *connected* files/registries/data that must remain consistent so we don’t ship a new jar that just crashes somewhere else.

### Impact radius checklists
- **Worldgen / datapack changes** (`src/main/resources/data/**/worldgen/**`, biome tags, biome modifiers)
  - Verify all referenced IDs exist (features, structures, structure_sets, template_pools, processor_lists, tags)
  - Grep for common invalid/renamed vanilla IDs (e.g., biomes like `minecraft:mountains` are invalid in 1.20.1)
  - Verify structure_set placement schema (e.g., `minecraft:random_spread` includes required fields like `spread_type`)
  - Check `data/forge/biome_modifier/*.json` references correct tags/ids
- **Registries / DeferredRegister changes**
  - Check all `RegistryObject` usages for eager `.get()` during registration
  - Check cross-registry references (items referencing entities/blocks; block entities referencing blocks)
  - Confirm client-only registrations stay under `client/`
- **Assets / GeckoLib JSON changes**
  - Confirm `assets/kruemblegard/**` file paths match code `ResourceLocation`s
  - Ensure animation/model JSONs are well-formed and avoid known fragile patterns (e.g., keyframes)
- **Gameplay flow changes**
  - Re-scan Trigger → Controller → Boss flow to ensure trigger placement/removal and server/client separation still holds

### CI-first default (IMPORTANT)
- If the user reports a crash/CI failure or asks for a fix, and you make code/resource changes:
  - **Stage + commit + push by default** to trigger GitHub Actions and produce an updated jar artifact.
  - Only skip commit/push if the user explicitly asks you not to, or if you’re still mid-debug and expect more immediate edits.
- If there are uncommitted changes when the user expects a new jar, treat that as a bug in the workflow and push.
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
- Always respect: mod id `kruemblegard`, base package `com.kruemblegard`, and asset paths under `assets/kruemblegard`.
- Always follow the Trigger → Controller → Boss flow when changing gameplay.
- Client-only features (renderer, music, GeckoLib model wiring) go under `client/`.
- Persistent logic (arena controller, boss state, arena build) goes under `blockentity/`, `entity/`, `world/arena/`.
- Don’t suggest code outside: `src/main/java/com/kruemblegard`.

## Build / run
- CI build command: `./gradlew --no-daemon clean build`
- CI: `.github/workflows/build.yml` uses the Gradle wrapper; keep CI on `./gradlew`.

