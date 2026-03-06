# Insider One QA Automation Suite

This monorepo contains the complete quality assurance automation infrastructure for the Insider One platform, covering UI, API, and Load testing.

---

## Modules

| Module | Description | Tech Stack |
|---|---|---|
| [`ui-automation`](./ui-automation) | End-to-end browser automation following POM & Flow patterns | **Java, Selenium, JUnit 5** |
| [`api-automation`](./api-automation) | REST API functional and contract testing using model-driven patterns | **Java, RestAssured, JUnit 5** |
| [`load-testing`](./load-testing) | Performance, load, and stress testing for the search module (k6 based) | **JS, k6, Docker** |

---

## Repository Structure

```text
insider-one-qa-assessment/
├── .github/
│   └── workflows/          # CI/CD pipeline definitions
├── ui-automation/          # Browser-based E2E tests
├── api-automation/         # API-level functional tests
├── load-testing/           # Performance and load tests
├── docs/                   # implementation plans and reports
├── docker-compose.yml      # Multi-module container orchestration
├── .gitignore
└── README.md
```

---

## Quick Start

### Prerequisites

- **Java JDK 17+**
- **Maven**
- **Docker & Docker Compose v2+**
- **Git**

### Clone the repository
```bash
git clone https://github.com/sinankanat12/insider-one-qa-assessment.git
cd insider-one-qa-assessment
```

### Module Execution (via Docker)

You can run individual modules or the entire suite using Docker profiles:

```bash
# Run UI Automation
docker compose --profile ui up --build

# Run API Automation
docker compose --profile api up --build

# Run Load Testing
docker compose --profile load up --build
```

---

## CI / CD

The suite is integrated with **GitHub Actions**. Every pull request or push to the main branch triggers automated test runs across all modules, with Allure results stored as artifacts.

---

## License
Internal use only — Insider One QA Team.
