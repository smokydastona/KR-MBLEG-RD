from __future__ import annotations

import json
import math
from dataclasses import dataclass
from pathlib import Path
from typing import Literal

from PIL import Image


FaceName = Literal["top", "bottom", "north", "south", "east", "west"]


PALETTE = {
    "ceramic": {
        "light": "#E9DCCB",
        "mid": "#D4C3B1",
        "dark": "#B8A795",
        "deep": "#9C8A78",
        "line": "#6E5F52",
    },
    "membrane": {
        "highlight": "#F4A9B8",
        "mid": "#D97A8A",
        "shadow": "#B85C6D",
        "deep": "#8E3D4E",
        "line": "#5A2A33",
    },
    "air": {
        "bright": "#FFFFFF",
        "mid": "#C0F0FF",
        "shadow": "#8AC2D1",
        "deep": "#4F7F8C",
        "line": "#2C4A52",
    },
    "crystal": {
        "bright": "#E8FFFF",
        "mid": "#A5EAF7",
        "shadow": "#6AB5C8",
        "deep": "#3E7A8A",
        "line": "#1F4A55",
    },
    "shell": {
        "highlight": "#F2E4D6",
        "mid": "#D9C2B0",
        "shadow": "#B89F8C",
        "deep": "#8F6F5A",
        "line": "#5C4638",
    },
}


def _hex_to_rgb(hex_color: str) -> tuple[int, int, int]:
    if not (isinstance(hex_color, str) and len(hex_color) == 7 and hex_color.startswith("#")):
        raise ValueError(f"Invalid hex color: {hex_color!r}")
    r = int(hex_color[1:3], 16)
    g = int(hex_color[3:5], 16)
    b = int(hex_color[5:7], 16)
    return r, g, b


def _make_grid(size: int, fill: str) -> list[list[str]]:
    return [[fill for _ in range(size)] for _ in range(size)]


def _write_json(path: Path, obj: object) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(obj, indent=2) + "\n", encoding="utf-8")


def _write_png_from_grid(path: Path, grid: list[list[str]]) -> None:
    height = len(grid)
    width = len(grid[0]) if height else 0
    img = Image.new("RGBA", (width, height))
    px = img.load()
    for y in range(height):
        if len(grid[y]) != width:
            raise ValueError("Non-rectangular grid")
        for x in range(width):
            r, g, b = _hex_to_rgb(grid[y][x])
            px[x, y] = (r, g, b, 255)
    path.parent.mkdir(parents=True, exist_ok=True)
    img.save(path)


def _write_png_from_frames_vertical(path: Path, frames: list[list[list[str]]]) -> None:
    if not frames:
        raise ValueError("No frames")
    frame_h = len(frames[0])
    frame_w = len(frames[0][0]) if frame_h else 0
    for frame in frames:
        if len(frame) != frame_h or any(len(row) != frame_w for row in frame):
            raise ValueError("Frame size mismatch")

    img = Image.new("RGBA", (frame_w, frame_h * len(frames)))
    px = img.load()
    for i, frame in enumerate(frames):
        for y in range(frame_h):
            for x in range(frame_w):
                r, g, b = _hex_to_rgb(frame[y][x])
                px[x, y + i * frame_h] = (r, g, b, 255)

    path.parent.mkdir(parents=True, exist_ok=True)
    img.save(path)


def _circle_mask(size: int, cx: int, cy: int, radius: int) -> list[list[bool]]:
    mask: list[list[bool]] = []
    r2 = radius * radius
    for y in range(size):
        row: list[bool] = []
        for x in range(size):
            dx = x - cx
            dy = y - cy
            row.append((dx * dx + dy * dy) <= r2)
        mask.append(row)
    return mask


def _ellipse_mask(size: int, x0: int, y0: int, x1: int, y1: int) -> list[list[bool]]:
    # Inclusive bounds.
    if x1 < x0 or y1 < y0:
        raise ValueError("Invalid ellipse bounds")
    rx = (x1 - x0) / 2.0
    ry = (y1 - y0) / 2.0
    cx = x0 + rx
    cy = y0 + ry

    mask: list[list[bool]] = []
    for y in range(size):
        row: list[bool] = []
        for x in range(size):
            if rx == 0 or ry == 0:
                row.append(False)
                continue
            nx = (x - cx) / rx
            ny = (y - cy) / ry
            row.append((nx * nx + ny * ny) <= 1.0)
        mask.append(row)
    return mask


def _spiral_arms_mask(
    size: int,
    cx: int,
    cy: int,
    radius_min: float,
    radius_max: float,
    arms: int,
    phase: float,
    k: float,
    thickness: float,
) -> list[list[bool]]:
    # Creates a multi-arm spiral by selecting pixels near any arm curve.
    mask: list[list[bool]] = []
    two_pi = math.tau
    for y in range(size):
        row: list[bool] = []
        for x in range(size):
            dx = x - cx
            dy = y - cy
            r = math.hypot(dx, dy)
            if r < radius_min or r > radius_max:
                row.append(False)
                continue

            a = math.atan2(dy, dx)
            # Reduce angle into [0, 2pi)
            if a < 0:
                a += two_pi

            # Arm curves: a ≈ (k * r + phase + arm_offset)
            on = False
            for i in range(arms):
                target = (k * r + phase + (two_pi * i / arms)) % two_pi
                # Smallest angular distance.
                d = abs(((a - target + math.pi) % two_pi) - math.pi)
                if d <= thickness:
                    on = True
                    break
            row.append(on)
        mask.append(row)
    return mask


def _is_outline(mask: list[list[bool]], x: int, y: int) -> bool:
    if not mask[y][x]:
        return False
    h = len(mask)
    w = len(mask[0])
    for nx, ny in ((x - 1, y), (x + 1, y), (x, y - 1), (x, y + 1)):
        if nx < 0 or ny < 0 or nx >= w or ny >= h:
            return True
        if not mask[ny][nx]:
            return True
    return False


@dataclass(frozen=True)
class GeneratedBlockAssets:
    block_id: str
    face_grids: dict[FaceName, list[list[str]]]
    item_grid: list[list[str]]

    # If true, emit a horizontal-facing blockstate with rotated variants.
    horizontal_facing: bool = False

    # If true, emit a boolean `powered` property in the blockstate variants.
    powered: bool = False

    # Optional: additional enum-like blockstate properties to include in variants.
    # Example: {"lift_state": ["idle", "rising", "falling"]}
    enum_variants: dict[str, list[str]] | None = None

    # Optional: animated face frames and runtime metadata.
    animated_textures: dict[str, list[list[list[str]]]] | None = None
    mcmeta_by_texture: dict[str, object] | None = None


def generate_pressure_conduit() -> GeneratedBlockAssets:
    size = 32
    ceramic_light = PALETTE["ceramic"]["light"]
    ceramic_mid = PALETTE["ceramic"]["mid"]
    ceramic_dark = PALETTE["ceramic"]["dark"]
    ceramic_deep = PALETTE["ceramic"]["deep"]
    glow = PALETTE["crystal"]["bright"]

    # Top/bottom: a centered ceramic tube with a vertical glow seam.
    cx = cy = 16
    radius = 5  # ~10px diameter (blueprint calls out 10px)
    mask = _circle_mask(size, cx, cy, radius)

    top = _make_grid(size, ceramic_light)
    for y in range(size):
        for x in range(size):
            if not mask[y][x]:
                continue

            if _is_outline(mask, x, y):
                top[y][x] = ceramic_deep
                continue

            # Interior fill.
            color = ceramic_mid

            # Highlight/shadow bands.
            if x == cx - radius + 1:
                color = ceramic_light
            if x == cx + radius - 1:
                color = ceramic_dark

            # Glow seam.
            if x == cx:
                color = glow

            top[y][x] = color

    bottom = [row[:] for row in top]

    # Side faces: horizontal band with a horizontal glow seam.
    band_top = 11
    band_bottom = 21
    seam_y = 16

    side = _make_grid(size, ceramic_light)
    for y in range(band_top, band_bottom + 1):
        for x in range(size):
            if y == band_top or y == band_bottom:
                side[y][x] = ceramic_deep
                continue

            color = ceramic_mid

            if y == band_top + 1:
                color = ceramic_light
            if y == band_bottom - 1:
                color = ceramic_dark

            if y == seam_y:
                color = glow

            side[y][x] = color

    face_grids: dict[FaceName, list[list[str]]] = {
        "top": top,
        "bottom": bottom,
        "north": side,
        "south": [row[:] for row in side],
        "east": [row[:] for row in side],
        "west": [row[:] for row in side],
    }

    # Item icon: use the "front" (side) face.
    item_grid = [row[:] for row in side]

    return GeneratedBlockAssets(
        block_id="pressure_conduit",
        face_grids=face_grids,
        item_grid=item_grid,
    )


