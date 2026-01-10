param(
    [string]$WorkspaceRoot = (Resolve-Path "$PSScriptRoot\.." | Select-Object -ExpandProperty Path)
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$ModId = 'kruemblegard'

$LootTablesRoot   = Join-Path $WorkspaceRoot 'src/main/resources/data/kruemblegard/loot_tables'
$LootModifiersDir = Join-Path $WorkspaceRoot 'src/main/resources/data/kruemblegard/loot_modifiers'
$DocPath          = Join-Path $WorkspaceRoot 'docs/Loot_Table_Bible.md'

function Read-JsonFile {
    param([Parameter(Mandatory=$true)][string]$FilePath)

    # Some JSON files in the repo are UTF-8 with BOM, which can break ConvertFrom-Json.
    $bytes = [System.IO.File]::ReadAllBytes($FilePath)
    if ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
        $bytes = $bytes[3..($bytes.Length - 1)]
    }

    $text = [System.Text.Encoding]::UTF8.GetString($bytes)
    return ($text | ConvertFrom-Json)
}

function Replace-Section {
    param(
        [Parameter(Mandatory=$true)][string]$FilePath,
        [Parameter(Mandatory=$true)][string]$StartMarker,
        [Parameter(Mandatory=$true)][string]$EndMarker,
        [Parameter(Mandatory=$true)][string]$NewContent
    )

    $text = Get-Content -Raw -Encoding UTF8 $FilePath

    $startIndex = $text.IndexOf($StartMarker)
    $endIndex = $text.IndexOf($EndMarker)

    if ($startIndex -lt 0 -or $endIndex -lt 0 -or $endIndex -le $startIndex) {
        throw "Markers not found or out of order in $FilePath"
    }

    $before = $text.Substring(0, $startIndex + $StartMarker.Length)
    $after  = $text.Substring($endIndex)

    $replacement = "`r`n`r`n$NewContent`r`n`r`n"
    Set-Content -Encoding UTF8 -NoNewline -Path $FilePath -Value ($before + $replacement + $after)
}

function Get-RelPath {
    param([Parameter(Mandatory=$true)][string]$Path)

    $rel = $Path.Substring($WorkspaceRoot.Length).TrimStart([char[]]@('\\','/'))
    return ($rel -replace '\\','/')
}

function To-CompactJson {
    param([Parameter(Mandatory=$true)]$Obj)

    try {
        return ($Obj | ConvertTo-Json -Depth 64 -Compress)
    } catch {
        return '(unserializable)'
    }
}

function Get-Prop {
    param(
        [Parameter(Mandatory=$true)]$Obj,
        [Parameter(Mandatory=$true)][string]$Name
    )

    if ($null -eq $Obj) { return $null }

    $props = $Obj.PSObject.Properties.Name
    if ($props -contains $Name) {
        return $Obj.$Name
    }

    return $null
}

function Collect-EntryItemNames {
    param([Parameter(Mandatory=$true)]$Entry)

    $results = New-Object System.Collections.Generic.List[string]

    if ($null -eq $Entry) { return $results }

    $type = Get-Prop $Entry 'type'

    # Common leaf nodes
    if ($type -eq 'minecraft:item') {
        $name = Get-Prop $Entry 'name'
        if ($name) { $results.Add([string]$name) }
        return $results
    }
    if ($type -eq 'minecraft:tag') {
        $name = Get-Prop $Entry 'name'
        if ($name) { $results.Add(('tag:' + [string]$name)) }
        return $results
    }

    # Containers: alternatives / group / sequence / dynamic / etc.
    foreach ($childKey in @('children', 'entries')) {
        $children = Get-Prop $Entry $childKey
        if ($children) {
            foreach ($c in $children) {
                foreach ($x in (Collect-EntryItemNames -Entry $c)) {
                    $results.Add($x)
                }
            }
        }
    }

    # Some entries embed another entry under 'entry'
    $inner = Get-Prop $Entry 'entry'
    if ($inner) {
        foreach ($x in (Collect-EntryItemNames -Entry $inner)) {
            $results.Add($x)
        }
    }

    return $results
}

function Has-AnyConditionsOrFunctions {
    param([Parameter(Mandatory=$true)]$Obj)

    if ($null -eq $Obj) { return $false }

    $conditions = Get-Prop $Obj 'conditions'
    if ($conditions -and $conditions.Count -gt 0) { return $true }

    $functions = Get-Prop $Obj 'functions'
    if ($functions -and $functions.Count -gt 0) { return $true }

    return $false
}

