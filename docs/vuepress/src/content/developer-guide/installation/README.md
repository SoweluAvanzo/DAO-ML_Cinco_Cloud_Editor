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


### 2. Run a local Kubernetes cluster

*(CincoCloud works best with 4 CPU cores, 8Gb of RAM and 60GB of free disc space)*

1. Start the cluster:

    **Linux**

    * Start the cluster with the default Docker driver<br>
    `minikube start --cpus 4 --memory 8192 --driver=docker --disk-size 60000mb`

    **Windows**

    * Start the cluster with the hyperv driver:<br>
    `minikube start --cpus 4 --memory 8192 --driver=hyperv --disk-size 60000mb`

    **macOS**

    * Start the cluster with the one of the drivers from the table above:<br>
    `minikube start --cpus 4 --memory 8192 --driver=docker --disk-size 60000mb`

2. Enable necessary plugins:

```
  minikube addons enable default-storageclass && \
  minikube addons enable ingress && \
  minikube addons enable ingress-dns && \
  minikube addons enable storage-provisioner
```

3. Add the minikube IP address to the hosts file *(typically, this only has to be setup once)*

    **Linux**

      1. Execute `minikube ip` to retrieve the IP address of the cluster
      2. Add the entry `<IP> cinco-cloud` to the `/etc/hosts` file

    **Windows**

      1. Execute `minikube ip` to retrieve the IP address of the cluster
      2. Add the entry `<IP> cinco-cloud` to the `C:\Windows\System32\drivers\etc\hosts` file

    **MacOS**
    
      1. Add the entry `127.0.0.1 cinco-cloud` to the `/etc/hosts` file

### 3. Get the Sources

1. Clone the CincoCloud [repository][cinco-cloud-repository]

### 4. Create necessary secrets

1. In the cinco-cloud directory, create the file `infrastructure/helm/secrets.yaml`

2. Add a secret with the name `cinco-cloud-main-secrets` to the `secrets.yaml` file with the following contents and replace the placeholders with actual values of your choice:

```yaml
---
apiVersion: v1
kind: Secret
metadata:
  name: cinco-cloud-main-secrets
type: Opaque
data:
  passwordSecret: <BASE64_ENCODED_SECRET>
  databaseUser: <BASE64_ENCODED_DB_USER>
  databasePassword: <BASE64_ENCODED_DB_PASSWORD>
  artemisUser: <BASE64_ENCODED_ARTEMIS_USER>
  artemisPassword: <BASE64_ENCODED_ARTEMIS_USER>
  minioRootUser: <BASE64_ENCODED_MINIO_USER>
  minioRootAdmin: <BASE64_ENCODED_MINIO_PASSWORD>
  minioAccessKey: <BASE64_ENCODED_MINIO_SERVICE_ACCOUNT_ACCESS>
  minioSecretKey: <BASE64_ENCODED_MINIO_SERVICE_ACCOUNT_SECRET>
```

*We will create the access key and the secret key for Minio later on.*

3. Apply the secret to the cluster: `kubectl apply -f infrastructure/helm/secrets.yaml`.

### 5. Install Cert Manager

1. Follow the following guide to install the cert manager for local SSL support: [cert-manager].

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
2. In the root directory, execute `skaffold dev -p local-dev` and wait for all pods to be deployed.
   All pods listed by `kubectl get pods` should have the status `running`.
   Thanks to skaffold, you can now change the code and skaffold automatically rebuilds and redeploys new images with the changes.
3. After the first start, extract the root certificate from the cluster:

   * **Windows**

      `kubectl get secret cinco-cloud-local-ca-cert-secret -o jsonpath={.data.'tls\.crt'} | ForEach-Object {[System.Text.Encoding]::Unicode.GetString({System.Convert]::FromBase64String($_))} > cinco-cloud-local-rootCA.pem`

   * **Linux/MacOS**

      `kubectl get secret cinco-cloud-local-ca-cert-secret -o jsonpath={.data.'tls\.crt'} | base64 -d > cinco-cloud-local-rootCA.pem`

4. Add `cinco-cloud-local-rootCA.pem` to your browser's certificate store.
5. **MacOS**: Open a terminal and execute `minikube tunnel` to tunnel ingress ports to the host.
   Leave the terminal session open during the development.
6. Open `https://cinco-cloud/frontend` in a web browser to check if CincoCloud is reachable.
7. Setup Minio Storage Server *(only once)*

    * **Linux/Windows**

      1. Get the port of the [Minio][minio] Service: `kubectl get service minio-service -o jsonpath={.spec.ports[1].nodePort}`
      2. Get the minikube ip: `minikube ip`
      2. Open `http://<IP>:<PORT>` with the port obtained from the previous step

    * **MacOS**

      1. Open a terminal and execute `minikube service minio-service --url`.
         Two URLs starting with `http://127.0.0.1:<PORT>` will be displayed.
         One of them (propably the latter one) is the URL to the Minio admin console.
         Open the displayed URL in a web browser. 
         You can close the session after having created the service account. <p></p>

    1. Login with the credentials provided in `cinco-cloud-main-secrets`
    2. Navigate to *Identity > Service Accounts* and click on *Create service account*
    3. Create a service key with the details provided in `cinco-cloud-main-secrets` and click on `create`
    4. Restart the pod of the main service: `kubectl delete pod main-statefulset-0`

## Skaffold development profiles

Use one of the following profiles in conjunction with `skaffold dev -p <profile>`.

| No  | Name             | SSL   | Hot reload | Frontend URL | API URL |
|-----|------------------|-------|------------|--------------|---------|
| 1)  | `local-dev`      | *yes*  | *yes*      | `/frontend`  | `/`     |
| 2)  | `local-prod`     | *yes*  | *no*       | `/`          | `/`     |

1) provides a local development environment with hot reload.
If you want to simulate a production build on your local machine use 2).


[helm]: https://helm.sh/
[docker]: https://docs.docker.com/get-docker/
[skaffold]: https://skaffold.dev/
[minikube]: https://minikube.sigs.k8s.io/
[minio]: https://min.io/
[docker-secret]: https://kubernetes.io/docs/tasks/configure-pod-container/pull-image-private-registry/
[cinco-cloud-repository]: https://gitlab.com/scce/cinco-cloud
[kubectl]: https://kubernetes.io/docs/reference/kubectl/overview/
[cert-manager]: https://cert-manager.io/docs/installation/helm/#
