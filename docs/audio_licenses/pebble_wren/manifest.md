# Pebble Wren Audio Manifest

Checked: 2026-03-22

This manifest records the first-pass Pebble Wren sound pack that replaced vanilla parrot placeholders.

## Source Media

### Source A
- Downloaded source file name: `ruby-crowned_kinglet_source_pd.ogg`
- Source page: https://commons.wikimedia.org/wiki/File:Ruby-crowned_Kinglet.ogg
- Direct media URL: https://upload.wikimedia.org/wikipedia/commons/0/00/Ruby-crowned_Kinglet.ogg
- Source platform: Wikimedia Commons
- License: Public domain (`PD-self`)
- Author: G. McGrane
- Attribution required: no
- Approved for shipping: yes
- Source SHA-256: `605A58C0C7E9E7BADF4A8EE7D863DF365E28C36DF9F87A61E5D428735F06F5AE`
- Rights statement text:
  - `I, the copyright holder of this work, release this work into the public domain.`
  - `This applies worldwide.`

### Source B
- Downloaded source file name: `yellowstone_sandhill_crane_source_pd.mp3`
- Source page: https://commons.wikimedia.org/wiki/File:Yellowstone_sound_library_-_Sandhill_Crane_-_001.mp3
- Direct media URL: https://upload.wikimedia.org/wikipedia/commons/3/39/Yellowstone_sound_library_-_Sandhill_Crane_-_001.mp3
- Source platform: Wikimedia Commons
- License: Public domain (`PD-USGov` / `CC-PD-Mark` on Commons)
- Author: NPS & MSU Acoustic Atlas / Jennifer Jerrett
- Attribution required: no
- Approved for shipping: yes
- Source SHA-256: `E090AE6AA0E46932195BE998F44F96D63E95DB7C3C73FB6B241BDAC989F67CE6`
- Rights statement text:
  - `This work is in the public domain in the United States because it is a work prepared by an officer or employee of the United States Federal Government as part of that person's official duties.`
  - `This file has been identified as being free of known restrictions under copyright law, including all related and neighboring rights.`

## Derived Outputs

### `ambient`
- Derived output file: `src/main/resources/assets/kruemblegard/sounds/entity/pebble_wren/ambient.ogg`
- Intended in-game event: `entity.pebble_wren.ambient`
- Duration: `0.259977s`
- Derived SHA-256: `541254FE43DC51DAC4DDE624B97CE23F0E6FA9763612466FB2B5062B1E8377A6`
- Source media: Source A
- Processing chain: trim `0.10-0.36s`, high-pass `1800 Hz`, low-pass `9000 Hz`, `4 ms` fade in, `40 ms` fade out, limiter to about `-3 dBFS`, mono OGG

### `perch_call`
- Derived output file: `src/main/resources/assets/kruemblegard/sounds/entity/pebble_wren/perch_call.ogg`
- Intended in-game event: `entity.pebble_wren.perch_call`
- Duration: `0.340000s`
- Derived SHA-256: `622016D8C2B693CB8C6F6F9F873C7E7C988CF996A3B150C704BCF957A8E12BCF`
- Source media: Source A
- Processing chain: trim `1.02-1.36s`, high-pass `1900 Hz`, low-pass `9200 Hz`, `4 ms` fade in, `60 ms` fade out, limiter to about `-3 dBFS`, mono OGG

### `perch_reply`
- Derived output file: `src/main/resources/assets/kruemblegard/sounds/entity/pebble_wren/perch_reply.ogg`
- Intended in-game event: `entity.pebble_wren.perch_reply`
- Duration: `0.220000s`
- Derived SHA-256: `1C6BF0541BA99B1DDA740721EB3733A004CC08460DDCB01C88D0EA1F8D839074`
- Source media: Source A
- Processing chain: trim `2.02-2.24s`, high-pass `2000 Hz`, low-pass `9000 Hz`, `3 ms` fade in, `50 ms` fade out, limiter to about `-3 dBFS`, mono OGG

### `flourish`
- Derived output file: `src/main/resources/assets/kruemblegard/sounds/entity/pebble_wren/flourish.ogg`
- Intended in-game event: `entity.pebble_wren.flourish`
- Duration: `0.350000s`
- Derived SHA-256: `FA94F28CC13D69F23BB32F4759B275C38B73377319343F8C8B03D682A28027A5`
- Source media: Source A
- Processing chain: trim `3.12-3.47s`, high-pass `2000 Hz`, low-pass `9500 Hz`, `3 ms` fade in, `90 ms` fade out, limiter to about `-3 dBFS`, mono OGG

### `hurt`
- Derived output file: `src/main/resources/assets/kruemblegard/sounds/entity/pebble_wren/hurt.ogg`
- Intended in-game event: `entity.pebble_wren.hurt`
- Duration: `0.120385s`
- Derived SHA-256: `312D6697F5322085101717F486745A366DCD8E583D83D58CF4A8F27262ABE9A8`
- Source media: Source A
- Processing chain: trim `4.18-4.31s`, high-pass `2200 Hz`, low-pass `8500 Hz`, pitch up with `asetrate*1.08`, `2 ms` fade in, `40 ms` fade out, limiter to about `-3 dBFS`, mono OGG

### `death`
- Derived output file: `src/main/resources/assets/kruemblegard/sounds/entity/pebble_wren/death.ogg`
- Intended in-game event: `entity.pebble_wren.death`
- Duration: `0.340930s`
- Derived SHA-256: `23C52A58DF45108301205EEA9E4D0964CD210BAC89F8D57C73F952EAEB8EFDB2`
- Source media: Source A
- Processing chain: trim `5.34-5.64s`, high-pass `1700 Hz`, low-pass `7200 Hz`, pitch down with `asetrate*0.88`, `3 ms` fade in, `90 ms` fade out, limiter to about `-3 dBFS`, mono OGG

### `flutter`
- Derived output file: `src/main/resources/assets/kruemblegard/sounds/entity/pebble_wren/flutter.ogg`
- Intended in-game event: `entity.pebble_wren.flutter`
- Duration: `0.220000s`
- Derived SHA-256: `726DCBF972F241BD3C7BCBB0162B3D4DE06E9B950179DFC8B20F0EB6E62EE597`
- Source media: Source B
- Processing chain: trim `0.34-0.56s`, high-pass `700 Hz`, low-pass `7000 Hz`, `3 ms` fade in, `60 ms` fade out, limiter to about `-3 dBFS`, mono OGG

### `ore_ping`
- Derived output file: `src/main/resources/assets/kruemblegard/sounds/entity/pebble_wren/ore_ping.ogg`
- Intended in-game event: `entity.pebble_wren.ore_ping`
- Duration: `0.467982s`
- Derived SHA-256: `B885361AA525B369A23E9864A7DE722000C7AEBFDE6A0B1CF8851A4153B707BA`
- Source media: Source A
- Processing chain: trim `7.12-7.57s`, high-pass `1800 Hz`, low-pass `9000 Hz`, light echo (`18 ms`, `0.16`), `4 ms` fade in, `100 ms` fade out, limiter to about `-3 dBFS`, mono OGG

## Notes

- These files are first-pass gameplay assets intended to remove the remaining vanilla parrot placeholders from Pebble Wren behavior.
- The raw source media is cached in this folder so the source hashes remain reproducible.
- Final shipped OGGs were re-encoded with metadata stripped so source-side tags are not carried into the mod assets.
- If higher-variety public-domain songbird sources are added later, extend this manifest instead of replacing entries without recording the change.