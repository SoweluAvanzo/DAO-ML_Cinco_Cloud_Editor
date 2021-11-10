FROM quay.io/containers/podman:latest

RUN mkdir /var/lib/registry
VOLUME /var/lib/registry
RUN python3 -m ensurepip --default-pip
RUN pip install flask
COPY service.py /service.py
EXPOSE 5000 8000

CMD python3 /service.py & podman run \
  --name registry \
  --storage-driver=vfs \
  -p 5000:5000 \
  -v /var/lib/registry:/var/lib/registry \
  --restart=always \
  registry:2