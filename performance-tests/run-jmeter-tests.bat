@echo off
REM JMeter Performance Tests Script for Windows

REM Set variables
set JMETER_HOME=C:\path\to\apache-jmeter
set JMETER_TEST_PLAN=performance-tests\jmeter\water-quality-service.jmx
set JMETER_RESULTS=performance-tests\results
set JMETER_REPORT=performance-tests\report

REM Create directories if they don't exist
if not exist "%JMETER_RESULTS%" mkdir "%JMETER_RESULTS%"
if not exist "%JMETER_REPORT%" mkdir "%JMETER_REPORT%"

REM Run JMeter tests
echo Running JMeter performance tests...
"%JMETER_HOME%\bin\jmeter.bat" -n -t "%JMETER_TEST_PLAN%" -l "%JMETER_RESULTS%\results.jtl" -e -o "%JMETER_REPORT%"

REM Check if tests were successful
if %ERRORLEVEL% EQU 0 (
    echo JMeter tests completed successfully.
    echo Test results are available in: %JMETER_REPORT%
) else (
    echo JMeter tests failed.
    exit /b 1
)