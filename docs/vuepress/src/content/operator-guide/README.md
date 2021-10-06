# Operator Guide

## Server Setup

## Deployment

1. Create a Kubernetes context for the remote server (ls5vs024.cs.tu-dortmund.de) and name it `ls5vs024-context`
2. Execute `skaffold deploy -t latest --kube-context ls5vs024-context -n default -p ls5vs024 --status-check=true` from the root of the repository
