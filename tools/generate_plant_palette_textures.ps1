param(
    [string]$MaterialBiblePath,
    [int]$TextureSize = 16,
    [string]$OutputDir,
    [switch]$WriteToAssets,
    [string]$AssetsTexturesDir,
    [string]$BackupDir,
    [switch]$DryRun
)

# Resolve default paths relative to repo root
if (-not $PSScriptRoot) {
    $PSScriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
}

$repoRoot = Split-Path $PSScriptRoot -Parent

if (-not $MaterialBiblePath) {
    $MaterialBiblePath = Join-Path $repoRoot "docs\Plant_Material_Bible.md"
}

if (-not $AssetsTexturesDir) {
    $AssetsTexturesDir = Join-Path $repoRoot "src\main\resources\assets\kruemblegard\textures"
}

if (-not $OutputDir) {
    if ($WriteToAssets) {
        $OutputDir = $AssetsTexturesDir
    } else {
        $OutputDir = Join-Path $repoRoot "build/generated/plant_textures"
    }
}

if (-not (Test-Path $MaterialBiblePath)) {
    throw "Plant material bible markdown not found at path: $MaterialBiblePath"
}

if (-not (Test-Path $AssetsTexturesDir)) {
    throw "Assets textures directory not found at path: $AssetsTexturesDir"
}

Write-Host "Plant material bible:" $MaterialBiblePath
Write-Host "Assets textures:" $AssetsTexturesDir
Write-Host "Output directory:" $OutputDir
if ($WriteToAssets) {
    Write-Host "WriteToAssets: enabled (will overwrite existing PNGs)"
}
if ($DryRun) {
    Write-Host "DryRun: enabled (no files will be written)"
}

Add-Type -AssemblyName System.Drawing

function Clamp-Byte {
    param([int]$v)
    if ($v -lt 0) { return 0 }
    if ($v -gt 255) { return 255 }
    return $v
}

function Color-FromRgb {
    param($rgb, [int]$a = 255)
    if (-not $rgb) { return [System.Drawing.Color]::FromArgb($a, 128, 128, 128) }
    return [System.Drawing.Color]::FromArgb($a, (Clamp-Byte $rgb.R), (Clamp-Byte $rgb.G), (Clamp-Byte $rgb.B))
}

function Color-Shift {
    param([System.Drawing.Color]$c, [int]$delta)
    return [System.Drawing.Color]::FromArgb(
        $c.A,
        (Clamp-Byte ($c.R + $delta)),
        (Clamp-Byte ($c.G + $delta)),
        (Clamp-Byte ($c.B + $delta))
    )
}

function Hash-Seed {
    param([string]$text)
    $md5 = [System.Security.Cryptography.MD5]::Create()
    $bytes = [System.Text.Encoding]::UTF8.GetBytes($text)
    $hash = $md5.ComputeHash($bytes)
    $md5.Dispose()
    return [BitConverter]::ToUInt32($hash, 0)
}

function Hash-Bytes {
    param(
        [string]$text,
        [ValidateRange(1, 64)]
        [int]$length = 32
    )
    $sha = [System.Security.Cryptography.SHA256]::Create()
    $bytes = [System.Text.Encoding]::UTF8.GetBytes($text)
    $hash = $sha.ComputeHash($bytes)
    $sha.Dispose()
    if ($length -ge $hash.Length) { return $hash }
    return $hash[0..($length - 1)]
}

function New-Rng {
    param([uint32]$seed)
    [uint32]$state = $seed
    return {
        $state = [uint32]($state -bxor ((($state -shl 13) -band 0xFFFFFFFF)))
        $state = [uint32]($state -bxor ((($state -shr 17) -band 0xFFFFFFFF)))
        $state = [uint32]($state -bxor ((($state -shl 5) -band 0xFFFFFFFF)))
        return $state
    }.GetNewClosure()
}

function Rng-Range {
    param($rng, [int]$min, [int]$max)
    if ($max -lt $min) { $t = $min; $min = $max; $max = $t }
    $span = ($max - $min) + 1
    $v = & $rng
    return $min + ([int]($v % [uint32]$span))
}

function Set-PixelSafe {
    param(
        [System.Drawing.Bitmap]$bmp,
        [int]$x,
        [int]$y,
        [System.Drawing.Color]$c
    )
    if ($x -lt 0 -or $y -lt 0 -or $x -ge $bmp.Width -or $y -ge $bmp.Height) { return }
    $bmp.SetPixel($x, $y, $c)
}

