# Pruebas de estrés: JMeter y k6

Este directorio contiene planes/scripts para probar carga y estrés del backend (Quarkus) corriendo, por defecto, en `http://localhost:8080`.

## Requisitos
- Java 8+ (para JMeter)
- Apache JMeter 5.6+ (descargar portable)
- k6 0.48+ (instalable en Windows)

## Estructura
- `jmeter/stress_plan.jmx`: plan de JMeter parametrizable
- `k6/stress_test.js`: script de k6 con stages y login opcional
- `scripts/run-jmeter.ps1`: ejecuta JMeter con parámetros
- `scripts/run-k6.ps1`: ejecuta k6 con parámetros
- `results/`: carpeta sugerida para resultados (se crea al ejecutar)

Crea la carpeta de resultados si no existe:
```powershell
New-Item -ItemType Directory -Force -Path "load-tests/results" | Out-Null
```

---

## Ejecutar con un solo comando (recomendado)

- JMeter (PowerShell):
```powershell
# Si JMeter no está en PATH, especifica su ruta:
$env:JMETER_HOME = "C:\tools\apache-jmeter-5.6.2"

# Ejecutar con valores por defecto (http://localhost:8080)
.\load-tests	ests.ps1 # alias si lo creas, o usa:
.\load-tests
un.ps1  # (ejemplo) — si prefieres, usa directamente:
.\load-tests\scripts\run-jmeter.ps1 -Threads 50 -RampUp 30 -Duration 120 -DoAuth:$false

# Con autenticación y archivo de resultados personalizado
.\load-tests\scripts\run-jmeter.ps1 -DoAuth:$true -LoginEmail "admin@example.com" -LoginPassword "admin123" -ResultsFile "load-tests\results\jmeter_results.jtl"
```

- k6 (PowerShell):
```powershell
.\load-tests\scripts\run-k6.ps1 -BaseUrl "http://localhost:8080" -DoAuth:$false -OutFile "load-tests\results\k6_results.json"
# Con auth y stages custom
.\load-tests\scripts\run-k6.ps1 -DoAuth:$true -LoginEmail "admin@example.com" -LoginPassword "admin123" -Stage1Vus 20 -Stage1Duration "30s" -Stage2Vus 50 -Stage2Duration "60s" -Stage3Vus 0 -Stage3Duration "30s"
```

Nota PowerShell: para invocar un ejecutable o script por ruta usa el operador `&` o el prefijo `.\`. Ejemplo:
```powershell
& "$env:JMETER_HOME\bin\jmeter.bat" -n -t "load-tests\jmeter\stress_plan.jmx" -Jport=8080
```

---

## JMeter (modo no-GUI)

Variables principales (sobrescribir con `-J`):
- `protocol` (http/https) – default: `http`
- `host` – default: `localhost`
- `port` – default: `8080`
- `threads` – usuarios concurrentes – default: `50`
- `rampUp` – segundos de rampa – default: `30`
- `duration` – segundos totales – default: `120`
- `doAuth` – `true`/`false` para login previo – default: `false`
- `loginEmail`, `loginPassword`
- `resultsFile` – ruta del archivo `.jtl` – default: `load-tests\results\jmeter_results.jtl`

Ejemplo (PowerShell):
```powershell
$env:JMETER_HOME = "C:\tools\apache-jmeter-5.6.2"
New-Item -ItemType Directory -Force -Path "load-tests/results" | Out-Null
& "$env:JMETER_HOME\bin\jmeter.bat" -n -t "load-tests\jmeter\stress_plan.jmx" `
  -Jhost=localhost -Jport=8080 -Jthreads=100 -JrampUp=60 -Jduration=300 `
  -JdoAuth=false -JresultsFile="load-tests\results\jmeter_results.jtl"
```

---

## k6

Parámetros por variables de entorno:
- `BASE_URL` – default: `http://localhost:8080`
- `DO_AUTH` – `true`/`false` – default: `false`
- `LOGIN_EMAIL`, `LOGIN_PASSWORD`
- Stages: `STAGE_1_VUS`, `STAGE_1_DURATION`, `STAGE_2_VUS`, `STAGE_2_DURATION`, `STAGE_3_VUS`, `STAGE_3_DURATION`

Ejemplo (PowerShell):
```powershell
$env:BASE_URL = "http://localhost:8080"
$env:DO_AUTH = "false"
$env:STAGE_1_VUS = 20; $env:STAGE_1_DURATION = "30s"
$env:STAGE_2_VUS = 50; $env:STAGE_2_DURATION = "1m"
$env:STAGE_3_VUS = 0;  $env:STAGE_3_DURATION = "30s"

k6 run "load-tests\k6\stress_test.js" --out json="load-tests\results\k6_results.json"
```

---

## Endpoints cubiertos (inicial)
- `GET /empleado`
- `GET /usuarios`
- Login opcional: `POST /usuarios/login`

Puedes extender fácilmente ambos planes agregando más endpoints del backend.
