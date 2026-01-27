# Krümblegård — Mod Features Reference

This document is a **complete, living reference** of current Krümblegård features.

Keep it up to date whenever you add/remove/rename content.

## Documentation
- Block material rules + full block inventory: `docs/Block_Material_Bible.md`
- Item material rules + full item inventory: `docs/Item_Material_Bible.md`
- Sound rules + full sound inventory (with exact length requirements): `docs/Sound_Bible.md`
- After registry changes, refresh the generated inventories by running: `tools/generate_material_bibles.ps1`
- After sound changes, refresh the generated inventory by running: `tools/generate_sound_bible.ps1`

## Performance (Client)
- Client config file: `config/kruemblegard-client.toml`
- `Kruemblegard.Performance.Client.enableDistanceCulledCosmetics`
  - Enables cheap distance-based culling for Krümblegård-only cosmetic effects (particles/local ambience).
  - Intended to reduce client work without changing gameplay.
- `Kruemblegard.Performance.Client.cosmeticCullDistanceBlocks`
  - Max distance (blocks) at which cosmetic effects are spawned.
- `Kruemblegard.Performance.Client.cosmeticVerticalStretch`
  - Vertical stretch for culling checks. Effective distance uses: $dx^2 + dz^2 + (dy \cdot stretch)^2$.
- `Kruemblegard.Performance.Client.cosmeticCullEpsilonBlocks`
  - Conservative slack (blocks) added to cosmetic culling decisions to reduce flicker/false culls.
- `Kruemblegard.Performance.Client.enableViewConeCulledCosmetics`
  - Optional view-cone early-out for cosmetics (cheap frustum-ish check).
  - Disabled by default because some players prefer effects even when looking away.
- `Kruemblegard.Performance.Client.cosmeticViewConeHalfAngleDegrees`
  - Half-angle for the view cone (higher = less culling).
- `Kruemblegard.Performance.Client.cosmeticViewConeMarginDegrees`
  - Extra conservative margin added to the cone to reduce false culls.
- `Kruemblegard.Performance.Client.enableCosmeticParticleBudget`
  - Enables a per-tick budget for Krümblegård cosmetic particle spawning (caps worst-case spikes).
- `Kruemblegard.Performance.Client.cosmeticParticleBudgetPerTick`
  - The per-tick particle cap used by the cosmetic budget.
- `Kruemblegard.Performance.Client.enableRuntimeChecks`
  - Enables extra validation for performance helpers (primarily for development/debugging).
  - Also supports JVM flag: `-Dkruemblegard.checks=true`.
- `Kruemblegard.Performance.Client.projectileParticleSpawnIntervalTicks`
  - Trail particle spawn interval for Krümblegård projectiles. `1` = every tick.

## Performance (Debug)
- JVM flag: `-Dkruemblegard.debug.runegrowthTicks=true`
  - Logs Runegrowth `randomTick` counts per second, per dimension (server-side).
  - Useful for confirming whether Runegrowth spread/ticking is contributing to Wayfall lag.

## Worldgen (Tuning + Validation)
- Worldgen tuning config file: `config/kruemblegard-worldgen.json5`
  - Auto-created and auto-extended when new keys are added.
  - `strictValidation` (default `false`): when enabled, `WorldgenValidator` hard-fails at server start if critical worldgen registry IDs/tags are missing.
- Optional TerraBlender integration (Overworld): controlled by weights and per-biome toggles inside `terraBlender.overworld` in the same config.

## Core gameplay loop
- **Traprock** can appear as a dormant stone-creature.
- It awakens if a player interacts with it or lingers too close.
- After a player has encountered Traprock once, newly found Traprock have a high chance to awaken immediately.

## Mobs
- **Traprock** (`traprock`)
  - Implemented as a Blaze-derived GeckoLib mob.
  - Uses editable resources: `geo/traprock.geo.json`, `animations/traprock.animation.json`, `textures/entity/traprock.png`.

- **Pebblit** (`pebblit`)
  - Hostile Silverfish-like creature.
  - Can be tamed by right-clicking with cobblestone; follows its owner.
  - Right-click with empty hand (non-shift): toggles **sit and stay**.
  - Shift + right-click with empty hand: perches on the owner's shoulder **until it dies**, granting the owner **knockback resistance** while perched.
  - Pebblit attacks apply **knockback**.
  - Rendered via GeckoLib:
    - Geo: `assets/kruemblegard/geo/pebblit.geo.json`
    - Animations: `assets/kruemblegard/animations/pebblit.animation.json`
    - Texture: `assets/kruemblegard/textures/entity/pebblit.png`

