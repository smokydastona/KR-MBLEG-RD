# Contributing

## Asset sourcing
- Do **not** copy textures, models, sounds, music, or other assets from other mods.
- Even when code patterns are reusable, assets are often under different licenses.
- Prefer creating original assets or using assets you have explicit permission/license to use.

## Commits
- Prefer **Conventional Commits** for commit messages (see `.githooks/commit-msg`).

## Worldgen changes
- Keep worldgen data-driven under `src/main/resources/data/kruemblegard/worldgen/**`.
- Prefer tags under `data/kruemblegard/tags/worldgen/biome/**` to avoid hardcoded biome lists.
- If you add/rename worldgen IDs, update the corresponding keys in `src/main/java/com/kruemblegard/worldgen/ModWorldgenKeys.java`.
