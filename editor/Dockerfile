# build the language server
# --------------------------------
FROM docker.io/library/maven:3-adoptopenjdk-11 as backend-builder
WORKDIR /backend
COPY ./backend /backend
# outputs language server to releng/de.jabc.cinco.meta.core.parent/de.jabc.cinco.meta.core.ide/target/language-server
RUN cd releng/de.jabc.cinco.meta.core.parent && \
    mvn clean install -DskipTests
RUN mv releng/de.jabc.cinco.meta.core.parent/de.jabc.cinco.meta.core.ide/target/language-server /backend/language-server

# build the extension
# --------------------------------
FROM docker.io/library/node:10-buster-slim as extension-builder
WORKDIR /extension
COPY ./cinco-extension /extension
# outputs extension to /extension/cinco-extension-0.0.1.vsix
RUN yarn

# build the theia editor
# --------------------------------
FROM docker.io/library/openjdk:11.0.11-slim-buster
ENV NPM_CONFIG_PREFIX=/home/node/.npm-global
# make readable for root only
RUN chmod -R 750 /var/run/
WORKDIR /editor
RUN useradd -ms /bin/bash theia
COPY --chown=theia:theia web /editor
# install node and yarn
RUN apt update && \
    apt install -y gnupg gnupg2 curl && \
    curl -sS https://dl.yarnpkg.com/debian/pubkey.gpg | apt-key add - && \
    echo "deb https://dl.yarnpkg.com/debian/ stable main" | tee /etc/apt/sources.list.d/yarn.list && \
    apt update && \
    apt install -y nodejs npm yarn && \
    apt remove -y gnupg gnupg2 curl && \
    apt autoremove -y
# copy language-server to singleton cinco-language-server-extension
COPY --from=backend-builder /backend/language-server/ /editor/cinco-language-server-extension/language-server/
# install theia
RUN yarn
# copy extension after the build
# otherwise the build will fail because the /plugins directory already exists
COPY --from=extension-builder /extension/cinco-extension-0.0.1.vsix /editor/browser-app/plugins
# integrate favicon
RUN cp /editor/favicon.ico /editor/browser-app/lib && \
	sed -i 's/<\/head>/<link rel="icon" href="favicon.ico" \/><\/head>/g' /editor/browser-app/lib/index.html
EXPOSE 3000
CMD cd /editor/browser-app && \
    yarn run theia start --port=3000 --root-dir=../examples/Example --plugins=local-dir:./plugins --hostname 0.0.0.0