# Wayfall Dimension - Aether Generation Pattern

This document maps Wayfall's worldgen to the Aether's proven pattern.

## Exact Matches to Aether

### Noise Settings (`wayfall.json`)
- ✅ `size_horizontal: 2, size_vertical: 1` (Aether uses 2/1 for chunky islands)
- ✅ `sea_level: -64` (prevents water spawning in sky)
- ✅ `aquifers_enabled: false`
- ✅ `disable_mob_generation: false` (mobs can spawn)
- ✅ `ore_veins_enabled: false`

### Dimension Type (`wayfall.json`)
- ✅ `natural: true` (prevents experimental warning)
- ✅ `has_skylight: true` (Aether has day/night cycles)
- ✅ No `fixed_time` (allows day/night cycles)
- ✅ Same structure as Aether highlands dimension_type

### Density Functions

#### Base 3D Noise (`base_3d_noise.json`)
- ✅ Uses `old_blended_noise` (Aether's exact type)
- ✅ Same parameters: `xz_scale: 0.1, y_scale: 0.02, xz_factor: 80.0, y_factor: 160.0`

#### Amplification System
- ✅ `amplification.json` noise (octave-based, matches Aether)
- ✅ `amplification` density function using `weird_scaled_sampler`
- ✅ `rarity_value_mapper: "type_1"` for dramatic size variation

#### Terrain Density (`terrain_density.json`)
- ✅ Uses Aether’s core “shattered” pattern constants (0.1, 0.2, -0.1, -0.27, 0.5)
- ⚠️ Not an exact copy: Wayfall adds its own `island_shape` (2D elevation + 3D noise) and a `mid_bulge` term that is tuned to keep islands playable above the Y=96 void cutoff while preserving air gaps.

#### Splines (Y-Slides)
- ✅ `mid_bulge.json`: ramps up after the void cutoff, then fades back down at higher Y so the dimension doesn’t fill solidly to build limit.
- ✅ Both use elevation_2d as coordinate (simplified from Aether's elevation_shattered)

#### Feature Placement
- ✅ All 70 placed features use `MOTION_BLOCKING` heightmap (works with has_skylight: true)
- ✅ Biome modifiers use `vegetal_decoration` step
- ✅ Block tag `wayfall_ground` defines valid substrate

## Simplifications (for maintainability)

### Factor
- **Aether**: Complex spline based on ridges, erosion, temperature
- **Wayfall**: Spline based on ridges/erosion/temperature (kept relatively simple compared to Aether’s full setup)
- **Reason**: Controls terrain variation without requiring full Overworld routing.

### Elevation
- **Aether**: Uses `shifted_noise` with `abs` for elevation_shattered
- **Wayfall**: Uses `shifted_noise` for `elevation_2d` (no `abs`) so elevation can be negative as well as positive.
- **Reason**: Negative elevation values are important to carve out empty space between islands; `abs` tends to push the whole field positive and can lead to overly-solid terrain.

## Result

Wayfall now uses the **proven Aether generation pattern** with:
- Aether-style chunky floating islands (not smooth vertical terrain)
- Dramatic island size variation (huge + tiny islands mixed)
- Proper Y-range (islands form at Y=100-190, not ground level)
- Smooth organic terrain (old_blended_noise)
- Day/night cycles (no experimental warning)
- Mob spawning enabled
- Custom blocks and biomes (Wayfall aesthetic)

**This is a functional Aether-style dimension using Krümblegård blocks.**
