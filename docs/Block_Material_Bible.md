# Krümblegård Block Material Bible

This document is the **single source of truth** for how **non-wood, non-plant blocks** should behave and read as *materials* in Krümblegård.

Scope:
- Geology / stone palettes
- Soils, sands, dusts, rubble, and similar “ground” blocks
- Structural stone variants (stairs/slabs/walls)

Out of scope (see instead):
- Wood: [docs/Wood_Material_Bible.md](Wood_Material_Bible.md)
- Plants/flora: [docs/Plant_Material_Bible.md](Plant_Material_Bible.md)

---

## Table of Contents

- [Global Rules](#global-rules)
- [Block Property Recipes](#block-property-recipes)
- [Asset & Data Checklist](#asset--data-checklist)
- [Registry Inventory (Generated)](#registry-inventory-generated)
- [Material Families](#material-families)
  - [Wayfall Geology](#wayfall-geology)
  - [Scarstone Family](#scarstone-family)
  - [Stoneveil Rubble](#stoneveil-rubble)

---

## Global Rules

- Prefer **data-driven + consistent** materials: if two blocks are “the same kind of stone,” they should share the same **strength/resistance/sound/tool** rules unless there is a gameplay reason.
- If a block has **stairs/slab/wall** variants, all variants must match the parent block’s core “material contract” (map color, tool requirements, strength tier, sound tier).
- Avoid inventing new one-off behaviors. If you need a special mechanic (e.g., tool-gated breaking), document it here and keep it localized to a dedicated block class.

---

## Block Property Recipes

These recipes mirror the patterns used in `ModBlocks`.

### Stone (Wayfall palette / general stone)
Use for: `fractured_wayrock`, `crushstone`, `stoneveil_rubble`, etc.

- `mapColor(MapColor.STONE)`
- `sound(SoundType.STONE)`
- `strength(hardness, resistance)`
- If it’s intended to behave like a “real” stone that requires a pickaxe tier: `requiresCorrectToolForDrops()`

### Deepslate-like stone (Scarstone tier)
Use for: `scarstone` family.

- `mapColor(MapColor.DEEPSLATE)`
- `sound(SoundType.DEEPSLATE)`
- `requiresCorrectToolForDrops()`
- Higher strength than basic stone.

### Falling soil/sand
Use for: `ashfall_loam`.

- Block type: `FallingBlock`
- `mapColor(MapColor.DIRT)` or `MapColor.SAND`
- Low strength, sand/soft sound

### Dust / ground cover substrate
Use for: `fault_dust`.

- Low strength, ground/soil sound
- Typically does **not** require tool tiers

---

## Asset & Data Checklist

For a new block material (and its variants), keep these in sync:

- Registration
  - Block in `src/main/java/com/kruemblegard/init/ModBlocks.java`
  - Item in `src/main/java/com/kruemblegard/registry/ModItems.java`
- Assets
  - Blockstate JSON: `src/main/resources/assets/kruemblegard/blockstates/<id>.json`
  - Block model JSON: `src/main/resources/assets/kruemblegard/models/block/<id>.json`
  - Item model JSON: `src/main/resources/assets/kruemblegard/models/item/<id>.json`
  - Textures: `src/main/resources/assets/kruemblegard/textures/block/<id>.png` (+ `_top/_side` as needed)
- Loot
  - Loot table: `src/main/resources/data/kruemblegard/loot_tables/blocks/<id>.json`
- Tags
  - Block tags (examples): `minecraft:mineable/pickaxe`, `minecraft:walls`, etc.
- Tooling
  - Run / update audit scripts in `tools/` if you add or rename textures/models.

---

## Registry Inventory (Generated)

This section is **auto-generated** from:
- `src/main/java/com/kruemblegard/init/ModBlocks.java`
- `src/main/java/com/kruemblegard/registry/ModItems.java`

Update workflow:
- After adding/removing/renaming blocks/items, run `tools/generate_material_bibles.ps1`.
- Commit the resulting doc changes in the same PR/commit as the content change.

<!-- AUTO-GENERATED:BLOCKS:START -->

### Blocks (All Registered)

#### ancient_waystone
- **ID**: `kruemblegard:ancient_waystone`
- **Class**: `AncientWaystoneBlock`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `3.5F, 30.0F`

#### ashbloom_leaves
- **ID**: `kruemblegard:ashbloom_leaves`
- **Class**: `AshbloomLeavesBlock`
- **Map color**: `MapColor.PLANT`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `0.2F`

#### ashfall_loam
- **ID**: `kruemblegard:ashfall_loam`
- **Class**: `FallingBlock`
- **Map color**: `MapColor.DIRT`
- **Sound**: `SoundType.SAND`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `0.6F, 0.6F`

#### ashfall_stone
- **ID**: `kruemblegard:ashfall_stone`
- **Class**: `Block`
- **Map color**: `MapColor.SAND`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `1.2F, 6.0F`

#### ashfall_stone_slab
- **ID**: `kruemblegard:ashfall_stone_slab`
- **Class**: `SlabBlock`
- **Map color**: `MapColor.SAND`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `1.2F, 6.0F`

#### ashfall_stone_stairs
- **ID**: `kruemblegard:ashfall_stone_stairs`
- **Class**: `StairBlock`
- **Map color**: `MapColor.SAND`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `1.2F, 6.0F`

#### ashfall_stone_wall
- **ID**: `kruemblegard:ashfall_stone_wall`
- **Class**: `WallBlock`
- **Map color**: `MapColor.SAND`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `1.2F, 6.0F`

#### ashmoss
- **ID**: `kruemblegard:ashmoss`
- **Class**: `AshmossBlock`
- **Map color**: `MapColor.COLOR_GRAY`
- **Sound**: `SoundType.MOSS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `0.6F, 0.6F`

#### ashmoss_carpet
- **ID**: `kruemblegard:ashmoss_carpet`
- **Class**: `CarpetBlock`
- **Map color**: `MapColor.COLOR_GRAY`
- **Sound**: `SoundType.MOSS_CARPET`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `0.1F`

#### ashpetal
- **ID**: `kruemblegard:ashpetal`
- **Class**: `WayfallPlantBlock`
- **Map color**: `MapColor.COLOR_LIGHT_GRAY`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### ashveil
- **ID**: `kruemblegard:ashveil`
- **Class**: `AshveilBlock`
- **Map color**: `MapColor.COLOR_GRAY`
- **Sound**: `SoundType.MOSS_CARPET`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### attuned_stone
- **ID**: `kruemblegard:attuned_stone`
- **Class**: `Block`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `3.0F, 30.0F`

#### attuned_stone_slab
- **ID**: `kruemblegard:attuned_stone_slab`
- **Class**: `SlabBlock`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `3.0F, 30.0F`

#### attuned_stone_stairs
- **ID**: `kruemblegard:attuned_stone_stairs`
- **Class**: `StairBlock`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `3.0F, 30.0F`

#### attuned_stone_wall
- **ID**: `kruemblegard:attuned_stone_wall`
- **Class**: `WallBlock`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `3.0F, 30.0F`

#### black_echo_fungus
- **ID**: `kruemblegard:black_echo_fungus`
- **Class**: `BonemealableWayfallFungusBlock`
- **Map color**: `MapColor.COLOR_BLACK`
- **Sound**: `SoundType.FUNGUS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### brown_mushroom_block_slab
- **ID**: `kruemblegard:brown_mushroom_block_slab`
- **Class**: `SlabBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### cairn_moss
- **ID**: `kruemblegard:cairn_moss`
- **Class**: `WayfallPlantBlock`
- **Map color**: `MapColor.COLOR_GRAY`
- **Sound**: `SoundType.MOSS_CARPET`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### cairnroot
- **ID**: `kruemblegard:cairnroot`
- **Class**: `WayfallPlantBlock`
- **Map color**: `MapColor.COLOR_GRAY`
- **Sound**: `SoundType.ROOTS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### chiseled_scarstone
- **ID**: `kruemblegard:chiseled_scarstone`
- **Class**: `Block`
- **Map color**: `MapColor.DEEPSLATE`
- **Sound**: `SoundType.DEEPSLATE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `4.5F, 60.0F`

#### cracked_scarstone
- **ID**: `kruemblegard:cracked_scarstone`
- **Class**: `Block`
- **Map color**: `MapColor.DEEPSLATE`
- **Sound**: `SoundType.DEEPSLATE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `3.0F, 36.0F`

#### cracked_scarstone_slab
- **ID**: `kruemblegard:cracked_scarstone_slab`
- **Class**: `SlabBlock`
- **Map color**: `MapColor.DEEPSLATE`
- **Sound**: `SoundType.DEEPSLATE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `3.0F, 36.0F`

#### cracked_scarstone_stairs
- **ID**: `kruemblegard:cracked_scarstone_stairs`
- **Class**: `StairBlock`
- **Map color**: `MapColor.DEEPSLATE`
- **Sound**: `SoundType.DEEPSLATE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `3.0F, 36.0F`

#### cracked_scarstone_wall
- **ID**: `kruemblegard:cracked_scarstone_wall`
- **Class**: `WallBlock`
- **Map color**: `MapColor.DEEPSLATE`
- **Sound**: `SoundType.DEEPSLATE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `3.0F, 36.0F`

#### crushstone
- **ID**: `kruemblegard:crushstone`
- **Class**: `Block`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `1.2F, 6.0F`

#### crushstone_slab
- **ID**: `kruemblegard:crushstone_slab`
- **Class**: `SlabBlock`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `1.2F, 6.0F`

#### crushstone_stairs
- **ID**: `kruemblegard:crushstone_stairs`
- **Class**: `StairBlock`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `1.2F, 6.0F`

#### crushstone_wall
- **ID**: `kruemblegard:crushstone_wall`
- **Class**: `WallBlock`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `1.2F, 6.0F`

#### driftbloom
- **ID**: `kruemblegard:driftbloom`
- **Class**: `WayfallPlantBlock`
- **Map color**: `MapColor.COLOR_PINK`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### driftwood_leaves
- **ID**: `kruemblegard:driftwood_leaves`
- **Class**: `DriftwoodLeavesBlock`
- **Map color**: `MapColor.PLANT`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `0.2F`

#### dustpetal
- **ID**: `kruemblegard:dustpetal`
- **Class**: `WayfallPlantBlock`
- **Map color**: `MapColor.COLOR_LIGHT_GRAY`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### echo_puff
- **ID**: `kruemblegard:echo_puff`
- **Class**: `BonemealableWayfallFungusBlock`
- **Map color**: `MapColor.COLOR_PURPLE`
- **Sound**: `SoundType.FUNGUS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### echocap
- **ID**: `kruemblegard:echocap`
- **Class**: `EchocapBlock`
- **Map color**: `MapColor.COLOR_PURPLE`
- **Sound**: `SoundType.FUNGUS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### emberwarmed_runegrowth
- **ID**: `kruemblegard:emberwarmed_runegrowth`
- **Class**: `RunegrowthVariantBlock`
- **Map color**: `MapColor.FIRE`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `0.6F, 0.6F`

#### fallseed_pods
- **ID**: `kruemblegard:fallseed_pods`
- **Class**: `WayfallPlantBlock`
- **Map color**: `MapColor.COLOR_BROWN`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### falsepath_thorns
- **ID**: `kruemblegard:falsepath_thorns`
- **Class**: `WayfallPlantBlock`
- **Map color**: `MapColor.COLOR_BROWN`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### fault_dust
- **ID**: `kruemblegard:fault_dust`
- **Class**: `Block`
- **Map color**: `MapColor.SAND`
- **Sound**: `SoundType.ROOTED_DIRT`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `0.5F, 0.5F`

#### faultgrass
- **ID**: `kruemblegard:faultgrass`
- **Class**: `WayfallPlantBlock`
- **Map color**: `MapColor.PLANT`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### flowering_ashbloom_leaves
- **ID**: `kruemblegard:flowering_ashbloom_leaves`
- **Class**: `AshbloomLeavesBlock`
- **Map color**: `MapColor.PLANT`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `0.2F`

#### fractured_wayrock
- **ID**: `kruemblegard:fractured_wayrock`
- **Class**: `Block`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `2.0F, 18.0F`

#### fractured_wayrock_slab
- **ID**: `kruemblegard:fractured_wayrock_slab`
- **Class**: `SlabBlock`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `2.0F, 18.0F`

#### fractured_wayrock_stairs
- **ID**: `kruemblegard:fractured_wayrock_stairs`
- **Class**: `StairBlock`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `2.0F, 18.0F`

#### fractured_wayrock_wall
- **ID**: `kruemblegard:fractured_wayrock_wall`
- **Class**: `WallBlock`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `2.0F, 18.0F`

#### frostbound_runegrowth
- **ID**: `kruemblegard:frostbound_runegrowth`
- **Class**: `RunegrowthVariantBlock`
- **Map color**: `MapColor.ICE`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `0.6F, 0.6F`

#### ghoulberry_shrub
- **ID**: `kruemblegard:ghoulberry_shrub`
- **Class**: `BerryBushBlock`
- **Map color**: `MapColor.COLOR_GREEN`
- **Sound**: `SoundType.SWEET_BERRY_BUSH`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### giant_black_echo_fungus_cap
- **ID**: `kruemblegard:giant_black_echo_fungus_cap`
- **Class**: `HugeMushroomBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### giant_black_echo_fungus_cap_slab
- **ID**: `kruemblegard:giant_black_echo_fungus_cap_slab`
- **Class**: `SlabBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### giant_black_echo_fungus_stem
- **ID**: `kruemblegard:giant_black_echo_fungus_stem`
- **Class**: `HugeMushroomBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### giant_echo_puff_cap
- **ID**: `kruemblegard:giant_echo_puff_cap`
- **Class**: `HugeMushroomBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### giant_echo_puff_cap_slab
- **ID**: `kruemblegard:giant_echo_puff_cap_slab`
- **Class**: `SlabBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### giant_echo_puff_stem
- **ID**: `kruemblegard:giant_echo_puff_stem`
- **Class**: `HugeMushroomBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### giant_echocap_cap
- **ID**: `kruemblegard:giant_echocap_cap`
- **Class**: `HugeMushroomBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### giant_echocap_cap_slab
- **ID**: `kruemblegard:giant_echocap_cap_slab`
- **Class**: `SlabBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### giant_echocap_stem
- **ID**: `kruemblegard:giant_echocap_stem`
- **Class**: `HugeMushroomBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### giant_griefcap_cap
- **ID**: `kruemblegard:giant_griefcap_cap`
- **Class**: `UndersideParticleHugeMushroomBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### giant_griefcap_cap_slab
- **ID**: `kruemblegard:giant_griefcap_cap_slab`
- **Class**: `SlabBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### giant_griefcap_stem
- **ID**: `kruemblegard:giant_griefcap_stem`
- **Class**: `HugeMushroomBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### giant_memory_rot_cap
- **ID**: `kruemblegard:giant_memory_rot_cap`
- **Class**: `HugeMushroomBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### giant_memory_rot_cap_slab
- **ID**: `kruemblegard:giant_memory_rot_cap_slab`
- **Class**: `SlabBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### giant_memory_rot_stem
- **ID**: `kruemblegard:giant_memory_rot_stem`
- **Class**: `HugeMushroomBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### giant_static_fungus_cap
- **ID**: `kruemblegard:giant_static_fungus_cap`
- **Class**: `HugeMushroomBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### giant_static_fungus_cap_slab
- **ID**: `kruemblegard:giant_static_fungus_cap_slab`
- **Class**: `SlabBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### giant_static_fungus_stem
- **ID**: `kruemblegard:giant_static_fungus_stem`
- **Class**: `HugeMushroomBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### giant_voidcap_briar_cap
- **ID**: `kruemblegard:giant_voidcap_briar_cap`
- **Class**: `HugeMushroomBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### giant_voidcap_briar_cap_slab
- **ID**: `kruemblegard:giant_voidcap_briar_cap_slab`
- **Class**: `SlabBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### giant_voidcap_briar_stem
- **ID**: `kruemblegard:giant_voidcap_briar_stem`
- **Class**: `HugeMushroomBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### giant_wayburn_fungus_cap
- **ID**: `kruemblegard:giant_wayburn_fungus_cap`
- **Class**: `UndersideParticleHugeMushroomBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### giant_wayburn_fungus_cap_slab
- **ID**: `kruemblegard:giant_wayburn_fungus_cap_slab`
- **Class**: `SlabBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### giant_wayburn_fungus_stem
- **ID**: `kruemblegard:giant_wayburn_fungus_stem`
- **Class**: `HugeMushroomBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### giant_wayrot_fungus_cap
- **ID**: `kruemblegard:giant_wayrot_fungus_cap`
- **Class**: `UndersideParticleHugeMushroomBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### giant_wayrot_fungus_cap_slab
- **ID**: `kruemblegard:giant_wayrot_fungus_cap_slab`
- **Class**: `SlabBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### giant_wayrot_fungus_stem
- **ID**: `kruemblegard:giant_wayrot_fungus_stem`
- **Class**: `HugeMushroomBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### glimmerpine_leaves
- **ID**: `kruemblegard:glimmerpine_leaves`
- **Class**: `GlimmerpineLeavesBlock`
- **Map color**: `MapColor.PLANT`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `0.2F`

#### glimmerpine_planks
- **ID**: `kruemblegard:glimmerpine_planks`
- **Class**: `Block`
- **Map color**: `MapColor.WOOD`
- **Sound**: `SoundType.WOOD`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `2.0F, 3.0F`

#### gravemint
- **ID**: `kruemblegard:gravemint`
- **Class**: `WayfallPlantBlock`
- **Map color**: `MapColor.COLOR_GREEN`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### gravevine
- **ID**: `kruemblegard:gravevine`
- **Class**: `GravevineBlock`
- **Map color**: `MapColor.COLOR_BLACK`
- **Sound**: `SoundType.VINE`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### griefcap
- **ID**: `kruemblegard:griefcap`
- **Class**: `BonemealableWayfallFungusBlock`
- **Map color**: `MapColor.COLOR_BROWN`
- **Sound**: `SoundType.FUNGUS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### memory_rot
- **ID**: `kruemblegard:memory_rot`
- **Class**: `BonemealableWayfallFungusBlock`
- **Map color**: `MapColor.COLOR_RED`
- **Sound**: `SoundType.SLIME_BLOCK`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### milestone_grass
- **ID**: `kruemblegard:milestone_grass`
- **Class**: `WayfallPlantBlock`
- **Map color**: `MapColor.COLOR_GRAY`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### misstep_vine
- **ID**: `kruemblegard:misstep_vine`
- **Class**: `WayfallPlantBlock`
- **Map color**: `MapColor.COLOR_GREEN`
- **Sound**: `SoundType.VINE`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### moteshrub
- **ID**: `kruemblegard:moteshrub`
- **Class**: `WayfallReactivePlantBlock`
- **Map color**: `MapColor.COLOR_LIGHT_GRAY`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### paleweft_corn
- **ID**: `kruemblegard:paleweft_corn`
- **Class**: `PaleweftCornCropBlock`
- **Map color**: `MapColor.PLANT`
- **Sound**: `SoundType.CROP`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### paleweft_grass
- **ID**: `kruemblegard:paleweft_grass`
- **Class**: `PaleweftGrassBlock`
- **Map color**: `MapColor.PLANT`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### paleweft_tall_grass
- **ID**: `kruemblegard:paleweft_tall_grass`
- **Class**: `DoublePlantBlock`
- **Map color**: `MapColor.PLANT`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### pathreed
- **ID**: `kruemblegard:pathreed`
- **Class**: `WayfallPlantBlock`
- **Map color**: `MapColor.PLANT`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### polished_ashfall_stone
- **ID**: `kruemblegard:polished_ashfall_stone`
- **Class**: `Block`
- **Map color**: `MapColor.SAND`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `1.5F, 6.0F`

#### polished_ashfall_stone_slab
- **ID**: `kruemblegard:polished_ashfall_stone_slab`
- **Class**: `SlabBlock`
- **Map color**: `MapColor.SAND`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `1.5F, 6.0F`

#### polished_ashfall_stone_stairs
- **ID**: `kruemblegard:polished_ashfall_stone_stairs`
- **Class**: `StairBlock`
- **Map color**: `MapColor.SAND`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `1.5F, 6.0F`

#### polished_ashfall_stone_wall
- **ID**: `kruemblegard:polished_ashfall_stone_wall`
- **Class**: `WallBlock`
- **Map color**: `MapColor.SAND`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `1.5F, 6.0F`

#### polished_scarstone
- **ID**: `kruemblegard:polished_scarstone`
- **Class**: `Block`
- **Map color**: `MapColor.DEEPSLATE`
- **Sound**: `SoundType.DEEPSLATE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `4.0F, 50.0F`

#### polished_scarstone_slab
- **ID**: `kruemblegard:polished_scarstone_slab`
- **Class**: `SlabBlock`
- **Map color**: `MapColor.DEEPSLATE`
- **Sound**: `SoundType.DEEPSLATE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `4.0F, 50.0F`

#### polished_scarstone_stairs
- **ID**: `kruemblegard:polished_scarstone_stairs`
- **Class**: `StairBlock`
- **Map color**: `MapColor.DEEPSLATE`
- **Sound**: `SoundType.DEEPSLATE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `4.0F, 50.0F`

#### polished_scarstone_wall
- **ID**: `kruemblegard:polished_scarstone_wall`
- **Class**: `WallBlock`
- **Map color**: `MapColor.DEEPSLATE`
- **Sound**: `SoundType.DEEPSLATE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `4.0F, 50.0F`

#### polished_stoneveil_rubble
- **ID**: `kruemblegard:polished_stoneveil_rubble`
- **Class**: `Block`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `2.6F, 20.0F`

#### polished_stoneveil_rubble_slab
- **ID**: `kruemblegard:polished_stoneveil_rubble_slab`
- **Class**: `SlabBlock`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `2.6F, 20.0F`

#### polished_stoneveil_rubble_stairs
- **ID**: `kruemblegard:polished_stoneveil_rubble_stairs`
- **Class**: `StairBlock`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `2.6F, 20.0F`

#### polished_stoneveil_rubble_wall
- **ID**: `kruemblegard:polished_stoneveil_rubble_wall`
- **Class**: `WallBlock`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `2.6F, 20.0F`

#### pyrokelp
- **ID**: `kruemblegard:pyrokelp`
- **Class**: `PyrokelpHeadBlock`
- **Map color**: `MapColor.COLOR_ORANGE`
- **Sound**: `SoundType.VINE`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### pyrokelp_plant
- **ID**: `kruemblegard:pyrokelp_plant`
- **Class**: `PyrokelpPlantBlock`
- **Map color**: `MapColor.COLOR_ORANGE`
- **Sound**: `SoundType.VINE`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### red_mushroom_block_slab
- **ID**: `kruemblegard:red_mushroom_block_slab`
- **Class**: `SlabBlock`
- **Map color**: TBD
- **Sound**: TBD
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### reverse_portal_spores
- **ID**: `kruemblegard:reverse_portal_spores`
- **Class**: `WayfallPlantBlock`
- **Map color**: `MapColor.COLOR_PURPLE`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### rubble_tilth
- **ID**: `kruemblegard:rubble_tilth`
- **Class**: `RubbleTilthBlock`
- **Map color**: `MapColor.DIRT`
- **Sound**: `SoundType.GRAVEL`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `0.6F, 0.6F`

#### ruin_thistle
- **ID**: `kruemblegard:ruin_thistle`
- **Class**: `WayfallPlantBlock`
- **Map color**: `MapColor.COLOR_BROWN`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### rune_sprouts
- **ID**: `kruemblegard:rune_sprouts`
- **Class**: `WayfallPlantBlock`
- **Map color**: `MapColor.COLOR_LIGHT_BLUE`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### runebloom
- **ID**: `kruemblegard:runebloom`
- **Class**: `RunebloomBlock`
- **Map color**: `MapColor.COLOR_BLUE`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### runeblossom
- **ID**: `kruemblegard:runeblossom`
- **Class**: `WayfallReactivePlantBlock`
- **Map color**: `MapColor.COLOR_BLUE`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### runed_stoneveil_rubble
- **ID**: `kruemblegard:runed_stoneveil_rubble`
- **Class**: `RunedStoneveilRubbleBlock`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `2.8F, 22.0F`

#### runed_stoneveil_rubble_slab
- **ID**: `kruemblegard:runed_stoneveil_rubble_slab`
- **Class**: `SlabBlock`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `2.8F, 22.0F`

#### runed_stoneveil_rubble_stairs
- **ID**: `kruemblegard:runed_stoneveil_rubble_stairs`
- **Class**: `StairBlock`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `2.8F, 22.0F`

#### runed_stoneveil_rubble_wall
- **ID**: `kruemblegard:runed_stoneveil_rubble_wall`
- **Class**: `WallBlock`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `2.8F, 22.0F`

#### runedrift_reed
- **ID**: `kruemblegard:runedrift_reed`
- **Class**: `WayfallPlantBlock`
- **Map color**: `MapColor.COLOR_LIGHT_BLUE`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### runegrowth
- **ID**: `kruemblegard:runegrowth`
- **Class**: `RunegrowthBlock`
- **Map color**: `MapColor.COLOR_BLUE`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `0.6F, 0.6F`

#### runic_debris
- **ID**: `kruemblegard:runic_debris`
- **Class**: `Block`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `5.0F, 60.0F`

#### scarstone
- **ID**: `kruemblegard:scarstone`
- **Class**: `ScarstoneBlock`
- **Map color**: `MapColor.DEEPSLATE`
- **Sound**: `SoundType.DEEPSLATE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `3.5F, 42.0F`

#### scarstone_slab
- **ID**: `kruemblegard:scarstone_slab`
- **Class**: `SlabBlock`
- **Map color**: `MapColor.DEEPSLATE`
- **Sound**: `SoundType.DEEPSLATE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `3.5F, 42.0F`

#### scarstone_stairs
- **ID**: `kruemblegard:scarstone_stairs`
- **Class**: `StairBlock`
- **Map color**: `MapColor.DEEPSLATE`
- **Sound**: `SoundType.DEEPSLATE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `3.5F, 42.0F`

#### scarstone_wall
- **ID**: `kruemblegard:scarstone_wall`
- **Class**: `WallBlock`
- **Map color**: `MapColor.DEEPSLATE`
- **Sound**: `SoundType.DEEPSLATE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `3.5F, 42.0F`

#### sliproot
- **ID**: `kruemblegard:sliproot`
- **Class**: `WayfallPlantBlock`
- **Map color**: `MapColor.COLOR_GRAY`
- **Sound**: `SoundType.ROOTS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### soulberry_shrub
- **ID**: `kruemblegard:soulberry_shrub`
- **Class**: `SoulberryShrubBlock`
- **Map color**: `MapColor.COLOR_GREEN`
- **Sound**: `SoundType.SWEET_BERRY_BUSH`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### standing_stone
- **ID**: `kruemblegard:standing_stone`
- **Class**: `Block`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `4.0F, 60.0F`

#### static_fungus
- **ID**: `kruemblegard:static_fungus`
- **Class**: `BonemealableWayfallFungusBlock`
- **Map color**: `MapColor.COLOR_LIGHT_GRAY`
- **Sound**: `SoundType.FUNGUS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### stoneveil_rubble
- **ID**: `kruemblegard:stoneveil_rubble`
- **Class**: `Block`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `2.2F, 18.0F`

#### stoneveil_rubble_slab
- **ID**: `kruemblegard:stoneveil_rubble_slab`
- **Class**: `SlabBlock`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `2.2F, 18.0F`

#### stoneveil_rubble_stairs
- **ID**: `kruemblegard:stoneveil_rubble_stairs`
- **Class**: `StairBlock`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `2.2F, 18.0F`

#### stoneveil_rubble_wall
- **ID**: `kruemblegard:stoneveil_rubble_wall`
- **Class**: `WallBlock`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `2.2F, 18.0F`

#### transit_bloom
- **ID**: `kruemblegard:transit_bloom`
- **Class**: `WayfallPlantBlock`
- **Map color**: `MapColor.COLOR_PINK`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### transit_fern
- **ID**: `kruemblegard:transit_fern`
- **Class**: `WayfallPlantBlock`
- **Map color**: `MapColor.PLANT`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### twilight_bulb
- **ID**: `kruemblegard:twilight_bulb`
- **Class**: `WayfallReactivePlantBlock`
- **Map color**: `MapColor.COLOR_BLACK`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### verdant_runegrowth
- **ID**: `kruemblegard:verdant_runegrowth`
- **Class**: `RunegrowthVariantBlock`
- **Map color**: `MapColor.COLOR_GREEN`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `0.6F, 0.6F`

#### void_lichen
- **ID**: `kruemblegard:void_lichen`
- **Class**: `WayfallPlantBlock`
- **Map color**: `MapColor.COLOR_PURPLE`
- **Sound**: `SoundType.MOSS_CARPET`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### voidcap_briar
- **ID**: `kruemblegard:voidcap_briar`
- **Class**: `BonemealableWayfallFungusBlock`
- **Map color**: `MapColor.COLOR_BLACK`
- **Sound**: `SoundType.FUNGUS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### voidfelt
- **ID**: `kruemblegard:voidfelt`
- **Class**: `VoidfeltBlock`
- **Map color**: `MapColor.COLOR_BLACK`
- **Sound**: `SoundType.ROOTED_DIRT`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `0.6F, 0.6F`

#### voidfern
- **ID**: `kruemblegard:voidfern`
- **Class**: `WayfallReactivePlantBlock`
- **Map color**: `MapColor.COLOR_PURPLE`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### waybind_creeper
- **ID**: `kruemblegard:waybind_creeper`
- **Class**: `WayfallPlantBlock`
- **Map color**: `MapColor.COLOR_GREEN`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### wayburn_fungus
- **ID**: `kruemblegard:wayburn_fungus`
- **Class**: `BonemealableWayfallFungusBlock`
- **Map color**: `MapColor.COLOR_ORANGE`
- **Sound**: `SoundType.FUNGUS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### wayfall_copper_ore
- **ID**: `kruemblegard:wayfall_copper_ore`
- **Class**: `Block`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `3.0F, 30.0F`

#### wayfall_diamond_ore
- **ID**: `kruemblegard:wayfall_diamond_ore`
- **Class**: `Block`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `4.0F, 30.0F`

#### wayfall_iron_ore
- **ID**: `kruemblegard:wayfall_iron_ore`
- **Class**: `Block`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `true`
- **Strength tier**: `3.0F, 30.0F`

#### wayfall_portal
- **ID**: `kruemblegard:wayfall_portal`
- **Class**: `WayfallPortalBlock`
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: `2.0F, 18.0F`

#### waygrasp_vine
- **ID**: `kruemblegard:waygrasp_vine`
- **Class**: `WayfallPlantBlock`
- **Map color**: `MapColor.COLOR_GREEN`
- **Sound**: `SoundType.VINE`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### waylily
- **ID**: `kruemblegard:waylily`
- **Class**: `WaylilyBlock`
- **Map color**: `MapColor.COLOR_BLUE`
- **Sound**: `SoundType.LILY_PAD`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### waypoint_mold
- **ID**: `kruemblegard:waypoint_mold`
- **Class**: `WayfallPlantBlock`
- **Map color**: `MapColor.COLOR_BROWN`
- **Sound**: `SoundType.FUNGUS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### wayrot_fungus
- **ID**: `kruemblegard:wayrot_fungus`
- **Class**: `BonemealableWayfallFungusBlock`
- **Map color**: `MapColor.COLOR_GREEN`
- **Sound**: `SoundType.FUNGUS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### wayscar_ivy
- **ID**: `kruemblegard:wayscar_ivy`
- **Class**: `WayfallPlantBlock`
- **Map color**: `MapColor.COLOR_GREEN`
- **Sound**: `SoundType.VINE`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### wayseed_cluster
- **ID**: `kruemblegard:wayseed_cluster`
- **Class**: `WayfallPlantBlock`
- **Map color**: `MapColor.COLOR_YELLOW`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### waythread
- **ID**: `kruemblegard:waythread`
- **Class**: `WayfallPlantBlock`
- **Map color**: `MapColor.COLOR_LIGHT_BLUE`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### whispervine
- **ID**: `kruemblegard:whispervine`
- **Class**: `WayfallReactivePlantBlock`
- **Map color**: `MapColor.COLOR_BLACK`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

#### wispstalk
- **ID**: `kruemblegard:wispstalk`
- **Class**: `WispstalkBlock`
- **Map color**: `MapColor.PLANT`
- **Sound**: `SoundType.GRASS`
- **Tool rule**: requiresCorrectToolForDrops = `false`
- **Strength tier**: TBD

<!-- AUTO-GENERATED:BLOCKS:END -->

---

## Material Families

### Wayfall Geology

#### Fractured Wayrock
- **IDs**: `kruemblegard:fractured_wayrock` (+ stairs/slab/wall)
- **Vanilla analog**: `minecraft:stone` (general-purpose stone building block)
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: base block is non-colliding portal material elsewhere, but the palette variants use `requiresCorrectToolForDrops()`
- **Drop/processing**: like vanilla stone — requires Silk Touch to drop itself; otherwise drops `kruemblegard:crushstone`. Smelting/blasting `kruemblegard:crushstone` yields `kruemblegard:fractured_wayrock`.
- **Strength tier**: `2.0F / 18.0F`

#### Crushstone
- **IDs**: `kruemblegard:crushstone` (+ stairs/slab/wall)
- **Vanilla analog**: `minecraft:cobblestone` / `minecraft:stone` (soft-ish stone)
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: does not currently require correct tool for drops
- **Drop/processing**: used as the “cobble” output for Fractured Wayrock; can be smelted/blasted back into Fractured Wayrock.
- **Strength tier**: `1.2F / 6.0F`

#### Ashfall Loam
- **IDs**: `kruemblegard:ashfall_loam`
- **Vanilla analog**: `minecraft:sand` (falling, soft ground)
- **Type**: `FallingBlock`
- **Map color**: `MapColor.DIRT`
- **Sound**: `SoundType.SAND`
- **Strength tier**: `0.6F / 0.6F`

#### Fault Dust
- **IDs**: `kruemblegard:fault_dust`
- **Vanilla analog**: `minecraft:rooted_dirt` (dry soil/ground substrate)
- **Map color**: `MapColor.SAND`
- **Sound**: `SoundType.ROOTED_DIRT`
- **Strength tier**: `0.5F / 0.5F`

#### Rubble Tilth
- **IDs**: `kruemblegard:rubble_tilth`
- **Vanilla analog**: `minecraft:farmland` (farmland-like soil)
- **Type**: `FarmBlock` (Wayfall farmland analogue)
- **Visual moisture**: uses a dry top texture at low moisture and a moist top texture at high moisture (vanilla farmland-style feedback).
- **Dry-out behavior**: when it dries out (or is trampled / can’t survive), it reverts into `kruemblegard:fault_dust` (not vanilla dirt).

#### Voidfelt
- **IDs**: `kruemblegard:voidfelt`
- **Vanilla analog**: `minecraft:mycelium` (spreading, biome-flavored soil)
- **Behavior**:
  - As of 1.0.582, natural random-tick spread/collapse is disabled to reduce Wayfall server tick lag.
  - Tagged as a `minecraft:nylium` equivalent so nether planting/generation rules can treat it as a valid substrate.

### Scarstone Family

Scarstone is a deepslate-tier stone with special tool gating.

#### Scarstone
- **IDs**: `kruemblegard:scarstone` (+ stairs/slab/wall)
- **Vanilla analog**: `minecraft:deepslate` (but with custom “sub-iron tool penalty” behavior)
- **Map color**: `MapColor.DEEPSLATE`
- **Sound**: `SoundType.DEEPSLATE`
- **Tool rule**: `requiresCorrectToolForDrops()` and the `ScarstoneBlock` class further penalizes sub-iron tools.
- **Strength tier**: `3.5F / 42.0F`
- **Special mechanic** (must stay true):
  - Below iron-tier tools mine very slowly, take extra durability damage, and yield `cracked_scarstone` as drops.

#### Cracked Scarstone
- **IDs**: `kruemblegard:cracked_scarstone` (+ stairs/slab/wall)
- **Vanilla analog**: `minecraft:deepslate` / `minecraft:cobbled_deepslate` (damaged variant)
- **Map color**: `MapColor.DEEPSLATE`
- **Sound**: `SoundType.DEEPSLATE`
- **Tool rule**: `requiresCorrectToolForDrops()`
- **Strength tier**: `3.0F / 36.0F`

#### Polished Scarstone
- **IDs**: `kruemblegard:polished_scarstone` (+ stairs/slab/wall)
- **Vanilla analog**: `minecraft:polished_deepslate`
- **Strength tier**: `4.0F / 50.0F`

#### Chiseled Scarstone
- **IDs**: `kruemblegard:chiseled_scarstone`
- **Vanilla analog**: `minecraft:chiseled_deepslate`
- **Strength tier**: `4.5F / 60.0F`

### Stoneveil Rubble

Rubble is a stone structural palette for ruins.

#### Stoneveil Rubble
- **IDs**: `kruemblegard:stoneveil_rubble` (+ stairs/slab/wall)
- **Vanilla analog**: `minecraft:cobblestone` / `minecraft:stone_bricks` (ruin/building stone)
- **Map color**: `MapColor.STONE`
- **Sound**: `SoundType.STONE`
- **Tool rule**: `requiresCorrectToolForDrops()`
- **Strength tier**: `2.2F / 18.0F`

#### Polished Stoneveil Rubble
- **IDs**: `kruemblegard:polished_stoneveil_rubble` (+ stairs/slab/wall)
- **Vanilla analog**: `minecraft:stone_bricks`
- **Strength tier**: `2.6F / 20.0F`

#### Runed Stoneveil Rubble
- **IDs**: `kruemblegard:runed_stoneveil_rubble` (+ stairs/slab/wall)
- **Vanilla analog**: `minecraft:chiseled_stone_bricks` (decorative runed stone)
- **Strength tier**: `2.8F / 22.0F`







