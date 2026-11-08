import { useState } from "react";

import {
  WORK_CATEGORIES,
  WORK_STATUSES,
  toISODate,
} from "../../features/work-logs/workLogData";

function createEmptyForm() {
  return {
    title: "",
    category: WORK_CATEGORIES[0],
    date: toISODate(new Date()),
    hours: "",
    status: "Completed",
    description: "",
    evidenceUrl: "",
  };
}

function WorkLogForm({ log, onSave, onCancel }) {
  const [form, setForm] = useState(() =>
    log
      ? {
          title: log.title,
          category: log.category,
          date: log.date,
          hours: String(log.hours),
          status: log.status,
          description: log.description ?? "",
          evidenceUrl: log.evidenceUrl ?? "",
        }
      : createEmptyForm(),
  );
  const [errors, setErrors] = useState({});

  const today = toISODate(new Date());

  function handleChange(event) {
    const { name, value } = event.target;

    setForm((current) => ({ ...current, [name]: value }));
    setErrors((current) => ({ ...current, [name]: undefined }));
  }

  function validate() {
    const nextErrors = {};
    const title = form.title.trim();
    const hours = Number(form.hours);

    if (!title) {
      nextErrors.title = "Title is required.";
    } else if (title.length > 120) {
      nextErrors.title = "Keep the title under 120 characters.";
    }

    if (!WORK_CATEGORIES.includes(form.category)) {
      nextErrors.category = "Choose a category.";
    }

    if (!form.date) {
      nextErrors.date = "Date is required.";
    } else if (form.date > today) {
      nextErrors.date = "Date cannot be in the future.";
    }

    if (!form.hours || Number.isNaN(hours)) {
      nextErrors.hours = "Enter the hours spent.";
    } else if (hours < 0.25 || hours > 24) {
      nextErrors.hours = "Hours must be between 0.25 and 24.";
    }

    if (!WORK_STATUSES.includes(form.status)) {
      nextErrors.status = "Choose a status.";
    }

    if (form.evidenceUrl.trim()) {
      try {
        const url = new URL(form.evidenceUrl.trim());
        if (!["http:", "https:"].includes(url.protocol)) {
          throw new Error("Unsupported protocol");
        }
      } catch {
        nextErrors.evidenceUrl =
          "Use a full URL starting with http:// or https://.";
      }
    }

    return nextErrors;
  }

  function handleSubmit(event) {
    event.preventDefault();

    const nextErrors = validate();
    if (Object.keys(nextErrors).length > 0) {
      setErrors(nextErrors);
      return;
    }

    onSave({
      title: form.title.trim(),
      category: form.category,
      date: form.date,
      hours: Number(form.hours),
      status: form.status,
      description: form.description.trim(),
      evidenceUrl: form.evidenceUrl.trim(),
    });
  }

  return (
    <form className="worklog-form" onSubmit={handleSubmit} noValidate>
      <div className="form-field">
        <label htmlFor="worklog-title">Title</label>
        <input
          id="worklog-title"
          name="title"
          type="text"
          value={form.title}
          onChange={handleChange}
          placeholder="What did you work on?"
          autoFocus
          aria-invalid={Boolean(errors.title)}
          aria-describedby={errors.title ? "worklog-title-error" : undefined}
        />
        {errors.title && (
          <p className="field-error" id="worklog-title-error">
            {errors.title}
          </p>
        )}
      </div>

      <div className="form-grid">
        <div className="form-field">
          <label htmlFor="worklog-category">Category</label>
          <select
            id="worklog-category"
            name="category"
            value={form.category}
            onChange={handleChange}
            aria-invalid={Boolean(errors.category)}
          >
            {WORK_CATEGORIES.map((category) => (
              <option key={category} value={category}>
                {category}
              </option>
            ))}
          </select>
          {errors.category && (
            <p className="field-error">{errors.category}</p>
          )}
        </div>

        <div className="form-field">
          <label htmlFor="worklog-date">Date</label>
          <input
            id="worklog-date"
            name="date"
            type="date"
            value={form.date}
            max={today}
            onChange={handleChange}
            aria-invalid={Boolean(errors.date)}
            aria-describedby={errors.date ? "worklog-date-error" : undefined}
          />
          {errors.date && (
            <p className="field-error" id="worklog-date-error">
              {errors.date}
            </p>
          )}
        </div>

        <div className="form-field">
          <label htmlFor="worklog-hours">Hours spent</label>
          <input
            id="worklog-hours"
            name="hours"
            type="number"
            min="0.25"
            max="24"
            step="0.25"
            value={form.hours}
            onChange={handleChange}
            placeholder="e.g. 2.5"
            aria-invalid={Boolean(errors.hours)}
            aria-describedby={errors.hours ? "worklog-hours-error" : undefined}
          />
          {errors.hours && (
            <p className="field-error" id="worklog-hours-error">
              {errors.hours}
            </p>
          )}
        </div>

        <div className="form-field">
          <label htmlFor="worklog-status">Status</label>
          <select
            id="worklog-status"
            name="status"
            value={form.status}
            onChange={handleChange}
            aria-invalid={Boolean(errors.status)}
          >
            {WORK_STATUSES.map((status) => (
              <option key={status} value={status}>
                {status}
              </option>
            ))}
          </select>
          {errors.status && <p className="field-error">{errors.status}</p>}
        </div>
      </div>

      <div className="form-field">
        <label htmlFor="worklog-description">Description</label>
        <textarea
          id="worklog-description"
          name="description"
          rows="3"
          value={form.description}
          onChange={handleChange}
          placeholder="Add details about what you did, learned, or reviewed."
        />
      </div>

      <div className="form-field">
        <label htmlFor="worklog-evidence">
          Evidence URL <span className="optional">(optional)</span>
        </label>
        <input
          id="worklog-evidence"
          name="evidenceUrl"
          type="url"
          value={form.evidenceUrl}
          onChange={handleChange}
          placeholder="https://github.com/..."
          aria-invalid={Boolean(errors.evidenceUrl)}
          aria-describedby={
            errors.evidenceUrl ? "worklog-evidence-error" : undefined
          }
        />
        {errors.evidenceUrl && (
          <p className="field-error" id="worklog-evidence-error">
            {errors.evidenceUrl}
          </p>
        )}
      </div>

      <div className="modal-actions">
        <button
          type="button"
          className="secondary-button"
          onClick={onCancel}
        >
          Cancel
        </button>

        <button type="submit" className="primary-button">
          {log ? "Save changes" : "Add work log"}
        </button>
      </div>
    </form>
  );
}

export default WorkLogForm;
