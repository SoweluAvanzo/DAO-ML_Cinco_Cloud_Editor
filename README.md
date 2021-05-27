# Cinco Cloud

## Development

### Requirements

- [Docker][docker]
- [Skaffold][skaffold]
- [Minikube][minikube]

### Install

1. Create a `secret.yaml` file in `.infrastructure` with the following contents

    ```yaml
    apiVersion: v1
    kind: Secret
    metadata:
      name: gitlab-registry-secret
      namespace: default
    data:
      .dockerconfigjson: <KEY>
    type: kubernetes.io/dockerconfigjson
    ```

    where `<KEY>` is the encoded `dockerconfig.json` file for `registry.gitlab.com` (see [here][docker-secret])

1. Start a local single node cluster: `minikube start`
2. Enable addons: `minikube enable default-storageclass ingress storage-provisioner` *(once)*
3. Deploy kubernetes files locally: `skaffold dev`
4. Execute `kubectl get ingress` and wait until `ADDRESS` and `HOSTS` are visible
5. Add the entry `<ADDRESS> <HOST>` to the `/etc/hosts` file *(once)*
6. Open `http://ADDRESS` in a Web browser

[docker]: https://docs.docker.com/get-docker/
[skaffold]: https://skaffold.dev/
[minikube]: https://minikube.sigs.k8s.io/
[docker-secret]: https://kubernetes.io/docs/tasks/configure-pod-container/pull-image-private-registry/