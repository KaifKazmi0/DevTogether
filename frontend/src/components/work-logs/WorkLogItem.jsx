import {
  CalendarDays,
  Clock,
  ExternalLink,
  Pencil,
  Trash2,
} from "lucide-react";

import { categoryIcons } from "../../features/work-logs/categoryIcons";
import {
  formatLogDate,
  getStatusClass,
} from "../../features/work-logs/workLogData";

function WorkLogItem({ log, onEdit, onDelete }) {
  const Icon = categoryIcons[log.category];

  return (
    <article className="worklog-item">
      <div className="activity-icon">{Icon && <Icon size={20} />}</div>

      <div className="worklog-main">
        <div className="worklog-title-row">
          <h3>{log.title}</h3>
          <span className={`worklog-status ${getStatusClass(log.status)}`}>
            {log.status}
          </span>
        </div>

        {log.description && <p>{log.description}</p>}

        <div className="worklog-meta">
          <span className="activity-category">{log.category}</span>

          <span>
            <CalendarDays size={13} />
            {formatLogDate(log.date)}
          </span>

          <span>
            <Clock size={13} />
            {log.hours} hrs
          </span>

          {log.evidenceUrl && (
            <a
              className="worklog-evidence"
              href={log.evidenceUrl}
              target="_blank"
              rel="noreferrer"
            >
              Evidence
              <ExternalLink size={12} />
            </a>
          )}
        </div>
      </div>

      <div className="worklog-actions">
        <button
          type="button"
          className="icon-button"
          onClick={() => onEdit(log)}
          aria-label={`Edit ${log.title}`}
          title="Edit log"
        >
          <Pencil size={16} />
        </button>

        <button
          type="button"
          className="icon-button delete-button"
          onClick={() => onDelete(log)}
          aria-label={`Delete ${log.title}`}
          title="Delete log"
        >
          <Trash2 size={16} />
        </button>
      </div>
    </article>
  );
}

export default WorkLogItem;
