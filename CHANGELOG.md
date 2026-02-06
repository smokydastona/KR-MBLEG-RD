# Changelog

All notable changes to this project will be documented in this file.

Changelog entries are grouped by the exact mod version embedded in the built jar.

## 1.0.799 (2026-02-06)
- Feature(worldgen/wayfall): add Ashspire Cactus clusters to Strata Collapse + Fracture Shoals (rare Emberbloom variant) and a very rare Giant Ashspire Colossus landmark in Strata Collapse.
- Content(loot/items): add Ashspire drops (Needles, Volatile Pulp, Shards, Volatile Resin, Night Flower).

## 1.0.791 (2026-02-06)
- Tuning(worldgen/wayfall): overhaul Wayfall tree distribution: limit global trees to Ashbloom saplings + Wayroot + Echowood; move biome “signature” trees to per-biome injectors; enforce no trees in Strata Collapse, Fracture Shoals, and Basin of Scars.
- Tuning(worldgen/mushrooms): stop injecting vanilla huge mushrooms into Wayfall biomes.

## 1.0.796 (2026-02-06)
- UX(creative): split the mod creative tab into three tabs (Building Blocks, Natural Blocks, Miscellaneous) and sort entries to keep variant families together.

## 1.0.790 (2026-02-06)
- Feature(worldgen/ashbloom): Ashbloom now generates as small plants on Ashmoss (rarely flowering), and requires bonemeal to grow into a tree (no random tick growth).

## 1.0.789 (2026-02-04)
- Chore(ci): trigger GitHub Actions build (no gameplay/content changes).

## 1.0.788 (2026-02-04)
- Fix(textures): Glimmerpine leaves now use the correct texture (was incorrectly pointing at Driftwood leaves).

## 1.0.787 (2026-02-04)
- Chore(textures): resize a small set of Driftwood + Hollowway Tree block textures to 256x256 (no cropping; non-square textures skipped).

## 1.0.786 (2026-02-03)
- Content(textures): update Driftwood + Hollowway Tree door/trapdoor textures.

## 1.0.785 (2026-02-02)
- Content(textures): update `driftwood_log_top` and `driftwood_planks` textures.

## 1.0.784 (2026-02-02)
- Chore(tools): remove the redundant item-only texture resizer; use `tools/resize_textures_to_256.ps1` for both block + item textures.

## 1.0.783 (2026-02-02)
- Chore(textures): resize additional Driftwillow block textures to 256x256 (no cropping; non-square textures skipped).
- Chore(tools): add an item-only texture resizer (`tools/resize_item_textures_to_256.ps1`) with the same flags/reporting as the main resizer.

## 1.0.780 (2026-02-02)
- Chore(textures): resize additional eligible square block textures to 256x256 (no cropping; non-square textures skipped).

## 1.0.782 (2026-02-02)
- Chore(textures): resize additional eligible square block textures to 256x256 (no cropping; non-square textures skipped).

## 1.0.769 (2026-01-31)
- Feature(wood): add sign + hanging sign blocks/items and boat + chest boat items for all 14 custom Wayfall wood families (with recipes and placeholder models/textures so everything loads before bespoke art).
- Chore(resources): remove unused dedicated `*_button.png` block textures (buttons use their matching plank textures via models).
- Fix(mobs): Scattered Enderman now switches to carry-walk animation while moving with a carried block.

## 1.0.758 (2026-01-30)
- Tuning(worldgen/echowood): Echowood schematic “processors” now map red wool markers to Echowood franch wood, tinted glass to structure void, force non-persistent leaves, and convert generic log/wood blocks into 90% franch wood / 10% franch planks.

## 1.0.766 (2026-01-31)
- Tuning(worldgen/wayroot): Wayroot schematic pivot markers (`minecraft:red_wool`) now remap to `wayroot_franch_wood` (normal + mega), matching the standard for future schematic trees.
- Chore(textures): resize eligible square block/item textures to 256x256 (no cropping; non-square textures skipped).

## 1.0.767 (2026-01-31)
- Fix(worldgen/leaves): include `*_franch_planks` in each species' `*_logs` (anchor) tag so the 90/10 trunk mix doesn't disconnect leaf networks and trigger immediate decay.

## 1.0.759 (2026-01-31)
- Tuning(i18n): rename all `*_franch_wood` blocks to “<Tree Type> Tree Trunk” and align `*_franch_planks` display names to match.

## 1.0.760 (2026-01-31)
- Fix(compat/treeharvester): Tree Harvester cleanup now clears `*_franch_planks` blocks (even though they are tagged as `minecraft:logs` for tree detection).
- Fix(resources): repair invalid JSON in `data/kruemblegard/tags/blocks/tree_harvester_leaf_like.json`.

## 1.0.761 (2026-01-31)
- Compat(treeharvester): add a safe manual fallback so Krümblegård trees can still be felled (skips trees with `persistent=true` leaves).

## 1.0.765 (2026-01-31)
- Docs(worldgen/schematics): add a dedicated schematic-tree implementation guide (`docs/TREE_SCHEMATICS.md`) to keep new tree schematics consistent.

## 1.0.764 (2026-01-31)
- Fix(worldgen/wayroot): Wayroot schematic palette remaps now convert fence / fence gate / trapdoor placeholders into Wayroot `*_franch*` helper blocks (instead of leaving craftable fence/trapdoor blocks embedded).

## 1.0.763 (2026-01-31)
- Fix(worldgen/wayroot): Wayroot schematic palette remaps now convert fence / fence gate / trapdoor placeholders into trunk blocks so trees don't spawn with those blocks embedded.
- Tuning(worldgen/wayroot): Wayroot schematic trunks now use the same 90% `wayroot_franch_wood` / 10% `wayroot_franch_planks` mix as Echowood.

## 1.0.762 (2026-01-31)
- Fix(compat/treeharvester): tag Krümblegård giant fungus caps/cap-slabs as `minecraft:leaves` so Tree Harvester recognizes and clears them even with non-vanilla cap map colors.
- Tuning(compat/treeharvester): manual fallback trigger now tolerates Tree Harvester sneak/axe config (requires either sneaking or holding an axe).

## 1.0.757 (2026-01-30)
- Fix(worldgen/echowood): repair malformed Echowood configured feature JSONs (`echowood/1..5`) that could crash world creation with registry parsing errors.

## 1.0.756 (2026-01-30)
- Tuning(worldgen/echowood): bias natural Echowood selection toward living schematics while keeping dead variants available.

## 1.0.755 (2026-01-30)
- Feature(worldgen/echowood): migrate Echowood (normal + mega) from procedural tree features to schematic-driven placement with Wayroot-style palette remaps into Echowood Franch blocks.

## 1.0.754 (2026-01-30)
- Fix(tags): add all `*_franch_planks` blocks to vanilla `minecraft:logs` and `minecraft:logs_that_burn`.

## 1.0.753 (2026-01-30)
- Docs(compat/treeharvester): update the Tree Harvester notes to reflect Franch helper blocks now living in vanilla `minecraft:leaves`.

## 1.0.752 (2026-01-30)
- Fix(tags): add all non-wood/non-planks `*_franch*` blocks to vanilla `minecraft:leaves`.

## 1.0.748 (2026-01-30)
- Fix(tags): add all `*_franch_wood` blocks to vanilla `minecraft:logs` and `minecraft:logs_that_burn`.

## 1.0.747 (2026-01-30)
- Fix(tags): add `kruemblegard:red_mushroom_block_slab` and `kruemblegard:brown_mushroom_block_slab` to vanilla `minecraft:wart_blocks`.
- Fix(client/maps): giant fungi caps/cap-slabs now use species-specific `MapColor` so Xaero's minimap/world map shows distinct colors per mushroom type.
- Fix(compat/harvestmods): giant mushroom cleanup now breaks `HugeMushroomBlock` caps (non-stem) without relying on cap map colors.

## 1.0.735 (2026-01-30)
- Compat(treeharvester): start moving Franch harvesting toward native Tree Harvester behavior (vanilla-tag driven).

## 1.0.741 (2026-01-30)
- Fix(compat/treeharvester): stop tagging Franch building blocks as vanilla `minecraft:leaves`; Tree Harvester now clears Franch helper blocks via `#kruemblegard:tree_harvester_leaf_like`.
- Fix(compat/treeharvester): stop tagging `*_franch_planks`/`*_franch_wood` as vanilla `minecraft:logs`/`minecraft:logs_that_burn` (prevents harvest mods from treating building blocks as trees).
- Fix(compat/mushrooms): Tree Harvester now clears mushroom cap slab blocks via `#kruemblegard:tree_harvester_mushroom_cap_slabs`.
- Compat(fallingtree): giant fungi stems are in `minecraft:logs` and caps/cap slabs are in `minecraft:wart_blocks`.

## 1.0.742 (2026-01-30)
- Fix(compat/fallingtree): when FallingTree fells a tree, Krümblegård now clears Franch helper blocks and relocates nearby drops to the player.
- Fix(tags): expand `#kruemblegard:tree_harvester_leaf_like` to include all Franch schematic variants (gate/stairs/slab/trapdoor).

## 1.0.736 (2026-01-30)
- Compat(treeharvester): native vanilla tagging for Franch blocks: `*_franch_wood` + `*_franch_planks` are now in `minecraft:logs`/`minecraft:logs_that_burn`, and all other Franch blocks are now in `minecraft:leaves`.

## 1.0.737 (2026-01-30)
- Chore(compat/treeharvester): remove leftover `kruemblegard:treeharvester_clear` tag plumbing (now unnecessary since Franch is tagged via vanilla `minecraft:leaves`/`minecraft:logs`).

## 1.0.738 (2026-01-30)
- Chore(resources): delete obsolete `data/kruemblegard/tags/blocks/treeharvester_clear.json` (no longer used).

## 1.0.739 (2026-01-30)
- Chore(docs): clean up Tree Harvester changelog notes to match the current vanilla-tag approach.
## 1.0.740 (2026-01-30)
- Chore(resources): delete obsolete `data/kruemblegard/tags/blocks/franch_branches.json` (no longer referenced).

## 1.0.734 (2026-01-30)
- Fix(compat/treeharvester): Tree Harvester cleanup logic improvement (superseded by later native vanilla-tag approach).
- Fix(compat/treeharvester): restore all mushroom slab blocks (giant fungus cap slabs + red/brown mushroom block slabs) in `minecraft:leaves` so Tree Harvester clears them consistently.

## 1.0.733 (2026-01-30)
- Fix(compat/treeharvester): `minecraft:leaves` is no longer polluted with non-leaf Franch building blocks; only branch helper blocks are treated as leaves via `#kruemblegard:franch_branches`.
- Fix(blocks/franch): Franch/leaves distance anchoring is now matching-species (Franch only stays valid when connected to the correct `*_logs` / `*_franch_logs` tag).

## 1.0.732 (2026-01-30)
- Fix(worldgen/schematics): schematics now require valid solid ground under their bottom-layer footprint and won't spawn on leaves/logs/huge mushroom blocks; plants remain replaceable and support columns can replace plants.

## 1.0.731 (2026-01-30)
- Fix(worldgen/schematics): support-beard now uses only the schematic bottom layer footprint and only supports the perimeter, so terrain isn't filled inside the structure.

## 1.0.730 (2026-01-30)
- Chore(assets/textures): resize a small set of square block textures to 256x256 (no cropping).
- Chore(tools): `tools/resize_textures_to_256.ps1` now keeps only the newest reports by default (`*_latest.txt`).

## 1.0.729 (2026-01-30)
- Tuning(blocks/franch_planks): franch planks now behave like normal planks (no decay), pick-block returns the matching normal plank, and default drops are 50/50 planks vs stick.
- Tuning(loot/leaves): halve sapling + stick drop rates for all 14 Wayfall leaf blocks.
- Feature(blocks/mega_franch_leaves): add schematic-only `*_mega_franch_leaves` with half the (new) normal Wayfall leaf drop chances; mega Wayroot schematics place these leaves instead of normal leaves.

## 1.0.728 (2026-01-30)
- Tuning(worldgen/schematics): support “beard” now samples nearby terrain blocks so the fill matches the local biome palette instead of defaulting to dirt.

## 1.0.727 (2026-01-30)
- Tuning(worldgen/schematics): add a short soil support “beard” under schematic-placed Wayroot trees (regular + mega) and giant mushrooms so bases don’t overhang uneven terrain.

## 1.0.726 (2026-01-30)
- Compat(treeharvester): when Tree Harvester harvests a tree, Kruemblegård immediately clears nearby leaves + all franch helper blocks and relocates the resulting drops to the harvesting player’s feet.

## 1.0.725 (2026-01-30)
- Feature(blocks/franch_wood): add schematic-only `*_franch_wood` trunk blocks (one per wood family, including vanilla woods) that look/names-match normal wood but have special drops.
- Tuning(worldgen/wayroot): schematic trunk blocks now remap to `wayroot_franch_wood` instead of random 50/50 `wayroot_log`/`wayroot_wood` per placed block.

## 1.0.724 (2026-01-30)
- Fix(blocks/franch): all franch blocks now drop nothing (no more stick drops).
- Fix(blocks/string_franch): string franch is now non-colliding and fully invisible in-world, with a dedicated transparent texture.

## 1.0.723 (2026-01-30)
- Content(textures): update Runebloom, Wayburn Fungus, Wayglass leaves, and Wispstalk textures.

## 1.0.722 (2026-01-30)
- Content(textures): update multiple leaf textures (Ashbloom/Cairn Tree/Driftwillow/Driftwood/Fallbark/Glimmerpine/Hollowway Tree/Monument Oak).

## 1.0.721 (2026-01-30)
- Docs(changelog): align version entries for 1.0.719–1.0.720 after pushing a pending local commit.

## 1.0.720 (2026-01-30)
- Fix(worldgen/wayroot): mega Wayroot schematic placement now uses the same palette remaps as normal Wayroot trees (skip `minecraft:red_wool` markers, remap `minecraft:tripwire` → `kruemblegard:string_franch`, and convert wood construction blocks into Wayroot franch variants).

## 1.0.719 (2026-01-30)
- Content(textures): update `driftwillow_leaves` texture.

## 1.0.718 (2026-01-30)
- Docs(changelog): record the 1.0.717 Scattered Enderman animation tweak.

## 1.0.717 (2026-01-30)
- Tuning(mobs/scattered_enderman): carry-block animation now includes leg motion so movement reads correctly while carrying.

## 1.0.716 (2026-01-30)
- Fix(mobs/scattered_enderman): adjust the `held_block` bone pivot so carried blocks align correctly in-hand.

## 1.0.715 (2026-01-30)
- Chore(assets/textures): resize all square block + item textures to 256x256 (no cropping); non-square textures are intentionally left unchanged.

## 1.0.714 (2026-01-30)
- Chore(tools): rename the texture resizer to `tools/resize_textures_to_256.ps1` and expand it to process both `textures/block` and `textures/item`.

