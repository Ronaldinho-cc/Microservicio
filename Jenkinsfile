pipeline {
    agent any

    environment {
        // 🔐 Credenciales seguras
        SONAR_TOKEN = credentials('SONAR_TOKEN')

        // 🔑 Configuración de SonarCloud
        SONAR_PROJECT_KEY = '366ba08a8b01e3ccd5bccae92ecb89466fceef01'
        SONAR_ORG = 'mariaavilaconde'
    }

    tools {
        jdk 'jdk17'
        maven 'Maven3'
    }

    stages {
        stage('Checkout') {
            steps {
                echo '📦 Clonando el repositorio...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo '⚙️ Compilando el proyecto con Maven...'
                sh 'mvn -B clean verify'
            }
        }

        stage('SonarCloud Analysis') {
            steps {
                withSonarQubeEnv('SonarCloud') {
                    echo '🔍 Ejecutando análisis en SonarCloud...'
                    sh """
                        mvn sonar:sonar \
                            -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                            -Dsonar.organization=${SONAR_ORG} \
                            -Dsonar.host.url=https://sonarcloud.io \
                            -Dsonar.token=${SONAR_TOKEN}
                    """
                }
            }
        }

        stage('Package') {
            when {
                expression { currentBuild.result == null || currentBuild.result == 'SUCCESS' }
            }
            steps {
                echo '📦 Empaquetando el artefacto final (JAR ejecutable)...'
                sh 'mvn clean package -DskipTests'

                echo '📂 Listando archivos generados...'
                sh 'ls -l target'
            }
        }

        stage('Archive Artifact') {
            steps {
                echo '💾 Archivando el archivo JAR generado...'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline ejecutado exitosamente. Artefacto JAR disponible en Jenkins.'
        }
        failure {
            echo '❌ Falló la ejecución del pipeline.'
        }
    }
}