import path = require('path');
import * as vscode from 'vscode';
import { workbenchOutput, extensionContext } from '../extension'
import { copy } from '../helper/toolHelper';
import { Command } from './common-types';
import { initializeScaffold } from './scaffold';
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
