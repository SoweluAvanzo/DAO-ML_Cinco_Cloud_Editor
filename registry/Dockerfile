FROM quay.io/containers/podman:latest

RUN mkdir /var/lib/registry
VOLUME /var/lib/registry

EXPOSE 5000

CMD podman run \
  --name registry \
  --storage-driver=vfs \
  -p 5000:5000 \
  -v /dev:/dev \
  -v /var/lib/registry:/var/lib/registry \
  --restart=always \
  registry:2