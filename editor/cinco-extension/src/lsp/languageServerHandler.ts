'use strict';
import { workspace, ExtensionContext } from 'vscode';
import { LanguageClientOptions, StreamInfo, Trace } from 'vscode-languageclient';
import { CincoLanguageClient } from './cincoLanguageClient';
import * as net from 'net';

export function createCincoLanguageServerClient(context: ExtensionContext, languageIds: string[]) : CincoLanguageClient {    
    /* FALLBACK: starting language server
        const executablExt = process.platform == 'win32' ? '.bat' : '';
        const executable = 'cinco-language-server' + executablExt;
        const command = context.asAbsolutePath(path.join("language-server", 'bin', executable));
    */
    let connectionInfo = {
        port: 5008
    };
    let serverOptions = () => {
        // Connect to language server via socket
        let socket = net.connect(connectionInfo);
        let result: StreamInfo = {
            writer: socket,
            reader: socket
        };
        return Promise.resolve(result);
    };
    const clientOptions: LanguageClientOptions = {
        documentSelector: languageIds,
        synchronize: {
            configurationSection: 'cinco-language-server',
            fileEvents: workspace.createFileSystemWatcher('**/*.*') // TODO: restrict to fileFormats
        }
    }
    var client: CincoLanguageClient = new CincoLanguageClient(languageIds, serverOptions, clientOptions);
    client.trace = Trace.Verbose;
    context.subscriptions.push(client.start());
    return client;
}