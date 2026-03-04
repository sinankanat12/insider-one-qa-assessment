const STATUS_STYLES = {
  idle: "bg-gray-100 text-gray-600",
  starting: "bg-yellow-100 text-yellow-700 animate-pulse",
  running: "bg-green-100 text-green-700",
  stopped: "bg-blue-100 text-blue-700",
  error: "bg-red-100 text-red-700",
};

export function StatusBadge({ status }) {
  const style = STATUS_STYLES[status] || STATUS_STYLES.idle;
  return (
    <span className={`inline-flex items-center px-3 py-1 rounded-full text-sm font-medium ${style}`}>
      {status.charAt(0).toUpperCase() + status.slice(1)}
    </span>
  );
}
