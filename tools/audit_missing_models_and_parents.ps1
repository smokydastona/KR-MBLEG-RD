param(
    [string]$AssetsRoot = "src/main/resources/assets",
    [string]$Namespace = "kruemblegard"
)

$ErrorActionPreference = 'Stop'

function Normalize-Path([string]$p) {
    return $p -replace '\\','/'
}

function Resolve-ModelPath([string]$assetsRootAbs, [string]$modelId) {
    # modelId examples:
    # - minecraft:block/cube_all (builtin, ignore)
    # - kruemblegard:block/attuned_stone
    # - block/attuned_stone (treat as current namespace)
    if ([string]::IsNullOrWhiteSpace($modelId)) { return $null }

    $ns = $null
    $path = $null

    if ($modelId -match '^[a-z0-9_.-]+:' ) {
        $parts = $modelId.Split(':', 2)
        $ns = $parts[0]
        $path = $parts[1]
    } else {
        # Vanilla commonly omits the namespace for minecraft models.
        # Examples: "block/cube_all", "item/generated", "item/template_spawn_egg".
        if ($modelId.StartsWith('block/') -or $modelId.StartsWith('item/')) {
            $ns = 'minecraft'
        } else {
            $ns = $Namespace
        }
        $path = $modelId
    }

    if ($ns -eq 'minecraft') {
        return @{ kind = 'minecraft'; absPath = $null; relPath = $null }
    }

    $jsonRel = "assets/$ns/models/$path.json"
    $jsonAbs = Join-Path $assetsRootAbs (Join-Path $ns (Join-Path "models" ($path + ".json")))

    return @{ kind = 'file'; absPath = $jsonAbs; relPath = $jsonRel }
}

$workspaceRoot = (Get-Location).Path
$assetsRootAbs = Join-Path $workspaceRoot $AssetsRoot

if (-not (Test-Path $assetsRootAbs)) {
    Write-Host "ERROR: assets root not found: $assetsRootAbs"
    exit 1
}

$modelRoots = @(
    (Join-Path $assetsRootAbs "$Namespace\models\block"),
    (Join-Path $assetsRootAbs "$Namespace\models\item")
)

$blockstatesRoot = Join-Path $assetsRootAbs "$Namespace/blockstates"

$missingParents = New-Object System.Collections.Generic.List[object]
$missingBlockstateModels = New-Object System.Collections.Generic.List[object]
$missingItemOverrideModels = New-Object System.Collections.Generic.List[object]

