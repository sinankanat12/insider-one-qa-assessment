from typing import Any
from pydantic import BaseModel, Field


class EndpointConfig(BaseModel):
    path: str = Field(..., examples=["/arama"])
    method: str = Field(default="GET", examples=["GET"])
    weight: int = Field(default=1, ge=1)
    query_params: dict[str, Any] = Field(default_factory=dict)


class StartTestRequest(BaseModel):
    base_url: str = Field(..., examples=["https://www.n11.com"])
    endpoints: list[EndpointConfig] = Field(..., min_length=1)
    user_count: int = Field(default=1, ge=1)
    spawn_rate: int = Field(default=1, ge=1)
    duration_seconds: int = Field(default=60, ge=5)


class StartTestResponse(BaseModel):
    status: str
    pid: int | None = None
    message: str


class StopTestResponse(BaseModel):
    status: str
    message: str


class StatusResponse(BaseModel):
    status: str
    pid: int | None = None
    uptime_seconds: float | None = None
    error_message: str | None = None


class EndpointStats(BaseModel):
    name: str
    method: str
    num_requests: int
    num_failures: int
    avg_response_time_ms: float
    min_response_time_ms: float
    max_response_time_ms: float
    p50_response_time_ms: float
    p95_response_time_ms: float
    p99_response_time_ms: float
    rps: float
    failure_rate_pct: float


class Totals(BaseModel):
    num_requests: int
    num_failures: int
    rps: float
    avg_response_time_ms: float
    p95_response_time_ms: float
    failure_rate_pct: float


class ErrorEntry(BaseModel):
    name: str
    error: str
    occurrences: int


class MetricsResponse(BaseModel):
    timestamp: str
    status: str
    stats: list[EndpointStats] = Field(default_factory=list)
    totals: Totals | None = None
    errors: list[ErrorEntry] = Field(default_factory=list)


class HealthResponse(BaseModel):
    ok: bool = True


class TestHistoryResponse(BaseModel):
    id: int
    created_at: str
    base_url: str
    endpoints: int
    duration_seconds: int
    num_requests: int
    num_failures: int
    avg_rps: float
    avg_response_time_ms: float
    p95_response_time_ms: float
    failure_rate_pct: float

    class Config:
        from_attributes = True
