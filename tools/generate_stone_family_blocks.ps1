[Diagnostics.CodeAnalysis.SuppressMessageAttribute('PSUseApprovedVerbs', '', Justification = 'Local helper functions in this script are not exported cmdlets; keep names concise.')]
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

function New-StairsBlockstateJson {
    param([string]$base)

    # Standard stairs mapping; keep compact to reduce file size.
    return @"
{
  "variants": {
    "facing=east,half=bottom,shape=straight": { "model": "${ModId}:block/${base}_stairs" },
    "facing=west,half=bottom,shape=straight": { "model": "${ModId}:block/${base}_stairs", "y": 180 },
    "facing=south,half=bottom,shape=straight": { "model": "${ModId}:block/${base}_stairs", "y": 90 },
    "facing=north,half=bottom,shape=straight": { "model": "${ModId}:block/${base}_stairs", "y": 270 },

    "facing=east,half=top,shape=straight": { "model": "${ModId}:block/${base}_stairs", "x": 180 },
    "facing=west,half=top,shape=straight": { "model": "${ModId}:block/${base}_stairs", "x": 180, "y": 180 },
    "facing=south,half=top,shape=straight": { "model": "${ModId}:block/${base}_stairs", "x": 180, "y": 90 },
    "facing=north,half=top,shape=straight": { "model": "${ModId}:block/${base}_stairs", "x": 180, "y": 270 },

    "facing=east,half=bottom,shape=inner_left": { "model": "${ModId}:block/${base}_stairs_inner", "y": 270 },
    "facing=west,half=bottom,shape=inner_left": { "model": "${ModId}:block/${base}_stairs_inner", "y": 90 },
    "facing=south,half=bottom,shape=inner_left": { "model": "${ModId}:block/${base}_stairs_inner" },
    "facing=north,half=bottom,shape=inner_left": { "model": "${ModId}:block/${base}_stairs_inner", "y": 180 },

    "facing=east,half=bottom,shape=inner_right": { "model": "${ModId}:block/${base}_stairs_inner" },
    "facing=west,half=bottom,shape=inner_right": { "model": "${ModId}:block/${base}_stairs_inner", "y": 180 },
    "facing=south,half=bottom,shape=inner_right": { "model": "${ModId}:block/${base}_stairs_inner", "y": 90 },
    "facing=north,half=bottom,shape=inner_right": { "model": "${ModId}:block/${base}_stairs_inner", "y": 270 },

    "facing=east,half=bottom,shape=outer_left": { "model": "${ModId}:block/${base}_stairs_outer", "y": 270 },
    "facing=west,half=bottom,shape=outer_left": { "model": "${ModId}:block/${base}_stairs_outer", "y": 90 },
    "facing=south,half=bottom,shape=outer_left": { "model": "${ModId}:block/${base}_stairs_outer" },
    "facing=north,half=bottom,shape=outer_left": { "model": "${ModId}:block/${base}_stairs_outer", "y": 180 },

    "facing=east,half=bottom,shape=outer_right": { "model": "${ModId}:block/${base}_stairs_outer" },
    "facing=west,half=bottom,shape=outer_right": { "model": "${ModId}:block/${base}_stairs_outer", "y": 180 },
    "facing=south,half=bottom,shape=outer_right": { "model": "${ModId}:block/${base}_stairs_outer", "y": 90 },
    "facing=north,half=bottom,shape=outer_right": { "model": "${ModId}:block/${base}_stairs_outer", "y": 270 },

    "facing=east,half=top,shape=inner_left": { "model": "${ModId}:block/${base}_stairs_inner", "x": 180, "y": 270 },
    "facing=west,half=top,shape=inner_left": { "model": "${ModId}:block/${base}_stairs_inner", "x": 180, "y": 90 },
    "facing=south,half=top,shape=inner_left": { "model": "${ModId}:block/${base}_stairs_inner", "x": 180 },
    "facing=north,half=top,shape=inner_left": { "model": "${ModId}:block/${base}_stairs_inner", "x": 180, "y": 180 },

    "facing=east,half=top,shape=inner_right": { "model": "${ModId}:block/${base}_stairs_inner", "x": 180 },
    "facing=west,half=top,shape=inner_right": { "model": "${ModId}:block/${base}_stairs_inner", "x": 180, "y": 180 },
    "facing=south,half=top,shape=inner_right": { "model": "${ModId}:block/${base}_stairs_inner", "x": 180, "y": 90 },
    "facing=north,half=top,shape=inner_right": { "model": "${ModId}:block/${base}_stairs_inner", "x": 180, "y": 270 },

    "facing=east,half=top,shape=outer_left": { "model": "${ModId}:block/${base}_stairs_outer", "x": 180, "y": 270 },
    "facing=west,half=top,shape=outer_left": { "model": "${ModId}:block/${base}_stairs_outer", "x": 180, "y": 90 },
    "facing=south,half=top,shape=outer_left": { "model": "${ModId}:block/${base}_stairs_outer", "x": 180 },
    "facing=north,half=top,shape=outer_left": { "model": "${ModId}:block/${base}_stairs_outer", "x": 180, "y": 180 },

    "facing=east,half=top,shape=outer_right": { "model": "${ModId}:block/${base}_stairs_outer", "x": 180 },
    "facing=west,half=top,shape=outer_right": { "model": "${ModId}:block/${base}_stairs_outer", "x": 180, "y": 180 },
    "facing=south,half=top,shape=outer_right": { "model": "${ModId}:block/${base}_stairs_outer", "x": 180, "y": 90 },
    "facing=north,half=top,shape=outer_right": { "model": "${ModId}:block/${base}_stairs_outer", "x": 180, "y": 270 }
  }
}
"@
}

