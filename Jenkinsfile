pipeline {
  agent any

  options {
    timestamps()
    disableConcurrentBuilds()
  }

  triggers { pollSCM('H/2 * * * *') }

  environment {
    IMAGE             = 'miapp'
    APP_PORT_INTERNAL = '8080'
    SONARQUBE_ENV     = 'SonarLocal'
    MAIL_TO           = 'msblanco@unis.edu.gt, mariasofiablanco9@gmail.com'
    NETDATA_HOST        = '127.0.0.1'
    NETDATA_STATSD_PORT = '8125'
    NETDATA_JENKINS_URL = 'http://jenkins:8080'
  }

  stages {

    stage('Init Vars') {
      steps {
        script {
          // Base segura sin sudo (dentro de JENKINS_HOME)
          def base = env.JENKINS_HOME ?: '/var/jenkins_home'
          env.SQLITE_BASE = "${base}/sqlite"

          // Vars por rama
          env.PORT = (env.BRANCH_NAME == 'dev') ? '3001'
                   : (env.BRANCH_NAME == 'uat') ? '3002'
                   : '3003' // prod/master -> 3003

          env.SQLITE_DIR = "${env.SQLITE_BASE}/${env.BRANCH_NAME}"
          env.CNAME = "app_${env.BRANCH_NAME}"

          echo "Branch=${env.BRANCH_NAME}, PORT=${env.PORT}"
          echo "SQLite base=${env.SQLITE_BASE}, dir por rama=${env.SQLITE_DIR}"
          echo "Container name=${env.CNAME}"
        }
      }
    }

    stage('Prepare DB dir') {
      steps {
        sh '''
          set -e
          mkdir -p "${SQLITE_DIR}"
          chmod 777 "${SQLITE_DIR}"
          echo "SQLite dir: ${SQLITE_DIR}"
        '''
      }
    }

stage('Checkout') {
  steps {
    deleteDir()          // limpia workspace
    checkout scm
    sh '''
      echo "Branch: $(git rev-parse --abbrev-ref HEAD)"
      ls -la "$PWD/load-tests/k6" || true
    '''
  }
}


    stage('Build & Tests (Maven Wrapper)') {
      steps {
        script {
          def started = System.currentTimeMillis()
          try {
            sh '''
              set -e
              cd backend
              chmod +x mvnw
              ./mvnw -B -DskipTests=false clean verify
            '''
          } finally {
            def elapsed = System.currentTimeMillis() - started
            pushNetdataMetric('jenkins_pipeline_build_tests_duration_ms', elapsed, 'ms')
          }
        }
      }
      post {
        unstable { notify('INESTABLE', 'Unit tests con fallos') }
        failure  { notify('FALLÓ', 'Build o unit tests') }
      }
    }

    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv("${SONARQUBE_ENV}") {
          sh '''
            set -e
            cd backend
            ./mvnw -B org.sonarsource.scanner.maven:sonar-maven-plugin:4.0.0.4121:sonar \
              -Dsonar.projectKey=hospital-mbp-backend \
              -Dsonar.projectName="hospital-mbp-backend" \
              -Dsonar.java.binaries=target/classes \
              -Dsonar.host.url=$SONAR_HOST_URL \
              -Dsonar.token=$SONAR_AUTH_TOKEN
          '''
        }
      }
      post { failure { notify('FALLÓ', 'Análisis SonarQube') } }
    }

    stage('Quality Gate') {
      steps {
        script {
          def started = System.currentTimeMillis()
          try {
            timeout(time: 15, unit: 'MINUTES') {
              // Requiere webhook en Sonar → http(s)://<jenkins>/sonarqube-webhook/
              waitForQualityGate abortPipeline: true
            }
          } finally {
            def elapsed = System.currentTimeMillis() - started
            pushNetdataMetric('jenkins_pipeline_quality_gate_duration_ms', elapsed, 'ms')
          }
        }
      }
      post { failure { notify('FALLÓ', 'Quality Gate de SonarQube') } }
    }

    stage('Docker Build (backend/Dockerfile.jvm)') {
      steps {
        script {
          def started = System.currentTimeMillis()
          try {
            sh "docker build -f backend/Dockerfile.jvm -t ${IMAGE}:${env.BRANCH_NAME} backend"
          } finally {
            def elapsed = System.currentTimeMillis() - started
            pushNetdataMetric('jenkins_pipeline_docker_build_duration_ms', elapsed, 'ms')
          }
        }
      }
      post { failure { notify('FALLÓ', 'Docker build') } }
    }

    stage('Deploy per branch (SQLite)') {
      steps {
        script {
          def started = System.currentTimeMillis()
          try {
            sh '''
              set -e
              docker network create appnet || true
              docker rm -f "${CNAME}" || true

              # Montamos ${SQLITE_DIR} en /data y apuntamos la URL de SQLite ahí
              docker run -d --name "${CNAME}" --restart=unless-stopped \
                --network appnet \
                -p "${PORT}:${APP_PORT_INTERNAL}" \
                -v "${SQLITE_DIR}:/data" \
                -e QUARKUS_DATASOURCE_JDBC_URL="jdbc:sqlite:/data/app.db" \
                ${IMAGE}:${BRANCH_NAME}

              echo "✅ Desplegado ${CNAME} en puerto ${PORT} con DB en ${SQLITE_DIR} -> /data/app.db"
            '''
          } finally {
            def elapsed = System.currentTimeMillis() - started
            pushNetdataMetric('jenkins_pipeline_deploy_duration_ms', elapsed, 'ms')
          }
        }
      }
      post { failure { notify('FALLÓ', 'Despliegue por rama') } }
    }

