const BASE_URL = import.meta.env.VITE_API_BASE_URL || "";

async function request(path, options = {}) {
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: { "Content-Type": "application/json", ...options.headers },
    ...options,
  });
  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(`${res.status} ${res.statusText}: ${text}`);
  }
  return res.json();
}

export const backendClient = {
  startTest: (payload) =>
    request("/test/start", { method: "POST", body: JSON.stringify(payload) }),

  stopTest: () =>
    request("/test/stop", { method: "POST" }),

  getStatus: () =>
    request("/status"),

  getMetrics: () =>
    request("/metrics"),

  getHealth: () =>
    request("/health"),

  getHistory: () =>
    request("/test/history"),
};
