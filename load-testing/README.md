# Load Testing

Browser-controlled load test platform targeting n11.com search module.
Built with **Locust** (engine) + **FastAPI** (backend API) + **React** (dashboard UI).

---

## Architecture

```
Browser → React UI (port 3000)
              │  Nginx proxy
              ▼
         FastAPI Backend (port 8000)
              │  subprocess.Popen
              ▼
         Locust Engine (headless)
              │  HTTP load traffic
              ▼
           n11.com

Shared volume (locust-data):
  current_test.json  ← backend writes, locust reads at startup
  locust_stats.json  ← locust writes every ~2s, backend reads on /metrics
```

---

## HR Quick Start

```bash
# 1. Enter project root
cd insider-one-qa-assessment

# 2. Create .env (no changes needed for default n11.com target)
cp load-testing/.env.example load-testing/.env

# 3. Start all services (first run: ~3 min for image builds)
docker compose --profile load up --build

# 4. Open dashboard
#    http://localhost:3000

# 5. Stop
docker compose --profile load down
```

---

## Services

| Service | Port | Description |
|---|---|---|
| `load-frontend` | 3000 | React dashboard (Nginx) |
| `load-backend` | 8000 | FastAPI control API |
| `load-locust` | 8089 | Locust web UI (debug only) |

---

## Backend API

| Method | Path | Description |
|---|---|---|
| `POST` | `/test/start` | Start load test with config |
| `POST` | `/test/stop` | Stop running test (idempotent) |
| `GET` | `/status` | Process state — 1s polling |
| `GET` | `/metrics` | Live stats from shared volume — 3s polling |
| `GET` | `/health` | Docker healthcheck |

### `/test/start` request body

```json
{
  "base_url": "https://www.n11.com",
  "endpoints": [
    { "path": "/arama", "method": "GET", "weight": 3, "query_params": { "q": "laptop" } },
    { "path": "/",      "method": "GET", "weight": 1, "query_params": {} }
  ],
  "user_count": 1,
  "spawn_rate": 1,
  "duration_seconds": 60
}
```

---

## CI — Headless Locust Only

The GitHub Actions workflow (`.github/workflows/load-testing.yml`) runs Locust directly on the runner — no Docker, no frontend/backend.

**Trigger:** push/PR on `load-testing/**` or `workflow_dispatch`.

```bash
# Simulate CI locally
pip install -r load-testing/locust/requirements.txt
mkdir -p load-testing/locust/results

cat > load-testing/locust/results/current_test.json << 'EOF'
{
  "base_url": "https://www.n11.com",
  "endpoints": [
    { "path": "/arama", "method": "GET", "weight": 3, "query_params": { "q": "laptop" } }
  ],
  "user_count": 1,
  "spawn_rate": 1,
  "duration_seconds": 10
}
EOF

cd load-testing/locust
locust -f locustfile.py --headless -u 1 -r 1 -t 10s --json > results/locust_stats.json
```

---

## Acceptance Criteria (SLA Targets)

- p95 response time < 500 ms
- Error rate < 1%
- Throughput > 100 RPS (at target load)

---

## Folder Structure

```
load-testing/
├── .env.example
├── README.md
├── frontend/           # React dashboard (Vite + Tailwind)
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── package.json
│   └── src/
│       ├── api/backendClient.js
│       ├── components/
│       └── hooks/usePolling.js
├── backend/            # FastAPI control API
│   ├── Dockerfile
│   ├── requirements.txt
│   └── app/
│       ├── main.py
│       ├── routers/    (test.py, metrics.py)
│       ├── services/   (locust_manager.py, metrics_reader.py)
│       ├── models/     (schemas.py)
│       └── core/       (config.py)
└── locust/             # Locust engine
    ├── Dockerfile
    ├── requirements.txt
    ├── locustfile.py
    ├── task_builder.py
    └── results/        (shared volume mount point)
```
