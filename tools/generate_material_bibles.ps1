param(
    [string]$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$ModId = "kruemblegard"

$ModBlocksPath = Join-Path $RepoRoot "src/main/java/com/kruemblegard/init/ModBlocks.java"
$ModItemsPath  = Join-Path $RepoRoot "src/main/java/com/kruemblegard/registry/ModItems.java"

$BlockBiblePath = Join-Path $RepoRoot "docs/Block_Material_Bible.md"
$ItemBiblePath  = Join-Path $RepoRoot "docs/Item_Material_Bible.md"

function Get-CallSnippet {
    param(
        [Parameter(Mandatory=$true)][string]$Text,
        [Parameter(Mandatory=$true)][int]$CallStartIndex
    )

    $openParenIndex = $Text.IndexOf('(', $CallStartIndex)
    if ($openParenIndex -lt 0) {
        return $null
    }

    $depth = 0
    for ($i = $openParenIndex; $i -lt $Text.Length; $i++) {
        $c = $Text[$i]
        if ($c -eq '(') { $depth++ }
        elseif ($c -eq ')') {
            $depth--
            if ($depth -eq 0) {
                $end = $i
                # include trailing semicolon if present
                if ($end + 1 -lt $Text.Length -and $Text[$end + 1] -eq ';') {
                    $end++
                }
                return $Text.Substring($CallStartIndex, $end - $CallStartIndex + 1)
            }
        }
    }

    return $null
}

function Replace-Section {
    param(
        [Parameter(Mandatory=$true)][string]$FilePath,
        [Parameter(Mandatory=$true)][string]$StartMarker,
        [Parameter(Mandatory=$true)][string]$EndMarker,
        [Parameter(Mandatory=$true)][string]$NewContent
    )

    $doc = Get-Content -Raw -Encoding UTF8 $FilePath
    $startIndex = $doc.IndexOf($StartMarker)
    $endIndex = $doc.IndexOf($EndMarker)

    if ($startIndex -lt 0 -or $endIndex -lt 0 -or $endIndex -le $startIndex) {
        throw "Missing or invalid markers in $FilePath ($StartMarker / $EndMarker)"
    }

    $before = $doc.Substring(0, $startIndex + $StartMarker.Length)
    $after  = $doc.Substring($endIndex)

    $normalized = "`r`n`r`n" + $NewContent.Trim() + "`r`n`r`n"
    $updated = $before + $normalized + $after

    Set-Content -Path $FilePath -Value $updated -Encoding UTF8
}

function Parse-Blocks {
    $text = Get-Content -Raw -Encoding UTF8 $ModBlocksPath

    $matches = [regex]::Matches($text, 'BLOCKS\.register\(\s*"(?<id>[^"]+)"\s*,', [System.Text.RegularExpressions.RegexOptions]::Singleline)

    $results = @()
    foreach ($m in $matches) {
        $id = $m.Groups['id'].Value
        $snippet = Get-CallSnippet -Text $text -CallStartIndex $m.Index

        $class = $null
        if ($snippet -match '\(\)\s*->\s*new\s+(?<cls>[A-Za-z0-9_]+)') {
            $class = $Matches['cls']
        }

        $mapColor = $null
        if ($snippet -match 'mapColor\(MapColor\.(?<mc>[A-Z_]+)\)') {
            $mapColor = "MapColor.$($Matches['mc'])"
        }

        $sound = $null
        if ($snippet -match 'sound\(SoundType\.(?<st>[A-Z_]+)\)') {
            $sound = "SoundType.$($Matches['st'])"
        }

        $strength = $null
        if ($snippet -match 'strength\((?<s>[^\)]+)\)') {
            $strength = $Matches['s'].Trim()
        }

        $requiresTool = $false
        if ($snippet -match 'requiresCorrectToolForDrops\(\)') {
            $requiresTool = $true
        }

        $results += [pscustomobject]@{
            Id = $id
            Class = $class
            MapColor = $mapColor
            Sound = $sound
            Strength = $strength
            RequiresCorrectToolForDrops = $requiresTool
        }
    }

    # de-dupe and sort
    $results | Group-Object Id | ForEach-Object { $_.Group[0] } | Sort-Object Id
}

function Parse-Items {
    $text = Get-Content -Raw -Encoding UTF8 $ModItemsPath

    $ids = New-Object System.Collections.Generic.HashSet[string]

    foreach ($m in [regex]::Matches($text, 'registerBlockItem\(\s*"(?<id>[^"]+)"\s*,', [System.Text.RegularExpressions.RegexOptions]::Singleline)) {
        [void]$ids.Add($m.Groups['id'].Value)
    }

    foreach ($m in [regex]::Matches($text, 'ITEMS\.register\(\s*"(?<id>[^"]+)"\s*,', [System.Text.RegularExpressions.RegexOptions]::Singleline)) {
        [void]$ids.Add($m.Groups['id'].Value)
    }

    $results = @()

    foreach ($id in $ids) {
        $type = $null
        $details = @()

        $blockItemPattern = 'registerBlockItem\(\s*"' + [regex]::Escape($id) + '"\s*,'
        if ($text -match $blockItemPattern) {
            $type = "BlockItem"
        }

        $itemsRegisterPattern = 'ITEMS\.register\(\s*"' + [regex]::Escape($id) + '"\s*,'
        $m = [regex]::Match($text, $itemsRegisterPattern, [System.Text.RegularExpressions.RegexOptions]::Singleline)
        if ($m.Success) {
            $snippet = Get-CallSnippet -Text $text -CallStartIndex $m.Index

            if (-not $type -and $snippet -match '\(\)\s*->\s*new\s+(?<cls>[A-Za-z0-9_]+)') {
                $type = $Matches['cls']
            }

            if ($snippet -match '\.stacksTo\((?<n>\d+)\)') {
                $details += "stacksTo=$($Matches['n'])"
            }
            if ($snippet -match '\.rarity\(Rarity\.(?<r>[A-Z_]+)\)') {
                $details += "rarity=Rarity.$($Matches['r'])"
            }

            if ($snippet -match 'new\s+SwordItem\(\s*ModTiers\.(?<tier>[A-Z_]+)\s*,\s*(?<dmg>-?[0-9.]+)\s*,\s*(?<spd>-?[0-9.]+)') {
                $details += "tier=ModTiers.$($Matches['tier'])"
                $details += "attackDamageBonus=$($Matches['dmg'])"
                $details += "attackSpeed=$($Matches['spd'])"
            }
            if ($snippet -match 'new\s+PickaxeItem\(\s*ModTiers\.(?<tier>[A-Z_]+)\s*,\s*(?<dmg>-?[0-9.]+)\s*,\s*(?<spd>-?[0-9.]+)') {
                $details += "tier=ModTiers.$($Matches['tier'])"
                $details += "attackDamageBonus=$($Matches['dmg'])"
                $details += "attackSpeed=$($Matches['spd'])"
            }
            if ($snippet -match 'new\s+AxeItem\(\s*ModTiers\.(?<tier>[A-Z_]+)\s*,\s*(?<dmg>-?[0-9.]+)\s*,\s*(?<spd>-?[0-9.]+)') {
                $details += "tier=ModTiers.$($Matches['tier'])"
                $details += "attackDamageBonus=$($Matches['dmg'])"
                $details += "attackSpeed=$($Matches['spd'])"
            }
            if ($snippet -match 'new\s+ShovelItem\(\s*ModTiers\.(?<tier>[A-Z_]+)\s*,\s*(?<dmg>-?[0-9.]+)\s*,\s*(?<spd>-?[0-9.]+)') {
                $details += "tier=ModTiers.$($Matches['tier'])"
                $details += "attackDamageBonus=$($Matches['dmg'])"
                $details += "attackSpeed=$($Matches['spd'])"
            }
            if ($snippet -match 'new\s+HoeItem\(\s*ModTiers\.(?<tier>[A-Z_]+)\s*,\s*(?<dmg>-?[0-9.]+)\s*,\s*(?<spd>-?[0-9.]+)') {
                $details += "tier=ModTiers.$($Matches['tier'])"
                $details += "attackDamageBonus=$($Matches['dmg'])"
                $details += "attackSpeed=$($Matches['spd'])"
            }

            if ($snippet -match 'nutrition\((?<n>\d+)\)') {
                $details += "nutrition=$($Matches['n'])"
            }
            if ($snippet -match 'saturationMod\((?<s>[0-9.]+)f\)') {
                $details += "saturationMod=$($Matches['s'])"
            }

            if ($snippet -match 'new\s+ForgeSpawnEggItem\(\s*ModEntities\.(?<e>[A-Z_]+)\s*,\s*(?<c1>0x[0-9a-fA-F]+)\s*,\s*(?<c2>0x[0-9a-fA-F]+)') {
                $details += "entity=ModEntities.$($Matches['e'])"
                $details += "primaryColor=$($Matches['c1'])"
                $details += "secondaryColor=$($Matches['c2'])"
            }

            if ($snippet -match 'new\s+RecordItem\(\s*(?<comp>\d+)\s*,\s*(?<snd>[A-Za-z0-9_.]+)\s*,[\s\S]*?,\s*(?<len>\d+)\s*\)') {
                $details += "comparatorValue=$($Matches['comp'])"
                $details += "sound=$($Matches['snd'])"
                $details += "lengthTicks=$($Matches['len'])"
            }
        }

        if (-not $type) {
            $type = "Item"
        }

        $results += [pscustomobject]@{
            Id = $id
            Type = $type
            Details = ($details -join ", ")
        }
    }

    $results | Sort-Object Id
}

function Render-BlocksMarkdown {
    param([Parameter(Mandatory=$true)]$Blocks)

    $lines = New-Object System.Collections.Generic.List[string]
    $tick = '`'
    $lines.Add("### Blocks (All Registered)")
    $lines.Add("")

    foreach ($b in $Blocks) {
        $lines.Add("#### $($b.Id)")
        $lines.Add(("- **ID**: {0}{1}:{2}{0}" -f $tick, $ModId, $b.Id))
        if ($b.Class) { $lines.Add(("- **Class**: {0}{1}{0}" -f $tick, $b.Class)) } else { $lines.Add("- **Class**: TBD") }
        if ($b.MapColor) { $lines.Add(("- **Map color**: {0}{1}{0}" -f $tick, $b.MapColor)) } else { $lines.Add("- **Map color**: TBD") }
        if ($b.Sound) { $lines.Add(("- **Sound**: {0}{1}{0}" -f $tick, $b.Sound)) } else { $lines.Add("- **Sound**: TBD") }
        $toolVal = if ($b.RequiresCorrectToolForDrops) { 'true' } else { 'false' }
        $lines.Add(("- **Tool rule**: requiresCorrectToolForDrops = {0}{1}{0}" -f $tick, $toolVal))
        if ($b.Strength) { $lines.Add(("- **Strength tier**: {0}{1}{0}" -f $tick, $b.Strength)) } else { $lines.Add("- **Strength tier**: TBD") }
        $lines.Add("")
    }

    return ($lines -join "`r`n")
}

function Render-ItemsMarkdown {
    param([Parameter(Mandatory=$true)]$Items)

    $lines = New-Object System.Collections.Generic.List[string]
    $tick = '`'
    $lines.Add("### Items (All Registered)")
    $lines.Add("")

    foreach ($it in $Items) {
        $lines.Add("#### $($it.Id)")
        $lines.Add(("- **ID**: {0}{1}:{2}{0}" -f $tick, $ModId, $it.Id))
        $lines.Add(("- **Type**: {0}{1}{0}" -f $tick, $it.Type))
        if ($it.Details) {
            $lines.Add("- **Details**: $($it.Details)")
        } else {
            $lines.Add("- **Details**: (none parsed)")
        }
        $lines.Add("")
    }

    return ($lines -join "`r`n")
}

if (-not (Test-Path $ModBlocksPath)) { throw "Missing $ModBlocksPath" }
if (-not (Test-Path $ModItemsPath)) { throw "Missing $ModItemsPath" }
if (-not (Test-Path $BlockBiblePath)) { throw "Missing $BlockBiblePath" }
if (-not (Test-Path $ItemBiblePath)) { throw "Missing $ItemBiblePath" }

$blocks = Parse-Blocks
$items  = Parse-Items

$blocksMd = Render-BlocksMarkdown -Blocks $blocks
$itemsMd  = Render-ItemsMarkdown -Items $items

Replace-Section -FilePath $BlockBiblePath -StartMarker "<!-- AUTO-GENERATED:BLOCKS:START -->" -EndMarker "<!-- AUTO-GENERATED:BLOCKS:END -->" -NewContent $blocksMd
Replace-Section -FilePath $ItemBiblePath  -StartMarker "<!-- AUTO-GENERATED:ITEMS:START -->"  -EndMarker "<!-- AUTO-GENERATED:ITEMS:END -->"  -NewContent $itemsMd

Write-Host "Updated: $BlockBiblePath"
Write-Host "Updated: $ItemBiblePath"
