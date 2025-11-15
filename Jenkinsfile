pipeline {
  agent any

  options {
    timestamps()
    disableConcurrentBuilds()
  }

  environment {
    IMAGE             = 'miapp'
    APP_PORT_INTERNAL = '8080'
    SONARQUBE_ENV     = 'SonarLocal'
    MAIL_TO           = 'msblanco@unis.edu.gt, mariasofiablanco9@gmail.com'

    // Base de datos SQLite SIN sudo, dentro de JENKINS_HOME
    SQLITE_BASE = "${env.JENKINS_HOME ?: '/var/jenkins_home'}/sqlite"
    SQLITE_DIR  = "${SQLITE_BASE}/${env.BRANCH_NAME}"  // prod/dev/uat/master
  }

  triggers { pollSCM('H/2 * * * *') }

  stages {

    stage('Checkout') {
      steps { checkout scm }
      post { failure { notify('FALLÓ', 'Checkout del repo') } }
    }

    stage('Prepare DB dir') {
      when { anyOf { branch 'dev'; branch 'uat'; branch 'master'; branch 'prod' } }
      steps {
        sh '''
          set -e
          echo "JENKINS_HOME=${JENKINS_HOME}"
          echo "Creando directorio SQLite: ${SQLITE_DIR}"
          mkdir -p "${SQLITE_DIR}"
          chmod 777 "${SQLITE_DIR}" || true
          ls -ld "${SQLITE_DIR}"
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
      post { failure { notify('FALLÓ', 'Ejecución del análisis SonarQube') } }
    }

    stage('Quality Gate') {
      steps {
        timeout(time: 15, unit: 'MINUTES') {
          // Requiere webhook en SonarQube -> http(s)://<jenkins>/sonarqube-webhook/
          waitForQualityGate abortPipeline: true
        }
      }
      post { failure { notify('FALLÓ', 'Quality Gate de SonarQube (deuda técnica/bugs o timeout)') } }
    }

    stage('Docker Build (backend/Dockerfile.jvm)') {
      steps {
        // MUY IMPORTANTE: el contexto es "backend", por eso el Dockerfile
        // debe usar rutas SIN el prefijo "backend/"
        sh "docker build -f backend/Dockerfile.jvm -t ${IMAGE}:${env.BRANCH_NAME} backend"
      }
      post { failure { notify('FALLÓ', 'Docker build') } }
    }

    stage('Deploy per branch (SQLite)') {
      steps {
        script {
          def port = (env.BRANCH_NAME == 'dev') ? '3001'
                    : (env.BRANCH_NAME == 'uat') ? '3002'
                    : '3003' // master/prod

          def cname = "app_${env.BRANCH_NAME}"

          sh '''
            set -e
            docker network create appnet || true
          '''

          sh "docker rm -f ${cname} || true"

          // Montamos SQLITE_DIR como /data y forzamos la URL de SQLite
          sh """
            docker run -d --name ${cname} --restart=unless-stopped \\
              --network appnet \\
              -p ${port}:${APP_PORT_INTERNAL} \\
              -v "${SQLITE_DIR}:/data" \\
              -e QUARKUS_DATASOURCE_JDBC_URL="jdbc:sqlite:/data/app.db" \\
              ${IMAGE}:${env.BRANCH_NAME}
          """
          echo "✅ Desplegado ${cname} en puerto ${port} con DB en ${SQLITE_DIR} -> /data/app.db"
        }
      }
      post { failure { notify('FALLÓ', 'Despliegue por rama') } }
    }

    stage('Start Monitoring Stack') {
      steps {
        sh '''
          set -e
          cd monitoring
          export SLACK_WEBHOOK_URL=${SLACK_WEBHOOK_URL:?SLACK_WEBHOOK_URL}
          export ALERT_EMAILS=${ALERT_EMAILS:?ALERT_EMAILS}
          export SMTP_FROM=${SMTP_FROM:?SMTP_FROM}
          export SMTP_HOST=${SMTP_HOST:?SMTP_HOST}
          export SMTP_USER=${SMTP_USER:-}
          export SMTP_PASS=${SMTP_PASS:-}
          export SLACK_CHANNEL=${SLACK_CHANNEL:-#alerts}
          export SMTP_PORT=${SMTP_PORT:-587}
          bash render-config.sh
          docker compose up -d --remove-orphans
        '''
      }
    }

    stage('Stress test (k6 via Docker)') {
      steps {
        sh '''
          set -e
          PORT=$( [ "${BRANCH_NAME}" = "dev" ] && echo 3001 || ( [ "${BRANCH_NAME}" = "uat" ] && echo 3002 || echo 3003 ) )
          BASE_URL="http://localhost:${PORT}"
          echo "k6 BASE_URL=${BASE_URL}"
          docker run --rm --network host -e BASE_URL="${BASE_URL}" \
            -v "$PWD/load-tests/k6:/tests" grafana/k6 run /tests/stress.js
        '''
      }
    }

    stage('Stress test (JMeter via Docker)') {
      steps {
        sh '''
          set -e
          PORT=$( [ "${BRANCH_NAME}" = "dev" ] && echo 3001 || ( [ "${BRANCH_NAME}" = "uat" ] && echo 3002 || echo 3003 ) )
          mkdir -p load-tests/results
          docker run --rm --network host -v "$PWD/load-tests:/tests" justb4/jmeter:5.6.3 \
            -n -t /tests/jmeter/stress_test.jmx -l /tests/results/stress_results_${BRANCH_NAME}.jtl \
            -JBASE_HOST=localhost -JBASE_PORT=${PORT}
        '''
      }
    }
  }

  post {
    success { notify('OK', 'Pipeline completado') }
    failure { notify('FALLÓ', 'Fallo global del pipeline (catch-all)') }
    always  { sh "docker ps --format 'table {{.Names}}\\t{{.Ports}}\\t{{.Status}}'" }
  }
}

// ---- Helper para correo HTML (emailext) ----
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
  <p>Quality Gate (si aplica): revisa Sonar → <i>Project: hospital-mbp-backend</i></p>
  """
  emailext(
    subject: asunto,
    to: env.MAIL_TO,
    recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']],
    mimeType: 'text/html',
    body: html,
    replyTo: 'msblanco@unis.edu.gt',
    from:  'CI Hospital <msblanco@unis.edu.gt>'
  )
}
