# Krümblegård — Flora Reference (Plants & Trees)

This document is the **authoritative, up-to-date reference** for every **plant and tree** Krümblegård adds.

## Update rules (keep this current)
- If you add/rename/remove any plant/tree (block, item, worldgen, or behavior), **update this file in the same PR/commit**.
- If you change how a plant grows/drops/generates, update:
  - this file
  - [docs/MOD_FEATURES.md](MOD_FEATURES.md) (summary bullets)
  - [CHANGELOG.md](../CHANGELOG.md) if the change affects gameplay/content

## Texture workflow (so textures don’t get forgotten)
- Placeholder textures are currently maintained manually (no generator script is checked into `tools/`).
- Helpful tooling:
  - `tools/audit_block_textures.py` (checks blockstates/models/textures consistency)
  - `tools/resize_textures_to_256.ps1` (batch resize + reporting)
- These textures live under:
  - `src/main/resources/assets/kruemblegard/textures/block/`
  - `src/main/resources/assets/kruemblegard/textures/item/`
- When you replace placeholders with real art, keep the **same filenames** so models don’t need changes.

## Trees
Krümblegård has Wayfall tree **block sets implemented** (logs/planks/leaves/saplings as blocks + items).

Current limitations:
- Wayfall saplings grow into their matching worldgen configured features (variant selectors) via random ticks or bonemeal.
- Wayfall trees and plants are placed via configured/placed features and injected into biomes via Forge biome modifiers.
- The "Mechanic" notes below are design targets (special behaviors may still be WIP).

### Wayfall trees (block sets implemented; mechanics WIP)
These trees exist as blocks/items with placeholder textures. Generation + special mechanics can be implemented later.

Full per-tree reference: [TREES.md](TREES.md)

#### Ashbloom
- Appearance: Ash-toned wood family intended as a staple Wayfall starter palette.
- Use: Standard wood building set.
- Mechanic: No special mechanics yet.
- Placeholder textures:
  - `assets/kruemblegard/textures/block/ashbloom_log.png`
  - `assets/kruemblegard/textures/block/ashbloom_planks.png`
  - `assets/kruemblegard/textures/block/ashbloom_leaves.png`
  - `assets/kruemblegard/textures/block/ashbloom_sapling.png`

#### Glimmerpine
- Appearance: Pine-like wood family intended as a staple Wayfall palette.
- Use: Standard wood building set.
- Mechanic: No special mechanics yet.
- Placeholder textures:
  - `assets/kruemblegard/textures/block/glimmerpine_log.png`
  - `assets/kruemblegard/textures/block/glimmerpine_planks.png`
  - `assets/kruemblegard/textures/block/glimmerpine_leaves.png`
  - `assets/kruemblegard/textures/block/glimmerpine_sapling.png`

#### Driftwood
- Appearance: Weathered, pale wood intended as a staple Wayfall palette.
- Use: Standard wood building set.
- Mechanic: No special mechanics yet.
- Placeholder textures:
  - `assets/kruemblegard/textures/block/driftwood_log.png`
  - `assets/kruemblegard/textures/block/driftwood_planks.png`
  - `assets/kruemblegard/textures/block/driftwood_leaves.png`
  - `assets/kruemblegard/textures/block/driftwood_sapling.png`

#### Wayroot Tree
- Appearance: Stone-barked tree with exposed, floating roots that don’t touch the ground.
- Use: Logs used for waystone upgrades and teleport stabilizers.
- Mechanic: Roots slowly drift; breaking them can destabilize nearby blocks.
- Worldgen: Wayroot uses `kruemblegard:ashmoss` as its dirt provider and generates as a baobab-style silhouette using multipart canopy/branch placement.
- Placeholder textures:
  - `assets/kruemblegard/textures/block/wayroot_log.png`
  - `assets/kruemblegard/textures/block/wayroot_planks.png`
  - `assets/kruemblegard/textures/block/wayroot_leaves.png`
  - `assets/kruemblegard/textures/block/wayroot_sapling.png`

#### Fallbark
- Appearance: Tall, brittle trees that constantly shed slabs of bark downward.
- Use: Bark used for impact-resistant blocks.
- Mechanic: Standing under them is dangerous during “Wayfall tremors.”
- Placeholder textures:
  - `assets/kruemblegard/textures/block/fallbark_log.png`
  - `assets/kruemblegard/textures/block/fallbark_planks.png`
  - `assets/kruemblegard/textures/block/fallbark_leaves.png`
  - `assets/kruemblegard/textures/block/fallbark_sapling.png`

