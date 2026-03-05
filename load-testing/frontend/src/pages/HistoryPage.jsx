import { Header } from "../components/Header";
import { RequestLogs } from "../components/RequestLogs";

export function HistoryPage() {
    const mockHistory = [
        { id: 1, date: "2026-03-05 14:30", url: "https://www.n11.com", endpoints: 2, duration: 60, reqs: 14500, rps: 241.6, fail: 0.2 },
        { id: 2, date: "2026-03-05 12:15", url: "https://www.n11.com", endpoints: 5, duration: 300, reqs: 82000, rps: 273.3, fail: 1.5 },
        { id: 3, date: "2026-03-04 09:00", url: "https://api.n11.com", endpoints: 1, duration: 120, reqs: 12000, rps: 100.0, fail: 0.0 },
    ];

    const mockLogs = [
        { timestamp: "14:30:05", method: "GET", path: "/arama", status: 200, latency: 45 },
        { timestamp: "14:30:06", method: "POST", path: "/checkout", status: 500, latency: 1250 },
    ];

    return (
        <div className="min-h-screen bg-light-bg flex flex-col font-display">
            <Header />

            <main className="max-w-7xl mx-auto w-full px-6 py-8 space-y-8">

                {/* History Table */}
                <div className="bg-panel-light border border-border-gray p-0">
                    <h2 className="text-sm font-semibold uppercase tracking-widest text-text-charcoal p-4 border-b border-border-gray">Test History</h2>
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
                                <th className="py-3 px-4 font-normal">Fail %</th>
                                <th className="py-3 px-4 font-normal text-right">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            {mockHistory.map((h, i) => (
                                <tr key={h.id} className={`border-b border-border-gray hover:bg-light-bg transition-colors ${i === 0 ? 'bg-primary/5 border-l-4 border-l-primary' : 'border-l-4 border-l-transparent'}`}>
                                    <td className="py-3 px-4">{h.id}</td>
                                    <td className="py-3 px-4 text-gray-500">{h.date}</td>
                                    <td className="py-3 px-4 font-mono">{h.url}</td>
                                    <td className="py-3 px-4">{h.endpoints}</td>
                                    <td className="py-3 px-4">{h.duration}s</td>
                                    <td className="py-3 px-4 font-mono">{h.reqs.toLocaleString()}</td>
                                    <td className="py-3 px-4 font-mono">{h.rps.toFixed(1)}</td>
                                    <td className={`py-3 px-4 font-mono ${h.fail > 0 ? 'text-danger' : 'text-success'}`}>{h.fail}%</td>
                                    <td className="py-3 px-4 text-right">
                                        <button className="text-[11px] font-bold tracking-widest text-primary hover:underline uppercase">View Details</button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>

                {/* Selected Report */}
                <div className="bg-panel-light border border-border-gray p-0 mt-8">
                    <div className="p-4 border-b border-border-gray flex justify-between items-center bg-gray-50">
                        <h3 className="text-sm font-bold uppercase tracking-widest text-text-charcoal">
                            REPORT: 2026-03-05 14:30 | https://www.n11.com | 60 SECONDS
                        </h3>
                        <div className="flex gap-2">
                            <button className="text-[11px] font-bold tracking-widest bg-white border border-border-gray text-text-charcoal px-3 py-1.5 hover:bg-gray-50 transition-colors">EXPORT JSON</button>
                            <button className="text-[11px] font-bold tracking-widest bg-white border border-border-gray text-text-charcoal px-3 py-1.5 hover:bg-gray-50 transition-colors">EXPORT CSV</button>
                        </div>
                    </div>

                    <div className="p-6 grid grid-cols-4 gap-4">
                        {/* Report cards */}
                        <div className="border border-border-gray p-4 border-l-4 border-l-success">
                            <div className="text-xs text-gray-500 mb-1 uppercase tracking-wider">Total Requests</div>
                            <div className="text-3xl font-mono font-bold text-text-charcoal">14,500 <span className="text-sm font-normal text-gray-400">REQ</span></div>
                        </div>
                        <div className="border border-border-gray p-4 border-l-4 border-l-primary">
                            <div className="text-xs text-gray-500 mb-1 uppercase tracking-wider">Requests / Sec</div>
                            <div className="text-3xl font-mono font-bold text-text-charcoal">241.6 <span className="text-sm font-normal text-gray-400">REQ/S</span></div>
                        </div>
                        <div className="border border-border-gray p-4 border-l-4 border-l-warning">
                            <div className="text-xs text-gray-500 mb-1 uppercase tracking-wider">Avg Response Time</div>
                            <div className="text-3xl font-mono font-bold text-text-charcoal">85 <span className="text-sm font-normal text-gray-400">MS</span></div>
                        </div>
                        <div className="border border-border-gray p-4 border-l-4 border-l-danger">
                            <div className="text-xs text-gray-500 mb-1 uppercase tracking-wider">Failure Rate</div>
                            <div className="text-3xl font-mono font-bold text-text-charcoal">0.2% <span className="text-sm font-normal text-gray-400">FAIL</span></div>
                        </div>
                    </div>

                    <div className="p-6 pt-0">
                        <RequestLogs logs={mockLogs} onClear={() => { }} />
                    </div>
                </div>

            </main>
        </div>
    );
}
