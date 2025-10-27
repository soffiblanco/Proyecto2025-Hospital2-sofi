Param(
    [string]$BaseUrl = "http://localhost:8080",
    [bool]$DoAuth = $false,
    [string]$LoginEmail = "admin@example.com",
    [string]$LoginPassword = "admin123",
    [int]$Stage1Vus = 20,
    [string]$Stage1Duration = "30s",
    [int]$Stage2Vus = 50,
    [string]$Stage2Duration = "60s",
    [int]$Stage3Vus = 0,
    [string]$Stage3Duration = "30s",
    [string]$OutFile = "load-tests\results\k6_results.json",
    [string]$ScriptPath = "load-tests\k6\stress_test.js"
)

$ErrorActionPreference = "Stop"

# Crear carpeta de resultados
$resultsDir = Split-Path -Parent $OutFile
if ([string]::IsNullOrWhiteSpace($resultsDir)) { $resultsDir = "." }
New-Item -ItemType Directory -Force -Path $resultsDir | Out-Null

if (-not (Test-Path $ScriptPath)) {
    throw "No se encontró el script k6: $ScriptPath"
}

$env:BASE_URL = $BaseUrl
$env:DO_AUTH = if ($DoAuth) { "true" } else { "false" }
$env:LOGIN_EMAIL = $LoginEmail
$env:LOGIN_PASSWORD = $LoginPassword
$env:STAGE_1_VUS = $Stage1Vus
$env:STAGE_1_DURATION = $Stage1Duration
$env:STAGE_2_VUS = $Stage2Vus
$env:STAGE_2_DURATION = $Stage2Duration
$env:STAGE_3_VUS = $Stage3Vus
$env:STAGE_3_DURATION = $Stage3Duration

$arguments = @(
    "run",
    $ScriptPath,
    "--out", "json=$OutFile"
)

Write-Host "Ejecutando k6: k6 $($arguments -join ' ')" -ForegroundColor Cyan

& k6 @arguments

if ($LASTEXITCODE -ne 0) {
    throw "k6 terminó con código $LASTEXITCODE"
}

Write-Host "Listo. Resultados en: $OutFile" -ForegroundColor Green
