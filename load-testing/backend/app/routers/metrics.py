from fastapi import APIRouter

from app.models.schemas import HealthResponse, MetricsResponse, StatusResponse
from app.services.locust_manager import locust_manager
from app.services.metrics_reader import read_metrics

router = APIRouter(tags=["metrics"])


@router.get("/status", response_model=StatusResponse)
async def get_status():
    return StatusResponse(
        status=locust_manager.state,
        pid=locust_manager.pid,
        uptime_seconds=locust_manager.uptime_seconds,
        error_message=locust_manager.error_message,
    )


@router.get("/metrics", response_model=MetricsResponse)
async def get_metrics():
    return read_metrics(locust_manager.state)


@router.get("/health", response_model=HealthResponse)
async def health():
    return HealthResponse(ok=True)
