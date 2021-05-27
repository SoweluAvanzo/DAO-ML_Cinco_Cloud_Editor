FROM docker.io/google/dart:2.4.1
WORKDIR /app
RUN pub global activate webdev
COPY ./pubspec.yaml /app
COPY ./build.yaml /app
RUN pub get
COPY ./web /app/web
COPY ./lib /app/lib
RUN pub run build_runner build -o build

FROM docker.io/library/nginx:1.17.2
COPY default.conf /etc/nginx/conf.d/default.conf
COPY basic_auth /etc/nginx/basic_auth
COPY --from=0 /backend/build /usr/share/nginx/html
