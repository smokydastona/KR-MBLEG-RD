# Krümblegård — Tree Reference

This document describes every **tree (wood family)** currently in Krümblegård.

Scope/notes:
- All tree content is part of **Wayfall**.
- “Mechanics” below are either implemented behavior or design targets; if a mechanic isn’t implemented yet, it’s explicitly marked as such.
- For the full plant + tree catalog (including non-tree plants), see [FLORA_REFERENCE.md](FLORA_REFERENCE.md).

## Tree families (14)
- `ashbloom`
- `cairn_tree`
- `driftwillow`
- `driftwood`
- `echowood`
- `fallbark`
- `faultwood`
- `glimmerpine`
- `hollowway_tree`
- `monument_oak`
- `shardbark_pine`
- `wayglass`
- `wayroot`
- `waytorch_tree`

## Per-tree details

### Ashbloom
- **IDs**: `kruemblegard:ashbloom_*`
- **Block set**: log/wood (and stripped variants), planks, leaves, sapling, and the standard wood-family building blocks.
- **Worldgen**: `ashbloom_tree` (simple tree feature).
- **Appearance**: Ash-toned wood family intended as a staple Wayfall starter palette.
- **Mechanics**: no special mechanics yet (standard sapling growth + vanilla-style blocks).
- **Core textures**:
  - `assets/kruemblegard/textures/block/ashbloom_log.png`
  - `assets/kruemblegard/textures/block/ashbloom_log_top.png`
  - `assets/kruemblegard/textures/block/ashbloom_planks.png`
  - `assets/kruemblegard/textures/block/ashbloom_leaves.png`
  - `assets/kruemblegard/textures/block/ashbloom_sapling.png`

### Cairn Tree
- **IDs**: `kruemblegard:cairn_tree_*`
- **Appearance**: Tree grown through stacked stones, leaves shaped like memorial flags.
- **Use**: Drops Memorial Sap for soul-based crafting.
- **Mechanics**: design target — grows only near death markers or cairns.
- **Core textures**:
  - `assets/kruemblegard/textures/block/cairn_tree_log.png`
  - `assets/kruemblegard/textures/block/cairn_tree_log_top.png`
  - `assets/kruemblegard/textures/block/cairn_tree_planks.png`
  - `assets/kruemblegard/textures/block/cairn_tree_leaves.png`
  - `assets/kruemblegard/textures/block/cairn_tree_sapling.png`

### Driftwillow
- **IDs**: `kruemblegard:driftwillow_*`
- **Appearance**: Hanging branches that never quite touch the ground.
- **Use**: Slow-fall talismans.
- **Mechanics**: no special mechanics yet.
- **Core textures**:
  - `assets/kruemblegard/textures/block/driftwillow_log.png`
  - `assets/kruemblegard/textures/block/driftwillow_log_top.png`
  - `assets/kruemblegard/textures/block/driftwillow_planks.png`
  - `assets/kruemblegard/textures/block/driftwillow_leaves.png`
  - `assets/kruemblegard/textures/block/driftwillow_sapling.png`

### Driftwood
- **IDs**: `kruemblegard:driftwood_*`
- **Worldgen**: `driftwood_tree` (simple tree feature).
- **Appearance**: Weathered, pale wood intended as a staple Wayfall palette.
- **Mechanics**: no special mechanics yet (standard sapling growth + vanilla-style blocks).
- **Core textures**:
  - `assets/kruemblegard/textures/block/driftwood_log.png`
  - `assets/kruemblegard/textures/block/driftwood_log_top.png`
  - `assets/kruemblegard/textures/block/driftwood_planks.png`
  - `assets/kruemblegard/textures/block/driftwood_leaves.png`
  - `assets/kruemblegard/textures/block/driftwood_sapling.png`

### Echowood
- **IDs**: `kruemblegard:echowood_*`
- **Appearance**: Smooth gray wood with faint ripple patterns.
- **Use**: Craft sound-reactive blocks or redstone-like devices.
- **Mechanics**: design target — emits ambient noises tied to nearby player activity.
- **Core textures**:
  - `assets/kruemblegard/textures/block/echowood_log.png`
  - `assets/kruemblegard/textures/block/echowood_log_top.png`
  - `assets/kruemblegard/textures/block/echowood_planks.png`
  - `assets/kruemblegard/textures/block/echowood_leaves.png`
  - `assets/kruemblegard/textures/block/echowood_sapling.png`

### Fallbark
- **IDs**: `kruemblegard:fallbark_*`
- **Appearance**: Tall, brittle trees that constantly shed slabs of bark downward.
- **Use**: Bark used for impact-resistant blocks.
- **Mechanics**: design target — standing under them is dangerous during “Wayfall tremors.”
- **Core textures**:
  - `assets/kruemblegard/textures/block/fallbark_log.png`
  - `assets/kruemblegard/textures/block/fallbark_log_top.png`
  - `assets/kruemblegard/textures/block/fallbark_planks.png`
  - `assets/kruemblegard/textures/block/fallbark_leaves.png`
  - `assets/kruemblegard/textures/block/fallbark_sapling.png`

### Faultwood
- **IDs**: `kruemblegard:faultwood_*`
- **Appearance**: Jagged, cracked trunk constantly shedding chips.
- **Use**: Reinforced building blocks.
- **Mechanics**: no special mechanics yet.
- **Core textures**:
  - `assets/kruemblegard/textures/block/faultwood_log.png`
  - `assets/kruemblegard/textures/block/faultwood_log_top.png`
  - `assets/kruemblegard/textures/block/faultwood_planks.png`
  - `assets/kruemblegard/textures/block/faultwood_leaves.png`
  - `assets/kruemblegard/textures/block/faultwood_sapling.png`

