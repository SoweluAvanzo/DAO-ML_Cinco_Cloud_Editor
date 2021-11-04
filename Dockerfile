# build cinco extension
# --------------------------------
FROM docker.io/library/node:14.18-buster-slim as cinco-extension-builder
WORKDIR /cinco-extension
COPY ./vscode-extensions/cinco-extension /cinco-extension
# outputs extension to /cinco-extension/cinco-extension-0.0.1.vsix
RUN npm install -g vsce --unsafe-perm
RUN yarn

# build cinco-example-project-creator
# --------------------------------
FROM docker.io/library/node:14.18-buster-slim as cinco-example-project-creator-builder
WORKDIR /cinco-example-project-creator
COPY ./vscode-extensions/cinco-example-project-creator /cinco-example-project-creator
# outputs extension to /cinco-example-project-creator/cinco-example-project-creator-0.0.1.vsix
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
COPY ./cinco-ls /cinco-ls
RUN mvn clean install

# build the theia editor
# --------------------------------
FROM docker.io/library/openjdk:11.0.12-slim-bullseye
# default environment variables
ENV NPM_CONFIG_PREFIX=/home/node/.npm-global
ENV CINCO_CLOUD_HOST=cinco-cloud
ENV CINCO_CLOUD_PORT=80
ENV CINCO_CLOUD_DEBUG=false
ENV THEIA_WEBVIEW_EXTERNAL_ENDPOINT='{{hostname}}'
ENV PYRO_HOST="localhost"
ENV PYRO_PORT=8000
ENV PYRO_SUBPATH=""
# make readable for root only
RUN chmod -R 750 /var/run/
WORKDIR /editor
COPY ./editor /editor
RUN useradd -ms /bin/bash theia
COPY --chown=theia:theia editor /editor

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

# install python
RUN apt install -y python && python --version && sleep 10
# install npm dependencies
RUN npm install -g node-gyp && npm install -g typescript
# build theia-editor
RUN yarn

# copy vscode-extensions into plugins
COPY --from=cinco-extension-builder /cinco-extension/cinco-extension-0.0.1.vsix /editor/browser-app/plugins
COPY --from=cinco-example-project-creator-builder /cinco-example-project-creator/cinco-example-project-creator-0.0.1.vsix /editor/browser-app/plugins
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
    THEIA_WEBVIEW_EXTERNAL_ENDPOINT='{{hostname}}' \
    THEIA_MINI_BROWSER_HOST_PATTERN={{hostname}} \
    PYRO_HOST="${PYRO_HOST}" \
    PYRO_PORT="${PYRO_PORT}" \
    PYRO_SUBPATH="${PYRO_SUBPATH}" \
    yarn run theia start --port=3000 --root-dir=/editor/workspace --plugins=local-dir:./plugins --hostname 0.0.0.0