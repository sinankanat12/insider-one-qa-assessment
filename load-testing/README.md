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
