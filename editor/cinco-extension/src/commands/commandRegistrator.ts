import { commands, Disposable, ExtensionContext, window } from "vscode";
import { CincoLanguageClient } from "../lsp/cincoLanguageClient";
import { generationCommandId } from '../registry/commandRegistry';

export function registerGenerationCommand(context: ExtensionContext, languageClient: CincoLanguageClient) {
    const generationCmd: Disposable = commands.registerCommand(generationCommandId, () => generationCommand(languageClient));
    context.subscriptions.push(generationCmd);
}

async function generationCommand(languageClient: CincoLanguageClient) {
    // TODO: get sourceUri of cpd-document
    const sourceUri: string = "A";
    // TODO: get targetUri for generation path (static?)
    const targetUri: string = "B";

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