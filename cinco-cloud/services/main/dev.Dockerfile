#
# Build the quarkus backend. Move static frontend files to the quarkus resource
# folder so that we can build a fat jar that includes the frontend.
#
FROM docker.io/library/node:18-bullseye
WORKDIR /app/main
RUN apt-get update && apt-get -y install maven
# create directories
RUN mkdir -p /app/main/backend /app/main/frontend /app/resources
# copy frontend files
COPY ./main/frontend/*.json /app/main/frontend/
COPY ./main/frontend/src /app/main/frontend/src
RUN cd ./frontend && yarn
# copy backend files
COPY ./resources /app/resources
COPY ./main/backend/.mvn /app/main/backend/.mvn
COPY ./main/backend/mvnw /app/main/backend/
COPY ./main/backend/pom.xml /app/main/backend/
COPY ./main/backend/src /app/main/backend/src
RUN cd ./backend && mvn clean verify -DskipTests
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
      -Dcincocloud.ssl="${CINCO_CLOUD_SSL}" \
      -Dcincocloud.password.secret="${CINCO_CLOUD_PASSWORD_SECRET}" \
      -Dcincocloud.environment="${ENVIRONMENT}" \
      -Dquarkus.mailer.host="${CINCO_CLOUD_MAILER_HOST}" \
      -Dquarkus.mailer.port="${CINCO_CLOUD_MAILER_PORT}" \
      -Dquarkus.mailer.ssl="${CINCO_CLOUD_MAILER_SSL}" \
      -Dquarkus.mailer.from="${CINCO_CLOUD_MAILER_FROM}" \
      -Darchetype.image="${ARCHETYPE_IMAGE}" \
      -Darchetype.storage-class-name="${ARCHETYPE_STORAGE_CLASS_NAME}" \
      -Darchetype.storage="${ARCHETYPE_STORAGE}" \
      -Darchetype.host-path="${ARCHETYPE_HOST_PATH}" \
      -Darchetype.create-persistent-volumes="${ARCHETYPE_CREATE_PERSISTENT_VOLUMES}" \
      -Dminio.host="${MINIO_HOST}" \
      -Dminio.port="${MINIO_PORT}" \
      -Dminio.access-key="${MINIO_ACCESS_KEY}" \
      -Dminio.secret-key="${MINIO_SECRET_KEY}" \
      -Dmp.jwt.verify.publickey="${AUTH_PUBLIC_KEY}" \
      -Dauth.private-key="${AUTH_PRIVATE_KEY}" \
      -Dquarkus.http.port=8000 \
      -Dquarkus.http.host=0.0.0.0 \
      -Dquarkus.http.cors=true
