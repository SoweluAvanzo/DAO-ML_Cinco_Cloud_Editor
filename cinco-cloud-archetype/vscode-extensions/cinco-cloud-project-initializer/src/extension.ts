import * as vscode from 'vscode';
import { getWebviewContent } from './webview';
import * as simplegit from 'simple-git';
import * as rimraf from 'rimraf';
import path = require('path');
import { generateMGL, generateMSL, PROJECT_NAME_REGEXP } from './initialize-project';

export function activate(context: vscode.ExtensionContext) {
	isWorkspaceEmpty().then(isEmpty => {
		if(isEmpty) {
			openProjectInitializationWebview(context);
		}
	});

	let disposable = vscode.commands.registerCommand('cincoCloud.initializeProject', () => {
		openProjectInitializationWebview(context);
	});

	context.subscriptions.push(disposable);
}

async function openProjectInitializationWebview(context: vscode.ExtensionContext) {
	const panel = vscode.window.createWebviewPanel(
		'cincoCloudProjectInitializerWebview',
		'Project Initializer',
		vscode.ViewColumn.One,
		{
			"enableScripts": true
		}
	);

	panel.webview.html = await getWebviewContent(context, panel);

	panel.webview.onDidReceiveMessage(
		message => {
			switch (message.command) {
				case 'createExampleProject':
					pullRepoIntoWorkspace(message.repoUrl, message.branch, panel);
					break;
				case 'initializeProject':
					{
						const projectName = message.projectName;
						const mglContent = generateMGL(projectName, `${projectName}.msl`);
						const mslContent = generateMSL();
						createFile(projectName, 'mgl', mglContent);
						createFile(projectName, 'msl', mslContent);
						vscode.window.showInformationMessage('Project successfully initialized!');
						panel.dispose();
					}
					break;
				case 'initializeModelFile':
					{
						const fileName = message.modelName;
						const fileType = message.modelType;
						createFile(fileName, fileType, '');
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

function createFile(fileName: string, fileType: string, fileContent: string) {
	const workspaceFolder = vscode.workspace.workspaceFolders;
	if(!workspaceFolder || workspaceFolder.length < 1) {
		vscode.window.showErrorMessage('Workspace could not be found!');
		return;
	}
	const regexp = new RegExp(PROJECT_NAME_REGEXP);
	if(!fileName.match(regexp)){
		vscode.window.showErrorMessage(`Illegal file name: "${fileName}"`);
		return;
	}

	const workspaceRoot = workspaceFolder[0].uri.fsPath;
	const filePath = path.join(workspaceRoot, `${fileName}.${fileType}`);
	const fileUri = vscode.Uri.file(filePath);

	const utf8Encoder = new TextEncoder();
	vscode.workspace.fs.writeFile(fileUri, utf8Encoder.encode(fileContent));
}

async function isWorkspaceEmpty() {
	let isWorkspaceOpen = vscode.workspace.workspaceFolders && vscode.workspace.workspaceFolders.length > 0;

	if (isWorkspaceOpen) {
		return vscode.workspace.findFiles('**/*', '**/node_modules/**', 1) // Searches for any file, excluding node_modules, limit results to 1
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