function New-SlabBlockstateJson {
    param([string]$base)

    return @"
{
  "variants": {
    "type=bottom": { "model": "${ModId}:block/${base}_slab" },
    "type=top": { "model": "${ModId}:block/${base}_slab_top" },
    "type=double": { "model": "${ModId}:block/${base}_slab_double" }
  }
}
"@
}

function New-WallBlockstateJson {
    param([string]$base)

    return @"
{
  "multipart": [
    { "when": { "up": "true" }, "apply": { "model": "${ModId}:block/${base}_wall_post" } },

    { "when": { "north": "low" }, "apply": { "model": "${ModId}:block/${base}_wall_side", "uvlock": true } },
    { "when": { "east": "low" }, "apply": { "model": "${ModId}:block/${base}_wall_side", "y": 90, "uvlock": true } },
    { "when": { "south": "low" }, "apply": { "model": "${ModId}:block/${base}_wall_side", "y": 180, "uvlock": true } },
    { "when": { "west": "low" }, "apply": { "model": "${ModId}:block/${base}_wall_side", "y": 270, "uvlock": true } },

    { "when": { "north": "tall" }, "apply": { "model": "${ModId}:block/${base}_wall_side_tall", "uvlock": true } },
    { "when": { "east": "tall" }, "apply": { "model": "${ModId}:block/${base}_wall_side_tall", "y": 90, "uvlock": true } },
    { "when": { "south": "tall" }, "apply": { "model": "${ModId}:block/${base}_wall_side_tall", "y": 180, "uvlock": true } },
    { "when": { "west": "tall" }, "apply": { "model": "${ModId}:block/${base}_wall_side_tall", "y": 270, "uvlock": true } }
  ]
}
"@
}

function New-StairsModelJson {
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
    "bottom": "${ModId}:block/$base",
    "top": "${ModId}:block/$base",
    "side": "${ModId}:block/$base"
  }
}
"@
}

function New-SlabModelJson {
    param([string]$base, [string]$kind)

    $parent = switch ($kind) {
        'top' { 'minecraft:block/slab_top' }
        default { 'minecraft:block/slab' }
    }

    return @"
{
  "parent": "$parent",
  "textures": {
    "bottom": "${ModId}:block/$base",
    "top": "${ModId}:block/$base",
    "side": "${ModId}:block/$base"
  }
}
"@
}

function New-SlabDoubleModelJson {
    param([string]$base)

    return @"
{
  "parent": "minecraft:block/cube_all",
  "textures": {
    "all": "${ModId}:block/$base"
  }
}
"@
}

function New-WallModelJson {
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
    "wall": "${ModId}:block/$base"
  }
}
"@
}

function New-ItemBlockParentJson {
    param([string]$parent)

    return @"
{
  "parent": "$parent"
}
"@
}

function New-ItemWallInventoryJson {
    param([string]$base)

    return @"
{
  "parent": "minecraft:block/wall_inventory",
  "textures": {
    "wall": "${ModId}:block/$base"
  }
}
"@
}

function New-LootSimpleDropJson {
    param([string]$id)

  $itemId = $ModId + ':' + $id

    return @"
{
  "type": "minecraft:block",
  "pools": [
    { "rolls": 1, "entries": [{ "type": "minecraft:item", "name": "$itemId" }] }
  ]
}
"@
}

