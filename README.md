# Krümblegård (Forge 1.20.1)

Krümblegård is a Forge 1.20.1 mod centered around a rune-bound stone guardian boss, Wayfall (a void/floating-islands dimension), and a small bestiary of GeckoLib-rendered mobs.

Recommended Forge runtime: `1.20.1-47.4.0` (or newer 1.20.1 patch). If you see a shutdown crash like `SimpleCommentedConfig cannot be cast to CommentedFileConfig` when leaving a world, update the instance’s Forge.

Quick references:
- Feature reference (kept complete): [docs/MOD_FEATURES.md](docs/MOD_FEATURES.md)
- Changelog (matches jar version): [CHANGELOG.md](CHANGELOG.md)

## Repo basics
- Mod id: `kruemblegard`
- Base package: `com.kruemblegard`
- Real sources: `src/main/java` and `src/main/resources`
- Jar version format: `major.minor.<git commit count>` (see `build.gradle`)

## Dependencies
This project targets:
- Minecraft `1.20.1`
- Java `17`
- Forge `47.x`

Runtime dependencies (currently declared in `mods.toml` / Gradle):
- GeckoLib 4.x
- TerraBlender 3.x
- Waystones + Balm

## What’s in the mod (high level)
- **Traprock**: starts dormant (no AI / no movement); awakens if a player interacts or lingers too close, then attacks.
- **Pebblit**: hostile stone-bug; tamable with cobblestone; can sit, or perch on your shoulder (granting knockback resistance) until it dies.
- **The Great Hunger**: hostile GeckoLib-rendered mob.
- **Scattered Enderman**: Enderman-derived hostile mob (Wayfall biomes).
- **Moogloom**: Shatterplate Flats-exclusive mooshroom-like creature; shearing converts it to a normal Cow and drops Griefcap.
- **Wayfall**: a void/floating-islands dimension with its own biomes, palette, flora, and structure retheming.
- **Guidebook (Crumbling Codex)**: granted once on first join; page text is data-driven.
    - Text source: `src/main/resources/data/kruemblegard/books/crumbling_codex.json`

## Boss: Krümblegård
- **4 phases**, phase-specific visuals (bone hiding) and attack kits.
- **Boss music** is synchronized to the fight (engaged state).
- **Phase projectiles** support true 3D GeckoLib/Blockbench models (one projectile per phase).

### Blockbench / GeckoLib projectile export targets
These files are intentionally present as placeholders so you can export directly over them:

- Phase 1 bolt
    - Geo: `src/main/resources/assets/kruemblegard/geo/projectiles/kruemblegard_phase1_bolt.geo.json`
    - Animation: `src/main/resources/assets/kruemblegard/animations/projectiles/kruemblegard_phase1_bolt.animation.json`
- Phase 2 bolt
    - Geo: `src/main/resources/assets/kruemblegard/geo/projectiles/kruemblegard_phase2_bolt.geo.json`
    - Animation: `src/main/resources/assets/kruemblegard/animations/projectiles/kruemblegard_phase2_bolt.animation.json`
- Phase 3 meteor
    - Geo: `src/main/resources/assets/kruemblegard/geo/projectiles/kruemblegard_phase3_meteor.geo.json`
    - Animation: `src/main/resources/assets/kruemblegard/animations/projectiles/kruemblegard_phase3_meteor.animation.json`
- Phase 4 beam bolt
    - Geo: `src/main/resources/assets/kruemblegard/geo/projectiles/kruemblegard_phase4_beam_bolt.geo.json`
    - Animation: `src/main/resources/assets/kruemblegard/animations/projectiles/kruemblegard_phase4_beam_bolt.animation.json`

Note: the code currently plays these animation keys:
- `animation.kruemblegard_phase1_bolt.idle`
- `animation.kruemblegard_phase2_bolt.idle`
- `animation.kruemblegard_phase3_meteor.idle`
- `animation.kruemblegard_phase4_beam_bolt.idle`

## Config files
Forge configs:
- COMMON: `config/kruemblegard-common.toml`
    - `Kruemblegard.enableWaystones`, `Kruemblegard.waystoneRarity`
    - `Kruemblegard.Wayfall.*` (init budgeting, preload toggles)
    - `Kruemblegard.Boss.*` (health/armor, ability cooldowns, phase thresholds)
- CLIENT: `config/kruemblegard-client.toml`
    - Cosmetic/perf toggles (distance culling, budgets, projectile trail interval)

Worldgen tuning/validation:
- `config/kruemblegard-worldgen.json5`
    - `strictValidation` can hard-fail early if critical registry IDs/tags are missing.

## Worldgen organization
- Datapack content lives under `src/main/resources/data/kruemblegard/worldgen/**`.
- Code references to important worldgen IDs are centralized in `src/main/java/com/kruemblegard/worldgen/ModWorldgenKeys.java`.
- On server start, `com.kruemblegard.worldgen.WorldgenValidator` validates critical registry entries/tags (warn-only by default).

## Advancements
- Prefer custom triggers and fire them from gameplay code.
- Example trigger: `kruemblegard:pebblit_shoulder` (registered in `ModCriteria`).

## How to run (dev)
Common Gradle commands:
- `./gradlew prepareRunClientCompile`
- `./gradlew runClient`

Quick in-game testing:
- Spawn Traprock: `/summon kruemblegard:traprock`
- Spawn Pebblit: `/summon kruemblegard:pebblit`
- Teleport to Wayfall: `/execute in kruemblegard:wayfall run tp @s 0 160 0`

## Assets
This repo may include placeholders for some binary assets; see `CONTRIBUTING.md` for asset rules.

### Boss bar
Boss bar atlas texture:
- `src/main/resources/assets/kruemblegard/textures/gui/kruemblegard_bossbar.png`

Atlas layout (same as vanilla; 256×32):
- Background: `u=0..181`, `v=0..4`
- Fill: `u=0..181`, `v=5..9`
- Overlay/frame: `u=0..181`, `v=10..14`
- Hit-flash fill: `u=0..181`, `v=15..19`
- Icon: `u=182..197`, `v=0..15`

### Boss music
- Sound event key: `music.kruemblegard`
- Sound definitions: `src/main/resources/assets/kruemblegard/sounds.json`

## Contributing
- Don’t copy assets from other mods.
- Keep docs up to date when behavior changes: README.md, CHANGELOG.md, and docs/MOD_FEATURES.md.