def _apply_ceramic_frame(base: list[list[str]], x0: int, y0: int, x1: int, y1: int) -> None:
    ceramic_light = PALETTE["ceramic"]["light"]
    ceramic_mid = PALETTE["ceramic"]["mid"]
    ceramic_dark = PALETTE["ceramic"]["dark"]
    ceramic_deep = PALETTE["ceramic"]["deep"]

    # Outer border
    size = len(base)
    for i in range(size):
        base[0][i] = ceramic_deep
        base[size - 1][i] = ceramic_deep
        base[i][0] = ceramic_deep
        base[i][size - 1] = ceramic_deep

    # Frame rect around the membrane window.
    for y in range(y0, y1 + 1):
        for x in range(x0, x1 + 1):
            is_edge = x == x0 or x == x1 or y == y0 or y == y1
            if is_edge:
                base[y][x] = ceramic_dark
            else:
                base[y][x] = ceramic_mid

    # Subtle corner highlights.
    base[y0][x0] = ceramic_light
    base[y0][x1] = ceramic_light


def _draw_crystal_nodes(grid: list[list[str]]) -> None:
    bright = PALETTE["crystal"]["bright"]
    mid = PALETTE["crystal"]["mid"]
    for (x, y) in ((3, 3), (27, 3), (3, 27), (27, 27)):
        # 2x2 nodes.
        grid[y][x] = bright
        grid[y][x + 1] = mid
        grid[y + 1][x] = mid
        grid[y + 1][x + 1] = bright


def _draw_membrane_oval(
    grid: list[list[str]],
    bbox: tuple[int, int, int, int],
    highlight_band_width: int,
    shadow_band_width: int,
) -> None:
    x0, y0, x1, y1 = bbox
    mask = _ellipse_mask(len(grid), x0, y0, x1, y1)
    line = PALETTE["membrane"]["line"]
    highlight = PALETTE["membrane"]["highlight"]
    mid = PALETTE["membrane"]["mid"]
    shadow = PALETTE["membrane"]["shadow"]

    for y in range(len(grid)):
        for x in range(len(grid[y])):
            if not mask[y][x]:
                continue
            if _is_outline(mask, x, y):
                grid[y][x] = line
                continue

            # Interior shading bands are defined in screen-space (vertical bands).
            if x <= x0 + highlight_band_width - 1:
                grid[y][x] = highlight
            elif x >= x1 - shadow_band_width + 1:
                grid[y][x] = shadow
            else:
                grid[y][x] = mid


def generate_membrane_pump() -> GeneratedBlockAssets:
    size = 32
    ceramic_light = PALETTE["ceramic"]["light"]
    ceramic_mid = PALETTE["ceramic"]["mid"]
    ceramic_dark = PALETTE["ceramic"]["dark"]
    ceramic_deep = PALETTE["ceramic"]["deep"]
    glow = PALETTE["crystal"]["bright"]

    # Front face: ceramic body, membrane window, crystal corner nodes.
    def make_front_frame(expand_lr: int) -> list[list[str]]:
        front = _make_grid(size, ceramic_mid)
        # Ceramic frame around the window: use a fixed frame rect so animation only affects membrane.
        frame_rect = (6, 4, 25, 27)
        _apply_ceramic_frame(front, *frame_rect)
        _draw_crystal_nodes(front)

        # Membrane oval (animates left/right only).
        base_bbox = (9 - expand_lr, 7, 22 + expand_lr, 24)
        band = 1 + expand_lr
        _draw_membrane_oval(front, base_bbox, highlight_band_width=band, shadow_band_width=band)
        return front

    front_frames = [make_front_frame(0), make_front_frame(1), make_front_frame(2)]

    # Side faces: ceramic ribs + conduit port.
    side = _make_grid(size, ceramic_mid)
    # Outer border
    for i in range(size):
        side[0][i] = ceramic_deep
        side[size - 1][i] = ceramic_deep
        side[i][0] = ceramic_deep
        side[i][size - 1] = ceramic_deep

    ribs = [(4, 6), (11, 13), (18, 20)]
    for x0, x1 in ribs:
        for y in range(2, size - 2):
            for x in range(x0, x1 + 1):
                if x == x0:
                    side[y][x] = ceramic_light
                elif x == x1:
                    side[y][x] = ceramic_dark
                else:
                    side[y][x] = ceramic_mid

    # Conduit port 6x6 at x=13..18, y=13..18.
    for y in range(13, 19):
        for x in range(13, 19):
            is_edge = x in (13, 18) or y in (13, 18)
            side[y][x] = ceramic_deep if is_edge else ceramic_mid
    for y in range(14, 18):
        side[y][16] = glow

    # Top face: cap ring + horizontal seam.
    top = _make_grid(size, ceramic_mid)
    for i in range(size):
        top[0][i] = ceramic_deep
        top[size - 1][i] = ceramic_deep
        top[i][0] = ceramic_deep
        top[i][size - 1] = ceramic_deep

    cap_mask_outer = _circle_mask(size, 16, 16, 6)
    cap_mask_inner = _circle_mask(size, 16, 16, 4)
    for y in range(size):
        for x in range(size):
            if cap_mask_outer[y][x] and not cap_mask_inner[y][x]:
                top[y][x] = ceramic_dark
            elif cap_mask_inner[y][x]:
                top[y][x] = ceramic_light
    for x in range(12, 21):
        top[16][x] = glow

    # Bottom face: flat ceramic with shadow bias to the bottom.
    bottom = _make_grid(size, ceramic_mid)
    for i in range(size):
        bottom[0][i] = ceramic_deep
        bottom[size - 1][i] = ceramic_deep
        bottom[i][0] = ceramic_deep
        bottom[i][size - 1] = ceramic_deep
    for y in range(1, size - 1):
        for x in range(1, size - 1):
            if y >= 22:
                bottom[y][x] = ceramic_dark
            elif y <= 9:
                bottom[y][x] = ceramic_light
            else:
                bottom[y][x] = ceramic_mid

    face_grids: dict[FaceName, list[list[str]]] = {
        "top": top,
        "bottom": bottom,
        # Facing model will map north=front.
        "north": front_frames[0],
        "south": side,
        "east": side,
        "west": side,
    }

    # Item icon uses the relaxed front frame.
    item_grid = [row[:] for row in front_frames[0]]

    mcmeta = {
        "animation": {
            "frametime": 4,
            "frames": [0, 1, 2],
        }
    }

    return GeneratedBlockAssets(
        block_id="membrane_pump",
        face_grids=face_grids,
        item_grid=item_grid,
        horizontal_facing=True,
        animated_textures={"front": front_frames},
        mcmeta_by_texture={"front": mcmeta},
    )


