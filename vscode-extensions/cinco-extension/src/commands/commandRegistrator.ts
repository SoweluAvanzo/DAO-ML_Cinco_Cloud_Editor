import { commands, Disposable, ExtensionContext, Uri, window, workspace } from 'vscode';

import { CincoLanguageClient } from '../lsp/cincoLanguageClient';
import { generationCommandId, pushCommandId } from '../registry/commandRegistry';
import { pushFilesToRemote } from "../git/gitHandler";

export function registerGenerationCommand(context: ExtensionContext, languageClient: CincoLanguageClient) {
    const generationCmd: Disposable = commands.registerCommand(generationCommandId, () => generationCommand(languageClient));
    context.subscriptions.push(generationCmd);
}

async function generationCommand(languageClient: CincoLanguageClient) {
    const document = window.activeTextEditor.document;
    const sourceUri = document.uri.fsPath;
    console.log("Generating - sourceURI: " + sourceUri);
    const folder = workspace.getWorkspaceFolder(document.uri);
    if (!folder) {
        window.showErrorMessage(document.fileName + " needs to be in a workspace folder.");
    }
    // TODO: get targetUri for generation path (static?)
    const targetUri: string = folder.uri.fsPath;
    console.log("Generating - targetURI: " + targetUri);

    if (languageClient) {
        let value: string = await window.showInformationMessage(
            "Do you want to deploy the generated product?",
            ...["Yes", "No", "Cancel"]);
        if (value == "Cancel")
            return;
        // trigger generation
        languageClient.generate(sourceUri, targetUri, value == "Yes");
    } else {
        window.showInformationMessage("generator is not yet ready, please wait");
    }
}

export function registerPushCommand(context: ExtensionContext) {
    const pushCmd: Disposable = commands.registerCommand(pushCommandId, (fileUri: Uri) => pushCommand(fileUri));
    context.subscriptions.push(pushCmd);
}

async function pushCommand(fileUri: Uri) {
    pushFilesToRemote(fileUri);
}