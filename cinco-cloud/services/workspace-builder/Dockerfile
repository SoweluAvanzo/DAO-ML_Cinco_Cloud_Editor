FROM docker.io/library/openjdk:11-slim-buster as builder
WORKDIR /app
COPY ./resources /app/resources
COPY ./workspace-builder/pom.xml /app/workspace-builder/pom.xml
COPY ./workspace-builder/.mvn /app/workspace-builder/.mvn
COPY ./workspace-builder/mvnw /app/workspace-builder/mvnw
RUN cd /app/workspace-builder && ./mvnw verify
COPY ./workspace-builder /app/workspace-builder
RUN cd /app/workspace-builder && ./mvnw clean install package -DskipTests
RUN cd /app/workspace-builder/target

FROM quay.io/buildah/stable:v1.23.3
WORKDIR /app
COPY --from=builder /app/workspace-builder/target/quarkus-app/ /app
ENV JAVA_HOME=/opt/java/openjdk
COPY --from=eclipse-temurin:11 $JAVA_HOME $JAVA_HOME
ENV PATH "${JAVA_HOME}/bin:${PATH}"
EXPOSE 8000
CMD java \
    -Damqp-host="${AMPQ_HOST}" \
    -Damqp-port="${AMPQ_PORT}" \
    -Damqp-username="${AMPQ_USERNAME}" \
    -Damqp-password="${AMPQ_PASSWORD}" \
    -Dcincocloud.docker.registry.host="${DOCKER_REGISTRY_HOST}" \
    -Dcincocloud.docker.registry.port="${DOCKER_REGISTRY_PORT}" \
    -Dcincocloud.archetype.registry.url="${ARCHETYPE_REGISTRY_URL}" \
    -Dcincocloud.archetype.registry.username="${ARCHETYPE_REGISTRY_USERNAME}" \
    -Dcincocloud.archetype.registry.password="${ARCHETYPE_REGISTRY_PASSWORD}" \
    -Dcincocloud.archetype.image.tag="${CINCO_CLOUD_ARCHETYPE_IMAGE_TAG}" \
    -jar quarkus-run.jar