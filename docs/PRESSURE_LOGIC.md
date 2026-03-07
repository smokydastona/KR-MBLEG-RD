# Cephalari Pressure‑Logic — Unified Design Document (Production Spec)

This document is the canonical design spec for **Pressure‑Logic** (Cephalari Pneumatic Engineering): a Create‑style animated machinery and mechanical power ecosystem native to Wayfall.

---

## 1. The Core Identity

**Pressure‑Logic = Cephalari Pneumatic Engineering**

Not redstone.
Not a logic system.
Not a competitor.

It is:
- A mechanical power system
- A pneumatic/atmospheric engineering suite
- A Create‑style animated machinery set
- A Wayfall‑native automation ecosystem
- A system that redstone can control

**Redstone = signals**
**Pressure‑Logic = motion, power, machinery**

This is the same relationship as Create’s redstone integration — but with Cephalari flavor.

---

## 2. Pressure‑Logic Machinery Set

(Create‑style animated blocks + Cephalari biology)

Below is the full block family, all animated, all mechanical, all pneumatic.

### A. Pressure Turbine
**Role:** Converts pressure into rotational energy

**Animation:**
- Spiral plates spin faster with higher pressure
- Membrane flutters at low pressure

**Uses:**
- Drives shafts
- Powers gearboxes
- Runs pumps and conveyors

### B. Membrane Pump
**Role:** Moves pressure through conduits

**Animation:**
- Expanding/contracting organic membrane
- Pulses in rhythm with pressure

**Uses:**
- Pressurizing networks
- Feeding turbines
- Creating pressure loops

### C. Vent Piston (Soft‑Motion Actuator)
**Role:** Smooth, organic movement

**Animation:**
- Membrane inflates, pushing blocks gently
- No jerky piston motion

**Uses:**
- Lifts
- Rotating platforms
- Soft block movement
- Player‑safe elevators

### D. Spiral Gearbox
**Role:** Converts rotational speed

**Animation:**
- Interlocking spiral gears
- Direction changes visible

**Uses:**
- Speed control
- Torque conversion
- Multi‑machine networks

### E. Buoyancy Lift
**Role:** Pressure‑driven elevator

**Animation:**
- Platform floats upward
- Membrane vents adjust pressure

**Uses:**
- Wayfall traversal
- Village architecture
- Player bases

### F. Atmospheric Compressor
**Role:** Creates stable air pockets

**Animation:**
- Swirling air inside crystal shell
- Pulsing glow

**Uses:**
- Allows pressure‑logic in overworld
- Protects Cephalari machinery
- Enables cross‑dimension builds

### G. Pressure Loom (Flowwright Workstation)
**Role:** Crafts bio‑ceramic and coral‑fiber components

**Animation:**
- Loom arms weave under pressure pulses
- Membrane tension changes visibly

**Uses:**
- Crafting
- Village profession block
- Player automation

### H. Conveyor Membranes
**Role:** Organic conveyor belts

**Animation:**
- Pulsing membrane strips
- Items glide smoothly

**Uses:**
- Item transport
- Sorting systems
- Factory builds

---

## 2.1 Pneumatic Motion & Transport — Expanded Integration

These four blocks can form an entire movement and logistics branch of the Cephalari tech tree.

### 1. Vortex Funnel
**Function:** Pulls items/mobs inward using a rotating air vortex.

**Integration**
- Connects to Atmospheric Compressor for swirl power.
- Can be chained with Conveyor Membrane for item routing.
- Works with Pressure Valve to toggle suction.

**Gameplay Loop**
- Automated mob farms
- Item vacuum systems
- Sorting networks
- Player‑safe suction zones

**Optional Variants**
- Directional Vortex (pulls from one side only)
- Gentle Vortex (no mob damage)
- Harsh Vortex (applies damage)

**Progression Tier**
Mid‑game, after Atmospheric Compressor.

### 2. Pressure Rail
**Function:** Pushes entities along a linear track using membrane pulses.

**Integration**
- Uses Conveyor Membrane animation logic.
- Crystal seam indicates direction (like powered rails).
- Can be boosted by Pressure Turbine.

**Gameplay Loop**
- Silent minecart‑like transport
- Mob relocation
- Player‑launch corridors
- Item shuttling

**Optional Variants**
- Bidirectional Rail (toggle direction)
- High‑Pressure Rail (faster, consumes more pressure)
- Soft Rail (slower, safe for mobs)

**Progression Tier**
Early‑mid game, after Membrane Pump.

### 3. Pneumatic Catapult
**Function:** Launches items or players using a pressure burst.

**Integration**
- Powered by Pressure Conduit network.
- Charge level shown by Crystal Indicator Node.
- Can be automated with Pressure Oscillator (if added later).

**Gameplay Loop**
- Item flingers
- Player mobility
- Puzzle mechanics
- Long‑distance item transport

**Optional Variants**
- Arc‑Adjustable Catapult (crystal dial sets angle)
- Precision Catapult (short, accurate throws)
- Scatter Catapult (wide spray)

**Progression Tier**
Mid‑game, after Pressure Turbine.

### 4. Air‑Lift Tube
**Function:** Vertical transport tube with smooth, silent ascent.

**Integration**
- Base uses Atmospheric Compressor.
- Tube segments use Ceramic Ribs + Crystal Seams.
- Can be combined with Buoyancy Lift Platform for multi‑level systems.

**Gameplay Loop**
- Elevators
- Base access
- Mob sorting
- Vertical item transport

**Optional Variants**
- Down‑Draft Tube (pulls downward)
- Bidirectional Tube (toggle direction)
- High‑Flow Tube (faster, consumes more pressure)

**Progression Tier**
Mid‑late game, after Atmospheric Compressor.

---

## 2.2 Pneumatic Processing & Crafting — Expanded Integration

These four blocks create a pressure‑powered crafting branch, parallel to furnaces, anvils, and smithing tables.

### 5. Pressure Kiln
**Function:** Uses compressed air to superheat materials.

**Integration**
- Requires Atmospheric Compressor + Pressure Turbine.
- Crystal heat nodes glow based on temperature.
- Membrane bellows animate when active.

**Gameplay Loop**
- Smelting without fuel
- High‑temperature alloys
- Organic ceramics
- Crystal refinement

**Optional Variants**
- Overpressure Mode (faster, risk of explosion)
- Low‑Pressure Mode (slow, safe)

**Progression Tier**
Mid‑late game, after Pressure Turbine.

### 6. Membrane Press
**Function:** Flattens, shapes, or compresses materials.

**Integration**
- Uses large central membrane animation.
- Pressure gauge UI uses crystal seam logic.
- Works with Membrane Pump for force.

**Gameplay Loop**
- Plate crafting
- Organic sheets
- Pressed crystal wafers
- Structural components

**Optional Variants**
- Precision Press (exact thickness)
- Bulk Press (multi‑item)

**Progression Tier**
Early‑mid game, after Membrane Pump.

### 7. Crystal Infuser
**Function:** Injects compressed air into crystals to alter properties.

**Integration**
- Floating crystal core uses Atmospheric Swirl animation.
- Ceramic stabilizers prevent “crystal drift.”
- Works with Pressure Valve for fine control.

**Gameplay Loop**
- Crystal upgrades
- Pressure‑tuned gems
- Infused crafting materials
- Late‑game tech unlocks

**Optional Variants**
- Multi‑Infuser (two crystals at once)
- Deep Infuser (rare materials)

**Progression Tier**
Late game, after Atmospheric Compressor.

### 8. Pneumatic Separator
**Function:** Splits items based on density or pressure response.

**Integration**
- Two output chutes
- Membrane sorting gate
- Crystal indicator lights show routing
- Works with Vortex Funnel for intake

**Gameplay Loop**
- Automated sorting
- Ore processing
- Mob/item classification
- Organic vs inorganic separation

**Optional Variants**
- Tri‑Separator (three outputs)
- Density‑Adjustable Separator (crystal dial)

**Progression Tier**
Mid‑game, after Membrane Press.

---

## 2.3 Asset-Generator Hand-off Notes (Schema-Grade)