function Ensure-Dir {
    param([string]$path)
    if ([string]::IsNullOrWhiteSpace($path)) { return }
    if (-not (Test-Path $path)) {
        New-Item -ItemType Directory -Path $path -Force | Out-Null
    }
}

function Copy-Backup {
    param([string]$src, [string]$dst)
    Ensure-Dir (Split-Path -Parent $dst)
    Copy-Item -Path $src -Destination $dst -Force
}

function Normalize-Key {
    param([string]$name)
    if (-not $name) { return '' }
    $k = $name.ToLowerInvariant().Trim()
    $k = $k -replace '\(charged\)', 'charged'
    $k = $k -replace '[^a-z0-9]+', '_'
    $k = $k.Trim('_')
    return $k
}

function Parse-RgbTriplet {
    param([string]$text)
    if (-not $text) { return $null }
    $t = $text.Trim()
    if ($t -match '^(none|n/a|-)$') { return $null }
    if ($t -match '^(\d+)\s*,\s*(\d+)\s*,\s*(\d+)$') {
        return [pscustomobject]@{ R=[int]$matches[1]; G=[int]$matches[2]; B=[int]$matches[3] }
    }
    return $null
}

# Parse Master Palette (RGB)
$lines = Get-Content -Path $MaterialBiblePath
$inTable = $false
$paletteEntries = @()

foreach ($line in $lines) {
    if ($line -match '^##\s+Master Palette \(RGB\)') {
        $inTable = $true
        continue
    }
    if ($inTable) {
        if ($line -match '^---$' -or $line -match '^##\s+') { break }
        if ($line -match '^\|\s*Plant\s*\|') { continue }
        if ($line -match '^\|---') { continue }

        if ($line -match '^\|\s*(.+?)\s*\|\s*([^|]+)\|\s*([^|]+)\|\s*([^|]+)\|\s*([^|]+)\|\s*$') {
            $plant = $matches[1].Trim()
            $base = Parse-RgbTriplet $matches[2]
            $accent = Parse-RgbTriplet $matches[3]
            $emissive = Parse-RgbTriplet $matches[4]
            $level = ($matches[5].Trim())

            $paletteEntries += [pscustomobject]@{
                Plant = $plant
                Key = (Normalize-Key $plant)
                Base = $base
                Accent = $accent
                Emissive = $emissive
                EmissiveLevel = [int]($level -replace '[^0-9]', '')
            }
        }
    }
}

if ($paletteEntries.Count -eq 0) {
    throw "No palette entries parsed from Plant Material Bible (Master Palette (RGB) table)."
}

