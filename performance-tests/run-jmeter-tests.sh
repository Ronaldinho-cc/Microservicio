#!/bin/bash

# JMeter Performance Tests Script

# Set variables
JMETER_HOME="/path/to/apache-jmeter"
JMETER_TEST_PLAN="performance-tests/jmeter/water-quality-service.jmx"
JMETER_RESULTS="performance-tests/results"
JMETER_REPORT="performance-tests/report"

# Create directories if they don't exist
mkdir -p $JMETER_RESULTS
mkdir -p $JMETER_REPORT

# Run JMeter tests
echo "Running JMeter performance tests..."
$JMETER_HOME/bin/jmeter -n -t $JMETER_TEST_PLAN -l $JMETER_RESULTS/results.jtl -e -o $JMETER_REPORT

# Check if tests were successful
if [ $? -eq 0 ]; then
    echo "JMeter tests completed successfully."
    echo "Test results are available in: $JMETER_REPORT"
else
    echo "JMeter tests failed."
    exit 1
fi