# If you want to Test
1. run for database setup:

    `docker-compose -f postgres.yml`

2. run to compile the pyro-editor:

    `./compile.sh`

3. the folder `pyro-server` contains the executable jar-files

# if you just want to develop

1. run for database setup:

    `docker-compose -f postgres.yml up`

2. to compile the frontend (currently no good hotcode-injection):
    
    `./compileFrontend.sh`

3. to compile the backend dynamically with hotcode-injection:

    `./develop.sh`
