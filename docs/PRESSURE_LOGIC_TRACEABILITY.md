# Pressure‑Logic Traceability — Requirements → Implementation

This file is the **traceability checklist** for the canonical spec:
- `docs/PRESSURE_LOGIC.md`

It maps each requirement (or requirement cluster) to concrete implementation locations in:
- Java gameplay/logic
- Registries/creative tabs
- Assets (blockstates/models/textures)
- Tooling (asset generator)

## Legend
- **[x] Implemented**: present in code/assets/tooling.
- **[~] Partial**: present but missing spec sub‑details (notably “optional variants”).
- **[d] Doc / concept**: explicitly concept-level, player-facing, or blueprint guidance; not required as runtime code unless separately mandated.
- **[ ] Gap**: not implemented.

## Mandatory interpretation
For this checklist, the previously “optional variants” that are *mechanical behaviors* are treated as **mandatory** requirements.

Progression/village/questline sections in the spec are treated as **optional guidance** (not mandatory runtime systems) unless separately and explicitly mandated.

## 1) Core Identity (system-level)
- [d] Pressure‑Logic is *mechanical/pneumatic*; redstone is *signals* (design intent and documentation).

## 2) Pressure model + conduit simulation
### 2.1 Continuous pressure storage (0–100)
- [x] Pressure stored as clamped continuous value 0–100
  - `src/main/java/com/kruemblegard/pressurelogic/PressureValue.java`

### 2.2 Visual pressure level mapping (0–100 → 0–5)
- [x] Conversion helpers and mapping
  - `src/main/java/com/kruemblegard/pressurelogic/PressureUtil.java`

### 2.3 Pressure diffusion / network behavior
- [x] Conduit tick diffuses towards neighborhood average with step limiting + leak
  - `src/main/java/com/kruemblegard/blockentity/PressureConduitBlockEntity.java`

### 2.4 Pressure gating / inline valves
- [x] Valves act as redstone-controlled flow gates in connection logic
  - `src/main/java/com/kruemblegard/pressurelogic/PressureUtil.java`
  - `src/main/java/com/kruemblegard/block/PressureValveBlock.java`

## 3) Atmosphere stability
### 3.1 Wayfall stability baseline
- [x] Wayfall dimension treated as stable air
  - `src/main/java/com/kruemblegard/pressurelogic/PressureAtmosphere.java`

### 3.2 Compressor stability bubble (5×5×5)
- [x] Outside Wayfall, scan 5×5×5 around pos for active compressor with `stability_level > 0`
  - `src/main/java/com/kruemblegard/pressurelogic/PressureAtmosphere.java`
  - `src/main/java/com/kruemblegard/block/AtmosphericCompressorBlock.java` (defines `stability_level`)

## 4) Rotation network
- [x] Rotation derived from turbines, propagated through shafts/gearboxes, gated by clutches
  - `src/main/java/com/kruemblegard/rotationlogic/RotationUtil.java`
  - `src/main/java/com/kruemblegard/block/PressureTurbineBlock.java`
  - `src/main/java/com/kruemblegard/block/SpiralShaftBlock.java`
  - `src/main/java/com/kruemblegard/block/SpiralGearboxBlock.java`
  - `src/main/java/com/kruemblegard/block/PressureClutchBlock.java`

## 5) Block family — base machinery set
### 5.A Pressure Turbine
- [x] Has `rotation_speed` (0–5), samples input pressure, consumes pressure proportional to speed, schedules ticks
  - `src/main/java/com/kruemblegard/block/PressureTurbineBlock.java`

### 5.B Membrane Pump
- [x] Has `pulse_rate` (0–5) and `powered`; generates pressure into network when stable air; redstone controls on/off
  - `src/main/java/com/kruemblegard/block/MembranePumpBlock.java`

### 5.C Vent Piston (soft-motion actuator)
- [x] Has `extension` (0–16), requires stable air + conduit pressure to extend, consumes pressure per step, gently pushes entities
  - `src/main/java/com/kruemblegard/block/VentPistonBlock.java`
- [x] “Rotate blocks if configured” implemented as a strong-redstone rotate mode (>= 12 signal)
  - `src/main/java/com/kruemblegard/block/VentPistonBlock.java`

### 5.D Spiral Gearbox
- [x] Has `ratio` enum (1:1, 1:2, 2:1, 1:4, 4:1) and cycles on use
  - `src/main/java/com/kruemblegard/block/SpiralGearboxBlock.java`

### 5.E Buoyancy Lift Platform
- [x] Has `lift_state` enum (idle/rising/falling), stable air gating, consumes pressure from conduit below while lifting
  - `src/main/java/com/kruemblegard/block/BuoyancyLiftPlatformBlock.java`

### 5.F Atmospheric Compressor
- [x] Has `stability_level` (0–5), Wayfall baseline = 5; gently pressurizes adjacent conduit networks while active
  - `src/main/java/com/kruemblegard/block/AtmosphericCompressorBlock.java`

### 5.G Pressure Loom (Flowwright Workstation)
- [x] Powered + stable air + pressure + rotation gated; performs crafting-like transformations
  - `src/main/java/com/kruemblegard/block/PressureLoomBlock.java`
- [d] “Village profession block” is worldgen/NPC integration (not implemented here).

### 5.H Conveyor Membrane
- [x] Has `pulse_phase` (0–3), stable-air gating, uses rotation level to animate and move item entities
  - `src/main/java/com/kruemblegard/block/ConveyorMembraneBlock.java`

