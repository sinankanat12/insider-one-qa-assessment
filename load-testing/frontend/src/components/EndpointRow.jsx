import { useState } from "react";

export function EndpointRow({ endpoint, index, onChange, onRemove, disabled }) {
  const update = (field, value) => onChange(index, { ...endpoint, [field]: value });
  const [expanded, setExpanded] = useState(false);

  const queryParams = endpoint.query_params || {};
  const queryParamCount = Object.keys(queryParams).length;
  const queryString = queryParamCount > 0
    ? "?" + Object.entries(queryParams).map(([k, v]) => `${k}=${v}`).join("&")
    : "";

  const updateQueryParam = (oldKey, newKey, newValue) => {
    const params = { ...endpoint.query_params };
    if (oldKey !== newKey) delete params[oldKey];
    params[newKey] = newValue;
    update("query_params", params);
  };

  const addQueryParam = () => {
    update("query_params", { ...endpoint.query_params, "": "" });
  };

  const removeQueryParam = (key) => {
    const params = { ...endpoint.query_params };
    delete params[key];
    update("query_params", params);
  };

  return (
    <div className="bg-light-bg border border-border-gray transition-colors hover:border-gray-300">
      <div className="grid grid-cols-12 gap-2 items-center p-2">
        <div className="col-span-1 flex justify-center">
          <input
            type="checkbox"
            checked={endpoint.active !== false}
            onChange={(e) => update("active", e.target.checked)}
            disabled={disabled}
            className="w-4 h-4 text-primary bg-white border-border-gray focus:ring-primary focus:ring-1 cursor-pointer disabled:opacity-50"
          />
        </div>
        <div className="col-span-4">
          <input
            type="text"
            value={endpoint.path}
            onChange={(e) => update("path", e.target.value)}
            disabled={disabled}
            placeholder="/arama"
            className="w-full border border-border-gray px-2 py-1.5 text-sm focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary disabled:bg-gray-50 focus:ring-opacity-50"
          />
          {queryString && (
            <div className="mt-0.5 text-[10px] font-mono text-primary/70 truncate" title={`${endpoint.path}${queryString}`}>
              {queryString}
            </div>
          )}
        </div>
        <div className="col-span-2">
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
        <div className="col-span-2 flex justify-center gap-1">
          <button
            onClick={() => setExpanded(!expanded)}
            disabled={disabled}
            className={`text-[10px] font-bold tracking-wider px-2 py-1.5 transition-colors disabled:opacity-30 inline-flex items-center gap-0.5 ${queryParamCount > 0 ? 'text-primary bg-primary/10 hover:bg-primary/20' : 'text-gray-400 hover:text-gray-600 hover:bg-gray-100'}`}
            title="Query Parameters"
          >
            <span className="material-symbols-outlined text-[14px]">{expanded ? "expand_less" : "expand_more"}</span>
            {queryParamCount > 0 ? `${queryParamCount} PARAM` : "PARAMS"}
          </button>
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

      {expanded && (
        <div className="px-2 pb-2 pt-0 border-t border-border-gray mx-2 mb-1">
          <div className="flex items-center justify-between py-1.5">
            <span className="text-[10px] uppercase font-bold tracking-widest text-gray-500">Query Parameters</span>
            <button
              onClick={addQueryParam}
              disabled={disabled}
              className="text-[10px] font-bold tracking-widest text-primary hover:text-[#324ab2] transition-colors disabled:opacity-30 flex items-center gap-0.5"
            >
              <span className="material-symbols-outlined text-[12px]">add</span>
              ADD
            </button>
          </div>
          {Object.entries(endpoint.query_params || {}).map(([key, value], pi) => (
            <div key={pi} className="flex gap-1.5 items-center mb-1">
              <input
                type="text"
                value={key}
                onChange={(e) => updateQueryParam(key, e.target.value, value)}
                disabled={disabled}
                placeholder="key"
                className="w-1/3 border border-border-gray px-2 py-1 text-xs font-mono focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary disabled:bg-gray-50"
              />
              <span className="text-gray-400 text-xs">=</span>
              <input
                type="text"
                value={value}
                onChange={(e) => updateQueryParam(key, key, e.target.value)}
                disabled={disabled}
                placeholder="value"
                className="flex-1 border border-border-gray px-2 py-1 text-xs font-mono focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary disabled:bg-gray-50"
              />
              <button
                onClick={() => removeQueryParam(key)}
                disabled={disabled}
                className="text-gray-400 hover:text-danger p-0.5 transition-colors disabled:opacity-30"
              >
                <span className="material-symbols-outlined text-[14px]">close</span>
              </button>
            </div>
          ))}
          {queryParamCount === 0 && (
            <div className="text-[11px] text-gray-400 py-1">No query parameters</div>
          )}
        </div>
      )}
    </div>
  );
}
