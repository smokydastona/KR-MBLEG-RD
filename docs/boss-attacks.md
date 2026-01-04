# Krümblegård — Boss Attacks (Design / Behavior Notes)

This document describes what each boss attack does in-game, based on the current implementation in the mod.

## How attacks are timed
The boss has a simple **ability timeline** for most special attacks:

- **Windup (telegraph):** the boss plays an animation + sound and spawns some particles.
- **Impact tick:** the actual effect happens (projectile spawn, pull, dash impulse, etc.).
- **Recovery:** the remaining ticks before the ability finishes and cooldowns start.

In code, each ability has `totalTicks` and an `impactAt` tick inside that window.

Important detail: the internal counter **counts down** from `totalTicks` to `0`, and the impact triggers when `ticksRemaining == impactAt`.
So the Blockbench “impact frame” shown below is:

$$\text{impactFrame} = \text{totalTicks} - \text{impactAt}$$

For Blockbench (so you don't have to convert):

- Set your Blockbench project/timeline to **20 FPS**.
- Then **Frame 1 = 1 Minecraft tick**, and **Frame 0 is the start of the telegraph animation**.
- The "impact frame" listed below is the frame where the hit/spawn/pull happens.

## Phase overview
- **Phase 1:** Melee Swipe (fast) + Cleave (heavy) + Rune Bolt (ranged).
- **Phase 2:** Rune Dash (fast) + Gravitic Pull (heavy) + Rune Volley (ranged).
- **Phase 3:** Blink Strike (fast) + Meteor Arm (heavy) + Arcane Storm (ranged).
- **Phase 4:** Whirlwind (fast) + Meteor Shower (heavy) + Arcane Beam (ranged).

## Attacks

### 1) Melee Swipe (Phase 1 fast)
- **What it is:** custom boss melee (short windup before damage).
- **When used:** Phase 1 only, whenever the boss can reach its target.
- **Damage source:** uses the boss’s melee/attribute damage (config-driven via `BOSS_ATTACK_DAMAGE`).
- **Animation:** `animation.kruemblegard.attack_melee_swipe`.
- **Damage frame (Blockbench @20 FPS):** **Frame 6**.
- **Avoidance:** keep distance or break line-of-pathing; don’t let it stay in contact range.

### 2) Cleave (Phase 1 heavy)
- **Telegraph:** attack sound + soul particles; plays `animation.kruemblegard.attack_cleave`.
- **Timing:** `totalTicks = 18`, `impactAt = 10`.
- **Impact / damage frame (Blockbench @20 FPS):** **Frame 8**.
- **What happens on impact:** hits players in a short cone in front of the boss and knocks them back.
- **Damage:** ~`1.25x` the boss melee damage (based on `BOSS_ATTACK_DAMAGE`).
- **Avoidance:** stay out of its forward arc; strafe around the boss.

### 3) Rune Bolt (Phase 1 ranged)
- **Telegraph:** attack sound + soul particles; plays `animation.kruemblegard.attack_rune_bolt`.
- **Timing:** `totalTicks = 16`, `impactAt = 8`.
- **Impact / damage frame (Blockbench @20 FPS):** **Frame 8**.
- **What happens on impact:** spawns a `RuneBoltEntity` at the boss and fires it toward the current target.
- **Damage:** projectile-based (varies by entity).
- **Extra notes:** the bolt leaves END_ROD particles and despawns after ~80 ticks if it doesn’t hit.
- **Avoidance:** strafe / sidestep during the windup; keep moving so the line shot misses.

### 4) Rune Dash (Phase 2 fast)
- **Telegraph:** dash sound + soul particles; plays `animation.kruemblegard.attack_rune_dash`.
- **Timing:** `totalTicks = 14`, `impactAt = 7`.
- **Impact frame (Blockbench @20 FPS):** **Frame 7**.
- **What happens on impact:** boss lunges toward the target and applies brief slowness + chip damage to nearby players.
- **Avoidance:** sidestep during windup; don’t be directly in front of the boss.

### 5) Gravitic Pull (Phase 2 heavy)
- **Telegraph:** attack sound + soul particles; plays `animation.kruemblegard.attack_gravitic_pull`.
- **Timing:** `totalTicks = 18`, `impactAt = 10`.
- **Impact frame (Blockbench @20 FPS):** **Frame 8**.
- **What happens on impact:** pulls **players** in a radius toward the boss by adding velocity toward the boss.
  - Current radius: `16` blocks (bounding box inflated by 16).
  - Current pull strength: scaled to `0.4`.
- **Damage:** none (movement/control effect only).
- **Avoidance:** create distance before impact; jump/sprint after the pull to regain spacing.

### 6) Rune Volley (Phase 2 ranged)
- **Telegraph:** attack sound + soul particles; plays `animation.kruemblegard.attack_rune_volley`.
- **Timing:** `totalTicks = 20`, `impactAt = 10`.
- **Impact frame (Blockbench @20 FPS):** **Frame 10**.
- **What happens on impact:** fires a short 3-bolt spread toward the target.
- **Avoidance:** keep moving laterally; don’t backpedal in a straight line.

### 7) Blink Strike (Phase 3 fast)
- **Telegraph:** dash sound + soul particles; plays `animation.kruemblegard.attack_blink_strike`.
- **Timing:** `totalTicks = 14`, `impactAt = 8`.
- **Impact frame (Blockbench @20 FPS):** **Frame 6**.
- **What happens on impact:** boss teleports behind the target and attempts an immediate melee hit.
- **Avoidance:** keep moving and avoid being isolated; don’t rely on pure backpedaling.

### 8) Meteor Arm (Phase 3 heavy)
- **Telegraph:** attack sound + soul particles; plays `animation.kruemblegard.attack_meteor_arm`.
- **Timing:** `totalTicks = 22`, `impactAt = 12`.
- **Impact frame (Blockbench @20 FPS):** **Frame 10**.
- **What happens on impact:** spawns a `MeteorArmEntity` above the boss and sends it downward.
- **Damage:** projectile-based (varies by entity).
- **Extra notes:** the meteor arm continues to accelerate downward and leaves SMOKE particles; despawns after ~100 ticks.
- **Avoidance:** watch the drop; move out from under its fall path.

### 9) Arcane Storm (Phase 3 ranged)
- **Telegraph:** storm sound + soul particles; plays `animation.kruemblegard.attack_arcane_storm`.
- **Timing:** `totalTicks = 26`, `impactAt = 14`.
- **Impact frame (Blockbench @20 FPS):** **Frame 12**.
- **What happens on impact:** spawns 8 `ArcaneStormProjectileEntity` projectiles above random offsets around the boss.
  - Spawned around a ~10-block spread (random offsets in X/Z).
  - They fall straight down and leave ENCHANT particles.
- **Damage:** projectile-based (varies by entity).
- **Extra notes:** each storm projectile despawns after ~40 ticks if it doesn’t hit.
- **Avoidance:** keep moving and avoid standing still near the boss when the storm starts.

### 10) Whirlwind (Phase 4 fast)
- **Telegraph:** attack sound + soul particles; plays `animation.kruemblegard.attack_whirlwind`.
- **Timing:** `totalTicks = 18`, `impactAt = 9`.
- **Impact frame (Blockbench @20 FPS):** **Frame 9**.
- **What happens on impact:** AoE hit around the boss with knockback.
- **Avoidance:** don’t stack in melee range; create space on windup.

### 11) Meteor Shower (Phase 4 heavy)
- **Telegraph:** attack sound + soul particles; plays `animation.kruemblegard.attack_meteor_shower`.
- **Timing:** `totalTicks = 28`, `impactAt = 14`.
- **Impact frame (Blockbench @20 FPS):** **Frame 14**.
- **What happens on impact:** spawns multiple `MeteorArmEntity` drops around the boss.
- **Avoidance:** keep moving; don’t linger near the boss center.

### 12) Arcane Beam (Phase 4 ranged)
- **Telegraph:** attack sound + soul particles; plays `animation.kruemblegard.attack_arcane_beam`.
- **Timing:** `totalTicks = 22`, `impactAt = 12`.
- **Impact frame (Blockbench @20 FPS):** **Frame 10**.
- **What happens on impact:** a straight-line beam that damages players close to the beam path and briefly blinds.
- **Avoidance:** break line-of-sight and strafe out of the line.

## Phase transition pulse (not an “attack” but still combat pressure)
When phase changes while engaged, the boss does a small arena pulse:
- Grants itself temporary resistance/speed.
- Applies a brief slow to nearby players and applies an impulse:
  - Phase 2: small inward tug
  - Phase 3: stronger outward blast

---

If you want, I can add a short “animation mapping” table (attack → animation name → sound) or keep this purely gameplay-facing.