- **The Great Hunger** (`great_hunger`)
  - Hostile mob (GeckoLib-rendered).
  - Uses editable resources:
    - Geo: `assets/kruemblegard/geo/great_hunger.geo.json`
    - Animations: `assets/kruemblegard/animations/great_hunger.animation.json`
    - Texture: `assets/kruemblegard/textures/entity/great_hunger.png` (currently a placeholder)

- **Scattered Enderman** (`scattered_enderman`)
  - Enderman-derived hostile mob.
  - Spawns **uncommonly** in every Wayfall biome.
  - Rendered via GeckoLib with editable resources:
    - Geo: `assets/kruemblegard/geo/scattered_enderman.geo.json`
    - Animations: `assets/kruemblegard/animations/scattered_enderman.animation.json`
    - Animation controller reference: `assets/kruemblegard/controllers/scattered_enderman.animation_controllers.json`
    - Texture: `assets/kruemblegard/textures/entity/scattered_enderman.png` (placeholder; intended for you to repaint).
    - Eyes glow mask: `assets/kruemblegard/textures/entity/scattered_enderman_eyes.png` (placeholder; used as an emissive layer).
  - Starter animation tracks included:
    - `animation.scattered_enderman.idle`
    - `animation.scattered_enderman.look`
    - `animation.scattered_enderman.walk`
    - `animation.scattered_enderman.attack`
    - `animation.scattered_enderman.scream`
    - `animation.scattered_enderman.stare`
    - `animation.scattered_enderman.carry_block`
    - `animation.scattered_enderman.hold_block_idle`
    - `animation.scattered_enderman.teleport`
    - `animation.scattered_enderman.vanish`

- **Moogloom** (`moogloom`)
  - Mooshroom-like passive creature (vanilla model/AI) unique to **Shatterplate Flats**.
  - Special behavior: shearing converts it into a normal Cow (vanilla mooshroom behavior) and drops Griefcap.
  - Visual: has **Griefcap** mushrooms on its back and head.
  - Breeding: can cross-breed with vanilla mooshrooms; mixed pairings have a small chance to produce a brown mooshroom baby.
  - Texture: `assets/kruemblegard/textures/entity/moogloom.png`.
    - `animation.scattered_enderman.appear`
    - `animation.scattered_enderman.shake`
    - `animation.scattered_enderman.flinch`
    - `animation.scattered_enderman.death`
  - Geo bones are split for vanilla-style posing: `head`, `body`, `leftarm`, `rightarm`, `leftleg`, `rightleg`.

## Boss: Krümblegård
- **4 phases** with phase-specific visuals (bone hiding) and locomotion.
- **12 unique attacks** across phases (fast/heavy/ranged kits per phase).
- Phase transitions trigger one-shot transition animations.
- Combat pacing is tuned to **vanilla Warden** cadence (movement speed and 18-tick melee cooldown), with faster animation playback to match.
- Boss awards **Ender Dragon XP** on death.

- Phase-specific boss projectiles are GeckoLib-rendered and intended to be edited in Blockbench:
  - Geo: `assets/kruemblegard/geo/projectiles/kruemblegard_phase{1,2,3,4}_*.geo.json`
  - Animations: `assets/kruemblegard/animations/projectiles/kruemblegard_phase{1,2,3,4}_*.animation.json`

Note: Krümblegård is no longer spawned by waystones. It can still be spawned via commands/spawn egg for testing.

## Items & progression
- **Guidebook (Written Book)**
  - Granted once on first join (tracked in player persistent NBT).
  - Implemented as a vanilla `minecraft:written_book` pre-filled with pages.
  - Text source (editable): `src/main/resources/data/kruemblegard/books/crumbling_codex.json`.

- **Runic Core** (`runic_core`)
  - Dropped by Krümblegård.
  - Used to craft Runic tools.
  - Text source (editable): `src/main/resources/data/kruemblegard/books/crumbling_codex.json`.
