param(
    [string]$BlockstatesRoot = "src/main/resources/assets/kruemblegard/blockstates",
    [string]$Namespace = "kruemblegard"
)

$ErrorActionPreference = 'Stop'

if (-not (Test-Path $BlockstatesRoot)) {
    Write-Host "ERROR: Blockstates root not found: $BlockstatesRoot"
    exit 1
}

$files = Get-ChildItem -Path $BlockstatesRoot -Recurse -File -Filter '*.json'

[int]$filesChanged = 0
[int]$replacements = 0

foreach ($f in $files) {
    $raw = Get-Content -Raw -Path $f.FullName

    # Fix generator bug that wrote absolute-ish model ids like "/attuned_stone_slab".
    # In blockstate JSON, model ids should be like "kruemblegard:block/attuned_stone_slab".
    $updated = $raw -replace '"model"\s*:\s*"/([^"]+)"', ('"model": "' + $Namespace + ':block/$1"')

    if ($updated -ne $raw) {
        $localCount = ([regex]::Matches($raw, '"model"\s*:\s*"/([^"]+)"')).Count
        $replacements += $localCount
        $filesChanged++
        Set-Content -Path $f.FullName -Value $updated -NoNewline
    }
}

Write-Host "Fixed leading-slash model ids in blockstates. Files changed: $filesChanged; Replacements: $replacements"
