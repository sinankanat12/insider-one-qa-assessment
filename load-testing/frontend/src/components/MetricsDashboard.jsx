import { useCallback } from "react";
import { backendClient } from "../api/backendClient";
import { usePolling } from "../hooks/usePolling";

export function MetricsDashboard({ testStatus, metricsData, onMetricsUpdate }) {
  const isRunning = testStatus === "running" || testStatus === "starting";

  const fetchMetrics = useCallback(async () => {
    try {
      const data = await backendClient.getMetrics();
      onMetricsUpdate(data);
    } catch {
      // Polling continues on error
    }
  }, [onMetricsUpdate]);

  const pollInterval = parseInt(import.meta.env.VITE_METRICS_POLL_INTERVAL_MS) || 1000;
  usePolling(fetchMetrics, pollInterval, isRunning);

  if (!metricsData || !metricsData.totals) {
    return (
      <div className="grid grid-cols-4 gap-4">
        <MetricCard title="Total Requests" value="0" unit="REQ" colorClass="border-l-success" />
        <MetricCard title="Requests / Sec" value="0.0" unit="REQ/S" colorClass="border-l-primary" />
        <MetricCard title="Avg Response Time" value="0" unit="MS" colorClass="border-l-warning" />
        <MetricCard title="Failure Rate" value="0.0%" unit="FAIL" colorClass="border-l-danger" />
      </div>
    );
  }

  const { totals } = metricsData;

  const failureRate = totals.failure_rate_pct || 0;
  // Let's ensure rate format is bounded
  const failText = `${failureRate.toFixed(2)}%`;

  return (
    <div className="grid grid-cols-4 gap-4">
      <MetricCard title="Total Requests" value={totals.num_requests?.toLocaleString() || "0"} unit="REQ" colorClass="border-l-success" />
      <MetricCard title="Requests / Sec" value={totals.rps?.toFixed(1) || "0.0"} unit="REQ/S" colorClass="border-l-primary" />
      <MetricCard title="Avg Response Time" value={totals.avg_response_time_ms?.toFixed(0) || "0"} unit="MS" colorClass="border-l-warning" />
      <MetricCard title="Failure Rate" value={failText} unit="FAIL" colorClass="border-l-danger" />
    </div>
  );
}

function MetricCard({ title, value, unit, colorClass }) {
  return (
    <div className={`bg-panel-light border border-border-gray p-4 border-l-4 ${colorClass}`}>
      <div className="text-xs text-gray-500 mb-1 uppercase tracking-wider">{title}</div>
      <div className="text-3xl font-mono font-bold text-text-charcoal leading-none">
        {value} <span className="text-sm font-normal text-gray-400">{unit}</span>
      </div>
    </div>
  );
}
