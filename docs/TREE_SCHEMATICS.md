# Tree schematics reference (Krümblegård)

This doc is the “single source of truth” for how schematic-driven trees are authored and implemented in Krümblegård.

Goal: every new schematic-driven tree behaves consistently:
- spawns reliably on terrain (no floating bases)
- never ships “placeholder blocks” into the world
- uses Franch helper blocks the same way as existing trees
- plays nice with leaf decay + harvest mods

## Where things live

### Schematic files
- Stored as Sponge schematics (`.schem`) under:
  - `src/main/resources/data/kruemblegard/schematics/<tree_name>/*.schem`
- Referenced by resource location in configured features, for example:
  - `kruemblegard:schematics/wayroot/wayroot_1.schem`

### Worldgen config
- Configured features live under:
  - `src/main/resources/data/kruemblegard/worldgen/configured_feature/<tree_name>/...`
- For schematic-driven trees, these typically select from multiple schematic configured features.

### Code
Schematic placement is implemented in `src/main/java/com/kruemblegard/world/feature/`:
- `WayrootSchematicFeature` / `WayrootMegaSchematicFeature`
- `EchowoodSchematicFeature` / `EchowoodMegaSchematicFeature`
- Per-tree palette remaps (“processors”) are implemented as mapping helpers:
  - `WayrootSchematicMapping`
  - `EchowoodSchematicMapping`

## Core concept: palette remaps (“processors”)

We do **not** place the schematic palette 1:1.
Instead, every block state from the schematic is mapped through a small ruleset so the schematic can be authored using convenient placeholders.

Common remap goals:
- allow authoring with any vanilla wood family blocks
- force non-persistent leaves for worldgen
- ensure “Franch” helper blocks are used consistently
- remove / ignore debug markers

## Standard placeholder blocks

These are the established placeholders used when building the schematic templates:

### "Do not place" / structure-void markers
- `minecraft:tinted_glass` in the schematic palette means: **skip placement** (treated as structure void).
  - This is the primary “carve out” / “empty slot” marker.

### Pivot marker
- `minecraft:red_wool` is used as a **schematic pivot / center marker**.
  - Goal: **red wool itself should never appear** in the world after placement.
  - Recommended standard for new trees: map it to **<type>_franch_wood**.
    - This guarantees the pivot never leaves red wool in the world, and keeps the center “solid”.
  - Note: current trees differ:
    - Wayroot maps it to trunk material
    - Echowood maps it to trunk material (see “Existing tree-specific rules”)

### Invisible helper
- `minecraft:tripwire` can be used as a placeholder for the schematic-only `string_franch` block.
  - This allows “string” details (e.g., hanging bits) to exist in the schematic without visible/collidable blocks.

## Leaves policy (CRITICAL)

### Worldgen leaves must be non-persistent
Any leaf-like block encountered in a tree schematic should be remapped to the tree’s leaf block with:
- `persistent = false`
- a safe `distance` value (usually set to `1`)
- `waterlogged = false` (if applicable)

Why:
- prevents player-built-tree protections from blocking decay/harvest behaviors
- prevents “leaf spam” and weird decay after chunk load
- keeps all schematic trees consistent across species

Implementation pattern:
- “Leaf-like” detection is not only `#minecraft:leaves`.
  It also checks for leaf-ish properties like `persistent` / `distance` / `FranchDecay.DISTANCE`.

## Trunk policy (Franch wood/planks mix)

Schematic-driven trees intentionally place schematic-only trunk blocks so drops and harvest-mod behavior are controlled.

Standard behavior:
- Any log/wood-like input from the schematic palette is remapped to:
  - **90%** `*_franch_wood`
  - **10%** `*_franch_planks`

Important: anchor tags
- Because Krümblegård leaves/Franch use a species-specific anchor tag (e.g. `kruemblegard:<type>_logs`), that tag **must include both**:
  - `*_franch_wood`
  - `*_franch_planks`
  Otherwise, a plank “gap” in the trunk mix can disconnect a leaf cluster and cause fast decay right after worldgen.

Why:
- `*_franch_wood` looks like trunk blocks but has controlled drops
- `*_franch_planks` adds subtle visual variation without creating full plank “columns”

## Franch helper blocks in tree schematics

### What Franch is
Franch blocks are schematic-only “branch helper” blocks:
- `*_franch`
- `*_franch_gate`
- `*_franch_slab`
- `*_franch_stairs`
- `*_franch_trapdoor`

They are used to:
- extend leaf distance networks
- provide branch-like geometry/connectivity
- give harvest mods a consistent “leaf-like clutter” set to clear

### How Franch should appear in schematic trees
Recommended authoring + mapping approach:
- author branch helper shapes in the schematic using **wooden fences / fence gates / trapdoors / slabs / stairs** (whatever is convenient)
- the mapping code remaps those “wood construction” shapes into the species’ corresponding `*_franch*` blocks

