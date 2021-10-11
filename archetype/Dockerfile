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
COPY --from=pyro-server-frontend-builder /pyro-server/webapp/ /pyro-server/webapp/
COPY ./app /pyro-server/app
COPY ./compileBackend.sh /pyro-server/compileBackend.sh
# make outsourced compilation-script executable and execute it
RUN sed -i 's/\r$//' /pyro-server/compileBackend.sh  && \  
    chmod +x /pyro-server/compileBackend.sh
# outputs pyro-model-server to pyro-server/pyro-server
RUN /pyro-server/compileBackend.sh

# STEP 2: integrate
# --------------------------------
FROM registry.gitlab.com/scce/cinco-cloud-archetype/archetype:latest as cinco-cloud-archetype
# set baseHref path for static resources of frontend
ENV PYRO_SUBPATH=/
# integrate pyro-server into cinco-cloud-archetype
COPY --from=pyro-server-builder /pyro-server/pyro-server/ /editor/pyro-server-extension/pyro-server
#-----------------------------------------------------------------------
# modify baseHref path for static resources of frontend
#-----------------------------------------------------------------------
CMD cd /editor/pyro-server-extension/pyro-server/ && \
    jar xf ./app.jar ./META-INF/resources/index.html && \
    baseHref=$(echo "${PYRO_SUBPATH}" | sed "s/\//\\\\\//g") && \
    sed -i "s/base href=\"\/\"/base href=\"${baseHref}\"/g" ./META-INF/resources/index.html && \   
    jar uf ./app.jar ./META-INF/resources/index.html && \
    echo "Set baseHref to: ${PYRO_SUBPATH}" && \
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
    PYRO_HOST="${PYRO_HOST}" \
    PYRO_PORT="${PYRO_PORT}" \
    PYRO_SUBPATH="${PYRO_SUBPATH}" \
    yarn run theia start --port=3000 --root-dir=/editor/workspace --plugins=local-dir:./plugins --hostname 0.0.0.0
