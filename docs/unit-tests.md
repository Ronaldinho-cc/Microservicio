# Unit Tests Documentation

## Overview

This document describes the unit tests implemented for the Water Quality Microservice. We have implemented 3 unit tests covering the core services of the application.

## Test Classes

### 1. UserServiceImplTest

This test class covers the UserServiceImpl implementation with the following test cases:

1. **getAll_ShouldReturnAllUsers()**
   - Tests that the getAll() method returns all users from the repository
   - Verifies that the repository's findAll() method is called

2. **save_ShouldCreateUser_WhenEmailDoesNotExist()**
   - Tests that a user can be successfully created when the email doesn't exist
   - Verifies that the user is saved to the repository
   - Checks that the response contains the correct user information

3. **save_ShouldThrowException_WhenEmailAlreadyExists()**
   - Tests that an exception is thrown when trying to create a user with an existing email
   - Verifies that the repository's existsByEmail() method is called
   - Ensures that the save operation is not performed

### 2. DailyRecordServiceImplTest

This test class covers the DailyRecordServiceImpl implementation with the following test cases:

1. **save_ShouldCreateDailyRecord()**
   - Tests that a daily record can be successfully created
   - Verifies that the record is saved to the repository
   - Checks that the response contains the correct record information

2. **update_ShouldUpdateDailyRecord_WhenRecordExists()**
   - Tests that a daily record can be successfully updated when it exists
   - Verifies that the repository's findById() and save() methods are called

3. **update_ShouldThrowException_WhenRecordDoesNotExist()**
   - Tests that an exception is thrown when trying to update a non-existent record
   - Verifies that the repository's findById() method is called
   - Ensures that the save operation is not performed

### 3. TestingPointServiceImplTest

This test class covers the TestingPointServiceImpl implementation with the following test cases:

1. **save_ShouldCreateTestingPoint_WhenPointCodeIsProvided()**
   - Tests that a testing point can be successfully created when a point code is provided
   - Verifies that the point is saved to the repository
   - Checks that the response contains the correct point information

2. **activate_ShouldActivateTestingPoint_WhenPointExists()**
   - Tests that a testing point can be successfully activated when it exists
   - Verifies that the repository's findById() and save() methods are called
   - Checks that the status is updated to "ACTIVE"

3. **activate_ShouldThrowException_WhenPointDoesNotExist()**
   - Tests that an exception is thrown when trying to activate a non-existent point
   - Verifies that the repository's findById() method is called
   - Ensures that the save operation is not performed

## Running the Tests

To run the unit tests, execute the following command in the project root directory:

```bash
./mvnw test
```

## Test Dependencies

The following test dependencies were added to the pom.xml:

- spring-boot-starter-test (provided by Spring Boot parent)
- reactor-test (for testing reactive streams)
- de.flapdoodle.embed.mongo.spring30x (for embedded MongoDB testing)

## Test Results

All 9 tests (3 classes with 3 tests each) are currently passing.