## 1.0.713 (2026-01-30)
- Chore(tools): remove obsolete `tools/resize_fallbark_textures.ps1` (replaced by the block-wide resizer).

## 1.0.712 (2026-01-30)
- Chore(tools): add `tools/resize_block_textures_to_256.ps1` to resize all square block textures to 256x256 (no cropping) and report non-square textures.

## 1.0.711 (2026-01-30)
- Chore(assets/textures): remove unused wood component textures (`*_slab`, `*_stairs`, `*_pressure_plate`, `*_fence`, `*_fence_gate`) that were not referenced by any block models (these blocks reuse their `*_planks` textures).

## 1.0.707 (2026-01-29)
- Content(lang): all franch blocks now share the display name "Branch".

## 1.0.706 (2026-01-29)
- Tuning(worldgen/wayroot): schematic placement now randomizes trunk blocks 50/50 between `wayroot_log` and `wayroot_wood` per placed block.

## 1.0.705 (2026-01-29)
- Fix(blocks): `string_franch` now drops nothing (it is intended purely as a schematic helper block).
- Compat(worldgen/wayroot): schematic placement now skips `minecraft:red_wool` markers (center point helpers) and more robustly forces schematic leaves to be non-persistent Wayroot leaves.

## 1.0.704 (2026-01-29)
- Feature(blocks): add schematic-only `string_franch` (leaf-style decay/connectivity), intended as a tree-schematic helper.
- Compat(worldgen/wayroot): Wayroot schematic placement now remaps `minecraft:tripwire` to `kruemblegard:string_franch` so string placeholders can be used in tree templates.

## 1.0.703 (2026-01-29)
- Fix(client/render): Kruemblegard leaves now render with vanilla-style cutout transparency again (cutout_mipped).

## 1.0.701 (2026-01-29)
- Feature(worldgen/wayroot): normal Wayroot trees are now fully schematic-driven using 5 regular templates (3 living + 2 dead, living weighted slightly higher).
- Feature(worldgen/wayroot): Wayroot saplings now grow only the 3 living schematic variants.
- Compat(worldgen/wayroot): schematic placement remaps wood/leaves to Wayroot + Wayroot franch blocks, converts tinted glass to structure-void (skips placement), and forces leaves to be non-persistent.

## 1.0.700 (2026-01-29)
- Tuning(blocks): extend Franch + Kruemblegard leaves decay/connectivity distance from 7 → 10; franch blocks extend the log-connectivity graph but never act as distance-0 anchors.

## 1.0.699 (2026-01-30)
- Feature(blocks): add command/schematic-only “Franch” planks/slabs/stairs/trapdoors (one per wood type, including vanilla woods) with leaf-style decay and leaf-style stick drops.
- Compat(treeharvester): include franch planks/slabs/stairs/trapdoors in `minecraft:leaves` so they count as leaves for proximity checks.

## 1.0.698 (2026-01-29)
- Feature(blocks): add vanilla-wood command/schematic-only "Franch" fences and fence gates (oak/spruce/birch/jungle/acacia/dark oak/mangrove/cherry) with leaf-style decay and leaf-style stick drops.
- Compat(treeharvester): include vanilla-wood franch fences and gates in `minecraft:leaves` so they count as leaves for proximity checks.

## 1.0.697 (2026-01-29)
- Feature(blocks): add command/schematic-only "Franch" fence gates (one per wood type) that behave like fence gates but decay like leaves and use leaf-style stick drops.
- Compat(treeharvester): include franch fence gates in `minecraft:leaves` so they count as leaves for proximity checks.

## 1.0.696 (2026-01-29)
- Feature(blocks): add command/schematic-only "Franch" fences (one per wood type) that behave like fences but decay like leaves and drop sticks when broken.
- Tuning(blocks): Franch fences now use leaf-style stick drops (fortune-scaled chance) and are included in `minecraft:leaves` so Tree Harvester treats them like leaves.

## 1.0.695 (2026-01-29)
- Compat(treeharvester): include giant fungi cap slabs and red/brown mushroom block slabs in `minecraft:leaves` so Tree Harvester also breaks slab cap blocks during harvest.

## 1.0.694 (2026-01-29)
- Compat(worldgen): schematic-placed mega Wayroot trees now place leaves as non-persistent so Tree Harvester doesn't treat them as player-made by default.
- Compat(fungi): giant fungi caps/stems now use vanilla huge-mushroom map colors (caps: `DIRT`, stems: `WOOL`) so Tree Harvester can harvest them when huge mushrooms are enabled.

## 1.0.693 (2026-01-29)
- Content(textures): update leaf textures for Echowood and Wayroot.

## 1.0.683 (2026-01-29)
- Content(particles): Giant Wayburn Fungus Cap now drips lava particles from its underside; Giant Griefcap Cap drips water; Giant Wayrot Fungus Cap emits Spore Blossom-style particles.

## 1.0.682 (2026-01-29)
- Tuning(blocks): Giant Echo Puff Cap and Giant Wayburn Fungus Cap now emit light level `4` only when at least one cap face is an "inside" face (matches the vanilla cap/inside state logic).

## 1.0.681 (2026-01-29)
- Tuning(blocks): Wayburn Fungus and Echo Puff now emit a small light level (`3`).

## 1.0.680 (2026-01-29)
- Fix(assets/blocks): custom giant fungi cap blocks now render like vanilla mushroom blocks (face-aware “cap vs inside” using `HugeMushroomBlock` state + multipart models).

## 1.0.678 (2026-01-29)
- Fix(worldgen/fungi): schematic-based huge mushrooms now restore mushroom-cap face states after placement so the “inside” texture only appears on the underside (and cap/slab bottom faces consistently use vanilla `minecraft:block/mushroom_block_inside`).

## 1.0.679 (2026-01-29)
- Content(textures): update a batch of block textures (giant fungi caps/stems, plus Splinterspore and Wayroot blocks).

## 1.0.677 (2026-01-28)
- Fix(worldgen/fungi): schematic-based giant mushrooms now load schematics correctly during world generation (support `WorldGenLevel`), not just during bonemeal growth.

## 1.0.676 (2026-01-28)
- Tuning(worldgen/fungi): increase natural generation frequency for all 9 custom giant fungi (placed feature rarity filter `chance` 64 → 32).
- Feature(worldgen/fungi): expand vanilla huge red/brown mushroom natural generation to additional vanilla biomes where they normally appear (Dark Forest + Old Growth variants), while keeping the Wayfall injections.

## 1.0.675 (2026-01-28)
- Feature(worldgen/fungi): add natural worldgen placement for huge red/brown mushrooms in Mushroom Fields and selected Wayfall biomes.

## 1.0.674 (2026-01-28)
- Fix(worldgen/fungi): center giant mushroom schematics on the placement origin (bonemeal + worldgen now place centered).

## 1.0.673 (2026-01-28)
- Fix(worldgen/fungi): giant mushroom schematic placement no longer snaps to a heightmap; it now places relative to the provided origin (restores giant mushroom spawning).

## 1.0.672 (2026-01-28)
- Feature(worldgen/fungi): vanilla huge red/brown mushrooms now use the same schematic-based generator (shared 30 templates + palette remaps) as Kruemblegard giant fungi when generating in the overworld.
- Feature(blocks): add `red_mushroom_block_slab` + `brown_mushroom_block_slab` for schematic integration.

## 1.0.671 (2026-01-28)
- Tuning(blocks): Twilight Bulb light level adjusted to `8`.

## 1.0.670 (2026-01-28)
- Tuning(blocks): Twilight Bulb now emits torch-level light.

## 1.0.669 (2026-01-28)
- Fix(assets/blocks): Twilight Bulb now points at its own model/texture (and correct the texture filename typo).

## 1.0.668 (2026-01-28)
- Feature(worldgen/fungi): replace vanilla huge-mushroom giant fungi generation with schematic-based variants (30 shared templates) using palette remaps (tinted glass → air; quartz block → stem; quartz slab → cap slab; brown mushroom block → cap).

## 1.0.667 (2026-01-28)
- Feature(fungi): add slab variants for all giant fungi cap blocks (caps only; stems unchanged) with recipes, loot tables, and models.

## 1.0.666 (2026-01-28)
- Fix(content/lang): rename giant fungi cap blocks to use “Block” instead of “Cap” (stems unchanged).

## 1.0.665 (2026-01-28)
- Fix(mobs/moogloom): griefcaps now render at the correct height on Moogloom back/head (no more floating caps).

## 1.0.663 (2026-01-28)
- Fix(worldgen/wayroot): mega Wayroot schematic placement now snaps to the surface heightmap and centers/rotates correctly; also allows the trunk to replace soil at the base so schematics no longer spawn missing most blocks.
- Chore(worldgen/wayroot): include the third example mega Wayroot schematic variant in the schematic-only mega selector.

## 1.0.662 (2026-01-28)
- Fix(worldgen/wayroot): replace mega Wayroot baobab generation with schematic-based variants (2 curated `.schem` shapes) to stop chunk-load leaf decay/drop spam.
- Fix(worldgen/wayroot): Wayroot normal tree cores now generate `wayroot_leaves` as `persistent=true` to prevent occasional post-gen leaf loss.

## 1.0.661 (2026-01-28)
- Fix(worldgen/wayroot): normal Wayroot trees now select the leafy baobab core features directly (avoids the multipart selector occasionally picking branch-only pieces).

## 1.0.660 (2026-01-28)
- Fix(worldgen/wayroot): stop some Wayroot baobab trees spawning leafless by restoring `wayroot_leaves` as the `foliage_provider` in the core configured features.

## 1.0.659 (2026-01-28)
- Tuning(worldgen/fungi): diversify giant fungi silhouettes by mixing `huge_red_mushroom`/`huge_brown_mushroom` generators and varying `foliage_radius` per species/variant.
- Fix(mobs/moogloom): allow Mooglooms to spawn on Wayfall custom ground blocks (spawn placement no longer requires `minecraft:animals_spawnable_on`).

## 1.0.658 (2026-01-28)
- Fix(worldgen/wayroot): stop world creation crash by replacing invalid/fragile `wayroot/baobab_mega/{1,2,3}` configured feature definitions with canonical `minecraft:tree` configured features.

## 1.0.657 (2026-01-28)
- Feature(fungi): add 9 Wayfall fungi with bonemeal growth into giant variants (Black Echo Fungus, Echocap, Echo Puff, Griefcap, Static Fungus, Voidcap Briar, Wayburn Fungus, Wayrot Fungus, Memory Rot).
- Feature(worldgen/fungi): add natural giant fungi generation in the same biomes as their small variants; additionally include all giant fungi except Wayburn in Shatterplate Flats.
- Content(assets): add giant stem/cap blocks + items with blockstates/models and visually distinct placeholder textures.
- Content(loot/lang): add block loot tables and `en_us` entries for all giant fungi blocks.

## 1.0.656 (2026-01-28)
- Fix(worldgen/trees): correct `would_survive` sapling gating for normal (non-mega) tree configured features so each species checks its own sapling (prevents cross-species driftwood-sapling gating).
- Tuning(worldgen/trees): reduce normal-tree repeated placement counts (outer placement counts and internal attempt counts) to cut down on over-stacking/log tangles and improve silhouettes.

## 1.0.655 (2026-01-28)
- Tuning(worldgen/wayroot): reshape mega Wayroot baobab silhouettes (less core spam, fuller crown layering, and slightly denser branches) to better match the Arboria-style examples while staying within leaf-distance safety.

## 1.0.654 (2026-01-28)
- Tuning(worldgen/mega_trees): reshape the non-conifer mega tree families to read as real “trees” by switching their mega canopies away from `mega_pine_foliage_placer` to fuller `fancy_foliage_placer` crowns.
- Tuning(worldgen/mega_trees): reduce over-spammy branch placement counts (e.g. 16 → 8) so mega variants stop turning into chaotic log tangles.
- Fix(worldgen/mega_trees): remove redundant internal sapling-gating + `count: 64` placement blocks that were embedded inside several mega configured features (these belong in placed features / were copied from reference packs).
- Fix(worldgen/ashbloom): stop mega Ashbloom from generating leafless trees by restoring real leaf providers/placers for the `/3` mega path.
- Tuning(worldgen/wayglass): upgrade `wayglass/4` into a true mega (2×2 trunk + larger canopy + leaf-attachment fullness) so both mega selector routes look correct.

## 1.0.653 (2026-01-28)
- Tuning(worldgen/glimmerpine): rework mega Glimmerpine canopy to use `mega_pine_foliage_placer` + a light `attached_to_leaves` decorator so mega variants read as real conifers instead of tall bare trunks.

## 1.0.652 (2026-01-28)
- Fix(worldgen): remove UTF-8 BOM encoding from a batch of worldgen JSONs so Forge/Minecraft can decode them reliably.
- Fix(worldgen/ashbloom): fix variant 4 generating “leafless” trees by restoring `ashbloom_leaves` as the `foliage_provider`.

## 1.0.651 (2026-01-28)
- Fix(worldgen/wayroot): stop mega Wayroot baobab leaves from decaying on chunk load by switching mega variants back to the multipart (core + canopy + branches) setup and reducing the largest mega canopy radius.

## 1.0.650 (2026-01-26)
- Fix(worldgen): stop registry loading crash by keeping `giant_trunk_placer` height parameters within the `[0..32]` codec bounds for glimmerpine and Wayroot baobab mega configured features.

## 1.0.649 (2026-01-26)
- Fix(worldgen): stop world creation crash by simplifying a handful of mega-tree configured features to plain `minecraft:tree` definitions (removes nested inline placed-feature selectors that Forge is failing to decode).

## 1.0.648 (2026-01-26)
- Fix(worldgen): prevent world creation crash by removing invalid top-level placement modifiers embedded inside several mega tree configured features (these modifiers belong in placed features).
- Fix(worldgen/wayglass): stop `wayglass/4` from incorrectly gating on `driftwood_sapling`.

## 1.0.645 (2026-01-26)
- Content(textures): update leaf textures for ashbloom, faultwood, glimmerpine, hollowway_tree, monument_oak, splinterspore, wayglass, and waytorch_tree.

## 1.0.644 (2026-01-26)
- Tuning(worldgen/trees): apply Evergreen-style `surface_water_depth_filter` (no water) and broaden `would_survive` placement gating across tree placed features (normal + mega).

## 1.0.643 (2026-01-26)
- Tuning(worldgen/trees): add `would_survive` placement filters to mega placed features (BOP-style placement hygiene).

## 1.0.642 (2026-01-26)
- Tuning(worldgen/trees): retune all mega tree targets to be consistently tall and more species-distinct (reduced cross-species mega overlap).
- Tuning(worldgen/wayroot): increase Wayroot baobab mega core trunk heights so mega baobabs are actually tall.

## 1.0.641 (2026-01-26)
- Chore(docs): clarify updated tree silhouette notes and correct the 1.0.640 tree-tuning entry to include driftwood/fallbark.

