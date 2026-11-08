import { createSeedLogs, sortLogs } from "./workLogData";

const STORAGE_KEY = "devtogether.workLogs.v1";

function createId() {
  if (typeof crypto !== "undefined" && crypto.randomUUID) {
    return crypto.randomUUID();
  }

  return `log-${Date.now()}-${Math.random().toString(16).slice(2)}`;
}

function persist(logs) {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(logs));
  } catch {
    // Storage can be unavailable (for example, private browsing).
    // The UI still works for the current session.
  }
}

/**
 * Mock data access layer. When the Spring Boot API is ready, replace the
 * bodies of these functions with HTTP calls and keep the component API.
 */
export function getWorkLogs() {
  try {
    const stored = localStorage.getItem(STORAGE_KEY);

    if (stored === null) {
      const seed = createSeedLogs();
      persist(seed);

      return sortLogs(seed);
    }

    const parsed = JSON.parse(stored);
    if (!Array.isArray(parsed)) {
      throw new Error("Stored work logs are invalid");
    }

    return sortLogs(parsed);
  } catch {
    return sortLogs(createSeedLogs());
  }
}

export function createWorkLog(input) {
  const now = new Date().toISOString();
  const log = {
    ...input,
    id: createId(),
    source: "manual",
    createdAt: now,
    updatedAt: now,
  };

  persist([...getWorkLogs(), log]);

  return log;
}

export function updateWorkLog(id, updates) {
  const logs = getWorkLogs().map((log) =>
    log.id === id
      ? { ...log, ...updates, updatedAt: new Date().toISOString() }
      : log,
  );

  persist(logs);
}

export function deleteWorkLog(id) {
  persist(getWorkLogs().filter((log) => log.id !== id));
}
