# DevTogether — API Planning Notes

> **STATUS: PLANNED — NOT IMPLEMENTED**  
> **Notice:** This document defines the initial architectural proposals and planned endpoint contracts for the DevTogether backend. **None of the endpoints, controllers, DTOs, services, or database models listed below are currently implemented.** All schemas, exact payloads, and database mappings are subject to revision and marked as `TBD` where decisions are pending.

---

## 1. Architectural Overview & Conventions

### 1.1 Base Path
All backend REST API endpoints will be versioned under the base prefix:
```text
/api/v1
```

### 1.2 Protocol & Format
- **Protocol:** HTTP/1.1 and HTTPS
- **Payload Format:** `application/json` for both request bodies and responses
- **Character Encoding:** UTF-8
- **Date/Time Formatting:** ISO-8601 strings (e.g., `YYYY-MM-DD` for calendar dates and `YYYY-MM-DDTHH:mm:ssZ` for UTC timestamps)

---

## 2. Authentication & Authorization Considerations

### 2.1 Authentication Mechanism (`TBD`)
The authentication architecture is currently under evaluation:
- **Proposal A (Stateless JWT):** Clients authenticate via `/api/v1/auth/login` and receive a signed JSON Web Token (JWT). The token is transmitted in the standard HTTP header:
  ```http
  Authorization: Bearer <jwt_token>
  ```
- **Proposal B (Session Cookie):** Standard HTTP-only, secure session cookie managed by Spring Security.
- **Evaluation Status:** `TBD` (Team to decide based on deployment platform and CORS considerations).

### 2.2 Access Control & Ownership Enforcement
- **Authentication Required:** All endpoints outside of public authentication (`/api/v1/auth/login`, `/api/v1/auth/register`) will require a valid authenticated user context.
- **Ownership Verification:**
  - Users may only create, modify (`PATCH`), or delete (`DELETE`) daily work logs authored by themselves.
  - Attempting to modify another collaborator's log must result in an `HTTP 403 Forbidden` response.
- **Collaborator Visibility:**
  - Authenticated project collaborators have read access (`GET`) to view each other's daily logs, contribution heatmaps, streaks, and project tasks.

---

## 3. Proposed Endpoint Groups

### 3.1 Authentication (`/api/v1/auth`)
Handles user credential verification, session/token lifecycle, and registration.

| Method | Proposed Endpoint | Status | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/register` | `PLANNED — NOT IMPLEMENTED` | Register a new collaborator account (if self-registration is enabled). |
| `POST` | `/api/v1/auth/login` | `PLANNED — NOT IMPLEMENTED` | Validate credentials and return an authentication token or establish session. |
| `POST` | `/api/v1/auth/logout` | `PLANNED — NOT IMPLEMENTED` | Invalidate the current session or revoke token. |
| `GET` | `/api/v1/auth/me` | `PLANNED — NOT IMPLEMENTED` | Return profile details and permissions of the currently authenticated user. |

*Request / Response Schemas: `TBD`*

---

### 3.2 User Management (`/api/v1/users`)
Provides endpoints for inspecting collaborator profiles and maintaining user preferences.

| Method | Proposed Endpoint | Status | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/users` | `PLANNED — NOT IMPLEMENTED` | List project collaborators and their public profile information. |
| `GET` | `/api/v1/users/{id}` | `PLANNED — NOT IMPLEMENTED` | Retrieve profile metadata for a specific collaborator. |
| `PATCH` | `/api/v1/users/{id}` | `PLANNED — NOT IMPLEMENTED` | Update user settings (display name, GitHub username, timezone). Requires ownership. |

*Notes:*
- Timezone configuration (e.g., `Asia/Kolkata`, `America/New_York`) is essential for calculating active days and streak continuity near midnight boundaries.
*Request / Response Schemas: `TBD`*

---

### 3.3 Daily Work Logs (`/api/v1/logs`)
Allows collaborators to submit, view, modify, and delete daily work activity.

