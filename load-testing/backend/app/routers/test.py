from fastapi import APIRouter, HTTPException

from app.models.schemas import StartTestRequest, StartTestResponse, StopTestResponse
from app.services.locust_manager import locust_manager

router = APIRouter(prefix="/test", tags=["test"])


@router.post("/start", response_model=StartTestResponse)
async def start_test(request: StartTestRequest):
    try:
        await locust_manager.start(request)
    except Exception as exc:
        raise HTTPException(status_code=500, detail=str(exc))

    return StartTestResponse(
        status=locust_manager.state,
        pid=locust_manager.pid,
        message="Test started successfully",
    )


@router.post("/stop", response_model=StopTestResponse)
async def stop_test():
    await locust_manager.stop()
    return StopTestResponse(status=locust_manager.state, message="Test stopped")