- **Runic tool set**
  - Runic Sword (`runic_sword`)
  - Runic Pickaxe (`runic_pickaxe`)
  - Runic Axe (`runic_axe`)
  - Runic Shovel (`runic_shovel`)
  - Runic Hoe (`runic_hoe`)
  - Tier: Runic tools are Netherite-equivalent.
  - Crafting: Runic tools can be crafted directly using **Runic Ingots**, or upgraded from Diamond tools in a Smithing Table (Netherite-template style), keeping enchantments.

- **Runic Debris** (`runic_debris`)
  - Wayfall-only worldgen.
  - Drops itself when mined with a Diamond-tier pickaxe or better.
  - Smelt/Blast Runic Debris into **Runic Scrap**.
  - Combine **4x Copper Ingots + 4x Runic Scrap** to craft a **Runic Ingot**.
  - Combine **1x Netherite Ingot + 1x Runic Ingot** to craft an **Attuned Ingot**.

## Creative inventory

- `wayfall_portal`: a simple testing portal block you can place in creative; entities (players/mobs/items) that pass through it are sent to the Wayfall spawn.
  - On entry, Wayfall places a `wayfall_origin_island` structure template at the spawn and teleports the entity onto it (safe landing, no void falls).
  - Templates live under `data/kruemblegard/structures/wayfall_origin_island/` and are selected via the template pools under `data/kruemblegard/worldgen/template_pool/wayfall_origin_island/`.
  - Template landing marker: put a single `minecraft:barrier` block in the template at the intended **feet** position (i.e., the air block the player occupies), with a solid floor block directly below it.
    - The game removes the marker and teleports the player into that space.

- Wayfall-only vanilla structure retheming:
  - Vanilla shipwrecks placed in Wayfall swap their wood blocks to a local Wayfall wood palette (with a small chance to keep vanilla wood).
  - Underway Falls jungle temples swap temple stone blocks to the Scarstone family.
  - Implementation note: retheming runs on chunk load but avoids heavy neighbor-update storms to keep exploration/teleport testing smooth.
  - Stability note: retheme tracking is intentionally capped/evicted so it can’t grow without bound and stall autosaves in long /tp scouting sessions.

- Wayfall initialization (spawn island):
  - By default, initialization only runs when it’s actually needed (on entry).
  - Optional: `wayfallPreloadOnServerStart` (COMMON config) can be enabled to attempt a one-time pre-player preload, but may cause startup stutter on some systems.

- `ancient_waystone`: a **Waystones-backed** waystone variant (two-block tall like Waystones waystones) that opens the Waystones menu.
  - Requires Waystones + Balm.
  - Crafting: `kruemblegard:attuned_stone` around a `waystones:warp_stone` (recipe only loads when Waystones is installed).
  - Editable texture: `assets/kruemblegard/textures/block/ancient_waystone.png`
  - Models follow Waystones' top/bottom layout: `assets/kruemblegard/models/block/ancient_waystone_{bottom,top}.json`
  - Lore hook: if a player has never visited the Wayfall, using a Waystone has a small chance to teleport them to the Wayfall spawn landing (server-side).
    - Hold shift while using a Waystone to bypass this roll.

## Wayfall Skybox (Client)
- Wayfall renders a custom sky in-game (client-only).
- Uses a single equirectangular panorama texture (2:1 aspect ratio):
  - `assets/kruemblegard/textures/environment/wayfall_panorama.png`
- A placeholder can be generated via:
  - `tools/generate_wayfall_panorama_placeholder.ps1`

## Wayfall origin monument
- Deprecated: the separate block-built “origin monument” island is no longer generated.
- Wayfall portal entry only places the `wayfall_origin_island` structure template at `(0, 175, 0)`.

- Standing Stone + Attuned Stone blocks.
- Attuned Ore block (Wayfall-only worldgen).
- Wayfall Iron Ore + Wayfall Copper Ore + Wayfall Diamond Ore blocks (Wayfall worldgen; diamond spawns lower on average).
- Wayfall geology palette blocks:
  - Fractured Wayrock, Crushstone, Ashfall Loam, Fault Dust.
  - Scarstone family: Scarstone, Cracked Scarstone, Polished Scarstone, Chiseled Scarstone.
  - Stoneveil Rubble family: Stoneveil Rubble, Polished Stoneveil Rubble, Runed Stoneveil Rubble.
  - Stone-family variants exist for key geology blocks: stairs, slabs, and walls (Attuned Stone, Fractured Wayrock, Crushstone, Scarstone + variants, Stoneveil Rubble + variants).
  - Crafting integration: Wayfall stone blocks are added to vanilla stone material tags so they can be used for stone tools and common stone crafting (furnace/stonecutter + many redstone components).
