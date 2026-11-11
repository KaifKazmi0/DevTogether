import { useState } from "react";

import { categoryIcons } from "../../features/work-logs/categoryIcons";
import {
  WORK_CATEGORIES,
  formatLogDate,
  getStatusClass,
} from "../../features/work-logs/workLogData";

const categories = ["All", ...WORK_CATEGORIES];

function RecentActivity({ activities }) {
  const [selectedCategory, setSelectedCategory] = useState("All");

  const filteredActivities =
    selectedCategory === "All"
      ? activities
      : activities.filter((activity) => activity.category === selectedCategory);

  return (
    <section className="recent-activity">
      <div className="activity-filters">
        {categories.map((category) => (
          <button
            key={category}
            className={
              selectedCategory === category
                ? "activity-filter active"
                : "activity-filter"
            }
            onClick={() => setSelectedCategory(category)}
          >
            {category}
          </button>
        ))}
      </div>

      <div className="recent-activity-header">
        <div>
          <h2>Recent Work & Learning</h2>
          <p>Your latest development and learning activities.</p>
        </div>
      </div>

      {filteredActivities.length > 0 ? (
        <div className="activity-list">
          {filteredActivities.map((activity) => {
            const Icon = categoryIcons[activity.category];

            return (
              <article className="activity-item" key={activity.id}>
                <div className="activity-icon">
                  {Icon && <Icon size={20} />}
                </div>

                <div className="activity-details">
                  <h3>{activity.title}</h3>
                  {activity.description && <p>{activity.description}</p>}

                  <div className="activity-meta">
                    <span className="activity-category">
                      {activity.category}
                    </span>

                    <span>{formatLogDate(activity.date)}</span>
                    <span>{activity.hours} hrs</span>
                  </div>
                </div>

                <span
                  className={`activity-status ${getStatusClass(
                    activity.status,
                  )}`}
                >
                  {activity.status}
                </span>
              </article>
            );
          })}
        </div>
      ) : (
        <p className="activity-empty">
          No activities in this category yet.
        </p>
      )}
    </section>
  );
}

export default RecentActivity;
