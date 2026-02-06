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

#### ashbloom_boat
- **ID**: `kruemblegard:ashbloom_boat`
- **Type**: `KruemblegardBoatItem`
- **Details**: (none parsed)

#### ashbloom_chest_boat
- **ID**: `kruemblegard:ashbloom_chest_boat`
- **Type**: `KruemblegardBoatItem`
- **Details**: (none parsed)

#### ashbloom_hanging_sign
- **ID**: `kruemblegard:ashbloom_hanging_sign`
- **Type**: `HangingSignItem`
- **Details**: (none parsed)

#### ashbloom_sign
- **ID**: `kruemblegard:ashbloom_sign`
- **Type**: `SignItem`
- **Details**: (none parsed)

#### ashfall_loam
- **ID**: `kruemblegard:ashfall_loam`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### ashfall_stone
- **ID**: `kruemblegard:ashfall_stone`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### ashfall_stone_slab
- **ID**: `kruemblegard:ashfall_stone_slab`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### ashfall_stone_stairs
- **ID**: `kruemblegard:ashfall_stone_stairs`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### ashfall_stone_wall
- **ID**: `kruemblegard:ashfall_stone_wall`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### ashmoss
- **ID**: `kruemblegard:ashmoss`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### ashmoss_carpet
- **ID**: `kruemblegard:ashmoss_carpet`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### attuned_ingot
- **ID**: `kruemblegard:attuned_ingot`
- **Type**: `Item`
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

#### brown_mushroom_block_slab
- **ID**: `kruemblegard:brown_mushroom_block_slab`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### cairn_tree_boat
- **ID**: `kruemblegard:cairn_tree_boat`
- **Type**: `KruemblegardBoatItem`
- **Details**: (none parsed)

#### cairn_tree_chest_boat
- **ID**: `kruemblegard:cairn_tree_chest_boat`
- **Type**: `KruemblegardBoatItem`
- **Details**: (none parsed)

#### cairn_tree_hanging_sign
- **ID**: `kruemblegard:cairn_tree_hanging_sign`
- **Type**: `HangingSignItem`
- **Details**: (none parsed)

#### cairn_tree_sign
- **ID**: `kruemblegard:cairn_tree_sign`
- **Type**: `SignItem`
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

#### driftwillow_boat
- **ID**: `kruemblegard:driftwillow_boat`
- **Type**: `KruemblegardBoatItem`
- **Details**: (none parsed)

#### driftwillow_chest_boat
- **ID**: `kruemblegard:driftwillow_chest_boat`
- **Type**: `KruemblegardBoatItem`
- **Details**: (none parsed)

#### driftwillow_hanging_sign
- **ID**: `kruemblegard:driftwillow_hanging_sign`
- **Type**: `HangingSignItem`
- **Details**: (none parsed)

#### driftwillow_sign
- **ID**: `kruemblegard:driftwillow_sign`
- **Type**: `SignItem`
- **Details**: (none parsed)

#### driftwood_boat
- **ID**: `kruemblegard:driftwood_boat`
- **Type**: `KruemblegardBoatItem`
- **Details**: (none parsed)

#### driftwood_button
- **ID**: `kruemblegard:driftwood_button`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### driftwood_chest_boat
- **ID**: `kruemblegard:driftwood_chest_boat`
- **Type**: `KruemblegardBoatItem`
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

#### driftwood_hanging_sign
- **ID**: `kruemblegard:driftwood_hanging_sign`
- **Type**: `HangingSignItem`
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

#### driftwood_sign
- **ID**: `kruemblegard:driftwood_sign`
- **Type**: `SignItem`
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

#### echokern
- **ID**: `kruemblegard:echokern`
- **Type**: `Item`
- **Details**: nutrition=1, saturationMod=0.3

#### echowood_boat
- **ID**: `kruemblegard:echowood_boat`
- **Type**: `KruemblegardBoatItem`
- **Details**: (none parsed)

