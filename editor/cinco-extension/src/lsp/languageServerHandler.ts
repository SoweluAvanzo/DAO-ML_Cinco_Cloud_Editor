'use strict';
import * as path from 'path';
import { workspace, ExtensionContext } from 'vscode';
import { LanguageClientOptions } from 'vscode-languageclient';
import { CincoLanguageClient } from './cincoLanguageClient';

export function createCincoLanguageServerClient(context: ExtensionContext, languageIds: string[]) : CincoLanguageClient {    
    // starting language server
    const executablExt = process.platform == 'win32' ? '.bat' : '';
    const executable = 'cinco-language-server' + executablExt;
    const command = context.asAbsolutePath(path.join("language-server", 'bin', executable));
    const serverOptions = { command };
    const clientOptions: LanguageClientOptions = {
        documentSelector: languageIds,
        synchronize: {
            configurationSection: 'cinco-language-server',
            fileEvents: workspace.createFileSystemWatcher('**/*.*') // TODO: restrict to fileFormats
        }
    }
    var client: CincoLanguageClient = new CincoLanguageClient(languageIds, serverOptions, clientOptions);
    context.subscriptions.push(client.start());
    return client;
}