stage('Start Monitoring Stack') {
  steps {
    withCredentials([
      string(credentialsId: 'slack-webhook', variable: 'SLACK_WEBHOOK_URL'),
      usernamePassword(credentialsId: 'smtp-creds', usernameVariable: 'SMTP_USER', passwordVariable: 'SMTP_PASS'),
      usernamePassword(credentialsId: 'jenkins-monitor-creds', usernameVariable: 'NETDATA_JENKINS_USER', passwordVariable: 'NETDATA_JENKINS_TOKEN')
    ]) {
      sh '''
        set -Eeuo pipefail

        MON_DIR="${WORKSPACE}/monitoring"
        OUT_DIR="${MON_DIR}/generated"
        COMPOSE_FILE="${MON_DIR}/docker-compose.yml"

        mkdir -p "${OUT_DIR}"
        # --- guard contra directorio con nombre de archivo ---
        if [ -d "${OUT_DIR}/alertmanager.yml" ]; then
          rm -rf "${OUT_DIR}/alertmanager.yml"
        fi

        ALERT_EMAILS="${ALERT_EMAILS:-msblanco@unis.edu.gt,mariasofiablanco9@gmail.com}"
        SMTP_FROM="${SMTP_FROM:-mariasofiablanco9@example.com}"
        SMTP_HOST="${SMTP_HOST:-smtp.example.com}"
        SMTP_PORT="${SMTP_PORT:-587}"
        SLACK_CHANNEL="${SLACK_CHANNEL:-#alerts}"

        NETDATA_OUT="${OUT_DIR}/netdata"
        rm -rf "${NETDATA_OUT}"
        mkdir -p "${NETDATA_OUT}/go.d"

        cat > "${OUT_DIR}/alertmanager.yml" <<YAML
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
YAML

        echo "--- alertmanager.yml (preview) ---"
        head -n 30 "${OUT_DIR}/alertmanager.yml" || true

        cat > "${NETDATA_OUT}/health_alarm_notify.conf" <<CONF
# Autogenerado por pipeline Jenkins
SLACK_WEBHOOK_URL="${SLACK_WEBHOOK_URL}"
SLACK_CHANNEL="${SLACK_CHANNEL}"
DEFAULT_RECIPIENT_SLACK="${SLACK_CHANNEL}"
SLACK_ICON_EMOJI=":hospital:"
SLACK_USERNAME="Netdata"
SEND_SLACK="YES"

SEND_EMAIL="YES"
DEFAULT_RECIPIENT_EMAIL="${ALERT_EMAILS}"
EMAIL_SENDER="netdata@hospital.local"

DEFAULT_RECIPIENT_TELEGRAM="_no_notification_"
DEFAULT_RECIPIENT_DISCORD="_no_notification_"
DEFAULT_RECIPIENT_PAGERDUTY="_no_notification_"
CONF

        JENKINS_CONF="${NETDATA_OUT}/go.d/jenkins.conf"
        if [ -z "${NETDATA_JENKINS_USER:-}" ] || [ -z "${NETDATA_JENKINS_TOKEN:-}" ]; then
          cat > "${JENKINS_CONF}" <<CONF
# Jenkins collector deshabilitado (faltan credenciales)
jobs: []
CONF
          echo "⚠️ No se generó configuración de Jenkins para Netdata (faltan credenciales)."
        else
          cat > "${JENKINS_CONF}" <<CONF
jobs:
  - name: jenkins_pipeline
    url: "${NETDATA_JENKINS_URL}"
    username: "${NETDATA_JENKINS_USER}"
    password: "${NETDATA_JENKINS_TOKEN}"
    update_every: 30
    timeout: 15
    collect_jobs: true
    jobs_include:
      - ".*"
CONF
        fi

        echo "--- netdata/health_alarm_notify.conf (preview) ---"
        head -n 40 "${NETDATA_OUT}/health_alarm_notify.conf" || true
        echo "--- netdata/go.d/jenkins.conf (preview) ---"
        head -n 40 "${NETDATA_OUT}/go.d/jenkins.conf" || true

        docker compose -f "${COMPOSE_FILE}" up -d --remove-orphans
      '''
    }
  }
}


  }

  post {
    success {
      script {
        pushNetdataMetric('jenkins_pipeline_alert_probe', 1, 'g')
      }
      sleep time: 5, unit: 'SECONDS'
      script {
        pushNetdataMetric('jenkins_pipeline_alert_probe', 0, 'g')
      }
      notify('OK', 'Pipeline completado')
    }
    failure { notify('FALLÓ', 'Fallo global del pipeline (catch-all)') }
    always  { sh "docker ps --format 'table {{.Names}}\\t{{.Ports}}\\t{{.Status}}'" }
  }
}

