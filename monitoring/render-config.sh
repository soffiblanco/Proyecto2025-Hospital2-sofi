#!/usr/bin/env bash
set -euo pipefail

# Requiere 'envsubst' (paquete gettext). En Alpine: apk add --no-cache gettext
command -v envsubst >/dev/null || { echo "Falta 'envsubst' (gettext)"; exit 1; }

# Render Alertmanager
envsubst < alertmanager.yml.tpl > alertmanager.yml
echo "[render] alertmanager.yml listo"

# (opcional) valida YAML si tienes yq
# yq e '.' alertmanager.yml >/dev/null && echo "[render] YAML válido"
