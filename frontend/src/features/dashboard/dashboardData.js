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


export function generateContributionData() {
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
    const level = (index * 7 + date.getDate()) % 5;

    return {
      date: date.toISOString().split("T")[0],
      count: level === 0 ? 0 : level * 2,
      level,
    };
  });
}