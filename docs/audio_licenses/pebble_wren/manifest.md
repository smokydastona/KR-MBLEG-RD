# Pebble Wren Audio Manifest

Checked: 2026-03-22

This manifest records the second-pass Pebble Wren sound pack. Most cues are procedural, but the flutter cue now comes from a real public-domain wingbeat recording after the synthetic replacement failed to read as actual flapping.

## Source Strategy

- Generated in-house with `ffmpeg` filter graphs.
- Vocal cues are synthesized from layered sine-based chirps created with `aevalsrc`, shaped with high-pass / low-pass filtering and limiting.
- The flutter cue is trimmed from the public-domain Yellowstone Sandhill Crane recording and then filtered / pitched / sped up to fit a lighter tiny-bird flap.
- All outputs are rendered as mono OGG/Vorbis files under `src/main/resources/assets/kruemblegard/sounds/entity/pebble_wren/`.

## External Source Media

### Source A
- Downloaded source file name: `yellowstone_sandhill_crane_source_pd.mp3`
- Source page: https://commons.wikimedia.org/wiki/File:Yellowstone_sound_library_-_Sandhill_Crane_-_001.mp3
- Direct media URL: https://upload.wikimedia.org/wikipedia/commons/3/39/Yellowstone_sound_library_-_Sandhill_Crane_-_001.mp3
- Source platform: Wikimedia Commons
- License: Public domain (`PD-USGov` / `CC-PD-Mark` on Commons)
- Author: NPS & MSU Acoustic Atlas / Jennifer Jerrett
- Attribution required: no
- Approved for shipping: yes
- Source SHA-256: `E090AE6AA0E46932195BE998F44F96D63E95DB7C3C73FB6B241BDAC989F67CE6`
- Source file size: `388504 bytes`
- Rights statement text:
	- `This work is in the public domain in the United States because it is a work prepared by an officer or employee of the United States Federal Government as part of that person's official duties.`
	- `This file has been identified as being free of known restrictions under copyright law, including all related and neighboring rights.`

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
- Duration: `0.183152s`
- Derived SHA-256: `EA561DB95901C73E69F6BB7BAA6262B5BBAE50074DFAB0231669AB1525F0ED3F`
- Source media: Source A
- Processing chain: trim `0.09-0.43s` from the Sandhill Crane source, high-pass `1200 Hz`, low-pass `8500 Hz`, denoise, pitch up slightly with `asetrate`, speed up with `atempo=1.7`, light gain, very short fades, limiter, metadata stripped, mono OGG

### `ore_ping`
- Derived output file: `src/main/resources/assets/kruemblegard/sounds/entity/pebble_wren/ore_ping.ogg`
- Intended in-game event: `entity.pebble_wren.ore_ping`
- Duration: `0.491995s`
- Derived SHA-256: `2F0CD42B03B4E2C9CCCE8A9BC5294C2B3BFCD26CF01D46CD0379315125B14AC3`
- Synthesis chain: layered chirp phrase from `aevalsrc`, high-pass `2100 Hz`, low-pass `10000 Hz`, short echo tail, limiter, mono OGG

## Notes

- The Pebble Wren set is now a mixed-source pack: procedural chirps for vocal/social cues and one public-domain wildlife flap for the takeoff/landing cue.
- The manifest is the canonical provenance record for the current shipped Pebble Wren set.
- If the flutter cue is replaced again, keep the old source entry and append a new one rather than silently overwriting provenance.