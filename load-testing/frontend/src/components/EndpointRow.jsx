export function EndpointRow({ endpoint, index, onChange, onRemove, disabled }) {
  const update = (field, value) => onChange(index, { ...endpoint, [field]: value });

  return (
    <div className="grid grid-cols-12 gap-2 items-center bg-light-bg border border-border-gray p-2 transition-colors hover:border-gray-300">
      <div className="col-span-1 flex justify-center">
        <input
          type="checkbox"
          checked={endpoint.active !== false}
          onChange={(e) => update("active", e.target.checked)}
          disabled={disabled}
          className="w-4 h-4 text-primary bg-white border-border-gray focus:ring-primary focus:ring-1 cursor-pointer disabled:opacity-50"
        />
      </div>
      <div className="col-span-5">
        <input
          type="text"
          value={endpoint.path}
          onChange={(e) => update("path", e.target.value)}
          disabled={disabled}
          placeholder="/arama"
          className="w-full border border-border-gray px-2 py-1.5 text-sm focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary disabled:bg-gray-50 focus:ring-opacity-50"
        />
      </div>
      <div className="col-span-3">
        <select
          value={endpoint.method}
          onChange={(e) => update("method", e.target.value)}
          disabled={disabled}
          className="w-full border border-border-gray px-2 py-1.5 text-sm text-primary font-bold focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary disabled:bg-gray-50 bg-white"
        >
          {["GET", "POST", "PUT", "DELETE"].map((m) => (
            <option key={m}>{m}</option>
          ))}
        </select>
      </div>
      <div className="col-span-2">
        <input
          type="number"
          value={endpoint.weight}
          onChange={(e) => update("weight", parseInt(e.target.value) || 1)}
          disabled={disabled}
          min={1}
          placeholder="Wt"
          className="w-full border border-border-gray px-2 py-1.5 text-sm focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary disabled:bg-gray-50"
        />
      </div>
      <div className="col-span-1 flex justify-center">
        <button
          onClick={() => onRemove(index)}
          disabled={disabled}
          className="text-gray-400 hover:text-danger hover:bg-red-50 p-1 transition-colors disabled:opacity-30 inline-flex"
          title="Remove"
        >
          <span className="material-symbols-outlined text-[18px]">delete</span>
        </button>
      </div>
    </div>
  );
}
