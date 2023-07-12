# build cinco extension
# --------------------------------
FROM --platform=linux/amd64 docker.io/library/node:18-bookworm-slim as cinco-extension-builder
WORKDIR /cinco-extension
COPY ./cinco-cloud-archetype/vscode-extensions/cinco-extension /cinco-extension
RUN apt-get update && apt-get install -y python3
# outputs extension to /cinco-extension/cinco-extension-0.0.1.vsix
RUN npm install -g vsce --unsafe-perm
RUN yarn

# build cinco-project-initializer
# --------------------------------
FROM docker.io/library/node:18-bookworm-slim as cinco-project-initializer-builder
WORKDIR /cinco-project-initializer
COPY ./cinco-cloud-archetype/vscode-extensions/cinco-project-initializer /cinco-project-initializer
# outputs extension to /cinco-project-initializer/cinco-project-initializer-0.0.1.vsix
RUN npm install -g vsce --unsafe-perm
RUN yarn

# build the theia editor
# --------------------------------
FROM docker.io/library/node:18-bookworm-slim
# default environment variables
ENV NPM_CONFIG_PREFIX=/home/node/.npm-global
ENV CINCO_CLOUD_HOST=cinco-cloud
ENV CINCO_CLOUD_PORT=80
ENV CINCO_CLOUD_DEBUG=false
ENV MINIO_HOST='minio-service'
ENV MINIO_PORT=9000
ENV MINIO_ACCESS_KEY=''
ENV MINIO_SECRET_KEY=''
ENV THEIA_WEBVIEW_EXTERNAL_ENDPOINT={{hostname}}
ENV THEIA_MINI_BROWSER_HOST_PATTERN={{hostname}}
ENV INTERNAL_USE_SSL="false"
ENV EXTERNAL_USE_SSL="false"
ENV WORKSPACE_PATH="/editor/workspace"
ENV EDITOR_TYPE="LANGUAGE_EDITOR"
ENV ENVIRONMENT="local"
# make readable for root only
RUN chmod -R 750 /var/run/

# install node, yarn and other dependencies
RUN apt update && \
    apt install -y openjdk-17-jdk && \
    apt install -y libsecret-1-dev && \
    apt install -y make

RUN apt install -y g++

RUN apt install -y build-essential

# install unzip
RUN apt install -y unzip
# install python
RUN apt install -y python3
# install npm dependencies
RUN npm install -g node-gyp && npm install -g typescript

WORKDIR /editor
RUN useradd -ms /bin/bash theia
COPY --chown=theia:theia ./cinco-cloud-archetype/editor /editor

# build theia-editor
RUN yarn

# copy vscode-extensions into plugins
COPY --from=cinco-extension-builder /cinco-extension/cinco-extension-0.0.1.vsix /editor/browser-app/plugins
COPY --from=cinco-project-initializer-builder /cinco-project-initializer/cinco-project-initializer-0.0.1.vsix /editor/browser-app/plugins

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
    CINCO_CLOUD_DEBUG="${CINCO_CLOUD_DEBUG}" \
    CINCO_CLOUD_HOST="${CINCO_CLOUD_HOST}" \
    CINCO_CLOUD_PORT="${CINCO_CLOUD_PORT}" \
    MINIO_HOST="${MINIO_HOST}" \
    MINIO_PORT="${MINIO_PORT}" \
    MINIO_ACCESS_KEY="${MINIO_ACCESS_KEY}" \
    MINIO_SECRET_KEY="${MINIO_SECRET_KEY}" \
    THEIA_WEBVIEW_EXTERNAL_ENDPOINT="${THEIA_WEBVIEW_EXTERNAL_ENDPOINT}" \
    THEIA_MINI_BROWSER_HOST_PATTERN="${THEIA_MINI_BROWSER_HOST_PATTERN}" \
    INTERNAL_USE_SSL="${INTERNAL_USE_SSL}" \
    EXTERNAL_USE_SSL="${EXTERNAL_USE_SSL}" \
    WORKSPACE_PATH="${WORKSPACE_PATH}" \
    EDITOR_TYPE="${EDITOR_TYPE}" \
    ENVIRONMENT="${ENVIRONMENT}" \
    yarn run theia start --port=3000 --root-dir=/editor/workspace --plugins=local-dir:./plugins --hostname 0.0.0.0
