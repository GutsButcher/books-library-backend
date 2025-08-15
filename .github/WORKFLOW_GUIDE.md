# GitHub Actions Workflow Guide for Beginners

## 📁 Directory Structure Overview

```
.github/
├── actions/                    # Reusable components (like functions)
│   ├── setup-java-maven/      # Sets up Java environment
│   ├── docker-build-push/     # Builds and pushes Docker images
│   └── helm-deploy/           # Deploys to Kubernetes
└── workflows/                  # Main automation scripts
    ├── ci.yml                 # Runs tests and builds
    ├── cd-dev.yml            # Deploys to development
    ├── cd-staging.yml        # Deploys to staging
    ├── cd-prod.yml           # Deploys to production
    ├── pr-deploy.yml         # Creates preview for PRs
    └── promote.yml           # Promotes images between environments
```

## 🎯 Understanding the Separation

### Why Two Folders?

1. **`actions/`** = Reusable building blocks (like LEGO pieces)
2. **`workflows/`** = Complete automation flows (like LEGO instructions)

Think of it like cooking:
- **Actions** = Basic techniques (chopping, frying, boiling)
- **Workflows** = Complete recipes that use those techniques

## 📝 File Naming Convention

### Actions (Composite Actions)
- Named by what they do: `setup-java-maven`, `docker-build-push`
- Each action has its own folder with `action.yml` inside
- Lowercase with hyphens for readability

### Workflows
- Named by their purpose:
  - `ci.yml` = Continuous Integration (testing)
  - `cd-*.yml` = Continuous Deployment (deploying)
  - `pr-*.yml` = Pull Request related
  - `promote.yml` = Special operation (promotion)

## 🔧 Composite Actions Explained

### 1. setup-java-maven (`actions/setup-java-maven/action.yml`)

**What it does:** Prepares Java environment for building our app

**Why separate:** Used by multiple workflows, so we don't repeat code

**Key steps:**
```yaml
1. Install Java 17
2. Cache Maven dependencies (speeds up builds)
```

**When it's used:** 
- In CI pipeline before running tests
- In PR deployments before building

### 2. docker-build-push (`actions/docker-build-push/action.yml`)

**What it does:** Creates Docker images and uploads them to Docker Hub

**Why separate:** Complex logic for tagging images properly

**Key steps:**
```yaml
1. Set up Docker tools
2. Log in to Docker Hub
3. Generate smart tags (dev-20240101-abc123)
4. Build image for multiple platforms
5. Push to registry
```

**Special features:**
- Creates unique tags with date and commit ID
- Builds for both Intel and ARM processors
- Outputs the image tag for other steps to use

### 3. helm-deploy (`actions/helm-deploy/action.yml`)

**What it does:** Deploys our app to Kubernetes using Helm

**Why separate:** Deployment logic is complex and environment-specific

**Key steps:**
```yaml
1. Install Helm tool
2. Set up Kubernetes access
3. Add chart dependencies (PostgreSQL)
4. Deploy with environment-specific values
5. Check deployment status
```

## 📋 Main Workflows Explained

### 1. CI Pipeline (`workflows/ci.yml`)

**Triggers:** Every push and pull request to dev, staging, or main

**Purpose:** Test and build our application

**Flow:**
```
Code Push → Run Tests → Build JAR → Build Docker Image → Security Scan
```

**Key Jobs:**
1. **test** - Runs all unit tests
2. **build** - Creates the application JAR file
3. **docker** - Builds and pushes Docker image
4. **security-scan** - Checks for vulnerabilities

### 2. Development Deployment (`workflows/cd-dev.yml`)

**Triggers:** Push to `dev` branch

**Purpose:** Auto-deploy to development environment

**Flow:**
```
Dev Push → Get Image Tag → Deploy to Dev Kubernetes → Notify Team
```

**Key Features:**
- Automatic deployment
- Creates deployment records
- Sends Slack notifications

### 3. Staging Deployment (`workflows/cd-staging.yml`)

**Triggers:** Push to `staging` branch

**Purpose:** Deploy to staging for testing

**Flow:**
```
Staging Push → Dry Run → Deploy → Run Smoke Tests → Notify
```

