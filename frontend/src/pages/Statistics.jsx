import { ChartNoAxesCombined } from "lucide-react";

function Statistics() {
  return (
    <div className="dashboard">
      <div className="dashboard-heading">
        <h1>Statistics</h1>
        <p>Compare weekly activity, streaks, and category totals.</p>
      </div>

      <section className="placeholder-card">
        <ChartNoAxesCombined size={34} />
        <h2>Coming in the next step</h2>
        <p>
          Weekly comparisons, current and longest streaks, and category
          breakdowns will be built here. The work logs you add now already
          provide the data.
        </p>
      </section>
    </div>
  );
}

export default Statistics;
