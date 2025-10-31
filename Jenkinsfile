pipeline {
    agent any

    parameters {
        booleanParam(name: 'CLEAN_MAVEN_CACHE', defaultValue: false, description: 'Limpiar caché de Maven antes del build')
    }

    tools {
        jdk 'Java17'
        maven 'M3'
    }

    environment {
        GITHUB_REPO = 'https://github.com/Ronaldinho-cc/Microservicio.git'
        MAVEN_OPTS = '-Xmx1024m -Dmaven.wagon.http.retryHandler.count=3'
        MAVEN_CONFIG = '-Dmaven.wagon.httpconnectionManager.ttlSeconds=120'
    }

    stages {
        stage('Checkout') {
            steps {
                echo '📦 Clonando repositorio...'
                git branch: 'develop-clean', url: "${GITHUB_REPO}"
                
                echo '🧹 Limpiando caché de Maven si es necesario...'
                script {
                    if (params.CLEAN_MAVEN_CACHE == true) {
                        sh 'rm -rf ~/.m2/repository'
                        echo '✅ Caché de Maven limpiada'
                    }
                }
            }
        }

        stage('Build') {
            steps {
                echo '⚙️ Compilando el proyecto...'
                retry(3) {
                    sh '''
                        mvn clean compile \
                            -s maven-settings.xml \
                            -Dmaven.wagon.http.retryHandler.count=3 \
                            -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
                            -Dmaven.wagon.http.pool=false
                    '''
                }
            }
        }

        stage('Unit Tests') {
            steps {
                echo '🧪 Ejecutando pruebas unitarias...'
                sh 'mvn test -s maven-settings.xml -Dspring.profiles.active=test'
            }
            post {
                always {
                    echo '📄 Publicando resultados de pruebas...'
                    junit 'target/surefire-reports/*.xml'

                    script {
                        if (fileExists('target/site/jacoco/jacoco.xml')) {
                            recordCoverage(
                                tools: [[parser: 'JACOCO', pattern: 'target/site/jacoco/jacoco.xml']],
                                sourceCodeRetention: 'EVERY_BUILD',
                                failNoReports: false
                            )
                        } else {
                            echo '⚠️ No se encontró el reporte de JaCoCo'
                        }
                    }
                }
            }
        }

        stage('Integration Tests') {
            steps {
                echo '🔗 Ejecutando pruebas de integración...'
                sh 'mvn test -s maven-settings.xml -Dtest=*IntegrationTest'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Code Analysis') {
            steps {
                echo '🔍 Analizando código con SonarCloud...'
                withCredentials([string(credentialsId: 'sonarcloud-token', variable: 'SONAR_TOKEN')]) {
                    sh '''
                        mvn clean verify sonar:sonar \
                            -s maven-settings.xml \
                            -Dsonar.projectKey=MiAppBackend \
                            -Dsonar.organization=ronaldinho-cc \
                            -Dsonar.host.url=https://sonarcloud.io \
                            -Dsonar.token=$SONAR_TOKEN
                    '''
                }
            }
        }
    }

    post {
        always {
            echo '🧹 Limpiando workspace...'
            cleanWs()
        }
        success {
            echo '✅ Pipeline ejecutado con éxito!'
            slackSend(
                channel: '#notifications',
                color: 'good',
                message: """
                ✅ *BUILD EXITOSO*
                Proyecto: *${env.JOB_NAME}*
                Build: *#${env.BUILD_NUMBER}*
                Ver detalles: ${env.BUILD_URL}
                """
            )
        }
        failure {
            echo '❌ Pipeline falló!'
            slackSend(
                channel: '#notifications',
                color: 'danger',
                message: """
                ❌ *BUILD FALLIDO*
                Proyecto: *${env.JOB_NAME}*
                Build: *#${env.BUILD_NUMBER}*
                Ver detalles: ${env.BUILD_URL}
                """
            )
        }
    }
}
