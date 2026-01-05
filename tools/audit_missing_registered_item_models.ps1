# Audits registered item IDs (from ModItems.java) for missing item model JSON files.
# Run from repo root:
#   powershell -NoProfile -ExecutionPolicy Bypass -File tools/audit_missing_registered_item_models.ps1

param()

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$root = Resolve-Path '.'
$modItemsPath = Join-Path $root 'src/main/java/com/kruemblegard/registry/ModItems.java'
$modelsDir = Join-Path $root 'src/main/resources/assets/kruemblegard/models/item'

if (-not (Test-Path -LiteralPath $modItemsPath)) {
    throw "Missing file: $modItemsPath"
}

$src = Get-Content -LiteralPath $modItemsPath -Raw

$ids = New-Object System.Collections.Generic.HashSet[string]

# ITEMS.register("id", ...)
[regex]::Matches($src, 'ITEMS\.register\(\s*"([a-z0-9_]+)"', 'IgnoreCase') | ForEach-Object {
    $null = $ids.Add($_.Groups[1].Value)
}

# registerBlockItem("id", ...)
[regex]::Matches($src, 'registerBlockItem\(\s*"([a-z0-9_]+)"', 'IgnoreCase') | ForEach-Object {
    $null = $ids.Add($_.Groups[1].Value)
}

$missing = @()
foreach ($id in ($ids | Sort-Object)) {
    $modelPath = Join-Path $modelsDir ($id + '.json')
    if (-not (Test-Path -LiteralPath $modelPath)) {
        $missing += $id
    }
}

Write-Host ("Registered items found: {0}" -f $ids.Count)
Write-Host ("Missing item model JSONs: {0}" -f $missing.Count)
$missing | Select-Object -First 200
