# Audits item model JSONs for kruemblegard:item/* texture references that are missing PNGs.
# Run from repo root:
#   powershell -NoProfile -ExecutionPolicy Bypass -File tools/audit_missing_item_textures.ps1

param()

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$modelsDir = Join-Path (Resolve-Path '.') 'src/main/resources/assets/kruemblegard/models/item'
$texDir    = Join-Path (Resolve-Path '.') 'src/main/resources/assets/kruemblegard/textures/item'

$missing = New-Object System.Collections.Generic.HashSet[string]

Get-ChildItem -LiteralPath $modelsDir -Filter '*.json' -Recurse | ForEach-Object {
    $jsonText = Get-Content -LiteralPath $_.FullName -Raw
    if (-not $jsonText) { return }

    try {
        $model = $jsonText | ConvertFrom-Json
    }
    catch {
        return
    }

    $texturesProp = $model.PSObject.Properties.Match('textures') | Select-Object -First 1
    if ($null -eq $texturesProp) { return }

    $textures = $texturesProp.Value
    if ($null -eq $textures) { return }

    $textures.PSObject.Properties | ForEach-Object {
        $val = $_.Value
        if ($val -isnot [string]) { return }

        if ($val -match '^kruemblegard:item/(.+)$') {
            $pngPath = Join-Path $texDir ($Matches[1] + '.png')
            if (-not (Test-Path -LiteralPath $pngPath)) {
                $null = $missing.Add($pngPath)
            }
        }
    }
}

$missingList = New-Object string[] $missing.Count
$missing.CopyTo($missingList)
$missingList = @($missingList | Sort-Object)
Write-Host ("Missing item texture PNGs referenced by item models: {0}" -f $missing.Count)
$missingList | Select-Object -First 100