#### echowood_chest_boat
- **ID**: `kruemblegard:echowood_chest_boat`
- **Type**: `KruemblegardBoatItem`
- **Details**: (none parsed)

#### echowood_hanging_sign
- **ID**: `kruemblegard:echowood_hanging_sign`
- **Type**: `HangingSignItem`
- **Details**: (none parsed)

#### echowood_sign
- **ID**: `kruemblegard:echowood_sign`
- **Type**: `SignItem`
- **Details**: (none parsed)

#### emberwarmed_runegrowth
- **ID**: `kruemblegard:emberwarmed_runegrowth`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### fallbark_boat
- **ID**: `kruemblegard:fallbark_boat`
- **Type**: `KruemblegardBoatItem`
- **Details**: (none parsed)

#### fallbark_chest_boat
- **ID**: `kruemblegard:fallbark_chest_boat`
- **Type**: `KruemblegardBoatItem`
- **Details**: (none parsed)

#### fallbark_hanging_sign
- **ID**: `kruemblegard:fallbark_hanging_sign`
- **Type**: `HangingSignItem`
- **Details**: (none parsed)

#### fallbark_sign
- **ID**: `kruemblegard:fallbark_sign`
- **Type**: `SignItem`
- **Details**: (none parsed)

#### fault_dust
- **ID**: `kruemblegard:fault_dust`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### faultwood_boat
- **ID**: `kruemblegard:faultwood_boat`
- **Type**: `KruemblegardBoatItem`
- **Details**: (none parsed)

#### faultwood_chest_boat
- **ID**: `kruemblegard:faultwood_chest_boat`
- **Type**: `KruemblegardBoatItem`
- **Details**: (none parsed)

#### faultwood_hanging_sign
- **ID**: `kruemblegard:faultwood_hanging_sign`
- **Type**: `HangingSignItem`
- **Details**: (none parsed)

#### faultwood_sign
- **ID**: `kruemblegard:faultwood_sign`
- **Type**: `SignItem`
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

#### frostbound_runegrowth
- **ID**: `kruemblegard:frostbound_runegrowth`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### ghoulberries
- **ID**: `kruemblegard:ghoulberries`
- **Type**: `ItemNameBlockItem`
- **Details**: nutrition=2, saturationMod=0.1

#### ghoulberry_shrub
- **ID**: `kruemblegard:ghoulberry_shrub`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### giant_black_echo_fungus_cap
- **ID**: `kruemblegard:giant_black_echo_fungus_cap`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### giant_black_echo_fungus_cap_slab
- **ID**: `kruemblegard:giant_black_echo_fungus_cap_slab`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### giant_black_echo_fungus_stem
- **ID**: `kruemblegard:giant_black_echo_fungus_stem`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### giant_echo_puff_cap
- **ID**: `kruemblegard:giant_echo_puff_cap`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### giant_echo_puff_cap_slab
- **ID**: `kruemblegard:giant_echo_puff_cap_slab`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### giant_echo_puff_stem
- **ID**: `kruemblegard:giant_echo_puff_stem`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### giant_echocap_cap
- **ID**: `kruemblegard:giant_echocap_cap`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### giant_echocap_cap_slab
- **ID**: `kruemblegard:giant_echocap_cap_slab`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### giant_echocap_stem
- **ID**: `kruemblegard:giant_echocap_stem`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### giant_griefcap_cap
- **ID**: `kruemblegard:giant_griefcap_cap`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### giant_griefcap_cap_slab
- **ID**: `kruemblegard:giant_griefcap_cap_slab`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### giant_griefcap_stem
- **ID**: `kruemblegard:giant_griefcap_stem`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### giant_memory_rot_cap
- **ID**: `kruemblegard:giant_memory_rot_cap`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### giant_memory_rot_cap_slab
- **ID**: `kruemblegard:giant_memory_rot_cap_slab`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### giant_memory_rot_stem
- **ID**: `kruemblegard:giant_memory_rot_stem`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### giant_static_fungus_cap
- **ID**: `kruemblegard:giant_static_fungus_cap`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### giant_static_fungus_cap_slab
- **ID**: `kruemblegard:giant_static_fungus_cap_slab`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### giant_static_fungus_stem
- **ID**: `kruemblegard:giant_static_fungus_stem`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### giant_voidcap_briar_cap
- **ID**: `kruemblegard:giant_voidcap_briar_cap`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### giant_voidcap_briar_cap_slab
- **ID**: `kruemblegard:giant_voidcap_briar_cap_slab`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### giant_voidcap_briar_stem
- **ID**: `kruemblegard:giant_voidcap_briar_stem`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### giant_wayburn_fungus_cap
- **ID**: `kruemblegard:giant_wayburn_fungus_cap`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### giant_wayburn_fungus_cap_slab
- **ID**: `kruemblegard:giant_wayburn_fungus_cap_slab`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### giant_wayburn_fungus_stem
- **ID**: `kruemblegard:giant_wayburn_fungus_stem`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### giant_wayrot_fungus_cap
- **ID**: `kruemblegard:giant_wayrot_fungus_cap`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### giant_wayrot_fungus_cap_slab
- **ID**: `kruemblegard:giant_wayrot_fungus_cap_slab`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### giant_wayrot_fungus_stem
- **ID**: `kruemblegard:giant_wayrot_fungus_stem`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### glimmerpine_boat
- **ID**: `kruemblegard:glimmerpine_boat`
- **Type**: `KruemblegardBoatItem`
- **Details**: (none parsed)