### 5.I Pressure Valve (redstone integration)
- [x] Has `powered` state and facing; redstone controls open/close; used by pressure connection logic
  - `src/main/java/com/kruemblegard/block/PressureValveBlock.java`
  - `src/main/java/com/kruemblegard/pressurelogic/PressureUtil.java`

## 6) Expanded Integration — motion & transport
### 6.1 Vortex Funnel
- [x] Pulls items/mobs inward when powered; stable air gating; pressure cost; includes gentle/normal/harsh and directional modes
  - `src/main/java/com/kruemblegard/block/VortexFunnelBlock.java`
- [~] “Connects to compressor for swirl power” is represented via stable-air gating + adjacent conduit pressure; no explicit hard dependency on compressor block adjacency.

### 6.2 Pressure Rail
- [x] Powered gating; stable air + conduit pressure cost; affects entities on step; pulse animation state; mode affects speed and cost
  - `src/main/java/com/kruemblegard/block/PressureRailBlock.java`
- [x] “Bidirectional toggle direction” implemented via shift‑right‑click flipping `facing` 180° (no new blockstate)
  - `src/main/java/com/kruemblegard/block/PressureRailBlock.java`

### 6.3 Pneumatic Catapult
- [x] Uses conduit pressure to charge (`charge_level`) and fires on rising redstone edge; launches entities/items; stable air gating
  - `src/main/java/com/kruemblegard/block/PneumaticCatapultBlock.java`
- [~] Optional variants (angle dial / precision / scatter) not implemented.

### 6.4 Air‑Lift Tube
- [x] Entity vertical transport with tube mode + flow rate; stable air gating; redstone as control per mode
  - `src/main/java/com/kruemblegard/block/AirLiftTubeBlock.java`
- [x] “Consumes more pressure at high flow” implemented: requires adjacent conduit pressure and consumes more at higher `flow_rate`
  - `src/main/java/com/kruemblegard/block/AirLiftTubeBlock.java`

## 7) Expanded Integration — processing & crafting
### 7.5 Pressure Kiln
- [x] Stable air + rotation + pressure gating; can smelt items using vanilla smelting recipes; overpressure risk behavior present
  - `src/main/java/com/kruemblegard/block/PressureKilnBlock.java`

### 7.6 Membrane Press
- [x] Stable air + pressure + rotation gated; cycles `press_phase`; consumes pressure to convert Volatile Pulp → Volatile Resin
  - `src/main/java/com/kruemblegard/block/MembranePressBlock.java`

### 7.7 Crystal Infuser
- [x] Stable air + pressure gated; cycles `infuse_phase`; consumes pressure to convert Amethyst Shard → Attuned Rune Shard
  - `src/main/java/com/kruemblegard/block/CrystalInfuserBlock.java`

### 7.8 Pneumatic Separator
- [x] Stable air + pressure gated; routes items to sides based on heuristics; supports tri/density modes and active-side indicator
  - `src/main/java/com/kruemblegard/block/PneumaticSeparatorBlock.java`

## 8) Redstone integration suite (spec section 3)
- [x] Pressure valve (open/close)
  - `src/main/java/com/kruemblegard/block/PressureValveBlock.java`
- [x] Clutch (engage/disengage rotation)
  - `src/main/java/com/kruemblegard/block/PressureClutchBlock.java`
  - `src/main/java/com/kruemblegard/rotationlogic/RotationUtil.java`
- [x] Regulator (set pressure levels via redstone)
  - `src/main/java/com/kruemblegard/block/PressureRegulatorBlock.java`
- [x] Sequencer (redstone pulses → pressure pulses)
  - `src/main/java/com/kruemblegard/block/PressureSequencerBlock.java`
- [x] Sensor (pressure → redstone output)
  - `src/main/java/com/kruemblegard/block/PressureSensorBlock.java`

## 9) Registries, items, and creative tab rules
- [x] All Pressure‑Logic blocks registered
  - `src/main/java/com/kruemblegard/init/ModBlocks.java`
- [x] BlockItems + materials registered (including `coral_fiber`, `bio_ceramic`)
  - `src/main/java/com/kruemblegard/registry/ModItems.java`
  - `src/main/java/com/kruemblegard/registry/ModAutoBlockItems.java`
- [x] Dedicated Pressure‑Logic creative tab and exclusivity (Pressure‑Logic items excluded from other scanned tabs)
  - `src/main/java/com/kruemblegard/init/ModCreativeTabs.java`

## 10) Asset generator compliance (spec section 2.3)
- [x] Generator contains/handles the expanded 8 blocks and includes them in generation pipeline
  - `tools/generate_cephalari_engineering_assets.py`

## 11) Recipes, progression, villages, questline
- [d] “Crafting Recipes (Concept‑Level, Not JSON)” — acknowledged in spec as concept-only.
- [d] Progression tree / village structures — optional guidance + content planning; not required as runtime worldgen/NPC systems.
- [d] Optional tutorial questline — optional guidance; not required as a runtime quest system.

## 12) Texture pipeline requirements
- [x] Texture files exist for the Pressure‑Logic suite (block textures, including animated `.png.mcmeta` where applicable)
  - `src/main/resources/assets/kruemblegard/textures/block/`
- [d] UV-safe + literal pixel-grid blueprints are treated as **asset QA requirements**; this repository currently verifies presence/paths, but does not automatically pixel-validate the blueprints.

---

## TODO (optional future work)
- [ ] Pneumatic Catapult optional variants (angle dial / precision / scatter) if/when desired.
- [ ] Optional worldgen/NPC/quest onboarding (villages / Flowwright / tutorial questline) **only if desired**, and must be gated so it is never required for Pressure‑Logic to function.