- Wayfall flora (plants + shrubs + fungi), including Wispstalk, Gravevine (vines), Pyrokelp (vanilla-style head/body plant; generates as pre-grown columns in Basin of Scars), Echocap, Runebloom, Soulberry Shrub (can corrupt into Ghoulberry Shrub) and additional Wayfall plants.
  - Survival farming: Soulberries/Ghoulberries/Wispshoot are edible and can be planted to grow their matching plants.
  - Teaching moment: Wispstalk + berry shrubs are prickly when walked through (vanilla sweet-berry-bush style).
  - Utility: most Kruemblegard flora (plants, vines, saplings, leaves, and plant foods) can be composted.
  - Bees: many Kruemblegard plants are tagged as `minecraft:flowers`, so bees can breed/pollinate using them like vanilla flowers (food plants are excluded).
  - Biome identity: Wayfall biomes use distinct plant sets (via per-biome biome modifiers); each Wayfall biome still includes at least one food plant.
  - Detailed reference (keep updated): docs/FLORA_REFERENCE.md
- Paleweft farming (Wayfall)
  - **Paleweft Grass** and **Tall Paleweft Grass** use a “stitched” grass tint (samples multiple nearby grass colors).
  - Paleweft grass patches spawn across Wayfall biomes via worldgen (a random mix of short + tall), plus a secondary pass that adds extra **small** Paleweft Grass.
  - Paleweft is intentionally denser in warm/temperate Wayfall biomes (and especially **Underway Falls**) using a canopy-safe surface heightmap so it doesn’t “stick” to treetop leaves.
  - Worldgen: Paleweft patches only place into replaceable blocks (surface-safe; no terrain replacement).
  - Bonemealing **Runegrowth** can also trigger a local Paleweft bloom burst and has a chance to grow other biome-specific plants (excluding the food plants and saplings; no periodic ticking).
  - **Paleweft Seeds** drop from Paleweft grass (loot tables) and can be planted as **Paleweft Corn**.
  - **Paleweft Corn** is a 4-stage crop that grows on vanilla Farmland or **Rubble Tilth**.
  - Mature Paleweft Corn yields potato-like amounts of **Weftkern** and has a very rare **Echokern** bonus drop; both are edible (raw-potato-like). Weftkern crafts into **Weftmeal** (baked-potato-like).
  - **Rubble Tilth** is a Wayfall farmland analog created by hoeing blocks tagged as `kruemblegard:rubble_tillable`.
    - When it dries out (or gets trampled), it reverts to **Fault Dust**.
    - Visual feedback: at high moisture, it swaps to a moist top texture (vanilla farmland-style).
- Wayfall vegetation patches snap each placement attempt to the local surface heightmap (floating-island friendly) and only place into replaceable blocks (no carving holes in terrain).
- Wayfall water flora:
  - Waylily (above-water surface flower) + vanilla seagrass (underwater) generate across all Wayfall biomes with per-biome rarity tiers (Waylily uses the `waylily_patch*` placed features).
  - The deep lake feature (`wayfall_big_water_lake`) also adds extra Waylily on exposed lake surfaces and adds underwater seagrass by dressing parts of the lake floor (sand/gravel/clay) during generation.
  - Deep lakes are clamped to a chunk-safe size during worldgen to avoid “setBlock in a far chunk” warnings and severe world creation lag.
  - Underwater plants scan downward from the surface to the bottom of water columns, so they can survive and generate inside floating-island lakes.
  - Warm Wayfall biomes (tagged `#kruemblegard:wayfall_warm`) also get vanilla coral fans + sea pickles underwater.
