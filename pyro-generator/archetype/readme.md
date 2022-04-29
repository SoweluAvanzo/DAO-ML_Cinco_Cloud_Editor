# Dependencies

- You must have Dart version [2.4.1](https://storage.googleapis.com/dart-archive/channels/stable/release/2.4.1/linux_packages/dart_2.4.1-1_amd64.deb) (!)
- Maven 3.6.2 and above
- Java 11 and above

# If you want to Test
1. run for database setup:

    `docker-compose -f postgres.yml`

2. run to compile the pyro-editor:

    `./compile.sh`

3. the folder `pyro-server` contains the executable jar-files

# If you just want to develop

1. take the steps from above (needed to compile the xtend-files):

    `docker-compose -f postgres.yml up`

    and 
    
    `./compile.sh`

2. to compile the frontend only (currently no good hotcode-injection):
    
    `./compileFrontend.sh`

2. to compile the backend only:
    
    `./compileBackend.sh`

3. to compile the backend dynamically with hotcode-injection:

    `./develop.sh`

4. to just run the compiled pyro-server:

    `./run.sh`

# Build Cinco-Cloud-Product

1. to build the docker-image:

    `./buildDocker.sh`

2. to run the docker-image:

    `./runDocker.sh`