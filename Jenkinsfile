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
                
                // Capture test counts and failures (sandbox-safe)
                script {
                    def testResults = junit '**/target/surefire-reports/*.xml'
                    env.TEST_TOTAL = "${testResults.totalCount}"
                    env.TEST_PASSED = "${testResults.passCount}"
                    env.TEST_FAILED = "${testResults.failCount}"
                    env.TEST_SKIPPED = "${testResults.skipCount}"

                    // Extract failure reasons (Porting logic from previous project)
                    sh '''
                    python3 - <<'PY'
                    import glob, xml.etree.ElementTree as ET, os
                    files = glob.glob("target/surefire-reports/TEST-*.xml")
                    failed_items = []
                    for fpath in files:
                        root = ET.parse(fpath).getroot()
                        for tc in root.findall("testcase"):
                            name = tc.attrib.get("name", "unknown")
                            failure = tc.find("failure")
                            if failure is not None:
                                msg = (failure.attrib.get("message") or "").strip().splitlines()[0]
                                failed_items.append(f" - {name}: {msg}")
                    with open("failures.txt", "w") as f:
                        f.write("\\n".join(failed_items))
                    PY
                    '''
                    def failures = readFile('failures.txt').trim()
                    env.TEST_FAILURE_LIST = failures ?: " - None (All tests passed)"
                }
                
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
                    script {
                        def status = currentBuild.currentResult ?: 'SUCCESS'
                        def color = (status == 'SUCCESS') ? 'good' : (status == 'UNSTABLE' ? 'warning' : 'danger')
                        
                        def message = """*API Test Execution Summary [Build ${env.BUILD_NUMBER}]*
Status: *${status}*
Total Tests: *${env.TEST_TOTAL ?: '0'}* | Passed: *${env.TEST_PASSED ?: '0'}* | Failed: *${env.TEST_FAILED ?: '0'}* | Skipped: *${env.TEST_SKIPPED ?: '0'}*

*Failed test(s) and why:*
${env.TEST_FAILURE_LIST}

Build URL: ${env.BUILD_URL}
Allure Report: ${env.BUILD_URL}allure/"""

                        // Slack Notification
                        slackSend(color: color, message: message)

                        // Email Notification
                        emailext(
                            subject: "Jenkins Build ${status}: ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
                            body: message.replace('*', ''), // Strip markdown bold for email
                            recipientProviders: [culprits(), developers(), upstreamDevelopers()]
                        )
                    }
                    // Fix permissions so Jenkins can clean up
                    sh 'chmod -R 777 ${WORKSPACE} 2>/dev/null || true'
                }
            }
        }
    }
}
