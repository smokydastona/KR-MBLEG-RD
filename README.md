# Krümblegård (Forge 1.20.1)

Krümblegård is a Forge mod for Minecraft 1.20.1 featuring a rune-bound stone guardian boss, the **Wayfall** (void / floating-islands) dimension, and a bestiary of strange creatures.

Quick links:
- Feature reference (kept complete): [docs/MOD_FEATURES.md](docs/MOD_FEATURES.md)
- Changelog (matches jar version): [CHANGELOG.md](CHANGELOG.md)

## Requirements
- Minecraft `1.20.1`
- Forge `47.x` (the dev setup targets `1.20.1-47.4.0`)
- Java `17`

Runtime mod dependencies:
- GeckoLib 4.x
- TerraBlender 3.x
- Waystones (requires Balm)
- Optional (client-only): Waystone Injector (`waystoneinjector`)

Note: if you see a shutdown crash like `SimpleCommentedConfig cannot be cast to CommentedFileConfig` when leaving a world, update Forge.

## Highlights
- **Wayfall**: floating-islands void dimension with custom biomes, flora, and rethemed structures.
- **Krümblegård (Boss)**: 4-phase guardian fight with phase-based attacks and synced boss music.
- **Traprock**: looks like stone until disturbed (or linger too close), then awakens and attacks; after your first encounter, most newly found Traprocks won’t hide again.
- **Pebblit**: neutral stone-bug that retaliates; can be tamed with an Echokern; can sit or perch on your shoulder.
- **Cephalari Engineering (Pressure-Logic)**: early implementation (functional pressure network + basic redstone integration + first-pass mechanical rotation backbone) + `Pressure Conduit`, `Membrane Pump`, `Pressure Turbine`, `Spiral Shaft`, `Spiral Gearbox`, `Vent Piston`, `Atmospheric Compressor`, `Pressure Valve`, `Buoyancy Lift Platform`, `Conveyor Membrane`, `Pressure Loom`, `Pressure Clutch`, `Pressure Regulator`, `Pressure Sequencer`, `Pressure Sensor`, `Vortex Funnel`, `Pressure Rail`, `Pneumatic Catapult`, `Air Lift Tube`, `Pressure Kiln`, `Membrane Press`, `Crystal Infuser`, `Pneumatic Separator` blocks (WIP).

For the full, always-up-to-date list (woods, fungi, mobs, compatibility notes, worldgen rules), see [docs/MOD_FEATURES.md](docs/MOD_FEATURES.md).

## Guidebook
Players receive the **Crumbling Codex** once on first join.
- Text source: `src/main/resources/data/kruemblegard/books/crumbling_codex.json`

## Config
Configs generate under the instance `config/` folder.

- `kruemblegard-common.toml`
    - Boss stats/cooldowns and phase thresholds
    - Waystone-related settings (including False Waystones)
    - Wayfall initialization safety/performance toggles
    - Pressure-Logic toggles/performance settings (enable/disable + tick interval)
- `kruemblegard-client.toml`
    - Client-only cosmetic/performance settings
- `kruemblegard-worldgen.json5`
    - Worldgen tuning + optional strict validation (see [docs/MOD_FEATURES.md](docs/MOD_FEATURES.md) → Worldgen)

## Useful commands (testing)
- Spawn Traprock: `/summon kruemblegard:traprock`
- Spawn Pebblit: `/summon kruemblegard:pebblit`
- Spawn Trader Beetle: `/summon kruemblegard:trader_beetle`
- Teleport to Wayfall: `/execute in kruemblegard:wayfall run tp @s 0 160 0`

## Pressure interoperability (Forge capability)
Mods should interact with Krümblegård pressure via the `pressure_conduit` block.

- Capability: `PressureCapabilities.PRESSURE_HANDLER` (`IPressureHandler`)
- Notes: machines participate by reading/writing adjacent conduit pressure; machine blocks do not expose pressure capabilities yet.

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
- Source of truth: `src/main/java` + `src/main/resources` (the root Gradle project compiles from `sourceSets.main`)
- Jar version: `major.minor.<git commit count>` (see `build.gradle`)
    - CI should use a full git checkout (`fetch-depth: 0`) so versions match local.

Handy Gradle tasks:
- `./gradlew clean build`
- `./gradlew prepareRunClientCompile`
- `./gradlew prepareRunServerCompile`

Sound generation:
- `./tools/generate_pregen_sound_bible.ps1` → emits `docs/pregen_sound_manifest.json` + `docs/pregen_sound_bible.md`

## Contributing
- Don’t copy assets from other mods. See [CONTRIBUTING.md](CONTRIBUTING.md).

## Troubleshooting

### JVM crash: `EXCEPTION_ACCESS_VIOLATION` in `jvm.dll`
If you get a native crash report mentioning `EXCEPTION_ACCESS_VIOLATION (0xc0000005)` with a problematic frame inside `jvm.dll` (often in a `C2 CompilerThread`), that’s a JVM/native crash (not a normal Java exception stacktrace).

Common mitigations on Windows:
- Disable overlays/injectors (e.g., capture/overlay apps) and add your instance folder to antivirus exclusions.
- Switch the launcher’s Java runtime to a different Java 17 distribution (Temurin/Adoptium) instead of the bundled runtime.
- Isolation step: disable the Wayfall custom skybox renderer via JVM arg: `-Dkruemblegard.disableWayfallSkybox=true`
