param(
    [string]$AssetsRoot = "src/main/resources/assets/kruemblegard",
    [string[]]$FixWoodPrefixes = @(
        "ashbloom",
        "cairn_tree",
        "driftwillow",
        "driftwood",
        "echowood",
        "fallbark",
        "faultwood",
        "glimmerpine",
        "hollowway_tree",
        "monument_oak",
        "shardbark_pine",
        "wayglass",
        "wayroot",
        "waytorch_tree"
    ),
    [string]$Namespace = "kruemblegard"
)

$ErrorActionPreference = 'Stop'

$modelsBlockDir = Join-Path $AssetsRoot "models/block"
$modelsItemDir  = Join-Path $AssetsRoot "models/item"
$blockstatesDir = Join-Path $AssetsRoot "blockstates"

function Ensure-File([string]$path, [string]$content) {
    if (-not (Test-Path $path)) {
        $dir = Split-Path -Parent $path
        if (-not (Test-Path $dir)) { New-Item -ItemType Directory -Force -Path $dir | Out-Null }
        Set-Content -Path $path -Value $content -NoNewline
        return $true
    }
    return $false
}

function Model-ButtonInventory([string]$prefix) {
@"
{
    "textures": { "texture": "$Namespace:block/${prefix}_planks" },
    "parent": "minecraft:block/button_inventory"
}
"@
}
function Model-Button([string]$prefix) {
@"
{
    "textures": { "texture": "$Namespace:block/${prefix}_planks" },
    "parent": "minecraft:block/button"
}
"@
}
function Model-ButtonPressed([string]$prefix) {
@"
{
    "textures": { "texture": "$Namespace:block/${prefix}_planks" },
    "parent": "minecraft:block/button_pressed"
}
"@
}
function Model-FencePost([string]$prefix) {
@"
{
    "textures": { "texture": "$Namespace:block/${prefix}_planks" },
    "parent": "minecraft:block/fence_post"
}
"@
}
function Model-FenceSide([string]$prefix) {
@"
{
    "textures": { "texture": "$Namespace:block/${prefix}_planks" },
    "parent": "minecraft:block/fence_side"
}
"@
}
function Model-FenceInventory([string]$prefix) {
@"
{
    "textures": { "texture": "$Namespace:block/${prefix}_planks" },
    "parent": "minecraft:block/fence_inventory"
}
"@
}
function Model-FenceGate([string]$prefix) {
@"
{
    "textures": { "texture": "$Namespace:block/${prefix}_planks" },
    "parent": "minecraft:block/template_fence_gate"
}
"@
}
function Model-FenceGateOpen([string]$prefix) {
@"
{
    "textures": { "texture": "$Namespace:block/${prefix}_planks" },
    "parent": "minecraft:block/template_fence_gate_open"
}
"@
}
function Model-FenceGateWall([string]$prefix) {
@"
{
    "textures": { "texture": "$Namespace:block/${prefix}_planks" },
    "parent": "minecraft:block/template_fence_gate_wall"
}
"@
}
function Model-FenceGateWallOpen([string]$prefix) {
@"
{
    "textures": { "texture": "$Namespace:block/${prefix}_planks" },
    "parent": "minecraft:block/template_fence_gate_wall_open"
}
"@
}
function Model-Planks([string]$prefix) {
@"
{
    "textures": { "all": "$Namespace:block/${prefix}_planks" },
    "parent": "minecraft:block/cube_all"
}
"@
}
function Model-Leaves([string]$prefix) {
@"
{
    "textures": { "all": "$Namespace:block/${prefix}_leaves" },
    "parent": "minecraft:block/cube_all"
}
"@
}
function Model-Sapling([string]$prefix) {
@"
{
    "textures": { "cross": "$Namespace:block/${prefix}_sapling" },
    "parent": "minecraft:block/cross"
}
"@
}

function Model-PressurePlateUp([string]$prefix) {
@"
{
    "textures": { "texture": "$Namespace:block/${prefix}_planks" },
    "parent": "minecraft:block/pressure_plate_up"
}
"@
}
function Model-PressurePlateDown([string]$prefix) {
@"
{
    "textures": { "texture": "$Namespace:block/${prefix}_planks" },
    "parent": "minecraft:block/pressure_plate_down"
}
"@
}

function Model-Slab([string]$prefix) {
@"
{
    "textures": {
        "bottom": "$Namespace:block/${prefix}_planks",
        "top": "$Namespace:block/${prefix}_planks",
        "side": "$Namespace:block/${prefix}_planks"
    },
    "parent": "minecraft:block/slab"
}
"@
}
function Model-SlabTop([string]$prefix) {
@"
{
    "textures": {
        "bottom": "$Namespace:block/${prefix}_planks",
        "top": "$Namespace:block/${prefix}_planks",
        "side": "$Namespace:block/${prefix}_planks"
    },
    "parent": "minecraft:block/slab_top"
}
"@
}
function Model-SlabDouble([string]$prefix) {
@"
{
    "textures": { "all": "$Namespace:block/${prefix}_planks" },
    "parent": "minecraft:block/cube_all"
}
"@
}

