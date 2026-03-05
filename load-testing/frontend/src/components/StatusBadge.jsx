const STATUS_STYLES = {
  idle: "bg-gray-100 text-gray-600",
  starting: "bg-yellow-100 text-yellow-700 animate-pulse",
  running: "bg-green-100 text-green-700",
  stopped: "bg-blue-100 text-blue-700",
  error: "bg-red-100 text-red-700",
};

export function StatusBadge({ status, elapsedTime }) {
  let bgClass = "bg-gray-100 text-gray-800";
  let pulse = false;

  if (status === "running") {
    bgClass = "bg-success text-white";
    pulse = true;
  } else if (status === "starting") {
    bgClass = "bg-warning text-white";
    pulse = true;
  } else if (status === "stopped") {
    bgClass = "bg-gray-200 text-gray-700";
  }

  return (
    <div className={`inline-flex items-center gap-2 px-4 py-1.5 rounded text-[11px] font-bold tracking-widest uppercase ${bgClass}`}>
      {pulse && <span className="relative flex h-2 w-2">
        <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-white opacity-75"></span>
        <span className="relative inline-flex rounded-full h-2 w-2 bg-white"></span>
      </span>}
      <span>{status || "idle"}</span>
      {status === "running" && elapsedTime && (
        <span className="ml-1 opacity-90 font-mono tracking-normal">{elapsedTime} ELAPSED</span>
      )}
    </div>
  );
}
