# DevTogether — Work Logs

## 1. Feature overview

The Work Logs page (`/logs`) is the first functional dashboard feature. It lets a
collaborator record work that GitHub cannot see: learning, research, reviews,
testing, documentation, planning, and bug fixes.

Implemented behaviour:

- List all work logs, newest first.
- Search by title or description.
- Filter by category and status.
- Summary cards for entries, total hours, completed, and open logs.
- Create, edit, and delete logs through a modal form.
- Inline validation with accessible error messages.
- Persist logs in `localStorage` until the backend API is available.
- Show the five most recent logs on the dashboard Overview page.

## 2. Files

| File | Responsibility |
|---|---|
| `src/pages/WorkLogs.jsx` | Page state, filtering, summary, create/edit/delete flow |
| `src/components/work-logs/WorkLogForm.jsx` | Controlled form and validation |
| `src/components/work-logs/WorkLogItem.jsx` | One log row with edit/delete actions |
| `src/components/ui/Modal.jsx` | Reusable dialog with Escape key, backdrop click, scroll lock |
| `src/features/work-logs/workLogData.js` | Categories, statuses, seed data, date and status helpers |
| `src/features/work-logs/workLogService.js` | Mock data access layer backed by `localStorage` |
| `src/features/work-logs/categoryIcons.js` | Category-to-Lucide-icon mapping shared with Recent Activity |

Routes were added in `src/App.jsx` for `/logs`, `/statistics`, `/tasks`, and a
catch-all 404 page. Statistics and Tasks currently render placeholder pages so
the sidebar never navigates to a blank screen.

## 3. Data model

```js
{
  id: "uuid",
  title: "Implemented work log management",
  category: "Development",
  date: "2026-11-08",
  hours: 3.5,
  status: "Completed",
  description: "Built the list, filters, and form.",
  evidenceUrl: "https://github.com/...",
  source: "manual",
  createdAt: "2026-11-08T10:00:00.000Z",
  updatedAt: "2026-11-08T10:00:00.000Z"
}
```

Categories: Development, Learning, Research, Bug Fix, Documentation, Testing,
Code Review, Planning.

Statuses: Completed, In Progress, Blocked.

## 4. Data flow

1. `WorkLogs` initializes `logs` with `getWorkLogs()` from the service.
2. Filters and summary values are derived with `useMemo`; the full log list is
   never mutated.
3. Submitting the form calls `createWorkLog` or `updateWorkLog`.
4. Deleting calls `deleteWorkLog` after confirmation.
5. After every mutation, `getWorkLogs()` is called again to refresh React state.
6. `Dashboard` calls `getWorkLogs().slice(0, 5)` so the Overview stays in sync.

The service is intentionally the only place that knows about persistence.
When the Spring Boot API is ready, replace the service function bodies with
`fetch` calls and keep the component interface unchanged.

## 5. Validation rules

| Field | Rule |
|---|---|
| Title | Required, trimmed, at most 120 characters |
| Category | Must be one of the supported categories |
| Date | Required, cannot be in the future |
| Hours | Required, between 0.25 and 24 |
| Status | Completed, In Progress, or Blocked |
| Evidence URL | Optional; must use `http://` or `https://` |

Dates are formatted locally (`YYYY-MM-DD`) instead of using
`toISOString()` so positive-offset timezones do not shift the calendar date.

## 6. Testing checklist

- [x] `/logs` renders the list instead of a blank page.
- [x] Add a log and see it appear at the top of the list.
- [x] Reload the page and confirm the log persists.
- [x] Edit a log and see the updated values.
- [x] Delete a log through the confirmation dialog.
- [x] Invalid title, hours, future date, and evidence URL show inline errors.
- [x] Search, category, and status filters combine correctly.
- [x] Empty states appear for no logs and for no filter matches.
- [x] Overview shows the five latest logs.
- [x] Desktop and 390px mobile layouts render correctly.
- [x] `npm run lint` and `npm run build` pass.

## 7. Next steps

1. Statistics page: weekly comparison, streaks, category breakdown.
2. Tasks page: statuses, assignees, due dates, progress.
3. Auth pages and protected routes.
4. Replace the mock service with the real `/api/v1/logs` endpoints.
