apiVersion: 1
contactPoints:
  - orgId: 1
    name: slack-and-email
    receivers:
      - uid: slack_receiver
        type: slack
        settings:
          url: ${SLACK_WEBHOOK_URL}
          recipient: '${SLACK_CHANNEL}'
          title: 'Grafana ALERT: {{ .Title }}'
          text: '{{ .Message }}'
      - uid: email_receiver
        type: email
        settings:
          addresses: '${ALERT_EMAILS}'

