# Netdata alert routing (generado por el pipeline)
# Slack
SLACK_WEBHOOK_URL="__SLACK_WEBHOOK_URL__"
SLACK_CHANNEL="__SLACK_CHANNEL__"
DEFAULT_RECIPIENT_SLACK="__SLACK_CHANNEL__"
SLACK_ICON_EMOJI=":hospital:"
SLACK_USERNAME="Netdata"
SEND_SLACK="YES"

# Email
SEND_EMAIL="YES"
DEFAULT_RECIPIENT_EMAIL="__ALERT_EMAILS__"
EMAIL_SENDER="netdata@hospital.local"

# Evita notificaciones duplicadas en otros canales
DEFAULT_RECIPIENT_TELEGRAM="_no_notification_"
DEFAULT_RECIPIENT_DISCORD="_no_notification_"
DEFAULT_RECIPIENT_PAGERDUTY="_no_notification_"


