import { useEffect, useRef } from "react";

/**
 * Generic polling hook.
 * @param {() => Promise<void>} fetchFn - async function called each interval
 * @param {number} intervalMs - polling interval in milliseconds
 * @param {boolean} enabled - polling only runs when true
 */
export function usePolling(fetchFn, intervalMs, enabled) {
  const savedFn = useRef(fetchFn);

  useEffect(() => {
    savedFn.current = fetchFn;
  }, [fetchFn]);

  useEffect(() => {
    if (!enabled) return;

    // Fire immediately on enable
    savedFn.current();

    const id = setInterval(() => {
      savedFn.current();
    }, intervalMs);

    return () => clearInterval(id);
  }, [enabled, intervalMs]);
}
