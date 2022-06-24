# Developing for the Cinco-Language-Server and Pyro-Model-Server

You don't need the fullstack of the cinco-cloud to develop on the level of the archetype. The project is split in a modular fashion. 

Developing for the archetype happens on three levels:

    Cinco-Cloud Theia-Editor
    Cinco-Language-Server
    Pyro-Model-Server

This Readme is about developing for the latter two.

**NOTE: For dependencies (like Java-Version and such) please look into the Readme-files of the corresponding projects.**

## Cinco-Language-Server

The languageServer for the cinco-languages:
    
    - located: `cinco-language-server``
    - compile: `mvn clean install`

### Developing the language-server

You can open the project as a classic mvn project inside the Eclipse IDE. That means also, that debugging with breakpoints is possible. If you run `mvn clean compile` in the root of the project, you can find the executable artifact with the folder `cinco-language-server\de.jabc.cinco.meta.core.ide\target\language-server`.

**Congratulations! You obtained a `cinco-language-server`!**

#### Short Background of structure:

Each Cinco-Language (Mgl, Msl/Style, CPD) has multiple Java-Packages, that are based on Xtext generated Language-Servers, i.e. combine into a Language-Server. And the Language-Servers of all langauges are connected to a single `cinco-language-server` inside the `de.jabc.cinco.meta.core.ide` package. Beside those packages, the are additional packages, like the `utils`-package to serve recurring procedures and operations. One of the most important packages is the `de.jabc.cinco.meta.plugin.pyro.generator`, which contains the Generator of the language-server.

#### IMPORTANT
**Like said before, developing for the `language-server` works best inside the Eclipse IDE, because it supports Xtend and Xtext and you can also develop interactively with the Debugger. (NOTE: some legacy dependencies sometimes lead to visual errors inside some eclipse instances, but that should not be a problem)**

### Generating a pyro-server
If you compile the project a maven-test will be executed inside `cinco-language-server\de.jabc.cinco.meta.productdefinition.ide`. This Test utilizes a test-project from the contained `test-data/test-project` folder, that contains multiple `mgls` and stuff. This test is used to ensure, that the generator of the language server works correctly. After the test ran successful, a `test-data/pyro` folder will appear, that contains the fully generated sources for the `pyro-model-server`(formally `pyro-product`). This server will supply all cinco-features needed for the `cinco-cloud-product`.

**Congratulations! You obtained a `pyro-model-server-project`!**


## Pyro-Model-Server

Developing for the `pyro-model-server` can be tedious with the wrong mindset. Inside the `pyro` folder you will find several scripts that can be executed for that purpose. Here is a quick guide how to use those scripts and what the do:

### Compiling the Model-Server

0. create a database for the product (This can be reused)

    `docker-compose -f postgres.yml up`

1. and (needed to compile the xtend-files)
    
    `./compile.sh`

    
    With the folder `pyro/pyro-server` you will find the fully compiled `pyro-model-server`.
    **Congratulations! You obtained a `pyro-model-server`!**

2. A: to compile the frontend only (currently no good hotcode-injection):
    
    `./compileFrontend.sh`

2. B: to compile the backend only:
    
    `./compileBackend.sh`

3. to compile the backend dynamically with hotcode-injection:

    `./develop.sh`

4. to just run the compiled pyro-server (faster than develop/debug mode):

    `./run.sh`

**Congratulations! You Executed a `pyro-model-server`!**

### Creating a Graphmodel

0. Install `VSCode`.
1. <a href="/assets/pyro-client-extension-0.0.1.vsix" download="">Download</a> the pyro-client-extension for vscode.
2. Install the extension into `VSCode`.
3. While the `pyro-model-server` runs, right-click into the explorer of your `VSCode`-instance and select `New Graphmodel` in the context-menu.
4. Select a Type and give it a name.
5. If everything went well your client should show a modelling canvas of your new created graphmodel.
6. In your `VSCode`-instance select the `Output view` and select the `PYRO-CLIENT` in the drop-down-menu on the left.
7. Inside the logs, there should be a link in the form of: `http://localhost:8000/#/editor/1?ext=extended_flowgraph&token=asd`
8. write it down for later.

**Up to this point, the procedure can be used to rudimentary testing.**

**Congratulations! You connected to your `pyro-model-server`!**

### Developing Backend

You can open the `app`-folder, that contains the backend, inside the Eclipse IDE as classic maven-project. In combination with the `develop.sh` you can now utilize hotcode-injection.
TIPP: If you also setup remote-debugging-configuration, you can add the Eclipse debugger with breakpoints to your workflow and be super interactive and efficient.

### Developing Frontend

The `webapp`-folder contains the frontend and is best to open in `VSCode`. You need to install `Dart Extension` to have dart support, which is currently the language of the frontend. Using frontend debugging:

When opened you also have a debug-config called `Launch localhost`. In most cases you currently need to modify it. Open the file `webapp/.vscode/launch.js`. Here are multiple key-value-pairs. You need to modify the value of "url": "http://localhost:8000/#/editor/1?ext=extended_flowgraph&token=asd". In `Creating a Graphmodel` you wrote down such a link. You can place it inside here.
Now you can run `Launch localhost` and start developing and debugging the frontend.

**NOTE: You have currently no HotCode-Injection. If you've made changes to the code, you need to run `./compileFrontend.sh` while `./develop.sh` is running to have an interactive experience.**

### Building a DockerImage (broken):

    1. to build the docker-image:

    `./buildDocker.sh`

    2. to run the docker-image:

    `./runDocker.sh`