function Is-ComplexLootTable {
    param([Parameter(Mandatory=$true)]$Loot)

    $pools = Get-Prop $Loot 'pools'
    if (-not $pools -or $pools.Count -eq 0) { return $true }
    if ($pools.Count -ne 1) { return $true }

    foreach ($pool in $pools) {
        if (Has-AnyConditionsOrFunctions -Obj $pool) { return $true }

        $entries = Get-Prop $pool 'entries'
        if (-not $entries -or $entries.Count -eq 0) { return $true }
        if ($entries.Count -ne 1) { return $true }

        foreach ($entry in $entries) {
            if (Has-AnyConditionsOrFunctions -Obj $entry) { return $true }

            $type = Get-Prop $entry 'type'
            if ($type -ne 'minecraft:item') { return $true }
        }
    }

    return $false
}

function Parse-LootTableFile {
    param([Parameter(Mandatory=$true)][string]$FilePath)

    $json = Read-JsonFile -FilePath $FilePath

    $rel = Get-RelPath -Path $FilePath

    # e.g. .../loot_tables/blocks/ashveil.json -> kruemblegard:blocks/ashveil
    $afterRoot = $rel.Substring((Get-RelPath -Path $LootTablesRoot).Length).TrimStart('/')
    $idPath = $afterRoot.Substring(0, $afterRoot.Length - '.json'.Length)
    $lootTableId = "$ModId:$idPath"

    $pools = Get-Prop $json 'pools'

    $itemNames = New-Object System.Collections.Generic.HashSet[string]
    if ($pools) {
        foreach ($pool in $pools) {
            $entries = Get-Prop $pool 'entries'
            if ($entries) {
                foreach ($entry in $entries) {
                    foreach ($name in (Collect-EntryItemNames -Entry $entry)) {
                        [void]$itemNames.Add([string]$name)
                    }
                }
            }
        }
    }

    $itemsSorted = $itemNames.ToArray() | Sort-Object

    return [pscustomobject]@{
        FilePath = $FilePath
        RelPath = $rel
        LootTableId = $lootTableId
        Pools = $pools
        Items = $itemsSorted
        IsComplex = (Is-ComplexLootTable -Loot $json)
        Raw = $json
    }
}

function Render-LootTableSummaryRow {
    param([Parameter(Mandatory=$true)]$Info)

    $tick = '`'

    $itemsText = if ($Info.Items.Count -gt 0) {
        ($Info.Items | ForEach-Object { "$tick$_$tick" }) -join ', '
    } else {
        '(none detected)'
    }

    $complex = if ($Info.IsComplex) { 'yes' } else { 'no' }

    $fileLink = "[$($Info.RelPath)]($($Info.RelPath))"

    return "| $($tick + $Info.LootTableId + $tick) | $fileLink | $itemsText | $complex |"
}

function Render-LootTableExpanded {
    param([Parameter(Mandatory=$true)]$Info)

    $tick = '`'
    $lines = New-Object System.Collections.Generic.List[string]

    $lines.Add("#### $($Info.LootTableId)")
    $lines.Add(('- **File**: [{0}]({0})' -f $Info.RelPath))

    if ($Info.Items.Count -gt 0) {
        $itemsText = ($Info.Items | ForEach-Object { "$tick$_$tick" }) -join ', '
        $lines.Add("- **Items referenced**: $itemsText")
    } else {
        $lines.Add('- **Items referenced**: (none detected)')
    }

    $pools = $Info.Pools
    if (-not $pools) {
        $lines.Add('- **Pools**: (none)')
        $lines.Add('')
        return $lines
    }

    $lines.Add("- **Pools**: $($pools.Count)")

    $poolIndex = 0
    foreach ($pool in $pools) {
        $poolIndex++
        $rolls = Get-Prop $pool 'rolls'
        $bonus = Get-Prop $pool 'bonus_rolls'

        $rollsText = if ($null -eq $rolls) { '(default)' } elseif ($rolls -is [int] -or $rolls -is [double]) { [string]$rolls } else { (To-CompactJson $rolls) }
        $bonusText = if ($null -eq $bonus) { '(none)' } elseif ($bonus -is [int] -or $bonus -is [double]) { [string]$bonus } else { (To-CompactJson $bonus) }

        $lines.Add('')
        $lines.Add("**Pool $poolIndex**")
        $lines.Add("- rolls: $($tick + $rollsText + $tick)")
        $lines.Add("- bonus_rolls: $($tick + $bonusText + $tick)")

        $conditions = Get-Prop $pool 'conditions'
        if ($conditions -and $conditions.Count -gt 0) {
            $lines.Add("- conditions: $($tick + (To-CompactJson $conditions) + $tick)")
        }

        $functions = Get-Prop $pool 'functions'
        if ($functions -and $functions.Count -gt 0) {
            $lines.Add("- functions: $($tick + (To-CompactJson $functions) + $tick)")
        }

        $entries = Get-Prop $pool 'entries'
        if ($entries -and $entries.Count -gt 0) {
            $lines.Add("- entries: $($entries.Count)")
            $entryIndex = 0
            foreach ($entry in $entries) {
                $entryIndex++
                $etype = Get-Prop $entry 'type'
                $ename = Get-Prop $entry 'name'
                $weight = Get-Prop $entry 'weight'

                $descParts = @()
                if ($etype) { $descParts += "type=$etype" }
                if ($ename) { $descParts += "name=$ename" }
                if ($null -ne $weight) { $descParts += "weight=$weight" }

                $desc = if ($descParts.Count -gt 0) { $descParts -join ', ' } else { '(untyped entry)' }
                $lines.Add("  - $desc")

                $eConds = Get-Prop $entry 'conditions'
                if ($eConds -and $eConds.Count -gt 0) {
                    $lines.Add("    - conditions: $($tick + (To-CompactJson $eConds) + $tick)")
                }

                $eFuncs = Get-Prop $entry 'functions'
                if ($eFuncs -and $eFuncs.Count -gt 0) {
                    $lines.Add("    - functions: $($tick + (To-CompactJson $eFuncs) + $tick)")
                }
            }
        } else {
            $lines.Add('- entries: 0')
        }
    }

    $lines.Add('')
    return $lines
}

