# DevTogether --- Shared Contribution Dashboard

**Product requirements · Architecture · Database · API · Delivery
plan**\
**Version:** 1.0\
**Status:** Planning / ready for repository setup

## 1. Executive summary

DevTogether is a shared contribution-tracking dashboard for two
collaborators. It combines:

1.  **Manual daily work logs** recording what each person worked on.
2.  **GitHub activity** imported from a connected repository.

The dashboard presents individual GitHub-style contribution heatmaps,
daily logs, current and longest streaks, weekly comparisons, and
project-task progress.

The goal is to encourage consistent collaboration and maintain a
transparent record of work---not to rank developers solely by commit
count. Testing, code review, debugging, documentation, design, and
planning should be recordable even when they do not produce commits.

## 2. Goals and non-goals

### Goals

-   Give both collaborators a shared view of activity while keeping each
    person's contributions distinct.
-   Record what was done, when it was done, approximate time spent, and
    supporting links.
-   Show a GitHub-style calendar heatmap for each developer.
-   Calculate active days, current streak, longest streak, weekly
    totals, and task completion.
-   Import GitHub activity without duplicating events during repeated
    synchronization.
-   Deploy a usable first version at little or no cost using free tiers
    where suitable.

### Non-goals for the first version

-   A full project-management suite or replacement for GitHub Issues and
    Pull Requests.
-   A precise measurement of developer productivity or code quality.
-   Complex organization/team support beyond the two collaborators.
-   Redis, Kafka, Kubernetes, or microservices in the MVP. Add
    infrastructure only if there is a real need.

## 3. MVP features

  -----------------------------------------------------------------------
  Feature                 Description             Acceptance criteria
  ----------------------- ----------------------- -----------------------
  Authentication          Two named users can     Only authenticated
                          access the shared       users can create or
                          dashboard.              edit their own logs.

  Daily work log          Date, summary,          A user can create,
                          category, time spent,   view, edit, and delete
                          status, optional        their own log.
                          evidence URL.           

  Individual heatmaps     One calendar heatmap    Selecting a date shows
                          per developer, with a   the day's recorded work
                          shared view.            and imported activity.

  Streaks                 Current and longest     Rules are documented
                          consecutive active-day  and use the user's
                          streak.                 configured timezone.

  Weekly comparison       Compare active days,    Both users are shown
                          completed tasks, and    with the same date
                          logged hours.           range and definitions.

  Project progress        Tasks with assignee,    Counts and progress are
                          status, dates, and      derived from task
                          optional GitHub issue   records.
                          link.                   

  GitHub sync             Import supported        Sync is repeatable
                          commits, pull requests, without creating
                          reviews, and issue      duplicate events.
                          activity.               

  Responsive UI           Dashboard usable on     Core views work at
                          desktop and mobile.     common mobile and
                                                  desktop widths.
  -----------------------------------------------------------------------

## 4. Contribution rules

Define these rules before using the dashboard so the comparison remains
fair:

-   An **active day** is a calendar day with at least one qualifying
    contribution event or an approved manual work log.
-   A **streak** counts consecutive active calendar days in the user's
    configured timezone. Recommended default: a missed day resets the
    streak.
-   Do not add GitHub events and manual logs together as if they were
    separate units of work. Display activity counts and work-log counts
    separately.
-   A heatmap intensity score can be based on distinct qualifying
    activities or a capped daily score. Keep the formula visible and
    avoid presenting it as a productivity score.
-   A task is completed only when its status is set to `Done`; a commit
    alone should not automatically mark a task complete.
-   Manual entries should allow non-code work such as testing, design,
    planning, documentation, and review.