Write-Host "Parsed" $paletteEntries.Count "palette entries."`n

$paletteMap = @{}
foreach ($p in $paletteEntries) {
    $paletteMap[$p.Key] = $p
}

# Special key mapping for known filenames
if ($paletteMap.ContainsKey('fault_dust')) { $paletteMap['fault_dust'] = $paletteMap['fault_dust'] }

# Scan models to find plant textures in use
$modelsBlockDir = Join-Path $repoRoot "src\main\resources\assets\kruemblegard\models\block"
$modelsItemDir = Join-Path $repoRoot "src\main\resources\assets\kruemblegard\models\item"

if (-not (Test-Path $modelsBlockDir)) { throw "Missing block models dir: $modelsBlockDir" }
if (-not (Test-Path $modelsItemDir)) { throw "Missing item models dir: $modelsItemDir" }

$blockTextures = @{}  # name -> @{"Type"="cross"|"surface"; "Parents"=@() }

function Add-TextureUse {
    param([hashtable]$dict, [string]$name, [string]$type, [string]$parent)
    if (-not $name) { return }
    if (-not $dict.ContainsKey($name)) {
        $dict[$name] = [pscustomobject]@{ Type=$type; Parents=@($parent) }
        return
    }
    if ($type -eq 'cross' -and $dict[$name].Type -ne 'cross') {
        $dict[$name].Type = 'cross'
    }
    if ($parent -and ($dict[$name].Parents -notcontains $parent)) {
        $dict[$name].Parents += $parent
    }
}

Get-ChildItem -Path $modelsBlockDir -Filter "*.json" -File | ForEach-Object {
    $raw = Get-Content -Path $_.FullName -Raw
    try {
        $j = $raw | ConvertFrom-Json
    } catch {
        return
    }

    $parent = ''
    if ($null -ne $j.parent) { $parent = [string]$j.parent }
    $isCross = $false
    if ($parent -match 'cross') { $isCross = $true }

    if ($null -ne $j.textures) {
        foreach ($prop in $j.textures.PSObject.Properties) {
            $tex = [string]$prop.Value
            if ($tex -match '^kruemblegard:block/(.+)$') {
                $name = $matches[1]
                if ($isCross) {
                    Add-TextureUse -dict $blockTextures -name $name -type 'cross' -parent $parent
                }
            }
        }
    }
}

# Ensure palette-defined surface blocks are included even if not cross
foreach ($k in $paletteMap.Keys) {
    switch ($k) {
        'ashmoss' { Add-TextureUse -dict $blockTextures -name 'ashmoss' -type 'surface' -parent 'bible' }
        'runegrowth' {
            Add-TextureUse -dict $blockTextures -name 'runegrowth_top' -type 'surface' -parent 'bible'
            Add-TextureUse -dict $blockTextures -name 'runegrowth_side' -type 'surface' -parent 'bible'
        }
        'voidfelt' {
            Add-TextureUse -dict $blockTextures -name 'voidfelt_top' -type 'surface' -parent 'bible'
            Add-TextureUse -dict $blockTextures -name 'voidfelt_side' -type 'surface' -parent 'bible'
        }
        'fault_dust' { Add-TextureUse -dict $blockTextures -name 'fault_dust' -type 'surface' -parent 'bible' }
        'cairn_moss' { Add-TextureUse -dict $blockTextures -name 'cairn_moss' -type 'surface' -parent 'bible' }
    }
}

# Ashfall Loam is a podzol-like ground block; it currently borrows Fault Dust palette.
Add-TextureUse -dict $blockTextures -name 'ashfall_loam_top' -type 'surface' -parent 'ground'
Add-TextureUse -dict $blockTextures -name 'ashfall_loam_side' -type 'surface' -parent 'ground'

$targets = @()
foreach ($kv in $blockTextures.GetEnumerator()) {
    $targets += [pscustomobject]@{ RelPath = ("block/" + $kv.Key + ".png"); Name=$kv.Key; Kind=$kv.Value.Type }
}

$targets = $targets | Sort-Object RelPath -Unique
if ($targets.Count -eq 0) {
    throw "No plant textures discovered from models/bible."
}

Write-Host "Found" $targets.Count "block texture targets to generate."`n

if ($WriteToAssets -and -not $BackupDir) {
    $stamp = Get-Date -Format "yyyyMMdd_HHmmss"
    $BackupDir = Join-Path $repoRoot "build/texture_backups/$stamp"
}

if ($WriteToAssets) {
    Write-Host "Backup directory:" $BackupDir
    Ensure-Dir $BackupDir
}

function Pick-Palette {
    param([string]$textureName)

    $n = $textureName.ToLowerInvariant()
    if ($paletteMap.ContainsKey($n)) { return $paletteMap[$n] }

    if ($n -match '^runegrowth_(top|side)$') {
        if ($paletteMap.ContainsKey('runegrowth')) { return $paletteMap['runegrowth'] }
    }

    if ($n -match '^voidfelt_(top|side)$') {
        if ($paletteMap.ContainsKey('voidfelt')) { return $paletteMap['voidfelt'] }
    }

    if ($n -match '^ashfall_loam_(top|side)$') {
        if ($paletteMap.ContainsKey('fault_dust')) { return $paletteMap['fault_dust'] }
    }

    if ($n -match '^runebloom_\d+$') {
        $k = 'runebloom_all_variants'
        if ($paletteMap.ContainsKey($k)) { return $paletteMap[$k] }
        $k2 = 'runebloom_all_variants'
    }

    if ($n -match '^soulberry_shrub_stage\d+$') {
        if ($paletteMap.ContainsKey('soulberry_shrub')) { return $paletteMap['soulberry_shrub'] }
    }
    if ($n -match '^ghoulberry_shrub_stage\d+$') {
        if ($paletteMap.ContainsKey('ghoulberry_shrub')) { return $paletteMap['ghoulberry_shrub'] }
    }

    # Fallback: pick from known palettes deterministically (keeps Wayfall vibe).
    $fallbackKeys = @('ashmoss','runegrowth','voidfelt','fault_dust','cairn_moss','wispstalk','gravevine','echocap','driftbloom','dustpetal','griefcap','transit_fern','misstep_vine','waygrasp_vine','voidcap_briar')
    $present = @($fallbackKeys | Where-Object { $paletteMap.ContainsKey($_) })
    if ($present.Count -gt 0) {
        $idx = [int]((Hash-Seed ("fallback:" + $textureName)) % [uint32]$present.Count)
        return $paletteMap[$present[$idx]]
    }
    return $paletteEntries[0]
}

