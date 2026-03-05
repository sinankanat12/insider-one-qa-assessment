"""
Reads real-time metrics from Locust's web UI REST API.
"""
import httpx
from datetime import datetime, timezone

from app.core.config import settings
from app.models.schemas import EndpointStats, ErrorEntry, MetricsResponse, Totals

LOCUST_STATS_URL = f"http://127.0.0.1:{settings.locust_web_port}/stats/requests"


def _safe_float(val, default: float = 0.0) -> float:
    try:
        return float(val) if val is not None else default
    except (TypeError, ValueError):
        return default


def read_metrics(current_status: str) -> MetricsResponse:
    timestamp = datetime.now(timezone.utc).isoformat()

    if current_status not in ("running", "starting", "stopped"):
        return MetricsResponse(timestamp=timestamp, status=current_status)

    try:
        resp = httpx.get(LOCUST_STATS_URL, timeout=2.0)
        resp.raise_for_status()
        data = resp.json()
    except Exception:
        return MetricsResponse(timestamp=timestamp, status=current_status)

    raw_stats = data.get("stats", [])
    raw_errors = data.get("errors", [])

    stats: list[EndpointStats] = []
    totals: Totals | None = None

    for entry in raw_stats:
        name = entry.get("name", "")
        if name == "Aggregated":
            totals = Totals(
                num_requests=int(entry.get("num_requests", 0)),
                num_failures=int(entry.get("num_failures", 0)),
                rps=_safe_float(entry.get("current_rps")),
                avg_response_time_ms=_safe_float(entry.get("avg_response_time")),
                p95_response_time_ms=_safe_float(
                    entry.get("response_times", {}).get("0.95", entry.get("max_response_time", 0))
                ),
                failure_rate_pct=_safe_float(data.get("fail_ratio", 0)) * 100,
            )
            continue

        num_requests = int(entry.get("num_requests", 0))
        num_failures = int(entry.get("num_failures", 0))
        stats.append(EndpointStats(
            name=name,
            method=entry.get("method", "GET"),
            num_requests=num_requests,
            num_failures=num_failures,
            avg_response_time_ms=_safe_float(entry.get("avg_response_time")),
            min_response_time_ms=_safe_float(entry.get("min_response_time")),
            max_response_time_ms=_safe_float(entry.get("max_response_time")),
            p50_response_time_ms=_safe_float(entry.get("current_response_time_percentile", {}).get("0.5")),
            p95_response_time_ms=_safe_float(entry.get("current_response_time_percentile", {}).get("0.95")),
            p99_response_time_ms=_safe_float(entry.get("current_response_time_percentile", {}).get("0.99")),
            rps=_safe_float(entry.get("current_rps")),
            failure_rate_pct=(num_failures / num_requests * 100) if num_requests > 0 else 0.0,
        ))

    errors = [
        ErrorEntry(
            name=e.get("name", ""),
            error=e.get("error", ""),
            occurrences=int(e.get("occurrences", 0)),
        )
        for e in raw_errors
    ]

    return MetricsResponse(
        timestamp=timestamp,
        status=current_status,
        stats=stats,
        totals=totals,
        errors=errors,
    )
