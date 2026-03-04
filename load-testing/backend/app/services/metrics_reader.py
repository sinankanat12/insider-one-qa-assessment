"""
Reads locust_stats.json from the shared volume and normalizes it
into the MetricsResponse schema.
"""
import json
import os
from datetime import datetime, timezone

from app.core.config import settings
from app.models.schemas import EndpointStats, ErrorEntry, MetricsResponse, Totals


STATS_PATH = os.path.join(settings.locust_results_dir, "locust_stats.json")


def _safe_float(val, default: float = 0.0) -> float:
    try:
        return float(val) if val is not None else default
    except (TypeError, ValueError):
        return default


def _failure_rate(requests: int, failures: int) -> float:
    if requests == 0:
        return 0.0
    return round((failures / requests) * 100, 2)


def read_metrics(current_status: str) -> MetricsResponse:
    timestamp = datetime.now(timezone.utc).isoformat()

    if not os.path.exists(STATS_PATH):
        return MetricsResponse(timestamp=timestamp, status=current_status)

    try:
        with open(STATS_PATH, "r") as f:
            raw = json.load(f)
    except (json.JSONDecodeError, OSError):
        return MetricsResponse(timestamp=timestamp, status=current_status)

    # Locust --json format: list of stat entries (last entry is "Aggregated")
    if not isinstance(raw, list):
        return MetricsResponse(timestamp=timestamp, status=current_status)

    stats: list[EndpointStats] = []
    totals: Totals | None = None
    errors: list[ErrorEntry] = []

    for entry in raw:
        name = entry.get("name", "")
        method = entry.get("method", "GET")
        num_requests = int(entry.get("num_requests", 0))
        num_failures = int(entry.get("num_failures", 0))
        avg_rt = _safe_float(entry.get("avg_response_time"))
        min_rt = _safe_float(entry.get("min_response_time"))
        max_rt = _safe_float(entry.get("max_response_time"))

        # Locust stores percentiles under response_times dict keyed by ms string
        rt_dict = entry.get("response_times", {})
        p50 = _safe_float(entry.get("median_response_time") or rt_dict.get("50"))
        p95 = _safe_float(entry.get("ninetieth_response_time") or rt_dict.get("95"))
        p99 = _safe_float(rt_dict.get("99") or rt_dict.get("99.0"))

        rps = _safe_float(entry.get("current_rps") or entry.get("requests_per_second"))

        if name == "Aggregated":
            totals = Totals(
                num_requests=num_requests,
                num_failures=num_failures,
                rps=rps,
                avg_response_time_ms=avg_rt,
                p95_response_time_ms=p95,
                failure_rate_pct=_failure_rate(num_requests, num_failures),
            )
        else:
            stats.append(
                EndpointStats(
                    name=name,
                    method=method,
                    num_requests=num_requests,
                    num_failures=num_failures,
                    avg_response_time_ms=avg_rt,
                    min_response_time_ms=min_rt,
                    max_response_time_ms=max_rt,
                    p50_response_time_ms=p50,
                    p95_response_time_ms=p95,
                    p99_response_time_ms=p99,
                    rps=rps,
                    failure_rate_pct=_failure_rate(num_requests, num_failures),
                )
            )

    # Extract errors from errors section if present
    for err_entry in entry.get("errors", {}).values() if isinstance(raw, list) and raw else []:
        errors.append(
            ErrorEntry(
                name=err_entry.get("name", ""),
                error=err_entry.get("error", ""),
                occurrences=int(err_entry.get("occurrences", 0)),
            )
        )

    return MetricsResponse(
        timestamp=timestamp,
        status=current_status,
        stats=stats,
        totals=totals,
        errors=errors,
    )
