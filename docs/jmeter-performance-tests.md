# JMeter Performance Tests Documentation

## Overview

This document describes the JMeter performance tests implemented for the Water Quality Microservice. JMeter is used to simulate load on the application and measure its performance under various conditions.

## Test Plan

The JMeter test plan (`water-quality-service.jmx`) includes the following components:

### Thread Group: User Load Simulation
- Number of Threads (users): 50
- Ramp-up Period: 60 seconds
- Loop Count: 10 iterations

This configuration simulates 50 concurrent users accessing the system over 60 seconds, with each user performing 10 iterations of the test scenarios.

### HTTP Requests

1. **Get All Users**
   - Method: GET
   - Path: /api/admin/users
   - Purpose: Test the endpoint that retrieves all users

2. **Get All Testing Points**
   - Method: GET
   - Path: /api/admin/testing-points
   - Purpose: Test the endpoint that retrieves all testing points

3. **Get All Daily Records**
   - Method: GET
   - Path: /api/admin/daily-records
   - Purpose: Test the endpoint that retrieves all daily records

### Listeners

1. **View Results Tree**
   - Provides detailed information about each request and response
   - Useful for debugging and analysis

2. **Summary Report**
   - Provides statistical information about the test results
   - Includes metrics like average response time, throughput, and error rate

## Running JMeter Tests

### Prerequisites
1. Apache JMeter 5.4.1 or later installed
2. Java 8 or later installed

### Using the Shell Script (Linux/Mac)
```bash
./performance-tests/run-jmeter-tests.sh
```

### Using the Batch Script (Windows)
```cmd
performance-tests\run-jmeter-tests.bat
```

### Manual Execution
```bash
/path/to/apache-jmeter/bin/jmeter -n -t performance-tests/jmeter/water-quality-service.jmx -l performance-tests/results/results.jtl -e -o performance-tests/report
```

## Test Results

The test execution generates two types of results:

1. **JTL File**: `performance-tests/results/results.jtl`
   - Raw test results in CSV format
   - Contains detailed information about each request

2. **HTML Dashboard**: `performance-tests/report/index.html`
   - Interactive dashboard with charts and graphs
   - Provides visual representation of performance metrics

## Key Performance Metrics

The JMeter report provides the following key metrics:

1. **Average Response Time**: Average time taken to process requests
2. **Median Response Time**: Median time taken to process requests
3. **90th Percentile**: 90% of requests are processed within this time
4. **Throughput**: Number of requests processed per second
5. **Error Rate**: Percentage of failed requests
6. **Bytes Sent/Received**: Network traffic statistics

## Interpreting Results

### Response Times
- **Good**: < 200ms
- **Acceptable**: 200ms - 1000ms
- **Poor**: > 1000ms

### Error Rates
- **Target**: 0%
- **Acceptable**: < 1%
- **Poor**: > 1%

### Throughput
- Higher values indicate better performance
- Should be consistent across multiple test runs

## Jenkins Integration

The Jenkins pipeline includes a stage for running JMeter tests:

```groovy
stage('Performance Tests') {
    steps {
        sh './performance-tests/run-jmeter-tests.sh'
    }
    post {
        always {
            publishHTML([
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'performance-tests/report',
                reportFiles: 'index.html',
                reportName: 'JMeter Performance Test Report'
            ])
        }
    }
}
```

This configuration:
1. Runs the JMeter tests during the build
2. Publishes the HTML dashboard as a build artifact
3. Makes the report accessible from the Jenkins build page

## Best Practices

1. **Regular Testing**: Run performance tests regularly to detect performance regressions
2. **Baseline Metrics**: Establish baseline performance metrics for comparison
3. **Test Data**: Use realistic test data that represents production usage
4. **Environment**: Run tests in an environment similar to production
5. **Monitoring**: Monitor system resources (CPU, memory, disk I/O) during tests
6. **Analysis**: Analyze results to identify bottlenecks and optimization opportunities