## 1.0.640 (2026-01-26)
- Tuning(worldgen/trees): push stronger per-species silhouettes (less overlap) by retuning the primary tree variant for cairn_tree, driftwillow, driftwood, echowood, fallbark, faultwood, hollowway_tree, monument_oak, splinterspore, wayglass, and waytorch_tree.

## 1.0.639 (2026-01-26)
- Tuning(worldgen/wayroot): wire Wayroot baobab (normal + mega) to select among the 3 Arboria-style variants (equal-chance selection).

## 1.0.638 (2026-01-26)
- Tuning(worldgen/wayroot): rework Wayroot baobab-style generation into an Arboria-inspired multipart setup (core + canopy + directional branches).
- Tuning(worldgen/wayroot): retheme Wayroot root patches to place `kruemblegard:wayroot_log` + `kruemblegard:ashmoss_carpet` and loosen ground checks to allow any solid support.

## 1.0.637 (2026-01-26)
- Chore(worldgen/wayroot): remove unused legacy configured/placed features (`wayroot/1..5`, `wayroot/mega_1`, `wayroot/mega_2`) after switching Wayroot to baobab-style generation.

## 1.0.636 (2026-01-26)
- Tuning(worldgen/wayroot): switch Wayroot tree generation to a baobab-style configured feature (including a dedicated mega variant) and align sapling growth with the new feature.

## 1.0.635 (2026-01-26)
- Chore(kruemblegard_boss): remove unused legacy melee state field (`meleeImpactDone`).

## 1.0.634 (2026-01-26)
- Fix(kruemblegard_boss): remove obsolete melee impact-timing reference (`MELEE_IMPACT_AT`) after switching to Warden-style instant-hit melee.

## 1.0.633 (2026-01-26)
- Chore(docs): reorder changelog entries newest-first.

## 1.0.632 (2026-01-26)
- Chore(kruemblegard_boss): remove unused legacy melee timing constants after Warden-style melee refactor.

## 1.0.631 (2026-01-26)
- Tuning(kruemblegard_boss): match vanilla Warden movement speed and melee cadence (18-tick cooldown) and speed up animations to match.

## 1.0.630 (2026-01-26)
- Chore(docs): restructure README to be more player-facing (install + gameplay first; pack maker/artist and dev notes moved lower).

## 1.0.629 (2026-01-26)
- Chore(docs): rewrite README.md to reflect current gameplay, required dependencies, and Blockbench/GeckoLib projectile export targets.

## 1.0.628 (2026-01-26)
- Feature(kruemblegard_boss): add phase-specific 3D projectile entity scaffolding (4 variants) with GeckoLib model hooks + placeholder Blockbench export targets.
- Tuning(kruemblegard_boss): phase 4 “beam” is now a real projectile entity (modelable/animatable) instead of particles + ray damage.

## 1.0.627 (2026-01-26)
- Fix(kruemblegard_boss): projectile entities are now visible (previously particles-only/invisible renderer).

## 1.0.626 (2026-01-26)
- Tuning(kruemblegard_boss): faster chasing + better target tracking during windups (faces target during melee/abilities; melee hitbox slightly more forgiving).

## 1.0.625 (2026-01-26)
- Tuning(kruemblegard_boss): reduce downtime between attacks (global/per-ability cooldowns roll faster; melee windup + post-swing cooldown reduced).

## 1.0.615 (2026-01-25)
- Fix(wayfall/mobs): Wayfall air-swimming axolotls and glow squid now pick destinations and steer via vanilla `MoveControl` (less slippery; target-chasing works for axolotls).

## 1.0.616 (2026-01-25)
- Fix(wayfall/mobs): treat air like water for Wayfall “air swimmers” by applying swim-style propulsion in air (they now actually move around instead of stalling).

## 1.0.617 (2026-01-25)
- Fix(wayfall/mobs): apply air-swim propulsion post-tick so axolotls and glow squid reliably move through air (vanilla water movement can stall when not in water).

## 1.0.618 (2026-01-25)
- Fix(wayfall/worldgen): origin spawn island now applies a biome-aware processor list so Runegrowth blocks in the island template match the biome’s Runegrowth variant.

## 1.0.619 (2026-01-25)
- Fix(scattered_enderman): force a cutout render type so the base texture no longer renders as solid black.
- Fix(scattered_enderman): correct carried-block transform so held blocks render right-side-up.

## 1.0.620 (2026-01-25)
- Fix(scattered_enderman): also force cutout render type from the GeoModel side and add a packed-light safety fallback (addresses cases where the renderer override isn’t hit / light ends up as 0).

## 1.0.621 (2026-01-25)
- Fix(build): resolve CI compilation failure in `ScatteredEndermanRenderer` (invalid `getPackedLight` override).

## 1.0.622 (2026-01-25)
- Fix(scattered_enderman): remove the auto-glowing eyes layer to avoid shader incompatibilities and better isolate the remaining “all-black” base texture issue.

## 1.0.623 (2026-01-25)
- Fix(scattered_enderman): add a `held_block` Geo bone and render the carried block anchored to that bone (fixes misalignment and upside-down transforms).

## 1.0.624 (2026-01-25)
- Tuning(scattered_enderman): increase the held-block render scale (double size).

## 1.0.614 (2026-01-25)
- Content(runegrowth): split Runegrowth into 4 blocks (Resonant/Frostbound/Verdant/Emberwarmed) and update Wayfall surface/worldgen to place the correct variant by biome.
- Fix(tilth): improve Rubble Tilth crop support (Forge `PlantType.CROP`/`IPlantable`) and register an inventory item for Rubble Tilth.

## 1.0.613 (2026-01-25)
- Perf(runegrowth): reduce server tick load by throttling spread attempts and avoiding any potential sync chunk loads from random ticks.
- Debug(runegrowth): add optional per-dimension Runegrowth random-tick counter logging (enable with JVM flag `-Dkruemblegard.debug.runegrowthTicks=true`).

## 1.0.612 (2026-01-24)
- Perf(wayfall): reduce chunk-load lag from vanilla-structure retheming by skipping non-candidate blocks, avoiding level lookups, and removing per-block RNG overhead.

## 1.0.611 (2026-01-24)
- Chore(wayfall): remove leftover voidfall rescue code.

## 1.0.610 (2026-01-24)
- Fix(wayfall): remove Wayfall voidfall rescue (vanilla void) logic.

## 1.0.606 (2026-01-24)
- Perf(wayfall): reduce lag spikes during Wayfall shipwreck/jungle temple retheming by avoiding neighbor-update storms on chunk load.
- Tuning(wayfall): voidfall rescue no longer runs for creative/spectator players (helps admin /tp biome/structure testing).

## 1.0.607 (2026-01-24)
- Perf/Stability(wayfall): cap/evict Wayfall vanilla-structure retheme tracking data to prevent autosaves and “Save and Quit” from hanging after wide /tp exploration.

## 1.0.608 (2026-01-24)
- Fix/Perf(wayfall): make pre-player Wayfall preload opt-in (`wayfallPreloadOnServerStart=false` by default) to prevent choppy world startup and stalled chunk loading.

## 1.0.609 (2026-01-24)
- Fix/Perf(wayfall): remove voidfall “next life” target scanning that could generate far chunks (via heightmap probes) while in Wayfall; voidfall rescue now returns you to the Wayfall spawn landing without forcing remote chunkgen.

## 1.0.594 (2026-01-23)
- Fix(wayfall): restore mobs/items going through the Wayfall portal, but prevent non-player entities from bootstrapping expensive Wayfall initialization (they can travel once Wayfall is initialized by a player).

## 1.0.602 (2026-01-23)
- Perf/Stability(wayfall): add a per-tick time budget for Wayfall init work and defer the heaviest placement step to a later tick if the server is short on time.

## 1.0.603 (2026-01-23)
- Perf/Stability(wayfall): make spawn island placement even more conservative via COMMON config `wayfallInitPlacementMinRemainingMillis`.

## 1.0.604 (2026-01-23)
- Fix/Stability(wayfall): prevent spawn island placement from being deferred forever when `wayfallInitPlacementMinRemainingMillis` is set too close to the per-tick budget.

## 1.0.605 (2026-01-24)
- Fix(wayfall): do not override command teleports (/tp) into Wayfall with the spawn-island safety teleport; safety now applies only to actual portal travel (or temporary holding state).

## 1.0.601 (2026-01-23)
- Perf(wayfall): do a one-time pre-player preload/init of the Wayfall spawn-island area on server/world start, then return to player-driven chunk loading.

## 1.0.600 (2026-01-24)
- Perf/Stability(wayfall): remove remaining forced FULL-chunk loading and neighbor-update storms from spawn island placement/landing calculation.
- Fix(wayfall): avoid doing spawn-island placement work inside the portal teleporter; players may be held briefly (no gravity) near the anchor until the queued safety teleport moves them onto the island.
- Debug(wayfall): add COMMON config toggle `wayfallDebugLogging` to emit extra init/ticket progress logs.

## 1.0.595 (2026-01-23)
- Perf(wayfall): remove forced FULL-chunk loading during spawn-island validation and origin monument placement; these now only proceed when chunks are already ticketed/loaded.

## 1.0.596 (2026-01-23)
- Perf(wayfall): schedule spawn-island chunk ticketing + placement over multiple ticks (avoids doing heavy Wayfall init work in one server tick).

## 1.0.597 (2026-01-23)
- Perf(wayfall): add COMMON config budget (`wayfallInitTasksPerTick`) and ensure Wayfall init work is spread across ticks.

## 1.0.598 (2026-01-23)
- Removed(wayfall): deleted/disabled the Origin Monument feature; spawn island is the only Wayfall bootstrap structure.

## 1.0.593 (2026-01-23)
- Reverted: the temporary change that made the Wayfall portal player-only.

## 1.0.588 (2026-01-24)
- Tuning(worldgen/wayfall): further reduce Underway Falls waterfall spring lag by requiring a supporting block below, encouraging in-rock placement, and making the feature much rarer.

## 1.0.589 (2026-01-23)
- Tuning(moogloom): scale the Griefcap mushrooms down to 50% size.

## 1.0.590 (2026-01-23)
- Fix(moogloom): adjust Griefcap render transforms so scaled caps sit flush on the Moogloom again (no floating).

## 1.0.591 (2026-01-23)
- Fix(moogloom): lower scaled Griefcaps so they don’t hover above the back/head.

## 1.0.592 (2026-01-23)
- Perf(wayfall): speed up origin monument island placement by avoiding expensive neighbor updates during bulk block writes (reduces entry hitching / chunk stalls).

## 1.0.587 (2026-01-23)
- Perf(wayfall): reduce Wayfall entry chunk stalls by only preloading chunks intersecting the spawn-island template (plus small padding) instead of requesting a huge FULL-chunk square.

## 1.0.551 (2026-01-22)
- Fix(waylily): Waylily now places 1 block above the water surface (upper is in air with water directly below) so it no longer replaces/ruins the water surface.
- Fix(worldgen/flora): align Waylily patch placement + deep lake Waylily placement with the above-water rule.
- Tuning(worldgen/lakes): stop snapping deep lake centers to chunk centers (reduces chunk-border “hard line” shore seams).

## 1.0.552 (2026-01-22)
- Fix(worldgen/lakes): guard deep lake block placement with `ensureCanWrite` to prevent “setBlock in a far chunk” warnings during generation.

## 1.0.553 (2026-01-22)

## 1.0.583 (2026-01-22)
- Stability/Performance: fixed Wayfall origin island re-placement loop by loading template chunks before validating placement and by checking within template bounds.

## 1.0.584 (2026-01-22)
- Performance: prevent Wayfall entry hitching by avoiding forced FULL chunk loads during routine spawn-island validation (force-validation now only happens on Wayfall dimension load).

## 1.0.585 (2026-01-22)
- Stability: disable routine spawn-island validation during Wayfall entry to prevent false-positive re-placement loops (validation only runs on dimension load).
- Tuning(worldgen/lakes): increase big deep lake max diameter to ~50 blocks (max radius 25).
- Tuning(worldgen/lakes): Underway Falls now has a higher frequency of large deep lakes (water + occasional lava).
- Tuning(worldgen/lakes): large deep lakes now build a 2-layer Stoneveil Rubble shoreline berm and blend the outer edge downward into terrain to reduce surface spill sheets.

## 1.0.586 (2026-01-22)
- Fix(assets): stop auto-registering block-items for internal growing-plant body blocks (e.g. `pyrokelp_plant`) to prevent missing item-model warnings.
- Fix(assets): glimmerpine leaves temporarily reuse an existing leaves texture to prevent black/minimap texture errors until custom art is ready.
- Fix(assets): map unshipped Kruemblegard sound events to the existing placeholder track to stop missing `.ogg` warnings.

## 1.0.554 (2026-01-22)
- Tuning(worldgen/lakes): big deep lakes now support up to ~100 block diameter (max radius 50) while biasing toward ~50 block average diameter.
- Feature(spawns): Glow Squid now spawn in all Wayfall biomes.
- Feature(spawns): Wayfall fish spawns now follow vanilla-style temperature tiers (warm: tropical/puffer; temperate: cod; cold: salmon).

## 1.0.555 (2026-01-22)
- Feature(wayfall/spawns): Glow Squid are now air-adapted in Wayfall (they can float/swim through open air without suffocating).

## 1.0.556 (2026-01-22)
- Balance(moogloom): Moogloom now drops Griefcap instead of Voidcap Briar.

## 1.0.557 (2026-01-22)
- Feature(moogloom): Moogloom now renders Griefcap mushrooms on its back/head instead of vanilla red mushrooms.

## 1.0.558 (2026-01-22)
- Art(moogloom): update Moogloom texture.

## 1.0.559 (2026-01-22)
- Fix(worldgen/flora): underwater flora patches (seagrass/coral fans/sea pickles) now scan down from the surface to the bottom of water columns so they can generate inside floating-island lakes.

## 1.0.560 (2026-01-22)
- Feature(flora): add Pyrokelp (a climbable, twisting-vine-like plant) that naturally generates only in Basin of Scars.

## 1.0.561 (2026-01-22)
- Feature(worldgen/flora): Pyrokelp now generates as taller pre-grown columns (instead of single sprouts) in Basin of Scars.
- Art(flora): Pyrokelp now has its own smokey-grey texture with red cracks.

## 1.0.562 (2026-01-22)
- Art(flora): Pyrokelp now has a second texture variant used randomly (vanilla-like visual variety).

## 1.0.563 (2026-01-22)
- Feature(flora): Pyrokelp is now a true vanilla-style head/body growing plant (`pyrokelp` + `pyrokelp_plant`) so the top segment uses the “top” texture like twisting vines.

## 1.0.564 (2026-01-22)
- Fix(client): set Pyrokelp render layer to cutout so transparent pixels render correctly.

## 1.0.565 (2026-01-22)
- Fix(assets): add missing Pyrokelp item model/texture so the inventory item no longer renders as missing.

## 1.0.566 (2026-01-22)
- Feature(flora): Pyrokelp now grows naturally and responds to bonemeal like vanilla twisting vines (bonemeal works on any segment; growth comes from the head).