// ---- Helper de correo (emailext simple) ----
def pushNetdataMetric(String metric, def value, String type = 'ms') {
  def host = env.NETDATA_HOST?.trim()
  def port = env.NETDATA_STATSD_PORT?.trim()

  if (!host || !port) {
    echo "Netdata no configurado; omito métrica ${metric}"
    return
  }

  def payload = "${metric}:${value}|${type}"

  try {
    sh(
      label: "netdata ${metric}",
      script: """#!/bin/bash
set -eo pipefail
if [ -z "${env.NETDATA_HOST}" ] || [ -z "${env.NETDATA_STATSD_PORT}" ]; then
  exit 0
fi
echo -n '${payload}' > /dev/udp/${env.NETDATA_HOST}/${env.NETDATA_STATSD_PORT}
"""
    )
  } catch (err) {
    echo "No se pudo enviar métrica ${metric} a Netdata: ${err}"
  }
}

def notify(String estado, String motivo) {
  def asunto = "[${env.JOB_NAME}][${env.BRANCH_NAME}] #${env.BUILD_NUMBER} – ${estado}"
  def html = """
  <h2>${motivo}</h2>
  <table border="1" cellpadding="6" cellspacing="0">
    <tr><td><b>Job</b></td><td>${env.JOB_NAME}</td></tr>
    <tr><td><b>Rama</b></td><td>${env.BRANCH_NAME}</td></tr>
    <tr><td><b>Build</b></td><td>#${env.BUILD_NUMBER} – ${currentBuild.currentResult}</td></tr>
    <tr><td><b>Duración</b></td><td>${currentBuild.durationString}</td></tr>
    <tr><td><b>Consola</b></td><td><a href="${env.BUILD_URL}console">${env.BUILD_URL}console</a></td></tr>
    <tr><td><b>Artefactos</b></td><td><a href="${env.BUILD_URL}artifact">${env.BUILD_URL}artifact</a></td></tr>
  </table>
  <p>Quality Gate: revisar en Sonar → <i>Project: hospital-mbp-backend</i></p>
  """
  emailext(
    subject: asunto,
    to: env.MAIL_TO,
    mimeType: 'text/html',
    body: html,
    replyTo: 'msblanco@unis.edu.gt',
    from:  'CI Hospital <msblanco@unis.edu.gt>'
  )
}
