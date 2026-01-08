# Wayfall tree Material Bible 

This document combines:
- **Wayfall Flora Material Bible --- COMPLETE (14 Trees)**
- **Wayfall Flora Material Bible --- PRODUCTION EDITION**
- **Wayfall Flora Material Bible (Compiled Complete Reference)** (verbatim stub text preserved)

---

## Table of Contents

- [Global Rules](#global-rules)
- [Global Color & Emissive Rules](#global-color--emissive-rules)
- [Emissive Mask Intensity Levels](#emissive-mask-intensity-levels)
- [Master Palette (RGB)](#master-palette-rgb)
- [Atlas Rules (Critical)](#atlas-rules-critical)
- [Variant States (All Trees)](#variant-states-all-trees)
- [Tree Families (14)](#tree-families-14)
- [Implementation Notes](#implementation-notes)
- [Appendix: Compiled Complete Reference (Stub)](#appendix-compiled-complete-reference-stub)

---

## Global Rules

-   No organic green coloration
-   Stone, ash, crystal, or void-influenced materials only
-   Log tops never use organic growth rings
-   Every family must read instantly different in silhouette and surface
    language
-   If the silhouette reads without texture noise, the design is correct.

---

## Global Color & Emissive Rules

-   Base saturation: LOW
-   Accent saturation: CONTROLLED
-   Hue drift outside the Master Palette is not allowed.
    Minor value variation (±10 RGB) is acceptable; hue shifts are not.
-   Emissive is never white; always tinted
-   Emissive pixels must be edge-contained (no glow bleed in base
    texture)

### Emissive Mask Intensity Levels

-   Level 0: No glow (Fallbark, Driftwood)
-   Level 1: Faint (Cairn, Ashbloom)
-   Level 2: Active (Wayroot, Echowood, Waytorch)
-   Level 3: Luminous (Wayglass, Shardbark)

---

## Master Palette (RGB)

| Family | Base RGB | Accent RGB | Emissive RGB |
|---|---:|---:|---:|
| Ashbloom | 190,190,185 | 160,160,155 | none |
| Cairn | 165,165,160 | 140,140,135 | 120,115,110 |
| Driftwillow | 175,170,165 | 150,145,140 | none |
| Driftwood | 200,195,185 | 170,165,155 | none |
| Echowood | 180,180,185 | 160,165,175 | 120,140,170 |
| Fallbark | 170,160,150 | 140,120,100 | none |
| Faultwood | 155,155,160 | 110,110,115 | 90,100,120 |
| Glimmerpine | 170,185,200 | 140,160,190 | 130,170,210 |
| Hollowway | 145,145,150 | 90,95,110 | 80,90,120 |
| Monument Oak | 160,155,150 | 130,120,110 | 110,105,100 |
| Shardbark | 175,185,195 | 150,170,200 | 150,190,255 |
| Wayglass | 200,220,235 | 170,200,230 | 160,210,255 |
| Wayroot | 155,160,165 | 120,150,160 | 110,170,190 |
| Waytorch | 130,120,110 | 160,120,80 | 255,170,90 |

---

## Atlas Rules (Critical)

-   Each tree family occupies ONE contiguous atlas strip
-   Logs + log_top must align vertically
-   Planks, stairs, slabs share edge logic
-   Doors are 2× height but reuse plank palette
-   Trapdoors must visually match planks when closed

### Forbidden

-   Mixing palettes across families
-   Sharing emissive masks
-   Reusing log_top geometry

---

## Universal Log Top Rules (<type>_log_top)

- Never circular growth rings (these are not biological trees)
- Log tops must visually explain:
    - material origin
    - internal structure
    - energy state
- All tops should tile seamlessly on all four edges.
- Log tops must tile seamlessly on all four horizontal edges with no visible seam logic.

---

## Variant States (All Trees)

- Not all families must support all variants; unused variants must not be stubbed.

### Cracked

-   +20% contrast
-   Added fracture pixels
-   Reduced emissive clarity

### Stabilized

-   Cleaner edges
-   Reduced noise
-   Slight emissive refinement

### Void-Touched

-   Darkened base
-   Purple-blue emissive override
-   Edge erosion

### Ancient

-   Desaturated
-   Dust overlays
-   Micro-chipping

---

## Tree Families (14)

This section fully defines **all 14 tree (wood family) types** present
in Krümblegård / Wayfall. It includes appearance language for:
- logs
- stripped logs
- log tops
- wood blocks
- planks
- stairs, slabs
- fences, gates
- doors, trapdoors
- leaves (visual-only guidance)

This is a **single source of truth** for texture artists and block
implementation.

---

### 1. Ashbloom

**Theme:** Ash-coated starter wood

-   **Log:** Pale ash-gray bark, powdery soot texture
-   **Stripped Log:** Smooth, chalky stone-wood core
-   **Log Top:** Soft radial ash bloom, no rings
-   **Planks:** Light gray boards dusted with ash
-   **Doors/Trapdoors:** Simple slab construction, utilitarian
-   **Leaves:** Ash-white, brittle, slightly translucent

**Palette:** Base 190,190,185 · Accent 160,160,155 · Emissive none

---

### 2. Cairn Tree

**Theme:** Memorial stone assemblage

**Unstripped Log — Cairn Log**

Material Identity: Stacked memorial stones.

Surface

Each “ring” is a separate stone.

Chips, erosion, inscriptions.

No repeating stone faces.

Color

Mixed stone palette:

Ash gray

Bone white

Muted slate

Occasional faded pigment stains.

**Stripped Log — Stripped Cairn Log**

Material Identity: Exposed memorial core.

Surface

Inner stones revealed.

More inscriptions and symbol marks.

Smooth from long handling and ritual use.

Color

Warmer stone tones

Fewer dark shadows

Behavior Read

Sacred.

Intimate, not structural.

**Cairn Log Top (CAIRN_LOG_TOP)**

Appearance

Stacked stone ends visible.

Each stone has unique wear.

No symmetry.

Color

Mixed memorial stone palette.

Read

“Each layer remembers.”

**Cairn Wood (all sides barked) (CAIRN_WOOD)**

Stones wrap fully around.

No bark differentiation.

**Planks — Cairn Planks**

Cut stone slabs.

Chisel marks visible.

Inscription fragments at edges.

**Cairn Stairs / Slabs (CAIRN_STAIRS / CAIRN_SLAB)**

Cut memorial stone.

Chisel marks visible.

Occasional inscription fragments.

**Cairn Fence / Fence Gate (CAIRN_FENCE / CAIRN_FENCE_GATE)**

Grave-marker-like posts.

Gate resembles a ritual barrier.

**Cairn Door (CAIRN_DOOR)**

Design

Twin memorial slabs.

Etched symbols near base.

Behavior Read

Sacred threshold, not utility.

**Cairn Trapdoor (CAIRN_TRAPDOOR)**

Stone memorial plate.

Worn smooth by time.

**Palette:** Base 165,165,160 · Accent 140,140,135 · Emissive 120,115,110

---

### 3. Driftwillow

**Theme:** Weightless, hanging decay

-   **Log:** Smooth eroded bark, vertical stretch marks
-   **Stripped Log:** Fibrous stone core, elongated pores
-   **Log Top:** Oval void with trailing grain
-   **Planks:** Long, soft-edged boards
-   **Doors:** Draped slab look
-   **Leaves:** Long hanging mineral fronds

**Palette:** Base 175,170,165 · Accent 150,145,140 · Emissive none

---

### 4. Driftwood

**Theme:** Bleached, water-worn

-   **Log:** Pale weathered wood, rounded erosion
-   **Stripped Log:** Clean light core
-   **Log Top:** Smoothed spiral erosion pattern
-   **Planks:** Soft beige-gray
-   **Leaves:** Sparse, salt-dried

**Palette:** Base 200,195,185 · Accent 170,165,155 · Emissive none

---

### 5. Echowood

**Unstripped Log — Echowood Log**

Material Identity: Frozen sound waves.

Surface

Concentric ripple rings wrapping the trunk.

Absolutely smooth finish.

Rings repeat rhythmically.

Color

Soft gray base

Blue-lavender ripples

No cracks, no damage

**Stripped Log — Stripped Echowood Log**

Material Identity: Resonance core.

Surface

Perfectly smooth stone cylinder.

Ripple pattern becomes vertical wave lines.

Subtle internal glow when active.

Color

Slightly brighter gray

Muted cyan highlights

Behavior Read

Reactive.

Engineered for sound conduction.

**Echowood Log Top (ECHOWOOD_LOG_TOP)**

Appearance

Perfect circular ripple pattern.

No cracks, no noise.

Concentric wave geometry.

Color

Light gray base

Soft blue-violet ripples

Read

“Sound fossilized.”

**Echowood Wood (all sides barked) (ECHOWOOD_WOOD)**

Ripple pattern wraps all sides.

Perfect symmetry.

**Planks — Echowood Planks**

Flat stone panels.

Ripple echoes visible across seams.

Almost polished appearance.

**Echowood Stairs / Slabs (ECHOWOOD_STAIRS / ECHOWOOD_SLAB)**

Polished stone panels.

Ripple echoes align across blocks.

Almost reflective.

**Echowood Fence / Fence Gate (ECHOWOOD_FENCE / ECHOWOOD_FENCE_GATE)**

Smooth stone struts.

Subtle waveform grooves.

Gate hums visually.

**Echowood Door (ECHOWOOD_DOOR)**

Design

Single seamless slab.

Ripple pattern flows vertically.

Behavior Read

Slides silently.

**Echowood Trapdoor (ECHOWOOD_TRAPDOOR)**

Thin polished panel.

Ripple pattern intensifies when opened.

**Palette:** Base 180,180,185 · Accent 160,165,175 · Emissive 120,140,170

---

### 6. Fallbark

**Unstripped Log — Fallbark Log**

Material Identity: Failing stone armor.

Surface

Vertical bark plates stacked loosely.

Deep shadow gaps between plates.

Hairline cracks and flaking edges.

Color

Dusty gray base

Rust-tan undertones

Dark charcoal interior gaps

**Stripped Log — Stripped Fallbark Log**

Material Identity: Fragile stone spine.

Surface

Tall, brittle vertical striations.

Core looks cracked, unstable.

No glow — completely inert.

Color

Chalky gray

Powdered stone highlights

Subtle crumbling edges

Behavior Read

Unsafe.

Looks like it could collapse if struck.

**Fallbark Log Top (FALLBARK_LOG_TOP)**

Appearance

Jagged, collapsed cross-section.

Uneven bark plates visible around edge.

Crumbling stone dust at center.

Color

Chalk gray interior

Dark bark fragments

Read

“This will not last.”

**Fallbark Wood (all sides barked) (FALLBARK_WOOD)**

Bark plates on all faces.

Deep shadow gaps everywhere.

**Planks — Fallbark Planks**

Thin, brittle slabs.

Uneven edges.

Hairline fracture lines across surface.

Visually weaker than other planks.

**Fallbark Stairs / Slabs (FALLBARK_STAIRS / FALLBARK_SLAB)**

Thin, fragile stone slabs.

Micro-cracks across surfaces.

Corners look ready to snap.

**Fallbark Fence / Fence Gate (FALLBARK_FENCE / FALLBARK_FENCE_GATE)**

Uneven stone posts.

Misaligned heights.

Gate looks unsafe and temporary.

**Fallbark Door (FALLBARK_DOOR)**

Design

Composite of stacked bark plates.

Gaps visible between layers.

Behavior Read

Feels like it might collapse when used.

**Fallbark Trapdoor (FALLBARK_TRAPDOOR)**

Thin layered stone.

Visible fracture lines.

No glow.

**Palette:** Base 170,160,150 · Accent 140,120,100 · Emissive none

---

### 7. Faultwood

**Theme:** Seismic fracture wood

-   **Log:** Jagged cracks with dark fault lines
-   **Stripped Log:** Exposed stressed core
-   **Log Top:** Starburst fracture
-   **Planks:** Reinforced stone boards
-   **Doors:** Interlocking slab plates
-   **Leaves:** Broken stone clusters

**Palette:** Base 155,155,160 · Accent 110,110,115 · Emissive 90,100,120

---

### 8. Glimmerpine

**Theme:** Cold luminous pine

-   **Log:** Vertical crystalline grain
-   **Stripped Log:** Bright reflective core
-   **Log Top:** Radiating needle pattern
-   **Planks:** Cool-toned refined boards
-   **Leaves:** Sharp glowing needles

**Palette:** Base 170,185,200 · Accent 140,160,190 · Emissive 130,170,210

---

### 9. Hollowway Tree

**Theme:** Void-centered storage tree

-   **Log:** Split bark revealing darkness
-   **Stripped Log:** Hollow inner channel
-   **Log Top:** Open void ring
-   **Planks:** Framed hollow panels
-   **Doors:** Portal-like slab
-   **Leaves:** Sparse, curled mineral leaves

**Palette:** Base 145,145,150 · Accent 90,95,110 · Emissive 80,90,120

---

### 10. Monument Oak

**Theme:** Ancient historical titan

-   **Log:** Massive carved bark with etchings
-   **Stripped Log:** Polished ancient stone core
-   **Log Top:** Layered age rings (non-organic)
-   **Planks:** Heavy monumental slabs
-   **Doors:** Temple-gate style
-   **Leaves:** Large stone plates

**Palette:** Base 160,155,150 · Accent 130,120,110 · Emissive 110,105,100

Note: “Rings” must be architectural strata, not circular growth bands.

---

### 11. Shardbark Pine

**Theme:** Weaponized crystal pine

-   **Log:** Razor-edged crystal bark
-   **Stripped Log:** Dense crystal spine
-   **Log Top:** Exploded shard geometry
-   **Planks:** Faceted boards
-   **Doors:** Angular crystal panels
-   **Leaves:** Needle shards

**Palette:** Base 175,185,195 · Accent 150,170,200 · Emissive 150,190,255

---

### 12. Wayglass

**Unstripped Log — Wayglass Log**

Material Identity: Crystalline growth.

Surface

Faceted crystal exterior.

Internal fractures refract light.

Sharp, clean edges.

Color

Translucent cyan

Violet internal highlights

Clear-to-cloudy gradients

Transparency rule: Wayglass transparency is refractive, not empty; no fully clear pixels.

**Stripped Log — Stripped Wayglass Log**

Material Identity: Prismatic core.

Surface

Clean crystal core exposed.

Fewer fractures, more transparency.

Bright light channel down center.

Color

Near-clear crystal

Strong cyan/violet refraction

Behavior Read

Highly energetic.

Designed to conduct magic/light.

**Wayglass Log Top (WAYGLASS_LOG_TOP)**

Appearance

Prismatic crystal cross-section.

Radiating light refraction lines.

No solid “center”.

Color

Clear → cyan → violet gradients.

Read

“Light trapped in matter.”

**Wayglass Wood (all sides barked) (WAYGLASS_WOOD)**

Faceted crystal on all sides.

Internal refractions visible.

**Planks — Wayglass Planks**

Crystal panels.

Hard seams.

Light bleeds between blocks.

**Wayglass Stairs / Slabs (WAYGLASS_STAIRS / WAYGLASS_SLAB)**

Crystal planes.

Hard edges.

Light bleeds through seams.

**Wayglass Fence / Fence Gate (WAYGLASS_FENCE / WAYGLASS_FENCE_GATE)**

Thin crystal pylons.

Floating connector beams.

**Wayglass Door (WAYGLASS_DOOR)**

Design

Tall prismatic panel.

Vertical light core.

Behavior Read

Phases open like refracted light.

**Wayglass Trapdoor (WAYGLASS_TRAPDOOR)**

Thin crystal plate.

Brightens when active.

**Palette:** Base 200,220,235 · Accent 170,200,230 · Emissive 160,210,255

---

### 13. Wayroot

**Unstripped Log — Wayroot Log**

Material Identity: Animated monolith bark.

Surface

Stratified stone bark with horizontal compression lines.

Cracks form angular rune-like geometry.

Fractures glow faint cyan/blue from within.

Color

Base: slate gray

Accent: cold teal veins

Glow: dim cyan (1–2 pixels wide)

Edge Logic

Corners look chipped, never rounded.

Bark layers appear to “float” microscopically apart.

**Stripped Log — Stripped Wayroot Log**

Material Identity: Arcane stone core.

Surface

Smooth, carved stone surface.

Vertical rune channels replacing bark cracks.

Core glow is stronger but contained.

Color

Pale stone gray base

Clean cyan rune channels

Less noise than unstripped

Behavior Read

Feels engineered, stabilized.

Looks suitable for precision constructs.

**Wayroot Log Top (WAYROOT_LOG_TOP)**

Appearance

Fractured stone cross-section.

Angular shard geometry radiating outward.

Central glowing nexus crack (not a ring).

Color

Dark slate outer stone

Cyan glow lines converging inward

Read

“This was never alive. It was anchored.”

**Wayroot Wood (all sides barked) (WAYROOT_WOOD)**

Continuous stratified stone on all faces.

No visible “top” logic.

**Planks — Wayroot Planks**

Rectangular stone slabs.

Clean seams.

Subtle glowing joins between blocks.

No wood grain — architectural stone logic only.

**Wayroot Stairs / Slabs (WAYROOT_STAIRS / WAYROOT_SLAB)**

Thick stone steps.

Subtle floating seam glow at joins.

Underside shows fractured stone, not flat cuts.

**Wayroot Fence / Fence Gate (WAYROOT_FENCE / WAYROOT_FENCE_GATE)**

Heavy stone pylons.

Cyan-lit runic locking seams.

Gate appears to phase open, not swing.

**Wayroot Door (WAYROOT_DOOR)**

Design

Monolithic stone slab split vertically.

Central rune column glows when active.

Behavior Read

Door slides or phases, not hinged.

**Wayroot Trapdoor (WAYROOT_TRAPDOOR)**

Inset stone plate.

Fractured geometry pattern.

Glow visible through seams when open.

**Palette:** Base 155,160,165 · Accent 120,150,160 · Emissive 110,170,190

---

### 14. Waytorch Tree

**Theme:** Living lantern tree

-   **Log:** Dark bark with embedded glowing nodes
-   **Stripped Log:** Exposed light channels
-   **Log Top:** Central ember core
-   **Planks:** Warm-lit seams
-   **Doors:** Lantern-panel doors
-   **Leaves:** Small glowing pods

**Palette:** Base 130,120,110 · Accent 160,120,80 · Emissive 255,170,90

---

## Leaf / Foliage Material Rules

Never organic green.

- Leaves must read as mineral, crystal, or fabricated matter — never fibrous.
- Noise must be minimal; silhouette carries the identity.

Shapes are:

shards

slabs

banners

Transparency is allowed only for Wayglass.

---

## Final Artist Handoff Notes

If someone follows this document:

Every tree will be recognizable at a glance.

Stripped variants will feel purposeful, not cosmetic.

Wood families will feel engineered, not decorative.

## Implementation Notes

All families support:
- planks, stairs, slabs
- fence, fence gate
- door, trapdoor
- pressure plate, button (visual match planks)

Before shipping any block:
- Reads correctly at 16×16
- Matches palette table
- Emissive obeys rules
- Atlas alignment verified

Palette values are authoritative and may be used by procedural texture generators.

---

## Appendix: Compiled Complete Reference (Stub)

# Wayfall Flora Material Bible

(Compiled Complete Reference)

\[Content compiled from prior messages...\]