#### glimmerpine_chest_boat
- **ID**: `kruemblegard:glimmerpine_chest_boat`
- **Type**: `KruemblegardBoatItem`
- **Details**: (none parsed)

#### glimmerpine_hanging_sign
- **ID**: `kruemblegard:glimmerpine_hanging_sign`
- **Type**: `HangingSignItem`
- **Details**: (none parsed)

#### glimmerpine_sign
- **ID**: `kruemblegard:glimmerpine_sign`
- **Type**: `SignItem`
- **Details**: (none parsed)

#### gravevine
- **ID**: `kruemblegard:gravevine`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### great_hunger_spawn_egg
- **ID**: `kruemblegard:great_hunger_spawn_egg`
- **Type**: `ForgeSpawnEggItem`
- **Details**: entity=ModEntities.GREAT_HUNGER, primaryColor=0x3a2a20, secondaryColor=0xc6a35d

#### hollowway_tree_boat
- **ID**: `kruemblegard:hollowway_tree_boat`
- **Type**: `KruemblegardBoatItem`
- **Details**: (none parsed)

#### hollowway_tree_chest_boat
- **ID**: `kruemblegard:hollowway_tree_chest_boat`
- **Type**: `KruemblegardBoatItem`
- **Details**: (none parsed)

#### hollowway_tree_hanging_sign
- **ID**: `kruemblegard:hollowway_tree_hanging_sign`
- **Type**: `HangingSignItem`
- **Details**: (none parsed)

#### hollowway_tree_sign
- **ID**: `kruemblegard:hollowway_tree_sign`
- **Type**: `SignItem`
- **Details**: (none parsed)

#### kruemblegard_spawn_egg
- **ID**: `kruemblegard:kruemblegard_spawn_egg`
- **Type**: `ForgeSpawnEggItem`
- **Details**: entity=ModEntities.KRUEMBLEGARD, primaryColor=0x3b2f4a, secondaryColor=0x7a4fff

#### menu_tab
- **ID**: `kruemblegard:menu_tab`
- **Type**: `Item`
- **Details**: (none parsed)

#### monument_oak_boat
- **ID**: `kruemblegard:monument_oak_boat`
- **Type**: `KruemblegardBoatItem`
- **Details**: (none parsed)

#### monument_oak_chest_boat
- **ID**: `kruemblegard:monument_oak_chest_boat`
- **Type**: `KruemblegardBoatItem`
- **Details**: (none parsed)

