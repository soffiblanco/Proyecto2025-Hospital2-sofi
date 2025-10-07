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