function Draw-Signature {
    param(
        [System.Drawing.Bitmap]$bmp,
        [string]$sigKey,
        [System.Drawing.Color]$a,
        [System.Drawing.Color]$b,
        [int]$alpha = 255,
        [switch]$AllowTransparent
    )

    $w = $bmp.Width
    $h = $bmp.Height
    $sig = Hash-Bytes $sigKey 16
    $seen = @{}
    for ($i = 0; $i -lt 4; $i++) {
        $x = [int]($sig[$i * 2] % $w)
        $y = [int]($sig[$i * 2 + 1] % $h)
        $key = "$x,$y"
        if ($seen.ContainsKey($key)) { continue }
        $seen[$key] = $true

        $c0 = if (($sig[12 + $i] % 2) -eq 0) { $a } else { $b }
        $c = [System.Drawing.Color]::FromArgb($alpha, $c0.R, $c0.G, $c0.B)

        if ($AllowTransparent) {
            $existing = $bmp.GetPixel($x, $y)
            if ($existing.A -eq 0) {
                # Nudge to ensure non-empty pixel exists for signature.
                $bmp.SetPixel($x, $y, $c)
            } else {
                $bmp.SetPixel($x, $y, $c)
            }
        } else {
            $bmp.SetPixel($x, $y, $c)
        }
    }
}

function Clear-Transparent {
    param([System.Drawing.Bitmap]$bmp)
    for ($y = 0; $y -lt $bmp.Height; $y++) {
        for ($x = 0; $x -lt $bmp.Width; $x++) {
            $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(0, 0, 0, 0))
        }
    }
}

function Draw-TileNoise {
    param(
        [System.Drawing.Bitmap]$bmp,
        [System.Drawing.Color]$base,
        [System.Drawing.Color]$accent,
        [string]$seedKey,
        [int]$fine = 8,
        [int]$mid = 4
    )

    $w = $bmp.Width
    $h = $bmp.Height
    $rng = New-Rng -seed (Hash-Seed $seedKey)

    # broad layer: periodic sin noise to tile cleanly
    $phaseX = (Rng-Range $rng 0 1000) / 1000.0
    $phaseY = (Rng-Range $rng 0 1000) / 1000.0

    for ($y = 0; $y -lt $h; $y++) {
        for ($x = 0; $x -lt $w; $x++) {
            $fx = 2.0 * [Math]::PI * (($x / [double]$w) + $phaseX)
            $fy = 2.0 * [Math]::PI * (($y / [double]$h) + $phaseY)
            $broad = [Math]::Sin($fx) * 6 + [Math]::Cos($fy) * 6
            $c = Color-Shift $base ([int]([Math]::Round($broad)))
            $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(255, $c.R, $c.G, $c.B))
        }
    }

    # medium speckle
    for ($i = 0; $i -lt ($w * $h / $mid); $i++) {
        $x = Rng-Range $rng 0 ($w - 1)
        $y = Rng-Range $rng 0 ($h - 1)
        $c = Color-Shift $accent (Rng-Range $rng -10 6)
        Set-PixelSafe $bmp $x $y ([System.Drawing.Color]::FromArgb(255, $c.R, $c.G, $c.B))
    }

    # fine noise
    for ($i = 0; $i -lt ($w * $h / $fine); $i++) {
        $x = Rng-Range $rng 0 ($w - 1)
        $y = Rng-Range $rng 0 ($h - 1)
        $c = Color-Shift $base (Rng-Range $rng -12 12)
        Set-PixelSafe $bmp $x $y ([System.Drawing.Color]::FromArgb(255, $c.R, $c.G, $c.B))
    }
}