## 5. Suggested technology architecture

  -----------------------------------------------------------------------
  Layer                   Technology              Responsibilities
  ----------------------- ----------------------- -----------------------
  Frontend                React + TypeScript +    Dashboard, heatmaps,
                          Vite                    daily log form, task
                                                  list, charts, API
                                                  client.

  Charts                  Recharts or a small     Weekly comparison and
                          custom heatmap          project progress
                          component               visualizations.

  Backend                 Java + Spring Boot      Authentication,
                                                  validation, business
                                                  rules, API, GitHub
                                                  synchronization.

  Database                PostgreSQL              Users, logs, GitHub
                                                  events, projects,
                                                  tasks, sync status.

  Integration             GitHub REST API         Read repository
                                                  activity and link
                                                  evidence to the
                                                  relevant account.

  Local development       Docker Compose          Run PostgreSQL and
                                                  supporting services
                                                  consistently.

  Hosting                 Free static hosting +   Publicly reachable app;
                          free backend/database   check sleep, quota,
                          tiers if suitable       storage, and expiry
                                                  restrictions.
  -----------------------------------------------------------------------

**MVP architecture:** one React frontend, one Spring Boot backend, one
PostgreSQL database, and a GitHub API integration. Avoid Redis or
message queues until performance or asynchronous processing makes them
necessary.

## 6. High-level data flow

1.  A collaborator signs in and submits a daily work log.
2.  The frontend sends the entry to the Spring Boot API; the backend
    validates ownership and saves it to PostgreSQL.
3.  A scheduled or manually triggered sync requests GitHub repository
    activity using a server-side token.
4.  The backend normalizes events, identifies the author, stores the
    external event ID, and ignores duplicates.
5.  Dashboard endpoints aggregate manual logs and GitHub events by user
    and date.
6.  The frontend renders heatmaps, streaks, weekly comparisons, and
    project progress.

## 7. Data model

  ------------------------------------------------------------------------------
  Table                   Suggested fields        Notes
  ----------------------- ----------------------- ------------------------------
  `users`                 `id`, `display_name`,   Use a stable internal ID;
                          `github_username`,      GitHub username can change.
                          `timezone`,             
                          `created_at`            

  `daily_logs`            `id`, `user_id`,        Validate hours and keep an
                          `work_date`, `summary`, audit-friendly timestamp.
                          `category`,             
                          `hours_spent`,          
                          `status`,               
                          `evidence_url`,         
                          `created_at`,           
                          `updated_at`            

  `github_events`         `id`, `user_id`,        Add a unique constraint on
                          `external_id`,          provider/repository/external
                          `event_type`,           identifier as appropriate.
                          `event_url`,            
                          `occurred_at`, `title`, 
                          `repository`,           
                          `synced_at`             

  `projects`              `id`, `name`,           Supports the project whose
                          `repository_owner`,     activity is tracked.
                          `repository_name`,      
                          `created_at`            

  `project_tasks`         `id`, `project_id`,     Tasks can link to GitHub
                          `title`, `description`, Issues or PRs.
                          `assignee_id`,          
                          `status`, `start_date`, 
                          `due_date`, `issue_url` 

  `sync_runs`             `id`, `started_at`,     Useful for debugging and
                          `completed_at`,         retrying sync failures.
                          `status`,               
                          `cursor_or_since`,      
                          `error_summary`         
  ------------------------------------------------------------------------------

## 8. API plan

These are proposed contracts, not implemented routes. Keep the API
consistent and document the final version with Springdoc/OpenAPI.

  -----------------------------------------------------------------------------------
  Method + endpoint                               Purpose
  ----------------------------------------------- -----------------------------------
  `POST /api/auth/register`                       Create an account if
                                                  self-registration is enabled;
                                                  otherwise seed two users
                                                  administratively.

  `POST /api/auth/login`                          Authenticate and return a
                                                  session/token according to the
                                                  chosen auth design.

  `GET /api/users`                                List collaborators visible to the
                                                  project.

  `POST /api/logs`                                Create a daily work log.

  `GET /api/logs?from=YYYY-MM-DD&to=YYYY-MM-DD`   Fetch logs for a date range.

  `PATCH /api/logs/{id}`                          Update an owned work log.

  `DELETE /api/logs/{id}`                         Delete an owned work log.

  `GET /api/contributions/heatmap?year=YYYY`      Return per-user daily contribution
                                                  summaries.

  `GET /api/contributions/streaks`                Return current and longest streak
                                                  per user.

  `GET /api/contributions/weekly?from=&to=`       Return comparable weekly
                                                  statistics.

  `GET /api/tasks`                                List project tasks.

  `POST /api/github/sync`                         Trigger a sync for an authorized
                                                  project/admin.

  `GET /api/github/sync-status`                   Show last sync result and errors.
  -----------------------------------------------------------------------------------

