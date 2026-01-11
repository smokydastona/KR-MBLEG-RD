param(
    [int] $Size = 64
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Get-RepoRoot {
    $here = (Resolve-Path -LiteralPath $PSScriptRoot).Path
    while ($true) {
        if (Test-Path -LiteralPath (Join-Path $here 'build.gradle')) { return $here }
        $parent = Split-Path -Parent $here
        if ($parent -eq $here) { throw 'Could not locate repo root (build.gradle not found).' }
        $here = $parent
    }
}

Add-Type -AssemblyName System.Drawing

$repoRoot = Get-RepoRoot
$outFile = Join-Path $repoRoot 'src\main\resources\assets\kruemblegard\textures\entity\scattered_enderman.png'
$outDir = Split-Path -Parent $outFile
if (!(Test-Path -LiteralPath $outDir)) {
    New-Item -ItemType Directory -Path $outDir | Out-Null
}

$bmp = New-Object System.Drawing.Bitmap($Size, $Size, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
try {
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    try {
        $g.Clear([System.Drawing.Color]::FromArgb(255, 24, 24, 28))

        $magenta = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 255, 0, 255))
        $dark = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 0, 0, 0))
        $white = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 240, 240, 240))
        $green = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 80, 255, 160))

        try {
            $block = [Math]::Max(2, [int]($Size / 8))
            for ($y = 0; $y -lt $Size; $y += $block) {
                for ($x = 0; $x -lt $Size; $x += $block) {
                    $isMagenta = ((([int]($x / $block) + [int]($y / $block)) % 2) -eq 0)
                    $brush = if ($isMagenta) { $magenta } else { $dark }
                    $g.FillRectangle($brush, $x, $y, $block, $block)
                }
            }

            # Simple label-ish shapes (avoids needing fonts)
            $pad = [int]($Size * 0.08)
            $g.FillRectangle($white, $pad, $pad, $Size - 2*$pad, [int]($Size * 0.18))
            $g.FillRectangle($green, $pad, [int]($Size * 0.78), $Size - 2*$pad, [int]($Size * 0.14))
        }
        finally {
            $magenta.Dispose(); $dark.Dispose(); $white.Dispose(); $green.Dispose()
        }
    }
    finally {
        $g.Dispose()
    }

    if (Test-Path -LiteralPath $outFile) {
        Remove-Item -LiteralPath $outFile -Force
    }

    $bmp.Save($outFile, [System.Drawing.Imaging.ImageFormat]::Png)
}
finally {
    $bmp.Dispose()
}

Write-Output "Wrote placeholder texture -> $outFile"