#### Echowood
- Appearance: Smooth gray wood with faint ripple patterns.
- Use: Craft sound-reactive blocks or redstone-like devices.
- Mechanic: Emits ambient noises tied to nearby player activity.
- Placeholder textures:
  - `assets/kruemblegard/textures/block/echowood_log.png`
  - `assets/kruemblegard/textures/block/echowood_planks.png`
  - `assets/kruemblegard/textures/block/echowood_leaves.png`
  - `assets/kruemblegard/textures/block/echowood_sapling.png`

#### Cairn Tree
- Appearance: Tree grown through stacked stones, leaves shaped like memorial flags.
- Use: Drops Memorial Sap for soul-based crafting.
- Mechanic: Grows only near death markers or cairns.
- Placeholder textures:
  - `assets/kruemblegard/textures/block/cairn_tree_log.png`
  - `assets/kruemblegard/textures/block/cairn_tree_planks.png`
  - `assets/kruemblegard/textures/block/cairn_tree_leaves.png`
  - `assets/kruemblegard/textures/block/cairn_tree_sapling.png`

#### Wayglass Tree
- Appearance: Crystalline trunk, translucent branches.
- Use: Glass-like wood for light-conducting blocks.
- Mechanic: Refracts light and enchantment particles.
- Placeholder textures:
  - `assets/kruemblegard/textures/block/wayglass_log.png`
  - `assets/kruemblegard/textures/block/wayglass_planks.png`
  - `assets/kruemblegard/textures/block/wayglass_leaves.png`
  - `assets/kruemblegard/textures/block/wayglass_sapling.png`

#### Splinterspore
- Appearance: Tall, needle-like crystal bark.
- Use: Craft piercing projectiles.
- Placeholder textures:
  - `assets/kruemblegard/textures/block/splinterspore_log.png`
  - `assets/kruemblegard/textures/block/splinterspore_planks.png`
  - `assets/kruemblegard/textures/block/splinterspore_leaves.png`
  - `assets/kruemblegard/textures/block/splinterspore_sapling.png`

#### Hollowway Tree
- Appearance: Trunk split down the center, empty inside.
- Use: Craft storage blocks with void-linked inventory.
- Placeholder textures:
  - `assets/kruemblegard/textures/block/hollowway_tree_log.png`
  - `assets/kruemblegard/textures/block/hollowway_tree_planks.png`
  - `assets/kruemblegard/textures/block/hollowway_tree_leaves.png`
  - `assets/kruemblegard/textures/block/hollowway_tree_sapling.png`

#### Driftwillow
- Appearance: Hanging branches that never quite touch the ground.
- Use: Slow-fall talismans.
- Placeholder textures:
  - `assets/kruemblegard/textures/block/driftwillow_log.png`
  - `assets/kruemblegard/textures/block/driftwillow_planks.png`
  - `assets/kruemblegard/textures/block/driftwillow_leaves.png`
  - `assets/kruemblegard/textures/block/driftwillow_sapling.png`

#### Monument Oak
- Appearance: Massive stone-wood hybrid, bark etched with history.
- Use: Lore crafting, advancement triggers.
- Placeholder textures:
  - `assets/kruemblegard/textures/block/monument_oak_log.png`
  - `assets/kruemblegard/textures/block/monument_oak_planks.png`
  - `assets/kruemblegard/textures/block/monument_oak_leaves.png`
  - `assets/kruemblegard/textures/block/monument_oak_sapling.png`

#### Waytorch Tree
- Appearance: Glowing nodes along trunk like lanterns.
- Use: Renewable light sources.
- Placeholder textures:
  - `assets/kruemblegard/textures/block/waytorch_tree_log.png`
  - `assets/kruemblegard/textures/block/waytorch_tree_planks.png`
  - `assets/kruemblegard/textures/block/waytorch_tree_leaves.png`
  - `assets/kruemblegard/textures/block/waytorch_tree_sapling.png`

#### Faultwood
- Appearance: Jagged, cracked trunk constantly shedding chips.
- Use: Reinforced building blocks.
- Placeholder textures:
  - `assets/kruemblegard/textures/block/faultwood_log.png`
  - `assets/kruemblegard/textures/block/faultwood_planks.png`
  - `assets/kruemblegard/textures/block/faultwood_leaves.png`
  - `assets/kruemblegard/textures/block/faultwood_sapling.png`

## Plants