### 1) Extend the block list in the schema

Add these 8 blocks to `blocks_to_generate`:

{
  "blocks_to_generate": [
    "pressure_conduit",
    "membrane_pump",
    "pressure_turbine",
    "spiral_gearbox",
    "vent_piston",
    "atmospheric_compressor",
    "conveyor_membrane",
    "buoyancy_lift_platform",
    "pressure_valve",

    "vortex_funnel",
    "pressure_rail",
    "pneumatic_catapult",
    "air_lift_tube",
    "pressure_kiln",
    "membrane_press",
    "crystal_infuser",
    "pneumatic_separator"
  ]
}

Everything else in the schema (palette, format, workflow) stays the same.

### 2) Block blueprints for the new 8 (for texture generation)

These are design blueprints—what another AI should turn into 32×32 faces using your palette + JSON format.

#### 2.1 Vortex Funnel
**Role:** Suction block that pulls items/mobs inward.

**Top:**
- Central atmospheric swirl (air palette) 10–12px diameter.
- 4 ceramic vanes at diagonals (shell/coral or ceramic mid/dark).
- Crystal bright ring at inner edge of vanes.

**Sides:**
- Swirl motif near center (air mid/shadow).
- Ceramic frame around edges.
- Small crystal nodes indicating suction strength.

**Bottom:**
- Dark ceramic ring.
- Air deep + air linework swirl (inverted, like exhaust).

**Animation:**
- 4‑frame swirl rotation on top face (air tones rotating).

#### 2.2 Pressure Rail
**Role:** Linear transport track.

**Top:**
- Central membrane strip running along X axis, 6–8px wide.
- Membrane highlight on one side, shadow on the other.
- Crystal seam line offset slightly to indicate direction (e.g., one pixel off center).

**Sides:**
- Ceramic casing with ribs.
- Crystal bright dots at intervals (like powered rail lamps).

**Bottom:**
- Flat ceramic with darker band at edges.

**Animation:**
- 4‑frame pulse moving along the membrane strip (left→right or right→left).

#### 2.3 Pneumatic Catapult
**Role:** Launches items/players.

**Top:**
- Membrane cup: circular depression, membrane mid/shadow with highlight at rim.
- Crystal charge indicator: small crystal bright node near front edge.
- Ceramic base around cup.

**Sides:**
- Ceramic body with a slight slope toward the launch direction.
- Crystal line or nodes indicating charge level.

**Bottom:**
- Dark ceramic with a small air swirl (pressure vent).

**Animation:**
- 3–4 frames: membrane cup “tenses” (bulges slightly) then snaps back.

#### 2.4 Air‑Lift Tube
**Role:** Vertical transport.

**Top:**
- Open ring: ceramic ring with inner air swirl.
- Crystal bright ring at inner edge.

**Sides:**
- Vertical ceramic ribs (3–4) evenly spaced.
- Crystal seam running vertically (center).
- Air mid/bright gradient inside to suggest flow.

**Bottom:**
- Atmospheric compressor‑like swirl.
- Ceramic ring.

**Animation:**
- Air tones scrolling upward inside side faces.

#### 2.5 Pressure Kiln
**Role:** High‑temp pressure smelter.

**Top:**
- Ceramic crucible: circular recess with shell/coral or ceramic mid/dark.
- Crystal heat nodes around rim (crystal bright/mid).
- Inner glow: air bright + crystal bright.

**Sides:**
- Ceramic walls with vertical heat fins.
- Crystal nodes that brighten when active.
- Membrane bellows hinted at lower sides (membrane mid/shadow).

**Bottom:**
- Dark ceramic with small air vents.

**Animation:**
- Flickering glow inside crucible (air bright/mid + crystal bright).

#### 2.6 Membrane Press
**Role:** Presses/plates materials.

**Top:**
- Large central membrane disk (membrane mid/shadow).
- Outer ceramic frame.
- Small crystal gauge at one corner (pressure indicator).

**Sides:**
- Ceramic body with vertical press supports.
- Membrane band visible at mid‑height (press plate).
- Crystal seam showing active/inactive.

**Bottom:**
- Flat ceramic, darker band at edges.

**Animation:**
- 2–4 frames: membrane disk moves slightly down/up (press motion).

#### 2.7 Crystal Infuser
**Role:** Infuses crystals with pressure.

**Top:**
- Floating crystal core: crystal mid/bright with linework.
- Atmospheric swirl ring around it (air mid/shadow).
- Ceramic stabilizer arms at 3–4 points.

**Sides:**
- Ceramic pillars.
- Crystal channels (vertical lines).
- Air swirl hints near mid‑height.

**Bottom:**
- Ceramic base with crystal socket.

**Animation:**
- Crystal core pulses (brightness cycling).
- Swirl ring rotates or pulses.

#### 2.8 Pneumatic Separator
**Role:** Splits items based on pressure response.

**Top:**
- Central membrane gate (membrane mid/shadow).
- Two output chutes indicated by ceramic channels leading to opposite edges.
- Crystal indicator lights above each chute (left/right).

**Sides:**
- Ceramic body with two distinct output sides.
- Crystal bright/mid nodes above each output.
- Membrane slit visible at center.

**Bottom:**
- Ceramic with small vents.

**Animation:**
- Membrane gate oscillates slightly.
- Crystal indicator lights blink depending on “active” side (for future logic).

### 3) How another AI should generate textures from these blueprints

For each of the 8 blocks:
- Read the blueprint (above).
- For each face (top, bottom, north, south, east, west):
  - Lay out a 32×32 grid.
  - Place major shapes (rings, strips, cups, ribs, swirls) using the Cephalari palette.
  - Apply shading: light → mid → dark → deep according to direction and curvature.
  - Add crystal seams/nodes using crystal bright/mid/shadow.
  - Add membrane regions using membrane highlight/mid/shadow/deep.
  - Add air swirls using air bright/mid/shadow/deep.
  - Output each face as a 32×32 JSON array of hex codes, exactly as defined in your schema.
- Generate ASCII mockups using the symbol mapping:
  - . ceramic light
  - # ceramic mass
  - @ membrane
  - * crystal
  - = shell‑coral
  - ~ air/swirl
- Generate block model JSON using the standard cube parent and texture names:
  - <namespace>:block/<block_name>_<face>
- If the block has animation:
  - Define frames as separate textures or arrays.
  - Create an animation JSON with frametime and frames list.
- Generate a 32×32 item icon:
  - Based on the front/most readable face.
  - Simplified but recognizably the same block.

### 4) Schema extension for these blocks (logic only)

You can add a per‑block metadata section to your schema like:

{
  "block_blueprints": {
    "vortex_funnel": {
      "role": "suction",
      "animated_faces": ["top"],
      "primary_materials": ["air", "ceramic", "crystal"]
    },
    "pressure_rail": {
      "role": "linear_transport",
      "animated_faces": ["top"],
      "primary_materials": ["membrane", "ceramic", "crystal"]
    },
    "pneumatic_catapult": {
      "role": "launcher",
      "animated_faces": ["top"],
      "primary_materials": ["membrane", "ceramic", "crystal", "air"]
    },
    "air_lift_tube": {
      "role": "vertical_transport",
      "animated_faces": ["sides"],
      "primary_materials": ["air", "ceramic", "crystal"]
    },
    "pressure_kiln": {
      "role": "smelter",
      "animated_faces": ["top"],
      "primary_materials": ["ceramic", "crystal", "air", "membrane"]
    },
    "membrane_press": {
      "role": "press",
      "animated_faces": ["top"],
      "primary_materials": ["membrane", "ceramic", "crystal"]
    },
    "crystal_infuser": {
      "role": "infuser",
      "animated_faces": ["top", "sides"],
      "primary_materials": ["crystal", "air", "ceramic"]
    },
    "pneumatic_separator": {
      "role": "separator",
      "animated_faces": ["top"],
      "primary_materials": ["membrane", "ceramic", "crystal"]
    }
  }
}

This is what to emphasize and which faces animate.

---

## 2.4 The Complete Cephalari Pneumatic Engineering Suite (Unified Overview)

