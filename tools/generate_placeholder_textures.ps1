# Generates simple placeholder PNG textures for Krümblegård flora.
# Run from repo root:
#   powershell -NoProfile -ExecutionPolicy Bypass -File tools/generate_placeholder_textures.ps1

[Diagnostics.CodeAnalysis.SuppressMessageAttribute('PSUseApprovedVerbs', '', Justification = 'Local helper functions in this script are not exported cmdlets; keep names concise.')]
param()

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

Add-Type -AssemblyName System.Drawing

function New-Texture16([string]$filePath, [scriptblock]$draw) {
    if (Test-Path -LiteralPath $filePath) {
        return
    }

    $parent = Split-Path -Parent $filePath
    if (-not (Test-Path -LiteralPath $parent)) {
        New-Item -ItemType Directory -Path $parent | Out-Null
    }

    $bmp = New-Object System.Drawing.Bitmap 16, 16, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    try {
        for ($y = 0; $y -lt 16; $y++) {
            for ($x = 0; $x -lt 16; $x++) {
                $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(0, 0, 0, 0))
            }
        }

        & $draw $bmp

        $bmp.Save($filePath, [System.Drawing.Imaging.ImageFormat]::Png)
    }
    finally {
        $bmp.Dispose()
    }
}

function P([System.Drawing.Bitmap]$bmp, [int]$x, [int]$y, [System.Drawing.Color]$c) {
    if ($x -ge 0 -and $x -lt 16 -and $y -ge 0 -and $y -lt 16) {
        $bmp.SetPixel($x, $y, $c)
    }
}

function Dot([System.Drawing.Bitmap]$bmp, [int]$x, [int]$y, [System.Drawing.Color]$c) {
    P $bmp $x $y $c
    P $bmp ($x+1) $y $c
    P $bmp $x ($y+1) $c
    P $bmp ($x+1) ($y+1) $c
}

function Get-Seed([string]$s) {
    $bytes = [System.Text.Encoding]::UTF8.GetBytes($s)
    $sha = [System.Security.Cryptography.SHA256]::Create()
    try {
        $hash = $sha.ComputeHash($bytes)
        return [BitConverter]::ToInt32($hash, 0)
    }
    finally {
        $sha.Dispose()
    }
}

function New-HashedDots([System.Drawing.Bitmap]$bmp, [string]$key, [System.Drawing.Color]$a, [System.Drawing.Color]$b) {
    $rng = New-Object System.Random (Get-Seed $key)

    # subtle speckle so every placeholder is visually distinct
    for ($i = 0; $i -lt 22; $i++) {
        $x = $rng.Next(0, 15)
        $y = $rng.Next(0, 15)
        $c = if (($i % 2) -eq 0) { $a } else { $b }
        P $bmp $x $y $c
    }
}

function New-PlantTexture([string]$fileName, [string]$key, [System.Drawing.Color]$primary, [System.Drawing.Color]$accent) {
    $filePath = Join-Path $texBlock $fileName
    New-Texture16 $filePath {
        param($bmp)

        # stem
        for ($y = 5; $y -le 14; $y++) {
            P $bmp 8 $y $stem
        }

        # petals / cap-ish cluster
        Dot $bmp 7 3 $primary
        Dot $bmp 9 3 $primary
        Dot $bmp 8 4 $primary
        P $bmp 8 5 $accent

        New-HashedDots $bmp $key $primary $accent
    }
}

function New-FungusTexture([string]$fileName, [string]$key, [System.Drawing.Color]$capColor, [System.Drawing.Color]$capShade) {
    $filePath = Join-Path $texBlock $fileName
    New-Texture16 $filePath {
        param($bmp)

        # stalk
        for ($y = 8; $y -le 14; $y++) { P $bmp 8 $y ([System.Drawing.Color]::FromArgb(255, 235, 230, 220)) }

        # cap
        for ($x = 5; $x -le 11; $x++) { P $bmp $x 6 $capColor }
        for ($x = 4; $x -le 12; $x++) { P $bmp $x 7 $capColor }
        for ($x = 5; $x -le 11; $x++) { P $bmp $x 8 $capShade }

        New-HashedDots $bmp $key $capColor $capShade
    }
}

function New-VineTexture([string]$fileName, [string]$key, [System.Drawing.Color]$v1, [System.Drawing.Color]$v2) {
    $filePath = Join-Path $texBlock $fileName
    New-Texture16 $filePath {
        param($bmp)

        for ($y = 2; $y -le 14; $y++) {
            $x = 8 + [int]([Math]::Round([Math]::Sin(($y + 1) / 2.0)))
            P $bmp $x $y $v1
            if ($y % 3 -eq 0) {
                P $bmp ($x-1) $y $v2
                P $bmp ($x+1) $y $v2
            }
        }

        New-HashedDots $bmp $key $v1 $v2
    }
}

