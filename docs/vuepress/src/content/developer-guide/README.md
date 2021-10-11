# Developer Guide

## Preparations

*For CincoCloud to run locally, ensure that you have at least 60GB of free disc space available.*

1. Install the following software
    - [Docker][docker]
    - [Helm][helm]
    - [Skaffold][skaffold]
    - [Minikube][minikube]

    - If you're running Windows:
    
        Activate `hyper-v` in Windows. It will be used instead of docker. Also, you need atleast `60GB` of disk-storage and administrator-rights. Almost all commands need to be run with high privileges, because of the `hyper-v`-context.

2. Run a local Kubernetes cluster
    1. Start the cluster: `minikube start --cpus 4 --memory 8192` (CincoCloud works best with 4 CPU cores and 8Gb of RAM)
    
        - on Windows: `minikube start --cpus 4 --memory 8192 --driver=hyperv --disk-size 60000mb`

            NOTE: With this command the diskspace is set to around 60GB from which about 30GB are used for the registry of the infrastructure. If you run into issues, adjust this value. We recommend to use Windows only for development purpose, but not for real deployment.

    2. Enable necessary plugins:

    ```
      minikube addons enable default-storageclass && \
      minikube addons enable ingress && \
      minikube addons enable ingress-dns && \
      minikube addons enable storage-provisioner
    ```

3. Add the minikube IP address to the `/etc/hosts` file
    1. Execute `minikube ip` to retrieve the IP address
    2. Add an entry to the `/etc/hosts`: `<IP> cinco-cloud`
        - on Windows: `C:\Windows\System32\drivers\etc\hosts`

4. Create a deploy token in the [cinco cloud archetype repository][cinco-cloud-archetype] with `read_registry` rights

5. Create a `secrets.yaml` file and add the following secret, for `<USERNAME>` and `<PASSWORD>` base64 encode and enter the credentials from step 4:

    ```
    apiVersion: v1
    kind: Secret
    metadata:
      name: cinco-cloud-archetype-registry-credentials
    type: Opaque
    data:
      username: <USERNAME>
      password: <PASSWORD>
    ```

6. Create and apply secrets for the GitLab registry
    1. Create a secret for the cinco cloud repository:
        - `kubectl create secret docker-registry cinco-cloud-registry-secret --docker-server=registry.gitlab.com --docker-username=<USERNAME> --docker-password=<USERNAME> --dry-run=client -o yaml`
    2. Create a secret for the cinco cloud archetype repository:
        - `kubectl create secret docker-registry cinco-cloud-archetype-registry-secret --docker-server=registry.gitlab.com --docker-username=<USERNAME> --docker-password=<USERNAME> --dry-run=client -o yaml`
    3. Copy the terminal output in the`secrets.yaml` file

7. Apply the secret to the cluster: `kubectl apply -f secrets.yaml`


## Installation

1. Clone the repository and ensure that the cluster is running
2. In the root of the repository directory, execute `skaffold dev` and wait for all pods to be deployed
3. Open `http://cinco-cloud` in a Web browser

[helm]: https://helm.sh/
[docker]: https://docs.docker.com/get-docker/
[skaffold]: https://skaffold.dev/
[minikube]: https://minikube.sigs.k8s.io/
[docker-secret]: https://kubernetes.io/docs/tasks/configure-pod-container/pull-image-private-registry/
[cinco-cloud-archetype]: https://gitlab.com/scce/cinco-cloud-archetype