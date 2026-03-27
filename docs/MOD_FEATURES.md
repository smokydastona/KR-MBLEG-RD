# Krümblegård — Mod Features Reference

This document is a **complete, living reference** of current Krümblegård features.

Keep it up to date whenever you add/remove/rename content.

## Documentation
- Block material rules + full block inventory: `docs/Block_Material_Bible.md`
- Item material rules + full item inventory: `docs/Item_Material_Bible.md`
- Mob roster + audit-driven TODOs: `docs/mob_bible.md`
- Sound rules + full sound inventory (with exact length requirements): `docs/Sound_Bible.md`
- Sound design intent + current asset tracker: `docs/SOUND_TRACKING.md`
- After registry changes, refresh the generated inventories by running: `tools/generate_material_bibles.ps1`
- After sound changes, refresh the generated inventory by running: `tools/generate_sound_bible.ps1`
- Texture resize helpers (256x256, no cropping, reports written under `tools/_reports`):
  - `tools/resize_textures_to_256.ps1` (block + item)

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
- JVM flag: `-Dkruemblegard.disableWayfallSkybox=true`
  - Disables the custom Wayfall panorama skybox renderer (falls back to vanilla sky rendering).
  - Intended as an isolation toggle for native `jvm.dll` crashes reported during JIT compilation.

## Worldgen (Tuning + Validation)
- Worldgen tuning config file: `config/kruemblegard-worldgen.json5`
  - Auto-created and auto-extended when new keys are added.
  - `strictValidation` (default `false`): when enabled, `WorldgenValidator` runs at server start and hard-fails if critical worldgen registry IDs/tags are missing.
    - When `strictValidation=false`, validation is skipped (keeps server start/world creation lightweight).
- Optional TerraBlender integration (Overworld): controlled by weights and per-biome toggles inside `terraBlender.overworld` in the same config.

- **Ancient Way Ruins (Overworld structure)**
  - Generated from `examples/wayfall_temple.schem`, shipped at runtime as `data/kruemblegard/schematics/ancient_way_ruins/wayfall_temple.schem`.
  - Placement anchor: the schematic's **red wool** marker is treated as the true structure origin and snapped to the generated world position; it is not placed into the final structure.
  - Global count: the overworld structure set is finite and globally capped at three placements per world, with the placements kept well over 1500 blocks apart by concentric-ring spacing.
  - Vanilla spacing guard: the structure set also excludes placements within 32 chunks of the vanilla `minecraft:strongholds` structure set so ruins do not crowd stronghold terrain.
  - Explorer maps: master cartographers can sell a locator map to the nearest Ancient Way Ruins structure.
  - Aging pass: placement applies a structure-specific ruin processor pass that strips red-wool marker blocks to air, chips blocks away, mosses/cracks masonry, and converts `sculk_vein` markers into an ancient-city-style mix of air, `sculk_sensor`, and Warden-capable `sculk_shrieker` blocks.
  - Monster control: natural monster spawns inside the structure bounding box are constrained to the three Cephalari zombie variants, while Wardens summoned by shriekers remain allowed.

- **Ashmoss → Ashbloom (azalea-style)**
  - Ashbloom now generates naturally as **small plants** on Ashmoss (with a rare flowering variant).
  - Ashbloom saplings are **bonemeal-only** (no random tick growth into trees).

- **Ashspire Cactus (Wayfall flora)**
  - Ashspire Cactus generates in **Strata Collapse** and **Fracture Shoals** as small, tighter column clusters.
    - Height: typically 2–3 blocks (1–2 cactus + `Ashspire Emberbloom` cap), with a rare 4-block “spire” (3 cactus + cap).
    - Growth: `Ashspire Emberbloom` now favors upward growth and only occasionally creates a single side split, so mature plants stay mostly spire-like instead of over-branching.
    - Rendering: the cactus body now uses the same multipart chorus-plant block model layout as vanilla and now only ships the side/no-side cactus model variants plus a thin `ashspire_cactus` inventory alias model backed by the shared `ashspire_cactus_side` texture that the multipart blockstate actually references; the emberbloom cap assets likewise live under canonical `ashspire_emberbloom_*` naming with only the live top and dead-cap textures kept because the chorus-flower-style model does not use separate side or bottom maps.
    - `Ashspire Emberbloom` is a chorus-flower-style cap block (emits light) and now swaps to a darker Ashspire-specific dead-cap texture instead of falling back to the vanilla chorus palette.
    - Dead-age caps now drop and pick as a separate `Dead Ashspire Emberbloom` item, which places the cap back in its age-5 dead state instead of converting it to the live bloom item.
  - Survival substrate is tag-driven: `#kruemblegard:ashspire_cactus_growable_on`.

- **Ambient rock schematics (Wayfall)**
  - All Wayfall biomes can generate small rock formations from a large schematic pool.
  - Variation:
    - Random rotation per placement.
    - Per-biome density variation (deterministic per biome).
    - Weathering/remap pass per placement (mossy/cracked variants + minor edge chipping).
  - Placement rules:
    - Rocks are allowed to be partially buried by replacing `#kruemblegard:rock_bury_replaceable`.
    - Rocks avoid floating placements via the same schematic “support beard” system used by schematic trees (fills small gaps under the footprint; depth-capped).

- Schematic terrain support (“beard”): schematic-driven trees (Wayroot, Echowood, Glimmerpine; regular + mega variants) and giant mushroom schematics add a short support fill under their trunk/stem footprint when placed on uneven terrain.
  - Fill rules: replaceables-only, avoids liquids, depth-capped to prevent giant pillars.
  - Palette: support blocks are sampled from nearby terrain so the fill matches the local biome surface (sand/gravel/custom soils/etc) instead of always defaulting to dirt.

