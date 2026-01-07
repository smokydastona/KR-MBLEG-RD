# Audits kruemblegard item models for likely texture mismatches.
#
# What it checks:
# 1) For item models that use the generated item parent, verifies layer0 is kruemblegard:item/<model_stem>.
# 2) Detects multiple item models referencing the same kruemblegard:item/<tex> texture path.
# 3) Optionally detects multiple different item textures that are byte-identical (same file hash).
#
# Run from repo root:
#   powershell -NoProfile -ExecutionPolicy Bypass -File tools/audit_item_model_texture_mismatches.ps1

param(
    [switch]$IncludeHashDuplicates,
    [int]$MaxList = 200,
    [string]$OutputPath
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$repoRoot  = Resolve-Path '.'
$modelsDir = Join-Path $repoRoot 'src/main/resources/assets/kruemblegard/models/item'
$itemTexDir = Join-Path $repoRoot 'src/main/resources/assets/kruemblegard/textures/item'

function Try-ReadJsonFile {
    param([string]$path)
    if (-not (Test-Path -LiteralPath $path)) { return $null }
    $jsonText = Get-Content -LiteralPath $path -Raw
    if (-not $jsonText) { return $null }
    try { return $jsonText | ConvertFrom-Json }
    catch { return $null }
}

function Normalize-Parent {
    param([string]$parent)
    if (-not $parent) { return '' }
    # Normalize common forms.
    if ($parent -eq 'minecraft:item/generated') { return 'item/generated' }
    if ($parent -eq 'item/generated') { return 'item/generated' }
    if ($parent -eq 'minecraft:item/handheld') { return 'item/handheld' }
    if ($parent -eq 'item/handheld') { return 'item/handheld' }
    return $parent
}

$items = @()

Get-ChildItem -LiteralPath $modelsDir -Filter '*.json' -Recurse | ForEach-Object {
    $modelPath = $_.FullName
    $stem = [IO.Path]::GetFileNameWithoutExtension($_.Name)

    $model = Try-ReadJsonFile -path $modelPath
    if ($null -eq $model) {
        $items += [pscustomobject]@{
            Model = $stem
            ModelPath = $modelPath
            Parent = ''
            Layer0 = ''
            ExpectedLayer0 = ''
            Layer0MatchesExpected = $false
            TexturePngPath = ''
            TextureExists = $false
            TextureBytes = 0
            TextureHash = ''
            Notes = 'Invalid JSON'
        }
        return
    }

    $parent = ''
    $parentProp = $model.PSObject.Properties.Match('parent') | Select-Object -First 1
    if ($null -ne $parentProp -and ($parentProp.Value -is [string])) {
        $parent = Normalize-Parent -parent $parentProp.Value
    }

    $layer0 = ''
    $texturesProp = $model.PSObject.Properties.Match('textures') | Select-Object -First 1
    if ($null -ne $texturesProp -and $null -ne $texturesProp.Value) {
        $texObj = $texturesProp.Value
        $layer0Prop = $texObj.PSObject.Properties.Match('layer0') | Select-Object -First 1
        if ($null -ne $layer0Prop -and ($layer0Prop.Value -is [string])) {
            $layer0 = $layer0Prop.Value
        }
    }

    $expectedLayer0 = ''
    $layer0Matches = $false
    $notes = @()

    if ($parent -eq 'item/generated' -or $parent -eq 'item/handheld') {
        $expectedLayer0 = "kruemblegard:item/$stem"
        if ($layer0 -eq $expectedLayer0) {
            $layer0Matches = $true
        }
        else {
            $notes += 'Layer0 differs from convention'
        }
    }

    $pngPath = ''
    $exists = $false
    $bytes = 0
    $hash = ''

    if ($layer0 -match '^kruemblegard:item/(.+)$') {
        $pngPath = Join-Path $itemTexDir ($Matches[1] + '.png')
        if (Test-Path -LiteralPath $pngPath) {
            $exists = $true
            $bytes = (Get-Item -LiteralPath $pngPath).Length
            if ($IncludeHashDuplicates) {
                $hash = (Get-FileHash -Algorithm SHA256 -LiteralPath $pngPath).Hash
            }
        }
        else {
            $notes += 'Missing item texture PNG'
        }
    }

    if (($parent -eq 'item/generated' -or $parent -eq 'item/handheld') -and -not $layer0) {
        $notes += 'Missing textures.layer0'
    }

    $items += [pscustomobject]@{
        Model = $stem
        ModelPath = $modelPath
        Parent = $parent
        Layer0 = $layer0
        ExpectedLayer0 = $expectedLayer0
        Layer0MatchesExpected = $layer0Matches
        TexturePngPath = $pngPath
        TextureExists = $exists
        TextureBytes = $bytes
        TextureHash = $hash
        Notes = ($notes -join '; ')
    }
}

$mismatch = @($items | Where-Object { $_.Notes -match 'Layer0 differs from convention' -or $_.Notes -match 'Missing textures\.layer0' })
$missingPng = @($items | Where-Object { $_.Notes -match 'Missing item texture PNG' })

$refDupes = @(
    $items |
    Where-Object { $_.Layer0 -match '^kruemblegard:item/' } |
    Group-Object -Property Layer0 |
    Where-Object { $_.Count -gt 1 } |
    Sort-Object Count -Descending
)

$hashDupes = @()
if ($IncludeHashDuplicates) {
    $hashDupes = @(
        $items |
        Where-Object { $_.TextureHash } |
        Group-Object -Property TextureHash |
        Where-Object { $_.Count -gt 1 } |
        Sort-Object Count -Descending
    )
}

Write-Host "Item model count: $($items.Count)"
Write-Host "Convention mismatches (generated/handheld layer0 != expected): $($mismatch.Count)"
Write-Host "Missing referenced item PNGs: $($missingPng.Count)"
Write-Host "Duplicate layer0 references: $($refDupes.Count)"
if ($IncludeHashDuplicates) {
    Write-Host "Duplicate PNG file hashes (byte-identical): $($hashDupes.Count)"
}

if ($mismatch.Count -gt 0) {
    Write-Host "\n--- Convention mismatches (showing up to $MaxList) ---"
    $mismatch | Select-Object -First $MaxList Model, Parent, Layer0, ExpectedLayer0, Notes | Format-Table -AutoSize
}

if ($missingPng.Count -gt 0) {
    Write-Host "\n--- Missing PNGs (showing up to $MaxList) ---"
    $missingPng | Select-Object -First $MaxList Model, Layer0, TexturePngPath, Notes | Format-Table -AutoSize
}

if ($refDupes.Count -gt 0) {
    Write-Host "\n--- Duplicate layer0 references (showing up to $MaxList) ---"
    $refDupes | Select-Object -First $MaxList | ForEach-Object {
        $layer0 = $_.Name
        $models = ($_.Group | Select-Object -ExpandProperty Model | Sort-Object) -join ', '
        [pscustomobject]@{ Layer0 = $layer0; Count = $_.Count; Models = $models }
    } | Format-Table -AutoSize
}

if ($IncludeHashDuplicates -and $hashDupes.Count -gt 0) {
    Write-Host "\n--- Duplicate PNG hashes (showing up to $MaxList) ---"
    $hashDupes | Select-Object -First $MaxList | ForEach-Object {
        $hash = $_.Name
        $group = $_.Group
        $paths = ($group | Select-Object -ExpandProperty TexturePngPath | Sort-Object -Unique) -join ', '
        $models = ($group | Select-Object -ExpandProperty Model | Sort-Object -Unique) -join ', '
        [pscustomobject]@{ Count = $_.Count; TexturePaths = $paths; Models = $models }
    } | Format-Table -AutoSize
}

if ($OutputPath) {
    $out = Resolve-Path (Split-Path -Parent $OutputPath) -ErrorAction SilentlyContinue
    if (-not $out) {
        New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputPath) | Out-Null
    }
    $items | Export-Csv -NoTypeInformation -Path $OutputPath
    Write-Host "\nWrote CSV: $OutputPath"
}
