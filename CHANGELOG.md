# Changelog

All notable changes to this project will be documented in this file.

Changelog entries are grouped by the exact mod version embedded in the built jar.

## Unreleased
- Mobs: add Traprock (dormant until disturbed; Blaze-derived GeckoLib mob with editable geo/anim/texture).
- Mobs: add Pebblit (hostile; tameable with cobblestone; follows owner).
- Gameplay: remove Mimic Waystone block (Traprock is spawned via spawn egg/commands for now).
- Dimension: add Wayfall (`kruemblegard:wayfall`) with End-like baseline terrain and custom Wayfall biomes.
- Worldgen: enrich Wayfall biomes (ambience + small biome features) and restrict Wayfall spawns to Krümblegård mobs only.
- Worldgen: add Attuned Ore (Wayfall-only) that drops Attuned Rune Shards (Fortune affects drops).
- Worldgen: add Wayfall plants (Wispstalk, Gravevine, Echocap, Runebloom, Soulberry Shrubs) + Ghoulberry corruption.
- Blocks/items: expand Wayfall wood sets with full vanilla-style wood-family blocks (stairs/slabs/fences/gates/doors/trapdoors/buttons/pressure plates) including recipes + loot.
- Assets: give each Wayfall wood-family block its own dedicated texture file (placeholders copied from planks for now).
- Blocks: Wayfall saplings grow into their matching worldgen configured features (random ticks + bonemeal).
- Blocks/items: add initial scaffolding for new Wayfall staple flora (Voidfern, Runeblossom, Moteshrub, Ashveil, Twilight Bulb, Whispervine).
- Blocks/items: add new Wayfall staple wood sets (Ashbloom, Glimmerpine, Driftwood) and register simple tree Features.
- Worldgen: wire Wayfall staple flora patches + staple trees into Wayfall biomes via configured/placed features + biome modifiers (global staples + biome-specific sets).
- Worldgen: replace staple tree worldgen with Arboria-derived vanilla tree feature templates (multiple variants per species) while swapping in Kruemblegård logs/leaves.
- Blocks: make all Wayfall saplings grow into their matching Arboria-derived tree feature variants (configured-feature selectors), matching natural worldgen.
- Worldgen: distribute Wayfall trees + plants per biome via `data/forge/biome_modifier/add_wayfall_*_flora.json` (matching `docs/WAYFALL_BIOMES.md`).
- Worldgen: replace `#arboria:tree_branch_replaceable` references with `#kruemblegard:tree_branch_replaceable` and define the new block tag.
- Loot: add loot tables for Wayfall staple flora + the three staple wood sets.
- Assets: add placeholder blockstates + item models for Wayfall staples to avoid missing-model rendering.
- Dev tooling: add workspace JSON schema overrides to reduce false-positive loot table/blockstate validation errors.
- Items: Runic tools upgraded to a custom tier above Netherite; recipes now use Attuned Rune Shards + Runic Core.
- Advancements: remove obsolete survived/cleansed advancements tied to removed encounter flow.
- Docs/Codex: update guidebook + docs to match Traprock behavior.
- Fix: resolve “Failed to load registries” on world creation by correcting Wayfall `dimension_type` fields and worldgen feature JSON codecs (including `minecraft:disk` target predicate format).
- Fix: resolve “Failed to load registries” by adding missing `kruemblegard:ashbloom/branch_set/2` configured feature referenced by Ashbloom tree variants.
- Fix: resolve “Failed to load registries” by fixing `minecraft:disk` configured feature schema (`state_provider` is rule-based; `target` is a block predicate) and by correcting sapling selector features to include `placement` entries.
- Fix: register on-ground spawn placements for Wayfall mobs so Traprock/Pebblit don’t spawn in midair/void.

## 1.0.110 (2026-01-03)
- Docs/workflow: update Copilot instructions to require updating all relevant docs.
- Docs: add `docs/MOD_FEATURES.md` as the complete mod feature reference.

