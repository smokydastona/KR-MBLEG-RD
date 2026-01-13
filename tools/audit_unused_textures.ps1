[CmdletBinding()]
param(
  [string]$ModId = 'kruemblegard',
  [int]$MaxList = 500
)

$ErrorActionPreference = 'Stop'

function Normalize-RelPath([string]$Path) {
  return ($Path -replace '\\','/').TrimStart('/').ToLowerInvariant()
}

function Add-Ref([System.Collections.Generic.HashSet[string]]$Set, [string]$Value) {
  if ([string]::IsNullOrWhiteSpace($Value)) { return }
  $v = $Value.Trim()
  if ($v.StartsWith('#')) { return }
  if ($v.StartsWith('minecraft:')) { return }
  if ($v.StartsWith("${ModId}:")) { $v = $v.Substring($ModId.Length + 1) }
  $v = Normalize-RelPath $v
  if ($v.EndsWith('.png')) { $p = $v } else { $p = "$v.png" }
  [void]$Set.Add($p)
}

function Walk-Json($Obj, [System.Collections.Generic.HashSet[string]]$Refs) {
  if ($null -eq $Obj) { return }
  if ($Obj -is [string]) { return }

  if ($Obj -is [System.Collections.IDictionary]) {
    foreach ($k in $Obj.Keys) {
      $name = [string]$k
      $val = $Obj[$k]
      if ($name -in @('textures','texture','particle')) {
        if ($val -is [string]) {
          Add-Ref $Refs $val
        } elseif ($val -is [System.Collections.IDictionary]) {
          foreach ($vv in $val.Values) { if ($vv -is [string]) { Add-Ref $Refs $vv } }
        } elseif ($val -is [psobject]) {
          foreach ($pp in $val.PSObject.Properties) {
            if ($pp.Value -is [string]) { Add-Ref $Refs $pp.Value }
          }
        } elseif ($val -is [System.Collections.IEnumerable]) {
          foreach ($x in $val) {
            if ($x -is [string]) { Add-Ref $Refs $x }
            elseif ($x -is [System.Collections.IDictionary]) {
              foreach ($vv in $x.Values) { if ($vv -is [string]) { Add-Ref $Refs $vv } }
            } elseif ($x -is [psobject]) {
              foreach ($pp in $x.PSObject.Properties) {
                if ($pp.Value -is [string]) { Add-Ref $Refs $pp.Value }
              }
            }
          }
        }
      }

      Walk-Json $val $Refs
    }
    return
  }

  if ($Obj -is [System.Collections.IEnumerable]) {
    foreach ($x in $Obj) { Walk-Json $x $Refs }
    return
  }

  foreach ($p in $Obj.PSObject.Properties) {
    $name = $p.Name
    $val = $p.Value

    if ($name -in @('textures','texture','particle')) {
      if ($val -is [string]) {
        Add-Ref $Refs $val
      } elseif ($val -is [System.Collections.IDictionary]) {
        foreach ($vv in $val.Values) { if ($vv -is [string]) { Add-Ref $Refs $vv } }
      } elseif ($val -is [psobject]) {
        foreach ($pp in $val.PSObject.Properties) {
          if ($pp.Value -is [string]) { Add-Ref $Refs $pp.Value }
        }
      } elseif ($val -is [System.Collections.IEnumerable]) {
        foreach ($x in $val) {
          if ($x -is [string]) { Add-Ref $Refs $x }
          elseif ($x -is [System.Collections.IDictionary]) {
            foreach ($vv in $x.Values) { if ($vv -is [string]) { Add-Ref $Refs $vv } }
          } elseif ($x -is [psobject]) {
            foreach ($pp in $x.PSObject.Properties) {
              if ($pp.Value -is [string]) { Add-Ref $Refs $pp.Value }
            }
          }
        }
      }
    }

    Walk-Json $val $Refs
  }
}

$assetsRoot = (Resolve-Path "src/main/resources/assets/$ModId").ProviderPath
$texturesRoot = Join-Path $assetsRoot 'textures'

$trimChars = [char[]]@('\','/')

$texFiles = Get-ChildItem -Path $texturesRoot -Recurse -File -Filter *.png |
  ForEach-Object {
    $rel = $_.FullName.Substring($texturesRoot.Length).TrimStart($trimChars)
    Normalize-RelPath $rel
  }

$refs = New-Object 'System.Collections.Generic.HashSet[string]'

# Parse JSON under assets/<modid>, excluding the textures directory itself
$jsonFiles = Get-ChildItem -Path $assetsRoot -Recurse -File -Filter *.json |
  Where-Object { $_.FullName -notlike "*\\textures\\*" }

foreach ($f in $jsonFiles) {
  try {
    $raw = Get-Content -Raw -Encoding UTF8 $f.FullName
    $obj = $raw | ConvertFrom-Json -ErrorAction Stop
    Walk-Json $obj $refs
  } catch {
    # Some files in assets may not be strict JSON (or may not parse as PSCustomObject); ignore.
  }
}

# Also catch Java ResourceLocation texture references like "textures/entity/foo.png"
$javaRegex = [regex]'textures/(?<p>[a-z0-9_./-]+?\.png)'
Get-ChildItem -Path 'src/main/java' -Recurse -File -Filter *.java | ForEach-Object {
  $t = Get-Content -Raw -Encoding UTF8 $_.FullName
  foreach ($m in $javaRegex.Matches($t)) {
    Add-Ref $refs $m.Groups['p'].Value
  }
}

$unused = $texFiles | Where-Object { -not $refs.Contains($_) } | Sort-Object

Write-Output "TOTAL_PNG=$($texFiles.Count)  REFERENCED=$($refs.Count)  UNUSED=$($unused.Count)"
$unused | Select-Object -First $MaxList
if ($unused.Count -gt $MaxList) {
  Write-Output "...and $($unused.Count - $MaxList) more"
}