### Wayfall surface covers (blocks)
These are surface materials that define the Wayfall ground language. They are documented here because they are part of the flora/surface ecosystem.

#### Ashmoss
- **IDs**
  - Block: `kruemblegard:ashmoss`
  - Item: `kruemblegard:ashmoss` (BlockItem)
- **How it behaves**
  - Intended for ash-heavy biomes.
  - As of 1.0.582, natural random-tick spread/collapse is disabled to reduce Wayfall server tick lag; Ashmoss can still be spread via bonemeal/worldgen.
- **Assets**
  - Texture: `assets/kruemblegard/textures/block/ashmoss.png`

#### Runegrowth
- **IDs**
  - Block: `kruemblegard:runegrowth`
  - Item: `kruemblegard:runegrowth` (BlockItem)
- **How it behaves**
  - Visual biome-temperature variants (cold/temperate/warm/hot).
  - As of 1.0.582, natural random-tick spread/collapse is disabled to reduce Wayfall server tick lag; distribution is driven by worldgen and bonemeal.
  - Drops Fault Dust when broken unless Silk Touch (like vanilla grass).
- **Assets**
  - Texture (top): `assets/kruemblegard/textures/block/runegrowth_top.png`
  - Texture (side): `assets/kruemblegard/textures/block/runegrowth_side.png`

#### Voidfelt
- **IDs**
  - Block: `kruemblegard:voidfelt`
  - Item: `kruemblegard:voidfelt` (BlockItem)
- **How it behaves**
  - Generated-only surface.
  - As of 1.0.582, natural random-tick spread/collapse is disabled to reduce Wayfall server tick lag.
  - Tagged as a `minecraft:nylium` equivalent, so vanilla/nether planting rules can treat it as a valid substrate.
- **Assets**
  - Texture (top): `assets/kruemblegard/textures/block/voidfelt_top.png`
  - Texture (side): `assets/kruemblegard/textures/block/voidfelt_side.png`

### Wayfall ground layers (blocks)
These are surface/ground blocks that are part of Wayfall’s material language, even if they’re not “plants”.

#### Fault Dust
- **IDs**
  - Block: `kruemblegard:fault_dust`
  - Item: `kruemblegard:fault_dust` (BlockItem)
- **Assets**
  - Texture: `assets/kruemblegard/textures/block/fault_dust.png`

### Wispstalk
- **IDs**
  - Block: `kruemblegard:wispstalk`
  - Item: `kruemblegard:wispstalk` (BlockItem)
- **Where it appears**
  - Worldgen: Wayfall vegetation patches (biome modifier adds it to Wayfall biomes).
  - Placement rules: only places where it **would survive**.
- **How it behaves**
  - Has an `age` property (0–3).
  - Randomly grows toward age 3 when either:
    - near “enchanted” blocks (bookshelf/lectern/enchanting table/chiseled bookshelf), OR
    - it’s night with strong moon brightness.
  - Emits subtle End Rod particles in `animateTick`.
- **Player interaction**
  - Right-click harvest when `age` is 2–3:
    - drops `kruemblegard:wispshoot`
    - resets the plant back to `age=1`
  - Prickly: walking through a grown Wispstalk slows you and can deal a small amount of damage (vanilla sweet-berry-bush style).
- **Drops**
  - Uses loot table: `data/kruemblegard/loot_tables/blocks/wispstalk.json`.
- **Assets**
  - Blockstate: `assets/kruemblegard/blockstates/wispstalk.json`
  - Block models: `assets/kruemblegard/models/block/wispstalk_stage0.json` … `wispstalk_stage3.json`
  - Block textures: `assets/kruemblegard/textures/block/wispstalk_stage0.png` … `wispstalk_stage3.png`

### Gravevine
- **IDs**
  - Block: `kruemblegard:gravevine`
  - Item: `kruemblegard:gravevine` (BlockItem)
  - Related item: `kruemblegard:remnant_seeds`
- **Where it appears**
  - Worldgen: Wayfall vegetation patches.
- **How it behaves**
  - Functions as a **vanilla-vine-equivalent** block: wall/ceiling attach + climbable + vanilla-style blockstate rotations (including underside placement).
  - Renders as **cutout** (like vanilla vines).
  - Random ticks produce occasional ash particles when near:
    - skull blocks, and/or
    - Krümblegård stone structures (Ancient Waystone / Standing Stone).
- **Drops (important)**
  - Uses loot table: `data/kruemblegard/loot_tables/blocks/gravevine.json`:
    - With Silk Touch: drops the block.
    - Without Silk Touch: drops `remnant_seeds`.
