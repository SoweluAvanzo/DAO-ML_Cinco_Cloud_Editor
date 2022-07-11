# Developing for the Cinco-Language-Server and Pyro-Model-Server

You don't need the full stack of Cinco Cloud to develop on the archetype level. Cinco Cloud is split in a modular fashion. 

Developing the archetype happens on three levels:

    Cinco Cloud Theia Editor
    Cinco Language Server
    Pyro Model Server

This Readme is about developing for the Cinco Language Server and the Pyro Model Server.

**NOTE: For dependencies (e.g. Java versions and similar) please look into the Readme files of the corresponding projects.**

## Cinco Language Server

The language server for the cinco-languages is located in the `cinco-language-server` folder and can be compiled using `mvn clean install`.

### Developing the language server

You can open the project as a classic Maven project inside the Eclipse IDE. That means also that debugging with breakpoints is possible. If you run `mvn clean package` in the root of the project, you can find the executable artifact within the folder `cinco-language-server/de.jabc.cinco.meta.core.ide/target/language-server/bin`.

**Congratulations! You have built a `cinco-language-server`!**

### Structural Information

Each Cinco-Language (MGL, MSL/Style, CPD) has multiple Java-Packages that are based on Xtext-generated language servers. These language servers are combined into a single `cinco-language-server` inside the `de.jabc.cinco.meta.core.ide` package. Beside those packages, there are additional packages, like the `utils` package to serve recurring procedures and operations. One of the most important packages is `de.jabc.cinco.meta.plugin.pyro.generator`, which contains the generator of the language server.

### IMPORTANT
**Like mentioned before, developing for the `language-server` works best inside the Eclipse IDE, because it supports Xtend and Xtext and you can also develop interactively using the debugger. (NOTE: some legacy dependencies sometimes lead to visual errors inside some eclipse instances, but that should not be a problem)**

### Generating a Pyro Server
If you compile the project a maven test will be executed inside `cinco-language-server/de.jabc.cinco.meta.productdefinition.ide`. This test utilizes a test project from the contained `test-data/test-project` folder, that contains multiple MGLs and additional resources. This test is used to ensure that the generator of the language server works correctly. After the test has run successfully, a `test-data/pyro` folder will be created, that contains the fully generated sources for the `pyro-model-server` (formally called `pyro-product`). This server will supply all Cinco features needed for the Cinco Cloud product.

**Congratulations! You have built a Pyro model server project!**

## Pyro Model Server

Developing for the Pyro model server can be tedious with the wrong mindset. Inside the `pyro` folder you will find several scripts that can be executed to avoid such tediousness. Here is a quick guide how to use these scripts and to describe what they do:

**Note: If you are developing under Windows, you need to copy the `pyro` folder to a location with a small path length (e.g. desktop). Windows limits the length of allowed paths, which can cause problems when compiling the frontend.** 

### Compiling the Model-Server

1. create a database for the product (This can be reused)

    `docker compose -f postgres.yml up`

2. compile the Dart files
    
    `./compile.sh`

    Within the folder `pyro/pyro-server` you will find the fully compiled Pyro model server.
    **Congratulations! You have built a Pyro model server!**

3.  to just run the compiled Pyro server:
    
    `./run.sh`

    **optional** to compile the backend dynamically with hot code injection (slower than run):

    `./develop.sh`

    **Congratulations! You are running a Pyro model server!**

    
4. **optional** to compile the frontend (currently no good hot code injection) and the backend use:

    `./compileFrontend.sh`

    and

    `./compileBackend.sh`

#### Known Database Issues 
Depending on your operating system, the scripts may fail. The `pyro/env.list` file contains environment variables. One of them contains the Docker variable `host.docker.internal`. If you notice, that the database encounters an error:

0. use `docker ps` and write down the name of the container (`NAMES`) that uses the image `postgres:X.X` (e.g. `postgres:11.2`).

1. run the follwing command, where `pyro_postgres_1` has to be replaced with the name of the container you've written down. This will read out an IP adresse that should be `host.docker.internal`:

    `docker inspect pyro_postgres_1  -f '{ { (index (index .NetworkSettings.Ports "5432/tcp") 0).HostIp } }'`

2. replace `host.docker.internal` inside `pyro/env.list` with that IP adresse.

### Creating a Graphmodel

0. Install VSCode.
1. <a href="/assets/pyro-client-extension-0.0.1.vsix" download="">Download</a> the pyro-client-extension for vscode (or use a locally built version).
2. Install the extension into VSCode.
3. While the Pyro model server is running, right-click into the explorer of your VSCode instance and select `New Graphmodel` in the context-menu.
4. Select a graphmodel type and give it a name.
5. If everything went well, your client should show a modelling canvas of your newly created graphmodel.
6. In your VSCode instance select the `Output` view and select `PYRO-CLIENT` in the drop-down-menu.
7. Inside the logs, there should be a link similar to: `http://localhost:8000/#/editor/1?ext=extended_flowgraph&token=asd`
8. Write it down for later.

**Up to this point, the procedure can be used to test rudimentarily.**

**Congratulations! You have connected to your Pyro model server!**

### Developing Backend

You can open the `app` folder that contains the backend inside the Eclipse IDE as a classical Maven project. In combination with the `develop.sh` you can now utilize hot code injection.

*TIP:* If you also setup remote debugging configuration, you can add the Eclipse debugger with breakpoints to your workflow and be super interactive and efficient.

### Developing Frontend

The `webapp` folder contains the frontend and is best opened in `VSCode`. You need to install the `Dart Extension` to have dart support, which is currently the language of the frontend.

You also have access to a debug config called `Launch localhost`. In most cases you have to adjust it for your purposes manually. Open the file `webapp/.vscode/launch.js`, it contains multiple key-value-pairs. You need to modify the value of "url": "http://localhost:8000/#/editor/1?ext=extended_flowgraph&token=asd". In `Creating a Graphmodel` you wrote down such a link.
Now you can run `Launch localhost` and start developing and debugging the frontend.

**NOTE: You do not currently have hot code injection. If you've made changes to the code, you need to run `./compileFrontend.sh` while `./develop.sh` is running to have an interactive experience.**

### Building a Docker image:

1. to build the docker-image of an `cinco-cloud-archetype` utilizing the `pyro-model-server`:

    `./buildDocker.sh`

2. to run the docker-image:

    `./runDocker.sh`