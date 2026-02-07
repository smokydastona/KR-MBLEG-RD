# Wayfall Plant Material Bible

This document is the **single source of truth** for how **Wayfall plants** should look and read at a material level.

It is intended to match the **detail level and enforceable rules** of [docs/Wood_Material_Bible.md](Wood_Material_Bible.md), but for **non-tree flora**.

Tooling:
- This palette is a design/spec reference. (No plant texture generator script is currently checked into `tools/`.)

If you add/rename/remove a plant block/item/texture, update both:
- [docs/FLORA_REFERENCE.md](FLORA_REFERENCE.md) (gameplay + IDs)
- this file (visual language + palette)

---

## Table of Contents

- [Global Rules](#global-rules)
- [Global Color & Emissive Rules](#global-color--emissive-rules)
- [Emissive Mask Intensity Levels](#emissive-mask-intensity-levels)
- [Master Palette (RGB)](#master-palette-rgb)
- [Pixel/Texture Rules](#pixeltexture-rules)
- [Plant Set Definitions](#plant-set-definitions)

---

## Global Rules

- **No organic “overworld green.”** Wayfall flora can be plantlike in shape, but must read as **ash/stone/void/crystal influenced**.
- Greens are permitted only when **desaturated and mineralized**; no living grass hues.
- **Every plant must be instantly distinct** by silhouette and surface language, not just by hue.
- **Emissive is intentional**: no random glitter everywhere; emissive pixels must be **edge-contained**.
- **Readable at 16×16**: big shapes first, micro-noise last.
- If the silhouette reads without noise, the texture is correct.

---

## Global Color & Emissive Rules

- Base saturation: **low**
- Accent saturation: **controlled**
- Hue drift outside the Master Palette is not allowed.
	Minor value shifts (±10 RGB) are acceptable; hue shifts are not.
- Emissive is **never pure white**; always tinted
- If a plant has an emissive channel conceptually, its emissive should cluster into **nodes/veins/specks**, not full-face wash.
- Emissive pixels must not touch texture edges unless the plant conceptually emits along boundaries (e.g., runes crossing blocks).

---

## Emissive Mask Intensity Levels

Use these levels when designing textures (even if the current implementation uses a single PNG):

- **Level 0:** none (purely matte, dead/ash/stone)
- **Level 1:** faint (rare specks, subtle veins)
- **Level 2:** active (clear nodes, readable in darkness)
- **Level 3:** luminous (strongest glow, but still tinted and bounded)

---

## Master Palette (RGB)

This palette table is the canonical “design contract” per plant.

| Plant | Base RGB | Accent RGB | Emissive RGB | Emissive Level |
|---|---:|---:|---:|---:|
| Ashmoss | 165,170,168 | 125,130,128 | none | 0 |
| Runegrowth | 85,90,95 | 135,140,145 | 115,170,200 | 2 |
| Voidfelt | 28,30,36 | 55,60,72 | 95,70,140 | 1 |
| Fault Dust | 150,145,140 | 110,105,100 | none | 0 |
| Cairn Moss | 120,125,130 | 150,155,160 | 120,170,210 | 2 |
| Wispstalk | 40,70,60 | 70,120,110 | 140,255,250 | 2 |
| Gravevine | 30,55,28 | 70,90,65 | none | 0 |
| Echocap | 200,195,185 | 150,60,170 | 60,220,220 | 1 |
| Runebloom (all variants) | 55,80,70 | 110,120,125 | per-variant (see Runebloom) | 2 |
| Soulberry Shrub | 35,95,55 | 80,130,70 | 120,210,255 | 1 |
| Ghoulberry Shrub | 55,95,55 | 80,130,70 | 180,90,220 | 2 |
| Driftbloom | 95,105,110 | 170,175,185 | 190,220,255 | 2 |
| Dustpetal | 165,160,155 | 190,185,180 | none | 0 |
| Griefcap | 75,65,80 | 120,95,130 | 150,190,255 | 1 |
| Transit Fern | 55,75,70 | 85,125,120 | 120,190,210 | 1 |
| Misstep Vine | 35,70,40 | 20,55,28 | none | 0 |
| Waygrasp Vine | 40,85,45 | 95,115,105 | 120,170,190 | 2 |
| Voidcap Briar | 25,25,30 | 60,55,70 | 130,70,180 | 2 |

Notes:
- “Fault Dust” appears in the mod as a surface block; it’s listed here because it’s part of the Wayfall flora/surface language.
- Runebloom has multiple texture variants already; its emissive is intentionally “petal glow”, not stalk glow.

---

## Pixel/Texture Rules

### 1) Transparent plants (cross models)
Applies to: Wispstalk, Gravevine, Echocap, Runebloom, shrubs, vines, most small plants.

- Background must be fully transparent.
- At least 30–40% of the texture must remain transparent.
- Favor **chunky clusters** (3–6 pixel blobs) over single-pixel noise.
- Avoid perfect symmetry.

### 2) Full-block surface covers
Applies to: Ashmoss, Runegrowth, Voidfelt, Fault Dust.

- Texture must tile on all edges.
- Use **broad value shapes** (8×8 scale) first, then medium noise (4×4), then fine noise (1–2 px).
- Never rely on high saturation; contrast should come from **value** and **pattern**.
- Gradients must wrap seamlessly; no directional lighting implied.

### 3) “Wayfall runes” rule
If runes appear (Runegrowth, Runebloom, Waygrasp, etc):
- Runes are **not letters**; use abstract strokes.
- Runes should be sparse and readable (don’t cover the whole texture).

---

## Plant Set Definitions

Below are canonical “material identities” for each plant.

### Ashmoss
**Type:** Moss block (full cube)

**Material Identity:** Powdery ash felt with compacted soot.

**Surface language**
- Matte, dusty, low-contrast.
- Occasional darker soot clumps.

**Emissive**
- Level 0.

**Texture file(s)**
- `assets/kruemblegard/textures/block/ashmoss.png`

---

### Runegrowth
**Type:** Grass block (full cube)

**Material Identity:** Mineral growth that etches runes into the surface.

Gameplay notes (vanilla-like):
- Spreads onto nearby Wayfall dirt blocks while powered (Fault Dust / Ashfall Loam).
- Drops Fault Dust unless Silk Touch.

**Surface language**
- Base is stone-dust with embedded hairline fractures.
- Accents are rune strokes and small junctions.

**Emissive**
- Level 2: rune junctions glow; strokes may be non-emissive or faint.

**Texture file(s)**
- `assets/kruemblegard/textures/block/runegrowth_top.png`
- `assets/kruemblegard/textures/block/runegrowth_side.png`

---

### Voidfelt
**Type:** Podzol (dirt family, full cube)

**Material Identity:** Light-absorbing felt-like growth that “drinks” brightness.

**Surface language**
- Very dark base.
- Soft fibrous directionality (subtle vertical/horizontal grain).
- Sparse violet flecks.

**Emissive**
- Level 1: rare purple motes.

**Texture file(s)**
- `assets/kruemblegard/textures/block/voidfelt_top.png`
- `assets/kruemblegard/textures/block/voidfelt_side.png`

---

### Fault Dust
**Type:** Dirt (full cube)

**Material Identity:** Pale mineral dust swept into fault lines.

**Surface language**
- Grainy, chalky texture with directional drift.
- Slight value gradient is allowed, but must tile.

**Emissive**
- Level 0.

**Texture file(s)**
- `assets/kruemblegard/textures/block/fault_dust.png`

---

### Cairn Moss
**Type:** Cross model plant / moss clump

**Material Identity:** Thick gray moss threaded with memorial-light veins.

**Surface language**
- Chunky moss pads, not leafy grass.
- Veins follow “cairn-like” segmented routes.
- Should read as a clump (negative space is required), not a flat carpet.

**Emissive**
- Level 2: vein nodes glow.

**Texture file(s)**
- `assets/kruemblegard/textures/block/cairn_moss.png`

---

### Wispstalk
**Type:** Cross model plant

**Material Identity:** Hollow reed with floating, cold flame bulbs.

**Silhouette rules**
- Tall and thin; a single stem dominates.
- Bulbs must be separated and readable.

**Surface language**
- Stem is dark teal/greenish but desaturated (Wayfall-safe).
- Bulbs are icy cyan with slight variation.

**Emissive**
- Level 2: bulbs glow.

**Texture file(s)**
- `assets/kruemblegard/textures/block/wispstalk.png`

---

### Gravevine
**Type:** Cross model plant

**Material Identity:** Vine that feeds on remains; bone flecks embedded.

**Silhouette rules**
- Twisting line with small side knots.

**Surface language**
- Base is deep, sick green-brown.
- Accent is bone/chalk specks (very sparse).

**Emissive**
- Level 0.

**Texture file(s)**
- `assets/kruemblegard/textures/block/gravevine.png`

---

### Echocap
**Type:** Cross model fungus

**Material Identity:** Resonant mushroom; cap has “note-speckle” surface.

**Silhouette rules**
- Wide cap, short stalk.

**Surface language**
- Cap uses purple-magenta accent but stays muted.
- Specks imply vibration, not glitter.

**Emissive**
- Level 1: small specks can glow faintly.

**Texture file(s)**
- `assets/kruemblegard/textures/block/echocap.png`

---

### Runebloom (variants 0–5)
**Type:** Cross model plant with variants

**Material Identity:** Flower that manifests a rune-spectrum (not natural petals).

**Variant rules**
- Each variant must read as the same species, different charge/hue.
- Keep stem consistent; change petal emissive color.

**Emissive**
- Level 2: petals glow; centers are dark.
- Stems and leaves must never be emissive.

**Texture file(s)**
- `assets/kruemblegard/textures/block/runebloom.png` (16-frame sprite sheet via `.png.mcmeta`)

**Per-variant emissive suggestions**
- 0: 120,200,255
- 1: 200,120,255
- 2: 255,180,90
- 3: 255,120,160
- 4: 130,255,170
- 5: 240,240,140

---

### Soulberry Shrub (stage 0–3)
**Type:** Cross model shrub with growth stages

**Material Identity:** Shrub with soul-charged berries; calm, clean glow.

**Stage rules**
- Stage 0: leaves only
- Stage 1–3: increasing berry count and clustering

**Emissive**
- Level 1: berries glow faintly.

**Texture file(s)**
- `assets/kruemblegard/textures/block/soulberry_shrub_stage0.png` … `stage3.png`

---

### Ghoulberry Shrub (stage 0–3)
**Type:** Cross model shrub with growth stages

**Material Identity:** Corrupted Soulberry; glow is unstable and violet.

**Stage rules**
- Keep silhouette similar to Soulberry, but with darker leaf mass and harsher berry colors.

**Emissive**
- Level 2: berries glow stronger than Soulberry.

**Texture file(s)**
- `assets/kruemblegard/textures/block/ghoulberry_shrub_stage0.png` … `stage3.png`

---

### Driftbloom
**Type:** Cross model medium plant

**Material Identity:** Floating petals held by static/levitation.

**Surface language**
- Petals should look slightly separated (gaps/negative space).
- Add 1–2 bright “lift motes” near the top.

**Emissive**
- Level 2: lift motes glow.

**Texture file(s)**
- `assets/kruemblegard/textures/block/driftbloom.png`

---

### Dustpetal
**Type:** Cross model small plant

**Material Identity:** Brittle ash-flower that crumbles.

**Surface language**
- Very light gray-beige petals with chipped edges.

**Emissive**
- Level 0.

**Texture file(s)**
- `assets/kruemblegard/textures/block/dustpetal.png`

---

### Griefcap
**Type:** Cross model fungus

**Material Identity:** Drooping fungus shedding tear-like spores.

**Surface language**
- “Droop” should be visible in silhouette (weight at ends).
- Add 2–3 bright spore droplets.

**Emissive**
- Level 1: droplets may glow faintly.

**Texture file(s)**
- `assets/kruemblegard/textures/block/griefcap.png`

---

### Transit Fern
**Type:** Cross model small/medium plant

**Material Identity:** Fractal leaf that implies motion and paths.

**Surface language**
- Leaflets should form repeating “path steps” (but not perfectly symmetric).

**Emissive**
- Level 1: tiny route pixels.

**Texture file(s)**
- `assets/kruemblegard/textures/block/transit_fern.png`

---

### Misstep Vine
**Type:** Cross model vine

**Material Identity:** Nearly invisible trip-vine.

**Surface language**
- Extremely thin strokes.
- Contrast must be low vs air, but still visible on stone backdrops.

**Emissive**
- Level 0.

**Texture file(s)**
- `assets/kruemblegard/textures/block/misstep_vine.png`

---

### Waygrasp Vine
**Type:** Cross model vine

**Material Identity:** Vine with slow-reaching tendrils and rune nodes.

**Surface language**
- Tendrils are thicker than Misstep.
- 2–4 node pixels form a “grasp” pattern.

**Emissive**
- Level 2: nodes glow.

**Texture file(s)**
- `assets/kruemblegard/textures/block/waygrasp_vine.png`

---

### Voidcap Briar
**Type:** Cross model fungus/briar

**Material Identity:** Spiky void-black growth with violet pressure points.

**Surface language**
- Spines (triangular pixels) and negative space.
- Rare violet points.

**Emissive**
- Level 2: pressure points glow.

**Texture file(s)**
- `assets/kruemblegard/textures/block/voidcap_briar.png`

---

## Appendix: Planned plants

Many additional Wayfall plants exist as design targets in [docs/FLORA_REFERENCE.md](FLORA_REFERENCE.md) but may not yet have assets/blocks.

When you implement them, add entries above and assign a palette row in the Master Palette.
