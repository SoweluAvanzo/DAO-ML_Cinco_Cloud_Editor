# copy frontend
mkdir app/src/main/resources/META-INF;
mkdir app/src/main/resources/META-INF/resources;
mkdir app/src/main/resources/META-INF/resources/packages;
cp -r webapp/build/web/* app/src/main/resources/META-INF/resources;
rm -r app/src/main/resources/META-INF/resources/packages;
cp -r webapp/build/packages/ app/src/main/resources/META-INF/resources/packages/;

# build backend
cd app;
    mvn clean package -DskipTests;
cd ../;

# publish pyro-server
mkdir pyro-server;
cp app/target/*-runner.jar pyro-server/app.jar;
cp -r app/target/lib pyro-server/lib;