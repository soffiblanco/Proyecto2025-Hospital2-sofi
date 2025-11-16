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
      steps { checkout scm }
      post { failure { notify('FALLÓ', 'Checkout del repo') } }
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
      script {
        // Defaults en Groovy (sin interpolación)
        def slackChannel = (env.SLACK_CHANNEL?.trim()) ? env.SLACK_CHANNEL.trim() : '#alerts'
        def smtpFrom     = (env.SMTP_FROM?.trim()) ? env.SMTP_FROM.trim() : 'alerts@example.com'
        def smtpHost     = (env.SMTP_HOST?.trim()) ? env.SMTP_HOST.trim() : 'smtp.example.com'
        def smtpPort     = (env.SMTP_PORT?.trim()) ? env.SMTP_PORT.trim() : '587'
        def alertEmails  = (env.ALERT_EMAILS?.trim()) ? env.ALERT_EMAILS.trim() : 'msblanco@unis.edu.gt,mariasofiablanco9@gmail.com'
        def MON_DIR      = "${env.WORKSPACE}/monitoring"

        // 1) Asegura carpeta "generated" y valida que el template exista donde corresponde
        sh '''
          set -e
          mkdir -p "${MON_DIR}/generated"
          echo "Listando monitoring/templates:"
          ls -la "${MON_DIR}/templates" || true
          test -f "${MON_DIR}/templates/alertmanager.yml.tpl" || { echo "❌ No existe ${MON_DIR}/templates/alertmanager.yml.tpl"; exit 1; }
        '''

        // 2) Renderiza DESDE templates/ -> HACIA generated/ (sin interpolar secretos en Groovy)
        sh '''
          docker run --rm \
            -e SLACK_WEBHOOK_URL="$SLACK_WEBHOOK_URL" \
            -e ALERT_EMAILS="$ALERT_EMAILS" \
            -e SMTP_FROM="$SMTP_FROM" \
            -e SMTP_HOST="$SMTP_HOST" \
            -e SMTP_USER="$SMTP_USER" \
            -e SMTP_PASS="$SMTP_PASS" \
            -e SLACK_CHANNEL="$SLACK_CHANNEL" \
            -e SMTP_PORT="$SMTP_PORT" \
            -v "${MON_DIR}:/w" alpine:3.20 sh -lc '
              set -e
              apk add --no-cache gettext >/dev/null
              cd /w
              mkdir -p generated
              envsubst < templates/alertmanager.yml.tpl > generated/alertmanager.yml
              echo "--- alertmanager.yml (primeras líneas) ---"
              head -n 30 generated/alertmanager.yml || true
            '
        '''

        // 3) Levanta el stack con el compose que monta ./generated/alertmanager.yml
        sh '''
          set -e
          cd "${MON_DIR}"
          docker compose -f docker-compose.monitor.yml up -d --remove-orphans
        '''
      }
    }
  }
}





    stage('Stress test (k6 via Docker)') {
      when { expression { fileExists('load-tests/k6/stress.js') } }
      steps {
        sh '''
          set -e
          BASE_URL="http://localhost:${PORT}"
          echo "k6 BASE_URL=${BASE_URL}"
          docker run --rm --network host \
            -e BASE_URL="${BASE_URL}" \
            -v "$PWD/load-tests/k6:/tests" grafana/k6 run /tests/stress.js
        '''
      }
      post { failure { notify('FALLÓ', 'Stress test k6') } }
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