### Glimmerpine
- **IDs**: `kruemblegard:glimmerpine_*`
- **Worldgen**: `glimmerpine_tree` (simple tree feature).
- **Appearance**: Pine-like wood family intended as a staple Wayfall palette.
- **Mechanics**: no special mechanics yet (standard sapling growth + vanilla-style blocks).
- **Core textures**:
  - `assets/kruemblegard/textures/block/glimmerpine_log.png`
  - `assets/kruemblegard/textures/block/glimmerpine_log_top.png`
  - `assets/kruemblegard/textures/block/glimmerpine_planks.png`
  - `assets/kruemblegard/textures/block/glimmerpine_leaves.png`
  - `assets/kruemblegard/textures/block/glimmerpine_sapling.png`

### Hollowway Tree
- **IDs**: `kruemblegard:hollowway_tree_*`
- **Appearance**: Trunk split down the center, empty inside.
- **Use**: Craft storage blocks with void-linked inventory.
- **Mechanics**: no special mechanics yet.
- **Core textures**:
  - `assets/kruemblegard/textures/block/hollowway_tree_log.png`
  - `assets/kruemblegard/textures/block/hollowway_tree_log_top.png`
  - `assets/kruemblegard/textures/block/hollowway_tree_planks.png`
  - `assets/kruemblegard/textures/block/hollowway_tree_leaves.png`
  - `assets/kruemblegard/textures/block/hollowway_tree_sapling.png`

### Monument Oak
- **IDs**: `kruemblegard:monument_oak_*`
- **Appearance**: Massive stone-wood hybrid, bark etched with history.
- **Use**: Lore crafting, advancement triggers.
- **Mechanics**: no special mechanics yet.
- **Core textures**:
  - `assets/kruemblegard/textures/block/monument_oak_log.png`
  - `assets/kruemblegard/textures/block/monument_oak_log_top.png`
  - `assets/kruemblegard/textures/block/monument_oak_planks.png`
  - `assets/kruemblegard/textures/block/monument_oak_leaves.png`
  - `assets/kruemblegard/textures/block/monument_oak_sapling.png`

### Shardbark Pine
- **IDs**: `kruemblegard:shardbark_pine_*`
- **Appearance**: Tall, needle-like crystal bark.
- **Use**: Craft piercing projectiles.
- **Mechanics**: no special mechanics yet.
- **Core textures**:
  - `assets/kruemblegard/textures/block/shardbark_pine_log.png`
  - `assets/kruemblegard/textures/block/shardbark_pine_log_top.png`
  - `assets/kruemblegard/textures/block/shardbark_pine_planks.png`
  - `assets/kruemblegard/textures/block/shardbark_pine_leaves.png`
  - `assets/kruemblegard/textures/block/shardbark_pine_sapling.png`

### Wayglass Tree
- **IDs**: `kruemblegard:wayglass_*`
- **Appearance**: Crystalline trunk, translucent branches.
- **Use**: Glass-like wood for light-conducting blocks.
- **Mechanics**: design target — refracts light and enchantment particles.
- **Core textures**:
  - `assets/kruemblegard/textures/block/wayglass_log.png`
  - `assets/kruemblegard/textures/block/wayglass_log_top.png`
  - `assets/kruemblegard/textures/block/wayglass_planks.png`
  - `assets/kruemblegard/textures/block/wayglass_leaves.png`
  - `assets/kruemblegard/textures/block/wayglass_sapling.png`

### Wayroot Tree
- **IDs**: `kruemblegard:wayroot_*`
- **Appearance**: Stone-barked tree with exposed, floating roots that don’t touch the ground.
- **Use**: Logs used for waystone upgrades and teleport stabilizers.
- **Mechanics**: design target — roots slowly drift; breaking them can destabilize nearby blocks. The `wayroot_sapling` also supports a 2x2 (spruce-style) placement to grow a larger Wayroot.
  - Mega growth uses `kruemblegard:stoneveil_rubble` as its dirt provider and may convert nearby ground into `kruemblegard:runed_stoneveil_rubble` (spruce podzol-style).
- **Core textures**:
  - `assets/kruemblegard/textures/block/wayroot_log.png`
  - `assets/kruemblegard/textures/block/wayroot_log_top.png`
  - `assets/kruemblegard/textures/block/wayroot_planks.png`
  - `assets/kruemblegard/textures/block/wayroot_leaves.png`
  - `assets/kruemblegard/textures/block/wayroot_sapling.png`

### Waytorch Tree
- **IDs**: `kruemblegard:waytorch_tree_*`
- **Appearance**: Glowing nodes along trunk like lanterns.
- **Use**: Renewable light sources.
- **Mechanics**: no special mechanics yet.
- **Core textures**:
  - `assets/kruemblegard/textures/block/waytorch_tree_log.png`
  - `assets/kruemblegard/textures/block/waytorch_tree_log_top.png`
  - `assets/kruemblegard/textures/block/waytorch_tree_planks.png`
  - `assets/kruemblegard/textures/block/waytorch_tree_leaves.png`
  - `assets/kruemblegard/textures/block/waytorch_tree_sapling.png`
