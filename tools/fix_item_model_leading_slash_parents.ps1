param(
    [string]$ItemModelsRoot = "src/main/resources/assets/kruemblegard/models/item",
    [string]$Namespace = "kruemblegard"
)

$ErrorActionPreference = 'Stop'

if (-not (Test-Path $ItemModelsRoot)) {
    Write-Host "ERROR: Item models root not found: $ItemModelsRoot"
    exit 1
}

$files = Get-ChildItem -Path $ItemModelsRoot -Recurse -File -Filter '*.json'

[int]$filesChanged = 0
[int]$replacements = 0

foreach ($f in $files) {
    $raw = Get-Content -Raw -Path $f.FullName

    # Fix generator bug that wrote parents like "/scarstone_slab".
    $updated = $raw -replace '"parent"\s*:\s*"/([^"]+)"', ('"parent": "' + $Namespace + ':block/$1"')

    if ($updated -ne $raw) {
        $localCount = ([regex]::Matches($raw, '"parent"\s*:\s*"/([^"]+)"')).Count
        $replacements += $localCount
        $filesChanged++
        Set-Content -Path $f.FullName -Value $updated -NoNewline
    }
}

Write-Host "Fixed leading-slash parents in item models. Files changed: $filesChanged; Replacements: $replacements"
