[CmdletBinding()]
param()

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

function Get-RepoRoot {
    $root = Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..')
    return $root.Path
}

function Read-JsonFile {
    param([Parameter(Mandatory = $true)][string]$Path)

    $raw = Get-Content -LiteralPath $Path -Raw -Encoding UTF8
    try {
        return $raw | ConvertFrom-Json
    } catch {
        throw "Failed to parse JSON: $Path`n$($_.Exception.Message)"
    }
}

function Get-Prop {
    param(
        [Parameter(Mandatory = $true)]$Obj,
        [Parameter(Mandatory = $true)][string]$Name
    )

    if ($null -eq $Obj) { return $null }

    $p = $Obj.PSObject.Properties[$Name]
    if ($null -eq $p) { return $null }
    return $p.Value
}

function Format-NumberOrRange {
    param($Value)

    if ($null -eq $Value) { return '' }

    if ($Value -is [double] -or $Value -is [int] -or $Value -is [long]) {
        return "$Value"
    }

    $min = Get-Prop $Value 'min'
    $max = Get-Prop $Value 'max'
    if ($null -ne $min -or $null -ne $max) {
        if ($null -eq $min) { $min = '?' }
        if ($null -eq $max) { $max = '?' }
        return "$min..$max"
    }

    $type = Get-Prop $Value 'type'
    if ($null -ne $type) {
        return "{type=$type}"
    }

    return "{object}"
}

function Format-CondList {
    param($Conditions)

    if ($null -eq $Conditions) { return @() }
    $out = @()
    foreach ($c in @($Conditions)) {
        $name = Get-Prop $c 'condition'
        if ([string]::IsNullOrWhiteSpace($name)) {
            $out += '{condition?}'
        } else {
            $out += $name
        }
    }
    return $out
}

function Format-FuncList {
    param($Functions)

    if ($null -eq $Functions) { return @() }
    $out = @()
    foreach ($f in @($Functions)) {
        $name = Get-Prop $f 'function'
        if ([string]::IsNullOrWhiteSpace($name)) {
            $out += '{function?}'
        } else {
            $out += $name
        }
    }
    return $out
}

function Describe-Entry {
    param(
        [Parameter(Mandatory = $true)]$Entry,
        [Parameter(Mandatory = $true)][int]$Indent
    )

    $pad = (' ' * $Indent)
    $type = Get-Prop $Entry 'type'
    if ([string]::IsNullOrWhiteSpace($type)) { $type = '{type?}' }

    $name = Get-Prop $Entry 'name'
    $weight = Get-Prop $Entry 'weight'
    $quality = Get-Prop $Entry 'quality'

    $entryConditions = @(Format-CondList (Get-Prop $Entry 'conditions'))
    $entryFunctions = @(Format-FuncList (Get-Prop $Entry 'functions'))

    $bits = @()
    $bits += "type=$type"
    if (-not [string]::IsNullOrWhiteSpace($name)) { $bits += "name=$name" }
    if ($null -ne $weight) { $bits += "weight=$weight" }
    if ($null -ne $quality) { $bits += "quality=$quality" }

    if ($entryConditions.Count -gt 0) { $bits += ("conds=[" + ($entryConditions -join ', ') + "]") }
    if ($entryFunctions.Count -gt 0) { $bits += ("funcs=[" + ($entryFunctions -join ', ') + "]") }

    $lines = @()
    $lines += ($pad + '- ' + ($bits -join ' | '))

    $children = Get-Prop $Entry 'children'
    if ($null -ne $children) {
        foreach ($child in @($children)) {
            $lines += (Describe-Entry -Entry $child -Indent ($Indent + 2))
        }
    }

    return $lines
}

