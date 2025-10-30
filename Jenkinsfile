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
            docker run -d --name ${cname} --restart=unless-stopped \\
              --network appnet \\
              -e DB_FILE='${dbFile}' \\
              -p ${port}:${APP_PORT_INTERNAL} \\
              -v ${hostDir}:/data/sqlite \\
              ${IMAGE}:${env.BRANCH_NAME}
          """

          echo "✅ Desplegado ${cname} en puerto ${port} usando DB_FILE=${dbFile}"
        }
      }
    }
  }

  post {
    always {
      sh "docker ps --format 'table {{.Names}}\\t{{.Ports}}\\t{{.Status}}'"
    }
  }
}