function Draw-Runes {
    param(
        [System.Drawing.Bitmap]$bmp,
        [System.Drawing.Color]$stroke,
        [System.Drawing.Color]$emissive,
        [string]$seedKey,
        [int]$count = 5,
        [int]$alpha = 255,
        [int]$emAlpha = 240
    )

    $w = $bmp.Width
    $h = $bmp.Height
    $rng = New-Rng -seed (Hash-Seed ($seedKey + ':runes'))

    for ($i = 0; $i -lt $count; $i++) {
        $x0 = Rng-Range $rng 1 ($w - 2)
        $y0 = Rng-Range $rng 1 ($h - 2)
        $len = Rng-Range $rng 3 8
        $dx = (Rng-Range $rng -1 1)
        $dy = (Rng-Range $rng -1 1)
        if ($dx -eq 0 -and $dy -eq 0) { $dx = 1 }

        for ($s = 0; $s -lt $len; $s++) {
            $x = $x0 + ($dx * $s)
            $y = $y0 + ($dy * $s)
            $x = ($x + $w) % $w
            $y = ($y + $h) % $h
            $c = [System.Drawing.Color]::FromArgb($alpha, $stroke.R, $stroke.G, $stroke.B)
            $bmp.SetPixel($x, $y, $c)

            if (-not $emissive.IsEmpty -and (Rng-Range $rng 0 6) -eq 0) {
                $e = [System.Drawing.Color]::FromArgb($emAlpha, $emissive.R, $emissive.G, $emissive.B)
                $bmp.SetPixel($x, $y, $e)
            }
        }
    }
}

function Draw-SurfaceBlock {
    param(
        [System.Drawing.Bitmap]$bmp,
        [string]$name,
        [System.Drawing.Color]$base,
        [System.Drawing.Color]$accent,
        [System.Drawing.Color]$emissive,
        [int]$emLevel,
        [string]$seedKey
    )

    Draw-TileNoise -bmp $bmp -base $base -accent $accent -seedKey $seedKey

    switch -Regex ($name) {
        '^runegrowth_top$' {
            # top face: runes are the primary identity
            Draw-Runes -bmp $bmp -stroke $accent -emissive $emissive -seedKey $seedKey -count 6 -alpha 255 -emAlpha 235
        }
        '^runegrowth_side$' {
            # side face: fewer, more subtle runes
            Draw-Runes -bmp $bmp -stroke (Color-Shift $accent -6) -emissive $emissive -seedKey $seedKey -count 3 -alpha 220 -emAlpha 210
        }
        '^voidfelt_(top|side)$' {
            # fibrous grain
            $rng = New-Rng -seed (Hash-Seed ($seedKey + ':fibers'))
            for ($y = 0; $y -lt $bmp.Height; $y++) {
                for ($x = 0; $x -lt $bmp.Width; $x++) {
                    if (($x % 3) -eq 0 -and (Rng-Range $rng 0 2) -eq 0) {
                        $c = Color-Shift $base (Rng-Range $rng -4 2)
                        $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(255, $c.R, $c.G, $c.B))
                    }
                }
            }
            if (-not $emissive.IsEmpty -and $emLevel -ge 1) {
                $rng2 = New-Rng -seed (Hash-Seed ($seedKey + ':motes'))
                for ($i = 0; $i -lt 6; $i++) {
                    $x = Rng-Range $rng2 1 ($bmp.Width - 2)
                    $y = Rng-Range $rng2 1 ($bmp.Height - 2)
                    $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(220, $emissive.R, $emissive.G, $emissive.B))
                }
            }
        }
        default { }
    }

    Draw-Signature -bmp $bmp -sigKey ($seedKey + ':sig') -a $base -b $accent -alpha 255
}

