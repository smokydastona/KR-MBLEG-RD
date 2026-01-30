param(
  [switch]$Write
)

$ErrorActionPreference = 'Stop'

# Resizes all block textures under assets/kruemblegard/textures/block to 256x256.
# Rules:
# - Absolutely no cropping.
# - If a texture is not square, it is skipped (no changes).
# - Only square textures are eligible for resizing to 256x256.
# - Default is dry-run; pass -Write to modify files.
# Output:
# - A list of changed textures (or would-change in dry-run).
# - A list of textures that are not square.

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$blockTexturesDir = Join-Path $repoRoot 'src\main\resources\assets\kruemblegard\textures\block'

if (-not (Test-Path $blockTexturesDir)) {
  throw "Block textures directory not found: $blockTexturesDir"
}

Add-Type -AssemblyName System.Drawing

function Load-BitmapNoLock([string]$path) {
  $bytes = [System.IO.File]::ReadAllBytes($path)
  $ms = New-Object System.IO.MemoryStream(,$bytes)
  try {
    $bmp = [System.Drawing.Bitmap]::FromStream($ms)
    try {
      $rect = New-Object System.Drawing.Rectangle(0, 0, $bmp.Width, $bmp.Height)
      return $bmp.Clone($rect, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    } finally {
      $bmp.Dispose()
    }
  } finally {
    $ms.Dispose()
  }
}

function Resize-To256([System.Drawing.Bitmap]$bmp) {
  $dest = New-Object System.Drawing.Bitmap 256, 256, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
  $dest.SetResolution($bmp.HorizontalResolution, $bmp.VerticalResolution)

  $g = [System.Drawing.Graphics]::FromImage($dest)
  try {
    $g.CompositingMode = [System.Drawing.Drawing2D.CompositingMode]::SourceCopy
    $g.CompositingQuality = [System.Drawing.Drawing2D.CompositingQuality]::HighQuality
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality

    if ($bmp.Width -lt 256 -or $bmp.Height -lt 256) {
      $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
      $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::Half
    } else {
      $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
      $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    }

    $g.DrawImage($bmp, 0, 0, 256, 256)
  } finally {
    $g.Dispose()
  }

  return $dest
}

$files = Get-ChildItem -Path $blockTexturesDir -Recurse -File | Where-Object { $_.Extension -ieq '.png' } | Sort-Object FullName

if ($files.Count -eq 0) {
  Write-Output "No .png files found under: $blockTexturesDir"
  exit 0
}

$nonSquare = New-Object System.Collections.Generic.List[string]
$changedOrWouldChange = New-Object System.Collections.Generic.List[string]

foreach ($f in $files) {
  $rel = $f.FullName.Substring($repoRoot.Length + 1)

  $bmp = Load-BitmapNoLock $f.FullName
  try {
    $w = $bmp.Width
    $h = $bmp.Height

    if ($w -ne $h) {
      $nonSquare.Add($rel)
      continue
    }

    if ($w -eq 256 -and $h -eq 256) {
      continue
    }

    $changedOrWouldChange.Add($rel)

    if (-not $Write) {
      continue
    }

    $resized = Resize-To256 $bmp
    try {
      $tmp = "$($f.FullName).tmp"
      try {
        $resized.Save($tmp, [System.Drawing.Imaging.ImageFormat]::Png)
        Move-Item -Force $tmp $f.FullName
      } finally {
        if (Test-Path $tmp) { Remove-Item -Force $tmp }
      }
    } finally {
      $resized.Dispose()
    }
  } finally {
    $bmp.Dispose()
  }
}

$report = New-Object System.Collections.Generic.List[string]

if ($Write) {
  $report.Add("Changed textures ($($changedOrWouldChange.Count)) (resized to 256x256):")
} else {
  $report.Add("Would change textures ($($changedOrWouldChange.Count)) (square, not 256x256):")
}

if ($changedOrWouldChange.Count -eq 0) {
  $report.Add("  (none)")
} else {
  foreach ($p in $changedOrWouldChange) {
    $report.Add("  $p")
  }
}

$report.Add("Non-square textures ($($nonSquare.Count)) (skipped):")
if ($nonSquare.Count -eq 0) {
  $report.Add("  (none)")
} else {
  foreach ($p in $nonSquare) {
    $report.Add("  $p")
  }
}

Write-Output ($report -join [Environment]::NewLine)
