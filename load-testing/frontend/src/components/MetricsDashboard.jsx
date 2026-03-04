import { useCallback } from "react";
import { backendClient } from "../api/backendClient";
import { usePolling } from "../hooks/usePolling";
import { MetricsCard } from "./MetricsCard";

export function MetricsDashboard({ testStatus, metricsData, onMetricsUpdate }) {
  const isRunning = testStatus === "running";

  const fetchMetrics = useCallback(async () => {
    try {
      const data = await backendClient.getMetrics();
      onMetricsUpdate(data);
    } catch {
      // Polling continues on error — no need to surface transient failures
    }
  }, [onMetricsUpdate]);

  const pollInterval = parseInt(import.meta.env.VITE_METRICS_POLL_INTERVAL_MS) || 3000;
  usePolling(fetchMetrics, pollInterval, isRunning);

  if (!metricsData) {
    return (
      <div className="text-center py-12 text-gray-400 text-sm">
        {isRunning ? "Collecting metrics…" : "Start a test to see metrics"}
      </div>
    );
  }

  const { stats = [], totals, errors = [] } = metricsData;

  return (
    <div className="space-y-6">
      {totals && (
        <div className="bg-indigo-50 border border-indigo-200 rounded-lg p-4">
          <p className="text-xs font-semibold text-indigo-700 uppercase tracking-wide mb-3">Totals</p>
          <div className="grid grid-cols-2 sm:grid-cols-5 gap-4 text-sm">
            <TotalMetric label="Total Requests" value={totals.num_requests} />
            <TotalMetric label="RPS" value={totals.rps?.toFixed(1)} />
            <TotalMetric label="Avg (ms)" value={totals.avg_response_time_ms?.toFixed(0)} />
            <TotalMetric label="p95 (ms)" value={totals.p95_response_time_ms?.toFixed(0)} />
            <TotalMetric
              label="Failure rate"
              value={`${totals.failure_rate_pct?.toFixed(2)}%`}
              colorClass={totals.failure_rate_pct > 1 ? "text-red-600" : "text-green-600"}
            />
          </div>
        </div>
      )}

      {stats.length > 0 && (
        <div>
          <p className="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-3">
            Per-endpoint
          </p>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {stats.map((s, i) => (
              <MetricsCard key={i} stat={s} />
            ))}
          </div>
        </div>
      )}

      {errors.length > 0 && (
        <div>
          <p className="text-xs font-semibold text-red-500 uppercase tracking-wide mb-2">Errors</p>
          <div className="space-y-1">
            {errors.map((e, i) => (
              <div key={i} className="text-xs bg-red-50 border border-red-200 rounded px-3 py-2">
                <span className="font-mono text-red-700">{e.name}</span>
                {" — "}
                {e.error}
                {" "}
                <span className="text-red-400">({e.occurrences}×)</span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

function TotalMetric({ label, value, colorClass = "text-gray-800" }) {
  return (
    <div>
      <div className="text-xs text-indigo-500">{label}</div>
      <div className={`font-bold text-base ${colorClass}`}>{value ?? "—"}</div>
    </div>
  );
}
