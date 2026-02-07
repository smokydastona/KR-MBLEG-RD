# Krümblegård (Forge 1.20.1)

Krümblegård adds a rune-bound stone guardian boss, Wayfall (a void/floating-islands dimension), and a small bestiary of strange creatures.

Quick links:
- Feature reference (complete): [docs/MOD_FEATURES.md](docs/MOD_FEATURES.md)
- Changelog (matches jar version): [CHANGELOG.md](CHANGELOG.md)

## Install
Requirements:
- Minecraft `1.20.1`
- Forge `47.x` (recommended: `1.20.1-47.4.0` or newer)
- Java `17`

This mod currently expects these mods to be present (declared in `mods.toml`):
- GeckoLib 4.x
- TerraBlender 3.x
- Waystones (and Balm)

If you see a shutdown crash like `SimpleCommentedConfig cannot be cast to CommentedFileConfig` when leaving a world, update your Forge version.

## What you’ll find
- **Wayfall**: a floating-islands void dimension with its own biomes, palette, flora, and rethemed vanilla structures.
- **Wayfall woods**: 14 custom wood families with vanilla-style building sets (including signs/hanging signs and boats/chest boats).
- **Krümblegård (Boss)**: a 4-phase stone guardian fight with phase-based attack kits and synced boss music.
- **Traprock**: looks like stone until you disturb it (or linger too close), then it awakens and attacks.
- **Pebblit**: hostile stone-bug you can tame with cobblestone; can sit or perch on your shoulder.
- **The Great Hunger**: hostile creature.
- **Scattered Enderman**: an Enderman variant that stalks Wayfall.
- **Moogloom**: a Shatterplate Flats mooshroom-like creature; shearing turns it into a normal cow and drops Griefcap.
- **Wayfall fungi**: nine fungi that can be bonemealed into giant cap/stem variants, with natural giant generation.

## Guidebook
Players receive the **Crumbling Codex** once on first join.
- Text source (for pack makers): `src/main/resources/data/kruemblegard/books/crumbling_codex.json`

## Config (optional)
Configs are generated under your instance’s `config/` folder.

- `kruemblegard-common.toml`
    - Boss stats/cooldowns and phase thresholds
    - Wayfall initialization safety/performance toggles
    - Waystone-related settings
- `kruemblegard-client.toml`
    - Client-only cosmetic/performance settings

## Useful commands (for testing)
- Spawn Traprock: `/summon kruemblegard:traprock`
- Spawn Pebblit: `/summon kruemblegard:pebblit`
- Teleport to Wayfall: `/execute in kruemblegard:wayfall run tp @s 0 160 0`

## For pack makers / artists

### Blockbench / GeckoLib projectile export targets
Krümblegård’s phase projectiles support true 3D GeckoLib/Blockbench models (one projectile per phase). These placeholders exist so you can export directly over them:

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

Animation keys currently used by code:
- `animation.kruemblegard_phase1_bolt.idle`
- `animation.kruemblegard_phase2_bolt.idle`
- `animation.kruemblegard_phase3_meteor.idle`
- `animation.kruemblegard_phase4_beam_bolt.idle`

### Boss UI / audio
- Boss bar atlas: `src/main/resources/assets/kruemblegard/textures/gui/kruemblegard_bossbar.png`
- Music key: `music.kruemblegard` (see `src/main/resources/assets/kruemblegard/sounds.json`)

## For developers (repo)
- Mod id: `kruemblegard` | Base package: `com.kruemblegard`
- Sources: `src/main/java` + `src/main/resources`
- Jar version: `major.minor.<git commit count>` (see `build.gradle`)

## Contributing
- Don’t copy assets from other mods. See `CONTRIBUTING.md`.

## Troubleshooting

### JVM crash: `EXCEPTION_ACCESS_VIOLATION` in `jvm.dll`
If you get a native crash report mentioning `EXCEPTION_ACCESS_VIOLATION (0xc0000005)` with a *problematic frame* inside `jvm.dll` (often in a `C2 CompilerThread`), that’s a JVM/native crash (not a normal Java exception stacktrace).

Common mitigations for modpacks on Windows:
- Disable overlays/injectors (e.g., Overwolf capture/overlay) and add your instance folder to antivirus exclusions.
- Switch the launcher’s Java runtime to a different Java 17 distribution (Temurin/Adoptium) instead of the bundled runtime.
- Isolation step: disable the Wayfall custom skybox renderer:
    - Add JVM arg: `-Dkruemblegard.disableWayfallSkybox=true`
