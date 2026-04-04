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
- Optional (performance): Chunk Optimization

Localization coverage:
- Krümblegård ships language JSON files for every Minecraft Java 1.20.1 supported locale so mod text still resolves when players pick any built-in game language.
- `src/main/resources/assets/kruemblegard/lang/en_us.json` remains the source of truth; run `./tools/sync_lang_files.ps1` after any text-key change so every locale keeps the full key set and falls back to English only for keys that still need translation.
- Non-English locale files must keep the same keys as `en_us.json`; only the values should change during translation passes.
- `python tools/translate_lang_locales.py` remains available only as a draft-generation helper; review its output before shipping it. If mod compound terms are coming through untranslated, try `--hint-compounds` to split certain protected compounds (e.g., `Traprock` -> `Trap rock`) as a translation hint.
- Draft translation runs now persist completed chunks back into the target locale file and reuse a per-language cache under `tools/_reports/translation_cache`, so long seeding passes can resume after interruption instead of retranslating repeated strings.
- If CI is failing only on the obvious-English coverage gate, `python tools/break_obvious_english_fallbacks.py` can clear exact-English fallback values offline by appending a zero-width space.
- Localization tooling dependency: install `tools/requirements-localization.txt` into the repo venv before running `tools/translate_lang_locales.py` on a clean machine.
- CI also runs `./tools/sync_lang_files.ps1 -Verify`, so the build fails if locale sync would rewrite files or if any non-exempt translation locale still looks like obvious English fallback content.
- Optional stricter gate: `./tools/sync_lang_files.ps1 -Verify -RequireModTermTranslations` additionally fails if specific mod name-terms (like `Waystone`) remain effectively English (exact match, or only differing by the zero-width-space coverage marker).

Note: if you see a shutdown crash like `SimpleCommentedConfig cannot be cast to CommentedFileConfig` when leaving a world, update Forge.

