pipeline {
    agent none

    stages {
        stage('Checkout') {
            agent any
            steps {
                git branch: 'main', url: 'https://github.com/pekyi1/Automation-Test-Execution-with-Jenkins-CI-CD.git'
            }
        }

        stage('Test') {
            agent {
                docker {
                    image 'maven:3.9.5-eclipse-temurin-17-alpine'
                    args '-u root'
                }
            }
            steps {
                sh 'mvn clean test'
            }
            post {
                always {
                    // Copy any root-level allure-results into target/ as a fallback
                    sh 'if [ -d allure-results ]; then mkdir -p target/allure-results && cp -r allure-results/* target/allure-results/; fi'
                    // Debug: show what was generated
                    sh 'echo "=== allure-results contents ===" && ls -la allure-results/ 2>/dev/null || echo "No root allure-results"'
                    sh 'echo "=== target/allure-results contents ===" && ls -la target/allure-results/ 2>/dev/null || echo "No target/allure-results"'
                    stash name: 'results', includes: 'target/allure-results/**, target/surefire-reports/**'
                    // Apply full permissions so Jenkins can clean up root-owned files
                    sh 'chmod -R 777 ${WORKSPACE}'
                }
            }
        }

        stage('Reports') {
            agent any
            steps {
                // Clean workspace before unstashing (ignore errors from previous root-owned files)
                sh 'rm -rf ${WORKSPACE}/* ${WORKSPACE}/.[!.]* 2>/dev/null || true'
                unstash 'results'
                // Debug: verify unstashed files
                sh 'echo "=== Unstashed allure-results ===" && find target/allure-results -type f 2>/dev/null | head -20 || echo "No allure results found"'
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
            post {
                always {
                    // Fix permissions so Jenkins can clean up
                    sh 'chmod -R 777 ${WORKSPACE} 2>/dev/null || true'
                }
            }
        }
    }
}
