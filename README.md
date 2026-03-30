# Automation Test Execution with Jenkins CI/CD

This project demonstrates an automated API testing suite using **REST Assured** and **TestNG**, integrated with a **Jenkins CI/CD** pipeline. It uses the `FakeStoreAPI` as the target for the tests.

## 🛠️ Project Structure

- **`src/test/java/com/example/tests`**: Contains the API test scripts (REST Assured).
- **`pom.xml`**: Maven configuration with all required dependencies.
- **`testng.xml`**: Defines the test suite execution order.
- **`Dockerfile`**: Containerization setup to run tests inside a Docker environment.
- **`Jenkinsfile`**: Declarative pipeline for automated CI/CD execution.

## 🚀 How to this Run Locally

### 1. Using Maven
Ensure you have Java 17+ and Maven installed.
```bash
mvn clean test
```

### 2. Using Docker
Build and run the test container:
```bash
docker build -t api-automation-tests .
docker run api-automation-tests
```

## 🏗️ Jenkins Configuration

### 1. Prerequisites
Install the following plugins from **Manage Jenkins > Plugins**:
- `Git`
- `Pipeline`
- `Allure Jenkins Plugin` (for advanced test reporting)
- `HTML Publisher` (for TestNG reports)
- `JUnit` (for test result analysis)
- `Slack Notification` (optional but recommended)

### 2. Pipeline Setup
1.  **New Item**: Create a **Pipeline** job named `FakeStoreAPI-Automation`.
2.  **Triggers**: Check **GitHub hook trigger for GITScm polling**.
3.  **Pipeline Definition**: 
    - Select **Pipeline script from SCM**.
    - **SCM**: Git.
    - **Repository URL**: `https://github.com/pekyi1/Automation-Test-Execution-with-Jenkins-CI-CD.git`.
    - **Branch**: `*/main`.

## 🔗 Webhook Integration (Automatic Triggers)

To trigger the pipeline automatically whenever code is pushed:
1.  **GitHub**: Navigate to your repository > **Settings > Webhooks > Add webhook**.
2.  **Payload URL**: Enter your ngrok URL followed by `/github-webhook/` (e.g., `https://your-ngrok-url.ngrok.io/github-webhook/`).
3.  **Content type**: `application/json`.
4.  **Events**: Select "Just the push event".
5.  **Save**: Click **Add webhook**.

## 🔔 Slack Notifications

To get build updates in Slack:
1.  **Slack App**: Create a Slack app in your workspace and get a Bot User OAuth Token.
2.  **Jenkins Credential**: Add the token as a "Secret text" credential in Jenkins named `slack-token`.
3.  **Configure System**: In **Manage Jenkins > System**, find **Slack** and set:
    - **Workspace**: Your Slack workspace name.
    - **Token Credential**: Select `slack-token`.
    - **Default Channel**: `#your-channel-name`.
4.  **Jenkinsfile**: The provided `Jenkinsfile` is ready to be extended with the `slackSend` step in the `post` block.

---
**Lab Assignment - QA Automation Module**
- **Author**: Fred Pekyi
- **Version**: 1.0.0
