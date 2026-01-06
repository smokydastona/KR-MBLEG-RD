param(
    [string]$ModId = 'kruemblegard',
    [switch]$WhatIf
)

$ErrorActionPreference = 'Stop'

$textureDir = "src/main/resources/assets/$ModId/textures/block"
$modelDirs = @(
    "src/main/resources/assets/$ModId/models/block",
    "src/main/resources/assets/$ModId/models/item"
)

if (-not (Test-Path $textureDir)) {
    throw "Texture directory not found: $textureDir"
}

$woodTypes = Get-ChildItem -File $textureDir -Filter '*_planks.png' |
    ForEach-Object { $_.BaseName -replace '_planks$','' } |
    Sort-Object -Unique

if ($woodTypes.Count -eq 0) {
    throw "No *_planks.png textures found under $textureDir"
}

$targets = @('stairs','slab','fence','fence_gate','button','pressure_plate')

$changedFiles = 0
$totalReplacements = 0

foreach ($modelDir in $modelDirs) {
    if (-not (Test-Path $modelDir)) { continue }

    Get-ChildItem -File $modelDir -Filter '*.json' | ForEach-Object {
        $path = $_.FullName
        $text = Get-Content $path -Raw
        $original = $text

        foreach ($wood in $woodTypes) {
            foreach ($t in $targets) {
                $from = "$($ModId):block/$wood`_$t"
                $to = "$($ModId):block/$wood`_planks"
                if ($text -like "*$from*") {
                    $text = $text.Replace($from, $to)
                }
            }
        }

        if ($text -ne $original) {
            $changedFiles++
            $totalReplacements += ([regex]::Matches($original, [regex]::Escape("$($ModId):block/")).Count - [regex]::Matches($text, [regex]::Escape("$($ModId):block/")).Count)

            if ($WhatIf) {
                Write-Output "WOULD UPDATE: $path"
            } else {
                Set-Content -Path $path -Value $text -NoNewline
                Write-Output "UPDATED: $path"
            }
        }
    }
}

if ($WhatIf) {
    Write-Output "Done (WhatIf). Would update $changedFiles files."
} else {
    Write-Output "Done. Updated $changedFiles files."
}
