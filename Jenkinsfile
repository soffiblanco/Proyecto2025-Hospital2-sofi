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
  // Mapea PUERTO, VOLUMEN y DB por rama
  def port, hostDir, dbFile, cname
  switch (env.BRANCH_NAME) {
    case 'dev':
      port   = '3001'
      hostDir= '/srv/sqlite/dev'
      dbFile = '/data/sqlite/dev.db'
      cname  = 'app_dev'
      break
    case 'uat':
      port   = '3002'
      hostDir= '/srv/sqlite/uat'
      dbFile = '/data/sqlite/uat.db'
      cname  = 'app_uat'
      break
    case 'master':
      // <<— master con SU puerto y SU volumen
      port   = '3001'    // o 3004 si ya usas 3001 en dev
      hostDir= '/srv/sqlite/master'
      dbFile = '/data/sqlite/master.db'
      cname  = 'app_master'
      break
    case 'prod':
      port   = '3003'
      hostDir= '/srv/sqlite/prod'
      dbFile = '/data/sqlite/prod.db'
      cname  = 'app_prod'
      break
    default:
      // ramas feature/* aisladas
      port   = '3010'
      hostDir= "/srv/sqlite/${env.BRANCH_NAME}"
      dbFile = "/data/sqlite/${env.BRANCH_NAME}.db"
      cname  = "app_${env.BRANCH_NAME}"
  }

  sh 'docker network create appnet || true'

  // TIP: chequeo rápido de puerto en uso (solo informativo)
  sh """
    if ss -ltn | grep -q ":${port} "; then
      echo "⚠️  Puerto ${port} ya en uso en host. Voy a reemplazar contenedor ${cname} si existe."
    fi
  """

  // Reemplaza contenedor si existe
  sh "docker rm -f ${cname} || true"

  // Asegura que exista el directorio del volumen
  sh "mkdir -p ${hostDir}"

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