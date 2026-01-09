# Changelog

All notable changes to this project will be documented in this file.

Changelog entries are grouped by the exact mod version embedded in the built jar.

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

