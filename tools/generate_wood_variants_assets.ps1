# Generates blockstate/model/item-model JSON assets for wood/stripped variants.
# Run from repo root:
#   powershell -NoProfile -ExecutionPolicy Bypass -File tools/generate_wood_variants_assets.ps1

param()

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
    if (Test-Path -LiteralPath $path) { return }
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

function New-CubeColumnModelJson([string]$id) {
    $model = @{
        parent = 'minecraft:block/cube_column'
        textures = @{
            end  = "kruemblegard:block/$id"
            side = "kruemblegard:block/$id"
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
    $ids = @(
        "${w}_wood",
        "stripped_${w}_log",
        "stripped_${w}_wood"
    )

    foreach ($id in $ids) {
        Write-IfMissing (Join-Path $blockstatesDir "$id.json") (New-AxisBlockstateJson $id)
        Write-IfMissing (Join-Path $modelsBlockDir "$id.json") (New-CubeColumnModelJson $id)
        Write-IfMissing (Join-Path $modelsItemDir "$id.json") (New-ItemModelJson $id)
    }
}

Write-Host 'Generated missing JSON assets for wood/stripped variants (skips existing files).'