## 1.0.108 (2026-01-03)
- Items: grant the Crumbling Codex once per player on first join.
- Items: move Crumbling Codex page text to `data/kruemblegard/books/crumbling_codex.json` for easy edits.

## 1.0.107 (2026-01-03)
- Boss: phase-based bone hiding (Phase 2 hides armor, Phase 3 hides `-p3` bones, Phase 4 hides whirlwind/debris bones).
- Boss: Phase 4 uses a separate texture placeholder (`kruemblegard_phase4.png`).
- Boss: awards Ender Dragon XP on death.
- Loot: Krümblegård drops a new `runic_core` item.
- Animations: add Phase 4-only idle/move animation slots (`idle_phase4`, `move_phase4`).
- Animations: add phase transition one-shots (`phase2_transition`, `phase3_transition`, `phase4_transition`).
- Items: add `runic_core` and a full Runic tool set (sword/pickaxe/axe/shovel/hoe); remove `runic_core_fragment`.
- Items: Crumbling Codex now opens as a pre-filled guidebook (and appears filled in creative tabs).
- Remove `radiant_essence`.
- Remove `false_waystone` (block + worldgen + biome modifier/tag).
- UI: enhanced custom boss bar (icon + overlay + hit-flash), still fully texture-driven.

## 1.0.104 (2026-01-03)
- Animations: remove unused `animation.kruemblegard.attack` so Blockbench only shows the 12 attacks + idle/move/hurt/death.

## 1.0.103 (2026-01-03)
- Animations: remove legacy unused `boss_attack_*` entries so Blockbench only shows the active animation set.

## 1.0.102 (2026-01-03)
- Animations: add missing `attack_*` boss attack animation entries (placeholders) for Blockbench editing.
- Boss/docs: switch boss attack animations from `boss_attack_*` back to the `attack_*` keys.

## 1.0.101 (2026-01-03)
- Animations: rename boss attack animation keys to a consistent `boss_attack_*` naming scheme.
- Boss/docs: update references to the renamed animations.

## 1.0.100 (2026-01-03)
- Animations: fix duplicate animation-key collisions by renaming the four overlapping attack animations to unique boss-specific keys.

## 1.0.99 (2026-01-03)
- Animations: rename the 12 boss attack animations to ability-specific keys (e.g. `attack_cleave`, `attack_rune_bolt`, etc.).
- Boss/docs: update code + documentation to reference the renamed animations.

## 1.0.98 (2026-01-03)
- Docs: clarify how `totalTicks`/`impactAt` maps to Blockbench impact frames.

## 1.0.97 (2026-01-03)
- Docs: refresh config snippet and correct changelog date.
- Animations: update the JSON controller example to reference the new attack animation names.

## 1.0.96 (2026-01-03)
- Boss: add Phase 4 and expand to 12 unique attacks (fast/heavy/ranged per phase).
- Boss: add 12 new GeckoLib attack animations.
- Docs: update boss attack timings/animation names.

## 1.0.95 (2026-01-03)
- Boss: each phase now uses a fast/heavy/ranged attack kit (less close-range bias).

## 1.0.94 (2026-01-03)
- Boss: minor geo + animation JSON tweaks.

## 1.0.93 (2026-01-03)
- Update Kruemblegård boss GeckoLib animation definitions.
- Docs: restore missing changelog version sections.

## 1.0.92 (2026-01-03)
- CI/versioning: ensure jar version matches GitHub Actions by using full-history checkout and a numeric `1.0.<commitCount>` version.

## 1.0.91 (2026-01-03)
- Versioning/docs: add versioned changelog sections and deterministic jar versioning.

## 1.0.90 (2026-01-03)
- Fix `kruemblegard.animation.json` malformed JSON that could crash client startup (GeckoLib animation loader).
- Update Kruemblegård boss model UVs/armor geometry and refresh the entity texture.
- Workflow: clarify that “push” includes stage+commit+push when the working tree is dirty.
- Further tweak Kruemblegård boss geo UV mappings/armor pieces.
- Update Kruemblegård boss GeckoLib animation definitions.

