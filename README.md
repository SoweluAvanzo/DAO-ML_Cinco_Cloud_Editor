# DAO-ML Editor based on Cinco Cloud
This project concerns the implementation of a proof-of-concept editor for DAO-ML using Cinco Cloud. The project folder contains all the files necessary to execute the editor, generate and edit DAO-ML files (.dao), and to automatically generate the corresponding Solidity code. As this project is the result of the integration of the DAO-ML to Solidity translator as a component of the Cinco Cloud architecture, we refer the reader to the following repositories for the original projects:
- [Cinco Cloud] https://gitlab.com/scce/cinco-cloud for the complete Cinco Cloud documentation and licenses.
- [DAO-ML to Solidity Translator] https://github.com/SoweluAvanzo/DAO-ML_to_Solidity.
  
In addition to the files present in the original project repositories, an implementation of the DAO-ML language is given using the Meta-Style Language and Meta-Graph Language, that enable the implementation of visual modeling languages using Cinco Cloud.
Furthermore, a codec that generates XML files complying with the DAO-ML schema incorporating Cinco Cloud native properties is implemented and integrated with Cinco Cloud.
# License Information
This project integrates multiple components, each governed by its respective license. Below is the licensing information for the different parts of this project:
- DAO-ML to Solidity Translator: All files related to the DAO-ML to Solidity translator within this repository are governed by the license specified in the original DAO-ML to Solidity Translator repository. Please refer to the original repository for the complete license details.
- Cinco Cloud Core Features: All files concerning the core features of Cinco Cloud are governed by the license specified in the Cinco Cloud repository. Please consult the Cinco Cloud repository for detailed licensing information.
- Project-Specific Additions: The files specific to this project, including the DAO-ML language implementation and the XML codec for DAO-ML schema with Cinco Cloud properties, are licensed under the Eclipse Public License 2.0 (EPL-2.0).
[EPL2](https://www.eclipse.org/legal/epl-2.0/)

# How to Use
To use the editor, we recommend executing locally the Cinco Cloud Archetype according to the instructions reported below from the original repository:

1. to compile the docker image execute **from the root of the project**:

    `./build.sh`

2. to run the docker image execute:

    `./run.sh`

    (It is important that both ports `3000` and `8000` are exposed)

optional:
    the `env.list` contains environment variables which will be used by the run-script.

## Related projects and Used Technologies

[Cinco Cloud][cc] - This editor is based on the Cinco Cloud Archetype.

[Theia][theia] - We are using Theia as a foundation for our editor.

[Typescript][typescript] - Programming language.

[GLSP][glsp] - Our graphical editor is based on the GLSP project.

[Language Server Protocol][lsp] - A protocol for IDE agnostic programming language development.

[Langium][langium] - Textual meta-languages are provided using Langium.

[Sprotty][sprotty] - Used to visualize and edit graphical models.

[//]: # "Source definitions"
[cc]: https://gitlab.com/scce/cinco-cloud "Cinco Cloud"
[theia]: https://github.com/eclipse-theia/theia "Theia"
[typescript]: https://www.typescriptlang.org/ "Typescript"
[glsp]: https://github.com/eclipse-glsp/glsp "The Graphical Language Server Platform"
[lsp]: https://microsoft.github.io/language-server-protocol/ "Language Server Protocol"
[langium]: https://langium.org/ "Langium"
[sprotty]: https://sprotty.org/ "Sprotty"

## Contributors
The development and integration of the project specific features, including the modeling language definitions in MGL and MSL and the codec for this project were conducted by Daniel Sami Mitwalli.