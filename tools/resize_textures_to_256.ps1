param(
  [switch]$Write,
  [string]$ReportDir,
  [int]$PreviewCount = 50,
  [switch]$NoReports
)

$ErrorActionPreference = 'Stop'

# Resizes textures to 256x256.
# Targets (in order):
#  1) assets/kruemblegard/textures/block
#  2) assets/kruemblegard/textures/item
# Rules:
# - Absolutely no cropping.
# - If a texture is not square, it is skipped (no changes).
# - Only square textures are eligible for resizing to 256x256.
# - Default is dry-run; pass -Write to modify files.
# Output:
# - Writes full lists to report files (unless -NoReports).
# - Prints a short preview in the console.

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path

if (-not $ReportDir -or $ReportDir.Trim().Length -eq 0) {
  $ReportDir = Join-Path $repoRoot 'tools\_reports'
}

$targets = @(
  @{ Name = 'block'; Path = (Join-Path $repoRoot 'src\main\resources\assets\kruemblegard\textures\block') },
  @{ Name = 'item';  Path = (Join-Path $repoRoot 'src\main\resources\assets\kruemblegard\textures\item') }
)

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

function Process-TextureTree([string]$label, [string]$dirPath) {
  $result = [PSCustomObject]@{
    Label = $label
    DirPath = $dirPath
    ChangedOrWouldChange = New-Object System.Collections.Generic.List[string]
    NonSquare = New-Object System.Collections.Generic.List[string]
    FilesScanned = 0
  }

  if (-not (Test-Path $dirPath)) {
    return $result
  }

  $files = Get-ChildItem -Path $dirPath -Recurse -File | Where-Object { $_.Extension -ieq '.png' } | Sort-Object FullName
  foreach ($f in $files) {
    $result.FilesScanned++
    $rel = $f.FullName.Substring($repoRoot.Length + 1)

    $bmp = Load-BitmapNoLock $f.FullName
    try {
      $w = $bmp.Width
      $h = $bmp.Height

      if ($w -ne $h) {
        $result.NonSquare.Add($rel)
        continue
      }

      if ($w -eq 256 -and $h -eq 256) {
        continue
      }

      $result.ChangedOrWouldChange.Add($rel)

      if (-not $Write) {
        continue
      }

      $resized = Resize-To256 $bmp
      try {
        $tmp = "$($f.FullName).tmp"
        try {
          $resized.Save($tmp, [System.Drawing.Imaging.ImageFormat]::Png)
          try {
            [System.IO.File]::SetAttributes($f.FullName, [System.IO.FileAttributes]::Normal)
          } catch {
            # Best-effort: some files may not allow attribute changes.
          }
          Copy-Item -Force $tmp $f.FullName
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

  return $result
}

$reports = foreach ($t in $targets) {
  Process-TextureTree $t.Name $t.Path
}

$allChanged = New-Object System.Collections.Generic.List[string]
$allNonSquare = New-Object System.Collections.Generic.List[string]
foreach ($r in $reports) {
  foreach ($p in $r.ChangedOrWouldChange) { $allChanged.Add($p) }
  foreach ($p in $r.NonSquare) { $allNonSquare.Add($p) }
}

$timestamp = (Get-Date).ToString('yyyyMMdd-HHmmss')
$changedReportPath = Join-Path $ReportDir ("textures_changed_{0}.txt" -f $timestamp)
$nonSquareReportPath = Join-Path $ReportDir ("textures_non_square_{0}.txt" -f $timestamp)

if (-not $NoReports) {
  New-Item -ItemType Directory -Force -Path $ReportDir | Out-Null
  ($allChanged | Sort-Object) | Set-Content -Encoding UTF8 $changedReportPath
  ($allNonSquare | Sort-Object) | Set-Content -Encoding UTF8 $nonSquareReportPath
}

$lines = New-Object System.Collections.Generic.List[string]
$lines.Add("Resize target: 256x256 (no cropping)")
$lines.Add(("Mode: {0}" -f ($(if ($Write) { 'WRITE' } else { 'DRY-RUN' }))))
$lines.Add("")

foreach ($r in $reports) {
  $lines.Add("== $($r.Label) textures ==")
  $lines.Add("Scanned: $($r.FilesScanned)")
  $lines.Add(("Square not-256: {0}" -f $r.ChangedOrWouldChange.Count))
  $lines.Add(("Non-square skipped: {0}" -f $r.NonSquare.Count))
  $lines.Add("")
}

if (-not $NoReports) {
  $lines.Add("Full lists written to:")
  $lines.Add("  $changedReportPath")
  $lines.Add("  $nonSquareReportPath")
  $lines.Add("")
}

if ($PreviewCount -gt 0) {
  $lines.Add("Preview (first $PreviewCount) - square not-256:")
  if ($allChanged.Count -eq 0) {
    $lines.Add("  (none)")
  } else {
    foreach ($p in ($allChanged | Sort-Object | Select-Object -First $PreviewCount)) { $lines.Add("  $p") }
  }
  $lines.Add("")

  $lines.Add("Preview (first $PreviewCount) - non-square skipped:")
  if ($allNonSquare.Count -eq 0) {
    $lines.Add("  (none)")
  } else {
    foreach ($p in ($allNonSquare | Sort-Object | Select-Object -First $PreviewCount)) { $lines.Add("  $p") }
  }
}

Write-Output ($lines -join [Environment]::NewLine)

# Avoid inheriting a stale $LASTEXITCODE from earlier native commands.
$global:LASTEXITCODE = 0
exit 0
