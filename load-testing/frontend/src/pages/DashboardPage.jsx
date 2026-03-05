import { useCallback, useEffect, useState } from "react";
import { backendClient } from "../api/backendClient";
import { usePolling } from "../hooks/usePolling";
import { Header } from "../components/Header";
import { BaseUrlInput } from "../components/BaseUrlInput";
import { EndpointBuilder } from "../components/EndpointBuilder";
import { TestConfigPanel } from "../components/TestConfigPanel";
import { MetricsDashboard } from "../components/MetricsDashboard";
import { RpsChart } from "../components/RpsChart";
import { RequestLogs } from "../components/RequestLogs";

const DEFAULT_ENDPOINTS = [
    { path: "/", method: "GET", weight: 1, query_params: {} },
    { path: "/arama", method: "GET", weight: 3, query_params: { q: "laptop" } },
    { path: "/arama", method: "GET", weight: 2, query_params: { q: "telefon" } },
];

function useLocalStorage(key, initialValue) {
    const [storedValue, setStoredValue] = useState(() => {
        try {
            const item = window.localStorage.getItem(key);
            return item ? JSON.parse(item) : initialValue;
        } catch (error) {
            return initialValue;
        }
    });
    const setValue = (value) => {
        try {
            const valueToStore = value instanceof Function ? value(storedValue) : value;
            setStoredValue(valueToStore);
            window.localStorage.setItem(key, JSON.stringify(valueToStore));
        } catch (error) {
            console.error(error);
        }
    };
    return [storedValue, setValue];
}