function Parse-LootModifierFile {
    param([Parameter(Mandatory=$true)][string]$FilePath)

    $json = Read-JsonFile -FilePath $FilePath

    return [pscustomobject]@{
        FilePath = $FilePath
        RelPath = (Get-RelPath -Path $FilePath)
        Type = (Get-Prop $json 'type')
        Conditions = (Get-Prop $json 'conditions')
        Additions = (Get-Prop $json 'additions')
        Raw = $json
    }
}

function Render-Doc {
    $lines = New-Object System.Collections.Generic.List[string]

    $lines.Add('### Loot Tables (All)')
    $lines.Add('')
    $lines.Add('| Loot table | File | Items referenced | Complex? |')
    $lines.Add('|---|---|---|---|')

    $lootFiles = @()
    if (Test-Path $LootTablesRoot) {
        $lootFiles = Get-ChildItem -Path $LootTablesRoot -Recurse -File -Filter '*.json' | Sort-Object FullName
    }

    $infos = @()
    foreach ($f in $lootFiles) {
        $infos += (Parse-LootTableFile -FilePath $f.FullName)
    }

    foreach ($info in ($infos | Sort-Object LootTableId)) {
        $lines.Add((Render-LootTableSummaryRow -Info $info))
    }

    $complex = $infos | Where-Object { $_.IsComplex }

    $lines.Add('')
    $lines.Add('### Loot Tables (Expanded: Complex Only)')
    $lines.Add('')
    $lines.Add('A table is considered “complex” if it has multiple pools, uses non-`minecraft:item` entry types, or includes conditions/functions.')
    $lines.Add('')

    foreach ($info in ($complex | Sort-Object LootTableId)) {
        foreach ($l in (Render-LootTableExpanded -Info $info)) {
            $lines.Add($l)
        }
    }

    $lines.Add('')
    $lines.Add('### Global Loot Modifiers')
    $lines.Add('')

    $modFiles = @()
    if (Test-Path $LootModifiersDir) {
        $modFiles = Get-ChildItem -Path $LootModifiersDir -File -Filter '*.json' | Sort-Object FullName
    }

    if ($modFiles.Count -eq 0) {
        $lines.Add('(none)')
        return ($lines -join "`r`n")
    }

    foreach ($mf in $modFiles) {
        $m = Parse-LootModifierFile -FilePath $mf.FullName
        $lines.Add("#### $($m.RelPath)")
        $lines.Add("- type: `$(if ($m.Type) { $m.Type } else { '(missing)' })`")
        if ($m.Conditions) { $lines.Add("- conditions: `$(To-CompactJson $m.Conditions)`") }
        if ($m.Additions) { $lines.Add("- additions: `$(To-CompactJson $m.Additions)`") }
        $lines.Add('')
    }

    return ($lines -join "`r`n")
}

if (-not (Test-Path $DocPath)) {
    throw "Missing doc template: $DocPath"
}

$generated = Render-Doc

Replace-Section -FilePath $DocPath -StartMarker '<!-- AUTO-GENERATED:LOOT_TABLES:START -->' -EndMarker '<!-- AUTO-GENERATED:LOOT_TABLES:END -->' -NewContent $generated

Write-Output "Updated: $(Get-RelPath -Path $DocPath)"