Here is the complete, unified, system‑level description of every Cephalari pneumatic/pressure‑tech block, including:
- What each block does
- How it works internally
- How it connects to the rest of the network
- What role it plays in the overall Cephalari engineering ecosystem

This is the full, authoritative overview of the entire pneumatic suite — both the original 9 blocks and the 8 new ones.

🧱 THE COMPLETE CEPHALARI PNEUMATIC ENGINEERING SUITE
All blocks, all functions, all connections — one unified system.
I’ll group them into functional categories so the whole ecosystem becomes intuitive.

🌬️ A. PRESSURE GENERATION & FLOW CONTROL
These blocks create, move, regulate, and store pressure — the backbone of the entire system.

### 1. Pressure Conduit
**What it does:**
Carries compressed air between blocks.

**How it works:**
- Hollow ceramic tube
- Crystal seam shows pressure level
- No moving parts

**Connections:**
- Connects everything
- Required for all pneumatic networks

**Role:**
The “pipes” of the system.

### 2. Membrane Pump
**What it does:**
Generates pressure by pulsing a membrane.

**How it works:**
- Membrane expands/contracts
- Pulls air in, pushes air out
- Crystal node regulates rhythm

**Connections:**
- Feeds Pressure Conduits
- Often first pressure source in early game

**Role:**
Entry‑level pressure generator.

### 3. Pressure Turbine
**What it does:**
Amplifies pressure using rotational airflow.

**How it works:**
- Spiral ceramic blades
- Crystal core stabilizes spin
- Converts airflow into higher pressure

**Connections:**
- Sits between pumps and high‑demand machines
- Boosts throughput

**Role:**
Mid‑game pressure amplifier.

### 4. Spiral Gearbox
**What it does:**
Converts pressure pulses into rotational force.

**How it works:**
- Shell‑coral gears
- Membrane clutch
- Crystal timing nodes

**Connections:**
- Drives mechanical blocks
- Can power Vent Piston or Conveyor Membrane

**Role:**
Pressure → mechanical motion converter.

### 5. Vent Piston
**What it does:**
Creates linear motion using directional airflow.

**How it works:**
- Membrane chamber
- Air pushes piston rod outward
- Crystal valve controls stroke

**Connections:**
- Used in automation
- Works with Gearbox for timing

**Role:**
Pneumatic piston for machines/doors.

### 6. Pressure Valve
**What it does:**
Opens/closes pressure flow.

**How it works:**
- Ceramic valve wheel
- Crystal seam shows open/closed
- Membrane flap inside

**Connections:**
- Controls direction
- Used in logic systems

**Role:**
The redstone repeater of pneumatic tech.

🌀 B. MOTION & TRANSPORT SYSTEMS
These blocks move items, mobs, or players using airflow and membrane pulses.

### 7. Conveyor Membrane
**What it does:**
Moves items along a surface.

**How it works:**
- Membrane pulses in a wave
- Crystal seam shows direction

**Connections:**
- Works with Vortex Funnel, Separator, Catapult

**Role:**
Organic conveyor belt.

### 8. Buoyancy Lift Platform
**What it does:**
Creates a floating platform using upward pressure.

**How it works:**
- Membrane underside
- Air swirl pushes upward
- Crystal stabilizers

**Connections:**
- Works with Air‑Lift Tube
- Can be chained vertically

**Role:**
Floating elevator platform.

### 9. Vortex Funnel
**What it does:**
Pulls items/mobs inward.

**How it works:**
- Atmospheric swirl core
- Ceramic vanes shape suction
- Crystal nodes regulate radius

**Connections:**
- Feeds Pneumatic Separator
- Works with Conveyor Membrane

**Role:**
Item/mob vacuum.

### 10. Pressure Rail
**What it does:**
Pushes entities/items along a track.

**How it works:**
- Membrane strip pulses directionally
- Crystal seam indicates flow

**Connections:**
- Works with Catapult, Air‑Lift Tube
- Controlled by Pressure Valves

**Role:**
Silent organic minecart rail.

### 11. Pneumatic Catapult
**What it does:**
Launches items/players.

**How it works:**
- Membrane cup stores pressure
- Crystal node shows charge
- Sudden release flings payload

**Connections:**
- Works with Pressure Rail
- Can feed Air‑Lift Tube

**Role:**
Long‑distance transport.

### 12. Air‑Lift Tube
**What it does:**
Vertical airflow elevator.

**How it works:**
- Atmospheric compressor at base
- Ceramic ribs stabilize airflow
- Crystal seam regulates lift

**Connections:**
- Works with Buoyancy Platform
- Connects to Pressure Conduits

**Role:**
Vertical transport tube.

🔥 C. PROCESSING & CRAFTING MACHINES
These blocks transform materials using pressure, heat, membranes, and crystals.

### 13. Atmospheric Compressor
**What it does:**
Compresses ambient air into high‑pressure flow.

**How it works:**
- Swirl chamber
- Crystal stabilizer
- Ceramic shell

**Connections:**
- Feeds Pressure Kiln, Infuser, Air‑Lift Tube

**Role:**
High‑tier pressure generator.

### 14. Pressure Kiln
**What it does:**
Fuel‑free smelting using compressed air.

**How it works:**
- Ceramic crucible
- Crystal heat nodes
- Membrane bellows

**Connections:**
- Requires Atmospheric Compressor
- Outputs to Membrane Press or Infuser

**Role:**
High‑temperature crafting.

### 15. Membrane Press
**What it does:**
Flattens or compresses materials.

**How it works:**
- Large membrane plate
- Crystal pressure gauge
- Ceramic frame

**Connections:**
- Works with Separator
- Powered by Membrane Pump or Turbine

**Role:**
Plate/sheet/wafer crafting.

### 16. Crystal Infuser
**What it does:**
Injects compressed air into crystals to modify them.

**How it works:**
- Floating crystal core
- Atmospheric swirl injection
- Ceramic stabilizers

**Connections:**
- Requires Atmospheric Compressor
- Produces infused materials for late‑game tech

**Role:**
Crystal upgrading.

### 17. Pneumatic Separator
**What it does:**
Sorts items by density/pressure response.

**How it works:**
- Membrane sorting gate
- Crystal indicator lights
- Two output chutes

**Connections:**
- Intake from Vortex Funnel
- Output to Conveyor Membrane or Rails

**Role:**
Automated sorting.

🧩 D. HOW THE ENTIRE SYSTEM FITS TOGETHER
Here’s the full Cephalari pneumatic loop:

1. Generate pressure
- Membrane Pump → early
- Pressure Turbine → mid
- Atmospheric Compressor → late

2. Move pressure
- Pressure Conduits
- Pressure Valves
- Spiral Gearbox
- Vent Piston

3. Move items/entities
- Vortex Funnel → intake
- Conveyor Membrane → horizontal
- Pressure Rail → fast horizontal
- Pneumatic Catapult → long‑range
- Air‑Lift Tube → vertical
- Buoyancy Lift Platform → platform elevator

4. Process materials
- Pressure Kiln → smelt
- Membrane Press → shape
- Crystal Infuser → upgrade
- Pneumatic Separator → sort

5. Build automation
- Combine all above
- Pressure Valves act as logic gates
- Turbines amplify throughput
- Gearboxes convert pressure to motion

---

## 3. Pressure‑Logic + Redstone Integration

They are partners, not competitors.

Redstone controls pressure‑logic via:
- **Pressure Valves** (open/close with redstone)
- **Clutches** (engage/disengage turbines)
- **Regulators** (set pressure levels via redstone)
- **Sequencers** (redstone pulses → pressure pulses)
- **Sensors** (pressure → redstone output)

This mirrors Create’s clutch/gearshift/redstone‑controlled machines.

---

## 4. Player Progression Tree

How players naturally enter the system.

**Stage 1 — Discovery**
- Find Cephalari village
- See animated machinery
- Learn basics from Flowwright NPC

**Stage 2 — Starter Tools**
- Craft Pressure Conduits
- Craft Membrane Pump
- Build first pressure loop