function New-TreeLogTexture([string]$fileName, [string]$key, [System.Drawing.Color]$barkA, [System.Drawing.Color]$barkB, [System.Drawing.Color]$ring) {
    $filePath = Join-Path $texBlock $fileName
    New-Texture16 $filePath {
        param($bmp)

        # vertical bark stripes
        for ($x = 0; $x -lt 16; $x++) {
            $c = if (($x % 2) -eq 0) { $barkA } else { $barkB }
            for ($y = 0; $y -lt 16; $y++) {
                P $bmp $x $y $c
            }
        }

        # small ring accent
        Dot $bmp 7 7 $ring
        New-HashedDots $bmp $key $barkA $ring
    }
}

function New-TreePlanksTexture([string]$fileName, [string]$key, [System.Drawing.Color]$pA, [System.Drawing.Color]$pB) {
    $filePath = Join-Path $texBlock $fileName
    New-Texture16 $filePath {
        param($bmp)

        # horizontal plank bands
        for ($y = 0; $y -lt 16; $y++) {
            $c = if (($y % 4) -lt 2) { $pA } else { $pB }
            for ($x = 0; $x -lt 16; $x++) {
                P $bmp $x $y $c
            }
        }

        New-HashedDots $bmp $key $pA $pB
    }
}

function New-TreeLeavesTexture([string]$fileName, [string]$key, [System.Drawing.Color]$lA, [System.Drawing.Color]$lB) {
    $filePath = Join-Path $texBlock $fileName
    New-Texture16 $filePath {
        param($bmp)

        for ($y = 0; $y -lt 16; $y++) {
            for ($x = 0; $x -lt 16; $x++) {
                if ((($x + $y) % 2) -eq 0) { P $bmp $x $y $lA } else { P $bmp $x $y $lB }
            }
        }

        New-HashedDots $bmp $key $lA $lB
    }
}

function New-TreeSaplingTexture([string]$fileName, [string]$key, [System.Drawing.Color]$lA, [System.Drawing.Color]$accent) {
    $filePath = Join-Path $texBlock $fileName
    New-Texture16 $filePath {
        param($bmp)

        # tiny sprout
        P $bmp 8 12 $stem
        P $bmp 8 11 $stem
        Dot $bmp 7 9 $lA
        Dot $bmp 9 9 $lA
        P $bmp 8 10 $accent

        New-HashedDots $bmp $key $lA $accent
    }
}

$root = (Resolve-Path '.')
$texBlock = Join-Path $root 'src/main/resources/assets/kruemblegard/textures/block'
$texItem  = Join-Path $root 'src/main/resources/assets/kruemblegard/textures/item'

# Palette
$glowA = [System.Drawing.Color]::FromArgb(220, 140, 255, 250)
$glowB = [System.Drawing.Color]::FromArgb(200, 60, 220, 220)
$stem  = [System.Drawing.Color]::FromArgb(255, 40, 70, 60)
$vine  = [System.Drawing.Color]::FromArgb(255, 35, 85, 40)
$vine2 = [System.Drawing.Color]::FromArgb(255, 20, 55, 28)
$cap   = [System.Drawing.Color]::FromArgb(255, 150, 60, 170)
$cap2  = [System.Drawing.Color]::FromArgb(255, 90, 25, 120)
$leaf  = [System.Drawing.Color]::FromArgb(255, 40, 110, 60)
$leaf2 = [System.Drawing.Color]::FromArgb(255, 25, 75, 40)
$berrySoul = [System.Drawing.Color]::FromArgb(255, 120, 210, 255)
$berryGhoul = [System.Drawing.Color]::FromArgb(255, 180, 90, 220)
$seed  = [System.Drawing.Color]::FromArgb(255, 190, 160, 95)
$petal = [System.Drawing.Color]::FromArgb(255, 180, 250, 200)
$petal2 = [System.Drawing.Color]::FromArgb(255, 90, 200, 140)

# --- Blocks ---
New-Texture16 (Join-Path $texBlock 'wispstalk.png') {
    param($bmp)

    # stem
    for ($y = 4; $y -le 14; $y++) {
        P $bmp 8 $y $stem
        if ($y -ge 7 -and ($y % 2 -eq 0)) { P $bmp 7 $y $stem }
    }

    # glow bulbs
    Dot $bmp 7 2 $glowA
    Dot $bmp 9 3 $glowB
    Dot $bmp 6 5 $glowB
    Dot $bmp 9 6 $glowA
}

