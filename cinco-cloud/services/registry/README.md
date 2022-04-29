# Registry

This is a private, unsecured [podman][Podman] registry for use in a Kubernetes cluster and an API to call the garbage collection of the registry.

## Arguments

### Volume mounts

|---------------------|------------------------------------------|
| `/var/lib/registry` | Directory where Docker images are stored |

### Ports

|------|----------------------|
| 5000 | The registry port    |
| 8000 | The port for the API |

[podman]: https://podman.io/