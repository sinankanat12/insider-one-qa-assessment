# Monorepo Skeleton Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Create a professional monorepo skeleton for `insider-one-qa-assessment` with folder structure, README files, .gitignore, and docker-compose.yml scaffold.

**Architecture:** All files live at the root of `/Users/sinankanat/Desktop/insider-one-qa-assessment/`. No source code is written — only project scaffolding (README, .gitignore, docker-compose skeleton, folder structure). The three modules (ui-automation, api-automation, load-testing) are empty shells ready for future implementation.

**Tech Stack:** Git, Docker Compose v3.8, Markdown

---

### Task 1: Create directory structure

**Files:**
- Create: `ui-automation/` (directory)
- Create: `api-automation/` (directory)
- Create: `load-testing/` (directory)
- Create: `.github/workflows/` (directory)

**Step 1: Create directories**

```bash
mkdir -p ui-automation api-automation load-testing .github/workflows
```

**Step 2: Verify structure**

```bash
ls -la
```

Expected output includes: `ui-automation/`, `api-automation/`, `load-testing/`, `.github/`

**Step 3: Commit**

```bash
git add .github ui-automation api-automation load-testing
git commit -m "chore: create monorepo folder structure"
```

---

### Task 2: Create root .gitignore

**Files:**
- Create: `.gitignore`

**Step 1: Create the file with the following content**

File: `.gitignore`

```gitignore
# =====================
# Java
# =====================
*.class
*.jar
*.war
*.ear
*.nar
*.zip
*.tar.gz
*.rar
target/
.mvn/
.gradle/
build/
out/
*.iml

# =====================
# Python
# =====================
__pycache__/
*.py[cod]
*$py.class
*.pyo
*.pyd
.Python
env/
venv/
.venv/
ENV/
.env
*.egg
*.egg-info/
dist/
build/
*.spec
.pytest_cache/
.mypy_cache/
.ruff_cache/
htmlcov/
.coverage
coverage.xml
*.cover

# =====================
# Node.js
# =====================
node_modules/
npm-debug.log*
yarn-debug.log*
yarn-error.log*
pnpm-debug.log*
.pnpm-store/
.npm/
.yarn/
dist/
.next/
.nuxt/
.cache/
*.log

# =====================
# IDE - IntelliJ IDEA / JetBrains
# =====================
.idea/
*.iws
*.ipr
.idea_modules/

# =====================
# IDE - VS Code
# =====================
.vscode/
*.code-workspace

# =====================
# IDE - Eclipse
# =====================
.classpath
.project
.settings/
.factorypath

# =====================
# OS
# =====================
.DS_Store
.DS_Store?
._*
.Spotlight-V8
.Trashes
ehthumbs.db
Thumbs.db
desktop.ini

# =====================
# Docker
# =====================
.docker/
docker-compose.override.yml

# =====================
# Test Reports & Artifacts
# =====================
allure-results/
allure-report/
test-results/
reports/
screenshots/
videos/
downloads/

# =====================
# Secrets / Env
# =====================
.env
.env.*
secrets.yml
secrets.yaml
*.pem
*.key
*.p12
```

**Step 2: Verify file exists**

```bash
cat .gitignore | head -20
```

Expected: First 20 lines of the .gitignore file printed.

**Step 3: Commit**

```bash
git add .gitignore
git commit -m "chore: add comprehensive .gitignore for Java, Python, Node and IDE files"
```

---

### Task 3: Create root docker-compose.yml

**Files:**
- Create: `docker-compose.yml`

**Step 1: Create the file with the following content**

File: `docker-compose.yml`

```yaml
version: "3.8"

# ============================================================
# insider-one-qa-assessment — Docker Compose Skeleton
#
# Services are NOT yet implemented.
# Each service block is a placeholder ready for future setup.
# ============================================================

services:

  # ----------------------------------------------------------
  # UI Automation (e.g. Selenium / Playwright)
  # ----------------------------------------------------------
  ui-automation:
    build:
      context: ./ui-automation
      dockerfile: Dockerfile
    # image: ui-automation:latest
    container_name: ui-automation
    environment:
      - ENV=ci
    volumes:
      - ./ui-automation:/app
      - ./reports/ui:/app/reports
    networks:
      - qa-network
    profiles:
      - ui

  # ----------------------------------------------------------
  # API Automation (e.g. RestAssured / Pytest / Karate)
  # ----------------------------------------------------------
  api-automation:
    build:
      context: ./api-automation
      dockerfile: Dockerfile
    # image: api-automation:latest
    container_name: api-automation
    environment:
      - ENV=ci
      - BASE_URL=${BASE_URL:-https://api.example.com}
    volumes:
      - ./api-automation:/app
      - ./reports/api:/app/reports
    networks:
      - qa-network
    profiles:
      - api

  # ----------------------------------------------------------
  # Load Testing (e.g. k6 / Gatling / Locust)
  # ----------------------------------------------------------
  load-testing:
    build:
      context: ./load-testing
      dockerfile: Dockerfile
    # image: load-testing:latest
    container_name: load-testing
    environment:
      - ENV=ci
      - TARGET_URL=${TARGET_URL:-https://api.example.com}
      - VIRTUAL_USERS=${VIRTUAL_USERS:-10}
      - DURATION=${DURATION:-30s}
    volumes:
      - ./load-testing:/app
      - ./reports/load:/app/reports
    networks:
      - qa-network
    profiles:
      - load

networks:
  qa-network:
    driver: bridge

volumes:
  reports:
```

**Step 2: Verify file is valid YAML**

```bash
docker compose config --quiet 2>&1 || echo "Note: Dockerfiles not yet created, skeleton is valid YAML"
```

