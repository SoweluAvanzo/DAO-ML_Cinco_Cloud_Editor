FROM node:10-buster

# Install Java and update
RUN apt-get update && \
    apt-get install -y default-jdk libsecret-1-dev xvfb libx11-dev libxkbfile-dev maven libxml2-utils && \
    apt-get upgrade -y

# Make readable for root only
RUN chmod -R 750 /var/run/

WORKDIR /cinco-cloud

RUN useradd -ms /bin/bash theia

COPY --chown=theia:theia . /cinco-cloud

# Set location to place global npm dependencies
ENV NPM_CONFIG_PREFIX=/home/node/.npm-global

# Expose port
EXPOSE 3000

USER theia

# Build backend
RUN cd backend/releng/de.jabc.cinco.meta.core.parent && \
    mvn clean install -DskipTests && \
    cd ../../../

# Build frontend
RUN cd cinco-extension/ && \
    yarn && \
    cd ../web/ && \
    yarn && \
    cd ..

# Integrate favicon
RUN cp /cinco-cloud/web/favicon.ico /cinco-cloud/web/browser-app/lib && \
	sed -i 's/<\/head>/<link rel="icon" href="favicon.ico" \/><\/head>/g' /cinco-cloud/web/browser-app/lib/index.html

CMD cd /cinco-cloud/web/browser-app && yarn start --hostname 0.0.0.0
