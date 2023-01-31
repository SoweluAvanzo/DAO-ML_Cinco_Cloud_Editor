FROM docker.io/library/maven:3.8.3-adoptopenjdk-15 as builder
WORKDIR /app
COPY ./resources /app/resources
COPY ./workspace-builder/pom.xml /app/workspace-builder/pom.xml
COPY ./workspace-builder/.mvn /app/workspace-builder/.mvn
COPY ./workspace-builder/mvnw /app/workspace-builder/mvnw
RUN cd /app/workspace-builder && mvn verify -DskipTests
COPY ./workspace-builder /app/workspace-builder
RUN cd /app/workspace-builder && mvn clean install package -DskipTests
RUN cd /app/workspace-builder/target

FROM docker.io/library/maven:3-adoptopenjdk-11 as dart-downloader
WORKDIR /downloads
RUN curl -O https://storage.googleapis.com/dart-archive/channels/stable/release/2.4.1/sdk/dartsdk-linux-arm64-release.zip
RUN apt update
RUN apt install -y unzip
RUN unzip /downloads/dartsdk-linux-arm64-release.zip

FROM docker.io/library/maven:3-adoptopenjdk-11
WORKDIR /app
RUN apt update && apt install -y zip
COPY ./workspace-builder/scripts /app/scripts
COPY --from=builder /app/workspace-builder/target/quarkus-app/ /app
COPY --from=dart-downloader /downloads/dart-sdk /app/dart-sdk
ENV PATH="${PATH}:/app/dart-sdk/bin"
EXPOSE 8000
CMD java \
    -Damqp-host="${AMPQ_HOST}" \
    -Damqp-port="${AMPQ_PORT}" \
    -Damqp-username="${AMPQ_USERNAME}" \
    -Damqp-password="${AMPQ_PASSWORD}" \
    -Dminio.host="${MINIO_HOST}" \
    -Dminio.port="${MINIO_PORT}" \
    -Dminio.access-key="${MINIO_ACCESS_KEY}" \
    -Dminio.secret-key="${MINIO_SECRET_KEY}" \
    -jar quarkus-run.jar
