# Selenium UI Tests Documentation

## Overview

This document describes the Selenium UI tests implemented for the Water Quality Microservice. Selenium is used for automating web application testing to ensure the user interface functions correctly.

## Test Dependencies

The following dependencies have been added to the pom.xml for Selenium testing:

```xml
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>4.15.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.github.bonigarcia</groupId>
    <artifactId>webdrivermanager</artifactId>
    <version>5.6.2</version>
    <scope>test</scope>
</dependency>
```

## Test Class: LoginTest

The `LoginTest.java` class contains UI tests for the login functionality:

### Setup and Teardown

- **@BeforeEach setUp()**: Configures ChromeDriver with WebDriverManager and initializes the WebDriver
- **@AfterEach tearDown()**: Closes the browser and cleans up resources

### Test Methods

1. **testLoginPageLoad()**
   - Navigates to the login page
   - Verifies that essential login elements (username field, password field, login button) are present and displayed

2. **testInvalidLogin()**
   - Navigates to the login page
   - Enters invalid credentials
   - Submits the login form
   - Verifies that an error message is displayed

## WebDriver Configuration

The tests use ChromeDriver with the following configuration:

```java
ChromeOptions options = new ChromeOptions();
options.addArguments("--headless");
options.addArguments("--no-sandbox");
options.addArguments("--disable-dev-shm-usage");
```

The headless mode allows tests to run without a graphical interface, making them suitable for CI/CD environments.

## WebDriverManager

WebDriverManager automatically manages the ChromeDriver binary:

```java
WebDriverManager.chromedriver().setup();
```

This eliminates the need to manually download and configure the ChromeDriver executable.

## Running Selenium Tests

### With Maven
```bash
./mvnw test -Dtest=pe.edu.vallegrande.ms_water_quality.selenium.LoginTest
```

### From IDE
Run the LoginTest class directly from your IDE's test runner.

## Integration with Jenkins

The Selenium tests can be integrated into the Jenkins pipeline by adding a stage:

```groovy
stage('UI Tests') {
    steps {
        sh 'mvn test -Dtest=*selenium*'
    }
    post {
        always {
            // Archive test results
            junit 'target/surefire-reports/*selenium*.xml'
        }
    }
}
```

## Best Practices

1. **Page Object Model**: For larger test suites, implement the Page Object Model pattern to improve maintainability
2. **Explicit Waits**: Use WebDriverWait with ExpectedConditions instead of Thread.sleep()
3. **Headless Mode**: Use headless mode for CI/CD environments
4. **Test Data**: Use consistent test data or implement test data management
5. **Parallel Execution**: For large test suites, consider parallel test execution
6. **Screenshots**: Capture screenshots on test failures for debugging
7. **Browser Compatibility**: Test across multiple browsers if needed

## Extending the Test Suite

To add more UI tests:

1. Create new test classes in the `src/test/java/pe/edu/vallegrande/ms_water_quality/selenium` directory
2. Follow the same pattern as LoginTest
3. Use meaningful test method names that describe the test scenario
4. Include appropriate assertions to verify expected behavior
5. Ensure proper cleanup in @AfterEach methods

## Troubleshooting

### Common Issues

1. **ChromeDriver Version Mismatch**: WebDriverManager should handle this automatically, but if issues occur, ensure the versions are compatible
2. **Element Not Found**: Use explicit waits and verify that selectors are correct
3. **Timeout Issues**: Adjust WebDriverWait timeouts as needed
4. **Headless Mode Issues**: Some elements might behave differently in headless mode; test both modes if needed

### Debugging Tips

1. Run tests in non-headless mode to visually observe the test execution
2. Add screenshots at key points in the test
3. Use logging to track test execution flow
4. Verify that the application under test is running and accessible