import { StatusBadge } from "./StatusBadge";

export function ControlBar({ testStatus, onStart, onStop, loading }) {
  const isRunning = testStatus === "running" || testStatus === "starting";

  return (
    <div className="flex items-center gap-4">
      <StatusBadge status={testStatus} />

      {!isRunning ? (
        <button
          onClick={onStart}
          disabled={loading}
          className="px-5 py-2 bg-indigo-600 text-white text-sm font-medium rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 disabled:opacity-50 transition-colors"
        >
          {loading ? "Starting…" : "Start Test"}
        </button>
      ) : (
        <button
          onClick={onStop}
          disabled={loading}
          className="px-5 py-2 bg-red-600 text-white text-sm font-medium rounded-md hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500 disabled:opacity-50 transition-colors"
        >
          {loading ? "Stopping…" : "Stop Test"}
        </button>
      )}
    </div>
  );
}
