# build the base image with Python 3.8
# --------------------------------
FROM docker.io/library/node:18-bookworm-slim as base
RUN apt update
RUN apt install -y build-essential zlib1g-dev libncurses5-dev libgdbm-dev libnss3-dev libssl-dev libsqlite3-dev libreadline-dev libffi-dev curl libbz2-dev liblzma-dev libx11-dev libxkbfile-dev libsecret-1-dev git
RUN curl -O https://www.python.org/ftp/python/3.8.2/Python-3.8.2.tar.xz
RUN tar -xf Python-3.8.2.tar.xz
RUN cd Python-3.8.2 \
    && ./configure --enable-optimizations --enable-loadable-sqlite-extensions \
    && make -j 4 \
    && make install
RUN corepack enable

# build the theia editor image
# --------------------------------
FROM base
# default environment variables
ENV NPM_CONFIG_PREFIX=/home/node/.npm-global
ENV CINCO_CLOUD_HOST=cinco-cloud
ENV CINCO_CLOUD_PORT=80
ENV MINIO_HOST='minio-service'
ENV MINIO_PORT=9000
ENV MINIO_ACCESS_KEY=''
ENV MINIO_SECRET_KEY=''
ENV THEIA_WEBVIEW_ENDPOINT_PATTERN={{hostname}}
ENV THEIA_WEBVIEW_EXTERNAL_ENDPOINT={{hostname}}
ENV THEIA_MINI_BROWSER_HOST_PATTERN={{hostname}}
ENV INTERNAL_USE_SSL="false"
ENV EXTERNAL_USE_SSL="false"
ENV WORKSPACE_PATH="/editor/workspace"
ENV EDITOR_TYPE="LANGUAGE_EDITOR"
ENV META_LANGUAGES_FOLDER="workspace/languages"
ENV TRANSPILATION_MODE="WATCH"
ENV NODE_ENV="development"

# disable chromium install for puppeteer, because of arm64 incompatibility
ENV PUPPETEER_SKIP_DOWNLOAD=true
ENV PUPPETEER_SKIP_CHROMIUM_DOWNLOAD=true

# make readable for root only
RUN chmod -R 750 /var/run/

# install dependencies
RUN apt update && apt install -y libsecret-1-dev make unzip curl g++ build-essential

# install npm dependencies
RUN npm install -g node-gyp && npm install -g typescript

WORKDIR /editor
RUN useradd -ms /bin/bash theia
COPY --chown=theia:theia ./cinco-cloud-archetype/editor /editor

# clean plugins
RUN mkdir -p /editor/browser-app/plugins

# clean languages folder
RUN cd /editor/cinco-glsp-server && rm -r languages && node clean-languages.js

# build minio-client
RUN cd minio-client && yarn && cd ../

# build theia-editor
RUN yarn

# runtime configuration
VOLUME /editor/workspace
EXPOSE 3000 3003 5007 9000 9222
CMD cd minio-client && node bundle/cinco-minio-client --metaFolder ../cinco-glsp-server/languages && cd ../ && \
    cd /editor/browser-app && \
    CINCO_CLOUD_HOST="${CINCO_CLOUD_HOST}" \
    CINCO_CLOUD_PORT="${CINCO_CLOUD_PORT}" \
    MINIO_HOST="${MINIO_HOST}" \
    MINIO_PORT="${MINIO_PORT}" \
    MINIO_ACCESS_KEY="${MINIO_ACCESS_KEY}" \
    MINIO_SECRET_KEY="${MINIO_SECRET_KEY}" \
    THEIA_WEBVIEW_EXTERNAL_ENDPOINT="${THEIA_WEBVIEW_EXTERNAL_ENDPOINT}" \
    THEIA_WEBVIEW_ENDPOINT_PATTERN="${THEIA_WEBVIEW_ENDPOINT_PATTERN}" \
    THEIA_MINI_BROWSER_HOST_PATTERN="${THEIA_MINI_BROWSER_HOST_PATTERN}" \
    INTERNAL_USE_SSL="${INTERNAL_USE_SSL}" \
    EXTERNAL_USE_SSL="${EXTERNAL_USE_SSL}" \
    WORKSPACE_PATH="${WORKSPACE_PATH}" \
    TRANSPILATION_MODE="${TRANSPILATION_MODE}" \
    EDITOR_TYPE="${EDITOR_TYPE}" \
    ENVIRONMENT="${ENVIRONMENT}" \
    yarn run theia start --port=3000 --CINCO_GLSP=5007 -WEB_SERVER_PORT=3003 --remote-debugging-port=9222 --no-cluster --loglevel=debug --root-dir=/editor/workspace --plugins=local-dir:./plugins --hostname 0.0.0.0 --META_DEV_MODE --META_LANGUAGES_FOLDER="${META_LANGUAGES_FOLDER}"
