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
        sh '''
          set -e
          cd backend
          chmod +x mvnw
          ./mvnw -B -DskipTests=false clean verify
        '''
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
        timeout(time: 15, unit: 'MINUTES') {
          // Requiere webhook en Sonar → http(s)://<jenkins>/sonarqube-webhook/
          waitForQualityGate abortPipeline: true
        }
      }
      post { failure { notify('FALLÓ', 'Quality Gate de SonarQube') } }
    }

    stage('Docker Build (backend/Dockerfile.jvm)') {
      steps {
        sh "docker build -f backend/Dockerfile.jvm -t ${IMAGE}:${env.BRANCH_NAME} backend"
      }
      post { failure { notify('FALLÓ', 'Docker build') } }
    }

    stage('Deploy per branch (SQLite)') {
      steps {
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
      }
      post { failure { notify('FALLÓ', 'Despliegue por rama') } }
    }

stage('Start Monitoring Stack') {
  steps {
    withCredentials([
      string(credentialsId: 'slack-webhook', variable: 'SLACK_WEBHOOK_URL'),
      usernamePassword(credentialsId: 'smtp-creds', usernameVariable: 'SMTP_USER', passwordVariable: 'SMTP_PASS')
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
        SMTP_FROM="${SMTP_FROM:-alerts@example.com}"
        SMTP_HOST="${SMTP_HOST:-smtp.example.com}"
        SMTP_PORT="${SMTP_PORT:-587}"
        SLACK_CHANNEL="${SLACK_CHANNEL:-#alerts}"

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

        docker compose -f "${COMPOSE_FILE}" up -d --remove-orphans
      '''
    }
  }
}

stage('Stress test (k6 via Docker)') {
  steps {
    sh '''#!/usr/bin/env bash
set -euo pipefail

TEST_DIR="$WORKSPACE/load-tests/k6"
TEST_FILE="$TEST_DIR/stress.js"
BASE_URL="http://localhost:${PORT:-3003}"

# 1) Asegurar carpeta
mkdir -p "$TEST_DIR"

# 2) Crear script si no existe o está vacío
if [ ! -s "$TEST_FILE" ]; then
  cat > "$TEST_FILE" <<'JS'
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 5,
  duration: '15s',
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<800'],
  },
};

export default function () {
  const base = __ENV.BASE_URL || 'http://localhost:3003';
  const res = http.get(`${base}/q/health`);
  check(res, { 'status 200': (r) => r.status === 200 });
  sleep(1);
}
JS
fi

# 3) Mostrar que el archivo EXISTE en host
echo "📁 Host $TEST_DIR:"
ls -la "$TEST_DIR"
echo "——— contenido stress.js ———"
head -n 20 "$TEST_FILE" || true

# 4) Verificar que el volumen tenga el archivo dentro del contenedor
docker run --rm -v "$TEST_DIR:/tests:ro" busybox sh -lc 'echo "📦 /tests:"; ls -la /tests; head -n 10 /tests/stress.js || true'

# 5) Ejecutar k6 (nota: usamos ruta ABSOLUTA dentro del contenedor)
docker run --rm --network host \
  -e BASE_URL="$BASE_URL" \
  -v "$TEST_DIR:/tests:ro" \
  grafana/k6:latest run /tests/stress.js
'''
  }
}






    stage('Stress test (JMeter via Docker)') {
      when { expression { fileExists('load-tests/jmeter/stress_test.jmx') } }
      steps {
        sh '''
          set -e
          mkdir -p load-tests/results
          docker run --rm --network host \
            -v "$PWD/load-tests:/tests" justb4/jmeter:5.6.3 \
            -n -t /tests/jmeter/stress_test.jmx \
            -l /tests/results/stress_results_${BRANCH_NAME}.jtl \
            -JBASE_HOST=localhost -JBASE_PORT=${PORT}
        '''
      }
      post { failure { notify('FALLÓ', 'Stress test JMeter') } }
    }
  }

  post {
    success { notify('OK', 'Pipeline completado') }
    failure { notify('FALLÓ', 'Fallo global del pipeline (catch-all)') }
    always  { sh "docker ps --format 'table {{.Names}}\\t{{.Ports}}\\t{{.Status}}'" }
  }
}

// ---- Helper de correo (emailext simple) ----
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
