import { useMemo, useState } from "react";
import { ClipboardList, Plus, Search, SearchX } from "lucide-react";

import Modal from "../components/ui/Modal";
import WorkLogForm from "../components/work-logs/WorkLogForm";
import WorkLogItem from "../components/work-logs/WorkLogItem";

import {
  WORK_CATEGORIES,
  WORK_STATUSES,
} from "../features/work-logs/workLogData";
import {
  createWorkLog,
  deleteWorkLog,
  getWorkLogs,
  updateWorkLog,
} from "../features/work-logs/workLogService";

function WorkLogs() {
  const [logs, setLogs] = useState(() => getWorkLogs());
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState("All");
  const [status, setStatus] = useState("All");
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingLog, setEditingLog] = useState(null);
  const [logToDelete, setLogToDelete] = useState(null);

  const filteredLogs = useMemo(() => {
    const query = search.trim().toLowerCase();

    return logs.filter((log) => {
      const matchesQuery =
        !query ||
        log.title.toLowerCase().includes(query) ||
        (log.description ?? "").toLowerCase().includes(query);

      const matchesCategory = category === "All" || log.category === category;
      const matchesStatus = status === "All" || log.status === status;

      return matchesQuery && matchesCategory && matchesStatus;
    });
  }, [logs, search, category, status]);

  const summary = useMemo(() => {
    const hours = logs.reduce((total, log) => total + Number(log.hours || 0), 0);
    const completed = logs.filter((log) => log.status === "Completed").length;

    return {
      entries: logs.length,
      hours: Number.isInteger(hours) ? hours : hours.toFixed(2),
      completed,
      open: logs.length - completed,
    };
  }, [logs]);

  const hasFilters = search !== "" || category !== "All" || status !== "All";

  function refreshLogs() {
    setLogs(getWorkLogs());
  }

  function openCreateForm() {
    setEditingLog(null);
    setIsFormOpen(true);
  }

  function openEditForm(log) {
    setEditingLog(log);
    setIsFormOpen(true);
  }

  function closeForm() {
    setIsFormOpen(false);
    setEditingLog(null);
  }

  function handleSave(values) {
    if (editingLog) {
      updateWorkLog(editingLog.id, values);
    } else {
      createWorkLog(values);
    }

    refreshLogs();
    closeForm();
  }

  function handleDelete() {
    if (!logToDelete) return;

    deleteWorkLog(logToDelete.id);
    refreshLogs();
    setLogToDelete(null);
  }

  function clearFilters() {
    setSearch("");
    setCategory("All");
    setStatus("All");
  }

  return (
    <div className="dashboard">
      <div className="page-heading">
        <div>
          <h1>Work Logs</h1>
          <p>
            Record what you worked on — code, learning, reviews, and everything
            in between.
          </p>
        </div>

        <button type="button" className="primary-button" onClick={openCreateForm}>
          <Plus size={17} />
          Add Work Log
        </button>
      </div>

      <div className="worklog-summary">
        <div className="worklog-summary-item">
          <span>Entries</span>
          <strong>{summary.entries}</strong>
        </div>

        <div className="worklog-summary-item">
          <span>Hours logged</span>
          <strong>{summary.hours} hrs</strong>
        </div>

        <div className="worklog-summary-item">
          <span>Completed</span>
          <strong>{summary.completed}</strong>
        </div>

        <div className="worklog-summary-item">
          <span>In progress / blocked</span>
          <strong>{summary.open}</strong>
        </div>
      </div>

      <section className="worklog-panel">
        <div className="worklog-toolbar">
          <label className="worklog-search">
            <Search size={16} />
            <input
              type="search"
              value={search}
              onChange={(event) => setSearch(event.target.value)}
              placeholder="Search logs..."
              aria-label="Search work logs"
            />
          </label>

          <select
            value={category}
            onChange={(event) => setCategory(event.target.value)}
            aria-label="Filter by category"
          >
            <option value="All">All categories</option>
            {WORK_CATEGORIES.map((item) => (
              <option key={item} value={item}>
                {item}
              </option>
            ))}
          </select>

          <select
            value={status}
            onChange={(event) => setStatus(event.target.value)}
            aria-label="Filter by status"
          >
            <option value="All">All statuses</option>
            {WORK_STATUSES.map((item) => (
              <option key={item} value={item}>
                {item}
              </option>
            ))}
          </select>

          {hasFilters && (
            <button
              type="button"
              className="clear-filters"
              onClick={clearFilters}
            >
              Clear filters
            </button>
          )}
        </div>

        {filteredLogs.length > 0 ? (
          <>
            <p className="worklog-list-count">
              Showing {filteredLogs.length} of {logs.length}{" "}
              {logs.length === 1 ? "log" : "logs"} · newest first
            </p>

            <div className="worklog-list">
              {filteredLogs.map((log) => (
                <WorkLogItem
                  key={log.id}
                  log={log}
                  onEdit={openEditForm}
                  onDelete={setLogToDelete}
                />
              ))}
            </div>
          </>
        ) : logs.length === 0 ? (
          <div className="worklog-empty">
            <ClipboardList size={34} />
            <h3>No work logs yet</h3>
            <p>
              Add your first log to start building your contribution history.
            </p>
            <button
              type="button"
              className="primary-button"
              onClick={openCreateForm}
            >
              <Plus size={17} />
              Add Work Log
            </button>
          </div>
        ) : (
          <div className="worklog-empty">
            <SearchX size={34} />
            <h3>No logs match your filters</h3>
            <p>Try a different search term, category, or status.</p>
            <button
              type="button"
              className="secondary-button"
              onClick={clearFilters}
            >
              Clear filters
            </button>
          </div>
        )}
      </section>

      {isFormOpen && (
        <Modal
          title={editingLog ? "Edit work log" : "Add work log"}
          onClose={closeForm}
        >
          <WorkLogForm
            log={editingLog}
            onSave={handleSave}
            onCancel={closeForm}
          />
        </Modal>
      )}

      {logToDelete && (
        <Modal title="Delete work log" onClose={() => setLogToDelete(null)}>
          <p className="modal-text">
            Are you sure you want to delete{" "}
            <strong>{logToDelete.title}</strong>? This action cannot be undone.
          </p>

          <div className="modal-actions">
            <button
              type="button"
              className="secondary-button"
              onClick={() => setLogToDelete(null)}
            >
              Cancel
            </button>

            <button type="button" className="danger-button" onClick={handleDelete}>
              Delete log
            </button>
          </div>
        </Modal>
      )}
    </div>
  );
}

export default WorkLogs;
