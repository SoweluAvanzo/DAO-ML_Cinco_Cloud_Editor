import { LanguageClient,  ServerOptions, LanguageClientOptions } from "vscode-languageclient";
import * as vscode from "vscode";
import { workbenchOutput } from "../extension";
import { GenerateRequest, GenerateRequestEndpoint, GenerateResponse } from "./communication/lspMessageExtension";


export class CincoLanguageClient extends LanguageClient {
    languageIds: string[];
    instance: CincoLanguageClient;

    constructor(languageIds: string[], serverOptions: ServerOptions, clientOptions: LanguageClientOptions) {
        super("cinco-language-server", serverOptions, clientOptions);
        this.languageIds = languageIds;
        this.instance = this;
    }

    generate(sourceUri: string, targetUri: string, execute: boolean) {
        var generateRequest: GenerateRequest = new GenerateRequest();
        generateRequest.sourceUri = sourceUri;
        generateRequest.targetUri = targetUri;
        generateRequest.execute = execute;
        
        this.sendRequest(GenerateRequestEndpoint.type, generateRequest)
            .then( (response: GenerateResponse) => {
                this.onGenerateFinished(response);
            });
    }

    onGenerateFinished(response: GenerateResponse) {
        const message = "generated cinco-product to: "+response.targetUri;
        vscode.window.showInformationMessage(message);
        workbenchOutput.appendLine(message);
    }
}