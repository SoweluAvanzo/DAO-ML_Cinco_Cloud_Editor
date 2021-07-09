import { commands, Disposable, ExtensionContext, window, workspace } from "vscode";
import * as vscode from 'vscode';
import { CincoLanguageClient } from "../lsp/cincoLanguageClient";
import { generationCommandId } from '../registry/commandRegistry';
import { WorkspaceFoldersFeature } from "vscode-languageclient/lib/workspaceFolders";
import { relative } from "path";

export function registerGenerationCommand(context: ExtensionContext, languageClient: CincoLanguageClient) {
    const generationCmd: Disposable = commands.registerCommand(generationCommandId, () => generationCommand(languageClient));
    context.subscriptions.push(generationCmd);
}

async function generationCommand(languageClient: CincoLanguageClient) {
    const document = window.activeTextEditor.document;
    const sourceUri = document.uri.fsPath;
    const relativePath = workspace.asRelativePath(sourceUri, false);
    const folder = workspace.workspaceFolders.find((value, index, obj) => {
        try {
            const recreated = vscode.Uri.joinPath(value.uri, relativePath).fsPath;
            return recreated == sourceUri;
        } catch(e) {
            return false;
        }
    });
    if(!folder) {
        window.showErrorMessage("file needs to be in a workspace folder.");
    }
    // TODO: get targetUri for generation path (static?)
    const targetUri: string = folder.uri.fsPath;

    if(languageClient) {
        let value: string = await window.showInformationMessage(
            "Do you want to run the generated Pyro Product?",
            ...["Yes", "No", "Cancel"]);
        if(value=="Cancel")
            return;
        // trigger generation
        languageClient.generate(sourceUri, targetUri, value == "Yes");
    } else {
        window.showInformationMessage("generator is not yet ready, please wait");
    }
}