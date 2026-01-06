param(
    [string]$ModId = 'kruemblegard',
    [switch]$Delete,
    [switch]$WhatIf,
    [int]$Show = 30
)

$ErrorActionPreference = 'Stop'

$assetsRoot = "src/main/resources/assets/$ModId"
$modelRoots = @(
    "$assetsRoot/models",
    "$assetsRoot/blockstates"
)

$textureRoot = "$assetsRoot/textures/block"
if (-not (Test-Path $textureRoot)) {
    throw "Texture root not found: $textureRoot"
}

$targets = '(stairs|slab|fence|fence_gate|button|pressure_plate)'

# Collect referenced block texture names (without extension)
$referenced = New-Object System.Collections.Generic.HashSet[string]

$pattern = "$ModId:block/([a-z0-9_./-]+)"

foreach ($root in $modelRoots) {
    if (-not (Test-Path $root)) { continue }

    $files = Get-ChildItem -Recurse -File $root -Filter '*.json'
    if ($files.Count -eq 0) { continue }

    foreach ($match in (Select-String -Path $files.FullName -Pattern $pattern -AllMatches).Matches) {
        [void]$referenced.Add($match.Groups[1].Value)
    }
}

# Candidate textures are specific derived wood-family pngs.
$candidates = Get-ChildItem -Recurse -File $textureRoot -Filter '*.png' |
    Where-Object { $_.BaseName -match "_$targets$" }

$unused = $candidates |
    Where-Object { -not $referenced.Contains($_.BaseName) } |
    Sort-Object FullName

Write-Output ("UNUSED_COUNT=" + $unused.Count)
if ($Show -gt 0) {
    Write-Output ("FIRST_${Show}:")
    $unused | Select-Object -First $Show -ExpandProperty FullName
}

if ($Delete) {
    if ($WhatIf) {
        $unused | ForEach-Object { Write-Output "WOULD_DELETE: $($_.FullName)" }
        Write-Output "Done (WhatIf)."
        return
    }

    foreach ($f in $unused) {
        Remove-Item -LiteralPath $f.FullName -Force
        Write-Output "DELETED: $($f.FullName)"
    }

    Write-Output "Done."
}