- **Assets**
  - Blockstate: `assets/kruemblegard/blockstates/gravevine.json`
  - Block model: `assets/kruemblegard/models/block/gravevine.json`
  - Block texture: `assets/kruemblegard/textures/block/gravevine.png`
  - Item texture: `assets/kruemblegard/textures/item/remnant_seeds.png`

### Echocap
- **IDs**
  - Block: `kruemblegard:echocap`
  - Item: `kruemblegard:echocap` (BlockItem)
- **Where it appears**
  - Worldgen: Wayfall vegetation patches.
- **How it behaves**
  - When a player steps on it (client-side), it emits Note particles.
  - (Bonus behavior exists elsewhere: chat near Echocaps triggers a pulse effect.)
- **Drops**
  - Implemented in code: drops the block item.
- **Assets**
  - Block model: `assets/kruemblegard/models/block/echocap.json`
  - Texture: `assets/kruemblegard/textures/block/echocap.png`

### Runebloom
- **IDs**
  - Block: `kruemblegard:runebloom`
  - Item: `kruemblegard:runebloom` (BlockItem)
  - Related item: `kruemblegard:rune_petals`
- **Where it appears**
  - Worldgen: Wayfall vegetation patches.
- **How it behaves**
  - Has a `variant` property (0–5).
  - Randomly re-rolls its variant based on:
    - biome key hash
    - nearby block signals (attuned stone / end stone / standing stone adjacency)
- **Drops (important)**
  - Implemented in code:
    - With Silk Touch: drops the block.
    - Without Silk Touch: drops 1–2 `rune_petals`.
- **Assets**
  - Block model: `assets/kruemblegard/models/block/runebloom.json`
  - Block texture: `assets/kruemblegard/textures/block/runebloom.png` (16-frame sprite sheet via `.png.mcmeta`)
  - Item texture: `assets/kruemblegard/textures/item/rune_petals.png`

### Soulberry Shrub
- **IDs**
  - Block: `kruemblegard:soulberry_shrub`
  - Item: `kruemblegard:soulberry_shrub` (BlockItem)
  - Berry item: `kruemblegard:soulberries`
- **Where it appears**
  - Worldgen: Wayfall vegetation patches.
- **How it behaves**
  - Has `age` (0–3). Grows over time.
  - Right-click harvest when mature:
    - If `age >= 2`, drops berries and resets to age 1.
- **Corruption**
  - May corrupt into Ghoulberry Shrub if hostile mobs are nearby.
- **Drops**
  - Harvesting drops berry item (`soulberries`).
  - Block loot table exists for berries/block fallback: `data/kruemblegard/loot_tables/blocks/soulberry_shrub.json`.
- **Assets**
  - Block models: `assets/kruemblegard/models/block/soulberry_shrub_stage0..3.json`
  - Block textures: `assets/kruemblegard/textures/block/soulberry_shrub_stage0..3.png`
  - Item texture: `assets/kruemblegard/textures/item/soulberries.png`

### Ghoulberry Shrub
- **IDs**
  - Block: `kruemblegard:ghoulberry_shrub`
  - Item: `kruemblegard:ghoulberry_shrub` (BlockItem)
  - Berry item: `kruemblegard:ghoulberries`
- **Where it appears**
  - Typically created via Soulberry corruption; can also generate via shrub patches.
- **How it behaves**
  - Same growth + right-click harvest structure as Soulberry.
- **Drops**
  - Harvesting drops berry item (`ghoulberries`).
  - Block loot table exists for berries/block fallback: `data/kruemblegard/loot_tables/blocks/ghoulberry_shrub.json`.
- **Assets**
  - Block models: `assets/kruemblegard/models/block/ghoulberry_shrub_stage0..3.json`
  - Block textures: `assets/kruemblegard/textures/block/ghoulberry_shrub_stage0..3.png`
  - Item texture: `assets/kruemblegard/textures/item/ghoulberries.png`

### Wayfall staple flora (reactive plants)
Some Wayfall staple plants are implemented using `WayfallReactivePlantBlock`.

- **Activation (visual-only):**
  - can activate near players (per-plant radius), and/or
  - can activate when near blocks tagged `kruemblegard:waystone_energy_sources`.
- **Particles:** each plant has an idle particle and an “active” particle; active particles are emitted more frequently.
  - The mod provides a custom particle type: `kruemblegard:arcane_spark`.
  - Currently, Runeblossom, Twilight Bulb, and Whispervine use `arcane_spark` as their active particle.

