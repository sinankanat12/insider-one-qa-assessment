import { useCallback, useState } from "react";
import { backendClient } from "./api/backendClient";
import { BaseUrlInput } from "./components/BaseUrlInput";
import { ControlBar } from "./components/ControlBar";
import { EndpointBuilder } from "./components/EndpointBuilder";
import { MetricsDashboard } from "./components/MetricsDashboard";
import { TestConfigPanel } from "./components/TestConfigPanel";
import { usePolling } from "./hooks/usePolling";

const DEFAULT_ENDPOINTS = [
  { path: "/arama", method: "GET", weight: 3, query_params: { q: "laptop" } },
  { path: "/", method: "GET", weight: 1, query_params: {} },
];

export default function App() {
  // --- Form state ---
  const [baseUrl, setBaseUrl] = useState("https://www.n11.com");
  const [endpoints, setEndpoints] = useState(DEFAULT_ENDPOINTS);
  const [userCount, setUserCount] = useState(1);
  const [spawnRate, setSpawnRate] = useState(1);
  const [durationSeconds, setDurationSeconds] = useState(60);

  // --- Lifecycle state ---
  const [testStatus, setTestStatus] = useState("idle");
  const [pid, setPid] = useState(null);
  const [actionLoading, setActionLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState(null);

  // --- Metrics state ---
  const [metricsData, setMetricsData] = useState(null);

  // --- Status polling (1s, always active) ---
  const fetchStatus = useCallback(async () => {
    try {
      const data = await backendClient.getStatus();
      setTestStatus(data.status);
      setPid(data.pid ?? null);
      if (data.error_message) setErrorMessage(data.error_message);
    } catch {
      // transient — ignore
    }
  }, []);

  const statusInterval = parseInt(import.meta.env.VITE_STATUS_POLL_INTERVAL_MS) || 1000;
  usePolling(fetchStatus, statusInterval, true);

  // --- Handlers ---
  const handleConfigChange = (field, value) => {
    if (field === "userCount") setUserCount(value);
    else if (field === "spawnRate") setSpawnRate(value);
    else if (field === "durationSeconds") setDurationSeconds(value);
  };

  const handleStart = async () => {
    setActionLoading(true);
    setErrorMessage(null);
    setMetricsData(null);
    try {
      await backendClient.startTest({
        base_url: baseUrl,
        endpoints,
        user_count: userCount,
        spawn_rate: spawnRate,
        duration_seconds: durationSeconds,
      });
      setTestStatus("running");
    } catch (err) {
      setErrorMessage(err.message);
    } finally {
      setActionLoading(false);
    }
  };

  const handleStop = async () => {
    setActionLoading(true);
    try {
      await backendClient.stopTest();
      setTestStatus("stopped");
    } catch (err) {
      setErrorMessage(err.message);
    } finally {
      setActionLoading(false);
    }
  };

  const isRunning = testStatus === "running" || testStatus === "starting";

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white border-b border-gray-200 px-6 py-4">
        <div className="max-w-5xl mx-auto flex items-center justify-between">
          <h1 className="text-lg font-semibold text-gray-900">Load Test Dashboard</h1>
          <ControlBar
            testStatus={testStatus}
            onStart={handleStart}
            onStop={handleStop}
            loading={actionLoading}
          />
        </div>
      </header>

      <main className="max-w-5xl mx-auto px-6 py-8 space-y-8">
        {errorMessage && (
          <div className="bg-red-50 border border-red-300 text-red-700 rounded-md px-4 py-3 text-sm">
            {errorMessage}
          </div>
        )}

        <div className="bg-white rounded-lg border border-gray-200 p-6 space-y-6">
          <BaseUrlInput value={baseUrl} onChange={setBaseUrl} disabled={isRunning} />
          <EndpointBuilder endpoints={endpoints} onChange={setEndpoints} disabled={isRunning} />
          <TestConfigPanel
            userCount={userCount}
            spawnRate={spawnRate}
            durationSeconds={durationSeconds}
            onChange={handleConfigChange}
            disabled={isRunning}
          />
        </div>

        <div className="bg-white rounded-lg border border-gray-200 p-6">
          <h2 className="text-sm font-semibold text-gray-700 mb-4">Live Metrics</h2>
          <MetricsDashboard
            testStatus={testStatus}
            metricsData={metricsData}
            onMetricsUpdate={setMetricsData}
          />
        </div>
      </main>
    </div>
  );
}