function Draw-CrossPlant {
    param(
        [System.Drawing.Bitmap]$bmp,
        [string]$name,
        [System.Drawing.Color]$base,
        [System.Drawing.Color]$accent,
        [System.Drawing.Color]$emissive,
        [int]$emLevel,
        [string]$seedKey
    )

    Clear-Transparent $bmp
    $w = $bmp.Width
    $h = $bmp.Height
    $rng = New-Rng -seed (Hash-Seed $seedKey)

    $stemColor = Color-Shift $base -14
    $leafColor = Color-Shift $base (Rng-Range $rng -4 8)
    $petalColor = Color-Shift $accent (Rng-Range $rng -6 10)

    $isVine = ($name -match '(vine|ivy|creeper)')
    $isReed = ($name -match '(reed|fern|stalk)')
    $isFungus = ($name -match '(cap|fungus|mold)')
    $isShrub = ($name -match '(shrub|briar|thorns)')
    $isRunebloom = ($name -match '^runebloom_\d+$')

    # stage density modifiers
    $stage = 0
    if ($name -match '_stage(\d+)$') { $stage = [int]$matches[1] }

    if ($isVine) {
        # diagonal strands
        $lines = 2 + ($stage)
        for ($i = 0; $i -lt $lines; $i++) {
            $x0 = Rng-Range $rng 0 3
            $y0 = Rng-Range $rng 0 3
            $dx = 1
            $dy = 1
            $len = 10 + (Rng-Range $rng 0 6)
            for ($s = 0; $s -lt $len; $s++) {
                $x = ($x0 + $dx * $s) % $w
                $y = ($y0 + $dy * $s) % $h
                $c = Color-Shift $stemColor (Rng-Range $rng -4 4)
                $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(255, $c.R, $c.G, $c.B))
                if ((Rng-Range $rng 0 3) -eq 0) {
                    $lx = [Math]::Max(0, [Math]::Min($w - 1, $x + (Rng-Range $rng -1 1)))
                    $ly = [Math]::Max(0, [Math]::Min($h - 1, $y + (Rng-Range $rng -1 1)))
                    $bmp.SetPixel($lx, $ly, [System.Drawing.Color]::FromArgb(255, $leafColor.R, $leafColor.G, $leafColor.B))
                }
            }
        }
    }
    elseif ($isReed) {
        # vertical stalks
        $stalks = 2 + (Rng-Range $rng 0 2)
        for ($s = 0; $s -lt $stalks; $s++) {
            $x = Rng-Range $rng 4 ($w - 5)
            $top = Rng-Range $rng 1 4
            for ($y = $h - 1; $y -ge $top; $y--) {
                $c = Color-Shift $stemColor (Rng-Range $rng -2 2)
                $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(255, $c.R, $c.G, $c.B))
                if ((Rng-Range $rng 0 4) -eq 0) {
                    $bmp.SetPixel([Math]::Max(0, $x - 1), $y, [System.Drawing.Color]::FromArgb(255, $leafColor.R, $leafColor.G, $leafColor.B))
                }
            }
        }
    }
    elseif ($isFungus) {
        # mushroom cap + stem
        $cx = [int]([Math]::Floor($w / 2))
        $stemX0 = $cx - 1
        $stemX1 = $cx
        $stemTop = [Math]::Max(2, [int]([Math]::Round($h * 0.55)))
        for ($y = $stemTop; $y -lt $h; $y++) {
            for ($x = $stemX0; $x -le $stemX1; $x++) {
                $c = Color-Shift $stemColor (Rng-Range $rng -3 3)
                $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(255, $c.R, $c.G, $c.B))
            }
        }
        $capY = [Math]::Max(1, $stemTop - 2)
        $r = 5
        for ($y = 0; $y -lt $capY + 3; $y++) {
            for ($x = 0; $x -lt $w; $x++) {
                $dx = $x - $cx
                $dy = $y - $capY
                if (($dx*$dx + $dy*$dy) -le ($r*$r) -and $y -le $capY + 1) {
                    $c = Color-Shift $petalColor (Rng-Range $rng -6 6)
                    $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(255, $c.R, $c.G, $c.B))
                }
            }
        }
    }
    else {
        # flower/shrub: stem + clustered blobs
        $cx = [int]([Math]::Floor($w / 2))
        $stemH = [Math]::Max(6, [int]([Math]::Round($h * 0.62)))
        for ($y = $h - 1; $y -ge $stemH; $y--) {
            $c = Color-Shift $stemColor (Rng-Range $rng -2 2)
            $bmp.SetPixel($cx, $y, [System.Drawing.Color]::FromArgb(255, $c.R, $c.G, $c.B))
            if ((Rng-Range $rng 0 3) -eq 0) {
                $bmp.SetPixel([Math]::Max(0, $cx - 1), $y, [System.Drawing.Color]::FromArgb(255, $leafColor.R, $leafColor.G, $leafColor.B))
            }
        }

        $blobCount = if ($isShrub) { 28 + $stage * 10 } else { 18 + $stage * 6 }
        for ($i = 0; $i -lt $blobCount; $i++) {
            $x = Rng-Range $rng 2 ($w - 3)
            $y = Rng-Range $rng 1 ($stemH + 2)
            $useAccent = ((Rng-Range $rng 0 4) -eq 0)
            $c0 = if ($useAccent) { $petalColor } else { $leafColor }
            $a = 255
            $c = Color-Shift $c0 (Rng-Range $rng -6 6)
            $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb($a, $c.R, $c.G, $c.B))
            if ((Rng-Range $rng 0 2) -eq 0) {
                Set-PixelSafe $bmp ($x + (Rng-Range $rng -1 1)) ($y + (Rng-Range $rng -1 1)) ([System.Drawing.Color]::FromArgb($a, $c.R, $c.G, $c.B))
            }
        }
    }

    if ($isRunebloom) {
        # petals glow
        if (-not $emissive.IsEmpty) {
            $petalNodes = 6
            for ($i = 0; $i -lt $petalNodes; $i++) {
                $x = Rng-Range $rng 3 ($w - 4)
                $y = Rng-Range $rng 2 8
                $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(235, $emissive.R, $emissive.G, $emissive.B))
            }
        }
    }
    elseif (-not $emissive.IsEmpty -and $emLevel -ge 1) {
        $glowCount = if ($emLevel -ge 2) { 6 } else { 3 }
        for ($i = 0; $i -lt $glowCount; $i++) {
            $x = Rng-Range $rng 1 ($w - 2)
            $y = Rng-Range $rng 1 ($h - 2)
            $existing = $bmp.GetPixel($x, $y)
            if ($existing.A -gt 0) {
                $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(235, $emissive.R, $emissive.G, $emissive.B))
            }
        }
    }

    Draw-Signature -bmp $bmp -sigKey ($seedKey + ':sig') -a $base -b $accent -alpha 255 -AllowTransparent
}

