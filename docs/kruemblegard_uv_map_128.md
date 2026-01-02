# Krümblegård — 128×128 UV Map Spec

This spec is generated from the actual GeckoLib geometry file:
- `src/main/resources/assets/kruemblegard/geo/kruemblegard.geo.json`

Texture size: **128 × 128**

## UV Mapping Rules (Box UV)
Each cube uses Blockbench/Bedrock **box UV** with a single `uv: [u, v]` and `size: [x, y, z]`.

Face order (Minecraft convention):
1. North
2. East
3. South
4. West
5. Up
6. Down

Given:
- `u, v` = cube `uv` (top-left of the cube’s UV net)
- `x, y, z` = cube `size` (X width, Y height, Z depth)

The face UV rectangles are:
- **Up**: $(u + z,\ v)$ size $(x \times z)$
- **Down**: $(u + z + x,\ v)$ size $(x \times z)$
- **West**: $(u,\ v + z)$ size $(z \times y)$
- **North**: $(u + z,\ v + z)$ size $(x \times y)$
- **East**: $(u + z + x,\ v + z)$ size $(z \times y)$
- **South**: $(u + z + x + z,\ v + z)$ size $(x \times y)$

All coordinates below are absolute pixels in the 128×128 texture.

## UV Map Summary Grid (UV Origins)
These are the `uv` origins used by each cube in the model:
- `[0,0]` Torso — Stone Chunk A
- `[24,0]` Torso — Stone Chunk B
- `[48,0]` Torso — Stone Chunk C
- `[0,24]` Head — Main Stone
- `[32,24]` Right Arm — Segment A
- `[56,24]` Right Arm — Segment B
- `[72,24]` Left Arm — Segment A
- `[88,24]` Left Arm — Segment B
- `[0,48]` Whirlwind — Ring A
- `[48,48]` Whirlwind — Ring B
- `[67,8]` Debris — Floating Chunk
- `[66,8]` Top Stone — Chunk A
- `[70,10]` Top Stone — Chunk B
- `[67,9]` Lower Stone — Chunk A
- `[68,10]` Lower Stone — Chunk B

## Per-Cube UV Regions

### Torso — Stone Chunk A
Cube size: **4 × 10 × 6** (`x=4, y=10, z=6`)  
UV origin: **(0, 0)**

- North: **(6, 6)** (4 × 10)
- East: **(10, 6)** (6 × 10)
- South: **(16, 6)** (4 × 10)
- West: **(0, 6)** (6 × 10)
- Up: **(6, 0)** (4 × 6)
- Down: **(10, 0)** (4 × 6)

### Torso — Stone Chunk B
Cube size: **5 × 9 × 7** (`x=5, y=9, z=7`)  
UV origin: **(24, 0)**

- North: **(31, 7)** (5 × 9)
- East: **(36, 7)** (7 × 9)
- South: **(43, 7)** (5 × 9)
- West: **(24, 7)** (7 × 9)
- Up: **(31, 0)** (5 × 7)
- Down: **(36, 0)** (5 × 7)

### Torso — Stone Chunk C
Cube size: **4 × 8 × 4** (`x=4, y=8, z=4`)  
UV origin: **(48, 0)**

- North: **(52, 4)** (4 × 8)
- East: **(56, 4)** (4 × 8)
- South: **(60, 4)** (4 × 8)
- West: **(48, 4)** (4 × 8)
- Up: **(52, 0)** (4 × 4)
- Down: **(56, 0)** (4 × 4)

### Head — Main Stone
Cube size: **8 × 8 × 6** (`x=8, y=8, z=6`)  
UV origin: **(0, 24)**

- North: **(6, 30)** (8 × 8)
- East: **(14, 30)** (6 × 8)
- South: **(20, 30)** (8 × 8)
- West: **(0, 30)** (6 × 8)
- Up: **(6, 24)** (8 × 6)
- Down: **(14, 24)** (8 × 6)

**Rune Eyes (recommended placement)**
- Paint on the **North** face region **(6,30)** → size **8×8**.
- Eye size: **2×3** each.
- Glow gradient: `#7A4FFF` → `#4FD1FF`.

### Right Arm — Segment A
Cube size: **6 × 8 × 6** (`x=6, y=8, z=6`)  
UV origin: **(32, 24)**

- North: **(38, 30)** (6 × 8)
- East: **(44, 30)** (6 × 8)
- South: **(50, 30)** (6 × 8)
- West: **(32, 30)** (6 × 8)
- Up: **(38, 24)** (6 × 6)
- Down: **(44, 24)** (6 × 6)

### Right Arm — Segment B
Cube size: **4 × 6 × 4** (`x=4, y=6, z=4`)  
UV origin: **(56, 24)**

