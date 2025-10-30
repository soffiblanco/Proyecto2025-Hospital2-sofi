# Monitoring stack

Pasos para dejar Prometheus, Grafana y Alertmanager funcionando con las alertas y dashboards provistos.

## Requisitos

- Docker y Docker Compose instalados en la VM.
- Contenedores `app_dev`, `app_uat` y `app_prod` corriendo en la red `appnet` (los levanta el Jenkinsfile).
- Jenkins, SonarQube, etc. también conectados a `appnet` para que sus métricas estén disponibles.

## 1. Generar configuraciones con tus credenciales

El stack usa plantillas para inyectar el webhook de Slack y los datos SMTP.

```bash
cd monitoring
export SLACK_WEBHOOK_URL="https://hooks.slack.com/services/..."
export ALERT_EMAILS="ops@tu-dominio.com,dev@tu-dominio.com"
export SMTP_FROM="alertas@tu-dominio.com"
export SMTP_HOST="smtp-relay.gmail.com"
# Opcional si tu relay exige autenticación
export SMTP_USER=""
export SMTP_PASS=""
export SLACK_CHANNEL="#alerts"
export SMTP_PORT=587

bash render-config.sh
```

Esto genera:

- `generated/alertmanager.yml`
- `generated/grafana-contact-points.yaml`

## 2. Levantar el stack

```bash
docker compose up -d
```

Servicios expuestos:

- Prometheus: http://<VM>:9090
- Grafana: http://<VM>:3000 (admin / admin)
- Alertmanager: http://<VM>:9093
- Blackbox exporter: http://<VM>:9115
- cAdvisor: http://<VM>:8085

## 3. Dashboards y alertas

Grafana carga automáticamente:

- `App Performance`: 4 gráficas (disponibilidad, p95, CPU y RAM de los contenedores `app_dev/uat/prod`).
- `Pipeline Performance`: 4 gráficas (status de Jenkins, latencia de blackbox, CPU host, load).

Prometheus evalúa las reglas en `prometheus/rules/alerts.yml`:

1. `HostCPUHigh` (CPU host > 70%).
2. `BackendAvailabilityLow` (disponibilidad < 95%).
3. `BackendLatencyHighP95` (p95 > 1s).
4. `BackendMemoryHigh` (memoria contenedores > ~600MB).

Alertmanager y Grafana envían notificaciones usando las credenciales generadas en el paso 1.

## 4. Jenkins

Para que la gráfica de pipeline muestre datos, instala en Jenkins el plugin **Prometheus metrics**, habilita el endpoint `/prometheus` y conecta el contenedor a `appnet`:

```bash
docker network connect appnet jenkins || true
```

## 5. Detener

```bash
docker compose down
```