## Highlights
- **Wayfall**: floating-islands void dimension with custom biomes, flora, and rethemed structures.
- **Ancient Way Ruins**: a new red-wool-anchored overworld ruin structure sourced directly from `wayfall_temple.schem`, globally limited to three concentric-ring placements per world, hard-blocked from generating within 2048 blocks of world origin, kept away from vanilla strongholds via a structure-set exclusion zone, weathered during placement, mapped by master cartographers, seeded with a lighter ancient-city-style sculk conversion mix where about half the marker sculk weathers back into ruined stone masonry, about ten percent of the remaining sculk darkens into blackstone variants, select purple terracotta fades toward pink, quartz blocks and pillars mineralize into diorite-family stone, and palette-used quartz stairs and slabs now weather into matching polished diorite stair/slab states so the ruin can age without flattening its decorative silhouettes, while the surviving warning network now collapses harder into air, leaves a thinner residual sensor web, and keeps summon-capable shriekers genuinely rare, and restricted to Cephalari zombie-family monster spawns inside its bounding box while summoned Wardens remain valid.
- **Telekinesis enchantment**: a new very-rare enchanting-table/anvil enchantment for tools and weapons only, routing block drops and both melee or projectile kill rewards from the enchanted item into the attacking player's inventory when space allows, spilling only leftovers at the player, playing the vanilla pickup cue on successful absorption, and redirecting mob XP orbs plus later-spawned modded death drops/orbs to the player's feet through the same short-range collector window, appearing in End City and Ancient City loot both as enchanted books and pre-enchanted gear, selling from master librarians as a book, occasionally rolling onto master Fletcher/Toolsmith/Weaponsmith stock at tuned lower rates, and now called out directly in the Crumbling Codex plus native item tooltips.
- **Creative tabs**: Krümblegård blocks and items now live in the mod's own custom creative tabs instead of being mixed into vanilla tab inventories, with the Telekinesis enchanted book remaining visible alongside vanilla enchanted books as the intentional exception.
- **Wayfall hot surfaces**: `emberwarmed_runegrowth` now appears more reliably in the hot surface palette, with Strata Collapse promoted onto the emberwarmed top-layer set and Fracture Shoals/Basin of Scars/Strata Collapse all keeping emberwarmed on the first shallow cut below the surface instead of dropping straight to fault dust.
- **Wayfall ambient music**: the registered `kruemblegard:music.wayfall` biome music event now maps back to `assets/kruemblegard/sounds/horror-background-atmosphere-09_universfield.ogg`, so Wayfall biomes stop logging a missing music warning and play their intended ambient track again.
- **Ashspire flora**: the Wayfall Ashspire cactus line now uses only the canonical live asset set that is actually referenced at runtime, with the cactus routed through chorus-style side/no-side variants, the emberbloom cap kept on canonical `ashspire_emberbloom_*` assets, and the dead cap remaining a separate placeable item state.
- **Cephalari cure/zombify audio**: the Cephalari conversion events now reuse vanilla zombie-villager cure/conversion sounds instead of pointing at missing custom sound files, keeping the log clean until bespoke audio is added again.
- **Scarsteel progression**: Tier 3 Scarstone metallurgy line with a custom diamond-equivalent tool tier, full equipment set, and a direct attuned forging recipe for `scarsteel_ingot`.
- **Krümblegård (Boss)**: 4-phase guardian fight with phase-based attacks and synced boss music.
- **Traprock**: looks like stone until disturbed (or linger too close), then awakens and attacks; after your first encounter, most newly found Traprocks won’t hide again.
- **Driftwhale**: pod-roaming sky-swimmer with slower, body-led GeckoLib motion, an always-on breathing layer, a real hurt-startle reaction, an explicit death animation trigger, and occasional thermal-lift surges so it reads as a weighty airborne creature instead of a fast-flapping fish.
- **Pebble Wren**: small tameable ambient songbird that now naturally roams every Wayfall biome and the Overworld in smaller ambient flocks, while keeping its twitchier perch-and-flight rhythm, ore-direction ping, dedicated custom chirp/flourish/flutter sound set, and a lighter loot table that always yields feathers plus one context-weighted plant seed, favoring Paleweft/Remnant in Wayfall and vanilla crop seeds in the Overworld.
- **Wayfall bats**: vanilla bats now also receive a Wayfall biome-spawn injection tuned to the same lighter ambient pressure band as Pebble Wrens, plus a Wayfall-specific spawn predicate that lets them use island overhangs and enclosed air pockets instead of relying on vanilla darkness-and-sea-level assumptions; Overworld bat spawning stays on vanilla rules instead of being double-added by the mod.
- **Scaralon Beetle**: rune-etched flying mount whose eggs now cluster into 1-4 egg turtle-style clutches, and whose orphaned larvae look for reachable tree trunks, climb onto the bark, and sap-suck to cut their maturation time in half whenever no adult Scaralon is nearby.
- **Mossback Tortoise**: armored grazer that is shearable for Moss Carpet, regrows its shell moss by grazing on grass blocks or any of the four runegrowth variants, and now shares those runegrowth grazing rules with vanilla sheep wool regrowth.
- **Fault Crawler handedness**: Fault Crawlers now roll their mirrored left-handed variant at a true 50/50 split on spawn instead of relying on vanilla's rare left-handed mob chance, so the GeckoLib mirrored animation set shows up evenly across the species.
- **Pebblit**: neutral stone-bug that retaliates, can be tamed with an Echokern, can sit or perch on your shoulder, and now naturally spawns across Basin of Scars, Crumbled Crossing, Fracture Shoals, Shatterplate Flats, Strata Collapse, and Underway Falls.
- **Grave Cairn**: dormant rubble ambusher that now rolls into every Wayfall biome while still favoring high-altitude spawn positions.
- **Cephalari Golem**: attuned village guardian that now behaves like a vanilla Iron Golem, with Cephalari-specific rendering, a Runebloom flower-offering visual swap, and separate passive/angry idle and move animation sets. Its village replacement handoff now preserves the normal Iron Golem spawn initialization, the angry locomotion state drops back to passive as soon as the golem loses its target, and because the entity still inherits from `IronGolem`, other mobs keep treating it through the same vanilla golem-targeting class checks.
- **Zombified Cephalari**: baby undead Cephalari always spawn as jockeys, and their random animal mounts now inherit the same target classes a zombie would attack while also copying the rider's current target so the mount keeps fighting if the Cephalari rider dies first.
- **Zombie jockey mount support**: the passive animal mount pool used by baby undead Cephalari now receives Forge-side `attack_damage` attribute support, so chickens, pigs, sheep, and rabbits can safely run the inherited hostile melee AI instead of crashing when they try to attack.
- **Cephalari workstations**: the Brine Garden Basin and Tendril-Forge are functional job-site blocks for custom Cephalari professions. Keepers propagate berries, seeds, and brine cultures, while Architects spin resonance-fed tendril stock from runic inputs. Both stations use local attuned/runic resonance bonuses instead of the retired machinery subsystem, both professions render dedicated live/zombified overlay attire, their POIs are wired into vanilla job-site acquisition tags so villagers and Cephalari can claim them naturally, and their trade pools now culminate in themed master-rank gift/masterwork offers with tighter late-tier restock caps.
- **Mirrored custom mob variants**: custom Geo-rendered mobs now pick a deterministic left-handed mirrored variant from their UUID, so some spawns permanently read as the opposite-handed version of the authored animation set without needing separate animation files.
- **Cephalari adult forms**: Spiral Strider, DriftSkimmer, Treadwinder, and Echo Harness all share the embedded Cephalari body/profession texture mapping path, render through explicit shell/body/profession layer passes for both live Cephalari appearances and stand-alone adult-form entities, and now keep their full per-form animation routing on the live Cephalari path as well, so idle, move, trade, work, celebrate, hurt, zombify, death, and sleep all resolve from the matching adult-form animation file. Their in-bed pose mirror remains restricted to east-west bed facings and applies separately from the spawn-time left-handed variant so the disconnected sleep rigs do not clip into one side of the bed.