**Special Features:**
- Does a dry run first (practice deployment)
- Runs smoke tests after deployment
- More thorough than dev deployment

### 4. Production Deployment (`workflows/cd-prod.yml`)

**Triggers:** Push to `main` branch OR manual trigger

**Purpose:** Deploy to live production

**Flow:**
```
Validate → Backup → Deploy → Verify → Notify (Rollback if failed)
```

**Special Features:**
- Runs on self-hosted runner (your WSL)
- Creates backups before deployment
- Automatic rollback if deployment fails
- Requires staging-tested images

### 5. PR Preview (`workflows/pr-deploy.yml`)

**Triggers:** When PR is opened or updated

**Purpose:** Create temporary environment for testing PRs

**Flow:**
```
PR Created → Build → Deploy to Temp Namespace → Comment on PR
PR Closed → Clean up deployment
```

**Cool Features:**
- Each PR gets its own environment
- Automatically cleaned up when PR closes
- Comments deployment URL on the PR

### 6. Image Promotion (`workflows/promote.yml`)

**Triggers:** Manual only

**Purpose:** Promote tested images between environments

**Flow:**
```
Choose Source/Target → Validate Path → Copy & Retag Image → Trigger Deployment
```

**Promotion Rules:**
- dev → staging only
- staging → production only
- Creates audit trail

## 🏷️ Image Tagging Strategy

Our images are tagged like: `environment-date-commit`

Examples:
- `dev-20240115-abc123def`
- `staging-20240115-def456ghi`
- `pr-42-ghi789jkl`

**Why this matters:**
- Know exactly which code is deployed
- Can trace back to specific commit
- Never overwrite existing images

## 🔄 The Complete Flow

```
1. Developer creates feature branch
2. Opens PR → PR Preview deploys
3. Merges to dev → Auto-deploy to dev
4. Dev tested → Merge to staging → Auto-deploy to staging
5. Staging tested → Promote image → Deploy to production
```

## 💡 Key Concepts for Beginners

### Environment Progression
```
Development (dev) → Staging → Production (prod)
```
- **Dev**: Where developers test new features
- **Staging**: Production-like environment for final testing
- **Production**: The live application users see

### Why Separate Environments?
- Test changes safely
- Catch bugs before they reach users
- Easy rollback if something breaks

### Self-Hosted Runner
- Production uses your WSL machine instead of GitHub's servers
- Why? Direct access to your production Kubernetes cluster
- Configured with label: `wsl`

## 🛠️ Required Secrets

These are like passwords stored safely in GitHub:

1. **Docker Registry Access**
   - `DOCKER_USERNAME`: Your Docker Hub username
   - `DOCKER_PASSWORD`: Your Docker Hub password

2. **Kubernetes Access**
   - `KUBECONFIG_DEV`: Access to dev cluster
   - `KUBECONFIG_STAGING`: Access to staging cluster
   - `KUBECONFIG_PROD`: Access to production cluster

3. **Notifications**
   - `SLACK_WEBHOOK`: For sending deployment notifications

## 📚 Common Scenarios

### "I want to deploy my feature"
1. Create feature branch
2. Push code → CI runs automatically
3. Create PR → Preview deploys automatically
4. Merge to dev → Deploys to dev automatically

### "I want to deploy to production"
1. Ensure code is in staging and tested
2. Go to Actions → Promote workflow
3. Select staging → prod
4. Enter the staging image tag
5. Click "Run workflow"

### "Something went wrong in production"
- Don't panic! The workflow automatically rolls back
- Check the Actions tab for error details
- Fix the issue and redeploy

## 🎓 Tips for Beginners

1. **Start with dev deployments** - Less risky to experiment
2. **Always check CI status** - Don't merge if tests fail
3. **Use PR previews** - Test before merging
4. **Follow the progression** - Don't skip environments
5. **Read the logs** - GitHub Actions shows detailed logs

## 🚀 Getting Started

1. Fork the repository
2. Set up your secrets in Settings → Secrets
3. Create a feature branch
4. Make a small change
5. Push and watch the magic happen!

Remember: These workflows are like smart robots that do repetitive tasks for you. Once set up, they save hours of manual work!