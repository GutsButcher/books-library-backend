# CI/CD Pipeline Guide

## Overview

This project uses a comprehensive CI/CD pipeline with GitHub Actions, Docker, and Helm for Kubernetes deployments.

## Pipeline Architecture

```
Feature Branch → Dev → Staging → Production
```

### Image Tagging Strategy

- **Feature branches**: `pr-{number}-{sha}`
- **Dev branch**: `dev-{date}-{sha}`
- **Staging branch**: `staging-{date}-{sha}`
- **Production**: `staging-{date}-{sha}` (promoted from staging)

## Workflows

### 1. CI Pipeline (`ci.yml`)
Triggers on all pushes and PRs to dev, staging, and main branches.

**Steps:**
- Run tests with Maven
- Build application JAR
- Build and push Docker image
- Run security scanning with Trivy
- Generate SBOM

### 2. PR Preview Deployments (`pr-deploy.yml`)
Creates ephemeral environments for pull requests.

**Features:**
- Automatic deployment to `book-library-pr-{number}` namespace
- Cleanup on PR close
- Comments on PR with deployment URL

### 3. Environment Deployments
- **Dev** (`cd-dev.yml`): Automatic deployment on push to dev
- **Staging** (`cd-staging.yml`): Automatic deployment on push to staging
- **Production** (`cd-prod.yml`): Manual or automatic deployment from main

### 4. Image Promotion (`promote.yml`)
Manual workflow to promote tested images between environments.

## Helm Chart Structure

```
helm/book-library/
├── Chart.yaml              # Chart metadata
├── values.yaml            # Default values
├── values-dev.yaml        # Dev environment overrides
├── values-staging.yaml    # Staging environment overrides
├── values-prod.yaml       # Production environment overrides
└── templates/
    ├── deployment.yaml    # Kubernetes deployment
    ├── service.yaml       # Kubernetes service
    ├── ingress.yaml       # Ingress configuration
    ├── configmap.yaml     # Application configuration
    ├── hpa.yaml          # Horizontal Pod Autoscaler
    └── secret.yaml       # Secrets management
```

## Required Secrets

Configure these in GitHub repository settings:

### Docker Registry
- `DOCKER_USERNAME`: Docker Hub username
- `DOCKER_PASSWORD`: Docker Hub password/token

### Kubernetes Access
- `KUBECONFIG_DEV`: Base64-encoded kubeconfig for dev cluster
- `KUBECONFIG_STAGING`: Base64-encoded kubeconfig for staging cluster
- `KUBECONFIG_PROD`: Base64-encoded kubeconfig for prod cluster (for self-hosted runner)

### Notifications
- `SLACK_WEBHOOK`: Slack webhook URL for deployment notifications

## Deployment Commands

### Manual Deployment
```bash
# Deploy to dev
helm upgrade --install book-library-dev ./helm/book-library \
  --namespace book-library-dev \
  --values ./helm/book-library/values.yaml \
  --values ./helm/book-library/values-dev.yaml \
  --set image.tag=dev-20240101-abc123

# Deploy to staging
helm upgrade --install book-library-staging ./helm/book-library \
  --namespace book-library-staging \
  --values ./helm/book-library/values.yaml \
  --values ./helm/book-library/values-staging.yaml \
  --set image.tag=staging-20240101-def456

# Deploy to production
helm upgrade --install book-library ./helm/book-library \
  --namespace book-library-prod \
  --values ./helm/book-library/values.yaml \
  --values ./helm/book-library/values-prod.yaml \
  --set image.tag=staging-20240101-def456
```

### Rollback
```bash
# List releases
helm list -n book-library-prod

# Rollback to previous version
helm rollback book-library -n book-library-prod

# Rollback to specific revision
helm rollback book-library 3 -n book-library-prod
```

## Best Practices Implemented

1. **Immutable Image Tags**: Each build creates a unique tag with environment, date, and commit SHA
2. **Environment Progression**: Images must be tested in lower environments before promotion
3. **Atomic Deployments**: Using Helm's `--atomic` flag for automatic rollback on failure
4. **Health Checks**: Configured liveness and readiness probes
5. **Resource Limits**: Defined CPU and memory limits for all environments
6. **Horizontal Scaling**: HPA configured for staging and production
7. **Security Scanning**: Automated vulnerability scanning with Trivy
8. **SBOM Generation**: Software Bill of Materials for compliance
9. **Graceful Shutdown**: Configured for zero-downtime deployments

## Monitoring

The application exposes metrics at:
- `/actuator/health` - Health status
- `/actuator/health/liveness` - Liveness probe endpoint
- `/actuator/health/readiness` - Readiness probe endpoint
- `/actuator/prometheus` - Prometheus metrics

## Troubleshooting

### Check deployment status
```bash
kubectl get pods -n book-library-dev
kubectl describe pod <pod-name> -n book-library-dev
kubectl logs <pod-name> -n book-library-dev
```

### View Helm release
```bash
helm status book-library-dev -n book-library-dev
helm get values book-library-dev -n book-library-dev
```

### Debug deployment
```bash
helm install book-library-dev ./helm/book-library \
  --dry-run --debug \
  --namespace book-library-dev \
  --values ./helm/book-library/values.yaml \
  --values ./helm/book-library/values-dev.yaml
```