## Additional Wayfall plants (implemented blocks; mechanics WIP)
These plants exist as blocks/items and are included in Wayfall flora patches.

Current limitations:
- Placement is mostly broad (tag-based across Wayfall); some plants now have per-biome distribution via biome modifiers.
- The "Mechanic" notes below are design targets; most are decorative right now.

### Medium plants / shrubs

#### Pathreed
- Appearance: Tall reeds that lean in one direction.
- Use: Craft navigation tools.
- Mechanic: Always points toward the nearest waystone or exit.
- Placeholder texture: `assets/kruemblegard/textures/block/pathreed.png`

#### Faultgrass
- Appearance: Jagged, stone-bladed grass.
- Use: Craft bleeding traps or spike blocks.
- Mechanic: Slowly spreads along cracks.
- Placeholder texture: `assets/kruemblegard/textures/block/faultgrass.png`

#### Driftbloom
- Appearance: Floating flowers unattached to ground.
- Use: Levitation-related potions.
- Mechanic: Breaks free and floats away if left too long.
- Placeholder texture: `assets/kruemblegard/textures/block/driftbloom.png`

#### Cairn Moss
- Appearance: Thick gray moss with glowing veins.
- Use: Dampens sound, used in stealth gear.
- Mechanic: Grows on player-built structures over time.
- Placeholder texture: `assets/kruemblegard/textures/block/cairn_moss.png`

#### Waylily
- Appearance: Flat, rune-marked lily pads over void gaps.
- Use: Temporary platforms.
- Mechanic: Disappear after being stepped on too long.
- Behavior (current): Lily-pad-like water plant; places only on water surfaces and includes a 1-block-deep, waterlogged tail.
- Worldgen (current): Generates near water across all Wayfall biomes (only where water exists), with per-biome rarity tiers.
- Placeholder textures:
  - `assets/kruemblegard/textures/block/waylily.png` (upper pad)
  - `assets/kruemblegard/textures/block/waylily_tail.png` (lower tail)

### Fungi & weird growths

#### Griefcap
- Appearance: Drooping mushroom with tear-like spores.
- Use: Brewing debuff-cleansing potions.
- Mechanic: Grows near player death locations.
- Placeholder texture: `assets/kruemblegard/textures/block/griefcap.png`

#### Static Fungus
- Appearance: Crackling, jittering mushroom.
- Use: Craft anti-teleport anchors.
- Mechanic: Disrupts endermen and teleporting mobs.
- Placeholder texture: `assets/kruemblegard/textures/block/static_fungus.png`

#### Wayrot Fungus
- Appearance: Eats into stone like mold.
- Use: Craft decay-based blocks.
- Mechanic: Slowly converts nearby stone types.
- Placeholder texture: `assets/kruemblegard/textures/block/wayrot_fungus.png`

#### Echo Puff
- Appearance: Round fungus that pops loudly.
- Use: Sound traps.
- Mechanic: Activates when mobs approach.
- Placeholder texture: `assets/kruemblegard/textures/block/echo_puff.png`

### Small / utility plants

#### Ruin Thistle
- Appearance: Dry, sharp thistle growing from rubble.
- Use: Craft armor padding.
- Mechanic: Deals damage when walked through.
- Placeholder texture: `assets/kruemblegard/textures/block/ruin_thistle.png`

#### Wayseed Cluster
- Appearance: Small glowing seed nodules.
- Use: Replant waystone-linked flora.
- Mechanic: Seeds inherit biome traits.
- Placeholder texture: `assets/kruemblegard/textures/block/wayseed_cluster.png`

#### Void Lichen
- Appearance: Black-violet growth that absorbs light.
- Use: Craft darkness blocks.
- Mechanic: Reduces light level around it.
- Placeholder texture: `assets/kruemblegard/textures/block/void_lichen.png`

#### Transit Fern
- Appearance: Fractal leaf pattern.
- Use: Potion duration extenders.
- Mechanic: Grows only along old paths.
- Placeholder texture: `assets/kruemblegard/textures/block/transit_fern.png`

### Creeping / dangerous flora

#### Misstep Vine
- Appearance: Thin, nearly invisible vine.
- Use: Trap crafting.
- Mechanic: Causes knockback toward void gaps.
- Placeholder texture: `assets/kruemblegard/textures/block/misstep_vine.png`

