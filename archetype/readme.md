# If you want to Test
1. run for database setup:

    `docker-compose -f postgres.yml`

2. run to compile the pyro-editor:

    `./compile.sh`

3. the folder `pyro-server` contains the executable jar-files

# if you just want to develop

1. take the steps from above (needed to compile the xtend-files):

    `docker-compose -f postgres.yml up`
    
    and 
    
    `./compile.sh`

2. to compile the frontend only (currently no good hotcode-injection):
    
    `./compileFrontend.sh`

3. to compile the backend dynamically with hotcode-injection:

    `./develop.sh`