## Compatibility
- **Tree Harvester (Serilum)**
  - Krümblegård wood blocks are included in vanilla tags (`minecraft:logs`, `minecraft:logs_that_burn`, `minecraft:leaves`) so they’re treated as normal trees.
  - Note: Tree Harvester itself defaults to “must hold an axe” and “must sneak to harvest” (configurable in Tree Harvester).
  - Note: Tree Harvester can ignore player-made trees when leaf blocks have `persistent=true` (also configurable). Krümblegård schematic/worldgen trees force non-persistent leaf states so they’re harvestable by default.
  - Franch helper blocks (everything `*_franch*` **except** `*_franch_wood` and `*_franch_planks`) are included in vanilla `minecraft:leaves` so Tree Harvester treats/clears them like leaves.
  - `*_franch_wood` and `*_franch_planks` blocks are included in vanilla `minecraft:logs` and `minecraft:logs_that_burn`.
  - When Tree Harvester harvests a tree, Krümblegård additionally forces nearby leaves + Franch helper blocks (`#kruemblegard:tree_harvester_leaf_like`) to clear immediately (including `*_franch_planks` if present in the tree) and relocates the resulting drops to the harvesting player’s feet.
  - Franch/leaves validity is **species-aware**: Franch/leaves only stay connected (distance-0 anchor) when linked to matching-species logs (e.g., Ashbloom Franch connects to `#kruemblegard:ashbloom_logs`, Oak Franch connects to `#kruemblegard:oak_franch_logs`).
  - Giant fungi caps/stems are registered as `HugeMushroomBlock`.
    - Stems use `MapColor.WOOL` so Tree Harvester can detect/harvest them.
    - Caps use **species-specific** map colors so minimap/worldmap mods (e.g., Xaero) show different cap colors per fungus.
    - Compatibility: caps are additionally included in `minecraft:leaves` so Tree Harvester treats them as leaf blocks even when cap map colors are not vanilla red/brown.
  - Giant fungi cap **slabs** and red/brown mushroom block slabs are cleared via `#kruemblegard:tree_harvester_mushroom_cap_slabs` (Tree Harvester doesn't natively detect slab cap blocks).
  - Additionally, when Tree Harvester fells a giant mushroom, Krümblegård clears any nearby `HugeMushroomBlock` caps (non-stem) during cleanup so cap detection doesn't rely on specific cap map colors.
  - Note: Tree Harvester has a hardcoded “scan up to 30 blocks above base” tree-detection pass; extremely tall trees with all leaves above that height may still not trigger.

- **FallingTree (RakambdaOrg)**
  - Krümblegård trees work via vanilla tags (`minecraft:logs`, `minecraft:leaves`).
  - Giant fungi stems are treated as trunks and detected via harvest-mod logic; Krümblegård then clears nearby caps/cap-slabs during cleanup.
  - When FallingTree fells a tree, Krümblegård also clears nearby leaves + Franch helper blocks and moves the resulting drops to the player.

- **BotanyPots (Darkhax)**
  - When BotanyPots is installed, Krümblegård saplings and Wayfall fungi can be grown in pots via data-driven `botanypots:block_derived_crop` recipes.
  - Sapling crops define explicit tree-like drops (logs + leaves) via a `botanypots:items` drop provider.
  - These recipes are gated behind Forge `forge:mod_loaded` conditions so they do not load without BotanyPots.

## Core gameplay loop
- **Traprock** can appear as a dormant stone-creature.
- It awakens if a player interacts with it or lingers too close.
- After a player has encountered Traprock once, most newly found Traprock will spawn already awake (with a small chance to remain dormant again).

## Mobs
- **Mirrored mob variants**
  - Geo-rendered custom mobs now derive a deterministic left-handed mirrored render variant from their UUID so some spawns permanently play the authored animation set as a mirrored east-west-handed version without needing separate animation resources.
  - Fault Crawlers explicitly override the vanilla left-handed roll and set that mirrored variant at a true 50/50 split during spawn finalization.
  - Bed-specific mirroring remains separate from that spawn trait and only participates on east-west bed facings, so sleeping poses can still flip independently when a bed-side correction is needed.

- **Traprock** (`traprock`)
  - Implemented as a Blaze-derived GeckoLib mob.
  - Uses melee attacks and a ranged attack that throws a stone projectile.
  - Uses editable resources: `geo/traprock.geo.json`, `animations/traprock.animation.json`, `textures/entity/traprock.png`.

- **Pebblit** (`pebblit`)
  - Neutral Silverfish-like creature (retaliates when attacked).
  - When first attacked, it performs a **call** animation before becoming hostile.
    - The call alerts nearby **untamed** Pebblits to also call, then become angry (Piglin-style group aggro).
    - During the call, Pebblits are **immune to all damage** until the animation completes.
    - **Tamed** Pebblits cannot call and do not join call chains.
  - When angry, its left/right angry spike arms animate during melee hits.
  - Can be tamed by right-clicking with an **Echokern**; follows its owner.
  - Right-click with empty hand (non-shift): toggles **sit and stay**.
  - Shift + right-click with empty hand: perches on the owner's shoulder **until it dies**, granting the owner **knockback resistance** while perched.
    - While perched, the owner also receives a visible status effect icon: **Pebblit Shoulder (Knockback Resistance)**.
  - Pebblit attacks apply **knockback**.
  - Natural spawns: Basin of Scars, Crumbled Crossing, Fracture Shoals, Shatterplate Flats, Strata Collapse, and Underway Falls (the six warm Wayfall biomes via `#kruemblegard:wayfall_warm`).
  - Rendered via GeckoLib:
    - Geo: `assets/kruemblegard/geo/pebblit.geo.json`
    - Animations: `assets/kruemblegard/animations/pebblit.animation.json`
    - Texture: `assets/kruemblegard/textures/entity/pebblit.png`

- **Unkeeper** (`unkeeper`)
  - Hostile mob (GeckoLib-rendered).
  - Uses editable resources:
    - Geo: `assets/kruemblegard/geo/unkeeper.geo.json`
    - Animations: `assets/kruemblegard/animations/unkeeper.animation.json`
    - Animation controller reference (Bedrock-style): `assets/kruemblegard/controllers/unkeeper.animation_controllers.json`
    - Texture: `assets/kruemblegard/textures/entity/unkeeper.png`
  - Rendering:
    - Eye glow bones (`eye_glow_L`, `eye_glow_R`) are rendered full-bright via `UnkeeperEyesLayer`.

- **Driftwhale** (`driftwhale`)
  - Pod-roaming sky-swimmer (GeckoLib-rendered).
  - Territorial predator: Driftwhales will hunt **Glow Squid** on sight.
    - Glow Squid try to flee when Driftwhales get close.
  - Natural spawns: all Wayfall biomes (`#kruemblegard:wayfall`), but only where there's enough vertical clearance.
    - Spawns in pods (group size 2–4).
    - Spawns in varying sizes (adults: 1.0×–2.5× scale; never smaller than base size).
    - Babies: 0.8×–1.0× scale.
  - Spawn behavior: chooses a safe grounded spawn position, then lifts into open air immediately.
  - Visual: tapered body + tail fluke with small drifting stone fragments.
  - Animation feel: uses slower body-led idle and glide loops with staggered tentacle follow-through, gentler fin stabilization, calmer drift-stone motion, a lightweight breathing layer that runs on top of both idle and movement, a real hurt-startle reaction, an explicit death collapse, and occasional thermal-lift surges while peacefully air-swimming.
  - Drops Phantom Membrane.
  - Uses editable resources:
    - Geo: `assets/kruemblegard/geo/driftwhale.geo.json`
    - Animations: `assets/kruemblegard/animations/driftwhale.animation.json`
    - Animation controller reference (Bedrock-style): `assets/kruemblegard/controllers/driftwhale.animation_controllers.json`
    - Texture: `assets/kruemblegard/textures/entity/driftwhale.png`

- **Pebble Wren** (`pebble_wren`)
  - Small, breedable ambient seed-eater and flying Wayfall bird (GeckoLib-rendered).
  - Visual: upright “display bird” silhouette with crest + fan tail.
  - Befriend: feed Wheat Seeds to befriend it (tamed wrens follow their owner).
  - Ore-find: while befriended, crouch-feed Wheat Seeds to trigger an ore direction ping (plays a brief display animation).
  - Movement: uses a twitchier songbird-style perch-and-flight rhythm with short, frequent airborne bursts, quicker owner/flock regrouping, preferred higher landings on visible perches such as branches, leaves, fences, and walls, occasional perched chirps with the nearest perched answer/tiny flourish motion, slight pitch variation across perch calls, an occasional one-extra-reply conversational burst, loose hop synchronization in small wild flocks, shared ground pecking/foraging and playful hop-chase bouts among nearby wild wrens, occasional whole-flock flush takeoffs, explicit stimulus flushes when a nearby player rushes the flock or a wren is suddenly hurt, ground strolling while relaxed, flying navigation + air-wander flocking when displaced, and a dedicated flap loop while airborne; ordered-to-sit wrens still land instead of hovering.
  - Audio: now uses dedicated custom `entity.pebble_wren.*` sound events for ambient chirps, perch calls/replies, social flourishes, flutter takeoffs/landings, hurt/death, and ore-find pings; most of the set is procedurally synthesized in-house with `ffmpeg` filter graphs, while the flutter cue is a lighter trimmed cut from the PSFX example file `beating-wings-small-001.ogg`, all tracked in `docs/audio_licenses/pebble_wren/manifest.md`.
  - Tempt/breed item: Wheat Seeds.
  - Natural spawns: all Wayfall biomes (`#kruemblegard:wayfall`) plus all Overworld biomes (`#minecraft:is_overworld`), now counted against the ambient mob cap like vanilla bats, with reduced ambient flock pressure at weight 4 and group size 1-3.
  - Drops Feathers and (occasionally) Wheat Seeds.
  - Uses editable resources:
    - Geo: `assets/kruemblegard/geo/pebble_wren.geo.json`
    - Animations: `assets/kruemblegard/animations/pebble_wren.animation.json`
    - Animation controller reference (Bedrock-style): `assets/kruemblegard/controllers/pebble_wren.animation_controllers.json`
    - Textures: `assets/kruemblegard/textures/entity/pebble_wren/pebble_wren_1.png` .. `pebble_wren_14.png`

- **Bat coverage in Wayfall** (`minecraft:bat`)
  - Vanilla bats now receive an extra biome-spawn injection across all Wayfall biomes (`#kruemblegard:wayfall`) so they can appear anywhere Pebble Wrens do inside Wayfall.
  - Wayfall bat injection now uses the same lighter ambient pressure band as Pebble Wrens: weight 4 and group size 1-3.
  - Wayfall also grants bats a custom spawn predicate that accepts enclosed air pockets and island overhangs in that dimension, so they are not blocked by vanilla bat assumptions about darker low-altitude cave space.
  - Overworld bat spawning is unchanged and still comes from vanilla biome data rather than an extra Krümblegård modifier.

- **Mossback Tortoise** (`mossback_tortoise`)
  - Slow, armored, breedable grazer (GeckoLib-rendered).
  - Moss variants: spawns with one of several moss patch patterns.
  - Shearing: use Shears to remove moss patches and get Moss Carpet.
  - Regrowth: a sheared tortoise regrows its moss by grazing on Grass Blocks or any of the four runegrowth variants (`runegrowth`, `frostbound_runegrowth`, `verdant_runegrowth`, `emberwarmed_runegrowth`); eaten runegrowth falls back to `fault_dust`.
  - Sheep parity: vanilla sheep can also regrow wool by grazing those four runegrowth variants.
  - Bush immunity: immune to thorny/berry-bush collision damage (sweet berry bush / cactus-style brambles).
  - Tempt/breed item: Seagrass.
  - Natural spawns: Underway Falls (`#kruemblegard:underway_falls`).
  - Drops Seagrass and has a rare Scute drop.
  - Uses editable resources:
    - Geo: `assets/kruemblegard/geo/mossback_tortoise.geo.json`
    - Animations: `assets/kruemblegard/animations/mossback_tortoise.animation.json`
    - Animation controller reference (Bedrock-style): `assets/kruemblegard/controllers/mossback_tortoise.animation_controllers.json`
    - Texture: `assets/kruemblegard/textures/entity/mossback_tortoise.png`

- **Grave Cairn** (`grave_cairn`)
  - Hostile living rubble mound (GeckoLib-rendered) with a dormant ambush state.
  - Dormant behavior: spawns dormant (no AI) until a player gets close, damages it, or breaks blocks nearby.
  - Combat: melee, close-range slam knockback, and a mid-range stone toss.
  - Damage reaction: when hit, briefly collapses inward (rubble burst) and may eject a Pebblit.
  - Summoning: summons Pebblits in waves at 75% / 50% / 25% health.
  - Final stand: below ~20% health, gains speed/knockback resistance and more aggressively buffs nearby Pebblits.
  - Death: collapses into rubble and releases a final Pebblit swarm.
  - Natural spawns: all Wayfall biomes (`#kruemblegard:wayfall`) at high altitude (Wayfall-only; Y >= 96).
  - Drops Cobblestone/Bones (and occasional Iron Nuggets).
  - Uses editable resources:
    - Geo: `assets/kruemblegard/geo/grave_cairn.geo.json`
    - Animations: `assets/kruemblegard/animations/grave_cairn.animation.json`
    - Animation controller reference (Bedrock-style): `assets/kruemblegard/controllers/grave_cairn.animation_controllers.json`
    - Texture: `assets/kruemblegard/textures/entity/grave_cairn.png`

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

- **Fault Crawler** (`fault_crawler`)
  - Spider-like creature formed from unstable stone plates.
  - Neutral until provoked.
  - Handedness: rolls the mirrored left-handed variant at a 50/50 split on spawn.
  - Natural spawns: **Wayfall temperate + warm (non-hot) biomes**.
    - Included: `#kruemblegard:wayfall_temperate` + warm biomes excluding `kruemblegard:basin_of_scars` and `kruemblegard:fracture_shoals`.
    - Not included: `#kruemblegard:wayfall_cold`.
  - Often spawns buried and emerges when a player gets close.
  - Uses a slam attack and can occasionally emit a small knockback “fault pulse”.
  - Drops `Fault Shard`.
  - Rendered via GeckoLib:
    - Geo: `assets/kruemblegard/geo/fault_crawler.geo.json`
    - Animations: `assets/kruemblegard/animations/fault_crawler.animation.json`
    - Texture: `assets/kruemblegard/textures/entity/fault_crawler.png`

- **Scaralon Beetle** (`scaralon_beetle`)
  - Large, rune-etched flying beetle **mount** (horse-style taming + saddle riding).
  - Can be fitted with a **Chest** (donkey/mule-style) to gain extra inventory storage.
  - Can be decorated with **wool/carpet** (Trader Llama-style decor): applying a carpet/wool item equips it into the Scaralon's **decor slot** (under the saddle) and sets the decor color (removable/swappable from the inventory GUI).
  - Natural spawns: **Wayfall warm biomes** (biome tag: `#kruemblegard:wayfall_warm`).
  - Wild Scaralons are skittish and will avoid players.
  - Tough baseline: high base **armor** and **armor toughness**.
  - Takes **no fall damage**.
  - Immune to **sweet berry bush / bramble / thorn** collision damage (including most modded thorny plants that reuse vanilla damage types).
  - Ground controls are **horse-like**: **WASD** to move; **Space** to jump; holding **Space** charges a higher jump.
  - Sprint: hold **Sprint** to move faster on the ground (and to get a small speed boost while flying).
  - Ground animations include horse-equivalent states: idle, walk, run (gallop), and jump.
  - Takeoff: release a sufficiently charged jump to launch high into the air and transition into **powered flight mode**.
  - Flight mode toggle: **double-tap Space** while grounded (also works as a true **double-jump** shortly after leaving the ground).
  - Flight controls: **Space** to ascend, **X** to descend, **WASD** to steer, **mouse pitch** to climb/dive while moving forward, **Left Shift** to dismount.
  - Flight mode automatically exits back to ground mode when the beetle touches solid ground.
  - Flight uses **stamina** (about **10 seconds** at full): while in flight mode you can hover/ascend/descend until stamina runs out; when empty, the beetle **forces an auto-land** (you lose fine control and it rapidly descends straight down). Stamina regenerates while on the ground.
  - **HUD:** while riding, flight stamina renders as a vanilla-style **melon-slice icon row** (hunger-like) so you can see how close you are to needing to land.
  - Safety: if you ride off a cliff or hit water, it automatically engages flight mode to prevent a hard fall. If you jump/fall off mid-air, you get a short slow-fall (no fall damage) and the beetle hovers nearby; stamina refills instantly.
  - Wayfall flavor: in the **Wayfall** dimension, unmounted Scaralons can **air-swim** through open air (Glow Squid-like) as a rescue/void-safety behavior.
    - They prefer ground locomotion when safe, but will float/roam when falling fast, over a long drop, or chasing a target.
  - Unmounted safety: when a wild/tame Scaralon is in **water** or **over the void**, it will engage flight and try to climb and find nearby solid ground to land on.
  - Unmounted flight autopilot: if an unmounted Scaralon ends up in **flight mode**, it will **keep moving** in the air (with brief hovers to change direction), **face its travel direction**, and will **always land eventually** (hard limit: about **1.5×** the max flight stamina time). When forced to land, it keeps searching until it finds a **safe solid landing zone** (requires solid support + headroom and rejects **water/liquids/waterlogged blocks**), and will roam outward / widen its search radius instead of hovering forever over oceans.
  - Attracted to **Melon Slices**.
  - Breeding: **Melon Slices only** (holding a melon slice prevents mounting; right-click will feed/breed instead).
  - Egg-laying: when bred, one parent will pick a nearby reachable spot and **lay Scaralon eggs** (turtle-style, **1–4 eggs**). Eggs hatch into **larva**, and extra Scaralon egg items can be stacked into that same nest block until it reaches a full 4-egg clutch.
  - Egg visuals: Scaralon eggs use a dedicated texture (`assets/kruemblegard/textures/block/scaralon_egg.png`) and the block model reflects the clutch size (1–4 eggs), turtle-style.
  - Egg predators: zombie-family mobs that smash turtle eggs will also smash Scaralon eggs; Scaralons will attack zombies to defend nests.
    - Larva grow into adults that inherit an adult texture variant from one of the parents (**50/50**), with a small chance of a random **mutation** variant.
  - Larva are **bucketable** (gives a `Scaralon Larva Bucket`).
  - Larva movement: larva **cannot fly**. When no adult Scaralon is nearby, they seek a **reachable** spot on the side of **tree trunks/branches** (logs, not leaves; must have nearby leaves), climb up the bark, and **sap-suck**, which makes them mature in about **half the time**. They detach if an adult returns, if they are knocked loose, or if the tree target stops being valid.
  - Larva can also **climb walls like spiders** when bumping into blocks, and visually rotate to align with the surface they’re climbing.
  - Harvest: right-click an adult with **Shears** to harvest `Rune-Etched Chitin Plates` (cooldown).
  - Drops `Bug Meat` (can be cooked into `Cooked Bug Meat` via furnace/smoker/campfire).
  - Renewable elytra loop: babies shed `Elytra Scutes` **when maturing into adults**.
    - Craft: `5x Elytra Scute → 1x Elytra Wing`.
    - Craft: `2x Elytra Wing → 1x Elytra`.
  - Chitin Plates can be crafted into **iron-equivalent** chitin armor pieces.
  - Rendered via GeckoLib:
    - Adult:
      - Geo: `assets/kruemblegard/geo/scaralon_beetle.geo.json`
      - Animations: `assets/kruemblegard/animations/scaralon_beetle.animation.json`
      - Textures: `assets/kruemblegard/textures/entity/scaralon_beetle/scaralon_beetle_1.png` .. `_9.png` (randomly assigned on spawn; bred offspring inherit 50/50 + small mutation chance)
      - Carpet textures (16 colors): `assets/kruemblegard/textures/entity/scaralon_beetle/decor/<color>.png`
      - Rider seat marker: geo bone named `seat` (its `pivot` is used to align the player while mounted)
    - Baby (larva form):
      - Geo: `assets/kruemblegard/geo/scaralon_larva.geo.json`
      - Animations: `assets/kruemblegard/animations/scaralon_larva.animation.json`
      - Texture: `assets/kruemblegard/textures/entity/scaralon_beetle/scaralon_larva.png`

- **Trader Beetle** (`trader_beetle`)
  - Wandering Trader mount variant of Scaralon.
  - **Despawns** on a timer (Trader Llama-style).
  - Wayfall behavior: Wandering Traders can spawn in **Wayfall**, and any trader that spawns there will **always** have a Trader Beetle mount.
  - Traders that spawn with a Trader Beetle mount will always have a **chest attached** containing some **wandering trader-themed loot**.
  - Default trader decor: spawns with the default trader-style carpet color.
  - Texture variants: Trader beetles only use Scaralon textures **1, 3, and 6**.
  - Breeding: Trader beetles can breed with regular Scaralons; eggs always hatch into a **regular Scaralon** (not a trader beetle).
  - Rendered via GeckoLib (same geo/animations as Scaralon):
    - Geo: `assets/kruemblegard/geo/scaralon_beetle.geo.json`
    - Animations: `assets/kruemblegard/animations/scaralon_beetle.animation.json`
    - Base textures: uses `assets/kruemblegard/textures/entity/scaralon_beetle/scaralon_beetle_1.png`, `_3.png`, and `_6.png`
    - Carpet textures (16 colors): `assets/kruemblegard/textures/entity/scaralon_beetle/trader_decor/<color>.png`

- **Wyrdwing** (`wyrdwing`)
  - Yi qi-inspired, membrane-winged creature with **default gliding** aerial locomotion.
  - Natural spawns: **Basin of Scars only** (biome tag: `#kruemblegard:basin_of_scars`).
  - Air locomotion:
    - **Glide** is the default (slows descent, adds slight forward drift).
    - **Flaps** when trying to gain altitude.
    - **Hovers** when nearly stationary in mid-air.
    - Has a simple **flight budget**: it prefers being airborne, but after staying in the air too long it becomes **exhausted** and will **force-land** to recover.
    - **Emergency void recovery**: if it falls below the world, it snaps back to a safe height.
    - Takes **no fall damage**.
  - Behavior:
    - **Tameable** with `Bug Meat`.
    - Wild Wyrdwings can be **mildly aggressive** (may decide to harass nearby players).
    - Attacks via **swoop** passes while airborne, and hunts **bug/arthropod** mobs.
    - Ground combat can include an optional **pounce** burst (animation hook: `animation.wyrdwing.pounce`).
    - Melee hits can optionally play an alternate **scratch/claw** animation (animation hook: `animation.wyrdwing.scratch`).
    - While idle on the ground, it can occasionally play flavor one-shots: **call** (`animation.wyrdwing.call_1`) and **shake** (`animation.wyrdwing.shake`).
    - Scavenges dropped `Bug Meat` items and plays an **eat** animation.
    - **Flees** (drops aggression) when low on health.
    - **Tree-seeking**: tends to pick nearby trees as perch targets when idle.
    - While airborne and idle, it will **orbit/circle** its chosen perch tree and can **fly toward perch targets** (instead of only walking to them).
    - Faces the direction it is moving in.
  - Rendered via GeckoLib:
    - Geo: `assets/kruemblegard/geo/wyrdwing.geo.json`
    - Animations: `assets/kruemblegard/animations/wyrdwing.animation.json`
    - Texture: `assets/kruemblegard/textures/entity/wyrdwing.png` (placeholder paint).

- **Vanilla mob tweaks (Wayfall)**
  - Silverfish spawn in **Faulted Expanse** and **Basin of Scars**.
  - Silverfish and Endermites drop `Bug Meat`.
  - Silverfish, Endermites, and Spiders avoid nearby Wyrdwings.

- **Cephalari** (`cephalari`)
  - Villager-class species: inherits vanilla villager behavior (AI/POIs/professions/trading/job blocks, including modded).
  - Custom professions:
    - **Keeper** uses the **Brine Garden Basin** (`brine_garden_basin`) job site.
      - Basin behavior: accepts volatile resin, rune petals, berry harvests, and seed stock; slowly propagates them into Bio-Resin, Moisture Stones, or extra crop stock.
      - Basin bonuses: works faster in wet biomes, around water/kelp, and near attuned stone or rubble tilth.
    - **Architect** uses the **Tendril-Forge** (`tendril_forge`) job site.
      - Forge behavior: accepts volatile resin plus runic materials and spins them into Tendril Strands, Resonance Shards, and Moisture Stones.
      - Forge bonuses: works faster in enclosed spaces and around attuned stone, runic debris, standing stones, or waypoint mold.
  - Job-site acquisition: the Brine Garden Basin and Tendril-Forge POIs are included in vanilla `acquirable_job_site` and `village` POI tags, so unemployed villagers and Cephalari can seek them out and adopt the matching professions through the normal brain flow.
  - Workstation visuals: Keepers and Architects now ship with dedicated profession overlays for both normal and zombified Cephalari renderers.
  - Trading: Keepers and Architects both use full five-tier trade pools with multiple offers per rank. Their trades now pull from Krümblegard crop stock, flora, runic materials, crafted workstation outputs, and their own job-site blocks in a tighter vanilla-style progression, with lower late-tier restock caps and master-only gift/masterwork offers that require both emeralds and a themed catalyst item.
  - Body texture variants: assigned at spawn/birth based on the biome.
    - Spawn: **10%** bonus variant, **5%** other-biome variant, otherwise current biome.
    - Breeding: baby texture is **25%** parent A / **25%** parent B / **50%** current biome, with a **10%** chance to be random.
  - Breeding: Cephalari can breed with Cephalari, but **cannot** cross-breed with vanilla villagers.
  - Wayfall-adapted: Cephalari take periodic suffocation damage in non-Wayfall dimensions **other than the Overworld**.
  - Mobility: takes **no fall damage**.
  - Targeting: hostile mobs treat Cephalari the same as vanilla villagers.
  - Personality: Cephalari have a small temperament variation (`calm`/`curious`/`skittish`) that slightly affects idle animation selection/tempo and movement speed, with occasional subtle one-shot “personality” idles.
  - Village defense: villages that spawn iron golems near Cephalari will spawn **Cephalari Golems** (`cephalari_golem`) instead (player-built / player-spawned iron golems are unaffected).
  - Adult forms: adult Cephalari use one of four adult-form body types.
    - **Spiral Strider** (`spiral_strider`)
    - **DriftSkimmer** (`driftskimmer`)
    - **Treadwinder** (`treadwinder`)
    - **Echo Harness** (`echo_harness`)
    - Rendering: all four adult-form geos use the same embedded Cephalari shell/profession UV mapping so the rider body texture and profession layers render consistently regardless of adult-form type.
    - Animation routing: live adult-form Cephalari now use the matching form-specific animation resource for idle, move, trade, work, celebrate, hurt, riding, zombify, death, and sleep rather than mixing those states back through the base `cephalari.animation.json` file.
    - Bed pose mirroring: the adult-form sleep correction still only mirrors east-west bed facings, and that bed-specific correction layers separately on top of the deterministic left-handed spawn variant.
    - These adult-form entities are **not rideable by players** (the Scaralon Beetle is the only player-rideable mob).
  - Trading: if a Cephalari has a profession, right-clicking the Cephalari opens the Cephalari trading UI.
  - Zombification: converts into an undead Cephalari variant when killed by an undead; curing converts back into Cephalari.
    - **Zombified Cephalari** (`cephalari_zombie`) when killed by a Zombie.
    - **Husked Cephalari** (`cephalari_husk`) when killed by a Husk.
    - **Drowned Cephalari** (`cephalari_drowned`) when killed by a Drowned.
    - Cinematic: conversion plays a 2.2s transformation (cephalari collapse + forced riding-pose sync before swap; no spawned zombie mount entity).
    - Natural spawns:
      - Overworld: Zombified Cephalari can spawn in the Overworld.
      - Wayfall (biome-driven split):
        - Husked Cephalari spawn only in the 3 hottest + 2 coldest Wayfall biomes (tag: `#kruemblegard:wayfall_cephalari_husk`).
        - Zombified Cephalari spawn in the remaining Wayfall biomes (tag: `#kruemblegard:wayfall_cephalari_zombie`).
        - Drowned Cephalari spawn in water across all Wayfall biomes.
    - Water conversions (vanilla-like): Husked -> Zombified when submerged long enough; Zombified -> Drowned when submerged long enough.
    - Baby zombies: baby undead Cephalari always spawn as a jockey (guaranteed random mount). Those mounts are forced permanently hostile with the same base target classes as a zombie, while the rider is alive the mount copies the rider's current target so it will keep attacking that victim even if the Cephalari zombie dies first, and the passive animal mount pool is given Forge-side `attack_damage` support so hostile pigs, sheep, rabbits, and chickens can safely use melee attack AI.
    - Drowned visual: drowned Cephalari render a vanilla Drowned-style outer overlay layer.
    - Loot: undead variants use the same loot behavior as their vanilla counterparts (Zombie/Husk/Drowned).
    - VFX: cure/zombify use dedicated particle sprites in `assets/kruemblegard/textures/particle/` (shell dust/fragments/spirals + zombify cracks).
    - Audio: cure/zombify currently reuse vanilla zombie-villager cure/conversion sound events until dedicated Cephalari audio assets are restored.
  - Visual (adult-form appearance): the base render uses a Cephalari golem texture on non-`cephalari` bones (body), and overlay passes re-texture only the embedded `cephalari` subtree using the Cephalari body texture plus profession + badge overlays on their dedicated bones.
    - Visual (adult-form entities): the stand-alone adult-form entities (`spiral_strider`, `driftskimmer`, `treadwinder`, `echo_harness`) render their embedded `cephalari` subtree using the adult-form entity's own stored body texture + profession/level data (no passenger linkage required).
    - Visual (adult-form outer-form bones): all 4 adult-form outer-form bones use the shared golem-atlas path, and both the live Cephalari adult-form appearance renderer and the stand-alone adult-form renderer draw the outer shell, embedded `cephalari` subtree, and profession overlays through explicit per-bone layer passes instead of mixing in GeckoLib's default base cube pass.
    - Animation (adult forms): Spiral Strider, DriftSkimmer, Treadwinder, and Echo Harness now each have a dedicated sleep loop that plays both for stand-alone adult-form entities after they settle through the night and for bed-sleeping Cephalari villagers when those villagers are rendered through an adult-form appearance, with the in-bed pose mirror restricted to east-west bed facings and composed separately with the deterministic left-handed mob variant so one authored disconnected rig can sleep cleanly on both sides of the bed.
  - Visual (profession overlays): when a Cephalari has a profession, it renders profession overlays on the dedicated `profession` and `profession_hat` geo bones and the level badge overlay on the dedicated `profession_level` bone. For both profession and level badge textures it prefers a mod-provided texture under the profession's namespace, falling back to vanilla villager (or zombie-villager) textures.
  - Visual (zombified): rendered as Scaralon-style deterministic cutout/no-cull layered passes: zombified **inner layer** first, then the selected zombified **outer layer** overlay, then profession + badge overlays. The dedicated profession overlay bones are only painted by the profession layer (inner/outer passes do not paint those bones).
  - Rendered via GeckoLib (placeholder assets):
    - Geo: `assets/kruemblegard/geo/cephalari.geo.json` and `assets/kruemblegard/geo/cephalari_zombie.geo.json` (babies)
    - Geo (adult zombie variants): `assets/kruemblegard/geo/cephalari_zombie_1.geo.json` .. `_5.geo.json`
    - Animations: `assets/kruemblegard/animations/cephalari.animation.json` and `assets/kruemblegard/animations/cephalari_zombie.animation.json`
    - Textures (Cephalari): `assets/kruemblegard/textures/entity/cephalari/cephalari/*.png`
    - Textures (Zombified Cephalari): `assets/kruemblegard/textures/entity/cephalari/cephalari_zombie/*.png`
  - Adult-form assets (GeckoLib placeholder assets):
    - Geo: `assets/kruemblegard/geo/{spiral_strider,driftskimmer,treadwinder,echo_harness}.geo.json`
    - Animations: `assets/kruemblegard/animations/{spiral_strider,driftskimmer,treadwinder,echo_harness}.animation.json`
    - Textures: shared variants reused from the golem set `assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_1.png` .. `_6.png`

- **Cephalari Golem** (`cephalari_golem`)
  - Iron Golem-class village defender used by Cephalari villages.
  - Vanilla parity: uses vanilla Iron Golem attributes, combat, roaming, and village-defense behavior.
  - Animation states: switches between separate passive and angry idle/move loops based on synced server target state, so the hostile pose appears during pursuit and drops back to passive immediately when the golem no longer has a target.
  - Mob interaction parity: because the entity inherits from `IronGolem`, vanilla and modded mobs that use the normal Iron Golem class-based targeting or avoidance checks treat Cephalari Golems the same way; the village replacement path now also runs full spawn finalization so the replacement keeps normal roaming state.
  - Runebloom offering: when offering a flower, it visually offers a **Runebloom** to nearby child villagers (vanilla or Cephalari).
  - Spawn egg: `Cephalari Golem Spawn Egg`.
  - Loot: drops Runebloom with the same count/chances as vanilla iron golems drop poppies.
  - Rendered via GeckoLib:
    - Geo: `assets/kruemblegard/geo/cephalari_golem.geo.json`
    - Animations: `assets/kruemblegard/animations/cephalari_golem.animation.json`
    - Controllers: `assets/kruemblegard/controllers/cephalari_golem.animation_controllers.json`
    - Textures: `assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_1.png` .. `_6.png`

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

- **Crushstone tool set**
  - Crushstone Pickaxe (`crushstone_pickaxe`)
  - Crushstone Axe (`crushstone_axe`)
  - Crushstone Shovel (`crushstone_shovel`)
  - Crushstone Hoe (`crushstone_hoe`)
  - Tier: stone-equivalent starter tools.
  - Crafting: vanilla-shaped recipes using `kruemblegard:crushstone` + sticks.
  - Repair: repairable with Crushstone (via vanilla `minecraft:stone_tool_materials` tag).

- **Scarsteel progression set**
  - Scarsteel Ingot (`scarsteel_ingot`)
  - Scarsteel Sword (`scarsteel_sword`)
  - Scarsteel Pickaxe (`scarsteel_pickaxe`)
  - Scarsteel Axe (`scarsteel_axe`)
  - Scarsteel Shovel (`scarsteel_shovel`)
  - Scarsteel Hoe (`scarsteel_hoe`)
  - Scarsteel Hammer (`scarsteel_hammer`)
  - Scarsteel Helmet (`scarsteel_helmet`)
  - Scarsteel Chestplate (`scarsteel_chestplate`)
  - Scarsteel Leggings (`scarsteel_leggings`)
  - Scarsteel Boots (`scarsteel_boots`)
  - Tier: diamond-equivalent custom tool tier (`ModTiers.SCARSTEEL`) repaired with `scarsteel_ingot`.
  - Crafting: Scarsteel Ingot is crafted from Scarstone + Attuned Ingot, then used for the full tool, weapon, hammer, and armor line.
  - Armor: uses a dedicated Scarsteel armor material with diamond-equivalent stats and `scarsteel_ingot` repair behavior, while still reusing vanilla diamond armor textures for now.
  - Current placeholder state: the hammer is a generic item without bespoke hammer logic yet, and item art currently uses placeholder vanilla textures.

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
  - Underway Falls jungle temples swap temple stone blocks (including mossy variants/stairs/slabs/walls) to the Scarstone family.
  - Implementation note: retheming runs on chunk load but avoids heavy neighbor-update storms to keep exploration/teleport testing smooth.
  - Stability note: retheme tracking is intentionally capped/evicted so it can’t grow without bound and stall autosaves in long /tp scouting sessions.

- Wayfall-only custom structure: **Lost Pillager Ship** (`lost_pillager_ship`)
  - Outpost-style hostile spawns (pillagers spawn within the structure bounding box).
  - Uses a Sponge/WorldEdit `.schem` under `data/kruemblegard/schematics/structures/lost_pillager_ship.schem`.
  - Marker processing (in the schematic): white stained glass → air; red wool → adjacent planks (spruce fallback); red stained glass → placement/ground reference (removed); beacon → vindicator spawner; pink wool → bastion-treasure chest.
  - Container loot: defaults to ~60% village-house loot and ~40% pillager-outpost loot (pink marker chests always use bastion treasure loot).

- Wayfall initialization (spawn island):
  - By default, initialization only runs when it’s actually needed (on entry).
  - Optional: `wayfallPreloadOnServerStart` (COMMON config) can be enabled to attempt a one-time pre-player preload, but may cause startup stutter on some systems.

- Wayfall sleeping:
  - Beds work normally (no explosions) and set your spawn.
  - You can sleep at any time, but sleeping does **not** advance time; you wake after a short nap.
  - First Wayfall sleep grants the advancement **“Time Is an Illusion, Lunchtime Doubly So.”**

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
- Wayfall wood families: 14 custom woods with vanilla-style building sets, including signs/hanging signs and boats/chest boats.
- Attuned Ore block (Wayfall-only worldgen).
- Wayfall Iron Ore + Wayfall Copper Ore + Wayfall Diamond Ore blocks (Wayfall worldgen; diamond spawns lower on average).
- Wayfall geology palette blocks:
  - Fractured Wayrock, Crushstone, Ashfall Loam, Fault Dust.
  - Scarstone family: Scarstone, Cracked Scarstone, Polished Scarstone, Chiseled Scarstone.
  - Stoneveil Rubble family: Stoneveil Rubble, Polished Stoneveil Rubble, Runed Stoneveil Rubble.
  - Stone-family variants exist for key geology blocks: stairs, slabs, and walls (Attuned Stone, Fractured Wayrock, Crushstone, Scarstone + variants, Stoneveil Rubble + variants).
  - Crafting integration: Wayfall stone blocks are added to vanilla stone material tags so they can be used for stone tools and common stone crafting (furnace/stonecutter + many redstone components).
- Wayfall flora (plants + shrubs + fungi), including Wispstalk, Gravevine (vines), Pyrokelp (vanilla-style head/body plant; generates as pre-grown columns in Basin of Scars), Runebloom, Soulberry Shrub (can corrupt into Ghoulberry Shrub), and Wayfall fungi (Black Echo Fungus, Echocap, Echo Puff, Griefcap, Static Fungus, Voidcap Briar, Wayburn Fungus, Wayrot Fungus, Memory Rot).
  - Survival farming: Soulberries/Ghoulberries/Wispshoot are edible and can be planted to grow their matching plants.
  - Giant fungi: the nine Wayfall fungi can be bonemealed into giant cap/stem variants and also generate naturally as giants in the same biomes as their small variants.
    - Worldgen cohesion: giant fungi silhouettes are schematic-based (30 shared templates) and vanilla huge red/brown mushrooms also use the same schematic system when generated.
      - Natural generation: huge red/brown mushrooms generate naturally in `minecraft:mushroom_fields` and vanilla forest biomes that normally get huge mushrooms (Dark Forest and Old Growth forests/taigas).
    - Schematic palette remaps use placeholders: tinted glass → air; quartz block → stem; quartz slab → mushroom slab; brown mushroom block → cap.
    - Shatterplate Flats: includes all giant fungi except Wayburn.
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
  - Waylily grows kelp-style: an underwater stalk rises from the floor, shows an underwater bud while submerged, and blooms into an above-water surface flower when it reaches the surface (worldgen uses the `waylily_patch*` placed features).
  - The deep lake feature (`wayfall_big_water_lake`) also adds extra Waylily on exposed lake surfaces and adds underwater seagrass by dressing parts of the lake floor (sand/gravel/clay) during generation.
  - Deep lakes are clamped to a chunk-safe size during worldgen to avoid “setBlock in a far chunk” warnings and severe world creation lag.
  - Underwater plants scan downward from the surface to the bottom of water columns, so they can survive and generate inside floating-island lakes.
  - Warm Wayfall biomes (tagged `#kruemblegard:wayfall_warm`) also get vanilla coral fans + sea pickles underwater.
- Wayfall staple flora (new): Voidfern, Runeblossom, Moteshrub, Ashveil, Twilight Bulb, Whispervine.
- Wayfall trees (block sets): logs/planks/leaves/saplings exist as blocks/items.
  - Wayfall wood families include `*_log`, `*_wood`, and stripped `stripped_*_{log,wood}` variants.
  - Most Wayfall tree configured features use `kruemblegard:fault_dust` as their `dirt_provider` (no `minecraft:rooted_dirt` placements).
  - Axes strip Wayfall logs/wood into their stripped variants.
  - Most saplings grow over time via random ticks (no light requirement) or bonemeal.
    - Exception: Ashbloom saplings are bonemeal-only (azalea-style).
    - Wayfall saplings grow into their matching worldgen configured features (variant selectors), matching natural generation.
    - All Wayfall saplings support 2x2 (spruce-style) placement to grow a larger **mega** tree, using Evergreen-style multi-option selectors.
  - All Wayfall trees have a rare **mega** variant that can also generate naturally in Wayfall.
    - Most mega selectors choose between two Evergreen-style mega outcomes; each `mega_1`/`mega_2` delegates to that tree’s existing large “assembled” variants (typically `*/5` and `*/4`).
    - Wayroot and Echowood are exceptions and are schematic-driven:
      - Wayroot is tuned to a baobab-style silhouette and uses `kruemblegard:ashmoss` as its `dirt_provider`.
        - Normal Wayroot is fully schematic-driven and selects from 5 templates (3 living + 2 dead), with living variants weighted slightly higher.
        - Wayroot saplings only grow into the 3 living schematic variants.
        - Mega Wayroot uses 3 curated schematic-based variants (to keep the silhouette stable and prevent leaf decay/drop spam on chunk load).
        - Wayroot schematic “processors” (palette remaps) enforce consistent Wayroot materials:
          - Tinted glass → structure void (ignored)
          - Tripwire → `string_franch`
          - Any leaves (any species) → Wayroot leaves, forced non-persistent
          - Any log/wood (any species) → 90% Wayroot franch wood, 10% Wayroot franch planks
          - Any wooden fence / fence gate / trapdoor placeholders in the template → remapped into Wayroot `*_franch*` helper blocks
      - Echowood is fully schematic-driven.
        - Echowood saplings only grow into the 3 living schematic variants.
        - Natural Echowood worldgen can also select from 2 dead schematic variants.
        - Mega Echowood uses 3 curated schematic-based variants.
        - Echowood schematic “processors” (palette remaps) enforce consistent Echowood materials:
          - Tinted glass → structure void (ignored)
          - Tripwire → `string_franch`
          - Any leaves (any species) → Echowood leaves, forced non-persistent
          - Red wool (schematic center marker) → Echowood franch wood
          - Any log/wood (any species) → 90% Echowood franch wood, 10% Echowood franch planks
        - Implementation checklist / standards: [TREE_SCHEMATICS.md](TREE_SCHEMATICS.md)
  - All Kruemblegard wood-family blocks are flammable like vanilla (logs/wood/leaves + planks and wooden derivatives like slabs/stairs/fences/doors/signs; saplings burn fast).
  - Schematic-only “Franch” helper blocks:
    - Leaf-network helpers: `*_franch`, `*_franch_gate`, `*_franch_slab`, `*_franch_stairs`, `*_franch_trapdoor` (includes vanilla wood types)
      - Behave like fences / fence gates (connectivity + collision + rendering), but also decay like leaves when not persistent.
      - Not craftable and intentionally has no block item; intended for commands and schematic placement only.
      - Drops nothing when broken/decayed (schematic helper blocks).
      - Included in `minecraft:leaves` (block tag) so Tree Harvester treats them like leaves for proximity checks.
      - Franch blocks participate in an extended 10-block log-distance network and can extend Kruemblegard leaf connectivity without acting as distance-0 "roots" by themselves.
        - Note: vanilla `minecraft:*_leaves` still use the vanilla 7-block decay rules; the extended 10-block logic applies to Kruemblegard leaves and all franch blocks.
    - `string_franch` is a special schematic helper used as a tripwire placeholder in templates; it is invisible and has no collision.
    - Schematic-only “Franch planks”: `*_franch_planks` (includes vanilla wood types)
      - Behave like normal planks (no decay) and pick-block returns the matching normal plank block.
      - Default drops are 50/50 normal planks vs stick (loot-table driven).
      - When common tree-harvester mods are present, drops are 1/3 planks, 1/3 stick, 1/3 nothing (to reduce excessive drops during full-tree harvesting).
  - Schematic-only “Franch wood” trunk blocks: `*_franch_wood` (includes vanilla wood types)
    - Behave like normal wood blocks (axis rotation, flammability, no decay).
    - Intended for schematic placement; display name matches normal wood (e.g., “Spruce Wood”) and pick-block returns the normal wood block.
    - Default drops are 50/50 wood vs log (per broken block).
    - When common tree-harvester mods are present, drops are 1/3 wood, 1/3 log, 1/3 nothing (to reduce excessive trunk drops during full-tree harvesting).
  - Leaf drop tuning:
    - Wayfall leaves have reduced sapling and stick drop rates (Fortune-scaled chances are halved).
    - Mega Wayroot schematics place special schematic-only `*_mega_franch_leaves` (not obtainable via pick-block); these have half the normal Wayfall leaf drop chances again.
  - Staple wood sets: Ashbloom, Glimmerpine, Driftwood.
  - Custom worldgen Features exist for data-driven placement (`registry/ModFeatures`):
    - `world/feature/WayfallSimpleTreeFeature` (simple tree helper)
    - `world/feature/EchowoodMegaSchematicFeature` (places curated mega Echowood schematics)
    - `world/feature/EchowoodSchematicFeature` (places normal Echowood schematics with palette remaps)
    - `world/feature/WayrootMegaSchematicFeature` (places curated mega Wayroot schematics)
    - `world/feature/WayrootSchematicFeature` (places normal Wayroot schematics with palette remaps)
    - `world/feature/GiantMushroomSchematicFeature` (places giant Wayfall fungi from shared schematic templates)
  - Giant Wayfall fungi (Griefcap, Static Fungus, Wayrot Fungus, Wayburn Fungus, Black Echo Fungus, Echocap, Echo Puff, Voidcap Briar, Memory Rot) use 30 shared schematic variants with palette remapping:
    - Tinted glass → air (ignored)
    - Quartz blocks → the species stem
    - Quartz slabs → the species cap slab
    - Brown mushroom blocks → the species cap
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
- Current Wayfall runegrowth surface assignment:
  - **Frostbound Runegrowth**: Hollow Transit Plains, Glyphscar Reach.
  - **Verdant Runegrowth**: Midweft Wilds, Shatterplate Flats, Crumbled Crossing, Underway Falls.
  - **Emberwarmed Runegrowth**: Basin of Scars, Fracture Shoals, Strata Collapse.
  - **Resonant Runegrowth**: Driftway Chasm, Faulted Expanse, Riven Causeways.
  - Hot-biome shallow cuts: Basin of Scars, Fracture Shoals, and Strata Collapse now keep **Emberwarmed Runegrowth** through the first shallow subsurface layer before falling back to **Fault Dust**.
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
- Wayfall ambient music uses `kruemblegard:music.wayfall`, mapped in `assets/kruemblegard/sounds.json` to `assets/kruemblegard/sounds/horror-background-atmosphere-09_universfield.ogg`.

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
