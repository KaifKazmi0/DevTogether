import { Flame, GitCommit, Clock, CheckCircle } from "lucide-react";

import StatCard from "../components/dashboard/StatCard";

import ContributionHeatmap from "../components/dashboard/ContributionHeatmap";
import {
  dashboardStats,
  generateContributionData,
} from "../features/dashboard/dashboardData";

const icons = {
  contributions: GitCommit,
  streak: Flame,
  hours: Clock,
  tasks: CheckCircle,
};

function Dashboard() {
  const contributions = generateContributionData();
  return (
    <div className="dashboard">
      <div className="dashboard-heading">
        <div>
          <h1>Overview</h1>
          <p>Track your progress and contributions.</p>
        </div>
      </div>

      <div className="stats-grid">
        {dashboardStats.map((stat) => (
          <StatCard
            key={stat.id}
            title={stat.title}
            value={stat.value}
            subtitle={stat.subtitle}
            icon={icons[stat.type]}
          />
        ))}
      </div>
      <ContributionHeatmap contributions={contributions} />
    </div>
  );
}

export default Dashboard;