#### Waybind Creeper
- Appearance: Low crawling plant with rune nodes.
- Use: Craft binding talismans.
- Mechanic: Temporarily locks player movement.
- Placeholder texture: `assets/kruemblegard/textures/block/waybind_creeper.png`

### Specialized biome plants

#### Milestone Grass
- Appearance: Grass with carved numeric markings.
- Use: Craft mapping tools.
- Placeholder texture: `assets/kruemblegard/textures/block/milestone_grass.png`

#### Runedrift Reed
- Appearance: Floating reed bundles.
- Use: Potion amplifiers.
- Placeholder texture: `assets/kruemblegard/textures/block/runedrift_reed.png`

#### Wayscar Ivy
- Appearance: Grows along vertical stone scars.
- Use: Climbing gear.
- Placeholder texture: `assets/kruemblegard/textures/block/wayscar_ivy.png`

#### Ashpetal
- Appearance: Gray flower shedding ash-like particles.
- Use: Smoke bombs.
- Placeholder texture: `assets/kruemblegard/textures/block/ashpetal.png`

#### Transit Bloom
- Appearance: Blurred, motion-streaked petals.
- Use: Speed potions.
- Placeholder texture: `assets/kruemblegard/textures/block/transit_bloom.png`

#### Cairnroot
- Appearance: Thick root clusters pushing up stones.
- Use: Summoning anchors.
- Placeholder texture: `assets/kruemblegard/textures/block/cairnroot.png`

### Fungi & corrupted growth

#### Waypoint Mold
- Appearance: Circular fungal rings.
- Use: Teleport recall consumables.
- Placeholder texture: `assets/kruemblegard/textures/block/waypoint_mold.png`

#### Black Echo Fungus
- Appearance: Absorbs sound visually.
- Use: Silence potions.
- Placeholder texture: `assets/kruemblegard/textures/block/black_echo_fungus.png`

#### Wayburn Fungus
- Appearance: Glowing cracks, smoldering edges.
- Use: Fire-resistance crafting.
- Where it appears: Basin of Scars (worldgen patch placement).
- Placeholder texture: `assets/kruemblegard/textures/block/wayburn_fungus.png`

#### Memory Rot
- Appearance: Pulsing, fleshy stone growth.
- Use: Lore manipulation items.
- Placeholder texture: `assets/kruemblegard/textures/block/memory_rot.png`

### Dangerous / interactive flora

#### Falsepath Thorns
- Appearance: Form arrows pointing wrong directions.
- Use: Trap crafting.
- Placeholder texture: `assets/kruemblegard/textures/block/falsepath_thorns.png`

#### Sliproot
- Appearance: Wet-looking stone roots.
- Use: Craft slide mechanics.
- Placeholder texture: `assets/kruemblegard/textures/block/sliproot.png`

#### Waygrasp Vine
- Appearance: Slowly reaching tendrils.
- Use: Binding traps.
- Placeholder texture: `assets/kruemblegard/textures/block/waygrasp_vine.png`

#### Voidcap Briar
- Appearance: Spiky void-black mushroom.
- Use: Void damage potions.
- Placeholder texture: `assets/kruemblegard/textures/block/voidcap_briar.png`

### Small flavor plants

#### Dustpetal
- Appearance: Crumbles when touched.
- Use: Decorative + crafting filler.
- Placeholder texture: `assets/kruemblegard/textures/block/dustpetal.png`

#### Rune Sprouts
- Appearance: Tiny glowing runes emerging from soil.
- Use: Early-game enchanting.
- Placeholder texture: `assets/kruemblegard/textures/block/rune_sprouts.png`

#### Waythread
- Appearance: Hair-thin glowing fibers.
- Use: Tether items.
- Placeholder texture: `assets/kruemblegard/textures/block/waythread.png`

#### Gravemint
- Appearance: Pale minty leaves.
- Use: Debuff resistance food.
- Placeholder texture: `assets/kruemblegard/textures/block/gravemint.png`

#### Fallseed Pods
- Appearance: Seed pods that drop endlessly.
- Use: Gravity-based crafting.
- Placeholder texture: `assets/kruemblegard/textures/block/fallseed_pods.png`

#### Reverse Portal Spores (custom particle plant)
- Appearance: A faint, spore-like growth that sheds reverse-portal motes.
- Use: Biome flavor + particle source for Underway Falls.
- Mechanic: Intended to emit reverse-portal particles when nearby players move through the area.
- Placeholder texture: `assets/kruemblegard/textures/block/reverse_portal_spores.png`
