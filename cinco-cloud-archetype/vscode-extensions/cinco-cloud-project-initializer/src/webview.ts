import path = require('path');
import * as vscode from 'vscode';

export function getWebviewContent(context: vscode.ExtensionContext, panel: vscode.WebviewPanel): string {
    const currentTheme = vscode.workspace.getConfiguration('workbench').get('colorTheme') as string;
    const isDarkTheme = currentTheme.toLowerCase().includes('dark');
    const backgroundColor = isDarkTheme ? '#1E1E1E' : '#FFFFFF';
    const textColor = isDarkTheme ? '#D4D4D4' : '#333333';

    const onDiskPath = vscode.Uri.joinPath(context.extensionUri, 'images', 'cinco_cloud_logo.png');
    const logoSrc = panel.webview.asWebviewUri(onDiskPath);

    const exampleProjects = [
        {
            name: 'Flowgraph',
            repoUrl: 'https://ls5gitlab.cs.tu-dortmund.de/cinco-cloud-examples/flowgraph.git',
            branch: 'main'
        },
        {
            name: 'Webstory',
            repoUrl: 'https://ls5gitlab.cs.tu-dortmund.de/cinco-cloud-examples/webstory.git',
            branch: 'main'
        },
    ];

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
                        height: 100vh;
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
                </style>
            </head>
            <body>
                <!-- Initial buttons -->
                <div id="initialView">
                    <img src="${logoSrc}" alt="Cinco Cloud Logo">
                    <h1>Welcome to Cinco Cloud!</h1>
                    <button onclick="showInitializeProject()">Initialize Project</button>
                    <button onclick="showCreateExampleProject()">Create Example Project</button>
                </div>

                <!-- Input for project name -->
                <div id="nameInputView" style="display: none;">
                    <input type="text" pattern="^[A-Za-z]+$" id="projectName" placeholder="Enter project name">
                    <div>
                        <button onclick="showInitialView()">Back</button>
                        <button onclick="initializeProject()">Confirm</button>
                    </div>
                </div>

                <!-- Example projects buttons -->
                <div id="exampleProjectsView" style="display: none;">
                    ${exampleProjects.map((exampleProject) => {
                        return `<button onclick="createExampleProject('${exampleProject.repoUrl}', '${exampleProject.branch}')">Example: ${exampleProject.name}</button>`;
                    }).join('')}
                    <button onclick="showInitialView()">Back</button>
                </div>

                <script>
                    const vscode = acquireVsCodeApi();

                    function showInitializeProject() {
                        const initialView = document.getElementById('initialView');
                        const nameInputView = document.getElementById('nameInputView');

                        initialView.style.display = 'none';
                        nameInputView.style.display = 'flex';
                    }

                    function showCreateExampleProject() {
                        const initialView = document.getElementById('initialView');
                        const exampleProjectsView = document.getElementById('exampleProjectsView');

                        initialView.style.display = 'none';
                        exampleProjectsView.style.display = 'flex';
                    }

                    function initializeProject() {
                        const projectName = document.getElementById('projectName').value;
                        vscode.postMessage({ command: 'initializeProject', projectName: projectName });
                    }

                    function createExampleProject(repoUrl, branch) {
                        vscode.postMessage({ command: 'createExampleProject', repoUrl: repoUrl, branch: branch });
                    }

                    function showInitialView() {
                        const initialView = document.getElementById('initialView');
                        const nameInputView = document.getElementById('nameInputView');
                        const exampleProjectsView = document.getElementById('exampleProjectsView');
                    
                        initialView.style.display = 'flex';
                        nameInputView.style.display = 'none';
                        exampleProjectsView.style.display = 'none';
                    }
                </script>
            </body>
        </html>
    `;
}