## 9. GitHub integration and security

-   Start with a single repository configured by owner and repository
    name.
-   Use GitHub REST API endpoints for commits, pull requests, reviews,
    and issues as needed. Verify each endpoint's permissions and
    pagination behavior.
-   Use a fine-grained token or GitHub App with the minimum required
    permissions. Some public-repository reads may work without
    authentication.
-   Store secrets only in backend environment variables or the hosting
    provider's secret manager. Never put tokens in frontend code or
    commit them to Git.
-   Use HTTPS in production and validate incoming data. Restrict edits
    to a user's own work logs.
-   Deduplicate imported events using stable external identifiers.
    Rate-limit sync and handle API errors gracefully.
-   Do not infer that all work happened on the commit date alone; manual
    logs should cover work that GitHub does not represent.

Reference: [GitHub REST API
documentation](https://docs.github.com/en/rest).

## 10. Free-tier deployment strategy

Target a ₹0 deployment initially, but free-tier terms change. Before
choosing providers, check current limits, sleep behavior, database
retention/expiry, outbound bandwidth, build limits, and whether a
payment method is required.

  -----------------------------------------------------------------------
  Component               Possible approach       Important caveat
  ----------------------- ----------------------- -----------------------
  Frontend                GitHub Pages or a free  Static hosting cannot
                          static-hosting tier     run the Spring Boot
                                                  backend.

  Backend                 Free                    May sleep after
                          application-hosting     inactivity or have
                          tier where Java         build/runtime quotas.
                          services are supported  

  Database                Free managed PostgreSQL Some free databases
                          tier or a self-hosted   pause or expire; keep
                          local database during   backups.
                          development             

  Local development       Docker Compose          Recommended even if
                                                  production is hosted
                                                  elsewhere.
  -----------------------------------------------------------------------

Keep database backups and test restore procedures. Do not promise
permanent free hosting: provider policies and quotas can change. Local
development is sufficient for the first milestone; deploy after the core
workflow works.

## 11. Repository structure

``` text
devtogether/
├── frontend/                 # React + TypeScript + Vite
│   ├── src/
│   │   ├── components/
│   │   ├── features/
│   │   │   ├── heatmap/
│   │   │   ├── daily-logs/
│   │   │   ├── weekly-stats/
│   │   │   └── tasks/
│   │   ├── pages/
│   │   ├── services/          # API client
│   │   └── types/
│   └── package.json
├── backend/                  # Spring Boot
│   ├── src/main/java/.../
│   │   ├── auth/
│   │   ├── users/
│   │   ├── logs/
│   │   ├── contributions/
│   │   ├── github/
│   │   └── tasks/
│   └── pom.xml
├── infra/
│   └── docker-compose.yml
├── docs/
│   ├── project-documentation.md
│   └── api-notes.md
├── .env.example
├── .gitignore
└── README.md
```

## 12. Implementation roadmap

  -----------------------------------------------------------------------
  Milestone               Work                    Definition of done
  ----------------------- ----------------------- -----------------------
  M0 --- Repository       Create repository,      Both collaborators can
                          README, issues, branch  clone and run the
                          rules, `.gitignore` and starter project.
                          `.env.example`.         

  M1 --- Backend          Spring Boot,            API validation and
  foundation              PostgreSQL, migrations, basic tests pass.
                          user model, daily-log   
                          CRUD.                   

  M2 --- Frontend         React layout, routing,  Both users can add and
  foundation              API client, daily log   view logs.
                          form and list.          

  M3 --- Heatmaps and     Daily aggregation,      Metrics match
  metrics                 active-day rules,       documented rules for
                          streaks and weekly      test fixtures.
                          charts.                 

  M4 --- Tasks            Task CRUD, assignments, Dashboard shows total,
                          statuses, progress      completed, and
                          summary.                in-progress tasks.

  M5 --- GitHub sync      Token configuration,    Repeated syncs do not
                          pagination, event       inflate activity.
                          normalization,          
                          deduplication and sync  
                          status.                 

  M6 --- Security and     Auth, authorization,    Both collaborators can
  deployment              secrets, backups, CI    use it online and
                          checks, deployment.     restore a database
                                                  backup.
  -----------------------------------------------------------------------

## 13. Testing checklist

-   [ ] A user cannot edit or delete another user's daily log.
-   [ ] A repeated GitHub sync does not create duplicate events.
-   [ ] Events near midnight are assigned to the correct configured
    timezone date.
-   [ ] Current and longest streak calculations handle missed days and
    year boundaries.
-   [ ] A day with manual work but no GitHub activity still appears on
    the heatmap.
-   [ ] A GitHub event and a manual log on the same day do not
    accidentally double-count the same task.
-   [ ] Weekly comparisons use the same start/end dates and definitions
    for both users.
-   [ ] The UI has empty, loading, error, and mobile-responsive states.
-   [ ] Secrets are not present in the repository or frontend build.
-   [ ] Database backup and restore have been tested.

## 14. Suggested first-week plan for two developers

  -----------------------------------------------------------------------
  Day                     Developer A             Developer B
  ----------------------- ----------------------- -----------------------
  1                       Create repo, README,    Agree on feature scope,
                          project issues, branch  wireframe, contribution
                          conventions.            rules and data model.

  2                       Initialize Spring Boot  Initialize React +
                          and PostgreSQL          TypeScript + Vite and
                          configuration.          basic layout.

  3                       Implement daily-log     Build daily-log form
                          entity, repository and  and list using mock
                          service.                data/API contract.

  4                       Add CRUD endpoints and  Connect UI to API and
                          validation tests.       implement loading/error
                                                  states.

  5                       Add heatmap aggregation Render heatmap and date
                          endpoint and sample     detail panel.
                          fixtures.               

  6                       Add streak and          Add weekly comparison
                          weekly-stat service     chart and task summary
                          tests.                  UI.

  7                       Review PRs, fix bugs    Test end-to-end flow
                          and update              and prepare next-week
                          documentation.          issues.
  -----------------------------------------------------------------------

This is a suggested schedule, not a deadline. Adjust it around classes
and other commitments. Each developer should open focused PRs and review
the other's work.

## 15. Repository conventions

-   Use issues for tasks and small pull requests for changes.
-   Suggested branch names: `feat/daily-logs`, `feat/heatmap-api`,
    `feat/heatmap-ui`, `fix/streak-timezone`.
-   Suggested commit formats: `feat: add daily contribution log`,
    `fix: handle streak date boundary`, `docs: clarify activity rules`.
-   Keep `main` protected if practical, and require the other
    collaborator's review for meaningful changes.
-   Update README setup instructions whenever environment variables or
    commands change.

## 16. Suggested README opening

> DevTogether is a shared contribution-tracking dashboard for small
> developer teams. It combines manual work logs with GitHub activity to
> visualize consistency, daily contributions, streaks, weekly
> comparisons, and project progress. The goal is transparent
> collaboration---not measuring developer worth by commit volume.

## 17. References

-   [GitHub REST API documentation](https://docs.github.com/en/rest)
-   [Spring Boot
    documentation](https://docs.spring.io/spring-boot/index.html)
-   [React documentation](https://react.dev/)
-   [PostgreSQL documentation](https://www.postgresql.org/docs/)
-   [Docker Compose documentation](https://docs.docker.com/compose/)
-   [Recharts documentation](https://recharts.org/)
