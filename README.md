# Cinco Cloud Archetype

This project represents an archetypical theia-editor for a cinco-cloud product.

## Structure
- `vsocde-extensions` contains all extensions, that can be developed withing vscode. They are used inside the theia editor by installing the packaged *.vsix into the `web/browser-app/plugins`-folder.
- `web` contains a eclipse theia-editor-project with all needed theia-extensions and files

## Getting started
USE THESE SCRIPTS ONLY FOR DEVELOPMENT PURPOSE!

1. to compile the docker image execute:
    
    `./build.sh`

2. to setup a database for a pyro-application run:

    `docker-compose -f postgres.yml up`

3. to run the docker image execute:

    `./run.sh`

    (It is important that both ports `3000` and `8000` are exposed)

optional:
    the `env.list` contains environment variables which will be used by the run-script.