- Wayfall staple flora (new): Voidfern, Runeblossom, Moteshrub, Ashveil, Twilight Bulb, Whispervine.
- Wayfall trees (block sets): logs/planks/leaves/saplings exist as blocks/items.
  - Wayfall wood families include `*_log`, `*_wood`, and stripped `stripped_*_{log,wood}` variants.
  - Most Wayfall tree configured features use `kruemblegard:fault_dust` as their `dirt_provider` (no `minecraft:rooted_dirt` placements).
  - Axes strip Wayfall logs/wood into their stripped variants.
  - Saplings grow over time via random ticks (no light requirement) or bonemeal.
    - Wayfall saplings grow into their matching worldgen configured features (variant selectors), matching natural generation.
    - All Wayfall saplings support 2x2 (spruce-style) placement to grow a larger **mega** tree, using Evergreen-style multi-option selectors.
  - All Wayfall trees have a rare **mega** variant that can also generate naturally in Wayfall.
    - Most mega selectors choose between two Evergreen-style mega outcomes; each `mega_1`/`mega_2` delegates to that tree’s existing large “assembled” variants (typically `*/5` and `*/4`).
    - Wayroot is an exception: its worldgen is currently tuned to a baobab-style silhouette (including a dedicated mega configured feature) and uses `kruemblegard:ashmoss` as its `dirt_provider`.
  - All Kruemblegard wood-family blocks are flammable like vanilla (logs/wood/leaves + planks and wooden derivatives like slabs/stairs/fences/doors/signs; saplings burn fast).
  - Staple wood sets: Ashbloom, Glimmerpine, Driftwood.
  - A simple custom tree Feature exists for data-driven placement (`registry/ModFeatures` + `world/feature/WayfallSimpleTreeFeature`).
  - Full per-tree reference: [TREES.md](TREES.md)

### Reactive staple plants
- Some Wayfall staples use a “reactive” plant base that can activate near blocks tagged as `kruemblegard:waystone_energy_sources`.
- Reactive staple plants can also activate near players (per-plant radius tuning) and emit different idle vs. active particles.
- The mod includes a custom particle type (`kruemblegard:arcane_spark`) used by select reactive plants when active.

### Wayfall surface covers
- Ashmoss is a moss block surface.
- Ashmoss has a matching carpet variant: **Ashmoss Carpet**.
- Runegrowth is a grass-like surface family.
- Voidfelt is a mycelium-like dirt surface.
- Runegrowth is split into 4 separate blocks:
  - **Frostbound Runegrowth** (cold)
  - **Resonant Runegrowth** (temperate)
  - **Verdant Runegrowth** (warm)
  - **Emberwarmed Runegrowth** (hot)
- Each variant can additionally switch to a snowy-side appearance when snow is directly above.
- Runegrowth spreads over **Fault Dust** under grass-like conditions (sufficient light and not waterlogged).
- Voidfelt can spread over **Fault Dust** (void biome only) under similar grass/mycelium-like conditions.
- Ashmoss prefers “ash-heavy” Wayfall biomes (tag: `kruemblegard:wayfall_ash_heavy`).
- Bonemeal on Ashmoss can spread it onto nearby **Fault Dust**, place Ashmoss Carpet, and grow pale grasses.
- Ashmoss spread/bonemeal conversion targets are data-driven via the `kruemblegard:ashmoss_spread_targets` block tag (includes Kruemblegard stone blocks).
- Worldgen: Ashmoss patches project placement to the ground surface (no-leaves heightmap) and only replace blocks in `kruemblegard:ashmoss_spread_targets` (prevents floating patches).
- Voidfelt is rare: it is primarily generated in **Shatterplate Flats** (tag: `kruemblegard:wayfall_void`) and slowly reverts outside void-biome conditions.

## Dimensions
  - Void dimension with Aether-inspired floating islands.
  - Uses only Krümblegård Wayfall biomes (no vanilla biomes).
  - Detailed biome list (keep updated): docs/WAYFALL_BIOMES.md
  - Attuned Ore generates here.
  - Spawns in Wayfall are limited to Krümblegård mobs (no vanilla mob spawns).
- Wayfall uses custom noise settings (`kruemblegard:wayfall`) with `kruemblegard:fractured_wayrock` as the base terrain block.
- Wayfall terrain shaping is driven by custom `worldgen/noise/**` + `worldgen/density_function/**` entries (instead of End island routing).
- Safety: entering Wayfall via the portal always lands you on a dedicated spawn island (a small tapered "asteroid") placed at **(0, 175, 0)** (with cleared headroom).

