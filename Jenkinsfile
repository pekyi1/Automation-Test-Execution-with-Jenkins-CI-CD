pipeline {
    agent none
    
    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
    }

    triggers {
        githubPush()
    }

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

    post {
        always {
            script {
                // Fetch test summary from the build's test result action
                def testResult = currentBuild.rawBuild.getAction(hudson.tasks.test.AbstractTestResultAction.class)
                def total = 0, passed = 0, failed = 0, skipped = 0
                
                if (testResult != null) {
                    total = testResult.totalCount
                    passed = testResult.passCount
                    failed = testResult.failCount
                    skipped = testResult.skipCount
                }

                def status = currentBuild.result ?: 'SUCCESS'
                def color = (status == 'SUCCESS') ? 'good' : (status == 'UNSTABLE' ? 'warning' : 'danger')
                
                def message = """*API Test Execution Summary [Build ${env.BUILD_NUMBER}]*
Status: *${status}*
Total Tests: *${total}* | Passed: *${passed}* | Failed: *${failed}* | Skipped: *${skipped}*
Repo: `${env.GIT_URL}` | Branch: `${env.BRANCH_NAME ?: 'main'}`
Build URL: ${env.RUN_DISPLAY_URL ?: env.BUILD_URL}
Allure Report: ${env.BUILD_URL}allure/"""

                // Slack Notification
                slackSend(color: color, message: message)

                // Email Notification
                emailext(
                    subject: "Jenkins Build ${status}: ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
                    body: message.replace('*', ''), 
                    recipientProviders: [culprits(), developers(), upstreamDevelopers()]
                )
            }
        }
    }
}
