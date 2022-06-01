#!/bin/bash

cd webapp;
/usr/lib/dart/bin/pub global activate webdev;
/usr/lib/dart/bin/pub get;
/usr/lib/dart/bin/pub run build_runner build -o build;

cd ../;
# copy frontend
mkdir -p app/src/main/resources/META-INF/resources/packages;
cp -r webapp/build/web/* app/src/main/resources/META-INF/resources;
rm -r app/src/main/resources/META-INF/resources/packages;
cp -r webapp/build/packages/ app/src/main/resources/META-INF/resources/packages/;

cd app;
mvn clean package -DskipTests;
cd ../;

# publish pyro-server
mkdir pyro-server;
cp app/target/*-runner.jar pyro-server/app.jar;
cp -r app/target/lib pyro-server/lib;

zip -r pyro-server.zip pyro-server