**Stage 3 — Mechanical Power**
- Unlock Pressure Turbine
- Build first rotating machinery
- Use redstone to control valves

**Stage 4 — Automation**
- Conveyor membranes
- Vent pistons
- Pressure Loom

**Stage 5 — Advanced Engineering**
- Spiral Gearboxes
- Atmospheric Compressors
- Buoyancy Lifts
- Multi‑machine factories

**Stage 6 — Hybrid Systems**
- Redstone + Pressure‑Logic factories
- Cephalari mount‑charging stations
- Gravity‑adaptive machinery

---

## 5. Village Structures (Updated for Mechanical System)

### Flow Engine Chamber → Pressure Engine Hall
- Turbines powering village machinery
- Membrane pumps feeding conduits
- Gearboxes distributing rotation
- Shift sensors monitoring gravity

### Ceramic Kilns
- Animated vents
- Pressure‑driven bellows

### Memory Gardens
- Pressure‑logic irrigation
- Atmospheric compressors maintaining air

### Mount Bays
- Pressure‑charging stations
- Vent pistons adjusting mount platforms

---

## 6. Why This System Is Worth Using

Pressure‑Logic Machinery gives players:
- Animated machines
- Rotational power
- Pneumatic movement
- Soft pistons
- Organic conveyors
- Gravity‑adaptive builds
- Atmospheric engineering
- A full Create‑style tech tree
- A unique Cephalari aesthetic
- Redstone compatibility

This is no longer “Cephalari redstone.”
This is a full mechanical engineering system with its own identity and gameplay loop.

---

## 7. Full Implementation Spec (Condensed)

### Pressure
- Stored as continuous value (0–100)
- Drives turbines, pistons, pumps
- Generated by pumps, maintained by compressors

### Rotation
- Derived from pressure turbines
- Distributed via spiral gearboxes

### Atmosphere
- Wayfall air required
- Compressors allow cross‑dimension builds

### Redstone Integration
- Valves, clutches, regulators
- Sensors output redstone

### Animation
- All machinery uses membrane, spiral, or buoyancy motifs
- Smooth Create‑style motion

---

## 8. Crafting Recipes (Concept‑Level, Not JSON)

These recipes use Cephalari materials and Wayfall resources, keeping the tech tree distinct from overworld redstone.

### Pressure Conduit
- Coral Fiber ×2
- Bio‑Ceramic Fragment ×1
- Wayfall Crystal Dust ×1
Produces: 4 conduits

### Membrane Pump
- Elastic Membrane ×1
- Bio‑Ceramic Casing ×2
- Pressure Conduit ×2
- Crystal Dust ×1

### Pressure Turbine
- Spiral Shell Plate ×1
- Bio‑Ceramic Frame ×3
- Membrane Pump ×1
- Wayfall Crystal Core ×1

### Spiral Gearbox
- Bio‑Ceramic Frame ×2
- Spiral Gear ×2
- Pressure Turbine (any tier) ×1

### Vent Piston
- Elastic Membrane ×1
- Bio‑Ceramic Casing ×1
- Pressure Conduit ×1
- Coral Fiber ×1

### Atmospheric Compressor
- Wayfall Crystal Core ×1
- Bio‑Ceramic Frame ×4
- Membrane Pump ×1
- Coral Fiber ×2

### Buoyancy Lift Platform
- Bio‑Ceramic Platform ×1
- Vent Pistons ×2
- Pressure Conduits ×2
- Stabilized Crystal ×1

### Conveyor Membrane
- Elastic Membrane ×2
- Coral Fiber ×2
- Bio‑Ceramic Strip ×1

These recipes reinforce the Cephalari identity: organic, ceramic, pressure‑based, and crystal‑infused.

---

## 9. Blockstate + Model Behavior (High‑Level Spec)

### Pressure Turbine
- Has a `rotation_speed` property (0–5)
- Speed increases with pressure input
- Outputs rotational energy to adjacent gearboxes
- Emits soft humming + membrane flutter sound

### Membrane Pump
- Has `pulse_rate` property
- Animates membrane expansion
- Moves pressure through conduits
- Can be redstone‑controlled (on/off)

### Vent Piston
- Has `extension` property (0.0–1.0)
- Smooth interpolation animation
- Can push entities gently
- Can rotate blocks if configured

### Spiral Gearbox
- Has `ratio` property (1:1, 1:2, 2:1, 1:4, 4:1)
- Changes rotation speed
- Animates interlocking spiral gears

### Atmospheric Compressor
- Has `stability_level` property
- Creates 5×5×5 bubble of Wayfall‑density air
- Required for pressure‑logic outside Wayfall

### Conveyor Membrane
- Has `pulse_phase` property
- Moves items smoothly
- Organic, rhythmic animation

### Buoyancy Lift
- Has `lift_state` property (idle, rising, falling)
- Smooth vertical motion
- Pressure‑driven, not piston‑driven

---

## 10. Animation Blueprints (Pixel‑Art Pipeline)

### Membrane Animation
- 3–5 frames
- Frame 1: relaxed membrane
- Frame 3: fully expanded
- Frame 5: contracted
- Loop: 1→3→5→3→1

### Spiral Gear Rotation
- 8‑frame rotation
- Each frame rotates the spiral gear by 45°
- Works with any palette

### Turbine Spin
- 6‑frame spin cycle
- Spiral plates blur slightly at high speed
- Outer membrane flutters

### Conveyor Pulse
- 4‑frame wave animation
- Membrane bulges forward in sequence
- Moves items visually

### Buoyancy Lift
- Platform texture remains static
- Vent pistons animate membrane expansion
- Lift motion handled by block entity interpolation

---

## 11. Wiki‑Ready Documentation Set (Player‑Facing)

### Cephalari Pressure‑Logic Machinery
Pressure‑Logic Machinery is a pneumatic engineering system native to the Wayfall dimension.
It uses pressure, membranes, and spiral mechanics to create smooth, organic motion.

**What It Does**
- Moves items
- Powers machines
- Lifts players
- Rotates platforms
- Creates stable air pockets
- Automates Cephalari crafting

**What It Doesn’t Do**
- Replace redstone
- Provide logic gates
- Work outside Wayfall without a compressor

**How It Integrates With Redstone**

Redstone controls:
- Valves
- Pumps
- Turbines
- Gearboxes
- Lift states

Pressure‑logic provides:
- Motion
- Rotation
- Power
- Atmospheric stability

Together, they form a hybrid engineering system.

---

## 12. Player Tutorial Questline (Optional)

**Quest 1: “Breath of Wayfall”**
- Discover a Cephalari village
- Speak to a Flowwright
- Receive Pressure Conduits

**Quest 2: “First Pulse”**
- Craft a Membrane Pump
- Build a pressure loop
- Power a small turbine

**Quest 3: “Motion Without Muscles”**
- Craft a Vent Piston
- Build a soft‑motion lift

**Quest 4: “The Spiral Turns”**
- Craft a Spiral Gearbox
- Build a rotating platform

**Quest 5: “Air That Remembers”**
- Craft an Atmospheric Compressor
- Bring pressure‑logic to the overworld

**Quest 6: “Cephalari Engineering”**
- Build a full factory using:
  - Turbines
  - Pumps
  - Conveyors
  - Gearboxes
  - Lifts

---

# Pressure‑Logic Machinery — UV‑Safe Texture Specs (32×32)

All textures assume 32×32 resolution (preferred standard), but they scale down to 16×16 and up to 64×64 by exact pixel‑doubling/halving.

Constraints:
- UV‑safe for 16×16 / 32×32 / 64×64
- Hard‑pixel, vanilla‑compatible shading
- No gradients
- No anti‑aliasing
- Clear silhouette rules
- Tile‑safe edges

## 1. Pressure Conduit
**Canvas:** 32×32

**UV Layout:**
- Top: 32×32 tile
- Side: 32×32 tile
- Bottom: identical to top (vanilla pipe logic)

**Silhouette:**
- 2–3 pixel‑wide tube running through center
- Hard edges
- No diagonal smoothing
- Glow seam = 1‑pixel line, centered

