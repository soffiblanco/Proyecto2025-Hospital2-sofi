route:
  receiver: 'team-alerts'
  group_by: ['alertname']
  group_wait: 30s
  group_interval: 2m
  repeat_interval: 2h

receivers:
  - name: 'team-alerts'
    slack_configs:
      - send_resolved: true
        api_url: '${SLACK_WEBHOOK_URL}'
        channel: '${SLACK_CHANNEL}'
        title: 'ALERTA {{ .Status }}: {{ .CommonLabels.alertname }}'
        text: |
          Detalles:
          {{ range .Alerts }}• {{ .Annotations.summary }} ({{ .Labels.severity }})
          {{ end }}
    email_configs:
      - to: '${ALERT_EMAILS}'
        from: '${SMTP_FROM}'
        smarthost: '${SMTP_HOST}:${SMTP_PORT}'
        auth_username: '${SMTP_USER}'
        auth_identity: '${SMTP_USER}'
        auth_password: '${SMTP_PASS}'
        require_tls: true


