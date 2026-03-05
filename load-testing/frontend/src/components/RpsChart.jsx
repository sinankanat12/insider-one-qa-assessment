import { useEffect, useState } from "react";

const MAX_POINTS = 60; // 60 seconds of data

export function RpsChart({ isRunning, currentRps, currentErrors }) {
    const [dataPoints, setDataPoints] = useState([]);

    useEffect(() => {
        if (!isRunning) return;
        setDataPoints((prev) => {
            const now = new Date();
            const newPoint = { time: now, rps: currentRps || 0, errors: currentErrors || 0 };
            const updated = [...prev, newPoint];
            if (updated.length > MAX_POINTS) return updated.slice(updated.length - MAX_POINTS);
            return updated;
        });
    }, [isRunning, currentRps, currentErrors]); // this won't perfectly poll unless currentRps changes or we trigger it another way. Actually, MetricsDashboard polls and updates state. DashboardPage will pass currentRps.

    // To draw SVG
    if (dataPoints.length === 0) {
        return (
            <div className="h-64 flex items-center justify-center text-sm text-gray-400 border border-border-gray rounded bg-panel-light">
                No chart data yet...
            </div>
        );
    }

    const width = 800;
    const height = 250;
    const padding = 20;

    const maxRps = Math.max(10, ...dataPoints.map(d => d.rps + d.errors));

    const getX = (index) => padding + (index / (MAX_POINTS - 1)) * (width - 2 * padding);
    const getY = (value) => height - padding - (value / maxRps) * (height - 2 * padding);

    const rpsPath = dataPoints.map((d, i) => `${i === 0 ? 'M' : 'L'} ${getX(i)} ${getY(d.rps)}`).join(" ");
    const errPath = dataPoints.map((d, i) => `${i === 0 ? 'M' : 'L'} ${getX(i)} ${getY(d.errors)}`).join(" ");

    const bgPathRps = `${rpsPath} L ${getX(dataPoints.length - 1)} ${height - padding} L ${getX(0)} ${height - padding} Z`;

    return (
        <div className="bg-panel-light border border-border-gray p-4 flex flex-col">
            <div className="flex justify-between items-center mb-4">
                <h3 className="text-sm font-semibold uppercase tracking-widest text-text-charcoal bg-white">Requests Per Second (Real-Time)</h3>
                <div className="flex items-center gap-4 text-xs font-mono">
                    <div className="flex items-center gap-2">
                        <span className="w-3 h-3 rounded-full bg-primary inline-block"></span>
                        200 OK
                    </div>
                    <div className="flex items-center gap-2">
                        <span className="w-3 h-3 rounded-full bg-danger inline-block"></span>
                        5XX ERROR
                    </div>
                </div>
            </div>
            <div className="relative w-full overflow-hidden" style={{ height: "250px" }}>
                <svg viewBox={`0 0 ${width} ${height}`} preserveAspectRatio="none" className="w-full h-full">
                    <defs>
                        <linearGradient id="rpsGradient" x1="0" x2="0" y1="0" y2="1">
                            <stop offset="0%" stopColor="#4361ee" stopOpacity="0.2" />
                            <stop offset="100%" stopColor="#4361ee" stopOpacity="0" />
                        </linearGradient>
                    </defs>

                    {/* Grid lines */}
                    <line x1={padding} y1={padding} x2={width - padding} y2={padding} stroke="#e2e8f0" strokeDasharray="4 4" />
                    <line x1={padding} y1={height / 2} x2={width - padding} y2={height / 2} stroke="#e2e8f0" strokeDasharray="4 4" />
                    <line x1={padding} y1={height - padding} x2={width - padding} y2={height - padding} stroke="#e2e8f0" strokeDasharray="4 4" />

                    {/* Area under RPS */}
                    {dataPoints.length > 1 && <path d={bgPathRps} fill="url(#rpsGradient)" />}

                    {/* Lines */}
                    {dataPoints.length > 1 && (
                        <>
                            <path d={rpsPath} fill="none" stroke="#4361ee" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
                            <path d={errPath} fill="none" stroke="#e63946" strokeWidth="2" strokeDasharray="4 4" strokeLinecap="round" strokeLinejoin="round" />
                        </>
                    )}
                </svg>
            </div>
        </div>
    );
}
