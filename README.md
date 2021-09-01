# Cinco Cloud

## Development

### Requirements

Install

- [Docker][docker]
- [Helm][helm]
- [Skaffold][skaffold]
- [Minikube][minikube]

### Install

1. Create a `secret.yaml` file in `./infrastructure/helm` with the following contents

    ```yaml
    apiVersion: v1
    kind: Secret
    metadata:
      name: cinco-cloud-registry-secret
      namespace: default
    data:
      .dockerconfigjson: <KEY>
    type: kubernetes.io/dockerconfigjson
    ```

    where `<KEY>` is the encoded `dockerconfig.json` file for `registry.gitlab.com` (see [here][docker-secret])

2. Start a local single node cluster: `minikube start`
3. Enable addons: `minikube enable default-storageclass ingress storage-provisioner` *(once)*
4. Deploy kubernetes files locally: `skaffold dev`
5. Execute `kubectl get ingress` and wait until `ADDRESS` and `HOSTS` are visible
6. Add the entry `<ADDRESS> cinco-cloud` to the `/etc/hosts` file *(once)*
7. Open `http://cinco-cloud` in a Web browser

[helm]: https://helm.sh/
[docker]: https://docs.docker.com/get-docker/
[skaffold]: https://skaffold.dev/
[minikube]: https://minikube.sigs.k8s.io/
[docker-secret]: https://kubernetes.io/docs/tasks/configure-pod-container/pull-image-private-registry/


### Deploy

1. Install the secret created from the local development on the target server
2. Create a context for the remote server and name it `ls5vs024-context`
3. Execute `skaffold deploy -t latest --kube-context ls5vs024-context -n default -p ls5vs024 --status-check=true`