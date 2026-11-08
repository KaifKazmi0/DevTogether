export const WORK_CATEGORIES = [
  "Development",
  "Learning",
  "Research",
  "Bug Fix",
  "Documentation",
  "Testing",
  "Code Review",
  "Planning",
];

export const WORK_STATUSES = ["Completed", "In Progress", "Blocked"];

/**
 * Formats a local Date as YYYY-MM-DD without converting to UTC.
 * `toISOString()` can shift the date backwards in positive-offset timezones.
 */
export function toISODate(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");

  return `${year}-${month}-${day}`;
}

function daysAgo(days) {
  const date = new Date();
  date.setDate(date.getDate() - days);

  return toISODate(date);
}

/**
 * Sample logs used the first time the dashboard runs.
 * Dates are relative to today so the list stays current.
 */
export function createSeedLogs() {
  const now = new Date().toISOString();

  const base = {
    evidenceUrl: "",
    source: "manual",
    createdAt: now,
    updatedAt: now,
  };

  return [
    {
      ...base,
      id: "seed-1",
      title: "Implemented work log management",
      category: "Development",
      date: daysAgo(0),
      hours: 3.5,
      status: "Completed",
      description: "Built the work log list, filters, and create/edit form.",
    },
    {
      ...base,
      id: "seed-2",
      title: "Studied React form state",
      category: "Learning",
      date: daysAgo(0),
      hours: 1.5,
      status: "Completed",
      description: "Practised controlled inputs, validation, and modal state.",
    },
    {
      ...base,
      id: "seed-3",
      title: "Researched GitHub API integration",
      category: "Research",
      date: daysAgo(1),
      hours: 2,
      status: "Completed",
      description: "Explored approaches to importing repository activity.",
    },
    {
      ...base,
      id: "seed-4",
      title: "Created contribution heatmap",
      category: "Development",
      date: daysAgo(2),
      hours: 2,
      status: "Completed",
      description: "Built the 12-week contribution activity visualization.",
    },
    {
      ...base,
      id: "seed-5",
      title: "Reviewed dashboard implementation",
      category: "Code Review",
      date: daysAgo(3),
      hours: 1,
      status: "In Progress",
      description: "Reviewing component structure and responsive behaviour.",
      evidenceUrl: "https://github.com/KaifKazmi0/DevTogether",
    },
    {
      ...base,
      id: "seed-6",
      title: "Wrote activity tracking documentation",
      category: "Documentation",
      date: daysAgo(4),
      hours: 1.25,
      status: "Completed",
      description:
        "Documented categories, record structure, and double-counting rules.",
    },
    {
      ...base,
      id: "seed-7",
      title: "Tested responsive navigation",
      category: "Testing",
      date: daysAgo(5),
      hours: 1.5,
      status: "Completed",
      description: "Verified the sidebar drawer, backdrop, and mobile breakpoints.",
    },
    {
      ...base,
      id: "seed-8",
      title: "Planned weekly statistics section",
      category: "Planning",
      date: daysAgo(6),
      hours: 2,
      status: "Completed",
      description: "Defined comparison metrics and the weekly date window.",
    },
    {
      ...base,
      id: "seed-9",
      title: "Fixed heatmap label alignment",
      category: "Bug Fix",
      date: daysAgo(8),
      hours: 0.75,
      status: "Completed",
      description: "Aligned weekday labels with their heatmap rows.",
    },
    {
      ...base,
      id: "seed-10",
      title: "Investigating timezone edge case in streaks",
      category: "Bug Fix",
      date: daysAgo(10),
      hours: 1,
      status: "Blocked",
      description: "Blocked until the backend exposes user timezone settings.",
    },
  ];
}

/** Newest date first; newer records win ties on the same day. */
export function sortLogs(logs) {
  return [...logs].sort((a, b) => {
    if (a.date !== b.date) {
      return a.date < b.date ? 1 : -1;
    }

    return (b.createdAt ?? "").localeCompare(a.createdAt ?? "");
  });
}

export function formatLogDate(isoDate) {
  if (!isoDate) return "";

  const today = toISODate(new Date());
  if (isoDate === today) return "Today";

  const yesterdayDate = new Date();
  yesterdayDate.setDate(yesterdayDate.getDate() - 1);
  if (isoDate === toISODate(yesterdayDate)) return "Yesterday";

  const date = new Date(`${isoDate}T00:00:00`);
  if (Number.isNaN(date.getTime())) return isoDate;

  return date.toLocaleDateString(undefined, {
    day: "numeric",
    month: "short",
    year: "numeric",
  });
}

export function getStatusClass(status) {
  if (status === "Completed") return "completed";
  if (status === "Blocked") return "blocked";

  return "in-progress";
}
