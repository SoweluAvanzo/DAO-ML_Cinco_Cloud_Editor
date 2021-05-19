#
# Build the pyro quarkus backend.
#
FROM docker.io/library/openjdk:15-jdk-alpine3.11 as builder-backend
WORKDIR /app/backend
COPY ./app /app/backend
RUN ./mvnw verify
RUN ./mvnw package -DskipTests

#
# Build the fat pyro quarkus jar.
#
FROM docker.io/library/openjdk:15-jdk-alpine3.11
WORKDIR /app
COPY --from=builder-backend /app/backend/target/*-runner.jar /app/app.jar
COPY --from=builder-backend /app/backend/target/lib /app/lib
RUN ls -all
EXPOSE 8000
CMD java \
    -Dquarkus.datasource.jdbc.url="jdbc:postgresql://${DATABASE_URL}" \
    -Dquarkus.datasource.username="${DATABASE_USER}" \
    -Dquarkus.datasource.password="${DATABASE_PASSWORD}" \
    -Dquarkus.http.port=8000 \
    -Damqp-host="${AMPQ_HOST}" \
    -Damqp-port="${AMPQ_PORT}" \
    -Damqp-username="${AMPQ_USERNAME}" \
    -Damqp-password="${AMPQ_PASSWORD}" \
    -jar app.jar
