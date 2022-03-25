import path = require('path');
import * as vscode from 'vscode';
import { workbenchOutput, extensionContext } from '../extension'
import { copy } from '../helper/toolHelper';
import { getWebviewContent } from './webview';

const exampleFolder = "exampleFiles/";

export async function openProjectInitializationView(
    workspaceFsPath: string,
    currentPanel: vscode.WebviewPanel | null,
    setCurrentPanel: (panel: vscode.WebviewPanel | null) => void,
) {
    workbenchOutput.appendLine(workspaceFsPath);

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
        },
    );

    setCurrentPanel(panel);

    panel.webview.html = getWebviewContent();

    panel.webview.onDidReceiveMessage(
        _ => {
            initializeExampleProject(workspaceFsPath);
            panel.dispose();
        }
    )

    panel.onDidDispose(
        () => { setCurrentPanel(null); },
        null,
        extensionContext.subscriptions,
    );
}

function initializeExampleProject(workspaceFsPath: string) {
    const pathToExampleFiles = extensionContext.asAbsolutePath(path.join(exampleFolder));
    workbenchOutput.appendLine("Creating example project to: "+workspaceFsPath);
    try {
        workspaceFsPath = vscode.Uri.parse(workspaceFsPath).fsPath;
    } catch(e) {
        workbenchOutput.appendLine("can't use vscode.Uri. It is probably not implemented correctly...");    
    }
    workbenchOutput.appendLine("CopyAll Files from: "+pathToExampleFiles+"\nto: "+workspaceFsPath);
    copy(pathToExampleFiles, workspaceFsPath, true).then(() => {
        workbenchOutput.appendLine("Example project successfully created.");
    }).catch(()=> {
        workbenchOutput.appendLine("Creation of example project failed.");
    });
}
