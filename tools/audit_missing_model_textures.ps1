# Audits block+item model JSONs for kruemblegard:block/* and kruemblegard:item/* texture references that are missing PNGs.
# Run from repo root:
#   powershell -NoProfile -ExecutionPolicy Bypass -File tools/audit_missing_model_textures.ps1

param(
    [switch]$IncludeParents
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$repoRoot = Resolve-Path '.'

$blockModelsDir = Join-Path $repoRoot 'src/main/resources/assets/kruemblegard/models/block'
$itemModelsDir  = Join-Path $repoRoot 'src/main/resources/assets/kruemblegard/models/item'

$blockTexDir = Join-Path $repoRoot 'src/main/resources/assets/kruemblegard/textures/block'
$itemTexDir  = Join-Path $repoRoot 'src/main/resources/assets/kruemblegard/textures/item'

$missing = New-Object System.Collections.Generic.HashSet[string]

function Try-ReadJsonFile {
    param([string]$path)
    if (-not (Test-Path -LiteralPath $path)) { return $null }
    $jsonText = Get-Content -LiteralPath $path -Raw
    if (-not $jsonText) { return $null }
    try { return $jsonText | ConvertFrom-Json }
    catch { return $null }
}

function Resolve-ParentModelPath {
    param(
        [string]$parent,
        [ValidateSet('block','item')][string]$kind
    )

    # We only resolve kruemblegard namespace parents.
    if (-not $parent) { return $null }
    if ($parent -match '^kruemblegard:(block|item)/(.+)$') {
        $parentKind = $Matches[1]
        $stem = $Matches[2]
        $dir = if ($parentKind -eq 'block') { $blockModelsDir } else { $itemModelsDir }
        return Join-Path $dir ($stem + '.json')
    }

    return $null
}

function Audit-Model {
    param(
        [string]$modelPath,
        [ValidateSet('block','item')][string]$kind,
        [System.Collections.Generic.HashSet[string]]$visited
    )

    if (-not $modelPath) { return }
    if ($visited.Contains($modelPath)) { return }
    $null = $visited.Add($modelPath)

    $model = Try-ReadJsonFile -path $modelPath
    if ($null -eq $model) { return }

    $texturesProp = $model.PSObject.Properties.Match('textures') | Select-Object -First 1
    if ($null -ne $texturesProp) {
        $textures = $texturesProp.Value
        if ($null -ne $textures) {
            $textures.PSObject.Properties | ForEach-Object {
                $val = $_.Value
                if ($val -isnot [string]) { return }

                if ($val -match '^kruemblegard:block/(.+)$') {
                    $pngPath = Join-Path $blockTexDir ($Matches[1] + '.png')
                    if (-not (Test-Path -LiteralPath $pngPath)) {
                        $null = $missing.Add($pngPath)
                    }
                }
                elseif ($val -match '^kruemblegard:item/(.+)$') {
                    $pngPath = Join-Path $itemTexDir ($Matches[1] + '.png')
                    if (-not (Test-Path -LiteralPath $pngPath)) {
                        $null = $missing.Add($pngPath)
                    }
                }
            }
        }
    }

    if ($IncludeParents) {
        $parentProp = $model.PSObject.Properties.Match('parent') | Select-Object -First 1
        if ($null -ne $parentProp -and ($parentProp.Value -is [string])) {
            $parentPath = Resolve-ParentModelPath -parent $parentProp.Value -kind $kind
            if ($null -ne $parentPath) {
                Audit-Model -modelPath $parentPath -kind $kind -visited $visited
            }
        }
    }
}

$visited = New-Object System.Collections.Generic.HashSet[string]

Get-ChildItem -LiteralPath $blockModelsDir -Filter '*.json' -Recurse | ForEach-Object {
    Audit-Model -modelPath $_.FullName -kind 'block' -visited $visited
}

Get-ChildItem -LiteralPath $itemModelsDir -Filter '*.json' -Recurse | ForEach-Object {
    Audit-Model -modelPath $_.FullName -kind 'item' -visited $visited
}

$missingList = New-Object string[] $missing.Count
$missing.CopyTo($missingList)
$missingList = @($missingList | Sort-Object)

Write-Host ("Missing texture PNGs referenced by kruemblegard models: {0}" -f $missing.Count)
$missingList | Select-Object -First 200
