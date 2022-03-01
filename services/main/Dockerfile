## build from parent directory: docker build -f main/Dockerfile .

#
# Build the angular dart frontend.
#
# Note: Get MissingPortFileException from the Dart compiler during then build
#       if Linux Kernel > 5.4 is used on the host.
#

# TODO: uncomment this as soon as the branch is merged with the main branch
#FROM docker.io/library/node:16.14.0-buster-slim as builder-frontend
#WORKDIR /app/frontend
#COPY ./main/frontend/package.json /app/frontend
#COPY ./main/frontend/package-lock.json /app/frontend
#RUN npm install
#COPY ./main/frontend/ /app/frontend
#RUN npm run build

#
# Build the quarkus backend. Move static frontend files to the quarkus resource
# folder so that we can build a fat jar that includes the frontend.
#
FROM docker.io/library/openjdk:15-jdk-slim as builder-backend
WORKDIR /app/main/backend
COPY ./resources /app/resources
COPY ./main/backend/.mvn /app/main/backend/.mvn
COPY ./main/backend/mvnw /app/main/backend/
COPY ./main/backend/pom.xml /app/main/backend/
RUN ./mvnw verify
COPY ./main/backend/src /app/main/backend/src
# TODO: uncomment this as soon as the branch is merged with the main branch
#COPY --from=builder-frontend /app/frontend/dist/cinco-cloud-frontend/ /app/main/backend/src/main/resources/META-INF/resources/
RUN ./mvnw package -DskipTests

#
# Run the fat quarkus jar.
#
FROM docker.io/library/openjdk:15-jdk-slim
WORKDIR /app
COPY --from=builder-backend /app/main/backend/target/quarkus-app/ /app
EXPOSE 8000 9000
CMD java \
    -Dquarkus.datasource.jdbc.url="jdbc:postgresql://${DATABASE_URL}" \
    -Dquarkus.datasource.username="${DATABASE_USER}" \
    -Dquarkus.datasource.password="${DATABASE_PASSWORD}" \
    -Damqp-host="${AMPQ_HOST}" \
    -Damqp-port="${AMPQ_PORT}" \
    -Damqp-username="${AMPQ_USERNAME}" \
    -Damqp-password="${AMPQ_PASSWORD}" \
    -Dpodman.registry.host="${PODAN_REGISTRY_HOST}" \
    -Dpodman.registry.port="${PODAN_REGISTRY_PORT}" \
    -Dpodman.registry.api.port="${PODAN_REGISTRY_API_PORT}" \
    -Dkubernetes.namespace="${KUBERNETES_NAMESPACE}" \
    -Dcincocloud.host="${CINCO_CLOUD_HOST}" \
    -Dcincocloud.password.secret="${CINCO_CLOUD_PASSWORD_SECRET}" \
    -Dcincocloud.archetype.image.tag="${CINCO_CLOUD_ARCHETYPE_IMAGE_TAG}" \
    -Dcincocloud.use-ssl="${CINCO_CLOUD_USE_SSL}" \
    -Dquarkus.http.port=8000 \
    -jar quarkus-run.jar
