import { Flame, GitCommit, Clock, CheckCircle } from "lucide-react";

import StatCard from "../components/dashboard/StatCard";
import RecentActivity from "../components/dashboard/RecentActivity";
import ContributionHeatmap from "../components/dashboard/ContributionHeatmap";

import {
  dashboardStats,
  developerHeatmaps,
} from "../features/dashboard/dashboardData";
import { getWorkLogs } from "../features/work-logs/workLogService";

const icons = {
  contributions: GitCommit,
  streak: Flame,
  hours: Clock,
  tasks: CheckCircle,
};

function Dashboard() {
  const recentLogs = getWorkLogs().slice(0, 5);

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

      <div className="developer-heatmaps">
        {developerHeatmaps.map((developer) => (
          <ContributionHeatmap
            key={developer.id}
            developerName={developer.name}
            contributions={developer.contributions}
          />
        ))}
      </div>

      <RecentActivity activities={recentLogs} />
    </div>
  );
}

export default Dashboard;