**Palette:**
- Bio‑ceramic: muted beige + coral tint
- Glow seam: cyan‑white (Wayfall crystal)

**Shading:**
- 3‑tone shading:
  - highlight (1px)
  - midtone (majority)
  - shadow (1px opposite side)

**Notes:**
- Must tile seamlessly on all sides
- Glow seam must align perfectly across edges

## 2. Membrane Pump
**Canvas:** 32×32

**UV Layout:**
- Front: membrane window
- Sides: ceramic casing
- Top: circular pressure cap
- Bottom: flat ceramic

**Silhouette:**
- Membrane = oval shape, 1‑pixel outline
- Casing = thick ceramic frame
- No transparency

**Palette:**
- Membrane: pink‑coral → purple (biological)
- Casing: beige‑ceramic
- Crystal nodes: cyan‑white

**Animation Frames (3‑frame loop):**
- Frame 1: relaxed membrane (flat)
- Frame 2: mid‑expansion (bulge 1–2px)
- Frame 3: full expansion (bulge 3–4px)

All frames must share identical UV boundaries.

## 3. Pressure Turbine
**Canvas:** 32×32

**UV Layout:**
- Front: spiral plate
- Sides: ceramic housing
- Top: vented cap
- Bottom: flat ceramic

**Silhouette:**
- Spiral = 3‑pixel‑thick arms
- Must rotate cleanly in 8‑frame cycle
- No anti‑aliasing on curves

**Palette:**
- Plates: shell‑white + coral tint
- Housing: ceramic beige
- Glow vents: cyan‑white

**Animation Frames (6‑frame spin):**
- Each frame rotates spiral by 60°.
- Spiral arms must remain UV‑aligned and centered.

## 4. Spiral Gearbox
**Canvas:** 32×32

**UV Layout:**
- Front: exposed spiral gears
- Sides: ceramic casing
- Top: gear access plate
- Bottom: flat ceramic

**Silhouette:**
- Gears = 4–5 teeth, 2‑pixel thickness
- Interlocking gear teeth must not overlap
- No diagonal smoothing

**Palette:**
- Gears: darker coral‑shell
- Casing: ceramic beige
- Glow seam: cyan‑white

**Animation Frames (8‑frame rotation):**
- Rotate gears by 45° per frame.
- Both gears rotate in opposite directions.

## 5. Atmospheric Compressor
**Canvas:** 32×32

**UV Layout:**
- Front: crystal sphere window
- Sides: ceramic frame
- Top: swirling air vent
- Bottom: ceramic base

**Silhouette:**
- Sphere = 6–8px diameter
- Hard outline
- No transparency
- Swirl = 1‑pixel spiral inside sphere

**Palette:**
- Sphere: cyan‑white + blue
- Frame: ceramic beige
- Swirl: white highlight

**Animation Frames (4‑frame swirl):**
- Rotate internal swirl by 90° each frame.

## 6. Vent Piston (Soft‑Motion Actuator)
**Canvas:** 32×32

**UV Layout:**
- Front: membrane actuator
- Sides: ceramic casing
- Top: pressure cap
- Bottom: ceramic base

**Silhouette:**
- Membrane = circular, 1‑pixel outline
- Extension animation uses block entity model, not texture stretch
- Texture must not imply piston rods

**Palette:**
- Membrane: pink‑coral
- Casing: ceramic beige
- Glow seam: cyan‑white

**Animation Frames:** none — movement handled by model interpolation.

## 7. Conveyor Membrane
**Canvas:** 32×32

**UV Layout:**
- Top: membrane strip
- Sides: ceramic frame
- Bottom: ceramic

**Silhouette:**
- Membrane = 3–4px‑wide strip
- Pulsing wave = 1‑pixel bulges
- Must tile seamlessly horizontally

**Palette:**
- Membrane: coral‑pink
- Frame: ceramic beige

**Animation Frames (4‑frame pulse):**
- Frame 1: flat membrane
- Frame 2: bulge left
- Frame 3: bulge center
- Frame 4: bulge right
- Loops 1→4.

## 8. Buoyancy Lift Platform
**Canvas:** 32×32

**UV Layout:**
- Top: ceramic platform with vent rings
- Sides: vertical ceramic strips
- Bottom: membrane vents

**Silhouette:**
- Top ring = 2‑pixel‑thick circle
- No transparency
- No animation on texture

**Palette:**
- Ceramic: beige
- Vents: cyan‑white
- Membrane underside: coral‑pink

## 9. Pressure Valve (Redstone Integration Block)
**Canvas:** 32×32

**UV Layout:**
- Front: valve wheel
- Sides: ceramic casing
- Top: pressure indicator
- Bottom: ceramic

**Silhouette:**
- Wheel = 6px diameter
- 4 spokes
- Hard outline

**Palette:**
- Wheel: coral‑shell
- Indicator: cyan‑white
- Casing: beige

**Animation Frames:** none — state changes via model.

---

# Pixel‑Grid Accurate Texture Blueprints (Literal Specs)

The remainder of this document contains literal pixel‑coordinate blueprints for each block face and animation cycle, intended for direct use in a pixel‑art pipeline.

> Note: The blueprint text below is preserved in full from the unified spec input.

## Pressure Conduit — 32×32 UV‑Safe Texture Blueprint

### Canvas
32×32, hard‑pixel, no AA, no subpixel curves.

### Tile Behavior
- All four sides must tile seamlessly.
- Glow seam must align perfectly across edges.
- No transparency.

### Top / Bottom Face (identical)
A centered ceramic tube with a glowing seam.

- Tube diameter: 10px
- Tube outline: 1px darker ceramic
- Inner tube: 8px mid‑tone ceramic
- Glow seam: 1px cyan‑white vertical line, centered
- Shadow: 1px darker ceramic on right side
- Highlight: 1px lighter ceramic on left side

**Pixel‑accurate placement**
- Tube center: x=16, y=16
- Glow seam: x=16, full height
- Tube edges: x=11 → x=21

### Side Faces
A horizontal tube segment with identical shading logic.

- Tube height: 10px
- Tube outline: 1px darker ceramic
- Inner tube: 8px mid‑tone ceramic
- Glow seam: 1px cyan‑white horizontal line, centered
- Shadow: 1px darker ceramic on bottom
- Highlight: 1px lighter ceramic on top

**Pixel‑accurate placement**
- Tube center: y=16
- Glow seam: y=16, full width
- Tube edges: y=11 → y=21

---

## Membrane Pump — 32×32 UV‑Safe Texture Blueprint

### Canvas
32×32, no transparency, no subpixel curves.

### Front Face (Membrane Window)
Overall layout:
- Membrane oval centered on the face
- Ceramic frame around it
- Crystal nodes embedded at 4 corners

**Membrane Oval Bounding Box**
- Width: 14px
- Height: 18px
- Top‑left corner: (x=9, y=7)
- Bottom‑right corner: (x=22, y=24)

**Outline**
- 1px dark‑coral outline around the oval
- Must be continuous, no broken pixels

**Interior Shading (3‑tone)**
- Highlight: left side, 1px vertical band
- Midtone: center mass
- Shadow: right side, 1px vertical band

**Crystal Nodes**
- 4 nodes, 2×2px each
- Top‑left: (3,3)
- Top‑right: (27,3)
- Bottom‑left: (3,27)
- Bottom‑right: (27,27)
- Palette: cyan‑white, 2‑tone

**Ceramic Frame**
- 3px thick border around membrane
- Palette: beige‑ceramic (3‑tone)
- Hard edges, no beveling

### Side Faces (Left / Right)
Layout:
- Vertical ceramic ribs
- Central pressure conduit port
- No membrane visible

**Ribs**
- 3 ribs
- Each rib = 3px wide
- Spacing = 3px
- Rib positions (left face):
  - Rib 1: x=4–6
  - Rib 2: x=11–13
  - Rib 3: x=18–20

**Shading**
- Highlight on left edge of each rib
- Shadow on right edge

**Conduit Port**
- 6×6px square
- Centered at (x=13, y=13)
- Glow seam: 1px cyan vertical line at x=16

