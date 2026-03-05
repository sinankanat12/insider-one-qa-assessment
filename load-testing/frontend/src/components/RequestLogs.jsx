export function RequestLogs({ logs, onClear }) {
    return (
        <div className="bg-panel-light border border-border-gray flex flex-col" style={{ minHeight: "300px" }}>
            <div className="flex justify-between items-center p-4 border-b border-border-gray bg-white">
                <h3 className="text-sm font-semibold uppercase tracking-widest text-text-charcoal m-0">REQUEST LOGS</h3>
                <div className="flex gap-2">
                    <button className="text-[11px] font-bold tracking-widest text-text-charcoal bg-light-bg px-3 py-1.5 hover:bg-gray-200 transition-colors">
                        EXPORT JSON
                    </button>
                    <button
                        onClick={onClear}
                        className="text-[11px] font-bold tracking-widest text-danger bg-red-50 px-3 py-1.5 hover:bg-red-100 transition-colors"
                    >
                        CLEAR RESULTS
                    </button>
                </div>
            </div>
            <div className="overflow-auto custom-scrollbar flex-1">
                <table className="w-full text-left font-mono text-[11px]">
                    <thead className="bg-light-bg sticky top-0 text-gray-500 uppercase">
                        <tr>
                            <th className="py-2 px-4 font-normal">Timestamp</th>
                            <th className="py-2 px-4 font-normal">Method</th>
                            <th className="py-2 px-4 font-normal">Path</th>
                            <th className="py-2 px-4 font-normal">Status</th>
                            <th className="py-2 px-4 font-normal text-right">Latency</th>
                        </tr>
                    </thead>
                    <tbody>
                        {logs.length === 0 ? (
                            <tr>
                                <td colSpan={5} className="py-8 text-center text-gray-400">No requests logged yet</td>
                            </tr>
                        ) : (
                            logs.map((log, i) => (
                                <tr key={i} className="border-b border-border-gray hover:bg-light-bg transition-colors">
                                    <td className="py-2 px-4 text-gray-500">{log.timestamp}</td>
                                    <td className="py-2 px-4 text-primary font-bold">{log.method}</td>
                                    <td className="py-2 px-4 truncate max-w-[200px]" title={log.path}>{log.path}</td>
                                    <td className="py-2 px-4">
                                        <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${log.status >= 200 && log.status < 300 ? 'bg-success/10 text-success' : 'bg-danger/10 text-danger'
                                            }`}>
                                            {log.status || 'ERROR'}
                                        </span>
                                    </td>
                                    <td className="py-2 px-4 text-right">{log.latency} ms</td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
}
