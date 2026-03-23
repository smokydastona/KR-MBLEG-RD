# Pebble Wren Audio Manifest

Checked: 2026-03-22

This manifest records the second-pass Pebble Wren sound pack that replaced the noisy field-recording-derived first pass with fully procedural synthesized assets.

## Generation Method

- Generated in-house with `ffmpeg` filter graphs.
- Vocal cues are synthesized from layered sine-based chirps created with `aevalsrc`, shaped with high-pass / low-pass filtering and limiting.
- The flutter cue is synthesized from layered `anoisesrc` bursts with filtering, fades, delays, and limiting.
- No third-party recordings are used in the shipped Pebble Wren assets.
- All outputs are rendered as mono OGG/Vorbis files under `src/main/resources/assets/kruemblegard/sounds/entity/pebble_wren/`.

## Derived Outputs

### `ambient`
- Derived output file: `src/main/resources/assets/kruemblegard/sounds/entity/pebble_wren/ambient.ogg`
- Intended in-game event: `entity.pebble_wren.ambient`
- Duration: `0.260000s`
- Derived SHA-256: `2FD23A97052A3029149044183A61D364300EAC1F793AC85C634EC822EEE84DC7`
- Synthesis chain: two bright rising/falling chirps from `aevalsrc`, high-pass `2200 Hz`, low-pass `10000 Hz`, limiter, mono OGG

### `perch_call`
- Derived output file: `src/main/resources/assets/kruemblegard/sounds/entity/pebble_wren/perch_call.ogg`
- Intended in-game event: `entity.pebble_wren.perch_call`
- Duration: `0.340000s`
- Derived SHA-256: `FB533170D55AF24800399CB3A302A78A51FACB7B426723F29D93F4573DD22F6F`
- Synthesis chain: three assertive layered chirps from `aevalsrc`, high-pass `2200 Hz`, low-pass `10200 Hz`, limiter, mono OGG

### `perch_reply`
- Derived output file: `src/main/resources/assets/kruemblegard/sounds/entity/pebble_wren/perch_reply.ogg`
- Intended in-game event: `entity.pebble_wren.perch_reply`
- Duration: `0.220000s`
- Derived SHA-256: `91EB4D6C18E07384A16811D59378C3C0AB771CEA2ABE8DF5B084E87E8A06F30B`
- Synthesis chain: two softer descending chirps from `aevalsrc`, high-pass `2400 Hz`, low-pass `9800 Hz`, limiter, mono OGG

### `flourish`
- Derived output file: `src/main/resources/assets/kruemblegard/sounds/entity/pebble_wren/flourish.ogg`
- Intended in-game event: `entity.pebble_wren.flourish`
- Duration: `0.350000s`
- Derived SHA-256: `4E379F74F7CBF5D05EE9259FC9D3AF262070F743ECB60F85D5A2FAD1F142A326`
- Synthesis chain: four quick decorative chirps from `aevalsrc`, high-pass `2300 Hz`, low-pass `10400 Hz`, limiter, mono OGG

### `hurt`
- Derived output file: `src/main/resources/assets/kruemblegard/sounds/entity/pebble_wren/hurt.ogg`
- Intended in-game event: `entity.pebble_wren.hurt`
- Duration: `0.120000s`
- Derived SHA-256: `57D6C862BAF564251EE6C23C4C3EE595EF51C6C1CE328AE6DF365E388F9121EE`
- Synthesis chain: single sharp high chirp from `aevalsrc`, high-pass `2600 Hz`, low-pass `9800 Hz`, limiter, mono OGG

### `death`
- Derived output file: `src/main/resources/assets/kruemblegard/sounds/entity/pebble_wren/death.ogg`
- Intended in-game event: `entity.pebble_wren.death`
- Duration: `0.340000s`
- Derived SHA-256: `12B91A2CD7D82E27A1EADEFAAAC04940E7122D4B93FF53A47889161467143F49`
- Synthesis chain: two subdued descending chirps from `aevalsrc`, high-pass `1800 Hz`, low-pass `7600 Hz`, limiter, mono OGG

### `flutter`
- Derived output file: `src/main/resources/assets/kruemblegard/sounds/entity/pebble_wren/flutter.ogg`
- Intended in-game event: `entity.pebble_wren.flutter`
- Duration: `0.220000s`
- Derived SHA-256: `564AC70B797FE8B70E8A3AA399E83728BF75E62A54F8AEDB460593A6046F1FD6`
- Synthesis chain: four delayed filtered-noise bursts from `anoisesrc`, fades, high/low-pass filtering, limiter, mono OGG

### `ore_ping`
- Derived output file: `src/main/resources/assets/kruemblegard/sounds/entity/pebble_wren/ore_ping.ogg`
- Intended in-game event: `entity.pebble_wren.ore_ping`
- Duration: `0.491995s`
- Derived SHA-256: `2F0CD42B03B4E2C9CCCE8A9BC5294C2B3BFCD26CF01D46CD0379315125B14AC3`
- Synthesis chain: layered chirp phrase from `aevalsrc`, high-pass `2100 Hz`, low-pass `10000 Hz`, short echo tail, limiter, mono OGG

## Notes

- These files are procedural gameplay assets intended to replace both the old vanilla parrot placeholders and the noisy first-pass field-derived clips.
- The manifest is the canonical provenance record for the current shipped Pebble Wren set.
- If higher-quality self-generated or clearly redistributable recordings are added later, append a new section rather than silently replacing this record.