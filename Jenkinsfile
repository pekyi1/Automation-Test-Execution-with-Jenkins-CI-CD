pipeline {
    agent {
        docker {
            image 'maven:3.9.5-eclipse-temurin-17-alpine'
            args '-u root'
        }
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/pekyi1/Automation-Test-Execution-with-Jenkins-CI-CD.git'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn clean test'
            }
            post {
                always {
                    allure results: [[path: 'target/allure-results']]
                    junit '**/target/surefire-reports/*.xml'
                    
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/surefire-reports',
                        reportFiles: 'index.html',
                        reportName: 'API Test Reports'
                    ])
                }
            }
        }

        stage('Artifacts') {
            steps {
                archiveArtifacts artifacts: 'target/allure-results/**, target/surefire-reports/**'
            }
        }
    }

    post {
        success {
            echo "Build Success! 🎉 Pipeline completed successfully."
        }
        failure {
            echo "Build Failed! ❌ One or more tests failed."
        }
        always {
            cleanWs()
        }
    }
}
