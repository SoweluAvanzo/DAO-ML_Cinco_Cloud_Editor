ARG ARCHETYPE_IMAGE_TAG=latest
# STEP 1: build the pyro server
# --------------------------------
FROM docker.io/google/dart:2.4.1 as pyro-server-frontend-builder
WORKDIR /pyro-server
COPY ./webapp /pyro-server/webapp
COPY ./compileFrontend.sh /pyro-server/compileFrontend.sh
RUN sed -i 's/\r$//' /pyro-server/compileFrontend.sh  && \  
    chmod +x /pyro-server/compileFrontend.sh
RUN /pyro-server/compileFrontend.sh
# --------------------------------
FROM docker.io/library/maven:3-adoptopenjdk-11 as pyro-server-builder
WORKDIR /pyro-server
# copy frontend into this container
COPY --from=pyro-server-frontend-builder /pyro-server/app/ /pyro-server/app/
COPY ./app /pyro-server/app
COPY ./compileBackend.sh /pyro-server/compileBackend.sh
# make outsourced compilation-script executable and execute it
RUN sed -i 's/\r$//' /pyro-server/compileBackend.sh  && \  
    chmod +x /pyro-server/compileBackend.sh
# outputs pyro-model-server to pyro-server/pyro-server
RUN /pyro-server/compileBackend.sh

# STEP 2: integrate
# --------------------------------
FROM registry.gitlab.com/scce/cinco-cloud/archetype:${ARCHETYPE_IMAGE_TAG} as cinco-cloud-archetype
# set baseHref path for static resources of frontend
ENV EXTERNAL_PYRO_SUBPATH=/
ENV WORKSPACE_PATH=/editor/workspace
# integrate pyro-server into cinco-cloud-archetype
COPY --from=pyro-server-builder /pyro-server/pyro-server/ /editor/pyro-server-extension/pyro-server
#-----------------------------------------------------------------------
# modify baseHref path for static resources of frontend
#-----------------------------------------------------------------------
CMD cd /editor/pyro-server-extension/pyro-server/ && \
    jar xf ./app.jar ./META-INF/resources/index.html && \
    baseHref=$(echo "${EXTERNAL_PYRO_SUBPATH}" | sed "s/\//\\\\\//g") && \
    sed -i "s/base href=\"\/\"/base href=\"${baseHref}\"/g" ./META-INF/resources/index.html && \   
    jar uf ./app.jar ./META-INF/resources/index.html && \
    echo "Set baseHref to: ${EXTERNAL_PYRO_SUBPATH}" && \
    #-----------------------------------------------------------------------
    # cinco-cloud-archetype-cmd
    #-----------------------------------------------------------------------
    cd /editor/browser-app && \
    # TODO: comment CINCO_CLOUD_DEBUG out, for debug purpose only (insecure)
    DATABASE_URL="${DATABASE_URL}" \
    DATABASE_USER="${DATABASE_USER}" \
    DATABASE_PASSWORD="${DATABASE_PASSWORD}" \
    CINCO_CLOUD_DEBUG=${CINCO_CLOUD_DEBUG} \
    CINCO_CLOUD_HOST="${CINCO_CLOUD_HOST}" \
    CINCO_CLOUD_PORT="${CINCO_CLOUD_PORT}" \
    THEIA_WEBVIEW_EXTERNAL_ENDPOINT='{{hostname}}' \
    THEIA_MINI_BROWSER_HOST_PATTERN={{hostname}} \
    INTERNAL_PYRO_HOST="${INTERNAL_PYRO_HOST}" \
    INTERNAL_PYRO_PORT="${INTERNAL_PYRO_PORT}" \
    INTERNAL_EXTERNAL_PYRO_SUBPATH="${INTERNAL_PYRO_SUBPATH}" \
    INTERNAL_USE_SSL="${INTERNAL_USE_SSL}" \
    EXTERNAL_PYRO_HOST="${EXTERNAL_PYRO_HOST}" \
    EXTERNAL_PYRO_PORT="${EXTERNAL_PYRO_PORT}" \
    EXTERNAL_EXTERNAL_PYRO_SUBPATH="${EXTERNAL_PYRO_SUBPATH}" \
    EXTERNAL_USE_SSL="${EXTERNAL_USE_SSL}" \
    WORKSPACE_PATH="${WORKSPACE_PATH}" \
    yarn run theia start --port=3000 --root-dir=/editor/workspace --plugins=local-dir:./plugins --hostname 0.0.0.0
