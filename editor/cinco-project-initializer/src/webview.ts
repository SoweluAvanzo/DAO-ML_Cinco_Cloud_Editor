import * as vscode from "vscode";
import * as process from "process";

const EDITOR_TYPE_KEY = "EDITOR_TYPE";
const LANGUAGE_EDITOR = "LANGUAGE_EDITOR";

export async function getWebviewContent(
  context: vscode.ExtensionContext,
  panel: vscode.WebviewPanel
): Promise<string> {
    const editorType = process.env[EDITOR_TYPE_KEY];
    const isLanguageEditor = editorType === LANGUAGE_EDITOR;

    const currentTheme = vscode.workspace
        .getConfiguration("workbench")
        .get("colorTheme") as string;
    const isDarkTheme = currentTheme.toLowerCase().includes("dark");
    const backgroundColor = isDarkTheme ? "#1E1E1E" : "#FFFFFF";
    const textColor = isDarkTheme ? "#D4D4D4" : "#333333";

    const onDiskPath = vscode.Uri.joinPath(
        context.extensionUri,
        "images",
        "cinco-cloud-logo.png"
    );
    const logoSrc = panel.webview.asWebviewUri(onDiskPath);

    const exampleProjects = isLanguageEditor
        ? [
            {
                name: "Flowgraph",
                repoUrl:
                "https://ls5gitlab.cs.tu-dortmund.de/cinco-cloud-examples/flowgraph.git",
                branch: "main",
            },
            {
                name: "Webstory",
                repoUrl:
                "https://ls5gitlab.cs.tu-dortmund.de/cinco-cloud-examples/webstory.git",
                branch: "main",
            },
            ]
        : [];

    const modelTypes = await fetchModelTypes();

    return `
        <html>
            <head>
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        background-color: ${backgroundColor};
                        color: ${textColor};
                        padding: 16px;
                        display: flex;
                        flex-direction: column;
                        justify-content: center;
                        align-items: center;
                        height: 100%;
                    }
        
                    body > div {
                        display: flex;
                        flex-direction: column;
                        align-items: center;
                        gap: 16px;
                    }

                    img {
                        max-width: 256px;
                    }
        
                    button {
                        background-color: #333333;
                        color: #D4D4D4;
                        border: none;
                        padding: 8px 16px;
                        margin: 4px;
                        border-radius: 3px;
                        cursor: pointer;
                        transition: background-color 0.2s;
                    }
        
                    button:hover {
                        background-color: #444444;
                    }
        
                    input[type="text"] {
                        padding: 8px;
                        border: 1px solid #333;
                        background-color: #3C3C3C;
                        color: #D4D4D4;
                        border-radius: 3px;
                        margin-right: 4px;
                    }
        
                    select[type="text"] {
                        padding: 8px;
                        border: 1px solid #333;
                        background-color: #3C3C3C;
                        color: #D4D4D4;
                        border-radius: 3px;
                        margin-right: 4px;
                    }
                </style>
            </head>
            <body>
                <!-- Initial buttons -->

                <img src="${logoSrc}" alt="Cinco Cloud Logo">
                <div id="initialView">
                    <h1>Welcome to Cinco Cloud!</h1>
                    ${
                      isLanguageEditor
                        ? '<button onclick="showInitializeProject()">Initialize Project</button>'
                        : ""
                    }
                    ${
                      exampleProjects.length > 0
                        ? `<button onclick="showCreateExampleProject()">Create Example Project</button>`
                        : ``
                    }
                    ${
                        modelTypes.length > 0
                            ? `<button onclick="showInitializeModels()">Create Model</button>`
                            : exampleProjects.length <= 0 && !isLanguageEditor
                            ? `This editor has no registered model types.`
                            : ""
                    }
                </div>

                <!-- Input for project name -->
                <div id="projectNameInputView" style="display: none;">                        
                    <h1>Create a Project</h1>
                    <input type="text" pattern="^[A-Za-z]+$" id="projectName" placeholder="Enter project name">
                    <div>
                        <button onclick="showInitialView()">Back</button>
                        <button onclick="initializeProject()">Confirm</button>
                    </div>
                </div>

                <!-- Input for model name -->
                <div id="modelNameInputView" style="display: none;">
                    <h1>Create a Model</h1>
                    <input type="text" pattern="^[A-Za-z]+$" id="modelName" placeholder="Enter model name">
                    <span>
                    <label for="modelType">Chose model type:</label>
                    <select type="text" name="modelType" id="modelType">
                        ${modelTypes.map(
                            (type) =>
                              `<option value="${type.diagramExtension}">${type.label}</option>`
                        ).join(`\n`)}
                    </select>
                    </span>
                    <div>
                        <button onclick="showInitialView()">Back</button>
                        <button onclick="initializeModels()">Confirm</button>
                    </div>
                </div>

                <!-- Example projects buttons -->
                <div id="exampleProjectsView" style="display: none;">
                    ${exampleProjects
                      .map((exampleProject) => {
                        return `<button onclick="createExampleProject('${exampleProject.repoUrl}', '${exampleProject.branch}')">Example: ${exampleProject.name}</button>`;
                      })
                      .join("")}
                    <button onclick="showInitialView()">Back</button>
                </div>

                <script>
                    const vscode = acquireVsCodeApi();

                    function showInitialView() {
                        const exampleProjectsView = document.getElementById('exampleProjectsView');
                        const initialView = document.getElementById('initialView');
                        const modelNameInputView = document.getElementById('modelNameInputView');
                        const projectNameInputView = document.getElementById('projectNameInputView');
                    
                        exampleProjectsView.style.display = 'none';
                        modelNameInputView.style.display = 'none';
                        projectNameInputView.style.display = 'none';
                        initialView.style.display = 'flex';
                    }

                    function showInitializeProject() {
                        const exampleProjectsView = document.getElementById('exampleProjectsView');
                        const initialView = document.getElementById('initialView');
                        const modelNameInputView = document.getElementById('modelNameInputView');
                        const projectNameInputView = document.getElementById('projectNameInputView');

                        exampleProjectsView.style.display = 'none';
                        initialView.style.display = 'none';
                        modelNameInputView.style.display = 'none';
                        projectNameInputView.style.display = 'flex';
                    }

                    function showInitializeModels() {
                        const exampleProjectsView = document.getElementById('exampleProjectsView');
                        const initialView = document.getElementById('initialView');
                        const modelNameInputView = document.getElementById('modelNameInputView');
                        const projectNameInputView = document.getElementById('projectNameInputView');

                        exampleProjectsView.style.display = 'none';
                        initialView.style.display = 'none';
                        projectNameInputView.style.display = 'none';
                        modelNameInputView.style.display = 'flex';
                    }

                    function showCreateExampleProject() {
                        const exampleProjectsView = document.getElementById('exampleProjectsView');
                        const initialView = document.getElementById('initialView');
                        const modelNameInputView = document.getElementById('modelNameInputView');
                        const projectNameInputView = document.getElementById('projectNameInputView');

                        initialView.style.display = 'none';
                        modelNameInputView.style.display = 'none';
                        projectNameInputView.style.display = 'none';
                        exampleProjectsView.style.display = 'flex';
                    }

                    function initializeProject() {
                        const projectName = document.getElementById('projectName').value;
                        vscode.postMessage({ command: 'initializeProject', projectName: projectName });
                    }

                    function initializeModels() {
                        const modelName = document.getElementById('modelName').value;
                        const modelType = document.getElementById('modelType').value;
                        vscode.postMessage({ command: 'initializeModelFile', modelName: modelName, modelType: modelType });
                    }

                    function createExampleProject(repoUrl, branch) {
                        vscode.postMessage({ command: 'createExampleProject', repoUrl: repoUrl, branch: branch });
                    }
                </script>
            </body>
        </html>
    `;
}

async function fetchModelTypes() {
  // fetch modelTypes
  console.log("ProjectInitializer: fetching modelTypes...");
  const modelTypesAccessible = (await vscode.commands.getCommands()).indexOf(
    "cinco.meta_spec"
  );
  if (!modelTypesAccessible) {
    console.log("ProjectInitializer: no modelTypes accessible.");
  }
  const metaSpec =
    (await vscode.commands.getCommands()).indexOf("cinco.meta_spec") >= 0
      ? await vscode.commands.executeCommand("cinco.meta_spec")
      : undefined;
  const modelTypes = metaSpec
    ? ((metaSpec as any).graphTypes as any[]).map((g) => ({
        diagramExtension: g.diagramExtension,
        label: g.label,
      }))
    : [];
  console.log(`ProjectInitializer: found ${modelTypes.length} modelTypes!`);
  return modelTypes;
}
