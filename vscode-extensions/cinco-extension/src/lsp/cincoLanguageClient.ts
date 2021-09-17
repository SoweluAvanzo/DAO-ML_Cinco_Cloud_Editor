import * as vscode from 'vscode';
import { LanguageClient, LanguageClientOptions, ServerOptions } from 'vscode-languageclient';

import { workbenchOutput } from '../extension';
import { executeProduct } from '../grpc/grpc-handler';
import { GenerateRequest, GenerateRequestEndpoint, GenerateResponse } from './communication/lspMessageExtension';


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

        try {
            this.sendRequest(GenerateRequestEndpoint.type, generateRequest)
                .then((response: GenerateResponse) => {
                    this.onGenerateFinished(response, execute);
                });
        } catch (e) {
            const message = "LanguageClient could not request generation";
            vscode.window.showErrorMessage(message);
            workbenchOutput.appendLine(message);
        }

    }

    onGenerateFinished(response: GenerateResponse, execute: boolean) {
        const message = "generated cinco-product to: " + response.targetUri;
        vscode.window.showInformationMessage(message);
        workbenchOutput.appendLine(message);

        if (execute) {
            const message2 = "executing cinco-product";
            vscode.window.showInformationMessage(message2);
            workbenchOutput.appendLine(message2);
            const outputPath = vscode.Uri.parse(response.targetUri).fsPath;
            console.log("Generated artifact: " + outputPath);
            executeProduct(outputPath);
        }
    }
}
