import * as vscode from 'vscode';
import { openProjectInitializationView } from './projectInitialization';

export let workbenchOutput: vscode.OutputChannel;
export let extensionContext: vscode.ExtensionContext
export let currentPanel : vscode.WebviewPanel | null;
const commandId = "info.scce.cinco-cloud.initialize-project";

export function activate(context: vscode.ExtensionContext) {
    workbenchOutput =
        vscode.window.createOutputChannel("Project Initialization");
    extensionContext = context;
    currentPanel = null;
    context.subscriptions.push(
        vscode.commands.registerCommand(
            commandId,
            (workspaceFsPath: string) =>
                openProjectInitializationView(
                    workspaceFsPath,
                    currentPanel,
                    panel => { currentPanel = panel; },
                )
        )
    );
}
