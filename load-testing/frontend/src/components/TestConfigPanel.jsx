export function TestConfigPanel({ userCount, spawnRate, durationSeconds, onChange, disabled }) {
  return (
    <div className="space-y-4">
      <div>
        <label className="block text-[10px] uppercase font-bold tracking-widest text-text-charcoal mb-2">VIRTUAL USERS</label>
        <input
          type="number"
          value={userCount}
          min={1}
          disabled={disabled}
          onChange={(e) => onChange("userCount", parseInt(e.target.value) || 1)}
          className="w-full border border-border-gray px-3 py-2 text-sm focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary disabled:bg-gray-50"
        />
      </div>
      <div className="grid grid-cols-2 gap-4">
        <div>
          <label className="block text-[10px] uppercase font-bold tracking-widest text-text-charcoal mb-2">SPAWN RATE / s</label>
          <input
            type="number"
            value={spawnRate}
            min={1}
            disabled={disabled}
            onChange={(e) => onChange("spawnRate", parseInt(e.target.value) || 1)}
            className="w-full border border-border-gray px-3 py-2 text-sm focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary disabled:bg-gray-50"
          />
        </div>
        <div>
          <label className="block text-[10px] uppercase font-bold tracking-widest text-text-charcoal mb-2">DURATION (s)</label>
          <input
            type="number"
            value={durationSeconds}
            min={5}
            disabled={disabled}
            onChange={(e) => onChange("durationSeconds", parseInt(e.target.value) || 60)}
            className="w-full border border-border-gray px-3 py-2 text-sm focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary disabled:bg-gray-50"
          />
        </div>
      </div>
    </div>
  );
}
