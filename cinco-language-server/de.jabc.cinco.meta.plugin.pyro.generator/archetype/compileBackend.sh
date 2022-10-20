# build backend

# status code of the last command
status=0;

cd app;
mvn clean package -DskipTests;
status=$?
if [[ $status -ne 0 ]]; then
    exit $status;
fi
cd ../;

# publish pyro-server
mkdir pyro-server;
cp app/target/*-runner.jar pyro-server/app.jar;
cp -r app/target/lib pyro-server/lib;