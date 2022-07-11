# build cinco extension
# --------------------------------
FROM docker.io/library/node:14.18-buster-slim as cinco-extension-builder
WORKDIR /cinco-extension
COPY ./vscode-extensions/cinco-extension /cinco-extension
# outputs extension to /cinco-extension/cinco-extension-0.0.1.vsix
RUN npm install -g vsce --unsafe-perm
RUN yarn

# build cinco-project-initializer
# --------------------------------
FROM docker.io/library/node:14.18-buster-slim as cinco-project-initializer-builder
WORKDIR /cinco-project-initializer
COPY ./vscode-extensions/cinco-project-initializer /cinco-project-initializer
# outputs extension to /cinco-project-initializer/cinco-project-initializer-0.0.1.vsix
RUN npm install -g vsce --unsafe-perm
RUN yarn

# build pyro client
# --------------------------------
FROM docker.io/library/node:14.18-buster-slim as pyro-client-builder
WORKDIR /pyro-client-extension
COPY ./vscode-extensions/pyro-client-extension /pyro-client-extension
# outputs extension to /pyro-client-extension/pyro-client-extension-0.0.1.vsix
RUN npm install -g vsce --unsafe-perm
RUN yarn

# build the cinco-language-server
# --------------------------------
FROM docker.io/library/maven:3.8.1-jdk-11-openj9 as cinco-ls-builder
WORKDIR /cinco-ls
COPY ./cinco-language-server /cinco-ls
RUN mvn clean install

# build the theia editor
# --------------------------------
FROM docker.io/library/openjdk:18-ea-14-slim-bullseye
# default environment variables
ENV NPM_CONFIG_PREFIX=/home/node/.npm-global
ENV CINCO_CLOUD_HOST=cinco-cloud
ENV CINCO_CLOUD_PORT=80
ENV CINCO_CLOUD_DEBUG=false
ENV MINIO_HOST='minio-service'
ENV MINIO_PORT=9000
ENV MINIO_ACCESS_KEY=''
ENV MINIO_SECRET_KEY=''
ENV THEIA_WEBVIEW_EXTERNAL_ENDPOINT='{{hostname}}'
ENV INTERNAL_USE_SSL="false"
ENV INTERNAL_PYRO_HOST="localhost"
ENV INTERNAL_PYRO_PORT=8000
ENV INTERNAL_PYRO_SUBPATH=""
ENV EXTERNAL_USE_SSL="false"
ENV EXTERNAL_PYRO_HOST="localhost"
ENV EXTERNAL_PYRO_PORT=8000
ENV EXTERNAL_PYRO_SUBPATH=""
ENV PYRO_SERVER_BINARIES_FILE=""
ENV ENVIRONMENT="local"
# make readable for root only
RUN chmod -R 750 /var/run/

# install node, yarn and other dependencies
RUN apt update && \
    apt install -y gnupg gnupg2 curl && \
    curl -sS https://dl.yarnpkg.com/debian/pubkey.gpg | apt-key add - && \
    echo "deb https://dl.yarnpkg.com/debian/ stable main" | tee /etc/apt/sources.list.d/yarn.list && \
    apt update && \
    apt install -y nodejs npm yarn && \
    apt remove -y gnupg gnupg2 curl && \
    apt install -y libsecret-1-dev && \
    apt autoremove -y

# install unzip
RUN apt install -y unzip
# install python
RUN apt install -y python && python --version && sleep 10
# install npm dependencies
RUN npm install -g node-gyp && npm install -g typescript

WORKDIR /editor
RUN useradd -ms /bin/bash theia
COPY --chown=theia:theia ./editor /editor

# build theia-editor
RUN yarn

# copy vscode-extensions into plugins
COPY --from=cinco-extension-builder /cinco-extension/cinco-extension-0.0.1.vsix /editor/browser-app/plugins
COPY --from=cinco-project-initializer-builder /cinco-project-initializer/cinco-project-initializer-0.0.1.vsix /editor/browser-app/plugins
COPY --from=pyro-client-builder /pyro-client-extension/pyro-client-extension-0.0.1.vsix /editor/browser-app/plugins
# copy cinco-language-server into backend
COPY --from=cinco-ls-builder /cinco-ls/de.jabc.cinco.meta.core.ide/target/language-server /editor/cinco-language-server-extension/language-server

# integrate favicon
RUN sed -i 's/<\/head>/<link rel="icon" href="favicon.ico" \/><\/head>/g' /editor/browser-app/lib/index.html

# runtime configuration
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
    MINIO_HOST="${MINIO_HOST}" \
    MINIO_PORT="${MINIO_PORT}" \
    MINIO_ACCESS_KEY="${MINIO_ACCESS_KEY}" \
    MINIO_SECRET_KEY="${MINIO_SECRET_KEY}" \
    THEIA_WEBVIEW_EXTERNAL_ENDPOINT='{{hostname}}' \
    THEIA_MINI_BROWSER_HOST_PATTERN={{hostname}} \
    INTERNAL_USE_SSL="${INTERNAL_USE_SSL}" \
    INTERNAL_PYRO_HOST="${INTERNAL_PYRO_HOST}" \
    INTERNAL_PYRO_PORT="${INTERNAL_PYRO_PORT}" \
    INTERNAL_PYRO_SUBPATH="${INTERNAL_PYRO_SUBPATH}" \
    EXTERNAL_USE_SSL="${EXTERNAL_USE_SSL}" \
    EXTERNAL_PYRO_HOST="${EXTERNAL_PYRO_HOST}" \
    EXTERNAL_PYRO_PORT="${EXTERNAL_PYRO_PORT}" \
    EXTERNAL_PYRO_SUBPATH="${EXTERNAL_PYRO_SUBPATH}" \
    PYRO_SERVER_BINARIES_FILE="${PYRO_SERVER_BINARIES_FILE}" \
    ENVIRONMENT="${ENVIRONMENT}" \
    yarn run theia start --port=3000 --root-dir=/editor/workspace --plugins=local-dir:./plugins --hostname 0.0.0.0
