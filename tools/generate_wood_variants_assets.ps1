# Generates blockstate/model/item-model JSON assets for wood/stripped variants.
#
# Texture rules assumed by this generator:
# - <family>_wood uses the SAME bark texture on all faces (reuses <family>_log texture)
# - stripped_<family>_wood uses the SAME stripped bark texture on all faces (reuses stripped_<family>_log texture)
# - stripped_<family>_log reuses the unstripped end-grain (<family>_log_top)
#
# Run from repo root:
#   powershell -NoProfile -ExecutionPolicy Bypass -File tools/generate_wood_variants_assets.ps1
#   powershell -NoProfile -ExecutionPolicy Bypass -File tools/generate_wood_variants_assets.ps1 -Force

param(
    [switch]$Force
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$root = (Resolve-Path '.')
$assetsRoot = Join-Path $root 'src/main/resources/assets/kruemblegard'
$blockstatesDir = Join-Path $assetsRoot 'blockstates'
$modelsBlockDir = Join-Path $assetsRoot 'models/block'
$modelsItemDir  = Join-Path $assetsRoot 'models/item'

function Ensure-Dir([string]$path) {
    if (-not (Test-Path -LiteralPath $path)) {
        New-Item -ItemType Directory -Path $path | Out-Null
    }
}

function Write-IfMissing([string]$path, [string]$content) {
    if (Test-Path -LiteralPath $path) {
        if (-not $Force) { return }

        $existing = Get-Content -LiteralPath $path -Raw
        if ($existing -eq $content) { return }
    }
    $parent = Split-Path -Parent $path
    Ensure-Dir $parent
    Set-Content -LiteralPath $path -Value $content -Encoding UTF8
}

function New-AxisBlockstateJson([string]$id) {
    $variants = @{
        'axis=y' = @{ model = "kruemblegard:block/$id" }
        'axis=x' = @{ model = "kruemblegard:block/$id"; x = 90; y = 90 }
        'axis=z' = @{ model = "kruemblegard:block/$id"; x = 90 }
    }

    return (@{ variants = $variants } | ConvertTo-Json -Depth 6)
}

function New-CubeColumnModelJson([string]$sideTextureId, [string]$endTextureId) {
    $model = @{
        parent = 'minecraft:block/cube_column'
        textures = @{
            end  = "kruemblegard:block/$endTextureId"
            side = "kruemblegard:block/$sideTextureId"
        }
    }

    return ($model | ConvertTo-Json -Depth 6)
}

function New-ItemModelJson([string]$id) {
    return (@{ parent = "kruemblegard:block/$id" } | ConvertTo-Json -Depth 3)
}

$woodFamilies = @(
    'wayroot',
    'fallbark',
    'echowood',
    'cairn_tree',
    'wayglass',
    'shardbark_pine',
    'hollowway_tree',
    'driftwillow',
    'monument_oak',
    'waytorch_tree',
    'faultwood',
    'ashbloom',
    'glimmerpine',
    'driftwood'
)

foreach ($w in $woodFamilies) {
    $entries = @(
        # bark blocks: reuse log side texture on all faces
        @{ id = "${w}_wood";           side = "${w}_log";           end = "${w}_log" },
        @{ id = "stripped_${w}_wood";  side = "stripped_${w}_log";  end = "stripped_${w}_log" },

        # stripped log: reuse unstripped end-grain (log_top)
        @{ id = "stripped_${w}_log";   side = "stripped_${w}_log";  end = "${w}_log_top" }
    )

    foreach ($e in $entries) {
        $id = $e.id
        Write-IfMissing (Join-Path $blockstatesDir "$id.json") (New-AxisBlockstateJson $id)
        Write-IfMissing (Join-Path $modelsBlockDir "$id.json") (New-CubeColumnModelJson $e.side $e.end)
        Write-IfMissing (Join-Path $modelsItemDir "$id.json") (New-ItemModelJson $id)
    }
}

if ($Force) {
    Write-Host 'Generated JSON assets for wood/stripped variants (overwrote when content differed).'
} else {
    Write-Host 'Generated missing JSON assets for wood/stripped variants (skips existing files).'
}
