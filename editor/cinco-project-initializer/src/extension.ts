import * as vscode from 'vscode';
import { getWebviewContent } from './webview';
import * as simplegit from 'simple-git';
import * as rimraf from 'rimraf';
import path = require('path');
import { generateMGL, generateMSL, PROJECT_NAME_REGEXP } from './initialize-project';

export function activate(context: vscode.ExtensionContext) {
    isWorkspaceEmpty().then(isEmpty => {
        if (isEmpty) {
            openProjectInitializationWebview(context);
        }
    });

    let disposable = vscode.commands.registerCommand('cinco.initialize-project', () => {
        openProjectInitializationWebview(context);
    });

    context.subscriptions.push(disposable);
}

async function openProjectInitializationWebview(context: vscode.ExtensionContext) {
    const panel = vscode.window.createWebviewPanel('cincoProjectInitializerWebview', 'Project Initializer', vscode.ViewColumn.One, {
        enableScripts: true
    });

    panel.webview.html = await getWebviewContent(context, panel);

    panel.webview.onDidReceiveMessage(
        async message => {
            switch (message.command) {
                case 'createExampleProject':
                    pullRepoIntoWorkspace(message.repoUrl, message.branch, panel);
                    break;
                case 'initializeProject':
                    {
                        const projectName = message.projectName;
                        const mglContent = generateMGL(projectName, `${projectName}.msl`);
                        const mslContent = generateMSL();
                        await createFile(projectName, 'mgl', mglContent, 'languages');
                        await createFile(projectName, 'msl', mslContent, 'languages');
                        vscode.window.showInformationMessage('Project successfully initialized!');
                        panel.dispose();
                    }
                    break;
                case 'initializeModelFile':
                    {
                        const fileName = message.modelName;
                        const fileType = message.modelType;
                        await createFile(fileName, fileType, '');
                        vscode.window.showInformationMessage('Modelfile successfully initialized!');
                        panel.dispose();
                    }
                    break;
            }
        },
        undefined,
        context.subscriptions
    );
}

async function createFile(fileName: string, fileType: string, fileContent: string, workspaceSubFolder?: string): Promise<void> {
    const workspaceFolder = vscode.workspace.workspaceFolders;
    if (!workspaceFolder || workspaceFolder.length < 1) {
        vscode.window.showErrorMessage('Workspace could not be found!');
        return;
    }
    const regexp = new RegExp(PROJECT_NAME_REGEXP);
    if (!fileName.match(regexp)) {
        vscode.window.showErrorMessage(`Illegal file name: "${fileName}"`);
        return;
    }

    const workspaceRoot = workspaceFolder[0].uri.fsPath;
    const folderPath = workspaceSubFolder ? path.join(workspaceRoot, workspaceSubFolder) : workspaceRoot;

    if (workspaceSubFolder) {
        const uri = vscode.Uri.parse(folderPath);

        const stat = await getStats(uri);
        if (stat) {
            switch (stat.type) {
                case vscode.FileType.File:
                    throw new Error('Could not create Folder: ' + folderPath);
                case vscode.FileType.Directory:
                    break;
                case vscode.FileType.Unknown:
                    break;
            }
        }
        try {
            await vscode.workspace.fs.createDirectory(uri);
        } catch (e) {
            throw new Error('Error creating Folder: ' + folderPath);
        }
    }
    const filePath = path.join(folderPath, `${fileName}.${fileType}`);
    const fileUri = vscode.Uri.file(filePath);
    const utf8Encoder = new TextEncoder();
    vscode.workspace.fs.writeFile(fileUri, utf8Encoder.encode(fileContent));
}

async function getStats(uri: vscode.Uri): Promise<vscode.FileStat | undefined> {
    try {
        return await vscode.workspace.fs.stat(uri);
    } catch (e) {
        return undefined;
    }
}

async function isWorkspaceEmpty() {
    let isWorkspaceOpen = vscode.workspace.workspaceFolders && vscode.workspace.workspaceFolders.length > 0;

    if (isWorkspaceOpen) {
        return vscode.workspace
            .findFiles('**/*', '**/node_modules/**', 1) // Searches for any file, excluding node_modules, limit results to 1
            .then(files => {
                if (files.length === 0) {
                    return true;
                } else {
                    return false;
                }
            });
    }
    return false;
}

async function pullRepoIntoWorkspace(repoUrl: string, branchName: string, panel: vscode.WebviewPanel) {
    // Get the current workspace path
    let workspaceFolders = vscode.workspace.workspaceFolders;
    if (!workspaceFolders) {
        vscode.window.showErrorMessage('No workspace is open.');
        return;
    }

    let workspacePath = workspaceFolders[0].uri.fsPath;

    // Use simple-git to clone the repository
    const git = simplegit.simpleGit();
    try {
        await git.clone(repoUrl, workspacePath, ['-b', branchName]);

        // Once cloned, remove the .git directory
        let gitDir = path.join(workspacePath, '.git');
        rimraf.sync(gitDir);

        vscode.window.showInformationMessage('Example successfully initialized!');
        panel.dispose();
    } catch (error: any) {
        vscode.window.showErrorMessage(`Failed to pull example repository: ${error.message}`);
    }
}

export function deactivate() {}
