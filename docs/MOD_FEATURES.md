# Krümblegård — Mod Features Reference

This document is a **complete, living reference** of current Krümblegård features.

Keep it up to date whenever you add/remove/rename content.

## Documentation
- Block material rules + full block inventory: `docs/Block_Material_Bible.md`
- Item material rules + full item inventory: `docs/Item_Material_Bible.md`
- After registry changes, refresh the generated inventories by running: `tools/generate_material_bibles.ps1`

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

## Boss: Krümblegård
- **4 phases** with phase-specific visuals (bone hiding) and locomotion.
- **12 unique attacks** across phases (fast/heavy/ranged kits per phase).
- Phase transitions trigger one-shot transition animations.
- Boss awards **Ender Dragon XP** on death.

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
  - Tier: Runic tools are above Netherite.
  - Crafting: Runic tools require Attuned Rune Shards + a Runic Core catalyst.

## Creative inventory

- `wayfall_portal`: a simple testing portal block you can place in creative; walking into it sends you to the Wayfall spawn.

- `ancient_waystone`: a **Waystones-backed** waystone variant (two-block tall like Waystones waystones) that opens the Waystones menu.
  - Requires Waystones + Balm.
  - Editable texture: `assets/kruemblegard/textures/block/ancient_waystone.png`
  - Models follow Waystones' top/bottom layout: `assets/kruemblegard/models/block/ancient_waystone_{bottom,top}.json`

## Wayfall Skybox (Client)
- Wayfall renders a custom sky in-game (client-only).
- Uses a single equirectangular panorama texture (2:1 aspect ratio):
  - `assets/kruemblegard/textures/environment/wayfall_panorama.png`
- A placeholder can be generated via:
  - `tools/generate_wayfall_panorama_placeholder.ps1`

- Standing Stone + Attuned Stone blocks.
- Attuned Ore block (Wayfall-only worldgen).
- Wayfall geology palette blocks:
  - Fractured Wayrock, Crushstone, Ashfall Loam, Fault Dust.
  - Scarstone family: Scarstone, Cracked Scarstone, Polished Scarstone, Chiseled Scarstone.
  - Stoneveil Rubble family: Stoneveil Rubble, Polished Stoneveil Rubble, Runed Stoneveil Rubble.
  - Stone-family variants exist for key geology blocks: stairs, slabs, and walls (Attuned Stone, Fractured Wayrock, Crushstone, Scarstone + variants, Stoneveil Rubble + variants).
- Wayfall flora (plants + shrubs + fungi), including Wispstalk, Gravevine, Echocap, Runebloom, Soulberry Shrub (can corrupt into Ghoulberry Shrub) and additional Wayfall plants.
  - Detailed reference (keep updated): docs/FLORA_REFERENCE.md
- Wayfall staple flora (new): Voidfern, Runeblossom, Moteshrub, Ashveil, Twilight Bulb, Whispervine.
- Wayfall trees (block sets): logs/planks/leaves/saplings exist as blocks/items.
  - Wayfall wood families include `*_log`, `*_wood`, and stripped `stripped_*_{log,wood}` variants.
  - Axes strip Wayfall logs/wood into their stripped variants.
  - Saplings can grow via random ticks or bonemeal.
    - Wayfall saplings grow into their matching worldgen configured features (variant selectors), matching natural generation.
  - Staple wood sets: Ashbloom, Glimmerpine, Driftwood.
  - A simple custom tree Feature exists for data-driven placement (`registry/ModFeatures` + `world/feature/WayfallSimpleTreeFeature`).
  - Full per-tree reference: [TREES.md](TREES.md)

### Reactive staple plants
- Some Wayfall staples use a “reactive” plant base that can activate near blocks tagged as `kruemblegard:waystone_energy_sources`.

### Wayfall surface covers
- Ashmoss is a moss block surface.
- Runegrowth is a grass block surface.
- Voidfelt is a podzol-like dirt surface.
- Runegrowth emerges near blocks tagged as `kruemblegard:waystone_energy_sources`.
- Ashmoss prefers “ash-heavy” Wayfall biomes (tag: `kruemblegard:wayfall_ash_heavy`).
- Voidfelt is primarily generated in void biomes (tag: `kruemblegard:wayfall_void`) and slowly reverts outside them.

## Dimensions
  - Void dimension with Aether-inspired floating islands.
  - Uses only Krümblegård Wayfall biomes (no vanilla biomes).
  - Detailed biome list (keep updated): docs/WAYFALL_BIOMES.md
  - Attuned Ore generates here.
  - Spawns in Wayfall are limited to Krümblegård mobs (no vanilla mob spawns).
- Wayfall uses custom noise settings (`kruemblegard:wayfall`) with `kruemblegard:fractured_wayrock` as the base terrain block.
- Wayfall terrain shaping is driven by custom `worldgen/noise/**` + `worldgen/density_function/**` entries (instead of End island routing).
- Safety: entering Wayfall via the portal searches for nearby solid ground and will generate a small platform if the spawn area is void.
- Safety: falling into the Wayfall void teleports you to a precomputed safe landing spot in a random dimension (including mod dimensions), and immediately prepares the next destination.
- Wayfall surface palette can vary by biome tag:
  - `#kruemblegard:wayfall_ash_heavy` → surface defaults to **Ashfall Loam** with shallow **Crushstone** beneath.
  - `#kruemblegard:wayfall_void` → surface defaults to **Voidfelt** with shallow **Crushstone** beneath.
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
- Wayfall ambient music uses `kruemblegard:music.wayfall` (source file: `assets/kruemblegard/sounds/jungle-ish-beat-for-video-games.ogg`).
- Item: `wayfall_music_disc` plays the Wayfall ambient track in jukeboxes.

## Config
- Common config: `config/kruemblegard-common.toml`
- Includes `enableWaystones` and `waystoneRarity` (see `config/ModConfig`).

## Worldgen
- Worldgen is primarily data-driven under `data/kruemblegard/worldgen/**`.
- Important worldgen IDs are centralized in `src/main/java/com/kruemblegard/worldgen/ModWorldgenKeys.java`.
- Critical datapack registry entries/tags are sanity-checked on server start via `com.kruemblegard.worldgen.WorldgenValidator`.
- Wayfall flora placement is data-driven via biome modifiers/tags/worldgen JSON.
- Wayfall flora is injected via Forge biome modifiers:
  - Global “staple” trees/plants are injected into all Wayfall biomes via `data/forge/biome_modifier/add_wayfall_plants.json`.
  - Biome-specific trees/plants are injected via `data/forge/biome_modifier/add_wayfall_*_flora.json`.

## Removed / not present (by design)
- `false_waystone` (block + worldgen + biome modifier/tag) was removed.
- `radiant_essence` was removed.
- `runic_core_fragment` was removed.