This keeps the schematic templates simple while still ensuring the final placed tree uses Franch helper blocks (not craftable building blocks).

## Terrain support (“support beard”)

Schematic-driven trees can overhang uneven terrain.
To prevent floating bases, we apply a short “support beard” after placement:
- computes the schematic bottom-footprint (only where the schematic actually placed blocks at `y == 0`)
- fills only the perimeter columns under that footprint
- fills downward through air/replaceables/leaves/plants until it hits a sturdy block
- chooses a fill block based on nearby terrain palette (prefers local blocks over plain dirt)

Notes:
- The beard is intentionally conservative (perimeter only) to avoid “filling in” the interior.

## Surface validation (spawn safety)

Before placing the schematic:
- reject if placing into fluids
- reject if the surface/top block is invalid (leaves/logs/replaceable_by_trees/huge mushroom blocks)
- reject if blocks beneath the footprint are not sturdy

This avoids:
- trees spawning into leaf canopies
- trees spawning on logs
- weird floating-on-plants behavior

## Existing tree-specific rules (current state)

### Wayroot
- Mapping: `WayrootSchematicMapping`
- Tinted glass → structure void
- Red wool pivot marker → `wayroot_franch_wood`
- Tripwire → `string_franch`
- Leaf-like → `wayroot_leaves` (non-persistent)
- Trunk-like → 90% `wayroot_franch_wood`, 10% `wayroot_franch_planks`
- Wooden fence / gate / trapdoor placeholders → Wayroot `*_franch*` helper blocks

### Echowood
- Mapping: `EchowoodSchematicMapping`
- Tinted glass → structure void
- Tripwire → `string_franch`
- Leaf-like → `echowood_leaves` (non-persistent)
- Trunk-like → 90% `echowood_franch_wood`, 10% `echowood_franch_planks`
- Red wool pivot marker → `echowood_franch_wood` (legacy choice; acceptable but not the recommended default for new trees)

## Checklist: adding a new schematic-driven tree

Use this checklist for every new schematic tree species.

### 1) Blocks and items
- Add the normal wood set: `*_log`, `*_wood`, stripped variants, `*_planks`, `*_leaves`, `*_sapling`.
- Add schematic-only blocks:
  - `*_franch_wood`
  - `*_franch_planks`
  - `*_franch*` helper blocks (fence-like variants)
  - (optional) `*_mega_franch_leaves` if you need mega-tuned drop rates

### 2) Tags (compat + behavior)
- Ensure logs/wood are in the correct vanilla tags:
  - `minecraft:logs`, `minecraft:logs_that_burn` (as appropriate)
- Ensure leaves are in `minecraft:leaves`.
- Ensure Franch helper blocks are treated as leaf-like for cleanup/harvest systems:
  - either via `minecraft:leaves` (if that’s your current policy)
  - and/or via `kruemblegard:tree_harvester_leaf_like` (if you’re keeping helper blocks out of vanilla leaves)

### 3) Schematics
- Put new `.schem` files under `data/kruemblegard/schematics/<tree_name>/`.
- Use placeholders consistently:
  - tinted glass = void
  - red wool = pivot marker
  - tripwire = string franch
  - wood construction shapes = intended Franch helper geometry

### 4) Feature + mapping code
- Create/clone a `*SchematicFeature` and `*MegaSchematicFeature` if needed.
- Create/clone a `*SchematicMapping` implementing the remap rules.
- Verify:
  - leaf-like mapping forces non-persistent
  - trunk-like mapping uses the 90/10 mix
  - wood construction placeholders map to `*_franch*` helper blocks

### 5) Worldgen JSON
- Add configured feature JSON(s) referencing the new schematics.
- Add selector/weighted lists for living vs dead variants if desired.
- Add placed features and biome modifiers/tags as needed.

### 6) Final “don’t ship a broken tree” checks
- Spawn the tree in a flat test area and on uneven terrain.
- Confirm:
  - no placeholder blocks remain (tinted glass/red wool/etc)
  - no craftable fence/trapdoor blocks appear where Franch should be
  - leaves are non-persistent
  - trunk contains some (not too many) `*_franch_planks`
  - Tree Harvester / FallingTree cleanup clears Franch helper clutter appropriately

## Debugging tips

If a tree spawns with the wrong blocks:
- First check the schematic palette: did the template use a block/tag that the mapper doesn’t handle?
- Then check the mapping class for that tree:
  - Are the placeholder rules ordered correctly?
  - Are you accidentally matching `#minecraft:logs` before matching construction placeholders?

If a tree floats:
- check `hasValidSurfaceUnderBottomFootprint(...)` and `applySupportBeard(...)` are called
- confirm the bottom footprint is being recorded correctly (only `y == 0` cells that actually placed a block)
