# Registry

This is a private, unsecured [podman][Podman] registry for use in a Kubernetes cluster.

## Arguments

### Volume mounts

|---------------------|------------------------------------------|
| `/var/lib/registry` | Directory where Docker images are stored |

### Ports

|------|-------------------|
| 5000 | The registry port |


## Examples

### Build the image

`podman build -t podman-registry:latest .`

### Run a Podman registry

`podman run -p 5000:5000 -v /var/lib/registry:/var/lib/registry --privileged localhost/podman-registry:latest`

* The `--privileged` flag is mandatory

### Login to the registry

`podman login --tls-verify=false localhost:5000`

* The `--tls-verify` flag is mandatory

### Push an image to the registry

1. `podman pull docker.io/library/nginx:latest`
2. `podman tag docker.io/library/nginx:latest localhost:5000/nginx/latest`
3. `podman login --tls-verify=false localhost:5000`
4. `podman push --tls-verify=false localhost:5000/nginx/latest`


[podman]: https://podman.io/