New-Texture16 (Join-Path $texBlock 'gravevine.png') {
    param($bmp)

    # twisting vine
    for ($y = 2; $y -le 14; $y++) {
        $x = 8 + [int]([Math]::Round([Math]::Sin($y / 2.0)))
        P $bmp $x $y $vine
        if ($y % 3 -eq 0) {
            P $bmp ($x-1) $y $vine2
            P $bmp ($x+1) $y $vine2
        }
    }

    # small bone-ish accents
    P $bmp 6 6 ([System.Drawing.Color]::FromArgb(255, 200, 200, 200))
    P $bmp 10 9 ([System.Drawing.Color]::FromArgb(255, 200, 200, 200))
}

New-Texture16 (Join-Path $texBlock 'echocap.png') {
    param($bmp)

    # stalk
    for ($y = 8; $y -le 14; $y++) { P $bmp 8 $y ([System.Drawing.Color]::FromArgb(255, 235, 230, 220)) }

    # cap
    for ($x = 5; $x -le 11; $x++) { P $bmp $x 6 $cap }
    for ($x = 4; $x -le 12; $x++) { P $bmp $x 7 $cap }
    for ($x = 5; $x -le 11; $x++) { P $bmp $x 8 $cap2 }

    # subtle "note" specks
    P $bmp 6 5 $glowB
    P $bmp 10 5 $glowB
}

$runebloomColors = @(
    [System.Drawing.Color]::FromArgb(255, 120, 200, 255),
    [System.Drawing.Color]::FromArgb(255, 200, 120, 255),
    [System.Drawing.Color]::FromArgb(255, 255, 180, 90),
    [System.Drawing.Color]::FromArgb(255, 255, 120, 160),
    [System.Drawing.Color]::FromArgb(255, 130, 255, 170),
    [System.Drawing.Color]::FromArgb(255, 240, 240, 140)
)

for ($i = 0; $i -lt 6; $i++) {
    $c = $runebloomColors[$i]
    $file = Join-Path $texBlock ("runebloom_{0}.png" -f $i)

    New-Texture16 $file {
        param($bmp)

        # stem
        for ($y = 6; $y -le 14; $y++) { P $bmp 8 $y $stem }

        # petals
        Dot $bmp 7 3 $c
        Dot $bmp 9 3 $c
        Dot $bmp 8 4 $c

        # center
        P $bmp 8 5 ([System.Drawing.Color]::FromArgb(255, 35, 25, 15))
    }
}

for ($stage = 0; $stage -le 3; $stage++) {
    $soulFile = Join-Path $texBlock ("soulberry_shrub_stage{0}.png" -f $stage)
    $ghoulFile = Join-Path $texBlock ("ghoulberry_shrub_stage{0}.png" -f $stage)

    New-Texture16 $soulFile {
        param($bmp)

        # leaf mass
        for ($y = 6; $y -le 14; $y++) {
            for ($x = 5; $x -le 11; $x++) {
                if ((($x + $y) % 2) -eq 0) { P $bmp $x $y $leaf } else { P $bmp $x $y $leaf2 }
            }
        }

        # berries by stage
        if ($stage -ge 1) { Dot $bmp 6 10 $berrySoul }
        if ($stage -ge 2) { Dot $bmp 9 12 $berrySoul }
        if ($stage -ge 3) { Dot $bmp 8 9 $berrySoul }
    }

    New-Texture16 $ghoulFile {
        param($bmp)

        # sickly leaf mass
        $sick = [System.Drawing.Color]::FromArgb(255, 80, 130, 70)
        $sick2 = [System.Drawing.Color]::FromArgb(255, 55, 95, 55)

        for ($y = 6; $y -le 14; $y++) {
            for ($x = 5; $x -le 11; $x++) {
                if ((($x + $y) % 2) -eq 0) { P $bmp $x $y $sick } else { P $bmp $x $y $sick2 }
            }
        }

        # ghoul berries by stage
        if ($stage -ge 1) { Dot $bmp 6 10 $berryGhoul }
        if ($stage -ge 2) { Dot $bmp 9 12 $berryGhoul }
        if ($stage -ge 3) { Dot $bmp 8 9 $berryGhoul }
    }
}