| Method | Proposed Endpoint | Status | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/logs` | `PLANNED — NOT IMPLEMENTED` | Create a new manual work log entry for the authenticated user. |
| `GET` | `/api/v1/logs` | `PLANNED — NOT IMPLEMENTED` | Query work logs with optional query filters (`userId`, `from`, `to`, `category`, `page`, `size`). |
| `GET` | `/api/v1/logs/{id}` | `PLANNED — NOT IMPLEMENTED` | Fetch details of a single work log by its ID. |
| `PATCH` | `/api/v1/logs/{id}` | `PLANNED — NOT IMPLEMENTED` | Update fields in an existing work log. Restricted to log owner. |
| `DELETE`| `/api/v1/logs/{id}` | `PLANNED — NOT IMPLEMENTED` | Delete an existing work log. Restricted to log owner. |

*Proposed Log Fields (Subject to final JPA Entity design):*
- `id`: Unique identifier (`TBD: UUID vs BIGSERIAL`)
- `userId`: ID of the authoring collaborator
- `workDate`: Date of activity (`YYYY-MM-DD`)
- `summary`: Short text description of work done
- `category`: Work classification (`Development`, `Bug Fix`, `Code Review`, `Testing`, `Documentation`, `Design`, `Planning`)
- `hoursSpent`: Numerical hours spent (e.g., `0.5` to `16.0`)
- `status`: Completion state (`Completed`, `In Progress`, `Blocked`)
- `evidenceUrl`: Optional URL link to PR, ticket, document, or commit
*Request / Response Schemas: `TBD`*

---

### 3.4 Contributions & Heatmap (`/api/v1/contributions`)
Aggregates activity data for calendar heatmaps and streak metrics.

| Method | Proposed Endpoint | Status | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/contributions/heatmap` | `PLANNED — NOT IMPLEMENTED` | Retrieve daily contribution counts/intensities per user over a given year (`?year=YYYY`) or range. |
| `GET` | `/api/v1/contributions/streaks` | `PLANNED — NOT IMPLEMENTED` | Calculate and return current streak, longest streak, and active day counts per collaborator. |
| `GET` | `/api/v1/contributions/summary` | `PLANNED — NOT IMPLEMENTED` | High-level summary of total logs, total hours, and imported GitHub events. |

*Design Notes:*
- Activity intensities should account for both manual logs and GitHub events without conflating them into a single misleading "productivity score".
- Calculations evaluate midnight boundaries in each collaborator's configured timezone.
*Request / Response Schemas: `TBD`*

---

### 3.5 Weekly Statistics (`/api/v1/contributions/weekly`)
Provides comparative statistics between the two developers across synchronized weekly windows.

| Method | Proposed Endpoint | Status | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/contributions/weekly` | `PLANNED — NOT IMPLEMENTED` | Retrieve side-by-side weekly statistics (active days, logged hours, completed tasks) for a date range (`?from=YYYY-MM-DD&to=YYYY-MM-DD`). |

*Request / Response Schemas: `TBD`*

---

### 3.6 Tasks Management (`/api/v1/tasks`)
Enables lightweight tracking of project tasks.

| Method | Proposed Endpoint | Status | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/tasks` | `PLANNED — NOT IMPLEMENTED` | Create a new project task. |
| `GET` | `/api/v1/tasks` | `PLANNED — NOT IMPLEMENTED` | Retrieve tasks with optional filters (`status`, `assigneeId`, `projectId`). |
| `GET` | `/api/v1/tasks/{id}` | `PLANNED — NOT IMPLEMENTED` | Retrieve details for a specific task. |
| `PATCH` | `/api/v1/tasks/{id}` | `PLANNED — NOT IMPLEMENTED` | Update task properties, assignees, or status (`Todo`, `In Progress`, `Done`). |
| `DELETE`| `/api/v1/tasks/{id}` | `PLANNED — NOT IMPLEMENTED` | Delete a project task. |

