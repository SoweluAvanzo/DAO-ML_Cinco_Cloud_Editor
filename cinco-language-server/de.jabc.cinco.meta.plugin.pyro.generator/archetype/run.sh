#!/bin/bash
# loading environment variables
if [ "$unamestr" = 'FreeBSD' ]; then
    export $(grep -v '^#' env.list | xargs -0)
else
    export $(grep -v '^#' env.list | xargs -d '\n')
fi
source env.list;
# executing pyro-server
java \
	-Dquarkus.datasource.jdbc.url="jdbc:postgresql://${DATABASE_URL}" \
    -Dquarkus.datasource.username="${DATABASE_USER}" \
    -Dquarkus.datasource.password="${DATABASE_PASSWORD}" \
    -Dquarkus.http.port=8000 \
    -Ddebug \
    -jar pyro-server/app.jar;