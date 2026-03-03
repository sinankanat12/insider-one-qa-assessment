# API Automation

Functional and contract tests for the [Petstore Swagger API](https://petstore.swagger.io/) `/pet` endpoints.

![API Tests](https://github.com/<org>/insider-one-qa-assessment/actions/workflows/api-automation.yml/badge.svg)

---

## Tech Stack

| Tool | Version |
|---|---|
| Java | 17 |
| Maven | 3.9+ |
| RestAssured | 5.4.0 |
| JUnit 5 | 5.10.2 |
| Jackson | 2.17.0 |
| Allure | 2.25.0 |

---

## Prerequisites

- Java 17+
- Maven 3.9+
- Docker (for containerised runs)
- Network access to `https://petstore.swagger.io`

---

## Running Tests

### Locally

```bash
cd api-automation
mvn test
```

### Generate Allure report

```bash
mvn allure:report
open target/site/allure-maven-plugin/index.html
```

### With Docker

```bash
cd api-automation
docker build -t api-automation:local .
```

Test results are inside the container at `/app/target/allure-results/`. Extract with:

```bash
docker cp <container_id>:/app/target/allure-results ./allure-results
```

---

## Test Coverage

15 tests across 5 classes — 10 positive, 5 negative.

| Class | Scenarios | Endpoints |
|---|---|---|
| `CreatePetTest` | P1, P2, N5 | `POST /pet` |
| `GetPetTest` | P3, N1, N4 | `GET /pet/{petId}` |
| `UpdatePetTest` | P4, P5 | `PUT /pet` |
| `DeletePetTest` | P6, P7, N2 | `DELETE /pet/{petId}` |
| `FindByStatusTest` | P8, P9, P10, N3 | `GET /pet/findByStatus` |

Full scenario list: [docs/plans/2026-03-04-qa-api-automation.md](../docs/plans/2026-03-04-qa-api-automation.md)

---

## Architecture

```
api-automation/
├── src/
│   ├── main/java/com/insiderone/qa/
│   │   ├── client/
│   │   │   ├── BaseClient.java       # RestAssured config + Allure filter + error handler
│   │   │   └── PetClient.java        # /pet endpoint methods
│   │   ├── model/
│   │   │   ├── Pet.java
│   │   │   ├── Category.java
│   │   │   └── Tag.java
│   │   └── factory/
│   │       └── PetFactory.java       # static test data factory
│   └── test/
│       ├── java/com/insiderone/qa/tests/pet/
│       │   ├── CreatePetTest.java
│       │   ├── GetPetTest.java
│       │   ├── UpdatePetTest.java
│       │   ├── DeletePetTest.java
│       │   └── FindByStatusTest.java
│       └── resources/
│           └── allure.properties
├── Dockerfile
└── pom.xml
```

---

## CI/CD

Tests run automatically on every push or pull request that modifies files under `api-automation/`. Allure reports are uploaded as GitHub Actions artifacts (30-day retention) and optionally published to GitHub Pages.

Workflow: [`.github/workflows/api-automation.yml`](../.github/workflows/api-automation.yml)
