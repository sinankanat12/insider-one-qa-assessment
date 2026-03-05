from fastapi import APIRouter, HTTPException, Depends
from sqlalchemy.orm import Session

from app.models.schemas import StartTestRequest, StartTestResponse, StopTestResponse, TestHistoryResponse
from app.services.locust_manager import locust_manager
from app.database import get_db
from app.models import history

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


@router.get("/history", response_model=list[TestHistoryResponse])
def get_history(db: Session = Depends(get_db)):
    records = db.query(history.TestHistory).order_by(history.TestHistory.id.desc()).all()
    # Serialize to response model, formatting the date
    result = []
    for r in records:
        result.append(TestHistoryResponse(
            id=r.id,
            created_at=r.created_at.strftime("%Y-%m-%d %H:%M:%S") if r.created_at else "",
            base_url=r.base_url or "",
            endpoints=r.endpoints or 0,
            duration_seconds=r.duration_seconds or 0,
            num_requests=r.num_requests or 0,
            num_failures=r.num_failures or 0,
            avg_rps=r.avg_rps or 0.0,
            avg_response_time_ms=r.avg_response_time_ms or 0.0,
            p95_response_time_ms=r.p95_response_time_ms or 0.0,
            failure_rate_pct=r.failure_rate_pct or 0.0
        ))
    return result
