# Cinco Cloud Archetype

This project represents an archetypical theia-editor for a cinco-cloud product.

## Structure

- `vscode-extensions` contains all extensions, that can be developed within vscode. They are used inside the theia editor by installing the packaged *.vsix into the `editor/browser-app/plugins`-folder.
- `editor` contains a eclipse theia-editor-project with all needed theia-extensions and files
- `cinco-ls` contains the language-server project for the Cinco-DSLs (CPD, MGL, Style)

## Getting started

USE THESE SCRIPTS ONLY FOR DEVELOPMENT PURPOSE!

1. to compile the docker image execute **from the root of the cinco-cloud project**:
    
    `docker build -f cinco-cloud-archetype/Dockerfile -t editor .`

2. to setup a database for a pyro-application run:

    `docker compose -f postgres.yml up`

3. to run the docker image execute:

    `./run.sh`

    (It is important that both ports `3000` and `8000` are exposed)

optional:
    the `env.list` contains environment variables which will be used by the run-script.
