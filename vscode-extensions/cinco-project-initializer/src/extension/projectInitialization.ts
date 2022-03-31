import * as path from 'path'
import * as vscode from 'vscode'
import { workbenchOutput, extensionContext } from './main'
import { copy } from './helper/toolHelper'
import { Command } from '../common/model'
import { initializeScaffold } from './scaffold'
import { getWebviewContent } from './webview-template'
import { getWorkspaceFsPath } from './workspace'

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
        (message: Command) => {
            switch (message.tag) {
                case 'CreateScaffold':
                    initializeScaffold(workspaceFsPath, message.data);
                    panel.dispose();
                    break;
                case 'CreateExample':
                    initializeExampleProject(workspaceFsPath);
                    panel.dispose();
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

function initializeExampleProject(workspaceFsPath: string): void {
    const pathToExampleFiles = extensionContext.asAbsolutePath(path.join(exampleFolder));
    workbenchOutput.appendLine("Creating example project to: "+workspaceFsPath);
    workbenchOutput.appendLine("CopyAll Files from: "+pathToExampleFiles+"\nto: "+workspaceFsPath);
    copy(pathToExampleFiles, workspaceFsPath, true).then(() => {
        workbenchOutput.appendLine("Example project successfully created.");
    }).catch(()=> {
        workbenchOutput.appendLine("Creation of example project failed.");
    });
}
