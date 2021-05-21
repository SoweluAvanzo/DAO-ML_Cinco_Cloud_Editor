'use strict';

import { ExtensionContext, window } from 'vscode';
import { createCincoLanguageServerClient } from './lsp/languageServerHandler';
import { languageIds } from './registry/languageRegistry';
import { registerGenerationCommand } from './commands/commandRegistrator'

export let workbenchOutput = window.createOutputChannel("cinco-extension");
export var extensionContext: ExtensionContext;

export function activate(context: ExtensionContext) {
    extensionContext = context;
    
    // register languageFeatures for associated language
    const languageClient = createCincoLanguageServerClient(
        extensionContext,
        languageIds
    );
    registerGenerationCommand(extensionContext, languageClient);
}