# CINCO-CLOUD
This is the branch for the `CINCO-Cloud` Project.

## Project Structure

The cinco-cloud consists of a frontend and a backend.

The cinco-extension is located in the `cinco-extension/`. Specific documentation can be found in the [cinco-extension README](cinco-extension/README.md)

The cinco-languageserver is located in the `backend/releng/de.jabc.cinco.meta.core.parent`. Specific documentation can be found in the [README](backend/releng/de.jabc.cinco.meta.core.parent/README.md)

The frontend is located in the `web/` folder and frontend specific documentation can be found in the [frontend README](web/README.md)

The backend is located in the `backend/` folder and backend specific documentation can be found in the [backend README](backend/README.md)

## Used Projects

This project relies on other projects:

- https://github.com/eclipse-emfcloud/coffee-editor
- https://github.com/eclipsesource/jsonforms
- https://github.com/eclipse-glsp/glsp
- https://github.com/eclipse-emfcloud/emfcloud-modelserver
- https://github.com/eclipse-emfcloud/emfcloud-modelserver-theia
- https://github.com/eclipse-emfcloud/theia-tree-editor

Parts of this README are taken from the coffee-editor.

## Prerequisites

### Dependencies
- Java 11 (or greater)
- NodeJS (version 10.17)
- Yarn
- maven

NOTE: we recommend using [nvm](https://github.com/creationix/nvm#install-script) to use NodeJS 10.17 for development use.

    nvm install 10.17
    nvm use 10.17
    npm install -g yarn

For maven please check the installation documentation for [maven](http://maven.apache.org/install.html).

### Install linux packages (if necessary).

    sudo apt-get install g++-4.8 libsecret-1-dev xvfb libx11-dev libxkbfile-dev libxml2-utils

### Install python (needed from theia dependencies):

Please check the installation description [here](https://github.com/nodejs/node-gyp#installation).

On Windows the most reliable way seems to be to install Python and set `npm config set python "C:\Path\To\python.exe"`.

## Getting started

Initialize submodules:

    git submodule init

    git submodule update

Build the cinco-cloud:

    ./run.sh

Run the cinco-cloud:

    ./run.sh -r

Open http://localhost:3000 in the browser.

## The build and run script

The `run.sh` script provides funtionality to build the cinco-cloud, download used libraries, and run the IDE.
Every part step can be executed independently from each other by using the corresponding paramater:

`-c`: cleans the workspace

`-b`: Builds the backend services

`-f`: Builds the frontend shown in the web browser

`-r`: Runs the cinco-cloud and exposes it at http://localhost:3000

## Workspace
If you want initially specific files to show up in the workspace-folder of the theia-application,
put them into `backend/example/Example`.

## Docker

Build the docker-image:

    docker build -t editor .

Run the docker-image:

    docker run -p 0.0.0.0:3000:3000 --rm editor

Open http://localhost:3000 in the browser.
