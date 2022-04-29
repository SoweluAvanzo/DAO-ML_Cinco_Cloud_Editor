'use strict';
import { ExtensionContext, window } from 'vscode';

import { registerGenerationCommand, registerPushCommand } from './commands/commandRegistrator';
import { createCincoLanguageServerClient } from './lsp/languageServerHandler';
import { languageIds } from './registry/languageRegistry';
import { initGitExtension } from "./git/gitHandler";


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

    // register git integration
    initGitExtension();
    registerPushCommand(extensionContext);
}