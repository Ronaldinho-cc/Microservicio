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
                echo '🧪 Ejecutando pruebas unitarias con cobertura...'
                script {
                    try {
                        sh '''
                            mvn clean test jacoco:report \
                                -s maven-settings.xml \
                                -Dsurefire.failIfNoSpecifiedTests=false \
                                -Djacoco.destFile=target/jacoco.exec
                        '''
                        echo '✅ Pruebas unitarias completadas'
                    } catch (Exception e) {
                        echo "⚠️ Algunas pruebas fallaron: ${e.getMessage()}"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
            post {
                always {
                    script {
                        // Listar archivos generados para debug
                        sh 'find target -type f -name "*.xml" | head -10 || echo "No XML files found"'
                        
                        // Publicar resultados de pruebas
                        try {
                            junit allowEmptyResults: true, testResults: 'target/surefire-reports/TEST-*.xml'
                            echo '✅ Resultados de pruebas publicados'
                        } catch (Exception e) {
                            echo "⚠️ Error publicando resultados de pruebas: ${e.getMessage()}"
                        }

                        // Publicar reporte de cobertura JaCoCo
                        try {
                            if (fileExists('target/site/jacoco/jacoco.xml')) {
                                publishHTML([
                                    allowMissing: false,
                                    alwaysLinkToLastBuild: true,
                                    keepAll: true,
                                    reportDir: 'target/site/jacoco',
                                    reportFiles: 'index.html',
                                    reportName: 'JaCoCo Coverage Report'
                                ])
                                echo '✅ Reporte de cobertura JaCoCo publicado'
                            } else {
                                echo '⚠️ No se encontró el reporte de JaCoCo'
                            }
                        } catch (Exception e) {
                            echo "⚠️ Error publicando cobertura: ${e.getMessage()}"
                        }
                    }
                }
            }
        }

        stage('Integration Tests') {
            steps {
                echo '🔗 Ejecutando pruebas de integración...'
                script {
                    try {
                        sh '''
                            mvn test -s maven-settings.xml \
                                -Dtest=*IntegrationTest,*PerformanceTest \
                                -Dsurefire.failIfNoSpecifiedTests=false
                        '''
                        echo '✅ Pruebas de integración completadas'
                    } catch (Exception e) {
                        echo "⚠️ Algunas pruebas de integración fallaron: ${e.getMessage()}"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
            post {
                always {
                    script {
                        try {
                            junit allowEmptyResults: true, testResults: 'target/surefire-reports/TEST-*.xml'
                            echo '✅ Resultados de integración publicados'
                        } catch (Exception e) {
                            echo "⚠️ Error publicando resultados de integración: ${e.getMessage()}"
                        }
                    }
                }
            }
        }

        stage('Code Analysis') {
            steps {
                echo '🔍 Analizando código con SonarCloud...'
                withCredentials([string(credentialsId: 'sonarcloud-token', variable: 'SONAR_TOKEN')]) {
                    sh '''
                        mvn clean compile sonar:sonar \
                            -s maven-settings.xml \
                            -Dsonar.projectKey=MiAppBackend \
                            -Dsonar.organization=ronaldinho-cc \
                            -Dsonar.host.url=https://sonarcloud.io \
                            -Dsonar.token=$SONAR_TOKEN \
                            -Dsonar.skipTests=true
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
                channel: '#jenkins-test',
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
                channel: '#jenkins-test',
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
