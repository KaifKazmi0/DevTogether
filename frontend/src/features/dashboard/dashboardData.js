export const dashboardStats = [
  {
    id: 1,
    title: "Total Contributions",
    value: "128",
    subtitle: "+12 this week",
    type: "contributions",
  },
  {
    id: 2,
    title: "Current Streak",
    value: "12 days",
    subtitle: "Personal best: 18 days",
    type: "streak",
  },
  {
    id: 3,
    title: "Hours Logged",
    value: "42.5 hrs",
    subtitle: "This week",
    type: "hours",
  },
  {
    id: 4,
    title: "Completed Tasks",
    value: "18",
    subtitle: "3 remaining",
    type: "tasks",
  },
];


export function generateContributionData(seed = 1) {
  const today = new Date();

  // Find the Saturday of the current week
  const endDate = new Date(today);
  endDate.setDate(today.getDate() + (6 - today.getDay()));

  // Start 12 weeks before the ending Saturday
  const startDate = new Date(endDate);
  startDate.setDate(endDate.getDate() - 83);

  return Array.from({ length: 84 }, (_, index) => {
    const date = new Date(startDate);
    date.setDate(startDate.getDate() + index);

    // Deterministic mock activity level: 0 to 4
    const level = (index * 7 + date.getDate() + seed*3) % 5;

    return {
      date: date.toISOString().split("T")[0],
      count: level === 0 ? 0 : level * 2,
      level,
    };
  });
}

export const developerHeatmaps = [
  {
    id: 1,
    name: "You",
    contributions: generateContributionData(1),
  },
  {
    id: 2,
    name: "Collaborator",
    contributions: generateContributionData(3),
  },
];



export const activityCategories = [
  "Development",
  "Learning",
  "Research",
  "Bug Fix",
  "Documentation",
  "Testing",
  "Code Review",
  "Planning",
];

export const recentActivities = [
  {
    id: 1,
    title: "Implemented responsive dashboard navigation",
    category: "Development",
    date: "2026-11-04",
    hours: 3.5,
    status: "Completed",
    description: "Implemented mobile sidebar and responsive layout.",
    evidenceUrl: "",
    source: "manual",
  },
  {
    id: 2,
    title: "Studied React state management",
    category: "Learning",
    date: "2026-11-04",
    hours: 1.5,
    status: "Completed",
    description: "Learned useState, props and lifting state up.",
    evidenceUrl: "",
    source: "manual",
  },
  {
    id: 3,
    title: "Researched GitHub API integration",
    category: "Research",
    date: "2026-11-03",
    hours: 2,
    status: "Completed",
    description: "Explored approaches to importing repository activity.",
    evidenceUrl: "",
    source: "manual",
  },
  {
    id: 4,
    title: "Created contribution heatmap",
    category: "Development",
    date: "2026-11-02",
    hours: 2,
    status: "Completed",
    description: "Built a 12-week contribution activity visualization.",
    evidenceUrl: "",
    source: "manual",
  },
  {
    id: 5,
    title: "Reviewed dashboard implementation",
    category: "Code Review",
    date: "2026-11-01",
    hours: 1,
    status: "In Progress",
    description: "Reviewed component structure and implementation.",
    evidenceUrl: "",
    source: "manual",
  },
];