function New-LootSlabDropJson {
    param([string]$id)

  $itemId = $ModId + ':' + $id

    return @"
{
  "type": "minecraft:block",
  "pools": [
    {
      "rolls": 1,
      "entries": [{ "type": "minecraft:item", "name": "$itemId" }],
      "functions": [
        {
          "function": "minecraft:set_count",
          "count": 2,
          "conditions": [
            {
              "condition": "minecraft:block_state_property",
              "block": "$itemId",
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

function New-RecipeStairsJson {
    param([string]$base)

  $baseId = $ModId + ':' + $base
  $resultId = $ModId + ':' + "${base}_stairs"

    return @"
{
  "type": "minecraft:crafting_shaped",
  "pattern": [
    "X  ",
    "XX ",
    "XXX"
  ],
  "key": {
    "X": { "item": "$baseId" }
  },
  "result": { "item": "$resultId", "count": 4 }
}
"@
}

function New-RecipeSlabJson {
    param([string]$base)

  $baseId = $ModId + ':' + $base
  $resultId = $ModId + ':' + "${base}_slab"

    return @"
{
  "type": "minecraft:crafting_shaped",
  "pattern": [
    "XXX"
  ],
  "key": {
    "X": { "item": "$baseId" }
  },
  "result": { "item": "$resultId", "count": 6 }
}
"@
}

function New-RecipeWallJson {
    param([string]$base)

  $baseId = $ModId + ':' + $base
  $resultId = $ModId + ':' + "${base}_wall"

    return @"
{
  "type": "minecraft:crafting_shaped",
  "pattern": [
    "XXX",
    "XXX"
  ],
  "key": {
    "X": { "item": "$baseId" }
  },
  "result": { "item": "$resultId", "count": 6 }
}
"@
}

foreach ($base in $Bases) {
    foreach ($kind in @('stairs', 'slab', 'wall')) {
        $blockstatePath = Join-Path $blockstatesDir "${base}_${kind}.json"

        $blockstateJson = switch ($kind) {
          'stairs' { New-StairsBlockstateJson $base }
          'slab' { New-SlabBlockstateJson $base }
          'wall' { New-WallBlockstateJson $base }
        }

        Write-JsonFile -Path $blockstatePath -Json $blockstateJson
    }

    # Block models
    Write-JsonFile -Path (Join-Path $modelsBlockDir "${base}_stairs.json") -Json (New-StairsModelJson $base 'normal')
    Write-JsonFile -Path (Join-Path $modelsBlockDir "${base}_stairs_inner.json") -Json (New-StairsModelJson $base 'inner')
    Write-JsonFile -Path (Join-Path $modelsBlockDir "${base}_stairs_outer.json") -Json (New-StairsModelJson $base 'outer')

    Write-JsonFile -Path (Join-Path $modelsBlockDir "${base}_slab.json") -Json (New-SlabModelJson $base 'bottom')
    Write-JsonFile -Path (Join-Path $modelsBlockDir "${base}_slab_top.json") -Json (New-SlabModelJson $base 'top')
    Write-JsonFile -Path (Join-Path $modelsBlockDir "${base}_slab_double.json") -Json (New-SlabDoubleModelJson $base)

    Write-JsonFile -Path (Join-Path $modelsBlockDir "${base}_wall_post.json") -Json (New-WallModelJson $base 'post')
    Write-JsonFile -Path (Join-Path $modelsBlockDir "${base}_wall_side.json") -Json (New-WallModelJson $base 'side')
    Write-JsonFile -Path (Join-Path $modelsBlockDir "${base}_wall_side_tall.json") -Json (New-WallModelJson $base 'side_tall')

    # Item models
    Write-JsonFile -Path (Join-Path $modelsItemDir "${base}_stairs.json") -Json (New-ItemBlockParentJson "${ModId}:block/${base}_stairs")
    Write-JsonFile -Path (Join-Path $modelsItemDir "${base}_slab.json") -Json (New-ItemBlockParentJson "${ModId}:block/${base}_slab")
    Write-JsonFile -Path (Join-Path $modelsItemDir "${base}_wall.json") -Json (New-ItemWallInventoryJson $base)

    # Loot tables
    Write-JsonFile -Path (Join-Path $lootBlocksDir "${base}_stairs.json") -Json (New-LootSimpleDropJson "${base}_stairs")
    Write-JsonFile -Path (Join-Path $lootBlocksDir "${base}_slab.json") -Json (New-LootSlabDropJson "${base}_slab")
    Write-JsonFile -Path (Join-Path $lootBlocksDir "${base}_wall.json") -Json (New-LootSimpleDropJson "${base}_wall")

    # Recipes
    Write-JsonFile -Path (Join-Path $recipesDir "${base}_stairs.json") -Json (New-RecipeStairsJson $base)
    Write-JsonFile -Path (Join-Path $recipesDir "${base}_slab.json") -Json (New-RecipeSlabJson $base)
    Write-JsonFile -Path (Join-Path $recipesDir "${base}_wall.json") -Json (New-RecipeWallJson $base)
}

Write-Host "Generated stone family blocks for: $($Bases -join ', ')"
