import * as vscode from 'vscode'

export function getWorkspaceFsPath(): string | null {

    const workspaceFolders = vscode.workspace.workspaceFolders;

    if (workspaceFolders === undefined) {
        return null;
    }

    return workspaceFolders[0].uri.fsPath;
}
