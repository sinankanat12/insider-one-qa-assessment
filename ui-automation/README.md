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
