import { Link, useLocation } from "react-router-dom";
import { StatusBadge } from "./StatusBadge";

export function Header({ testStatus, elapsedTime }) {
    const location = useLocation();
    const isDashboard = location.pathname === "/";

    return (
        <header className="bg-panel-light border-b border-border-gray px-6 py-4 flex items-center justify-between">
            <div className="flex items-center gap-2">
                <span className="material-symbols-outlined text-primary text-3xl">speed</span>
                <h1 className="text-xl font-black uppercase tracking-tight text-text-charcoal m-0">
                    LOAD TEST MANAGER
                </h1>
            </div>

            {isDashboard && (
                <div className="flex-1 flex justify-center">
                    <StatusBadge status={testStatus} elapsedTime={elapsedTime} />
                </div>
            )}

            <div>
                {isDashboard ? (
                    <Link
                        to="/history"
                        className="text-sm font-bold text-text-charcoal hover:text-primary transition-colors flex items-center gap-1 uppercase tracking-widest"
                    >
                        <span className="material-symbols-outlined text-[18px]">history</span>
                        History
                    </Link>
                ) : (
                    <Link
                        to="/"
                        className="text-sm font-bold text-text-charcoal hover:text-primary transition-colors flex items-center gap-1 uppercase tracking-widest"
                    >
                        <span className="material-symbols-outlined text-[18px]">arrow_back</span>
                        Back to Dashboard
                    </Link>
                )}
            </div>
        </header>
    );
}
