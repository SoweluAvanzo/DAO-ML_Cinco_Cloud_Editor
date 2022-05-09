# build backend
cd app;
    mvn clean package -DskipTests;
cd ../;

# publish pyro-server
mkdir pyro-server;
cp app/target/*-runner.jar pyro-server/app.jar;
cp -r app/target/lib pyro-server/lib;