def generate_pressure_turbine() -> GeneratedBlockAssets:
    size = 32
    ceramic_light = PALETTE["ceramic"]["light"]
    ceramic_mid = PALETTE["ceramic"]["mid"]
    ceramic_dark = PALETTE["ceramic"]["dark"]
    ceramic_deep = PALETTE["ceramic"]["deep"]
    shell_hi = PALETTE["shell"]["highlight"]
    shell_mid = PALETTE["shell"]["mid"]
    shell_shadow = PALETTE["shell"]["shadow"]
    shell_line = PALETTE["shell"]["line"]
    glow = PALETTE["crystal"]["bright"]
    glow_mid = PALETTE["crystal"]["mid"]

    # Common housing base: ceramic with an inner ring.
    def make_base_face() -> list[list[str]]:
        g = _make_grid(size, ceramic_mid)
        for i in range(size):
            g[0][i] = ceramic_deep
            g[size - 1][i] = ceramic_deep
            g[i][0] = ceramic_deep
            g[i][size - 1] = ceramic_deep
        return g

    # Front face: spiral plate assembly (animated 6-frame rotation).
    def make_front_frame(frame_index: int) -> list[list[str]]:
        g = make_base_face()

        # Ceramic housing ring around the spiral.
        outer = _circle_mask(size, 16, 16, 11)
        inner = _circle_mask(size, 16, 16, 9)
        for y in range(size):
            for x in range(size):
                if outer[y][x] and not inner[y][x]:
                    g[y][x] = ceramic_dark

        # Glow vents embedded in the ring (4 nodes).
        for (x, y) in ((16, 5), (27, 16), (16, 27), (5, 16)):
            g[y][x] = glow
            if 0 <= x + 1 < size:
                g[y][x + 1] = glow_mid

        # Spiral plates inside 20x20 bounding box (x/y 6..26), with 3 arms.
        phase = (math.tau * frame_index) / 6.0
        spiral = _spiral_arms_mask(
            size=size,
            cx=16,
            cy=16,
            radius_min=1.5,
            radius_max=10.0,
            arms=3,
            phase=phase,
            k=0.55,
            thickness=0.20,
        )

        # Core cluster 3x3 at center.
        for y in range(15, 18):
            for x in range(15, 18):
                g[y][x] = shell_mid

        for y in range(size):
            for x in range(size):
                if not spiral[y][x]:
                    continue
                # Plate shading: highlight on leading edge (approx by quadrant).
                dx = x - 16
                dy = y - 16
                if dx - dy > 0:
                    g[y][x] = shell_hi
                else:
                    g[y][x] = shell_shadow

        # Outline the spiral region lightly.
        for y in range(6, 27):
            for x in range(6, 27):
                if spiral[y][x] and any(
                    0 <= nx < size and 0 <= ny < size and not spiral[ny][nx]
                    for nx, ny in ((x - 1, y), (x + 1, y), (x, y - 1), (x, y + 1))
                ):
                    g[y][x] = shell_line

        return g

    front_frames = [make_front_frame(i) for i in range(6)]

    # Side faces: simple vented ceramic panel.
    side = make_base_face()
    for y in range(6, 27):
        for x in range(8, 25):
            side[y][x] = ceramic_mid
    # Vents (glow slits)
    for y in (12, 16, 20):
        for x in range(10, 23):
            side[y][x] = glow_mid
        for x in (10, 22):
            side[y][x] = glow

    # Top: ring + axle cap.
    top = make_base_face()
    ring_outer = _circle_mask(size, 16, 16, 10)
    ring_inner = _circle_mask(size, 16, 16, 7)
    for y in range(size):
        for x in range(size):
            if ring_outer[y][x] and not ring_inner[y][x]:
                top[y][x] = ceramic_dark
            elif ring_inner[y][x]:
                top[y][x] = ceramic_light
    for y in range(14, 19):
        for x in range(14, 19):
            top[y][x] = shell_mid
    top[16][16] = shell_line

    bottom = make_base_face()

    face_grids: dict[FaceName, list[list[str]]] = {
        "top": top,
        "bottom": bottom,
        "north": front_frames[0],
        "south": side,
        "east": side,
        "west": side,
    }

    item_grid = [row[:] for row in front_frames[0]]

    mcmeta = {
        "animation": {
            "frametime": 2,
            "frames": [0, 1, 2, 3, 4, 5],
        }
    }

    return GeneratedBlockAssets(
        block_id="pressure_turbine",
        face_grids=face_grids,
        item_grid=item_grid,
        horizontal_facing=True,
        animated_textures={"front": front_frames},
        mcmeta_by_texture={"front": mcmeta},
    )


def _gear_mask(
    size: int,
    cx: int,
    cy: int,
    radius: float,
    tooth_count: int,
    tooth_depth: float,
    phase: float,
) -> list[list[bool]]:
    mask: list[list[bool]] = []
    two_pi = math.tau
    for y in range(size):
        row: list[bool] = []
        for x in range(size):
            dx = x - cx
            dy = y - cy
            r = math.hypot(dx, dy)
            if r == 0:
                row.append(True)
                continue
            a = math.atan2(dy, dx)
            if a < 0:
                a += two_pi

            # Tooth modulation around the circle.
            t = (a + phase) * tooth_count / two_pi
            frac = t - math.floor(t)
            # Triangular wave in [0,1].
            tri = 1.0 - abs(2.0 * frac - 1.0)
            r_max = radius + tooth_depth * tri
            row.append(r <= r_max)
        mask.append(row)
    return mask


def generate_spiral_gearbox() -> GeneratedBlockAssets:
    size = 32
    ceramic_light = PALETTE["ceramic"]["light"]
    ceramic_mid = PALETTE["ceramic"]["mid"]
    ceramic_dark = PALETTE["ceramic"]["dark"]
    ceramic_deep = PALETTE["ceramic"]["deep"]
    shell_hi = PALETTE["shell"]["highlight"]
    shell_mid = PALETTE["shell"]["mid"]
    shell_shadow = PALETTE["shell"]["shadow"]
    shell_line = PALETTE["shell"]["line"]
    glow = PALETTE["crystal"]["bright"]

    def make_base() -> list[list[str]]:
        g = _make_grid(size, ceramic_mid)
        for i in range(size):
            g[0][i] = ceramic_deep
            g[size - 1][i] = ceramic_deep
            g[i][0] = ceramic_deep
            g[i][size - 1] = ceramic_deep
        # Subtle ceramic panel border.
        for y in range(3, size - 3):
            g[y][3] = ceramic_dark
            g[y][size - 4] = ceramic_dark
        for x in range(3, size - 3):
            g[3][x] = ceramic_dark
            g[size - 4][x] = ceramic_dark
        return g

    def make_front_frame(frame_index: int) -> list[list[str]]:
        g = make_base()

        # Two interlocking gears.
        phase = (math.tau * frame_index) / 4.0
        gear_left = _gear_mask(size, 12, 16, radius=6.0, tooth_count=10, tooth_depth=1.6, phase=phase)
        gear_right = _gear_mask(size, 20, 16, radius=6.0, tooth_count=10, tooth_depth=1.6, phase=-phase)

        for y in range(size):
            for x in range(size):
                on_left = gear_left[y][x]
                on_right = gear_right[y][x]
                if not (on_left or on_right):
                    continue

                # Basic shading: highlight on upper-left, shadow on lower-right.
                dx = x - 16
                dy = y - 16
                if dx + dy < 0:
                    g[y][x] = shell_hi
                elif dx + dy > 0:
                    g[y][x] = shell_shadow
                else:
                    g[y][x] = shell_mid

        # Gear outlines.
        for y in range(1, size - 1):
            for x in range(1, size - 1):
                if (gear_left[y][x] or gear_right[y][x]) and any(
                    not (gear_left[ny][nx] or gear_right[ny][nx])
                    for nx, ny in ((x - 1, y), (x + 1, y), (x, y - 1), (x, y + 1))
                ):
                    g[y][x] = shell_line

        # Central crystal seam.
        for y in range(8, 25):
            g[y][16] = glow
        return g

    front_frames = [make_front_frame(i) for i in range(4)]

    # Side/top/bottom: ceramic casing.
    side = make_base()
    for y in range(10, 23):
        side[y][10] = ceramic_light
        side[y][21] = ceramic_dark

    top = make_base()
    ring_outer = _circle_mask(size, 16, 16, 9)
    ring_inner = _circle_mask(size, 16, 16, 6)
    for y in range(size):
        for x in range(size):
            if ring_outer[y][x] and not ring_inner[y][x]:
                top[y][x] = ceramic_dark
            elif ring_inner[y][x]:
                top[y][x] = ceramic_light
    for x in range(12, 21):
        top[16][x] = glow

    bottom = make_base()

    face_grids: dict[FaceName, list[list[str]]] = {
        "top": top,
        "bottom": bottom,
        "north": front_frames[0],
        "south": side,
        "east": side,
        "west": side,
    }

    item_grid = [row[:] for row in front_frames[0]]

    mcmeta = {
        "animation": {
            "frametime": 3,
            "frames": [0, 1, 2, 3],
        }
    }

    return GeneratedBlockAssets(
        block_id="spiral_gearbox",
        face_grids=face_grids,
        item_grid=item_grid,
        horizontal_facing=True,
        animated_textures={"front": front_frames},
        mcmeta_by_texture={"front": mcmeta},
    )