function Describe-LootTable {
    param(
        [Parameter(Mandatory = $true)]$Table,
        [Parameter(Mandatory = $true)][string]$Id,
        [Parameter(Mandatory = $true)][string]$RelativePath
    )

    $type = Get-Prop $Table 'type'
    $randomSequence = Get-Prop $Table 'random_sequence'

    $topConds = @(Format-CondList (Get-Prop $Table 'conditions'))
    $topFuncs = @(Format-FuncList (Get-Prop $Table 'functions'))

    $pools = @((Get-Prop $Table 'pools'))
    if ($null -eq $pools -or $pools.Count -eq 0 -or $null -eq $pools[0]) { $pools = @() }

    $lines = @()
    $lines += "### $Id"
    $lines += "- Source: $RelativePath"
    if (-not [string]::IsNullOrWhiteSpace($type)) { $lines += "- Type: $type" }
    if (-not [string]::IsNullOrWhiteSpace($randomSequence)) { $lines += "- Random sequence: $randomSequence" }
    if ($topConds.Count -gt 0) { $lines += ('- Table conditions: ' + ($topConds -join ', ')) }
    if ($topFuncs.Count -gt 0) { $lines += ('- Table functions: ' + ($topFuncs -join ', ')) }
    $lines += "- Pools: $($pools.Count)"

    $poolIndex = 0
    foreach ($p in $pools) {
        $poolIndex++

        $rolls = Format-NumberOrRange (Get-Prop $p 'rolls')
        $bonusRolls = Format-NumberOrRange (Get-Prop $p 'bonus_rolls')

        $poolConds = @(Format-CondList (Get-Prop $p 'conditions'))
        $poolFuncs = @(Format-FuncList (Get-Prop $p 'functions'))

        $entries = @((Get-Prop $p 'entries'))
        if ($null -eq $entries -or $entries.Count -eq 0 -or $null -eq $entries[0]) { $entries = @() }

        $bonusText = ''
        if (-not [string]::IsNullOrWhiteSpace($bonusRolls)) {
            $bonusText = ", bonus=$bonusRolls"
        }
        $lines += "- Pool ${poolIndex}: rolls=$rolls$bonusText, entries=$($entries.Count)"
        if ($poolConds.Count -gt 0) { $lines += ('  - Conditions: ' + ($poolConds -join ', ')) }
        if ($poolFuncs.Count -gt 0) { $lines += ('  - Functions: ' + ($poolFuncs -join ', ')) }

        foreach ($e in $entries) {
            $lines += (Describe-Entry -Entry $e -Indent 2)
        }
    }

    $lines += ''
    return $lines
}

$repoRoot = Get-RepoRoot
$dataRoot = Join-Path $repoRoot 'src\main\resources\data\kruemblegard'
$lootTablesRoot = Join-Path $dataRoot 'loot_tables'
$lootModifiersRoot = Join-Path $dataRoot 'loot_modifiers'
$docPath = Join-Path $repoRoot 'docs\Loot_Table_Bible.md'

if (-not (Test-Path -LiteralPath $lootTablesRoot)) {
    throw "Loot tables folder not found: $lootTablesRoot"
}

$lootTableFiles = @(Get-ChildItem -LiteralPath $lootTablesRoot -Recurse -File -Filter '*.json' | Sort-Object FullName)
$modifierFiles = @()
if (Test-Path -LiteralPath $lootModifiersRoot) {
    $modifierFiles = @(Get-ChildItem -LiteralPath $lootModifiersRoot -Recurse -File -Filter '*.json' | Sort-Object FullName)
}

$generated = New-Object System.Collections.Generic.List[string]
$generated.Add('# Loot Table Index (auto-generated)')
$generated.Add('')
$generated.Add('**DO NOT EDIT** this section by hand. Regenerate with: `powershell -NoProfile -ExecutionPolicy Bypass -File tools\\generate_loot_table_bible.ps1`')
$generated.Add('')
$generated.Add("Generated: $([DateTime]::Now.ToString('yyyy-MM-dd HH:mm:ss'))")
$generated.Add('')