*Design Notes:*
- A task is considered completed solely when its status transitions to `Done`.
- Tasks can optionally store an `issueUrl` linking to GitHub Issues or Pull Requests.
*Request / Response Schemas: `TBD`*

---

### 3.7 GitHub Integration (`/api/v1/github`)
Controls repository synchronization and inspects external event ingestion.

| Method | Proposed Endpoint | Status | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/github/sync` | `PLANNED — NOT IMPLEMENTED` | Trigger an on-demand GitHub synchronization run for the configured repository. |
| `GET` | `/api/v1/github/sync-status` | `PLANNED — NOT IMPLEMENTED` | Retrieve the execution status, timestamp, and error log of recent synchronization runs. |
| `GET` | `/api/v1/github/events` | `PLANNED — NOT IMPLEMENTED` | Retrieve imported GitHub events (commits, PRs, reviews, issues) with pagination and filtering. |

*Design Notes:*
- Synchronization requests must handle GitHub REST API rate limits and leverage pagination.
- Events must be uniquely deduplicated using the GitHub event identifier (`external_id`).
*Request / Response Schemas: `TBD`*

---

## 4. Cross-Cutting Design Considerations

### 4.1 Request Validation
All mutating requests (`POST`, `PATCH`, `PUT`) will be validated using standard Jakarta Bean Validation constraints (`jakarta.validation.constraints`):
- Non-blank strings: `@NotBlank`, `@Size(min = ..., max = ...)`
- Numeric hours: `@NotNull`, `@DecimalMin("0.25")`, `@DecimalMax("24.0")`
- Valid dates: `@NotNull`, `@PastOrPresent`
- Enumerations: `@Pattern` or custom validators for categories and statuses
- When validation fails, the API will return `HTTP 400 Bad Request` with field-level error messages.

### 4.2 Standardized Error Responses
Errors will return a structured JSON response following RFC 7807 (Problem Details) or a standard error payload:

```json
{
  "timestamp": "2026-10-31T18:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for request parameters",
  "path": "/api/v1/logs",
  "validationErrors": [
    {
      "field": "hoursSpent",
      "message": "Hours spent must be greater than or equal to 0.25"
    }
  ]
}
```

Standard HTTP status code mappings:
- `200 OK`: Request succeeded.
- `201 Created`: Resource successfully created.
- `204 No Content`: Resource successfully deleted.
- `400 Bad Request`: Validation failure or malformed payload.
- `401 Unauthorized`: Authentication missing or token expired.
- `403 Forbidden`: Authenticated user lacks permission to access or modify resource.
- `404 Not Found`: Target resource does not exist.
- `409 Conflict`: Resource state conflict (e.g., duplicate unique constraint).
- `500 Internal Server Error`: Unhandled server error.

### 4.3 Pagination and Sorting
Collections (such as `/api/v1/logs` and `/api/v1/github/events`) will use Spring Data pagination query conventions:
- `page`: Zero-indexed page number (default: `0`).
- `size`: Number of records per page (default: `20`, maximum: `100`).
- `sort`: Field and direction (e.g., `sort=workDate,desc`).

Proposed paginated response wrapper (`TBD`):
```json
{
  "content": [],
  "pageNumber": 0,
  "pageSize": 20,
  "totalElements": 42,
  "totalPages": 3,
  "last": false
}
```

---

## 5. Pending Technical Decisions (`TBD`)

The following technical decisions require team alignment before implementation:
1. **Database Migration Tool:** Flyway vs. Liquibase for managing PostgreSQL schema migrations.
2. **Entity Primary Key Strategy:** `UUID` vs `BIGSERIAL` (autoincrementing 64-bit integer).
3. **Session vs. Token Security:** Stateless JWT tokens stored in localStorage/cookie vs HTTP-only server sessions.
4. **Soft Deletes:** Whether daily logs and tasks should support soft deletion (`deleted_at` timestamp) or physical row removal.
5. **Scheduled Sync Cadence:** Frequency of automated background GitHub sync (e.g., hourly cron vs on-demand only).

