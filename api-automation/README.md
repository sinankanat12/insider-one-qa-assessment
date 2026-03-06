# Insider One API Automation Framework

This project contains a comprehensive suite of functional and contract tests for the [Petstore Swagger API](https://petstore.swagger.io/) `/pet` endpoints. It is built using **Java**, **RestAssured**, and **JUnit 5**, emphasizing clean code, reusability, and detailed reporting.

## 🚀 Key Features

- **Base Client Abstraction:** Centralized RestAssured configuration, request specification, and error handling in `BaseClient`.
- **Model-Driven Testing:** Uses POJOs (Plain Old Java Objects) for Pet, Category, and Tag entities with Jackson for seamless JSON serialization/deserialization.
- **Test Data Factory:** Implements the Factory pattern (`PetFactory`) to generate dynamic and randomized test data, reducing test fragility.
- **Fluent API Client:** `PetClient` provides a clean, readable interface for interacting with API endpoints.
- **Detailed Reporting:** Allure integration provides rich request/response logs and execution timelines.
- **Dockerized Execution:** Ready for containerized execution in any environment.

## 🛠 Tech Stack

- **Language:** Java 17
- **API Client:** RestAssured 5.4.0
- **Test Framework:** JUnit 5
- **JSON Handler:** Jackson (fasterxml)
- **Build Tool:** Maven
- **Reporting:** Allure

## 📋 Prerequisites

- **Java JDK 17+**
- **Maven** installed and in your PATH.
- **Network access** to `https://petstore.swagger.io`.

## 🏃 Running Tests

### Run all tests
```bash
mvn clean test
```

### Run specific test class
```bash
mvn test -Dtest=CreatePetTest
```

## 📊 Reporting

### Generating Allure Reports
After running the tests, Allure results are generated in `target/allure-results`. To view the interactive report:

```bash
mvn allure:serve
```

Or if you have the allure command-line tool:
```bash
allure serve target/allure-results
```

## 📂 Architecture & Folder Structure

```text
api-automation/
├── src/main/java/com/insiderone/qa/
│   ├── client/      # API client implementations (RestAssured logic)
│   ├── factory/     # Test data factories (PetFactory)
│   └── model/       # Data models (POJOs for JSON mapping)
├── src/test/java/com/insiderone/qa/
│   └── tests/pet/   # Functional and contract test classes
├── src/test/resources/
│   └── allure.properties  # Allure configuration
├── Dockerfile       # Container definition
└── pom.xml          # Project dependencies
```

## 🧪 Test Coverage

The suite covers various scenarios including:
- **Positive:** Create, read, update, delete, and find pets by status.
- **Negative:** Non-existent IDs, invalid status parameters, and malformed requests.
- **Contract:** Schema validation and correct HTTP status code assertions.
