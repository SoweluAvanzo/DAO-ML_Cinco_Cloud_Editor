# Developing for the Theia-Editor

You don't need the fullstack of the cinco-cloud to develop on the level of the archetype. The project is split in a modular fashion. 

Developing for the archetype happens on three levels:

    Cinco-Cloud Theia-Editor
    Cinco-Language-Server
    Pyro-Model-Server

This Readme is about developing for the first.

**NOTE: For dependencies (like Java-Version and such) please look into the Readme-files of the corresponding projects.**

The `cinco-cloud-archetype` contains two folder:

    editor
    vscode-extensions

**NOTE: The `build.sh` and `run.sh` are currently broken, and would be used to build a docker-image for the editor**

The first contains the Theia-Editor with all Theia-Extension that define the archetype.
The second contains all VSCode-Extension, that can be used inside the editor of the archetype.

## Difference between VSCode and Theia Extensions

There is a small difference between VSCode Extensions and Theia Extensions. Both can add almost the same functionality to the editor, can be written in TypeScript, compiled by `yarn` (or `npm`), but differ in code Style. **But** if you develop a `VSCode`-Extension for the `Theia-Editor` it can not be ensured, that it works as expected. For that you will need to test it manually (can be tedious). You will have no real debugging functionality. Developing a Theia-Extension on the other hand, gives full interactive development with hot-code injection, debugging and breakpoints. Also the Theia-API allows more freedom, has less restrictions, but is also less-good documented, than the VSCode-API. What API suits best is use-case dependent.

__TIPP: You need to work with `bad old-fashioned` print-statements/logging
(If you find another more efficient way, please contact one of us :D ).__

## Developing VSCode Extensions

Please look [here](https://code.visualstudio.com/api/get-started/your-first-extension).

## Developing the Theia-Editor

Please look [here](https://theia-ide.org/docs/authoring_extensions/)

### Registering Theia-Extensions
If you created an extensions, you need to register it in several files, since the project is a multi-module project based on `lerna`:

    package.json
    tsconfig.json
    .vscode/launch.json (for debugging, several spots)

Do it analog to all the other extensions.

### Developing Theia-Extensions
If you now want to develop interactively

0. Open the `editor` folder inside `VSCode`.
1. run `yarn` inside the `editor` to compile the whole editor-project.
2. run `yarn watch` to enable `hot-code injection` and interactive compiling.
3. use the predefined debug-configs inside vscode to start the `backend` and the `frontend` separately

**Congratulations! You are now running a Theia-Editor**

At this point you may exerience that, the CINCO-Languages are not supported. This is because the Cinco-Language-Server is apparently not present inside these files. Follow the Section `DEVELOPING: Cinco-Language-Server and Pyro-Model-Server` and compile a `cinco-language-server`, as well as a `pyro-model-server`. Put the resulting servers inside the following folders:

    language-server => cinco-language-server-extension/language-server
    pyro-server => pyro-server-extension/pyro-server

Re-run the debug-configs and...
**Congratulations! You are now running a cinco-cloud-archetype!**

### Tipps

**You can run the `pyro-model-server` local with the `./develop.sh` while the editor is running, if the `pyro-server` is not inside the `pyro-server-extension`. This way you gain all the interactive benefits for developing the `pyro-model-server` AND the `theia-extensions`!**