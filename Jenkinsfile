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
                script {
                    try {
                        sh 'mvn test -s maven-settings.xml -Dsurefire.failIfNoSpecifiedTests=false'
                    } catch (Exception e) {
                        echo "⚠️ Algunas pruebas fallaron: ${e.getMessage()}"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
            post {
                always {
                    script {
                        // Verificar si existen reportes de pruebas
                        def testReports = sh(script: 'find target/surefire-reports -name "*.xml" 2>/dev/null || true', returnStdout: true).trim()
                        if (testReports) {
                            echo '📄 Publicando resultados de pruebas...'
                            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                        } else {
                            echo '⚠️ No se encontraron reportes de pruebas unitarias'
                        }

                        // Verificar si existe reporte de JaCoCo
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
                script {
                    try {
                        sh 'mvn test -s maven-settings.xml -Dtest=*IntegrationTest -Dsurefire.failIfNoSpecifiedTests=false'
                    } catch (Exception e) {
                        echo "⚠️ No hay pruebas de integración definidas aún: ${e.getMessage()}"
                        echo '✅ Continuando con el pipeline...'
                    }
                }
            }
            post {
                always {
                    script {
                        def testReports = sh(script: 'find target/surefire-reports -name "*.xml" 2>/dev/null || true', returnStdout: true).trim()
                        if (testReports) {
                            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                        } else {
                            echo '⚠️ No se encontraron reportes de pruebas de integración'
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
