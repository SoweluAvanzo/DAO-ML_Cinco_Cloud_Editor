#
# Build the quarkus backend. Move static frontend files to the quarkus resource
# folder so that we can build a fat jar that includes the frontend.
#
FROM docker.io/library/maven:3.8.3-adoptopenjdk-15
WORKDIR /app/main
RUN apt-get update
# install Node.js
RUN apt-get -y install curl
RUN curl -sL https://deb.nodesource.com/setup_16.x | bash -
RUN apt-get -y install nodejs
# create directories
RUN mkdir /app/main/backend
RUN mkdir /app/main/frontend
RUN mkdir /app/resources
# copy frontend files
COPY ./main/frontend/*.json /app/main/frontend/
RUN cd ./frontend && npm install
COPY ./main/frontend/src /app/main/frontend/src
# copy backend files
COPY ./resources /app/resources
COPY ./main/backend/.mvn /app/main/backend/.mvn
COPY ./main/backend/mvnw /app/main/backend/
COPY ./main/backend/pom.xml /app/main/backend/
RUN cd ./backend && mvn clean verify -DskipTests
COPY ./main/backend/src /app/main/backend/src
# run the frontend and the backend
EXPOSE 4200 8000 9000
CMD cd /app/main/frontend && npm run start -- --host=0.0.0.0 --disable-host-check --configuration=development-k8s --hmr=true & \
    cd /app/main/backend && mvn quarkus:dev \
      -Dquarkus.datasource.jdbc.url="jdbc:postgresql://${DATABASE_URL}" \
      -Dquarkus.datasource.username="${DATABASE_USER}" \
      -Dquarkus.datasource.password="${DATABASE_PASSWORD}" \
      -Damqp-host="${AMPQ_HOST}" \
      -Damqp-port="${AMPQ_PORT}" \
      -Damqp-username="${AMPQ_USERNAME}" \
      -Damqp-password="${AMPQ_PASSWORD}" \
      -Dpodman.registry.api.port="${PODAN_REGISTRY_API_PORT}" \
      -Dkubernetes.namespace="${KUBERNETES_NAMESPACE}" \
      -Dcincocloud.host="${CINCO_CLOUD_HOST}" \
      -Dcincocloud.password.secret="${CINCO_CLOUD_PASSWORD_SECRET}" \
      -Dcincocloud.environment="${ENVIRONMENT}" \
      -Darchetype.image="${ARCHETYPE_IMAGE}" \
      -Darchetype.storage-class-name="${ARCHETYPE_STORAGE_CLASS_NAME}" \
      -Darchetype.storage="${ARCHETYPE_STORAGE}" \
      -Darchetype.host-path="${ARCHETYPE_HOST_PATH}" \
      -Dminio.host="${MINIO_HOST}" \
      -Dminio.port="${MINIO_PORT}" \
      -Dminio.access-key="${MINIO_ACCESS_KEY}" \
      -Dminio.secret-key="${MINIO_SECRET_KEY}" \
      -Dquarkus.http.port=8000 \
      -Dquarkus.http.host=0.0.0.0 \
      -Dquarkus.http.cors=true
