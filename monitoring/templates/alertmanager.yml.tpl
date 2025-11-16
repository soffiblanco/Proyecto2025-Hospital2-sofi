
global:
  resolve_timeout: 5m

  # SMTP global (usará tus variables de entorno)
  smtp_from:        '${SMTP_FROM}'
  smtp_smarthost:   '${SMTP_HOST}:${SMTP_PORT}'
  smtp_auth_username:'${SMTP_USER}'
  smtp_auth_password:'${SMTP_PASS}'
  smtp_require_tls: true

route:
  receiver: 'team-alerts'
  group_by: ['alertname']
  group_wait: 30s
  group_interval: 2m
  repeat_interval: 2h

receivers:
  - name: 'team-alerts'

    # ======= Slack (Incoming Webhook) =======
    slack_configs:
      - send_resolved: true
        api_url:  '${SLACK_WEBHOOK_URL}'   # URL del Incoming Webhook
        channel:  '${SLACK_CHANNEL}'       # Ej: #monitoreo
        username: 'ProyectoHospital'
        title:   'ALERTA {{ .Status | toUpper }}: {{ .CommonLabels.alertname }}'
        text: |-
          *Estado:* {{ .Status }}
          *Severidad:* {{ .CommonLabels.severity }}
          *Etiquetas:* {{ .CommonLabels }}
          *Resumen:*
          {{ range .Alerts }}• {{ .Annotations.summary }} ({{ .Labels.severity }}){{ end }}

    # ======= Email =======
    email_configs:
      - to: '${ALERT_EMAILS}'     # Ej: msblanco@unis.edu.gt,mariasofiablanco9@gmail.com