Expected: Either a validation confirmation or the expected Dockerfile-not-found warning (both are acceptable at this stage).

**Step 3: Commit**

```bash
git add docker-compose.yml
git commit -m "chore: add docker-compose.yml skeleton for ui-automation, api-automation and load-testing"
```

---

### Task 4: Create module README files

**Files:**
- Create: `ui-automation/README.md`
- Create: `api-automation/README.md`
- Create: `load-testing/README.md`

**Step 1: Create `ui-automation/README.md` with the following content**

File: `ui-automation/README.md`

```markdown
# UI Automation

End-to-end browser automation tests for the Insider One platform.

## Overview

This module contains browser-based end-to-end tests using [Selenium / Playwright — TBD].
Tests cover critical user journeys across the web application.

## Tech Stack

> To be defined during implementation.

- Language: Java or Python
- Framework: Selenium WebDriver / Playwright
- Reporting: Allure

## Prerequisites

- Java 17+ / Python 3.10+ (depending on chosen stack)
- Chrome / Firefox browser installed
- Docker (optional, for containerised runs)

## Running Tests

### Locally

```bash
# To be filled in after implementation
```

### With Docker

```bash
docker compose --profile ui up --build
```

## Folder Structure

```
ui-automation/
├── src/              # Test source code
├── reports/          # Test run reports (generated)
├── Dockerfile        # Container definition (to be created)
└── README.md
```

## Reports

After a test run, Allure reports are generated under `reports/`.
```

**Step 2: Create `api-automation/README.md` with the following content**

File: `api-automation/README.md`

```markdown
# API Automation

Contract and functional tests for the Insider One REST / GraphQL APIs.

## Overview

This module contains automated API tests validating request/response contracts,
status codes, schema correctness, and business logic across all API endpoints.

## Tech Stack

> To be defined during implementation.

- Language: Java or Python
- Framework: RestAssured / Pytest + HTTPX / Karate
- Reporting: Allure

## Prerequisites

- Java 17+ / Python 3.10+ (depending on chosen stack)
- Network access to target API environment
- Docker (optional, for containerised runs)

## Configuration

Tests read target URL from the `BASE_URL` environment variable.

```bash
export BASE_URL=https://api.example.com
```

## Running Tests

### Locally

```bash
# To be filled in after implementation
```

### With Docker

```bash
docker compose --profile api up --build
```

## Folder Structure

```
api-automation/
├── src/              # Test source code
├── reports/          # Test run reports (generated)
├── Dockerfile        # Container definition (to be created)
└── README.md
```
```

**Step 3: Create `load-testing/README.md` with the following content**

File: `load-testing/README.md`

```markdown
# Load Testing

Performance and load tests for the Insider One platform.

## Overview

This module contains load, stress, and spike tests to measure system behaviour
under concurrent user traffic. Tests are designed to identify performance
bottlenecks and validate SLA thresholds.

## Tech Stack

> To be defined during implementation.

- Tool: k6 / Gatling / Locust (TBD)
- Metrics: Response time (p95, p99), error rate, throughput (RPS)
- Reporting: k6 HTML report / Gatling HTML report

## Prerequisites

- k6 / Gatling / Locust installed (depending on chosen stack)
- Docker (optional, for containerised runs)

## Configuration

Key parameters are controlled via environment variables:

| Variable        | Default                    | Description                     |
|-----------------|----------------------------|---------------------------------|
| `TARGET_URL`    | `https://api.example.com`  | Base URL under test             |
| `VIRTUAL_USERS` | `10`                       | Number of concurrent users      |
| `DURATION`      | `30s`                      | Test duration                   |

## Running Tests

### Locally

```bash
# To be filled in after implementation
```

### With Docker

```bash
docker compose --profile load up --build
```

## Folder Structure

```
load-testing/
├── scenarios/        # Load test scripts / scenarios
├── reports/          # Test run reports (generated)
├── Dockerfile        # Container definition (to be created)
└── README.md
```

## Acceptance Criteria (SLA Targets)

> To be defined per endpoint / scenario.

- p95 response time < 500 ms
- Error rate < 1%
- Throughput > 100 RPS
```

**Step 4: Verify files exist**

```bash
ls ui-automation/README.md api-automation/README.md load-testing/README.md
```

Expected: All three paths printed without error.

**Step 5: Commit**

```bash
git add ui-automation/README.md api-automation/README.md load-testing/README.md
git commit -m "docs: add module README files for ui-automation, api-automation and load-testing"
```

---

### Task 5: Create root README.md

**Files:**
- Modify: `README.md` (overwrite the existing placeholder)

**Step 1: Overwrite `README.md` with the following content**

File: `README.md`

```markdown
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
```

**Step 2: Verify the file**

```bash
head -10 README.md
```

Expected: First 10 lines of the new root README printed.

**Step 3: Commit**

```bash
git add README.md
git commit -m "docs: add professional root README with module overview and quick start guide"
```

---

### Task 6: Final verification

**Step 1: Verify complete directory structure**

```bash
find . -not -path './.git/*' -not -path './.claude/*' | sort
```

Expected output (order may vary):

```
.
./.github
./.github/workflows
./.gitignore
./README.md
./api-automation
./api-automation/README.md
./docker-compose.yml
./docs
./docs/plans
./docs/plans/2026-03-04-monorepo-skeleton.md
./load-testing
./load-testing/README.md
./ui-automation
./ui-automation/README.md
```

**Step 2: Verify git log**

```bash
git log --oneline
```

Expected: 5 new commits (Tasks 1–5) on top of the initial commit.

**Step 3: Done**

Monorepo skeleton is complete. All modules are empty shells ready for implementation.
