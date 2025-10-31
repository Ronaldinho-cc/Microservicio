# Water Quality Microservice - Testing and CI/CD Documentation

## Overview

This documentation covers the complete testing and CI/CD implementation for the Water Quality Microservice. The implementation includes unit tests, Jenkins CI/CD pipeline, SonarQube code analysis, JMeter performance tests, and Selenium UI tests.

## Implemented Components

### 1. Unit Tests
Implementation of 3 unit tests covering core services:
- UserServiceImplTest
- DailyRecordServiceImplTest
- TestingPointServiceImplTest

[Detailed Unit Tests Documentation](unit-tests.md)

### 2. Jenkins CI/CD Pipeline
Implementation of Jenkins to validate unit tests and automate the build and deployment process.

[Detailed Jenkins CI/CD Documentation](jenkins-ci-cd.md)

### 3. SonarQube Integration
Implementation of Jenkins with SonarQube tool for static code analysis.

[Detailed SonarQube Documentation](sonarqube-analysis.md)

### 4. JMeter Performance Tests
Implementation of load tests with JMeter to validate application performance.

[Detailed JMeter Documentation](jmeter-performance-tests.md)

### 5. Selenium UI Tests
Implementation of UI tests with Selenium for functional testing of the user interface.

[Detailed Selenium Documentation](selenium-ui-tests.md)

## Project Structure

```
vg-ms-water-quality/
├── src/
│   ├── main/
│   │   └── java/
│   └── test/
│       └── java/
│           ├── pe/edu/vallegrande/ms_water_quality/
│           │   ├── application/services/impl/
│           │   │   ├── UserServiceImplTest.java
│           │   │   ├── DailyRecordServiceImplTest.java
│           │   │   └── TestingPointServiceImplTest.java
│           │   └── selenium/
│           │       └── LoginTest.java
├── performance-tests/
│   ├── jmeter/
│   │   └── water-quality-service.jmx
│   ├── run-jmeter-tests.sh
│   └── run-jmeter-tests.bat
├── docs/
│   ├── README.md
│   ├── unit-tests.md
│   ├── jenkins-ci-cd.md
│   ├── sonarqube-analysis.md
│   ├── jmeter-performance-tests.md
│   └── selenium-ui-tests.md
├── Jenkinsfile
└── pom.xml
```

## Getting Started

### Prerequisites
1. Java 17
2. Maven 3.8+
3. Jenkins server
4. SonarQube server
5. Apache JMeter
6. Chrome browser (for Selenium tests)

### Running Tests Locally

1. **Unit Tests**:
   ```bash
   ./mvnw test
   ```

2. **JMeter Performance Tests**:
   ```bash
   ./performance-tests/run-jmeter-tests.sh
   ```

3. **Selenium UI Tests**:
   ```bash
   ./mvnw test -Dtest=*selenium*
   ```

## CI/CD Pipeline

The Jenkins pipeline automates the following workflow:

1. Code checkout from Git
2. Compilation and unit testing
3. SonarQube static analysis
4. Packaging of the application
5. Performance testing with JMeter
6. Deployment (placeholder)
7. Notifications via Slack

## Monitoring and Reporting

### Test Results
- Unit test results are published via Jenkins JUnit plugin
- JMeter performance test results are available as HTML reports
- SonarQube analysis provides code quality metrics

### Notifications
- Slack notifications for build success/failure
- Email notifications can be configured as needed

## Maintaining the Implementation

### Updating Tests
1. Add new test classes in the appropriate directories
2. Update the Jenkinsfile if new test categories are added
3. Maintain documentation in the docs/ directory

### Updating Dependencies
1. Update versions in pom.xml
2. Update Jenkins tools configuration as needed
3. Verify compatibility with existing tests

### Extending the Pipeline
1. Add new stages to the Jenkinsfile
2. Configure required Jenkins plugins
3. Update documentation accordingly

## Troubleshooting

### Common Issues
1. **Dependency Issues**: Ensure all required tools and plugins are installed
2. **Configuration Issues**: Verify environment variables and configuration files
3. **Test Failures**: Check test reports for detailed error information

### Support
For issues with the implementation, refer to the detailed documentation for each component or contact the development team.