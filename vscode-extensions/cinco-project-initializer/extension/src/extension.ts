import * as vscode from 'vscode';
import * as fs from 'fs'
import { openProjectInitializationView } from './projectInitialization';
import { getWorkspaceFsPath } from './workspace';

export let workbenchOutput: vscode.OutputChannel;
export let extensionContext: vscode.ExtensionContext
export let currentPanel : vscode.WebviewPanel | null;
const commandId = "info.scce.cinco-cloud.initialize-project";

export function activate(context: vscode.ExtensionContext) {
    workbenchOutput =
        vscode.window.createOutputChannel("Project Initialization");
    extensionContext = context;
    currentPanel = null;

    function openProjectInitializationViewInScope() {
        openProjectInitializationView(
            currentPanel,
            panel => { currentPanel = panel; },
        );
    }

    const workspaceFsPath = getWorkspaceFsPath();

    if (workspaceFsPath === null) {
        workbenchOutput.appendLine(
            'No workspace loaded, not showing initialization page'
        );
    } else if (isDirectoryEmpty(workspaceFsPath)) {
        openProjectInitializationViewInScope();
    }

    context.subscriptions.push(
        vscode.commands.registerCommand(
            commandId,
            openProjectInitializationViewInScope
        )
    );
}

function isDirectoryEmpty(path: string): boolean {
    const directory = fs.opendirSync(path);
    const workspaceIsEmpty = directory.readSync() === null;
    directory.close();
    return workspaceIsEmpty;
}
