# Insider One — QA Automation Assessment

A **production-grade QA automation suite** covering the three pillars of software quality: **UI testing**, **API testing**, and **performance testing**. Each module is independently runnable, fully containerized with Docker, integrated into CI/CD via GitHub Actions, and produces detailed Allure / artifact-based reports.

| Module | What It Does | Stack |
|--------|-------------|-------|
| [`ui-automation`](./ui-automation) | End-to-end browser tests using Page Object Model & Flow patterns | Java 17, Selenium, JUnit 5, Allure |
| [`api-automation`](./api-automation) | REST API functional, negative, and contract tests | Java 17, RestAssured, JUnit 5, Allure |
| [`load-testing`](./load-testing) | Full-stack load testing platform with real-time dashboard | Locust, FastAPI, React, Docker Compose |

---

## Architecture Overview

```
insider-one-qa-assessment/
├── ui-automation/           # Selenium-based E2E tests
│   ├── pages/               #   Page Object classes
│   ├── flows/               #   Multi-page user journey abstractions
│   └── tests/               #   JUnit 5 test classes
│
├── api-automation/          # RestAssured API tests
│   ├── client/              #   Fluent API clients (BaseClient → PetClient)
│   ├── model/               #   POJOs with Jackson serialization
│   ├── factory/             #   Test data factories (randomized)
│   └── tests/               #   Functional + contract tests
│
├── load-testing/            # 3-service load testing platform
│   ├── frontend/            #   React 18 + Vite + Tailwind dashboard
│   ├── backend/             #   FastAPI control API + SQLite history
│   └── locust/              #   Dynamic TaskSet builder (no hardcoded endpoints)
│
├── .github/workflows/       # CI/CD — one pipeline per module
├── docker-compose.yml       # Multi-profile orchestration (ui / api / load)
└── pages-assets/            # GitHub Pages — Allure report portal
```

---

## Quick Start

### Prerequisites

- **Java JDK 17+** and **Maven** (for UI & API modules)
- **Docker & Docker Compose v2+** (for containerized execution)
- **Python 3.11+** (optional — for local Locust runs)

### Clone & Run

```bash
git clone https://github.com/sinankanat12/insider-one-qa-assessment.git
cd insider-one-qa-assessment
```

Each module can run independently via Docker profiles:

```bash
# UI Tests — spins up Selenium Grid (Hub + Chrome + Firefox) + test runner
# Note: docker-compose.yml uses seleniarm/* images for Apple Silicon (M-series) Macs.
# On Intel/AMD machines, replace seleniarm/* with selenium/* in docker-compose.yml.
docker compose --profile ui up --build

# API Tests — runs RestAssured tests against Petstore Swagger API
docker compose --profile api up --build

# Load Tests — launches React dashboard + FastAPI backend + Locust engine
docker compose --profile load up --build
# Then open http://localhost:3000 and click START TEST
```

Or run locally without Docker:

```bash
# UI Tests
cd ui-automation && mvn clean test

# API Tests
cd api-automation && mvn clean test
```

---

## Module Highlights

### UI Automation

- **Page Object Model** with a `BasePage` providing centralized wait strategies, scrolling, and element interaction utilities
- **Flow-based abstraction** — complex multi-page journeys (e.g., `JobSearchFlow`) are encapsulated in dedicated flow classes
- **Automatic screenshots on failure** via a custom JUnit 5 extension, attached to Allure reports
- **Selenium Grid support** — Docker Compose orchestrates Hub + Chrome/Firefox nodes for cross-browser testing
- **`InsiderFailingUITest`** — a deliberately failing test that demonstrates the screenshot capture mechanism

### API Automation

- **Fluent API client** (`PetClient`) built on a `BaseClient` abstraction with centralized RestAssured configuration
- **Model-driven testing** — POJOs for Pet, Category, and Tag with Jackson serialization
- **Factory pattern** (`PetFactory`) for randomized, dynamic test data to reduce test fragility
- **Full CRUD coverage** — positive, negative (non-existent IDs, malformed requests), and contract tests (schema validation, HTTP status assertions)

### Load Testing

This goes beyond a simple script — it's a **complete 3-service platform**:

- **React dashboard** — configure endpoints, virtual users, spawn rate, and duration; monitor RPS, response times, and failure rates in real-time
- **FastAPI backend** — manages Locust subprocess lifecycle, streams metrics via Locust's REST API, stores test history in SQLite with CSV export
- **Dynamic Locust engine** — endpoints are built from JSON configuration at runtime (no code changes needed); supports weighted traffic distribution across scenarios
- **Two execution modes** — interactive dashboard mode (Docker) and headless mode (CI/terminal)

> See [`load-testing/README.md`](./load-testing) for the full architecture diagram, API reference, and dashboard feature breakdown.

---

## CI/CD

Three independent GitHub Actions workflows — each triggers only on changes to its respective module:

| Workflow | Trigger Path | What It Does |
|----------|-------------|--------------|
| `ui-automation.yml` | `ui-automation/**` | Runs Selenium tests with Allure reporting |
| `api-automation.yml` | `api-automation/**` | Runs RestAssured tests with Allure reporting |
| `load-testing.yml` | `load-testing/**` | Runs Locust headless, uploads stats as artifacts |

All workflows also support `workflow_dispatch` for manual triggering.

---

## Docker Services

The entire suite is orchestrated through a single `docker-compose.yml` with **profile-based isolation**:

| Service | Profile | Port | Purpose |
|---------|---------|------|---------|
| `selenium-hub` | `ui` | 4444 | Selenium Grid Hub (seleniarm) |
| `chrome-node` | `ui` | 7900 | Chromium browser node (seleniarm) |
| `firefox-node` | `ui` | 7901 | Firefox browser node (seleniarm) |
| `ui-automation` | `ui` | — | Test runner (depends on Grid) |
| `api-automation` | `api` | — | RestAssured test runner |
| `load-frontend` | `load` | 3000 | React dashboard (Nginx) |
| `load-backend` | `load` | 8000 | FastAPI control API |
| `load-locust` | `load` | 8089 | Locust engine |

---

## Tech Stack

| Category | Technologies |
|----------|-------------|
| **Languages** | Java 17, Python 3.11, JavaScript (ES6+) |
| **UI Testing** | Selenium WebDriver, JUnit 5, Allure |
| **API Testing** | RestAssured 5.4.0, Jackson, JUnit 5, Allure |
| **Load Testing** | Locust 2.29, FastAPI 0.111, React 18, Vite 5, Tailwind CSS 3 |
| **Infrastructure** | Docker Compose, Selenium Grid 4, Nginx, SQLite |
| **CI/CD** | GitHub Actions |
| **Reporting** | Allure Reports, GitHub Pages, CSV export |

---

## Reporting

- **Allure Reports** — UI and API modules generate rich, interactive HTML reports with request/response logs, execution timelines, and failure screenshots
- **GitHub Pages** — automated report portal at the repository's Pages URL
- **Load Testing History** — SQLite-backed history with CSV export from the dashboard

```bash
# View Allure report after a local test run
cd ui-automation && allure serve target/allure-results
cd api-automation && allure serve target/allure-results
```
