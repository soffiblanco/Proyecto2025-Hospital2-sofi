# Ambiente de Desarrollo

## Requisitos
- Java 17+
- Maven (o usar el wrapper incluido `./mvnw`)
- Node.js 18+ y npm

## Preparación inicial
1. Crear la carpeta de datos para SQLite: `mkdir -p backend/data`
2. Verificar que `backend/data/` permanezca vacío (los `.db` se generan al levantar el backend).

## Backend (Quarkus)
- Con wrapper: `./mvnw quarkus:dev -Dquarkus.profile=dev`
- Con Maven local: `mvn quarkus:dev -Dquarkus.profile=dev`

El backend expone la API en `http://localhost:8080`, utiliza la base `backend/data/dev.db` y permite CORS hacia `http://localhost:5173`.

### Cómo se elige la base de datos
- Por defecto, cada perfil usa:
  - dev  → `backend/data/dev.db`
  - uat  → `backend/data/uat.db`
  - prod → `backend/data/prod.db`
- Puedes sobrescribir la ruta con la variable de entorno `DB_FILE`:
  - Ejemplo: `DB_FILE=./backend/data/otra.db ./mvnw quarkus:dev -Dquarkus.profile=dev`

### Levantar por rama (recomendado)
- Estar en la rama correspondiente (`dev`/`uat`/`master`).
- Ejecutar: `backend/scripts/run-by-branch.sh`
- Si la base del perfil no existe:
  - Si estás en `uat`/`master` y existe `dev.db`, el script clona `dev.db` a la base destino.
  - Si no, se crea vacía y Hibernate la inicializa (en dev: `generation=update`).

### Clonar manualmente
- `backend/scripts/clone-db.sh` → copia `dev.db` a `uat.db` y `prod.db`.

## Frontend (Vite)
1. Instalar dependencias: `npm ci`
2. Levantar en modo desarrollo: `npm run dev`

El frontend consume la API desde `import.meta.env.VITE_API_URL` definido en `.env.development`.

### Puertos
- Backend: `http://localhost:8080`
- Health (Quarkus): `http://localhost:8080/q/health`
- Frontend: `http://localhost:5173`

## Verificación rápida
- Backend: `http://localhost:8080`
- Health (Quarkus): `http://localhost:8080/q/health`
- Frontend: `http://localhost:5173`

## Monitoreo CI/CD (Prometheus, Grafana y Netdata)

El pipeline de Jenkins levanta automáticamente el stack definido en `monitoring/docker-compose.yml`, que ahora incluye Netdata para métricas del host y del pipeline.

### Credenciales requeridas en Jenkins
Configura los siguientes `Credentials`:

- `slack-webhook` → tipo **Secret text** con el Webhook de Slack.
- `smtp-creds` → tipo **Username with password** (usuario/contraseña SMTP).
- `jenkins-monitor-creds` → tipo **Username with password** (usuario Jenkins + API Token) usado por Netdata para consultar el estado de los jobs.

Además, puedes sobrescribir vía variables del job:

| Variable | Descripción | Default |
|----------|-------------|---------|
| `ALERT_EMAILS` | Lista de correos para alertas | `msblanco@unis.edu.gt,mariasofiablanco9@gmail.com` |
| `SLACK_CHANNEL` | Canal de Slack destino | `#alerts` |
| `SMTP_FROM`, `SMTP_HOST`, `SMTP_PORT` | Configuración SMTP opcional | Ver `Jenkinsfile` |

### Levantar manualmente el stack

```sh
cd monitoring
docker compose up -d
```

Servicios expuestos:

- Prometheus → `http://localhost:9090`
- Grafana → `http://localhost:3000` (admin/admin)
- Alertmanager → `http://localhost:9093`
- Netdata → `http://localhost:19999`

### Gráficas y alertas en Netdata

- **Performance (automáticas del agente)**: CPU (`system.cpu`), RAM (`system.ram`), Disco (`disk.util`), Red (`system.net`). Las alertas personalizadas están en `monitoring/netdata/health.d/performance.conf`.
- **Pipeline (StatsD)**: tiempos de `Build & Tests`, `Quality Gate`, `Docker Build` y `Deploy`, enviados desde el `Jenkinsfile` vía StatsD a Netdata. Las reglas se encuentran en `monitoring/netdata/health.d/pipeline.conf`.

Todas las alarmas se envían por correo y Slack mediante el archivo generado `monitoring/generated/netdata/health_alarm_notify.conf`.

### Cómo probar el flujo completo

1. Ejecuta el pipeline (o corre el stack manualmente si quieres validar local).
2. Revisa en `docker ps` que el contenedor `netdata` esté activo y que exposición 19999/8125 esté disponible.
3. Accede a `http://localhost:19999` y busca las gráficas bajo:
   - `Netdata Monitoring / System Overview` para desempeño.
   - `StatsD` → métricas `jenkins_pipeline_*`.
4. Fuerza alertas modificando umbrales en `monitoring/netdata/health.d/*.conf` o generando cargas artificiales; valida recepción en correo y Slack.
