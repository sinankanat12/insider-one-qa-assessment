from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.core.config import settings
from app.routers import metrics, test
from app.database import engine
from app.models import history

# Create tables
history.Base.metadata.create_all(bind=engine)

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Ensure current_test.json exists for Locust engine on startup
    import os
    import json
    from datetime import datetime

    config_path = os.path.join(settings.locust_config_dir, "current_test.json")
    if not os.path.exists(config_path):
        os.makedirs(settings.locust_config_dir, exist_ok=True)
        default_config = {
            "base_url": settings.target_base_url,
            "endpoints": [
                {"path": "/arama", "method": "GET", "weight": 2, "query_params": {"q": "laptop"}},
                {"path": "/", "method": "GET", "weight": 1}
            ],
            "user_count": settings.default_user_count,
            "spawn_rate": settings.default_spawn_rate,
            "duration_seconds": settings.default_duration_seconds,
            "started_at": datetime.utcnow().isoformat() + "Z",
        }
        with open(config_path, "w") as f:
            json.dump(default_config, f, indent=2)
    yield


app = FastAPI(
    title="Load Testing Backend",
    description="Controls Locust load test process and exposes metrics",
    version="1.0.0",
    lifespan=lifespan,
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.cors_origins_list,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(test.router)
app.include_router(metrics.router)
