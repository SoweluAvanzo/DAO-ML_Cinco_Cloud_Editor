<div align='center'>

<br />

<img src="https://gitlab.com/scce/cinco-cloud/-/raw/main/docs/vuepress/src/.vuepress/public/assets/cinco_cloud_logo.png" width="10%" alt="Cinco Cloud Logo" />

<h2>CINCO CLOUD - ARCHETYPE</h2>

</div>

## Contents

This project represents an archetypical theia-editor for a cinco-cloud product.

## Documentation

Indepth Documentation is under construction and will be found as part of our [website](https://scce.gitlab.io/cinco-cloud/).


### Structure

- `vscode-extensions` contains all extensions, that can be developed within vscode. They are used inside the theia editor by installing the packaged *.vsix into the `editor/browser-app/plugins`-folder.
- `editor` contains a eclipse theia-editor-project with all needed theia-extensions and files
- `cinco-ls` contains the language-server project for the Cinco-DSLs (CPD, MGL, Style)

### Getting started

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

## Related projects and Used Technologies

[Theia][theia] - We are using Theia as a foundation for our editor.

[Typescript][typescript] - Programming language.

[GLSP][glsp] - Our graphical editor is based on the GLSP project.

[Language Server Protocol][lsp] - A protocol for IDE agnostic programming language development.

[Langium][langium] - Textual meta-languages are provided using Langium.

[Sprotty][sprotty] - Used to visualize and edit graphical models.

[//]: # "Source definitions"
[theia]: https://github.com/eclipse-theia/theia "Theia"
[typescript]: https://www.typescriptlang.org/ "Typescript"
[glsp]: https://github.com/eclipse-glsp/glsp "The Graphical Language Server Platform"
[lsp]: https://microsoft.github.io/language-server-protocol/ "Language Server Protocol"
[langium]: https://langium.org/ "Langium"
[sprotty]: https://sprotty.org/ "Sprotty"

## License

[EPL2](https://www.eclipse.org/legal/epl-2.0/)