def generate_vent_piston() -> GeneratedBlockAssets:
    size = 32
    ceramic_light = PALETTE["ceramic"]["light"]
    ceramic_mid = PALETTE["ceramic"]["mid"]
    ceramic_dark = PALETTE["ceramic"]["dark"]
    ceramic_deep = PALETTE["ceramic"]["deep"]
    ceramic_line = PALETTE["ceramic"]["line"]
    membrane_hi = PALETTE["membrane"]["highlight"]
    membrane_mid = PALETTE["membrane"]["mid"]
    membrane_line = PALETTE["membrane"]["line"]
    air_hi = PALETTE["air"]["mid"]
    air_shadow = PALETTE["air"]["shadow"]
    glow = PALETTE["crystal"]["bright"]

    def make_base() -> list[list[str]]:
        g = _make_grid(size, ceramic_mid)
        for i in range(size):
            g[0][i] = ceramic_deep
            g[size - 1][i] = ceramic_deep
            g[i][0] = ceramic_deep
            g[i][size - 1] = ceramic_deep
        # Inner panel frame.
        for x in range(2, size - 2):
            g[2][x] = ceramic_dark
            g[size - 3][x] = ceramic_dark
        for y in range(2, size - 2):
            g[y][2] = ceramic_dark
            g[y][size - 3] = ceramic_dark
        # Corner rivets.
        for (x, y) in ((4, 4), (27, 4), (4, 27), (27, 27)):
            g[y][x] = ceramic_light
        return g

    def make_front_frame(frame_index: int) -> list[list[str]]:
        g = make_base()

        # Central piston face plate.
        for y in range(8, 24):
            for x in range(9, 23):
                g[y][x] = ceramic_dark
        for y in range(10, 22):
            for x in range(11, 21):
                g[y][x] = ceramic_mid

        # Membrane gasket ring.
        ring_outer = _ellipse_mask(size, 10, 9, 21, 22)
        ring_inner = _ellipse_mask(size, 12, 11, 19, 20)
        for y in range(size):
            for x in range(size):
                if ring_outer[y][x] and not ring_inner[y][x]:
                    g[y][x] = membrane_mid
        for y in range(1, size - 1):
            for x in range(1, size - 1):
                if (ring_outer[y][x] and not ring_inner[y][x]) and any(
                    not (ring_outer[ny][nx] and not ring_inner[ny][nx])
                    for nx, ny in ((x - 1, y), (x + 1, y), (x, y - 1), (x, y + 1))
                ):
                    g[y][x] = membrane_line

        # Vent shutters (animated).
        # Frame 0 -> closed, Frame 3 -> most open.
        open_h = 2 + frame_index  # 2..5
        slit_half_len = 8
        for cy in (12, 16, 20):
            for dy in range(-(open_h // 2), (open_h + 1) // 2):
                y = cy + dy
                for x in range(16 - slit_half_len, 16 + slit_half_len + 1):
                    g[y][x] = air_hi
            # slit outline
            y_top = cy - (open_h // 2) - 1
            y_bottom = cy + ((open_h + 1) // 2)
            for x in range(16 - slit_half_len, 16 + slit_half_len + 1):
                if 0 <= y_top < size:
                    g[y_top][x] = ceramic_line
                if 0 <= y_bottom < size:
                    g[y_bottom][x] = ceramic_line
            for y in range(y_top + 1, y_bottom):
                if 0 <= y < size:
                    g[y][16 - slit_half_len] = ceramic_line
                    g[y][16 + slit_half_len] = ceramic_line

        # Add a small pulsing crystal indicator.
        blink = (frame_index % 2) == 0
        g[6][16] = glow if blink else air_shadow
        g[25][16] = glow if not blink else air_shadow

        # Highlights for depth.
        for x in range(11, 21):
            g[10][x] = ceramic_light
            g[21][x] = ceramic_deep
        for y in range(10, 22):
            g[y][11] = ceramic_light
            g[y][20] = ceramic_deep

        # Membrane highlight.
        for x in range(14, 18):
            g[11][x] = membrane_hi

        return g

    front_frames = [make_front_frame(i) for i in range(4)]

    side = make_base()
    # Side piston barrel silhouette.
    for y in range(9, 23):
        for x in range(12, 20):
            side[y][x] = ceramic_dark
    for y in range(11, 21):
        for x in range(14, 18):
            side[y][x] = ceramic_mid
    # Vent ports on side.
    for y in (13, 19):
        for x in range(8, 11):
            side[y][x] = air_shadow
            side[y + 1][x] = air_hi

    top = make_base()
    # Top vent grille.
    for x in range(8, 25):
        top[12][x] = ceramic_line
        top[20][x] = ceramic_line
    for y in range(13, 20):
        for x in range(9, 24):
            if (x + y) % 3 == 0:
                top[y][x] = air_shadow
    top[16][16] = glow

    bottom = make_base()

    face_grids: dict[FaceName, list[list[str]]] = {
        "top": top,
        "bottom": bottom,
        "north": front_frames[0],
        "south": side,
        "east": side,
        "west": side,
    }

    item_grid = [row[:] for row in front_frames[0]]

    mcmeta = {
        "animation": {
            "frametime": 3,
            "frames": [0, 1, 2, 3],
        }
    }

    return GeneratedBlockAssets(
        block_id="vent_piston",
        face_grids=face_grids,
        item_grid=item_grid,
        horizontal_facing=True,
        animated_textures={"front": front_frames},
        mcmeta_by_texture={"front": mcmeta},
    )


def generate_atmospheric_compressor() -> GeneratedBlockAssets:
    size = 32
    ceramic_light = PALETTE["ceramic"]["light"]
    ceramic_mid = PALETTE["ceramic"]["mid"]
    ceramic_dark = PALETTE["ceramic"]["dark"]
    ceramic_deep = PALETTE["ceramic"]["deep"]
    ceramic_line = PALETTE["ceramic"]["line"]

    shell_hi = PALETTE["shell"]["highlight"]
    shell_mid = PALETTE["shell"]["mid"]
    shell_shadow = PALETTE["shell"]["shadow"]
    shell_line = PALETTE["shell"]["line"]

    air_bright = PALETTE["air"]["bright"]
    air_mid = PALETTE["air"]["mid"]
    air_shadow = PALETTE["air"]["shadow"]
    air_deep = PALETTE["air"]["deep"]
    air_line = PALETTE["air"]["line"]

    crystal_bright = PALETTE["crystal"]["bright"]
    crystal_mid = PALETTE["crystal"]["mid"]
    crystal_shadow = PALETTE["crystal"]["shadow"]
    crystal_deep = PALETTE["crystal"]["deep"]
    crystal_line = PALETTE["crystal"]["line"]

    def make_casing_base() -> list[list[str]]:
        g = _make_grid(size, ceramic_mid)
        for i in range(size):
            g[0][i] = ceramic_deep
            g[size - 1][i] = ceramic_deep
            g[i][0] = ceramic_deep
            g[i][size - 1] = ceramic_deep
        # Inner frame
        for x in range(2, size - 2):
            g[2][x] = ceramic_dark
            g[size - 3][x] = ceramic_dark
        for y in range(2, size - 2):
            g[y][2] = ceramic_dark
            g[y][size - 3] = ceramic_dark
        # Corners
        for (x, y) in ((4, 4), (27, 4), (4, 27), (27, 27)):
            g[y][x] = ceramic_light
        return g

    def make_top() -> list[list[str]]:
        g = make_casing_base()
        # Crystal intake cap
        outer = _circle_mask(size, 16, 16, 10)
        inner = _circle_mask(size, 16, 16, 7)
        core = _circle_mask(size, 16, 16, 4)
        for y in range(size):
            for x in range(size):
                if outer[y][x] and not inner[y][x]:
                    g[y][x] = crystal_shadow
                elif inner[y][x] and not core[y][x]:
                    g[y][x] = crystal_mid
                elif core[y][x]:
                    g[y][x] = crystal_bright
        g[16][16] = air_bright
        return g

    def make_bottom() -> list[list[str]]:
        g = make_casing_base()
        # Ceramic baseplate with vents
        for y in range(10, 23):
            g[y][8] = ceramic_line
            g[y][23] = ceramic_line
        for x in range(9, 23):
            g[10][x] = ceramic_line
            g[22][x] = ceramic_line
        for y in range(12, 21):
            for x in range(10, 22):
                if (x + y) % 4 == 0:
                    g[y][x] = air_shadow
        return g

    def make_side() -> list[list[str]]:
        g = make_casing_base()
        # Side conduits / ribs
        for y in range(6, 26):
            g[y][7] = ceramic_dark
            g[y][24] = ceramic_dark
        for x in range(10, 22):
            g[8][x] = ceramic_dark
            g[25][x] = ceramic_deep
        # Small indicator
        g[16][4] = crystal_bright
        g[16][5] = crystal_mid
        return g

    def make_front_frame(frame_index: int) -> list[list[str]]:
        g = make_casing_base()

        # Crystal window frame (shell-like bezel)
        outer = _ellipse_mask(size, 6, 6, 25, 25)
        inner = _ellipse_mask(size, 9, 9, 22, 22)
        core = _ellipse_mask(size, 12, 12, 19, 19)
        for y in range(size):
            for x in range(size):
                if outer[y][x] and not inner[y][x]:
                    # Bezel shading
                    if (x - 16) + (y - 16) < 0:
                        g[y][x] = shell_hi
                    elif (x - 16) + (y - 16) > 0:
                        g[y][x] = shell_shadow
                    else:
                        g[y][x] = shell_mid
                elif inner[y][x] and not core[y][x]:
                    g[y][x] = crystal_mid
                elif core[y][x]:
                    g[y][x] = crystal_bright

        # Swirling air: 3-arm spiral with phase shift + pulsing glow
        phase = (math.tau * frame_index) / 6.0
        arms = _spiral_arms_mask(
            size,
            16,
            16,
            radius_min=2.0,
            radius_max=8.5,
            arms=3,
            phase=phase,
            k=0.75,
            thickness=0.22,
        )

        pulse = 0.5 + 0.5 * math.sin(phase)
        for y in range(size):
            for x in range(size):
                if not core[y][x]:
                    continue
                if arms[y][x]:
                    g[y][x] = air_bright if pulse > 0.5 else air_mid
                else:
                    # Background air gradient
                    dy = abs(y - 16)
                    dx = abs(x - 16)
                    d = dx + dy
                    if d < 6:
                        g[y][x] = air_mid
                    elif d < 10:
                        g[y][x] = air_shadow
                    else:
                        g[y][x] = air_deep

        # Outline the bezel and window
        for y in range(1, size - 1):
            for x in range(1, size - 1):
                if (outer[y][x] and not inner[y][x]) and any(
                    not (outer[ny][nx] and not inner[ny][nx])
                    for nx, ny in ((x - 1, y), (x + 1, y), (x, y - 1), (x, y + 1))
                ):
                    g[y][x] = shell_line
                if (inner[y][x] and not core[y][x]) and any(
                    not (inner[ny][nx] and not core[ny][nx])
                    for nx, ny in ((x - 1, y), (x + 1, y), (x, y - 1), (x, y + 1))
                ):
                    g[y][x] = crystal_line

        # Small air-line glyphs
        for x in range(12, 21):
            g[28][x] = air_line
        g[28][16] = crystal_bright

        return g

    front_frames = [make_front_frame(i) for i in range(6)]

    top = make_top()
    bottom = make_bottom()
    side = make_side()

    face_grids: dict[FaceName, list[list[str]]] = {
        "top": top,
        "bottom": bottom,
        "north": front_frames[0],
        "south": side,
        "east": side,
        "west": side,
    }

    item_grid = [row[:] for row in front_frames[0]]

    mcmeta = {
        "animation": {
            "frametime": 3,
            "frames": [0, 1, 2, 3, 4, 5],
        }
    }

    return GeneratedBlockAssets(
        block_id="atmospheric_compressor",
        face_grids=face_grids,
        item_grid=item_grid,
        horizontal_facing=True,
        animated_textures={"front": front_frames},
        mcmeta_by_texture={"front": mcmeta},
    )


def generate_pressure_valve() -> GeneratedBlockAssets:
    size = 32
    ceramic_light = PALETTE["ceramic"]["light"]
    ceramic_mid = PALETTE["ceramic"]["mid"]
    ceramic_dark = PALETTE["ceramic"]["dark"]
    ceramic_deep = PALETTE["ceramic"]["deep"]
    ceramic_line = PALETTE["ceramic"]["line"]

    membrane_hi = PALETTE["membrane"]["highlight"]
    membrane_mid = PALETTE["membrane"]["mid"]
    membrane_shadow = PALETTE["membrane"]["shadow"]
    membrane_line = PALETTE["membrane"]["line"]

    crystal_bright = PALETTE["crystal"]["bright"]
    crystal_mid = PALETTE["crystal"]["mid"]
    crystal_shadow = PALETTE["crystal"]["shadow"]
    crystal_line = PALETTE["crystal"]["line"]

    air_mid = PALETTE["air"]["mid"]
    air_shadow = PALETTE["air"]["shadow"]
    air_line = PALETTE["air"]["line"]

    def make_base() -> list[list[str]]:
        g = _make_grid(size, ceramic_mid)
        for i in range(size):
            g[0][i] = ceramic_deep
            g[size - 1][i] = ceramic_deep
            g[i][0] = ceramic_deep
            g[i][size - 1] = ceramic_deep
        # Inner frame
        for x in range(2, size - 2):
            g[2][x] = ceramic_dark
            g[size - 3][x] = ceramic_dark
        for y in range(2, size - 2):
            g[y][2] = ceramic_dark
            g[y][size - 3] = ceramic_dark
        # Corner studs
        for (x, y) in ((4, 4), (27, 4), (4, 27), (27, 27)):
            g[y][x] = ceramic_light
        return g

    def make_front() -> list[list[str]]:
        g = make_base()

        # Central valve body.
        outer = _circle_mask(size, 16, 16, 10)
        mid = _circle_mask(size, 16, 16, 8)
        inner = _circle_mask(size, 16, 16, 5)
        core = _circle_mask(size, 16, 16, 2)

        for y in range(size):
            for x in range(size):
                if outer[y][x] and not mid[y][x]:
                    g[y][x] = ceramic_dark
                elif mid[y][x] and not inner[y][x]:
                    # membrane ring
                    if (x - 16) + (y - 16) < 0:
                        g[y][x] = membrane_hi
                    elif (x - 16) + (y - 16) > 0:
                        g[y][x] = membrane_shadow
                    else:
                        g[y][x] = membrane_mid
                elif inner[y][x] and not core[y][x]:
                    g[y][x] = ceramic_mid
                elif core[y][x]:
                    g[y][x] = crystal_bright

        # Valve slot + arrow glyph (indicates flow direction).
        for x in range(10, 23):
            g[16][x] = air_shadow
        for x in range(12, 21):
            g[15][x] = air_mid
            g[17][x] = air_mid
        g[16][22] = air_mid
        g[15][21] = air_mid
        g[17][21] = air_mid
        for x in range(10, 23):
            g[14][x] = air_line
            g[18][x] = air_line

        # Crystal indicator lens on top.
        lens_outer = _ellipse_mask(size, 13, 5, 18, 10)
        lens_inner = _ellipse_mask(size, 14, 6, 17, 9)
        for y in range(size):
            for x in range(size):
                if lens_outer[y][x] and not lens_inner[y][x]:
                    g[y][x] = crystal_shadow
                elif lens_inner[y][x]:
                    g[y][x] = crystal_mid
        g[7][16] = crystal_bright
        g[6][16] = crystal_line

        # Outlines
        for y in range(1, size - 1):
            for x in range(1, size - 1):
                if (outer[y][x] and not mid[y][x]) and any(
                    not (outer[ny][nx] and not mid[ny][nx])
                    for nx, ny in ((x - 1, y), (x + 1, y), (x, y - 1), (x, y + 1))
                ):
                    g[y][x] = ceramic_line
                if (mid[y][x] and not inner[y][x]) and any(
                    not (mid[ny][nx] and not inner[ny][nx])
                    for nx, ny in ((x - 1, y), (x + 1, y), (x, y - 1), (x, y + 1))
                ):
                    g[y][x] = membrane_line
                if (lens_outer[y][x] and not lens_inner[y][x]) and any(
                    not (lens_outer[ny][nx] and not lens_inner[ny][nx])
                    for nx, ny in ((x - 1, y), (x + 1, y), (x, y - 1), (x, y + 1))
                ):
                    g[y][x] = crystal_line

        return g

    def make_side() -> list[list[str]]:
        g = make_base()
        # Side ribs
        for y in range(6, 26):
            g[y][8] = ceramic_dark
            g[y][23] = ceramic_dark
        # Small conduit ports
        for y in (12, 18):
            for x in range(4, 7):
                g[y][x] = air_shadow
                g[y + 1][x] = air_mid
        # Crystal latch
        g[16][27] = crystal_mid
        g[16][28] = crystal_bright
        return g

    top = make_base()
    # Top hatch ring
    ring_outer = _circle_mask(size, 16, 16, 9)
    ring_inner = _circle_mask(size, 16, 16, 6)
    for y in range(size):
        for x in range(size):
            if ring_outer[y][x] and not ring_inner[y][x]:
                top[y][x] = ceramic_dark
            elif ring_inner[y][x]:
                top[y][x] = ceramic_light
    top[16][16] = crystal_bright

    bottom = make_base()

    front = make_front()
    side = make_side()

    face_grids: dict[FaceName, list[list[str]]] = {
        "top": top,
        "bottom": bottom,
        "north": front,
        "south": side,
        "east": side,
        "west": side,
    }

    item_grid = [row[:] for row in front]

    return GeneratedBlockAssets(
        block_id="pressure_valve",
        face_grids=face_grids,
        item_grid=item_grid,
        horizontal_facing=True,
        powered=True,
    )


def generate_buoyancy_lift_platform() -> GeneratedBlockAssets:
    # Matches docs/PRESSURE_LOGIC.md "Buoyancy Lift Platform" blueprint.
    size = 32
    ceramic_light = PALETTE["ceramic"]["light"]
    ceramic_mid = PALETTE["ceramic"]["mid"]
    ceramic_dark = PALETTE["ceramic"]["dark"]
    ceramic_deep = PALETTE["ceramic"]["deep"]
    crystal_bright = PALETTE["crystal"]["bright"]
    crystal_mid = PALETTE["crystal"]["mid"]
    membrane_highlight = PALETTE["membrane"]["highlight"]
    membrane_mid = PALETTE["membrane"]["mid"]
    membrane_shadow = PALETTE["membrane"]["shadow"]

    # --- Top: ceramic disk with concentric vent rings and 2x2 crystal seam nodes.
    top = _make_grid(size, ceramic_light)
    disk = _circle_mask(size, 16, 16, 11)  # diameter 22
    for y in range(size):
        for x in range(size):
            if disk[y][x]:
                top[y][x] = ceramic_mid
    for y in range(size):
        for x in range(size):
            if _is_outline(disk, x, y):
                top[y][x] = ceramic_deep

    ring_outer_outer = _circle_mask(size, 16, 16, 9)  # diameter 18
    ring_outer_inner = _circle_mask(size, 16, 16, 8)
    ring_inner_outer = _circle_mask(size, 16, 16, 7)  # diameter 14
    ring_inner_inner = _circle_mask(size, 16, 16, 6)
    for y in range(size):
        for x in range(size):
            if ring_outer_outer[y][x] and not ring_outer_inner[y][x]:
                top[y][x] = ceramic_dark
            if ring_inner_outer[y][x] and not ring_inner_inner[y][x]:
                top[y][x] = ceramic_dark

    def place_node(x0: int, y0: int) -> None:
        for yy in range(y0, y0 + 2):
            for xx in range(x0, x0 + 2):
                if 0 <= xx < size and 0 <= yy < size:
                    top[yy][xx] = crystal_mid
        if 0 <= x0 < size and 0 <= y0 < size:
            top[y0][x0] = crystal_bright

    place_node(15, 3)
    place_node(27, 15)
    place_node(15, 27)
    place_node(3, 15)

    # --- Side: two vertical struts, three vent ports, and a 1px glow seam at x=16.
    side = _make_grid(size, ceramic_light)
    for y in range(size):
        for x in range(4, 8):
            side[y][x] = ceramic_mid
        for x in range(24, 28):
            side[y][x] = ceramic_mid
        side[y][16] = crystal_mid

    def place_vent_port(x0: int, y0: int) -> None:
        for yy in range(y0, y0 + 3):
            for xx in range(x0, x0 + 3):
                if 0 <= xx < size and 0 <= yy < size:
                    side[yy][xx] = ceramic_dark
        cx = x0 + 1
        cy = y0 + 1
        if 0 <= cx < size and 0 <= cy < size:
            side[cy][cx] = crystal_bright

    place_vent_port(12, 8)
    place_vent_port(12, 14)
    place_vent_port(12, 20)

    # --- Bottom: ceramic ring + membrane vent cluster with four lobes.
    bottom = _make_grid(size, ceramic_mid)
    for y in range(size):
        for x in range(size):
            if x < 2 or x >= size - 2 or y < 2 or y >= size - 2:
                bottom[y][x] = ceramic_deep

    cluster = _circle_mask(size, 16, 16, 6)  # diameter 12
    for y in range(size):
        for x in range(size):
            if cluster[y][x]:
                bottom[y][x] = membrane_mid
    for y in range(size):
        for x in range(size):
            if _is_outline(cluster, x, y):
                bottom[y][x] = membrane_shadow
    bottom[16][16] = membrane_highlight

    def place_lobe(x0: int, y0: int) -> None:
        for yy in range(y0, y0 + 4):
            for xx in range(x0, x0 + 4):
                if 0 <= xx < size and 0 <= yy < size:
                    bottom[yy][xx] = membrane_highlight

    place_lobe(14, 6)
    place_lobe(22, 14)
    place_lobe(14, 22)
    place_lobe(6, 14)

    face_grids: dict[FaceName, list[list[str]]] = {
        "top": top,
        "bottom": bottom,
        "north": side,
        "south": side,
        "east": side,
        "west": side,
    }

    item_grid = [row[:] for row in top]

    return GeneratedBlockAssets(
        block_id="buoyancy_lift_platform",
        face_grids=face_grids,
        item_grid=item_grid,
        enum_variants={"lift_state": ["idle", "rising", "falling"]},
    )


def emit_assets(repo_root: Path, assets: GeneratedBlockAssets) -> None:
    modid = "kruemblegard"

    # Source-of-truth JSON pixel arrays (per-face) for the hand-off spec.
    src_json_path = (
        repo_root
        / "src/main/resources/assets"
        / modid
        / "textures_src/cephalari_engineering"
        / f"{assets.block_id}.json"
    )
    src_payload = {
        "block": assets.block_id,
        "textures": assets.face_grids,
        "item_icon_32x32": assets.item_grid,
    }
    _write_json(src_json_path, src_payload)

    # PNG textures for Minecraft runtime.
    textures_block_dir = repo_root / "src/main/resources/assets" / modid / "textures/block"
    textures_item_dir = repo_root / "src/main/resources/assets" / modid / "textures/item"

    def _write_face_texture(texture_suffix: str, face_key: str, grid: list[list[str]]) -> None:
        png_path = textures_block_dir / f"{assets.block_id}_{texture_suffix}.png"
        if assets.animated_textures and face_key in assets.animated_textures:
            _write_png_from_frames_vertical(png_path, assets.animated_textures[face_key])
            if assets.mcmeta_by_texture and face_key in assets.mcmeta_by_texture:
                _write_json(
                    png_path.with_suffix(png_path.suffix + ".mcmeta"),
                    assets.mcmeta_by_texture[face_key],
                )
        else:
            _write_png_from_grid(png_path, grid)

    _write_face_texture("top", "top", assets.face_grids["top"])
    _write_face_texture("bottom", "bottom", assets.face_grids["bottom"])

    # Convention:
    # - For symmetric blocks, we emit a single _side.
    # - For asymmetric blocks, we may emit a _front (optionally animated) and a _side.
    _write_face_texture("front", "front", assets.face_grids["north"])

    # Always emit a _side texture from the south face if present, otherwise from north.
    side_source = assets.face_grids.get("south") or assets.face_grids["north"]
    _write_face_texture("side", "side", side_source)
    _write_png_from_grid(textures_item_dir / f"{assets.block_id}.png", assets.item_grid)

    # Blockstate + models.
    blockstates_dir = repo_root / "src/main/resources/assets" / modid / "blockstates"
    models_block_dir = repo_root / "src/main/resources/assets" / modid / "models/block"
    models_item_dir = repo_root / "src/main/resources/assets" / modid / "models/item"

    def _variant_key(props: dict[str, str]) -> str:
        if not props:
            return ""
        parts: list[str] = []
        if "facing" in props:
            parts.append(f"facing={props['facing']}")
        if "powered" in props:
            parts.append(f"powered={props['powered']}")
        for k in sorted(props.keys()):
            if k in ("facing", "powered"):
                continue
            parts.append(f"{k}={props[k]}")
        return ",".join(parts)

    combos: list[tuple[dict[str, str], int | None]] = [({}, None)]

    if assets.horizontal_facing:
        expanded: list[tuple[dict[str, str], int | None]] = []
        for props, _ in combos:
            for facing, y in (("north", None), ("east", 90), ("south", 180), ("west", 270)):
                p2 = dict(props)
                p2["facing"] = facing
                expanded.append((p2, y))
        combos = expanded

    if assets.powered:
        expanded = []
        for props, y in combos:
            for powered in ("false", "true"):
                p2 = dict(props)
                p2["powered"] = powered
                expanded.append((p2, y))
        combos = expanded

    if assets.enum_variants:
        for prop_name, values in sorted(assets.enum_variants.items()):
            expanded = []
            for props, y in combos:
                for v in values:
                    p2 = dict(props)
                    p2[prop_name] = v
                    expanded.append((p2, y))
            combos = expanded

    variants: dict[str, dict[str, object]] = {}
    for props, y in combos:
        key = _variant_key(props)
        entry: dict[str, object] = {"model": f"{modid}:block/{assets.block_id}"}
        if y is not None:
            entry["y"] = y
        variants[key] = entry

    _write_json(
        blockstates_dir / f"{assets.block_id}.json",
        {"variants": variants},
    )

    _write_json(
        models_block_dir / f"{assets.block_id}.json",
        {
            "parent": "minecraft:block/cube",
            "textures": {
                "top": f"{modid}:block/{assets.block_id}_top",
                "bottom": f"{modid}:block/{assets.block_id}_bottom",
                "north": f"{modid}:block/{assets.block_id}_front",
                "south": f"{modid}:block/{assets.block_id}_side",
                "east": f"{modid}:block/{assets.block_id}_side",
                "west": f"{modid}:block/{assets.block_id}_side",
            },
        },
    )

    _write_json(
        models_item_dir / f"{assets.block_id}.json",
        {"parent": f"{modid}:block/{assets.block_id}"},
    )

    # Loot table (drop self) so it behaves correctly in-game.
    loot_dir = repo_root / "src/main/resources/data" / modid / "loot_tables/blocks"
    _write_json(
        loot_dir / f"{assets.block_id}.json",
        {
            "type": "minecraft:block",
            "pools": [
                {
                    "rolls": 1,
                    "entries": [
                        {
                            "type": "minecraft:item",
                            "name": f"{modid}:{assets.block_id}",
                        }
                    ],
                    "conditions": [
                        {
                            "condition": "minecraft:survives_explosion"
                        }
                    ],
                }
            ],
        },
    )


def generate_conveyor_membrane() -> GeneratedBlockAssets:
    # Matches docs/PRESSURE_LOGIC.md "Conveyor Membrane" blueprint.
    size = 32
    ceramic_light = PALETTE["ceramic"]["light"]
    ceramic_mid = PALETTE["ceramic"]["mid"]
    ceramic_dark = PALETTE["ceramic"]["dark"]
    ceramic_deep = PALETTE["ceramic"]["deep"]
    glow = PALETTE["crystal"]["bright"]

    membrane_hi = PALETTE["membrane"]["highlight"]
    membrane_mid = PALETTE["membrane"]["mid"]
    membrane_shadow = PALETTE["membrane"]["shadow"]

    def make_side() -> list[list[str]]:
        g = _make_grid(size, ceramic_light)
        # Flat casing with a slightly darker bottom band.
        for y in range(size):
            for x in range(size):
                if y >= 22:
                    g[y][x] = ceramic_mid

        # Ribs.
        for y in range(2, size - 2):
            for x0, x1 in ((6, 8), (14, 16), (22, 24)):
                for x in range(x0, x1 + 1):
                    g[y][x] = ceramic_dark

        # Glow seam.
        for y in range(size):
            g[y][16] = glow

        # Border.
        for i in range(size):
            g[0][i] = ceramic_deep
            g[size - 1][i] = ceramic_deep
            g[i][0] = ceramic_deep
            g[i][size - 1] = ceramic_deep
        return g

    side = make_side()

    def make_top_frame(bulge: tuple[int, int, int, int] | None) -> list[list[str]]:
        g = _make_grid(size, ceramic_light)

        # Ceramic frames at left/right.
        for y in range(size):
            for x in range(0, 4):
                g[y][x] = ceramic_mid
            for x in range(28, 32):
                g[y][x] = ceramic_mid

        # Shading for ceramic frames.
        for y in range(size):
            g[y][0] = ceramic_deep
            g[y][31] = ceramic_deep
            g[y][3] = ceramic_dark
            g[y][28] = ceramic_dark

        # Membrane strip baseline: x=4..27, y=11..20
        for y in range(11, 21):
            for x in range(4, 28):
                if y == 12:
                    g[y][x] = membrane_hi
                elif 13 <= y <= 18:
                    g[y][x] = membrane_mid
                elif y == 19:
                    g[y][x] = membrane_shadow
                else:
                    g[y][x] = membrane_mid

        # Optional bulge region (x0..x1, y0..y1 inclusive), applied as a highlight cap.
        if bulge is not None:
            x0, x1, y0, y1 = bulge
            for y in range(y0, y1 + 1):
                for x in range(x0, x1 + 1):
                    if 0 <= x < size and 0 <= y < size:
                        # Top of bulge is highlight, lower row is mid.
                        g[y][x] = membrane_hi if y == y0 else membrane_mid

        # Outer border.
        for i in range(size):
            g[0][i] = ceramic_deep
            g[size - 1][i] = ceramic_deep
            g[i][0] = ceramic_deep
            g[i][size - 1] = ceramic_deep

        return g

    # Frames based on blueprint coordinates.
    top_frames = [
        make_top_frame(None),
        make_top_frame((5, 8, 10, 11)),      # left bulge cap
        make_top_frame((13, 18, 10, 11)),    # center bulge cap
        make_top_frame((23, 26, 10, 11)),    # right bulge cap
    ]

    # Bottom: flat ceramic with darker bottom band.
    bottom = _make_grid(size, ceramic_mid)
    for y in range(size):
        for x in range(size):
            if y >= 22:
                bottom[y][x] = ceramic_dark
            elif y <= 9:
                bottom[y][x] = ceramic_light
            else:
                bottom[y][x] = ceramic_mid
    for i in range(size):
        bottom[0][i] = ceramic_deep
        bottom[size - 1][i] = ceramic_deep
        bottom[i][0] = ceramic_deep
        bottom[i][size - 1] = ceramic_deep

    face_grids: dict[FaceName, list[list[str]]] = {
        "top": top_frames[0],
        "bottom": bottom,
        "north": side,
        "south": side,
        "east": side,
        "west": side,
    }

    item_grid = [row[:] for row in top_frames[0]]

    mcmeta = {
        "animation": {
            "frametime": 3,
            "frames": [0, 1, 2, 3],
        }
    }

    return GeneratedBlockAssets(
        block_id="conveyor_membrane",
        face_grids=face_grids,
        item_grid=item_grid,
        enum_variants={"pulse_phase": ["0", "1", "2", "3"]},
        animated_textures={"top": top_frames},
        mcmeta_by_texture={"top": mcmeta},
    )


def generate_pressure_loom() -> GeneratedBlockAssets:
    # Concept-level spec in docs/PRESSURE_LOGIC.md: Flowwright workstation with weaving motion.
    # Since no UV blueprint is provided yet, keep this minimal and palette-locked:
    # - Ceramic casing
    # - Front face: simple "loom" frame with a moving shuttle highlight (animated)
    size = 32
    ceramic_light = PALETTE["ceramic"]["light"]
    ceramic_mid = PALETTE["ceramic"]["mid"]
    ceramic_dark = PALETTE["ceramic"]["dark"]
    ceramic_deep = PALETTE["ceramic"]["deep"]
    glow = PALETTE["crystal"]["bright"]

    membrane_hi = PALETTE["membrane"]["highlight"]
    membrane_mid = PALETTE["membrane"]["mid"]
    membrane_shadow = PALETTE["membrane"]["shadow"]
    membrane_deep = PALETTE["membrane"]["deep"]
    membrane_line = PALETTE["membrane"]["line"]

    def make_side() -> list[list[str]]:
        g = _make_grid(size, ceramic_light)
        # Subtle vertical ribbing.
        for y in range(1, size - 1):
            for x in range(1, size - 1):
                if x in (6, 7, 15, 16, 24, 25):
                    g[y][x] = ceramic_dark
                elif x in (8, 17, 26):
                    g[y][x] = ceramic_mid

        # Glow seam.
        for y in range(size):
            g[y][16] = glow

        # Border.
        for i in range(size):
            g[0][i] = ceramic_deep
            g[size - 1][i] = ceramic_deep
            g[i][0] = ceramic_deep
            g[i][size - 1] = ceramic_deep
        return g

    side = make_side()

    def make_top() -> list[list[str]]:
        g = _make_grid(size, ceramic_light)

        # Central plate.
        for y in range(6, 26):
            for x in range(6, 26):
                g[y][x] = ceramic_mid

        # Inset + seam.
        for y in range(10, 22):
            for x in range(10, 22):
                g[y][x] = ceramic_light
        for y in range(10, 22):
            g[y][16] = glow

        # Border.
        for i in range(size):
            g[0][i] = ceramic_deep
            g[size - 1][i] = ceramic_deep
            g[i][0] = ceramic_deep
            g[i][size - 1] = ceramic_deep
        return g

    top = make_top()
    bottom = _make_grid(size, ceramic_mid)
    for i in range(size):
        bottom[0][i] = ceramic_deep
        bottom[size - 1][i] = ceramic_deep
        bottom[i][0] = ceramic_deep
        bottom[i][size - 1] = ceramic_deep

    def make_front_frame(shuttle_x: int) -> list[list[str]]:
        g = _make_grid(size, ceramic_light)

        # Outer border.
        for i in range(size):
            g[0][i] = ceramic_deep
            g[size - 1][i] = ceramic_deep
            g[i][0] = ceramic_deep
            g[i][size - 1] = ceramic_deep

        # Loom frame posts.
        for y in range(5, 27):
            for x in (7, 8, 23, 24):
                g[y][x] = ceramic_dark
        # Crossbar.
        for x in range(7, 25):
            g[7][x] = ceramic_dark
            g[8][x] = ceramic_mid

        # Weaving bed background.
        for y in range(11, 24):
            for x in range(10, 22):
                g[y][x] = ceramic_mid

        # Threads (membrane tension strip).
        for y in range(12, 23):
            for x in range(11, 21):
                if (x + y) % 4 == 0:
                    g[y][x] = membrane_shadow
                else:
                    g[y][x] = membrane_mid
        for x in range(11, 21):
            g[12][x] = membrane_hi
            g[22][x] = membrane_deep

        # Moving shuttle highlight line.
        sx = max(11, min(20, shuttle_x))
        for y in range(14, 21):
            g[y][sx] = membrane_hi
            if sx + 1 <= 20:
                g[y][sx + 1] = membrane_line

        # Glow seam in casing.
        for y in range(5, 27):
            g[y][16] = glow

        return g

    front_frames = [
        make_front_frame(12),
        make_front_frame(15),
        make_front_frame(18),
        make_front_frame(15),
    ]

    face_grids: dict[FaceName, list[list[str]]] = {
        "top": top,
        "bottom": bottom,
        "north": front_frames[0],
        "south": side,
        "east": side,
        "west": side,
    }

    item_grid = [row[:] for row in front_frames[0]]

    mcmeta = {
        "animation": {
            "frametime": 3,
            "frames": [0, 1, 2, 3],
        }
    }

    return GeneratedBlockAssets(
        block_id="pressure_loom",
        face_grids=face_grids,
        item_grid=item_grid,
        horizontal_facing=True,
        animated_textures={"front": front_frames},
        mcmeta_by_texture={"front": mcmeta},
    )


def generate_pressure_clutch() -> GeneratedBlockAssets:
    # Concept-level spec in docs/PRESSURE_LOGIC.md: redstone-controlled engagement for turbines.
    # No UV blueprint provided yet; keep this minimal + palette-locked.
    size = 32
    ceramic_light = PALETTE["ceramic"]["light"]
    ceramic_mid = PALETTE["ceramic"]["mid"]
    ceramic_dark = PALETTE["ceramic"]["dark"]
    ceramic_deep = PALETTE["ceramic"]["deep"]
    ceramic_line = PALETTE["ceramic"]["line"]

    glow = PALETTE["crystal"]["bright"]
    crystal_mid = PALETTE["crystal"]["mid"]
    crystal_shadow = PALETTE["crystal"]["shadow"]

    def border(g: list[list[str]]) -> None:
        for i in range(size):
            g[0][i] = ceramic_deep
            g[size - 1][i] = ceramic_deep
            g[i][0] = ceramic_deep
            g[i][size - 1] = ceramic_deep

    def make_top() -> list[list[str]]:
        g = _make_grid(size, ceramic_light)
        # Central plate.
        for y in range(7, 25):
            for x in range(7, 25):
                g[y][x] = ceramic_mid
        # Highlight + shadow bands.
        for y in range(7, 25):
            g[y][8] = ceramic_light
            g[y][23] = ceramic_dark
        for x in range(7, 25):
            g[8][x] = ceramic_light
            g[23][x] = ceramic_dark

        # Small crystal key notch.
        for y in range(14, 18):
            g[y][16] = glow

        border(g)
        return g

    def make_bottom() -> list[list[str]]:
        g = _make_grid(size, ceramic_mid)
        for y in range(24, 32):
            for x in range(size):
                g[y][x] = ceramic_dark
        border(g)
        return g

    def make_side() -> list[list[str]]:
        g = _make_grid(size, ceramic_light)
        # Ribbing + casing depth.
        for y in range(2, size - 2):
            for x in range(1, size - 1):
                if y >= 22:
                    g[y][x] = ceramic_mid
                if x in (6, 7, 24, 25):
                    g[y][x] = ceramic_dark
        # Glow seam.
        for y in range(size):
            g[y][16] = glow
        border(g)
        return g

    def make_front() -> list[list[str]]:
        g = _make_grid(size, ceramic_light)

        # Main casing.
        for y in range(4, 28):
            for x in range(4, 28):
                g[y][x] = ceramic_mid
        for y in range(5, 27):
            g[y][4] = ceramic_deep
            g[y][27] = ceramic_dark
        for x in range(4, 28):
            g[4][x] = ceramic_deep
            g[27][x] = ceramic_dark

        # Clutch ring.
        ring = _circle_mask(size, 16, 16, 9)
        hub = _circle_mask(size, 16, 16, 4)
        for y in range(size):
            for x in range(size):
                if not ring[y][x]:
                    continue
                if _is_outline(ring, x, y):
                    g[y][x] = ceramic_deep
                else:
                    g[y][x] = ceramic_dark
        for y in range(size):
            for x in range(size):
                if not hub[y][x]:
                    continue
                if _is_outline(hub, x, y):
                    g[y][x] = ceramic_line
                else:
                    g[y][x] = ceramic_mid

        # Crystal indicator notch.
        for y in range(12, 20):
            g[y][24] = crystal_shadow
            g[y][25] = crystal_mid
        g[15][25] = glow
        g[16][25] = glow

        # Glow seam.
        for y in range(4, 28):
            g[y][16] = glow

        border(g)
        return g

    top = make_top()
    bottom = make_bottom()
    side = make_side()
    front = make_front()

    face_grids: dict[FaceName, list[list[str]]] = {
        "top": top,
        "bottom": bottom,
        "north": front,
        "south": side,
        "east": side,
        "west": side,
    }

    item_grid = [row[:] for row in front]

    return GeneratedBlockAssets(
        block_id="pressure_clutch",
        face_grids=face_grids,
        item_grid=item_grid,
        horizontal_facing=True,
        powered=True,
    )


def main() -> None:
    repo_root = Path(__file__).resolve().parents[1]

    blocks: list[GeneratedBlockAssets] = [
        generate_pressure_conduit(),
        generate_membrane_pump(),
        generate_pressure_turbine(),
        generate_spiral_gearbox(),
        generate_vent_piston(),
        generate_atmospheric_compressor(),
        generate_pressure_valve(),
        generate_buoyancy_lift_platform(),
        generate_conveyor_membrane(),
        generate_pressure_loom(),
        generate_pressure_clutch(),
    ]

    for block_assets in blocks:
        emit_assets(repo_root, block_assets)

    print(f"Generated {len(blocks)} block(s): {', '.join(b.block_id for b in blocks)}")


if __name__ == "__main__":
    main()