# --- Items ---
New-Texture16 (Join-Path $texItem 'remnant_seeds.png') {
    param($bmp)

    Dot $bmp 6 10 $seed
    Dot $bmp 9 9 $seed
    P $bmp 8 12 ([System.Drawing.Color]::FromArgb(255, 120, 90, 45))
}

New-Texture16 (Join-Path $texItem 'rune_petals.png') {
    param($bmp)

    Dot $bmp 7 6 $petal
    Dot $bmp 9 6 $petal
    Dot $bmp 8 8 $petal2
    P $bmp 8 10 $petal2
}

New-Texture16 (Join-Path $texItem 'soulberries.png') {
    param($bmp)

    Dot $bmp 6 10 $berrySoul
    Dot $bmp 9 9 $berrySoul
    Dot $bmp 8 12 $berrySoul
    P $bmp 8 8 $leaf2
}

New-Texture16 (Join-Path $texItem 'ghoulberries.png') {
    param($bmp)

    Dot $bmp 6 10 $berryGhoul
    Dot $bmp 9 9 $berryGhoul
    Dot $bmp 8 12 $berryGhoul
    P $bmp 8 8 ([System.Drawing.Color]::FromArgb(255, 55, 95, 55))
}

Write-Host 'Generated placeholder flora textures.'
Write-Host "Block textures: $texBlock"
Write-Host "Item textures:  $texItem"

# --- Planned flora & trees (concept placeholders) ---

$plannedTrees = @(
    'wayroot',
    'fallbark',
    'echowood',
    'cairn_tree',
    'wayglass',
    'shardbark_pine',
    'hollowway_tree',
    'driftwillow',
    'monument_oak',
    'waytorch_tree',
    'faultwood',
    'ashbloom',
    'glimmerpine',
    'driftwood'
)

$plannedPlants = @(
    # medium plants / shrubs
    'pathreed',
    'faultgrass',
    'driftbloom',
    'cairn_moss',
    'waylily',
    # fungi & weird growths
    'griefcap',
    'static_fungus',
    'wayrot_fungus',
    'echo_puff',
    # small / utility
    'ruin_thistle',
    'wayseed_cluster',
    'void_lichen',
    'transit_fern',
    # creeping / dangerous
    'misstep_vine',
    'waybind_creeper',
    # specialized biome plants
    'milestone_grass',
    'runedrift_reed',
    'wayscar_ivy',
    'ashpetal',
    'transit_bloom',
    'cairnroot',
    # fungi & corrupted growth
    'waypoint_mold',
    'black_echo_fungus',
    'wayburn_fungus',
    'memory_rot',
    # dangerous / interactive
    'falsepath_thorns',
    'sliproot',
    'waygrasp_vine',
    'voidcap_briar',
    # small flavor plants
    'dustpetal',
    'rune_sprouts',
    'waythread',
    'gravemint',
    'fallseed_pods',
    # special
    'reverse_portal_spores'
)

foreach ($t in $plannedTrees) {
    $key = "tree:$t"

    # tree-specific palette tweaks (still placeholder)
    $barkA = [System.Drawing.Color]::FromArgb(255, 85, 85, 90)
    $barkB = [System.Drawing.Color]::FromArgb(255, 60, 60, 66)
    $ring  = $glowB

    $leafA = [System.Drawing.Color]::FromArgb(255, 60, 120, 80)
    $leafB = [System.Drawing.Color]::FromArgb(255, 40, 95, 60)

    if ($t -eq 'wayglass') {
        $barkA = [System.Drawing.Color]::FromArgb(255, 140, 190, 210)
        $barkB = [System.Drawing.Color]::FromArgb(255, 90, 140, 170)
        $leafA = [System.Drawing.Color]::FromArgb(180, 170, 220, 255)
        $leafB = [System.Drawing.Color]::FromArgb(160, 120, 190, 230)
        $ring = $glowA
    }

    if ($t -eq 'waytorch_tree') {
        $ring = [System.Drawing.Color]::FromArgb(255, 255, 210, 120)
    }

    New-TreeLogTexture    ("{0}_log.png" -f $t)     $key $barkA $barkB $ring
    New-TreeLogTexture    ("{0}_wood.png" -f $t)    "$key:wood" $barkA $barkB $ring

    # stripped variants: slightly brighter / less ring emphasis (still placeholder)
    $sbarkA = [System.Drawing.Color]::FromArgb(255, [Math]::Min(255, $barkA.R + 18), [Math]::Min(255, $barkA.G + 18), [Math]::Min(255, $barkA.B + 18))
    $sbarkB = [System.Drawing.Color]::FromArgb(255, [Math]::Min(255, $barkB.R + 18), [Math]::Min(255, $barkB.G + 18), [Math]::Min(255, $barkB.B + 18))
    $sring  = [System.Drawing.Color]::FromArgb(210, $ring.R, $ring.G, $ring.B)
    New-TreeLogTexture    ("stripped_{0}_log.png" -f $t)  "$key:stripped_log" $sbarkA $sbarkB $sring
    New-TreeLogTexture    ("stripped_{0}_wood.png" -f $t) "$key:stripped_wood" $sbarkA $sbarkB $sring

    New-TreePlanksTexture ("{0}_planks.png" -f $t)  $key $barkA $barkB
    New-TreeLeavesTexture ("{0}_leaves.png" -f $t)  $key $leafA $leafB
    New-TreeSaplingTexture ("{0}_sapling.png" -f $t) $key $leafA $ring
}

