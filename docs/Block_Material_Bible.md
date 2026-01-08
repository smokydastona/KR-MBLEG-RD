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
