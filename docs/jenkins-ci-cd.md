# Jenkins CI/CD Pipeline Documentation

## Overview

This document describes the Jenkins CI/CD pipeline implementation for the Water Quality Microservice. The pipeline automates the build, test, analysis, and deployment processes.

## Jenkinsfile

The Jenkins pipeline is defined in the `Jenkinsfile` located in the project root. It contains the following stages:

### 1. Checkout
- Clones the repository from the specified Git URL
- Uses the 'develop' branch by default

### 2. Build
- Compiles the Java source code using Maven
- Command: `mvn clean compile`

### 3. Test
- Runs unit tests using Maven
- Command: `mvn test`
- Publishes test results using the JUnit plugin
- Archives test reports for future reference

### 4. SonarQube Analysis
- Performs static code analysis using SonarQube
- Requires a SonarQube server configured in Jenkins with the name 'SonarQube'
- Command: `mvn sonar:sonar`

### 5. Build and Package
- Packages the application into a JAR file
- Command: `mvn package -DskipTests`
- Archives the generated JAR file

### 6. Performance Tests
- Runs JMeter performance tests
- Executes the script: `./performance-tests/run-jmeter-tests.sh`
- Publishes HTML performance test reports

### 7. Deploy
- Placeholder for deployment steps
- Currently contains a simple echo command

## Post-Build Actions

### Notifications
- Sends Slack notifications on both success and failure
- Configured to send messages to the '#jenkins-notifications' channel
- Includes build status, job name, build number, and URL

### Cleanup
- Cleans up the workspace after each build

## Required Jenkins Plugins

The following Jenkins plugins are required for this pipeline:

1. Pipeline Plugin
2. Git Plugin
3. JUnit Plugin
4. SonarQube Scanner Plugin
5. HTML Publisher Plugin
6. Slack Notification Plugin

## Required Tools Configuration

The pipeline requires the following tools to be configured in Jenkins:

1. Maven 3.8.1 (or compatible version)
2. JDK 17 (or compatible version)

## Required Environment Variables

The pipeline uses the following environment variables:

- JAVA_OPTS: Set to '-Xms128m -Xmx256m' for memory optimization

## SonarQube Integration

To enable SonarQube analysis:

1. Install the SonarQube Scanner plugin in Jenkins
2. Configure a SonarQube server in Jenkins with the name 'SonarQube'
3. Ensure the SonarQube Maven plugin is included in the pom.xml

## Slack Integration

To enable Slack notifications:

1. Install the Slack Notification plugin in Jenkins
2. Configure a Slack app with incoming webhook capabilities
3. Add the Slack integration to Jenkins global configuration
4. Create a '#jenkins-notifications' channel in Slack (or modify the Jenkinsfile to use a different channel)