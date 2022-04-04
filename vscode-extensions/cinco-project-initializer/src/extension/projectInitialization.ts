import * as path from 'path'
import * as fse from 'fs-extra'
import * as vscode from 'vscode'
import { workbenchOutput, extensionContext } from './main'
import { MessageToClient, MessageToServer } from '../common/model'
import { initializeScaffold } from './scaffold'
import { getWebviewContent } from './webview-template'
import { getWorkspaceFsPath } from './workspace'
import { isDirectoryEmpty } from './filesystem-helper'

const exampleFolder = "exampleFiles/";

export async function openProjectInitializationView(
    currentPanel: vscode.WebviewPanel | null,
    setCurrentPanel: (panel: vscode.WebviewPanel | null) => void,
) {
    const workspaceFsPath = getWorkspaceFsPath();

    if (workspaceFsPath === null) {
        workbenchOutput.appendLine(
            'No workspace loaded, not showing initialization page'
        );
        return;
    }

    if (currentPanel != null) {
        currentPanel.reveal();
        return;
    }

    const panel = vscode.window.createWebviewPanel(
        'project-initialization',
        'Project Initialization',
        vscode.ViewColumn.Active,
        {
            enableScripts: true,
            localResourceRoots: [
                vscode.Uri.file(extensionContext.extensionPath),
            ],
        },
    );

    function postMessage(message: MessageToClient): void {
        panel.webview.postMessage(message);
    }

    setCurrentPanel(panel);

    panel.webview.html = getWebviewContent(
        panel.webview,
        panel.webview.asWebviewUri(
            vscode.Uri.file(
                path.join(
                    extensionContext.extensionPath,
                    'src', 'webview', 'main.css'
                )
            )
        ),
        panel.webview.asWebviewUri(
            vscode.Uri.file(
                path.join(
                    extensionContext.extensionPath,
                    'out', 'webview', 'bundle.js'
                )
            )
        ),
    );

    panel.webview.onDidReceiveMessage(
        (message: MessageToServer) => {
            switch (message.tag) {
                case 'CreateScaffold':
                    const scaffoldInitiated =
                        initializeScaffold(
                            postMessage,
                            workspaceFsPath,
                            message.data,
                        );
                    if (scaffoldInitiated) {
                        panel.dispose();
                    }
                    break;
                case 'CreateExample':
                    const exampleProjectInitiated =
                        initializeExampleProject(postMessage, workspaceFsPath);
                    if (exampleProjectInitiated) {
                        panel.dispose();
                    }
                    break;
            }
        }
    )

    panel.onDidDispose(
        () => { setCurrentPanel(null); },
        null,
        extensionContext.subscriptions,
    );
}

function initializeExampleProject(
    postMessage: (message: MessageToClient) => void,
    workspaceFsPath: string,
): boolean {
    if (!isDirectoryEmpty(workspaceFsPath)) {
        postMessage({
            tag: 'ServerError',
            error: 'Cannot create example project, workspace is not empty.'
        });
        return false;
    }

    const exampleDirectory =
        extensionContext.asAbsolutePath(path.join(exampleFolder));
    fse.copySync(exampleDirectory, workspaceFsPath);

    return true;
}