For the full, always-up-to-date list (woods, fungi, mobs, compatibility notes, worldgen rules), see [docs/MOD_FEATURES.md](docs/MOD_FEATURES.md).

## Guidebook
Players receive the **Crumbling Codex** once on first join.
- Text source: `src/main/resources/data/kruemblegard/books/crumbling_codex.json`

## Config
Configs generate under the instance `config/` folder.

- `kruemblegard-common.toml`
    - Boss stats/cooldowns and phase thresholds
    - Waystone-related settings (including False Waystones)
    - Telekinesis villager pricing/chance tuning for librarians, fletchers, toolsmiths, and weaponsmiths
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
- Wayfall biome music key: `music.wayfall` → `assets/kruemblegard/sounds/horror-background-atmosphere-09_universfield.ogg`

## For developers (repo)
- Mod id: `kruemblegard` | Base package: `com.kruemblegard`
- Source of truth: `src/main/java` + `src/main/resources` (the root Gradle project compiles from `sourceSets.main`)
- Jar version: `major.minor.<git commit count>` (see `build.gradle`)
    - CI should use a full git checkout (`fetch-depth: 0`) so versions match local.
- GitHub Actions build workflow: uses Node 24-ready `actions/checkout`, `actions/setup-java`, `gradle/actions/setup-gradle`, and `actions/upload-artifact` majors to stay ahead of the GitHub-hosted runner Node 20 retirement.
- Localization workflow: edit `src/main/resources/assets/kruemblegard/lang/en_us.json`, run `./tools/sync_lang_files.ps1`, refresh any non-English locale values you are actively translating, then verify with `./tools/sync_lang_files.ps1 -Verify` before committing.

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
