## build from parent directory: docker build -f main/Dockerfile .

#
# Build the angular frontend.
#
FROM docker.io/library/node:16.14.0-buster-slim as builder-frontend
WORKDIR /app/frontend
COPY ./main/frontend/*.json /app/frontend/
RUN npm install
COPY ./main/frontend/src /app/frontend/src
RUN env NG_BUILD_MANGLE=false npm run build --configuration=production

#
# Build the quarkus backend. Move static frontend files to the quarkus resource
# folder so that we can build a fat jar that includes the frontend.
#
FROM docker.io/library/maven:3.8.3-adoptopenjdk-15 as builder-backend
WORKDIR /app/main/backend
COPY ./resources /app/resources
COPY ./main/backend/.mvn /app/main/backend/.mvn
COPY ./main/backend/mvnw /app/main/backend/
COPY ./main/backend/pom.xml /app/main/backend/
RUN mvn verify -DskipTests
COPY ./main/backend/src /app/main/backend/src
COPY --from=builder-frontend /app/frontend/dist/cinco-cloud-frontend/ /app/main/backend/src/main/resources/META-INF/resources/
RUN mvn package -DskipTests

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
    -Dkubernetes.namespace="${KUBERNETES_NAMESPACE}" \
    -Dcincocloud.host="${CINCO_CLOUD_HOST}" \
    -Dcincocloud.password.secret="${CINCO_CLOUD_PASSWORD_SECRET}" \
    -Dcincocloud.environment="${ENVIRONMENT}" \
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
    -jar quarkus-run.jar
