# Krümblegård Item Material Bible

This document is the **single source of truth** for how **items** should behave and read as *materials* in Krümblegård.

Scope:
- General items (components, seeds, food)
- Tools/weapons (tiers, stats)
- Spawn eggs / music discs
- Block items (as items)

Out of scope (see instead):
- Blocks: [docs/Block_Material_Bible.md](Block_Material_Bible.md)
- Wood blocks: [docs/Wood_Material_Bible.md](Wood_Material_Bible.md)
- Plants/flora blocks: [docs/Plant_Material_Bible.md](Plant_Material_Bible.md)

---

## Table of Contents

- [Global Rules](#global-rules)
- [Asset & Data Checklist](#asset--data-checklist)
- [Registry Inventory (Generated)](#registry-inventory-generated)

---

## Global Rules

- Prefer **data-driven + consistent** items: items in the same family should share stack sizes, rarities, and patterns.
- If an item is a **block item**, it should be registered consistently (prefer the `registerBlockItem(...)` helper).
- Tool sets should always document:
  - Tier
  - Attack damage and attack speed (as defined in code)
  - Intended role (mining vs combat vs utility)

---

## Asset & Data Checklist

For a new item, keep these in sync:

- Registration
  - Item in `src/main/java/com/kruemblegard/registry/ModItems.java`
- Assets
  - Item model JSON: `src/main/resources/assets/kruemblegard/models/item/<id>.json`
  - Textures: `src/main/resources/assets/kruemblegard/textures/item/<id>.png`
- Tags (if applicable)
  - `minecraft:music_discs`, tool tags, etc.
- Loot / recipes (if applicable)
  - Loot tables: `src/main/resources/data/kruemblegard/loot_tables/**`
  - Recipes: `src/main/resources/data/kruemblegard/recipes/**`

---

## Registry Inventory (Generated)

This section is **auto-generated** from:
- `src/main/java/com/kruemblegard/registry/ModItems.java`

Update workflow:
- After adding/removing/renaming items, run `tools/generate_material_bibles.ps1`.
- Commit the resulting doc changes in the same PR/commit as the content change.

<!-- AUTO-GENERATED:ITEMS:START -->

### Items (All Registered)

#### ancient_waystone
- **ID**: `kruemblegard:ancient_waystone`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### ashfall_loam
- **ID**: `kruemblegard:ashfall_loam`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### ashmoss
- **ID**: `kruemblegard:ashmoss`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### attuned_ore
- **ID**: `kruemblegard:attuned_ore`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### attuned_rune_shard
- **ID**: `kruemblegard:attuned_rune_shard`
- **Type**: `Item`
- **Details**: (none parsed)

#### attuned_stone
- **ID**: `kruemblegard:attuned_stone`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### attuned_stone_slab
- **ID**: `kruemblegard:attuned_stone_slab`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### attuned_stone_stairs
- **ID**: `kruemblegard:attuned_stone_stairs`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### attuned_stone_wall
- **ID**: `kruemblegard:attuned_stone_wall`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### chiseled_scarstone
- **ID**: `kruemblegard:chiseled_scarstone`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### cracked_scarstone
- **ID**: `kruemblegard:cracked_scarstone`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### cracked_scarstone_slab
- **ID**: `kruemblegard:cracked_scarstone_slab`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### cracked_scarstone_stairs
- **ID**: `kruemblegard:cracked_scarstone_stairs`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### cracked_scarstone_wall
- **ID**: `kruemblegard:cracked_scarstone_wall`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### crushstone
- **ID**: `kruemblegard:crushstone`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### crushstone_slab
- **ID**: `kruemblegard:crushstone_slab`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### crushstone_stairs
- **ID**: `kruemblegard:crushstone_stairs`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### crushstone_wall
- **ID**: `kruemblegard:crushstone_wall`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### driftwood_button
- **ID**: `kruemblegard:driftwood_button`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### driftwood_door
- **ID**: `kruemblegard:driftwood_door`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### driftwood_fence
- **ID**: `kruemblegard:driftwood_fence`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### driftwood_fence_gate
- **ID**: `kruemblegard:driftwood_fence_gate`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### driftwood_leaves
- **ID**: `kruemblegard:driftwood_leaves`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### driftwood_planks
- **ID**: `kruemblegard:driftwood_planks`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### driftwood_pressure_plate
- **ID**: `kruemblegard:driftwood_pressure_plate`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### driftwood_sapling
- **ID**: `kruemblegard:driftwood_sapling`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### driftwood_slab
- **ID**: `kruemblegard:driftwood_slab`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### driftwood_stairs
- **ID**: `kruemblegard:driftwood_stairs`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### driftwood_trapdoor
- **ID**: `kruemblegard:driftwood_trapdoor`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### driftwood_wood
- **ID**: `kruemblegard:driftwood_wood`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### echocap
- **ID**: `kruemblegard:echocap`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### fault_dust
- **ID**: `kruemblegard:fault_dust`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### fractured_wayrock
- **ID**: `kruemblegard:fractured_wayrock`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### fractured_wayrock_slab
- **ID**: `kruemblegard:fractured_wayrock_slab`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### fractured_wayrock_stairs
- **ID**: `kruemblegard:fractured_wayrock_stairs`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### fractured_wayrock_wall
- **ID**: `kruemblegard:fractured_wayrock_wall`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### ghoulberries
- **ID**: `kruemblegard:ghoulberries`
- **Type**: `Item`
- **Details**: nutrition=2, saturationMod=0.1

#### ghoulberry_shrub
- **ID**: `kruemblegard:ghoulberry_shrub`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### gravevine
- **ID**: `kruemblegard:gravevine`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### great_hunger_spawn_egg
- **ID**: `kruemblegard:great_hunger_spawn_egg`
- **Type**: `ForgeSpawnEggItem`
- **Details**: entity=ModEntities.GREAT_HUNGER, primaryColor=0x3a2a20, secondaryColor=0xc6a35d

#### kruemblegard_spawn_egg
- **ID**: `kruemblegard:kruemblegard_spawn_egg`
- **Type**: `ForgeSpawnEggItem`
- **Details**: entity=ModEntities.KRUEMBLEGARD, primaryColor=0x3b2f4a, secondaryColor=0x7a4fff

#### menu_tab
- **ID**: `kruemblegard:menu_tab`
- **Type**: `Item`
- **Details**: (none parsed)

#### pebblit_spawn_egg
- **ID**: `kruemblegard:pebblit_spawn_egg`
- **Type**: `ForgeSpawnEggItem`
- **Details**: entity=ModEntities.PEBBLIT, primaryColor=0x5a5147, secondaryColor=0xb4aa9d

#### polished_scarstone
- **ID**: `kruemblegard:polished_scarstone`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### polished_scarstone_slab
- **ID**: `kruemblegard:polished_scarstone_slab`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### polished_scarstone_stairs
- **ID**: `kruemblegard:polished_scarstone_stairs`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### polished_scarstone_wall
- **ID**: `kruemblegard:polished_scarstone_wall`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### polished_stoneveil_rubble
- **ID**: `kruemblegard:polished_stoneveil_rubble`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### polished_stoneveil_rubble_slab
- **ID**: `kruemblegard:polished_stoneveil_rubble_slab`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### polished_stoneveil_rubble_stairs
- **ID**: `kruemblegard:polished_stoneveil_rubble_stairs`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### polished_stoneveil_rubble_wall
- **ID**: `kruemblegard:polished_stoneveil_rubble_wall`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### remnant_seeds
- **ID**: `kruemblegard:remnant_seeds`
- **Type**: `Item`
- **Details**: (none parsed)

#### rune_petals
- **ID**: `kruemblegard:rune_petals`
- **Type**: `RunePetalItem`
- **Details**: (none parsed)

#### runebloom
- **ID**: `kruemblegard:runebloom`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### runed_stoneveil_rubble
- **ID**: `kruemblegard:runed_stoneveil_rubble`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### runed_stoneveil_rubble_slab
- **ID**: `kruemblegard:runed_stoneveil_rubble_slab`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### runed_stoneveil_rubble_stairs
- **ID**: `kruemblegard:runed_stoneveil_rubble_stairs`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### runed_stoneveil_rubble_wall
- **ID**: `kruemblegard:runed_stoneveil_rubble_wall`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### runegrowth
- **ID**: `kruemblegard:runegrowth`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### runic_axe
- **ID**: `kruemblegard:runic_axe`
- **Type**: `AxeItem`
- **Details**: (none parsed)

#### runic_core
- **ID**: `kruemblegard:runic_core`
- **Type**: `Item`
- **Details**: (none parsed)

#### runic_hoe
- **ID**: `kruemblegard:runic_hoe`
- **Type**: `HoeItem`
- **Details**: tier=ModTiers.RUNIC, attackDamageBonus=-3, attackSpeed=0.0

#### runic_pickaxe
- **ID**: `kruemblegard:runic_pickaxe`
- **Type**: `PickaxeItem`
- **Details**: tier=ModTiers.RUNIC, attackDamageBonus=1, attackSpeed=-2.8

#### runic_shovel
- **ID**: `kruemblegard:runic_shovel`
- **Type**: `ShovelItem`
- **Details**: (none parsed)

#### runic_sword
- **ID**: `kruemblegard:runic_sword`
- **Type**: `SwordItem`
- **Details**: tier=ModTiers.RUNIC, attackDamageBonus=3, attackSpeed=-2.4

#### scarstone
- **ID**: `kruemblegard:scarstone`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### scarstone_slab
- **ID**: `kruemblegard:scarstone_slab`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### scarstone_stairs
- **ID**: `kruemblegard:scarstone_stairs`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### scarstone_wall
- **ID**: `kruemblegard:scarstone_wall`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### soulberries
- **ID**: `kruemblegard:soulberries`
- **Type**: `Item`
- **Details**: nutrition=4, saturationMod=0.3

#### soulberry_shrub
- **ID**: `kruemblegard:soulberry_shrub`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### standing_stone
- **ID**: `kruemblegard:standing_stone`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### stoneveil_rubble
- **ID**: `kruemblegard:stoneveil_rubble`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### stoneveil_rubble_slab
- **ID**: `kruemblegard:stoneveil_rubble_slab`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### stoneveil_rubble_stairs
- **ID**: `kruemblegard:stoneveil_rubble_stairs`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### stoneveil_rubble_wall
- **ID**: `kruemblegard:stoneveil_rubble_wall`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### stripped_driftwood_log
- **ID**: `kruemblegard:stripped_driftwood_log`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### stripped_driftwood_wood
- **ID**: `kruemblegard:stripped_driftwood_wood`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### traprock_spawn_egg
- **ID**: `kruemblegard:traprock_spawn_egg`
- **Type**: `ForgeSpawnEggItem`
- **Details**: entity=ModEntities.TRAPROCK, primaryColor=0x3a2f2a, secondaryColor=0xd86b2e

#### voidfelt
- **ID**: `kruemblegard:voidfelt`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### wispstalk
- **ID**: `kruemblegard:wispstalk`
- **Type**: `BlockItem`
- **Details**: (none parsed)

<!-- AUTO-GENERATED:ITEMS:END -->

