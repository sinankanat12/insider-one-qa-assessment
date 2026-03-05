import { EndpointRow } from "./EndpointRow";

const DEFAULT_ENDPOINT = { path: "/arama", method: "GET", weight: 1, query_params: { q: "laptop" } };

export function EndpointBuilder({ endpoints, onChange, disabled }) {
  const addEndpoint = () =>
    onChange([...endpoints, { path: "/", method: "GET", weight: 1, active: true }]);

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
        <label className="block text-[10px] uppercase font-bold tracking-widest text-text-charcoal">ENDPOINTS</label>
        <button
          onClick={addEndpoint}
          disabled={disabled}
          className="flex items-center gap-1 text-[10px] font-bold uppercase tracking-widest text-primary hover:text-[#324ab2] transition-colors disabled:opacity-30"
        >
          <span className="material-symbols-outlined text-[14px]">add</span>
          Add Endpoint
        </button>
      </div>

      {/* Header row */}
      <div className="grid grid-cols-12 gap-2 mb-1 px-2">
        <div className="col-span-1 flex justify-center text-[10px] text-gray-500 font-bold uppercase tracking-widest">On</div>
        <div className="col-span-4 text-[10px] text-gray-500 font-bold uppercase tracking-widest">Path</div>
        <div className="col-span-2 text-[10px] text-gray-500 font-bold uppercase tracking-widest">Method</div>
        <div className="col-span-2 text-[10px] text-gray-500 font-bold uppercase tracking-widest">Weight</div>
        <div className="col-span-3"></div>
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
