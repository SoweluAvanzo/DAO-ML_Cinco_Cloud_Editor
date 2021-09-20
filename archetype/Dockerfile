# STEP 1: build the pyro server
# --------------------------------
FROM docker.io/google/dart:2.4.1 as pyro-server-frontend-builder
WORKDIR /pyro-server
COPY ./webapp /pyro-server/webapp
RUN cd webapp \
    && pub global activate webdev \
    && pub get \
    && pub run build_runner build -o build
# --------------------------------
FROM docker.io/library/maven:3-adoptopenjdk-11 as pyro-server-builder
WORKDIR /pyro-server
# copy frontend into this container
COPY --from=pyro-server-frontend-builder /pyro-server/webapp/ /pyro-server/webapp/
COPY ./app /pyro-server/app
COPY ./compile.sh /pyro-server/compile.sh
# make outsourced compilation-script executable
RUN sed -i 's/\r$//' /pyro-server/compile.sh  && \  
    chmod +x /pyro-server/compile.sh
# outputs pyro-model-server to pyro-server/pyro-server
RUN /pyro-server/compile.sh

# STEP 2: integrate
# --------------------------------
FROM registry.gitlab.com/scce/cinco-cloud-archetype/archetype:latest as cinco-cloud-archetype
ENV NPM_CONFIG_PREFIX=/home/node/.npm-global
ENV CINCO_CLOUD_HOST=cinco-cloud
ENV CINCO_CLOUD_PORT=80
ENV CINCO_CLOUD_DEBUG=false
ENV THEIA_WEBVIEW_EXTERNAL_ENDPOINT='{{hostname}}'
ENV PYRO_HOST="localhost"
# integrate pyro-server into cinco-cloud-archetype
COPY --from=pyro-server-builder /pyro-server/pyro-server/ /editor/pyro-server-extension/pyro-server/
VOLUME /editor/workspace
EXPOSE 3000 8000
CMD cd /editor/browser-app && \
    # TODO: comment CINCO_CLOUD_DEBUG out, for debug purpose only (insecure)
    DATABASE_URL="${DATABASE_URL}" \
    DATABASE_USER="${DATABASE_USER}" \
    DATABASE_PASSWORD="${DATABASE_PASSWORD}" \
    CINCO_CLOUD_DEBUG=${CINCO_CLOUD_DEBUG} \
    CINCO_CLOUD_HOST="${CINCO_CLOUD_HOST}" \
    CINCO_CLOUD_PORT="${CINCO_CLOUD_PORT}" \
    THEIA_WEBVIEW_EXTERNAL_ENDPOINT="${THEIA_WEBVIEW_EXTERNAL_ENDPOINT}" \
    PYRO_HOST="${PYRO_HOST}" \
    yarn run theia start --port=3000 --root-dir=/editor/workspace --plugins=local-dir:./plugins --hostname 0.0.0.0