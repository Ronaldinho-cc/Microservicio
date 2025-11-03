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
        // MAVEN_CONFIG se puede dejar fuera ya que usas las propiedades en los comandos sh
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

        stage('Build & Install') { // Cambiado a 'Install' para tener el JAR listo
            steps {
                echo '⚙️ Compilando, ejecutando Unit Tests y generando Jacoco...'
                // Usamos 'install' para que el JAR esté en el repositorio local para otras dependencias.
                // Usamos -DskipTests para compilar primero sin ejecutar tests.
                retry(3) {
                    sh '''
                        mvn install -DskipTests \
                            -s maven-settings.xml \
                            -Dmaven.wagon.http.retryHandler.count=3 \
                            -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
                            -Dmaven.wagon.http.pool=false
                    '''
                }
            }
        }

        stage('Unit Tests & Jacoco') { // Separamos la ejecución de tests después de la compilación
            steps {
                echo '🧪 Ejecutando pruebas unitarias con cobertura...'
                script {
                    try {
                        // EJECUCIÓN: Usamos 'test' (no 'clean test') y jacoco:report
                        sh '''
                            mvn test jacoco:report \
                                -s maven-settings.xml \
                                -Dsurefire.failIfNoSpecifiedTests=false \
                                -Djacoco.destFile=target/jacoco.exec
                        '''
                        echo '✅ Pruebas unitarias completadas'
                    } catch (Exception e) {
                        echo "⚠️ Algunas pruebas unitarias fallaron: ${e.getMessage()}"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
            post {
                always {
                    script {
                        // Publicar resultados de pruebas de Unit Tests
                        try {
                            // Usamos **/surefire-reports/TEST-*.xml para capturar el último run de tests
                            junit allowEmptyResults: true, testResults: 'target/surefire-reports/TEST-*.xml' 
                            echo '✅ Resultados de pruebas unitarias publicados'
                        } catch (Exception e) {
                            echo "⚠️ Error publicando resultados de pruebas: ${e.getMessage()}"
                        }

                        // Publicar reporte de cobertura JaCoCo
                        try {
                            if (fileExists('target/site/jacoco/index.html')) {
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
                        // EJECUCIÓN: No usar 'clean'
                        sh '''
                            mvn failsafe:integration-test \
                                -s maven-settings.xml \
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
                            // Usar el plugin 'failsafe' para Integration Tests
                            junit allowEmptyResults: true, testResults: 'target/failsafe-reports/TEST-*.xml' 
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
                    // Usamos 'verify' en lugar de 'clean compile sonar:sonar' para no limpiar resultados.
                    sh '''
                        mvn verify sonar:sonar \
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
                *${currentBuild.result}* - Ver detalles: ${env.BUILD_URL}
                """
            )
        }
    }
}
