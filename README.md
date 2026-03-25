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
- **Ashspire flora**: the Wayfall Ashspire cactus line now uses only the canonical live asset set that is actually referenced at runtime, with the cactus routed through chorus-style side/no-side variants, the emberbloom cap kept on canonical `ashspire_emberbloom_*` assets, and the dead cap remaining a separate placeable item state.
- **Scarsteel progression**: Tier 3 Scarstone metallurgy line with a custom diamond-equivalent tool tier, full equipment set, and a direct attuned forging recipe for `scarsteel_ingot`.
- **Krümblegård (Boss)**: 4-phase guardian fight with phase-based attacks and synced boss music.
- **Traprock**: looks like stone until disturbed (or linger too close), then awakens and attacks; after your first encounter, most newly found Traprocks won’t hide again.
- **Driftwhale**: pod-roaming sky-swimmer with slower, body-led GeckoLib motion, an always-on breathing layer, a real hurt-startle reaction, an explicit death animation trigger, and occasional thermal-lift surges so it reads as a weighty airborne creature instead of a fast-flapping fish.
- **Pebble Wren**: small tameable Wayfall bird with a twitchier songbird rhythm; it now prefers higher visible perches like branches and posts, answers perch chirps through the nearest perched Wren with tiny flourishes and slightly varied calls, can sometimes trade one extra quiet reply, coordinates short perch hops a bit more in small flocks, gathers into little ground-pecking and playful hop groups when wild, will sometimes flush into the air together as a flock, now also flushes on specific stimuli like a nearby sprinting player or sudden damage, gives its ore-direction ping when befriended, and now uses a dedicated custom sound set with in-house procedural chirps plus a lighter trimmed wing-flutter cue from the PSFX example library instead of vanilla parrot placeholders.
- **Scaralon Beetle**: rune-etched flying mount whose eggs now cluster into 1-4 egg turtle-style clutches, and whose orphaned larvae look for reachable tree trunks, climb onto the bark, and sap-suck to cut their maturation time in half whenever no adult Scaralon is nearby.
- **Pebblit**: neutral stone-bug that retaliates; can be tamed with an Echokern; can sit or perch on your shoulder.
- **Cephalari Golem**: attuned village guardian that slowly loses stability away from runic structures and recharges near attuned stonework.

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
- `kruemblegard-client.toml`
    - Client-only cosmetic/performance settings
- `kruemblegard-worldgen.json5`
    - Worldgen tuning + optional strict validation (see [docs/MOD_FEATURES.md](docs/MOD_FEATURES.md) → Worldgen)

## Useful commands (testing)
- Spawn Traprock: `/summon kruemblegard:traprock`
- Spawn Pebblit: `/summon kruemblegard:pebblit`
- Spawn Trader Beetle: `/summon kruemblegard:trader_beetle`
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

### Gradle fails to delete `build/` on Windows
If Gradle fails with errors like “Unable to delete directory …\build” (often when the repo lives under OneDrive), this repo automatically relocates `buildDir` to `%LOCALAPPDATA%/kruemblegard-build` on Windows to avoid sync/file-lock issues.

### JVM crash: `EXCEPTION_ACCESS_VIOLATION` in `jvm.dll`
If you get a native crash report mentioning `EXCEPTION_ACCESS_VIOLATION (0xc0000005)` with a problematic frame inside `jvm.dll` (often in a `C2 CompilerThread`), that’s a JVM/native crash (not a normal Java exception stacktrace).

Common mitigations on Windows:
- Disable overlays/injectors (e.g., capture/overlay apps) and add your instance folder to antivirus exclusions.
- Switch the launcher’s Java runtime to a different Java 17 distribution (Temurin/Adoptium) instead of the bundled runtime.
- Isolation step: disable the Wayfall custom skybox renderer via JVM arg: `-Dkruemblegard.disableWayfallSkybox=true`