# Group loot tables by top folder under loot_tables
$byGroup = @{}
foreach ($f in $lootTableFiles) {
    $rel = $f.FullName.Substring($lootTablesRoot.Length).TrimStart('\','/')
    $top = ($rel -split '[\\/]')[0]
    if ([string]::IsNullOrWhiteSpace($top)) { $top = '(root)' }
    if (-not $byGroup.ContainsKey($top)) { $byGroup[$top] = New-Object System.Collections.Generic.List[object] }
    $byGroup[$top].Add($f)
}

$generated.Add('## Summary')
$generated.Add("- Loot tables: $($lootTableFiles.Count)")
foreach ($k in ($byGroup.Keys | Sort-Object)) {
    $generated.Add("  - ${k}: $($byGroup[$k].Count)")
}
$generated.Add("- Global loot modifiers: $($modifierFiles.Count)")
$generated.Add('')

$generated.Add('## Loot Tables')
$generated.Add('')

foreach ($groupName in ($byGroup.Keys | Sort-Object)) {
    $generated.Add("## $groupName")
    $generated.Add('')

    foreach ($file in $byGroup[$groupName]) {
        $relPath = $file.FullName.Substring($repoRoot.Length).TrimStart('\','/') -replace '\\','/'
        $tableRel = $file.FullName.Substring($lootTablesRoot.Length).TrimStart('\','/')
        $idPath = ($tableRel -replace '\\','/' -replace '\.json$','')
        $tableId = "kruemblegard:$idPath"

        $table = Read-JsonFile -Path $file.FullName
        foreach ($line in (Describe-LootTable -Table $table -Id $tableId -RelativePath $relPath)) {
            $generated.Add($line)
        }
    }
}

$generated.Add('## Global Loot Modifiers')
$generated.Add('')
if ($modifierFiles.Count -eq 0) {
    $generated.Add('- (none)')
    $generated.Add('')
} else {
    foreach ($mf in $modifierFiles) {
        $relPath = $mf.FullName.Substring($repoRoot.Length).TrimStart('\','/') -replace '\\','/'
        $obj = Read-JsonFile -Path $mf.FullName

        $generated.Add("### $relPath")

        $type = Get-Prop $obj 'type'
        if (-not [string]::IsNullOrWhiteSpace($type)) {
            $generated.Add("- Type: $type")
        }

        $entries = Get-Prop $obj 'entries'
        if ($null -ne $entries) {
            $generated.Add("- Entries: $(@($entries).Count)")
        }

        $conditions = @(Format-CondList (Get-Prop $obj 'conditions'))
        if ($conditions.Count -gt 0) {
            $generated.Add('- Conditions: ' + ($conditions -join ', '))
        }

        $generated.Add('')
    }
}

$begin = '<!-- BEGIN AUTO-GENERATED -->'
$end = '<!-- END AUTO-GENERATED -->'

$preambleLines = @(
    '# Krümblegård Loot Table Bible',
    '',
    'This document is the **single source of truth** for loot-table behavior in Krümblegård.',
    '',
    'Scope:',
    '- Block loot tables under `data/kruemblegard/loot_tables/blocks/`',
    '- Entity loot tables under `data/kruemblegard/loot_tables/entities/`',
    '- Global loot modifiers under `data/kruemblegard/loot_modifiers/`',
    '',
    'Update workflow:',
    '- After adding/removing/renaming loot tables (or changing drops/conditions)',
    '- Commit the resulting doc changes in the same commit as the loot change.',
    '',
    $begin
)

$prefix = ($preambleLines -join "`r`n") + "`r`n"
$suffix = "`r`n$end`r`n"

$existing = $null
if (Test-Path -LiteralPath $docPath) {
    $existing = Get-Content -LiteralPath $docPath -Raw -Encoding UTF8
}

if ($null -ne $existing -and $existing.Contains($begin) -and $existing.Contains($end)) {
    $pre = $existing.Substring(0, $existing.IndexOf($begin) + $begin.Length)
    $post = $existing.Substring($existing.IndexOf($end))
    $prefix = $pre + "`r`n"
    $suffix = "`r`n" + $post
}

$content = ($generated -join "`r`n")
$final = $prefix + $content + $suffix

Set-Content -LiteralPath $docPath -Value $final -Encoding UTF8
Write-Output "WROTE: $docPath"
