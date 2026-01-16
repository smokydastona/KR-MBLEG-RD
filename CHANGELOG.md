# Changelog

All notable changes to this project will be documented in this file.

Changelog entries are grouped by the exact mod version embedded in the built jar.

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

