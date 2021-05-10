# 
# Build the angular dart frontend.
# 
# Note: Get MissingPortFileException from the Dart compiler during then build  
#       if Linux Kernel > 5.4 is used on the host.
#
FROM google/dart:2.4.1 as builder-frontend
WORKDIR /app/frontend
COPY ./frontend /app/frontend
RUN pub global activate webdev \
    && pub get \
    && pub run build_runner build -o build

#
# Build the pyro quarkus backend. Move static frontend files to the quarkus resource 
# folder so that we can build a fat jar that includes the frontend. 
#
FROM openjdk:15-jdk-alpine3.11 as builder-backend
WORKDIR /app/backend
COPY ./backend /app/backend
COPY --from=builder-frontend /backend/frontend/build/web/ /app/backend/src/main/resources/META-INF/resources/
# docker does not resolve simlink, need to be resolved manually
COPY --from=builder-frontend /backend/frontend/build/packages/ /app/backend/src/main/resources/META-INF/resources/packages/
RUN ./mvnw clean package -DskipTests

#
# Build the fat pyro quarkus jar.
#
FROM openjdk:15-jdk-alpine3.11
WORKDIR /app
COPY --from=builder-backend /backend/backend/target/*-runner.jar /app/app.jar
COPY --from=builder-backend /backend/backend/target/lib /app/lib
RUN ls -all
EXPOSE 8000
CMD java \
    -Dquarkus.datasource.jdbc.url="jdbc:postgresql://${DATABASE_URL}" \
    -Dquarkus.datasource.username="${DATABASE_USER}" \
    -Dquarkus.datasource.password="${DATABASE_PASSWORD}" \
    -Dquarkus.http.port=8000 \
    -jar app.jar