### Top Face
- Cap Diameter: 12px
- Center: (16,16)
- Bounding box: (10,10) → (22,22)

Ring:
- 2px thick
- Outer ring: darker ceramic
- Inner ring: lighter ceramic

Crystal Seam:
- 1px cyan line
- Horizontal, centered at y=16
- x=12 → x=20

### Bottom Face
Flat ceramic plate.
- 3‑tone ceramic
- Shadow bias toward bottom edge
- No seams, no glow

### Animation Frames (3‑Frame Breathing Cycle)
Frame 1 — Relaxed
- Oval exactly as defined above
- No bulge

Frame 2 — Mid‑Expansion
- Expand membrane outward by 1px on left and right
- New bounding box: (8,7) → (23,24)
- Highlight band widens by 1px
- Shadow band widens by 1px

Frame 3 — Full Expansion
- Expand membrane outward by 2px on left and right
- New bounding box: (7,7) → (24,24)
- Highlight and shadow bands both 2px wide

**Important UV Rule:** The ceramic frame NEVER moves. Only the membrane pixels animate.

---

## Pressure Turbine — 32×32 UV‑Safe Texture Blueprint

This is the Cephalari “mechanical heart” block.
You get front, sides, top, bottom, and full 6‑frame rotation cycle.

### Canvas
32×32, hard‑pixel, no AA, no transparency.

### Front Face (Spiral Plate Assembly)
Overall Layout
- Spiral plate centered
- 3‑pixel‑thick spiral arms
- Ceramic housing ring
- Glow vents embedded in ring

Pixel‑Accurate Placement

Spiral Plate
- Bounding box: x = 6 → 26, y = 6 → 26 (20×20px)

Spiral Arm Thickness
- Always 3px wide
- Hard corners, no diagonal smoothing

Spiral Center
- Pixel at (16,16)
- 3×3px core cluster

Glow Vents
4 vents, 2×2px each
- Top: (15,3)
- Bottom: (15,27)
- Left: (3,15)
- Right: (27,15)
- Palette: cyan‑white (2‑tone)

Ceramic Housing Ring
- Ring thickness: 3px
- Outer boundary: full 32×32
- Inner boundary: 26×26 (centered)
- Palette: 3‑tone ceramic (light/mid/dark)

### Animation — 6‑Frame Spiral Rotation
Each frame rotates the spiral by 60° around (16,16).
The housing and vents never move.

Frame 1 (0°)
- Spiral arm starts at 12 o’clock
- Arm extends upward from center: (16,16) → (16,10)
- Next arm at 120°
- Next arm at 240°

Frame 2 (60°)
- Spiral rotated clockwise
- Top arm now at 2 o’clock
- Arm endpoints approx: (19,12), (12,19), (12,12)

Frame 3 (120°)
- Top arm now at 4 o’clock
- Endpoints approx: (20,16), (16,20), (12,16)

Frame 4 (180°)
- Top arm now at 6 o’clock
- Endpoints approx: (16,20), (20,16), (12,16)

Frame 5 (240°)
- Top arm now at 8 o’clock
- Endpoints approx: (12,19), (19,12), (16,12)

Frame 6 (300°)
- Top arm now at 10 o’clock
- Endpoints approx: (12,16), (16,12), (20,16)

Important Rule
You do not redraw the spiral from scratch each frame — you rotate the arm endpoints around (16,16) and redraw the 3‑pixel‑thick arms.

### Side Faces (Left / Right)
Non‑animated.

Layout
- Horizontal ceramic ribs
- Glow seam running horizontally
- No spiral visible

Pixel‑Accurate Specs
Ribs:
- 3 ribs
- Each rib = 3px tall
- Spacing = 3px
- Rib positions (left side):
  - Rib 1: y=6–8
  - Rib 2: y=13–15
  - Rib 3: y=20–22

Glow Seam:
- 1px cyan line
- y=16, full width

### Top Face
Layout
- Circular turbine cap
- Vent slits
- Crystal seam

Pixel‑Accurate Specs
Cap Diameter: 14px
- Center: (16,16)
- Bounding box: (9,9) → (23,23)

Vent Slits:
- 4 slits
- Each slit = 1×4px
- Positions:
  - North: (16,6)
  - South: (16,26)
  - East: (26,16)
  - West: (6,16)

Crystal Seam:
- 1px cyan line
- x=16, y=12 → y=20

### Bottom Face
Flat ceramic plate
- 3‑tone shading
- Darkest band at bottom edge
- No vents, no glow

---

## Spiral Gearbox — 32×32 UV‑Safe Texture Blueprint

Two interlocking gears, each with 8‑frame rotational animation, and a ceramic housing that remains perfectly static across all frames.

### Canvas
32×32, hard‑pixel, no AA, no transparency.

### Front Face (Exposed Dual Spiral Gears)

Overall Layout
- Two gears, each 12×12px
- Offset diagonally
- 3‑pixel‑thick teeth
- Ceramic housing ring around them
- Glow seam between gears

Pixel‑Accurate Placement

Gear A (Top‑Left Gear)
- Bounding box: x = 5 → 16, y = 5 → 16
- Center: (10,10)

Gear B (Bottom‑Right Gear)
- Bounding box: x = 16 → 27, y = 16 → 27
- Center: (21,21)

Gear Tooth Specs
- Tooth thickness: 3px
- Tooth length: 3px
- 8 teeth per gear (N, NE, E, SE, S, SW, W, NW)
- Hard corners, no diagonal smoothing

Interlocking Rule
- Teeth must not overlap
- 1px gap between gears at closest point
- Glow seam (cyan‑white) fills the gap
- 1px vertical line at x=16, y=12→20
- 1px horizontal line at y=16, x=12→20

Ceramic Housing Ring
- 3px thick
- Outer boundary: full 32×32
- Inner boundary: 26×26
- Palette: 3‑tone ceramic
- Must remain static across all frames

### Animation — 8‑Frame Rotation Cycle
Each gear rotates 45° per frame.
Gear A rotates clockwise.
Gear B rotates counter‑clockwise.
Housing and glow seams never move.

#### Gear A — Tooth Endpoints (Clockwise Rotation)
Frame 1 (0°)
- N: (10,5)
- NE: (13,7)
- E: (16,10)
- SE: (13,13)
- S: (10,16)
- SW: (7,13)
- W: (5,10)
- NW: (7,7)

Frame 2 (45°)
- N: (13,7)
- NE: (16,10)
- E: (13,13)
- SE: (10,16)
- S: (7,13)
- SW: (5,10)
- W: (7,7)
- NW: (10,5)

Frame 3 (90°)
- N: (16,10)
- NE: (13,13)
- E: (10,16)
- SE: (7,13)
- S: (5,10)
- SW: (7,7)
- W: (10,5)
- NW: (13,7)

Frame 4 (135°)
- N: (13,13)
- NE: (10,16)
- E: (7,13)
- SE: (5,10)
- S: (7,7)
- SW: (10,5)
- W: (13,7)
- NW: (16,10)

Frame 5 (180°)
- N: (10,16)
- NE: (7,13)
- E: (5,10)
- SE: (7,7)
- S: (10,5)
- SW: (13,7)
- W: (16,10)
- NW: (13,13)

Frame 6 (225°)
- N: (7,13)
- NE: (5,10)
- E: (7,7)
- SE: (10,5)
- S: (13,7)
- SW: (16,10)
- W: (13,13)
- NW: (10,16)

Frame 7 (270°)
- N: (5,10)
- NE: (7,7)
- E: (10,5)
- SE: (13,7)
- S: (16,10)
- SW: (13,13)
- W: (10,16)
- NW: (7,13)

Frame 8 (315°)
- N: (7,7)
- NE: (10,5)
- E: (13,7)
- SE: (16,10)
- S: (13,13)
- SW: (10,16)
- W: (7,13)
- NW: (5,10)

#### Gear B — Tooth Endpoints (Counter‑Clockwise Rotation)
Same pattern, but reversed rotation around center (21,21).

