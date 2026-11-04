import {
  BookOpen,
  Code2,
  Search,
  Bug,
  FileText,
  FlaskConical,
  GitPullRequest,
  Lightbulb,
} from "lucide-react";

import { useState } from "react";

const categoryIcons = {
  Development: Code2,
  Learning: BookOpen,
  Research: Search,
  "Bug Fix": Bug,
  Documentation: FileText,
  Testing: FlaskConical,
  "Code Review": GitPullRequest,
  Planning: Lightbulb,
};

function RecentActivity({ activities }) {
  const [selectedCategory, setSelectedCategory] = useState("All");

  const categories = [
    "All",
    "Development",
    "Learning",
    "Research",
    "Bug Fix",
    "Documentation",
    "Testing",
    "Code Review",
    "Planning",
  ];

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

      <div className="activity-list">
        {filteredActivities.map((activity) => {
          const Icon = categoryIcons[activity.category];

          return (
            <article className="activity-item" key={activity.id}>
              <div className="activity-icon">{Icon && <Icon size={20} />}</div>

              <div className="activity-details">
                <h3>{activity.title}</h3>
                <p>{activity.description}</p>

                <div className="activity-meta">
                  <span className="activity-category">{activity.category}</span>

                  <span>{activity.date}</span>
                  <span>{activity.hours} hrs</span>
                </div>
              </div>

              <span
                className={`activity-status ${
                  activity.status === "Completed" ? "completed" : "in-progress"
                }`}
              >
                {activity.status}
              </span>
            </article>
          );
        })}
      </div>
    </section>
  );
}

export default RecentActivity;
