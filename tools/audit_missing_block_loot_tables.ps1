# Audits which blocks are missing loot tables (based on blockstates present).
# Usage: powershell -NoProfile -ExecutionPolicy Bypass -File tools/audit_missing_block_loot_tables.ps1

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$repoRoot = Split-Path -Parent $PSScriptRoot
$blockstatesDir = Join-Path $repoRoot 'src/main/resources/assets/kruemblegard/blockstates'
$lootDir = Join-Path $repoRoot 'src/main/resources/data/kruemblegard/loot_tables/blocks'

if (-not (Test-Path $blockstatesDir)) { throw "Missing blockstates dir: $blockstatesDir" }
if (-not (Test-Path $lootDir)) { throw "Missing loot tables dir: $lootDir" }

$blockIds = Get-ChildItem -Path $blockstatesDir -Filter '*.json' -File | ForEach-Object { $_.BaseName }
$lootIds = Get-ChildItem -Path $lootDir -Filter '*.json' -File | ForEach-Object { $_.BaseName }

$lootSet = [System.Collections.Generic.HashSet[string]]::new([StringComparer]::OrdinalIgnoreCase)
foreach ($id in $lootIds) { [void]$lootSet.Add($id) }

$missing = [System.Collections.Generic.List[string]]::new()
foreach ($id in $blockIds) {
    if (-not $lootSet.Contains($id)) {
        $missing.Add($id)
    }
}

$missingSorted = $missing | Sort-Object
$missingCount = @($missingSorted).Count

Write-Host "Missing block loot tables (based on blockstates): $missingCount"
if ($missingCount -gt 0) {
    $missingSorted | ForEach-Object { Write-Host " - $_" }
}