## 1.0.567 (2026-01-22)
- Fix(flora): Pyrokelp growth/bonemeal no longer requires being in Basin of Scars (vanilla twisting-vines behavior once placed); worldgen still restricts natural generation to Basin of Scars.
- Fix(creative): hide internal `pyrokelp_plant` item entry from the mod creative tab to prevent duplicate-looking Pyrokelp entries.

## 1.0.568 (2026-01-22)
- Feature(worldgen/structures): Wayfall biomes now include vanilla shipwreck structures (Crumbled Crossing, Underway Falls, Riven Causeways, Driftway Chasm, Betweenlight Void, Hollow Transit Plains).

## 1.0.569 (2026-01-22)
- Feature(worldgen/structures): Underway Falls now includes vanilla jungle temples.

## 1.0.577 (2026-01-22)
- Tuning(wayfall/ambience): Glow Squid now actively roam through open air (squid-bird vibe) instead of slowly drifting.
- Fix(spawns/wayfall): fish spawn placement no longer assumes sea-level Y; cod/salmon/tropical/puffer can now spawn in high-altitude Wayfall lakes.

## 1.0.578 (2026-01-22)
- Feature(spawns/wayfall): Axolotls now spawn in Underway Falls.
- Feature(wayfall/ambience): Axolotls are now air-adapted in Wayfall and can swim through open air.

## 1.0.579 (2026-01-22)
- Fix(spawns): prevent client crash during spawn placement registration (Forge requires null placement/heightmap when using OR).

## 1.0.580 (2026-01-22)
- Tuning(spawns/wayfall): Glow Squid spawning is now anchored to Wayfall water bodies (in-water or just above the surface) to avoid runaway open-air spawning and freezes.

## 1.0.581 (2026-01-22)
- Tuning(worldgen/wayfall): Underway Falls waterfall sources no longer generate in open air and are less frequent to reduce server tick lag (chunk loading + AI freezes).

## 1.0.582 (2026-01-22)
- Perf(wayfall): reduce server tick lag by removing random ticks from common Wayfall ground covers and non-ticking flora.
- Perf(wayfall): optimize Wispstalk growth checks and Runebloom variant updates.

## 1.0.576 (2026-01-22)
- Feature(spawns/wayfall): Glow Squid can now spawn at any height and light level in Wayfall (including open air).

## 1.0.575 (2026-01-22)
- Fix(moogloom): Moogloom can now reliably breed with vanilla mooshrooms; mixed pairings now produce the intended mixed offspring results.

## 1.0.574 (2026-01-22)
- Fix(worldgen/lakes): clamp deep lake writes to within ±1 chunk of the placement origin to stop “setBlock in a far chunk” spam and worldgen lag.
- Tuning(worldgen/lakes): reduce `wayfall_big_water_lake` max radius to 16 (was 50).

## 1.0.573 (2026-01-22)
- Feature(moogloom): mixed Moogloom+Mooshroom breeding now has a 5% chance to produce a brown mooshroom baby.
- Tuning(moogloom): Moogloom+Moogloom breeding now always produces a Moogloom baby.

## 1.0.572 (2026-01-22)
- Fix(moogloom): Mooglooms now shear like vanilla mooshrooms (convert into a normal cow) while dropping Griefcap.
- Feature(moogloom): Mooglooms can now breed with vanilla mooshrooms; offspring is 50/50 Moogloom vs mooshroom.

## 1.0.571 (2026-01-22)
- Fix(client/moogloom): Griefcap mushrooms now render with vanilla mooshroom transforms (including head attachment), so they no longer look disconnected.
- Fix(client/scattered_enderman): carried blocks now use vanilla Enderman carried-block transform (correct hand/arm placement).

## 1.0.570 (2026-01-22)
- Feature(worldgen/structures): vanilla shipwrecks placed in Wayfall now swap their wood blocks to a biome-specific Wayfall wood palette (with some chance of vanilla wood remaining).
- Feature(worldgen/structures): Underway Falls jungle temples now swap temple stone blocks to the Scarstone family.

## 1.0.524 (2026-01-22)
- Fix(worldgen/trees): make the “large assembled” tree variants (`*/4` and `*/5`, used by all mega selectors) generate true 2x2 trunks by switching to `minecraft:giant_trunk_placer`; this makes 2x2 sapling mega growth look properly “mega” instead of like a tall single-trunk tree.

## 1.0.539 (2026-01-22)
- Fix(assets): repair `waylily_upper.json` so it is valid vanilla block-model JSON (prevents client model parse errors).
- Fix(worldgen/lakes): deep lake generation is now irregular-shaped and properly sealed by placing barrier blocks into adjacent air pockets below the surface (prevents lakes spilling into caves).
- Fix(worldgen/lakes): snap deep lake center to chunk center and clamp extreme radii to avoid “setBlock in a far chunk” spam during worldgen.
- Tuning(worldgen/lakes): reduce large deep lake configured radius from 20–50 to 18–24 for stability.
- Fix(worldgen/lakes): Basin of Scars no longer injects large deep water lakes.

## 1.0.540 (2026-01-21)
- Chore(worldgen/lakes): remove unused `basin_of_scars_big_water_lake` placed feature after disabling Basin-of-Scars big water lake injection.

## 1.0.541 (2026-01-21)
- Fix(worldgen): add a strict validation guard to ensure all lake/pond placed features run in the `lakes` generation step (before any plant/tree placement).

## 1.0.542 (2026-01-21)
- Fix(waylily): player placement now snaps to the true water surface even if you click underwater (vanilla lily-pad feel).
- Fix(worldgen/lakes): deep lake water surface is generated at the correct height (+1 above terrain surface), so Waylilies placed by the lake feature are no longer 1 block too low.

## 1.0.543 (2026-01-21)
- Tuning(worldgen/lakes): deep lakes now use multi-lobed, smooth-noise outlines for more irregular, natural shapes.
- Tuning(worldgen/lakes): add a small “cave cap” at lake edges when under a ceiling to help lakes blend into cave roofs/walls instead of cutting clean holes.

## 1.0.544 (2026-01-22)
- Fix(assets/waylily): replace the upper model with a simple built-in model (pad + crossed planes) so the Waylily top reliably renders with the correct texture.
- Fix(worldgen/lakes): seal internal cave air pockets below the lake surface so deep lakes don’t leave exposed side-water or leak into tunnels.
- Feature(worldgen/lakes): deep water lakes now dress some lake-floor blocks and place seagrass during generation so lakes consistently get underwater flora.

## 1.0.545 (2026-01-22)
- Fix(assets/waylily): convert the custom Waylily upper model to a vanilla-safe Java block-model (supported rotation format/angles, no `#missing` textures) while keeping a multi-petal 3D look.

## 1.0.525 (2026-01-21)
- Fix(assets): Waylily block now correctly resolves its upper model by matching the full blockstate (`part=upper,waterlogged=false`) instead of falling back to missing-model.
- Fix(worldgen/flora): Wayfall water flora (Waylily, seagrass, warm-water coral fans + sea pickles) now scan downward from the surface to find water before attempting placement; this makes them generate in floating-island lakes/ponds instead of only in “true ocean” terrain.

## 1.0.526 (2026-01-21)
- Tuning(assets): Waylily upper now uses a simple built-in 3D flower model (pad + stem + petals) instead of a flat lily-pad parent.

## 1.0.527 (2026-01-21)
- Feature(waylily): Waylily is now a lotus-like surface flower with hanging waterlogged roots; if water is deep enough it can also spawn a 2-block-long roots chain for variety.

## 1.0.528 (2026-01-21)
- Fix(assets): Waylily tail model now uses only vanilla-supported block-model rotation angles (multiples of 22.5°) to prevent model parse errors.
- Fix(worldgen/flora): water flora placed features now use `environment_scan.max_steps=32` (vanilla codec max) so registries load without crashing.
- Fix(assets): add a fallback blockstate variant for `part=upper,waterlogged=true` so resource loading never hits a missing variant.

## 1.0.529 (2026-01-21)
- Fix(worldgen/flora): Waylily water-surface placement now offsets to the block above the found water surface, so it can actually pass `replaceable`/`would_survive` checks and generate in lakes/ponds.
- Fix(assets): keep Waylily upper/tail models within vanilla block-model constraints (no invalid angles / no missing texture keys).

## 1.0.530 (2026-01-22)
- Fix(waylily): Waylily item placement now works reliably in creative; upper can be waterlogged and survives correctly, and tails are created when water is deep enough.

## 1.0.531 (2026-01-22)
- Fix(waylily): Waylily item now places on the water surface (vanilla lily-pad behavior) even when you click underwater; the tail hangs underneath as intended.

## 1.0.532 (2026-01-22)
- Tuning(waylily): Waylily item placement is now surface-only; clicking underwater does nothing.

## 1.0.533 (2026-01-22)
- Fix(waylily): surface-only placement now correctly detects the surface even when the raycast hits the block under the water.
- Fix(worldgen/flora): Wayfall water flora predicates no longer require `replaceable` in water; Waylily now requires surface water (air above + water at position).

## 1.0.534 (2026-01-22)
- Fix(worldgen/flora): underwater flora (seagrass/coral fans/sea pickles) now targets the lake/pond floor (`OCEAN_FLOOR_WG` + +1 offset) instead of the surface water block, so it can survive and generate in floating-island lakes.

## 1.0.535 (2026-01-21)
- Feature(worldgen/lakes): add a very rare large deep Wayfall water lake (10–15 blocks deep) that can span multiple chunks.

## 1.0.536 (2026-01-21)
- Tuning(worldgen/lakes): increase large deep lake radius to 20–50.

## 1.0.537 (2026-01-21)
- Feature(worldgen/lakes): add a Basin of Scars-only large deep lava lake (20–50 radius, 10–15 deep).

## 1.0.520 (2026-01-21)
- Fix(worldgen/trees): make all `*/mega.json` selectors match Evergreen-0-6-3’s object-form `features` entries (`{ "feature": "…", "placement": [] }`) instead of string IDs; this removes ambiguity around placed-feature vs configured-feature resolution during sapling growth and improves 2x2 mega reliability.

## 1.0.519 (2026-01-22)
- Tuning(worldgen/trees): rework all mega tree variants to follow the Evergreen-0-6-3-style assembly pattern: each tree’s `mega_1`/`mega_2` now delegates to that tree’s existing large “assembled” variants (typically `*/5` and `*/4`, with per-tree exceptions where needed), reducing “mega spruce clone” convergence and preserving each species’ branch/root layering.

## 1.0.518 (2026-01-22)
- Tuning(worldgen/trees): rework all mega tree variants to follow an evergreen-style split: `mega_1` uses a spruce-like conical foliage placer and `mega_2` uses a pine-like crowned foliage placer, with stronger trunk-height separation so mega variants no longer look identical.

## 1.0.517 (2026-01-21)
- Fix(worldgen/flora): prevent vanilla seagrass / coral fans / sea pickles from generating on land by requiring water at the placement position (and allowing a small vertical spread so patches still find water above the ocean floor heightmap).

## 1.0.516 (2026-01-21)
- Fix(worldgen/trees): add missing mega-tree placed features (`*/mega_1`, `*/mega_2`) used by the mega selectors; prevents a registry load crash when creating/loading worlds.
- Fix(assets): add missing Moogloom spawn egg item model to prevent missing-model warnings.

## 1.0.515 (2026-01-22)
- Feature(worldgen/flora): warm Wayfall biomes now generate vanilla coral fans and sea pickles underwater.
- Data(worldgen): add `#kruemblegard:wayfall_warm` biome tag and a dedicated biome modifier for warm-water flora.

## 1.0.514 (2026-01-22)
- Tuning(worldgen/flora): Waylily and vanilla seagrass now generate across all Wayfall biomes (only where water exists), with per-biome rarity tiers.
- Fix(worldgen/flora): stop flora patches from targeting leaf canopies by switching plant placed-feature heightmaps to `MOTION_BLOCKING_NO_LEAVES`.

## 1.0.513 (2026-01-22)
- Fix(worldgen/flora): rework Waylily into a water-surface lily-pad with a waterlogged tail block below, and update its patch feature to place on water surfaces.
- Tuning(assets): add placeholder textures for Moogloom and Waylily (upper + tail).

## 1.0.512 (2026-01-22)
- Feature(mobs): add **Moogloom** (Shatterplate Flats-exclusive mooshroom-like creature) with a custom texture, shear-to-kill behavior, and cow drops + a chance for Voidcap Briar.

## 1.0.499 (2026-01-21)
- Fix(assets): Gravevine underside attachment no longer renders as missing texture (purple/black).

## 1.0.500 (2026-01-21)
- Fix(assets): Gravevine now uses vanilla vine blockstate rotation for `up=true`, so underside placement shows the correct “top” vine segment.

## 1.0.501 (2026-01-21)
- Fix(client): render Gravevine as `cutout` (like vanilla vines) so transparent pixels no longer render black.

## 1.0.503 (2026-01-21)
- Docs(flora): clarify Gravevine uses vanilla vine rotations + cutout rendering; remove deleted model reference.

## 1.0.504 (2026-01-21)
- Tuning(assets): rotate Gravevine underside orientation 90° to better align with side texture.

## 1.0.505 (2026-01-21)
- Tuning(blocks): align Gravevine block properties with vanilla vines (no occlusion + vine sound).

## 1.0.507 (2026-01-21)
- Fix(wayfall): apply jigsaw replacement when placing `wayfall_origin_island` templates so jigsaw marker blocks don't remain.

## 1.0.508 (2026-01-21)
- Feature(worldgen): rename the Shatterplate Flats unique tree from **Shardbark Pine** to **Splinterspore** (full registry rename).
- Fix(registry): add missing-mapping remaps so existing worlds with `shardbark_pine_*` blocks/items load and convert to `splinterspore_*`.
- Tuning(blocks): treat `voidfelt` as a nylium-equivalent substrate so nether tree generation rules can accept it.

## 1.0.509 (2026-01-21)
- Tuning(worldgen): Wayburn Fungus now generates only in **Basin of Scars**.

## 1.0.510 (2026-01-21)
- Feature(worldgen): all tree saplings now support 2x2 (mega) growth using Evergreen-style multi-option `mega` configured features.

## 1.0.511 (2026-01-22)
- Feature(worldgen): mega tree variants now generate naturally (rare) across Wayfall biomes, matching the new per-tree mega selectors.

## 1.0.494 (2026-01-20)
- Tuning(assets): Runebloom now uses a single 16-frame sprite-sheet texture (`runebloom_0.png` + `.png.mcmeta`) for animation.

## 1.0.493 (2026-01-21)
- Feature(flora): Gravevine is now a vine-equivalent block (wall/ceiling attach + climbable) and its worldgen patch now places it as vines instead of ground plants.

## 1.0.489 (2026-01-20)
- Fix(wayfall): Wayfall portal block now teleports when you enter it (cooldown gating bug).