Frame 1 (0°)
- N: (21,16)
- NE: (24,18)
- E: (27,21)
- SE: (24,24)
- S: (21,27)
- SW: (18,24)
- W: (16,21)
- NW: (18,18)

Frame 2 (45° CCW)
- N: (18,18)
- NE: (21,16)
- E: (24,18)
- SE: (27,21)
- S: (24,24)
- SW: (21,27)
- W: (18,24)
- NW: (16,21)

Frame 3 (90° CCW)
- N: (16,21)
- NE: (18,18)
- E: (21,16)
- SE: (24,18)
- S: (27,21)
- SW: (24,24)
- W: (21,27)
- NW: (18,24)

Frame 4 (135° CCW)
- N: (18,24)
- NE: (16,21)
- E: (18,18)
- SE: (21,16)
- S: (24,18)
- SW: (27,21)
- W: (24,24)
- NW: (21,27)

Frame 5 (180° CCW)
- N: (21,27)
- NE: (18,24)
- E: (16,21)
- SE: (18,18)
- S: (21,16)
- SW: (24,18)
- W: (27,21)
- NW: (24,24)

Frame 6 (225° CCW)
- N: (24,24)
- NE: (21,27)
- E: (18,24)
- SE: (16,21)
- S: (18,18)
- SW: (21,16)
- W: (24,18)
- NW: (27,21)

Frame 7 (270° CCW)
- N: (27,21)
- NE: (24,24)
- E: (21,27)
- SE: (18,24)
- S: (16,21)
- SW: (18,18)
- W: (21,16)
- NW: (24,18)

Frame 8 (315° CCW)
- N: (24,18)
- NE: (27,21)
- E: (24,24)
- SE: (21,27)
- S: (18,24)
- SW: (16,21)
- W: (18,18)
- NW: (21,16)

### Side Faces
Layout
- Vertical ceramic ribs
- Glow seam
- No gears visible

Pixel‑Accurate Specs
Ribs:
- 3 ribs
- Each rib = 3px wide
- Rib positions:
  - Rib 1: x=6–8
  - Rib 2: x=14–16
  - Rib 3: x=22–24

Glow Seam:
- 1px cyan line
- x=16, full height

### Top Face
Layout
- Gear access plate
- 4 bolt nodes
- Crystal seam

Pixel‑Accurate Specs
Plate:
- 20×20px
- Centered at (16,16)

Bolts:
- 2×2px each
- Positions: (10,10), (22,10), (10,22), (22,22)

Crystal Seam:
- 1px cyan line
- y=16, x=12→20

### Bottom Face
Flat ceramic plate, 3‑tone shading.

---

## Vent Piston — 32×32 UV‑Safe Texture Blueprint

This block has four visually important faces:
- Front — membrane actuator
- Sides — ceramic casing
- Top — pressure cap
- Bottom — ceramic base

### Canvas
32×32, hard‑pixel, no AA, no transparency.

### Front Face — Membrane Actuator
Overall Layout
- Circular membrane centered
- 1px dark‑coral outline
- 3‑tone pink‑coral interior
- Ceramic frame around membrane
- Crystal seam nodes at N/E/S/W

Pixel‑Accurate Placement

Membrane Circle
- Diameter: 18px
- Bounding box: x = 7 → 24, y = 7 → 24
- Center: (16,16)

Outline
- 1px dark‑coral ring
- Must be continuous
- No broken pixels
- No diagonal AA

Interior Shading (3‑tone)
- Highlight band: 1px vertical band on left side (x = 8–9, y = 8–23)
- Midtone mass: x = 10–21, y = 8–23
- Shadow band: 1px vertical band on right side (x = 22–23, y = 8–23)

Crystal Seam Nodes (2×2px each)
- North: (15,3)
- East: (27,15)
- South: (15,27)
- West: (3,15)
- Palette: cyan‑white (2‑tone)

Ceramic Frame
- 3px thick
- Outer boundary: full 32×32
- Inner boundary: 26×26
- Palette: 3‑tone ceramic
- Static across all animations

### Animation (Model‑Driven, Not Texture‑Driven)
The membrane texture does not stretch.
The block entity model handles extension.
However, you need 3 texture frames for subtle breathing:

Frame 1 — Neutral
- Membrane exactly as defined above

Frame 2 — Mid‑Pulse
- Expand membrane outward by 1px on left and right
- New bounding box: x = 6 → 25, y = 7 → 24
- Outline adjusts accordingly
- Highlight/shadow bands widen by 1px

Frame 3 — Full Pulse
- Expand membrane outward by 2px
- New bounding box: x = 5 → 26, y = 7 → 24
- Highlight/shadow bands widen by 2px

Important Rule
The ceramic frame NEVER moves. Only the membrane pixels animate.

### Side Faces — Ceramic Casing
Layout
- Vertical ceramic ribs
- Glow seam running vertically
- No membrane visible

Pixel‑Accurate Specs
Ribs:
- 3 ribs
- Each rib = 3px wide
- Rib positions:
  - Rib 1: x = 6–8
  - Rib 2: x = 14–16
  - Rib 3: x = 22–24

Shading:
- Highlight on left edge of each rib
- Shadow on right edge

Glow Seam:
- 1px cyan‑white line
- x = 16, full height

### Top Face — Pressure Cap
Layout
- Circular ceramic cap
- Crystal seam
- Vent slits

Pixel‑Accurate Specs
Cap Diameter: 14px
- Bounding box: x = 9 → 23, y = 9 → 23

Vent Slits:
- 1×4px each
- North: (16,6)
- South: (16,26)
- East: (26,16)
- West: (6,16)

Crystal Seam:
- 1px cyan line
- Horizontal
- y = 16, x = 12 → 20

### Bottom Face — Ceramic Base
- Flat ceramic
- 3‑tone shading
- Darkest band at bottom edge
- No glow, no vents

---

## Atmospheric Compressor — 32×32 UV‑Safe Texture Blueprint

A crystal sphere containing a swirling air vortex, held inside a bio‑ceramic frame.

### Canvas
32×32, hard‑pixel, no AA, no transparency.

### Front Face — Crystal Sphere + Swirl
Overall Layout
- Central crystal sphere
- 1px bright outline
- 3‑tone interior
- Swirling air vortex (animated)
- Ceramic frame around sphere
- Crystal seam nodes at corners

Pixel‑Accurate Placement

Crystal Sphere
- Diameter: 18px
- Bounding box: x = 7 → 24, y = 7 → 24
- Center: (16,16)

Sphere Outline
- 1px bright cyan‑white
- Must be continuous
- No AA

Interior Shading (3‑tone crystal)
- Highlight arc: upper‑left quadrant
- Midtone mass: majority of interior
- Shadow arc: lower‑right quadrant

Ceramic Frame
- 3px thick
- Outer boundary: full 32×32
- Inner boundary: 26×26
- Palette: 3‑tone ceramic

Crystal Seam Nodes (2×2px each)
- Top‑left: (3,3)
- Top‑right: (27,3)
- Bottom‑left: (3,27)
- Bottom‑right: (27,27)

### Animation — 4‑Frame Swirling Air Vortex
The sphere stays static. Only the internal swirl animates.

Swirl Specs
- 1px‑thick spiral line
- 8–10px diameter
- Centered at (16,16)
- No AA
- No transparency

Frame 1 — Base Swirl
- (16,16) → (18,16) → (18,14) → (16,12) → (14,12)

Frame 2 — 90° Rotation
- (16,16) → (16,14) → (14,14) → (12,16) → (12,18)

Frame 3 — 180° Rotation
- (16,16) → (14,16) → (14,18) → (16,20) → (18,20)

Frame 4 — 270° Rotation
- (16,16) → (16,18) → (18,18) → (20,16) → (20,14)

Important Rule
The swirl must never touch the sphere outline. Minimum gap: 2px.

### Side Faces — Ceramic Ribs + Crystal Seam
Layout
- Vertical ceramic ribs
- Glow seam
- No sphere visible

Pixel‑Accurate Specs
Ribs:
- 3 ribs
- Each rib = 3px wide
- Rib positions:
  - Rib 1: x = 6–8
  - Rib 2: x = 14–16
  - Rib 3: x = 22–24

