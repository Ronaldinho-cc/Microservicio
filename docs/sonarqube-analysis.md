# SonarQube Analysis Documentation

## Overview

This document describes the SonarQube integration for the Water Quality Microservice. SonarQube is used for continuous inspection of code quality to perform automatic reviews with static analysis of code to detect bugs, code smells, and security vulnerabilities.

## Maven Configuration

The SonarQube Maven plugin has been added to the pom.xml file:

```xml
<plugin>
    <groupId>org.sonarsource.scanner.maven</groupId>
    <artifactId>sonar-maven-plugin</artifactId>
    <version>3.9.1.2184</version>
</plugin>
```

## Jenkins Integration

The Jenkins pipeline includes a stage for SonarQube analysis:

```groovy
stage('SonarQube Analysis') {
    steps {
        withSonarQubeEnv('SonarQube') {
            sh "mvn sonar:sonar"
        }
    }
}
```

## SonarQube Properties

To configure SonarQube analysis, create a `sonar-project.properties` file in the project root with the following content:

```properties
# Project identification
sonar.projectKey=vg-ms-water-quality
sonar.projectName=Water Quality Microservice
sonar.projectVersion=1.0

# Source code location
sonar.sources=src/main/java
sonar.tests=src/test/java

# Language
sonar.language=java

# Encoding of the source code
sonar.sourceEncoding=UTF-8

# Java specific properties
sonar.java.binaries=target/classes
sonar.java.libraries=target/libs
sonar.java.test.binaries=target/test-classes
sonar.java.test.libraries=target/libs

# Coverage reporting
sonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
```

## Quality Profiles

SonarQube uses quality profiles to define coding rules. For this Java project, the following profiles are recommended:

1. Sonar way (built-in profile)
2. FindBugs (additional bug detection rules)
3. PMD (code quality rules)
4. Checkstyle (coding standards)

## Quality Gates

Quality gates are sets of conditions that projects must meet to be considered "quality gate passed". Recommended conditions for this project:

1. Coverage >= 80%
2. Duplicated Lines (%) <= 3%
3. Maintainability Rating <= A
4. Security Rating <= A
5. Reliability Rating <= A

## Running SonarQube Analysis Locally

To run SonarQube analysis locally, execute the following command:

```bash
mvn sonar:sonar
```

This requires:
1. A running SonarQube server
2. Proper configuration in `sonar-project.properties` or via command line parameters
3. SonarQube authentication token (can be passed via `-Dsonar.login=your_token`)

## Interpreting SonarQube Reports

The SonarQube dashboard provides several key metrics:

1. **Bugs**: Issues that represent real bugs in the code
2. **Vulnerabilities**: Security-related issues
3. **Code Smells**: Maintainability issues
4. **Coverage**: Test coverage percentage
5. **Duplications**: Code duplication percentage
6. **Lines of Code**: Total lines of code in the project

## Best Practices

1. Run SonarQube analysis on every commit
2. Set up quality gates to prevent merging of low-quality code
3. Regularly review and update quality profiles
4. Monitor technical debt and work to reduce it
5. Use SonarQube's "leak period" to focus on new code quality