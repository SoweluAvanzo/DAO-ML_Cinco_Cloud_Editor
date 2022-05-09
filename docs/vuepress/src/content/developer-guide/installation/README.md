# Installation

On this page, you find a guide on how to run CincoCloud locally for development purposes on Linux, Windows and macOS.


## Preparations

*In order to run CincoCloud locally, ensure that you have at least 8GB of RAM and at least 60GB of free disc space available.*


### 1. Install necessary software

Install the following software:

- [Docker][docker]
- [Helm][helm]
- [Skaffold][skaffold]
- [Minikube][minikube]
- [Kubectl][kubectl]

**On Windows (additionally)**

Activate `hyper-v` in Windows. It will be used instead of docker. Also, you need atleast `60GB` of disk-storage and administrator-rights. Almost all commands need to be run with high privileges, because of the `hyper-v`-context.

**On macOS (additionally)**

Since the default Docker driver for minikube currently does not support Ingress very well, we need to install an additional driver.
We tested it with `hyperkit` and `vmware` drivers.
Install one of them.

| Driver     | Installation                                |
|------------|---------------------------------------------|
| `hyperkit` | `brew install hyperkit`                     |
| `vmware`   | `brew install docker-machine-driver-vmware` |


### 2. Run a local Kubernetes cluster

*(CincoCloud works best with 4 CPU cores, 8Gb of RAM and 60GB of free disc space)*

1. Start the cluster:

    **On Linux**

    * Start the cluster with the default Docker driver<br>
    `minikube start --cpus 4 --memory 8192 --disk-size 60000mb`

    **On Windows**

    * Start the cluster with the hyperv driver:<br>
    `minikube start --cpus 4 --memory 8192 --driver=hyperv --disk-size 60000mb`

    **On macOS**

    * Start the cluster with the one of the drivers from the table above:<br>
    `minikube start --cpus 4 --memory 8192 --driver=hyperkit --disk-size 60000mb`

2. Enable necessary plugins:

```
  minikube addons enable default-storageclass && \
  minikube addons enable ingress && \
  minikube addons enable ingress-dns && \
  minikube addons enable storage-provisioner
```

3. Add the minikube IP address to the hosts file *(typically, this only has to be setup once)*
    1. Execute `minikube ip` to retrieve the IP address of the cluster
    2. Add an entry `<IP> cinco-cloud` to the hosts file:
        * **Windows**: `C:\Windows\System32\drivers\etc\hosts`
        * **Linux and macOS**: `/etc/hosts`

4. **macOS** users also have to do the following:
    1. Install [CoreDNS](https://coredns.io) (you can use `brew install coredns`)
    2. Execute the installation step 4 (Configure in-cluster DNS server to resolve local DNS names inside cluster) of [the minikube Ingress DNS docs](https://minikube.sigs.k8s.io/docs/handbook/addons/ingress-dns/#installation)

### 3. Get the Sources

1. Clone the CincoCloud [repository][cinco-cloud-repository]

### 4. Create necessary secrets

1. Create a deploy token in the [cinco cloud repository][cinco-cloud-repository] with `read_registry` rights

2. In the cinco-cloud directory, create the file `infrastructure/helm/secrets.yaml` and add the following secret, for `<USERNAME>` and `<PASSWORD>` base64 encode and enter the credentials from the previous step:

    ```
    apiVersion: v1
    kind: Secret
    metadata:
      name: cinco-cloud-registry-credentials
    type: Opaque
    data:
      username: <USERNAME>
      password: <PASSWORD>
    ```

3. Create and apply secrets for the GitLab registry
    1. Create a secret for the cinco cloud repository:<br>
       `kubectl create secret docker-registry cinco-cloud-registry-secret --docker-server=registry.gitlab.com --docker-username=<USERNAME> --docker-password=<USERNAME> --dry-run=client -o yaml`
    2. Copy the terminal output in the`secrets.yaml` file

4. Ensure that you separate all secrets in the `secrets.yaml` file with a new line containing `---`.

5. Apply the secret to the cluster: `kubectl apply -f infrastructure/helm/secrets.yaml`.

6. Install [cert-manager].

## Run CincoCloud

1. Ensure that the local cluster is running.
   Run `minikube status` and check if the output looks like
      ```
      minikube
      type: Control Plane
      host: Running
      kubelet: Running
      apiserver: Running
      kubeconfig: Configured
      ``
2. In the cinco-cloud directory, execute `skaffold dev -p local-dev` and wait for all pods to be deployed.
   All pods listed by `kubectl get pods` should have the status `running`.
   Thanks to skaffold, you can now change the code and skaffold automatically rebuilds and redeploys new images with the changes.
3. Open `https://cinco-cloud/frontend` in a web browser.
4. After the first start, your browser will reject the self-signed certificate from the `cert-manager` and we have to add it to the browser's certificate store.


## Skaffold development profiles

Use one of the following profiles in conjunction with `skaffold dev -p <profile>`.

| No | Name             | SSL   | Hot reload | Frontend URL | API URL |
|----|------------------|-------|------------|--------------|---------|
| 1  | `local-dev`      | *yes*  | *yes*      | `/frontend`  | `/`     |
| 2  | `local-prod`     | *yes*  | *no*       | `/`          | `/`     |

1 provides a local development environment with hot reload.
If you want to simulate a production build on your local machine use 2.


[helm]: https://helm.sh/
[docker]: https://docs.docker.com/get-docker/
[skaffold]: https://skaffold.dev/
[minikube]: https://minikube.sigs.k8s.io/
[docker-secret]: https://kubernetes.io/docs/tasks/configure-pod-container/pull-image-private-registry/
[cinco-cloud-repository]: https://gitlab.com/scce/cinco-cloud
[kubectl]: https://kubernetes.io/docs/reference/kubectl/overview/
[cert-manager]: https://cert-manager.io/docs/installation/helm/#
