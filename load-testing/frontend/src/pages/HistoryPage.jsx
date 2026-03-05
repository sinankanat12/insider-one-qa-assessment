import { useState, useEffect } from "react";
import { Header } from "../components/Header";
import { backendClient } from "../api/backendClient";

export function HistoryPage() {
    const [history, setHistory] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        backendClient.getHistory()
            .then(data => setHistory(data))
            .catch(err => console.error("Failed to fetch history", err))
            .finally(() => setLoading(false));
    }, []);

    const handleExportCsv = () => {
        if (history.length === 0) return;

        const headers = ["ID", "Date & Time", "Base URL", "Endpoints", "Duration (s)", "Requests", "Failures", "Avg RPS", "Avg Response Time (ms)", "P95 Response Time (ms)", "Failure Rate (%)"];
        const rows = history.map(h => [
            h.id,
            h.created_at,
            h.base_url,
            h.endpoints,
            h.duration_seconds,
            h.num_requests,
            h.num_failures,
            h.avg_rps.toFixed(2),
            h.avg_response_time_ms.toFixed(2),
            h.p95_response_time_ms.toFixed(2),
            (h.failure_rate_pct || 0).toFixed(2),
        ]);

        const csvContent = [
            headers.join(","),
            ...rows.map(r => r.map(v => `"${v}"`).join(",")),
        ].join("\n");

        const blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });
        const url = URL.createObjectURL(blob);
        const link = document.createElement("a");
        link.href = url;
        link.download = `test-history-${new Date().toISOString().slice(0, 10)}.csv`;
        link.click();
        URL.revokeObjectURL(url);
    };

    return (
        <div className="min-h-screen bg-light-bg flex flex-col font-display">
            <Header />

            <main className="max-w-7xl mx-auto w-full px-6 py-8">
                <div className="bg-panel-light border border-border-gray p-0">
                    <div className="flex justify-between items-center p-4 border-b border-border-gray">
                        <h2 className="text-sm font-semibold uppercase tracking-widest text-text-charcoal">Test History</h2>
                        <button
                            onClick={handleExportCsv}
                            disabled={history.length === 0}
                            className="flex items-center gap-1.5 text-[11px] font-bold tracking-widest text-text-charcoal bg-white border border-border-gray px-3 py-1.5 hover:bg-gray-50 transition-colors disabled:opacity-30 disabled:cursor-not-allowed"
                        >
                            <span className="material-symbols-outlined text-[14px]">download</span>
                            EXPORT CSV
                        </button>
                    </div>
                    <table className="w-full text-left text-[13px]">
                        <thead className="bg-gray-50 text-gray-500 uppercase">
                            <tr>
                                <th className="py-3 px-4 font-normal">#</th>
                                <th className="py-3 px-4 font-normal">Date & Time</th>
                                <th className="py-3 px-4 font-normal">Base URL</th>
                                <th className="py-3 px-4 font-normal">Endpoints</th>
                                <th className="py-3 px-4 font-normal">Duration</th>
                                <th className="py-3 px-4 font-normal">Requests</th>
                                <th className="py-3 px-4 font-normal">Avg RPS</th>
                                <th className="py-3 px-4 font-normal">Avg RT (ms)</th>
                                <th className="py-3 px-4 font-normal">Fail %</th>
                            </tr>
                        </thead>
                        <tbody>
                            {loading ? (
                                <tr><td colSpan="9" className="py-6 text-center text-gray-400">Loading history...</td></tr>
                            ) : history.length === 0 ? (
                                <tr><td colSpan="9" className="py-6 text-center text-gray-400">No test history found.</td></tr>
                            ) : history.map((h) => (
                                <tr key={h.id} className="border-b border-border-gray hover:bg-light-bg transition-colors">
                                    <td className="py-3 px-4">{h.id}</td>
                                    <td className="py-3 px-4 text-gray-500">{h.created_at}</td>
                                    <td className="py-3 px-4 font-mono">{h.base_url}</td>
                                    <td className="py-3 px-4">{h.endpoints}</td>
                                    <td className="py-3 px-4">{h.duration_seconds}s</td>
                                    <td className="py-3 px-4 font-mono">{h.num_requests.toLocaleString()}</td>
                                    <td className="py-3 px-4 font-mono">{h.avg_rps.toFixed(1)}</td>
                                    <td className="py-3 px-4 font-mono">{Math.round(h.avg_response_time_ms)}</td>
                                    <td className={`py-3 px-4 font-mono ${(h.failure_rate_pct || 0) > 0 ? 'text-danger' : 'text-success'}`}>{(h.failure_rate_pct || 0).toFixed(1)}%</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </main>
        </div>
    );
}