- North: **(60, 28)** (4 × 6)
- East: **(64, 28)** (4 × 6)
- South: **(68, 28)** (4 × 6)
- West: **(56, 28)** (4 × 6)
- Up: **(60, 24)** (4 × 4)
- Down: **(64, 24)** (4 × 4)

### Left Arm — Segment A
Cube size: **4 × 10 × 4** (`x=4, y=10, z=4`)  
UV origin: **(72, 24)**

- North: **(76, 28)** (4 × 10)
- East: **(80, 28)** (4 × 10)
- South: **(84, 28)** (4 × 10)
- West: **(72, 28)** (4 × 10)
- Up: **(76, 24)** (4 × 4)
- Down: **(80, 24)** (4 × 4)

### Left Arm — Segment B
Cube size: **3 × 6 × 3** (`x=3, y=6, z=3`)  
UV origin: **(88, 24)**

- North: **(91, 27)** (3 × 6)
- East: **(94, 27)** (3 × 6)
- South: **(97, 27)** (3 × 6)
- West: **(88, 27)** (3 × 6)
- Up: **(91, 24)** (3 × 3)
- Down: **(94, 24)** (3 × 3)

### Whirlwind — Ring A
Cube size: **16 × 4 × 16** (`x=16, y=4, z=16`)  
UV origin: **(0, 48)**

- North: **(16, 64)** (16 × 4)
- East: **(32, 64)** (16 × 4)
- South: **(48, 64)** (16 × 4)
- West: **(0, 64)** (16 × 4)
- Up: **(16, 48)** (16 × 16)
- Down: **(32, 48)** (16 × 16)

### Whirlwind — Ring B
Cube size: **12 × 3 × 12** (`x=12, y=3, z=12`)  
UV origin: **(48, 48)**

- North: **(60, 60)** (12 × 3)
- East: **(72, 60)** (12 × 3)
- South: **(84, 60)** (12 × 3)
- West: **(48, 60)** (12 × 3)
- Up: **(60, 48)** (12 × 12)
- Down: **(72, 48)** (12 × 12)

### Debris — Floating Chunk
Cube size: **4 × 4 × 6** (`x=4, y=4, z=6`)  
UV origin: **(67, 8)**

- North: **(73, 14)** (4 × 4)
- East: **(77, 14)** (6 × 4)
- South: **(83, 14)** (4 × 4)
- West: **(67, 14)** (6 × 4)
- Up: **(73, 8)** (4 × 6)
- Down: **(77, 8)** (4 × 6)

### Lower Stone — Chunk A
Cube size: **4 × 2 × 5** (`x=4, y=2, z=5`)  
UV origin: **(67, 9)**

- North: **(72, 14)** (4 × 2)
- East: **(76, 14)** (5 × 2)
- South: **(81, 14)** (4 × 2)
- West: **(67, 14)** (5 × 2)
- Up: **(72, 9)** (4 × 5)
- Down: **(76, 9)** (4 × 5)

### Lower Stone — Chunk B
Cube size: **5 × 2 × 4** (`x=5, y=2, z=4`)  
UV origin: **(68, 10)**

- North: **(72, 14)** (5 × 2)
- East: **(77, 14)** (4 × 2)
- South: **(81, 14)** (5 × 2)
- West: **(68, 14)** (4 × 2)
- Up: **(72, 10)** (5 × 4)
- Down: **(77, 10)** (5 × 4)

### Top Stone — Chunk A
Cube size: **5 × 3 × 6** (`x=5, y=3, z=6`)  
UV origin: **(66, 8)**

- North: **(72, 14)** (5 × 3)
- East: **(77, 14)** (6 × 3)
- South: **(83, 14)** (5 × 3)
- West: **(66, 14)** (6 × 3)
- Up: **(72, 8)** (5 × 6)
- Down: **(77, 8)** (5 × 6)

### Top Stone — Chunk B
Cube size: **3 × 2 × 4** (`x=3, y=2, z=4`)  
UV origin: **(70, 10)**

- North: **(74, 14)** (3 × 2)
- East: **(77, 14)** (4 × 2)
- South: **(81, 14)** (3 × 2)
- West: **(70, 14)** (4 × 2)
- Up: **(74, 10)** (3 × 4)
- Down: **(77, 10)** (3 × 4)

## Texturing Guidelines
### Stone Material
- Base: `#5A5A5A` → `#3A3A3A`
- Edge wear: lighter grays
- Cracks: `#1A1A1A`
- Moss hints: `#4C5E3A` (sparingly)

### Runes
- Glow gradient: `#7A4FFF` (core) → `#4FD1FF` (outer)
- Place runes on: head (eyes), torso stones, fists, arm segments

### Whirlwind
- Motion streaks: dark center, lighter edges
- Subtle radial blur lines
