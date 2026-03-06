# API Automation Framework

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

## 🧪 Test Cases

### CreatePetTest — `POST /pet`

| # | Test Case | Type | Expected |
|---|-----------|------|----------|
| 1 | Create pet with all fields (name, category, tags, photoUrls, status) | Positive | 200 — all fields persisted correctly |
| 2 | Create pet with minimal fields (id, name, photoUrls only) | Positive | 200 — pet created successfully |
| 3 | Create pet with empty body (no required fields) | Negative | 400/405/500 — handled gracefully |
| 4 | Create pet with 500-character name | Boundary | 200 — long name persisted without truncation |
| 5 | Create pet with emoji + Japanese + special characters in name | Encoding | 200 — UTF-8 encoding preserved correctly |
| 6 | Create pet with `Long.MAX_VALUE` as ID | Boundary | 200 — extreme ID value handled |

### GetPetTest — `GET /pet/{petId}`

| # | Test Case | Type | Expected |
|---|-----------|------|----------|
| 7 | Get pet by valid ID | Positive | 200 — correct pet data returned |
| 8 | Get pet with non-existent ID (`Long.MAX_VALUE`) | Negative | 404 — pet not found |
| 9 | Get pet with negative ID (-1) | Negative | 400/404 — invalid ID rejected |

### UpdatePetTest — `PUT /pet` & `POST /pet/{petId}`

| # | Test Case | Type | Expected |
|---|-----------|------|----------|
| 10 | Update pet name via PUT — verify persistence with GET | Positive | 200 — name updated and persisted |
| 11 | Update pet status to "sold" via PUT — verify persistence | Positive | 200 — status changed to "sold" |
| 12 | Update pet with duplicate tags (same id & name) | Edge Case | 200 — duplicates handled, data consistent |
| 13 | Update non-existent pet via form data (`POST /pet/{petId}`) | Negative | 404 — "not found" error response |
| 14 | Update existing pet via form data — verify persistence | Positive | 200 — name and status updated |

### DeletePetTest — `DELETE /pet/{petId}`

| # | Test Case | Type | Expected |
|---|-----------|------|----------|
| 15 | Delete existing pet | Positive | 200 — pet deleted successfully |
| 16 | Verify deleted pet is no longer retrievable (GET after DELETE) | Positive | 404 — pet not found after deletion |
| 17 | Delete non-existent pet | Negative | 404 — not found |

### FindByStatusTest — `GET /pet/findByStatus`

| # | Test Case | Type | Expected |
|---|-----------|------|----------|
| 18 | Find by status "available" — verify all returned pets have correct status | Positive | 200 — non-empty list, all statuses match |
| 19 | Find by status "pending" | Positive | 200 — valid list returned |
| 20 | Find by status "sold" | Positive | 200 — valid list returned |
| 21 | Find by invalid status ("not_a_real_status_xyz") | Negative | 200/400 — no 500 (server must not crash) |

### PetSecurityTest — Injection & XSS

| # | Test Case | Type | Expected |
|---|-----------|------|----------|
| 22 | Create pet with XSS payload: `<script>alert('XSS')</script>` | Security | 200/400 — API does not crash (no 500) |
| 23 | Create pet with XSS payload: `<img src=x onerror=alert(1)>` | Security | 200/400 — payload sanitized or stored safely |
| 24 | Create pet with XSS payload: `javascript:alert(1)` | Security | 200/400 — no script execution risk |
| 25 | Create pet with SQL injection: `' OR '1'='1` | Security | 200/400 — database not compromised |
| 26 | Create pet with SQL injection: `DROP TABLE pets;--` | Security | 200/400 — no destructive SQL execution |
| 27 | Create pet with SQL injection: `1; SELECT * FROM users` | Security | 200/400 — query not executed |

### PetTechAndPerformanceTest — Non-Functional

| # | Test Case | Type | Expected |
|---|-----------|------|----------|
| 28 | GET response time must be under 3000ms | Performance | Response time < 3s |
| 29 | GET response must include `Content-Type: application/json` and `Date` headers | Technical | Required headers present |

---

**Total: 29 test cases** across 7 test classes — covering functional (CRUD), negative, boundary, encoding, security (XSS + SQLi), and non-functional (performance + technical) scenarios.
