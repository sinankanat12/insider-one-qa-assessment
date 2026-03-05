# Load Testing — n11.com Search Module

A full-stack **load testing platform** built to investigate the behavior of n11.com's search module under load. Rather than a simple Locust script, this project delivers a **real-time web dashboard** that lets you configure, execute, monitor, and analyze load tests — all from the browser.

Built with **Locust** (load engine) + **FastAPI** (control API) + **React** (dashboard UI), containerized with **Docker Compose**.

---

## Test Scenarios

The following scenarios target n11.com's **search module** — the header search bar and the search results listing page.

### Scenario 1: Homepage Load

| Item | Detail |
|------|--------|
| **Objective** | Verify homepage availability and response time under load |
| **Endpoint** | `GET /` |
| **Expected** | HTTP 200, page loads within acceptable response time |
| **Weight** | 1 (background traffic) |

### Scenario 2: Search with Keyword — "laptop"

| Item | Detail |
|------|--------|
| **Objective** | Test search module behavior when a user searches for "laptop" via the header search bar |
| **Endpoint** | `GET /arama?q=laptop` |
| **Expected** | HTTP 200, search results page returns product listings |
| **Weight** | 3 (primary scenario — highest traffic) |

### Scenario 3: Search with Keyword — "telefon"

| Item | Detail |
|------|--------|
| **Objective** | Test search module with a different keyword to observe consistency across different search terms |
| **Endpoint** | `GET /arama?q=telefon` |
| **Expected** | HTTP 200, search results page returns product listings |
| **Weight** | 2 (secondary scenario) |

### Traffic Distribution

With the default weights (1 : 3 : 2), approximate traffic distribution:

| Scenario | Weight | Traffic % |
|----------|--------|-----------|
| Homepage | 1 | ~17% |
| Search "laptop" | 3 | ~50% |
| Search "telefon" | 2 | ~33% |

> Scenarios are fully configurable from the dashboard UI — you can add/remove endpoints, change search keywords, adjust weights, and modify query parameters without touching any code.

### Acceptance Criteria (SLA Targets)

| Metric | Target |
|--------|--------|
| p95 Response Time | < 500 ms |
| Error Rate | < 1% |
| Throughput | > 100 RPS (at target load) |

---

## Quick Start

```bash
# 1. Navigate to project root
cd insider-one-qa-assessment

# 2. Create .env (no changes needed for default n11.com target)
cp load-testing/.env.example load-testing/.env

# 3. Start all services (first run: ~3 min for image builds)
docker compose --profile load up --build

# 4. Open dashboard
#    http://localhost:3000

# 5. Click "START TEST" — metrics stream in real-time

# 6. When done
docker compose --profile load down
```

---

## What This Project Does

This is not just a Locust script — it's a **complete load testing platform** with three integrated services:

### 1. Real-Time Dashboard (React + Tailwind + Nginx)
- Configure target URL, endpoints, query parameters, virtual users, spawn rate, and duration
- One-click start/stop test execution
- Live metric cards: Total Requests, RPS, Avg Response Time, Failure Rate
- Real-time RPS chart that updates every second
- Request log stream showing individual requests with status codes and latency
- All configuration persisted in browser localStorage

### 2. Control API (FastAPI + SQLAlchemy)
- RESTful API that manages the entire Locust subprocess lifecycle
- Real-time metrics streaming from Locust's internal web API
- Test history stored in SQLite — every completed test is automatically recorded
- CSV export of historical test results for reporting

### 3. Load Engine (Locust — Dynamic TaskSet)
- Endpoints are **not hardcoded** — they're dynamically built from JSON configuration at runtime
- Supports GET, POST, PUT, DELETE with custom query parameters and weighted traffic distribution
- Realistic browser headers (User-Agent, Accept-Language) to simulate real user traffic
- Headless execution controlled entirely by the backend API

---

## Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                     Browser (localhost:3000)                   │
│  ┌─────────────┐  ┌──────────────┐  ┌─────────────────────┐ │
│  │ Config Panel │  │ Metrics Cards│  │ RPS Chart + Logs    │ │
│  │ Endpoints    │  │ RPS, p95, %  │  │ Real-time stream    │ │
│  │ Start/Stop   │  │ Failure Rate │  │ Per-request detail  │ │
│  └──────┬──────┘  └──────┬───────┘  └──────────┬──────────┘ │
└─────────┼────────────────┼──────────────────────┼────────────┘
          │ POST /test/*   │ GET /metrics (1s)    │ GET /status (1s)
          ▼                ▼                      ▼
┌──────────────────────────────────────────────────────────────┐
│                    FastAPI Backend (:8000)                     │
│  ┌──────────────────┐  ┌────────────────┐  ┌──────────────┐ │
│  │  locust_manager   │  │ metrics_reader │  │   SQLite DB  │ │
│  │  (subprocess)     │  │ (HTTP → Locust)│  │  (history)   │ │
│  └────────┬─────────┘  └───────┬────────┘  └──────────────┘ │
└───────────┼─────────────────────┼────────────────────────────┘
            │ spawns              │ GET /stats/requests
            ▼                     ▼
┌──────────────────────────────────────────────────────────────┐
│                 Locust Engine (subprocess)                     │
│  ┌──────────────────┐  ┌────────────────────────────────────┐│
│  │  locustfile.py    │  │  task_builder.py                   ││
│  │  (entry point)    │  │  (dynamic TaskSet from JSON)       ││
│  └──────────────────┘  └────────────────────────────────────┘│
│                              │ HTTP traffic                    │
└──────────────────────────────┼────────────────────────────────┘
                               ▼
                        https://www.n11.com
```

**Data flow:**
1. User configures test in React UI → `POST /test/start` with JSON payload
2. Backend writes `current_test.json` to shared volume, spawns Locust subprocess
3. Locust reads config, builds weighted TaskSet dynamically, starts sending traffic
4. Frontend polls `/metrics` every second → backend fetches from Locust's REST API (`/stats/requests`)
5. Live metrics (RPS, response times, failures) stream to dashboard in real-time
6. On test completion, backend saves summary to SQLite → visible in History page

---

## Services

| Service | Port | Technology | Description |
|---------|------|------------|-------------|
| `load-frontend` | 3000 | React 18 + Vite 5 + Tailwind 3 + Nginx | Dashboard UI |
| `load-backend` | 8000 | FastAPI + SQLAlchemy + httpx | Control API + Locust manager |
| `load-locust` | 8089 | Locust 2.29 | Web UI for debugging (optional) |

---

## Backend API

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/test/start` | Start load test with configuration |
| `POST` | `/test/stop` | Stop running test |
| `GET` | `/test/history` | Retrieve all past test results |
| `GET` | `/status` | Current test state (idle/running/stopped/error) |
| `GET` | `/metrics` | Live stats from Locust engine |
| `GET` | `/health` | Health check |

### Sample Request — `/test/start`

```json
{
  "base_url": "https://www.n11.com",
  "endpoints": [
    { "path": "/",      "method": "GET", "weight": 1, "query_params": {} },
    { "path": "/arama", "method": "GET", "weight": 3, "query_params": { "q": "laptop" } },
    { "path": "/arama", "method": "GET", "weight": 2, "query_params": { "q": "telefon" } }
  ],
  "user_count": 1,
  "spawn_rate": 1,
  "duration_seconds": 60
}
```

---

## Dashboard Features

### Test Configuration (Left Panel)
- **Base URL** — Target website (default: `https://www.n11.com`)
- **Endpoints** — Add/remove endpoints with path, HTTP method, weight, and query parameters
- **Query Parameters** — Inline preview showing `?q=laptop` below each endpoint, expandable editor for key-value pairs
- **Virtual Users** — Number of concurrent simulated users
- **Spawn Rate** — Users spawned per second
- **Duration** — Test duration in seconds
- All fields are **disabled during test execution** to prevent configuration changes mid-test

### Real-Time Monitoring (Right Panel)
- **Metric Cards** — Total Requests, Requests/Sec, Avg Response Time, Failure Rate
- **RPS Chart** — Live line chart updating every second
- **Request Logs** — Scrollable table showing individual requests with timestamp, method, path (including query params), status code, and latency

### History Page
- **Test History Table** — All past test runs with date, base URL, endpoints, duration, requests, avg RPS, avg response time, and failure rate
- **CSV Export** — Download full history as CSV file for reporting

---

## Two Execution Modes

This project supports two ways to run load tests — a full **dashboard mode** for interactive testing and a **headless mode** for CI/CD automation.

### Dashboard Mode (Docker Compose)

The primary mode — a full web UI for configuring, running, and monitoring tests in real-time.

```bash
docker compose --profile load up --build
# Open http://localhost:3000
```

- 3 services start: React frontend, FastAPI backend, Locust engine
- Configure endpoints, users, duration from the browser
- Click START — metrics stream live to the dashboard
- Backend spawns Locust with `--autostart --web-port 8089`
- Backend polls Locust's REST API (`/stats/requests`) for real-time metrics
- On completion, results are saved to SQLite and visible in the History page

### Headless Mode (CI / Terminal)

Runs Locust directly — no UI, no backend, no Docker required. Ideal for CI pipelines and quick terminal tests.

```bash
locust -f locustfile.py --headless -u 1 -r 1 -t 60s --json > results.json
```

- No frontend or backend involved — just Locust + Python
- `--json` flag outputs stats to stdout when the test **completes** (not during)
- `--headless` disables Locust's web UI entirely
- Configuration is read from `current_test.json` at startup
- Exit code 1 if any HTTP failures occurred (expected for sites with bot protection)

### Comparison

| | Dashboard Mode | Headless Mode |
|---|---|---|
| **Start command** | `docker compose --profile load up` | `locust -f locustfile.py --headless` |
| **UI** | React dashboard at localhost:3000 | None — terminal output only |
| **Backend** | FastAPI manages Locust subprocess | Not used |
| **Metrics** | Real-time streaming every 1s | JSON dump after test ends |
| **Config** | Browser UI (dynamic) | `current_test.json` (static) |
| **History** | SQLite + CSV export | Artifact upload |
| **Use case** | Interactive testing & monitoring | CI/CD automation |

---

## CI/CD — GitHub Actions

The workflow (`.github/workflows/load-testing.yml`) runs Locust in **headless mode** on every push — no Docker needed.

**Trigger:** Push/PR on `load-testing/**` or manual `workflow_dispatch` (with configurable duration and user count).

**Pipeline steps:**
1. Checkout code + set up Python 3.11
2. Install Locust from `load-testing/locust/requirements.txt`
3. Write test config (all 3 search scenarios) to `current_test.json`
4. Run Locust headless for configured duration
5. Upload `locust_stats.json` + `locust.log` as artifacts (30-day retention)

**Note on exit codes:** n11.com has bot protection that may return HTTP 403 for automated requests. Locust treats non-2xx responses as failures and exits with code 1. The pipeline handles this gracefully with `|| true` — the test results are still captured and uploaded regardless of HTTP status codes. A 403 from bot protection is an expected observation, not a pipeline failure.

```bash
# Simulate CI locally
pip install -r load-testing/locust/requirements.txt
mkdir -p load-testing/locust/results

cat > load-testing/locust/results/current_test.json << 'EOF'
{
  "base_url": "https://www.n11.com",
  "endpoints": [
    { "path": "/",      "method": "GET", "weight": 1, "query_params": {} },
    { "path": "/arama", "method": "GET", "weight": 3, "query_params": { "q": "laptop" } },
    { "path": "/arama", "method": "GET", "weight": 2, "query_params": { "q": "telefon" } }
  ],
  "user_count": 1,
  "spawn_rate": 1,
  "duration_seconds": 10
}
EOF

cd load-testing/locust
locust -f locustfile.py --headless -u 1 -r 1 -t 10s --json > results/locust_stats.json
```

Artifacts are uploaded with 30-day retention.

---

## Tech Stack

| Layer | Technology | Version |
|-------|------------|---------|
| Frontend | React + Vite + Tailwind CSS | 18.3 / 5.4 / 3.4 |
| Backend | FastAPI + Uvicorn + Pydantic | 0.111 / 0.30 / 2.7 |
| Database | SQLAlchemy + SQLite | 2.0+ |
| Load Engine | Locust | 2.29 |
| Metrics Transport | httpx → Locust REST API | 0.27 |
| Containerization | Docker + Docker Compose | v3.8 |
| Web Server | Nginx | 1.27 |
| CI/CD | GitHub Actions | — |

---

## Folder Structure

```
load-testing/
├── .env.example                    # All environment variables documented
├── README.md
├── frontend/                       # React dashboard
│   ├── Dockerfile                  # Multi-stage: Node 20 build → Nginx serve
│   ├── nginx.conf                  # Reverse proxy /test,/metrics,/status → backend
│   ├── package.json
│   └── src/
│       ├── api/backendClient.js    # HTTP client for backend API
│       ├── hooks/usePolling.js     # Generic polling hook
│       ├── pages/
│       │   ├── DashboardPage.jsx   # Main test control + monitoring
│       │   └── HistoryPage.jsx     # Test history + CSV export
│       └── components/
│           ├── Header.jsx
│           ├── BaseUrlInput.jsx
│           ├── EndpointBuilder.jsx
│           ├── EndpointRow.jsx     # Path + method + weight + query params editor
│           ├── TestConfigPanel.jsx
│           ├── MetricsDashboard.jsx
│           ├── RpsChart.jsx
│           ├── RequestLogs.jsx
│           └── StatusBadge.jsx
├── backend/                        # FastAPI control API
│   ├── Dockerfile
│   ├── requirements.txt
│   └── app/
│       ├── main.py                 # App entry + CORS + router registration
│       ├── database.py             # SQLite engine + session factory
│       ├── core/config.py          # Pydantic settings from .env
│       ├── models/
│       │   ├── schemas.py          # Request/response models
│       │   └── history.py          # TestHistory ORM model
│       ├── routers/
│       │   ├── test.py             # /test/start, /test/stop, /test/history
│       │   └── metrics.py          # /status, /metrics, /health
│       └── services/
│           ├── locust_manager.py   # Subprocess lifecycle + metrics caching
│           └── metrics_reader.py   # Reads Locust web API → MetricsResponse
└── locust/                         # Locust load engine
    ├── Dockerfile
    ├── requirements.txt
    ├── locustfile.py               # Entry point — reads config, builds user class
    ├── task_builder.py             # Dynamic TaskSet from JSON configuration
    └── results/                    # Shared volume mount (config + stats + logs)
```