## 1.0.490 (2026-01-20)
- Fix(waystones): Ancient Waystone now delegates to the real Waystones waystone when its block entity is missing, restoring interaction.

## 1.0.491 (2026-01-20)
- Fix(waystones): Ancient Waystone now converts to a real Waystones waystone on placement so teleports to it no longer fail as “moved/missing”.

## 1.0.488 (2026-01-20)
- Fix(registry): register the missing-mappings remap handler on the Forge event bus so the mod doesn’t crash during CONSTRUCT.

## 1.0.487 (2026-01-20)
- Tuning(assets): update Runic progression item textures (Attuned Ingot, Runic Ingot, Runic Scrap).

## 1.0.486 (2026-01-20)
- Tuning(assets): update Wayfall Copper Ore texture.

## 1.0.485 (2026-01-20)
- Tuning(worldgen): make Wayroot trees use/spread Ashmoss instead of Stoneveil Rubble / Runed Stoneveil Rubble.

## 1.0.484 (2026-01-20)
- Feature(worldgen): add Wayfall Copper Ore (`kruemblegard:wayfall_copper_ore`) which drops Raw Copper (Fortune supported; Silk Touch drops the ore).

## 1.0.439 (2026-01-16)
- Tuning(worldgen): make Wayfall tree configured features use `kruemblegard:fault_dust` as their dirt provider (no more `minecraft:rooted_dirt` placements).
- Tuning(blocks): make all Kruemblegard `*_log`, `*_wood`, and `*_leaves` blocks flammable like vanilla.

## 1.0.440 (2026-01-16)
- Tuning(blocks): expand vanilla-style flammability to the full Kruemblegard wood family (planks + wooden derivatives like slabs/stairs/fences/doors/signs, plus saplings).

## 1.0.441 (2026-01-16)
- Fix(blocks): ensure Kruemblegard trees/wood actually burn in the built jar by locating the flammability registration method by signature (obfuscation-safe reflection).

## 1.0.442 (2026-01-16)
- Fix(items): ensure all Kruemblegard logs/wood (including stripped variants) are tagged as `minecraft:logs_that_burn` so they smelt into charcoal like vanilla.

## 1.0.443 (2026-01-16)
- Tuning(mobs): allow bees to treat many Kruemblegard plants as flowers for breeding/pollination by extending `minecraft:flowers`.

## 1.0.444 (2026-01-16)
- Fix(wayfall): remove the separate block-built Wayfall origin monument island so portal entry only places the `wayfall_origin_island` structure (no extra cone platform).

## 1.0.445 (2026-01-16)
- Tuning(wayfall): set Wayfall's dimension spawn to the origin-island landing and clamp portal landing to the local surface.

## 1.0.446 (2026-01-16)
- Tuning(wayfall): avoid landing on leaf canopies by clamping to `MOTION_BLOCKING_NO_LEAVES` when possible.

## 1.0.447 (2026-01-16)
- Tuning(worldgen): drastically reduce Attuned Stone disk generation frequency (rarity-based, no longer per-chunk spam).

## 1.0.448 (2026-01-17)
- Fix(client): adjust Scattered Enderman carried-block transforms so held blocks sit in its arms (not behind / too low).

## 1.0.449 (2026-01-17)
- Tuning(worldgen): retune Wayfall biome temperatures (glyphscar_reach colder; underway_falls warmer; fracture_shoals warmer).

## 1.0.450 (2026-01-17)
- Tuning(worldgen): move Wayfall snow cover from `underway_falls` to `glyphscar_reach`.

## 1.0.451 (2026-01-17)
- Tuning(worldgen): increase Paleweft Grass patch density in warm/temperate Wayfall biomes (and make Underway Falls more jungle-like) using a no-leaves surface heightmap for better ground placement under canopy.

## 1.0.452 (2026-01-17)
- Fix(wayfall): remove the emergency 3x3 barrier landing pad so portal entry only places the origin-island NBT structure (no extra barrier platform).

## 1.0.453 (2026-01-17)
- Fix(wayfall): always remove the origin-island landing marker (`minecraft:barrier`) after computing the portal landing.

## 1.0.456 (2026-01-17)
- Fix(worldgen): resolve a Wayfall chunkgen crash (FeatureSorter feature-order cycle) by making shared flora features use a consistent ordering between `crumbled_crossing` and `faulted_expanse`.

## 1.0.457 (2026-01-17)
- Fix(blocks): make `voidfelt` and `ashmoss` place like normal blocks (mycelium-style), removing the Wayfall-only survival/placement restriction and preventing them from popping off when unsupported.

## 1.0.458 (2026-01-17)
- Feature(blocks): make `ashmoss` behave more like a true moss surface: bonemeal can spread it over nearby **Fault Dust**, place matching `ashmoss_carpet`, and bloom pale grasses.
- Feature(blocks): add `ashmoss_carpet`.
- Tuning(assets): give `ashmoss` separate top/side texture slots (moss-block style).

## 1.0.459 (2026-01-17)
- Tuning(blocks): allow Ashmoss spread/bonemeal conversion to target Kruemblegard stone blocks (data-driven via `kruemblegard:ashmoss_spread_targets`).

## 1.0.460 (2026-01-17)
- Tuning(assets): make `ashmoss_carpet` reuse the `ashmoss_top` texture (no separate carpet texture file).

## 1.0.461 (2026-01-17)
- Fix(worldgen): make Ashmoss patches generate embedded in terrain (no floating placements) by projecting to a no-leaves surface heightmap and only replacing blocks tagged `kruemblegard:ashmoss_spread_targets`.

## 1.0.462 (2026-01-17)
- Tuning(worldgen): increase Paleweft grass patch density ~3x across Wayfall biomes.

## 1.0.463 (2026-01-18)

## 1.0.467 (2026-01-18)
- Tweak(worldgen): Increase the secondary small Paleweft grass pass by ~4x (short grass only).

## 1.0.468 (2026-01-20)
- Fix(worldgen): Paleweft patches now require replaceable targets (and survival), preventing them from replacing terrain blocks.

## 1.0.469 (2026-01-20)
- Tweak(tags): Food plants are no longer tagged as bee flowers (removed Wispstalk from `minecraft:flowers`).

## 1.0.470 (2026-01-20)
- Tweak(worldgen): Basin of Scars basalt/magma surface "disks" now generate as irregular blobs (less circular).

- Fix(assets): rotate all Kruemblegard door blockstate variants +90° on the Y axis so door textures apply with the correct facing.

## 1.0.471 (2026-01-20)
- Chore(assets): remove unused wood-family textures (stripped log top variants + an unused Wayroot leaves variant).

## 1.0.472 (2026-01-20)
- Feature(wayfall): first-time players have a small chance to be pulled into the Wayfall when using a Waystone (shift-use bypasses).

## 1.0.473 (2026-01-20)
- Tuning(items): Attuned Ore now drops Raw Attuned Ore (Diamond/Netherite/Runic pickaxes; Fortune supported) and smelts/blasts into Attuned Ingots.
- Tuning(items): Runic tool crafting + Runic tier repairs now use Attuned Ingots instead of Attuned Rune Shards.

## 1.0.474 (2026-01-20)
- Tuning(items): Runic tools are now Netherite-equivalent (no longer strictly above Netherite).

## 1.0.475 (2026-01-20)
- Feature(items): rename Attuned Ore progression to a Netherite-like flow: Runic Debris → Runic Scrap → Runic Ingot.
- Feature(items): Runic tools can now be upgraded from Diamond tools in a Smithing Table using the Netherite Upgrade Template + Runic Ingot (keeps enchantments).
- Tuning(recipes): Attuned Ingots are now crafted from 1x Netherite Ingot + 1x Runic Ingot.
- Fix(save-compat): remap legacy `attuned_ore` / `raw_attuned_ore` IDs to the new Runic Debris/Scrap IDs so old worlds keep loading.
- Fix(assets): rename the Runic Debris block texture to `runic_debris.png` and update the model reference.
- Tuning(assets): Runic Debris now uses dedicated top/side textures (`runic_debris_top.png`, `runic_debris_side.png`).
- Tuning(worldgen): clamp Runic Debris ore height so it never generates above Wayfall Diamond Ore.

## 1.0.465 (2026-01-18)
- Fix(assets): adjust Kruemblegard door blockstate rotations by an additional +180° (final alignment fix).

## 1.0.466 (2026-01-18)
- Fix(worldgen): make Wayfall flora patch placement require replaceable targets (air/replaceables) in addition to survival checks, preventing patches from replacing terrain blocks and cutting holes.

## 1.0.438 (2026-01-16)
- Tuning(wayfall): apply a -8 X/Z placement offset to Wayfall lake placed features so generated lakes are centered.

## 1.0.437 (2026-01-16)
- Tuning(wayfall): make Wayfall water lakes use a Stoneveil Rubble + Runed Stoneveil Rubble barrier mix, and lava lakes use a Scarstone + Cracked Scarstone barrier mix.

## 1.0.436 (2026-01-16)
- Feature(wayfall): add surface water lakes across Wayfall biomes, with Basin of Scars water lakes being rarer.
- Feature(wayfall): add rare surface lava lakes across non-Basin Wayfall biomes (Basin remains more lava-forward).

## 1.0.435 (2026-01-16)
- Fix(wayfall): make Wayfall portal entry more reliable by syncing player teleport with `ServerPlayer.teleportTo` and adding a post-dimension-change fail-safe teleport onto the spawn island landing.

## 1.0.429 (2026-01-16)
- Fix(wayfall): add logging + a last-resort invisible landing pad if the spawn island template fails to provide a solid floor (prevents void falls).

