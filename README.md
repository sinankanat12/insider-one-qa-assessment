# insider-one-qa-assessment

A monorepo containing the full QA automation suite for the Insider One platform.

---

## Modules

| Module | Description | Stack |
|---|---|---|
| [`ui-automation`](./ui-automation) | End-to-end browser automation tests | Selenium / Playwright (TBD) |
| [`api-automation`](./api-automation) | REST / GraphQL API contract & functional tests | RestAssured / Pytest (TBD) |
| [`load-testing`](./load-testing) | Performance, load, and stress tests | k6 / Gatling / Locust (TBD) |

---

## Repository Structure

```
insider-one-qa-assessment/
├── .github/
│   └── workflows/          # CI/CD pipeline definitions (coming soon)
├── ui-automation/
│   └── README.md
├── api-automation/
│   └── README.md
├── load-testing/
│   └── README.md
├── docs/
│   └── plans/              # Implementation plans
├── docker-compose.yml      # Container orchestration skeleton
├── .gitignore
└── README.md
```

---

## Quick Start

### Prerequisites

- Git
- Docker & Docker Compose v2+
- (Module-specific runtimes — see each module's README)

### Clone the repository

```bash
git clone https://github.com/<org>/insider-one-qa-assessment.git
cd insider-one-qa-assessment
```

### Run a specific module with Docker

```bash
# UI Automation
docker compose --profile ui up --build

# API Automation
docker compose --profile api up --build

# Load Testing
docker compose --profile load up --build
```

### Run all modules

```bash
docker compose --profile ui --profile api --profile load up --build
```

---

## CI / CD

GitHub Actions workflows will be added under `.github/workflows/` as each module is implemented.

---

## Contributing

1. Create a feature branch from `main`.
2. Follow the coding conventions in each module's README.
3. Ensure all tests pass locally before opening a pull request.
4. Add or update documentation for any changes.

---

## License

Internal use only — Insider One QA Team.
