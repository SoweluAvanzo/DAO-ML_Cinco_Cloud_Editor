# Installation

On this page, you find a guide on how to run CincoCloud locally for development purposes on Linux, Windows and macOS.


## Preparations

*In order to run CincoCloud locally, ensure that you have at least 8GB of RAM and at least 60GB of free disc space available.*


### 1. Install necessary software

Install the following software:

- [Docker][docker]
- [Helm][helm]
- [Skaffold][skaffold] *Use a version < v2.0.0, e.g. v1.39.4*
- [Minikube][minikube]
- [Kubectl][kubectl]

### 2. Run a local Kubernetes cluster

*(CincoCloud works best with 4 CPU cores, 8Gb of RAM and 60GB of free disc space)*

1. Start the cluster with the Docker driver

```
  minikube start --cpus 4 --memory 8192 --disk-size 60000mb --driver=docker
```

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

      1. Add the entry `127.0.0.1 cinco-cloud` to the `C:\Windows\System32\drivers\etc\hosts` file

    **MacOS**
    
      1. Add the entry `127.0.0.1 cinco-cloud` to the `/etc/hosts` file

### 3. Get the Sources

1. Clone the CincoCloud [repository][cinco-cloud-repository]

### 4. Install necessary secrets

* Apply preset development secrets to the cluster: <br>
  `kubectl apply -f infrastructure/helm/secrets-local.yaml`.

* Alternatively, create your own `secrets.yaml` file.
  The contents of the file should look like the following template.
  Ensure that the name of the secret is `cinco-cloud-main-secrets` and replace the placeholders with actual values of your choice:

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
    minioAccessKey: <BASE64_ENCODED_MINIO_ACCESS_KEY>
    minioSecretKey: <BASE64_ENCODED_MINIO_ACCESS_KEY_SECRET>
    authPublicKey: <BASE64_ENCODED_RSA_PUBLIC_KEY>
    authPrivateKey: <BASE64_ENCODED_RSA_PUBLIC_KEY>
  ```
   
  *Create the public and private key, e.g., using `openssl`:*

  ```
  openssl genrsa -out rsaPrivateKey.pem 2048
  openssl rsa -pubout -in rsaPrivateKey.pem -out publicKey.pem
  ```

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

   **Note for ARM users:** Please use the additional profile `arm` to run Cinco Cloud. The complete command is `skaffold dev -p local-dev,arm`.

3. After the first start, extract the root certificate from the cluster:

   * **Windows**

      `kubectl get secret cinco-cloud-local-ca-cert-secret -o jsonpath="{.data.tls.crt}" | ForEach-Object {[System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String($_))} > cinco-cloud-local-rootCA.pem`

   * **Linux/MacOS**

      `kubectl get secret cinco-cloud-local-ca-cert-secret -o jsonpath={.data.'tls\.crt'} | base64 -d > cinco-cloud-local-rootCA.pem`

4. Add `cinco-cloud-local-rootCA.pem` to your browser's certificate store.
5. **Windows / MacOS**: Open a terminal and execute `minikube tunnel` to tunnel ingress ports to the host.
   Leave the terminal session open during the development.
6. Open `https://cinco-cloud/frontend` in a web browser to check if CincoCloud is reachable.
7. Setup Minio Storage Server *(only once)*

    * **Windows / Linux**

      1. Execute `kubectl port-forward minio-statefulset-0 9001:9001`.
         Leave the terminal session open until you finished setting up Minio.
      2. Open `http://127.0.0.1:9001`

    * **MacOS**

      1. Open a terminal and execute `minikube service minio-service --url`.
         Two URLs starting with `http://127.0.0.1:<PORT>` will be displayed.
         One of them (propably the latter one) is the URL to the Minio admin console.
         Open the displayed URL in a web browser. 
         You can close the session after having created the access key. <p></p>

    1. Login with the credentials provided in `cinco-cloud-main-secrets`.
       Per default, the credentials are `minioadmin:minioadmin`.
    2. Navigate to *User > Access Keys* and click on *Create access key*
    3. Create an access key with the details provided in `cinco-cloud-main-secrets` and click on `create`.
       In the default development secrets, the access key and the secret key are both set to `minio-sa`. 
    4. Restart the pod of the main service: `kubectl delete pod main-statefulset-0`

## Skaffold development profiles

Use one of the following profiles in conjunction with `skaffold dev -p <profile>`.

| No  | Name             | SSL   | Hot reload | Frontend URL | API URL |
|-----|------------------|-------|------------|--------------|---------|
| 1)  | `local-dev`      | *yes*  | *yes*      | `/frontend`  | `/`     |
| 2)  | `local-prod`     | *yes*  | *no*       | `/`          | `/`     |

1) provides a local development environment with hot reload.
If you want to simulate a production build on your local machine use 2).

**Please note:** If you are using ARM architecture you should always use the profile `arm` in addition to the profiles above.


[helm]: https://helm.sh/
[docker]: https://docs.docker.com/get-docker/
[skaffold]: https://skaffold.dev/
[minikube]: https://minikube.sigs.k8s.io/
[minio]: https://min.io/
[docker-secret]: https://kubernetes.io/docs/tasks/configure-pod-container/pull-image-private-registry/
[cinco-cloud-repository]: https://gitlab.com/scce/cinco-cloud
[kubectl]: https://kubernetes.io/docs/reference/kubectl/overview/
[cert-manager]: https://cert-manager.io/docs/installation/helm/#
