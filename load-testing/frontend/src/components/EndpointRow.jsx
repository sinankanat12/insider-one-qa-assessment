export function EndpointRow({ endpoint, index, onChange, onRemove, disabled }) {
  const update = (field, value) => onChange(index, { ...endpoint, [field]: value });

  const handleQueryParams = (raw) => {
    // Parse simple key=value&key2=value2 format
    const parsed = {};
    raw.split("&").forEach((pair) => {
      const [k, v] = pair.split("=");
      if (k?.trim()) parsed[k.trim()] = v?.trim() ?? "";
    });
    update("query_params", parsed);
  };

  const queryParamsStr = Object.entries(endpoint.query_params || {})
    .map(([k, v]) => `${k}=${v}`)
    .join("&");

  return (
    <div className="grid grid-cols-12 gap-2 items-start">
      <div className="col-span-4">
        <input
          type="text"
          value={endpoint.path}
          onChange={(e) => update("path", e.target.value)}
          disabled={disabled}
          placeholder="/arama"
          className="w-full border border-gray-300 rounded px-2 py-1.5 text-sm focus:outline-none focus:ring-1 focus:ring-indigo-400 disabled:bg-gray-50"
        />
      </div>
      <div className="col-span-2">
        <select
          value={endpoint.method}
          onChange={(e) => update("method", e.target.value)}
          disabled={disabled}
          className="w-full border border-gray-300 rounded px-2 py-1.5 text-sm focus:outline-none focus:ring-1 focus:ring-indigo-400 disabled:bg-gray-50"
        >
          {["GET", "POST", "PUT", "DELETE"].map((m) => (
            <option key={m}>{m}</option>
          ))}
        </select>
      </div>
      <div className="col-span-1">
        <input
          type="number"
          value={endpoint.weight}
          onChange={(e) => update("weight", parseInt(e.target.value) || 1)}
          disabled={disabled}
          min={1}
          className="w-full border border-gray-300 rounded px-2 py-1.5 text-sm focus:outline-none focus:ring-1 focus:ring-indigo-400 disabled:bg-gray-50"
        />
      </div>
      <div className="col-span-4">
        <input
          type="text"
          value={queryParamsStr}
          onChange={(e) => handleQueryParams(e.target.value)}
          disabled={disabled}
          placeholder="q=laptop&category=3"
          className="w-full border border-gray-300 rounded px-2 py-1.5 text-sm focus:outline-none focus:ring-1 focus:ring-indigo-400 disabled:bg-gray-50"
        />
      </div>
      <div className="col-span-1 flex justify-center">
        <button
          onClick={() => onRemove(index)}
          disabled={disabled}
          className="text-red-400 hover:text-red-600 text-lg leading-none disabled:opacity-30"
          title="Remove"
        >
          ×
        </button>
      </div>
    </div>
  );
}