#### monument_oak_hanging_sign
- **ID**: `kruemblegard:monument_oak_hanging_sign`
- **Type**: `HangingSignItem`
- **Details**: (none parsed)

#### monument_oak_sign
- **ID**: `kruemblegard:monument_oak_sign`
- **Type**: `SignItem`
- **Details**: (none parsed)

#### moogloom_spawn_egg
- **ID**: `kruemblegard:moogloom_spawn_egg`
- **Type**: `ForgeSpawnEggItem`
- **Details**: entity=ModEntities.MOOGLOOM, primaryColor=0x2b1f33, secondaryColor=0x7d5aa6

#### paleweft_grass
- **ID**: `kruemblegard:paleweft_grass`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### paleweft_seeds
- **ID**: `kruemblegard:paleweft_seeds`
- **Type**: `ItemNameBlockItem`
- **Details**: (none parsed)

#### paleweft_tall_grass
- **ID**: `kruemblegard:paleweft_tall_grass`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### pebblit_spawn_egg
- **ID**: `kruemblegard:pebblit_spawn_egg`
- **Type**: `ForgeSpawnEggItem`
- **Details**: entity=ModEntities.PEBBLIT, primaryColor=0x5a5147, secondaryColor=0xb4aa9d

#### polished_ashfall_stone
- **ID**: `kruemblegard:polished_ashfall_stone`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### polished_ashfall_stone_slab
- **ID**: `kruemblegard:polished_ashfall_stone_slab`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### polished_ashfall_stone_stairs
- **ID**: `kruemblegard:polished_ashfall_stone_stairs`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### polished_ashfall_stone_wall
- **ID**: `kruemblegard:polished_ashfall_stone_wall`
- **Type**: `BlockItem`
- **Details**: (none parsed)

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

#### pyrokelp
- **ID**: `kruemblegard:pyrokelp`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### red_mushroom_block_slab
- **ID**: `kruemblegard:red_mushroom_block_slab`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### remnant_seeds
- **ID**: `kruemblegard:remnant_seeds`
- **Type**: `Item`
- **Details**: (none parsed)

#### rubble_tilth
- **ID**: `kruemblegard:rubble_tilth`
- **Type**: `BlockItem`
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
- **Details**: tier=ModTiers.RUNIC, attackDamageBonus=5.0, attackSpeed=-3.0

#### runic_core
- **ID**: `kruemblegard:runic_core`
- **Type**: `Item`
- **Details**: (none parsed)

#### runic_debris
- **ID**: `kruemblegard:runic_debris`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### runic_hoe
- **ID**: `kruemblegard:runic_hoe`
- **Type**: `HoeItem`
- **Details**: tier=ModTiers.RUNIC, attackDamageBonus=-3, attackSpeed=0.0

#### runic_ingot
- **ID**: `kruemblegard:runic_ingot`
- **Type**: `Item`
- **Details**: (none parsed)

#### runic_pickaxe
- **ID**: `kruemblegard:runic_pickaxe`
- **Type**: `PickaxeItem`
- **Details**: tier=ModTiers.RUNIC, attackDamageBonus=1, attackSpeed=-2.8

#### runic_scrap
- **ID**: `kruemblegard:runic_scrap`
- **Type**: `Item`
- **Details**: (none parsed)

#### runic_shovel
- **ID**: `kruemblegard:runic_shovel`
- **Type**: `ShovelItem`
- **Details**: tier=ModTiers.RUNIC, attackDamageBonus=1.5, attackSpeed=-3.0

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

#### scattered_enderman_spawn_egg
- **ID**: `kruemblegard:scattered_enderman_spawn_egg`
- **Type**: `ForgeSpawnEggItem`
- **Details**: entity=ModEntities.SCATTERED_ENDERMAN, primaryColor=0x161616, secondaryColor=0x9a30ff

#### soulberries
- **ID**: `kruemblegard:soulberries`
- **Type**: `ItemNameBlockItem`
- **Details**: nutrition=4, saturationMod=0.3