Glow Seam:
- 1px cyan‑white
- x = 16, full height

### Top Face — Crystal Cap + Air Vents
Layout
- Circular cap
- 4 vent slits
- Crystal seam

Pixel‑Accurate Specs
Cap Diameter: 14px
- Bounding box: x = 9 → 23, y = 9 → 23

Vent Slits:
- 1×4px each
- North: (16,6)
- South: (16,26)
- East: (26,16)
- West: (6,16)

Crystal Seam:
- 1px cyan line
- y = 16, x = 12 → 20

### Bottom Face — Ceramic Base
- Flat ceramic
- 3‑tone shading
- Darkest band at bottom edge

---

## Conveyor Membrane — 32×32 UV‑Safe Texture Blueprint

Cephalari equivalent of a Create‑style belt: an organic, pulsing membrane strip.

### Canvas
32×32, hard‑pixel, no AA, no transparency.

### Top Face — Membrane Strip (Animated)
Overall Layout
- Central membrane strip
- 3‑tone shading
- Ceramic frame on left/right edges
- 4‑frame pulse animation

Pixel‑Accurate Placement

Membrane Strip
- Height: 10px
- Bounding box: y = 11 → 20, x = 4 → 27
- Centerline: y = 16

Ceramic Side Frames
- Left frame: x = 0–3
- Right frame: x = 28–31
- 3‑tone ceramic shading
- Static across all frames

Membrane Shading (Frame 1 baseline)
- Highlight band: 1px tall at y = 12
- Midtone mass: y = 13–18
- Shadow band: 1px tall at y = 19

### Animation — 4‑Frame Pulse Cycle
The membrane pulses left → center → right → center.
Only the membrane pixels animate. Ceramic frames remain static.

Frame 1 — Flat / Neutral
- Membrane: x = 4–27, y = 11–20

Frame 2 — Left Bulge
- Bulge width: 6px, bulge height: 2px
- Bulge coords: x = 4–9, y = 10–11
- Outline: (4,11) → (9,11) and (5,10) → (8,10)

Frame 3 — Center Bulge
- Bulge width: 8px, bulge height: 2px
- Bulge coords: x = 12–19, y = 10–11
- Outline: (12,11) → (19,11) and (13,10) → (18,10)

Frame 4 — Right Bulge
- Bulge width: 6px, bulge height: 2px
- Bulge coords: x = 22–27, y = 10–11
- Outline: (22,11) → (27,11) and (23,10) → (26,10)

### Side Faces — Ceramic Casing
Static.

Layout
- Vertical ceramic ribs
- Glow seam
- No membrane visible

Pixel‑Accurate Specs
Ribs:
- 3 ribs
- Each rib = 3px wide
- Rib positions:
  - Rib 1: x = 6–8
  - Rib 2: x = 14–16
  - Rib 3: x = 22–24

Glow Seam:
- 1px cyan‑white
- x = 16, full height

### Bottom Face — Ceramic Base
Flat ceramic, 3‑tone shading, darkest band at bottom.

---

## Buoyancy Lift Platform — 32×32 UV‑Safe Texture Blueprint

Cephalari smooth, pressure‑driven elevator platform.

### Canvas
32×32, hard‑pixel, no AA, no transparency.

### Top Face — Ceramic Platform + Vent Rings

Overall Layout
- Central ceramic disk
- Concentric vent rings
- Crystal seam nodes
- Slight radial shading

Pixel‑Accurate Placement

Platform Disk
- Diameter: 22px
- Bounding box: x = 5 → 26, y = 5 → 26
- Center: (16,16)

Disk Outline
- 1px dark‑ceramic ring
- Must be continuous
- No AA

Vent Rings (Concentric)
Inner Vent Ring
- Diameter: 14px
- Bounding box: x = 9 → 23, y = 9 → 23
- Color: darker ceramic

Outer Vent Ring
- Diameter: 18px
- Bounding box: x = 7 → 24, y = 7 → 24
- Color: mid‑tone ceramic

Crystal Seam Nodes (2×2px each)
- North: (15,3)
- East: (27,15)
- South: (15,27)
- West: (3,15)
- Palette: cyan‑white (2‑tone)

### Side Faces — Ceramic Struts + Vent Ports

Layout
- Vertical ceramic struts
- Vent ports
- Glow seam

Pixel‑Accurate Specs
Struts
- 2 struts
- Each strut = 4px wide
- Positions:
  - Strut 1: x = 4–7
  - Strut 2: x = 24–27

Vent Ports
- 3 ports
- Each port = 3×3px
- Positions:
  - (12,8)
  - (12,14)
  - (12,20)
- Palette: dark ceramic + cyan center pixel

Glow Seam
- 1px cyan‑white
- x = 16, full height

### Bottom Face — Membrane Vents

Overall Layout
- Central membrane cluster
- 4 vent lobes
- Ceramic ring

Pixel‑Accurate Placement
Membrane Cluster
- Diameter: 12px
- Bounding box: x = 10 → 22, y = 10 → 22

Vent Lobes
Each lobe = 4×4px
- North: (14,6)
- East: (22,14)
- South: (14,22)
- West: (6,14)

Ceramic Ring
- 2px thick
- Outer boundary: full 32×32
- Inner boundary: 28×28

---

## Pressure Valve — 32×32 UV‑Safe Texture Blueprint

### Canvas
32×32, hard‑pixel, no AA, no transparency.

### Front Face — Valve Wheel + Pressure Indicator

Overall Layout
- Central valve wheel
- 4 spokes
- Ceramic housing
- Pressure indicator slit
- Crystal seam nodes

Pixel‑Accurate Placement

Valve Wheel
- Diameter: 14px
- Bounding box: x = 9 → 22, y = 9 → 22
- Center: (16,16)

Wheel Outline
- 1px dark‑ceramic ring
- Continuous, no AA

Spokes
Each spoke = 2px wide, 5px long.

North Spoke
- (15,9) → (16,13)

East Spoke
- (22,15) → (18,16)

South Spoke
- (15,22) → (16,18)

West Spoke
- (9,15) → (13,16)

Wheel Hub
- 4×4px
- Centered at (16,16)
- Coordinates: x = 14 → 17, y = 14 → 17

Pressure Indicator Slit
- Width: 10px
- Height: 2px
- Coordinates: x = 11 → 20, y = 24 → 25
- Fill: cyan‑white when “open”, dark ceramic when “closed”
(Texture variant, not animation.)

Crystal Seam Nodes (2×2px each)
- Top‑left: (3,3)
- Top‑right: (27,3)
- Bottom‑left: (3,27)
- Bottom‑right: (27,27)

### Side Faces — Ceramic Casing + Glow Seam

Layout
- Vertical ceramic ribs
- Glow seam
- No wheel visible

Pixel‑Accurate Specs
Ribs
- 3 ribs
- Each rib = 3px wide
- Rib positions:
  - Rib 1: x = 6–8
  - Rib 2: x = 14–16
  - Rib 3: x = 22–24

Glow Seam
- 1px cyan‑white
- x = 16, full height

### Top Face — Pressure Cap + Crystal Seam

Layout
- Circular ceramic cap
- Crystal seam
- Bolt nodes

Pixel‑Accurate Specs
Cap Diameter
- 14px
- Bounding box: x = 9 → 23, y = 9 → 23

Bolt Nodes
Each bolt = 2×2px
- (12,12)
- (20,12)
- (12,20)
- (20,20)

Crystal Seam
- 1px cyan line
- y = 16, x = 12 → 20

### Bottom Face — Ceramic Base
Flat ceramic, 3‑tone shading, darkest band at bottom.

---

## Checklist (Texture Suite)

All core machinery blocks now have UV‑safe, tile‑safe, vanilla‑compatible pixel specs and literal coordinate blueprints:
- Pressure Conduit
- Membrane Pump
- Pressure Turbine
- Spiral Gearbox
- Vent Piston
- Atmospheric Compressor
- Conveyor Membrane
- Buoyancy Lift Platform
- Pressure Valve
