export function TestConfigPanel({ userCount, spawnRate, durationSeconds, onChange, disabled }) {
  return (
    <div>
      <p className="text-sm font-medium text-gray-700 mb-2">Test Configuration</p>
      <div className="grid grid-cols-3 gap-3">
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">Users</label>
          <input
            type="number"
            value={userCount}
            min={1}
            disabled={disabled}
            onChange={(e) => onChange("userCount", parseInt(e.target.value) || 1)}
            className="w-full border border-gray-300 rounded px-2 py-1.5 text-sm focus:outline-none focus:ring-1 focus:ring-indigo-400 disabled:bg-gray-50"
          />
        </div>
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">Spawn rate/s</label>
          <input
            type="number"
            value={spawnRate}
            min={1}
            disabled={disabled}
            onChange={(e) => onChange("spawnRate", parseInt(e.target.value) || 1)}
            className="w-full border border-gray-300 rounded px-2 py-1.5 text-sm focus:outline-none focus:ring-1 focus:ring-indigo-400 disabled:bg-gray-50"
          />
        </div>
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">Duration (s)</label>
          <input
            type="number"
            value={durationSeconds}
            min={5}
            disabled={disabled}
            onChange={(e) => onChange("durationSeconds", parseInt(e.target.value) || 60)}
            className="w-full border border-gray-300 rounded px-2 py-1.5 text-sm focus:outline-none focus:ring-1 focus:ring-indigo-400 disabled:bg-gray-50"
          />
        </div>
      </div>
    </div>
  );
}
