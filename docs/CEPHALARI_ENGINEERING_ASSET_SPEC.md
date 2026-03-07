# Cephalari Engineering Texture & Asset Specification — Hand‑Off Document

**Version:** 1.0  
**Author:** Jarin  
**Purpose:** Provide all instructions required for any AI assistant to continue generating Cephalari engineering block textures, JSONs, icons, UI, and codex entries.

---

## 1. Palette (HEX‑LOCKED)

All textures must use **ONLY** the following hex codes.

### 1.1 Ceramic (Bio‑Ceramic Casing)
- `#E9DCCB` — Ceramic Light
- `#D4C3B1` — Ceramic Mid
- `#B8A795` — Ceramic Dark
- `#9C8A78` — Ceramic Deep
- `#6E5F52` — Ceramic Linework

### 1.2 Membrane (Bio‑Elastic Actuators)
- `#F4A9B8` — Membrane Highlight
- `#D97A8A` — Membrane Mid
- `#B85C6D` — Membrane Shadow
- `#8E3D4E` — Membrane Deep
- `#5A2A33` — Membrane Linework

### 1.3 Crystal (Wayfall Pressure Crystals)
- `#E8FFFF` — Crystal Bright
- `#A5EAF7` — Crystal Mid
- `#6AB5C8` — Crystal Shadow
- `#3E7A8A` — Crystal Deep
- `#1F4A55` — Crystal Linework

### 1.4 Shell‑Coral (Gears, Turbines)
- `#F2E4D6` — Shell Highlight
- `#D9C2B0` — Shell Mid
- `#B89F8C` — Shell Shadow
- `#8F6F5A` — Shell Deep
- `#5C4638` — Shell Linework

### 1.5 Atmospheric (Swirls, Air Effects)
- `#FFFFFF` — Air Bright
- `#C0F0FF` — Air Mid
- `#8AC2D1` — Air Shadow
- `#4F7F8C` — Air Deep
- `#2C4A52` — Air Linework

### 1.6 Biome Tint Anchors (Multipliers)
- `#A8FFCC` — Lush
- `#FFD8A8` — Arid
- `#A8D4FF` — Cold
- `#FF8AA8` — Corrupted
- `#88A8FF` — Deep Wayfall

---

## 2. Texture Format (Mandatory)

All textures must be delivered as:

### 2.1 JSON array of 32 arrays × 32 hex codes

Example:

```json
{
  "top": [
    ["#E9DCCB", "#E9DCCB" /* ...32 columns */],
    /* ...32 rows */
  ]
}
```

### 2.2 One JSON object per face

Faces must be named:
- `top`
- `bottom`
- `north`
- `south`
- `east`
- `west`

### 2.3 No compression, no shorthand

Every pixel must be explicitly written.

---

## 3. Workflow (Mandatory)

For each block, the assistant must output:
- ASCII mockup (structure only)
- Full 32×32 hex‑pixel JSON for each face
- Block model JSON
- Animation JSON (if applicable)
- Item icon (32×32 hex‑pixel JSON)

Blocks must be done one at a time.

---

## 4. ASCII Mockup Rules

- Use `.` for background
- Use `#` for primary material mass
- Use `@` for membrane
- Use `*` for crystal
- Use `=` for shell‑coral
- Use `~` for atmospheric swirls

ASCII mockups are visual only and do not need to match palette.

---

## 5. Block Model JSON Rules

- Use standard Minecraft block model format
- `"parent": "block/cube"` unless otherwise specified
- `"textures"` must reference face names
- No UV rotation unless required
- No tint indices unless biome variants are being generated

---

## 6. Animation JSON Rules

If a block animates:
- Use `"animation"` object
- `"frametime"` must be 2–6 ticks depending on block
- Frames must reference separate texture files or arrays
- No interpolation

---

## 7. Item Icon Rules

- 32×32 hex‑pixel JSON
- Must visually match the block’s front face
- Must use the same palette
- Must include crystal seam nodes if present

---

## 8. Blocks in the Suite

The assistant must generate assets for the following blocks:
- Pressure Conduit
- Membrane Pump
- Pressure Turbine
- Spiral Gearbox
- Vent Piston
- Atmospheric Compressor
- Conveyor Membrane
- Buoyancy Lift Platform
- Pressure Valve

Each block has already been blueprint‑specified earlier in the conversation.

---

## 9. Remaining Tasks for the Next AI

The next assistant must:
- Continue generating textures block‑by‑block
- Follow the palette exactly
- Follow the JSON format exactly
- Follow the ASCII mockup rules
- Follow the animation rules
- Follow the block model rules
- Produce item icons
- Produce biome‑tinted variants
- Produce UI textures
- Produce the Cephalari Engineering Codex

---

## 10. Hand‑Off Summary

This document contains everything required for any AI assistant to continue the work without referencing prior conversation.

