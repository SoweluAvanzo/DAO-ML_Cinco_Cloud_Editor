#
# Build the pyro quarkus backend.
#
FROM docker.io/library/openjdk:15-jdk-alpine3.11 as builder-backend
WORKDIR /app/backend
COPY ./.mvn /app/backend/.mvn
COPY ./mvnw /app/backend/
COPY ./pom.xml /app/backend/
RUN ./mvnw verify
COPY ./src /app/backend/src
RUN ./mvnw package -DskipTests

#
# Build the pyro quarkus jar.
#
FROM docker.io/library/ubuntu:20.10
WORKDIR /app
RUN apt-get -y update
RUN apt-get -y install runc buildah openjdk-11-jdk
COPY --from=builder-backend /app/backend/target/quarkus-app/ /app
EXPOSE 8000
VOLUME ["/app/data"]
CMD java \
    -Dquarkus.datasource.jdbc.url="jdbc:postgresql://${DATABASE_URL}" \
    -Dquarkus.datasource.username="${DATABASE_USER}" \
    -Dquarkus.datasource.password="${DATABASE_PASSWORD}" \
    -Dquarkus.http.port=8000 \
    -Damqp-host="${AMPQ_HOST}" \
    -Damqp-port="${AMPQ_PORT}" \
    -Damqp-username="${AMPQ_USERNAME}" \
    -Damqp-password="${AMPQ_PASSWORD}" \
    -Dpodman.registry.host="${PODAN_REGISTRY_HOST}" \
    -Dpodman.registry.port="${PODAN_REGISTRY_PORT}" \
    -Dpodman.registry.api.port="${PODAN_REGISTRY_API_PORT}" \
    -Dkubernetes.namespace="${KUBERNETES_NAMESPACE}" \
    -jar quarkus-run.jar
