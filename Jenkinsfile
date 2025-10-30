pipeline {
  agent any

  options {
    timestamps()
    disableConcurrentBuilds()
  }

  environment {
    IMAGE          = 'miapp'         // imagen local por rama: miapp:<branch>
    APP_PORT_INTERNAL = '8080'       // Quarkus expone 8080 dentro del contenedor
    SONARQUBE_ENV  = 'SonarLocal'    // nombre del servidor Sonar en Jenkins
  }

  triggers { pollSCM('H/2 * * * *') } // sin webhooks

  stages {

    stage('Checkout') {
      steps { checkout scm }
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
    }

    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('SonarLocal') {
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
    }
    
    

    stage('Quality Gate') {
      steps {
        timeout(time: 5, unit: 'MINUTES') {
          waitForQualityGate abortPipeline: true
        }
      }
    }

    stage('Docker Build (backend/Dockerfile.jvm)') {
      steps {
        // Usa TU Dockerfile dentro de backend y el contexto "backend/"
        sh "docker build -f backend/Dockerfile.jvm -t ${IMAGE}:${env.BRANCH_NAME} backend"
      }
    }

    stage('Deploy per branch (SQLite)') {
      steps {
        script {
          // Puertos externos (uno por rama)
          def port   = (env.BRANCH_NAME == 'dev') ? '3001' :
                       (env.BRANCH_NAME == 'uat') ? '3002' : '3003'

          // Directorios host donde viven los .db (creados previamente en la VM)
          def hostDir = (env.BRANCH_NAME == 'dev') ? '/srv/sqlite/dev' :
                        (env.BRANCH_NAME == 'uat') ? '/srv/sqlite/uat' : '/srv/sqlite/prod'

          // Ruta DENTRO del contenedor que Quarkus leerá por env DB_FILE
          def dbFile  = (env.BRANCH_NAME == 'dev') ? '/data/sqlite/dev.db' :
                        (env.BRANCH_NAME == 'uat') ? '/data/sqlite/uat.db' : '/data/sqlite/prod.db'

          def cname = "app_${env.BRANCH_NAME}"

          // Red para comunicar apps/Sonar/Jenkins
          sh 'docker network create appnet || true'

          // Reemplaza contenedor si existe
          sh "docker rm -f ${cname} || true"

          // Despliegue
          sh """
            docker run -d --name ${cname} --restart=unless-stopped \
              --network appnet \
              -e DB_FILE='${dbFile}' \
              -p ${port}:${APP_PORT_INTERNAL} \
              -v ${hostDir}:/data/sqlite \
              ${IMAGE}:${env.BRANCH_NAME}
          """

          echo "✅ Desplegado ${cname} en puerto ${port} usando DB_FILE=${dbFile}"
        }
      }
    }

    stage('Start Monitoring Stack') {
      steps {
        sh '''
          set -e
          # Reemplaza placeholders con variables si existen en Jenkins
          SLACK_W=${SLACK_WEBHOOK_URL:-}
          ALERT_EMAILS_VAR=${ALERT_EMAILS:-}
          SMTP_FROM_VAR=${SMTP_FROM:-}
          SMTP_HOST_VAR=${SMTP_HOST:-}
          SMTP_USER_VAR=${SMTP_USER:-}
          SMTP_PASS_VAR=${SMTP_PASS:-}

          cd monitoring
          # Solo sustituye si hay valores definidos
          if [ -n "$SLACK_W" ]; then sed -i "s|__SLACK_WEBHOOK__|$SLACK_W|g" alertmanager/alertmanager.yml grafana/provisioning/alerting/contact-points.yaml; fi
          if [ -n "$ALERT_EMAILS_VAR" ]; then sed -i "s|__ALERT_EMAILS__|$ALERT_EMAILS_VAR|g" alertmanager/alertmanager.yml grafana/provisioning/alerting/contact-points.yaml; fi
          if [ -n "$SMTP_FROM_VAR" ]; then sed -i "s|__SMTP_FROM__|$SMTP_FROM_VAR|g" alertmanager/alertmanager.yml; fi
          if [ -n "$SMTP_HOST_VAR" ]; then sed -i "s|__SMTP_HOST__|$SMTP_HOST_VAR|g" alertmanager/alertmanager.yml; fi
          if [ -n "$SMTP_USER_VAR" ]; then sed -i "s|__SMTP_USER__|$SMTP_USER_VAR|g" alertmanager/alertmanager.yml; fi
          if [ -n "$SMTP_PASS_VAR" ]; then sed -i "s|__SMTP_PASS__|$SMTP_PASS_VAR|g" alertmanager/alertmanager.yml; fi

          docker compose up -d
        '''
      }
    }

    stage('Stress test (k6 via Docker)') {
      steps {
        sh '''
          set -e
          # Selecciona puerto externo por rama
          PORT=$( [ "${BRANCH_NAME}" = "dev" ] && echo 3001 || ( [ "${BRANCH_NAME}" = "uat" ] && echo 3002 || echo 3003 ) )
          BASE_URL="http://localhost:${PORT}"
          echo "k6 BASE_URL=${BASE_URL}"
          docker run --rm --network host -e BASE_URL="${BASE_URL}" -v "$PWD/load-tests/k6:/tests" grafana/k6 run /tests/stress.js
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
    always {
      sh "docker ps --format 'table {{.Names}}\\t{{.Ports}}\\t{{.Status}}'"
    }
  }
}