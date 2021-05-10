FROM google/dart:2.4.1
WORKDIR /app
COPY . .
RUN pub global activate webdev \
 && pub get \
 && /root/.pub-cache/bin/webdev build

FROM nginx:1.17.2
COPY default.conf /etc/nginx/conf.d/default.conf
COPY basic_auth /etc/nginx/basic_auth
COPY --from=0 /backend/build /usr/share/nginx/html
