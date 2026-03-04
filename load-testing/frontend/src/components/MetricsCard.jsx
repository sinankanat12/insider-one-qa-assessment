export function MetricsCard({ stat }) {
  const failureColor =
    stat.failure_rate_pct > 5
      ? "text-red-600"
      : stat.failure_rate_pct > 1
      ? "text-yellow-600"
      : "text-green-600";

  const p95Color =
    stat.p95_response_time_ms > 1000
      ? "text-red-600"
      : stat.p95_response_time_ms > 500
      ? "text-yellow-600"
      : "text-green-600";

  return (
    <div className="bg-white border border-gray-200 rounded-lg p-4 shadow-sm">
      <div className="flex items-center justify-between mb-3">
        <span className="text-xs font-mono text-gray-500 bg-gray-100 px-2 py-0.5 rounded">
          {stat.method}
        </span>
        <span className="text-sm font-medium text-gray-800 truncate ml-2 flex-1 text-right">
          {stat.name}
        </span>
      </div>

      <div className="grid grid-cols-2 gap-x-4 gap-y-2 text-sm">
        <Metric label="RPS" value={stat.rps?.toFixed(1)} />
        <Metric label="Avg (ms)" value={stat.avg_response_time_ms?.toFixed(0)} />
        <Metric label="p95 (ms)" value={stat.p95_response_time_ms?.toFixed(0)} colorClass={p95Color} />
        <Metric label="p99 (ms)" value={stat.p99_response_time_ms?.toFixed(0)} />
        <Metric label="Requests" value={stat.num_requests} />
        <Metric label="Failures" value={`${stat.failure_rate_pct?.toFixed(2)}%`} colorClass={failureColor} />
      </div>
    </div>
  );
}

function Metric({ label, value, colorClass = "text-gray-800" }) {
  return (
    <div>
      <div className="text-xs text-gray-400">{label}</div>
      <div className={`font-semibold ${colorClass}`}>{value ?? "—"}</div>
    </div>
  );
}