### Small pools

- Underway Falls can generate rare high-altitude waterfall sources.
- Wayfall biomes can generate surface water lakes (lake barriers use a Stoneveil Rubble + Runed Stoneveil Rubble mix).
- Wayfall biomes can also very rarely generate **large deep water lakes** (10–15 blocks deep) that can span across chunk borders.
- Wayfall biomes can generate rare surface lava lakes (lake barriers use a Scarstone + Cracked Scarstone mix); Basin of Scars is more lava-forward.
- Basin of Scars can also very rarely generate **large deep lava lakes** (18–24 radius, 10–15 deep) using Scarstone barriers.
- Lake placement uses a -8 X/Z offset so the generated lake is centered on the placement position.
- Wayfall surface palette can vary by biome tag:
  - `#kruemblegard:wayfall_ash_heavy` → surface defaults to **Ashfall Loam** with shallow **Crushstone** beneath.
  - `#kruemblegard:wayfall_void` → surface defaults to **Voidfelt** (rare; Shatterplate Flats only) with shallow **Crushstone** beneath.
  - Otherwise, the surface defaults to **Runegrowth** (the correct Runegrowth variant is chosen per-biome; **Fault Dust** is used as a soil layer in some biomes).
- Wayfall flora/saplings use the `kruemblegard:wayfall_ground` block tag for valid substrate (so the terrain palette can evolve without hard-coded `END_STONE`).

## Gameplay rules
- Scarstone has progression gating:
  - Breaking with a tool below iron tier is slower, damages the tool, and drops Cracked Scarstone.

## Advancements / criteria (project-specific)
- Vanilla advancements are not granted directly.
- `init/ModCriteria` is the home for custom triggers.
- `kruemblegard:pebblit_shoulder` grants the advancement **A little Clingy** when a Pebblit is perched.

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
- Wayfall ambient music uses `kruemblegard:music.wayfall` (source file: `assets/kruemblegard/sounds/horror-background-atmosphere-09_universfield.ogg`).

## Config
- Common config: `config/kruemblegard-common.toml`
- Includes `enableWaystones` and `waystoneRarity` (see `config/ModConfig`).
- Also includes `waystoneWayfallTeleportEnabled` and `waystoneWayfallTeleportChance` (see `config/ModConfig`).
- Wayfall init performance knobs:
  - `wayfallInitTasksPerTick`: max queued Wayfall init tasks processed per server tick.
  - `wayfallInitMaxMillisPerTick`: time budget (ms) per server tick for Wayfall init work; remaining work is deferred.
  - `wayfallInitPlacementMinRemainingMillis`: minimum remaining time (ms) required before the heaviest spawn island placement step is allowed to run.
  - (Removed) `wayfallInitChunksTicketedPerTick`: Wayfall no longer does ongoing chunk ticketing.
    - Wayfall does a one-time small preload around the spawn island on world/server start (or when the Wayfall level first exists), then returns to player-driven chunk loading.
  - `wayfallDebugLogging`: enables extra Wayfall init progress logging.

## Worldgen
- Worldgen is primarily data-driven under `data/kruemblegard/worldgen/**`.
- Important worldgen IDs are centralized in `src/main/java/com/kruemblegard/worldgen/ModWorldgenKeys.java`.
- Critical datapack registry entries/tags are sanity-checked on server start via `com.kruemblegard.worldgen.WorldgenValidator` (including a check that Wayfall biomes actually contain expected injected placed features).
- Wayfall flora placement is data-driven via biome modifiers/tags/worldgen JSON.
- Wayfall flora is injected via Forge biome modifiers:
  - Biome selection is tag-driven via explicit `forge:tag` selectors (for Forge 47.4.0 compatibility).
  - Global “staple” trees/plants are injected into all Wayfall biomes via `data/kruemblegard/forge/biome_modifier/add_wayfall_plants.json`.
  - Biome-specific trees/plants are injected via `data/kruemblegard/forge/biome_modifier/add_wayfall_*_flora.json`.

## Trees
- Wayfall saplings grow over time via random ticks like vanilla saplings (bonemeal accelerates growth).

## Removed / not present (by design)
- `false_waystone` (block + worldgen + biome modifier/tag) was removed.
- `radiant_essence` was removed.
- `runic_core_fragment` was removed.