function Model-Stairs([string]$prefix) {
@"
{
    "textures": {
        "bottom": "$Namespace:block/${prefix}_planks",
        "top": "$Namespace:block/${prefix}_planks",
        "side": "$Namespace:block/${prefix}_planks"
    },
    "parent": "minecraft:block/stairs"
}
"@
}
function Model-InnerStairs([string]$prefix) {
@"
{
    "textures": {
        "bottom": "$Namespace:block/${prefix}_planks",
        "top": "$Namespace:block/${prefix}_planks",
        "side": "$Namespace:block/${prefix}_planks"
    },
    "parent": "minecraft:block/inner_stairs"
}
"@
}
function Model-OuterStairs([string]$prefix) {
@"
{
    "textures": {
        "bottom": "$Namespace:block/${prefix}_planks",
        "top": "$Namespace:block/${prefix}_planks",
        "side": "$Namespace:block/${prefix}_planks"
    },
    "parent": "minecraft:block/outer_stairs"
}
"@
}

function Model-TrapdoorBottom([string]$prefix) {
@"
{
    "textures": { "texture": "$Namespace:block/${prefix}_trapdoor" },
    "parent": "minecraft:block/template_trapdoor_bottom"
}
"@
}
function Model-TrapdoorTop([string]$prefix) {
@"
{
    "textures": { "texture": "$Namespace:block/${prefix}_trapdoor" },
    "parent": "minecraft:block/template_trapdoor_top"
}
"@
}
function Model-TrapdoorOpen([string]$prefix) {
@"
{
    "textures": { "texture": "$Namespace:block/${prefix}_trapdoor" },
    "parent": "minecraft:block/template_trapdoor_open"
}
"@
}

function Model-DoorBottomLeft([string]$prefix) {
@"
{
    "textures": {
        "bottom": "$Namespace:block/${prefix}_door_bottom",
        "top": "$Namespace:block/${prefix}_door_top"
    },
    "parent": "minecraft:block/door_bottom_left"
}
"@
}
function Model-DoorBottomLeftOpen([string]$prefix) {
@"
{
    "textures": {
        "bottom": "$Namespace:block/${prefix}_door_bottom",
        "top": "$Namespace:block/${prefix}_door_top"
    },
    "parent": "minecraft:block/door_bottom_left_open"
}
"@
}
function Model-DoorBottomRight([string]$prefix) {
@"
{
    "textures": {
        "bottom": "$Namespace:block/${prefix}_door_bottom",
        "top": "$Namespace:block/${prefix}_door_top"
    },
    "parent": "minecraft:block/door_bottom_right"
}
"@
}
function Model-DoorBottomRightOpen([string]$prefix) {
@"
{
    "textures": {
        "bottom": "$Namespace:block/${prefix}_door_bottom",
        "top": "$Namespace:block/${prefix}_door_top"
    },
    "parent": "minecraft:block/door_bottom_right_open"
}
"@
}
function Model-DoorTopLeft([string]$prefix) {
@"
{
    "textures": {
        "bottom": "$Namespace:block/${prefix}_door_bottom",
        "top": "$Namespace:block/${prefix}_door_top"
    },
    "parent": "minecraft:block/door_top_left"
}
"@
}
function Model-DoorTopLeftOpen([string]$prefix) {
@"
{
    "textures": {
        "bottom": "$Namespace:block/${prefix}_door_bottom",
        "top": "$Namespace:block/${prefix}_door_top"
    },
    "parent": "minecraft:block/door_top_left_open"
}
"@
}
function Model-DoorTopRight([string]$prefix) {
@"
{
    "textures": {
        "bottom": "$Namespace:block/${prefix}_door_bottom",
        "top": "$Namespace:block/${prefix}_door_top"
    },
    "parent": "minecraft:block/door_top_right"
}
"@
}
function Model-DoorTopRightOpen([string]$prefix) {
@"
{
    "textures": {
        "bottom": "$Namespace:block/${prefix}_door_bottom",
        "top": "$Namespace:block/${prefix}_door_top"
    },
    "parent": "minecraft:block/door_top_right_open"
}
"@
}

[int]$created = 0
[int]$updated = 0

# 1) Provide a self-contained fallback for ancient_waystone (avoid missing when Waystones isn't present)
$ancientBlockModel = Join-Path $modelsBlockDir "ancient_waystone.json"
$ancientItemModel  = Join-Path $modelsItemDir  "ancient_waystone.json"

$ancientBlockContent = @"
{
    "parent": "minecraft:block/cube_all",
    "textures": { "all": "$Namespace:block/standing_stone" }
}
"@
if ((Get-Content -Raw -Path $ancientBlockModel) -ne $ancientBlockContent) {
    Set-Content -Path $ancientBlockModel -Value $ancientBlockContent -NoNewline
    $updated++
}
$ancientItemContent = @"
{
    "parent": "$Namespace:block/ancient_waystone"
}
"@
if ((Get-Content -Raw -Path $ancientItemModel) -ne $ancientItemContent) {
    Set-Content -Path $ancientItemModel -Value $ancientItemContent -NoNewline
    $updated++
}