## 1.0.428 (2026-01-16)
- Fix(wayfall): make portal landing use the origin-island marker safely (won't delete the only support block and drop the player).

## 1.0.427 (2026-01-16)
- Chore(structures): add landing marker `minecraft:barrier` blocks to Wayfall origin-island templates for consistent portal-entry spawn placement.

## 1.0.426 (2026-01-16)
- Change(wayfall): portal entry now places a `wayfall_origin_island` structure template at Wayfall spawn and teleports the player onto it (no more block-built spawn platform).
- Tuning(wayfall): origin-island templates can define an explicit landing point using a `minecraft:barrier` marker block (removed after placement).

## 1.0.424 (2026-01-16)
- Fix(structures): correct Wayfall origin-island template pool `location` paths to match the on-disk palette subfolders.
- Chore(worldgen): validate origin-island template pools and referenced structure template `.nbt` files at server start (warn by default; strict-fail when enabled).

## 1.0.421 (2026-01-16)
- Perf(client): add a per-tick cosmetic particle budget to cap worst-case FX spikes (Sodium-style “budgeted work”).
- Perf(client): add optional view-cone culling + conservative epsilon for cosmetic effects (reduces false culls / flicker).
- Refactor(client): centralize cosmetic spawn rules in a shared policy helper (keeps compatibility hooks explicit).

## 1.0.422 (2026-01-16)
- Feature(worldgen): add optional TerraBlender overworld region scaffolding (config-driven weights + per-biome toggles; safe by default).
- Feature(worldgen): add `config/kruemblegard-worldgen.json5` (auto-created/extended) for worldgen tuning.
- Feature(worldgen): support strict worldgen validation (hard-fail at server start when `strictValidation=true`).

## 1.0.420 (2026-01-16)
- Fix(world/wayfall): place the Wayfall origin monument only when a player enters Wayfall (avoids force-loading chunks at dimension load).
- Chore(docs): document “no-tick chunk loading” safety guidance for future long-running world operations.

## 1.0.419 (2026-01-16)
- Perf(client): add optional distance-culling + throttling for Krümblegård cosmetic effects (projectile trail particles).
- Perf(server): projectile trail particles are now client-only (reduces server work and particle packet spam).

## 1.0.384 (2026-01-15)
- Chore(assets): update Scattered Enderman Geo/Animation JSONs.

## 1.0.390 (2026-01-15)
- Tuning(assets): Runegrowth now uses a snowier side texture when placed near snow.

## 1.0.391 (2026-01-15)
- Feature(blocks): add Ashfall Stone + Polished Ashfall Stone (stairs/slabs/walls) as Ashfall Loam’s sandstone-style family.

## 1.0.392 (2026-01-15)
- Tuning(assets): split Ashfall Stone / Polished Ashfall Stone into explicit top/side/bottom textures (placeholder PNGs) and wire models accordingly.

## 1.0.393 (2026-01-15)
- Chore(assets): remove unused legacy `ashfall_stone.png` textures after switching to top/side/bottom placeholders.

## 1.0.394 (2026-01-15)
- Feature(assets): Runegrowth now swaps textures by biome base temperature (cold/temperate/warm/hot), layered with the existing near-snow “snowy” variant.

## 1.0.395 (2026-01-15)
- Chore(assets): update block textures (Runegrowth temperature variants + Ashfall Loam/Stone placeholders).

## 1.0.396 (2026-01-15)
- Tuning(worldgen): adjust several Wayfall biome base temperatures (affects Runegrowth temperature-variant textures).

## 1.0.397 (2026-01-15)
- Tuning(worldgen): Strata Collapse terrain layers now use Ashfall Loam on top, Ashfall Stone below, then a Stoneveil/Runed Stoneveil rubble mix.

## 1.0.398 (2026-01-15)
- Tuning(worldgen): make Strata Collapse the only Wayfall biome that generates Ashfall Loam/Stone in terrain; all other biomes use the default surface stack (Runegrowth → Fault Dust → Stoneveil/Runed rubble mix).

## 1.0.399 (2026-01-15)
- Tuning(worldgen): Shatterplate Flats surface stack now uses Voidfelt on top, Fault Dust below, then the Stoneveil/Runed rubble mix.

## 1.0.400 (2026-01-15)
- Tuning(worldgen): Basin of Scars terrain now uses a Scarstone/Cracked Scarstone mix as deeper sublayers beneath the default surface stack.
- Chore(docs): update Wayfall biome reference to reflect current surface stacks.

## 1.0.401 (2026-01-16)
- Tuning(worldgen): add explicit Wayfall surface stacks for Fracture Shoals and Glyphscar Reach (fractured wayrock + biome-specific deeper mixes).
- Chore(docs): update Wayfall biome reference to reflect the new stacks.

## 1.0.402 (2026-01-16)
- Chore(assets): update `chiseled_scarstone` texture.

## 1.0.403 (2026-01-16)
- Tuning(worldgen): remove Fractured Wayrock as Wayfall’s global deep base stone (kept only as explicit surface identity in select biomes).
- Fix(worldgen): update disk/ore configured features and Wayfall ground tag to match the new base stone.
- Chore(docs): clarify Wayfall surface/base-stone behavior in the Wayfall biome reference.

## 1.0.404 (2026-01-16)
- Feature(blocks): add Wayfall Iron Ore + Wayfall Diamond Ore (with placeholder textures, loot, and mining tags).

## 1.0.405 (2026-01-16)
- Feature(worldgen): generate Wayfall Iron Ore + Wayfall Diamond Ore across all Wayfall biomes (diamond spawns lower on average).

## 1.0.406 (2026-01-16)
- Chore(assets): update Wayfall ore textures.

## 1.0.407 (2026-01-16)
- Fix(assets): Runegrowth snowy variants now use mod-side textures (no more vanilla grass/dirt side).

## 1.0.408 (2026-01-16)
- Tuning(worldgen): force Runegrowth to generate as a single surface layer (reduces chunk-load updates from buried Runegrowth converting to Fault Dust).

## 1.0.409 (2026-01-16)
- Tuning(worldgen): make Basin of Scars feel more volcanic (hotter temperature, denser ash, basalt-deltas ambience).

## 1.0.410 (2026-01-16)
- Feature(worldgen): add volcanic basalt + magma disks to Basin of Scars.

## 1.0.411 (2026-01-16)
- Feature(wayfall): generate a single biome-themed origin monument island at `(0, 190, 0)` (one per world).

## 1.0.412 (2026-01-16)
- Chore(structures): add a cold-biomes Wayfall origin-island placeholder template with jigsaw data.

## 1.0.413 (2026-01-16)
- Chore(structures): add 5-variant template pools for each Wayfall origin-island type (default/ashfall/voidfelt/fractured/glyphscar/basin_of_scars/cold).

## 1.0.414 (2026-01-15)
- Tuning(worldgen): retune Wayfall biome base temperatures.

## 1.0.415 (2026-01-15)
- Tuning(assets): update Runegrowth temperature bands to match the new Wayfall biome temperature range.

## 1.0.385 (2026-01-15)
- Fix(client): render the carried block for Scattered Enderman (held blocks no longer appear invisible).

## 1.0.386 (2026-01-15)
- Fix(assets): correct Scattered Enderman UVs to match the 64x32 in-game texture.

## 1.0.387 (2026-01-15)
- Chore(assets): update Scattered Enderman geo/animation from Blockbench.

## 1.0.388 (2026-01-15)
- Fix(client): render carried blocks with vanilla Enderman transforms (correct texture + no more side-floating).

## 1.0.389 (2026-01-15)
- Fix(assets): shift Scattered Enderman UV anchors so the base/eyes textures no longer render as black in-game.

## 1.0.381 (2026-01-15)
- Chore(docs): record the exact duration for Wayfall ambient music in the Sound Bible (62.0s / 1240 ticks).

## 1.0.383 (2026-01-15)
- Feature(items): add the missing Scattered Enderman spawn egg.

## 1.0.382 (2026-01-15)
- Chore(docs): expand the Sound Bible with a per-mob sound checklist (Traprock, Pebblit, Great Hunger, Scattered Enderman, Boss).

## 1.0.379 (2026-01-15)
- Chore(items): remove the custom Wayfall music disc.

## 1.0.380 (2026-01-15)
- Tuning(wayfall): switch the Wayfall ambient music track to `horror-background-atmosphere-09_universfield.ogg`.
- Chore(assets): remove the old Wayfall ambient track `jungle-ish-beat-for-video-games.ogg`.

## 1.0.378 (2026-01-15)
- Fix(wayfall): portal entry now lands on a larger dedicated spawn island (tapered “asteroid” shape) at shared spawn X/Z to prevent spawning over void.
- Tuning(worldgen): Wayfall island bottoms no longer use a hard flat cutoff at Y=96; terrain now fades smoothly toward air below that band for more asteroid-like undersides.

## 1.0.377 (2026-01-15)
- Fix(wayfall): restore intended voidfall behavior: falling into Wayfall’s void throws you to a random safe landing spot in a random dimension (instead of keeping you in Wayfall), while keeping earlier void interception for reliability.

## 1.0.376 (2026-01-15)
- Fix(wayfall): make Wayfall portal landing deterministic by always placing entities onto a dedicated spawn platform at Wayfall’s shared spawn.
- Fix(wayfall): stabilize voidfall rescue by rescuing back to the Wayfall spawn platform and intercepting void damage earlier (attack/hurt), with a lower/safer early-trigger threshold.

## 1.0.375 (2026-01-15)
- Fix(wayfall): make Wayfall portal always enter at the Wayfall spawn landing (End-portal style), not the player’s last/old coordinates.

## 1.0.374 (2026-01-15)
- Fix(wayfall): fix voidfall rescue teleport not triggering by correcting heightmap landing logic and robustly detecting void damage.

## 1.0.373 (2026-01-15)
- Tuning(worldgen): lower the Wayfall void cutoff so floating islands taper more naturally (less flat-cut undersides).

## 1.0.372 (2026-01-15)
- Tuning(worldgen): reduce Wayfall biome size (more frequent biome transitions) so biomes are easier to find.

## 1.0.371 (2026-01-14)
- Tuning(worldgen): make the cold Wayfall biomes place snow layers on the surface during worldgen (`minecraft:freeze_top_layer`).

## 1.0.370 (2026-01-14)
- Fix(wayfall): harden portal landing selection to avoid spawning inside blocks (full entity collision check + safer fallback platform headroom).

## 1.0.369 (2026-01-14)
- Tuning(worldgen): make Hollow Transit Plains and Underway Falls cold enough for snow cover.

## 1.0.368 (2026-01-14)
- Fix(lang): add missing Paleweft translations (grass, tall grass, corn).

## 1.0.367 (2026-01-14)
- Fix(assets): correct wood stairs/slabs/pressure-plate item models so they no longer render as planks.

## 1.0.366 (2026-01-14)
- Fix(lang): fix Krümblegård mojibake strings and add missing translations for Paleweft Seeds, Weftkern, Echokern, and Weftmeal.
- Fix(creative): add Paleweft Seeds to Ingredients and Weft/Echo foods to Food & Drinks.

## 1.0.365 (2026-01-14)
- Tuning(items): make Weftkern and Echokern edible with raw-potato-like saturation; Weftmeal matches baked potato.

## 1.0.364 (2026-01-14)
- Chore(assets): update Paleweft Seeds item texture.

## 1.0.363 (2026-01-14)
- Chore(assets): update Weftmeal item texture.

## 1.0.358 (2026-01-14)
- Feature(assets): add moist vs. dry visual states for Rubble Tilth based on moisture (vanilla farmland-style).

## 1.0.357 (2026-01-14)
- Tuning(worldgen): make Voidfelt rare by limiting surface + patches to Shatterplate Flats; other biomes use Runegrowth on top.

## 1.0.356 (2026-01-14)
- Fix(tilth): make Rubble Tilth dry/trample into Fault Dust (not vanilla dirt).
- Tuning(surface): let Voidfelt spread onto Fault Dust in void biomes (mycelium-like behavior).

## 1.0.355 (2026-01-12)
- Fix(loot): fix Paleweft loot tables failing to load (invalid `minecraft:alternative` condition).
- Fix(worldgen): prevent Wayfall chunkgen crash from a feature-order cycle by making Paleweft patch injection order consistent.

## 1.0.354 (2026-01-12)
- Chore(tools): remove legacy unused-texture audit script.

## 1.0.353 (2026-01-12)
- Chore(assets): normalize all Fallbark textures to 256x256 for proper mipmapping.

## 1.0.352 (2026-01-12)
- Chore(assets): update Wayroot leaves texture.

## 1.0.351 (2026-01-12)
- Fix(worldgen): fix Paleweft patch configured-feature decoding so world creation no longer fails.
- Fix(assets): add missing item model for `paleweft_corn` to prevent model-load warnings.

## 1.0.350 (2026-01-12)
- Chore(assets): update Fallbark woodset block textures (log/planks/door/trapdoor).

## 1.0.349 (2026-01-12)
- Fix(flora): Runegrowth bonemeal now retries multiple nearby patch placements instead of returning after the first attempt.

## 1.0.348 (2026-01-12)
- Tuning(flora): Runegrowth bonemeal can also grow other local biome plant patches (excluding food plants and saplings) so biome flora is renewable.

## 1.0.347 (2026-01-12)
- Tuning(paleweft): restore Runegrowth bonemeal as an on-demand bloom burst (no periodic ticking) while keeping Wayfall worldgen patches.

## 1.0.345 (2026-01-12)
- Tuning(paleweft): remove the periodic “surface bonemeal/bloom” tick and switch Paleweft spawning to a Wayfall-only worldgen patch (mix of short + tall).

## 1.0.344 (2026-01-12)
- Feature(paleweft): add Paleweft Grass + Tall Paleweft Grass with a “stitched” grass tint.
- Feature(paleweft): add Paleweft Seeds + Paleweft Corn crop (4 stages) yielding Weftkern + rare Echokern.
- Feature(paleweft): add Weftmeal (bread-ish food) and a simple recipe.
- Feature(tilth): add Rubble Tilth (Wayfall farmland) created by hoeing blocks tagged as `kruemblegard:rubble_tillable`.
- Feature(world): add a Wayfall surface-bloom mechanic (Runegrowth bonemeal + ambient bloom) that can spawn Paleweft flora.

## 1.0.343 (2026-01-11)
- Tuning(composting): make Kruemblegard flora (plants, vines, saplings, leaves, and plant foods) compostable.

## 1.0.342 (2026-01-11)
- Tuning(trees): keep vanilla-ish sapling growth pacing (random-tick chance) while allowing Wayfall saplings to grow without a light requirement.

## 1.0.341 (2026-01-11)
- Tuning(trees): remove the light requirement from Wayfall sapling growth so “if it can be placed, it can grow”.

## 1.0.340 (2026-01-11)
- Fix(particles): avoid resolving particle registry objects during block registration to prevent the startup crash “Registry Object not present: kruemblegard:arcane_spark”.

## 1.0.339 (2026-01-11)
- Chore(assets): update Voidfelt block textures.

## 1.0.338 (2026-01-11)
- Tuning(worldgen): make Wayfall biome flora more distinct by removing the global plant injection and assigning plant patches per-biome.
- Tuning(worldgen): ensure every Wayfall biome still gets at least one food plant (Soulberry Shrub or Wispstalk).

## 1.0.337 (2026-01-11)
- Feature(particles): implement the `kruemblegard:arcane_spark` particle and register its client provider.
- Tuning(flora): use `arcane_spark` as the “active” particle for select reactive staple plants (Runeblossom, Twilight Bulb, Whispervine).

## 1.0.334 (2026-01-11)
- Feature(crafting): add Wayfall stone blocks to vanilla stone-material tags so they can craft stone tools and common stone components (furnace/stonecutter + many redstone recipes).

## 1.0.333 (2026-01-11)
- Chore(assets): add a placeholder `wispshoot` item texture and point the item model at it.

## 1.0.332 (2026-01-11)
- Chore(assets): remove legacy Wispstalk model/texture (`wispstalk.json` / `wispstalk.png`) after switching to stage visuals.

## 1.0.331 (2026-01-11)
- Chore(assets): split Wispstalk visuals into 4 growth-stage models/textures (`wispstalk_stage0..3`).

## 1.0.330 (2026-01-11)
- Feature(plants): add `wispshoot`, a new edible drop harvested from mature Wispstalk.
- Feature(plants): make Soulberries/Ghoulberries/Wispshoot plant their matching plants when used on suitable ground.
- Tuning(plants): make Soulberry Shrub, Ghoulberry Shrub, and Wispstalk prickly when walked through (vanilla sweet-berry-bush style).
- Note(assets): `wispshoot` currently reuses the Soulberries item texture as a placeholder.

## 1.0.329 (2026-01-10)
- Fix(mobs): actually remove the stray `scattered_enderman.animation_controllers.json` from `assets/.../animations` (GeckoLib only accepts animation JSON there), preventing the startup crash.

## 1.0.328 (2026-01-10)
- Fix(mobs): prevent GeckoLib startup crash by moving the Scattered Enderman animation-controller reference JSON out of `assets/.../animations`.

## 1.0.327 (2026-01-10)
- Feature(mobs): add an Enderman-style animation state machine for `scattered_enderman` (idle/walk/look/stare/scream/carry/attack/death + teleport sequence) and a reference controller JSON.

## 1.0.326 (2026-01-10)
- Feature(mobs): rework `scattered_enderman` geo into Enderman-style bones and import an Enderman-like animation set (idle/look/walk/attack/scream/stare/carry/teleport/vanish/appear/etc.).

## 1.0.325 (2026-01-10)
- Feature(mobs): expand `scattered_enderman` starter animations (angry/carry/attack/teleport) and drive them from vanilla-ish state (target, carried block, swing, teleport).

## 1.0.323 (2026-01-10)
- Feature(mobs): add `scattered_enderman`, an Enderman-derived mob that spawns uncommonly in all Wayfall biomes.

## 1.0.322 (2026-01-10)
- Feature(recipes): add a craftable Ancient Waystone recipe based on the Waystones-style pattern, using `attuned_stone`.

## 1.0.321 (2026-01-10)
- Fix(gameplay): Wayfall portal now transports mobs and item entities (not just players), matching vanilla portal behavior.

## 1.0.315 (2026-01-10)
- Fix(loot): make `runic_core` drop only from the Krümblegård boss by removing it from global loot modifiers.

## 1.0.314 (2026-01-10)
- Chore(docs): add an auto-generated Loot Table Bible (`docs/Loot_Table_Bible.md`) and a generator script (`tools/generate_loot_table_bible.ps1`) to keep it in sync with loot JSON.

## 1.0.313 (2026-01-10)
- Fix(assets): render `ashveil` with cutout transparency so it no longer shows a black halo around the texture.

## 1.0.312 (2026-01-10)
- Tuning(worldgen): make `runegrowth` part of the Wayfall terrain surface palette by generating it as the default top surface (fallback) and as the top cover over `fault_dust` biomes.

## 1.0.311 (2026-01-10)
- Tuning(worldgen): make `kruemblegard:crumbled_crossing` and `kruemblegard:strata_collapse` forest-like by restoring baseline Wayfall tree injections and adding extra-dense tree placements.

## 1.0.310 (2026-01-09)
- Fix(assets): update texture assets.

## 1.0.309 (2026-01-09)
- Fix(gameplay): make `runegrowth` behave like a true grass analog by spreading onto `fault_dust` without requiring nearby waystone energy; it still reverts to `fault_dust` when it can’t survive.

## 1.0.308 (2026-01-09)
- Fix(worldgen): prevent the Wayfall chunk-gen crash (`Feature order cycle found`) by making shared `vegetal_decoration` features use a consistent order across `kruemblegard:crumbled_crossing` and `kruemblegard:strata_collapse`.

## 1.0.306 (2026-01-09)
- Fix(worldgen): mitigate the Wayfall chunk-gen crash (`Feature order cycle found`) by removing custom tree injections from the two biomes called out as involved sources (`kruemblegard:crumbled_crossing`, `kruemblegard:strata_collapse`). Plant/fungus patches still generate.

## 1.0.307 (2026-01-09)
- Fix(worldgen): split the global Wayfall vegetation biome modifier into plants vs. trees, so trees no longer inject into the two biomes implicated by the `Feature order cycle found` crash (`kruemblegard:crumbled_crossing`, `kruemblegard:strata_collapse`).

## 1.0.302 (2026-01-08)
- Fix(worldgen): make Forge 47.4.0 load biome modifiers again by switching `biomes` selectors back to `#tag` string shorthand (the explicit `forge:tag` holder-set form failed registry parsing).
- Fix(worldgen): replace the unsupported `forge:add_structure` biome modifier for `megalithic_circle` with a harmless no-op (the structure is already governed by its `worldgen/structure` + `worldgen/structure_set` and biome tags).

## 1.0.305 (2026-01-08)
- Fix(worldgen): fix Wayfall chunk-gen crash (`Feature order cycle found`) by deleting the duplicated global Wayfall vegetation biome modifier so placed features are not injected twice.

## 1.0.304 (2026-01-08)
- Docs: correct the changelog history around the Wayfall feature-cycle investigation.

## 1.0.303 (2026-01-08)
- Chore(worldgen): rename the global Wayfall vegetation biome modifier to `00_add_wayfall_plants.json` to try to stabilize injection order (subsequent fix removed the duplicate file that was still present).

## 1.0.299 (2026-01-09)
- Fix(worldgen): Wayfall feature injections now apply on Forge 47.4.0 by using explicit `forge:tag` biome selectors in Forge biome modifiers (instead of `#tag` shorthand).

## 1.0.300 (2026-01-08)
- Fix(trees): Wayfall saplings now use vanilla sapling block properties, enabling natural growth via random ticks (bonemeal still works).

## 1.0.301 (2026-01-08)
- Fix(worldgen): move Forge biome modifiers to `data/kruemblegard/forge/biome_modifier` so Wayfall feature injections load correctly (previously they were under the `forge` namespace).

## 1.0.298 (2026-01-09)
- Chore(worldgen): improve startup diagnostics by validating that Wayfall biomes actually contain injected placed features (helps detect biome modifiers not applying).

## 1.0.294 (2026-01-09)
- Fix(wayfall): entering Wayfall now prefers spawning on the nearest surface (heightmap-based) and only builds a platform if no terrain is found.

## 1.0.295 (2026-01-08)
- Fix(advancements): the mod's root advancement is now granted on first entry to Wayfall (instead of immediately on first world tick).

## 1.0.297 (2026-01-08)
- Fix(worldgen): Wayfall feature injections now apply correctly by using `#tag` biome selectors in Forge biome modifiers.
- Tuning(worldgen): reduce Wayfall biome size by enabling additional climate channels (continentalness/depth) and increasing climate sampling frequency.

## 1.0.293 (2026-01-09)
- Fix(worldgen): Wayfall vegetation patches now re-sample the surface heightmap per placement attempt, so plant/fungus patches actually generate on floating islands instead of mostly failing over void.

## 1.0.292 (2026-01-08)
- Chore(build): pin the dev/compile Forge dependency back to `1.20.1-47.4.0` (the mod still supports newer Forge 47.x at runtime).

## 1.0.291 (2026-01-09)
- Fix(worldgen): Wayfall biome selection now varies across the world more reliably by using higher-frequency climate sampling for the multi-noise biome source.

## 1.0.287 (2026-01-09)
- Fix(loot): Wayfall tree leaves now drop their matching saplings and sticks like vanilla (shears/silk touch preserve leaves; fortune affects saplings/sticks; explosion rules match vanilla).

## 1.0.286 (2026-01-08)
- Feature(worldgen): add 2x2 “mega” natural Wayfall spawning variants for Glimmerpine, Monument Oak, and Driftwillow (new placed features: `kruemblegard:mega_glimmerpine_tree`, `kruemblegard:mega_monument_oak_tree`, `kruemblegard:mega_driftwillow_tree`).
- Tuning(trees): model the mega variants after Arboria-style giant-tree patterns (giant trunk placer + dense leaf attachments) while keeping each tree’s own logs/leaves.

## 1.0.285 (2026-01-08)
- Feature(worldgen): add natural Wayfall spawning for the mega Wayroot tree via a new placed feature (`kruemblegard:mega_wayroot_tree`) using the existing `wayroot/mega` configured feature.
- Tuning(trees): retune `wayroot/mega` to match Arboria-style “big spruce” proportions (taller trunk and denser crown/leaf attachments) while keeping Wayroot blocks and Stoneveil Rubble dirt.

## 1.0.283 (2026-01-08)
- Tuning(worldgen): make Wayfall floating islands less sparse (more consistent landmass) and improve biome variety by reducing extreme `wayfall/factor` spikes and increasing the `mid_bulge` contribution above the Y=130 cutoff.

## 1.0.284 (2026-01-08)
- Tuning(worldgen): increase Wayfall vegetation density by reducing `rarity_filter` chances on Wayfall-injected placed features (trees and plant patches) so islands above Y=130 reliably get biome flora.

## 1.0.274 (2026-01-08)
- Remove(worldgen): delete the Standing Stone scatter placed/configured features and remove their biome injections; Standing Stones are intended to be placed via structures instead.

## 1.0.273 (2026-01-08)
- Chore(docs): add a Sound Bible with exact duration (seconds/ticks) requirements and a generator script (`tools/generate_sound_bible.ps1`).

## 1.0.272 (2026-01-08)
- Chore(docs): add an Item Material Bible and auto-generated registry inventories for blocks/items (kept in sync via `tools/generate_material_bibles.ps1`).

## 1.0.271 (2026-01-08)
- Fix(worldgen): Wayfall islands no longer generate as a mostly-solid slab from Y=130 to build limit; reintroduced negative elevation shaping and reduced the mid-band bulge so the dimension has actual air space between islands for gameplay/feature spawning.

## 1.0.270 (2026-01-08)
- Tuning(trees): Wayroot mega growth now uses Stoneveil Rubble as the dirt provider and spreads Runed Stoneveil Rubble around the trunk (spruce podzol-style), replacing the previous Scarstone spread.

## 1.0.269 (2026-01-08)
- Feature(blocks): make `fractured_wayrock` behave like vanilla stone (Silk Touch drops itself, otherwise drops `crushstone`), and add smelting/blasting `crushstone` back into `fractured_wayrock`.

## 1.0.268 (2026-01-08)
- Chore(docs): add vanilla analog notes to the Block Material Bible entries.

## 1.0.267 (2026-01-08)
- Chore(docs): add a Block Material Bible to document geology/stone/ground material rules.

## 1.0.266 (2026-01-08)
- Feature(trees): Wayroot 2x2 growth now spreads Scarestone around the trunk (spruce podzol-style).

## 1.0.265 (2026-01-08)
- Feature(trees): `wayroot_sapling` now supports 2x2 (spruce-style) placement to grow a larger Wayroot.

## 1.0.264 (2026-01-08)
- Feature(wayfall): falling into the void in Wayfall now teleports the player to a precomputed safe landing spot in a random dimension (including mod dimensions), then immediately assigns the next destination.

## 1.0.263 (2026-01-08)
- Cleanup: physically delete all remaining Veilgrowth / Veilgrowth Charged assets and data files (blockstates/models/textures/loot/worldgen JSON) and remove Veilgrowth references from texture-audit/generation scripts.

## 1.0.262 (2026-01-08)
- Remove: delete Veilgrowth / Veilgrowth Charged from the mod entirely (no block, no item, no worldgen injection). Wayfall surface cover logic now uses Fault Dust as the neutral fallback.

## 1.0.261 (2026-01-08)
- Chore(build): bump Forge dependency to `1.20.1-47.4.10` (recommended runtime Forge version); if you hit a shutdown crash like `SimpleCommentedConfig cannot be cast to CommentedFileConfig` on world exit with older Forge builds, update your instance’s Forge.

## 1.0.260 (2026-01-08)
- Fix(assets): wire `fractured_wayrock_top` / `fractured_wayrock_side` into the fractured wayrock block + slab/stairs/wall models (previously everything used the single `fractured_wayrock` texture).

## 1.0.258 (2026-01-08)
- Fix(worldgen): move `attuned_ore` placed-feature height range into the floating-island band (Y=130..383) so ores/features actually generate in Wayfall above the cutoff.

## 1.0.257 (2026-01-08)
- Fix(worldgen): enforce Wayfall’s “void below Y=130” cutoff again while biasing terrain upward (via `wayfall/mid_bulge`) so every Wayfall biome reliably spawns floating islands above 130.

## 1.0.256 (2026-01-08)
- Fix(worldgen): remove the hard Wayfall terrain cutoff at Y=130 by moving the `final_density` clamp down to the dimension min Y (-32), so island bottoms aren’t perfectly flat.

## 1.0.255 (2026-01-08)
- Fix(worldgen): make Wayfall terrain actually generate by using `wayfall/island_shape` in terrain density (positive bias for islands) and removing an extra duplicated factor/amplification subtraction that kept density too negative.

## 1.0.254 (2026-01-08)
- Fix(assets): render saplings and plants with cutout so transparent pixels don’t appear black.

## 1.0.253 (2026-01-08)
- Fix(codex): remove the custom Crumbling Codex item and replace it with a vanilla `minecraft:written_book` guide (given once on first join).

## 1.0.252 (2026-01-08)
- Fix(assets): add Kruemblegard wall blocks to `minecraft:walls` so they connect to each other and vanilla walls.

## 1.0.251 (2026-01-08)
- Fix(assets): fix `menu_tab` item icon rendering by using `textures/item/menu_tab.png` (item models can’t sample from `textures/gui`).

## 1.0.250 (2026-01-08)
- Fix(worldgen): add empty `carvers.liquid` to all Wayfall biomes (matches vanilla biome schema; avoids datapack codec failures on stricter loaders).

## 1.0.245 (2026-01-08)
- Fix(codex): remove the extra filled Codex entry from creative tabs so only one Crumbling Codex shows up.
- Chore(assets): use `textures/gui/menu_tab.png` for the creative-tab icon and delete the unused duplicate texture.

## 1.0.246 (2026-01-08)
- Fix(codex): explicitly open the vanilla book screen for the Crumbling Codex (vanilla only auto-opens `minecraft:written_book`).

## 1.0.247 (2026-01-08)
- Fix(worldgen): restore Aether-style Wayfall terrain density sliding so islands generate (beyond just the entry platform).

## 1.0.243 (2026-01-07)
- Fix(codex): ensure Crumbling Codex always opens (even when obtained as an untagged creative/search stack).
- Tuning(assets): make Crumbling Codex render as a simple 3D book item model (less flat in-hand).
- Fix(assets): correct wood-family item model parents that were still pointing at driftwillow placeholders.
- Fix(assets): add missing placeholder wood door/trapdoor block textures for ashbloom/driftwood/glimmerpine.

## 1.0.244 (2026-01-07)
- Fix(assets): make door textures follow vanilla’s layout (door edge strip baked into `*_door_{top,bottom}.png`) so the front face renders correctly.
- Fix(assets): add placeholder `*_log_top` / `*_stem_top` textures when missing (copied from the side texture).

## 1.0.242 (2026-01-08)
- Fix(compat): harden Waystones BlockEntityType extension (avoids immutable-set crash; uses mutable copy when possible).
- Fix(assets): add missing `great_hunger_spawn_egg` item model.
- Fix(assets): normalize wood-family block model texture references and fence gate parents to eliminate missing-texture/model spam.

## 1.0.223 (2026-01-07)
- Fix(assets): backfill missing wood-family block models (doors, trapdoors, slabs, stairs, pressure plates) so blockstates resolve cleanly and blocks/items stop rendering as missing-model purple/black.
- Fix(tools): make `tools/audit_missing_models_and_parents.ps1` correctly handle blockstate JSON with empty-string variant keys and add CSV export + configurable output size.
- Chore(tools): expand `tools/fix_wood_models_and_refs.ps1` to cover more wood families and generate the common missing model templates.

## 1.0.224 (2026-01-07)
- Fix(assets): split Wayfall portal to a dedicated texture (`wayfall_portal.png`) instead of reusing `reverse_portal_spores.png`.

## 1.0.227 (2026-01-07)
- Feat(assets): animate `wayfall_portal.png` via `wayfall_portal.png.mcmeta` (16 frames, frametime=2, interpolate=false).

## 1.0.228 (2026-01-07)
- Fix(assets): update `wayfall_portal.png` to a vertically stacked 16-frame sheet compatible with `.png.mcmeta` animation.

## 1.0.229 (2026-01-07)
- Feat(compat): Ancient Waystone now opens the Waystones selection menu when the Waystones mod is installed (converts into a real waystone on first use).

## 1.0.230 (2026-01-07)
- Feat(compat): Ancient Waystone is now a native Waystones variant (extends Waystones' `WaystoneBlock`) with Waystones-style top/bottom models and a dedicated editable texture (`ancient_waystone.png`).

## 1.0.231 (2026-01-07)
- Feat(pebblit): add GeckoLib model/animations for Pebblit.
- Feat(pebblit): allow tamed Pebblit to perch on the owner's shoulder (right-click with empty hand), granting knockback resistance while perched.
- Feat(pebblit): Pebblit attacks now apply knockback.

## 1.0.232 (2026-01-07)
- Feat(pebblit): shift + right-click with empty hand perches a tamed Pebblit on your shoulder until it dies.
- Feat(pebblit): right-click with empty hand (non-shift) toggles sit-and-stay.
- Feat(advancement): add "A little Clingy" for perching a Pebblit.

## 1.0.233 (2026-01-07)
- Feat(pebblit): add GeckoLib animation hooks for sit/perching/perched states.

## 1.0.234 (2026-01-07)
- Feat(great_hunger): add initial The Great Hunger mob scaffolding (entity + GeckoLib renderer/model + geo/animations).

## 1.0.235 (2026-01-07)
- Feat(great_hunger): expand The Great Hunger animation set.

## 1.0.236 (2026-01-07)
- Tuning(assets): iterate on Great Hunger + Pebblit geo/animation placeholders.

## 1.0.237 (2026-01-07)
- Feat(great_hunger): register The Great Hunger (entity type, renderer, attributes/spawn placement, spawn egg, lang).

## 1.0.238 (2026-01-07)
- Feat(assets): add placeholder `great_hunger.png` texture and point Great Hunger model at it.

## 1.0.239 (2026-01-07)
- Fix(wayfall): increase portal landing search radius so it more reliably finds island ground (avoids spawning into empty void pockets).
- Docs: update Wayfall test teleport Y to 160.

## 1.0.221 (2026-01-07)
- Fix(assets): correct `ashbloom_door`, `driftwood_door`, and `glimmerpine_door` item models so they no longer reuse the `driftwillow_door` icon.
- Fix(assets): add placeholder door item textures for ashbloom/driftwood/glimmerpine (copied from their planks textures) so the icons at least match their wood set.
- Chore(tools): add `tools/audit_item_model_texture_mismatches.ps1` to systematically audit item-model → texture mappings (including duplicate/placeholder detection).

## 1.0.218 (2026-01-07)
- Fix(datapack): repair corrupted Wayfall wood recipes (removes `null` fields and invalid item strings) so datapack reload succeeds and recipes load properly.
- Fix(loot): update `kruemblegard:add_items` global loot modifier JSON to use `ItemStack` codec keys (`id` + `Count`).

## 1.0.217 (2026-01-07)
- Fix(worldgen): rework Wayfall multi-noise biome parameters into a non-overlapping temperature×humidity grid. Fixes only kruemblegard:crumbled_crossing spawning and allows all 12 Wayfall biomes to appear.
- Chore: remove unnecessary @SuppressWarnings("deprecation") flagged by diagnostics.

## 1.0.216 (2026-01-08)
- Fix(worldgen): CRITICAL - elevation noise definition was completely wrong. Aether uses firstOctave:-7 with amplitudes:[1.0, 0.75, 0.0, 0.0, 0.0] but we had firstOctave:-8 with [1.0, 0.75, 0.5, 0.25]. This noise drives the abs(shifted_noise) in elevation_2d, controlling island shape variation. Wrong octave/amplitudes = no terrain!

## 1.0.215 (2026-01-08)
- Fix(worldgen): CRITICAL - elevation_2d was using cache_2d with basic noise but Aether uses abs(shifted_noise) with xz_scale:0.5. This abs() makes elevation always positive, creating proper island shape variation. Was using wrong noise type entirely!

## 1.0.214 (2026-01-08)
- Fix(worldgen): CRITICAL - change range_choice threshold from Y=100 to Y=130 (Aether's exact value). Was blocking terrain generation between Y=100-130 where islands START forming. This was THE blocker.

## 1.0.213 (2026-01-08)
- Fix(worldgen): CRITICAL - match Aether's dimension height (min_y:-32, height:416) and Y-ranges (islands Y=130-202). Our dimension was Y=0-384 but Aether is Y=-32 to Y=384, causing islands to generate at wrong heights. Now matches exactly.
- Fix(worldgen): CRITICAL - add max wrapper to final_density matching Aether's final_islands structure. Aether uses max(terrain_density, range_choice) not just range_choice alone. This ensures terrain generates properly.

## 1.0.207 (2026-01-08)
- Fix(worldgen): use Aether's ACTUAL biome parameter pattern - extremely wide ranges like [-1.5, 1.5] for continentalness/depth/humidity. Aether biomes can spawn anywhere, differentiated by narrow temperature/weirdness ranges. This is the REAL Aether pattern.

## 1.0.206 (2026-01-08)
- Fix(worldgen): set continentalness to 0.0 for terrain (Aether pattern) and use only temperature/humidity/weirdness/erosion for biome variety. Continentalness was being used in both terrain formation AND biome selection, causing conflict.

## 1.0.205 (2026-01-08)
- Fix(worldgen): widen multi-noise biome parameters with overlap - old ranges were too narrow and non-overlapping (only crumbled_crossing could spawn). Now all 12 biomes use wide, overlapping ranges across full [-1.0, 1.0] spectrum for proper distribution.
## 1.0.204 (2026-01-08)
- Fix(worldgen): CRITICAL noise_router and final_density fixes - set continents/initial_density to 0.0 constants (Aether pattern), added range_choice threshold (islands only above Y=100), added interpolated/blend_density wrappers for proper chunk blending. This was THE root cause of no terrain generating.
- Fix(worldgen): restore continents noise function for biome variety (Aether uses single biome, but we want multi-noise biome distribution).

## 1.0.203 (2026-01-07)
- Fix(worldgen): use Aether's exact factor_shattered spline (triple-nested ridges→erosion→temperature spline with 1.0/2.0/7.5 values for biome-specific terrain drama).

## 1.0.202 (2026-01-07)
- Docs: add WAYFALL_AETHER_MAPPING.md documenting exact match to Aether's proven generation pattern. Wayfall is now a functional Aether-style dimension using Krümblegård blocks.

## 1.0.201 (2026-01-07)
- Feat(worldgen): add Aether's amplification system via weird_scaled_sampler (creates dramatic island size variation - some huge, some tiny) and enable mob spawning.

## 1.0.200 (2026-01-07)
- Fix(worldgen): apply Aether best practices - swap size_horizontal/vertical (2/1 creates proper chunky islands), lower sea_level to -64 (prevents sky water), use old_blended_noise for smoother organic terrain.

## 1.0.199 (2026-01-07)
- Fix(worldgen): correct spline Y-ranges to match Aether (islands form at Y=100-190, not Y=0-192 - this was why no terrain generated).

## 1.0.198 (2026-01-07)
- Fix(dimension): remove fixed_time from Wayfall dimension_type (this was the actual cause of experimental features warning).

## 1.0.197 (2026-01-07)
- Fix(worldgen): adjust Wayfall density baseline offsets to create proper void/solid threshold (was filling entire world with blocks due to too-high baseline values).

## 1.0.196 (2026-01-07)
- Fix(dimension): set natural=true and has_skylight=true in Wayfall dimension_type (removes experimental features warning on world creation, matches Aether configuration).

## 1.0.195 (2026-01-07)
- Fix(worldgen): implement Aether's actual spline-based Y-slides for Wayfall (examined real Aether mod files - they use dynamic elevation-based splines, not simple gradients).

## 1.0.194 (2026-01-07)
- Fix(worldgen): use End's proven multiplicative Y-slide pattern for Wayfall islands (copy exact approach from vanilla End dimension for reliable floating islands).

## 1.0.193 (2026-01-07)
- Fix(worldgen): rebuild Wayfall density with additive Y-slides for true floating islands (was creating flat layers with roof due to multiplicative gradient logic).

## 1.0.186 (2026-01-07)
- Fix(worldgen): rework Wayfall noise sampling + density shaping to produce smoother, larger floating islands (less blocky/shredded terrain).

## 1.0.192 (2026-01-07)
- Fix(worldgen): replace WORLD_SURFACE_WG with MOTION_BLOCKING heightmap in all 70 Wayfall placed features (fixes feature placement in no-skylight dimension).

## 1.0.191 (2026-01-07)
- Fix(worldgen): rebuild Wayfall density for Aether-authentic varied islands (multi-scale noise for size variety, high 3D detail for organic shapes, wider Y-range for height variation).

## 1.0.190 (2026-01-07)
- Fix(worldgen): correct Wayfall density math to use End-style multiplicative Y-slides (eliminates superflat generation; produces proper distinct floating islands).

## 1.0.189 (2026-01-07)
- Fix(worldgen): remodel Wayfall density to match Aether terrain topology (flat-topped islands with tapered bottoms, proper vertical layering, larger coherent landmasses).

## 1.0.188 (2026-01-07)
- Fix(worldgen): completely rebuild Wayfall density functions using End/Aether floating-islands pattern (discrete islands over void; eliminated inverted cave generation).

## 1.0.187 (2026-01-07)
- Tuning(worldgen): shift Wayfall generation toward many smaller, thinner floating islands (denser island field; more void; fewer merged masses).

## 1.0.184 (2026-01-06)
- Assets: regenerate plant + surface-flora textures from the Plant Material Bible, and add audit scripts to catch missing model texture references + duplicate generated textures.
- Wayfall surfaces: `runegrowth`/`voidfelt`/`ashfall_loam` now use top/side/bottom textures (podzol-like visuals).
- Gameplay: `runegrowth` is now grass-like (spreads when powered, reverts to `fault_dust` when unpowered) and drops `fault_dust` unless Silk Touch.
- Loot: `voidfelt` now drops `fault_dust` unless Silk Touch.
- Wayfall geology: `ashfall_loam` is now a falling block with sand-like sounds (still treated as dirt/ground via tags).

## 1.0.183 (2026-01-06)
- Assets: regenerate wood-family starter textures with strong per-family visual motifs so each wood type reads as distinct in-game.
- Dev tooling: wood texture generator now threads family style through planks/logs/leaves/doors/trapdoors/derived blocks.

## 1.0.179 (2026-01-06)
- Assets: `*_wood` + `stripped_*_wood` blocks now reuse their matching log textures, and redundant wood-texture PNGs were removed.

## 1.0.178 (2026-01-06)
- Fix(assets): logs and stripped logs now render proper end-grain via per-wood `<type>_log_top` textures (stripped logs reuse the unstripped top texture).

## 1.0.177 (2026-01-06)
- Fix(worldgen): resolve “Failed to load registries” on world creation by correcting Wayfall multi-noise biome source configuration and `minecraft:disk` configured feature `state_provider` codecs.

## 1.0.176 (2026-01-05)
- Wayfall: replace End-based noise routing with custom climate noises + density functions for a more Aether-like floating-islands feel.
- Wayfall: increase generation height to 384.
- Wayfall: portal arrivals now search for nearby solid ground and will generate a small safety platform if the area is void.

## 1.0.167 (2026-01-05)
- Traprock: after a player has encountered Traprock once, repeat encounters have a high chance to awaken immediately.

## 1.0.167 (2026-01-05)
- Assets: replace placeholder 16x16 textures for Wayfall surface blocks with higher-detail textures (Ashmoss, Fault Dust, Veilgrowth, Veilgrowth Charged).

## 1.0.168 (2026-01-05)
- Traprock: after a player has encountered Traprock once, repeat encounters have a high chance to awaken immediately.

## 1.0.169 (2026-01-05)
- Docs: align changelog entries with commit-count versioning.

## 1.0.170 (2026-01-05)
- Assets: update Wayfall ground-cover block textures (Cairn Moss, Runegrowth, Voidfelt).

## 1.0.171 (2026-01-05)
- Fix(assets): backfill missing stone-family textures referenced by generated models (removes missing-texture rendering).

## 1.0.172 (2026-01-05)
- Fix: `wayfall_portal` is now a walk-into portal (non-solid) that teleports players to the Wayfall spawn.

## 1.0.173 (2026-01-05)
- Fix(assets): stone-family block/item models now reference `kruemblegard:block/<name>` textures (removes duplicated root textures).

## 1.0.174 (2026-01-06)
- Fix(worldgen): correct `minecraft:disk` configured feature `state_provider` for Wayfall surface disks so they can generate.

## 1.0.175 (2026-01-05)
- Wayfall: switch biome distribution from checkerboard to multi-noise, and increase noise settings height/detail for more varied terrain.

## 1.0.165 (2026-01-06)
- Worldgen: centralize important datapack IDs into `worldgen/ModWorldgenKeys` and add runtime validation warnings via `WorldgenValidator`.
- Data-driven: make Crumbling Codex JSON loading codec-backed (safer parsing, fewer brittle assumptions).
- Networking/player state: add a tiny per-player persistent state wrapper + S2C sync packet to avoid client-side guessing.
- Repo/process: add a Conventional Commit hook (`.githooks/commit-msg`) and contributing guidance on asset licensing.

## Unreleased

- Wayfall: place `wayfall_origin_island` once at `(0, 175, 0)` on first Wayfall dimension load.
- Fix: Wayfall origin island will re-place itself if an old save marked it placed while templates were missing/empty.
- Wayfall: add rare high-altitude waterfall sources in Underway Falls.
- Basin of Scars: add surface lava lakes (custom placement for better visibility).
- Add Wayfall ambient music (`music.wayfall`) and a matching music disc item.
- Add a Wayfall-only dynamic custom skybox renderer (client) with 6 placeholder cubemap face textures.
- Mobs: add Traprock (dormant until disturbed; Blaze-derived GeckoLib mob with editable geo/anim/texture).
- Mobs: add Pebblit (hostile; tameable with cobblestone; follows owner).
- Gameplay: remove Mimic Waystone block (Traprock is spawned via spawn egg/commands for now).
- Dimension: add Wayfall (`kruemblegard:wayfall`) with End-like baseline terrain and custom Wayfall biomes.
- Worldgen: enrich Wayfall biomes (ambience + small biome features) and restrict Wayfall spawns to Krümblegård mobs only.
- Worldgen: add Attuned Ore (Wayfall-only) that drops Attuned Rune Shards (Fortune affects drops).
- Worldgen: add Wayfall plants (Wispstalk, Gravevine, Echocap, Runebloom, Soulberry Shrubs) + Ghoulberry corruption.
- Blocks/items: expand Wayfall wood sets with full vanilla-style wood-family blocks (stairs/slabs/fences/gates/doors/trapdoors/buttons/pressure plates) including recipes + loot.
- Blocks/items: add missing `*_wood` blocks and stripped `stripped_*_{log,wood}` variants for all Wayfall wood families.
- Gameplay: axes now strip Wayfall logs/wood into their stripped variants (preserves axis).
- Assets: give each Wayfall wood-family block its own dedicated texture file (placeholders copied from planks for now).
- Assets: backfill missing block/item textures referenced by models with autogenerated placeholder PNGs.
- Creative: place all mod items/blocks in the single Krümblegård creative tab (no vanilla tab injection).
- Creative: set the Krümblegård creative tab icon to `menu_tab.png`.
- Dev tooling: add `tools/audit_missing_item_textures.ps1` to verify item-model texture refs all exist.
- Dev tooling: add registered-item asset audits (`tools/audit_missing_registered_item_models.ps1`).
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
- Refactor: Wayfall flora/saplings now use the `kruemblegard:wayfall_ground` block tag for valid substrate (preparing for terrain palette changes).
- Wayfall: add geology palette blocks (Fractured Wayrock, Crushstone, Ashfall Loam, Fault Dust, Scarstone family, Stoneveil Rubble family) + placeholder textures/models.
- Wayfall: switch dimension noise settings to `kruemblegard:wayfall` so terrain defaults to Fractured Wayrock.
- Wayfall: surface palette is now biome-tag-driven in noise settings (Ashfall Loam in ash-heavy biomes, Voidfelt in void biomes).
- Wayfall: add shallow subsurface layering via surface rules (Crushstone under Ashfall Loam / Voidfelt).
- Wayfall: add surface cover blocks (Veilgrowth/Ashmoss/Runegrowth/Voidfelt) with basic spread/conversion rules + client-side ambience when standing still.
- Gameplay: Scarstone is iron-gated (weak tools are slower, take damage, and yield Cracked Scarstone).
- Fix: add missing block loot tables so all blocks drop as items.
- Blocks/items: add stone-family variants (stairs/slabs/walls) for Wayfall geology blocks (Attuned Stone, Fractured Wayrock, Crushstone, Scarstone + variants, Stoneveil Rubble + variants) including recipes + loot.
- Fix: fence gate block models now reference their planks textures (prevents missing-texture rendering).
- Refactor: wood-family derived block/item models (stairs/slabs/fences/buttons/pressure plates) now reuse their matching planks textures to reduce duplicated placeholder textures.
- Assets: remove unused wood-family derived fence textures now that models reuse planks.
- Fix: generate missing `en_us` block/item names so creative inventory shows readable names.
- Dev tooling: add `tools/generate_stone_family_blocks.ps1` to generate stone-family resource/data files.
- Fix: treat Wayfall surface palette blocks as `minecraft:dirt` so vanilla/mod saplings can be planted and tree features can generate on them.

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

