function ContributionHeatmap({ contributions, developerName }) {
  const weeks = [];

  for (let i = 0; i < contributions.length; i += 7) {
    weeks.push(contributions.slice(i, i + 7));
  }

  return (
    <section className="heatmap-card">
      <div className="heatmap-header">
        <div>
          <h2>{developerName}'s Contribution Activity</h2>
          <p>Your activity over the last 12 weeks</p>
        </div>
      </div>

      <div className="heatmap-container">
        <div className="heatmap-days">
          {["Mon", "Wed", "Fri"].map((day) => (
            <span key={day}>{day}</span>
          ))}
        </div>

        <div className="heatmap-grid">
          {weeks.map((week, weekIndex) => (
            <div className="heatmap-week" key={weekIndex}>
              {week.map((day) => (
                <div
                  key={day.date}
                  className={`heatmap-cell level-${day.level}`}
                  title={`${day.count} contributions on ${day.date}`}
                />
              ))}
            </div>
          ))}
        </div>
      </div>

      <div className="heatmap-legend">
        <span>Less</span>
        {[0, 1, 2, 3, 4].map((level) => (
          <div
            key={level}
            className={`heatmap-cell level-${level}`}
          />
        ))}
        <span>More</span>
      </div>
    </section>
  );
}

export default ContributionHeatmap;