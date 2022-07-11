# Developing for the Theia Editor

You don't need the full stack of Cinco Cloud to develop on the archetype level. Cinco Cloud is split in a modular fashion. 

Developing the archetype happens on three levels:

    Cinco Cloud Theia Editor
    Cinco Language Server
    Pyro Model Server

This Readme is about developing for the Cinco Cloud Theia Editor.

**NOTE: For dependencies (e.g. Java versions and similar) please look into the Readme files of the corresponding projects.**

The `cinco-cloud-archetype` contains two folder:

    editor
    vscode-extensions

`editor` contains the Theia editor with all Theia extensions, that define the archetype.
`vscode-extensions` contains all VSCode extensions that can be used inside the editor of the archetype.

## Differences between VSCode and Theia Extensions

There is a small difference between VSCode extensions and Theia extensions. Both can add almost the same functionality to the editor, can be written in TypeScript, be compiled by `yarn` or `npm`, but differ slightly in code style. **Please note** that if you develop a VSCode extension for the Theia editor it can not be ensured that it works as expected. Developing a Theia extension on the other hand, provides full interactive development with hot code injection, debugging and breakpoints. Also the Theia API allows more freedom, has less restrictions, but is also not as well documented as the VSCode API. Which API suits best is use-case specific.

## Developing VSCode Extensions

Please look [here](https://code.visualstudio.com/api/get-started/your-first-extension).

## Developing the Theia-Editor

Please look [here](https://theia-ide.org/docs/authoring_extensions/)

### Registering Theia-Extensions
If you created an extension, you need to register it in several files, since the project is a multi-module project based on `lerna`:

    package.json
    tsconfig.json
    .vscode/launch.json (for debugging, several occurrences)

Do it analogously to all the other extensions.

### Registering VSCode-Extensions
If you created an extension, you need to compile your extension to a `*.vsix` (with `vsce`) and move it into `editor/browser/plugins`. (Currently you have to do this procedure to all extensions inside `vscode-extensions` manually)

### Developing Theia-Extensions
If you now want to develop interactively, do the following:

1. open the `editor` folder with VSCode.
2. run `yarn` inside `editor` to compile the whole editor project.
3. run `yarn watch` to enable hot code injection and interactive compiling.
4. use the predefined VSCode debug configs to start the `backend` and the `frontend` separately.

**Congratulations! You are now running a Theia editor**

At this point you may experience that the Cinco languages are not supported. This is because the Cinco-Language-Server is apparently not present inside these files. Follow the Section `Developing for the Cinco-Language-Server and Pyro-Model-Server` and compile a `cinco-language-server`, as well as a `pyro-model-server`. Put the resulting servers inside the following folders:

    language-server => cinco-language-server-extension/language-server
    pyro-server => pyro-server-extension/pyro-server

Re-run the debug configs and...
**Congratulations! You are now running a cinco-cloud-archetype!**

### Tips

You can run the `pyro-model-server` locally with the `./develop.sh` while the editor is running, if the `pyro-server` is not inside the `pyro-server-extension`. This way you will gain all the interactive benefits for developing the `pyro-model-server` **AND** the `theia-extensions`!

### Building a Docker image:

1. to build the docker-image of the `cinco-cloud-archetype`, including the `cinco-language-server`:

    `./build.sh`

2. to run the docker-image:

    `./run.sh`

3. ***optional for development***: It can be tedious and time-consuming building the full `archetype`. To build it without the `language-server` use:

    `./buildMinimal.sh`