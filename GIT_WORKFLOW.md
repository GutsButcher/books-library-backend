# Git Workflow Best Practices

## Branch Structure

- **main**: Production-ready code (protected branch)
- **staging**: Pre-production testing environment
- **dev**: Development integration branch
- **feature/**: Feature branches created from dev

## Workflow Steps

### 1. Create Feature Branch
```bash
# Always create feature branches from dev
git checkout dev
git pull origin dev
git checkout -b feature/your-feature-name
```

### 2. Develop and Test
```bash
# Make your changes
# Run tests before committing
mvn test

# Stage and commit changes
git add -A
git commit -m "type: brief description

- Detailed change 1
- Detailed change 2"
```

### 3. Push Feature Branch
```bash
git push -u origin feature/your-feature-name
```

### 4. Create Pull Request
- Go to GitHub and create a PR from your feature branch to `dev`
- Include in PR description:
  - Summary of changes
  - Test results
  - Type of change (bug fix, feature, etc.)
  - Checklist of completed items

### 5. Code Review and Merge
- Wait for code review approval
- Ensure all CI/CD checks pass
- Merge to dev using GitHub's merge button

### 6. Deployment Flow
```
feature/* → dev → staging → main
```

## Commit Message Convention

Use conventional commits format:
- `feat:` New feature
- `fix:` Bug fix
- `docs:` Documentation changes
- `style:` Code style changes (formatting, etc.)
- `refactor:` Code refactoring
- `test:` Adding or updating tests
- `chore:` Maintenance tasks

## Testing Requirements

Before creating a PR:
1. All unit tests must pass: `mvn test`
2. New features must include tests
3. Integration tests should be updated if needed

## Example Feature Implementation

See the `feature/add-book-validation` branch for an example of:
- Creating a feature branch from dev
- Adding new functionality (ISBN validation)
- Writing comprehensive tests
- Following commit conventions
- Creating a descriptive PR

## Protected Branch Rules (Recommended)

Configure in GitHub repository settings:
- Protect `main` and `staging` branches
- Require PR reviews before merging
- Require status checks to pass (CI/CD)
- Dismiss stale PR approvals when new commits are pushed
- Include administrators in restrictions