# Generate
$assetBlockDir = Join-Path $AssetsTexturesDir "block"
if (-not (Test-Path $assetBlockDir)) { throw "Missing assets block textures dir: $assetBlockDir" }

foreach ($t in $targets) {
    $rel = $t.RelPath
    $srcPath = Join-Path $AssetsTexturesDir $rel
    $dstPath = Join-Path $OutputDir $rel
    Ensure-Dir (Split-Path -Parent $dstPath)

    $size = [Math]::Max(16, $TextureSize)
    $bmp = New-Object System.Drawing.Bitmap $size, $size, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    try {
        $pal = Pick-Palette -textureName $t.Name
        $base = Color-FromRgb $pal.Base
        $accent = Color-FromRgb $pal.Accent
        $emissive = if ($pal.Emissive) { Color-FromRgb $pal.Emissive } else { [System.Drawing.Color]::Empty }
        $emLevel = [int]$pal.EmissiveLevel

        # Runebloom: emissive per variant
        if ($t.Name -match '^runebloom_\d+$') {
            $sig = Hash-Bytes ("runebloom_em:" + $t.Name) 4
            $emissive = [System.Drawing.Color]::FromArgb(255, (Clamp-Byte (60 + $sig[0])), (Clamp-Byte (120 + $sig[1])), (Clamp-Byte (160 + $sig[2])))
            $emLevel = 2
        }

        $seedKey = ("plant:" + $t.Name)

        if ($t.Kind -eq 'cross') {
            Draw-CrossPlant -bmp $bmp -name $t.Name -base $base -accent $accent -emissive $emissive -emLevel $emLevel -seedKey $seedKey
        } else {
            Draw-SurfaceBlock -bmp $bmp -name $t.Name -base $base -accent $accent -emissive $emissive -emLevel $emLevel -seedKey $seedKey
        }

        if ($WriteToAssets) {
            if (Test-Path $srcPath) {
                $backupPath = Join-Path $BackupDir $rel
                Copy-Backup -src $srcPath -dst $backupPath
            }
        }

        if (-not $DryRun) {
            $bmp.Save($dstPath, [System.Drawing.Imaging.ImageFormat]::Png)
        }
        Write-Host "Generated" $rel
    }
    finally {
        $bmp.Dispose()
    }
}

Write-Host "Done. Generated" $targets.Count "plant textures into" $OutputDir
