"""Compare profession-related GeckoLib bones + UVs against spiral_strider.geo.json.

This is a validation helper to ensure all cephalari/vehicle geo files keep the
same bone structure and UV layout that overlays expect.

Run:
  python tools/compare_profession_geo_to_spiral_strider.py

Exit code:
  0: all match
  1: one or more mismatches
"""

from __future__ import annotations

import json
import sys
from pathlib import Path
from typing import Any

GEO_DIR = Path("src/main/resources/assets/kruemblegard/geo")
REFERENCE = GEO_DIR / "spiral_strider.geo.json"

TARGETS = [
    "cephalari.geo.json",
    "cephalari_zombie.geo.json",
    "cephalari_zombie_1.geo.json",
    "cephalari_zombie_2.geo.json",
    "cephalari_zombie_3.geo.json",
    "cephalari_zombie_4.geo.json",
    "cephalari_zombie_5.geo.json",
    "driftskimmer.geo.json",
    "treadwinder.geo.json",
    "echo_harness.geo.json",
]

REQUIRED_BONES = ["shell", "profession", "profession_hat", "profession_level"]


def _load_json(path: Path) -> dict[str, Any]:
    with path.open("r", encoding="utf-8") as f:
        return json.load(f)


def _bone_index(doc: dict[str, Any]) -> dict[str, dict[str, Any]]:
    geos = doc.get("minecraft:geometry")
    if not isinstance(geos, list) or not geos:
        raise ValueError("Missing minecraft:geometry[0]")
    bones = geos[0].get("bones")
    if not isinstance(bones, list):
        raise ValueError("Missing minecraft:geometry[0].bones")
    idx: dict[str, dict[str, Any]] = {}
    for b in bones:
        if isinstance(b, dict) and isinstance(b.get("name"), str):
            idx[b["name"]] = b
    return idx


def _uv_sig(value: Any) -> Any:
    if isinstance(value, list):
        return tuple(_uv_sig(v) for v in value)
    if isinstance(value, dict):
        return tuple((k, _uv_sig(v)) for k, v in sorted(value.items()))
    return value


def _compare_cube_uvs(ref_cubes: list[dict[str, Any]], tgt_cubes: list[dict[str, Any]]) -> list[str]:
    if len(ref_cubes) != len(tgt_cubes):
        return [f"cube_count {len(tgt_cubes)} != ref {len(ref_cubes)}"]

    issues: list[str] = []
    for i, (rc, tc) in enumerate(zip(ref_cubes, tgt_cubes)):
        if _uv_sig(rc.get("uv")) != _uv_sig(tc.get("uv")):
            issues.append(f"cube[{i}] uv differs")
    return issues


def main() -> int:
    if not REFERENCE.exists():
        print(f"ERROR: reference geo missing: {REFERENCE}")
        return 1

    ref_doc = _load_json(REFERENCE)
    ref_bones = _bone_index(ref_doc)

    for bone in ("profession", "profession_level"):
        if bone not in ref_bones:
            print(f"ERROR: reference missing bone: {bone}")
            return 1

    ref_prof_cubes = ref_bones["profession"].get("cubes", [])
    ref_badge_cubes = ref_bones["profession_level"].get("cubes", [])

    if not isinstance(ref_prof_cubes, list):
        ref_prof_cubes = []
    if not isinstance(ref_badge_cubes, list):
        ref_badge_cubes = []

    print("REF spiral_strider.geo.json:")
    print("  profession cubes:", len(ref_prof_cubes))
    print("  profession_level cubes:", len(ref_badge_cubes))
    print()

    any_bad = False

    for name in TARGETS:
        path = GEO_DIR / name
        if not path.exists():
            any_bad = True
            print(f"{name}: MISSING FILE")
            continue

        try:
            doc = _load_json(path)
            bones = _bone_index(doc)
        except Exception as e:  # noqa: BLE001
            any_bad = True
            print(f"{name}: FAILED TO PARSE/INDEX ({e})")
            continue

        missing = [b for b in REQUIRED_BONES if b not in bones]
        if missing:
            any_bad = True
            print(f"{name}: MISSING BONES {missing}")
            continue

        shell_pivot = tuple(bones["shell"].get("pivot", []))

        def chk_parent_pivot(bn: str) -> list[str]:
            b = bones[bn]
            parent = b.get("parent")
            pivot = tuple(b.get("pivot", []))
            issues: list[str] = []
            if parent != "shell":
                issues.append(f"{bn}.parent={parent}")
            if pivot != shell_pivot:
                issues.append(f"{bn}.pivot!=shell")
            return issues

        issues: list[str] = []
        issues += chk_parent_pivot("profession")
        issues += chk_parent_pivot("profession_hat")
        issues += chk_parent_pivot("profession_level")

        prof_cubes = bones["profession"].get("cubes", [])
        badge_cubes = bones["profession_level"].get("cubes", [])
        if not isinstance(prof_cubes, list):
            prof_cubes = []
        if not isinstance(badge_cubes, list):
            badge_cubes = []

        issues += [f"profession {x}" for x in _compare_cube_uvs(ref_prof_cubes, prof_cubes)]
        issues += [f"profession_level {x}" for x in _compare_cube_uvs(ref_badge_cubes, badge_cubes)]

        if issues:
            any_bad = True
            print(f"{name}:")
            for it in issues:
                print("  -", it)
        else:
            print(f"{name}: OK (matches spiral_strider for parents+pivots+UVs)")

    print("\nRESULT:", "NOT ALL MATCH" if any_bad else "ALL MATCH")
    return 1 if any_bad else 0


if __name__ == "__main__":
    raise SystemExit(main())