# 1) Scan model JSONs for missing parents
foreach ($root in $modelRoots) {
    if (-not (Test-Path $root)) { continue }

    $models = Get-ChildItem -Path $root -Recurse -File -Filter '*.json'
    foreach ($modelFile in $models) {
        $relModelFile = Normalize-Path ($modelFile.FullName.Substring($workspaceRoot.Length).TrimStart('\'))

        $json = $null
        try {
            $json = Get-Content -Raw -Path $modelFile.FullName | ConvertFrom-Json -Depth 100
        } catch {
            $missingParents.Add([pscustomobject]@{
                type = 'model_json_invalid'
                file = $relModelFile
                reference = ''
                expected = ''
                reason = $_.Exception.Message
            })
            continue
        }

        if ($null -ne $json.parent) {
            $resolved = Resolve-ModelPath $assetsRootAbs $json.parent
            if ($resolved -ne $null -and $resolved.kind -eq 'file') {
                if (-not (Test-Path $resolved.absPath)) {
                    $missingParents.Add([pscustomobject]@{
                        type = 'missing_parent_model'
                        file = $relModelFile
                        reference = $json.parent
                        expected = Normalize-Path ($resolved.relPath)
                        reason = 'parent model json not found'
                    })
                }
            }
        }

        # Item model override references (if present)
        if ($relModelFile -like '*/models/item/*' -and $null -ne $json.overrides) {
            foreach ($ov in $json.overrides) {
                if ($null -ne $ov.model) {
                    $resolved = Resolve-ModelPath $assetsRootAbs $ov.model
                    if ($resolved -ne $null -and $resolved.kind -eq 'file') {
                        if (-not (Test-Path $resolved.absPath)) {
                            $missingItemOverrideModels.Add([pscustomobject]@{
                                type = 'missing_item_override_model'
                                file = $relModelFile
                                reference = $ov.model
                                expected = Normalize-Path ($resolved.relPath)
                                reason = 'override model json not found'
                            })
                        }
                    }
                }
            }
        }
    }
}

# 2) Scan blockstates for missing model references
if (Test-Path $blockstatesRoot) {
    $blockstates = Get-ChildItem -Path $blockstatesRoot -Recurse -File -Filter '*.json'
    foreach ($bsFile in $blockstates) {
        $relBsFile = Normalize-Path ($bsFile.FullName.Substring($workspaceRoot.Length).TrimStart('\'))

        $json = $null
        try {
            $json = Get-Content -Raw -Path $bsFile.FullName | ConvertFrom-Json -Depth 100
        } catch {
            $missingBlockstateModels.Add([pscustomobject]@{
                type = 'blockstate_json_invalid'
                file = $relBsFile
                reference = ''
                expected = ''
                reason = $_.Exception.Message
            })
            continue
        }

        # Variants
        if ($null -ne $json.variants) {
            foreach ($prop in $json.variants.PSObject.Properties) {
                $variantVal = $prop.Value
                $modelsToCheck = @()

                if ($variantVal -is [System.Collections.IEnumerable] -and -not ($variantVal -is [string])) {
                    foreach ($entry in $variantVal) {
                        if ($null -ne $entry.model) { $modelsToCheck += $entry.model }
                    }
                } else {
                    if ($null -ne $variantVal.model) { $modelsToCheck += $variantVal.model }
                }

                foreach ($modelId in $modelsToCheck) {
                    $resolved = Resolve-ModelPath $assetsRootAbs $modelId
                    if ($resolved -ne $null -and $resolved.kind -eq 'file') {
                        if (-not (Test-Path $resolved.absPath)) {
                            $missingBlockstateModels.Add([pscustomobject]@{
                                type = 'missing_blockstate_model'
                                file = $relBsFile
                                reference = $modelId
                                expected = Normalize-Path ($resolved.relPath)
                                reason = 'blockstate model json not found'
                            })
                        }
                    }
                }
            }
        }

        # Multipart
        if ($null -ne $json.multipart) {
            foreach ($part in $json.multipart) {
                $apply = $part.apply
                if ($null -eq $apply) { continue }

                $modelsToCheck = @()
                if ($apply -is [System.Collections.IEnumerable] -and -not ($apply -is [string])) {
                    foreach ($entry in $apply) {
                        if ($null -ne $entry.model) { $modelsToCheck += $entry.model }
                    }
                } else {
                    if ($null -ne $apply.model) { $modelsToCheck += $apply.model }
                }

                foreach ($modelId in $modelsToCheck) {
                    $resolved = Resolve-ModelPath $assetsRootAbs $modelId
                    if ($resolved -ne $null -and $resolved.kind -eq 'file') {
                        if (-not (Test-Path $resolved.absPath)) {
                            $missingBlockstateModels.Add([pscustomobject]@{
                                type = 'missing_blockstate_model'
                                file = $relBsFile
                                reference = $modelId
                                expected = Normalize-Path ($resolved.relPath)
                                reason = 'blockstate model json not found'
                            })
                        }
                    }
                }
            }
        }
    }
}

Write-Host "Missing parent model JSONs: $($missingParents.Count)"
if ($missingParents.Count -gt 0) {
    Write-Host "--- Missing parents (first 200) ---"
    $missingParents | Select-Object -First 200 type,file,reference,expected,reason | Format-Table -AutoSize | Out-String | Write-Host
}

Write-Host "Missing blockstate model JSONs: $($missingBlockstateModels.Count)"
if ($missingBlockstateModels.Count -gt 0) {
    Write-Host "--- Missing blockstate models (first 200) ---"
    $missingBlockstateModels | Select-Object -First 200 type,file,reference,expected,reason | Format-Table -AutoSize | Out-String | Write-Host
}

Write-Host "Missing item override model JSONs: $($missingItemOverrideModels.Count)"
if ($missingItemOverrideModels.Count -gt 0) {
    Write-Host "--- Missing item override models (first 200) ---"
    $missingItemOverrideModels | Select-Object -First 200 type,file,reference,expected,reason | Format-Table -AutoSize | Out-String | Write-Host
}

$exitCode = 0
if ($missingParents.Count -gt 0 -or $missingBlockstateModels.Count -gt 0 -or $missingItemOverrideModels.Count -gt 0) {
    $exitCode = 2
}

exit $exitCode
