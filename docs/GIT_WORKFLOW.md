# DevTogether — Git Collaboration Workflow

## 1. Branch Strategy

We follow **Git Flow** with GitHub Pull Requests for collaboration.

| Branch | Purpose |
|---|---|
| `main` | Stable, production-ready code |
| `develop` | Integration branch for completed features |
| `feature/*` | New features and improvements |
| `fix/*` | Bug fixes |
| `release/*` | Preparing a release |
| `hotfix/*` | Urgent fixes to production |

**Golden rule:** Never work directly on `main` or `develop`.

## 2. Team Collaboration

Both developers are collaborators with **Write access** to the repository.

Either developer can:
- Create feature branches.
- Push their own branches.
- Open Pull Requests.
- Review and comment on the other developer's PRs.
- Approve and merge the other developer's PRs.

**A developer should not approve their own PR.** The other collaborator should review and approve it.

### Example collaboration

**Scenario A — I work on a feature:**
1. I create `feature/dashboard-heatmap` from `develop`.
2. I implement the feature and push my branch.
3. I open a PR targeting `develop`.
4. My friend reviews the code.
5. My friend approves and merges the PR.

**Scenario B — My friend works on a feature:**
1. My friend creates `feature/backend-auth` from `develop`.
2. They implement the feature and push their branch.
3. They open a PR targeting `develop`.
4. I review the code.
5. I approve and merge the PR.

This gives both developers experience with development, code review, and integration.

## 3. Starting a New Feature

Always start from the latest `develop` branch.

```bash
git switch develop
git pull origin develop
git switch -c feature/feature-name
```

Examples:

```bash
git switch -c feature/dashboard-heatmap
git switch -c feature/user-authentication
git switch -c feature/github-integration
```

## 4. Working on Your Feature

After making changes, inspect your work:

```bash
git status
git diff
```

Stage only the files related to your feature:

```bash
git add <file-name>
```

Review what is staged:

```bash
git diff --cached
```

Commit using Conventional Commits:

```bash
git commit -m "feat(frontend): add contribution heatmap"
```

Push your feature branch:

```bash
git push -u origin feature/feature-name
```

## 5. Pull Request Process

On GitHub:

1. Open a Pull Request.
2. Set **base** to `develop`.
3. Set **compare** to your feature branch.
4. Write a meaningful title and description.
5. Assign the other developer as reviewer.
6. Wait for review and approval.
7. Address requested changes if necessary.
8. The reviewer approves and merges the PR.

### PR title examples

```text
feat(frontend): add contribution heatmap
feat(backend): implement user registration
fix(auth): resolve token validation issue
docs(git): add collaboration workflow
```

### PR description template

```markdown
## What does this PR do?
Briefly describe the changes.

## Why is this change needed?
Explain the purpose.

## Changes made
- Change 1
- Change 2

## Testing
Explain how the changes were tested.

## Screenshots
Add screenshots if applicable.
```

## 6. After a PR Is Merged

Once the PR is merged into `develop`, update your local branch:

```bash
git switch develop
git pull origin develop
```

Delete your completed local feature branch:

```bash
git branch -d feature/feature-name
```

If GitHub has not automatically deleted the remote branch:

```bash
git push origin --delete feature/feature-name
```

Then create your next feature branch from the updated `develop`.

## 7. Branch Protection Rules

Configure branch protection for both `main` and `develop` in GitHub:

**Repository → Settings → Branches → Add branch protection rule**

Recommended settings:

- Require a Pull Request before merging.
- Require at least 1 approval.
- Require conversation resolution before merging.
- Disable force pushes.
- Disable branch deletion.
- Prevent administrators from bypassing these rules, if the option is available.

For a two-person team, one approval means the other developer must review the PR.

*Note: Branch protection feature availability depends on repository visibility and GitHub plan.*

## 8. Commit Convention

Follow Conventional Commits:

```text
<type>(<scope>): <description>
```

| Type | Usage |
|---|---|
| `feat` | New feature |
| `fix` | Bug fix |
| `docs` | Documentation |
| `refactor` | Code restructuring |
| `test` | Tests |
| `build` | Build system or dependencies |
| `chore` | Maintenance |
| `perf` | Performance improvement |

Examples:

```bash
git commit -m "feat(frontend): add dashboard navigation"
git commit -m "fix(backend): handle invalid login credentials"
git commit -m "docs(git): document pull request workflow"
```

## 9. Important Team Rules

1. Never commit directly to `main` or `develop`.
2. Every feature must have its own branch.
3. Every feature branch must be created from the latest `develop`.
4. All feature PRs must target `develop`.
5. The other developer reviews and approves the PR.
6. Do not merge your own PR without the required review.
7. Resolve review comments before merging.
8. Never force-push shared branches.
9. Do not commit `.env`, credentials, API keys, or other secrets.
10. Keep PRs focused and reasonably small.

## 10. Release Workflow

When `develop` is stable and ready for a release:

```bash
git switch develop
git pull origin develop
git switch -c release/v1.0.0
```

Test the release and make any final corrections.

Then open PRs:
- `release/v1.0.0` → `main`
- `release/v1.0.0` → `develop`

After merging into `main`, create a version tag:

```bash
git switch main
git pull origin main
git tag -a v1.0.0 -m "Release v1.0.0"
git push origin v1.0.0
```

## 11. Quick Reference

**Starting work:**

```bash
git switch develop
git pull origin develop
git switch -c feature/my-feature
```

**Saving and publishing work:**

```bash
git status
git add <files>
git diff --cached
git commit -m "feat(scope): description"
git push -u origin feature/my-feature
```

**After the PR is merged:**

```bash
git switch develop
git pull origin develop
git branch -d feature/my-feature
```

---

**Our collaboration principle:**

We are not just sharing code. We are practising a real software development workflow: independent feature development, peer review, controlled integration, and stable releases.
