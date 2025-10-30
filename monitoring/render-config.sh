#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TEMPLATE_DIR="${ROOT_DIR}/templates"
OUT_DIR="${ROOT_DIR}/generated"

mkdir -p "${OUT_DIR}"

# Defaults for optional variables
export SLACK_CHANNEL="${SLACK_CHANNEL:-#alerts}"
export SMTP_PORT="${SMTP_PORT:-587}"

missing_env=()
for var in SLACK_WEBHOOK_URL ALERT_EMAILS SMTP_FROM SMTP_HOST; do
  if [[ -z "${!var:-}" ]]; then
    missing_env+=("$var")
  fi
done

if (( ${#missing_env[@]} > 0 )); then
  printf '❌ Faltan variables de entorno requeridas: %s\n' "${missing_env[*]}" >&2
  printf '   Establece cada variable y vuelve a ejecutar, por ejemplo:\n' >&2
  printf '   export SLACK_WEBHOOK_URL=https://hooks.slack.com/...\n' >&2
  exit 1
fi

# Permitir dejar vacíos los campos de autenticación SMTP si se usa relay por IP
export SMTP_USER="${SMTP_USER:-}"
export SMTP_PASS="${SMTP_PASS:-}"

if [[ ! -f "${TEMPLATE_DIR}/alertmanager.yml.tpl" ]]; then
  echo "No se encuentra template de Alertmanager" >&2
  exit 1
fi

envsubst < "${TEMPLATE_DIR}/alertmanager.yml.tpl" > "${OUT_DIR}/alertmanager.yml"
envsubst < "${TEMPLATE_DIR}/grafana-contact-points.yaml.tpl" > "${OUT_DIR}/grafana-contact-points.yaml"

printf '✅ Configuraciones generadas en %s\n' "${OUT_DIR}"