It defines:
- The palette
- The texture format
- The workflow
- The block list
- The JSON rules
- The ASCII rules
- The animation rules
- The item icon rules
- The biome tint rules

Any AI can now continue the pipeline.

---

# Cephalari Automated Texture Generation Schema

**Version:** 1.0  
**Purpose:** Provide a machine‑readable, deterministic specification for generating Cephalari engineering textures, models, animations, icons, and variants.

## 1. Palette Definitions

```json
{
  "palette": {
    "ceramic": {
      "light":   "#E9DCCB",
      "mid":     "#D4C3B1",
      "dark":    "#B8A795",
      "deep":    "#9C8A78",
      "line":    "#6E5F52"
    },
    "membrane": {
      "highlight": "#F4A9B8",
      "mid":       "#D97A8A",
      "shadow":    "#B85C6D",
      "deep":      "#8E3D4E",
      "line":      "#5A2A33"
    },
    "crystal": {
      "bright": "#E8FFFF",
      "mid":    "#A5EAF7",
      "shadow": "#6AB5C8",
      "deep":   "#3E7A8A",
      "line":   "#1F4A55"
    },
    "shell": {
      "highlight": "#F2E4D6",
      "mid":       "#D9C2B0",
      "shadow":    "#B89F8C",
      "deep":      "#8F6F5A",
      "line":      "#5C4638"
    },
    "air": {
      "bright": "#FFFFFF",
      "mid":    "#C0F0FF",
      "shadow": "#8AC2D1",
      "deep":   "#4F7F8C",
      "line":   "#2C4A52"
    },
    "biome_tint": {
      "lush":      "#A8FFCC",
      "arid":      "#FFD8A8",
      "cold":      "#A8D4FF",
      "corrupted": "#FF8AA8",
      "deep":      "#88A8FF"
    }
  }
}
```

## 2. Texture Output Format

Each block face must be output as:

```json
{
  "face_name": [
    ["#HEX", "#HEX" /* ...32 columns */],
    /* ...32 rows */
  ]
}
```

Where `face_name` ∈ `top`, `bottom`, `north`, `south`, `east`, `west`.

## 3. Block Generation Workflow

```json
{
  "block": "<block_name>",
  "ascii_mockup": "<32x32 ASCII>",
  "textures": {
    "top":    "32x32_hex_array",
    "bottom": "32x32_hex_array",
    "north":  "32x32_hex_array",
    "south":  "32x32_hex_array",
    "east":   "32x32_hex_array",
    "west":   "32x32_hex_array"
  },
  "block_model_json": { "...": "minecraft_block_model" },
  "animation_json": { "...": "minecraft_animation_json_optional" },
  "item_icon_32x32": "32x32_hex_array"
}
```

Blocks must be generated one at a time.

## 4. ASCII Mockup Rules

```json
{
  "ascii_symbols": {
    ".": "ceramic_light",
    "#": "ceramic_mid_or_dark",
    "@": "membrane",
    "*": "crystal",
    "=": "shell_coral",
    "~": "air_swirl"
  }
}
```

## 5. Block Model JSON Schema

```json
{
  "parent": "block/cube",
  "textures": {
    "top":    "<namespace>:block/<block_name>_top",
    "bottom": "<namespace>:block/<block_name>_bottom",
    "north":  "<namespace>:block/<block_name>_north",
    "south":  "<namespace>:block/<block_name>_south",
    "east":   "<namespace>:block/<block_name>_east",
    "west":   "<namespace>:block/<block_name>_west"
  }
}
```

## 6. Animation JSON Schema

```json
{
  "animation": {
    "frametime": 4,
    "frames": [
      "<namespace>:block/<block_name>_frame0",
      "<namespace>:block/<block_name>_frame1"
    ]
  }
}
```

## 7. Item Icon Schema

```json
{
  "item_icon_32x32": [
    ["#HEX", "#HEX" /* ... */],
    /* ... */
  ]
}
```

## 8. Biome‑Tint Variant Schema

```json
{
  "biome_variant": {
    "biome": "<lush|arid|cold|corrupted|deep>",
    "tint_hex": "#RRGGBB",
    "base_texture": "<reference>",
    "output_texture": "32x32_hex_array"
  }
}
```

Tint rule:

`final = clamp(base * tint / 255)`

## 9. Blocks To Generate

```json
{
  "blocks": [
    "pressure_conduit",
    "membrane_pump",
    "pressure_turbine",
    "spiral_gearbox",
    "vent_piston",
    "atmospheric_compressor",
    "conveyor_membrane",
    "buoyancy_lift_platform",
    "pressure_valve"
  ]
}
```

## 10. Automation Logic

A fully automated assistant must:
- Read the block blueprint
- Generate ASCII mockup
- Generate 6 faces of 32×32 hex textures
- Generate block model JSON
- Generate animation JSON (if needed)
- Generate item icon
- Generate biome variants
- Repeat for next block

All outputs must be deterministic.
