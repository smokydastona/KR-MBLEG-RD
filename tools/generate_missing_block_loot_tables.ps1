# Generates missing block loot tables as simple "drop self" tables.
# Usage: powershell -NoProfile -ExecutionPolicy Bypass -File tools/generate_missing_block_loot_tables.ps1

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

if ($missingCount -eq 0) {
    Write-Host "No missing loot tables detected."
    exit 0
}

function New-DropSelfLootTableJson([string]$blockId) {
    $name = "kruemblegard:$blockId"

    # Note: keep schema minimal; most blocks should just drop themselves.
    return @{
        type = 'minecraft:block'
        pools = @(
            @{
                rolls = 1
                entries = @(
                    @{
                        type = 'minecraft:item'
                        name = $name
                    }
                )
                conditions = @(
                    @{
                        condition = 'minecraft:survives_explosion'
                    }
                )
            }
        )
    }
}

$created = 0
foreach ($id in $missingSorted) {
    $path = Join-Path $lootDir ("$id.json")
    if (Test-Path $path) {
        continue
    }

    $obj = New-DropSelfLootTableJson -blockId $id
    $json = $obj | ConvertTo-Json -Depth 10
    # Normalize line endings and indent consistency.
    $json = $json -replace "\r?\n", "`n"

    Set-Content -Path $path -Value $json -Encoding UTF8
    $created++
}

Write-Host "Created missing loot tables: $created"
Write-Host "Re-run tools/audit_missing_block_loot_tables.ps1 to verify."
