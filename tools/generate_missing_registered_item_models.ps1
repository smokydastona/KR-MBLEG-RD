# Generates missing item model JSON files for registered items (from ModItems.java).
# - If a blockstate exists for the id, generates a block-item model (prefers kruemblegard:block/<id>).
# - Otherwise generates item/generated with kruemblegard:item/<id>.
# Run from repo root:
#   powershell -NoProfile -ExecutionPolicy Bypass -File tools/generate_missing_registered_item_models.ps1

param(
    [switch]$WhatIf
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$root = Resolve-Path '.'
$modItemsPath = Join-Path $root 'src/main/java/com/kruemblegard/registry/ModItems.java'
$modelsItemDir = Join-Path $root 'src/main/resources/assets/kruemblegard/models/item'
$modelsBlockDir = Join-Path $root 'src/main/resources/assets/kruemblegard/models/block'
$blockstatesDir = Join-Path $root 'src/main/resources/assets/kruemblegard/blockstates'

if (-not (Test-Path -LiteralPath $modItemsPath)) {
    throw "Missing file: $modItemsPath"
}

$src = Get-Content -LiteralPath $modItemsPath -Raw

$ids = New-Object System.Collections.Generic.HashSet[string]
[regex]::Matches($src, 'ITEMS\.register\(\s*"([a-z0-9_]+)"', 'IgnoreCase') | ForEach-Object { $null = $ids.Add($_.Groups[1].Value) }
[regex]::Matches($src, 'registerBlockItem\(\s*"([a-z0-9_]+)"', 'IgnoreCase') | ForEach-Object { $null = $ids.Add($_.Groups[1].Value) }

if (-not (Test-Path -LiteralPath $modelsItemDir)) {
    if (-not $WhatIf) {
        New-Item -ItemType Directory -Path $modelsItemDir | Out-Null
    }
}

function Write-JsonFile([string]$path, [object]$obj) {
    $json = $obj | ConvertTo-Json -Depth 10
    if ($WhatIf) {
        Write-Host "[WhatIf] Would write $path"
        return
    }
    $parent = Split-Path -Parent $path
    if (-not (Test-Path -LiteralPath $parent)) {
        New-Item -ItemType Directory -Path $parent | Out-Null
    }
    Set-Content -LiteralPath $path -Value $json -Encoding UTF8
}

$created = 0
foreach ($id in ($ids | Sort-Object)) {
    $itemModelPath = Join-Path $modelsItemDir ($id + '.json')
    if (Test-Path -LiteralPath $itemModelPath) { continue }

    $blockstatePath = Join-Path $blockstatesDir ($id + '.json')
    $blockModelPath = Join-Path $modelsBlockDir ($id + '.json')

    if (Test-Path -LiteralPath $blockstatePath) {
        if (Test-Path -LiteralPath $blockModelPath) {
            Write-JsonFile $itemModelPath ([ordered]@{
                parent = "kruemblegard:block/$id"
            })
        }
        else {
            # Conservative fallback: render as a simple cube using the block texture.
            Write-JsonFile $itemModelPath ([ordered]@{
                parent = 'minecraft:block/cube_all'
                textures = [ordered]@{ all = "kruemblegard:block/$id" }
            })
        }
    }
    else {
        # Non-block item (or icon-only item). Assumes kruemblegard:item/<id>.
        # NOTE: menu_tab overrides this with a dedicated model already.
        if ($id -eq 'menu_tab') { continue }

        Write-JsonFile $itemModelPath ([ordered]@{
            parent = 'item/generated'
            textures = [ordered]@{ layer0 = "kruemblegard:item/$id" }
        })
    }

    $created++
}

Write-Host ("Generated item model JSONs: {0}" -f $created)
