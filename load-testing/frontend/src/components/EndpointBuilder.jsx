import { EndpointRow } from "./EndpointRow";

const DEFAULT_ENDPOINT = { path: "/arama", method: "GET", weight: 1, query_params: { q: "laptop" } };

export function EndpointBuilder({ endpoints, onChange, disabled }) {
  const addEndpoint = () =>
    onChange([...endpoints, { path: "/", method: "GET", weight: 1, query_params: {} }]);

  const updateEndpoint = (index, updated) => {
    const next = [...endpoints];
    next[index] = updated;
    onChange(next);
  };

  const removeEndpoint = (index) => {
    if (endpoints.length <= 1) return;
    onChange(endpoints.filter((_, i) => i !== index));
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-2">
        <label className="text-sm font-medium text-gray-700">Endpoints</label>
        <button
          onClick={addEndpoint}
          disabled={disabled}
          className="text-xs text-indigo-600 hover:text-indigo-800 font-medium disabled:opacity-30"
        >
          + Add endpoint
        </button>
      </div>

      {/* Header row */}
      <div className="grid grid-cols-12 gap-2 mb-1 px-0">
        <div className="col-span-4 text-xs text-gray-500 font-medium">Path</div>
        <div className="col-span-2 text-xs text-gray-500 font-medium">Method</div>
        <div className="col-span-1 text-xs text-gray-500 font-medium">Wt.</div>
        <div className="col-span-4 text-xs text-gray-500 font-medium">Query params</div>
      </div>

      <div className="space-y-2">
        {endpoints.map((ep, i) => (
          <EndpointRow
            key={i}
            endpoint={ep}
            index={i}
            onChange={updateEndpoint}
            onRemove={removeEndpoint}
            disabled={disabled}
          />
        ))}
      </div>
    </div>
  );
}