#### soulberry_shrub
- **ID**: `kruemblegard:soulberry_shrub`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### splinterspore_boat
- **ID**: `kruemblegard:splinterspore_boat`
- **Type**: `KruemblegardBoatItem`
- **Details**: (none parsed)

#### splinterspore_chest_boat
- **ID**: `kruemblegard:splinterspore_chest_boat`
- **Type**: `KruemblegardBoatItem`
- **Details**: (none parsed)

#### splinterspore_hanging_sign
- **ID**: `kruemblegard:splinterspore_hanging_sign`
- **Type**: `HangingSignItem`
- **Details**: (none parsed)

#### splinterspore_sign
- **ID**: `kruemblegard:splinterspore_sign`
- **Type**: `SignItem`
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

#### verdant_runegrowth
- **ID**: `kruemblegard:verdant_runegrowth`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### voidfelt
- **ID**: `kruemblegard:voidfelt`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### wayfall_copper_ore
- **ID**: `kruemblegard:wayfall_copper_ore`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### wayfall_diamond_ore
- **ID**: `kruemblegard:wayfall_diamond_ore`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### wayfall_iron_ore
- **ID**: `kruemblegard:wayfall_iron_ore`
- **Type**: `BlockItem`
- **Details**: (none parsed)

#### wayglass_boat
- **ID**: `kruemblegard:wayglass_boat`
- **Type**: `KruemblegardBoatItem`
- **Details**: (none parsed)

#### wayglass_chest_boat
- **ID**: `kruemblegard:wayglass_chest_boat`
- **Type**: `KruemblegardBoatItem`
- **Details**: (none parsed)

#### wayglass_hanging_sign
- **ID**: `kruemblegard:wayglass_hanging_sign`
- **Type**: `HangingSignItem`
- **Details**: (none parsed)

#### wayglass_sign
- **ID**: `kruemblegard:wayglass_sign`
- **Type**: `SignItem`
- **Details**: (none parsed)

#### wayroot_boat
- **ID**: `kruemblegard:wayroot_boat`
- **Type**: `KruemblegardBoatItem`
- **Details**: (none parsed)

#### wayroot_chest_boat
- **ID**: `kruemblegard:wayroot_chest_boat`
- **Type**: `KruemblegardBoatItem`
- **Details**: (none parsed)

#### wayroot_hanging_sign
- **ID**: `kruemblegard:wayroot_hanging_sign`
- **Type**: `HangingSignItem`
- **Details**: (none parsed)

#### wayroot_sign
- **ID**: `kruemblegard:wayroot_sign`
- **Type**: `SignItem`
- **Details**: (none parsed)

#### waytorch_tree_boat
- **ID**: `kruemblegard:waytorch_tree_boat`
- **Type**: `KruemblegardBoatItem`
- **Details**: (none parsed)

#### waytorch_tree_chest_boat
- **ID**: `kruemblegard:waytorch_tree_chest_boat`
- **Type**: `KruemblegardBoatItem`
- **Details**: (none parsed)

#### waytorch_tree_hanging_sign
- **ID**: `kruemblegard:waytorch_tree_hanging_sign`
- **Type**: `HangingSignItem`
- **Details**: (none parsed)

#### waytorch_tree_sign
- **ID**: `kruemblegard:waytorch_tree_sign`
- **Type**: `SignItem`
- **Details**: (none parsed)

#### weftkern
- **ID**: `kruemblegard:weftkern`
- **Type**: `Item`
- **Details**: nutrition=1, saturationMod=0.3

#### weftmeal
- **ID**: `kruemblegard:weftmeal`
- **Type**: `Item`
- **Details**: nutrition=5, saturationMod=0.6

#### wispshoot
- **ID**: `kruemblegard:wispshoot`
- **Type**: `ItemNameBlockItem`
- **Details**: nutrition=3, saturationMod=0.2

#### wispstalk
- **ID**: `kruemblegard:wispstalk`
- **Type**: `BlockItem`
- **Details**: (none parsed)

<!-- AUTO-GENERATED:ITEMS:END -->