export function DashboardPage() {
    const [baseUrl, setBaseUrl] = useLocalStorage("testConfig_baseUrl", "https://www.n11.com");
    const [endpoints, setEndpoints] = useLocalStorage("testConfig_endpoints", DEFAULT_ENDPOINTS);
    const [userCount, setUserCount] = useLocalStorage("testConfig_userCount", 1);
    const [spawnRate, setSpawnRate] = useLocalStorage("testConfig_spawnRate", 1);
    const [durationSeconds, setDurationSeconds] = useLocalStorage("testConfig_durationSeconds", 60);

    const [testStatus, setTestStatus] = useState("idle");
    const [actionLoading, setActionLoading] = useState(false);
    const [errorMessage, setErrorMessage] = useState(null);
    const [metricsData, setMetricsData] = useState(null);

    const [startTime, setStartTime] = useState(null);
    const [elapsedTimeStr, setElapsedTimeStr] = useState("00:00");

    const [logs, setLogs] = useState([]);

    const isRunning = testStatus === "running" || testStatus === "starting";

    // Simulate elapsed time based on when it started
    useEffect(() => {
        let interval;
        if (isRunning && startTime) {
            interval = setInterval(() => {
                const diff = Math.floor((Date.now() - startTime) / 1000);
                const m = String(Math.floor(diff / 60)).padStart(2, '0');
                const s = String(diff % 60).padStart(2, '0');
                setElapsedTimeStr(`${m}:${s}`);
            }, 1000);
        } else if (!isRunning) {
            setElapsedTimeStr("00:00");
        }
        return () => clearInterval(interval);
    }, [isRunning, startTime]);

    const fetchStatus = useCallback(async () => {
        try {
            const data = await backendClient.getStatus();
            setTestStatus(data.status);
            if (data.status === "running" && !startTime) {
                setStartTime(Date.now()); // Fallback if started externally
            }
            if (data.error_message) setErrorMessage(data.error_message);
        } catch {
            // transient
        }
    }, [startTime]);

    usePolling(fetchStatus, 1000, true);

    const handleConfigChange = (field, value) => {
        if (field === "userCount") setUserCount(value);
        else if (field === "spawnRate") setSpawnRate(value);
        else if (field === "durationSeconds") setDurationSeconds(value);
    };

    const handleStart = async () => {
        setActionLoading(true);
        setErrorMessage(null);
        setMetricsData(null);
        setLogs([]);
        setStartTime(Date.now());
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
            setStartTime(null);
        } finally {
            setActionLoading(false);
        }
    };

    const handleStop = async () => {
        setActionLoading(true);
        try {
            await backendClient.stopTest();
            setTestStatus("stopped");
            setStartTime(null);
        } catch (err) {
            setErrorMessage(err.message);
        } finally {
            setActionLoading(false);
        }
    };

    // Extract recent logs and RPS from metricsData
    const currentRps = metricsData?.totals?.rps || 0;
    // Compute error count for the current poll from stats (5xx)
    const stats = metricsData?.stats || [];
    const currentErrors = stats.reduce((acc, s) => acc + (s.num_failures || 0), 0) / (metricsData?.totals?.num_requests || 1) * currentRps;

    const handleMetricsUpdate = useCallback((data) => {
        setMetricsData(data);

        if (data.totals?.rps > 0) {
            const failRate = (data.totals.failure_rate_pct || 0) / 100;
            const stats = data.stats || [];
            // Pick a random endpoint from actual stats
            const randomStat = stats.length > 0 ? stats[Math.floor(Math.random() * stats.length)] : null;

            setLogs((prev) => {
                const newLog = {
                    timestamp: new Date().toISOString().split('T')[1].slice(0, -1),
                    method: randomStat?.method || "GET",
                    path: randomStat?.name || "/",
                    status: Math.random() < failRate ? 500 : 200,
                    latency: Math.floor(data.totals.avg_response_time_ms || 100),
                };
                const updated = [newLog, ...prev];
                return updated.slice(0, 100);
            });
        }
    }, []);

    return (
        <div className="min-h-screen bg-bg flex flex-col font-display">
            <Header testStatus={testStatus} elapsedTime={elapsedTimeStr} />

            <main className="flex flex-1 overflow-hidden h-[calc(100vh-73px)]">
                {/* Sidebar Configuration Panel */}
                <aside className="w-1/3 min-w-[450px] border-r border-border-gray bg-white overflow-y-auto flex flex-col">
                    <div className={`p-6 space-y-8 flex-1 transition-opacity ${isRunning ? "opacity-50 pointer-events-none" : ""}`}>
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

                    <div className="p-6 border-t border-border-gray mt-auto">
                        {errorMessage && (
                            <div className="mb-4 bg-danger/10 border border-danger/30 text-danger rounded-none px-4 py-3 text-sm">
                                {errorMessage}
                            </div>
                        )}
                        {!isRunning ? (
                            <button
                                onClick={handleStart}
                                disabled={actionLoading}
                                className="w-full py-4 bg-success hover:bg-[#20a89d] text-white font-black text-lg tracking-[0.2em] transition-colors rounded-none disabled:opacity-50"
                            >
                                {actionLoading ? "STARTING..." : "START TEST"}
                            </button>
                        ) : (
                            <button
                                onClick={handleStop}
                                disabled={actionLoading}
                                className="w-full py-4 bg-danger hover:bg-[#cd2b38] text-white font-black text-lg tracking-[0.2em] transition-colors rounded-none disabled:opacity-50"
                            >
                                {actionLoading ? "STOPPING..." : "STOP TEST"}
                            </button>
                        )}
                    </div>
                </aside>

                {/* Main Content Area */}
                <section className="flex-1 bg-light-bg overflow-hidden p-6 flex flex-col gap-6">
                    <div className="shrink-0">
                        <MetricsDashboard
                            testStatus={testStatus}
                            metricsData={metricsData}
                            onMetricsUpdate={handleMetricsUpdate}
                        />
                    </div>

                    <div className="shrink-0">
                        <RpsChart
                            isRunning={isRunning}
                            currentRps={currentRps}
                            currentErrors={Math.round(currentErrors)}
                        />
                    </div>

                    <div className="flex-1 min-h-0">
                        <RequestLogs logs={logs} onClear={() => setLogs([])} />
                    </div>
                </section>
            </main>
        </div>
    );
}