# 2) Fix item model parents for button/fence/fence_gate
$itemFiles = Get-ChildItem -Path $modelsItemDir -File -Filter '*.json'
foreach ($f in $itemFiles) {
    $name = [System.IO.Path]::GetFileNameWithoutExtension($f.Name)

    $suffix = $null
    if ($name.EndsWith('_button')) { $suffix = 'button' }
    elseif ($name.EndsWith('_fence_gate')) { $suffix = 'fence_gate' }
    elseif ($name.EndsWith('_fence')) { $suffix = 'fence' }

    if ($null -eq $suffix) { continue }

    $prefix = $name.Substring(0, $name.Length - ($suffix.Length + 1))

    $expectedParent = $null
    switch ($suffix) {
        'button'     { $expectedParent = "$Namespace:block/${prefix}_button_inventory" }
        'fence'      { $expectedParent = "$Namespace:block/${prefix}_fence_inventory" }
        'fence_gate' { $expectedParent = "$Namespace:block/${prefix}_fence_gate" }
    }

    $raw = Get-Content -Raw -Path $f.FullName

    # Replace a stale/incorrect parent with the expected pattern.
    $new = $raw
    if ($raw -match '"parent"\s*:\s*"[^"]+"') {
        $new = [regex]::Replace($raw, '"parent"\s*:\s*"[^"]+"', ('"parent": "' + $expectedParent + '"'))
    }

    if ($new -ne $raw) {
        Set-Content -Path $f.FullName -Value $new -NoNewline
        $updated++
    }
}

# 3) Ensure missing block models exist for selected wood families and fix their blockstates that still point at driftwillow
foreach ($prefix in $FixWoodPrefixes) {
    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_planks.json") (Model-Planks $prefix))
    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_leaves.json") (Model-Leaves $prefix))
    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_sapling.json") (Model-Sapling $prefix))

    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_button_inventory.json") (Model-ButtonInventory $prefix))
    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_button.json") (Model-Button $prefix))
    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_button_pressed.json") (Model-ButtonPressed $prefix))

    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_fence_post.json") (Model-FencePost $prefix))
    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_fence_side.json") (Model-FenceSide $prefix))
    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_fence_inventory.json") (Model-FenceInventory $prefix))

    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_fence_gate.json") (Model-FenceGate $prefix))
    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_fence_gate_open.json") (Model-FenceGateOpen $prefix))
    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_fence_gate_wall.json") (Model-FenceGateWall $prefix))
    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_fence_gate_wall_open.json") (Model-FenceGateWallOpen $prefix))

    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_pressure_plate.json") (Model-PressurePlateUp $prefix))
    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_pressure_plate_down.json") (Model-PressurePlateDown $prefix))

    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_slab.json") (Model-Slab $prefix))
    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_slab_top.json") (Model-SlabTop $prefix))
    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_slab_double.json") (Model-SlabDouble $prefix))

    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_stairs.json") (Model-Stairs $prefix))
    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_stairs_inner.json") (Model-InnerStairs $prefix))
    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_stairs_outer.json") (Model-OuterStairs $prefix))

    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_trapdoor_bottom.json") (Model-TrapdoorBottom $prefix))
    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_trapdoor_top.json") (Model-TrapdoorTop $prefix))
    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_trapdoor_open.json") (Model-TrapdoorOpen $prefix))

    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_door_bottom_left.json") (Model-DoorBottomLeft $prefix))
    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_door_bottom_left_open.json") (Model-DoorBottomLeftOpen $prefix))
    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_door_bottom_right.json") (Model-DoorBottomRight $prefix))
    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_door_bottom_right_open.json") (Model-DoorBottomRightOpen $prefix))
    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_door_top_left.json") (Model-DoorTopLeft $prefix))
    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_door_top_left_open.json") (Model-DoorTopLeftOpen $prefix))
    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_door_top_right.json") (Model-DoorTopRight $prefix))
    $created += [int](Ensure-File (Join-Path $modelsBlockDir "${prefix}_door_top_right_open.json") (Model-DoorTopRightOpen $prefix))

    $bsFiles = Get-ChildItem -Path $blockstatesDir -File -Filter "${prefix}_*.json"
    foreach ($bs in $bsFiles) {
        $bsRaw = Get-Content -Raw -Path $bs.FullName
        $bsNew = $bsRaw -replace "$Namespace:block/driftwillow_", "$Namespace:block/${prefix}_"

        if ($bsNew -ne $bsRaw) {
            Set-Content -Path $bs.FullName -Value $bsNew -NoNewline
            $updated++
        }
    }
}

Write-Host "Done. Models created: $created; Files updated: $updated"
