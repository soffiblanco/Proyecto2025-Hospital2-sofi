Param(
    [string]$JMeterHome = $env:JMETER_HOME,
    [string]$Protocol = "http",
    [string]$Host = "localhost",
    [int]$Port = 8080,
    [int]$Threads = 50,
    [int]$RampUp = 30,
    [int]$Duration = 120,
    [bool]$DoAuth = $false,
    [string]$LoginEmail = "admin@example.com",
    [string]$LoginPassword = "admin123",
    [string]$ResultsFile = "load-tests\results\jmeter_results.jtl",
    [string]$PlanPath = "load-tests\jmeter\stress_plan.jmx"
)

$ErrorActionPreference = "Stop"

# Crear carpeta de resultados
$resultsDir = Split-Path -Parent $ResultsFile
if ([string]::IsNullOrWhiteSpace($resultsDir)) { $resultsDir = "." }
New-Item -ItemType Directory -Force -Path $resultsDir | Out-Null

# Resolver ruta de JMeter
if ([string]::IsNullOrWhiteSpace($JMeterHome)) {
    $jmeterBat = "jmeter.bat"  # requiere que JMeter esté en el PATH
} else {
    $jmeterBat = Join-Path $JMeterHome "bin\jmeter.bat"
}

if (-not (Test-Path $PlanPath)) {
    throw "No se encontró el plan JMeter: $PlanPath"
}

$doAuthStr = if ($DoAuth) { "true" } else { "false" }

$arguments = @(
    "-n",
    "-t", $PlanPath,
    "-Jprotocol=$Protocol",
    "-Jhost=$Host",
    "-Jport=$Port",
    "-Jthreads=$Threads",
    "-JrampUp=$RampUp",
    "-Jduration=$Duration",
    "-JdoAuth=$doAuthStr",
    "-JloginEmail=$LoginEmail",
    "-JloginPassword=$LoginPassword",
    "-JresultsFile=$ResultsFile"
)

Write-Host "Ejecutando JMeter: $jmeterBat $($arguments -join ' ')" -ForegroundColor Cyan

& $jmeterBat @arguments

if ($LASTEXITCODE -ne 0) {
    throw "JMeter terminó con código $LASTEXITCODE"
}

Write-Host "Listo. Resultados en: $ResultsFile" -ForegroundColor Green
