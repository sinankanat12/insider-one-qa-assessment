export function BaseUrlInput({ value, onChange, disabled }) {
  return (
    <div>
      <label className="block text-[10px] uppercase font-bold tracking-widest text-text-charcoal mb-2">
        BASE URL
      </label>
      <input
        type="url"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        disabled={disabled}
        placeholder="https://www.n11.com"
        className="w-full border border-border-gray px-3 py-2 text-sm focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary disabled:bg-gray-50 focus:ring-opacity-50"
      />
    </div>
  );
}
