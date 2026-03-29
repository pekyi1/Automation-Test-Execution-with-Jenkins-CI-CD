pipeline {
    agent {
        docker {
            image 'maven:3.9.5-eclipse-temurin-17-alpine'
            args '-u root' // Helps with directory permissions in Docker
        }
    }

    stages {
        stage('Checkout') {
            steps {
                // Checkout code from Git
                git branch: 'main', url: 'https://github.com/pekyi1/Automation-Test-Execution-with-Jenkins-CI-CD.git'
            }
        }

        stage('Restore') {
            steps {
                // Pre-retrieve all dependencies to ensure they're ready
                sh 'mvn dependency:go-offline'
            }
        }

        stage('Test') {
            steps {
                // Run the test suite
                sh 'mvn test'
            }
            post {
                always {
                    // Generate Allure Report
                    allure results: [[path: 'target/allure-results']]

                    // Archive JUnit-formatted test results
                    junit '**/target/surefire-reports/*.xml'
                    
                    // Publish the HTML test report
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
                // Archive all results as build artifacts
                archiveArtifacts artifacts: 'target/allure-results/**, target/surefire-reports/**'
            }
        }
    }

    post {
        success {
            echo "Build Success! 🎉 Pipeline completed successfully."
            // Slack/Email notification logic could be added here
        }
        failure {
            echo "Build Failed! ❌ One or more tests failed."
            // Slack/Email notification logic could be added here
        }
        always {
            cleanWs()
        }
    }
}
