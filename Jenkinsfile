pipeline {
  agent any
  environment {
    IMAGE = "hospital-app"
    APP_PORT_INTERNAL = "8080"
    SONARQUBE_ENV = "SonarLocal"
  }
  triggers { pollSCM('H/2 * * * *') }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build & Unit Tests') {
      steps {
        sh 'chmod +x backend/mvnw'
        sh './backend/mvnw -f backend/pom.xml -B -DskipTests=false clean verify'
      }
    }

    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv("${SONARQUBE_ENV}") {
          sh './backend/mvnw -f backend/pom.xml -B sonar:sonar'
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

    stage('Docker Build') {
      steps {
        sh 'docker build -f backend/Dockerfile.jvm -t ${IMAGE}:${BRANCH_NAME} backend'
      }
    }

    stage('Deploy per branch (SQLite)') {
      steps {
        script {
          def port   = (BRANCH_NAME == 'dev') ? '3001' : (BRANCH_NAME == 'uat') ? '3002' : '3003'
          def hostDir= (BRANCH_NAME == 'dev') ? '/srv/sqlite/dev' : (BRANCH_NAME == 'uat') ? '/srv/sqlite/uat' : '/srv/sqlite/prod'
          def dbFile = (BRANCH_NAME == 'dev') ? '/data/sqlite/dev.db' : (BRANCH_NAME == 'uat') ? '/data/sqlite/uat.db' : '/data/sqlite/prod.db'
          def cname  = "app_${BRANCH_NAME}"

          sh "docker rm -f ${cname} || true"
          sh """
            docker run -d --name ${cname} --network appnet \
              -e DB_FILE='${dbFile}' \
              -p ${port}:${APP_PORT_INTERNAL} \
              -v ${hostDir}:/data/sqlite \
              ${IMAGE}:${BRANCH_NAME}
          """
          echo "✅ ${BRANCH_NAME} desplegado en http://<IP_VM>:${port}"
        }
      }
    }
  }
}
