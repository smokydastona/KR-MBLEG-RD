param(
    [string[]]$Bases = @(
        'attuned_stone',
        'fractured_wayrock',
        'crushstone',
        'scarstone',
        'cracked_scarstone',
        'polished_scarstone',
        'stoneveil_rubble',
        'polished_stoneveil_rubble',
        'runed_stoneveil_rubble'
    ),
    [string]$ModId = 'kruemblegard'
)

$ErrorActionPreference = 'Stop'

$repoRoot = Split-Path -Parent $PSScriptRoot

$assetsRoot = Join-Path $repoRoot "src/main/resources/assets/$ModId"
$dataRoot = Join-Path $repoRoot "src/main/resources/data/$ModId"

$blockstatesDir = Join-Path $assetsRoot 'blockstates'
$modelsBlockDir = Join-Path $assetsRoot 'models/block'
$modelsItemDir = Join-Path $assetsRoot 'models/item'

$lootBlocksDir = Join-Path $dataRoot 'loot_tables/blocks'
$recipesDir = Join-Path $dataRoot 'recipes'

$dirs = @(
    $blockstatesDir,
    $modelsBlockDir,
    $modelsItemDir,
    $lootBlocksDir,
    $recipesDir
)

foreach ($dir in $dirs) {
    if (-not (Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
    }
}

function Write-JsonFile {
    param(
        [Parameter(Mandatory)] [string]$Path,
        [Parameter(Mandatory)] [string]$Json
    )

    $normalized = $Json.Trim() + "`n"
    Set-Content -LiteralPath $Path -Value $normalized -Encoding UTF8
}

function Stairs-BlockstateJson {
    param([string]$base)

    # Standard stairs mapping; keep compact to reduce file size.
    return @"
{
  "variants": {
    "facing=east,half=bottom,shape=straight": { "model": "$ModId:block/${base}_stairs" },
    "facing=west,half=bottom,shape=straight": { "model": "$ModId:block/${base}_stairs", "y": 180 },
    "facing=south,half=bottom,shape=straight": { "model": "$ModId:block/${base}_stairs", "y": 90 },
    "facing=north,half=bottom,shape=straight": { "model": "$ModId:block/${base}_stairs", "y": 270 },

    "facing=east,half=top,shape=straight": { "model": "$ModId:block/${base}_stairs", "x": 180 },
    "facing=west,half=top,shape=straight": { "model": "$ModId:block/${base}_stairs", "x": 180, "y": 180 },
    "facing=south,half=top,shape=straight": { "model": "$ModId:block/${base}_stairs", "x": 180, "y": 90 },
    "facing=north,half=top,shape=straight": { "model": "$ModId:block/${base}_stairs", "x": 180, "y": 270 },

    "facing=east,half=bottom,shape=inner_left": { "model": "$ModId:block/${base}_stairs_inner", "y": 270 },
    "facing=west,half=bottom,shape=inner_left": { "model": "$ModId:block/${base}_stairs_inner", "y": 90 },
    "facing=south,half=bottom,shape=inner_left": { "model": "$ModId:block/${base}_stairs_inner" },
    "facing=north,half=bottom,shape=inner_left": { "model": "$ModId:block/${base}_stairs_inner", "y": 180 },

    "facing=east,half=bottom,shape=inner_right": { "model": "$ModId:block/${base}_stairs_inner" },
    "facing=west,half=bottom,shape=inner_right": { "model": "$ModId:block/${base}_stairs_inner", "y": 180 },
    "facing=south,half=bottom,shape=inner_right": { "model": "$ModId:block/${base}_stairs_inner", "y": 90 },
    "facing=north,half=bottom,shape=inner_right": { "model": "$ModId:block/${base}_stairs_inner", "y": 270 },

    "facing=east,half=bottom,shape=outer_left": { "model": "$ModId:block/${base}_stairs_outer", "y": 270 },
    "facing=west,half=bottom,shape=outer_left": { "model": "$ModId:block/${base}_stairs_outer", "y": 90 },
    "facing=south,half=bottom,shape=outer_left": { "model": "$ModId:block/${base}_stairs_outer" },
    "facing=north,half=bottom,shape=outer_left": { "model": "$ModId:block/${base}_stairs_outer", "y": 180 },

    "facing=east,half=bottom,shape=outer_right": { "model": "$ModId:block/${base}_stairs_outer" },
    "facing=west,half=bottom,shape=outer_right": { "model": "$ModId:block/${base}_stairs_outer", "y": 180 },
    "facing=south,half=bottom,shape=outer_right": { "model": "$ModId:block/${base}_stairs_outer", "y": 90 },
    "facing=north,half=bottom,shape=outer_right": { "model": "$ModId:block/${base}_stairs_outer", "y": 270 },

    "facing=east,half=top,shape=inner_left": { "model": "$ModId:block/${base}_stairs_inner", "x": 180, "y": 270 },
    "facing=west,half=top,shape=inner_left": { "model": "$ModId:block/${base}_stairs_inner", "x": 180, "y": 90 },
    "facing=south,half=top,shape=inner_left": { "model": "$ModId:block/${base}_stairs_inner", "x": 180 },
    "facing=north,half=top,shape=inner_left": { "model": "$ModId:block/${base}_stairs_inner", "x": 180, "y": 180 },

    "facing=east,half=top,shape=inner_right": { "model": "$ModId:block/${base}_stairs_inner", "x": 180 },
    "facing=west,half=top,shape=inner_right": { "model": "$ModId:block/${base}_stairs_inner", "x": 180, "y": 180 },
    "facing=south,half=top,shape=inner_right": { "model": "$ModId:block/${base}_stairs_inner", "x": 180, "y": 90 },
    "facing=north,half=top,shape=inner_right": { "model": "$ModId:block/${base}_stairs_inner", "x": 180, "y": 270 },

    "facing=east,half=top,shape=outer_left": { "model": "$ModId:block/${base}_stairs_outer", "x": 180, "y": 270 },
    "facing=west,half=top,shape=outer_left": { "model": "$ModId:block/${base}_stairs_outer", "x": 180, "y": 90 },
    "facing=south,half=top,shape=outer_left": { "model": "$ModId:block/${base}_stairs_outer", "x": 180 },
    "facing=north,half=top,shape=outer_left": { "model": "$ModId:block/${base}_stairs_outer", "x": 180, "y": 180 },

    "facing=east,half=top,shape=outer_right": { "model": "$ModId:block/${base}_stairs_outer", "x": 180 },
    "facing=west,half=top,shape=outer_right": { "model": "$ModId:block/${base}_stairs_outer", "x": 180, "y": 180 },
    "facing=south,half=top,shape=outer_right": { "model": "$ModId:block/${base}_stairs_outer", "x": 180, "y": 90 },
    "facing=north,half=top,shape=outer_right": { "model": "$ModId:block/${base}_stairs_outer", "x": 180, "y": 270 }
  }
}
"@
}

function Slab-BlockstateJson {
    param([string]$base)

    return @"
{
  "variants": {
    "type=bottom": { "model": "$ModId:block/${base}_slab" },
    "type=top": { "model": "$ModId:block/${base}_slab_top" },
    "type=double": { "model": "$ModId:block/${base}_slab_double" }
  }
}
"@
}

function Wall-BlockstateJson {
    param([string]$base)

    return @"
{
  "multipart": [
    { "when": { "up": "true" }, "apply": { "model": "$ModId:block/${base}_wall_post" } },

    { "when": { "north": "low" }, "apply": { "model": "$ModId:block/${base}_wall_side", "uvlock": true } },
    { "when": { "east": "low" }, "apply": { "model": "$ModId:block/${base}_wall_side", "y": 90, "uvlock": true } },
    { "when": { "south": "low" }, "apply": { "model": "$ModId:block/${base}_wall_side", "y": 180, "uvlock": true } },
    { "when": { "west": "low" }, "apply": { "model": "$ModId:block/${base}_wall_side", "y": 270, "uvlock": true } },

    { "when": { "north": "tall" }, "apply": { "model": "$ModId:block/${base}_wall_side_tall", "uvlock": true } },
    { "when": { "east": "tall" }, "apply": { "model": "$ModId:block/${base}_wall_side_tall", "y": 90, "uvlock": true } },
    { "when": { "south": "tall" }, "apply": { "model": "$ModId:block/${base}_wall_side_tall", "y": 180, "uvlock": true } },
    { "when": { "west": "tall" }, "apply": { "model": "$ModId:block/${base}_wall_side_tall", "y": 270, "uvlock": true } }
  ]
}
"@
}

function Stairs-ModelJson {
    param([string]$base, [string]$kind)

    $parent = switch ($kind) {
        'inner' { 'minecraft:block/inner_stairs' }
        'outer' { 'minecraft:block/outer_stairs' }
        default { 'minecraft:block/stairs' }
    }

    return @"
{
  "parent": "$parent",
  "textures": {
    "bottom": "$ModId:block/$base",
    "top": "$ModId:block/$base",
    "side": "$ModId:block/$base"
  }
}
"@
}

function Slab-ModelJson {
    param([string]$base, [string]$kind)

    $parent = switch ($kind) {
        'top' { 'minecraft:block/slab_top' }
        default { 'minecraft:block/slab' }
    }

    return @"
{
  "parent": "$parent",
  "textures": {
    "bottom": "$ModId:block/$base",
    "top": "$ModId:block/$base",
    "side": "$ModId:block/$base"
  }
}
"@
}

function Slab-DoubleModelJson {
    param([string]$base)

    return @"
{
  "parent": "minecraft:block/cube_all",
  "textures": {
    "all": "$ModId:block/$base"
  }
}
"@
}

function Wall-ModelJson {
    param([string]$base, [string]$kind)

    $parent = switch ($kind) {
        'post' { 'minecraft:block/template_wall_post' }
        'side_tall' { 'minecraft:block/template_wall_side_tall' }
        default { 'minecraft:block/template_wall_side' }
    }

    return @"
{
  "parent": "$parent",
  "textures": {
    "wall": "$ModId:block/$base"
  }
}
"@
}

function Item-BlockParentJson {
    param([string]$parent)

    return @"
{
  "parent": "$parent"
}
"@
}

function Item-WallInventoryJson {
    param([string]$base)

    return @"
{
  "parent": "minecraft:block/wall_inventory",
  "textures": {
    "wall": "$ModId:block/$base"
  }
}
"@
}

function Loot-SimpleDropJson {
    param([string]$id)

    return @"
{
  "type": "minecraft:block",
  "pools": [
    { "rolls": 1, "entries": [{ "type": "minecraft:item", "name": "${ModId}:${id}" }] }
  ]
}
"@
}

function Loot-SlabDropJson {
    param([string]$id)

    return @"
{
  "type": "minecraft:block",
  "pools": [
    {
      "rolls": 1,
      "entries": [{ "type": "minecraft:item", "name": "${ModId}:${id}" }],
      "functions": [
        {
          "function": "minecraft:set_count",
          "count": 2,
          "conditions": [
            {
              "condition": "minecraft:block_state_property",
              "block": "${ModId}:${id}",
              "properties": { "type": "double" }
            }
          ]
        }
      ]
    }
  ]
}
"@
}

function Recipe-StairsJson {
    param([string]$base)

    return @"
{
  "type": "minecraft:crafting_shaped",
  "pattern": [
    "X  ",
    "XX ",
    "XXX"
  ],
  "key": {
    "X": { "item": "${ModId}:${base}" }
  },
  "result": { "item": "${ModId}:${base}_stairs", "count": 4 }
}
"@
}

function Recipe-SlabJson {
    param([string]$base)

    return @"
{
  "type": "minecraft:crafting_shaped",
  "pattern": [
    "XXX"
  ],
  "key": {
    "X": { "item": "${ModId}:${base}" }
  },
  "result": { "item": "${ModId}:${base}_slab", "count": 6 }
}
"@
}

function Recipe-WallJson {
    param([string]$base)

    return @"
{
  "type": "minecraft:crafting_shaped",
  "pattern": [
    "XXX",
    "XXX"
  ],
  "key": {
    "X": { "item": "${ModId}:${base}" }
  },
  "result": { "item": "${ModId}:${base}_wall", "count": 6 }
}
"@
}

foreach ($base in $Bases) {
    foreach ($kind in @('stairs', 'slab', 'wall')) {
        $blockstatePath = Join-Path $blockstatesDir "${base}_${kind}.json"

        $blockstateJson = switch ($kind) {
            'stairs' { Stairs-BlockstateJson $base }
            'slab' { Slab-BlockstateJson $base }
            'wall' { Wall-BlockstateJson $base }
        }

        Write-JsonFile -Path $blockstatePath -Json $blockstateJson
    }

    # Block models
    Write-JsonFile -Path (Join-Path $modelsBlockDir "${base}_stairs.json") -Json (Stairs-ModelJson $base 'normal')
    Write-JsonFile -Path (Join-Path $modelsBlockDir "${base}_stairs_inner.json") -Json (Stairs-ModelJson $base 'inner')
    Write-JsonFile -Path (Join-Path $modelsBlockDir "${base}_stairs_outer.json") -Json (Stairs-ModelJson $base 'outer')

    Write-JsonFile -Path (Join-Path $modelsBlockDir "${base}_slab.json") -Json (Slab-ModelJson $base 'bottom')
    Write-JsonFile -Path (Join-Path $modelsBlockDir "${base}_slab_top.json") -Json (Slab-ModelJson $base 'top')
    Write-JsonFile -Path (Join-Path $modelsBlockDir "${base}_slab_double.json") -Json (Slab-DoubleModelJson $base)

    Write-JsonFile -Path (Join-Path $modelsBlockDir "${base}_wall_post.json") -Json (Wall-ModelJson $base 'post')
    Write-JsonFile -Path (Join-Path $modelsBlockDir "${base}_wall_side.json") -Json (Wall-ModelJson $base 'side')
    Write-JsonFile -Path (Join-Path $modelsBlockDir "${base}_wall_side_tall.json") -Json (Wall-ModelJson $base 'side_tall')

    # Item models
    Write-JsonFile -Path (Join-Path $modelsItemDir "${base}_stairs.json") -Json (Item-BlockParentJson "$ModId:block/${base}_stairs")
    Write-JsonFile -Path (Join-Path $modelsItemDir "${base}_slab.json") -Json (Item-BlockParentJson "$ModId:block/${base}_slab")
    Write-JsonFile -Path (Join-Path $modelsItemDir "${base}_wall.json") -Json (Item-WallInventoryJson $base)

    # Loot tables
    Write-JsonFile -Path (Join-Path $lootBlocksDir "${base}_stairs.json") -Json (Loot-SimpleDropJson "${base}_stairs")
    Write-JsonFile -Path (Join-Path $lootBlocksDir "${base}_slab.json") -Json (Loot-SlabDropJson "${base}_slab")
    Write-JsonFile -Path (Join-Path $lootBlocksDir "${base}_wall.json") -Json (Loot-SimpleDropJson "${base}_wall")

    # Recipes
    Write-JsonFile -Path (Join-Path $recipesDir "${base}_stairs.json") -Json (Recipe-StairsJson $base)
    Write-JsonFile -Path (Join-Path $recipesDir "${base}_slab.json") -Json (Recipe-SlabJson $base)
    Write-JsonFile -Path (Join-Path $recipesDir "${base}_wall.json") -Json (Recipe-WallJson $base)
}

Write-Host "Generated stone family blocks for: $($Bases -join ', ')"
