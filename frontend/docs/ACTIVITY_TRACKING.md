# DevTogether — Activity Tracking

## Objective

Track meaningful developer activity, including coding, learning, research, testing and collaboration.

A developer's contribution is not limited to GitHub commits.

## Activity Categories

| Category | Examples |
|---|---|
| Development | Building application features |
| Learning | Studying React, Java, DSA |
| Research | Exploring technologies and solutions |
| Bug Fix | Debugging application issues |
| Documentation | Writing technical documentation |
| Testing | Testing application functionality |
| Code Review | Reviewing pull requests |
| Planning | Designing architecture and planning features |

## Activity Record Structure

Each manual activity contains:

- `id`: Unique identifier
- `title`: Activity title
- `category`: Activity category
- `date`: Activity date
- `hours`: Time invested
- `status`: Completion status
- `description`: Additional details
- `evidenceUrl`: Optional supporting link
- `source`: Origin of the activity

## Activity Sources

### Manual activity

Entered by a developer.

Examples:
- Learning React hooks
- Researching an API
- Implementing a feature
- Reviewing code

### GitHub activity

Imported from GitHub.

Examples:
- Commits
- Pull requests
- Code reviews

## Double-counting prevention

Manual logs and imported GitHub events remain separate records.

The system should not automatically count a GitHub event as another manual contribution.

Future integration can associate related records using repository, commit or pull request references.

## Dashboard Integration

Activities will contribute to:
- Contribution heatmap
- Recent Work & Learning
- Total hours
- Weekly activity
- Category-based statistics
- Individual activity history

## Implementation Progress

- [x] Define activity categories
- [x] Define activity record structure
- [x] Create sample manual activity records
- [ ] Build Recent Work & Learning component
- [ ] Add category-based filtering
- [ ] Integrate backend API
- [ ] Integrate GitHub activity
- [ ] Implement duplicate detection and linking rules
