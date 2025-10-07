#!/usr/bin/env bash
set -euo pipefail

# Directorio raíz del repo y rama actual
REPO_DIR="$(git rev-parse --show-toplevel 2>/dev/null || pwd)"
BRANCH="$(git rev-parse --abbrev-ref HEAD 2>/dev/null || echo dev)"

# Rama -> perfil y archivo RELATIVO dentro de backend/
case "$BRANCH" in
  dev)    PROFILE="dev";  DB_REL="data/dev.db"  ;;
  uat)    PROFILE="uat";  DB_REL="data/uat.db"  ;;
  master) PROFILE="prod"; DB_REL="data/prod.db" ;;
  *)      PROFILE="dev";  DB_REL="data/dev.db"  ;;
esac

# Asegura carpeta de datos
mkdir -p "$REPO_DIR/backend/data"

# Clona dev.db si la DB destino no existe (para uat/prod)
if [[ ! -f "$REPO_DIR/backend/$DB_REL" ]]; then
  if [[ "$DB_REL" != "data/dev.db" && -f "$REPO_DIR/backend/data/dev.db" ]]; then
    echo "→ Clonando dev.db → $DB_REL"
    cp -f "$REPO_DIR/backend/data/dev.db" "$REPO_DIR/backend/$DB_REL"
  else
    echo "→ $DB_REL no existe. Hibernate la creará/actualizará."
  fi
fi

# ¡Clave!: exportar DB_FILE como **ruta ABSOLUTA**
export DB_FILE="$REPO_DIR/backend/$DB_REL"
echo "→ Branch: $BRANCH | Profile: $PROFILE | DB_FILE=$DB_FILE"

# Arrancar Quarkus sin tests continuos
cd "$REPO_DIR/backend"
if [[ -x "./mvnw" ]]; then
  ./mvnw quarkus:dev -Dquarkus.profile="$PROFILE" -Dquarkus.test.continuous-testing=disabled
else
  mvn quarkus:dev -Dquarkus.profile="$PROFILE" -Dquarkus.test.continuous-testing=disabled
fi
