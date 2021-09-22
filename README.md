# Cinco Cloud

## Development

### Preparations

1. Install the following software
    - [Docker][docker]
    - [Helm][helm]
    - [Skaffold][skaffold]
    - [Minikube][minikube]

2. Run a local Kubernetes cluster
    1. Start the cluster: `minikube start -cpus 4 --memory 8192` (CincoCloud works best with 4 CPU cores and 8Gb of RAM)
    2. Enable necessary plugins: `minikube addons enable default-storageclass ingress ingress-dns storage-provisioner`

3. Add the minikube IP address to the `/etc/hosts` file
    1. Execute `minikube ip` to retrieve the IP address
    2. Add an entry to the `/etc/hosts`: `<IP> cinco-cloud`

3. Create and apply a secret for the GitLab registry
    1. Create a secret: `kubectl create secret docker-registry cinco-cloud-registry-secret --docker-server=registry.gitlab.com --docker-username=<USERNAME> --docker-password=<USERNAME> --dry-run=client -o yaml`
    2. Copy the terminal output in a file called `secret.yaml`
    3. Apply the secret to the cluster: `kubectl apply -f secret.yaml`

### Installation

1. Clone the repository and ensure that the cluster is running
2. In the root of the repository directory, execute `skaffold dev` and wait for all pods to be deployed
3. Open `http://cinco-cloud` in a Web browser

## Deployment

1. Create a context for the remote server (ls5vs024.cs.tu-dortmund.de) and name it `ls5vs024-context`
2. Execute `skaffold deploy -t latest --kube-context ls5vs024-context -n default -p ls5vs024 --status-check=true` from the root of the repository


[helm]: https://helm.sh/
[docker]: https://docs.docker.com/get-docker/
[skaffold]: https://skaffold.dev/
[minikube]: https://minikube.sigs.k8s.io/
[docker-secret]: https://kubernetes.io/docs/tasks/configure-pod-container/pull-image-private-registry/
