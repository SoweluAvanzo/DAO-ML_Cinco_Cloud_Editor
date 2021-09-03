#
# Build the angular dart frontend.
#
# Note: Get MissingPortFileException from the Dart compiler during then build
#       if Linux Kernel > 5.4 is used on the host.
#
FROM docker.io/google/dart:2.4.1 as builder-frontend
WORKDIR /app/frontend
RUN pub global activate webdev
COPY ./frontend/pubspec.yaml /app/frontend
COPY ./frontend/build.yaml /app/frontend
RUN pub get
COPY ./frontend/web /app/frontend/web
COPY ./frontend/lib /app/frontend/lib
RUN pub run build_runner build -o build

#
# Build the pyro quarkus backend. Move static frontend files to the quarkus resource
# folder so that we can build a fat jar that includes the frontend.
#
FROM docker.io/library/openjdk:15-jdk-slim as builder-backend
WORKDIR /app/backend
COPY ./backend/.mvn /app/backend/.mvn
COPY ./backend/mvnw /app/backend/
COPY ./backend/pom.xml /app/backend/
RUN ./mvnw verify
COPY ./backend/src /app/backend/src
COPY --from=builder-frontend /app/frontend/build/web/ /app/backend/src/main/resources/META-INF/resources/
COPY --from=builder-frontend /app/frontend/build/packages/ /app/backend/src/main/resources/META-INF/resources/packages/
RUN ./mvnw package -DskipTests

#
# Build the fat pyro quarkus jar.
#
FROM docker.io/library/ubuntu:20.10
WORKDIR /app
RUN apt-get -y update
RUN apt-get -y install runc buildah openjdk-11-jdk
COPY --from=builder-backend /app/backend/target/quarkus-app/ /app
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
    -Dquarkus.http.port=8000 \
    -jar quarkus-run.jar