function Ensure-TextureFromRef([string]$ref) {
    # Only backfill our mod textures.
    if (-not $ref) { return }
    if ($ref -notmatch '^kruemblegard:(item|block)/(.+)$') { return }

    $kind = $Matches[1]
    $name = $Matches[2]

    # Avoid weird refs (atlas, etc)
    if ($name -match '[\\\s]' -or $name.Contains('..')) { return }

    $dir = if ($kind -eq 'item') { $texItem } else { $texBlock }
    $path = Join-Path $dir ("$name.png")

    if (Test-Path -LiteralPath $path) { return }

    New-Texture16 $path {
        param($bmp)

        $base = if ($kind -eq 'item') {
            [System.Drawing.Color]::FromArgb(255, 90, 90, 100)
        }
        else {
            [System.Drawing.Color]::FromArgb(255, 75, 75, 85)
        }

        # fill background
        for ($y = 0; $y -lt 16; $y++) {
            for ($x = 0; $x -lt 16; $x++) {
                P $bmp $x $y $base
            }
        }

        $a = [System.Drawing.Color]::FromArgb(255, 130, 130, 160)
        $b = [System.Drawing.Color]::FromArgb(255, 170, 120, 200)
        New-HashedDots $bmp "ref:$ref" $a $b
    }
}

function Backfill-TexturesFromModels([string]$modelsDir) {
    if (-not (Test-Path -LiteralPath $modelsDir)) { return }

    Get-ChildItem -LiteralPath $modelsDir -Filter '*.json' | ForEach-Object {
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
            if ($val -is [string]) {
                Ensure-TextureFromRef $val
            }
        }
    }
}

Backfill-TexturesFromModels (Join-Path $root 'src/main/resources/assets/kruemblegard/models/item')
Backfill-TexturesFromModels (Join-Path $root 'src/main/resources/assets/kruemblegard/models/block')

foreach ($p in $plannedPlants) {
    $key = "plant:$p"

    if ($p -match 'vine|ivy') {
        New-VineTexture ("{0}.png" -f $p) $key $vine $vine2
        continue
    }

    if ($p -match 'fungus|mold|rot|cap|puff|briar') {
        $capC = $cap
        $capS = $cap2
        if ($p -match 'black_echo') {
            $capC = [System.Drawing.Color]::FromArgb(255, 30, 30, 36)
            $capS = [System.Drawing.Color]::FromArgb(255, 70, 60, 90)
        }
        if ($p -match 'wayburn') {
            $capC = [System.Drawing.Color]::FromArgb(255, 220, 120, 60)
            $capS = [System.Drawing.Color]::FromArgb(255, 120, 40, 30)
        }
        New-FungusTexture ("{0}.png" -f $p) $key $capC $capS
        continue
    }

    # general plants
    $primary = [System.Drawing.Color]::FromArgb(255, 120, 210, 255)
    $accent  = [System.Drawing.Color]::FromArgb(255, 180, 250, 200)

    if ($p -match 'grass|reed') {
        $primary = [System.Drawing.Color]::FromArgb(255, 90, 200, 140)
        $accent  = [System.Drawing.Color]::FromArgb(255, 40, 120, 90)
    }

    if ($p -match 'lichen|moss') {
        $primary = [System.Drawing.Color]::FromArgb(255, 80, 120, 95)
        $accent  = $glowB
    }

    if ($p -match 'ash') {
        $primary = [System.Drawing.Color]::FromArgb(255, 160, 160, 160)
        $accent  = [System.Drawing.Color]::FromArgb(255, 90, 90, 90)
    }

    New-PlantTexture ("{0}.png" -f $p) $key $primary $accent
}

Write-Host 'Generated concept placeholder textures for planned trees & flora (skips any files that already exist).'
