# Maintainer Runbook (Krümblegård)

This repo is a Forge 1.20.1 + GeckoLib mod. CI is the authoritative clean build environment.

## The “ship-safe” workflow (do this for any change)

1. **Scan the workspace**
   - Check VS Code **Problems** (workspace-wide).
   - If you changed data/assets/registries/worldgen, do an “impact radius” scan (see below).

2. **Regenerate derived docs (when applicable)**
    - Materials: run the generator to update the material bibles.
       - VS Code task: **Generate Material Bibles**
       - CLI: `powershell -NoProfile -ExecutionPolicy Bypass -File tools\generate_material_bibles.ps1`
    - Sounds: run the generator to update `docs/Sound_Bible.md`.
       - VS Code task: **Generate Sound Bible**
       - CLI: `powershell -NoProfile -ExecutionPolicy Bypass -File tools\generate_sound_bible.ps1`
   - Loot tables: run the generator to update `docs/Loot_Table_Bible.md`.
     - VS Code task: **Generate Loot Table Bible**
     - CLI: `powershell -NoProfile -ExecutionPolicy Bypass -File tools\generate_loot_table_bible.ps1`

3. **Update human docs to match behavior**
   - `README.md` (dev notes + how to reproduce)
   - `docs/MOD_FEATURES.md` (feature reference)
   - Any related docs (Codex JSON text, lore docs, etc.)

4. **Changelog discipline (when notable)**
   - If you’re about to ship a jar, add a versioned entry in `CHANGELOG.md` matching the jar versioning scheme in `build.gradle`.

5. **Commit hygiene**
   - Keep generated doc changes (like `docs/Loot_Table_Bible.md`) in the same commit as their source changes.
   - Don’t commit `build/`, `.gradle/`, or other generated outputs.

## Impact radius checklists

### Worldgen / datapack changes (`src/main/resources/data/**/worldgen/**`)
- Verify referenced IDs exist (features, structures, structure_sets, template_pools, processor_lists, tags).
- Validate biome tags / biome modifiers references.
- Grep for invalid/renamed vanilla IDs (1.20.1 has many historical removals).

### Registries / DeferredRegister changes
- Avoid eager `.get()` during registration.
- Check cross-registry references (items referencing blocks/entities).
- Keep client-only registrations under `src/main/java/.../client/`.

### Assets / GeckoLib JSON
- Confirm `ResourceLocation` paths match files under `src/main/resources/assets/kruemblegard/**`.
- Validate animation/model JSONs are well-formed.

### Gameplay flow changes
- Re-scan the Traprock ambush flow (dormant → awaken → attack) and ensure server/client separation stays correct.

## Tooling

### Generator runner
If you want a single command for doc generation, run:

`powershell -NoProfile -ExecutionPolicy Bypass -File tools\run_generators.ps1`

This currently runs:
- Material bibles generator (`tools/generate_material_bibles.ps1`)
- Sound bible generator (`tools/generate_sound_bible.ps1`)
- Loot Table Bible generator (`tools/generate_loot_table_bible.ps1`)

## Long-running world operations (Chunky/GenMemoryLeakFix lessons)

If you add any feature that bulk loads/unloads chunks (pregen, large-area structure placement, mass scanning):

- Prefer **bounded work + backpressure** (don’t runaway-load chunks and explode memory).
- Prefer **stable progress reporting** (sampled ETA/rate; avoid per-tick spam).
- Prefer **resumable state** for long tasks (SavedData / disk persistence).
- Treat **unload-path cleanup** as required for anything “no-tick chunk loading” adjacent.

Important vanilla pitfall:
- Some vanilla cleanup paths only run when chunks tick. If you load/generate chunks without a ticking ticket, then unload them, you can expose retained-reference issues (MC-272673 class of bugs; see Chunky/GenMemoryLeakFix investigations).
- In Krümblegård, Wayfall’s origin monument is intentionally placed when a player enters Wayfall (chunks are ticketed/ticking), not at dimension load.
