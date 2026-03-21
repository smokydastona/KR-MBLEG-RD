# Krümblegård Mob Bible

This document is the **single source of truth** for the current Krümblegård mob roster and what each mob still needs (loot, spawns, sounds, assets).

Scope:
- Custom entity roster registered in code (mobs only; excludes boats)
- Per-mob authoring status as discovered by the Mob Audit report

Tools used / needed:
- Generate the audit report: `python tools/_reports/mob_audit_runner.py`
- Generate / update this doc: `powershell -NoProfile -ExecutionPolicy Bypass -File tools\\generate_mob_bible.ps1`
  - (or directly: `python tools/generate_mob_bible.py`)

Update workflow:
- After adding/removing a mob, changing renderer/model bindings, or adding loot/spawns/sounds
- Regenerate the audit report, then regenerate this doc
- Commit the resulting doc changes in the same commit as the content change

---

## Table of Contents

- [How to Read Status](#how-to-read-status)
- [Mob Inventory (Generated)](#mob-inventory-generated)

---

## How to Read Status

Status icons in this doc are sourced from the audit report:
- ✅ = present / wired
- ⚠️ = missing, unknown, or intentionally vanilla (needs confirmation)
- ❌ = confirmed defect (must fix)

---

## Mob Inventory (Generated)

This section is **auto-generated** from:
- `tools/_reports/mob_audit_report.json`

Update workflow:
- Regenerate the audit report: `python tools/_reports/mob_audit_runner.py`
- Then regenerate this doc: `powershell -NoProfile -ExecutionPolicy Bypass -File tools\\generate_mob_bible.ps1`

<!-- AUTO-GENERATED:MOBS:START -->

Generated: 2026-03-21 13:14:17

### Report Metadata
- Source: `C:/Users/smoky/OneDrive/Desktop/Homemade Mods/Krümblegård/tools/_reports/mob_audit_report.json`
- schemaVersion: `7`
- mobCount: `23`
- generatedBy: `tools/_reports/mob_audit_runner.py`

### Backlog (from audit)
- Missing entity loot tables: 9
  - `cephalari`, `driftskimmer`, `echo_harness`, `pebblit`, `spiral_strider`, `traprock`, `treadwinder`, `unkeeper`, `wyrdwing`
- No biome modifier refs (may not spawn naturally): 5
  - `cephalari_golem`, `driftskimmer`, `echo_harness`, `spiral_strider`, `treadwinder`
- No sounds.json matches (may rely on vanilla sounds): 12
  - `cephalari_drowned`, `cephalari_golem`, `cephalari_husk`, `cephalari_zombie`, `driftskimmer`, `driftwhale`, `echo_harness`, `grave_cairn`, `mossback_tortoise`, `pebble_wren`, `spiral_strider`, `treadwinder`
- Missing spawn eggs: 0
  - (none)
- Animation warnings (non-fatal): 1
  - `traprock`

### Confirmed Failures
- (none)

### Roster Status Table

| Mob | Score | Geo | Tex | Anim | AI | Loot | Sounds | Spawns | Egg | Integr | Perf |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `cephalari` | 90 | ✅ | ✅ | ✅ | ✅ | ⚠️ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `cephalari_drowned` | 95 | ✅ | ✅ | ✅ | ✅ | ✅ | ⚠️ | ✅ | ✅ | ✅ | ✅ |
| `cephalari_golem` | 90 | ✅ | ✅ | ✅ | ✅ | ✅ | ⚠️ | ⚠️ | ✅ | ✅ | ✅ |
| `cephalari_husk` | 95 | ✅ | ✅ | ✅ | ✅ | ✅ | ⚠️ | ✅ | ✅ | ✅ | ✅ |
| `cephalari_zombie` | 95 | ✅ | ✅ | ✅ | ✅ | ✅ | ⚠️ | ✅ | ✅ | ✅ | ✅ |
| `driftskimmer` | 80 | ✅ | ✅ | ✅ | ✅ | ⚠️ | ⚠️ | ⚠️ | ✅ | ✅ | ✅ |
| `driftwhale` | 95 | ✅ | ✅ | ✅ | ✅ | ✅ | ⚠️ | ✅ | ✅ | ✅ | ✅ |
| `echo_harness` | 80 | ✅ | ✅ | ✅ | ✅ | ⚠️ | ⚠️ | ⚠️ | ✅ | ✅ | ✅ |
| `fault_crawler` | 100 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `grave_cairn` | 95 | ✅ | ✅ | ✅ | ✅ | ✅ | ⚠️ | ✅ | ✅ | ✅ | ✅ |
| `kruemblegard` | 100 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `moogloom` | 100 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `mossback_tortoise` | 95 | ✅ | ✅ | ✅ | ✅ | ✅ | ⚠️ | ✅ | ✅ | ✅ | ✅ |
| `pebble_wren` | 95 | ✅ | ✅ | ✅ | ✅ | ✅ | ⚠️ | ✅ | ✅ | ✅ | ✅ |
| `pebblit` | 90 | ✅ | ✅ | ✅ | ✅ | ⚠️ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `scaralon_beetle` | 100 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `scattered_enderman` | 100 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `spiral_strider` | 80 | ✅ | ✅ | ✅ | ✅ | ⚠️ | ⚠️ | ⚠️ | ✅ | ✅ | ✅ |
| `trader_beetle` | 100 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `traprock` | 90 | ✅ | ✅ | ⚠️ | ✅ | ⚠️ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `treadwinder` | 80 | ✅ | ✅ | ✅ | ✅ | ⚠️ | ⚠️ | ⚠️ | ✅ | ✅ | ✅ |
| `unkeeper` | 90 | ✅ | ✅ | ✅ | ✅ | ⚠️ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `wyrdwing` | 90 | ✅ | ✅ | ✅ | ✅ | ⚠️ | ✅ | ✅ | ✅ | ✅ | ✅ |

### Per-Mob Detail

#### `cephalari`
- Score: `90`
- Status: geo=✅ tex=✅ anim=✅ ai=✅ loot=⚠️ sounds=✅ spawns=✅ egg=✅ integration=✅ perf=✅
- Classes: Entity: `CephalariEntity` | Renderer: `CephalariRenderer` | Model: `CephalariModel`
- Geo: `src/main/resources/assets/kruemblegard/geo/cephalari.geo.json`, `src/main/resources/assets/kruemblegard/geo/spiral_strider.geo.json`, `src/main/resources/assets/kruemblegard/geo/driftskimmer.geo.json`, `src/main/resources/assets/kruemblegard/geo/treadwinder.geo.json`, `src/main/resources/assets/kruemblegard/geo/echo_harness.geo.json`
- Animations: `src/main/resources/assets/kruemblegard/animations/cephalari.animation.json`
- Textures (6): `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_1.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_2.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_3.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_4.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_5.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_6.png`
- Loot table: **MISSING**
- Biome modifiers: `src/main/resources/data/kruemblegard/forge/biome_modifier/add_cephalari_drowned_spawns_wayfall.json`, `src/main/resources/data/kruemblegard/forge/biome_modifier/add_cephalari_husk_spawns_wayfall.json`, `src/main/resources/data/kruemblegard/forge/biome_modifier/add_cephalari_zombie_spawns_overworld.json`, `src/main/resources/data/kruemblegard/forge/biome_modifier/add_cephalari_zombie_spawns_wayfall.json`
- Lang keys: entity=`entity.kruemblegard.cephalari` spawnEgg=`item.kruemblegard.cephalari_spawn_egg`
- TODO: Add entity loot table
- Audit notes:
  - 11 animation bone names not found in geo bones (may include controller-only bones)
  - No entity loot table found under data/.../loot_tables/entities/

#### `cephalari_drowned`
- Score: `95`
- Status: geo=✅ tex=✅ anim=✅ ai=✅ loot=✅ sounds=⚠️ spawns=✅ egg=✅ integration=✅ perf=✅
- Classes: Entity: `CephalariDrownedEntity` | Renderer: `CephalariZombieRenderer` | Model: `CephalariZombieModel`
- Geo: `src/main/resources/assets/kruemblegard/geo/cephalari_zombie.geo.json`
- Animations: `src/main/resources/assets/kruemblegard/animations/cephalari_zombie.animation.json`
- Textures (26): `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_basin_of_scars.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_bonus_1.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_bonus_2.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_bonus_3.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_bonus_4.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_crumbled_crossing.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_driftway_chasm.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_faulted_expanse.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_fracture_shoals.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_glyphscar_reach.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_hollow_transit_plains.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_midweft_wilds.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_riven_causeways.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_shatterplate_flats.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_strata_collapse.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_underway_falls.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_1.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_2.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_3.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_4.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_5.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_6.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_zombie/cephalari_drowned_outer_layer.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_zombie/cephalari_husk_outer_layer.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_zombie/cephalari_inner_layer.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_zombie/cephalari_zombie_outer_layer.png`
- Loot table: `src/main/resources/data/kruemblegard/loot_tables/entities/cephalari_drowned.json`
- Biome modifiers: `src/main/resources/data/kruemblegard/forge/biome_modifier/add_cephalari_drowned_spawns_wayfall.json`
- Lang keys: entity=`entity.kruemblegard.cephalari_drowned` spawnEgg=`item.kruemblegard.cephalari_drowned_spawn_egg`
- TODO: Confirm vanilla sounds or add custom sounds
- Audit notes:
  - 5 animation bone names not found in geo bones (may include controller-only bones)
  - No sounds.json entries matched this mob id (may rely on vanilla sounds)

#### `cephalari_golem`
- Score: `90`
- Status: geo=✅ tex=✅ anim=✅ ai=✅ loot=✅ sounds=⚠️ spawns=⚠️ egg=✅ integration=✅ perf=✅
- Classes: Entity: `CephalariGolemEntity` | Renderer: `CephalariGolemRenderer` | Model: `CephalariGolemModel`
- Geo: `src/main/resources/assets/kruemblegard/geo/cephalari_golem.geo.json`
- Animations: `src/main/resources/assets/kruemblegard/animations/cephalari_golem.animation.json`
- Textures (26): `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_basin_of_scars.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_bonus_1.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_bonus_2.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_bonus_3.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_bonus_4.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_crumbled_crossing.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_driftway_chasm.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_faulted_expanse.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_fracture_shoals.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_glyphscar_reach.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_hollow_transit_plains.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_midweft_wilds.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_riven_causeways.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_shatterplate_flats.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_strata_collapse.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_underway_falls.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_1.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_2.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_3.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_4.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_5.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_6.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_zombie/cephalari_drowned_outer_layer.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_zombie/cephalari_husk_outer_layer.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_zombie/cephalari_inner_layer.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_zombie/cephalari_zombie_outer_layer.png`
- Loot table: `src/main/resources/data/kruemblegard/loot_tables/entities/cephalari_golem.json`
- Biome modifiers: **NONE FOUND**
- Lang keys: entity=`entity.kruemblegard.cephalari_golem` spawnEgg=`item.kruemblegard.cephalari_golem_spawn_egg`
- TODO: Add biome modifier (if should spawn naturally); Confirm vanilla sounds or add custom sounds
- Audit notes:
  - No sounds.json entries matched this mob id (may rely on vanilla sounds)
  - No biome modifier references found (may not spawn naturally)

#### `cephalari_husk`
- Score: `95`
- Status: geo=✅ tex=✅ anim=✅ ai=✅ loot=✅ sounds=⚠️ spawns=✅ egg=✅ integration=✅ perf=✅
- Classes: Entity: `CephalariHuskEntity` | Renderer: `CephalariZombieRenderer` | Model: `CephalariZombieModel`
- Geo: `src/main/resources/assets/kruemblegard/geo/cephalari_zombie.geo.json`
- Animations: `src/main/resources/assets/kruemblegard/animations/cephalari_zombie.animation.json`
- Textures (26): `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_basin_of_scars.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_bonus_1.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_bonus_2.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_bonus_3.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_bonus_4.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_crumbled_crossing.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_driftway_chasm.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_faulted_expanse.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_fracture_shoals.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_glyphscar_reach.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_hollow_transit_plains.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_midweft_wilds.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_riven_causeways.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_shatterplate_flats.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_strata_collapse.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_underway_falls.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_1.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_2.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_3.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_4.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_5.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_6.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_zombie/cephalari_drowned_outer_layer.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_zombie/cephalari_husk_outer_layer.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_zombie/cephalari_inner_layer.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_zombie/cephalari_zombie_outer_layer.png`
- Loot table: `src/main/resources/data/kruemblegard/loot_tables/entities/cephalari_husk.json`
- Biome modifiers: `src/main/resources/data/kruemblegard/forge/biome_modifier/add_cephalari_husk_spawns_wayfall.json`
- Lang keys: entity=`entity.kruemblegard.cephalari_husk` spawnEgg=`item.kruemblegard.cephalari_husk_spawn_egg`
- TODO: Confirm vanilla sounds or add custom sounds
- Audit notes:
  - 5 animation bone names not found in geo bones (may include controller-only bones)
  - No sounds.json entries matched this mob id (may rely on vanilla sounds)

#### `cephalari_zombie`
- Score: `95`
- Status: geo=✅ tex=✅ anim=✅ ai=✅ loot=✅ sounds=⚠️ spawns=✅ egg=✅ integration=✅ perf=✅
- Classes: Entity: `CephalariZombieEntity` | Renderer: `CephalariZombieRenderer` | Model: `CephalariZombieModel`
- Geo: `src/main/resources/assets/kruemblegard/geo/cephalari_zombie.geo.json`
- Animations: `src/main/resources/assets/kruemblegard/animations/cephalari_zombie.animation.json`
- Textures (26): `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_basin_of_scars.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_bonus_1.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_bonus_2.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_bonus_3.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_bonus_4.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_crumbled_crossing.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_driftway_chasm.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_faulted_expanse.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_fracture_shoals.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_glyphscar_reach.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_hollow_transit_plains.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_midweft_wilds.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_riven_causeways.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_shatterplate_flats.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_strata_collapse.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari/cephalari_underway_falls.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_1.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_2.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_3.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_4.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_5.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_6.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_zombie/cephalari_drowned_outer_layer.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_zombie/cephalari_husk_outer_layer.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_zombie/cephalari_inner_layer.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_zombie/cephalari_zombie_outer_layer.png`
- Loot table: `src/main/resources/data/kruemblegard/loot_tables/entities/cephalari_zombie.json`
- Biome modifiers: `src/main/resources/data/kruemblegard/forge/biome_modifier/add_cephalari_zombie_spawns_overworld.json`, `src/main/resources/data/kruemblegard/forge/biome_modifier/add_cephalari_zombie_spawns_wayfall.json`
- Lang keys: entity=`entity.kruemblegard.cephalari_zombie` spawnEgg=`item.kruemblegard.cephalari_zombie_spawn_egg`
- TODO: Confirm vanilla sounds or add custom sounds
- Audit notes:
  - 5 animation bone names not found in geo bones (may include controller-only bones)
  - No sounds.json entries matched this mob id (may rely on vanilla sounds)

#### `driftskimmer`
- Score: `80`
- Status: geo=✅ tex=✅ anim=✅ ai=✅ loot=⚠️ sounds=⚠️ spawns=⚠️ egg=✅ integration=✅ perf=✅
- Classes: Entity: `DriftSkimmerEntity` | Renderer: `DriftSkimmerRenderer` | Model: `DriftSkimmerModel`
- Geo: `src/main/resources/assets/kruemblegard/geo/driftskimmer.geo.json`
- Animations: `src/main/resources/assets/kruemblegard/animations/driftskimmer.animation.json`
- Textures (6): `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_1.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_2.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_3.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_4.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_5.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_6.png`
- Loot table: **MISSING**
- Biome modifiers: **NONE FOUND**
- Lang keys: entity=`entity.kruemblegard.driftskimmer` spawnEgg=`item.kruemblegard.driftskimmer_spawn_egg`
- TODO: Add entity loot table; Add biome modifier (if should spawn naturally); Confirm vanilla sounds or add custom sounds
- Audit notes:
  - Animation uses "*" wildcard bone (verify GeckoLib compatibility for this file)
  - No sounds.json entries matched this mob id (may rely on vanilla sounds)
  - No entity loot table found under data/.../loot_tables/entities/
  - No biome modifier references found (may not spawn naturally)

#### `driftwhale`
- Score: `95`
- Status: geo=✅ tex=✅ anim=✅ ai=✅ loot=✅ sounds=⚠️ spawns=✅ egg=✅ integration=✅ perf=✅
- Classes: Entity: `DriftwhaleEntity` | Renderer: `DriftwhaleRenderer` | Model: `DriftwhaleModel`
- Geo: `src/main/resources/assets/kruemblegard/geo/driftwhale.geo.json`
- Animations: `src/main/resources/assets/kruemblegard/animations/driftwhale.animation.json`
- Textures (1): `src/main/resources/assets/kruemblegard/textures/entity/driftwhale.png`
- Loot table: `src/main/resources/data/kruemblegard/loot_tables/entities/driftwhale.json`
- Biome modifiers: `src/main/resources/data/kruemblegard/forge/biome_modifier/add_wayfall_driftwhale_spawns.json`
- Lang keys: entity=`entity.kruemblegard.driftwhale` spawnEgg=`item.kruemblegard.driftwhale_spawn_egg`
- TODO: Confirm vanilla sounds or add custom sounds
- Audit notes:
  - No sounds.json entries matched this mob id (may rely on vanilla sounds)

#### `echo_harness`
- Score: `80`
- Status: geo=✅ tex=✅ anim=✅ ai=✅ loot=⚠️ sounds=⚠️ spawns=⚠️ egg=✅ integration=✅ perf=✅
- Classes: Entity: `EchoHarnessEntity` | Renderer: `EchoHarnessRenderer` | Model: `EchoHarnessModel`
- Geo: `src/main/resources/assets/kruemblegard/geo/echo_harness.geo.json`
- Animations: `src/main/resources/assets/kruemblegard/animations/echo_harness.animation.json`
- Textures (6): `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_1.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_2.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_3.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_4.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_5.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_6.png`
- Loot table: **MISSING**
- Biome modifiers: **NONE FOUND**
- Lang keys: entity=`entity.kruemblegard.echo_harness` spawnEgg=`item.kruemblegard.echo_harness_spawn_egg`
- TODO: Add entity loot table; Add biome modifier (if should spawn naturally); Confirm vanilla sounds or add custom sounds
- Audit notes:
  - Animation uses "*" wildcard bone (verify GeckoLib compatibility for this file)
  - No sounds.json entries matched this mob id (may rely on vanilla sounds)
  - No entity loot table found under data/.../loot_tables/entities/
  - No biome modifier references found (may not spawn naturally)

#### `fault_crawler`
- Score: `100`
- Status: geo=✅ tex=✅ anim=✅ ai=✅ loot=✅ sounds=✅ spawns=✅ egg=✅ integration=✅ perf=✅
- Classes: Entity: `FaultCrawlerEntity` | Renderer: `FaultCrawlerRenderer` | Model: `FaultCrawlerModel`
- Geo: `src/main/resources/assets/kruemblegard/geo/fault_crawler.geo.json`
- Animations: `src/main/resources/assets/kruemblegard/animations/fault_crawler.animation.json`
- Textures (1): `src/main/resources/assets/kruemblegard/textures/entity/fault_crawler.png`
- Loot table: `src/main/resources/data/kruemblegard/loot_tables/entities/fault_crawler.json`
- Biome modifiers: `src/main/resources/data/kruemblegard/forge/biome_modifier/add_wayfall_fault_crawler_spawns.json`
- Lang keys: entity=`entity.kruemblegard.fault_crawler` spawnEgg=`item.kruemblegard.fault_crawler_spawn_egg`

#### `grave_cairn`
- Score: `95`
- Status: geo=✅ tex=✅ anim=✅ ai=✅ loot=✅ sounds=⚠️ spawns=✅ egg=✅ integration=✅ perf=✅
- Classes: Entity: `GraveCairnEntity` | Renderer: `GraveCairnRenderer` | Model: `GraveCairnModel`
- Geo: `src/main/resources/assets/kruemblegard/geo/grave_cairn.geo.json`
- Animations: `src/main/resources/assets/kruemblegard/animations/grave_cairn.animation.json`
- Textures (1): `src/main/resources/assets/kruemblegard/textures/entity/grave_cairn.png`
- Loot table: `src/main/resources/data/kruemblegard/loot_tables/entities/grave_cairn.json`
- Biome modifiers: `src/main/resources/data/kruemblegard/forge/biome_modifier/add_wayfall_grave_cairn_spawns.json`
- Lang keys: entity=`entity.kruemblegard.grave_cairn` spawnEgg=`item.kruemblegard.grave_cairn_spawn_egg`
- TODO: Confirm vanilla sounds or add custom sounds
- Audit notes:
  - No sounds.json entries matched this mob id (may rely on vanilla sounds)

#### `kruemblegard`
- Score: `100`
- Status: geo=✅ tex=✅ anim=✅ ai=✅ loot=✅ sounds=✅ spawns=✅ egg=✅ integration=✅ perf=✅
- Classes: Entity: `KruemblegardBossEntity` | Renderer: `KruemblegardBossRenderer` | Model: `KruemblegardBossModel`
- Geo: `src/main/resources/assets/kruemblegard/geo/kruemblegard.geo.json`
- Animations: `src/main/resources/assets/kruemblegard/animations/kruemblegard.animation.json`
- Textures (2): `src/main/resources/assets/kruemblegard/textures/entity/kruemblegard.png`, `src/main/resources/assets/kruemblegard/textures/entity/kruemblegard_phase4.png`
- Loot table: `src/main/resources/data/kruemblegard/loot_tables/entities/kruemblegard.json`
- Biome modifiers: **NONE FOUND**
- Lang keys: entity=`entity.kruemblegard.kruemblegard` spawnEgg=`item.kruemblegard.kruemblegard_spawn_egg`

#### `moogloom`
- Score: `100`
- Status: geo=✅ tex=✅ anim=✅ ai=✅ loot=✅ sounds=✅ spawns=✅ egg=✅ integration=✅ perf=✅
- Classes: Entity: `MoogloomEntity` | Renderer: `MoogloomRenderer` | Model: `MoogloomModel`
- Geo: `src/main/resources/assets/kruemblegard/geo/moogloom.geo.json`
- Animations: `src/main/resources/assets/kruemblegard/animations/moogloom.animation.json`
- Textures (1): `src/main/resources/assets/kruemblegard/textures/entity/moogloom.png`
- Loot table: `src/main/resources/data/kruemblegard/loot_tables/entities/moogloom.json`
- Biome modifiers: `src/main/resources/data/kruemblegard/forge/biome_modifier/add_moogloom_spawns.json`
- Lang keys: entity=`entity.kruemblegard.moogloom` spawnEgg=`item.kruemblegard.moogloom_spawn_egg`
- Audit notes:
  - Uses vanilla sounds (no sounds.json entries expected)

#### `mossback_tortoise`
- Score: `95`
- Status: geo=✅ tex=✅ anim=✅ ai=✅ loot=✅ sounds=⚠️ spawns=✅ egg=✅ integration=✅ perf=✅
- Classes: Entity: `MossbackTortoiseEntity` | Renderer: `MossbackTortoiseRenderer` | Model: `MossbackTortoiseModel`
- Geo: `src/main/resources/assets/kruemblegard/geo/mossback_tortoise.geo.json`
- Animations: `src/main/resources/assets/kruemblegard/animations/mossback_tortoise.animation.json`
- Textures (1): `src/main/resources/assets/kruemblegard/textures/entity/mossback_tortoise.png`
- Loot table: `src/main/resources/data/kruemblegard/loot_tables/entities/mossback_tortoise.json`
- Biome modifiers: `src/main/resources/data/kruemblegard/forge/biome_modifier/add_wayfall_mossback_tortoise_spawns.json`
- Lang keys: entity=`entity.kruemblegard.mossback_tortoise` spawnEgg=`item.kruemblegard.mossback_tortoise_spawn_egg`
- TODO: Confirm vanilla sounds or add custom sounds
- Audit notes:
  - No sounds.json entries matched this mob id (may rely on vanilla sounds)

#### `pebble_wren`
- Score: `95`
- Status: geo=✅ tex=✅ anim=✅ ai=✅ loot=✅ sounds=⚠️ spawns=✅ egg=✅ integration=✅ perf=✅
- Classes: Entity: `PebbleWrenEntity` | Renderer: `PebbleWrenRenderer` | Model: `PebbleWrenModel`
- Geo: `src/main/resources/assets/kruemblegard/geo/pebble_wren.geo.json`
- Animations: `src/main/resources/assets/kruemblegard/animations/pebble_wren.animation.json`
- Textures (1): `src/main/resources/assets/kruemblegard/textures/entity/pebble_wren.png`
- Loot table: `src/main/resources/data/kruemblegard/loot_tables/entities/pebble_wren.json`
- Biome modifiers: `src/main/resources/data/kruemblegard/forge/biome_modifier/add_wayfall_pebble_wren_spawns.json`
- Lang keys: entity=`entity.kruemblegard.pebble_wren` spawnEgg=`item.kruemblegard.pebble_wren_spawn_egg`
- TODO: Confirm vanilla sounds or add custom sounds
- Audit notes:
  - No sounds.json entries matched this mob id (may rely on vanilla sounds)

#### `pebblit`
- Score: `90`
- Status: geo=✅ tex=✅ anim=✅ ai=✅ loot=⚠️ sounds=✅ spawns=✅ egg=✅ integration=✅ perf=✅
- Classes: Entity: `PebblitEntity` | Renderer: `PebblitRenderer` | Model: `PebblitModel`
- Geo: `src/main/resources/assets/kruemblegard/geo/pebblit.geo.json`
- Animations: `src/main/resources/assets/kruemblegard/animations/pebblit.animation.json`
- Textures (1): `src/main/resources/assets/kruemblegard/textures/entity/pebblit.png`
- Loot table: **MISSING**
- Biome modifiers: `src/main/resources/data/kruemblegard/forge/biome_modifier/add_wayfall_pebblit_spawns.json`
- Lang keys: entity=`entity.kruemblegard.pebblit` spawnEgg=`item.kruemblegard.pebblit_spawn_egg`
- TODO: Add entity loot table
- Audit notes:
  - No entity loot table found under data/.../loot_tables/entities/

#### `scaralon_beetle`
- Score: `100`
- Status: geo=✅ tex=✅ anim=✅ ai=✅ loot=✅ sounds=✅ spawns=✅ egg=✅ integration=✅ perf=✅
- Classes: Entity: `ScaralonBeetleEntity` | Renderer: `ScaralonBeetleRenderer` | Model: `ScaralonBeetleModel`
- Geo: `src/main/resources/assets/kruemblegard/geo/scaralon_larva.geo.json`, `src/main/resources/assets/kruemblegard/geo/scaralon_beetle.geo.json`
- Animations: `src/main/resources/assets/kruemblegard/animations/scaralon_larva.animation.json`, `src/main/resources/assets/kruemblegard/animations/scaralon_beetle.animation.json`
- Textures (10): `src/main/resources/assets/kruemblegard/textures/entity/scaralon_beetle/scaralon_larva.png`, `src/main/resources/assets/kruemblegard/textures/entity/scaralon_beetle/scaralon_beetle_1.png`, `src/main/resources/assets/kruemblegard/textures/entity/scaralon_beetle/scaralon_beetle_2.png`, `src/main/resources/assets/kruemblegard/textures/entity/scaralon_beetle/scaralon_beetle_3.png`, `src/main/resources/assets/kruemblegard/textures/entity/scaralon_beetle/scaralon_beetle_4.png`, `src/main/resources/assets/kruemblegard/textures/entity/scaralon_beetle/scaralon_beetle_5.png`, `src/main/resources/assets/kruemblegard/textures/entity/scaralon_beetle/scaralon_beetle_6.png`, `src/main/resources/assets/kruemblegard/textures/entity/scaralon_beetle/scaralon_beetle_7.png`, `src/main/resources/assets/kruemblegard/textures/entity/scaralon_beetle/scaralon_beetle_8.png`, `src/main/resources/assets/kruemblegard/textures/entity/scaralon_beetle/scaralon_beetle_9.png`
- Loot table: `src/main/resources/data/kruemblegard/loot_tables/entities/scaralon_beetle.json`
- Biome modifiers: `src/main/resources/data/kruemblegard/forge/biome_modifier/add_wayfall_warm_scaralon_beetle_spawns.json`
- Lang keys: entity=`entity.kruemblegard.scaralon_beetle` spawnEgg=`item.kruemblegard.scaralon_beetle_spawn_egg`

#### `scattered_enderman`
- Score: `100`
- Status: geo=✅ tex=✅ anim=✅ ai=✅ loot=✅ sounds=✅ spawns=✅ egg=✅ integration=✅ perf=✅
- Classes: Entity: `ScatteredEndermanEntity` | Renderer: `ScatteredEndermanRenderer` | Model: `ScatteredEndermanModel`
- Geo: `src/main/resources/assets/kruemblegard/geo/scattered_enderman.geo.json`
- Animations: `src/main/resources/assets/kruemblegard/animations/scattered_enderman.animation.json`
- Textures (1): `src/main/resources/assets/kruemblegard/textures/entity/scattered_enderman.png`
- Loot table: `(vanilla)`
- Biome modifiers: `src/main/resources/data/kruemblegard/forge/biome_modifier/add_scattered_enderman_spawns.json`
- Lang keys: entity=`entity.kruemblegard.scattered_enderman` spawnEgg=`item.kruemblegard.scattered_enderman_spawn_egg`
- Audit notes:
  - Uses vanilla sounds (no sounds.json entries expected)

#### `spiral_strider`
- Score: `80`
- Status: geo=✅ tex=✅ anim=✅ ai=✅ loot=⚠️ sounds=⚠️ spawns=⚠️ egg=✅ integration=✅ perf=✅
- Classes: Entity: `SpiralStriderEntity` | Renderer: `SpiralStriderRenderer` | Model: `SpiralStriderModel`
- Geo: `src/main/resources/assets/kruemblegard/geo/spiral_strider.geo.json`
- Animations: `src/main/resources/assets/kruemblegard/animations/spiral_strider.animation.json`
- Textures (6): `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_1.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_2.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_3.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_4.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_5.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_6.png`
- Loot table: **MISSING**
- Biome modifiers: **NONE FOUND**
- Lang keys: entity=`entity.kruemblegard.spiral_strider` spawnEgg=`item.kruemblegard.spiral_strider_spawn_egg`
- TODO: Add entity loot table; Add biome modifier (if should spawn naturally); Confirm vanilla sounds or add custom sounds
- Audit notes:
  - Animation uses "*" wildcard bone (verify GeckoLib compatibility for this file)
  - No sounds.json entries matched this mob id (may rely on vanilla sounds)
  - No entity loot table found under data/.../loot_tables/entities/
  - No biome modifier references found (may not spawn naturally)

#### `trader_beetle`
- Score: `100`
- Status: geo=✅ tex=✅ anim=✅ ai=✅ loot=✅ sounds=✅ spawns=✅ egg=✅ integration=✅ perf=✅
- Classes: Entity: `TraderBeetleEntity` | Renderer: `TraderBeetleRenderer` | Model: `TraderBeetleModel`
- Geo: `src/main/resources/assets/kruemblegard/geo/scaralon_larva.geo.json`, `src/main/resources/assets/kruemblegard/geo/scaralon_beetle.geo.json`
- Animations: `src/main/resources/assets/kruemblegard/animations/scaralon_larva.animation.json`, `src/main/resources/assets/kruemblegard/animations/scaralon_beetle.animation.json`
- Textures (10): `src/main/resources/assets/kruemblegard/textures/entity/scaralon_beetle/scaralon_larva.png`, `src/main/resources/assets/kruemblegard/textures/entity/scaralon_beetle/scaralon_beetle_1.png`, `src/main/resources/assets/kruemblegard/textures/entity/scaralon_beetle/scaralon_beetle_2.png`, `src/main/resources/assets/kruemblegard/textures/entity/scaralon_beetle/scaralon_beetle_3.png`, `src/main/resources/assets/kruemblegard/textures/entity/scaralon_beetle/scaralon_beetle_4.png`, `src/main/resources/assets/kruemblegard/textures/entity/scaralon_beetle/scaralon_beetle_5.png`, `src/main/resources/assets/kruemblegard/textures/entity/scaralon_beetle/scaralon_beetle_6.png`, `src/main/resources/assets/kruemblegard/textures/entity/scaralon_beetle/scaralon_beetle_7.png`, `src/main/resources/assets/kruemblegard/textures/entity/scaralon_beetle/scaralon_beetle_8.png`, `src/main/resources/assets/kruemblegard/textures/entity/scaralon_beetle/scaralon_beetle_9.png`
- Loot table: `(shared: scaralon_beetle)`
- Biome modifiers: **NONE FOUND**
- Lang keys: entity=`entity.kruemblegard.trader_beetle` spawnEgg=`item.kruemblegard.trader_beetle_spawn_egg`
- Audit notes:
  - Uses scaralon_beetle sounds (shared)
  - Loot is shared from scaralon_beetle
  - Spawns are not biome-modifier-driven (intentional)

#### `traprock`
- Score: `90`
- Status: geo=✅ tex=✅ anim=⚠️ ai=✅ loot=⚠️ sounds=✅ spawns=✅ egg=✅ integration=✅ perf=✅
- Classes: Entity: `TraprockEntity` | Renderer: `TraprockRenderer` | Model: `TraprockModel`
- Geo: `src/main/resources/assets/kruemblegard/geo/traprock.geo.json`
- Animations: `src/main/resources/assets/kruemblegard/animations/traprock.animation.json`
- Textures (1): `src/main/resources/assets/kruemblegard/textures/entity/traprock.png`
- Loot table: **MISSING**
- Biome modifiers: `src/main/resources/data/kruemblegard/forge/biome_modifier/add_wayfall_traprock_spawns.json`
- Lang keys: entity=`entity.kruemblegard.traprock` spawnEgg=`item.kruemblegard.traprock_spawn_egg`
- TODO: Add entity loot table
- Audit notes:
  - No entity loot table found under data/.../loot_tables/entities/

#### `treadwinder`
- Score: `80`
- Status: geo=✅ tex=✅ anim=✅ ai=✅ loot=⚠️ sounds=⚠️ spawns=⚠️ egg=✅ integration=✅ perf=✅
- Classes: Entity: `TreadwinderEntity` | Renderer: `TreadwinderRenderer` | Model: `TreadwinderModel`
- Geo: `src/main/resources/assets/kruemblegard/geo/treadwinder.geo.json`
- Animations: `src/main/resources/assets/kruemblegard/animations/treadwinder.animation.json`
- Textures (6): `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_1.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_2.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_3.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_4.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_5.png`, `src/main/resources/assets/kruemblegard/textures/entity/cephalari/cephalari_golem/cephalari_golem_6.png`
- Loot table: **MISSING**
- Biome modifiers: **NONE FOUND**
- Lang keys: entity=`entity.kruemblegard.treadwinder` spawnEgg=`item.kruemblegard.treadwinder_spawn_egg`
- TODO: Add entity loot table; Add biome modifier (if should spawn naturally); Confirm vanilla sounds or add custom sounds
- Audit notes:
  - Animation uses "*" wildcard bone (verify GeckoLib compatibility for this file)
  - No sounds.json entries matched this mob id (may rely on vanilla sounds)
  - No entity loot table found under data/.../loot_tables/entities/
  - No biome modifier references found (may not spawn naturally)

#### `unkeeper`
- Score: `90`
- Status: geo=✅ tex=✅ anim=✅ ai=✅ loot=⚠️ sounds=✅ spawns=✅ egg=✅ integration=✅ perf=✅
- Classes: Entity: `UnkeeperEntity` | Renderer: `UnkeeperRenderer` | Model: `UnkeeperModel`
- Geo: `src/main/resources/assets/kruemblegard/geo/unkeeper.geo.json`
- Animations: `src/main/resources/assets/kruemblegard/animations/unkeeper.animation.json`
- Textures (1): `src/main/resources/assets/kruemblegard/textures/entity/unkeeper.png`
- Loot table: **MISSING**
- Biome modifiers: `src/main/resources/data/kruemblegard/forge/biome_modifier/add_wayfall_unkeeper_spawns.json`
- Lang keys: entity=`entity.kruemblegard.unkeeper` spawnEgg=`item.kruemblegard.unkeeper_spawn_egg`
- TODO: Add entity loot table
- Audit notes:
  - No entity loot table found under data/.../loot_tables/entities/

#### `wyrdwing`
- Score: `90`
- Status: geo=✅ tex=✅ anim=✅ ai=✅ loot=⚠️ sounds=✅ spawns=✅ egg=✅ integration=✅ perf=✅
- Classes: Entity: `WyrdwingEntity` | Renderer: `WyrdwingRenderer` | Model: `WyrdwingModel`
- Geo: `src/main/resources/assets/kruemblegard/geo/wyrdwing.geo.json`
- Animations: `src/main/resources/assets/kruemblegard/animations/wyrdwing.animation.json`
- Textures (3): `src/main/resources/assets/kruemblegard/textures/entity/wyrdwing/wyrdwing_1.png`, `src/main/resources/assets/kruemblegard/textures/entity/wyrdwing/wyrdwing_2.png`, `src/main/resources/assets/kruemblegard/textures/entity/wyrdwing/wyrdwing_3.png`
- Loot table: **MISSING**
- Biome modifiers: `src/main/resources/data/kruemblegard/forge/biome_modifier/add_basin_of_scars_wyrdwing_spawns.json`
- Lang keys: entity=`entity.kruemblegard.wyrdwing` spawnEgg=`item.kruemblegard.wyrdwing_spawn_egg`
- TODO: Add entity loot table
- Audit notes:
  - No entity loot table found under data/.../loot_tables/entities/

<!-- AUTO-GENERATED:MOBS:END -->
