#!/usr/bin/env bash
set -euo pipefail
mkdir -p backend/data
if [[ ! -f backend/data/dev.db ]]; then
  echo "No existe backend/data/dev.db; levanta primero el perfil dev para crearla."
  exit 1
fi
cp -f backend/data/dev.db backend/data/uat.db
cp -f backend/data/dev.db backend/data/prod.db
echo "Clonado: dev.db → uat.db y prod.db"
